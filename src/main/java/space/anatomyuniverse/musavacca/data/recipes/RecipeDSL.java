package space.anatomyuniverse.musavacca.data.recipes;

//? if >=26.2 {
/*import net.minecraft.advancements.triggers.Criterion;
*///?} else {
import net.minecraft.advancements.Criterion;
//?}
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.data.recipes.SingleItemRecipeBuilder;
import net.minecraft.data.recipes.SmithingTransformRecipeBuilder;
import net.minecraft.resources.ResourceKey;
//? if >=1.21.11 {
/*import net.minecraft.resources.Identifier;
*///?} else {
import net.minecraft.resources.ResourceLocation;
//?}
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import space.anatomyuniverse.musavacca.data.recipes.dsl.Blasting;
import space.anatomyuniverse.musavacca.data.recipes.dsl.CampfireCooking;
import space.anatomyuniverse.musavacca.data.recipes.dsl.ShapedCrafting;
import space.anatomyuniverse.musavacca.data.recipes.dsl.ShapelessCrafting;
import space.anatomyuniverse.musavacca.data.recipes.dsl.ShapelessCountFamily;
import space.anatomyuniverse.musavacca.data.recipes.dsl.Smelting;
import space.anatomyuniverse.musavacca.data.recipes.dsl.SmithingTransforms;
import space.anatomyuniverse.musavacca.data.recipes.dsl.Smoking;
import space.anatomyuniverse.musavacca.data.recipes.dsl.Stonecutting;
import space.anatomyuniverse.musavacca.data.recipes.dsl.StonecuttingFamily;

import java.util.Objects;
import java.util.function.Consumer;

public class RecipeDSL {
    public interface Unlocker {
        Criterion<?> item(ItemLike itemLike);
        Criterion<?> tag(TagKey<Item> tag);
    }

    private final RecipeOutput output;
    private final String modId;
    private final Unlocker unlocker;
    private final HolderLookup.Provider registries;
    private final String recipePathPrefix;

    //? if >=1.21.2 {
    private final HolderLookup.RegistryLookup<Item> items;

    //?}
    public RecipeDSL(
            RecipeOutput output,
            String modId,
            Unlocker unlocker,
            HolderLookup.Provider registries
    ) {
        this(output, modId, unlocker, registries, "");
    }

    protected RecipeDSL(
            RecipeOutput output,
            String modId,
            Unlocker unlocker,
            HolderLookup.Provider registries,
            String recipePathPrefix
    ) {
        this.output = Objects.requireNonNull(output, "output");
        this.modId = Objects.requireNonNull(modId, "modId");
        this.unlocker = Objects.requireNonNull(unlocker, "unlocker");
        this.registries = Objects.requireNonNull(registries, "registries");
        this.recipePathPrefix = recipePathPrefix == null ? "" : recipePathPrefix;

        //? if >=1.21.2 {
        this.items = registries.lookupOrThrow(Registries.ITEM);
        //?}
    }

    public CompatRecipeDSL compat(String modId) {
        return new CompatRecipeDSL(this, modId);
    }

    public void compat(String modId, Consumer<CompatRecipeDSL> recipes) {
        Objects.requireNonNull(recipes, "recipes").accept(compat(modId));
    }

    public ShapedCrafting shaped(RecipeCategory category, ItemLike result, int count) {
        return new ShapedCrafting(this, category, result, count);
    }

    public ShapedCrafting shaped(RecipeCategory category, ItemLike result) {
        return shaped(category, result, 1);
    }

    public ShapelessCrafting shapeless(RecipeCategory category, ItemLike result, int count) {
        return new ShapelessCrafting(this, category, result, count);
    }

    public ShapelessCrafting shapeless(RecipeCategory category, ItemLike result) {
        return shapeless(category, result, 1);
    }

    public ShapelessCountFamily shapelessCountFamily(RecipeCategory category) {
        return new ShapelessCountFamily(this, category);
    }

    public Smelting smelt(Object input, RecipeCategory category, ItemLike result, float experience, int time) {
        return new Smelting(this, input, category, result, experience, time);
    }

    public Blasting blast(Object input, RecipeCategory category, ItemLike result, float experience, int time) {
        return new Blasting(this, input, category, result, experience, time);
    }

    public Smoking smoke(Object input, RecipeCategory category, ItemLike result, float experience, int time) {
        return new Smoking(this, input, category, result, experience, time);
    }

    public CampfireCooking campfire(Object input, RecipeCategory category, ItemLike result, float experience, int time) {
        return new CampfireCooking(this, input, category, result, experience, time);
    }

    public Stonecutting stonecut() {
        return new Stonecutting(this);
    }

    public StonecuttingFamily stonecutFamily() {
        return new StonecuttingFamily(this);
    }

    public SmithingTransforms transform() {
        return new SmithingTransforms(this);
    }

    public ShapelessCrafting shapelessCountToCount(
            RecipeCategory category,
            ItemLike result,
            int resultCount,
            Object input,
            int inputCount
    ) {
        if (inputCount <= 0 || inputCount > 9) {
            throw new IllegalArgumentException("inputCount must be in [1,9]");
        }

        return shapeless(category, result, resultCount).requiresCount(input, inputCount);
    }

    public ShapelessCrafting shapelessCounts(
            RecipeCategory category,
            ItemLike result,
            int resultCount,
            Object... ingredientCountPairs
    ) {
        return shapeless(category, result, resultCount).requiresPairs(ingredientCountPairs);
    }

    public void compressChain(
            ItemLike item,
            ItemLike block,
            RecipeCategory itemCategory,
            RecipeCategory blockCategory,
            String packPrefix
    ) {
        shaped(blockCategory, block)
                .pattern("xxx")
                .pattern("xxx")
                .pattern("xxx")
                .define('x', item)
                .unlockedByHas(item)
                .save(packPrefix + "/" + keyOf(block));

        shapeless(itemCategory, item, 9)
                .requires(block)
                .unlockedByHas(block)
                .save(packPrefix + "/" + keyOf(item) + "_from_block");
    }

    public RecipeOutput output() {
        return this.output;
    }

    public Unlocker unlocker() {
        return this.unlocker;
    }

    public HolderLookup.Provider registries() {
        return this.registries;
    }

    String modId() {
        return this.modId;
    }

    String recipePathPrefix() {
        return this.recipePathPrefix;
    }

    //? if >=1.21.11 {
    /*public HolderLookup.RegistryLookup<Item> items() {
        return this.items;
    }

    public Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(this.modId, joinFolder(this.recipePathPrefix, path));
    *///?} else if >=1.21.2 <1.21.11 {
    public HolderLookup.RegistryLookup<Item> items() {
        return this.items;
    }

    public ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(this.modId, joinFolder(this.recipePathPrefix, path));
    //?} else {
    /*public ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(this.modId, joinFolder(this.recipePathPrefix, path));
    *///?}
    }

    //? if >=1.21.11 {
    /*public ResourceKey<Recipe<?>> recipeKey(Identifier id) {
    *///?} else {
    public ResourceKey<Recipe<?>> recipeKey(ResourceLocation id) {
    //?}
        return ResourceKey.create(Registries.RECIPE, id);
    }

    public Ingredient ingredientFromTag(TagKey<Item> tag) {

        //? if >=1.21.2 {
        return Ingredient.of(this.items.getOrThrow(tag));
        //?} else {
        /*return Ingredient.of(tag);
        *///?}
    }

    public Ingredient ingredient(Object value) {
        if (value instanceof Ingredient ingredient) {
            return ingredient;
        }

        if (value instanceof ItemLike itemLike) {
            return Ingredient.of(itemLike);
        }

        if (value instanceof TagKey<?> tagKey && tagKey.registry().equals(Registries.ITEM)) {
            @SuppressWarnings("unchecked") TagKey<Item> tag = (TagKey<Item>) tagKey;
            return ingredientFromTag(tag);
        }

        throw new IllegalArgumentException("Unsupported ingredient: " + value);
    }

    //? if >=1.21.11 {
    /*public void save(ShapedRecipeBuilder builder, Identifier id) {
    *///?} else {
    public void save(ShapedRecipeBuilder builder, ResourceLocation id) {
    //?}

        //? if >=1.21.2 {
        builder.save(this.output, recipeKey(id));
        //?} else {
        /*builder.save(this.output, id);
        *///?}
    }

    //? if >=1.21.11 {
    /*public void save(ShapelessRecipeBuilder builder, Identifier id) {
    *///?} else {
    public void save(ShapelessRecipeBuilder builder, ResourceLocation id) {
    //?}

        //? if >=1.21.2 {
        builder.save(this.output, recipeKey(id));
        //?} else {
        /*builder.save(this.output, id);
        *///?}
    }

    //? if >=1.21.11 {
    /*public void save(SimpleCookingRecipeBuilder builder, Identifier id) {
    *///?} else {
    public void save(SimpleCookingRecipeBuilder builder, ResourceLocation id) {
    //?}

        //? if >=1.21.2 {
        builder.save(this.output, recipeKey(id));
        //?} else {
        /*builder.save(this.output, id);
        *///?}
    }

    //? if >=1.21.11 {
    /*public void save(SingleItemRecipeBuilder builder, Identifier id) {
    *///?} else {
    public void save(SingleItemRecipeBuilder builder, ResourceLocation id) {
    //?}

        //? if >=1.21.2 {
        builder.save(this.output, recipeKey(id));
        //?} else {
        /*builder.save(this.output, id);
        *///?}
    }

    //? if >=1.21.11 {
    /*public void save(SmithingTransformRecipeBuilder builder, Identifier id) {
    *///?} else {
    public void save(SmithingTransformRecipeBuilder builder, ResourceLocation id) {
    //?}

        //? if >=1.21.2 {
        builder.save(this.output, recipeKey(id));
        //?} else {
        /*builder.save(this.output, id);
        *///?}
    }

    public static String keyOf(ItemLike itemLike) {
        return BuiltInRegistries.ITEM.getKey(itemLike.asItem()).getPath();
    }

    public static String hasName(ItemLike itemLike) {
        return "has_" + keyOf(itemLike);
    }

    public static String joinFolder(String folder, String name) {
        if (folder == null || folder.isBlank()) {
            return name;
        }

        return folder.endsWith("/") ? folder + name : folder + "/" + name;
    }
}
