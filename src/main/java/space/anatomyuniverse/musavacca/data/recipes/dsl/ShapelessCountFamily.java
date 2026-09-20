package space.anatomyuniverse.musavacca.data.recipes.dsl;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import space.anatomyuniverse.musavacca.data.recipes.RecipeDSL;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public final class ShapelessCountFamily {
    private final RecipeDSL dsl;
    private final RecipeCategory category;

    private final List<Step> steps = new ArrayList<>();
    private final List<ItemLike> unlockItems = new ArrayList<>();
    private final List<TagKey<Item>> unlockTags = new ArrayList<>();

    private boolean reversible;

    public ShapelessCountFamily(
            RecipeDSL dsl,
            RecipeCategory category
    ) {
        this.dsl = Objects.requireNonNull(dsl, "dsl");
        this.category = Objects.requireNonNull(category, "category");
    }

    public ShapelessCountFamily step(
            ItemLike input,
            int inputCount,
            ItemLike result,
            int resultCount
    ) {
        Objects.requireNonNull(input, "input");
        Objects.requireNonNull(result, "result");

        validateInputCount(input, inputCount);
        validateResultCount(result, resultCount);

        if (sameItem(input, result)) {
            throw new IllegalArgumentException(
                    "A shapeless count family step cannot convert an item into itself: "
                            + RecipeDSL.keyOf(input)
            );
        }

        for (Step step : this.steps) {
            if (sameItem(step.input(), input)
                    && sameItem(step.result(), result)) {
                throw new IllegalArgumentException(
                        "Duplicate shapeless count family step: "
                                + RecipeDSL.keyOf(input)
                                + " -> "
                                + RecipeDSL.keyOf(result)
                );
            }
        }

        this.steps.add(new Step(
                input,
                inputCount,
                result,
                resultCount
        ));

        return this;
    }

    public ShapelessCountFamily reversible() {
        this.reversible = true;
        return this;
    }

    public ShapelessCountFamily unlockedByHas(ItemLike... items) {
        Objects.requireNonNull(items, "items");

        for (ItemLike item : items) {
            Objects.requireNonNull(item, "item");

            if (!containsItem(this.unlockItems, item)) {
                this.unlockItems.add(item);
            }
        }

        return this;
    }

    public ShapelessCountFamily unlockedByHas(TagKey<Item> tag) {
        Objects.requireNonNull(tag, "tag");

        if (!this.unlockTags.contains(tag)) {
            this.unlockTags.add(tag);
        }

        return this;
    }

    public void save(String folder) {
        if (this.steps.isEmpty()) {
            throw new IllegalStateException(
                    "Shapeless count family requires at least one step"
            );
        }

        String normalizedFolder =
                folder == null ? "" : folder.trim();

        Set<ResourceLocation> generatedIds = new HashSet<>();

        for (Step step : this.steps) {
            saveStep(
                    step,
                    normalizedFolder,
                    generatedIds
            );

            if (this.reversible) {
                validateInputCount(
                        step.result(),
                        step.resultCount()
                );

                saveStep(
                        new Step(
                                step.result(),
                                step.resultCount(),
                                step.input(),
                                step.inputCount()
                        ),
                        normalizedFolder,
                        generatedIds
                );
            }
        }
    }

    private void saveStep(
            Step step,
            String folder,
            Set<ResourceLocation> generatedIds
    ) {
        ResourceLocation id = this.dsl.id(
                RecipeDSL.joinFolder(
                        folder,
                        RecipeDSL.keyOf(step.input())
                                + "_to_"
                                + RecipeDSL.keyOf(step.result())
                )
        );

        if (!generatedIds.add(id)) {
            throw new IllegalStateException(
                    "Duplicate generated shapeless count family recipe id: "
                            + id
                            + ". Remove the duplicate/reverse step or disable reversible()."
            );
        }

        ShapelessCrafting recipe = this.dsl.shapelessCountToCount(
                this.category,
                step.result(),
                step.resultCount(),
                step.input(),
                step.inputCount()
        );

        for (ItemLike item : this.unlockItems) {
            recipe.unlockedByHas(item);
        }

        for (TagKey<Item> tag : this.unlockTags) {
            recipe.unlockedByHas(tag);
        }

        recipe.save(id);
    }

    private static void validateInputCount(
            ItemLike input,
            int count
    ) {
        if (count < 1 || count > 9) {
            throw new IllegalArgumentException(
                    "Shapeless recipe input count for "
                            + RecipeDSL.keyOf(input)
                            + " must be in [1,9], got "
                            + count
            );
        }
    }

    private static void validateResultCount(
            ItemLike result,
            int count
    ) {
        if (count < 1) {
            throw new IllegalArgumentException(
                    "Shapeless recipe result count for "
                            + RecipeDSL.keyOf(result)
                            + " must be >= 1, got "
                            + count
            );
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

    private record Step(
            ItemLike input,
            int inputCount,
            ItemLike result,
            int resultCount
    ) {}
}
