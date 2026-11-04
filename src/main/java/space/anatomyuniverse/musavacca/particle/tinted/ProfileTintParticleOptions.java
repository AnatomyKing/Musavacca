package space.anatomyuniverse.musavacca.particle.tinted;

import net.minecraft.core.particles.ParticleOptions;

public record ProfileTintParticleOptions(ProfileTintParticleType type, int color, int layer, int layerCount, int seed) implements ParticleOptions {
    public ProfileTintParticleOptions {
        color &= 0xFFFFFF;
        layerCount = Math.max(1, layerCount);
        layer = Math.max(0, Math.min(layer, layerCount - 1));
    }

    @Override
    public ProfileTintParticleType getType() {
        return this.type;
    }
}
