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
import net.minecraft.world.level.portal.PortalShape;
import net.minecraft.world.phys.Vec3;
import space.anatomyuniverse.musavacca.block.ModBlocks;
import space.anatomyuniverse.musavacca.block.custom.MusavaccaPortalDoorBlock;
import space.anatomyuniverse.musavacca.block.custom.MusavaccaPortalTrapdoorBlock;
import space.anatomyuniverse.musavacca.block.custom.VocoPostBlock;
import space.anatomyuniverse.musavacca.block.custom.VocoTableBlock;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoReceptorLogic.ReceptorPosition;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoTeleportLogic;
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
import space.anatomyuniverse.musavacca.economy.TeleportEconomy;

public final class HexTeleportResolver {
    private HexTeleportResolver() {}

    public record ResolvedEndpoint(UUID endpointId, ServerLevel level, HexTeleportDirectory.Endpoint endpoint, Vec3 targetPos, float yaw, float pitch) {}

    public record ResolvedDoorEndpoint(ServerLevel level, HexTeleportDirectory.DoorEndpoint endpoint) {}

    public record ResolvedTrapdoorEndpoint(ServerLevel level, HexTeleportDirectory.DoorEndpoint endpoint) {}

    public static boolean teleportToHex(ServerPlayer player, int hexColor, int cost) {
        MinecraftServer server = player.level().getServer();
        if (server == null) return false;
        int normalizedHex = HexTeleportDirectory.normalizeHex(hexColor);
        HexTeleportDirectory directory = HexTeleportDirectory.get(server);
        HexTeleportDirectory.PhoneRegistration phone = directory.getPhoneRegistrationByHex(normalizedHex).orElse(null);
        if (phone != null) return VocoCallerNetwork.teleportToPhone(player, phone, cost);
        Set<UUID> attempted = new HashSet<>();
        while (true) {
            List<HexTeleportDirectory.Endpoint> endpoints = directory.getEndpointsByHex(normalizedHex);
            HexTeleportDirectory.Endpoint next = null;
            for (HexTeleportDirectory.Endpoint endpoint : endpoints) {
                if (attempted.add(endpoint.endpointId())) {
                    next = endpoint;
                    break;
                }
            }
            if (next == null) break;
            Optional<ResolvedEndpoint> resolved = resolveEndpoint(server, next);
            if (resolved.isPresent()) {
                ResolvedEndpoint target = resolved.get();
                return TeleportEconomy.tryTeleport(player, cost, () -> teleport(player, target));
            }

        }
        //? if >=26.1 {
        /*player.sendOverlayMessage(Component.literal("No active teleport address found for #" + HexTeleportDirectory.toHex(normalizedHex) + "."));
        *///?} else {
        player.displayClientMessage(Component.literal("No active teleport address found for #" + HexTeleportDirectory.toHex(normalizedHex) + "."), true);
        //?}
        return false;
    }

    public static Optional<ResolvedEndpoint> resolveLinkedPortal(ServerLevel sourceLevel, UUID sourcePortalId) {
        MinecraftServer server = sourceLevel.getServer();
        HexTeleportDirectory directory = HexTeleportDirectory.get(server);
        HexTeleportDirectory.Endpoint target = directory.getLinkedPortalEndpoint(sourcePortalId).orElse(null);
        if (target == null) return Optional.empty();
        return resolveEndpoint(server, target);
    }

    public static Optional<ResolvedDoorEndpoint> resolveLinkedDoor(ServerLevel sourceLevel, String sourceOwnerKey) {
        MinecraftServer server = sourceLevel.getServer();
        HexTeleportDirectory directory = HexTeleportDirectory.get(server);
        HexTeleportDirectory.DoorEndpoint target = directory.getLinkedDoorEndpoint(sourceOwnerKey).orElse(null);
        if (target == null) return Optional.empty();
        return resolveDoorEndpoint(server, target);
    }

    public static Optional<ResolvedTrapdoorEndpoint> resolveLinkedTrapdoor(ServerLevel sourceLevel, String sourceOwnerKey) {
        MinecraftServer server = sourceLevel.getServer();
        HexTeleportDirectory directory = HexTeleportDirectory.get(server);
        HexTeleportDirectory.DoorEndpoint target = directory.getLinkedTrapdoorEndpoint(sourceOwnerKey).orElse(null);
        if (target == null) return Optional.empty();
        return resolveTrapdoorEndpoint(server, target);
    }

    public static Optional<ResolvedEndpoint> resolveEndpoint(MinecraftServer server, HexTeleportDirectory.Endpoint endpoint) {
        if (server == null || endpoint == null || endpoint.dimensionId() == null) return Optional.empty();
        ServerLevel ownerLevel = server.getLevel(ResourceKey.create(Registries.DIMENSION, endpoint.dimensionId()));
        if (ownerLevel == null) return Optional.empty();
        keepChunkLoaded(ownerLevel, endpoint.ownerPos());
        if (!isEndpointStillValid(ownerLevel, endpoint)) return Optional.empty();

        ServerLevel destinationLevel = ownerLevel;
        Vec3 targetPos;
        float yaw = 0;
        float pitch = 0;
        if (endpoint.kind().isPortal()) {
            if (!(ownerLevel.getBlockEntity(endpoint.ownerPos()) instanceof PearlPortalBlockEntity portal)) return Optional.empty();
            targetPos = portal.getPortalShape().center();
        } else if (endpoint.kind().isVoco()) {
            VocoTeleportLogic.EndpointTarget target = VocoTeleportLogic.getEndpointTarget(ownerLevel, endpoint.ownerPos(),
                    ReceptorPosition.byId(endpoint.slotId()));
            targetPos = target.pos();
            yaw = target.facing().yawDegrees();
            pitch = target.facing().pitchDegrees();
            if (target.custom() && target.dimension() != null && !target.dimension().isEmpty()) {
                try {
                    //? if >=1.21.11 {
                    /*destinationLevel = server.getLevel(ResourceKey.create(Registries.DIMENSION, net.minecraft.resources.Identifier.parse(target.dimension())));
                    *///?} else {
                    destinationLevel = server
                            .getLevel(ResourceKey.create(Registries.DIMENSION, net.minecraft.resources.ResourceLocation.parse(target.dimension())));
                    //?}
                } catch (IllegalArgumentException invalid) {
                    return Optional.empty();
                }
                if (destinationLevel == null) return Optional.empty();
            }
        } else return Optional.empty();
        keepChunkLoaded(destinationLevel, BlockPos.containing(targetPos));
        return Optional.of(new ResolvedEndpoint(endpoint.endpointId(), destinationLevel, endpoint, targetPos, yaw, pitch));
    }

    public static Optional<ResolvedDoorEndpoint> resolveDoorEndpoint(MinecraftServer server, HexTeleportDirectory.DoorEndpoint endpoint) {
        if (server == null || endpoint == null || endpoint.dimensionId() == null) {
            return Optional.empty();
        }
        ResourceKey<Level> dimensionKey = ResourceKey.create(Registries.DIMENSION, endpoint.dimensionId());
        ServerLevel level = server.getLevel(dimensionKey);
        if (level == null) return Optional.empty();
        keepChunkLoaded(level, endpoint.ownerPos());
        if (!isDoorEndpointStillValid(level, endpoint)) {
            return Optional.empty();
        }
        return Optional.of(new ResolvedDoorEndpoint(level, endpoint));
    }

    public static Optional<ResolvedTrapdoorEndpoint> resolveTrapdoorEndpoint(MinecraftServer server, HexTeleportDirectory.DoorEndpoint endpoint) {
        if (server == null || endpoint == null || endpoint.dimensionId() == null) {
            return Optional.empty();
        }
        ResourceKey<Level> dimensionKey = ResourceKey.create(Registries.DIMENSION, endpoint.dimensionId());
        ServerLevel level = server.getLevel(dimensionKey);
        if (level == null) return Optional.empty();
        keepChunkLoaded(level, endpoint.ownerPos());
        if (!isTrapdoorEndpointStillValid(level, endpoint)) {
            return Optional.empty();
        }
        return Optional.of(new ResolvedTrapdoorEndpoint(level, endpoint));
    }

    private static boolean isEndpointStillValid(ServerLevel level, HexTeleportDirectory.Endpoint endpoint) {
        BlockPos pos = endpoint.ownerPos();
        BlockState state = level.getBlockState(pos);
        return switch (endpoint.kind()) {
        case PEARL_PORTAL -> isValidPearlPortalEndpoint(level, pos, endpoint);
        case VOCO_POST_RECEPTOR_CORNER -> isValidVocoPostReceptorCornerEndpoint(level, pos, state, endpoint);
        case VOCO_TABLE_RECEPTOR_CORNER -> isValidVocoTableReceptorCornerEndpoint(level, pos, state, endpoint);
        default -> false;
        };
    }

    private static boolean isDoorEndpointStillValid(ServerLevel level, HexTeleportDirectory.DoorEndpoint endpoint) {
        BlockPos pos = endpoint.ownerPos();
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof MusavaccaPortalDoorBlock) || state.getValue(MusavaccaPortalDoorBlock.HALF) != DoubleBlockHalf.LOWER) {
            return false;
        }
        if (!(level.getBlockEntity(pos) instanceof MusavaccaPortalDoorBlockEntity doorBe) || !doorBe.hasHexColor()) {
            return false;
        }

        return endpoint.deviceId().equals(doorBe.getDeviceId()) && state.getValue(MusavaccaPortalDoorBlock.LIT)
                && state.getValue(MusavaccaPortalDoorBlock.LIT_PORTAL) && state.getValue(MusavaccaPortalDoorBlock.PORTAL)
                && HexTeleportDirectory.normalizeHex(doorBe.getHexColor()) == endpoint.hexColor();
    }

    private static boolean isTrapdoorEndpointStillValid(ServerLevel level, HexTeleportDirectory.DoorEndpoint endpoint) {
        BlockPos pos = endpoint.ownerPos();
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof MusavaccaPortalTrapdoorBlock)) {
            return false;
        }
        if (!(level.getBlockEntity(pos) instanceof MusavaccaPortalTrapdoorBlockEntity trapdoorBe) || !trapdoorBe.hasHexColor()) {
            return false;
        }

        return endpoint.deviceId().equals(trapdoorBe.getDeviceId()) && state.getValue(MusavaccaPortalTrapdoorBlock.LIT)
                && state.getValue(MusavaccaPortalTrapdoorBlock.LIT_PORTAL) && state.getValue(MusavaccaPortalTrapdoorBlock.PORTAL)
                && HexTeleportDirectory.normalizeHex(trapdoorBe.getHexColor()) == endpoint.hexColor();
    }

    private static boolean isValidPearlPortalEndpoint(ServerLevel level, BlockPos pos, HexTeleportDirectory.Endpoint endpoint) {
        if (!level.getBlockState(pos).is(ModBlocks.PEARL_PORTAL.get())) {
            return false;
        }
        if (!(level.getBlockEntity(pos) instanceof PearlPortalBlockEntity portalBe)) {
            return false;
        }
        return portalBe.isValidPortalTile() && endpoint.endpointId().equals(portalBe.getPortalId())
                && HexTeleportDirectory.normalizeHex(portalBe.getHexColor()) == endpoint.hexColor();
    }

    private static boolean isValidVocoPostReceptorCornerEndpoint(ServerLevel level, BlockPos pos, BlockState state, HexTeleportDirectory.Endpoint endpoint) {
        if (!(state.getBlock() instanceof VocoPostBlock)) {
            return false;
        }
        if (!state.hasProperty(VocoPostBlock.PORTAL) || !state.getValue(VocoPostBlock.PORTAL)) {
            return false;
        }
        if (!(level.getBlockEntity(pos) instanceof VocoPostBlockEntity postBe)) {
            return false;
        }
        return endpoint.deviceId().equals(postBe.getDeviceId()) && postBe.hasHexColor()
                && HexTeleportDirectory.normalizeHex(postBe.getHexColor()) == endpoint.hexColor();
    }

    private static boolean isValidVocoTableReceptorCornerEndpoint(ServerLevel level, BlockPos pos, BlockState state, HexTeleportDirectory.Endpoint endpoint) {
        if (!(state.getBlock() instanceof VocoTableBlock)) {
            return false;
        }
        if (!(level.getBlockEntity(pos) instanceof VocoTableBlockEntity tableBe)) {
            return false;
        }
        ReceptorPosition receptor = ReceptorPosition.byId(endpoint.slotId());
        if (!state.hasProperty(VocoTableBlock.portalProperty(receptor)) || !state.getValue(VocoTableBlock.portalProperty(receptor))) {
            return false;
        }
        return endpoint.deviceId().equals(tableBe.getDeviceId())
                && HexTeleportDirectory.normalizeHex(tableBe.getPortalHexColorOrNoTint(receptor)) == endpoint.hexColor();
    }

    public static boolean teleport(ServerPlayer player, ResolvedEndpoint resolved) {
        return teleportToTarget(player, resolved.level(), resolved.targetPos(), resolved.yaw(), resolved.pitch());
    }

    public static boolean teleportToTarget(ServerPlayer player, ServerLevel targetLevel, Vec3 wantedPos, float yaw, float pitch) {
        EntityDimensions dimensions = player.getDimensions(player.getPose());
        Vec3 safePos = PortalShape.findCollisionFreePosition(wantedPos, targetLevel, player, dimensions);
        HexTeleportPreloader.prepare(player, targetLevel, safePos);
        if (player.level() == targetLevel) {
            player.connection.teleport(safePos.x, safePos.y, safePos.z, yaw, pitch);
        //? if >=1.21.2 {
        } else if (!player.teleportTo(targetLevel, safePos.x, safePos.y, safePos.z, Set.of(), yaw, pitch, true)) return false;
        //?} else {
        /*} else if (!player.teleportTo(targetLevel, safePos.x, safePos.y, safePos.z, Set.of(), yaw, pitch)) return false;
        *///?}
        player.setYHeadRot(yaw);
        player.setYBodyRot(yaw);
        player.setPortalCooldown();
        targetLevel.playSound(null, safePos.x, safePos.y, safePos.z, SoundEvents.PORTAL_TRAVEL, SoundSource.PLAYERS, 0.35F, 1.15F);
        return true;
    }

    public static void keepChunkLoaded(ServerLevel level, BlockPos pos) {
        //? if >=26.1 {
        /*ChunkPos chunkPos = ChunkPos.containing(pos);
        *///?} else {
        ChunkPos chunkPos = new ChunkPos(pos);
        //?}
        //? if >=1.21.5 {
        level.getChunkSource().addTicketWithRadius(TicketType.PORTAL, chunkPos, 3);
        //?} else {
        /*level.getChunkSource().addRegionTicket(TicketType.PORTAL, chunkPos, 3, pos);
        *///?}
        level.getChunk(pos.getX() >> 4, pos.getZ() >> 4);
    }
}
