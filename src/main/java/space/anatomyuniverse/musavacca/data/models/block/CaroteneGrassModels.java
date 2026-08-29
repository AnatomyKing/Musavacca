package space.anatomyuniverse.musavacca.data.models.block;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.SnowyDirtBlock;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

//? if <1.21.4 {
/*import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
*///?} else {
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;

//? if <1.21.5 {
/*import net.minecraft.client.data.models.blockstates.Variant;
import net.minecraft.client.data.models.blockstates.VariantProperties;
*///?} else {
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.renderer.block.model.Variant;
import net.minecraft.util.random.WeightedList;
//?}
//?}

public final class CaroteneGrassModels {
    private CaroteneGrassModels() {}

    public record Entry(Block grass, Block shortGrass, Block tallGrass) {}

    //? if <1.21.4 {
    /*public static void generate(BlockStateProvider gen, Entry entry) {
        if (entry == null) return;

        String grassName = name(entry.grass());
        String shortName = name(entry.shortGrass());
        String tallName = name(entry.tallGrass());

        ResourceLocation dirt = texture(Blocks.DIRT, "");
        ResourceLocation grassTop = texture(entry.grass(), "_top");
        ResourceLocation grassSide = texture(entry.grass(), "_side");
        ResourceLocation snowySide = texture(entry.grass(), "_side_snowy");
        ResourceLocation shortTexture = texture(entry.shortGrass(), "");
        ResourceLocation tallBottomTexture = texture(entry.tallGrass(), "_bottom");
        ResourceLocation tallTopTexture = texture(entry.tallGrass(), "_top");

        ModelFile normal = gen.models().cubeBottomTop(
                grassName,
                grassSide,
                dirt,
                grassTop
        );

        ModelFile snowy = gen.models().cubeBottomTop(
                grassName + "_snowy",
                snowySide,
                dirt,
                grassTop
        );

        ModelFile shortGrass = gen.models().cross(
                shortName,
                shortTexture
        );

        ModelFile tallBottom = gen.models().cross(
                tallName + "_bottom",
                tallBottomTexture
        );

        ModelFile tallTop = gen.models().cross(
                tallName + "_top",
                tallTopTexture
        );

        gen.getVariantBuilder(entry.grass()).forAllStates(state ->
                state.getValue(SnowyDirtBlock.SNOWY)
                        ? ConfiguredModel.builder()
                                .modelFile(snowy)
                                .build()
                        : ConfiguredModel.allYRotations(
                                normal,
                                0,
                                false
                        )
        );

        gen.simpleBlock(entry.shortGrass(), shortGrass);

        gen.getVariantBuilder(entry.tallGrass()).forAllStates(state ->
                ConfiguredModel.builder()
                        .modelFile(
                                state.getValue(DoublePlantBlock.HALF)
                                        == DoubleBlockHalf.LOWER
                                        ? tallBottom
                                        : tallTop
                        )
                        .build()
        );

        gen.simpleBlockItem(entry.grass(), normal);

        gen.itemModels().singleTexture(
                shortName,
                gen.mcLoc("item/generated"),
                "layer0",
                shortTexture
        );

        gen.itemModels().singleTexture(
                tallName,
                gen.mcLoc("item/generated"),
                "layer0",
                tallTopTexture
        );
    }

    private static String name(Block block) {
        return net.minecraft.core.registries.BuiltInRegistries.BLOCK
                .getKey(block)
                .getPath();
    }

    private static ResourceLocation texture(Block block, String suffix) {
        ResourceLocation id =
                net.minecraft.core.registries.BuiltInRegistries.BLOCK
                        .getKey(block);

        return ResourceLocation.fromNamespaceAndPath(
                id.getNamespace(),
                "block/" + id.getPath() + suffix
        );
    }
    *///?} else {
    public static void generate(BlockModelGenerators gen, Entry entry) {
        if (entry == null) return;

        ResourceLocation normal =
                ModelLocationUtils.getModelLocation(entry.grass());

        ResourceLocation snowy =
                ModelLocationUtils.getModelLocation(
                        entry.grass(),
                        "_snowy"
                );

        ResourceLocation shortGrass =
                ModelLocationUtils.getModelLocation(entry.shortGrass());

        ResourceLocation tallBottom =
                ModelLocationUtils.getModelLocation(
                        entry.tallGrass(),
                        "_bottom"
                );

        ResourceLocation tallTop =
                ModelLocationUtils.getModelLocation(
                        entry.tallGrass(),
                        "_top"
                );

        ResourceLocation dirt =
                TextureMapping.getBlockTexture(Blocks.DIRT);

        ResourceLocation grassTop =
                TextureMapping.getBlockTexture(
                        entry.grass(),
                        "_top"
                );

        ResourceLocation grassSide =
                TextureMapping.getBlockTexture(
                        entry.grass(),
                        "_side"
                );

        ResourceLocation snowySide =
                TextureMapping.getBlockTexture(
                        entry.grass(),
                        "_side_snowy"
                );

        ResourceLocation shortTexture =
                TextureMapping.getBlockTexture(entry.shortGrass());

        ResourceLocation tallBottomTexture =
                TextureMapping.getBlockTexture(
                        entry.tallGrass(),
                        "_bottom"
                );

        ResourceLocation tallTopTexture =
                TextureMapping.getBlockTexture(
                        entry.tallGrass(),
                        "_top"
                );

        cubeBottomTop(
                gen,
                normal,
                dirt,
                grassTop,
                grassSide
        );

        cubeBottomTop(
                gen,
                snowy,
                dirt,
                grassTop,
                snowySide
        );

        cross(gen, shortGrass, shortTexture);
        cross(gen, tallBottom, tallBottomTexture);
        cross(gen, tallTop, tallTopTexture);

        registerBlockStates(
                gen,
                entry,
                normal,
                snowy,
                shortGrass,
                tallBottom,
                tallTop
        );

        gen.registerSimpleItemModel(entry.grass(), normal);

        flatItem(
                gen,
                entry.shortGrass(),
                shortTexture
        );

        flatItem(
                gen,
                entry.tallGrass(),
                tallTopTexture
        );
    }

    private static void registerBlockStates(
            BlockModelGenerators gen,
            Entry entry,
            ResourceLocation normal,
            ResourceLocation snowy,
            ResourceLocation shortGrass,
            ResourceLocation tallBottom,
            ResourceLocation tallTop
    ) {
        //? if <1.21.5 {
        /*java.util.List<Variant> normalVariants = java.util.List.of(
                Variant.variant()
                        .with(VariantProperties.MODEL, normal),

                Variant.variant()
                        .with(VariantProperties.MODEL, normal)
                        .with(
                                VariantProperties.Y_ROT,
                                VariantProperties.Rotation.R90
                        ),

                Variant.variant()
                        .with(VariantProperties.MODEL, normal)
                        .with(
                                VariantProperties.Y_ROT,
                                VariantProperties.Rotation.R180
                        ),

                Variant.variant()
                        .with(VariantProperties.MODEL, normal)
                        .with(
                                VariantProperties.Y_ROT,
                                VariantProperties.Rotation.R270
                        )
        );

        gen.blockStateOutput.accept(
                MultiVariantGenerator
                        .multiVariant(entry.grass())
                        .with(
                                PropertyDispatch
                                        .property(SnowyDirtBlock.SNOWY)
                                        .select(false, normalVariants)
                                        .select(
                                                true,
                                                Variant.variant().with(
                                                        VariantProperties.MODEL,
                                                        snowy
                                                )
                                        )
                        )
        );

        gen.blockStateOutput.accept(
                MultiVariantGenerator.multiVariant(
                        entry.shortGrass(),
                        Variant.variant().with(
                                VariantProperties.MODEL,
                                shortGrass
                        )
                )
        );

        gen.blockStateOutput.accept(
                MultiVariantGenerator
                        .multiVariant(entry.tallGrass())
                        .with(
                                PropertyDispatch
                                        .property(DoublePlantBlock.HALF)
                                        .select(
                                                DoubleBlockHalf.LOWER,
                                                Variant.variant().with(
                                                        VariantProperties.MODEL,
                                                        tallBottom
                                                )
                                        )
                                        .select(
                                                DoubleBlockHalf.UPPER,
                                                Variant.variant().with(
                                                        VariantProperties.MODEL,
                                                        tallTop
                                                )
                                        )
                        )
        );
        *///?} else {
        MultiVariant normalVariants = new MultiVariant(
                WeightedList.<Variant>builder()
                        .add(new Variant(normal))
                        .add(
                                new Variant(normal).with(
                                        BlockModelGenerators.Y_ROT_90
                                )
                        )
                        .add(
                                new Variant(normal).with(
                                        BlockModelGenerators.Y_ROT_180
                                )
                        )
                        .add(
                                new Variant(normal).with(
                                        BlockModelGenerators.Y_ROT_270
                                )
                        )
                        .build()
        );

        gen.blockStateOutput.accept(
                MultiVariantGenerator
                        .dispatch(entry.grass())
                        .with(
                                PropertyDispatch
                                        .initial(SnowyDirtBlock.SNOWY)
                                        .select(false, normalVariants)
                                        .select(
                                                true,
                                                BlockModelGenerators
                                                        .plainVariant(snowy)
                                        )
                        )
        );

        gen.blockStateOutput.accept(
                MultiVariantGenerator.dispatch(
                        entry.shortGrass(),
                        BlockModelGenerators.plainVariant(shortGrass)
                )
        );

        gen.blockStateOutput.accept(
                MultiVariantGenerator
                        .dispatch(entry.tallGrass())
                        .with(
                                PropertyDispatch
                                        .initial(DoublePlantBlock.HALF)
                                        .select(
                                                DoubleBlockHalf.LOWER,
                                                BlockModelGenerators
                                                        .plainVariant(tallBottom)
                                        )
                                        .select(
                                                DoubleBlockHalf.UPPER,
                                                BlockModelGenerators
                                                        .plainVariant(tallTop)
                                        )
                        )
        );
        //?}
    }

    private static void cubeBottomTop(
            BlockModelGenerators gen,
            ResourceLocation model,
            ResourceLocation bottom,
            ResourceLocation top,
            ResourceLocation side
    ) {
        ModelTemplates.CUBE_BOTTOM_TOP.create(
                model,
                new TextureMapping()
                        .put(TextureSlot.BOTTOM, bottom)
                        .put(TextureSlot.TOP, top)
                        .put(TextureSlot.SIDE, side),
                gen.modelOutput
        );
    }

    private static void cross(
            BlockModelGenerators gen,
            ResourceLocation model,
            ResourceLocation texture
    ) {
        ModelTemplates.CROSS.create(
                model,
                TextureMapping.cross(texture),
                gen.modelOutput
        );
    }

    private static void flatItem(
            BlockModelGenerators gen,
            Block block,
            ResourceLocation texture
    ) {
        ResourceLocation model =
                ModelLocationUtils.getModelLocation(block.asItem());

        ModelTemplates.FLAT_ITEM.create(
                model,
                TextureMapping.layer0(texture),
                gen.modelOutput
        );

        gen.itemModelOutput.accept(
                block.asItem(),
                ItemModelUtils.plainModel(model)
        );
    }
    //?}
}

