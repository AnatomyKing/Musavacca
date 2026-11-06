package space.anatomyuniverse.musavacca.particle.tinted;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
//? if >=1.21.11 {
/*import net.minecraft.resources.Identifier;
*///?} else {
import net.minecraft.resources.ResourceLocation;
//?}
import space.anatomyuniverse.musavacca.tint.PearlFireTintProfiles;

public final class CandleSpriteLayers {
    private CandleSpriteLayers() {}

    public static List<SpriteContents> prepare(List<SpriteContents> sprites) {
        List<SpriteContents> result = new ArrayList<>(sprites);
        var profile = PearlFireTintProfiles.PEARL_FLAME;
        int[] carriers = new int[profile.layerCount()];
        for (int i = 0; i < carriers.length; i++) carriers[i] = Math.round(profile.grayFactor(i) * 255) * 0x010101;
        for (SpriteContents source : sprites) {
            if (generated(source.name())) continue;
            NativeImage image = ((Source) source).musavacca$image();
            int width = image.getWidth(), height = image.getHeight();
            NativeImage[] masks = new NativeImage[carriers.length];
            try {
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int rgba = pixel(image, x, y);
                        if ((rgba >>> 24) == 0) continue;
                        int layer = band(gray(rgba), masks.length);
                        if (masks[layer] == null) masks[layer] = new NativeImage(width, height, true);
                        int color = (rgba & 0xFF000000) | carriers[layer];
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
            TextureAtlasSprite[] layers = new TextureAtlasSprite[PearlFireTintProfiles.PEARL_FLAME.layerCount()];
            boolean found = false;
            for (int i = 0; i < layers.length; i++) {
                layers[i] = sprites.get(id(sprite.contents().name(), i));
                found |= layers[i] != null;
            }
            if (found) ((Source) sprite.contents()).musavacca$layers(layers);
        }
    }

    static int gray(int argb) {
        return (54 * ((argb >>> 16) & 255) + 183 * ((argb >>> 8) & 255) + 19 * (argb & 255) + 128) >>> 8;
    }

    static int band(int gray, int count) {
        return (255 - gray) * count / 256;
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
