package space.anatomyuniverse.musavacca.data.recipes.dsl;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
//? if >=1.21.11 {
/*import net.minecraft.resources.Identifier;
*///?} else {
import net.minecraft.resources.ResourceLocation;
//?}
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import space.anatomyuniverse.musavacca.data.recipes.RecipeDSL;


public final class Blasting {
    private final RecipeDSL dsl;
    private final SimpleCookingRecipeBuilder builder;

    public Blasting(
            RecipeDSL dsl,
            Object input,
            RecipeCategory category,
            ItemLike result,
            float experience,
            int time
    ) {
        this.dsl = dsl;
        this.builder = SimpleCookingRecipeBuilder.blasting(
                this.dsl.ingredient(input),
                category,

                //? if >=26.1 {
                /*category == RecipeCategory.FOOD ? net.minecraft.world.item.crafting.CookingBookCategory.FOOD : category == RecipeCategory.BUILDING_BLOCKS ? net.minecraft.world.item.crafting.CookingBookCategory.BLOCKS : net.minecraft.world.item.crafting.CookingBookCategory.MISC,
                *///?}
                result,
                experience,
                time
        );
    }

    public Blasting group(String group) {
        this.builder.group(group);
        return this;
    }

    public Blasting unlockedByHas(ItemLike... items) {
        for (ItemLike item : items) {
            this.builder.unlockedBy(RecipeDSL.hasName(item), this.dsl.unlocker().item(item));
        }

        return this;
    }

    public Blasting unlockedByHas(TagKey<Item> tag) {
        this.builder.unlockedBy("has_" + tag.location().getPath(), this.dsl.unlocker().tag(tag));
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
        this.dsl.save(this.builder, id);
    }
}
