
package space.anatomyuniverse.musavacca.data.recipes;

import net.minecraft.advancements.Criterion;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;


//? if >=1.21.5 {
import net.minecraft.world.item.equipment.trim.TrimPattern;
import net.minecraft.core.Holder;
//?}


import java.util.Objects;
import java.util.function.Consumer;

public final class RecipeDSL {

    /** Adapter so we can call provider's protected has(...) from outside. */
    public interface Unlocker {
        Criterion<?> item(ItemLike itemLike);
        Criterion<?> tag(TagKey<Item> tag);
    }

    private final RecipeOutput out;
    private final Unlocker unlocker;
    private final String modId;

    //? if >=1.21.3 {
    private final HolderLookup.RegistryLookup<Item> items;
    //?}

    //? if <1.21.3 {
    /*public RecipeDSL(RecipeOutput out, String modId, Unlocker unlocker) {
        this.out = out;
        this.modId = modId;
        this.unlocker = unlocker;
    }
    *///?} else {
    public RecipeDSL(RecipeOutput out, String modId, Unlocker unlocker, HolderLookup.Provider registries) {
        this.out = out;
        this.modId = modId;
        this.unlocker = unlocker;
        this.items = registries.lookupOrThrow(Registries.ITEM);
    }
    //?}


    public ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(modId, path);
    }

    private ResourceKey<net.minecraft.world.item.crafting.Recipe<?>> recipeKey(ResourceLocation id) {
        return ResourceKey.create(Registries.RECIPE, id);
    }

    private Ingredient ingredientFromTag(TagKey<Item> tag) {
        //? if <1.21.3 {
        /*return Ingredient.of(tag);
        *///?} else
        return Ingredient.of(items.getOrThrow(tag));
    }

    private void saveCompat(ShapedRecipeBuilder b, ResourceLocation id) {
        //? if <1.21.3 {
        /*b.save(out, id);
        *///?} else
        b.save(out, recipeKey(id));
    }

    private void saveCompat(ShapelessRecipeBuilder b, ResourceLocation id) {
        //? if <1.21.3 {
        /*b.save(out, id);
        *///?} else
        b.save(out, recipeKey(id));
    }

    private void saveCompat(SimpleCookingRecipeBuilder b, ResourceLocation id) {
        //? if <1.21.3 {
        /*b.save(out, id);
        *///?} else
        b.save(out, recipeKey(id));
    }

    private void saveCompat(SingleItemRecipeBuilder b, ResourceLocation id) {
        //? if <1.21.3 {
        /*b.save(out, id);
        *///?} else
        b.save(out, recipeKey(id));
    }

    private void saveCompat(SmithingTransformRecipeBuilder b, ResourceLocation id) {
        //? if <1.21.3 {
        /*b.save(out, id);
        *///?} else
        b.save(out, recipeKey(id));
    }

    private void saveCompat(SmithingTrimRecipeBuilder b, ResourceLocation id) {
        //? if <1.21.3 {
        /*b.save(out, id);
        *///?} else
        b.save(out, recipeKey(id));
    }

    public Shapeless shapeless(RecipeCategory cat, ItemLike result, int count) {
        final ShapelessRecipeBuilder b;
        //? if <1.21.3 {
        /*b = ShapelessRecipeBuilder.shapeless(cat, result, count);
        *///?} else {
        b = ShapelessRecipeBuilder.shapeless(items, cat, new ItemStack(result.asItem(), count));
        //?}
        return new Shapeless(b);
    }

    public Shapeless shapeless(RecipeCategory cat, ItemLike result) {
        return shapeless(cat, result, 1);
    }

    public final class Shapeless {
        private final ShapelessRecipeBuilder b;
        private Shapeless(ShapelessRecipeBuilder b) { this.b = b; }


        public Shapeless requires(Object... ings) {
            forEachIngredient(ings, in -> addOnce(b, in));
            return this;
        }

        public Shapeless requiresCount(Object ingredient, int count) {
            if (count <= 0) throw new IllegalArgumentException("count must be >= 1");
            if (count > 9)  throw new IllegalArgumentException("shapeless max grid size is 9 (got " + count + ")");
            for (int i = 0; i < count; i++) addOnce(b, ingredient);
            return this;
        }

        public Shapeless requiresPairs(Object... pairs) {
            if ((pairs.length & 1) != 0) {
                throw new IllegalArgumentException("requiresPairs expects even number of args: (ingredient, count)*");
            }
            for (int i = 0; i < pairs.length; i += 2) {
                Object ing = pairs[i];
                Object cnt = pairs[i + 1];
                if (!(cnt instanceof Number n)) {
                    throw new IllegalArgumentException("Count must be a Number at index " + (i + 1));
                }
                requiresCount(ing, n.intValue());
            }
            return this;
        }

        public Shapeless group(String g) { b.group(g); return this; }

        public Shapeless unlockedByHas(ItemLike... items) {
            for (ItemLike i : items) b.unlockedBy(hasName(i), unlocker.item(i));
            return this;
        }

        public Shapeless unlockedByHas(TagKey<Item> tag) {
            b.unlockedBy("has_" + tag.location().getPath(), unlocker.tag(tag));
            return this;
        }

        public void save(String path) { saveCompat(b, id(path)); }
        public void save(ResourceLocation id) { saveCompat(b, id); }
    }

    public Shaped shaped(RecipeCategory cat, ItemLike result, int count) {
        final ShapedRecipeBuilder b;
        //? if <1.21.3 {
        /*b = ShapedRecipeBuilder.shaped(cat, result, count);
        *///?} else {
        b = ShapedRecipeBuilder.shaped(items, cat, new ItemStack(result.asItem(), count));
        //?}
        return new Shaped(b);
    }

    public Shaped shaped(RecipeCategory cat, ItemLike result) { return shaped(cat, result, 1); }

    public final class Shaped {
        private final ShapedRecipeBuilder b;
        private Shaped(ShapedRecipeBuilder b) { this.b = b; }

        public Shaped pattern(String line) { b.pattern(line); return this; }

        /** Map a single char to ItemLike / TagKey<Item> / Ingredient. */
        public Shaped define(char key, Object ingredient) {
            Objects.requireNonNull(ingredient, "ingredient");
            if (ingredient instanceof ItemLike il) b.define(key, il);
            else if (ingredient instanceof TagKey<?> tk && tk.registry().equals(Registries.ITEM)) {
                @SuppressWarnings("unchecked") TagKey<Item> t = (TagKey<Item>) tk;
                b.define(key, ingredientFromTag(t));
            } else if (ingredient instanceof Ingredient ing) b.define(key, ing);
            else throw new IllegalArgumentException("Unsupported ingredient for key '" + key + "': " + ingredient);
            return this;
        }

        public Shaped group(String g) { b.group(g); return this; }

        public Shaped unlockedByHas(ItemLike... items) {
            for (ItemLike i : items) b.unlockedBy(hasName(i), unlocker.item(i));
            return this;
        }

        public Shaped unlockedByHas(TagKey<Item> tag) {
            b.unlockedBy("has_" + tag.location().getPath(), unlocker.tag(tag));
            return this;
        }

        public void save(String path) { saveCompat(b, id(path)); }
        public void save(ResourceLocation id) { saveCompat(b, id); }
    }

    public Cooking cook() { return new Cooking(); }

    public final class Cooking {
        private SimpleCookingRecipeBuilder b;

        public Cooking smelt(Ingredient in, RecipeCategory cat, ItemLike outItem, float xp, int time) {
            b = SimpleCookingRecipeBuilder.smelting(in, cat, outItem, xp, time);
            return this;
        }
        public Cooking blast(Ingredient in, RecipeCategory cat, ItemLike outItem, float xp, int time) {
            b = SimpleCookingRecipeBuilder.blasting(in, cat, outItem, xp, time);
            return this;
        }
        public Cooking smoke(Ingredient in, RecipeCategory cat, ItemLike outItem, float xp, int time) {
            b = SimpleCookingRecipeBuilder.smoking(in, cat, outItem, xp, time);
            return this;
        }
        public Cooking campfire(Ingredient in, RecipeCategory cat, ItemLike outItem, float xp, int time) {
            b = SimpleCookingRecipeBuilder.campfireCooking(in, cat, outItem, xp, time);
            return this;
        }

        public Cooking unlockedByHas(ItemLike... items) {
            for (ItemLike i : items) b.unlockedBy(hasName(i), unlocker.item(i));
            return this;
        }
        public Cooking unlockedByHas(TagKey<Item> tag) {
            b.unlockedBy("has_" + tag.location().getPath(), unlocker.tag(tag));
            return this;
        }
        public Cooking group(String g) { b.group(g); return this; }

        public void save(String path) { require(); saveCompat(b, id(path)); }
        public void save(ResourceLocation id) { require(); saveCompat(b, id); }

        private void require() { if (b == null) throw new IllegalStateException("No cooking recipe configured"); }
    }

    public Stonecut stonecut() { return new Stonecut(); }

    public final class Stonecut {
        private SingleItemRecipeBuilder b;

        public Stonecut of(Ingredient in, RecipeCategory cat, ItemLike outItem, int count) {
            b = SingleItemRecipeBuilder.stonecutting(in, cat, outItem, count);
            return this;
        }
        public Stonecut unlockedByHas(ItemLike... items) {
            for (ItemLike i : items) b.unlockedBy(hasName(i), unlocker.item(i));
            return this;
        }
        public Stonecut unlockedByHas(TagKey<Item> tag) {
            b.unlockedBy("has_" + tag.location().getPath(), unlocker.tag(tag));
            return this;
        }
        public Stonecut group(String g) { b.group(g); return this; }

        public void save(String path) { require(); saveCompat(b, id(path)); }
        public void save(ResourceLocation id) { require(); saveCompat(b, id); }

        private void require() { if (b == null) throw new IllegalStateException("No stonecut recipe configured"); }
    }


    public void stonecutFamily(RecipeCategory cat,
                               ItemLike input,
                               String folder,
                               String fromSuffix,
                               Object... outCountPairs) {
        stonecutFamilyInternal(cat, Ingredient.of(input), folder, fromSuffix, input, null, outCountPairs);
    }

    public void stonecutFamily(RecipeCategory cat,
                               TagKey<Item> inputTag,
                               String folder,
                               String fromSuffix,
                               Object... outCountPairs) {
        stonecutFamilyInternal(cat, ingredientFromTag(inputTag), folder, fromSuffix, null, inputTag, outCountPairs);
    }

    private void stonecutFamilyInternal(RecipeCategory cat,
                                        Ingredient in,
                                        String folder,
                                        String fromSuffix,
                                        ItemLike unlockItem,
                                        TagKey<Item> unlockTag,
                                        Object... outCountPairs) {
        if ((outCountPairs.length & 1) != 0) {
            throw new IllegalArgumentException("stonecutFamily expects even args: (output, count)*");
        }

        final String suf = (fromSuffix == null || fromSuffix.isBlank())
                ? ""
                : (fromSuffix.startsWith("_") ? fromSuffix : "_" + fromSuffix);

        for (int i = 0; i < outCountPairs.length; i += 2) {
            Object outObj = outCountPairs[i];
            Object cntObj = outCountPairs[i + 1];

            if (!(outObj instanceof ItemLike outItem)) {
                throw new IllegalArgumentException("Output must be ItemLike at index " + i + " (got " + outObj + ")");
            }
            if (!(cntObj instanceof Number n)) {
                throw new IllegalArgumentException("Count must be Number at index " + (i + 1) + " (got " + cntObj + ")");
            }

            int count = n.intValue();
            if (count <= 0) throw new IllegalArgumentException("Count must be >= 1 (got " + count + ")");

            String path = joinFolder(folder, keyOf(outItem) + suf);

            Stonecut sc = stonecut().of(in, cat, outItem, count);

            if (unlockItem != null) sc.unlockedByHas(unlockItem);
            else if (unlockTag != null) sc.unlockedByHas(unlockTag);

            sc.save(path);
        }
    }

    public SmithingTransform transform() { return new SmithingTransform(); }
    public SmithingTrim trim() { return new SmithingTrim(); }

    public final class SmithingTransform {
        private SmithingTransformRecipeBuilder b;

        public SmithingTransform of(Ingredient template, Ingredient base, Ingredient addition,
                                    RecipeCategory cat, ItemLike result) {
            b = SmithingTransformRecipeBuilder.smithing(template, base, addition, cat, result.asItem());
            return this;
        }

        public SmithingTransform unlocksHas(ItemLike... items) {
            for (ItemLike i : items) b.unlocks(hasName(i), unlocker.item(i));
            return this;
        }
        public SmithingTransform unlocksHas(TagKey<Item> tag) {
            b.unlocks("has_" + tag.location().getPath(), unlocker.tag(tag));
            return this;
        }

        public void save(String path) { require(); saveCompat(b, id(path)); }
        public void save(ResourceLocation id) { require(); saveCompat(b, id); }

        private void require() { if (b == null) throw new IllegalStateException("No smithing transform configured"); }
    }

    public final class SmithingTrim {
        private SmithingTrimRecipeBuilder b;

        //? if <1.21.5 {
        /*public SmithingTrim of(Ingredient template, Ingredient base, Ingredient addition, RecipeCategory cat) {
            b = SmithingTrimRecipeBuilder.smithingTrim(template, base, addition, cat);
            return this;
        }
        *///?} else {
        public SmithingTrim of(Ingredient template, Ingredient base, Ingredient addition, Holder<TrimPattern> pattern, RecipeCategory cat) {
            b = SmithingTrimRecipeBuilder.smithingTrim(template, base, addition, pattern, cat);
            return this;
        }
        //?}

        public SmithingTrim unlocksHas(ItemLike... items) {
            for (ItemLike i : items) b.unlocks(hasName(i), unlocker.item(i));
            return this;
        }

        public SmithingTrim unlocksHas(TagKey<Item> tag) {
            b.unlocks("has_" + tag.location().getPath(), unlocker.tag(tag));
            return this;
        }

        public void save(String path) { require(); saveCompat(b, id(path)); }
        public void save(ResourceLocation id) { require(); saveCompat(b, id); }

        private void require() {
            if (b == null) throw new IllegalStateException("No smithing trim configured");
        }
    }


    public Shapeless shapelessCountToCount(RecipeCategory cat,
                                           ItemLike result, int resultCount,
                                           Object input, int inputCount) {
        if (inputCount <= 0 || inputCount > 9)
            throw new IllegalArgumentException("inputCount must be in [1,9]");
        return shapeless(cat, result, resultCount).requiresCount(input, inputCount);
    }

    public Shapeless shapelessCounts(RecipeCategory cat,
                                     ItemLike result, int resultCount,
                                     Object... ingredientCountPairs) {
        return shapeless(cat, result, resultCount).requiresPairs(ingredientCountPairs);
    }

    public void compressChain(ItemLike item, ItemLike block,
                              RecipeCategory itemCat, RecipeCategory blockCat,
                              String packPrefix) {
        shaped(blockCat, block)
                .pattern("xxx").pattern("xxx").pattern("xxx")
                .define('x', item)
                .unlockedByHas(item)
                .save(packPrefix + "/" + keyOf(block));

        shapeless(itemCat, item, 9)
                .requires(block)
                .unlockedByHas(block)
                .save(packPrefix + "/" + keyOf(item) + "_from_block");
    }

    private static void forEachIngredient(Object[] arr, Consumer<Object> fn) {
        for (Object o : arr) fn.accept(o);
    }

    private void addOnce(ShapelessRecipeBuilder b, Object ingredient) {
        if (ingredient instanceof ItemLike il) b.requires(il);
        else if (ingredient instanceof TagKey<?> tk && tk.registry().equals(Registries.ITEM)) {
            @SuppressWarnings("unchecked") TagKey<Item> t = (TagKey<Item>) tk;
            b.requires(ingredientFromTag(t));
        } else if (ingredient instanceof Ingredient ing) b.requires(ing);
        else throw new IllegalArgumentException("Unsupported ingredient: " + ingredient);
    }

    private static String joinFolder(String folder, String name) {
        if (folder == null || folder.isBlank()) return name;
        return folder.endsWith("/") ? (folder + name) : (folder + "/" + name);
    }

    public static String keyOf(ItemLike il) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(il.asItem());
        return id.getPath();
    }

    private static String hasName(ItemLike i) { return "has_" + keyOf(i); }
}
