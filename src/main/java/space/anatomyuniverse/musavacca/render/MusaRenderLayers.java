package space.anatomyuniverse.musavacca.render;

import com.google.common.collect.Sets;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.event.ModelEvent;
import space.anatomyuniverse.musavacca.block.ModBlocks;

import java.util.*;

// ============================================================================
// Version-specific imports (MUST stay in the import section)
// ============================================================================

//? if <1.21.5 {
/*import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.common.util.TriState;

//? if <1.21.4 {
/^import net.neoforged.neoforge.client.model.BakedModelWrapper;
 ^///?} else {
import net.minecraft.client.resources.model.DelegateBakedModel;
//?}
*///?} else {
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.util.TriState;
import net.minecraft.world.level.BlockAndTintGetter;
import net.neoforged.neoforge.client.model.DelegateBlockStateModel;

//? if >=1.21.8 {
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
//?}
//?}
// ============================================================================

public final class MusaRenderLayers {

    private static final Set<Block> CUTOUT_BLOCKS = Sets.newHashSet(
            ModBlocks.MUSAVACCA_EGG.get(),
            ModBlocks.HEX_BLOCK.get(),
            ModBlocks.PEARL_FIRE.get(),
            ModBlocks.VOCO_TABLE.get(),
            ModBlocks.VOCO_POST.get()
    );

    private static final Set<Block> TRANSLUCENT_BLOCKS = Sets.newHashSet(
            ModBlocks.PEARL_PORTAL.get()
//            ModBlocks.VOCO_RECEPTOR.get()

    );

    private static final Set<Block> NO_AO_BLOCKS = Sets.newHashSet(
            ModBlocks.HEX_BLOCK.get()
    );

    private MusaRenderLayers() {
    }

    public static void onModifyBakingResult(ModelEvent.ModifyBakingResult event) {
        if (CUTOUT_BLOCKS.isEmpty() && TRANSLUCENT_BLOCKS.isEmpty() && NO_AO_BLOCKS.isEmpty()) return;

        //? if <1.21.5 {
        /*onModifyBakingResultOldPipeline(event);
        *///?} else {
        onModifyBakingResultNewPipeline(event);
         //?}
    }

    // =========================================================================
    // 1.21.1–1.21.4 : old baked-model pipeline
    // =========================================================================
    //? if <1.21.5 {

    /*private static void onModifyBakingResultOldPipeline(ModelEvent.ModifyBakingResult event) {
        final Set<Block> touched = new HashSet<>();
        touched.addAll(CUTOUT_BLOCKS);
        touched.addAll(TRANSLUCENT_BLOCKS);
        touched.addAll(NO_AO_BLOCKS);

        //? if <1.21.4 {
        /^Map<Object, BakedModel> models = getAllBakedModelsCompat(event);
        if (models == null || models.isEmpty()) return;

        for (Block block : touched) {
            final RenderType forcedType =
                    CUTOUT_BLOCKS.contains(block) ? RenderType.cutout() :
                    TRANSLUCENT_BLOCKS.contains(block) ? RenderType.translucent() :
                    null;

            final TriState forcedAO = NO_AO_BLOCKS.contains(block) ? TriState.FALSE : TriState.DEFAULT;
            if (forcedType == null && forcedAO == TriState.DEFAULT) continue;

            for (BlockState state : block.getStateDefinition().getPossibleStates()) {
                final ModelResourceLocation mrl = BlockModelShaper.stateToModelLocation(state);
                models.computeIfPresent(mrl, (k, original) -> new ForcePropsBakedModel(original, forcedType, forcedAO));
            }
        }
        ^///?} else {
        Map<ModelResourceLocation, BakedModel> models = event.getBakingResult().blockStateModels();
        if (models == null || models.isEmpty()) return;

        for (Block block : touched) {
            final RenderType forcedType =
                    CUTOUT_BLOCKS.contains(block) ? RenderType.cutout() :
                            TRANSLUCENT_BLOCKS.contains(block) ? RenderType.translucent() :
                                    null;

            final TriState forcedAO = NO_AO_BLOCKS.contains(block) ? TriState.FALSE : TriState.DEFAULT;
            if (forcedType == null && forcedAO == TriState.DEFAULT) continue;

            for (BlockState state : block.getStateDefinition().getPossibleStates()) {
                final ModelResourceLocation mrl = BlockModelShaper.stateToModelLocation(state);
                models.computeIfPresent(mrl, (k, original) -> new ForcePropsBakedModel(original, forcedType, forcedAO));
            }
        }
        //?}
    }

    //? if <1.21.4 {
    /^@SuppressWarnings("unchecked")
    private static Map<Object, BakedModel> getAllBakedModelsCompat(ModelEvent.ModifyBakingResult event) {
        try {
            Method m = event.getClass().getMethod("getModels");
            Object r = m.invoke(event);
            if (r instanceof Map<?, ?> map) return (Map<Object, BakedModel>) map;
        } catch (Throwable ignored) {
        }

        try {
            Method m = event.getClass().getMethod("getBakingResult");
            Object bakingResult = m.invoke(event);
            if (bakingResult != null) {
                try {
                    Method m2 = bakingResult.getClass().getMethod("getModels");
                    Object r2 = m2.invoke(bakingResult);
                    if (r2 instanceof Map<?, ?> map) return (Map<Object, BakedModel>) map;
                } catch (Throwable ignored) {
                }

                try {
                    Method m3 = bakingResult.getClass().getMethod("models");
                    Object r3 = m3.invoke(bakingResult);
                    if (r3 instanceof Map<?, ?> map) return (Map<Object, BakedModel>) map;
                } catch (Throwable ignored) {
                }
            }
        } catch (Throwable ignored) {
        }

        return Collections.emptyMap();
    }
    ^///?}

    //? if <1.21.4 {
    /^private static final class ForcePropsBakedModel extends BakedModelWrapper<BakedModel> {
        private final RenderType forcedTypeOrNull;
        private final TriState forcedAO;

        ForcePropsBakedModel(BakedModel original, RenderType forcedTypeOrNull, TriState forcedAO) {
            super(original);
            this.forcedTypeOrNull = forcedTypeOrNull;
            this.forcedAO = forcedAO;
        }

        @Override
        public ChunkRenderTypeSet getRenderTypes(BlockState state, RandomSource rand, ModelData data) {
            if (forcedTypeOrNull != null) return ChunkRenderTypeSet.of(forcedTypeOrNull);
            return super.getRenderTypes(state, rand, data);
        }

        @Override
        public List<net.minecraft.client.renderer.block.model.BakedQuad> getQuads(
                BlockState state,
                Direction side,
                RandomSource rand,
                ModelData data,
                RenderType renderType
        ) {
            if (forcedTypeOrNull == null) {
                return super.getQuads(state, side, rand, data, renderType);
            }

            if (renderType != null && !renderType.equals(forcedTypeOrNull)) {
                return Collections.emptyList();
            }

            return super.getQuads(state, side, rand, data, null);
        }

        @Override
        public TriState useAmbientOcclusion(BlockState state, ModelData data, RenderType renderType) {
            if (forcedAO != TriState.DEFAULT) return forcedAO;
            return super.useAmbientOcclusion(state, data, renderType);
        }
    }}
    ^///?} else {
    private static final class ForcePropsBakedModel extends DelegateBakedModel {
        private final RenderType forcedTypeOrNull;
        private final TriState forcedAO;

        ForcePropsBakedModel(BakedModel original, RenderType forcedTypeOrNull, TriState forcedAO) {
            super(original);
            this.forcedTypeOrNull = forcedTypeOrNull;
            this.forcedAO = forcedAO;
        }

        @Override
        public ChunkRenderTypeSet getRenderTypes(BlockState state, RandomSource rand, ModelData data) {
            if (forcedTypeOrNull != null) return ChunkRenderTypeSet.of(forcedTypeOrNull);
            return super.getRenderTypes(state, rand, data);
        }

        @Override
        public List<net.minecraft.client.renderer.block.model.BakedQuad> getQuads(
                BlockState state,
                Direction side,
                RandomSource rand,
                ModelData data,
                RenderType renderType
        ) {
            if (forcedTypeOrNull == null) {
                return super.getQuads(state, side, rand, data, renderType);
            }

            if (renderType != null && !renderType.equals(forcedTypeOrNull)) {
                return Collections.emptyList();
            }

            return super.getQuads(state, side, rand, data, null);
        }

        @Override
        public TriState useAmbientOcclusion(BlockState state, ModelData data, RenderType renderType) {
            if (forcedAO != TriState.DEFAULT) return forcedAO;
            return super.useAmbientOcclusion(state, data, renderType);
        }
    }}
//?}

*///?} <1.21.5

// =========================================================================
// 1.21.5+ : BlockStateModel pipeline
// =========================================================================
//? if >=1.21.5 {

    private static void onModifyBakingResultNewPipeline(ModelEvent.ModifyBakingResult event) {
        Map<BlockState, BlockStateModel> models = event.getBakingResult().blockStateModels();
        if (models.isEmpty()) return;

        for (Map.Entry<BlockState, BlockStateModel> e : models.entrySet()) {
            BlockState state = e.getKey();
            BlockStateModel original = e.getValue();
            Block b = state.getBlock();

            //? if >=1.21.8 {
            ChunkSectionLayer forcedLayer = null;
            if (CUTOUT_BLOCKS.contains(b)) {
                forcedLayer = ChunkSectionLayer.CUTOUT;
            } else if (TRANSLUCENT_BLOCKS.contains(b)) {
                forcedLayer = ChunkSectionLayer.TRANSLUCENT;
            }
            //?} else {
            /*RenderType forcedLayer = null;
            if (CUTOUT_BLOCKS.contains(b)) {
                forcedLayer = RenderType.cutout();
            } else if (TRANSLUCENT_BLOCKS.contains(b)) {
                forcedLayer = RenderType.translucent();
            }
            *///?}

            TriState forcedAO = NO_AO_BLOCKS.contains(b) ? TriState.FALSE : TriState.DEFAULT;

            if (forcedLayer != null || forcedAO != TriState.DEFAULT) {
                e.setValue(new ForcePropsStateModel(original, forcedLayer, forcedAO));
            }
        }
    }

    private static final class ForcePropsStateModel extends DelegateBlockStateModel {
        private final Object forcedLayerOrNull;
        private final TriState forcedAO;

        ForcePropsStateModel(BlockStateModel delegate, Object forcedLayerOrNull, TriState forcedAO) {
            super(delegate);
            this.forcedLayerOrNull = forcedLayerOrNull;
            this.forcedAO = forcedAO;
        }

        @Override
        public void collectParts(BlockAndTintGetter level, BlockPos pos, BlockState state,
                                 RandomSource random, List<BlockModelPart> out) {
            List<BlockModelPart> original = new ArrayList<>();
            this.delegate.collectParts(level, pos, state, random, original);

            TextureAtlasSprite particle = this.particleIcon(level, pos, state);

            for (BlockModelPart part : original) {
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
            return (particle != null) ? particle : base.particleIcon();
        }

        @Override
        public TriState ambientOcclusion() {
            if (forcedAO != null && forcedAO != TriState.DEFAULT) return forcedAO;
            return base.ambientOcclusion();
        }

        //? if >=1.21.8 {
        @Override
        public ChunkSectionLayer getRenderType(BlockState state) {
            if (forcedLayerOrNull instanceof ChunkSectionLayer layer) return layer;
            return base.getRenderType(state);
        }
        //?} else {
        /*@Override
        public RenderType getRenderType(BlockState state) {
            if (forcedLayerOrNull instanceof RenderType rt) return rt;
            return base.getRenderType(state);
        }
        *///?}
    }
}
    //?} >=1.21.5
//}