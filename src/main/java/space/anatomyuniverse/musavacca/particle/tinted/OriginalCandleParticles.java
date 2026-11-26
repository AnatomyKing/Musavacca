package space.anatomyuniverse.musavacca.particle.tinted;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import space.anatomyuniverse.musavacca.tint.MusavaccaTints;
import space.anatomyuniverse.musavacca.tint.PearlFireTintProfiles;
import space.anatomyuniverse.musavacca.tint.PearlFireTintProfiles.Settings;

public final class OriginalCandleParticles {
    private static final ThreadLocal<Context> CONTEXT = new ThreadLocal<>();
    private static final ThreadLocal<Vec3> MOUNT = new ThreadLocal<>();

    private OriginalCandleParticles() {}

    public record Context(int color, Settings settings) {}

    @Nullable
    public static Context context(BlockState state, int color) {
        if (!MusavaccaTints.hasTint(color)) return null;
        Settings settings = state.is(BlockTags.CAMPFIRES) ? PearlFireTintProfiles.PEARL_CAMPFIRE
                : state.is(BlockTags.CANDLE_CAKES) ? PearlFireTintProfiles.PEARL_CANDLE_CAKE
                : PearlFireTintProfiles.PEARL_CANDLE;
        return new Context(color, settings);
    }

    public static void animate(BlockState state, Level level, BlockPos pos, RandomSource random, int color) {
        animateMounted(state, level, pos, random, color, Vec3.ZERO);
    }

    public static void animateMounted(BlockState state, Level level, BlockPos ground, RandomSource random, int color, Vec3 translation) {
        if (!level.isClientSide()) return;
        Context previous = swap(context(state, color));
        Vec3 previousMount = mountOffset();
        setMountOffset(translation);
        try {
            state.getBlock().animateTick(state, level, ground, random);
        } finally {
            setMountOffset(previousMount);
            swap(previous);
        }
    }

    @Nullable
    public static Context context() {
        return CONTEXT.get();
    }

    public static Vec3 mountOffset() {
        var offset = MOUNT.get();
        return offset == null ? Vec3.ZERO : offset;
    }

    public static void setMountOffset(Vec3 offset) {
        if (offset.x == 0 && offset.z == 0) MOUNT.remove();
        else MOUNT.set(offset);
    }

    @Nullable
    public static Context swap(@Nullable Context context) {
        Context previous = CONTEXT.get();
        if (context == null) CONTEXT.remove();
        else CONTEXT.set(context);
        return previous;
    }
}
