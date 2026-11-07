package space.anatomyuniverse.musavacca.particle.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.FlameParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
//? if <1.21.9 {
import net.minecraft.client.particle.ParticleRenderType;
//?}
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;
import space.anatomyuniverse.musavacca.particle.tinted.ProfileTintParticleOptions;
import space.anatomyuniverse.musavacca.particle.tinted.ProfileTintSprite;

public final class PearlFlameParticle extends FlameParticle {
    private PearlFlameParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd, TextureAtlasSprite initialSprite) {
        //? if >=1.21.9 {
        /*super(level, x, y, z, xd, yd, zd, initialSprite);
        *///?} else {
        super(level, x, y, z, xd, yd, zd);
        //?}
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
    private static PearlFlameParticle createLayer(ProfileTintParticleOptions options, ClientLevel level, SpriteSet sprites, double x, double y, double z,
            double xSpeed, double ySpeed, double zSpeed) {
        ProfileTintSprite.Layer layer = ProfileTintSprite.prepare(options);
        TextureAtlasSprite sprite = layer.sprite(sprites);
        RandomSource random = layer.random();
        double jitterX = (random.nextDouble() - random.nextDouble()) * 0.0125D;
        double jitterY = (random.nextDouble() - random.nextDouble()) * 0.0125D;
        double jitterZ = (random.nextDouble() - random.nextDouble()) * 0.0125D;
        double finalXd = (xSpeed * 0.15D) + ((random.nextDouble() - random.nextDouble()) * 0.0012D);
        double finalYd = (ySpeed * 0.15D) + 0.0015D + (random.nextDouble() * 0.0025D);
        double finalZd = (zSpeed * 0.15D) + ((random.nextDouble() - random.nextDouble()) * 0.0012D);
        float sizeRandom = 0.72F + (random.nextFloat() * 0.28F);
        float quadSize = 0.20F * sizeRandom;
        int lifetime = 20 + random.nextInt(18);
        double centerLayer = (layer.spriteLayerCount() - 1) * 0.5D;
        double layerOffset = (layer.spriteLayer() - centerLayer) * 0.00035D;
        double finalX = x + jitterX + (layerOffset * 0.25D);
        double finalY = y + jitterY + layerOffset;
        double finalZ = z + jitterZ - (layerOffset * 0.25D);
        PearlFlameParticle particle = new PearlFlameParticle(level, finalX, finalY, finalZ, finalXd, finalYd, finalZd, sprite);
        particle.setPos(finalX, finalY, finalZ);
        particle.xo = finalX;
        particle.yo = finalY;
        particle.zo = finalZ;
        particle.setParticleSpeed(finalXd, finalYd, finalZd);
        particle.quadSize = quadSize;
        particle.scale(0.50F);
        particle.setLifetime(lifetime);
        particle.setSprite(sprite);
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
            return PearlFlameParticle.createLayer(options, level, sprites, x, y, z, xSpeed, ySpeed, zSpeed);
        }
    }
}
