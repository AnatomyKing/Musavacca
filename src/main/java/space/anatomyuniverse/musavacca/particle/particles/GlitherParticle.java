package space.anatomyuniverse.musavacca.particle.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
//? if <1.21.9 {
import net.minecraft.client.particle.ParticleRenderType;
//?}
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;
import space.anatomyuniverse.musavacca.particle.tinted.ProfileTintParticleOptions;
import space.anatomyuniverse.musavacca.particle.tinted.ProfileTintSprite;

public final class GlitherParticle extends SimpleAnimatedParticle {
    private final SpriteSet sprites;
    private final int spriteLayer;
    private final int spriteLayerCount;
    private GlitherParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd, SpriteSet sprites,
            ProfileTintParticleOptions options) {
        super(level, x, y, z, sprites, 0.0125F);
        this.sprites = sprites;
        ProfileTintSprite.Layer layer = ProfileTintSprite.prepare(options);
        RandomSource random = layer.random();
        this.spriteLayerCount = layer.spriteLayerCount();
        this.spriteLayer = layer.spriteLayer();
        this.setPos(x, y, z);
        this.xo = x;
        this.yo = y;
        this.zo = z;
        this.setParticleSpeed(xd, yd, zd);
        this.quadSize = 0.095F * (0.92F + random.nextFloat() * 0.16F);
        this.lifetime = 60 + random.nextInt(12);
        layer.applyColor(this);
        this.setGlitherSpriteFromAge();
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
    @Override
    public void tick() {
        super.tick();
        this.setGlitherSpriteFromAge();
    }

    @Override
    public void move(double x, double y, double z) {
        this.setBoundingBox(this.getBoundingBox().move(x, y, z));
        this.setLocationFromBoundingbox();
    }

    private void setGlitherSpriteFromAge() {
        int frame = this.animationFrame();
        int textureIndex = (frame * this.spriteLayerCount) + this.spriteLayer;
        int maxTextureIndex = Math.max(0, (8 * this.spriteLayerCount) - 1);
        this.setSprite(this.sprites.get(textureIndex, maxTextureIndex));
    }

    private int animationFrame() {
        if (this.lifetime <= 0) return 0;
        int frame = (this.age * 8) / this.lifetime;
        return Math.max(0, Math.min(7, frame));
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
            return new GlitherParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, sprites, options);
        }
    }
}
