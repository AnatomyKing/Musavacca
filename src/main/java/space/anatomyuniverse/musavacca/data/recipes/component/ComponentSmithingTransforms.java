package space.anatomyuniverse.musavacca.data.recipes.component;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.registries.DeferredRegister;
import space.anatomyuniverse.musavacca.data.recipes.ComponentRecipeDSL;

//? if >=1.21.3 {
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.display.RecipeDisplay;

import java.util.Optional;
//?}

import java.util.List;
import java.util.function.Supplier;

public final class ComponentSmithingTransforms {
    private static Supplier<RecipeSerializer<ComponentRecipe>> serializer;

    public static void register(DeferredRegister<RecipeSerializer<?>> serializers) {
        serializer = serializers.register("component_smithing_transform", ComponentSerializer::new);
    }

    public static RecipeOutput output(
            RecipeOutput output,
            List<ComponentRecipeDSL.SmithingSource> sources
    ) {
        if (sources.isEmpty()) {
            return output;
        }

        return ComponentRecipeDSL.wrappingOutput(output, recipe -> {
            if (!(recipe instanceof SmithingTransformRecipe smithing)) {
                throw new IllegalStateException("Component smithing output received " + recipe.getClass().getName());
            }

            return new ComponentRecipe(smithing, sources);
        });
    }

    public static final class ComponentRecipe implements SmithingRecipe {
        private final SmithingTransformRecipe base;
        private final List<ComponentRecipeDSL.SmithingSource> sources;

        public ComponentRecipe(
                SmithingTransformRecipe base,
                List<ComponentRecipeDSL.SmithingSource> sources
        ) {
            this.base = base;
            this.sources = List.copyOf(sources);
        }

        public SmithingTransformRecipe base() {
            return this.base;
        }

        public List<ComponentRecipeDSL.SmithingSource> sources() {
            return this.sources;
        }

        @Override
        public boolean matches(SmithingRecipeInput input, Level level) {
            if (!this.base.matches(input, level)) {
                return false;
            }

            for (ComponentRecipeDSL.SmithingSource source : this.sources) {
                if (!ComponentRecipeDSL.requirementsMatch(
                        source.source(),
                        input.getItem(source.slot()),
                        level.registryAccess()
                )) {
                    return false;
                }
            }

            return true;
        }

        @Override
        public ItemStack assemble(SmithingRecipeInput input, HolderLookup.Provider registries) {
            ItemStack result = this.base.assemble(input, registries);

            for (ComponentRecipeDSL.SmithingSource source : this.sources) {
                ComponentRecipeDSL.transfer(
                        source.source(),
                        input.getItem(source.slot()),
                        result,
                        registries
                );
            }

            return result;
        }

        @Override
        public RecipeSerializer<? extends SmithingRecipe> getSerializer() {
            return serializer.get();
        }

        //? if <1.21.3 {
        /*@Override
        public String getGroup() {
            return this.base.getGroup();
        }

        @Override
        public boolean canCraftInDimensions(int width, int height) {
            return this.base.canCraftInDimensions(width, height);
        }

        @Override
        public ItemStack getResultItem(HolderLookup.Provider registries) {
            return this.base.getResultItem(registries);
        }

        @Override
        public NonNullList<Ingredient> getIngredients() {
            return this.base.getIngredients();
        }

        @Override
        public boolean isTemplateIngredient(ItemStack stack) {
            return this.base.isTemplateIngredient(stack);
        }

        @Override
        public boolean isBaseIngredient(ItemStack stack) {
            return this.base.isBaseIngredient(stack);
        }

        @Override
        public boolean isAdditionIngredient(ItemStack stack) {
            return this.base.isAdditionIngredient(stack);
        }
        *///?} else {
        @Override
        public String group() {
            return this.base.group();
        }

        @Override
        public boolean showNotification() {
            return this.base.showNotification();
        }

        @Override
        public PlacementInfo placementInfo() {
            return this.base.placementInfo();
        }

        @Override
        public RecipeBookCategory recipeBookCategory() {
            return this.base.recipeBookCategory();
        }

        @Override
        public List<RecipeDisplay> display() {
            return this.base.display();
        }

        @Override
        public Optional<Ingredient> templateIngredient() {
            return this.base.templateIngredient();
        }

        @Override
        public Ingredient baseIngredient() {
            return this.base.baseIngredient();
        }

        @Override
        public Optional<Ingredient> additionIngredient() {
            return this.base.additionIngredient();
        }
        //?}
    }

    private static final class ComponentSerializer implements RecipeSerializer<ComponentRecipe> {
        private static final MapCodec<ComponentRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                RecipeSerializer.SMITHING_TRANSFORM.codec().codec().fieldOf("base").forGetter(ComponentRecipe::base),
                ComponentRecipeDSL.SmithingSource.CODEC.listOf().fieldOf("sources").forGetter(ComponentRecipe::sources)
        ).apply(instance, ComponentRecipe::new));

        private static final StreamCodec<RegistryFriendlyByteBuf, ComponentRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        RecipeSerializer.SMITHING_TRANSFORM.streamCodec(),
                        ComponentRecipe::base,
                        ByteBufCodecs.fromCodecWithRegistries(ComponentRecipeDSL.SmithingSource.CODEC.listOf()),
                        ComponentRecipe::sources,
                        ComponentRecipe::new
                );

        @Override
        public MapCodec<ComponentRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ComponentRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }

    private ComponentSmithingTransforms() {}
}
