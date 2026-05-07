// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/particle/particles/PearlFlameParticle.java
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
    /*
     * Goal:
     * - keep FlameParticle behavior: vanilla move(), getQuadSize(), getLightColor()
     * - keep layers locked together with one shared seed
     * - still give each candle flame a tiny vanilla-like flicker
     * - avoid opaque-layer z fighting
     *
     * Vanilla candle spawns SMALL_FLAME with 0 motion.
     * So the candle block should keep spawning this with 0, 0, 0 speed.
     */

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

    /*
     * Very tiny layer split.
     *
     * This is NOT movement variation.
     * This only prevents all flame layers from occupying the exact same render plane.
     *
     * Keep this tiny. If you make this bigger, the flame layers will visibly separate.
     */
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

    /*
     * Vanilla FlameParticle returns PARTICLE_SHEET_OPAQUE.
     *
     * But pearl flame is not one vanilla sprite.
     * It is 4 stacked split sprites.
     *
     * If those 4 layers are opaque at the exact same depth, they can fight/block
     * each other. TRANSLUCENT keeps the same FlameParticle movement/light/size
     * behavior, but makes layered split textures stable.
     */
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

        /*
         * Every layer spawned by ProfileTintParticles.spawn(...) receives the same seed.
         * That means layer 0/1/2/3 all use the exact same:
         * - position jitter
         * - speed jitter
         * - size random
         * - lifetime random
         *
         * This keeps the layered flame acting like ONE vanilla flame.
         */
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

        /*
         * Tiny layer separation.
         *
         * It is centered, so with 4 layers:
         * layer 0 = slightly one way
         * layer 1 = barely one way
         * layer 2 = barely the other way
         * layer 3 = slightly the other way
         */
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

        /*
         * FlameParticle/RisingParticle constructor has its own randomization.
         * That randomization would be different per layer.
         *
         * So we let FlameParticle exist, then overwrite the shared visual state.
         * This keeps FlameParticle's vanilla methods while making the layers align.
         */
        particle.setPos(finalX, finalY, finalZ);
        particle.xo = finalX;
        particle.yo = finalY;
        particle.zo = finalZ;

        particle.setParticleSpeed(finalXd, finalYd, finalZd);

        /*
         * Same idea as vanilla SmallFlameProvider:
         * create FlameParticle, then scale(0.5F).
         *
         * We do not call pickSprite(...), because the layer index must select
         * pearl_flame_0 / pearl_flame_1 / pearl_flame_2 / pearl_flame_3 exactly.
         */
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