package space.anatomyuniverse.musavacca.particle.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.FlameParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.util.RandomSource;
//? if >=1.21.9 {
/*import net.minecraft.util.RandomSource;
 *///?}
import org.jetbrains.annotations.Nullable;
import space.anatomyuniverse.musavacca.particle.tinted.ProfileTintParticleOptions;
import space.anatomyuniverse.musavacca.particle.tinted.ProfileTintSprite;

public final class PearlFlameParticle extends FlameParticle {

    private static final double POSITION_JITTER = 0.0125D;

    private static final double HORIZONTAL_SPEED_JITTER = 0.0012D;
    private static final double UPWARD_SPEED_MIN = 0.0015D;
    private static final double UPWARD_SPEED_RANDOM = 0.0025D;

    private static final float BASE_QUAD_SIZE = 0.20F;
    private static final float SIZE_RANDOM_MIN = 0.72F;
    private static final float SIZE_RANDOM_RANGE = 0.28F;
    private static final float SMALL_FLAME_SCALE = 0.50F;

    private static final int MIN_LIFETIME = 20;
    private static final int RANDOM_LIFETIME = 18;

    private static final double LAYER_SEPARATION = 0.00035D;

    private PearlFlameParticle(
            ClientLevel level,
            double x,
            double y,
            double z,
            double xd,
            double yd,
            double zd
    ) {
        super(level, x, y, z, xd, yd, zd);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    private static PearlFlameParticle createLayer(
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

        double jitterX = (random.nextDouble() - random.nextDouble()) * POSITION_JITTER;
        double jitterY = (random.nextDouble() - random.nextDouble()) * POSITION_JITTER;
        double jitterZ = (random.nextDouble() - random.nextDouble()) * POSITION_JITTER;

        double finalXd = (xSpeed * 0.15D)
                + ((random.nextDouble() - random.nextDouble()) * HORIZONTAL_SPEED_JITTER);

        double finalYd = (ySpeed * 0.15D)
                + UPWARD_SPEED_MIN
                + (random.nextDouble() * UPWARD_SPEED_RANDOM);

        double finalZd = (zSpeed * 0.15D)
                + ((random.nextDouble() - random.nextDouble()) * HORIZONTAL_SPEED_JITTER);

        float sizeRandom = SIZE_RANDOM_MIN + (random.nextFloat() * SIZE_RANDOM_RANGE);
        float quadSize = BASE_QUAD_SIZE * sizeRandom;

        int lifetime = MIN_LIFETIME + random.nextInt(RANDOM_LIFETIME);

        double centerLayer = (layer.spriteLayerCount() - 1) * 0.5D;
        double layerOffset = (layer.spriteLayer() - centerLayer) * LAYER_SEPARATION;

        double finalX = x + jitterX + (layerOffset * 0.25D);
        double finalY = y + jitterY + layerOffset;
        double finalZ = z + jitterZ - (layerOffset * 0.25D);

        PearlFlameParticle particle = new PearlFlameParticle(
                level,
                finalX,
                finalY,
                finalZ,
                finalXd,
                finalYd,
                finalZd
        );

        particle.setPos(finalX, finalY, finalZ);
        particle.xo = finalX;
        particle.yo = finalY;
        particle.zo = finalZ;

        particle.setParticleSpeed(finalXd, finalYd, finalZd);

        particle.quadSize = quadSize;
        particle.scale(SMALL_FLAME_SCALE);
        particle.setLifetime(lifetime);

        particle.setSprite(layer.sprite());
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
            return PearlFlameParticle.createLayer(
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

        //? if <1.21.9 {
        @Override
        public @Nullable Particle createParticle(
                ProfileTintParticleOptions options,
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
                ProfileTintParticleOptions options,
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

