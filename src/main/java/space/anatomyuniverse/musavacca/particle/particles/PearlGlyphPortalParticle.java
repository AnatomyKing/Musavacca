package space.anatomyuniverse.musavacca.particle.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
//? if <1.21.9 {
import net.minecraft.client.particle.ParticleRenderType;
//?}
import net.minecraft.client.particle.PortalParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;
import space.anatomyuniverse.musavacca.particle.tinted.ProfileTintParticleOptions;
import space.anatomyuniverse.musavacca.particle.tinted.ProfileTintSprite;

public final class PearlGlyphPortalParticle extends PortalParticle {
    private PearlGlyphPortalParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd,
            TextureAtlasSprite initialSprite) {
        //? if >=1.21.9 {
        /*super(level, x, y, z, xd, yd, zd, initialSprite);
        *///?} else {
        super(level, x, y, z, xd, yd, zd);
        //?}
    }

    //? if >=1.21.9 {
    /*public net.minecraft.client.particle.SingleQuadParticle.Layer getLayer() {
        return net.minecraft.client.particle.SingleQuadParticle.Layer.TRANSLUCENT;
    }
    *///?}
    //? if <1.21.9 {
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }
    //?}
    private static PearlGlyphPortalParticle createLayer(ProfileTintParticleOptions options, ClientLevel level, SpriteSet sprites, double x, double y, double z,
            double xSpeed, double ySpeed, double zSpeed) {
        ProfileTintSprite.Layer layer = ProfileTintSprite.prepare(options);
        TextureAtlasSprite sprite = layer.sprite(sprites);
        RandomSource random = layer.random();
        PearlGlyphPortalParticle particle = new PearlGlyphPortalParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, sprite);
        particle.setSprite(sprite);
        particle.quadSize = 0.22F + (random.nextFloat() * 0.08F);
        particle.setLifetime(34 + random.nextInt(16));
        layer.applyColor(particle);
        return particle;
    }

    public static final class Provider implements ParticleProvider<ProfileTintParticleOptions> {
        private final SpriteSet sprites;
        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public @Nullable Particle createParticle(ProfileTintParticleOptions options, ClientLevel level, double x, double y, double z, double xSpeed,
                //? if >=1.21.9 {
                /*double ySpeed, double zSpeed, RandomSource random) {
                *///?} else {
                double ySpeed, double zSpeed) {
                //?}
            return PearlGlyphPortalParticle.createLayer(options, level, sprites, x, y, z, xSpeed, ySpeed, zSpeed);
        }
    }
}
