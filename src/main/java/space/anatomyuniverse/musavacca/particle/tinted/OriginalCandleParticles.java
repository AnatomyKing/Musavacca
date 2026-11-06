package space.anatomyuniverse.musavacca.particle.tinted;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public final class OriginalCandleParticles {
    private static final ThreadLocal<Integer> COLOR = new ThreadLocal<>();
    private OriginalCandleParticles() {}

    public static void animate(BlockState state, Level level, BlockPos pos, RandomSource random, int color) {
        if (!level.isClientSide()) return;
        Integer previous = COLOR.get();
        COLOR.set(color);
        try {
            state.getBlock().animateTick(state, level, pos, random);
        } finally {
            if (previous == null) COLOR.remove();
            else COLOR.set(previous);
        }
    }

    public static Integer color() { return COLOR.get(); }
}
