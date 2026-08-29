package space.anatomyuniverse.musavacca.teleport;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import space.anatomyuniverse.musavacca.block.custom.MusavaccaPortalTrapdoorBlock;
import net.minecraft.world.level.portal.PortalShape;
import net.minecraft.world.phys.Vec3;
import space.anatomyuniverse.musavacca.block.ModBlocks;
import space.anatomyuniverse.musavacca.block.custom.MusavaccaPortalDoorBlock;
import space.anatomyuniverse.musavacca.block.custom.VocoPostBlock;
import space.anatomyuniverse.musavacca.block.custom.VocoTableBlock;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoReceptorLogic.ReceptorPosition;
import space.anatomyuniverse.musavacca.block.entity.custom.MusavaccaPortalDoorBlockEntity;
import space.anatomyuniverse.musavacca.block.entity.custom.MusavaccaPortalTrapdoorBlockEntity;
import space.anatomyuniverse.musavacca.block.entity.custom.PearlPortalBlockEntity;
import space.anatomyuniverse.musavacca.block.entity.custom.VocoPostBlockEntity;
import space.anatomyuniverse.musavacca.block.entity.custom.VocoTableBlockEntity;
import space.anatomyuniverse.musavacca.vococaller.VocoCallerNetwork;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public final class HexTeleportResolver {

    private HexTeleportResolver() {}

    public record ResolvedEndpoint(
            UUID endpointId,
            ServerLevel level,
            HexTeleportDirectory.Endpoint endpoint
    ) {
        public Vec3 targetPos() {
            return this.endpoint.target().vec3();
        }

        public float yaw() {
            return this.endpoint.yaw();
        }

        public float pitch() {
            return this.endpoint.pitch();
        }
    }

    public record ResolvedDoorEndpoint(
            ServerLevel level,
            HexTeleportDirectory.DoorEndpoint endpoint
    ) {}

    public record ResolvedTrapdoorEndpoint(
            ServerLevel level,
            HexTeleportDirectory.DoorEndpoint endpoint
    ) {}

    public static boolean teleportToHex(
            ServerPlayer player,
            int hexColor
    ) {
        MinecraftServer server =
                player.level().getServer();

        if (server == null) {
            return false;
        }

        int normalizedHex =
                HexTeleportDirectory.normalizeHex(
                        hexColor
                );

        HexTeleportDirectory directory =
                HexTeleportDirectory.get(
                        server
                );

        HexTeleportDirectory.PhoneRegistration phone =
                directory
                        .getPhoneRegistrationByHex(
                                normalizedHex
                        )
                        .orElse(null);

        if (phone != null) {
            return VocoCallerNetwork
                    .teleportToPhone(
                            player,
                            phone
                    );
        }

        /*
         * Re-read the hex index after stale cleanup.
         *
         * That means if removing a stale active endpoint immediately
         * promotes a queued claim for the same hex, this same teleport
         * attempt can use the newly promoted endpoint instead of making
         * the player try a second time.
         */
        Set<UUID> attempted =
                new HashSet<>();

        while (true) {
            List<HexTeleportDirectory.Endpoint> endpoints =
                    directory.getEndpointsByHex(
                            normalizedHex
                    );

            HexTeleportDirectory.Endpoint next =
                    null;

            for (
                    HexTeleportDirectory.Endpoint endpoint :
                    endpoints
            ) {
                if (
                        attempted.add(
                                endpoint.endpointId()
                        )
                ) {
                    next =
                            endpoint;

                    break;
                }
            }

            if (next == null) {
                break;
            }

            Optional<ResolvedEndpoint> resolved =
                    resolveEndpoint(
                            server,
                            next
                    );

            if (resolved.isPresent()) {
                teleport(
                        player,
                        resolved.get()
                );

                return true;
            }

            HexTeleportAddressNetwork
                    .releaseEndpoint(
                            server,
                            next.endpointId()
                    );
        }

        player.displayClientMessage(
                Component.literal(
                        "No active teleport address found for #"
                                + HexTeleportDirectory.toHex(
                                normalizedHex
                        )
                                + "."
                ),
                true
        );

        return false;
    }

    public static boolean teleportToOwner(
            ServerPlayer player,
            String ownerKey
    ) {
        MinecraftServer server =
                player.level().getServer();

        if (server == null) {
            return false;
        }

        HexTeleportDirectory directory =
                HexTeleportDirectory.get(
                        server
                );

        HexTeleportDirectory.Endpoint endpoint =
                directory
                        .getEndpointByOwner(
                                ownerKey
                        )
                        .orElse(null);

        if (endpoint == null) {
            player.displayClientMessage(
                    Component.literal(
                            "This endpoint is not registered."
                    ),
                    true
            );

            return false;
        }

        Optional<ResolvedEndpoint> resolved =
                resolveEndpoint(
                        server,
                        endpoint
                );

        if (resolved.isEmpty()) {
            HexTeleportAddressNetwork
                    .releaseEndpoint(
                            server,
                            endpoint.endpointId()
                    );

            player.displayClientMessage(
                    Component.literal(
                            "This endpoint was stale and has been cleaned up."
                    ),
                    true
            );

            return false;
        }

        teleport(
                player,
                resolved.get()
        );

        return true;
    }

    public static Optional<ResolvedEndpoint> resolveLinkedPortal(
            ServerLevel sourceLevel,
            UUID sourcePortalId
    ) {
        MinecraftServer server =
                sourceLevel.getServer();

        HexTeleportDirectory directory =
                HexTeleportDirectory.get(
                        server
                );

        HexTeleportDirectory.Endpoint target =
                directory
                        .getLinkedPortalEndpoint(
                                sourcePortalId
                        )
                        .orElse(null);

        if (target == null) {
            return Optional.empty();
        }

        Optional<ResolvedEndpoint> resolved =
                resolveEndpoint(
                        server,
                        target
                );

        if (resolved.isEmpty()) {
            HexTeleportAddressNetwork
                    .releaseEndpoint(
                            server,
                            target.endpointId()
                    );
        }

        return resolved;
    }

    public static Optional<ResolvedDoorEndpoint> resolveLinkedDoor(
            ServerLevel sourceLevel,
            String sourceOwnerKey
    ) {
        MinecraftServer server =
                sourceLevel.getServer();

        HexTeleportDirectory directory =
                HexTeleportDirectory.get(
                        server
                );

        HexTeleportDirectory.DoorEndpoint target =
                directory
                        .getLinkedDoorEndpoint(
                                sourceOwnerKey
                        )
                        .orElse(null);

        if (target == null) {
            return Optional.empty();
        }

        Optional<ResolvedDoorEndpoint> resolved =
                resolveDoorEndpoint(
                        server,
                        target
                );

        if (resolved.isEmpty()) {
            HexTeleportAddressNetwork
                    .releaseOwner(
                            server,
                            target.ownerKey()
                    );
        }

        return resolved;
    }

    public static Optional<ResolvedTrapdoorEndpoint> resolveLinkedTrapdoor(
            ServerLevel sourceLevel,
            String sourceOwnerKey
    ) {
        MinecraftServer server =
                sourceLevel.getServer();

        HexTeleportDirectory directory =
                HexTeleportDirectory.get(
                        server
                );

        HexTeleportDirectory.DoorEndpoint target =
                directory
                        .getLinkedTrapdoorEndpoint(
                                sourceOwnerKey
                        )
                        .orElse(null);

        if (target == null) {
            return Optional.empty();
        }

        Optional<ResolvedTrapdoorEndpoint> resolved =
                resolveTrapdoorEndpoint(
                        server,
                        target
                );

        if (resolved.isEmpty()) {
            HexTeleportAddressNetwork
                    .releaseOwner(
                            server,
                            target.ownerKey()
                    );
        }

        return resolved;
    }

    public static Optional<ResolvedEndpoint> resolveEndpoint(
            MinecraftServer server,
            HexTeleportDirectory.Endpoint endpoint
    ) {
        if (
                server == null
                        || endpoint == null
                        || endpoint.dimensionId() == null
        ) {
            return Optional.empty();
        }

        ResourceKey<Level> dimensionKey =
                ResourceKey.create(
                        Registries.DIMENSION,
                        endpoint.dimensionId()
                );

        ServerLevel level =
                server.getLevel(
                        dimensionKey
                );

        if (level == null) {
            return Optional.empty();
        }

        /*
         * Owner chunk first:
         *
         * this makes a remote unloaded endpoint inspectable before
         * we validate its block/block entity.
         */
        keepChunkLoaded(
                level,
                endpoint.ownerPos()
        );

        if (
                !isEndpointStillValid(
                        level,
                        endpoint
                )
        ) {
            return Optional.empty();
        }

        /*
         * Voco custom targets can be in a different chunk than the
         * endpoint owner, so keep the actual landing chunk alive too.
         */
        keepChunkLoaded(
                level,
                BlockPos.containing(
                        endpoint.target().vec3()
                )
        );

        return Optional.of(
                new ResolvedEndpoint(
                        endpoint.endpointId(),
                        level,
                        endpoint
                )
        );
    }

    public static Optional<ResolvedDoorEndpoint> resolveDoorEndpoint(
            MinecraftServer server,
            HexTeleportDirectory.DoorEndpoint endpoint
    ) {
        if (
                server == null
                        || endpoint == null
                        || endpoint.dimensionId() == null
        ) {
            return Optional.empty();
        }

        ResourceKey<Level> dimensionKey =
                ResourceKey.create(
                        Registries.DIMENSION,
                        endpoint.dimensionId()
                );

        ServerLevel level =
                server.getLevel(
                        dimensionKey
                );

        if (level == null) {
            return Optional.empty();
        }

        /*
         * Doors can be arbitrarily far away and completely unloaded.
         * Ticket/load the stored lower-door owner chunk before touching
         * its block state or block entity.
         */
        keepChunkLoaded(
                level,
                endpoint.ownerPos()
        );

        if (
                !isDoorEndpointStillValid(
                        level,
                        endpoint
                )
        ) {
            return Optional.empty();
        }

        return Optional.of(
                new ResolvedDoorEndpoint(
                        level,
                        endpoint
                )
        );
    }

    public static Optional<ResolvedTrapdoorEndpoint> resolveTrapdoorEndpoint(
            MinecraftServer server,
            HexTeleportDirectory.DoorEndpoint endpoint
    ) {
        if (
                server == null
                        || endpoint == null
                        || endpoint.dimensionId() == null
        ) {
            return Optional.empty();
        }

        ResourceKey<Level> dimensionKey =
                ResourceKey.create(
                        Registries.DIMENSION,
                        endpoint.dimensionId()
                );

        ServerLevel level =
                server.getLevel(
                        dimensionKey
                );

        if (level == null) {
            return Optional.empty();
        }

        keepChunkLoaded(
                level,
                endpoint.ownerPos()
        );

        if (
                !isTrapdoorEndpointStillValid(
                        level,
                        endpoint
                )
        ) {
            return Optional.empty();
        }

        return Optional.of(
                new ResolvedTrapdoorEndpoint(
                        level,
                        endpoint
                )
        );
    }

    private static boolean isEndpointStillValid(
            ServerLevel level,
            HexTeleportDirectory.Endpoint endpoint
    ) {
        BlockPos pos =
                endpoint.ownerPos();

        BlockState state =
                level.getBlockState(
                        pos
                );

        return switch (endpoint.kind()) {
            case PEARL_PORTAL ->
                    isValidPearlPortalEndpoint(
                            level,
                            pos,
                            endpoint
                    );

            case VOCO_POST_RECEPTOR_CORNER ->
                    isValidVocoPostReceptorCornerEndpoint(
                            level,
                            pos,
                            state,
                            endpoint
                    );

            case VOCO_TABLE_RECEPTOR_CORNER ->
                    isValidVocoTableReceptorCornerEndpoint(
                            level,
                            pos,
                            state,
                            endpoint
                    );
        };
    }

    private static boolean isDoorEndpointStillValid(
            ServerLevel level,
            HexTeleportDirectory.DoorEndpoint endpoint
    ) {
        BlockPos pos =
                endpoint.ownerPos();

        BlockState state =
                level.getBlockState(
                        pos
                );

        if (
                !(state.getBlock()
                        instanceof MusavaccaPortalDoorBlock)
                        || state.getValue(
                        MusavaccaPortalDoorBlock.HALF
                )
                        != DoubleBlockHalf.LOWER
        ) {
            return false;
        }

        if (
                !(level.getBlockEntity(
                        pos
                )
                        instanceof MusavaccaPortalDoorBlockEntity doorBe)
                        || !doorBe.hasHexColor()
        ) {
            return false;
        }

        String expectedOwnerKey =
                HexTeleportDirectory
                        .doorOwnerKey(
                                level.dimension()
                                        .location(),
                                pos
                        );

        return endpoint.ownerKey()
                .equals(
                        expectedOwnerKey
                )
                && state.getValue(
                MusavaccaPortalDoorBlock.LIT
        )
                && state.getValue(
                MusavaccaPortalDoorBlock.LIT_PORTAL
        )
                && state.getValue(
                MusavaccaPortalDoorBlock.PORTAL
        )
                && HexTeleportDirectory
                .normalizeHex(
                        doorBe.getHexColor()
                )
                == endpoint.hexColor();
    }

    private static boolean isTrapdoorEndpointStillValid(
            ServerLevel level,
            HexTeleportDirectory.DoorEndpoint endpoint
    ) {
        BlockPos pos =
                endpoint.ownerPos();

        BlockState state =
                level.getBlockState(
                        pos
                );

        if (
                !(state.getBlock()
                        instanceof MusavaccaPortalTrapdoorBlock)
        ) {
            return false;
        }

        if (
                !(level.getBlockEntity(
                        pos
                )
                        instanceof MusavaccaPortalTrapdoorBlockEntity trapdoorBe)
                        || !trapdoorBe.hasHexColor()
        ) {
            return false;
        }

        String expectedOwnerKey =
                HexTeleportDirectory
                        .trapdoorOwnerKey(
                                level.dimension()
                                        .location(),
                                pos
                        );

        return endpoint.ownerKey()
                .equals(
                        expectedOwnerKey
                )
                && state.getValue(
                MusavaccaPortalTrapdoorBlock.LIT
        )
                && state.getValue(
                MusavaccaPortalTrapdoorBlock.LIT_PORTAL
        )
                && state.getValue(
                MusavaccaPortalTrapdoorBlock.PORTAL
        )
                && HexTeleportDirectory
                .normalizeHex(
                        trapdoorBe.getHexColor()
                )
                == endpoint.hexColor();
    }

    private static boolean isValidPearlPortalEndpoint(
            ServerLevel level,
            BlockPos pos,
            HexTeleportDirectory.Endpoint endpoint
    ) {
        if (
                !level.getBlockState(
                                pos
                        )
                        .is(
                                ModBlocks.PEARL_PORTAL.get()
                        )
        ) {
            return false;
        }

        if (
                !(level.getBlockEntity(
                        pos
                )
                        instanceof PearlPortalBlockEntity portalBe)
        ) {
            return false;
        }

        return portalBe.isValidPortalTile()
                && endpoint.endpointId()
                .equals(
                        portalBe.getPortalId()
                )
                && HexTeleportDirectory.normalizeHex(
                portalBe.getHexColor()
        )
                == endpoint.hexColor();
    }

    private static boolean isValidVocoPostReceptorCornerEndpoint(
            ServerLevel level,
            BlockPos pos,
            BlockState state,
            HexTeleportDirectory.Endpoint endpoint
    ) {
        if (
                !(state.getBlock()
                        instanceof VocoPostBlock)
        ) {
            return false;
        }

        if (
                !state.hasProperty(
                        VocoPostBlock.PORTAL
                )
                        || !state.getValue(
                        VocoPostBlock.PORTAL
                )
        ) {
            return false;
        }

        if (
                !(level.getBlockEntity(
                        pos
                )
                        instanceof VocoPostBlockEntity postBe)
        ) {
            return false;
        }

        return postBe.hasHexColor()
                && HexTeleportDirectory.normalizeHex(
                postBe.getHexColor()
        )
                == endpoint.hexColor();
    }

    private static boolean isValidVocoTableReceptorCornerEndpoint(
            ServerLevel level,
            BlockPos pos,
            BlockState state,
            HexTeleportDirectory.Endpoint endpoint
    ) {
        if (
                !(state.getBlock()
                        instanceof VocoTableBlock)
        ) {
            return false;
        }

        if (
                !(level.getBlockEntity(
                        pos
                )
                        instanceof VocoTableBlockEntity tableBe)
        ) {
            return false;
        }

        ReceptorPosition receptor =
                ReceptorPosition.byId(
                        endpoint.slotId()
                );

        if (
                !state.hasProperty(
                        VocoTableBlock.portalProperty(
                                receptor
                        )
                )
                        || !state.getValue(
                        VocoTableBlock.portalProperty(
                                receptor
                        )
                )
        ) {
            return false;
        }

        return HexTeleportDirectory.normalizeHex(
                tableBe.getPortalHexColorOrUnset(
                        receptor
                )
        )
                == endpoint.hexColor();
    }

    public static void teleport(
            ServerPlayer player,
            ResolvedEndpoint resolved
    ) {
        teleportToTarget(
                player,
                resolved.level(),
                resolved.targetPos(),
                resolved.yaw(),
                resolved.pitch()
        );
    }

    public static void teleportToTarget(
            ServerPlayer player,
            ServerLevel targetLevel,
            Vec3 wantedPos,
            float yaw,
            float pitch
    ) {
        EntityDimensions dimensions =
                player.getDimensions(
                        player.getPose()
                );

        Vec3 safePos =
                PortalShape.findCollisionFreePosition(
                        wantedPos,
                        targetLevel,
                        player,
                        dimensions
                );

        HexTeleportPreloader.prepare(
                player,
                targetLevel,
                safePos
        );

        if (
                player.level()
                        == targetLevel
        ) {
            player.connection.teleport(
                    safePos.x,
                    safePos.y,
                    safePos.z,
                    yaw,
                    pitch
            );
        } else {
            player.teleportTo(
                    targetLevel,
                    safePos.x,
                    safePos.y,
                    safePos.z,
                    Set.of(),
                    yaw,
                    pitch
                    //? if >=1.21.2
                    , true
            );
        }

        player.setYHeadRot(yaw);
        player.setYBodyRot(yaw);
        player.setPortalCooldown();

        targetLevel.playSound(
                null,
                safePos.x,
                safePos.y,
                safePos.z,
                SoundEvents.PORTAL_TRAVEL,
                SoundSource.PLAYERS,
                0.35F,
                1.15F
        );
    }

    public static void keepChunkLoaded(
            ServerLevel level,
            BlockPos pos
    ) {
        ChunkPos chunkPos =
                new ChunkPos(
                        pos
                );

        //? if >=1.21.5 {
        level.getChunkSource()
                .addTicketWithRadius(
                        TicketType.PORTAL,
                        chunkPos,
                        3
                );
        //?} else {
        /*level.getChunkSource()
                .addRegionTicket(
                        TicketType.PORTAL,
                        chunkPos,
                        3,
                        pos
                );
        *///?}

        level.getChunk(
                chunkPos.x,
                chunkPos.z
        );
    }
}
