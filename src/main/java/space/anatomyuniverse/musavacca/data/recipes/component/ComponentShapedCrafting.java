package space.anatomyuniverse.musavacca.data.recipes.component;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.registries.DeferredRegister;
import space.anatomyuniverse.musavacca.data.recipes.ComponentRecipeDSL;

//? if >=1.21.3 {
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
//?}

import net.minecraft.data.recipes.RecipeOutput;
import java.util.List;
import java.util.function.Supplier;

public final class ComponentShapedCrafting {
    private static Supplier<RecipeSerializer<ComponentRecipe>> serializer;

    public static void register(DeferredRegister<RecipeSerializer<?>> serializers) {
        serializer = serializers.register("component_shaped", ComponentSerializer::new);
    }

    public static RecipeOutput output(
            RecipeOutput output,
            List<ComponentRecipeDSL.Source> sources
    ) {
        if (sources.isEmpty()) {
            return output;
        }

        return ComponentRecipeDSL.wrappingOutput(output, recipe -> {
            if (!(recipe instanceof ShapedRecipe shaped)) {
                throw new IllegalStateException("Component shaped output received " + recipe.getClass().getName());
            }

            return new ComponentRecipe(shaped, sources);
        });
    }

    public static final class ComponentRecipe implements CraftingRecipe {
        private final ShapedRecipe base;
        private final List<ComponentRecipeDSL.Source> sources;

        public ComponentRecipe(
                ShapedRecipe base,
                List<ComponentRecipeDSL.Source> sources
        ) {
            this.base = base;
            this.sources = List.copyOf(sources);
        }

        public ShapedRecipe base() {
            return this.base;
        }

        public List<ComponentRecipeDSL.Source> sources() {
            return this.sources;
        }

        @Override
        public boolean matches(CraftingInput input, Level level) {
            return this.base.matches(input, level)
                    && ComponentRecipeDSL.assignSourceStacks(
                            input,
                            this.sources,
                            level.registryAccess()
                    ) != null;
        }

        @Override
        public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
            ItemStack result = this.base.assemble(input, registries);
            List<ItemStack> stacks = ComponentRecipeDSL.assignSourceStacks(input, this.sources, registries);

            if (stacks != null) {
                for (int i = 0; i < this.sources.size(); ++i) {
                    ComponentRecipeDSL.transfer(this.sources.get(i), stacks.get(i), result, registries);
                }
            }

            return result;
        }

        @Override
        public RecipeSerializer<? extends CraftingRecipe> getSerializer() {
            return serializer.get();
        }

        @Override
        public RecipeType<CraftingRecipe> getType() {
            return RecipeType.CRAFTING;
        }

        //? if <1.21.3 {
        /*@Override
        public String getGroup() {
            return this.base.getGroup();
        }
        *///?} else {
        @Override
        public String group() {
            return this.base.group();
        }
        //?}

        @Override
        public boolean showNotification() {
            return this.base.showNotification();
        }

        @Override
        public CraftingBookCategory category() {
            return this.base.category();
        }

        //? if <1.21.3 {
        /*@Override
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
        *///?} else {
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
        //?}
    }

    private static final class ComponentSerializer implements RecipeSerializer<ComponentRecipe> {
        private static final MapCodec<ComponentRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                RecipeSerializer.SHAPED_RECIPE.codec().codec().fieldOf("base").forGetter(ComponentRecipe::base),
                ComponentRecipeDSL.SOURCE_CODEC.listOf().fieldOf("sources").forGetter(ComponentRecipe::sources)
        ).apply(instance, ComponentRecipe::new));

        private static final StreamCodec<RegistryFriendlyByteBuf, ComponentRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        RecipeSerializer.SHAPED_RECIPE.streamCodec(),
                        ComponentRecipe::base,
                        ByteBufCodecs.fromCodecWithRegistries(ComponentRecipeDSL.SOURCE_CODEC.listOf()),
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

    private ComponentShapedCrafting() {}
}
