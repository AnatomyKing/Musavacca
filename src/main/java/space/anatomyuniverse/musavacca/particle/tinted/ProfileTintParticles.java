// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/particle/tinted/ProfileTintParticles.java
package space.anatomyuniverse.musavacca.particle.tinted;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

public final class ProfileTintParticles {
    private ProfileTintParticles() {}

    public static void spawn(
            Level level,
            RandomSource random,
            ParticleType<ProfileTintParticleOptions> renderType,
            int color,
            double x,
            double y,
            double z,
            double xSpeed,
            double ySpeed,
            double zSpeed
    ) {
        if (level == null || !level.isClientSide()) {
            return;
        }

        RandomSource rng = random == null ? level.random : random;
        int layerCount = ProfileTintSprite.layerCount(renderType);
        int seed = rng.nextInt();

        for (int layer = 0; layer < layerCount; layer++) {
            level.addParticle(
                    ProfileTintParticleOptions.layer(
                            renderType,
                            color,
                            layer,
                            layerCount,
                            seed
                    ),
                    x,
                    y,
                    z,
                    xSpeed,
                    ySpeed,
                    zSpeed
            );
        }
    }

    @SafeVarargs
    public static void spawnRandomVariant(
            Level level,
            RandomSource random,
            int color,
            double x,
            double y,
            double z,
            double xSpeed,
            double ySpeed,
            double zSpeed,
            ParticleType<ProfileTintParticleOptions>... renderTypes
    ) {
        if (level == null || !level.isClientSide()) {
            return;
        }

        if (renderTypes == null || renderTypes.length == 0) {
            return;
        }

        RandomSource rng = random == null ? level.random : random;
        ParticleType<ProfileTintParticleOptions> renderType =
                renderTypes[rng.nextInt(renderTypes.length)];

        spawn(
                level,
                rng,
                renderType,
                color,
                x,
                y,
                z,
                xSpeed,
                ySpeed,
                zSpeed
        );
    }
}