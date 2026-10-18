package space.anatomyuniverse.musavacca.particle.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.PortalParticle;
import net.minecraft.client.particle.SpriteSet;
//? if >=1.21.9 {
/*import net.minecraft.util.RandomSource;
*///?}
import net.minecraft.util.RandomSource;

import org.jetbrains.annotations.Nullable;
import space.anatomyuniverse.musavacca.particle.tinted.ProfileTintParticleOptions;
import space.anatomyuniverse.musavacca.particle.tinted.ProfileTintSprite;

public final class PearlGlyphPortalParticle extends PortalParticle {
    private static final float MIN_QUAD_SIZE = 0.22F;
    private static final float RANDOM_QUAD_SIZE = 0.08F;

    private static final int MIN_LIFETIME = 34;
    private static final int RANDOM_LIFETIME = 16;

    private PearlGlyphPortalParticle(
            ClientLevel level,
            double x,
            double y,
            double z,
            double xd,
            double yd,
            double zd,
            net.minecraft.client.renderer.texture.TextureAtlasSprite initialSprite
    ) {
        super(level, x, y, z, xd, yd, zd

                //? if >=1.21.9 {
                /*, initialSprite
                *///?}
        );
    }

    //? if >=1.21.9 {
    /*public net.minecraft.client.particle.SingleQuadParticle.Layer getLayer() {
        return net.minecraft.client.particle.SingleQuadParticle.Layer.TRANSLUCENT;
    }
    *///?} else {
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }
    //?}

    private static PearlGlyphPortalParticle createLayer(
            ProfileTintParticleOptions options,
            ClientLevel level,
            SpriteSet sprites,
            double x,
            double y,
            double z,
            double xSpeed,
            double ySpeed,
            double zSpeed
    ) {
        ProfileTintSprite.Layer layer = ProfileTintSprite.prepare(options, sprites);
        RandomSource random = layer.random();

        PearlGlyphPortalParticle particle = new PearlGlyphPortalParticle(
                level,
                x,
                y,
                z,
                xSpeed,
                ySpeed,
                zSpeed,
                layer.sprite()
        );

        particle.setSprite(layer.sprite());
        particle.quadSize = MIN_QUAD_SIZE + (random.nextFloat() * RANDOM_QUAD_SIZE);
        particle.setLifetime(MIN_LIFETIME + random.nextInt(RANDOM_LIFETIME));

        layer.applyColor(particle);

        return particle;
    }

    public static final class Provider implements ParticleProvider<ProfileTintParticleOptions> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        private Particle createInternal(
                ProfileTintParticleOptions options,
                ClientLevel level,
                double x,
                double y,
                double z,
                double xSpeed,
                double ySpeed,
                double zSpeed
        ) {
            return PearlGlyphPortalParticle.createLayer(
                    options,
                    level,
                    this.sprites,
                    x,
                    y,
                    z,
                    xSpeed,
                    ySpeed,
                    zSpeed
            );
        }

        @Override
        public @Nullable Particle createParticle(
                ProfileTintParticleOptions options,
                ClientLevel level,
                double x,
                double y,
                double z,
                double xSpeed,
                double ySpeed,
                //? if >=1.21.9 {
                /*double zSpeed,
                RandomSource random
                *///?} else {
                double zSpeed
                //?}
        ) {
            return this.createInternal(options, level, x, y, z, xSpeed, ySpeed, zSpeed);
        }

    }
}
