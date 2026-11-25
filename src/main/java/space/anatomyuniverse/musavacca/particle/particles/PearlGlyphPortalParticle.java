package space.anatomyuniverse.musavacca.particle.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.PortalParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.SimpleParticleType;
//? if <1.21.9 {
import net.minecraft.client.particle.ParticleRenderType;
//?}
//? if >=1.21.9 {
/*import net.minecraft.util.RandomSource;
*///?}

public final class PearlGlyphPortalParticle extends PortalParticle {
    private PearlGlyphPortalParticle(
            ClientLevel level, double x, double y, double z, double xd, double yd, double zd, TextureAtlasSprite sprite) {
        //? if >=1.21.9 {
        /*super(level, x, y, z, xd, yd, zd, sprite);
        *///?} else {
        super(level, x, y, z, xd, yd, zd);
        this.setSprite(sprite);
        //?}
        this.setColor(1.0F, 1.0F, 1.0F);
        this.quadSize = 0.22F + this.random.nextFloat() * 0.08F;
        this.setLifetime(34 + this.random.nextInt(16));
    }

    @Override
    //? if >=1.21.9 {
    /*public Layer getLayer() {
        return Layer.TRANSLUCENT;
    }
    *///?} else {
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }
    //?}

    public static final class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(
                SimpleParticleType options, ClientLevel level, double x, double y, double z, double xd, double yd,
                //? if >=1.21.9 {
                /*double zd,
                RandomSource random) {
                *///?} else {
                double zd) {
                //?}
            return new PearlGlyphPortalParticle(level, x, y, z, xd, yd, zd, this.sprites.get(0, 1));
        }
    }
}
