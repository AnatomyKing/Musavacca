package space.anatomyuniverse.musavacca.particle.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.GlowParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
//? if >=1.21.9 {
/*import net.minecraft.util.RandomSource;
 *///?}
import space.anatomyuniverse.musavacca.particle.utils.HexColorParticleOptions;

public final class HexFallingSporeBlossomProvider implements ParticleProvider<HexColorParticleOptions> {
    private final SpriteSet sprites;

    public HexFallingSporeBlossomProvider(SpriteSet sprites) {
        this.sprites = sprites;
    }

    private static final class HexGlowParticle extends GlowParticle {
        public HexGlowParticle(
                ClientLevel level,
                double x,
                double y,
                double z,
                double xd,
                double yd,
                double zd,
                SpriteSet sprites
        ) {
            super(level, x, y, z, xd, yd, zd, sprites);
        }
    }

    private Particle createInternal(
            HexColorParticleOptions options,
            ClientLevel level,
            double x,
            double y,
            double z
    ) {
        GlowParticle particle = new HexGlowParticle(
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

    //? if <1.21.9 {
    @Override
    public Particle createParticle(
            HexColorParticleOptions options,
            ClientLevel level,
            double x,
            double y,
            double z,
            double xSpeed,
            double ySpeed,
            double zSpeed
    ) {
        return this.createInternal(options, level, x, y, z);
    }
    //?} else {
    /*@Override
    public Particle createParticle(
            HexColorParticleOptions options,
            ClientLevel level,
            double x,
            double y,
            double z,
            double xSpeed,
            double ySpeed,
            double zSpeed,
            RandomSource random
    ) {
        return this.createInternal(options, level, x, y, z);
    }
    *///?}
}