package space.anatomyuniverse.musavacca.data.recipes.dsl;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
//? if >=1.21.11 {
/*import net.minecraft.resources.Identifier;
*///?} else {
import net.minecraft.resources.ResourceLocation;
//?}
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import space.anatomyuniverse.musavacca.data.recipes.RecipeDSL;

public final class ShapelessCrafting {
    private final RecipeDSL dsl;
    private final ShapelessRecipeBuilder builder;

    public ShapelessCrafting(RecipeDSL dsl, RecipeCategory category, ItemLike result, int count) {
        this.dsl = dsl;
        //? if >=26.1 {
        /*this.builder = ShapelessRecipeBuilder.shapeless(dsl.items(), category, new net.minecraft.world.item.ItemStackTemplate(result.asItem(), count));
        *///?} else if >=1.21.2 <26.1 {
        this.builder = ShapelessRecipeBuilder.shapeless(dsl.items(), category, new ItemStack(result.asItem(), count));
        //?} else {
        /*this.builder = ShapelessRecipeBuilder.shapeless(category, result, count);
        *///?}
    }

    //? if >=26.1 {
    /*public ShapelessCrafting(RecipeDSL dsl, RecipeCategory category, net.minecraft.world.item.ItemStackTemplate result) {
    *///?} else {
    public ShapelessCrafting(RecipeDSL dsl, RecipeCategory category, ItemStack result) {
    //?}
        this.dsl = dsl;
        //? if >=1.21.2 {
        this.builder = ShapelessRecipeBuilder.shapeless(dsl.items(), category, result);
        //?} else {
        /*this.builder = ShapelessRecipeBuilder.shapeless(category, result);
        *///?}
    }

    public ShapelessCrafting requires(Object... ingredients) {
        for (Object ingredient : ingredients) {
            this.builder.requires(this.dsl.ingredient(ingredient));
        }
        return this;
    }

    public ShapelessCrafting requiresCount(Object ingredient, int count) {
        if (count <= 0) throw new IllegalArgumentException("count must be >= 1");
        if (count > 9) throw new IllegalArgumentException("shapeless max grid size is 9 (got " + count + ")");
        for (int i = 0; i < count; ++i) {
            this.builder.requires(this.dsl.ingredient(ingredient));
        }
        return this;
    }

    public ShapelessCrafting requiresPairs(Object... pairs) {
        if ((pairs.length & 1) != 0) throw new IllegalArgumentException("requiresPairs expects even number of args: (ingredient, count)*");
        for (int i = 0; i < pairs.length; i += 2) {
            if (!(pairs[i + 1] instanceof Number count)) throw new IllegalArgumentException("Count must be a Number at index " + (i + 1));
            requiresCount(pairs[i], count.intValue());
        }
        return this;
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

    //? if >=1.21.11 {
    /*public void save(Identifier id) {
    *///?} else {
    public void save(ResourceLocation id) {
    //?}
        this.dsl.save(this.builder, id);
    }
}
