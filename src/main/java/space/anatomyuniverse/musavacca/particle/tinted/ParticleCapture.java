package space.anatomyuniverse.musavacca.particle.tinted;

import net.minecraft.client.particle.Particle;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public final class ParticleCapture {
    private static final ThreadLocal<Scope> CURRENT = new ThreadLocal<>();

    private ParticleCapture() {}

    private static final class Scope {
        private final ParticleTint tint;
        private Vec3 offset;

        private Scope(@Nullable ParticleTint tint, Vec3 offset) {
            this.tint = tint;
            this.offset = canonicalOffset(offset);
        }
    }

    public static void run(@Nullable ParticleTint tint, Vec3 offset, Runnable emit) {
        call(tint, offset, () -> {
            emit.run();
            return null;
        });
    }

    public static <T> T call(@Nullable ParticleTint tint, Vec3 offset, Supplier<T> emit) {
        Scope previous = CURRENT.get();
        CURRENT.set(new Scope(tint, offset));
        try {
            return emit.get();
        } finally {
            if (previous == null) CURRENT.remove();
            else CURRENT.set(previous);
        }
    }

    public static Vec3 offset() {
        Scope scope = CURRENT.get();
        return scope == null ? Vec3.ZERO : scope.offset;
    }

    public static void setOffset(Vec3 offset) {
        Scope scope = CURRENT.get();
        if (scope != null) scope.offset = canonicalOffset(offset);
    }

    private static Vec3 canonicalOffset(Vec3 offset) {
        return offset.x == 0 && offset.y == 0 && offset.z == 0 ? Vec3.ZERO : offset;
    }

    public static Particle wrap(Particle particle) {
        Scope scope = CURRENT.get();
        if (scope == null) return particle;
        ParticleTint tint = scope.tint;
        if (tint == null || !particle.isAlive() || particle instanceof TintedParticle
                || !(particle instanceof TintedParticle.Source source) || !tint.matches(particle)) return particle;
        return source.musavacca$wrap(tint);
    }
}
