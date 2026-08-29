package space.anatomyuniverse.musavacca.basuke.craft;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;
import space.anatomyuniverse.musavacca.block.ModBlocks;
import space.anatomyuniverse.musavacca.basuke.eating.VocoTableEatingLogic;
import space.anatomyuniverse.musavacca.component.ModDataComponents;
import space.anatomyuniverse.musavacca.item.ModItems;

import java.util.List;

public final class VocoTableCraftingRecipes {
    public static final int DEFAULT_EATING_TIME_TICKS =
            VocoTableEatingLogic.DEFAULT_EATING_TIME_TICKS;

    private static final List<VocoTableCraftingRecipe> RECIPES = List.of(
            recipe(
                    ModItems.FRACTURED_POTASSIUM_UPGRADE_SMITHING_TEMPLATE,
                    ModItems.POTASSIUM_INGOT.get(),
                    ModItems.POTASSIUM_UPGRADE_SMITHING_TEMPLATE.get()
            ),
            recipe(
                    ModItems.FRACTURED_POTASSIUM_UPGRADE_SMITHING_TEMPLATE,
                    ModItems.POTASSIUM_INGOT.get(),
                    ModItems.IMBUED_POTASSIUM_UPGRADE_SMITHING_TEMPLATE.get(),
                    true
            ),
            recipe(
                    ModItems.POTASSIUM_UPGRADE_SMITHING_TEMPLATE,
                    ModItems.POTASSIUM_INGOT.get(),
                    ModItems.IMBUED_POTASSIUM_UPGRADE_SMITHING_TEMPLATE.get(),
                    true
            ),

            recipe(
                    ModItems.MUSAVACCA_DOOR,
                    ModItems.BANANA_PEARL.get(),
                    ModItems.MUSAVACCA_IMBUED_DOOR.get(),
                    true
            ),
            recipe(
                    ModBlocks.MUSAVACCA_TRAPDOOR.get(),
                    ModItems.BANANA_PEARL.get(),
                    ModBlocks.MUSAVACCA_TRAPDOOR.get(),
                    true
            ),
            recipe(
                    ModItems.IMBUED_POTASSIUM_UPGRADE_SMITHING_TEMPLATE,
                    ModItems.POTASSIUM_INGOT.get(),
                    ModItems.IMBUED_POTASSIUM_UPGRADE_SMITHING_TEMPLATE.get(),
                    true
            ),

            recipe(
                    Items.NAME_TAG,
                    ModItems.SMALL_BANANA_PEARL.get(),
                    ModItems.SIM_CARD.get()
            )
    );

    private VocoTableCraftingRecipes() {}

    public static List<VocoTableCraftingRecipe> recipes() {
        return RECIPES;
    }

    @Nullable
    public static VocoTableCraftingRecipe findMatchingRecipe(
            ItemStack displayedStack,
            ItemStack edibleStack
    ) {
        return findMatchingRecipe(
                displayedStack,
                edibleStack,
                false
        );
    }

    @Nullable
    public static VocoTableCraftingRecipe findMatchingRecipe(
            ItemStack displayedStack,
            ItemStack edibleStack,
            boolean matchingCandleColorAvailable
    ) {
        if (
                displayedStack.is(
                        ModBlocks.MUSAVACCA_TRAPDOOR.get().asItem()
                )
                        && displayedStack.get(
                        ModDataComponents.HEX_COLOR.get()
                ) != null
        ) {
            return null;
        }

        VocoTableCraftingRecipe normalFallback = null;

        for (VocoTableCraftingRecipe recipe : RECIPES) {
            if (!recipe.matches(displayedStack, edibleStack)) {
                continue;
            }

            if (recipe.hexColorInject()) {
                if (matchingCandleColorAvailable) {
                    return recipe;
                }

                continue;
            }

            if (normalFallback == null) {
                normalFallback = recipe;
            }
        }

        return normalFallback;
    }

    private static VocoTableCraftingRecipe recipe(
            ItemLike display,
            ItemLike edible,
            ItemLike result
    ) {
        return recipe(
                display,
                edible,
                result,
                false
        );
    }

    private static VocoTableCraftingRecipe recipe(
            ItemLike display,
            ItemLike edible,
            ItemLike result,
            boolean hexColorInject
    ) {
        return new VocoTableCraftingRecipe(
                display,
                edible,
                result,
                DEFAULT_EATING_TIME_TICKS,
                hexColorInject
        );
    }
}
