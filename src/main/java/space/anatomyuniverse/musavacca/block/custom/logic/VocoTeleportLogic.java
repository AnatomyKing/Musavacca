package space.anatomyuniverse.musavacca.block.custom.logic;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import space.anatomyuniverse.musavacca.block.entity.custom.VocoReceptorBlockEntity;
import space.anatomyuniverse.musavacca.block.entity.custom.VocoTableBlockEntity;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoInteractLogic.ReceptorPosition;

public final class VocoTeleportLogic {
    private static final double TELEPORT_CLEARANCE = 0.35D;

    private VocoTeleportLogic() {}

    public static void teleportToReceptor(
            Level level,
            BlockPos pos,
            Player player,
            ReceptorPosition receptor
    ) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        Vec3 target = getTeleportPosition(pos, receptor);
        Facing facing = getFacing(level, pos, receptor);

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

    public static Facing getFacing(Level level, BlockPos pos, ReceptorPosition receptor) {
        BlockEntity be = level.getBlockEntity(pos);

        if (be instanceof VocoTableBlockEntity tableBe) {
            return new Facing(
                    tableBe.getYawDegrees(receptor),
                    tableBe.getPitchDegrees(receptor)
            );
        }

        if (be instanceof VocoReceptorBlockEntity receptorBe) {
            return new Facing(
                    receptorBe.getYawDegrees(),
                    receptorBe.getPitchDegrees()
            );
        }

        return new Facing(
                receptor.defaultYawDegrees(),
                receptor.defaultPitchDegrees()
        );
    }

    public static Vec3 getTeleportPosition(BlockPos pos, ReceptorPosition receptor) {
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

    public record Facing(int yawDegrees, int pitchDegrees) {}
}