package space.anatomyuniverse.musavacca.block;


import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
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
                            .mapColor(MapColor.COLOR_ORANGE)
                            .strength(0.1F, 6.0F)
                            .sound(SoundType.HONEY_BLOCK)
                            .noOcclusion()
                            .requiresCorrectToolForDrops()
                            .pushReaction(PushReaction.NORMAL)));

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

    public static final DeferredBlock<StrippableLogBlock> MUSAVACCA_STEM =
            BLOCKS.registerBlock("musavacca_stem",
                    props -> new StrippableLogBlock(props
                            .mapColor(MapColor.COLOR_ORANGE)
                            .strength(3.0F, 6.0F)
                            .sound(SoundType.WOOD)
                            .requiresCorrectToolForDrops()
                            .pushReaction(PushReaction.NORMAL),
                            STRIPPED_MUSAVACCA_STEM));

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


//    public static final DeferredBlock<SlabBlock> MUSAVACCA_SLAB =
//            BLOCKS.registerBlock("musavacca_slab",
//                    props -> new SlabBlock(props
//                            .mapColor(MapColor.COLOR_ORANGE)
//                            .strength(3.0F, 6.0F)
//                            .sound(SoundType.COPPER)
//                            .requiresCorrectToolForDrops()
//                            .pushReaction(PushReaction.NORMAL)));

    public static final DeferredBlock<Block> MUSAVACCA_PLANKS =
            BLOCKS.registerBlock("musavacca_planks",
                    props -> new Block(props
                            .mapColor(MapColor.COLOR_ORANGE)
                            .strength(3.0F, 6.0F)
                            .sound(SoundType.COPPER)
                            .requiresCorrectToolForDrops()
                            .pushReaction(PushReaction.NORMAL)));

//    public static final DeferredBlock<StairBlock> MUSAVACCA_STAIRS =
//            BLOCKS.registerBlock("musavacca_stairs",
//                    props -> new StairBlock(
//                            MUSAVACCA_PLANKS.get().defaultBlockState(),
//                            props.mapColor(MapColor.COLOR_ORANGE)
//                                    .strength(3.0F, 6.0F)
//                                    .sound(SoundType.COPPER)
//                                    .requiresCorrectToolForDrops()
//                                    .pushReaction(PushReaction.NORMAL)
//                    ));
//
//    public static final DeferredBlock<FenceBlock> MUSAVACCA_FENCE =
//            BLOCKS.registerBlock("musavacca_fence",
//                    props -> new FenceBlock(props
//                            .mapColor(MapColor.COLOR_ORANGE)
//                            .strength(3.0F, 6.0F)
//                            .sound(SoundType.COPPER)
//                            .requiresCorrectToolForDrops()
//                            .pushReaction(PushReaction.NORMAL)));
//
//    public static final DeferredBlock<WallBlock> MUSAVACCA_WALL =
//            BLOCKS.registerBlock("musavacca_wall",
//                    props -> new WallBlock(props
//                            .mapColor(MapColor.COLOR_ORANGE)
//                            .strength(3.0F, 6.0F)
//                            .sound(SoundType.COPPER)
//                            .requiresCorrectToolForDrops()
//                            .pushReaction(PushReaction.NORMAL)));

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

    public static final DeferredBlock<BananaPearlChaliceBlock> BANANA_PEARL_CHALICE =
            BLOCKS.registerBlock("banana_pearl_chalice",
                    props -> new BananaPearlChaliceBlock(
                            props.mapColor(MapColor.COLOR_ORANGE)
                                    .strength(0.2F, 3.0F)
                                    .sound(SoundType.CHAIN)
                                    .noOcclusion()
                                    .pushReaction(PushReaction.DESTROY)
                    ));



    public static final DeferredBlock<HexBlock> HEX_BLOCK =
            BLOCKS.registerBlock("hex_block",
                    props -> new HexBlock(props
                            .mapColor(MapColor.COLOR_ORANGE)
                            .strength(0.1F, 6.0F)
                            .sound(SoundType.HONEY_BLOCK)
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


    private static final Set<DeferredBlock<? extends Block>> SKIP_BLOCK_ITEMS = Set.of(
//            HEX_BLOCK
    );

    static {
        BLOCKS.getEntries().forEach(entry -> {
            if (!SKIP_BLOCK_ITEMS.contains(entry)) {
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
