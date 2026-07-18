package space.anatomyuniverse.musavacca.data.recipes.dsl;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import space.anatomyuniverse.musavacca.data.recipes.RecipeDSL;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class ShapedCrafting {
    private final RecipeDSL dsl;
    private final ShapedRecipeBuilder builder;
    private final List<RecipeDSL.ExtendedIngredient> extendedIngredients = new ArrayList<>();

    public ShapedCrafting(
            RecipeDSL dsl,
            RecipeCategory category,
            ItemLike result,
            int count
    ) {
        this.dsl = dsl;

        //? if <1.21.3 {
        /*this.builder = ShapedRecipeBuilder.shaped(category, result, count);
        *///?} else {
        this.builder = ShapedRecipeBuilder.shaped(
                dsl.items(),
                category,
                new ItemStack(result.asItem(), count)
        );
        //?}
    }

    public ShapedCrafting pattern(String line) {
        this.builder.pattern(line);
        return this;
    }

    public ShapedCrafting define(char key, Object ingredient) {
        Objects.requireNonNull(ingredient, "ingredient");

        if (ingredient instanceof RecipeDSL.ExtendedIngredient source) {
            this.builder.define(key, source.ingredient());
            this.extendedIngredients.add(source);
        } else if (ingredient instanceof ItemLike itemLike) {
            this.builder.define(key, itemLike);
        } else if (ingredient instanceof TagKey<?> tagKey
                && tagKey.registry().equals(net.minecraft.core.registries.Registries.ITEM)) {
            @SuppressWarnings("unchecked") TagKey<Item> tag = (TagKey<Item>) tagKey;
            this.builder.define(key, this.dsl.ingredientFromTag(tag));
        } else if (ingredient instanceof Ingredient value) {
            this.builder.define(key, value);
        } else {
            throw new IllegalArgumentException("Unsupported ingredient for key '" + key + "': " + ingredient);
        }

        return this;
    }

    public ShapedCrafting group(String group) {
        this.builder.group(group);
        return this;
    }

    public ShapedCrafting unlockedByHas(ItemLike... items) {
        for (ItemLike item : items) {
            this.builder.unlockedBy(RecipeDSL.hasName(item), this.dsl.unlocker().item(item));
        }

        return this;
    }

    public ShapedCrafting unlockedByHas(TagKey<Item> tag) {
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
                this.dsl.decorateOutput(RecipeDSL.RecipeKind.SHAPED, this.extendedIngredients)
        );
    }
}
