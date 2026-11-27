package space.anatomyuniverse.musavacca.block.custom.logic;

import net.minecraft.client.particle.CampfireSmokeParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SmokeParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import space.anatomyuniverse.musavacca.particle.tinted.ParticleCapture;
import space.anatomyuniverse.musavacca.particle.tinted.ParticleTint;
import space.anatomyuniverse.musavacca.tint.PearlFireTintProfiles;

import java.util.function.Predicate;
import java.util.function.Supplier;

public final class PearlFlameParticles {
    private PearlFlameParticles() {}

    public static void animate(BlockState state, Level level, BlockPos pos, RandomSource random, int color) {
        animateMounted(state, level, pos, random, color, Vec3.ZERO);
    }

    public static void animateMounted(BlockState state, Level level, BlockPos pos, RandomSource random, int color, Vec3 offset) {
        if (!level.isClientSide()) return;
        ParticleCapture.run(Client.tint(state, color), offset, () -> state.getBlock().animateTick(state, level, pos, random));
    }

    public static void run(Level level, BlockState state, int color, Runnable emit) {
        if (level.isClientSide()) ParticleCapture.run(Client.tint(state, color), Vec3.ZERO, emit);
        else emit.run();
    }

    public static <T> T call(Level level, BlockState state, int color, Supplier<T> emit) {
        return level.isClientSide() ? ParticleCapture.call(Client.tint(state, color), Vec3.ZERO, emit) : emit.get();
    }

    private static final class Client {
        private static final Predicate<Particle> FILTER = particle ->
                !(particle instanceof SmokeParticle) && !(particle instanceof CampfireSmokeParticle);

        @Nullable
        private static ParticleTint tint(BlockState state, int color) {
            return ParticleTint.of(color, PearlFireTintProfiles.forFlame(state), FILTER);
        }
    }
}
