package space.anatomyuniverse.musavacca.particle.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.SuspendedParticle;
//? if <1.21.9 {
import net.minecraft.core.particles.ParticleGroup;
//?} else {
/*import net.minecraft.util.RandomSource;
 *///?}
import net.minecraft.util.Mth;
import space.anatomyuniverse.musavacca.particle.utils.HexColorParticleOptions;

import java.util.Optional;

public class HexSporeBlossomAirParticle extends SuspendedParticle {
    private final SpriteSet sprites;

    //? if >=1.21.9 {
    /*private static final net.minecraft.core.particles.ParticleLimit SPORE_BLOSSOM_LIMIT =
            new net.minecraft.core.particles.ParticleLimit(1000);
    *///?}

    protected HexSporeBlossomAirParticle(
            ClientLevel level,
            SpriteSet sprites,
            double x,
            double y,
            double z,
            HexColorParticleOptions options
    ) {
        //? if <1.21.4 {
        /*super(level, sprites, x, y, z);
        this.setParticleSpeed(0.0, -0.8, 0.0);
        *///?} else if <1.21.9 {
        super(level, sprites, x, y, z, 0.0, -0.8, 0.0);
        //?} else {
        /*super(level, x, y, z, 0.0, -0.8, 0.0, sprites.first());
         *///?}

        this.sprites = sprites;
        this.lifetime = Mth.randomBetweenInclusive(level.getRandom(), 500, 1000);
        this.gravity = 0.01F;
        this.setColor(options.red(), options.green(), options.blue());
        this.setSpriteFromAge(this.sprites);
    }

    @Override
    public void tick() {
        this.setSpriteFromAge(this.sprites);
        super.tick();
    }

    //? if <1.21.9 {
    @Override
    public Optional<ParticleGroup> getParticleGroup() {
        return Optional.of(ParticleGroup.SPORE_BLOSSOM);
    }
    //?} else {
    /*@Override
    public Optional<net.minecraft.core.particles.ParticleLimit> getParticleLimit() {
        return Optional.of(SPORE_BLOSSOM_LIMIT);
    }
    *///?}

    public static final class Provider implements ParticleProvider<HexColorParticleOptions> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        private Particle createInternal(
                HexColorParticleOptions options,
                ClientLevel level,
                double x,
                double y,
                double z
        ) {
            return new HexSporeBlossomAirParticle(level, this.sprites, x, y, z, options);
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
}