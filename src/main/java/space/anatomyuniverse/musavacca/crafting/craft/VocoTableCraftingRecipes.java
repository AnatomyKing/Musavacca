package space.anatomyuniverse.musavacca.crafting.craft;

import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;
import space.anatomyuniverse.musavacca.block.ModBlocks;
import space.anatomyuniverse.musavacca.entity.mob.basuke.Basuke;
import space.anatomyuniverse.musavacca.item.ModItems;

import java.util.List;

public final class VocoTableCraftingRecipes {
    public static final int DEFAULT_EATING_TIME_TICKS = Basuke.DEFAULT_CRAFTING_EATING_TICKS;

    private static final List<VocoTableCraftingRecipe> RECIPES = List.of(
            recipe(Items.DIAMOND_SWORD, ModItems.POTASSIUM_INGOT.get(), ModItems.POTASSIUM_SWORD.get(), 2),
            recipe(Items.DIAMOND_PICKAXE, ModItems.POTASSIUM_INGOT.get(), ModItems.POTASSIUM_PICKAXE.get(), 2),
            recipe(Items.DIAMOND_SHOVEL, ModItems.POTASSIUM_INGOT.get(), ModItems.POTASSIUM_SHOVEL.get(), 2),
            recipe(Items.DIAMOND_HOE, ModItems.POTASSIUM_INGOT.get(), ModItems.POTASSIUM_HOE.get(), 2),
            recipe(Items.DIAMOND_AXE, ModItems.POTASSIUM_INGOT.get(), ModItems.POTASSIUM_AXE.get(), 2),

            recipe(Items.DIAMOND_HELMET, ModItems.POTASSIUM_INGOT.get(), ModItems.POTASSIUM_HELMET.get(), 2),
            recipe(Items.DIAMOND_CHESTPLATE, ModItems.POTASSIUM_INGOT.get(), ModItems.POTASSIUM_CHESTPLATE.get(), 2),
            recipe(Items.DIAMOND_LEGGINGS, ModItems.POTASSIUM_INGOT.get(), ModItems.POTASSIUM_LEGGINGS.get(), 2),
            recipe(Items.DIAMOND_BOOTS, ModItems.POTASSIUM_INGOT.get(), ModItems.POTASSIUM_BOOTS.get(), 2),

            /*
             * hexColorInject = true
             *
             * If all 4 Voco candle/receptor corners have the same lit color,
             * that color gets injected into the result stack as ModDataComponents.HEX_COLOR.
             *
             * The item does not need to support tinting for this to be safe.
             * If the item model ignores HEX_COLOR, nothing visual happens.
             */
            recipe(
                    ModItems.FRACTURED_POTASSIUM_UPGRADE_SMITHING_TEMPLATE,
                    ModItems.POTASSIUM_INGOT.get(),
                    ModItems.POTASSIUM_UPGRADE_SMITHING_TEMPLATE.get(),
                    2
            ),
            recipe(
                    ModItems.POTASSIUM_UPGRADE_SMITHING_TEMPLATE,
                    ModItems.POTASSIUM_INGOT.get(),
                    ModItems.IMBUED_POTASSIUM_UPGRADE_SMITHING_TEMPLATE.get(),
                    1,
                    true
            ),

            recipe(
                    ModBlocks.MUSAVACCA_DOOR,
                    ModItems.BANANA_PEARL.get(),
                    ModBlocks.MUSAVACCA_PORTAL_DOOR.get(),
                    1,
                    true
            ),

            recipe(
                    ModItems.IMBUED_POTASSIUM_UPGRADE_SMITHING_TEMPLATE,
                    ModItems.POTASSIUM_INGOT.get(),
                    ModItems.IMBUED_POTASSIUM_UPGRADE_SMITHING_TEMPLATE.get(),
                    1,
                    true
            ),

            recipe(Items.NAME_TAG, ModItems.SMALL_BANANA_PEARL.get(), ModItems.SIM_CARD.get(), 1)
    );

    private VocoTableCraftingRecipes() {}

    public static List<VocoTableCraftingRecipe> recipes() {
        return RECIPES;
    }

    @Nullable
    public static VocoTableCraftingRecipe findMatchingRecipe(
            net.minecraft.world.item.ItemStack displayedStack,
            net.minecraft.world.item.ItemStack edibleStack
    ) {
        for (VocoTableCraftingRecipe recipe : RECIPES) {
            if (recipe.matches(displayedStack, edibleStack)) {
                return recipe;
            }
        }

        return null;
    }

    private static VocoTableCraftingRecipe recipe(
            ItemLike display,
            ItemLike edible,
            ItemLike result,
            int litReceptorCost
    ) {
        return recipe(display, edible, result, litReceptorCost, false);
    }

    private static VocoTableCraftingRecipe recipe(
            ItemLike display,
            ItemLike edible,
            ItemLike result,
            int litReceptorCost,
            boolean hexColorInject
    ) {
        return new VocoTableCraftingRecipe(
                display,
                edible,
                result,
                DEFAULT_EATING_TIME_TICKS,
                litReceptorCost,
                hexColorInject
        );
    }
}