// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/particle/tinted/ProfileTintParticleOptions.java
package space.anatomyuniverse.musavacca.particle.tinted;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

public record ProfileTintParticleOptions(
        ParticleType<ProfileTintParticleOptions> type,
        int color,
        int layer,
        int layerCount,
        int seed
) implements ParticleOptions {
    private static final int RGB_MASK = 0xFFFFFF;

    public ProfileTintParticleOptions {
        color &= RGB_MASK;
        layerCount = Math.max(1, layerCount);
        layer = Math.max(0, Math.min(layer, layerCount - 1));
    }

    public static ProfileTintParticleOptions layer(
            ParticleType<ProfileTintParticleOptions> type,
            int color,
            int layer,
            int layerCount,
            int seed
    ) {
        return new ProfileTintParticleOptions(
                type,
                color,
                layer,
                layerCount,
                seed
        );
    }

    @Override
    public ParticleType<ProfileTintParticleOptions> getType() {
        return this.type;
    }
}