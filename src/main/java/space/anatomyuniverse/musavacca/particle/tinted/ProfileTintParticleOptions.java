package space.anatomyuniverse.musavacca.particle.tinted;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

public record ProfileTintParticleOptions(ParticleType<ProfileTintParticleOptions> type, int color, int layer, int layerCount, int seed) implements ParticleOptions {
    public ProfileTintParticleOptions {
        color &= 0xFFFFFF;
        layerCount = Math.max(1, layerCount);
        layer = Math.max(0, Math.min(layer, layerCount - 1));
    }

    public static ProfileTintParticleOptions layer(ParticleType<ProfileTintParticleOptions> type, int color, int layer, int layerCount, int seed) {
        return new ProfileTintParticleOptions(type, color, layer, layerCount, seed);
    }

    @Override
    public ParticleType<ProfileTintParticleOptions> getType() {
        return this.type;
    }
}
