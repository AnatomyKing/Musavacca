// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/particle/particles/HexFallingSporeBlossomProvider.java
package space.anatomyuniverse.musavacca.particle.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.GlowParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import org.jetbrains.annotations.Nullable;
import space.anatomyuniverse.musavacca.particle.utils.HexColorParticleOptions;

public final class HexFallingSporeBlossomProvider implements ParticleProvider<HexColorParticleOptions> {
    private final SpriteSet sprites;

    public HexFallingSporeBlossomProvider(SpriteSet sprites) {
        this.sprites = sprites;
    }

    @Override
    public @Nullable Particle createParticle(
            HexColorParticleOptions options,
            ClientLevel level,
            double x,
            double y,
            double z,
            double xSpeed,
            double ySpeed,
            double zSpeed
    ) {
        GlowParticle particle = new GlowParticle(
                level,
                x,
                y,
                z,
                0.0,
                0.0,
                0.0,
                this.sprites
        );

        particle.setColor(options.red(), options.green(), options.blue());

        particle.setParticleSpeed(
                (level.random.nextDouble() - 0.5D) * 0.005D,
                -0.010D - level.random.nextDouble() * 0.006D,
                (level.random.nextDouble() - 0.5D) * 0.005D
        );

        particle.setLifetime(level.random.nextInt(22) + 24);
        return particle;
    }
}