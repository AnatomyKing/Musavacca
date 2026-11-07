package space.anatomyuniverse.musavacca.data.recipes.dsl;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.SmithingTransformRecipeBuilder;
//? if >=1.21.11 {
/*import net.minecraft.resources.Identifier;
*///?} else {
import net.minecraft.resources.ResourceLocation;
//?}

import net.minecraft.world.level.ItemLike;
import space.anatomyuniverse.musavacca.data.recipes.RecipeDSL;

public final class SmithingTransforms {
    private final RecipeDSL dsl;
    private SmithingTransformRecipeBuilder builder;

    public SmithingTransforms(RecipeDSL dsl) {
        this.dsl = dsl;
    }

    public SmithingTransforms of(Object template, Object base, Object addition, RecipeCategory category, ItemLike result) {
        this.builder = SmithingTransformRecipeBuilder.smithing(this.dsl.ingredient(template), this.dsl.ingredient(base), this.dsl.ingredient(addition),
                category, result.asItem());
        return this;
    }

    public SmithingTransforms unlocksHas(ItemLike... items) {
        requireBuilder();
        for (ItemLike item : items) {
            this.builder.unlocks(RecipeDSL.hasName(item), this.dsl.unlocker().item(item));
        }
        return this;
    }

    public void save(String path) {
        save(this.dsl.id(path));
    }

    //? if >=1.21.11 {
    /*public void save(Identifier id) {
    *///?} else {
    public void save(ResourceLocation id) {
    //?}
        requireBuilder();
        this.dsl.save(this.builder, id);
    }

    private void requireBuilder() {
        if (this.builder == null) throw new IllegalStateException("No smithing transform configured");
    }
}
