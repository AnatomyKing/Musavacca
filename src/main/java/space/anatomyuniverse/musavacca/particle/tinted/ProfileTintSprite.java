package space.anatomyuniverse.musavacca.particle.tinted;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.util.RandomSource;
import space.anatomyuniverse.musavacca.tint.PearlFireTintProfiles;
import space.anatomyuniverse.musavacca.tint.PearlFireTintSource;
import space.anatomyuniverse.musavacca.tint.TintColorUtil;

public final class ProfileTintSprite {
    private ProfileTintSprite() {}

    public static Layer prepare(
            ProfileTintParticleOptions options,
            SpriteSet sprites
    ) {
        PearlFireTintProfiles.Profile profile = profile(options.getType());

        int spriteLayerCount = Math.max(1, options.layerCount());
        int spriteLayer = clamp(options.layer(), 0, spriteLayerCount - 1);

        int profileLayerCount = Math.max(1, profile.layerCount());
        int profileLayer = remapLayer(spriteLayer, spriteLayerCount, profileLayerCount);

        TextureAtlasSprite sprite = sprites.get(
                spriteLayer,
                Math.max(1, spriteLayerCount - 1)
        );

        int tint = PearlFireTintSource.profileTint(
                options.color(),
                profileLayer,
                profile
        );

        return new Layer(
                spriteLayer,
                spriteLayerCount,
                profileLayer,
                profileLayerCount,
                sprite,
                tint,
                options.seed()
        );
    }

    public static PearlFireTintProfiles.Profile profile(ParticleType<?> type) {
        if (type instanceof ProfileTintParticleType profileType) {
            return profileType.profile();
        }

        throw new IllegalArgumentException("Particle type is not a ProfileTintParticleType: " + type);
    }

    public static int layerCount(ParticleType<?> type) {
        if (type instanceof ProfileTintParticleType profileType) {
            return profileType.layerCount();
        }

        throw new IllegalArgumentException("Particle type is not a ProfileTintParticleType: " + type);
    }

    private static int remapLayer(int sourceLayer, int sourceCount, int targetCount) {
        if (sourceCount <= 1 || targetCount <= 1) {
            return 0;
        }

        float t = sourceLayer / (float) (sourceCount - 1);
        return clamp(Math.round(t * (targetCount - 1)), 0, targetCount - 1);
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    public record Layer(
            int spriteLayer,
            int spriteLayerCount,
            int profileLayer,
            int profileLayerCount,
            TextureAtlasSprite sprite,
            int tint,
            int seed
    ) {
        public RandomSource random() {
            return RandomSource.create(this.seed);
        }

        public void applyColor(Particle particle) {
            int rgb = TintColorUtil.rgb(this.tint);

            particle.setColor(
                    ((rgb >> 16) & 0xFF) / 255.0F,
                    ((rgb >> 8) & 0xFF) / 255.0F,
                    (rgb & 0xFF) / 255.0F
            );
        }
    }
}