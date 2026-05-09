// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/block/custom/logic/VocoTeleportLogic.java
package space.anatomyuniverse.musavacca.block.custom.logic;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import space.anatomyuniverse.musavacca.block.custom.VocoPostBlock;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoReceptorLogic.ReceptorPosition;
import space.anatomyuniverse.musavacca.block.entity.custom.VocoPostBlockEntity;
import space.anatomyuniverse.musavacca.block.entity.custom.VocoTableBlockEntity;
import space.anatomyuniverse.musavacca.teleport.HexTeleportDirectory;
import space.anatomyuniverse.musavacca.teleport.HexTeleportResolver;

public final class VocoTeleportLogic {
    private static final double TELEPORT_CLEARANCE = 0.35D;

    private VocoTeleportLogic() {}

    public static void teleportToReceptorCorner(
            Level level,
            BlockPos pos,
            Player player,
            ReceptorPosition receptor
    ) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        ReceptorPosition actualReceptor = actualReceptor(level, pos, receptor);
        String ownerKey = ownerKey(level, pos, actualReceptor);

        if (HexTeleportResolver.teleportToOwner(serverPlayer, ownerKey)) {
            return;
        }

        Vec3 target = getDefaultTeleportPosition(pos, actualReceptor);
        Facing facing = getFacing(level, pos, actualReceptor);

        serverPlayer.connection.teleport(
                target.x,
                target.y,
                target.z,
                facing.yawDegrees(),
                facing.pitchDegrees()
        );

        serverPlayer.setYHeadRot(facing.yawDegrees());
        serverPlayer.setYBodyRot(facing.yawDegrees());
    }

    public static boolean syncEndpoint(
            ServerLevel level,
            BlockPos pos,
            ReceptorPosition receptor,
            boolean active,
            int hexColor
    ) {
        HexTeleportDirectory directory = HexTeleportDirectory.get(level.getServer());
        ReceptorPosition actualReceptor = actualReceptor(level, pos, receptor);
        String ownerKey = ownerKey(level, pos, actualReceptor);

        if (!active) {
            directory.removeOwner(ownerKey);
            return true;
        }

        EndpointTarget target = getEndpointTarget(level, pos, actualReceptor);

        HexTeleportDirectory.Kind kind =
                level.getBlockEntity(pos) instanceof VocoTableBlockEntity
                        ? HexTeleportDirectory.Kind.VOCO_TABLE_RECEPTOR_CORNER
                        : HexTeleportDirectory.Kind.VOCO_POST_RECEPTOR_CORNER;

        return directory.registerVocoEndpoint(
                ownerKey,
                kind,
                hexColor,
                level.dimension().location(),
                pos,
                target.pos(),
                target.facing().yawDegrees(),
                target.facing().pitchDegrees(),
                target.custom(),
                actualReceptor.id()
        ).success();
    }

    public static String ownerKey(Level level, BlockPos pos, ReceptorPosition receptor) {
        if (level.getBlockEntity(pos) instanceof VocoTableBlockEntity) {
            return HexTeleportDirectory.vocoTableReceptorCornerOwnerKey(
                    level.dimension().location(),
                    pos,
                    receptor
            );
        }

        return HexTeleportDirectory.vocoPostReceptorCornerOwnerKey(
                level.dimension().location(),
                pos
        );
    }

    public static EndpointTarget getEndpointTarget(Level level, BlockPos pos, ReceptorPosition receptor) {
        BlockEntity be = level.getBlockEntity(pos);

        if (be instanceof VocoTableBlockEntity tableBe && tableBe.isCustomTargetEnabled(receptor)) {
            return new EndpointTarget(
                    tableBe.getCustomTarget(receptor),
                    new Facing(tableBe.getYawDegrees(receptor), tableBe.getPitchDegrees(receptor)),
                    true
            );
        }

        if (be instanceof VocoPostBlockEntity postBe) {
            ReceptorPosition postReceptor = actualReceptor(level, pos, receptor);

            if (postBe.isCustomTargetEnabled()) {
                return new EndpointTarget(
                        postBe.getCustomTarget(),
                        new Facing(postBe.getYawDegrees(), postBe.getPitchDegrees()),
                        true
                );
            }

            return new EndpointTarget(
                    getDefaultTeleportPosition(pos, postReceptor),
                    new Facing(postBe.getYawDegrees(), postBe.getPitchDegrees()),
                    false
            );
        }

        return new EndpointTarget(
                getDefaultTeleportPosition(pos, receptor),
                getFacing(level, pos, receptor),
                false
        );
    }

    public static Facing getFacing(Level level, BlockPos pos, ReceptorPosition receptor) {
        BlockEntity be = level.getBlockEntity(pos);

        if (be instanceof VocoTableBlockEntity tableBe) {
            return new Facing(
                    tableBe.getYawDegrees(receptor),
                    tableBe.getPitchDegrees(receptor)
            );
        }

        if (be instanceof VocoPostBlockEntity postBe) {
            return new Facing(
                    postBe.getYawDegrees(),
                    postBe.getPitchDegrees()
            );
        }

        return new Facing(
                receptor.defaultYawDegrees(),
                receptor.defaultPitchDegrees()
        );
    }

    public static Vec3 getDefaultTeleportPosition(BlockPos pos, ReceptorPosition receptor) {
        return switch (receptor) {
            case NORTH_EAST -> new Vec3(
                    pos.getX() + 1.0D + TELEPORT_CLEARANCE,
                    pos.getY(),
                    pos.getZ() - TELEPORT_CLEARANCE
            );

            case NORTH_WEST -> new Vec3(
                    pos.getX() - TELEPORT_CLEARANCE,
                    pos.getY(),
                    pos.getZ() - TELEPORT_CLEARANCE
            );

            case SOUTH_EAST -> new Vec3(
                    pos.getX() + 1.0D + TELEPORT_CLEARANCE,
                    pos.getY(),
                    pos.getZ() + 1.0D + TELEPORT_CLEARANCE
            );

            case SOUTH_WEST -> new Vec3(
                    pos.getX() - TELEPORT_CLEARANCE,
                    pos.getY(),
                    pos.getZ() + 1.0D + TELEPORT_CLEARANCE
            );
        };
    }

    private static ReceptorPosition actualReceptor(Level level, BlockPos pos, ReceptorPosition fallback) {
        BlockState state = level.getBlockState(pos);

        if (state.getBlock() instanceof VocoPostBlock) {
            return VocoPostBlock.receptorPosition(state);
        }

        return fallback;
    }

    public record Facing(int yawDegrees, int pitchDegrees) {}

    public record EndpointTarget(Vec3 pos, Facing facing, boolean custom) {}
}