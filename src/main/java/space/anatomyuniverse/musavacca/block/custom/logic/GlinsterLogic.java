
package space.anatomyuniverse.musavacca.block.custom.logic;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import space.anatomyuniverse.musavacca.particle.ModParticleTypes;

public final class GlinsterLogic {
    private GlinsterLogic() {}

    public static void animateTick(Level level, BlockPos pos, RandomSource random) {
        if (random.nextInt(4) != 0) return;

        for (int i = 0; i < 2; i++) {
            Direction face = Direction.getRandom(random);
            BlockPos adjacent = pos.relative(face);
            if (level.getBlockState(adjacent).isCollisionShapeFullBlock(level, adjacent)) continue;

            double x = pos.getX() + (face.getStepX() == 0 ? random.nextDouble() : 0.5D + face.getStepX() * 0.55D);
            double y = pos.getY() + (face.getStepY() == 0 ? random.nextDouble() : 0.5D + face.getStepY() * 0.55D);
            double z = pos.getZ() + (face.getStepZ() == 0 ? random.nextDouble() : 0.5D + face.getStepZ() * 0.55D);

            level.addParticle(ModParticleTypes.GLINSTER.get(), x, y, z, 0.0D, 0.0D, 0.0D);
        }
    }


    public static void burst(ServerLevel level, BlockPos pos) {
        level.sendParticles(ModParticleTypes.GLINSTER_BURST.get(),
                            pos.getX() + 0.5D, pos.getY() + 1.20D, pos.getZ() + 0.5D,
                            8, 0.12D, 0.12D, 0.12D, 0.006D);
    }



}
