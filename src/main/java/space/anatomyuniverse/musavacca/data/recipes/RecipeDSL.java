package space.anatomyuniverse.musavacca.data.recipes;

import net.minecraft.advancements.Criterion;
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
import net.minecraft.resources.ResourceLocation;
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
import space.anatomyuniverse.musavacca.data.recipes.dsl.Smelting;
import space.anatomyuniverse.musavacca.data.recipes.dsl.SmithingTransforms;
import space.anatomyuniverse.musavacca.data.recipes.dsl.Smoking;
import space.anatomyuniverse.musavacca.data.recipes.dsl.Stonecutting;

import java.util.List;

public final class RecipeDSL {
    public enum RecipeKind {
        SHAPED,
        SHAPELESS,
        SMELTING,
        BLASTING,
        SMOKING,
        CAMPFIRE,
        STONECUTTING,
        SMITHING_TRANSFORM
    }

    public interface ExtendedIngredient {
        Ingredient ingredient();

        RecipeOutput decorate(
                RecipeOutput output,
                RecipeKind kind,
                List<ExtendedIngredient> sources,
                HolderLookup.Provider registries
        );
    }

    public interface Unlocker {
        Criterion<?> item(ItemLike itemLike);
        Criterion<?> tag(TagKey<Item> tag);
    }

    public record SlottedExtendedIngredient(
            int slot,
            ExtendedIngredient delegate
    ) implements ExtendedIngredient {
        @Override
        public Ingredient ingredient() {
            return this.delegate.ingredient();
        }

        @Override
        public RecipeOutput decorate(
                RecipeOutput output,
                RecipeKind kind,
                List<ExtendedIngredient> sources,
                HolderLookup.Provider registries
        ) {
            return this.delegate.decorate(output, kind, sources, registries);
        }
    }

    private final RecipeOutput output;
    private final String modId;
    private final Unlocker unlocker;
    private final HolderLookup.Provider registries;

    //? if >=1.21.3 {
    private final HolderLookup.RegistryLookup<Item> items;
    //?}

    //? if <1.21.3 {
    /*public RecipeDSL(
            RecipeOutput output,
            String modId,
            Unlocker unlocker,
            HolderLookup.Provider registries
    ) {
        this.output = output;
        this.modId = modId;
        this.unlocker = unlocker;
        this.registries = registries;
    }
    *///?} else {
    public RecipeDSL(
            RecipeOutput output,
            String modId,
            Unlocker unlocker,
            HolderLookup.Provider registries
    ) {
        this.output = output;
        this.modId = modId;
        this.unlocker = unlocker;
        this.registries = registries;
        this.items = registries.lookupOrThrow(Registries.ITEM);
    }
    //?}

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

    //? if >=1.21.3 {
    public HolderLookup.RegistryLookup<Item> items() {
        return this.items;
    }
    //?}

    public ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(this.modId, path);
    }

    public ResourceKey<Recipe<?>> recipeKey(ResourceLocation id) {
        return ResourceKey.create(Registries.RECIPE, id);
    }

    public Ingredient ingredientFromTag(TagKey<Item> tag) {
        //? if <1.21.3 {
        /*return Ingredient.of(tag);
        *///?} else
        return Ingredient.of(this.items.getOrThrow(tag));
    }

    public Ingredient ingredient(Object value) {
        if (value instanceof ExtendedIngredient source) {
            return source.ingredient();
        }

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

    public ExtendedIngredient extended(Object value) {
        return value instanceof ExtendedIngredient source ? source : null;
    }

    public RecipeOutput decorateOutput(
            RecipeKind kind,
            List<ExtendedIngredient> sources
    ) {
        if (sources.isEmpty()) {
            return this.output;
        }

        return sources.getFirst().decorate(
                this.output,
                kind,
                List.copyOf(sources),
                this.registries
        );
    }

    public void save(ShapedRecipeBuilder builder, ResourceLocation id, RecipeOutput output) {
        //? if <1.21.3 {
        /*builder.save(output, id);
        *///?} else
        builder.save(output, recipeKey(id));
    }

    public void save(ShapelessRecipeBuilder builder, ResourceLocation id, RecipeOutput output) {
        //? if <1.21.3 {
        /*builder.save(output, id);
        *///?} else
        builder.save(output, recipeKey(id));
    }

    public void save(SimpleCookingRecipeBuilder builder, ResourceLocation id, RecipeOutput output) {
        //? if <1.21.3 {
        /*builder.save(output, id);
        *///?} else
        builder.save(output, recipeKey(id));
    }

    public void save(SingleItemRecipeBuilder builder, ResourceLocation id, RecipeOutput output) {
        //? if <1.21.3 {
        /*builder.save(output, id);
        *///?} else
        builder.save(output, recipeKey(id));
    }

    public void save(SmithingTransformRecipeBuilder builder, ResourceLocation id, RecipeOutput output) {
        //? if <1.21.3 {
        /*builder.save(output, id);
        *///?} else
        builder.save(output, recipeKey(id));
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




