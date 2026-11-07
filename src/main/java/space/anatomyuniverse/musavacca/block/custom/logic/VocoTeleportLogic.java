package space.anatomyuniverse.musavacca.block.custom.logic;

import java.util.UUID;
import space.anatomyuniverse.musavacca.teleport.TeleportDeviceIdentity;

import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import space.anatomyuniverse.musavacca.block.custom.VocoPostBlock;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoReceptorLogic.ReceptorPosition;
import space.anatomyuniverse.musavacca.block.entity.custom.VocoPostBlockEntity;
import space.anatomyuniverse.musavacca.block.entity.custom.VocoTableBlockEntity;
import space.anatomyuniverse.musavacca.teleport.HexTeleportAddressNetwork;
import space.anatomyuniverse.musavacca.teleport.HexTeleportDirectory;

public final class VocoTeleportLogic {
    private VocoTeleportLogic() {}

    public enum SyncResult {
        ACTIVE, QUEUED, INACTIVE
    }

    public static boolean syncEndpoint(ServerLevel level, BlockPos pos, ReceptorPosition receptor, boolean active, int hexColor) {
        SyncResult result = syncEndpointDetailed(level, pos, receptor, active, hexColor);
        return !active || result == SyncResult.ACTIVE;
    }

    public static SyncResult syncEndpointDetailed(ServerLevel level, BlockPos pos, ReceptorPosition receptor, boolean active, int hexColor) {
        MinecraftServer server = level.getServer();
        HexTeleportDirectory directory = HexTeleportDirectory.get(server);
        ReceptorPosition actualReceptor = actualReceptor(level, pos, receptor);
        BlockEntity owner = level.getBlockEntity(pos);
        if (!active) {
            HexTeleportAddressNetwork.releaseOwner(server, ownerKey(level, pos, actualReceptor));
            return SyncResult.INACTIVE;
        }
        HexTeleportDirectory.Kind kind = owner instanceof VocoTableBlockEntity ? HexTeleportDirectory.Kind.VOCO_TABLE_RECEPTOR_CORNER
                : HexTeleportDirectory.Kind.VOCO_POST_RECEPTOR_CORNER;
        if (owner instanceof VocoTableBlockEntity table) {
            table.setDeviceId(TeleportDeviceIdentity.unique(level, pos, table.getDeviceId()));
        } else if (owner instanceof VocoPostBlockEntity post) {
            post.setDeviceId(TeleportDeviceIdentity.unique(level, pos, post.getDeviceId()));
        }
        int normalizedHex = HexTeleportDirectory.normalizeHex(hexColor);
        UUID deviceId = owner instanceof VocoTableBlockEntity table ? table.getDeviceId()
                : owner instanceof VocoPostBlockEntity post ? post.getDeviceId() : null;
        HexTeleportDirectory.VocoRegistration registration =
                //? if >=1.21.11 {
                /*directory.registerOrQueueVocoEndpoint(deviceId, kind, normalizedHex, level.dimension().identifier(), pos,
                *///?} else {
                directory.registerOrQueueVocoEndpoint(deviceId, kind, normalizedHex, level.dimension().location(), pos,
                //?}
                        actualReceptor.id());
        HexTeleportDirectory.Endpoint removed = registration.removedActiveEndpoint();
        if (removed != null && removed.hexColor() != normalizedHex) {
            HexTeleportAddressNetwork.addressChanged(server, removed.hexColor(), normalizedHex);
        }
        if (registration.result().success()) return SyncResult.ACTIVE;
        return registration.result() == HexTeleportDirectory.Result.HEX_OCCUPIED ? SyncResult.QUEUED : SyncResult.INACTIVE;
    }

    public static String ownerKey(Level level, BlockPos pos, ReceptorPosition receptor) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof VocoTableBlockEntity table) {
            return HexTeleportDirectory.vocoTableReceptorCornerOwnerKey(table.getDeviceId(), receptor);
        }
        if (be instanceof VocoPostBlockEntity post) {
            return HexTeleportDirectory.vocoPostReceptorCornerOwnerKey(post.getDeviceId());
        }
        return "";
    }

    public static EndpointTarget getEndpointTarget(Level level, BlockPos pos, ReceptorPosition receptor) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof VocoTableBlockEntity tableBe) {
            if (tableBe.isCustomTargetEnabled(receptor)) {
                return new EndpointTarget(tableBe.getCustomTarget(receptor), new Facing(tableBe.getYawDegrees(receptor), tableBe.getPitchDegrees(receptor)),
                        true, tableBe.getCustomTargetDimension(receptor));
            }
            return defaultEndpointTarget(pos, receptor);
        }
        if (be instanceof VocoPostBlockEntity postBe) {
            ReceptorPosition postReceptor = actualReceptor(level, pos, receptor);
            if (postBe.isCustomTargetEnabled()) {
                return new EndpointTarget(postBe.getCustomTarget(), new Facing(postBe.getYawDegrees(), postBe.getPitchDegrees()), true,
                        postBe.getCustomTargetDimension());
            }
            return defaultEndpointTarget(pos, postReceptor);
        }
        return defaultEndpointTarget(pos, receptor);
    }

    public static Vec3 getCameraEditorPosition(BlockPos pos, ReceptorPosition receptor) {
        return getDefaultTeleportPosition(pos, receptor);
    }

    private static EndpointTarget defaultEndpointTarget(BlockPos pos, ReceptorPosition receptor) {
        return new EndpointTarget(getDefaultTeleportPosition(pos, receptor), defaultFacing(receptor), false, null);
    }

    private static Facing defaultFacing(ReceptorPosition receptor) {
        return new Facing(receptor.defaultYawDegrees(), receptor.defaultPitchDegrees());
    }

    public static Vec3 getDefaultTeleportPosition(BlockPos pos, ReceptorPosition receptor) {
        return switch (receptor) {
        case NORTH_EAST -> new Vec3(pos.getX() + 1.0D + 0.35D, pos.getY(), pos.getZ() - 0.35D);
        case NORTH_WEST -> new Vec3(pos.getX() - 0.35D, pos.getY(), pos.getZ() - 0.35D);
        case SOUTH_EAST -> new Vec3(pos.getX() + 1.0D + 0.35D, pos.getY(), pos.getZ() + 1.0D + 0.35D);
        case SOUTH_WEST -> new Vec3(pos.getX() - 0.35D, pos.getY(), pos.getZ() + 1.0D + 0.35D);
        };
    }

    private static ReceptorPosition actualReceptor(Level level, BlockPos pos, ReceptorPosition fallback) {
        BlockState state = level.getBlockState(pos);
        return state.getBlock() instanceof VocoPostBlock ? VocoPostBlock.receptorPosition(state) : fallback;
    }

    public record Facing(int yawDegrees, int pitchDegrees) {}

    public record EndpointTarget(Vec3 pos, Facing facing, boolean custom, String dimension) {}
}
