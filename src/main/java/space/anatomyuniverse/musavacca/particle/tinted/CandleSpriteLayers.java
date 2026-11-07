package space.anatomyuniverse.musavacca.particle.tinted;

import com.google.gson.JsonElement;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
//? if >=1.21.11 {
/*import net.minecraft.resources.Identifier;
*///?} else {
import net.minecraft.resources.ResourceLocation;
//?}
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import space.anatomyuniverse.musavacca.tint.PearlFireTintProfiles;

public final class CandleSpriteLayers {
    private CandleSpriteLayers() {}

    public static List<SpriteContents> prepare(List<SpriteContents> sprites, ResourceManager resources) {
        List<SpriteContents> result = new ArrayList<>(sprites);
        var profile = PearlFireTintProfiles.PEARL_CANDLE;
        int[] grays = new int[profile.layerCount()];
        for (int i = 0; i < grays.length; i++) grays[i] = Math.round(profile.grayFactor(i) * 255);
        Map<
                //? if >=1.21.11 {
                /*Identifier,
                *///?} else {
                ResourceLocation,
                //?}
                Range> ranges = new HashMap<>();
        for (SpriteContents source : sprites) {
            if (!generated(source.name())) ranges.put(source.name(), range(((Source) source).musavacca$image()));
        }
        group(resources, ranges);
        for (SpriteContents source : sprites) {
            if (generated(source.name())) continue;
            NativeImage image = ((Source) source).musavacca$image();
            int width = image.getWidth(), height = image.getHeight();
            Range range = ranges.get(source.name()).root();
            NativeImage[] masks = new NativeImage[grays.length];
            try {
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int rgba = pixel(image, x, y);
                        if ((rgba >>> 24) == 0) continue;
                        int layer = layer(gray(rgba), range.min, range.max, grays.length);
                        if (masks[layer] == null) masks[layer] = new NativeImage(width, height, true);
                        int color = (rgba & 0xFF000000) | grays[layer] * 0x010101;
                        //? if >=1.21.2 {
                        masks[layer].setPixel(x, y, color);
                        //?} else {
                        /*masks[layer].setPixelRGBA(x, y, color);
                        *///?}
                    }
                }
                for (int i = 0; i < masks.length; i++) {
                    if (masks[i] == null) continue;
                    result.add(((Source) source).musavacca$layer(id(source.name(), i), masks[i]));
                    masks[i] = null;
                }
            } finally {
                for (NativeImage mask : masks) if (mask != null) mask.close();
            }
        }
        return result;
    }

    public static void link(Map<
            //? if >=1.21.11 {
            /*Identifier,
            *///?} else {
            ResourceLocation,
            //?}
            TextureAtlasSprite> sprites) {
        for (TextureAtlasSprite sprite : sprites.values()) {
            if (generated(sprite.contents().name())) continue;
            TextureAtlasSprite[] layers = new TextureAtlasSprite[PearlFireTintProfiles.PEARL_CANDLE.layerCount()];
            boolean found = false;
            for (int i = 0; i < layers.length; i++) {
                layers[i] = sprites.get(id(sprite.contents().name(), i));
                found |= layers[i] != null;
            }
            if (found) ((Source) sprite.contents()).musavacca$layers(layers);
        }
    }

    private static Range range(NativeImage image) {
        Range range = new Range();
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int argb = pixel(image, x, y);
                if ((argb >>> 24) == 0) continue;
                float gray = gray(argb);
                range.min = Math.min(range.min, gray);
                range.max = Math.max(range.max, gray);
            }
        }
        return range;
    }

    private static void group(ResourceManager resources, Map<
            //? if >=1.21.11 {
            /*Identifier,
            *///?} else {
            ResourceLocation,
            //?}
            Range> ranges) {
        for (var entry : resources.listResources("particles", id -> id.getPath().endsWith(".json")).entrySet()) {
            try (var reader = entry.getValue().openAsReader()) {
                var textures = GsonHelper.parse(reader).getAsJsonArray("textures");
                if (textures == null) continue;
                List<Range> frames = new ArrayList<>();
                for (JsonElement texture : textures) {
                    var id =
                            //? if >=1.21.11 {
                            /*Identifier.parse(GsonHelper.convertToString(texture, "texture"));
                            *///?} else {
                            ResourceLocation.parse(GsonHelper.convertToString(texture, "texture"));
                            //?}
                    Range range = ranges.get(id);
                    if (range != null) frames.add(range);
                }
                for (int i = 1; i < frames.size(); i++) frames.get(0).merge(frames.get(i));
            } catch (IOException | RuntimeException exception) {
                LogUtils.getLogger().warn("Skipping candle particle grouping for {}", entry.getKey(), exception);
            }
        }
    }

    static float gray(int argb) {
        return (54 * ((argb >>> 16) & 255) + 183 * ((argb >>> 8) & 255) + 19 * (argb & 255)) / 256.0F;
    }

    static int layer(float gray, float min, float max, int count) {
        if (count <= 1) return 0;
        float position = max > min ? (max - gray) / (max - min) : 1.0F - gray / 255.0F;
        return Math.max(0, Math.min(count - 1, Math.round(position * (count - 1))));
    }

    private static final class Range {
        private Range parent = this;
        private float min = 255.0F, max = 0.0F;

        private Range root() {
            Range range = this;
            while (range.parent != range) {
                range.parent = range.parent.parent;
                range = range.parent;
            }
            return range;
        }

        private void merge(Range other) {
            Range first = this.root(), second = other.root();
            if (first == second) return;
            second.parent = first;
            first.min = Math.min(first.min, second.min);
            first.max = Math.max(first.max, second.max);
        }
    }

    private static int pixel(NativeImage image, int x, int y) {
        //? if >=1.21.2 {
        return image.getPixel(x, y);
        //?} else {
        /*int abgr = image.getPixelRGBA(x, y);
        return (abgr & 0xFF00FF00) | ((abgr & 255) << 16) | ((abgr >>> 16) & 255);
        *///?}
    }

    private static boolean generated(
            //? if >=1.21.11 {
            /*Identifier id
            *///?} else {
            ResourceLocation id
            //?}
    ) {
        return id.getNamespace().equals("musavacca") && id.getPath().startsWith("generated/candle/");
    }

    //? if >=1.21.11 {
    /*private static Identifier id(Identifier source, int layer) {
        return Identifier.fromNamespaceAndPath("musavacca", "generated/candle/" + source.getNamespace() + "/" + source.getPath() + "/" + layer);
    *///?} else {
    private static ResourceLocation id(ResourceLocation source, int layer) {
        return ResourceLocation.fromNamespaceAndPath("musavacca", "generated/candle/" + source.getNamespace() + "/" + source.getPath() + "/" + layer);
    //?}
    }

    public interface Source {
        NativeImage musavacca$image();

        SpriteContents musavacca$layer(
                //? if >=1.21.11 {
                /*Identifier id,
                *///?} else {
                ResourceLocation id,
                //?}
                NativeImage image);

        TextureAtlasSprite[] musavacca$layers();

        void musavacca$layers(TextureAtlasSprite[] layers);
    }
}
