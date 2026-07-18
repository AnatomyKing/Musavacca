package space.anatomyuniverse.musavacca.data.recipes.component;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredRegister;
import space.anatomyuniverse.musavacca.data.recipes.ComponentRecipeDSL;
import space.anatomyuniverse.musavacca.data.recipes.RecipeDSL;

import java.util.function.Supplier;

//? if >=1.21.3
import net.minecraft.world.item.crafting.RecipeBookCategory;

public final class ComponentSmelting {
    private static Supplier<RecipeSerializer<ComponentRecipe>> serializer;

    public static void register(DeferredRegister<RecipeSerializer<?>> serializers) {
        serializer = serializers.register("component_smelting", ComponentSerializer::new);
    }

    public static RecipeOutput output(
            RecipeOutput output,
            ComponentRecipeDSL.Source source
    ) {
        return ComponentRecipeDSL.wrappingOutput(output, recipe -> {
            if (!(recipe instanceof AbstractCookingRecipe cooking)) {
                throw new IllegalStateException("Component smelting output received " + recipe.getClass().getName());
            }

            return new ComponentRecipe(cooking, source);
        });
    }

    public static final class ComponentRecipe extends ComponentRecipeDSL.ComponentCookingRecipe {
        public ComponentRecipe(
                AbstractCookingRecipe base,
                ComponentRecipeDSL.Source source
        ) {
            super(RecipeDSL.RecipeKind.SMELTING, base, source);
        }

        @Override
        public RecipeSerializer<? extends AbstractCookingRecipe> getSerializer() {
            return serializer.get();
        }

        //? if >=1.21.3 {
        @Override
        public RecipeBookCategory recipeBookCategory() {
            return base().recipeBookCategory();
        }
        //?}
    }

    private static final class ComponentSerializer implements RecipeSerializer<ComponentRecipe> {
        private static final MapCodec<ComponentRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ComponentRecipeDSL.cookingBaseCodec(RecipeDSL.RecipeKind.SMELTING)
                        .fieldOf("base").forGetter(ComponentRecipe::base),
                ComponentRecipeDSL.SOURCE_CODEC.fieldOf("source").forGetter(ComponentRecipe::source)
        ).apply(instance, ComponentRecipe::new));

        private static final StreamCodec<RegistryFriendlyByteBuf, ComponentRecipe> STREAM_CODEC =
                ByteBufCodecs.fromCodecWithRegistries(CODEC.codec());

        @Override
        public MapCodec<ComponentRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ComponentRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }

    private ComponentSmelting() {}
}
