package space.anatomyuniverse.musavacca.basuke.craft;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;

public record VocoTableCraftingRecipe(
        @NotNull ItemLike display,
        @NotNull ItemLike edible,
        @NotNull ItemLike result,
        int eatingTimeTicks,
        boolean hexColorInject
) {
    public VocoTableCraftingRecipe {
        if (eatingTimeTicks <= 0) {
            throw new IllegalArgumentException(
                    "Voco table crafting eating time must be above 0."
            );
        }
    }

    public boolean matches(
            @NotNull ItemStack displayedStack,
            @NotNull ItemStack edibleStack
    ) {
        return displayedStack.is(this.display.asItem())
                && edibleStack.is(this.edible.asItem());
    }

    public ItemStack createResultStack() {
        return new ItemStack(this.result.asItem());
    }
}

