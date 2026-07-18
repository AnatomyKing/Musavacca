package space.anatomyuniverse.musavacca.data.recipes.dsl;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.SmithingTransformRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import space.anatomyuniverse.musavacca.data.recipes.RecipeDSL;

import java.util.ArrayList;
import java.util.List;

public final class SmithingTransforms {
    private final RecipeDSL dsl;
    private SmithingTransformRecipeBuilder builder;
    private final List<RecipeDSL.ExtendedIngredient> extendedIngredients = new ArrayList<>();

    public SmithingTransforms(RecipeDSL dsl) {
        this.dsl = dsl;
    }

    public SmithingTransforms of(
            Object template,
            Object base,
            Object addition,
            RecipeCategory category,
            ItemLike result
    ) {
        this.builder = SmithingTransformRecipeBuilder.smithing(
                ingredient(template, 0),
                ingredient(base, 1),
                ingredient(addition, 2),
                category,
                result.asItem()
        );

        return this;
    }

    private Ingredient ingredient(Object value, int slot) {
        if (value instanceof RecipeDSL.ExtendedIngredient source) {
            this.extendedIngredients.add(new RecipeDSL.SlottedExtendedIngredient(slot, source));
            return source.ingredient();
        }

        return this.dsl.ingredient(value);
    }

    public SmithingTransforms unlocksHas(ItemLike... items) {
        requireBuilder();

        for (ItemLike item : items) {
            this.builder.unlocks(RecipeDSL.hasName(item), this.dsl.unlocker().item(item));
        }

        return this;
    }

    public SmithingTransforms unlocksHas(TagKey<Item> tag) {
        requireBuilder();
        this.builder.unlocks("has_" + tag.location().getPath(), this.dsl.unlocker().tag(tag));
        return this;
    }

    public void save(String path) {
        save(this.dsl.id(path));
    }

    public void save(ResourceLocation id) {
        requireBuilder();
        this.dsl.save(
                this.builder,
                id,
                this.dsl.decorateOutput(RecipeDSL.RecipeKind.SMITHING_TRANSFORM, this.extendedIngredients)
        );
    }

    private void requireBuilder() {
        if (this.builder == null) {
            throw new IllegalStateException("No smithing transform configured");
        }
    }
}
