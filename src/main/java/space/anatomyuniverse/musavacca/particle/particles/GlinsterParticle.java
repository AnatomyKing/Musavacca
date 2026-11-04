
package space.anatomyuniverse.musavacca.particle.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
//? if <1.21.9 {
import net.minecraft.client.particle.ParticleRenderType;
//?}

public final class GlinsterParticle extends SimpleAnimatedParticle {
    private final SpriteSet sprites;
    private final boolean burst;

    private GlinsterParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd, SpriteSet sprites, boolean burst) {
        super(level, x, y, z, sprites, burst ? 0.002F : 0.0F);
        this.sprites = sprites;
        this.burst = burst;
        this.setPos(x, y, z);
        this.setParticleSpeed(burst ? xd : 0.0D, burst ? yd : 0.0D, burst ? zd : 0.0D);
        this.hasPhysics = false;
        this.quadSize = (burst ? 0.08F : 0.18F) * (0.85F + this.random.nextFloat() * 0.3F);
        this.lifetime = burst ? 28 : 14;
        this.setSpriteFromAge(sprites);
    }

    //? if >=1.21.9 {
    /*@Override
    public net.minecraft.client.particle.SingleQuadParticle.Layer getLayer() {
        return net.minecraft.client.particle.SingleQuadParticle.Layer.TRANSLUCENT;
    }
    *///?} else {
    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }
    //?}

    @Override
    public void tick() {
        super.tick();
        if (this.burst) this.setSprite(this.sprites.get((this.age / 2) % 7, 6));
    }

    @Override
    public void move(double x, double y, double z) {
        this.setBoundingBox(this.getBoundingBox().move(x, y, z));
        this.setLocationFromBoundingbox();
    }

    public static final class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;
        private final boolean burst;

        public Provider(SpriteSet sprites, boolean burst) {
            this.sprites = sprites;
            this.burst = burst;
        }

        @Override
        public Particle createParticle(SimpleParticleType options, ClientLevel level, double x, double y, double z, double xd, double yd,
                                       //? if >=1.21.9 {
                /*double zd, RandomSource random) {
                 *///?} else {
                                       double zd) {
            //?}
            return new GlinsterParticle(level, x, y, z, xd, yd, zd, this.sprites, this.burst);
        }
    }
}

