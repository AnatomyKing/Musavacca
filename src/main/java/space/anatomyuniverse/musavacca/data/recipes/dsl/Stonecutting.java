package space.anatomyuniverse.musavacca.data.recipes.dsl;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.SingleItemRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import space.anatomyuniverse.musavacca.data.recipes.RecipeDSL;

import java.util.ArrayList;
import java.util.List;

public final class Stonecutting {
    private final RecipeDSL dsl;
    private SingleItemRecipeBuilder builder;
    private final List<RecipeDSL.ExtendedIngredient> extendedIngredients = new ArrayList<>();

    public Stonecutting(RecipeDSL dsl) {
        this.dsl = dsl;
    }

    public Stonecutting of(
            Object input,
            RecipeCategory category,
            ItemLike result,
            int count
    ) {
        if (input instanceof RecipeDSL.ExtendedIngredient source) {
            this.extendedIngredients.add(source);
        }

        this.builder = SingleItemRecipeBuilder.stonecutting(
                this.dsl.ingredient(input),
                category,
                result,
                count
        );

        return this;
    }

    public Stonecutting group(String group) {
        requireBuilder();
        this.builder.group(group);
        return this;
    }

    public Stonecutting unlockedByHas(ItemLike... items) {
        requireBuilder();

        for (ItemLike item : items) {
            this.builder.unlockedBy(RecipeDSL.hasName(item), this.dsl.unlocker().item(item));
        }

        return this;
    }

    public Stonecutting unlockedByHas(TagKey<Item> tag) {
        requireBuilder();
        this.builder.unlockedBy("has_" + tag.location().getPath(), this.dsl.unlocker().tag(tag));
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
                this.dsl.decorateOutput(RecipeDSL.RecipeKind.STONECUTTING, this.extendedIngredients)
        );
    }

    private void requireBuilder() {
        if (this.builder == null) {
            throw new IllegalStateException("No stonecutting recipe configured");
        }
    }
}


