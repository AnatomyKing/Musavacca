package space.anatomyuniverse.musavacca.particle.utils;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

public record HexColorParticleOptions(ParticleType<HexColorParticleOptions> type, int color) implements ParticleOptions {
    public HexColorParticleOptions {
        color &= 0xFFFFFF;
    }

    @Override
    public ParticleType<HexColorParticleOptions> getType() {
        return this.type;
    }

    public float red() {
        return ((this.color >> 16) & 0xFF) / 255.0F;
    }

    public float green() {
        return ((this.color >> 8) & 0xFF) / 255.0F;
    }

    public float blue() {
        return (this.color & 0xFF) / 255.0F;
    }
}