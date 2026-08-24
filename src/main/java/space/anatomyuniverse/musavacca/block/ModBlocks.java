package space.anatomyuniverse.musavacca.block;


import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DoubleHighBlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import space.anatomyuniverse.musavacca.MusaCore;
import space.anatomyuniverse.musavacca.block.custom.*;

import java.util.List;
import java.util.Set;

public final class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MusaCore.MOD_ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MusaCore.MOD_ID);

    public static final DeferredBlock<Block> BANANA_PEARL_BLOCK =
            BLOCKS.registerBlock("banana_pearl_block",
                    props -> new Block(props
                            .mapColor(MapColor.COLOR_ORANGE)
                            .strength(3.0F, 6.0F)
                            .sound(SoundType.COPPER)
                            .requiresCorrectToolForDrops()
                            .pushReaction(PushReaction.NORMAL)));

    public static final DeferredBlock<Block> BANANA_PEARL_BRICKS =
            BLOCKS.registerBlock("banana_pearl_bricks",
                    props -> new Block(props
                            .mapColor(MapColor.COLOR_ORANGE)
                            .strength(3.0F, 6.0F)
                            .sound(SoundType.COPPER)
                            .requiresCorrectToolForDrops()
                            .pushReaction(PushReaction.NORMAL)));

    public static final DeferredBlock<BreakBlock> MUSAVACCA_EGG =
            BLOCKS.registerBlock("musavacca_egg",
                    props -> new BreakBlock(props
                            .mapColor(MapColor.PLANT)
                            .strength(0.4F)
                            .sound(SoundType.GRASS)
                            .randomTicks()
                            .noOcclusion()
                            .pushReaction(PushReaction.NORMAL)));

    private static DeferredItem<BlockItem> MusavaccaEggItems(String name, int age) {
        return ITEMS.registerItem(
                name,
                props -> new BlockItem(
                        MUSAVACCA_EGG.get(),
                        props.component(
                                DataComponents.BLOCK_STATE,
                                BlockItemStateProperties.EMPTY.with(BreakBlock.AGE, age)
                        )
                )
        );
    }
    public static final DeferredItem<BlockItem> UNRIPE_MUSAVACCA_EGG =
            MusavaccaEggItems("unripe_musavacca_egg", 0);

    public static final DeferredItem<BlockItem> RIPENING_MUSAVACCA_EGG =
            MusavaccaEggItems("ripening_musavacca_egg", 1);

    public static final DeferredItem<BlockItem> RIPE_MUSAVACCA_EGG =
            MusavaccaEggItems("ripe_musavacca_egg", 2);


    public static final DeferredBlock<Block> BANANA_PEARL_CHAPITER =
            BLOCKS.registerBlock("banana_pearl_chapiter",
                    props -> new BananaPearlChapiter(props
                            .mapColor(MapColor.COLOR_ORANGE)
                            .strength(3.0F, 6.0F)
                            .sound(SoundType.COPPER)
                            .requiresCorrectToolForDrops()
                            .pushReaction(PushReaction.NORMAL)));

    public static final DeferredBlock<RotatedPillarBlock> BANANA_PEARL_PILLAR =
            BLOCKS.registerBlock("banana_pearl_pillar",
                    props -> new RotatedPillarBlock(props
                            .mapColor(MapColor.COLOR_ORANGE)
                            .strength(3.0F, 6.0F)
                            .sound(SoundType.COPPER)
                            .requiresCorrectToolForDrops()
                            .pushReaction(PushReaction.NORMAL)));


    public static final DeferredBlock<RotatedPillarBlock> STRIPPED_MUSAVACCA_STEM =
            BLOCKS.registerBlock("stripped_musavacca_stem",
                    props -> new RotatedPillarBlock(props
                            .mapColor(MapColor.COLOR_ORANGE)
                            .strength(3.0F, 6.0F)
                            .sound(SoundType.WOOD)
                            .requiresCorrectToolForDrops()
                            .pushReaction(PushReaction.NORMAL)));

    public static final DeferredBlock<ExudatedStrippedMusavaccaStemBlock>
            EXUDATED_STRIPPED_MUSAVACCA_STEM =
            BLOCKS.registerBlock(
                    "exudated_stripped_musavacca_stem",
                    props -> new ExudatedStrippedMusavaccaStemBlock(
                            props
                                    .mapColor(MapColor.COLOR_ORANGE)
                                    .strength(3.0F, 6.0F)
                                    .sound(SoundType.WOOD)
                                    .requiresCorrectToolForDrops()
                                    .pushReaction(PushReaction.NORMAL),
                            STRIPPED_MUSAVACCA_STEM
                    )
            );

    public static final DeferredBlock<StrippableMusavaccaStemBlock> MUSAVACCA_STEM =
            BLOCKS.registerBlock("musavacca_stem",
                    props -> new StrippableMusavaccaStemBlock(props
                            .mapColor(MapColor.COLOR_ORANGE)
                            .strength(3.0F, 6.0F)
                            .sound(SoundType.WOOD)
                            .requiresCorrectToolForDrops()
                            .pushReaction(PushReaction.NORMAL),
                            STRIPPED_MUSAVACCA_STEM,
                            EXUDATED_STRIPPED_MUSAVACCA_STEM));

    public static final DeferredBlock<SmallBananaPearlBlock> SMALL_BANANA_PEARL_BLOCK =
            BLOCKS.registerBlock("small_banana_pearl_block",
                    props -> new SmallBananaPearlBlock(props
                            .mapColor(MapColor.COLOR_ORANGE)
                            .strength(0.1F, 3.0F)
                            .sound(SoundType.CHAIN)
                            .noOcclusion()
                            .isSuffocating((state, level, pos) -> false)
                            .isViewBlocking((state, level, pos) -> false)
                            .isRedstoneConductor((state, level, pos) -> false)
                            .isValidSpawn((state, level, pos, entityType) -> false)
                            .pushReaction(PushReaction.NORMAL)
                    ));

    public static final DeferredBlock<PearlFireBlock> PEARL_FIRE =
            BLOCKS.registerBlock("pearl_fire",
                    props -> new PearlFireBlock(props
                            .mapColor(MapColor.FIRE)
                            .replaceable()
                            //? if <1.21.10 {
                            .noCollission()
                            //?} else {
                            /*.noCollision()
                             *///?}
                            .instabreak()
                            .lightLevel(state -> 15)
                            .sound(SoundType.WOOL)
                            .pushReaction(PushReaction.DESTROY)
                            .noLootTable()));

    public static final DeferredBlock<PearlPortalBlock> PEARL_PORTAL =
            BLOCKS.registerBlock("pearl_portal",
                    props -> new PearlPortalBlock(props
                            .mapColor(MapColor.COLOR_PURPLE)
                            //? if <1.21.10 {
                            .noCollission()
                            //?} else {
                            /*.noCollision()
                             *///?}
                            .noOcclusion()
                            .strength(-1.0F, 3600000.0F)
                            .lightLevel(state -> 11)
                            .sound(SoundType.GLASS)
                            .pushReaction(PushReaction.BLOCK)
                            .noLootTable()));

    public static final DeferredBlock<Block> MUSAVACCA_PLANKS =
            BLOCKS.registerBlock("musavacca_planks",
                    props -> new Block(
                            props
                                    .mapColor(MapColor.COLOR_ORANGE)
                                    .strength(2.0F, 3.0F)
                                    .sound(ModWoodTypes.MUSAVACCA_BLOCK_SET.soundType())
                                    .pushReaction(PushReaction.NORMAL)
                    ));


    public static final DeferredBlock<StairBlock> MUSAVACCA_STAIRS =
            BLOCKS.registerBlock("musavacca_stairs",
                    props -> new StairBlock(
                            MUSAVACCA_PLANKS.get().defaultBlockState(),
                            props
                                    .mapColor(MapColor.COLOR_ORANGE)
                                    .strength(2.0F, 3.0F)
                                    .sound(ModWoodTypes.MUSAVACCA_BLOCK_SET.soundType())
                                    .pushReaction(PushReaction.NORMAL)
                    ));


    public static final DeferredBlock<SlabBlock> MUSAVACCA_SLAB =
            BLOCKS.registerBlock("musavacca_slab",
                    props -> new SlabBlock(
                            props
                                    .mapColor(MapColor.COLOR_ORANGE)
                                    .strength(2.0F, 3.0F)
                                    .sound(ModWoodTypes.MUSAVACCA_BLOCK_SET.soundType())
                                    .pushReaction(PushReaction.NORMAL)
                    ));


    public static final DeferredBlock<FenceBlock> MUSAVACCA_FENCE =
            BLOCKS.registerBlock("musavacca_fence",
                    props -> new FenceBlock(
                            props
                                    .mapColor(MapColor.COLOR_ORANGE)
                                    .strength(2.0F, 3.0F)
                                    .sound(ModWoodTypes.MUSAVACCA_BLOCK_SET.soundType())
                                    .forceSolidOn()
                                    .pushReaction(PushReaction.NORMAL)
                    ));


    public static final DeferredBlock<FenceGateBlock> MUSAVACCA_FENCE_GATE =
            BLOCKS.registerBlock("musavacca_fence_gate",
                    props -> new FenceGateBlock(
                            ModWoodTypes.MUSAVACCA,
                            props
                                    .mapColor(MapColor.COLOR_ORANGE)
                                    .strength(2.0F, 3.0F)
                                    .sound(ModWoodTypes.MUSAVACCA.soundType())
                                    .forceSolidOn()
                                    .pushReaction(PushReaction.NORMAL)
                    ));


    public static final DeferredBlock<MusavaccaPortalDoorBlock> MUSAVACCA_DOOR =
            BLOCKS.registerBlock("musavacca_door",
                    props -> new MusavaccaPortalDoorBlock(
                            ModWoodTypes.MUSAVACCA_BLOCK_SET,
                            props
                                    .mapColor(MapColor.COLOR_ORANGE)
                                    .strength(3.0F, 3.0F)
                                    .sound(ModWoodTypes.MUSAVACCA_BLOCK_SET.soundType())
                                    .noOcclusion()
                                    .pushReaction(PushReaction.DESTROY)
                    ));


    public static final DeferredBlock<MusavaccaPortalTrapdoorBlock> MUSAVACCA_TRAPDOOR =
            BLOCKS.registerBlock("musavacca_trapdoor",
                    props -> new MusavaccaPortalTrapdoorBlock(
                            ModWoodTypes.MUSAVACCA_BLOCK_SET,
                            props
                                    .mapColor(MapColor.COLOR_ORANGE)
                                    .strength(3.0F, 3.0F)
                                    .sound(ModWoodTypes.MUSAVACCA_BLOCK_SET.soundType())
                                    .noOcclusion()
                                    .pushReaction(PushReaction.DESTROY)
                    ));


    public static final DeferredBlock<PressurePlateBlock> MUSAVACCA_PRESSURE_PLATE =
            BLOCKS.registerBlock("musavacca_pressure_plate",
                    props -> new PressurePlateBlock(
                            ModWoodTypes.MUSAVACCA_BLOCK_SET,
                            props
                                    .mapColor(MapColor.COLOR_ORANGE)
                                    .strength(0.5F)
                                    .sound(ModWoodTypes.MUSAVACCA_BLOCK_SET.soundType())
                                    .forceSolidOn()
                                    //? if <1.21.10 {
                                    .noCollission()
                                    //?} else {
                                    /*.noCollision()
                                     *///?}
                                    .pushReaction(PushReaction.DESTROY)
                    ));


    public static final DeferredBlock<ButtonBlock> MUSAVACCA_BUTTON =
            BLOCKS.registerBlock("musavacca_button",
                    props -> new ButtonBlock(
                            ModWoodTypes.MUSAVACCA_BLOCK_SET,
                            30,
                            props
                                    .mapColor(MapColor.COLOR_ORANGE)
                                    .strength(0.5F)
                                    .sound(ModWoodTypes.MUSAVACCA_BLOCK_SET.soundType())
                                    //? if <1.21.10 {
                                    .noCollission()
                                    //?} else {
                                    /*.noCollision()
                                     *///?}
                                    .pushReaction(PushReaction.DESTROY)
                    ));

    public static final DeferredBlock<Block> MUSAVACCA_LEAVES =
            BLOCKS.registerBlock("musavacca_leaves", props -> {
                var p = props
                        .mapColor(MapColor.PLANT)
                        .strength(0.2F)
                        .randomTicks()
                        .sound(SoundType.GRASS)
                        .noOcclusion()
                        .isSuffocating((s, l, pos) -> false)
                        .isViewBlocking((s, l, pos) -> false)
                        .pushReaction(PushReaction.DESTROY);

                return new MusavaccaLeaves(0.0F, p);
            });

    private static DeferredBlock<MusavaccaCropBlock> musavaccaCropStage(String name, int age) {
        return BLOCKS.registerBlock(name,
                props -> new MusavaccaCropBlock(
                        age,
                        props.mapColor(MapColor.PLANT)
                                .noCollission()
                                .randomTicks()
                                .instabreak()
                                .sound(SoundType.CROP)
                                .pushReaction(PushReaction.DESTROY)
                                .noLootTable()
                )
        );
    }

    public static final DeferredBlock<MusavaccaCropBlock> MUSAVACCA_SPROUT =
            musavaccaCropStage("musavacca_sprout", 0);

    public static final DeferredBlock<MusavaccaCropBlock> MUSAVACCA_SUCKER =
            musavaccaCropStage("musavacca_sucker", 1);

    public static final DeferredBlock<MusavaccaCropBlock> MUSAVACCA_PLANT =
            musavaccaCropStage("musavacca_plant", 2);

    public static final DeferredBlock<MusavaccaSaplingBlock> MUSAVACCA_PSEUDOSTEM =
            BLOCKS.registerBlock("musavacca_pseudostem",
                    props -> new MusavaccaSaplingBlock(
                            props.mapColor(MapColor.PLANT)
                                    .noCollission()
                                    .randomTicks()
                                    .instabreak()
                                    .sound(SoundType.GRASS)
                                    .pushReaction(PushReaction.DESTROY)
                                    .noLootTable()
                    ));

    public static final DeferredBlock<DecorationBlock> BANANA_PEARL_CHALICE =
            BLOCKS.registerBlock("banana_pearl_chalice",
                    props -> new DecorationBlock(
                            props.mapColor(MapColor.COLOR_ORANGE)
                                    .strength(0.2F, 3.0F)
                                    .sound(SoundType.CHAIN)
                                    .noOcclusion()
                                    .pushReaction(PushReaction.DESTROY),
                            DecorationBlock.Options.builder()

                                    // Pick ONE per placement:
                                    // .floor() / .floorFacing() / .floorRotating()
                                    // .sneak() / .sneakFacing() / .sneakRotating()
                                    // .roof()  / .roofFacing()  / .roofRotating()
                                    //
                                    // .side() is always wall-facing only.
                                    //
                                    // fixed    = no rotation
                                    // facing   = 4-way north/east/south/west
                                    // rotating = 16-step skull-like rotation

                                    .floorRotating()
                                    .sneakRotating()
                                    .side()

                                    // Shape order: floor, sneak, side, roof.
                                    // Side shape is authored north-facing and auto-rotates hitbox.
                                    .shapes(DecorationBlock.ShapeSet.of(
                                            Block.box(4.0D, 0.0D, 4.0D, 12.0D, 12.0D, 12.0D),
                                            Block.box(1.75D, 0.0D, 2.0D, 14.25D, 7.0D, 14.0D),
                                            Block.box(4.5D, 0.5D, 2.25D, 11.5D, 12.5D, 9.25D),
                                            Block.box(4.0D, 4.0D, 4.0D, 12.0D, 16.0D, 12.0D)
                                    ))
                                    .build()
                    ));

    public static final DeferredBlock<VocoTableBlock> VOCO_TABLE =
            BLOCKS.registerBlock("voco_table",
                    props -> new VocoTableBlock(
                            props.mapColor(MapColor.COLOR_ORANGE)
                                    .strength(0.2F, 3.0F)
                                    .sound(SoundType.WOOD)
                                    .noOcclusion()
                                    .pushReaction(PushReaction.DESTROY)
                    ));

    public static final DeferredBlock<VocoPostBlock> VOCO_POST =
            BLOCKS.registerBlock("voco_post",
                    props -> new VocoPostBlock(
                            props.mapColor(MapColor.COLOR_ORANGE)
                                    .strength(0.2F, 3.0F)
                                    .sound(SoundType.WOOD)
                                    .noOcclusion()
                                    .pushReaction(PushReaction.DESTROY)
                    ));

    public static final DeferredBlock<HexBlock> HEX_BLOCK =
            BLOCKS.registerBlock("hex_block",
                    props -> new HexBlock(props
                            .mapColor(MapColor.PLANT)
                            .strength(0.2F)
                            .randomTicks()
                            .sound(SoundType.GRASS)
                            .noOcclusion()
                            .pushReaction(PushReaction.NORMAL)));

//    public static final DeferredItem<HexBlockItem> HEX_BLOCK_ITEM =
//            ITEMS.registerItem(
//                    "hex_block",
//                    props -> new HexBlockItem(
//                            HEX_BLOCK.get(),
//                            props.useBlockDescriptionPrefix()
//                    )
//            );

    public static final DeferredBlock<HardHexBlock> HARD_HEX_BLOCK =
            BLOCKS.registerBlock("hard_hex_block",
                    props -> new HardHexBlock(props
                            .mapColor(MapColor.COLOR_ORANGE)
                            .strength(0.1F, 6.0F)
                            .sound(SoundType.HONEY_BLOCK)
                            .noOcclusion()
                            .pushReaction(PushReaction.NORMAL)));

    private static DeferredBlock<PearlCandleBlock> pearlCandle(String name, Block vanillaCandleBlock) {
        return BLOCKS.registerBlock(name,
                props -> new PearlCandleBlock(
                        vanillaCandleBlock,
                        props
                                .mapColor(MapColor.WOOL)
                                .strength(0.1F)
                                .sound(SoundType.CANDLE)
                                .noOcclusion()
                                .lightLevel(PearlCandleBlock::candleLightLevel)
                                .pushReaction(PushReaction.DESTROY)
                )
        );
    }

    public static final DeferredBlock<PearlCandleBlock> PEARL_CANDLE =
            pearlCandle("pearl_candle", Blocks.CANDLE);

    public static final DeferredBlock<PearlCandleBlock> PEARL_WHITE_CANDLE =
            pearlCandle("pearl_white_candle", Blocks.WHITE_CANDLE);

    public static final DeferredBlock<PearlCandleBlock> PEARL_ORANGE_CANDLE =
            pearlCandle("pearl_orange_candle", Blocks.ORANGE_CANDLE);

    public static final DeferredBlock<PearlCandleBlock> PEARL_MAGENTA_CANDLE =
            pearlCandle("pearl_magenta_candle", Blocks.MAGENTA_CANDLE);

    public static final DeferredBlock<PearlCandleBlock> PEARL_LIGHT_BLUE_CANDLE =
            pearlCandle("pearl_light_blue_candle", Blocks.LIGHT_BLUE_CANDLE);

    public static final DeferredBlock<PearlCandleBlock> PEARL_YELLOW_CANDLE =
            pearlCandle("pearl_yellow_candle", Blocks.YELLOW_CANDLE);

    public static final DeferredBlock<PearlCandleBlock> PEARL_LIME_CANDLE =
            pearlCandle("pearl_lime_candle", Blocks.LIME_CANDLE);

    public static final DeferredBlock<PearlCandleBlock> PEARL_PINK_CANDLE =
            pearlCandle("pearl_pink_candle", Blocks.PINK_CANDLE);

    public static final DeferredBlock<PearlCandleBlock> PEARL_GRAY_CANDLE =
            pearlCandle("pearl_gray_candle", Blocks.GRAY_CANDLE);

    public static final DeferredBlock<PearlCandleBlock> PEARL_LIGHT_GRAY_CANDLE =
            pearlCandle("pearl_light_gray_candle", Blocks.LIGHT_GRAY_CANDLE);

    public static final DeferredBlock<PearlCandleBlock> PEARL_CYAN_CANDLE =
            pearlCandle("pearl_cyan_candle", Blocks.CYAN_CANDLE);

    public static final DeferredBlock<PearlCandleBlock> PEARL_PURPLE_CANDLE =
            pearlCandle("pearl_purple_candle", Blocks.PURPLE_CANDLE);

    public static final DeferredBlock<PearlCandleBlock> PEARL_BLUE_CANDLE =
            pearlCandle("pearl_blue_candle", Blocks.BLUE_CANDLE);

    public static final DeferredBlock<PearlCandleBlock> PEARL_BROWN_CANDLE =
            pearlCandle("pearl_brown_candle", Blocks.BROWN_CANDLE);

    public static final DeferredBlock<PearlCandleBlock> PEARL_GREEN_CANDLE =
            pearlCandle("pearl_green_candle", Blocks.GREEN_CANDLE);

    public static final DeferredBlock<PearlCandleBlock> PEARL_RED_CANDLE =
            pearlCandle("pearl_red_candle", Blocks.RED_CANDLE);

    public static final DeferredBlock<PearlCandleBlock> PEARL_BLACK_CANDLE =
            pearlCandle("pearl_black_candle", Blocks.BLACK_CANDLE);

    public static final List<DeferredBlock<PearlCandleBlock>> PEARL_CANDLES = List.of(
            PEARL_CANDLE,
            PEARL_WHITE_CANDLE,
            PEARL_ORANGE_CANDLE,
            PEARL_MAGENTA_CANDLE,
            PEARL_LIGHT_BLUE_CANDLE,
            PEARL_YELLOW_CANDLE,
            PEARL_LIME_CANDLE,
            PEARL_PINK_CANDLE,
            PEARL_GRAY_CANDLE,
            PEARL_LIGHT_GRAY_CANDLE,
            PEARL_CYAN_CANDLE,
            PEARL_PURPLE_CANDLE,
            PEARL_BLUE_CANDLE,
            PEARL_BROWN_CANDLE,
            PEARL_GREEN_CANDLE,
            PEARL_RED_CANDLE,
            PEARL_BLACK_CANDLE
    );





    public static final DeferredBlock<CaroteneGrassBlock> CAROTENE_GRASS =
            BLOCKS.registerBlock("carotene_grass",
                    props -> new CaroteneGrassBlock(
                            props.mapColor(MapColor.COLOR_ORANGE)
                                    .strength(0.6F)
                                    .sound(SoundType.GRASS)
                                    .randomTicks()
                                    .pushReaction(PushReaction.NORMAL)
                    ));

    public static final DeferredBlock<CaroteneShortGrassBlock> CAROTENE_SHORT_GRASS =
            BLOCKS.registerBlock("carotene_short_grass",
                    props -> new CaroteneShortGrassBlock(
                            props.mapColor(MapColor.PLANT)
                                    .replaceable()
                                    //? if <1.21.10 {
                                    .noCollission()
                                    //?} else {
                                    /*.noCollision()
                                     *///?}
                                    .instabreak()
                                    .sound(SoundType.GRASS)
                                    .offsetType(BlockBehaviour.OffsetType.XZ)
                                    .pushReaction(PushReaction.DESTROY)
                    ));

    public static final DeferredBlock<DoublePlantBlock> CAROTENE_TALL_GRASS =
            BLOCKS.registerBlock("carotene_tall_grass",
                    props -> new DoublePlantBlock(
                            props.mapColor(MapColor.PLANT)
                                    .replaceable()
                                    //? if <1.21.10 {
                                    .noCollission()
                                    //?} else {
                                    /*.noCollision()
                                     *///?}
                                    .instabreak()
                                    .sound(SoundType.GRASS)
                                    .offsetType(BlockBehaviour.OffsetType.XZ)
                                    .pushReaction(PushReaction.DESTROY)
                    ));

    private static final Set<DeferredBlock<? extends Block>> SKIP_BLOCK_ITEMS = Set.of(
//            HEX_BLOCK
            PEARL_FIRE,
            MUSAVACCA_EGG,
            MUSAVACCA_SPROUT,
            MUSAVACCA_SUCKER,
            MUSAVACCA_PLANT,
            MUSAVACCA_PSEUDOSTEM,
            PEARL_PORTAL,
            MUSAVACCA_DOOR
    );

    static {
        BLOCKS.getEntries().forEach(entry -> {
            if (!SKIP_BLOCK_ITEMS.contains(entry) && !PEARL_CANDLES.contains(entry)) {
                ITEMS.registerSimpleBlockItem(entry);
            }
        });
    }

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
    }

    private ModBlocks() {}
}
