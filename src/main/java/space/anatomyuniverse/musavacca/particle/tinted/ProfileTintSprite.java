package space.anatomyuniverse.musavacca.particle.tinted;

import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.RandomSource;
import space.anatomyuniverse.musavacca.tint.MusavaccaTints;
import space.anatomyuniverse.musavacca.tint.PearlFireTintProfiles;
import space.anatomyuniverse.musavacca.tint.PearlFireTintSource;

public final class ProfileTintSprite {
    private ProfileTintSprite() {}

    public static Layer prepare(ProfileTintParticleOptions options) {
        PearlFireTintProfiles.Profile profile = options.type().profile();
        int layerCount = options.layerCount();
        int layer = options.layer();
        float position = layerCount <= 1 ? 0.0F : layer / (float) (layerCount - 1);
        int profileLayer = Math.min(profile.layerCount() - 1, Math.round(position * (profile.layerCount() - 1)));
        int tint = PearlFireTintSource.profileTint(options.color(), profileLayer, profile);
        return new Layer(layer, layerCount, tint, options.seed());
    }

    public static int[] colors(int color, PearlFireTintProfiles.Profile profile) {
        int[] colors = new int[profile.layerCount()];
        for (int i = 0; i < colors.length; i++) colors[i] = PearlFireTintSource.profileTint(color, i, profile);
        return colors;
    }

    public static void applyColor(SingleQuadParticle particle, int tint) {
        int rgb = MusavaccaTints.rgb(tint);
        particle.setColor(((rgb >> 16) & 255) / 255.0F, ((rgb >> 8) & 255) / 255.0F, (rgb & 255) / 255.0F);
    }

    public record Layer(int spriteLayer, int spriteLayerCount, int tint, int seed) {
        public TextureAtlasSprite sprite(SpriteSet sprites) {
            return sprites.get(spriteLayer, Math.max(1, spriteLayerCount - 1));
        }

        public RandomSource random() {
            return RandomSource.create(seed);
        }

        public void applyColor(SingleQuadParticle particle) {
            ProfileTintSprite.applyColor(particle, this.tint);
        }
    }
}
