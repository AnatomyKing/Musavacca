package space.anatomyuniverse.musavacca.data.recipes.dsl;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import space.anatomyuniverse.musavacca.data.recipes.RecipeDSL;

import java.util.ArrayList;
import java.util.List;

public final class ShapelessCrafting {
    private final RecipeDSL dsl;
    private final ShapelessRecipeBuilder builder;
    private final List<RecipeDSL.ExtendedIngredient> extendedIngredients = new ArrayList<>();

    public ShapelessCrafting(
            RecipeDSL dsl,
            RecipeCategory category,
            ItemLike result,
            int count
    ) {
        this.dsl = dsl;

        //? if <1.21.3 {
        /*this.builder = ShapelessRecipeBuilder.shapeless(category, result, count);
        *///?} else {
        this.builder = ShapelessRecipeBuilder.shapeless(
                dsl.items(),
                category,
                new ItemStack(result.asItem(), count)
        );
        //?}
    }

    public ShapelessCrafting requires(Object... ingredients) {
        for (Object ingredient : ingredients) {
            addOnce(ingredient);
        }

        return this;
    }

    public ShapelessCrafting requiresCount(Object ingredient, int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("count must be >= 1");
        }

        if (count > 9) {
            throw new IllegalArgumentException("shapeless max grid size is 9 (got " + count + ")");
        }

        for (int i = 0; i < count; ++i) {
            addOnce(ingredient);
        }

        return this;
    }

    public ShapelessCrafting requiresPairs(Object... pairs) {
        if ((pairs.length & 1) != 0) {
            throw new IllegalArgumentException("requiresPairs expects even number of args: (ingredient, count)*");
        }

        for (int i = 0; i < pairs.length; i += 2) {
            if (!(pairs[i + 1] instanceof Number count)) {
                throw new IllegalArgumentException("Count must be a Number at index " + (i + 1));
            }

            requiresCount(pairs[i], count.intValue());
        }

        return this;
    }

    private void addOnce(Object ingredient) {
        if (ingredient instanceof RecipeDSL.ExtendedIngredient source) {
            this.builder.requires(source.ingredient());
            this.extendedIngredients.add(source);
        } else if (ingredient instanceof ItemLike itemLike) {
            this.builder.requires(itemLike);
        } else if (ingredient instanceof TagKey<?> tagKey && tagKey.registry().equals(Registries.ITEM)) {
            @SuppressWarnings("unchecked") TagKey<Item> tag = (TagKey<Item>) tagKey;
            this.builder.requires(this.dsl.ingredientFromTag(tag));
        } else if (ingredient instanceof Ingredient value) {
            this.builder.requires(value);
        } else {
            throw new IllegalArgumentException("Unsupported ingredient: " + ingredient);
        }
    }

    public ShapelessCrafting group(String group) {
        this.builder.group(group);
        return this;
    }

    public ShapelessCrafting unlockedByHas(ItemLike... items) {
        for (ItemLike item : items) {
            this.builder.unlockedBy(RecipeDSL.hasName(item), this.dsl.unlocker().item(item));
        }

        return this;
    }

    public ShapelessCrafting unlockedByHas(TagKey<Item> tag) {
        this.builder.unlockedBy("has_" + tag.location().getPath(), this.dsl.unlocker().tag(tag));
        return this;
    }

    public void save(String path) {
        save(this.dsl.id(path));
    }

    public void save(ResourceLocation id) {
        this.dsl.save(
                this.builder,
                id,
                this.dsl.decorateOutput(RecipeDSL.RecipeKind.SHAPELESS, this.extendedIngredients)
        );
    }
}




