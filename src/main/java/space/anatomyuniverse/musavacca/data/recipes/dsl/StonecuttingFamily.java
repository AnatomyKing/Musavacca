package space.anatomyuniverse.musavacca.data.recipes.dsl;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import space.anatomyuniverse.musavacca.data.recipes.RecipeDSL;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class StonecuttingFamily {
    private final RecipeDSL dsl;

    private Object input;
    private RecipeCategory category;
    private ItemLike primaryResult;
    private int count = 1;

    private final List<ItemLike> variants = new ArrayList<>();
    private final List<ItemLike> unlockItems = new ArrayList<>();
    private final List<TagKey<Item>> unlockTags = new ArrayList<>();

    private boolean interchangeable;

    public StonecuttingFamily(RecipeDSL dsl) {
        this.dsl = Objects.requireNonNull(dsl, "dsl");
    }

    public StonecuttingFamily of(
            Object input,
            RecipeCategory category,
            ItemLike primaryResult,
            int count
    ) {
        this.input = Objects.requireNonNull(input, "input");
        this.category = Objects.requireNonNull(category, "category");
        this.primaryResult = Objects.requireNonNull(primaryResult, "primaryResult");

        if (count < 1) {
            throw new IllegalArgumentException("count must be >= 1");
        }

        this.count = count;
        return this;
    }

    public StonecuttingFamily variant(ItemLike result) {
        Objects.requireNonNull(result, "result");

        if (!containsItem(this.variants, result)) {
            this.variants.add(result);
        }

        return this;
    }

    public StonecuttingFamily variants(ItemLike... results) {
        Objects.requireNonNull(results, "results");

        for (ItemLike result : results) {
            variant(result);
        }

        return this;
    }

    public StonecuttingFamily interchangeable() {
        this.interchangeable = true;
        return this;
    }

    public StonecuttingFamily unlockedByHas(ItemLike... items) {
        Objects.requireNonNull(items, "items");

        for (ItemLike item : items) {
            Objects.requireNonNull(item, "item");

            if (!containsItem(this.unlockItems, item)) {
                this.unlockItems.add(item);
            }
        }

        return this;
    }

    public StonecuttingFamily unlockedByHas(TagKey<Item> tag) {
        Objects.requireNonNull(tag, "tag");

        if (!this.unlockTags.contains(tag)) {
            this.unlockTags.add(tag);
        }

        return this;
    }

    public void save(String path) {
        save(this.dsl.id(path));
    }

    public void save(ResourceLocation id) {
        Objects.requireNonNull(id, "id");
        requireConfigured();

        List<ItemLike> results = familyResults();

        // Keep the primary recipe exactly at the explicitly supplied id.
        saveRecipe(
                this.input,
                this.primaryResult,
                this.count,
                id
        );

        // The extra variants use their normal item/block names in the same folder.
        for (ItemLike result : results) {
            if (sameItem(result, this.primaryResult)) {
                continue;
            }

            saveRecipe(
                    this.input,
                    result,
                    this.count,
                    siblingId(id, RecipeDSL.keyOf(result))
            );
        }

        if (!this.interchangeable) {
            return;
        }

        if (!(this.input instanceof ItemLike familyRoot)) {
            throw new IllegalStateException(
                    "Interchangeable stonecutting families require an ItemLike input"
            );
        }

        List<ItemLike> family = new ArrayList<>();
        addUnique(family, familyRoot);

        for (ItemLike result : results) {
            addUnique(family, result);
        }

        // The root -> every result direction was already generated above.
        // Generate every OTHER family member -> every different member.
        for (ItemLike familyInput : family) {
            if (sameItem(familyInput, familyRoot)) {
                continue;
            }

            for (ItemLike familyResult : family) {
                if (sameItem(familyInput, familyResult)) {
                    continue;
                }

                saveRecipe(
                        familyInput,
                        familyResult,
                        this.count,
                        siblingId(
                                id,
                                RecipeDSL.keyOf(familyResult)
                                        + "_from_"
                                        + RecipeDSL.keyOf(familyInput)
                        )
                );
            }
        }
    }

    private List<ItemLike> familyResults() {
        List<ItemLike> results = new ArrayList<>();
        addUnique(results, this.primaryResult);

        for (ItemLike variant : this.variants) {
            addUnique(results, variant);
        }

        return results;
    }

    private void saveRecipe(
            Object input,
            ItemLike result,
            int count,
            ResourceLocation id
    ) {
        Stonecutting recipe = this.dsl.stonecut()
                .of(
                        input,
                        this.category,
                        result,
                        count
                );

        for (ItemLike item : this.unlockItems) {
            recipe.unlockedByHas(item);
        }

        for (TagKey<Item> tag : this.unlockTags) {
            recipe.unlockedByHas(tag);
        }

        recipe.save(id);
    }

    private static ResourceLocation siblingId(
            ResourceLocation base,
            String name
    ) {
        String path = base.getPath();
        int slash = path.lastIndexOf('/');

        String folder = slash >= 0
                ? path.substring(0, slash + 1)
                : "";

        return ResourceLocation.fromNamespaceAndPath(
                base.getNamespace(),
                folder + name
        );
    }

    private static void addUnique(
            List<ItemLike> items,
            ItemLike item
    ) {
        if (!containsItem(items, item)) {
            items.add(item);
        }
    }

    private static boolean containsItem(
            List<ItemLike> items,
            ItemLike item
    ) {
        for (ItemLike existing : items) {
            if (sameItem(existing, item)) {
                return true;
            }
        }

        return false;
    }

    private static boolean sameItem(
            ItemLike first,
            ItemLike second
    ) {
        return first.asItem() == second.asItem();
    }

    private void requireConfigured() {
        if (this.input == null) {
            throw new IllegalStateException(
                    "No stonecutting family input configured"
            );
        }

        if (this.category == null) {
            throw new IllegalStateException(
                    "No stonecutting family category configured"
            );
        }

        if (this.primaryResult == null) {
            throw new IllegalStateException(
                    "No stonecutting family primary result configured"
            );
        }
    }
}
