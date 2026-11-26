package space.anatomyuniverse.musavacca.block.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.level.block.state.BlockState;
import space.anatomyuniverse.musavacca.block.custom.logic.PearlCandleConversion;
import space.anatomyuniverse.musavacca.particle.tinted.CandleSpriteLayers;
import space.anatomyuniverse.musavacca.tint.MusavaccaTints;
import space.anatomyuniverse.musavacca.tint.PearlFireTintProfiles;
import space.anatomyuniverse.musavacca.tint.PearlFireTintSource;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.BiConsumer;
//? if >=26.1 {
/*import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
*///?} else {
import net.minecraft.client.renderer.block.model.BakedQuad;
//?}
//? if >=1.21.11 {
/*import net.minecraft.client.model.geom.builders.UVPair;
*///?}

final class PearlCandleFlames {
    private static final int CACHE_LIMIT = 256;
    private static final Map<BakedQuad, List<Layer>> QUADS = new WeakHashMap<>();
    private static final LinkedHashMap<Integer, int[]> PALETTES = new LinkedHashMap<>(16, 0.75F, true);

    private PearlCandleFlames() {}

    private record Layer(BakedQuad quad, int index) {}

    static synchronized int[] palette(BlockState state, int hex) {
        if (!MusavaccaTints.hasTint(hex) || !PearlCandleConversion.value(state, "lit")) return null;
        int[] cached = PALETTES.get(hex);
        if (cached != null) return cached;
        var profile = PearlFireTintProfiles.PEARL_CANDLE;
        int[] colors = new int[profile.layerCount()];
        for (int i = 0; i < colors.length; i++)
            colors[i] = MusavaccaTints.rgb(PearlFireTintSource.profileTint(hex, i, profile));
        if (PALETTES.size() >= CACHE_LIMIT) PALETTES.pollFirstEntry();
        PALETTES.put(hex, colors);
        return colors;
    }

    static TextureAtlasSprite sprite(BakedQuad quad) {
        //? if >=26.1 {
        /*return quad.materialInfo().sprite();
        *///?} else if >=1.21.5 {
        return quad.sprite();
        //?} else {
        /*return quad.getSprite();
        *///?}
    }

    private static List<Layer> layers(BakedQuad quad) {
        TextureAtlasSprite sprite = sprite(quad);
        if (!sprite.contents().name().getPath().contains("_fire")) return List.of();
        if (!((Object) sprite.contents() instanceof CandleSpriteLayers.Source source)) return List.of();
        TextureAtlasSprite[] sprites = source.musavacca$layers();
        if (sprites == null) return List.of();
        synchronized (QUADS) {
            return QUADS.computeIfAbsent(quad, key -> {
                List<Layer> result = new ArrayList<>();
                for (int i = 0; i < sprites.length; i++) {
                    if (sprites[i] != null) result.add(new Layer(remap(key, sprite, sprites[i]), i));
                }
                return List.copyOf(result);
            });
        }
    }

    static void emit(BakedQuad quad, int[] palette, BiConsumer<BakedQuad, Integer> output) {
        List<Layer> layers = palette == null ? List.of() : layers(quad);
        if (layers.isEmpty()) {
            output.accept(quad, MusavaccaTints.NO_TINT);
            return;
        }
        for (Layer layer : layers) output.accept(layer.quad(), palette[layer.index()]);
    }

    private static float remap(float value, float from0, float from1, float to0, float to1) {
        return to0 + (value - from0) / (from1 - from0) * (to1 - to0);
    }

    private static float u(float value, TextureAtlasSprite from, TextureAtlasSprite to) {
        return remap(value, from.getU0(), from.getU1(), to.getU0(), to.getU1());
    }

    private static float v(float value, TextureAtlasSprite from, TextureAtlasSprite to) {
        return remap(value, from.getV0(), from.getV1(), to.getV0(), to.getV1());
    }

    //? if >=1.21.11 {
    /*private static long uv(long value, TextureAtlasSprite from, TextureAtlasSprite to) {
        return UVPair.pack(u(UVPair.unpackU(value), from, to), v(UVPair.unpackV(value), from, to));
    }
    *///?}

    private static BakedQuad remap(BakedQuad quad, TextureAtlasSprite from, TextureAtlasSprite to) {
        //? if >=1.21.11 {
        /*return new BakedQuad(quad.position0(), quad.position1(), quad.position2(), quad.position3(),
                uv(quad.packedUV0(), from, to), uv(quad.packedUV1(), from, to),
                uv(quad.packedUV2(), from, to), uv(quad.packedUV3(), from, to),
                //? if >=26.1 {
                /^quad.direction(), material(quad.materialInfo(), to), quad.bakedNormals(), quad.bakedColors());
                ^///?} else {
                quad.tintIndex(), quad.direction(), to, quad.shade(), quad.lightEmission(),
                quad.bakedNormals(), quad.bakedColors(), quad.hasAmbientOcclusion());
                //?}
        *///?} else {
        //? if >=1.21.5 {
        int[] vertices = quad.vertices().clone();
        //?} else {
        /*int[] vertices = quad.getVertices().clone();
        *///?}
        int stride = vertices.length / 4;
        for (int i = 0; i < 4; i++) {
            int offset = i * stride + 4;
            vertices[offset] = Float.floatToRawIntBits(u(Float.intBitsToFloat(vertices[offset]), from, to));
            vertices[offset + 1] = Float.floatToRawIntBits(v(Float.intBitsToFloat(vertices[offset + 1]), from, to));
        }
        //? if >=1.21.5 {
        return new BakedQuad(vertices, quad.tintIndex(), quad.direction(), to, quad.shade(), quad.lightEmission(), quad.hasAmbientOcclusion());
        //?} else if >=1.21.3 {
        /*return new BakedQuad(vertices, quad.getTintIndex(), quad.getDirection(), to, quad.isShade(), quad.getLightEmission(), quad.hasAmbientOcclusion());
        *///?} else {
        /*return new BakedQuad(vertices, quad.getTintIndex(), quad.getDirection(), to, quad.isShade(), quad.hasAmbientOcclusion());
        *///?}
        //?}
    }

    //? if >=26.1 {
    /*private static BakedQuad.MaterialInfo material(BakedQuad.MaterialInfo info, TextureAtlasSprite sprite) {
        return new BakedQuad.MaterialInfo(sprite,
                info.layer() == ChunkSectionLayer.SOLID ? ChunkSectionLayer.CUTOUT : info.layer(),
                info.itemRenderType(),
                //? if >=26.3 {
                /^info.itemGlintRenderType(), info.itemGlintSpecialRenderType(),
                info.tintIndex(), info.shadeDirectionOverride(),
                ^///?} else {
                info.tintIndex(), info.shade(),
                //?}
                info.lightEmission(), info.ambientOcclusion());
    }

    static int multiply(int original, int tint) {
        if (tint == MusavaccaTints.NO_TINT) return original;
        return (original & 0xFF000000)
                | ((((original >>> 16) & 255) * ((tint >>> 16) & 255) / 255) << 16)
                | ((((original >>> 8) & 255) * ((tint >>> 8) & 255) / 255) << 8)
                | ((original & 255) * (tint & 255) / 255);
    }
    *///?} else {
    abstract static class QuadOutput implements VertexConsumer {
        private final int[] palette;

        QuadOutput(int[] palette) {
            this.palette = palette;
        }

        @Override
        public final void putBulkData(PoseStack.Pose pose, BakedQuad quad, float[] brightness,
                                      float red, float green, float blue, float alpha, int[] light, int overlay
                                      //? if <1.21.11 {
                                      , boolean readColor
                                      //?}
        ) {
            emit(quad, palette, (face, tint) -> {
                boolean colored = tint != MusavaccaTints.NO_TINT;
                float r = colored ? red * ((tint >>> 16) & 255) / 255.0F : red;
                float g = colored ? green * ((tint >>> 8) & 255) / 255.0F : green;
                float b = colored ? blue * (tint & 255) / 255.0F : blue;
                write(pose, face, brightness, r, g, b, alpha, light, overlay, colored
                        //? if <1.21.11 {
                        , readColor
                        //?}
                );
            });
        }

        @Override
        public final void putBulkData(PoseStack.Pose pose, BakedQuad quad, float red, float green, float blue,
                                      float alpha, int light, int overlay) {
            putBulkData(pose, quad, new float[] {1, 1, 1, 1}, red, green, blue, alpha,
                    new int[] {light, light, light, light}, overlay
                    //? if <1.21.11 {
                    , false
                    //?}
            );
        }

        abstract void write(PoseStack.Pose pose, BakedQuad quad, float[] brightness, float red, float green, float blue,
                            float alpha, int[] light, int overlay, boolean colored
                            //? if <1.21.11 {
                            , boolean readColor
                            //?}
        );

        VertexConsumer delegate() {
            throw new UnsupportedOperationException("Expected a baked quad");
        }

        @Override public VertexConsumer addVertex(float x, float y, float z) { delegate().addVertex(x, y, z); return this; }
        @Override public VertexConsumer setColor(int r, int g, int b, int a) { delegate().setColor(r, g, b, a); return this; }
        @Override public VertexConsumer setColor(int color) { delegate().setColor(color); return this; }
        @Override public VertexConsumer setUv(float u, float v) { delegate().setUv(u, v); return this; }
        @Override public VertexConsumer setUv1(int u, int v) { delegate().setUv1(u, v); return this; }
        @Override public VertexConsumer setUv2(int u, int v) { delegate().setUv2(u, v); return this; }
        @Override public VertexConsumer setNormal(float x, float y, float z) { delegate().setNormal(x, y, z); return this; }
        //? if >=1.21.11 {
        /*@Override public VertexConsumer setLineWidth(float width) { delegate().setLineWidth(width); return this; }
        *///?}
    }
    //?}
}
