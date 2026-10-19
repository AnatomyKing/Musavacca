package space.anatomyuniverse.musavacca.render;

import com.google.common.collect.Sets;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.event.ModelEvent;
import space.anatomyuniverse.musavacca.block.ModBlocks;

import java.util.*;
//? if >=1.21.5 <1.21.6 {
/*import net.minecraft.client.renderer.RenderType;
*///?}

//? if >=26.1 {
/*import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
*///?} else if >=1.21.5 <26.1 {
import net.minecraft.client.renderer.block.model.BlockModelPart;
//?} else {
/*import net.minecraft.client.renderer.RenderType;
*///?}

//? if >=26.1 {
/*import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
*///?} else if >=1.21.5 <26.1 {
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
//?} else {
/*import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
*///?}
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
//? if >=26.1 {
/*import net.minecraft.util.TriState;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.neoforged.neoforge.client.model.DelegateBlockStateModel;

import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
*///?} else if >=1.21.6 <26.1 {
import net.minecraft.util.TriState;
import net.minecraft.world.level.BlockAndTintGetter;
import net.neoforged.neoforge.client.model.DelegateBlockStateModel;

import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
//?} else if >=1.21.5 <1.21.6 {
/*import net.minecraft.util.TriState;
import net.minecraft.world.level.BlockAndTintGetter;
import net.neoforged.neoforge.client.model.DelegateBlockStateModel;
*///?} else if >=1.21.4 <1.21.5 {
/*import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.common.util.TriState;

import net.minecraft.client.resources.model.DelegateBakedModel;
*///?} else {
/*import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.common.util.TriState;

import net.neoforged.neoforge.client.model.BakedModelWrapper;
*///?}

public final class MusaRenderLayers {

    private static final Set<Block> CUTOUT_BLOCKS = Sets.newHashSet(
            ModBlocks.MUSAVACCA_EGG.get(),
            ModBlocks.HEX_BLOCK.get(),
            ModBlocks.PEARL_FIRE.get(),
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

    private static final Set<Block> TRANSLUCENT_BLOCKS = Sets.newHashSet(

    );

    private static final Set<Block> NO_AO_BLOCKS = Sets.newHashSet(
            ModBlocks.HEX_BLOCK.get(),
            ModBlocks.MUSAVACCA_LEAVES.get(),
            ModBlocks.PEARL_FIRE.get(),
            ModBlocks.PEARL_PORTAL.get()
    );

    private static final Set<Block> NO_DIRECTIONAL_SHADE_BLOCKS = Sets.newHashSet(
            ModBlocks.PEARL_FIRE.get(),
            ModBlocks.PEARL_PORTAL.get()

    );

    private MusaRenderLayers() {
    }

    public static void onModifyBakingResult(ModelEvent.ModifyBakingResult event) {
        if (CUTOUT_BLOCKS.isEmpty() && TRANSLUCENT_BLOCKS.isEmpty()
                && NO_AO_BLOCKS.isEmpty() && NO_DIRECTIONAL_SHADE_BLOCKS.isEmpty()) {
            return;
        }

        //? if >=1.21.5 {
        onModifyBakingResultNewPipeline(event);
        //?} else {
        /*onModifyBakingResultOldPipeline(event);
        *///?}

    }

    //? if >=26.3 {
    /*private static void onModifyBakingResultNewPipeline(ModelEvent.ModifyBakingResult event) {
        var models = event.getBakingResult().blockStateModels();
        for (var entry : models.entrySet()) {
            Block block = entry.getKey().getBlock();

            // Preserve the older pipeline's precedence if a block appears in both sets.
            ChunkSectionLayer forcedLayer = CUTOUT_BLOCKS.contains(block)
                    ? ChunkSectionLayer.CUTOUT
                    : TRANSLUCENT_BLOCKS.contains(block) ? ChunkSectionLayer.TRANSLUCENT : null;

            boolean noAo = NO_AO_BLOCKS.contains(block);
            boolean noDirectionalShade = NO_DIRECTIONAL_SHADE_BLOCKS.contains(block);

            if (forcedLayer != null || noAo || noDirectionalShade) {
                entry.setValue(new ForceRenderPropertiesModel(
                        entry.getValue(), forcedLayer, noAo, noDirectionalShade
                ));
            }
        }
    }
    *///?} else if >=26.1 <26.3 {
    /*private static void onModifyBakingResultNewPipeline(ModelEvent.ModifyBakingResult event) {
        // Transparency is part of baked materials on 26.1+.
        // The existing no-AO set still needs its explicit lighting override.
        var models = event.getBakingResult().blockStateModels();
        for (var entry : models.entrySet()) {
            if (NO_AO_BLOCKS.contains(entry.getKey().getBlock())) {
                entry.setValue(new ForceAmbientOcclusionModel(entry.getValue()));
            }
        }
    }
    *///?} else if >=1.21.6 <26.1 {
    private static void onModifyBakingResultNewPipeline(ModelEvent.ModifyBakingResult event) {
        Map<BlockState, BlockStateModel> models = event.getBakingResult().blockStateModels();

        if (models.isEmpty()) {
            return;
        }

        for (Map.Entry<BlockState, BlockStateModel> entry : models.entrySet()) {
            BlockState state = entry.getKey();
            BlockStateModel original = entry.getValue();
            Block block = state.getBlock();

            ChunkSectionLayer forcedLayer = null;

            if (CUTOUT_BLOCKS.contains(block)) {
                forcedLayer = ChunkSectionLayer.CUTOUT;
            } else if (TRANSLUCENT_BLOCKS.contains(block)) {
                forcedLayer = ChunkSectionLayer.TRANSLUCENT;
            }

            TriState forcedAO = NO_AO_BLOCKS.contains(block) ? TriState.FALSE : TriState.DEFAULT;

            if (forcedLayer != null || forcedAO != TriState.DEFAULT) {
                entry.setValue(new ForcePropsStateModel(original, forcedLayer, forcedAO));
            }
        }
    }
    //?} else if >=1.21.5 <1.21.6 {
    /*private static void onModifyBakingResultNewPipeline(ModelEvent.ModifyBakingResult event) {
        Map<BlockState, BlockStateModel> models = event.getBakingResult().blockStateModels();

        if (models.isEmpty()) {
            return;
        }

        for (Map.Entry<BlockState, BlockStateModel> entry : models.entrySet()) {
            BlockState state = entry.getKey();
            BlockStateModel original = entry.getValue();
            Block block = state.getBlock();

            RenderType forcedLayer = null;

            if (CUTOUT_BLOCKS.contains(block)) {
                forcedLayer = RenderType.cutout();
            } else if (TRANSLUCENT_BLOCKS.contains(block)) {
                forcedLayer = RenderType.translucent();
            }

            TriState forcedAO = NO_AO_BLOCKS.contains(block) ? TriState.FALSE : TriState.DEFAULT;

            if (forcedLayer != null || forcedAO != TriState.DEFAULT) {
                entry.setValue(new ForcePropsStateModel(original, forcedLayer, forcedAO));
            }
        }
    }
    *///?} else if >=1.21.4 <1.21.5 {
    /*private static void onModifyBakingResultOldPipeline(ModelEvent.ModifyBakingResult event) {
        final Set<Block> touched = new HashSet<>();
        touched.addAll(CUTOUT_BLOCKS);
        touched.addAll(TRANSLUCENT_BLOCKS);
        touched.addAll(NO_AO_BLOCKS);

        Map<ModelResourceLocation, BakedModel> models = event.getBakingResult().blockStateModels();

        if (models == null || models.isEmpty()) {
            return;
        }

        for (Block block : touched) {
            final RenderType forcedType =
                    CUTOUT_BLOCKS.contains(block) ? RenderType.cutout() :
                            TRANSLUCENT_BLOCKS.contains(block) ? RenderType.translucent() :
                                    null;

            final TriState forcedAO = NO_AO_BLOCKS.contains(block) ? TriState.FALSE : TriState.DEFAULT;

            if (forcedType == null && forcedAO == TriState.DEFAULT) {
                continue;
            }

            for (BlockState state : block.getStateDefinition().getPossibleStates()) {
                final ModelResourceLocation mrl = BlockModelShaper.stateToModelLocation(state);
                models.computeIfPresent(mrl, (key, original) -> new ForcePropsBakedModel(original, forcedType, forcedAO));
            }
        }

    }
    *///?} else {
    /*private static void onModifyBakingResultOldPipeline(ModelEvent.ModifyBakingResult event) {
        final Set<Block> touched = new HashSet<>();
        touched.addAll(CUTOUT_BLOCKS);
        touched.addAll(TRANSLUCENT_BLOCKS);
        touched.addAll(NO_AO_BLOCKS);

        Map<Object, BakedModel> models = getAllBakedModelsCompat(event);
        if (models == null || models.isEmpty()) {
            return;
        }

        for (Block block : touched) {
            final RenderType forcedType =
                    CUTOUT_BLOCKS.contains(block) ? RenderType.cutout() :
                            TRANSLUCENT_BLOCKS.contains(block) ? RenderType.translucent() :
                                    null;

            final TriState forcedAO = NO_AO_BLOCKS.contains(block) ? TriState.FALSE : TriState.DEFAULT;

            if (forcedType == null && forcedAO == TriState.DEFAULT) {
                continue;
            }

            for (BlockState state : block.getStateDefinition().getPossibleStates()) {
                final ModelResourceLocation mrl = BlockModelShaper.stateToModelLocation(state);
                models.computeIfPresent(mrl, (key, original) -> new ForcePropsBakedModel(original, forcedType, forcedAO));
            }
        }

    }
    *///?}

    //? if >=26.3 {
    /*private static final class ForceRenderPropertiesModel extends DelegateBlockStateModel {
        private final ChunkSectionLayer forcedLayer;
        private final boolean noAo;
        private final boolean noDirectionalShade;

        ForceRenderPropertiesModel(BlockStateModel delegate,
                                   ChunkSectionLayer forcedLayer,
                                   boolean noAo,
                                   boolean noDirectionalShade) {
            super(delegate);
            this.forcedLayer = forcedLayer;
            this.noAo = noAo;
            this.noDirectionalShade = noDirectionalShade;
        }

        @Override
        public void collectParts(BlockAndTintGetter level, BlockPos pos, BlockState state,
                                 RandomSource random, List<BlockStateModelPart> out) {
            List<BlockStateModelPart> parts = new ArrayList<>();
            delegate.collectParts(level, pos, state, random, parts);
            for (BlockStateModelPart part : parts) {
                out.add(new ForcedRenderPart(part, forcedLayer, noAo, noDirectionalShade));
            }
        }

        @Override
        public int materialFlags() {
            return adjustMaterialFlags(delegate.materialFlags(), forcedLayer);
        }

        @Override
        public int materialFlags(BlockAndTintGetter level, BlockPos pos, BlockState state) {
            return adjustMaterialFlags(delegate.materialFlags(level, pos, state), forcedLayer);
        }
    }

    private record ForcedRenderPart(BlockStateModelPart delegate,
                                    ChunkSectionLayer forcedLayer,
                                    boolean noAo,
                                    boolean noDirectionalShade) implements BlockStateModelPart {
        @Override
        public List<net.minecraft.client.resources.model.geometry.BakedQuad> getQuads(Direction face) {
            var original = delegate.getQuads(face);
            if (original.isEmpty()) return original;

            List<net.minecraft.client.resources.model.geometry.BakedQuad> result =
                    new ArrayList<>(original.size());

            for (var quad : original) {
                var info = quad.materialInfo();
                ChunkSectionLayer layer = forcedLayer != null ? forcedLayer : info.layer();
                Direction shade = noDirectionalShade ? Direction.UP : info.shadeDirectionOverride();
                boolean ao = !noAo && info.ambientOcclusion();

                if (layer == info.layer()
                        && shade == info.shadeDirectionOverride()
                        && ao == info.ambientOcclusion()) {
                    result.add(quad);
                    continue;
                }

                var modifiedInfo = new net.minecraft.client.resources.model.geometry.BakedQuad.MaterialInfo(
                        info.sprite(), layer, info.itemRenderType(),
                        info.itemGlintRenderType(), info.itemGlintSpecialRenderType(),
                        info.tintIndex(), shade, info.lightEmission(), ao
                );

                result.add(new net.minecraft.client.resources.model.geometry.BakedQuad(
                        quad.position0(), quad.position1(), quad.position2(), quad.position3(),
                        quad.packedUV0(), quad.packedUV1(), quad.packedUV2(), quad.packedUV3(),
                        quad.direction(), modifiedInfo, quad.bakedNormals(), quad.bakedColors()
                ));
            }
            return result;
        }

        @Override public boolean useAmbientOcclusion() {
            return !noAo && delegate.useAmbientOcclusion();
        }

        @Override public TriState ambientOcclusion() {
            return noAo ? TriState.FALSE : delegate.ambientOcclusion();
        }

        @Override public net.minecraft.client.resources.model.sprite.Material.Baked particleMaterial() {
            return delegate.particleMaterial();
        }

        @Override public int materialFlags() {
            return adjustMaterialFlags(delegate.materialFlags(), forcedLayer);
        }
    }

    private static int adjustMaterialFlags(int flags, ChunkSectionLayer forcedLayer) {
        if (forcedLayer == ChunkSectionLayer.TRANSLUCENT) {
            return flags | net.minecraft.client.resources.model.geometry.BakedQuad.FLAG_TRANSLUCENT;
        }
        if (forcedLayer != null) {
            return flags & ~net.minecraft.client.resources.model.geometry.BakedQuad.FLAG_TRANSLUCENT;
        }
        return flags;
    }
    *///?} else if >=26.1 <26.3 {
    /*private static final class ForceAmbientOcclusionModel extends DelegateBlockStateModel {
        ForceAmbientOcclusionModel(BlockStateModel delegate) { super(delegate); }

        @Override
        public void collectParts(BlockAndTintGetter level, BlockPos pos, BlockState state,
                                 RandomSource random, List<BlockStateModelPart> out) {
            var parts = new ArrayList<BlockStateModelPart>();
            delegate.collectParts(level, pos, state, random, parts);
            for (var part : parts) out.add(new NoAmbientOcclusionPart(part));
        }
    }

    private record NoAmbientOcclusionPart(BlockStateModelPart delegate) implements BlockStateModelPart {
        @Override public List<net.minecraft.client.resources.model.geometry.BakedQuad> getQuads(Direction face) {
            return delegate.getQuads(face);
        }
        @Override public boolean useAmbientOcclusion() { return false; }
        @Override public TriState ambientOcclusion() { return TriState.FALSE; }
        @Override public net.minecraft.client.resources.model.sprite.Material.Baked particleMaterial() {
            return delegate.particleMaterial();
        }
        @Override public int materialFlags() { return delegate.materialFlags(); }
    }
    *///?} else if >=1.21.6 <26.1 {
    private static final class ForcePropsStateModel extends DelegateBlockStateModel {
        private final Object forcedLayerOrNull;
        private final TriState forcedAO;

        ForcePropsStateModel(BlockStateModel delegate, Object forcedLayerOrNull, TriState forcedAO) {
            super(delegate);
            this.forcedLayerOrNull = forcedLayerOrNull;
            this.forcedAO = forcedAO;
        }

        @Override
        public void collectParts(
                BlockAndTintGetter level,
                BlockPos pos,
                BlockState state,
                RandomSource random,
                List<BlockModelPart> out
        ) {
            List<BlockModelPart> originalParts = new ArrayList<>();
            this.delegate.collectParts(level, pos, state, random, originalParts);

            TextureAtlasSprite particle = this.particleIcon(level, pos, state);

            for (BlockModelPart part : originalParts) {
                out.add(new ForcePropsPart(part, particle, forcedLayerOrNull, forcedAO));
            }
        }
    }

    private record ForcePropsPart(
            BlockModelPart base,
            TextureAtlasSprite particle,
            Object forcedLayerOrNull,
            TriState forcedAO
    ) implements BlockModelPart {

        @Override
        public List<net.minecraft.client.renderer.block.model.BakedQuad> getQuads(Direction face) {
            return base.getQuads(face);
        }

        @Override
        public boolean useAmbientOcclusion() {
            return base.useAmbientOcclusion();
        }

        @Override
        public TextureAtlasSprite particleIcon() {
            return particle != null ? particle : base.particleIcon();
        }

        @Override
        public TriState ambientOcclusion() {
            if (forcedAO != null && forcedAO != TriState.DEFAULT) {
                return forcedAO;
            }

            return base.ambientOcclusion();
        }

        @Override
        public ChunkSectionLayer getRenderType(BlockState state) {
            if (forcedLayerOrNull instanceof ChunkSectionLayer layer) {
                return layer;
            }

            return base.getRenderType(state);
        }

    }
    //?} else if >=1.21.5 <1.21.6 {
    /*private static final class ForcePropsStateModel extends DelegateBlockStateModel {
        private final Object forcedLayerOrNull;
        private final TriState forcedAO;

        ForcePropsStateModel(BlockStateModel delegate, Object forcedLayerOrNull, TriState forcedAO) {
            super(delegate);
            this.forcedLayerOrNull = forcedLayerOrNull;
            this.forcedAO = forcedAO;
        }

        @Override
        public void collectParts(
                BlockAndTintGetter level,
                BlockPos pos,
                BlockState state,
                RandomSource random,
                List<BlockModelPart> out
        ) {
            List<BlockModelPart> originalParts = new ArrayList<>();
            this.delegate.collectParts(level, pos, state, random, originalParts);

            TextureAtlasSprite particle = this.particleIcon(level, pos, state);

            for (BlockModelPart part : originalParts) {
                out.add(new ForcePropsPart(part, particle, forcedLayerOrNull, forcedAO));
            }
        }
    }

    private record ForcePropsPart(
            BlockModelPart base,
            TextureAtlasSprite particle,
            Object forcedLayerOrNull,
            TriState forcedAO
    ) implements BlockModelPart {

        @Override
        public List<net.minecraft.client.renderer.block.model.BakedQuad> getQuads(Direction face) {
            return base.getQuads(face);
        }

        @Override
        public boolean useAmbientOcclusion() {
            return base.useAmbientOcclusion();
        }

        @Override
        public TextureAtlasSprite particleIcon() {
            return particle != null ? particle : base.particleIcon();
        }

        @Override
        public TriState ambientOcclusion() {
            if (forcedAO != null && forcedAO != TriState.DEFAULT) {
                return forcedAO;
            }

            return base.ambientOcclusion();
        }

        @Override
        public RenderType getRenderType(BlockState state) {
            if (forcedLayerOrNull instanceof RenderType renderType) {
                return renderType;
            }

            return base.getRenderType(state);
        }

    }
    *///?} else if >=1.21.4 <1.21.5 {
    /*private static final class ForcePropsBakedModel extends DelegateBakedModel {
        private final RenderType forcedTypeOrNull;
        private final TriState forcedAO;

        ForcePropsBakedModel(BakedModel original, RenderType forcedTypeOrNull, TriState forcedAO) {
            super(original);
            this.forcedTypeOrNull = forcedTypeOrNull;
            this.forcedAO = forcedAO;
        }

        @Override
        public ChunkRenderTypeSet getRenderTypes(BlockState state, RandomSource random, ModelData data) {
            if (forcedTypeOrNull != null) {
                return ChunkRenderTypeSet.of(forcedTypeOrNull);
            }

            return super.getRenderTypes(state, random, data);
        }

        @Override
        public List<net.minecraft.client.renderer.block.model.BakedQuad> getQuads(
                BlockState state,
                Direction side,
                RandomSource random,
                ModelData data,
                RenderType renderType
        ) {
            if (forcedTypeOrNull == null) {
                return super.getQuads(state, side, random, data, renderType);
            }

            if (renderType != null && !renderType.equals(forcedTypeOrNull)) {
                return Collections.emptyList();
            }

            return super.getQuads(state, side, random, data, null);
        }

        @Override
        public TriState useAmbientOcclusion(BlockState state, ModelData data, RenderType renderType) {
            if (forcedAO != TriState.DEFAULT) {
                return forcedAO;
            }

            return super.useAmbientOcclusion(state, data, renderType);
        }
    }
    *///?} else {
    /*@SuppressWarnings("unchecked")
    private static Map<Object, BakedModel> getAllBakedModelsCompat(ModelEvent.ModifyBakingResult event) {
        try {
            java.lang.reflect.Method method = event.getClass().getMethod("getModels");
            Object result = method.invoke(event);

            if (result instanceof Map<?, ?> map) {
                return (Map<Object, BakedModel>) map;
            }
        } catch (Throwable ignored) {
        }

        try {
            java.lang.reflect.Method method = event.getClass().getMethod("getBakingResult");
            Object bakingResult = method.invoke(event);

            if (bakingResult != null) {
                try {
                    java.lang.reflect.Method innerMethod = bakingResult.getClass().getMethod("getModels");
                    Object result = innerMethod.invoke(bakingResult);

                    if (result instanceof Map<?, ?> map) {
                        return (Map<Object, BakedModel>) map;
                    }
                } catch (Throwable ignored) {
                }

                try {
                    java.lang.reflect.Method innerMethod = bakingResult.getClass().getMethod("models");
                    Object result = innerMethod.invoke(bakingResult);

                    if (result instanceof Map<?, ?> map) {
                        return (Map<Object, BakedModel>) map;
                    }
                } catch (Throwable ignored) {
                }
            }
        } catch (Throwable ignored) {
        }

        return Collections.emptyMap();
    }

    private static final class ForcePropsBakedModel extends BakedModelWrapper<BakedModel> {
        private final RenderType forcedTypeOrNull;
        private final TriState forcedAO;

        ForcePropsBakedModel(BakedModel original, RenderType forcedTypeOrNull, TriState forcedAO) {
            super(original);
            this.forcedTypeOrNull = forcedTypeOrNull;
            this.forcedAO = forcedAO;
        }

        @Override
        public ChunkRenderTypeSet getRenderTypes(BlockState state, RandomSource random, ModelData data) {
            if (forcedTypeOrNull != null) {
                return ChunkRenderTypeSet.of(forcedTypeOrNull);
            }

            return super.getRenderTypes(state, random, data);
        }

        @Override
        public List<net.minecraft.client.renderer.block.model.BakedQuad> getQuads(
                BlockState state,
                Direction side,
                RandomSource random,
                ModelData data,
                RenderType renderType
        ) {
            if (forcedTypeOrNull == null) {
                return super.getQuads(state, side, random, data, renderType);
            }

            if (renderType != null && !renderType.equals(forcedTypeOrNull)) {
                return Collections.emptyList();
            }

            return super.getQuads(state, side, random, data, null);
        }

        @Override
        public TriState useAmbientOcclusion(BlockState state, ModelData data, RenderType renderType) {
            if (forcedAO != TriState.DEFAULT) {
                return forcedAO;
            }

            return super.useAmbientOcclusion(state, data, renderType);
        }
    }
    *///?}

}


