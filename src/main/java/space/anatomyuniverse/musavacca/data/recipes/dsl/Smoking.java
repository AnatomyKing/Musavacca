package space.anatomyuniverse.musavacca.data.recipes.dsl;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import space.anatomyuniverse.musavacca.data.recipes.RecipeDSL;

import java.util.ArrayList;
import java.util.List;

public final class Smoking {
    private final RecipeDSL dsl;
    private final SimpleCookingRecipeBuilder builder;
    private final List<RecipeDSL.ExtendedIngredient> extendedIngredients = new ArrayList<>();

    public Smoking(
            RecipeDSL dsl,
            Object input,
            RecipeCategory category,
            ItemLike result,
            float experience,
            int time
    ) {
        this.dsl = dsl;
        this.builder = SimpleCookingRecipeBuilder.smoking(
                ingredient(input),
                category,
                result,
                experience,
                time
        );
    }

    private net.minecraft.world.item.crafting.Ingredient ingredient(Object input) {
        if (input instanceof RecipeDSL.ExtendedIngredient source) {
            this.extendedIngredients.add(source);
        }

        return this.dsl.ingredient(input);
    }

    public Smoking group(String group) {
        this.builder.group(group);
        return this;
    }

    public Smoking unlockedByHas(ItemLike... items) {
        for (ItemLike item : items) {
            this.builder.unlockedBy(RecipeDSL.hasName(item), this.dsl.unlocker().item(item));
        }

        return this;
    }

    public Smoking unlockedByHas(TagKey<Item> tag) {
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
                this.dsl.decorateOutput(RecipeDSL.RecipeKind.SMOKING, this.extendedIngredients)
        );
    }
}


