package space.anatomyuniverse.musavacca.render;

import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.event.ModelEvent;
import space.anatomyuniverse.musavacca.block.ModBlocks;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

//? if <26.1 {
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraft.client.renderer.block.model.BakedQuad;
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
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
//?} else if >=26.1 {
/*import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.geometry.BakedQuad;
*///?}

public final class MusaRenderLayers {

    private static final Set<Block> CUTOUT_BLOCKS = Set.of(
            ModBlocks.MUSAVACCA_EGG.get(), ModBlocks.HEX_BLOCK.get(), ModBlocks.PEARL_FIRE.get(),
            ModBlocks.VOCO_TABLE.get(), ModBlocks.MUSAVACCA_SPROUT.get(), ModBlocks.MUSAVACCA_SUCKER.get(),
            ModBlocks.MUSAVACCA_PLANT.get(), ModBlocks.MUSAVACCA_PSEUDOSTEM.get(), ModBlocks.MUSAVACCA_TRAPDOOR.get(),
            ModBlocks.CAROTENE_GRASS.get(), ModBlocks.CAROTENE_SHORT_GRASS.get(), ModBlocks.CAROTENE_TALL_GRASS.get(),
            ModBlocks.BANANA_PEARL_MASCARON.get(), ModBlocks.VOCO_POST.get());

    private static final Set<Block> TRANSLUCENT_BLOCKS = Set.of(
            ModBlocks.PEARL_PORTAL.get(), ModBlocks.MUSAVACCA_DOOR.get());

    private static final Set<Block> NO_AO_BLOCKS = Set.of(
            ModBlocks.HEX_BLOCK.get(), ModBlocks.MUSAVACCA_LEAVES.get(), ModBlocks.PEARL_PORTAL.get());

    private static final Set<Block> NO_DIRECTIONAL_SHADE_BLOCKS = Set.of(ModBlocks.PEARL_PORTAL.get());

    private MusaRenderLayers() {}

    private record Properties(boolean noAo, boolean noShade) {
        boolean any() { return noAo || noShade; }
    }

    private static Properties properties(Block block) {
        return new Properties(NO_AO_BLOCKS.contains(block), NO_DIRECTIONAL_SHADE_BLOCKS.contains(block));
    }

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
            Properties p = properties(state.getBlock());
            return p.any() ? new ForcedStateModel(original, p) : original;
        });
        //?} else {
        /*replaceLegacy(event);
        *///?}
    }

    //? if <1.21.5 {
    /*private static void replaceLegacy(ModelEvent.ModifyBakingResult event) {
        //? if >=1.21.4 {
        replaceModels(event.getBakingResult().blockStateModels());
        //?} else {
        /^replaceModels(event.getModels());
        ^///?}
    }

    private static void replaceModels(java.util.Map<ModelResourceLocation, BakedModel> models) {
        for (Block block : NO_AO_BLOCKS) wrapModels(models, block);
        for (Block block : NO_DIRECTIONAL_SHADE_BLOCKS) if (!NO_AO_BLOCKS.contains(block)) wrapModels(models, block);
    }

    private static void wrapModels(java.util.Map<ModelResourceLocation, BakedModel> models, Block block) {
        Properties p = properties(block);
        for (BlockState state : block.getStateDefinition().getPossibleStates()) {
            ModelResourceLocation id = BlockModelShaper.stateToModelLocation(state);
            models.computeIfPresent(id, (key, original) -> new ForcedBakedModel(original, p));
        }
    }
    *///?}

    //? if >=1.21.5 {
    private static final class ForcedStateModel extends DelegateBlockStateModel {
        private final Properties properties;

        ForcedStateModel(BlockStateModel original, Properties properties) {
            super(original);
            this.properties = properties;
        }

        @Override public void collectParts(BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random,
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
        /*@Override public int materialFlags() { return delegate.materialFlags(); }
        @Override public int materialFlags(BlockAndTintGetter level, BlockPos pos, BlockState state) {
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
        @Override public List<BakedQuad> getQuads(Direction face) {
            //? if >=26.1 {
            /*List<BakedQuad> source = original.getQuads(face), result = null;
            for (int i = 0; i < source.size(); i++) {
                BakedQuad quad = source.get(i);
                BakedQuad.MaterialInfo material = material(quad.materialInfo(), properties);
                if (material == quad.materialInfo()) continue;
                if (result == null) result = new ArrayList<>(source);
                result.set(i, new BakedQuad(quad.position0(), quad.position1(), quad.position2(), quad.position3(),
                        quad.packedUV0(), quad.packedUV1(), quad.packedUV2(), quad.packedUV3(),
                        quad.direction(), material, quad.bakedNormals(), quad.bakedColors()));
            }
            return result == null ? source : List.copyOf(result);
            *///?} else {
            List<BakedQuad> quads = original.getQuads(face);
            return properties.noShade() ? withoutShade(quads) : quads;
            //?}
        }

        @Override public boolean useAmbientOcclusion() {
            return !properties.noAo() && original.useAmbientOcclusion();
        }

        @Override public TriState ambientOcclusion() {
            return properties.noAo() ? TriState.FALSE : original.ambientOcclusion();
        }

        //? if >=26.1 {
        /*@Override public net.minecraft.client.resources.model.sprite.Material.Baked particleMaterial() {
            return original.particleMaterial();
        }
        @Override public int materialFlags() { return original.materialFlags(); }
        *///?} else {
        @Override public TextureAtlasSprite particleIcon() { return original.particleIcon(); }
        //? if >=1.21.6 {
        @Override public ChunkSectionLayer getRenderType(BlockState state) { return original.getRenderType(state); }
        //?} else {
        /*@Override public RenderType getRenderType(BlockState state) { return original.getRenderType(state); }
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

        ForcedBakedModel(BakedModel original, Properties properties) {
            super(original);
            this.properties = properties;
        }

        @Override public List<BakedQuad> getQuads(BlockState state, Direction face, RandomSource random, ModelData data, RenderType type) {
            List<BakedQuad> quads = super.getQuads(state, face, random, data, type);
            return properties.noShade() ? withoutShade(quads) : quads;
        }

        @Override public TriState useAmbientOcclusion(BlockState state, ModelData data, RenderType type) {
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
        return new BakedQuad.MaterialInfo(info.sprite(), info.layer(), info.itemRenderType(),
                //? if >=26.3 {
                /^info.itemGlintRenderType(), info.itemGlintSpecialRenderType(),
                ^///?}
                info.tintIndex(), shade, info.lightEmission(), ao);
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
    private static boolean shaded(BakedQuad quad) { return quad.shade(); }
    //?} else if <1.21.5 {
    /*private static boolean shaded(BakedQuad quad) { return quad.isShade(); }
    *///?}

    //? if <26.1 {
    private static BakedQuad copyWithoutShade(BakedQuad quad) {
        //? if >=1.21.11 {
        /*return new BakedQuad(quad.position0(), quad.position1(), quad.position2(), quad.position3(),
                quad.packedUV0(), quad.packedUV1(), quad.packedUV2(), quad.packedUV3(),
                quad.tintIndex(), quad.direction(), quad.sprite(), false, quad.lightEmission(),
                quad.bakedNormals(), quad.bakedColors(), quad.hasAmbientOcclusion());
        *///?} else if >=1.21.5 {
        return new BakedQuad(quad.vertices(), quad.tintIndex(), quad.direction(), quad.sprite(), false,
                quad.lightEmission(), quad.hasAmbientOcclusion());
        //?} else if >=1.21.3 {
        /*return new BakedQuad(quad.getVertices(), quad.getTintIndex(), quad.getDirection(), quad.getSprite(), false,
                quad.getLightEmission(), quad.hasAmbientOcclusion());
        *///?} else {
        /*return new BakedQuad(quad.getVertices(), quad.getTintIndex(), quad.getDirection(), quad.getSprite(), false,
                quad.hasAmbientOcclusion());
        *///?}
    }
    //?}

}
