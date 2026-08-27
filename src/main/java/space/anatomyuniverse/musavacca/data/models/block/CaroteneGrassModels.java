package space.anatomyuniverse.musavacca.data.models.block;

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
import net.minecraft.client.data.models.blockstates.MultiPartGenerator;
import net.minecraft.client.renderer.block.model.Variant;
import net.minecraft.util.random.WeightedList;
//?}
//?}

public final class CaroteneGrassModels {
    private CaroteneGrassModels() {}

    public record Entry(
            Block grass,
            Block shortGrass,
            Block tallGrass
    ) {}

    // =========================================================
    // <= 1.21.3
    // =========================================================

    //? if <1.21.4 {
    /*public static void generate(
            BlockStateProvider gen,
            Entry entry
    ) {
        if (entry == null) return;

        String grassName = name(entry.grass());
        String shortName = name(entry.shortGrass());
        String tallName = name(entry.tallGrass());

        var dirt = texture(Blocks.DIRT, "");
        var grassTop = texture(entry.grass(), "_top");
        var grassSide = texture(entry.grass(), "_side");
        var grassSnowySide = texture(entry.grass(), "_side_snowy");
        var shortTexture = texture(entry.shortGrass(), "");
        var tallBottomTexture = texture(entry.tallGrass(), "_bottom");
        var tallTopTexture = texture(entry.tallGrass(), "_top");


        // =====================================================
        // BLOCK MODELS
        // =====================================================

        ModelFile normal = gen.models().cubeBottomTop(
                grassName,
                grassSide,
                dirt,
                grassTop
        );

        ModelFile snowy = gen.models().cubeBottomTop(
                grassName + "_snowy",
                grassSnowySide,
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


        // =====================================================
        // BLOCKSTATES
        // =====================================================

        // Vanilla grass behavior:
        // snowy=false -> random 0 / 90 / 180 / 270 rotation
        // snowy=true  -> single snowy model

        gen.getVariantBuilder(entry.grass())
                .forAllStates(state -> {
                    if (state.getValue(SnowyDirtBlock.SNOWY)) {
                        return ConfiguredModel.builder()
                                .modelFile(snowy)
                                .build();
                    }

                    return ConfiguredModel.allYRotations(
                            normal,
                            0,
                            false
                    );
                });


        gen.simpleBlock(
                entry.shortGrass(),
                shortGrass
        );


        gen.getVariantBuilder(entry.tallGrass())
                .forAllStates(state ->
                        ConfiguredModel.builder()
                                .modelFile(
                                        state.getValue(DoublePlantBlock.HALF)
                                                == DoubleBlockHalf.LOWER
                                                ? tallBottom
                                                : tallTop
                                )
                                .build()
                );


        // =====================================================
        // ITEM MODELS
        // =====================================================

        gen.simpleBlockItem(
                entry.grass(),
                normal
        );


        // Short grass:
        // item/generated -> block/carotene_short_grass

        gen.itemModels().singleTexture(
                shortName,
                gen.mcLoc("item/generated"),
                "layer0",
                shortTexture
        );


        // Tall grass:
        // vanilla-style item/generated -> block/carotene_tall_grass_top

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


    private static net.minecraft.resources.ResourceLocation texture(
            Block block,
            String suffix
    ) {
        var id = net.minecraft.core.registries.BuiltInRegistries.BLOCK
                .getKey(block);

        return net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(
                id.getNamespace(),
                "block/" + id.getPath() + suffix
        );
    }
    *///?}


    // =========================================================
    // >= 1.21.4
    // =========================================================

    //? if >=1.21.4 {
    public static void generate(
            BlockModelGenerators gen,
            Entry entry
    ) {
        if (entry == null) return;


        // =====================================================
        // MODEL LOCATIONS
        // =====================================================

        var normal =
                ModelLocationUtils.getModelLocation(entry.grass());

        var snowy =
                ModelLocationUtils.getModelLocation(
                        entry.grass(),
                        "_snowy"
                );

        var shortGrass =
                ModelLocationUtils.getModelLocation(
                        entry.shortGrass()
                );

        var tallBottom =
                ModelLocationUtils.getModelLocation(
                        entry.tallGrass(),
                        "_bottom"
                );

        var tallTop =
                ModelLocationUtils.getModelLocation(
                        entry.tallGrass(),
                        "_top"
                );


        var shortGrassItem =
                ModelLocationUtils.getModelLocation(
                        entry.shortGrass().asItem()
                );

        var tallGrassItem =
                ModelLocationUtils.getModelLocation(
                        entry.tallGrass().asItem()
                );


        // =====================================================
        // TEXTURES
        // =====================================================

        var dirt =
                TextureMapping.getBlockTexture(Blocks.DIRT);

        var grassTop =
                TextureMapping.getBlockTexture(
                        entry.grass(),
                        "_top"
                );

        var grassSide =
                TextureMapping.getBlockTexture(
                        entry.grass(),
                        "_side"
                );

        var grassSnowySide =
                TextureMapping.getBlockTexture(
                        entry.grass(),
                        "_side_snowy"
                );

        var shortTexture =
                TextureMapping.getBlockTexture(
                        entry.shortGrass()
                );

        var tallBottomTexture =
                TextureMapping.getBlockTexture(
                        entry.tallGrass(),
                        "_bottom"
                );

        var tallTopTexture =
                TextureMapping.getBlockTexture(
                        entry.tallGrass(),
                        "_top"
                );


        // =====================================================
        // BLOCK MODELS
        // =====================================================


        // -----------------------------------------------------
        // Carotene Grass
        //
        // top    -> carotene_grass_top
        // side   -> carotene_grass_side
        // bottom -> minecraft:block/dirt
        // -----------------------------------------------------

        ModelTemplates.CUBE_BOTTOM_TOP.create(
                normal,
                new TextureMapping()
                        .put(
                                TextureSlot.BOTTOM,
                                dirt
                        )
                        .put(
                                TextureSlot.TOP,
                                grassTop
                        )
                        .put(
                                TextureSlot.SIDE,
                                grassSide
                        ),
                gen.modelOutput
        );


        // -----------------------------------------------------
        // Snowy Carotene Grass
        //
        // top    -> carotene_grass_top
        // side   -> carotene_grass_side_snowy
        // bottom -> minecraft:block/dirt
        // -----------------------------------------------------

        ModelTemplates.CUBE_BOTTOM_TOP.create(
                snowy,
                new TextureMapping()
                        .put(
                                TextureSlot.BOTTOM,
                                dirt
                        )
                        .put(
                                TextureSlot.TOP,
                                grassTop
                        )
                        .put(
                                TextureSlot.SIDE,
                                grassSnowySide
                        ),
                gen.modelOutput
        );


        // Short grass block model

        ModelTemplates.CROSS.create(
                shortGrass,
                TextureMapping.cross(
                        shortTexture
                ),
                gen.modelOutput
        );


        // Tall grass bottom block model

        ModelTemplates.CROSS.create(
                tallBottom,
                TextureMapping.cross(
                        tallBottomTexture
                ),
                gen.modelOutput
        );


        // Tall grass top block model

        ModelTemplates.CROSS.create(
                tallTop,
                TextureMapping.cross(
                        tallTopTexture
                ),
                gen.modelOutput
        );


        // =====================================================
        // BLOCKSTATES
        // =====================================================


        // -----------------------------------------------------
        // 1.21.4
        // -----------------------------------------------------

        //? if <1.21.5 {
        
         /** Vanilla-style grass rotation:
         *
         * snowy=false:
         *   0 / 90 / 180 / 270
         *
         * snowy=true:
         *   snowy model only
         
        gen.blockStateOutput.accept(
                MultiVariantGenerator
                        .multiVariant(entry.grass())
                        .with(
                                PropertyDispatch
                                        .property(SnowyDirtBlock.SNOWY)

                                        .select(
                                                false,
                                                java.util.List.of(
                                                        Variant.variant()
                                                                .with(
                                                                        VariantProperties.MODEL,
                                                                        normal
                                                                ),

                                                        Variant.variant()
                                                                .with(
                                                                        VariantProperties.MODEL,
                                                                        normal
                                                                )
                                                                .with(
                                                                        VariantProperties.Y_ROT,
                                                                        VariantProperties.Rotation.R90
                                                                ),

                                                        Variant.variant()
                                                                .with(
                                                                        VariantProperties.MODEL,
                                                                        normal
                                                                )
                                                                .with(
                                                                        VariantProperties.Y_ROT,
                                                                        VariantProperties.Rotation.R180
                                                                ),

                                                        Variant.variant()
                                                                .with(
                                                                        VariantProperties.MODEL,
                                                                        normal
                                                                )
                                                                .with(
                                                                        VariantProperties.Y_ROT,
                                                                        VariantProperties.Rotation.R270
                                                                )
                                                )
                                        )

                                        .select(
                                                true,
                                                Variant.variant()
                                                        .with(
                                                                VariantProperties.MODEL,
                                                                snowy
                                                        )
                                        )
                        )
        );


        gen.blockStateOutput.accept(
                MultiVariantGenerator.multiVariant(
                        entry.shortGrass(),
                        Variant.variant()
                                .with(
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
                                                Variant.variant()
                                                        .with(
                                                                VariantProperties.MODEL,
                                                                tallBottom
                                                        )
                                        )

                                        .select(
                                                DoubleBlockHalf.UPPER,
                                                Variant.variant()
                                                        .with(
                                                                VariantProperties.MODEL,
                                                                tallTop
                                                        )
                                        )
                        )
        );
        *///?}


        // -----------------------------------------------------
        // 1.21.5+
        // -----------------------------------------------------

        //? if >=1.21.5 {

        /*
         * Public modern model API.
         *
         * Four equally weighted normal variants:
         *
         * 0°
         * 90°
         * 180°
         * 270°
         */

        MultiVariant normalGrassVariants = new MultiVariant(
                WeightedList.<Variant>builder()
                        .add(
                                new Variant(normal)
                        )
                        .add(
                                new Variant(normal)
                                        .with(
                                                BlockModelGenerators.Y_ROT_90
                                        )
                        )
                        .add(
                                new Variant(normal)
                                        .with(
                                                BlockModelGenerators.Y_ROT_180
                                        )
                        )
                        .add(
                                new Variant(normal)
                                        .with(
                                                BlockModelGenerators.Y_ROT_270
                                        )
                        )
                        .build()
        );


        /*
         * Carotene Grass:
         *
         * snowy=false -> four random rotations
         * snowy=true  -> snowy model
         */

        gen.blockStateOutput.accept(
                MultiVariantGenerator
                        .dispatch(entry.grass())
                        .with(
                                PropertyDispatch
                                        .initial(
                                                SnowyDirtBlock.SNOWY
                                        )
                                        .select(
                                                false,
                                                normalGrassVariants
                                        )
                                        .select(
                                                true,
                                                BlockModelGenerators
                                                        .plainVariant(
                                                                snowy
                                                        )
                                        )
                        )
        );


        // Short grass

        gen.blockStateOutput.accept(
                MultiVariantGenerator.dispatch(
                        entry.shortGrass(),
                        BlockModelGenerators.plainVariant(
                                shortGrass
                        )
                )
        );


        // Tall grass

        gen.blockStateOutput.accept(
                MultiPartGenerator
                        .multiPart(entry.tallGrass())

                        .with(
                                BlockModelGenerators
                                        .condition()
                                        .term(
                                                DoublePlantBlock.HALF,
                                                DoubleBlockHalf.LOWER
                                        ),

                                BlockModelGenerators.variant(
                                        new Variant(tallBottom)
                                )
                        )

                        .with(
                                BlockModelGenerators
                                        .condition()
                                        .term(
                                                DoublePlantBlock.HALF,
                                                DoubleBlockHalf.UPPER
                                        ),

                                BlockModelGenerators.variant(
                                        new Variant(tallTop)
                                )
                        )
        );
        //?}


        // =====================================================
        // ITEM MODELS
        // =====================================================


        // Carotene Grass item uses normal cube model

        gen.registerSimpleItemModel(
                entry.grass(),
                normal
        );


        // -----------------------------------------------------
        // Short grass inventory
        //
        // item/generated
        // layer0 -> block/carotene_short_grass
        // -----------------------------------------------------

        ModelTemplates.FLAT_ITEM.create(
                shortGrassItem,
                TextureMapping.layer0(
                        shortTexture
                ),
                gen.modelOutput
        );

        gen.itemModelOutput.accept(
                entry.shortGrass().asItem(),
                ItemModelUtils.plainModel(
                        shortGrassItem
                )
        );


        // -----------------------------------------------------
        // Tall grass inventory
        //
        // Vanilla-style:
        //
        // item/generated
        // layer0 -> block/carotene_tall_grass_top
        // -----------------------------------------------------

        ModelTemplates.FLAT_ITEM.create(
                tallGrassItem,
                TextureMapping.layer0(
                        tallTopTexture
                ),
                gen.modelOutput
        );

        gen.itemModelOutput.accept(
                entry.tallGrass().asItem(),
                ItemModelUtils.plainModel(
                        tallGrassItem
                )
        );
    }
    //?}
}