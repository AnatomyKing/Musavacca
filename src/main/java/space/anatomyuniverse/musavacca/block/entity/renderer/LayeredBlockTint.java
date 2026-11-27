package space.anatomyuniverse.musavacca.block.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.level.block.state.BlockState;
import space.anatomyuniverse.musavacca.MusaCore;
import space.anatomyuniverse.musavacca.block.ModBlocks;
import space.anatomyuniverse.musavacca.block.custom.MusavaccaPortalDoorBlock;
import space.anatomyuniverse.musavacca.block.custom.MusavaccaPortalTrapdoorBlock;
import space.anatomyuniverse.musavacca.block.custom.VocoPostBlock;
import space.anatomyuniverse.musavacca.block.custom.VocoTableBlock;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoReceptorLogic.ReceptorPosition;
import space.anatomyuniverse.musavacca.block.entity.custom.VocoTableBlockEntity;
import space.anatomyuniverse.musavacca.block.custom.logic.PearlFlameConversion;
import space.anatomyuniverse.musavacca.tint.PearlFireTintProfiles;
import space.anatomyuniverse.musavacca.tint.SpriteTintLayers;
import space.anatomyuniverse.musavacca.tint.MusavaccaTints;
import space.anatomyuniverse.musavacca.tint.PearlFireTintProfiles.Settings;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.Objects;
import org.jetbrains.annotations.Nullable;
//? if >=1.21.11 {
/*import net.minecraft.resources.Identifier;
*///?} else {
import net.minecraft.resources.ResourceLocation;
//?}
//? if >=26.1 {
/*import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
*///?} else {
import net.minecraft.client.renderer.block.model.BakedQuad;
//?}
//? if >=1.21.11 {
/*import net.minecraft.client.model.geom.builders.UVPair;
*///?}

public final class LayeredBlockTint {
    private static final Map<BakedQuad, BakedQuad[]> QUADS = new WeakHashMap<>();
    private static final Predicate<TextureAtlasSprite> PORTAL = sprite -> isPortal(sprite.contents());
    private static final Predicate<TextureAtlasSprite> FIRE = sprite -> isPearlFire(sprite.contents());
    private static final Predicate<TextureAtlasSprite> FLAME = sprite -> isFlame(sprite.contents());

    private final int color;
    private final boolean portalOnly;
    @Nullable private final int[] cornerColors;
    private final Settings settings;
    private final Predicate<TextureAtlasSprite> filter;

    private LayeredBlockTint(int hex, Settings settings, Predicate<TextureAtlasSprite> filter) {
        this(hex, settings, filter, false, null);
    }

    private LayeredBlockTint(int hex, Settings settings, Predicate<TextureAtlasSprite> filter,
                             boolean portalOnly, @Nullable int[] cornerColors) {
        this.color = hex;
        this.portalOnly = portalOnly;
        this.cornerColors = cornerColors;
        this.settings = Objects.requireNonNull(settings);
        this.filter = Objects.requireNonNull(filter);
    }

    @Nullable
    public static LayeredBlockTint of(int hex, Settings settings, Predicate<TextureAtlasSprite> filter) {
        return MusavaccaTints.hasTint(hex) ? new LayeredBlockTint(hex, settings, filter) : null;
    }

    public static void registerSpriteLayers() {
        SpriteTintLayers.register(TextureAtlas.LOCATION_BLOCKS,
                sprite -> isPortal(sprite) || isFlame(sprite), false);
        SpriteTintLayers.register(TextureAtlas.LOCATION_PARTICLES, sprite -> true, true);
        SpriteTintLayers.register(
                //? if >=1.21.11 {
                /*Identifier.fromNamespaceAndPath("minecraft", "textures/atlas/gui.png"),
                *///?} else {
                ResourceLocation.fromNamespaceAndPath("minecraft", "textures/atlas/gui.png"),
                //?}
                sprite -> sprite.name().getNamespace().equals(MusaCore.MOD_ID)
                        && switch (sprite.name().getPath()) {
                            case "flint_and_pearl", "flint_and_pearl_pressed" -> true;
                            default -> false;
                        }, false);
    }

    private static boolean isPortal(SpriteContents sprite) {
        return sprite.name().getNamespace().equals(MusaCore.MOD_ID) && switch (sprite.name().getPath()) {
            case "block/pearl_portal", "block/pearl_portal/pearl_portal" -> true;
            default -> false;
        };
    }

    private static boolean isPearlFire(SpriteContents sprite) {
        return sprite.name().getNamespace().equals(MusaCore.MOD_ID) && switch (sprite.name().getPath()) {
            case "block/pearl_fire_0", "block/pearl_fire_1" -> true;
            default -> false;
        };
    }

    private static boolean isFlame(SpriteContents sprite) {
        return sprite.name().getPath().contains("_fire");
    }

    public static boolean isPortalCarrier(BlockState state) {
        return state.is(ModBlocks.VOCO_POST.get()) || state.is(ModBlocks.VOCO_TABLE.get())
                || state.is(ModBlocks.MUSAVACCA_DOOR.get()) || state.is(ModBlocks.MUSAVACCA_TRAPDOOR.get());
    }

    public static boolean hasPortalOverlay(BlockState state) {
        if (state.is(ModBlocks.MUSAVACCA_DOOR.get()))
            return state.getValue(MusavaccaPortalDoorBlock.LIT_PORTAL) || state.getValue(MusavaccaPortalDoorBlock.PORTAL);
        if (state.is(ModBlocks.MUSAVACCA_TRAPDOOR.get()))
            return state.getValue(MusavaccaPortalTrapdoorBlock.LIT_PORTAL) || state.getValue(MusavaccaPortalTrapdoorBlock.PORTAL);
        if (state.is(ModBlocks.VOCO_POST.get())) return state.getValue(VocoPostBlock.PORTAL);
        if (!state.is(ModBlocks.VOCO_TABLE.get())) return false;
        for (var property : VocoTableBlock.RECEPTOR_PORTALS) {
            if (state.getValue(property)) return true;
        }
        return false;
    }

    public static boolean isPortalQuad(BakedQuad quad) {
        return PORTAL.test(sprite(quad));
    }

    boolean portalOnly() {
        return portalOnly;
    }

    @Nullable
    static LayeredBlockTint forVocoTable(VocoTableBlockEntity table) {
        BlockState state = table.getBlockState();
        if (!hasPortalOverlay(state)) return null;
        int[] colors = new int[ReceptorPosition.COUNT];
        for (var receptor : ReceptorPosition.values()) {
            colors[receptor.id()] = state.getValue(VocoTableBlock.portalProperty(receptor))
                    ? table.getCornerHexColor(receptor) : MusavaccaTints.NO_TINT;
        }
        return new LayeredBlockTint(MusavaccaTints.NO_TINT, PearlFireTintProfiles.PORTAL_BLOCK, PORTAL, true, colors);
    }

    @Nullable
    static LayeredBlockTint forBlock(BlockState state, int hex) {
        if (isPortalCarrier(state)) return hasPortalOverlay(state)
                ? new LayeredBlockTint(hex, PearlFireTintProfiles.PORTAL_BLOCK, PORTAL, true, null) : null;
        if (state.is(ModBlocks.PEARL_PORTAL.get())) return new LayeredBlockTint(hex, PearlFireTintProfiles.PORTAL_BLOCK, PORTAL);
        if (state.is(ModBlocks.PEARL_FIRE.get())) return new LayeredBlockTint(hex, PearlFireTintProfiles.FIRE_BLOCK, FIRE);
        return PearlFlameConversion.value(state, "lit")
                ? of(hex, PearlFireTintProfiles.forFlame(state), FLAME)
                : null;
    }

    private static TextureAtlasSprite sprite(BakedQuad quad) {
        //? if >=26.1 {
        /*return quad.materialInfo().sprite();
        *///?} else if >=1.21.5 {
        return quad.sprite();
        //?} else {
        /*return quad.getSprite();
        *///?}
    }

    private int color(BakedQuad quad) {
        if (cornerColors == null) return color;
        // Baked coordinates are block-local; the four table portal models occupy separate X/Z quadrants.
        float x = 0.0F, z = 0.0F;
        //? if >=1.21.11 {
        /*for (int i = 0; i < 4; i++) {
            var position = quad.position(i);
            x += position.x();
            z += position.z();
        }
        *///?} else {
        //? if >=1.21.5 {
        int[] vertices = quad.vertices();
        //?} else {
        /*int[] vertices = quad.getVertices();
        *///?}
        int stride = vertices.length / 4;
        for (int i = 0; i < 4; i++) {
            x += Float.intBitsToFloat(vertices[i * stride]);
            z += Float.intBitsToFloat(vertices[i * stride + 2]);
        }
        //?}
        var receptor = z < 2.0F
                ? (x >= 2.0F ? ReceptorPosition.NORTH_EAST : ReceptorPosition.NORTH_WEST)
                : (x >= 2.0F ? ReceptorPosition.SOUTH_EAST : ReceptorPosition.SOUTH_WEST);
        return cornerColors[receptor.id()];
    }

    private static BakedQuad[] layers(BakedQuad quad, TextureAtlasSprite[] sprites) {
        synchronized (QUADS) {
            return QUADS.computeIfAbsent(quad, key -> {
                BakedQuad[] result = new BakedQuad[sprites.length];
                TextureAtlasSprite sprite = sprite(key);
                for (int i = 0; i < sprites.length; i++) {
                    if (sprites[i] != null) result[i] = remap(key, sprite, sprites[i]);
                }
                return result;
            });
        }
    }

    void emit(BakedQuad quad, BiConsumer<BakedQuad, Integer> output) {
        TextureAtlasSprite sprite = sprite(quad);
        if (!filter.test(sprite)) {
            if (!portalOnly) output.accept(quad, MusavaccaTints.NO_TINT);
            return;
        }
        int color = color(quad);
        var data = MusavaccaTints.hasTint(color) ? SpriteTintLayers.layers(sprite) : null;
        if (data == null || data.sprites() == null) {
            output.accept(quad, MusavaccaTints.NO_TINT);
            return;
        }
        BakedQuad[] layers = layers(quad, data.sprites());
        int[] colors = SpriteTintLayers.palette(color, settings, data.tones());
        boolean emitted = false;
        for (int i = 0; i < layers.length; i++) {
            if (layers[i] == null) continue;
            output.accept(layers[i], colors[i]);
            emitted = true;
        }
        if (!emitted) output.accept(quad, MusavaccaTints.NO_TINT);
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
        private final LayeredBlockTint tint;

        QuadOutput(LayeredBlockTint tint) {
            this.tint = tint;
        }

        @Override
        public final void putBulkData(PoseStack.Pose pose, BakedQuad quad, float[] brightness,
                                      float red, float green, float blue, float alpha, int[] light, int overlay
                                      //? if <1.21.11 {
                                      , boolean readColor
                                      //?}
        ) {
            //? if >=1.21.9 {
            /*float[] shades = brightness.clone();
            int[] lights = light.clone();
            *///?} else {
            float[] shades = brightness;
            int[] lights = light;
            //?}
            this.tint.emit(quad, (face, tint) -> {
                boolean colored = tint != MusavaccaTints.NO_TINT;
                float r = colored ? red * ((tint >>> 16) & 255) / 255.0F : red;
                float g = colored ? green * ((tint >>> 8) & 255) / 255.0F : green;
                float b = colored ? blue * (tint & 255) / 255.0F : blue;
                write(pose, face, shades, r, g, b, alpha, lights, overlay, colored
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
