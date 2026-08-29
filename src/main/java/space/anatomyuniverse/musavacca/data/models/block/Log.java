
package space.anatomyuniverse.musavacca.data.models.block;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

//? if <1.21.4 {
/*import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import space.anatomyuniverse.musavacca.data.models.ModelUtil;
*///?} else {
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.resources.ResourceLocation;

//? if <1.21.5 {
/*import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.blockstates.Variant;
import net.minecraft.client.data.models.blockstates.VariantProperties;
*///?} else {
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
//?}
//?}

public final class Log {
    private Log() {}

    //? if <1.21.4 {
    /*public static void generate(BlockStateProvider gen, Block... blocks) {
        if (blocks == null) return;

        for (Block b : blocks) {
            if (!(b instanceof RotatedPillarBlock log)) continue;

            // Generates blockstate + models for x/y/z axis using:
            // side: block/<id>.png, end: block/<id>_top.png
            gen.logBlock(log);

            // Block item model -> block/<id>
            gen.simpleBlockItem(log, gen.models().getExistingFile(
                    ResourceLocation.fromNamespaceAndPath(
                            ModelUtil.idOf(log).getNamespace(),
                            "block/" + ModelUtil.pathOf(log)
                    )
            ));
        }
    }
    *///?} else {
    /**
     * 1.21.4+ log helper.
     *
     * Texture naming:
     * - side  : assets/<modid>/textures/block/<id>.png
     * - end   : assets/<modid>/textures/block/<id>_top.png
     */
    public static void generate(BlockModelGenerators gen, Block... blocks) {
        if (blocks == null) return;

        for (Block b : blocks) {
            if (b == null) continue;

            TextureMapping mapping = new TextureMapping()
                    .put(TextureSlot.PARTICLE, TextureMapping.getBlockTexture(b))
                    .put(TextureSlot.SIDE, TextureMapping.getBlockTexture(b))
                    .put(TextureSlot.END, TextureMapping.getBlockTexture(b, "_top"));

            ResourceLocation model = ModelTemplates.CUBE_COLUMN.create(b, mapping, gen.modelOutput);

            //? if <1.21.5 {
            /*gen.blockStateOutput.accept(
                    MultiVariantGenerator.multiVariant(b).with(
                            PropertyDispatch.property(BlockStateProperties.AXIS)
                                    .select(Direction.Axis.Y, variant(model,  0,  0))
                                    .select(Direction.Axis.Z, variant(model, 90,  0))
                                    .select(Direction.Axis.X, variant(model, 90, 90))
                    )
            );
            *///?} else {
            gen.blockStateOutput.accept(
                    MultiVariantGenerator.dispatch(
                            b,
                            BlockModelGenerators.plainVariant(model)
                    ).with(
                            PropertyDispatch.modify(BlockStateProperties.AXIS)
                                    .select(Direction.Axis.Y, BlockModelGenerators.NOP)
                                    .select(Direction.Axis.Z, BlockModelGenerators.X_ROT_90)
                                    .select(Direction.Axis.X, BlockModelGenerators.X_ROT_90.then(BlockModelGenerators.Y_ROT_90))
                    )
            );
            //?}

            gen.registerSimpleItemModel(b, model);
        }
    }

    //? if <1.21.5 {
    /*private static Variant variant(ResourceLocation modelId, int x, int y) {
        Variant v = Variant.variant().with(VariantProperties.MODEL, modelId);
        if (x != 0) v = v.with(VariantProperties.X_ROT, rot(x));
        if (y != 0) v = v.with(VariantProperties.Y_ROT, rot(y));
        return v;
    }

    private static VariantProperties.Rotation rot(int deg) {
        return switch (Math.floorMod(deg, 360)) {
            case 0 -> VariantProperties.Rotation.R0;
            case 90 -> VariantProperties.Rotation.R90;
            case 180 -> VariantProperties.Rotation.R180;
            case 270 -> VariantProperties.Rotation.R270;
            default -> throw new IllegalArgumentException("Unsupported rotation: " + deg);
        };
    }
    *///?}
    //?}
}


