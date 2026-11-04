package space.anatomyuniverse.musavacca.particle.tinted;

import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.RandomSource;
import space.anatomyuniverse.musavacca.tint.PearlFireTintProfiles;
import space.anatomyuniverse.musavacca.tint.PearlFireTintSource;
import space.anatomyuniverse.musavacca.tint.MusavaccaTints;

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

    public record Layer(int spriteLayer, int spriteLayerCount, int tint, int seed) {
        public TextureAtlasSprite sprite(SpriteSet sprites) {
            return sprites.get(spriteLayer, Math.max(1, spriteLayerCount - 1));
        }

        public RandomSource random() {
            return RandomSource.create(seed);
        }

        public void applyColor(SingleQuadParticle particle) {
            int rgb = MusavaccaTints.rgb(tint);
            particle.setColor(((rgb >> 16) & 0xFF) / 255.0F, ((rgb >> 8) & 0xFF) / 255.0F, (rgb & 0xFF) / 255.0F);
        }
    }
}
