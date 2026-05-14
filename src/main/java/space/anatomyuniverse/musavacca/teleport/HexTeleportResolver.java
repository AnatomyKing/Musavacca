// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/teleport/HexTeleportResolver.java
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
import net.minecraft.world.level.portal.PortalShape;
import net.minecraft.world.phys.Vec3;
import space.anatomyuniverse.musavacca.block.ModBlocks;
import space.anatomyuniverse.musavacca.block.custom.VocoPostBlock;
import space.anatomyuniverse.musavacca.block.custom.VocoTableBlock;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoReceptorLogic.ReceptorPosition;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoTeleportLogic;
import space.anatomyuniverse.musavacca.block.entity.custom.PearlPortalBlockEntity;
import space.anatomyuniverse.musavacca.block.entity.custom.VocoPostBlockEntity;
import space.anatomyuniverse.musavacca.block.entity.custom.VocoTableBlockEntity;

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

    public static boolean teleportToHex(ServerPlayer player, int hexColor) {
        MinecraftServer server = player.level().getServer();
        if (server == null) {
            return false;
        }

        int normalizedHex = HexTeleportDirectory.normalizeHex(hexColor);
        HexTeleportDirectory directory = HexTeleportDirectory.get(server);
        List<HexTeleportDirectory.Endpoint> endpoints = directory.getEndpointsByHex(normalizedHex);

        for (HexTeleportDirectory.Endpoint endpoint : endpoints) {
            Optional<ResolvedEndpoint> resolved = resolveEndpoint(server, endpoint);

            if (resolved.isPresent()) {
                teleport(player, resolved.get());
                return true;
            }

            VocoTeleportLogic.removeEndpointAndPromote(server, endpoint);
        }

        player.displayClientMessage(
                Component.literal("No active teleport address found for #" + HexTeleportDirectory.toHex(normalizedHex) + "."),
                true
        );

        return false;
    }

    public static boolean teleportToOwner(ServerPlayer player, String ownerKey) {
        MinecraftServer server = player.level().getServer();
        if (server == null) {
            return false;
        }

        HexTeleportDirectory directory = HexTeleportDirectory.get(server);
        HexTeleportDirectory.Endpoint endpoint = directory.getEndpointByOwner(ownerKey).orElse(null);

        if (endpoint == null) {
            player.displayClientMessage(Component.literal("This endpoint is not registered."), true);
            return false;
        }

        Optional<ResolvedEndpoint> resolved = resolveEndpoint(server, endpoint);
        if (resolved.isEmpty()) {
            VocoTeleportLogic.removeEndpointAndPromote(server, endpoint);
            player.displayClientMessage(Component.literal("This endpoint was stale and has been cleaned up."), true);
            return false;
        }

        teleport(player, resolved.get());
        return true;
    }

    public static Optional<ResolvedEndpoint> resolveLinkedPortal(ServerLevel sourceLevel, UUID sourcePortalId) {
        MinecraftServer server = sourceLevel.getServer();
        HexTeleportDirectory directory = HexTeleportDirectory.get(server);

        HexTeleportDirectory.Endpoint target = directory.getLinkedPortalEndpoint(sourcePortalId).orElse(null);
        if (target == null) {
            return Optional.empty();
        }

        Optional<ResolvedEndpoint> resolved = resolveEndpoint(server, target);

        if (resolved.isEmpty()) {
            VocoTeleportLogic.removeEndpointAndPromote(server, target);
        }

        return resolved;
    }

    public static Optional<ResolvedEndpoint> resolveEndpoint(MinecraftServer server, HexTeleportDirectory.Endpoint endpoint) {
        ResourceKey<Level> dimensionKey = ResourceKey.create(
                Registries.DIMENSION,
                endpoint.dimensionId()
        );

        ServerLevel level = server.getLevel(dimensionKey);
        if (level == null) {
            return Optional.empty();
        }

        keepChunkLoaded(level, endpoint.ownerPos());

        if (!isEndpointStillValid(level, endpoint)) {
            return Optional.empty();
        }

        keepChunkLoaded(level, BlockPos.containing(endpoint.target().vec3()));

        return Optional.of(new ResolvedEndpoint(endpoint.endpointId(), level, endpoint));
    }

    private static boolean isEndpointStillValid(ServerLevel level, HexTeleportDirectory.Endpoint endpoint) {
        BlockPos pos = endpoint.ownerPos();
        BlockState state = level.getBlockState(pos);

        return switch (endpoint.kind()) {
            case PEARL_PORTAL -> isValidPearlPortalEndpoint(level, pos, endpoint);
            case VOCO_POST_RECEPTOR_CORNER -> isValidVocoPostReceptorCornerEndpoint(level, pos, state, endpoint);
            case VOCO_TABLE_RECEPTOR_CORNER -> isValidVocoTableReceptorCornerEndpoint(level, pos, state, endpoint);
        };
    }

    private static boolean isValidPearlPortalEndpoint(
            ServerLevel level,
            BlockPos pos,
            HexTeleportDirectory.Endpoint endpoint
    ) {
        if (!level.getBlockState(pos).is(ModBlocks.PEARL_PORTAL.get())) {
            return false;
        }

        if (!(level.getBlockEntity(pos) instanceof PearlPortalBlockEntity portalBe)) {
            return false;
        }

        return portalBe.isValidPortalTile()
                && endpoint.endpointId().equals(portalBe.getPortalId())
                && HexTeleportDirectory.normalizeHex(portalBe.getHexColor()) == endpoint.hexColor();
    }

    private static boolean isValidVocoPostReceptorCornerEndpoint(
            ServerLevel level,
            BlockPos pos,
            BlockState state,
            HexTeleportDirectory.Endpoint endpoint
    ) {
        if (!(state.getBlock() instanceof VocoPostBlock)) {
            return false;
        }

        if (!state.hasProperty(VocoPostBlock.PORTAL) || !state.getValue(VocoPostBlock.PORTAL)) {
            return false;
        }

        if (!(level.getBlockEntity(pos) instanceof VocoPostBlockEntity postBe)) {
            return false;
        }

        return postBe.hasHexColor()
                && HexTeleportDirectory.normalizeHex(postBe.getHexColor()) == endpoint.hexColor();
    }

    private static boolean isValidVocoTableReceptorCornerEndpoint(
            ServerLevel level,
            BlockPos pos,
            BlockState state,
            HexTeleportDirectory.Endpoint endpoint
    ) {
        if (!(state.getBlock() instanceof VocoTableBlock)) {
            return false;
        }

        if (!(level.getBlockEntity(pos) instanceof VocoTableBlockEntity tableBe)) {
            return false;
        }

        ReceptorPosition receptor = ReceptorPosition.byId(endpoint.slotId());

        if (!state.hasProperty(VocoTableBlock.portalProperty(receptor))
                || !state.getValue(VocoTableBlock.portalProperty(receptor))) {
            return false;
        }

        return HexTeleportDirectory.normalizeHex(tableBe.getPortalHexColorOrUnset(receptor)) == endpoint.hexColor();
    }

    public static void teleport(ServerPlayer player, ResolvedEndpoint resolved) {
        ServerLevel targetLevel = resolved.level();
        Vec3 wantedPos = resolved.targetPos();

        EntityDimensions dimensions = player.getDimensions(player.getPose());
        Vec3 safePos = PortalShape.findCollisionFreePosition(
                wantedPos,
                targetLevel,
                player,
                dimensions
        );

        if (player.level() == targetLevel) {
            player.connection.teleport(
                    safePos.x,
                    safePos.y,
                    safePos.z,
                    resolved.yaw(),
                    resolved.pitch()
            );
        } else {
            player.teleportTo(
                    targetLevel,
                    safePos.x,
                    safePos.y,
                    safePos.z,
                    Set.of(),
                    resolved.yaw(),
                    resolved.pitch(),
                    true
            );
        }

        player.setYHeadRot(resolved.yaw());
        player.setYBodyRot(resolved.yaw());
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

    public static void keepChunkLoaded(ServerLevel level, BlockPos pos) {
        ChunkPos chunkPos = new ChunkPos(pos);

        level.getChunkSource().addTicketWithRadius(
                TicketType.PORTAL,
                chunkPos,
                3
        );

        level.getChunk(chunkPos.x, chunkPos.z);
    }
}