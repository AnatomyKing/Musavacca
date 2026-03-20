package space.anatomyuniverse.musavacca.particle.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.SuspendedParticle;
import net.minecraft.core.particles.ParticleGroup;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import space.anatomyuniverse.musavacca.particle.utils.HexColorParticleOptions;

import java.util.Optional;

public class HexSporeBlossomAirParticle extends SuspendedParticle {
    protected HexSporeBlossomAirParticle(
            ClientLevel level,
            SpriteSet sprites,
            double x,
            double y,
            double z,
            HexColorParticleOptions options
    ) {
        super(level, sprites, x, y, z, 0.0, -0.8, 0.0);

        this.lifetime = Mth.randomBetweenInclusive(level.getRandom(), 500, 1000);
        this.gravity = 0.01F;
        this.setColor(options.red(), options.green(), options.blue());
    }

    @Override
    public Optional<ParticleGroup> getParticleGroup() {
        return Optional.of(ParticleGroup.SPORE_BLOSSOM);
    }

    public static final class Provider implements ParticleProvider<HexColorParticleOptions> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
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
            return new HexSporeBlossomAirParticle(level, this.sprites, x, y, z, options);
        }
    }
}