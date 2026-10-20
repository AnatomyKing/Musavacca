package space.anatomyuniverse.musavacca.render;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.event.ModelEvent;
import space.anatomyuniverse.musavacca.block.ModBlocks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;

//? if >=26.1 {
/*import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.util.TriState;
import net.neoforged.neoforge.client.model.DelegateBlockStateModel;
*///?} else if >=1.21.6 {
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.util.TriState;
import net.minecraft.world.level.BlockAndTintGetter;
import net.neoforged.neoforge.client.model.DelegateBlockStateModel;
//?} else if >=1.21.5 {
/*import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.util.TriState;
import net.minecraft.world.level.BlockAndTintGetter;
import net.neoforged.neoforge.client.model.DelegateBlockStateModel;
*///?} else {
/*import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.common.util.TriState;
*///?}

//? if >=1.21.4 <1.21.5 {
/*import net.minecraft.client.resources.model.DelegateBakedModel;
*///?} else if <1.21.4 {
/*import net.neoforged.neoforge.client.model.BakedModelWrapper;
*///?}


public final class MusaRenderLayers {

    private static final Set<Block> CUTOUT_BLOCKS = Set.of(
            ModBlocks.MUSAVACCA_EGG.get(),
            ModBlocks.HEX_BLOCK.get(),
            ModBlocks.PEARL_FIRE.get(),
            ModBlocks.PEARL_PORTAL.get(),
            ModBlocks.VOCO_TABLE.get(),
            ModBlocks.VOCO_POST.get(),
            ModBlocks.MUSAVACCA_SPROUT.get(),
            ModBlocks.MUSAVACCA_SUCKER.get(),
            ModBlocks.MUSAVACCA_PLANT.get(),
            ModBlocks.MUSAVACCA_PSEUDOSTEM.get(),
            ModBlocks.MUSAVACCA_DOOR.get(),
            ModBlocks.MUSAVACCA_TRAPDOOR.get(),
            ModBlocks.CAROTENE_GRASS.get(),
            ModBlocks.CAROTENE_SHORT_GRASS.get(),
            ModBlocks.CAROTENE_TALL_GRASS.get(),
            ModBlocks.BANANA_PEARL_MASCARON.get()
    );

    private static final Set<Block> TRANSLUCENT_BLOCKS = Set.of(

    );

    private static final Set<Block> NO_AO_BLOCKS = Set.of(
            ModBlocks.HEX_BLOCK.get(),
            ModBlocks.MUSAVACCA_LEAVES.get(),
            ModBlocks.PEARL_PORTAL.get()
    );

    private static final Set<Block> NO_DIRECTIONAL_SHADE_BLOCKS = Set.of(
            ModBlocks.PEARL_PORTAL.get()
    );

    private MusaRenderLayers() {}

    private record Properties(boolean cutout, boolean translucent, boolean noAo, boolean noShade) {
        boolean any() { return cutout || translucent || noAo || noShade; }
    }

    private static Properties properties(Block block) {
        return new Properties(
                CUTOUT_BLOCKS.contains(block),
                TRANSLUCENT_BLOCKS.contains(block),
                NO_AO_BLOCKS.contains(block),
                NO_DIRECTIONAL_SHADE_BLOCKS.contains(block)
        );
    }

    private static Set<Block> configuredBlocks() {
        Set<Block> blocks = new LinkedHashSet<>();
        blocks.addAll(CUTOUT_BLOCKS);
        blocks.addAll(TRANSLUCENT_BLOCKS);
        blocks.addAll(NO_AO_BLOCKS);
        blocks.addAll(NO_DIRECTIONAL_SHADE_BLOCKS);
        return blocks;
    }

    public static void onModifyBakingResult(ModelEvent.ModifyBakingResult event) {
        if (!Collections.disjoint(CUTOUT_BLOCKS, TRANSLUCENT_BLOCKS)) {
            throw new IllegalStateException("A block cannot be both cutout and translucent");
        }

        //? if >=1.21.5 {
        var models = event.getBakingResult().blockStateModels();
        models.replaceAll((state, original) -> {
            Properties p = properties(state.getBlock());
            return p.any() ? new ForcedStateModel(original, p) : original;
        });
        //?} else {
        /*onModifyBakingResultLegacy(event);
        *///?}
    }

    //? if >=1.21.4 <1.21.5 {
    /*private static void onModifyBakingResultLegacy(ModelEvent.ModifyBakingResult event) {
        replaceLegacy(event.getBakingResult().blockStateModels());
    }
    *///?} else if <1.21.4 {
    /*private static void onModifyBakingResultLegacy(ModelEvent.ModifyBakingResult event) {
        replaceLegacy(event.getModels());
    }
    *///?}

    //? if <1.21.5 {
    /*private static void replaceLegacy(java.util.Map<ModelResourceLocation, BakedModel> models) {
        for (Block block : configuredBlocks()) {
            Properties p = properties(block);
            for (BlockState state : block.getStateDefinition().getPossibleStates()) {
                ModelResourceLocation key = BlockModelShaper.stateToModelLocation(state);
                models.computeIfPresent(key, (id, original) -> new ForcedBakedModel(original, p));
            }
        }
    }
    *///?}

    //? if >=26.1 {
    /*private static final class ForcedStateModel extends DelegateBlockStateModel {
        private final Properties properties;

        ForcedStateModel(BlockStateModel original, Properties properties) {
            super(original);
            this.properties = properties;
        }

        @Override
        public void collectParts(BlockAndTintGetter level, BlockPos pos, BlockState state,
                                 RandomSource random, List<BlockStateModelPart> output) {
            List<BlockStateModelPart> originals = new ArrayList<>();
            delegate.collectParts(level, pos, state, random, originals);
            for (BlockStateModelPart part : originals) {
                output.add(new ForcedPart(part, properties));
            }
        }

        @Override public int materialFlags() {
            return forceFlags(delegate.materialFlags(), properties);
        }

        @Override public int materialFlags(BlockAndTintGetter level, BlockPos pos, BlockState state) {
            return forceFlags(delegate.materialFlags(level, pos, state), properties);
        }
    }

    private record ForcedPart(BlockStateModelPart original, Properties properties)
            implements BlockStateModelPart {
        @Override public List<BakedQuad> getQuads(Direction face) {
            List<BakedQuad> source = original.getQuads(face);
            if (source.isEmpty()) return source;

            List<BakedQuad> rewritten = null;
            for (int i = 0; i < source.size(); i++) {
                BakedQuad quad = source.get(i);
                BakedQuad.MaterialInfo info = quad.materialInfo();
                BakedQuad.MaterialInfo changedInfo = adjustMaterial(info, properties);
                if (changedInfo != info && rewritten == null) rewritten = new ArrayList<>(source);
                if (changedInfo != info) {
                    rewritten.set(i, new BakedQuad(
                            quad.position0(), quad.position1(), quad.position2(), quad.position3(),
                            quad.packedUV0(), quad.packedUV1(), quad.packedUV2(), quad.packedUV3(),
                            quad.direction(), changedInfo, quad.bakedNormals(), quad.bakedColors()));
                }
            }
            return rewritten == null ? source : List.copyOf(rewritten);
        }


        @Override public boolean useAmbientOcclusion() {
            return !properties.noAo() && original.useAmbientOcclusion();
        }
        @Override public TriState ambientOcclusion() {
            return properties.noAo() ? TriState.FALSE : original.ambientOcclusion();
        }
        @Override public net.minecraft.client.resources.model.sprite.Material.Baked particleMaterial() {
            return original.particleMaterial();
        }
        @Override public int materialFlags() {
            return forceFlags(original.materialFlags(), properties);
        }
    }

    private static ChunkSectionLayer forcedLayer(Properties p, ChunkSectionLayer original) {
        return p.cutout() ? ChunkSectionLayer.CUTOUT
                : p.translucent() ? ChunkSectionLayer.TRANSLUCENT : original;
    }

    private static int forceFlags(int flags, Properties p) {
        if (p.translucent()) return flags | BakedQuad.FLAG_TRANSLUCENT;
        if (p.cutout()) return flags & ~BakedQuad.FLAG_TRANSLUCENT;
        return flags;
    }
    *///?}


    //? if >=26.3 {
    /*private static BakedQuad.MaterialInfo adjustMaterial(BakedQuad.MaterialInfo info, Properties p) {
        ChunkSectionLayer layer = forcedLayer(p, info.layer());
        boolean ao = !p.noAo() && info.ambientOcclusion();
        Direction shade = p.noShade() ? Direction.UP : info.shadeDirectionOverride();
        if (layer == info.layer() && ao == info.ambientOcclusion()
                && shade == info.shadeDirectionOverride()) return info;
        return new BakedQuad.MaterialInfo(
                info.sprite(), layer, info.itemRenderType(),
                info.itemGlintRenderType(), info.itemGlintSpecialRenderType(),
                info.tintIndex(), shade, info.lightEmission(), ao);
    }
    *///?} else if >=26.1 {
    /*private static BakedQuad.MaterialInfo adjustMaterial(BakedQuad.MaterialInfo info, Properties p) {
        ChunkSectionLayer layer = forcedLayer(p, info.layer());
        boolean ao = !p.noAo() && info.ambientOcclusion();
        boolean shade = !p.noShade() && info.shade();
        if (layer == info.layer() && ao == info.ambientOcclusion()
                && shade == info.shade()) return info;
        return new BakedQuad.MaterialInfo(
                info.sprite(), layer, info.itemRenderType(),
                info.tintIndex(), shade, info.lightEmission(), ao);
    }
    *///?}

    //? if >=1.21.5 <26.1 {
    private static final class ForcedStateModel extends DelegateBlockStateModel {
        private final Properties properties;

        ForcedStateModel(BlockStateModel original, Properties properties) {
            super(original);
            this.properties = properties;
        }

        @Override
        public void collectParts(BlockAndTintGetter level, BlockPos pos, BlockState state,
                                 RandomSource random, List<BlockModelPart> output) {
            List<BlockModelPart> originals = new ArrayList<>();
            delegate.collectParts(level, pos, state, random, originals);
            for (BlockModelPart part : originals) {
                output.add(new ForcedPart(part, properties));
            }
        }
    }
    //?}

    //? if >=1.21.6 <26.1 {
    private record ForcedPart(BlockModelPart original, Properties properties)
            implements BlockModelPart {
        @Override public List<BakedQuad> getQuads(Direction face) {
            List<BakedQuad> quads = original.getQuads(face);
            return properties.noShade() ? withoutDirectionalShade(quads) : quads;
        }
        @Override public boolean useAmbientOcclusion() {
            return !properties.noAo() && original.useAmbientOcclusion();
        }
        @Override public TriState ambientOcclusion() {
            return properties.noAo() ? TriState.FALSE : original.ambientOcclusion();
        }
        @Override public TextureAtlasSprite particleIcon() {
            return original.particleIcon();
        }
        @Override public ChunkSectionLayer getRenderType(BlockState state) {
            return properties.cutout() ? ChunkSectionLayer.CUTOUT
                    : properties.translucent() ? ChunkSectionLayer.TRANSLUCENT
                    : original.getRenderType(state);
        }
    }
    //?} else if >=1.21.5 {
    /*private record ForcedPart(BlockModelPart original, Properties properties)
            implements BlockModelPart {
        @Override public List<BakedQuad> getQuads(Direction face) {
            List<BakedQuad> quads = original.getQuads(face);
            return properties.noShade() ? withoutDirectionalShade(quads) : quads;
        }
        @Override public boolean useAmbientOcclusion() {
            return !properties.noAo() && original.useAmbientOcclusion();
        }
        @Override public TriState ambientOcclusion() {
            return properties.noAo() ? TriState.FALSE : original.ambientOcclusion();
        }
        @Override public TextureAtlasSprite particleIcon() {
            return original.particleIcon();
        }
        @Override public RenderType getRenderType(BlockState state) {
            return properties.cutout() ? RenderType.cutout()
                    : properties.translucent() ? RenderType.translucent()
                    : original.getRenderType(state);
        }
    }
    *///?}

    //? if >=1.21.5 <26.1 {
    private static List<BakedQuad> withoutDirectionalShade(List<BakedQuad> source) {
        List<BakedQuad> rewritten = null;
        for (int i = 0; i < source.size(); i++) {
            BakedQuad quad = source.get(i);
            if (!quad.shade()) continue;
            if (rewritten == null) rewritten = new ArrayList<>(source);
            rewritten.set(i, copyWithoutShade(quad));
        }
        return rewritten == null ? source : List.copyOf(rewritten);
    }
    //?}

    //? if >=1.21.11 <26.1 {
    /*private static BakedQuad copyWithoutShade(BakedQuad quad) {
        return new BakedQuad(
                quad.position0(), quad.position1(), quad.position2(), quad.position3(),
                quad.packedUV0(), quad.packedUV1(), quad.packedUV2(), quad.packedUV3(),
                quad.tintIndex(), quad.direction(), quad.sprite(), false,
                quad.lightEmission(), quad.bakedNormals(), quad.bakedColors(),
                quad.hasAmbientOcclusion());
    }
    *///?} else if >=1.21.5 {
    private static BakedQuad copyWithoutShade(BakedQuad quad) {
        return new BakedQuad(
                quad.vertices(), quad.tintIndex(), quad.direction(), quad.sprite(),
                false, quad.lightEmission(), quad.hasAmbientOcclusion());
    }
    //?}


    //? if >=1.21.4 <1.21.5 {
    /*private static final class ForcedBakedModel extends DelegateBakedModel {
    *///?} else if <1.21.4 {
    /*private static final class ForcedBakedModel extends BakedModelWrapper<BakedModel> {
    *///?}

    //? if <1.21.5 {
    /*    private final Properties properties;

        ForcedBakedModel(BakedModel original, Properties properties) {
            super(original);
            this.properties = properties;
        }

        private RenderType forcedRenderType() {
            return properties.cutout() ? RenderType.cutout()
                    : properties.translucent() ? RenderType.translucent() : null;
        }

        @Override
        public ChunkRenderTypeSet getRenderTypes(BlockState state, RandomSource random, ModelData data) {
            RenderType renderType = forcedRenderType();
            return renderType != null
                    ? ChunkRenderTypeSet.of(renderType)
                    : super.getRenderTypes(state, random, data);
        }

        @Override
        public List<BakedQuad> getQuads(BlockState state, Direction face, RandomSource random,
                                        ModelData data, RenderType renderType) {
            RenderType forced = forcedRenderType();
            if (forced != null && renderType != null && !forced.equals(renderType)) {
                return List.of();
            }
            List<BakedQuad> quads = super.getQuads(state, face, random, data,
                    forced != null ? null : renderType);
            return properties.noShade() ? withoutDirectionalShade(quads) : quads;
        }

        @Override
        public TriState useAmbientOcclusion(BlockState state, ModelData data, RenderType renderType) {
            return properties.noAo() ? TriState.FALSE
                    : super.useAmbientOcclusion(state, data, renderType);
        }
    }

    private static List<BakedQuad> withoutDirectionalShade(List<BakedQuad> source) {
        List<BakedQuad> rewritten = null;
        for (int i = 0; i < source.size(); i++) {
            BakedQuad quad = source.get(i);
            if (!quad.isShade()) continue;
            if (rewritten == null) rewritten = new ArrayList<>(source);
            rewritten.set(i, copyWithoutShade(quad));
        }
        return rewritten == null ? source : List.copyOf(rewritten);
    }
    *///?}

    //? if >=1.21.3 <1.21.5 {
    /*private static BakedQuad copyWithoutShade(BakedQuad quad) {
        return new BakedQuad(
                quad.getVertices(), quad.getTintIndex(), quad.getDirection(),
                quad.getSprite(), false, quad.getLightEmission(), quad.hasAmbientOcclusion());
    }
    *///?} else if <1.21.3 {
    /*private static BakedQuad copyWithoutShade(BakedQuad quad) {
        return new BakedQuad(
                quad.getVertices(), quad.getTintIndex(), quad.getDirection(),
                quad.getSprite(), false, quad.hasAmbientOcclusion());
    }
    *///?}
}
