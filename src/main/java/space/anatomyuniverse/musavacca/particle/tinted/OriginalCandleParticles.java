package space.anatomyuniverse.musavacca.particle.tinted;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import space.anatomyuniverse.musavacca.tint.MusavaccaTints;

public final class OriginalCandleParticles {
    private static final ThreadLocal<Integer> COLOR = new ThreadLocal<>();
    private static final ThreadLocal<Vec3> MOUNT = new ThreadLocal<>();
    private OriginalCandleParticles() {}

    public static void animate(BlockState state, Level level, BlockPos pos, RandomSource random, int color) {
        animateMounted(state, level, pos, random, color, Vec3.ZERO);
    }

    public static void animateMounted(BlockState state, Level level, BlockPos ground, RandomSource random, int color, Vec3 translation) {
        if (!level.isClientSide()) return;
        Integer previousColor = swap(color == MusavaccaTints.NO_TINT ? null : color);
        Vec3 previousMount = mountOffset();
        setMountOffset(translation);
        try {
            state.getBlock().animateTick(state, level, ground, random);
        } finally {
            setMountOffset(previousMount);
            swap(previousColor);
        }
    }

    @Nullable public static Integer color() { return COLOR.get(); }

    public static Vec3 mountOffset() {
        var offset = MOUNT.get();
        return offset == null ? Vec3.ZERO : offset;
    }

    public static void setMountOffset(Vec3 offset) {
        if (offset.x == 0 && offset.z == 0) MOUNT.remove();
        else MOUNT.set(offset);
    }

    @Nullable
    public static Integer swap(@Nullable Integer color) {
        Integer previous = COLOR.get();
        if (color == null) COLOR.remove();
        else COLOR.set(color);
        return previous;
    }
}
