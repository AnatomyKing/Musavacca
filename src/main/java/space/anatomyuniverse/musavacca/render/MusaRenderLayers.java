package space.anatomyuniverse.musavacca.render;

import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.event.ModelEvent;
import space.anatomyuniverse.musavacca.block.ModBlocks;
import space.anatomyuniverse.musavacca.block.entity.renderer.LayeredBlockTint;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

//? if <26.1 {
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
//?}
//? if <1.21.6 {
/*import net.minecraft.client.renderer.RenderType;
*///?}
//? if >=1.21.6 {
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
//?}
//? if <1.21.5 {
/*import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.common.util.TriState;

import java.util.Map;

*///?}
//? if <1.21.4 {
/*import net.neoforged.neoforge.client.model.BakedModelWrapper;
*///?} else if <1.21.5 {
/*import net.minecraft.client.resources.model.DelegateBakedModel;
*///?}
//? if >=1.21.5 {
import net.minecraft.core.BlockPos;
import net.minecraft.util.TriState;
import net.neoforged.neoforge.client.model.DelegateBlockStateModel;
//?}
//? if >=1.21.5 <26.1 {
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.level.BlockAndTintGetter;
//?} else if >=26.1 {
/*import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
*///?}

public final class MusaRenderLayers {

    //? if <26.1 {
    private static final Set<Block> CUTOUT_BLOCKS =
            Set.of(
                    ModBlocks.MUSAVACCA_EGG.get(),
                    ModBlocks.HEX_BLOCK.get(),
                    ModBlocks.PEARL_FIRE.get(),
                    ModBlocks.BANANA_PEARL_DUST.get(),
                    ModBlocks.VOCO_TABLE.get(),
                    ModBlocks.MUSAVACCA_SPROUT.get(),
                    ModBlocks.MUSAVACCA_SUCKER.get(),
                    ModBlocks.MUSAVACCA_PLANT.get(),
                    ModBlocks.MUSAVACCA_PSEUDOSTEM.get(),
                    ModBlocks.MUSAVACCA_TRAPDOOR.get(),
                    ModBlocks.CAROTENE_GRASS.get(),
                    ModBlocks.CAROTENE_SHORT_GRASS.get(),
                    ModBlocks.CAROTENE_TALL_GRASS.get(),
                    ModBlocks.BANANA_PEARL_MASCARON.get(),
                    ModBlocks.VOCO_POST.get());

    private static final Set<Block> TRANSLUCENT_BLOCKS = Set.of(ModBlocks.PEARL_PORTAL.get(), ModBlocks.MUSAVACCA_DOOR.get());
    //?}

    private static final Set<Block> NO_AO_BLOCKS =
            Set.of(ModBlocks.HEX_BLOCK.get(), ModBlocks.MUSAVACCA_LEAVES.get(), ModBlocks.PEARL_PORTAL.get());

    private static final Set<Block> NO_DIRECTIONAL_SHADE_BLOCKS = Set.of(ModBlocks.PEARL_PORTAL.get());

    private MusaRenderLayers() {}

    private enum PortalFaces { ALL, BASE, OVERLAY }

    private record Properties(boolean noAo, boolean noShade, PortalFaces portalFaces) {
        boolean any() {
            return noAo || noShade || portalFaces != PortalFaces.ALL;
        }

        Properties overlay() {
            return new Properties(noAo, noShade, PortalFaces.OVERLAY);
        }
    }

    private static Properties properties(BlockState state) {
        Block block = state.getBlock();
        return new Properties(NO_AO_BLOCKS.contains(block), NO_DIRECTIONAL_SHADE_BLOCKS.contains(block),
                LayeredBlockTint.hasPortalOverlay(state) ? PortalFaces.BASE : PortalFaces.ALL);
    }

    private static List<BakedQuad> filterPortalFaces(List<BakedQuad> source, Properties properties) {
        if (properties.portalFaces() == PortalFaces.ALL) return source;
        boolean overlay = properties.portalFaces() == PortalFaces.OVERLAY;
        List<BakedQuad> result = null;
        for (int i = 0; i < source.size(); i++) {
            BakedQuad quad = source.get(i);
            if (LayeredBlockTint.isPortalQuad(quad) != overlay) {
                if (result == null) result = new ArrayList<>(source.subList(0, i));
            } else if (result != null) result.add(quad);
        }
        return result == null ? source : result;
    }

    //? if >=1.21.5 {
    public static BlockStateModel portalModel(BlockStateModel model) {
        return model instanceof ForcedStateModel wrapped ? wrapped.portalModel : null;
    }
    //?} else {
    /*public static BakedModel portalModel(BakedModel model) {
        return model instanceof ForcedBakedModel wrapped ? wrapped.portalModel : null;
    }
    *///?}

    //? if <26.1 {
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            for (Block block : CUTOUT_BLOCKS) setLayer(block, true);
            for (Block block : TRANSLUCENT_BLOCKS) setLayer(block, false);
        });
    }

    //?}
    //? if <26.1 {
    private static void setLayer(Block block, boolean cutout) {
        //? if >=1.21.6 {
        ItemBlockRenderTypes.setRenderLayer(block, cutout ? ChunkSectionLayer.CUTOUT : ChunkSectionLayer.TRANSLUCENT);
        //?} else {
        /*ItemBlockRenderTypes.setRenderLayer(block, cutout ? RenderType.cutout() : RenderType.translucent());
        *///?}
    }
    //?}

    public static void onModifyBakingResult(ModelEvent.ModifyBakingResult event) {
        //? if >=1.21.5 {
        var models = event.getBakingResult().blockStateModels();
        models.replaceAll((state, original) -> {
            Properties p = properties(state);
            return p.any() ? new ForcedStateModel(original, p) : original;
        });
        //?} else if >=1.21.4 {
        /*replaceModels(event.getBakingResult().blockStateModels());
        *///?} else {
        /*replaceModels(event.getModels());
        *///?}
    }

    //? if <1.21.5 {
    /*private static void replaceModels(Map<ModelResourceLocation, BakedModel> models) {
        for (Block block : NO_AO_BLOCKS) wrapModels(models, block);
        for (Block block : NO_DIRECTIONAL_SHADE_BLOCKS) if (!NO_AO_BLOCKS.contains(block)) wrapModels(models, block);
        wrapModels(models, ModBlocks.VOCO_POST.get());
        wrapModels(models, ModBlocks.VOCO_TABLE.get());
        wrapModels(models, ModBlocks.MUSAVACCA_DOOR.get());
        wrapModels(models, ModBlocks.MUSAVACCA_TRAPDOOR.get());
    }

    private static void wrapModels(Map<ModelResourceLocation, BakedModel> models, Block block) {
        for (BlockState state : block.getStateDefinition().getPossibleStates()) {
            Properties p = properties(state);
            if (!p.any()) continue;
            ModelResourceLocation id = BlockModelShaper.stateToModelLocation(state);
            models.computeIfPresent(id, (key, original) -> new ForcedBakedModel(original, p));
        }
    }
    *///?}

    //? if >=1.21.5 {
    private static final class ForcedStateModel extends DelegateBlockStateModel {
        private final Properties properties;
        private final BlockStateModel portalModel;

        ForcedStateModel(BlockStateModel original, Properties properties) {
            super(original);
            this.properties = properties;
            this.portalModel = properties.portalFaces() == PortalFaces.BASE
                    ? new ForcedStateModel(original, properties.overlay()) : null;
        }

        @Override
        public void collectParts(RandomSource random,
                //? if >=26.1 {
                /*List<BlockStateModelPart> output) {
                *///?} else {
                List<BlockModelPart> output) {
                //?}
            int start = output.size();
            delegate.collectParts(random, output);
            if (properties.portalFaces() != PortalFaces.ALL) {
                for (int i = start; i < output.size(); i++) output.set(i, new ForcedPart(output.get(i), properties));
            }
        }

        @Override
        public void collectParts(
                BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random,
                //? if >=26.1 {
                /*List<BlockStateModelPart> output) {
                *///?} else {
                List<BlockModelPart> output) {
                //?}
            //? if >=26.1 {
            /*List<BlockStateModelPart> parts = new ArrayList<>();
            *///?} else {
            List<BlockModelPart> parts = new ArrayList<>();
            //?}
            delegate.collectParts(level, pos, state, random, parts);
            for (var part : parts) output.add(new ForcedPart(part, properties));
        }

        //? if >=26.1 {
        /*@Override
        public int materialFlags() {
            return delegate.materialFlags();
        }

        @Override
        public int materialFlags(BlockAndTintGetter level, BlockPos pos, BlockState state) {
            return delegate.materialFlags(level, pos, state);
        }
        *///?}
    }
    //?}

    //? if >=26.1 {
    /*private record ForcedPart(BlockStateModelPart original, Properties properties) implements BlockStateModelPart {
    *///?} else if >=1.21.5 {
    private record ForcedPart(BlockModelPart original, Properties properties) implements BlockModelPart {
    //?}
        //? if >=1.21.5 {
        @Override
        public List<BakedQuad> getQuads(Direction face) {
            //? if >=26.1 {
            /*List<BakedQuad> source = filterPortalFaces(original.getQuads(face), properties), result = null;
            for (int i = 0; i < source.size(); i++) {
                BakedQuad quad = source.get(i);
                BakedQuad.MaterialInfo material = material(quad.materialInfo(), properties);
                if (material == quad.materialInfo()) continue;
                if (result == null) result = new ArrayList<>(source);
                result.set(
                        i,
                        new BakedQuad(
                                quad.position0(),
                                quad.position1(),
                                quad.position2(),
                                quad.position3(),
                                quad.packedUV0(),
                                quad.packedUV1(),
                                quad.packedUV2(),
                                quad.packedUV3(),
                                quad.direction(),
                                material,
                                quad.bakedNormals(),
                                quad.bakedColors()));
            }
            return result == null ? source : List.copyOf(result);
            *///?} else {
            List<BakedQuad> quads = filterPortalFaces(original.getQuads(face), properties);
            return properties.noShade() ? withoutShade(quads) : quads;
            //?}
        }

        @Override
        public boolean useAmbientOcclusion() {
            return !properties.noAo() && original.useAmbientOcclusion();
        }

        @Override
        public TriState ambientOcclusion() {
            return properties.noAo() ? TriState.FALSE : original.ambientOcclusion();
        }

        //? if >=26.1 {
        /*@Override
        public Material.Baked particleMaterial() {
            return original.particleMaterial();
        }

        @Override
        public int materialFlags() {
            return original.materialFlags();
        }
        *///?} else {
        @Override
        public TextureAtlasSprite particleIcon() {
            return original.particleIcon();
        }

        //? if >=1.21.6 {
        @Override
        public ChunkSectionLayer getRenderType(BlockState state) {
            return original.getRenderType(state);
        }
        //?} else {
        /*@Override
        public RenderType getRenderType(BlockState state) {
            return original.getRenderType(state);
        }
        *///?}
        //?}
    }
        //?}

    //? if >=1.21.4 <1.21.5 {
    /*private static final class ForcedBakedModel extends DelegateBakedModel {
    *///?} else if <1.21.4 {
    /*private static final class ForcedBakedModel extends BakedModelWrapper<BakedModel> {
    *///?}
        //? if <1.21.5 {
        /*private final Properties properties;
        private final BakedModel portalModel;

        ForcedBakedModel(BakedModel original, Properties properties) {
            super(original);
            this.properties = properties;
            this.portalModel = properties.portalFaces() == PortalFaces.BASE
                    ? new ForcedBakedModel(original, properties.overlay()) : null;
        }

        @Override
        public List<BakedQuad> getQuads(BlockState state, Direction face, RandomSource random) {
            return filterPortalFaces(super.getQuads(state, face, random), properties);
        }

        @Override
        public List<BakedQuad> getQuads(BlockState state, Direction face, RandomSource random, ModelData data, RenderType type) {
            List<BakedQuad> quads = filterPortalFaces(super.getQuads(state, face, random, data, type), properties);
            return properties.noShade() ? withoutShade(quads) : quads;
        }

        @Override
        public TriState useAmbientOcclusion(BlockState state, ModelData data, RenderType type) {
            return properties.noAo() ? TriState.FALSE : super.useAmbientOcclusion(state, data, type);
        }
    }
        *///?}

    //? if >=26.1 {
    /*private static BakedQuad.MaterialInfo material(BakedQuad.MaterialInfo info, Properties p) {
        boolean ao = !p.noAo() && info.ambientOcclusion();
        //? if >=26.3 {
        /^var shade = p.noShade() ? Direction.UP : info.shadeDirectionOverride();
        if (ao == info.ambientOcclusion() && shade == info.shadeDirectionOverride()) return info;
        ^///?} else {
        var shade = !p.noShade() && info.shade();
        if (ao == info.ambientOcclusion() && shade == info.shade()) return info;
        //?}
        return new BakedQuad.MaterialInfo(
                info.sprite(),
                info.layer(),
                info.itemRenderType(),
                //? if >=26.3 {
                /^info.itemGlintRenderType(),
                info.itemGlintSpecialRenderType(),
                ^///?}
                info.tintIndex(),
                shade,
                info.lightEmission(),
                ao);
    }
    *///?}

    //? if <26.1 {
    private static List<BakedQuad> withoutShade(List<BakedQuad> source) {
        List<BakedQuad> result = null;
        for (int i = 0; i < source.size(); i++) {
            BakedQuad quad = source.get(i);
            if (!shaded(quad)) continue;
            if (result == null) result = new ArrayList<>(source);
            result.set(i, copyWithoutShade(quad));
        }
        return result == null ? source : List.copyOf(result);
    }

    //?}
    //? if >=1.21.5 <26.1 {
    private static boolean shaded(BakedQuad quad) {
        return quad.shade();
    }
    //?} else if <1.21.5 {
    /*private static boolean shaded(BakedQuad quad) {
        return quad.isShade();
    }
    *///?}

    //? if <26.1 {
    private static BakedQuad copyWithoutShade(BakedQuad quad) {
        //? if >=1.21.11 {
        /*return new BakedQuad(
                quad.position0(),
                quad.position1(),
                quad.position2(),
                quad.position3(),
                quad.packedUV0(),
                quad.packedUV1(),
                quad.packedUV2(),
                quad.packedUV3(),
                quad.tintIndex(),
                quad.direction(),
                quad.sprite(),
                false,
                quad.lightEmission(),
                quad.bakedNormals(),
                quad.bakedColors(),
                quad.hasAmbientOcclusion());
        *///?} else if >=1.21.5 {
        return new BakedQuad(
                quad.vertices(),
                quad.tintIndex(),
                quad.direction(),
                quad.sprite(),
                false,
                quad.lightEmission(),
                quad.hasAmbientOcclusion());
        //?} else if >=1.21.3 {
        /*return new BakedQuad(
                quad.getVertices(),
                quad.getTintIndex(),
                quad.getDirection(),
                quad.getSprite(),
                false,
                quad.getLightEmission(),
                quad.hasAmbientOcclusion());
        *///?} else {
        /*return new BakedQuad(
                quad.getVertices(), quad.getTintIndex(), quad.getDirection(), quad.getSprite(), false, quad.hasAmbientOcclusion());
        *///?}
    }
    //?}

}
