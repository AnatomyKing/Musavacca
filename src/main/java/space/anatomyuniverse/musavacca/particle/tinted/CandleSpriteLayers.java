package space.anatomyuniverse.musavacca.particle.tinted;

import com.google.gson.JsonElement;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.logging.LogUtils;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import space.anatomyuniverse.musavacca.tint.PearlFireTintProfiles;
import space.anatomyuniverse.musavacca.tint.MusavaccaTints;
import space.anatomyuniverse.musavacca.tint.PearlFireTintSource;
import space.anatomyuniverse.musavacca.particle.tinted.OriginalCandleParticles.Context;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

//? if >=1.21.11 {
/*import net.minecraft.resources.Identifier;
*///?} else {
import net.minecraft.resources.ResourceLocation;
//?}

public final class CandleSpriteLayers {
    private static final int MAX_LAYERS = 16;
    private static final int CACHE_LIMIT = 256;
    private static final LinkedHashMap<PaletteKey, int[]> PALETTES = new LinkedHashMap<>(16, 0.75F, true);

    private CandleSpriteLayers() {}

    public record Tones(List<Integer> grays) {
        public Tones {
            grays = List.copyOf(grays);
        }

        private int[] lookup() {
            int[] result = new int[256];
            for (int gray = 0; gray < result.length; gray++) {
                int best = 0;
                for (int i = 1; i < grays.size(); i++) {
                    if (Math.abs(gray - grays.get(i)) < Math.abs(gray - grays.get(best))) best = i;
                }
                result[gray] = best;
            }
            return result;
        }
    }

    public record Layers(Tones tones, TextureAtlasSprite[] sprites) {}

    private record PaletteKey(Context context, Tones tones) {}

    public static synchronized int[] palette(Context context, Tones tones) {
        PaletteKey key = new PaletteKey(context, tones);
        int[] cached = PALETTES.get(key);
        if (cached != null) return cached;
        var profile = PearlFireTintProfiles.of(context.settings(), tones.grays().stream().mapToInt(Integer::intValue).toArray());
        int[] colors = new int[profile.layerCount()];
        for (int i = 0; i < colors.length; i++)
            colors[i] = MusavaccaTints.rgb(PearlFireTintSource.profileTint(context.color(), i, profile));
        if (PALETTES.size() >= CACHE_LIMIT) PALETTES.pollFirstEntry();
        PALETTES.put(key, colors);
        return colors;
    }

    public static List<SpriteContents> prepare(List<SpriteContents> sprites, ResourceManager resources, boolean blocks) {
        List<SpriteContents> result = new ArrayList<>(sprites);
        Map<
                //? if >=1.21.11 {
                /*Identifier,
                *///?} else {
                ResourceLocation,
                //?}
                Histogram> ranges = new HashMap<>();
        for (SpriteContents source : sprites) {
            if (eligible(source, blocks)) ranges.put(source.name(), histogram(((Source) source).musavacca$image()));
        }
        if (!blocks) group(resources, ranges);
        for (SpriteContents source : sprites) {
            if (!eligible(source, blocks)) continue;
            Histogram group = ranges.get(source.name()).root();
            Tones tones = group.tones();
            if (tones.grays().isEmpty()) continue;
            generate(source, tones, group.lookup(), result);
        }
        return result;
    }

    private static void generate(SpriteContents source, Tones tones, int[] lookup, List<SpriteContents> result) {
        NativeImage image = ((Source) source).musavacca$image();
        int width = image.getWidth(), height = image.getHeight();
        List<Integer> grays = tones.grays();
        NativeImage[] masks = new NativeImage[grays.size()];
        try {
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int rgba = pixel(image, x, y);
                    if ((rgba >>> 24) == 0) continue;
                    int layer = lookup[gray(rgba)];
                    if (masks[layer] == null) masks[layer] = new NativeImage(width, height, true);
                    int color = (rgba & 0xFF000000) | grays.get(layer) * 0x010101;
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
            ((Source) source).musavacca$layers(new Layers(tones, null));
        } finally {
            for (NativeImage mask : masks) if (mask != null) mask.close();
        }
    }

    public static void link(Map<
            //? if >=1.21.11 {
            /*Identifier,
            *///?} else {
            ResourceLocation,
            //?}
            TextureAtlasSprite> sprites, boolean blocks) {
        for (TextureAtlasSprite sprite : sprites.values()) {
            if (!eligible(sprite.contents(), blocks)) continue;
            Source source = (Source) sprite.contents();
            Layers data = source.musavacca$layers();
            if (data == null) continue;
            TextureAtlasSprite[] layers = new TextureAtlasSprite[data.tones().grays().size()];
            boolean found = false;
            for (int i = 0; i < layers.length; i++) {
                layers[i] = sprites.get(id(sprite.contents().name(), i));
                found |= layers[i] != null;
            }
            source.musavacca$layers(found ? new Layers(data.tones(), layers) : null);
        }
    }

    private static boolean eligible(SpriteContents source, boolean blocks) {
        return !generated(source.name()) && (!blocks || source.name().getPath().contains("_fire"));
    }

    private static Histogram histogram(NativeImage image) {
        Histogram histogram = new Histogram();
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int argb = pixel(image, x, y);
                if ((argb >>> 24) != 0) histogram.counts[gray(argb)]++;
            }
        }
        return histogram;
    }

    private static void group(ResourceManager resources, Map<
            //? if >=1.21.11 {
            /*Identifier,
            *///?} else {
            ResourceLocation,
            //?}
            Histogram> ranges) {
        for (var entry : resources.listResources("particles", id -> id.getPath().endsWith(".json")).entrySet()) {
            try (var reader = entry.getValue().openAsReader()) {
                var textures = GsonHelper.parse(reader).getAsJsonArray("textures");
                if (textures == null) continue;
                List<Histogram> frames = new ArrayList<>();
                for (JsonElement texture : textures) {
                    var id =
                            //? if >=1.21.11 {
                            /*Identifier.parse(GsonHelper.convertToString(texture, "texture"));
                            *///?} else {
                            ResourceLocation.parse(GsonHelper.convertToString(texture, "texture"));
                            //?}
                    Histogram range = ranges.get(id);
                    if (range != null) frames.add(range);
                }
                for (int i = 1; i < frames.size(); i++) frames.get(0).merge(frames.get(i));
            } catch (IOException | RuntimeException exception) {
                LogUtils.getLogger().warn("Skipping candle particle grouping for {}", entry.getKey(), exception);
            }
        }
    }

    private static int gray(int argb) {
        return (54 * ((argb >>> 16) & 255) + 183 * ((argb >>> 8) & 255) + 19 * (argb & 255) + 128) >> 8;
    }

    private static final class Histogram {
        private Histogram parent = this;
        private final long[] counts = new long[256];
        private Tones tones;
        private int[] lookup;

        private Histogram root() {
            Histogram histogram = this;
            while (histogram.parent != histogram) {
                histogram.parent = histogram.parent.parent;
                histogram = histogram.parent;
            }
            return histogram;
        }

        private void merge(Histogram other) {
            Histogram first = root(), second = other.root();
            if (first == second) return;
            second.parent = first;
            for (int i = 0; i < first.counts.length; i++) first.counts[i] += second.counts[i];
        }

        private Tones tones() {
            if (tones != null) return tones;
            List<Integer> values = new ArrayList<>();
            for (int gray = 255; gray >= 0; gray--) if (counts[gray] != 0) values.add(gray);
            if (values.size() > MAX_LAYERS) {
                int max = values.getFirst(), min = values.getLast();
                long[] weights = new long[MAX_LAYERS], sums = new long[MAX_LAYERS];
                for (int gray : values) {
                    int bucket = Math.round((max - gray) * (MAX_LAYERS - 1.0F) / (max - min));
                    weights[bucket] += counts[gray];
                    sums[bucket] += counts[gray] * gray;
                }
                values.clear();
                for (int i = 0; i < MAX_LAYERS; i++) {
                    if (weights[i] != 0) values.add((int) Math.round(sums[i] / (double) weights[i]));
                }
            }
            return tones = new Tones(values);
        }

        private int[] lookup() {
            if (lookup == null) lookup = tones().lookup();
            return lookup;
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
        return Identifier.fromNamespaceAndPath(
                "musavacca", "generated/candle/" + source.getNamespace() + "/" + source.getPath() + "/0/" + layer);
    *///?} else {
    private static ResourceLocation id(ResourceLocation source, int layer) {
        return ResourceLocation.fromNamespaceAndPath(
                "musavacca", "generated/candle/" + source.getNamespace() + "/" + source.getPath() + "/0/" + layer);
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

        Layers musavacca$layers();

        void musavacca$layers(Layers layers);
    }
}
