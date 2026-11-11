package space.anatomyuniverse.musavacca.basuke.craft;

import java.util.List;
import java.util.Set;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.anatomyuniverse.musavacca.basuke.eating.VocoTableEatingLogic;
import space.anatomyuniverse.musavacca.block.ModBlocks;
import space.anatomyuniverse.musavacca.component.ModDataComponents;
import space.anatomyuniverse.musavacca.item.ModItems;

public final class VocoTableCraftingRecipes {
    public static final int DEFAULT_EATING_TIME_TICKS = VocoTableEatingLogic.DEFAULT_EATING_TIME_TICKS;
    private static final Set<String> WOOD_SHAPES = Set.of("stairs", "slab", "fence", "fence_gate", "door", "trapdoor", "button", "pressure_plate",
            "sign", "hanging_sign", "mosaic", "mosaic_stairs", "mosaic_slab");
    private static final List<Recipe> RECIPES = List.of(
            recipe(ModItems.FRACTURED_POTASSIUM_UPGRADE_SMITHING_TEMPLATE, ModItems.POTASSIUM_INGOT.get(), ModItems.POTASSIUM_UPGRADE_SMITHING_TEMPLATE.get()),
            recipe(ModItems.MUSAVACCA_DOOR, ModBlocks.HEX_BLOCK.get(), ModItems.MUSAVACCA_IMBUED_DOOR.get(), true),
            recipe(ModItems.MUSAVACCA_IMBUED_DOOR, ModBlocks.HEX_BLOCK.get(), ModItems.MUSAVACCA_IMBUED_DOOR.get(), true),
            recipe(ModBlocks.MUSAVACCA_TRAPDOOR.get(), ModBlocks.HEX_BLOCK.get(), ModBlocks.MUSAVACCA_TRAPDOOR.get(), true),
            recipe(ModItems.SIM_CARD.get(), ModBlocks.HEX_BLOCK.get(), ModItems.SIM_CARD.get(), true));

    private VocoTableCraftingRecipes() {}

    @Nullable
    public static Recipe findMatchingRecipe(ServerLevel level, ItemStack displayedStack, ItemStack edibleStack) {
        if (displayedStack.isEmpty() || edibleStack.isEmpty()) return null;
        for (Recipe recipe : RECIPES) {
            if (!recipe.matches(displayedStack, edibleStack)) continue;
            if (!recipe.hexColorInject() || edibleStack.get(ModDataComponents.HEX_COLOR.get()) != null) return recipe;
        }
        if (!ItemStack.isSameItemSameComponents(displayedStack, new ItemStack(displayedStack.getItem()))) return null;
        SingleRecipeInput input = new SingleRecipeInput(displayedStack.copyWithCount(1));
        //? if >=1.21.2 {
        for (RecipeHolder<?> holder : level.recipeAccess().getRecipes()) {
        //?} else {
        /*for (RecipeHolder<?> holder : level.getRecipeManager().getRecipes()) {
        *///?}
            if (!(holder.value() instanceof StonecutterRecipe stonecutting) || !stonecutting.matches(input, level)) continue;
            //? if >=26.1 {
            /*ItemStack result = stonecutting.assemble(input);
            *///?} else {
            ItemStack result = stonecutting.assemble(input, level.registryAccess());
            //?}
            if (result.isEmpty() || !result.is(edibleStack.getItem()) || result.is(displayedStack.getItem())) continue;
            if (!ItemStack.isSameItemSameComponents(result, new ItemStack(result.getItem(), result.getCount()))) continue;
            return new Recipe(displayedStack.getItem(), edibleStack.getItem(), result.getItem(), result.getCount(), false);
        }
        var source = BuiltInRegistries.ITEM.getKey(displayedStack.getItem());
        var target = BuiltInRegistries.ITEM.getKey(edibleStack.getItem());
        if (!source.getNamespace().equals(target.getNamespace()) || !source.getPath().endsWith("_planks")) return null;
        String wood = source.getPath().substring(0, source.getPath().length() - "_planks".length());
        String prefix = wood + "_";
        if (!target.getPath().startsWith(prefix)) return null;
        String shape = target.getPath().substring(prefix.length());
        if (!WOOD_SHAPES.contains(shape)) return null;
        return new Recipe(displayedStack.getItem(), edibleStack.getItem(), edibleStack.getItem(), shape.endsWith("slab") ? 2 : 1, false);
    }

    private static Recipe recipe(ItemLike display, ItemLike edible, ItemLike result) {
        return recipe(display, edible, result, false);
    }

    private static Recipe recipe(ItemLike display, ItemLike edible, ItemLike result, boolean hexColorInject) {
        return new Recipe(display, edible, result, 1, hexColorInject);
    }

    public record Recipe(@NotNull ItemLike display, @NotNull ItemLike edible, @NotNull ItemLike result, int resultCount,
            boolean hexColorInject) {
        public Recipe {
            if (resultCount <= 0) throw new IllegalArgumentException("Voco table crafting result count must be above 0.");
        }

        public boolean matches(@NotNull ItemStack displayedStack, @NotNull ItemStack edibleStack) {
            return displayedStack.is(this.display.asItem()) && edibleStack.is(this.edible.asItem());
        }

        public ItemStack createResultStack(ItemStack displayedStack) {
            return displayedStack.is(this.result.asItem()) ? displayedStack.copyWithCount(1) : new ItemStack(this.result.asItem());
        }
    }
}
