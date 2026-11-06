package space.anatomyuniverse.musavacca.block;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.BlockItem;

import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;

import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import space.anatomyuniverse.musavacca.MusaCore;
import space.anatomyuniverse.musavacca.block.custom.*;
import java.util.Set;

public final class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MusaCore.MOD_ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MusaCore.MOD_ID);
    public static final DeferredBlock<Block> BANANA_PEARL_BLOCK =
            BLOCKS.registerBlock("banana_pearl_block",
                    props -> new BananaPearlBlock(props
                            .mapColor(MapColor.COLOR_YELLOW)
                            .strength(3.0F, 6.0F)
                            .sound(SoundType.COPPER)
                            .requiresCorrectToolForDrops()
                            //? if >=26.3 {
                            /*.pushReaction(PushReaction.PUSH_PULL)));
                            *///?} else {
                            .pushReaction(PushReaction.NORMAL)));
                            //?}
    public static final DeferredBlock<Block> BANANA_PEARL_BRICKS =
            BLOCKS.registerBlock("banana_pearl_bricks",
                    props -> new BananaPearlBlock(props
                            .mapColor(MapColor.COLOR_YELLOW)
                            .strength(3.0F, 6.0F)
                            .sound(SoundType.COPPER)
                            .requiresCorrectToolForDrops()
                            //? if >=26.3 {
                            /*.pushReaction(PushReaction.PUSH_PULL)));
                            *///?} else {
                            .pushReaction(PushReaction.NORMAL)));
                            //?}
    public static final DeferredBlock<BreakBlock> MUSAVACCA_EGG =
            BLOCKS.registerBlock("musavacca_egg",
                    props -> new BreakBlock(props
                            .mapColor(MapColor.COLOR_ORANGE)
                            .strength(0.4F)
                            .sound(SoundType.GRASS)
                            .randomTicks()
                            .noOcclusion()
                            //? if >=26.3 {
                            /*.pushReaction(PushReaction.PUSH_PULL)));
                            *///?} else {
                            .pushReaction(PushReaction.NORMAL)));
                            //?}
    private static DeferredItem<BlockItem> MusavaccaEggItems(String name, int age) {
        return ITEMS.registerItem(
                name,
                props -> new BlockItem(MUSAVACCA_EGG.get(),
                        props.component(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY.with(BreakBlock.AGE, age)))
                //? if <1.21.2 {
                /*{ @Override public String getDescriptionId() { return getOrCreateDescriptionId(); } }
                *///?}
        );
    }

    public static final DeferredItem<BlockItem> UNRIPE_MUSAVACCA_EGG = MusavaccaEggItems("unripe_musavacca_egg", 0);
    public static final DeferredItem<BlockItem> RIPENING_MUSAVACCA_EGG = MusavaccaEggItems("ripening_musavacca_egg", 1);
    public static final DeferredItem<BlockItem> RIPE_MUSAVACCA_EGG = MusavaccaEggItems("ripe_musavacca_egg", 2);
    public static final DeferredBlock<Block> BANANA_PEARL_CHAPITER =
            BLOCKS.registerBlock("banana_pearl_chapiter",
                    props -> new BananaPearlChapiter(props
                            .mapColor(MapColor.COLOR_YELLOW)
                            .strength(3.0F, 6.0F)
                            .sound(SoundType.COPPER)
                            .requiresCorrectToolForDrops()
                            //? if >=26.3 {
                            /*.pushReaction(PushReaction.PUSH_PULL)));
                            *///?} else {
                            .pushReaction(PushReaction.NORMAL)));
                            //?}
    public static final DeferredBlock<Block> BANANA_PEARL_MASCARON =
            BLOCKS.registerBlock("banana_pearl_mascaron",
                    props -> new BananaPearlMascaron(props
                            .mapColor(MapColor.COLOR_YELLOW)
                            .strength(3.0F, 6.0F)
                            .sound(SoundType.COPPER)
                            .requiresCorrectToolForDrops()
                            .noOcclusion()
                            //? if >=26.3 {
                            /*.pushReaction(PushReaction.PUSH_PULL)));
                            *///?} else {
                            .pushReaction(PushReaction.NORMAL)));
                            //?}
    public static final DeferredBlock<RotatedPillarBlock> BANANA_PEARL_PILLAR =
            BLOCKS.registerBlock("banana_pearl_pillar",
                    props -> new BananaPearlPillarBlock(props
                            .mapColor(MapColor.COLOR_YELLOW)
                            .strength(3.0F, 6.0F)
                            .sound(SoundType.COPPER)
                            .requiresCorrectToolForDrops()
                            //? if >=26.3 {
                            /*.pushReaction(PushReaction.PUSH_PULL)));
                            *///?} else {
                            .pushReaction(PushReaction.NORMAL)));
                            //?}
    public static final DeferredBlock<RotatedPillarBlock> STRIPPED_MUSAVACCA_STEM =
            BLOCKS.registerBlock("stripped_musavacca_stem",
                    props -> new RotatedPillarBlock(props
                            .mapColor(MapColor.COLOR_BROWN)
                            .strength(2.0F, 2.0F)
                            .sound(SoundType.STEM)
                            //? if >=26.3 {
                            /*.pushReaction(PushReaction.PUSH_PULL)));
                            *///?} else {
                            .pushReaction(PushReaction.NORMAL)));
                            //?}
    public static final DeferredBlock<ExudatedStrippedMusavaccaStemBlock> EXUDATED_STRIPPED_MUSAVACCA_STEM =
            BLOCKS.registerBlock("exudated_stripped_musavacca_stem",
                    props -> new ExudatedStrippedMusavaccaStemBlock(
                            //? if >=26.3 {
                            /*props.mapColor(MapColor.COLOR_BROWN).strength(2.0F, 2.0F).sound(SoundType.STEM).pushReaction(PushReaction.PUSH_PULL),
                            *///?} else {
                            props.mapColor(MapColor.COLOR_BROWN).strength(2.0F, 2.0F).sound(SoundType.STEM).pushReaction(PushReaction.NORMAL),
                            //?}
                            STRIPPED_MUSAVACCA_STEM));
    public static final DeferredBlock<StrippableMusavaccaStemBlock> MUSAVACCA_STEM =
            BLOCKS.registerBlock("musavacca_stem",
                    props -> new StrippableMusavaccaStemBlock(
                            //? if >=26.3 {
                            /*props.mapColor(MapColor.COLOR_BROWN).strength(2.0F, 2.0F).sound(SoundType.STEM).pushReaction(PushReaction.PUSH_PULL),
                            *///?} else {
                            props.mapColor(MapColor.COLOR_BROWN).strength(2.0F, 2.0F).sound(SoundType.STEM).pushReaction(PushReaction.NORMAL),
                            //?}
                            STRIPPED_MUSAVACCA_STEM, EXUDATED_STRIPPED_MUSAVACCA_STEM));
    public static final DeferredBlock<SmallBananaPearlBlock> SMALL_BANANA_PEARL_BLOCK =
            BLOCKS.registerBlock("small_banana_pearl_block",
                    props -> new SmallBananaPearlBlock(props
                            .mapColor(MapColor.COLOR_YELLOW)
                            .strength(0.1F, 3.0F)
                            .sound(SoundType.CHAIN)
                            .noOcclusion()
                            .isSuffocating((state, level, pos) -> false)
                            //? if >=26.3 {
                            /*.isViewBlocking((state, level, pos, bounds) -> false)
                            *///?} else {
                            .isViewBlocking((state, level, pos) -> false)
                            //?}
                            .isRedstoneConductor((state, level, pos) -> false)
                            .isValidSpawn((state, level, pos, entityType) -> false)
                            //? if >=26.3 {
                            /*.pushReaction(PushReaction.PUSH_PULL)
                            *///?} else {
                            .pushReaction(PushReaction.NORMAL)
                            //?}
                    ));
    public static final DeferredBlock<PearlFireBlock> PEARL_FIRE =
            BLOCKS.registerBlock("pearl_fire",
                    props -> new PearlFireBlock(props
                            .mapColor(MapColor.FIRE)
                            .replaceable()
                            //? if >=1.21.9 {
                            /*.noCollision()
                            *///?} else {
                            .noCollission()
                            //?}
                            .instabreak()
                            .lightLevel(state -> 15)
                            .sound(SoundType.WOOL)
                            //? if >=26.3 {
                            /*.pushReaction(PushReaction.POPPED)
                            *///?} else {
                            .pushReaction(PushReaction.DESTROY)
                            //?}
                            .noLootTable()));
    public static final DeferredBlock<PearlPortalBlock> PEARL_PORTAL =
            BLOCKS.registerBlock("pearl_portal",
                    props -> new PearlPortalBlock(props
                            .mapColor(MapColor.COLOR_PURPLE)
                            //? if >=1.21.9 {
                            /*.noCollision()
                            *///?} else {
                            .noCollission()
                            //?}
                            .noOcclusion()
                            .strength(-1.0F, 3600000.0F)
                            .lightLevel(state -> 11)
                            .sound(SoundType.GLASS)
                            //? if >=26.3 {
                            /*.pushReaction(PushReaction.IMMOVEABLE)
                            *///?} else {
                            .pushReaction(PushReaction.BLOCK)
                            //?}
                            .noLootTable()));
    public static final DeferredBlock<Block> MUSAVACCA_PLANKS =
            BLOCKS.registerBlock("musavacca_planks",
                    props -> new Block(props
                            .mapColor(MapColor.COLOR_BROWN)
                            .strength(2.0F, 3.0F)
                            .sound(ModWoodTypes.MUSAVACCA_BLOCK_SET.soundType())
                            //? if >=26.3 {
                            /*.pushReaction(PushReaction.PUSH_PULL)));
                            *///?} else {
                            .pushReaction(PushReaction.NORMAL)));
                            //?}
    public static final DeferredBlock<Block> GILDED_MUSAVACCA_PLANKS =
            BLOCKS.registerBlock("gilded_musavacca_planks",
                    props -> new Block(props
                            .mapColor(MapColor.COLOR_BROWN)
                            .strength(2.2F, 3.0F)
                            .sound(ModWoodTypes.MUSAVACCA_BLOCK_SET.soundType())
                            //? if >=26.3 {
                            /*.pushReaction(PushReaction.PUSH_PULL)));
                            *///?} else {
                            .pushReaction(PushReaction.NORMAL)));
                            //?}
    public static final DeferredBlock<StairBlock> MUSAVACCA_STAIRS =
            BLOCKS.registerBlock("musavacca_stairs",
                    props -> new StairBlock(
                            MUSAVACCA_PLANKS.get().defaultBlockState(),
                            props
                                    .mapColor(MapColor.COLOR_BROWN)
                                    .strength(2.0F, 3.0F)
                                    .sound(ModWoodTypes.MUSAVACCA_BLOCK_SET.soundType())
                                    //? if >=26.3 {
                                    /*.pushReaction(PushReaction.PUSH_PULL)
                                    *///?} else {
                                    .pushReaction(PushReaction.NORMAL)
                                    //?}
                    ));
    public static final DeferredBlock<SlabBlock> MUSAVACCA_SLAB =
            BLOCKS.registerBlock("musavacca_slab",
                    props -> new SlabBlock(props
                            .mapColor(MapColor.COLOR_BROWN)
                            .strength(2.0F, 3.0F)
                            .sound(ModWoodTypes.MUSAVACCA_BLOCK_SET.soundType())
                            //? if >=26.3 {
                            /*.pushReaction(PushReaction.PUSH_PULL)));
                            *///?} else {
                            .pushReaction(PushReaction.NORMAL)));
                            //?}
    public static final DeferredBlock<FenceBlock> MUSAVACCA_FENCE =
            BLOCKS.registerBlock("musavacca_fence",
                    props -> new FenceBlock(props
                            .mapColor(MapColor.COLOR_BROWN)
                            .strength(2.0F, 3.0F)
                            .sound(ModWoodTypes.MUSAVACCA_BLOCK_SET.soundType())
                            .forceSolidOn()
                            //? if >=26.3 {
                            /*.pushReaction(PushReaction.PUSH_PULL)));
                            *///?} else {
                            .pushReaction(PushReaction.NORMAL)));
                            //?}
    public static final DeferredBlock<FenceGateBlock> MUSAVACCA_FENCE_GATE =
            BLOCKS.registerBlock("musavacca_fence_gate",
                    props -> new FenceGateBlock(
                            ModWoodTypes.MUSAVACCA,
                            props
                                    .mapColor(MapColor.COLOR_BROWN)
                                    .strength(2.0F, 3.0F)
                                    .sound(ModWoodTypes.MUSAVACCA.soundType())
                                    .forceSolidOn()
                                    //? if >=26.3 {
                                    /*.pushReaction(PushReaction.PUSH_PULL)
                                    *///?} else {
                                    .pushReaction(PushReaction.NORMAL)
                                    //?}
                    ));
    public static final DeferredBlock<MusavaccaPortalDoorBlock> MUSAVACCA_DOOR =
            BLOCKS.registerBlock("musavacca_door",
                    props -> new MusavaccaPortalDoorBlock(
                            ModWoodTypes.MUSAVACCA_BLOCK_SET,
                            props
                                    .mapColor(MapColor.COLOR_BROWN)
                                    .strength(3.0F, 3.0F)
                                    .sound(ModWoodTypes.MUSAVACCA_BLOCK_SET.soundType())
                                    .noOcclusion()
                                    //? if >=26.3 {
                                    /*.pushReaction(PushReaction.POPPED)
                                    *///?} else {
                                    .pushReaction(PushReaction.DESTROY)
                                    //?}
                    ));
    public static final DeferredBlock<MusavaccaPortalTrapdoorBlock> MUSAVACCA_TRAPDOOR =
            BLOCKS.registerBlock("musavacca_trapdoor",
                    props -> new MusavaccaPortalTrapdoorBlock(
                            ModWoodTypes.MUSAVACCA_BLOCK_SET,
                            props
                                    .mapColor(MapColor.COLOR_BROWN)
                                    .strength(3.0F, 3.0F)
                                    .sound(ModWoodTypes.MUSAVACCA_BLOCK_SET.soundType())
                                    .noOcclusion()
                                    //? if >=26.3 {
                                    /*.pushReaction(PushReaction.POPPED)
                                    *///?} else {
                                    .pushReaction(PushReaction.DESTROY)
                                    //?}
                    ));
    public static final DeferredBlock<PressurePlateBlock> MUSAVACCA_PRESSURE_PLATE =
            BLOCKS.registerBlock("musavacca_pressure_plate",
                    props -> new PressurePlateBlock(
                            ModWoodTypes.MUSAVACCA_BLOCK_SET,
                            props
                                    .mapColor(MapColor.COLOR_BROWN)
                                    .strength(0.5F)
                                    .sound(ModWoodTypes.MUSAVACCA_BLOCK_SET.soundType())
                                    .forceSolidOn()
                                    //? if >=1.21.9 {
                                    /*.noCollision()
                                    *///?} else {
                                    .noCollission()
                                    //?}
                                    //? if >=26.3 {
                                    /*.pushReaction(PushReaction.POPPED)
                                    *///?} else {
                                    .pushReaction(PushReaction.DESTROY)
                                    //?}
                    ));
    public static final DeferredBlock<ButtonBlock> MUSAVACCA_BUTTON =
            BLOCKS.registerBlock("musavacca_button",
                    props -> new ButtonBlock(
                            ModWoodTypes.MUSAVACCA_BLOCK_SET,
                            30,
                            props
                                    .mapColor(MapColor.COLOR_BROWN)
                                    .strength(0.5F)
                                    .sound(ModWoodTypes.MUSAVACCA_BLOCK_SET.soundType())
                                    //? if >=1.21.9 {
                                    /*.noCollision()
                                    *///?} else {
                                    .noCollission()
                                    //?}
                                    //? if >=26.3 {
                                    /*.pushReaction(PushReaction.POPPED)
                                    *///?} else {
                                    .pushReaction(PushReaction.DESTROY)
                                    //?}
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
                        //? if >=26.3 {
                        /*.isViewBlocking((s, l, pos, bounds) -> false)
                        .pushReaction(PushReaction.POPPED);
                        *///?} else {
                        .isViewBlocking((s, l, pos) -> false)
                        .pushReaction(PushReaction.DESTROY);
                        //?}
                return new MusavaccaLeaves(0.0F, p);
            });
    private static DeferredBlock<MusavaccaCropBlock> musavaccaCropStage(String name, int age) {
        return BLOCKS.registerBlock(name,
                props -> new MusavaccaCropBlock(
                        age,
                        props.mapColor(MapColor.PLANT)
                                //? if >=1.21.9 {
                                /*.noCollision()
                                *///?} else {
                                .noCollission()
                                //?}
                                .randomTicks()
                                .instabreak()
                                .sound(SoundType.CROP)
                                //? if >=26.3 {
                                /*.pushReaction(PushReaction.POPPED)
                                *///?} else {
                                .pushReaction(PushReaction.DESTROY)
                                //?}
                                .noLootTable()
                )
        );
    }

    public static final DeferredBlock<MusavaccaCropBlock> MUSAVACCA_SPROUT = musavaccaCropStage("musavacca_sprout", 0);
    public static final DeferredBlock<MusavaccaCropBlock> MUSAVACCA_SUCKER = musavaccaCropStage("musavacca_sucker", 1);
    public static final DeferredBlock<MusavaccaCropBlock> MUSAVACCA_PLANT = musavaccaCropStage("musavacca_plant", 2);
    public static final DeferredBlock<MusavaccaSaplingBlock> MUSAVACCA_PSEUDOSTEM =
            BLOCKS.registerBlock("musavacca_pseudostem",
                    props -> new MusavaccaSaplingBlock(
                            props.mapColor(MapColor.PLANT)
                                    //? if >=1.21.9 {
                                    /*.noCollision()
                                    *///?} else {
                                    .noCollission()
                                    //?}
                                    .randomTicks()
                                    .instabreak()
                                    .sound(SoundType.GRASS)
                                    //? if >=26.3 {
                                    /*.pushReaction(PushReaction.POPPED)
                                    *///?} else {
                                    .pushReaction(PushReaction.DESTROY)
                                    //?}
                                    .noLootTable()
                    ));
    public static final DeferredBlock<DecorationBlock> BANANA_PEARL_CHALICE =
            BLOCKS.registerBlock("banana_pearl_chalice",
                    props -> new DecorationBlock(
                            props.mapColor(MapColor.COLOR_YELLOW)
                                    .strength(0.2F, 3.0F)
                                    .sound(SoundType.CHAIN)
                                    .noOcclusion()
                                    //? if >=26.3 {
                                    /*.pushReaction(PushReaction.POPPED),
                                    *///?} else {
                                    .pushReaction(PushReaction.DESTROY),
                                    //?}
                            DecorationBlock.Options.builder()
                                    .floorRotating()
                                    .sneakRotating()
                                    .side()
                                    .shapes(DecorationBlock.ShapeSet.of(Block.box(4.0D, 0.0D, 4.0D, 12.0D, 12.0D, 12.0D),
                                            Block.box(1.75D, 0.0D, 2.0D, 14.25D, 7.0D, 14.0D), Block.box(4.5D, 0.5D, 2.25D, 11.5D, 12.5D, 9.25D),
                                            Block.box(4.0D, 4.0D, 4.0D, 12.0D, 16.0D, 12.0D)))
                                    .build()
                    ));
    public static final DeferredBlock<VocoTableBlock> VOCO_TABLE =
            BLOCKS.registerBlock("voco_table",
                    props -> new VocoTableBlock(props
                            .mapColor(MapColor.COLOR_BROWN)
                            .strength(2.0F, 3.0F)
                            .sound(SoundType.WOOD)
                            .noOcclusion()
                            .lightLevel(state -> state.getValue(VocoTableBlock.CANDLE_LIGHT) * 3)
                            //? if >=26.3 {
                            /*.pushReaction(PushReaction.POPPED)));
                            *///?} else {
                            .pushReaction(PushReaction.DESTROY)));
                            //?}
    public static final DeferredBlock<VocoPostBlock> VOCO_POST =
            BLOCKS.registerBlock("voco_post",
                    props -> new VocoPostBlock(props
                            .mapColor(MapColor.COLOR_BROWN)
                            .strength(2.0F, 3.0F)
                            .sound(SoundType.WOOD)
                            .noOcclusion()
                            //? if >=26.3 {
                            /*.pushReaction(PushReaction.POPPED)));
                            *///?} else {
                            .pushReaction(PushReaction.DESTROY)));
                            //?}
    public static final DeferredBlock<HexBlock> HEX_BLOCK =
            BLOCKS.registerBlock("hex_block",
                    props -> new HexBlock(props
                            .mapColor(MapColor.PLANT)
                            .strength(0.2F)
                            .randomTicks()
                            .sound(SoundType.GRASS)
                            .noOcclusion()
                            //? if >=26.3 {
                            /*.pushReaction(PushReaction.PUSH_PULL)));
                            *///?} else {
                            .pushReaction(PushReaction.NORMAL)));
                            //?}
    public static final DeferredBlock<HardHexBlock> HARD_HEX_BLOCK =
            BLOCKS.registerBlock("hard_hex_block",
                    props -> new HardHexBlock(props
                            .mapColor(MapColor.COLOR_ORANGE)
                            .strength(0.1F, 6.0F)
                            .sound(SoundType.HONEY_BLOCK)
                            .noOcclusion()
                            //? if >=26.3 {
                            /*.pushReaction(PushReaction.PUSH_PULL)));
                            *///?} else {
                            .pushReaction(PushReaction.NORMAL)));
                            //?}
    public static final DeferredBlock<StairBlock> BANANA_PEARL_STAIRS =
            BLOCKS.registerBlock("banana_pearl_stairs",
                    props -> new BananaPearlStairBlock(
                            BANANA_PEARL_BLOCK.get().defaultBlockState(),
                            props
                                    .mapColor(MapColor.COLOR_YELLOW)
                                    .strength(3.0F, 6.0F)
                                    .sound(SoundType.COPPER)
                                    .requiresCorrectToolForDrops()
                                    //? if >=26.3 {
                                    /*.pushReaction(PushReaction.PUSH_PULL)
                                    *///?} else {
                                    .pushReaction(PushReaction.NORMAL)
                                    //?}
                    ));
    public static final DeferredBlock<StairBlock> BANANA_PEARL_BRICK_STAIRS =
            BLOCKS.registerBlock("banana_pearl_brick_stairs",
                    props -> new BananaPearlStairBlock(
                            BANANA_PEARL_BRICKS.get().defaultBlockState(),
                            props
                                    .mapColor(MapColor.COLOR_YELLOW)
                                    .strength(3.0F, 6.0F)
                                    .sound(SoundType.COPPER)
                                    .requiresCorrectToolForDrops()
                                    //? if >=26.3 {
                                    /*.pushReaction(PushReaction.PUSH_PULL)
                                    *///?} else {
                                    .pushReaction(PushReaction.NORMAL)
                                    //?}
                    ));
    public static final DeferredBlock<PearlCandleBlock> PEARL_CANDLE = BLOCKS.registerBlock("pearl_candle",
            props -> new PearlCandleBlock(props.mapColor(MapColor.WOOL).strength(0.1F).sound(SoundType.CANDLE)
                    .noOcclusion().dynamicShape().lightLevel(PearlCandleBlock::candleLightLevel)
                    //? if >=26.3 {
                    /*.pushReaction(PushReaction.POPPED)
                    *///?} else {
                    .pushReaction(PushReaction.DESTROY)
                    //?}
            ));
    public static final DeferredBlock<CaroteneGrassBlock> CAROTENE_GRASS =
            BLOCKS.registerBlock("carotene_grass",
                    props -> new CaroteneGrassBlock(
                            props.mapColor(MapColor.COLOR_ORANGE)
                                    .strength(0.6F)
                                    .sound(SoundType.GRASS)
                                    .randomTicks()
                                    //? if >=26.3 {
                                    /*.pushReaction(PushReaction.PUSH_PULL)
                                    *///?} else {
                                    .pushReaction(PushReaction.NORMAL)
                                    //?}
                    ));
    public static final DeferredBlock<CaroteneShortGrassBlock> CAROTENE_SHORT_GRASS =
            BLOCKS.registerBlock("carotene_short_grass",
                    props -> new CaroteneShortGrassBlock(
                            props.mapColor(MapColor.COLOR_ORANGE)
                                    .replaceable()
                                    //? if >=1.21.9 {
                                    /*.noCollision()
                                    *///?} else {
                                    .noCollission()
                                    //?}
                                    .instabreak()
                                    .sound(SoundType.GRASS)
                                    .offsetType(BlockBehaviour.OffsetType.XZ)
                                    //? if >=26.3 {
                                    /*.pushReaction(PushReaction.POPPED)
                                    *///?} else {
                                    .pushReaction(PushReaction.DESTROY)
                                    //?}
                    ));
    public static final DeferredBlock<DoublePlantBlock> CAROTENE_TALL_GRASS =
            BLOCKS.registerBlock("carotene_tall_grass",
                    props -> new DoublePlantBlock(
                            props.mapColor(MapColor.COLOR_ORANGE)
                                    .replaceable()
                                    //? if >=1.21.9 {
                                    /*.noCollision()
                                    *///?} else {
                                    .noCollission()
                                    //?}
                                    .instabreak()
                                    .sound(SoundType.GRASS)
                                    .offsetType(BlockBehaviour.OffsetType.XZ)
                                    //? if >=26.3 {
                                    /*.pushReaction(PushReaction.POPPED)
                                    *///?} else {
                                    .pushReaction(PushReaction.DESTROY)
                                    //?}
                    ));
    private static final Set<DeferredBlock<? extends Block>> SKIP_BLOCK_ITEMS = Set.of(PEARL_FIRE, MUSAVACCA_EGG, MUSAVACCA_SPROUT, MUSAVACCA_SUCKER,
            MUSAVACCA_PLANT, MUSAVACCA_PSEUDOSTEM, PEARL_PORTAL, MUSAVACCA_DOOR, VOCO_TABLE);
    static {
        BLOCKS.getEntries().forEach(entry -> {
            if (!SKIP_BLOCK_ITEMS.contains(entry) && entry != PEARL_CANDLE) {
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
