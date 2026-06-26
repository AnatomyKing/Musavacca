package space.anatomyuniverse.musavacca.particle.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.util.RandomSource;
//? if >=1.21.9 {
/*import net.minecraft.util.RandomSource;
 *///?}
import org.jetbrains.annotations.Nullable;
import space.anatomyuniverse.musavacca.particle.tinted.ProfileTintParticleOptions;
import space.anatomyuniverse.musavacca.particle.tinted.ProfileTintParticleType;
import space.anatomyuniverse.musavacca.tint.PearlFireTintProfiles;
import space.anatomyuniverse.musavacca.tint.PearlFireTintSource;
import space.anatomyuniverse.musavacca.tint.TintColorUtil;

public final class GlitherParticle extends SimpleAnimatedParticle {
    /*
     * Glither = animated pearl-fire/profile sandwich particle.
     *
     * One logical glither is spawned as multiple real particle layers through
     * ProfileTintParticles.
     *
     * IMPORTANT:
     * This particle is different from PearlGlyphPortalParticle.
     *
     * Glyph:
     * - one static sprite per layer
     *
     * Glither:
     * - 8 animation frames
     * - 4 sandwich layers per animation frame
     *
     * JSON order must be frame-major:
     *
     * frame 7 layer 0
     * frame 7 layer 1
     * frame 7 layer 2
     * frame 7 layer 3
     *
     * frame 6 layer 0
     * frame 6 layer 1
     * frame 6 layer 2
     * frame 6 layer 3
     *
     * ...
     *
     * frame 0 layer 0
     * frame 0 layer 1
     * frame 0 layer 2
     * frame 0 layer 3
     */

    private static final int ANIMATION_FRAME_COUNT = 8;

    private static final float GRAVITY = 0.0125F;

    /*
     * Small deterministic size.
     *
     * Do NOT use:
     * this.quadSize *= something;
     *
     * The SimpleAnimatedParticle constructor may give every layer a slightly
     * different randomized base quadSize. For sandwich particles, that breaks
     * the perfect overlap.
     */
    private static final float BASE_QUAD_SIZE = 0.095F;
    private static final float SIZE_RANDOM_MIN = 0.92F;
    private static final float SIZE_RANDOM_RANGE = 0.16F;

    private static final int MIN_LIFETIME = 60;
    private static final int RANDOM_LIFETIME = 12;

    private final SpriteSet sprites;
    private final int spriteLayer;
    private final int spriteLayerCount;

    private GlitherParticle(
            ClientLevel level,
            double x,
            double y,
            double z,
            double xd,
            double yd,
            double zd,
            SpriteSet sprites,
            ProfileTintParticleOptions options
    ) {
        super(level, x, y, z, sprites, GRAVITY);

        this.sprites = sprites;

        PearlFireTintProfiles.Profile profile = profile(options);
        RandomSource random = RandomSource.create(options.seed());

        this.spriteLayerCount = Math.max(1, options.layerCount());
        this.spriteLayer = clamp(options.layer(), 0, this.spriteLayerCount - 1);

        /*
         * PERFECT sandwich:
         * All layers get the exact same position.
         *
         * No layerOffset.
         * No tiny z/y split.
         * No per-layer jitter.
         */
        this.setPos(x, y, z);
        this.xo = x;
        this.yo = y;
        this.zo = z;

        /*
         * Same velocity for every layer.
         */
        this.setParticleSpeed(xd, yd, zd);

        /*
         * Deterministic same size for every layer from the shared seed.
         *
         * Every layer receives the same seed from ProfileTintParticles, so every
         * layer gets the same random size.
         */
        this.quadSize = BASE_QUAD_SIZE * (SIZE_RANDOM_MIN + random.nextFloat() * SIZE_RANDOM_RANGE);

        /*
         * Deterministic same lifetime for every layer from the shared seed.
         */
        this.lifetime = MIN_LIFETIME + random.nextInt(RANDOM_LIFETIME);

        /*
         * Apply pearl-fire tint manually.
         *
         * We do not use ProfileTintSprite.prepare(...) here because that helper
         * assumes one sprite per layer. Glither has frame + layer indexing.
         */
        int profileLayer = remapLayer(
                this.spriteLayer,
                this.spriteLayerCount,
                Math.max(1, profile.layerCount())
        );

        int tint = PearlFireTintSource.profileTint(
                options.color(),
                profileLayer,
                profile
        );

        applyTint(this, tint);

        this.setGlitherSpriteFromAge();
    }

    /*
     * Translucent is correct for stacked mask/sandwich particles.
     */
    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void tick() {
        super.tick();
        this.setGlitherSpriteFromAge();
    }

    /*
     * Same movement style as your End Rod-like particle.
     */
    @Override
    public void move(double x, double y, double z) {
        this.setBoundingBox(this.getBoundingBox().move(x, y, z));
        this.setLocationFromBoundingbox();
    }

    private void setGlitherSpriteFromAge() {
        int frame = this.animationFrame();

        /*
         * Direct logical index:
         *
         * frame 0 = first frame in JSON, which is glither_7 in your reversed list.
         * frame 1 = glither_6
         * ...
         * frame 7 = glither_0
         *
         * layer 0-3 chooses the matching layer inside that frame.
         */
        int textureIndex = (frame * this.spriteLayerCount) + this.spriteLayer;

        /*
         * SpriteSet#get(age, lifetime) maps age across the loaded sprite list.
         * Passing textureIndex against maxTextureIndex gives us a stable direct
         * index as long as the JSON has exactly:
         *
         * ANIMATION_FRAME_COUNT * spriteLayerCount
         *
         * entries.
         */
        int maxTextureIndex = Math.max(0, (ANIMATION_FRAME_COUNT * this.spriteLayerCount) - 1);

        this.setSprite(this.sprites.get(textureIndex, maxTextureIndex));
    }

    private int animationFrame() {
        if (this.lifetime <= 0) {
            return 0;
        }

        int frame = (this.age * ANIMATION_FRAME_COUNT) / this.lifetime;
        return clamp(frame, 0, ANIMATION_FRAME_COUNT - 1);
    }

    private static PearlFireTintProfiles.Profile profile(ProfileTintParticleOptions options) {
        if (options.getType() instanceof ProfileTintParticleType profileType) {
            return profileType.profile();
        }

        throw new IllegalArgumentException("Glither particle type is not a ProfileTintParticleType: " + options.getType());
    }

    private static int remapLayer(int sourceLayer, int sourceCount, int targetCount) {
        if (sourceCount <= 1 || targetCount <= 1) {
            return 0;
        }

        float t = sourceLayer / (float) (sourceCount - 1);
        return clamp(Math.round(t * (targetCount - 1)), 0, targetCount - 1);
    }

    private static void applyTint(Particle particle, int tint) {
        int rgb = TintColorUtil.rgb(tint);

        particle.setColor(
                ((rgb >> 16) & 0xFF) / 255.0F,
                ((rgb >> 8) & 0xFF) / 255.0F,
                (rgb & 0xFF) / 255.0F
        );
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
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
            return new GlitherParticle(
                    level,
                    x,
                    y,
                    z,
                    xSpeed,
                    ySpeed,
                    zSpeed,
                    this.sprites,
                    options
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