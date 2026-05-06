package space.anatomyuniverse.musavacca.particle.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.PortalParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
//? if >=1.21.9 {
/*import net.minecraft.util.RandomSource;
 *///?}
import org.jetbrains.annotations.Nullable;
import space.anatomyuniverse.musavacca.particle.utils.HexColorParticleOptions;

public final class PearlGlyphParticle extends PortalParticle {
    /*
     * Vanilla PortalParticle is very small, around 0.05 - 0.07 quad size.
     * Glyphs need to read as symbols, not portal dust.
     */
    private static final float GLYPH_SIZE_MIN_MULTIPLIER = 4.25F;
    private static final float GLYPH_SIZE_RANDOM_MULTIPLIER = 2.25F;

    private PearlGlyphParticle(
            ClientLevel level,
            double x,
            double y,
            double z,
            double xd,
            double yd,
            double zd,
            SpriteSet sprites
    ) {
        super(level, x, y, z, xd, yd, zd);

        // Same vanilla provider idea: create PortalParticle, then pick from the SpriteSet.
        this.pickSprite(sprites);

        // Keep vanilla portal movement/light/rendering, only make the visual glyph bigger.
        this.quadSize *= GLYPH_SIZE_MIN_MULTIPLIER
                + (this.random.nextFloat() * GLYPH_SIZE_RANDOM_MULTIPLIER);
    }

    private static PearlGlyphParticle createVanillaPortalLike(
            ClientLevel level,
            SpriteSet sprites,
            double x,
            double y,
            double z,
            double xSpeed,
            double ySpeed,
            double zSpeed
    ) {
        return new PearlGlyphParticle(
                level,
                x,
                y,
                z,
                xSpeed,
                ySpeed,
                zSpeed,
                sprites
        );
    }

    public static final class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        private Particle createInternal(
                ClientLevel level,
                double x,
                double y,
                double z,
                double xSpeed,
                double ySpeed,
                double zSpeed
        ) {
            PearlGlyphParticle particle = createVanillaPortalLike(
                    level,
                    this.sprites,
                    x,
                    y,
                    z,
                    xSpeed,
                    ySpeed,
                    zSpeed
            );

            // White = no tint. Colored glyph textures keep their own colors.
            particle.setColor(1.0F, 1.0F, 1.0F);

            return particle;
        }

        //? if <1.21.9 {
        @Override
        public @Nullable Particle createParticle(
                SimpleParticleType type,
                ClientLevel level,
                double x,
                double y,
                double z,
                double xSpeed,
                double ySpeed,
                double zSpeed
        ) {
            return this.createInternal(level, x, y, z, xSpeed, ySpeed, zSpeed);
        }
        //?} else {
        /*@Override
        public @Nullable Particle createParticle(
                SimpleParticleType type,
                ClientLevel level,
                double x,
                double y,
                double z,
                double xSpeed,
                double ySpeed,
                double zSpeed,
                RandomSource random
        ) {
            return this.createInternal(level, x, y, z, xSpeed, ySpeed, zSpeed);
        }
        *///?}
    }

    public static final class TintedProvider implements ParticleProvider<HexColorParticleOptions> {
        private final SpriteSet sprites;

        public TintedProvider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        private Particle createInternal(
                HexColorParticleOptions options,
                ClientLevel level,
                double x,
                double y,
                double z,
                double xSpeed,
                double ySpeed,
                double zSpeed
        ) {
            PearlGlyphParticle particle = createVanillaPortalLike(
                    level,
                    this.sprites,
                    x,
                    y,
                    z,
                    xSpeed,
                    ySpeed,
                    zSpeed
            );

            // Overwrites vanilla portal purple with your portal's dynamic hex color.
            particle.setColor(options.red(), options.green(), options.blue());

            return particle;
        }

        //? if <1.21.9 {
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
            return this.createInternal(options, level, x, y, z, xSpeed, ySpeed, zSpeed);
        }
        //?} else {
        /*@Override
        public @Nullable Particle createParticle(
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
            return this.createInternal(options, level, x, y, z, xSpeed, ySpeed, zSpeed);
        }
        *///?}
    }
}