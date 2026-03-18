
package space.anatomyuniverse.musavacca.data.models.block;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

//? if <1.21.4 {
/*import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
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

public final class Chapiter {
    private Chapiter() {}

    //? if <1.21.4 {
    /*public static void generate(BlockStateProvider gen, Block... blocks) {
        if (blocks == null) return;

        for (Block b : blocks) {
            if (b == null) continue;

            // side = block/<id>.png
            ResourceLocation side = ModelUtil.blockTex(b);
            // bottom = block/<id>_bottom.png
            ResourceLocation bottom = ResourceLocation.fromNamespaceAndPath(
                    side.getNamespace(), side.getPath() + "_bottom"
            );
            // top = block/<id>_top.png
            ResourceLocation top = ResourceLocation.fromNamespaceAndPath(
                    side.getNamespace(), side.getPath() + "_top"
            );

            BlockModelBuilder model = gen.models().cubeBottomTop(
                    ModelUtil.pathOf(b),
                    side,
                    bottom,
                    top
            );

            // Handles all 6 facings (up/down/north/south/west/east)
            gen.directionalBlock(b, model);
            gen.simpleBlockItem(b, model);
        }
    }
    *///?} else {
    public static void generate(BlockModelGenerators gen, Block... blocks) {
        if (blocks == null) return;

        for (Block b : blocks) {
            if (b == null) continue;

            // side = block/<id>.png, top = block/<id>_top.png, bottom = block/<id>_bottom.png
            TextureMapping mapping = new TextureMapping()
                    .put(TextureSlot.PARTICLE, TextureMapping.getBlockTexture(b))
                    .put(TextureSlot.SIDE, TextureMapping.getBlockTexture(b))
                    .put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture(b, "_bottom"))
                    .put(TextureSlot.TOP, TextureMapping.getBlockTexture(b, "_top"));

            ResourceLocation model = ModelTemplates.CUBE_BOTTOM_TOP.create(b, mapping, gen.modelOutput);

            //? if <1.21.5 {
            /*gen.blockStateOutput.accept(
                    MultiVariantGenerator.multiVariant(b).with(
                            PropertyDispatch.property(BlockStateProperties.FACING)
                                    .select(Direction.UP,    variant(model,   0,   0))
                                    .select(Direction.DOWN,  variant(model, 180,   0))
                                    .select(Direction.NORTH, variant(model,  90,   0))
                                    .select(Direction.SOUTH, variant(model,  90, 180))
                                    .select(Direction.WEST,  variant(model,  90, 270))
                                    .select(Direction.EAST,  variant(model,  90,  90))
                    )
            );
            *///?} else {
            gen.blockStateOutput.accept(
                    MultiVariantGenerator.dispatch(
                            b,
                            BlockModelGenerators.plainVariant(model)
                    ).with(
                            PropertyDispatch.modify(BlockStateProperties.FACING)
                                    .select(Direction.UP, BlockModelGenerators.NOP)
                                    .select(Direction.DOWN, BlockModelGenerators.X_ROT_180)
                                    .select(Direction.NORTH, BlockModelGenerators.X_ROT_90)
                                    .select(Direction.SOUTH, BlockModelGenerators.X_ROT_90.then(BlockModelGenerators.Y_ROT_180))
                                    .select(Direction.WEST, BlockModelGenerators.X_ROT_90.then(BlockModelGenerators.Y_ROT_270))
                                    .select(Direction.EAST, BlockModelGenerators.X_ROT_90.then(BlockModelGenerators.Y_ROT_90))
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
