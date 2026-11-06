package space.anatomyuniverse.musavacca.particle.tinted;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public final class OriginalCandleParticles {
    private static final ThreadLocal<Boolean> ACTIVE = ThreadLocal.withInitial(() -> false);
    private OriginalCandleParticles() {}

    public static void animate(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!level.isClientSide()) return;
        boolean previous = ACTIVE.get();
        ACTIVE.set(true);
        try {
            state.getBlock().animateTick(state, level, pos, random);
        } finally {
            ACTIVE.set(previous);
        }
    }

    public static boolean isActive() {
        return ACTIVE.get();
    }
}
