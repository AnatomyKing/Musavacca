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
//? if >=1.21.2 {

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
        //? if >=26.1 {
        /*serializer = serializers.register("component_smithing_transform", () -> new RecipeSerializer<>(ComponentSerializer.CODEC, ComponentSerializer.STREAM_CODEC));
        *///?} else {
        serializer = serializers.register("component_smithing_transform", ComponentSerializer::new);
        //?}
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
        public ItemStack assemble(SmithingRecipeInput input

                //? if <26.1 {
                , HolderLookup.Provider registries
                //?}
        ) {

            //? if >=26.1 {
            /*HolderLookup.Provider registries = ComponentRecipeDSL.runtimeRegistries();
            *///?}
            ItemStack result = this.base.assemble(input

                    //? if <26.1 {
                    , registries
                    //?}
            );

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

        //? if >=1.21.2 {
        @Override
        public String group() {
            return this.base.group();
        }
        //?} else {
        /*@Override
        public String getGroup() {
            return this.base.getGroup();
        }
        *///?}

        //? if >=1.21.2 {
        @Override
        public boolean showNotification() {
            return this.base.showNotification();
        }
        //?} else {
        /*@Override
        public boolean canCraftInDimensions(int width, int height) {
            return this.base.canCraftInDimensions(width, height);
        }
        *///?}

        //? if >=1.21.2 {
        @Override
        public PlacementInfo placementInfo() {
            return this.base.placementInfo();
        }
        //?} else {
        /*@Override
        public ItemStack getResultItem(HolderLookup.Provider registries) {
            return this.base.getResultItem(registries);
        }
        *///?}

        //? if >=1.21.2 {
        @Override
        public RecipeBookCategory recipeBookCategory() {
            return this.base.recipeBookCategory();
        }
        //?} else {
        /*@Override
        public NonNullList<Ingredient> getIngredients() {
            return this.base.getIngredients();
        }
        *///?}

        //? if >=1.21.2 {
        @Override
        public List<RecipeDisplay> display() {
            return this.base.display();
        }
        //?} else {
        /*@Override
        public boolean isTemplateIngredient(ItemStack stack) {
            return this.base.isTemplateIngredient(stack);
        }
        *///?}

        //? if >=1.21.2 {
        @Override
        public Optional<Ingredient> templateIngredient() {
            return this.base.templateIngredient();
        }
        //?} else {
        /*@Override
        public boolean isBaseIngredient(ItemStack stack) {
            return this.base.isBaseIngredient(stack);
        }
        *///?}

        //? if >=1.21.5 {
        @Override
        public Ingredient baseIngredient() {
            return this.base.baseIngredient();
        }

        @Override
        public Optional<Ingredient> additionIngredient() {
            return this.base.additionIngredient();
        }
        //?} else if >=1.21.2 <1.21.5 {
        /*@Override
        public Optional<Ingredient> baseIngredient() {
            return this.base.baseIngredient();
        }

        @Override
        public Optional<Ingredient> additionIngredient() {
            return this.base.additionIngredient();
        }
        *///?} else {
        /*@Override
        public boolean isAdditionIngredient(ItemStack stack) {
            return this.base.isAdditionIngredient(stack);
        }
        *///?}

    }

    //? if >=26.1 {
    /*private static final class ComponentSerializer {
        private static final MapCodec<ComponentRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                net.minecraft.world.item.crafting.SmithingTransformRecipe.SERIALIZER.codec().codec().fieldOf("base").forGetter(ComponentRecipe::base),
    *///?} else {
    private static final class ComponentSerializer implements RecipeSerializer<ComponentRecipe> {
        private static final MapCodec<ComponentRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                RecipeSerializer.SMITHING_TRANSFORM.codec().codec().fieldOf("base").forGetter(ComponentRecipe::base),
    //?}
                ComponentRecipeDSL.SmithingSource.CODEC.listOf().fieldOf("sources").forGetter(ComponentRecipe::sources)
        ).apply(instance, ComponentRecipe::new));

        private static final StreamCodec<RegistryFriendlyByteBuf, ComponentRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        //? if >=26.1 {
                        /*net.minecraft.world.item.crafting.SmithingTransformRecipe.SERIALIZER.streamCodec(),
                        *///?} else {
                        RecipeSerializer.SMITHING_TRANSFORM.streamCodec(),
                        //?}
                        ComponentRecipe::base,
                        ByteBufCodecs.fromCodecWithRegistries(ComponentRecipeDSL.SmithingSource.CODEC.listOf()),
                        ComponentRecipe::sources,
                        ComponentRecipe::new
                );

        //? if <26.1 {
    
        @Override
        public MapCodec<ComponentRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ComponentRecipe> streamCodec() {
            return STREAM_CODEC;
        }
        //?}
    }

    private ComponentSmithingTransforms() {}
}
