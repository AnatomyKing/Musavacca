
package space.anatomyuniverse.musavacca.data.models;

import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import space.anatomyuniverse.musavacca.block.ModBlocks;
import space.anatomyuniverse.musavacca.block.entity.custom.HardHexBlockEntity;
import space.anatomyuniverse.musavacca.data.models.block.*;
import space.anatomyuniverse.musavacca.item.ModItems;

import java.util.Map;

public final class ModelSets {
    private ModelSets() {}

    public static Block[] cubeAllBlocks() {
        return new Block[] {
                ModBlocks.BANANA_PEARL_BLOCK.get(),
                ModBlocks.BANANA_PEARL_BRICKS.get(),
                ModBlocks.MUSAVACCA_PLANKS.get()
        };
    }

    public static Map<Block, BreakBlockOwn.AgeModels> breakBlockOwnModels() {
        return Map.of(
                ModBlocks.MUSAVACCA_EGG.get(),
                new BreakBlockOwn.AgeModels(
                        "musavacca:block/musavacca_egg/musavacca_egg_stage0",
                        "musavacca:block/musavacca_egg/musavacca_egg_stage1",
                        "musavacca:block/musavacca_egg/musavacca_egg_stage2",
                        "musavacca:block/musavacca_egg/musavacca_egg_attached_stage0",
                        "musavacca:block/musavacca_egg/musavacca_egg_attached_stage1",
                        "musavacca:block/musavacca_egg/musavacca_egg_attached_stage2"
                )
        );
    }

    public static Map<Block, String> cubeOwnModels() {
        return Map.of(
        );
    }


    public static Map<Block, CubeOwnTintedHexColorClipped.Entry> cubeOwnTintedHexColorClippedModels() {
        return Map.of(
                ModBlocks.HEX_BLOCK.get(),
                CubeOwnTintedHexColorClipped.Entry.dynamic(
                        "musavacca:block/lopha_blossom",
                        "musavacca:block/clipped_lopha_blossom"
                )
        );
    }

    public static Map<Block, CubeOwnTintedHexColor.Entry> cubeOwnTintedHexColorModels() {
        return Map.of(
//                ModBlocks.HEX_BLOCK.get(),
//                CubeOwnTintedHexColor.Entry.dynamic("musavacca:block/lopha_blossom"),

                ModBlocks.HARD_HEX_BLOCK.get(),
                CubeOwnTintedHexColor.Entry.constant(
                        "musavacca:block/hex_block",
                        HardHexBlockEntity.HARD_HEX_COLOR
                )
        );
    }

    public static Block[] bananaPearlChaliceBlocks() {
        return new Block[] {
                ModBlocks.BANANA_PEARL_CHALICE.get()
        };
    }

    public static Block[] smallBananaPearlBlocks() {
        return new Block[] {
                ModBlocks.SMALL_BANANA_PEARL_BLOCK.get()
        };
    }

    public static Map<Block, BarrelCropOwnTintedFoliage.AgeModels> barrelCropOwnTintedFoliageModels() {
        return Map.of(
                ModBlocks.MUSAVACCA_LEAVES.get(),
                new BarrelCropOwnTintedFoliage.AgeModels(
                        "musavacca:block/musavacca_leaves",
                        "musavacca:block/musavacca_leaves_crown",
                        "musavacca:block/musavacca_leaves_cross"
                )
        );
    }

    public static Map<Block, CubeFireBlock.Entry> cubeFireBlockModels() {
        return Map.of(
                ModBlocks.PEARL_FIRE.get(),
                CubeFireBlock.Entry.auto(
                        "pearl_fire",
                        "musavacca:block/custom_parent/tinted_template_fire_up",
                        "musavacca:block/custom_parent/tinted_template_fire_side",
                        "musavacca:block/custom_parent/tinted_template_fire_side_alt",
                        "musavacca:block/custom_parent/tinted_template_fire_floor",
                        "musavacca:block/custom_parent/tinted_template_fire_up_alt"
                )
        );
    }

    /*
    public static Map<Block, String> cubeOwnTintedFoliageModels() {
        return Map.of(
                // ModBlocks.SOME_LEAVES.get(), "musavacca:block/some_leaves"
        );
    }
    */

    public static Block[] chapiterBlocks() {
        return new Block[] {
                ModBlocks.BANANA_PEARL_CHAPITER.get()
        };
    }

    public static Block[] logBlocks() {
        return new Block[] {
                ModBlocks.BANANA_PEARL_PILLAR.get(),
                ModBlocks.MUSAVACCA_STEM.get(),
                ModBlocks.STRIPPED_MUSAVACCA_STEM.get()
        };
    }

    public static Block[] stairsBlocks() {
        return new Block[] {
        };
    }

    /** Crafter-like cube blocks (6 textures: _bottom/_top/_north/_south/_west/_east). */
    public static Block[] cubeCrafterLikeBlocks() {
        return new Block[] {
        };
    }

    /** Back-compat alias so old Musavacca provider files do not instantly break while you switch over. */
    public static Block[] CubeCrafterLikeBlocks() {
        return cubeCrafterLikeBlocks();
    }

    /** Non-block items that need item/generated models. */
    public static ItemLike[] flatItems() {
        return new ItemLike[] {
                ModItems.BANANA_PEARL.get(),
                ModItems.BIG_BANANA_PEARL.get(),
                ModItems.SMALL_BANANA_PEARL.get(),
                ModItems.FLINT_AND_PEARL.get(),
                ModItems.BANANA.get(),
                ModItems.ITEM_INTERACT.get(),
                ModBlocks.UNRIPE_MUSAVACCA_EGG.get(),
                ModBlocks.RIPENING_MUSAVACCA_EGG.get(),
                ModBlocks.RIPE_MUSAVACCA_EGG.get(),

        };
    }

    /** Non-block items that need item/handheld models. */
    public static ItemLike[] handheldItems() {
        return new ItemLike[] {
        };
    }
}