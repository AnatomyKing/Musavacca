package space.anatomyuniverse.musavacca.data.recipes.component;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.registries.DeferredRegister;
import space.anatomyuniverse.musavacca.data.recipes.ComponentRecipeDSL;

import java.util.function.Supplier;

public final class ComponentStonecutting {
    private static Supplier<RecipeSerializer<ComponentRecipe>> serializer;

    public static void register(DeferredRegister<RecipeSerializer<?>> serializers) {
        serializer = serializers.register("component_stonecutting", ComponentSerializer::new);
    }

    public static RecipeOutput output(
            RecipeOutput output,
            ComponentRecipeDSL.Source source
    ) {
        return ComponentRecipeDSL.wrappingOutput(output, recipe -> {
            if (!(recipe instanceof StonecutterRecipe stonecutting)) {
                throw new IllegalStateException("Component stonecutting output received " + recipe.getClass().getName());
            }

            return new ComponentRecipe(stonecutting, source);
        });
    }

    public static final class ComponentRecipe extends StonecutterRecipe {
        private final StonecutterRecipe base;
        private final ComponentRecipeDSL.Source source;

        public ComponentRecipe(
                StonecutterRecipe base,
                ComponentRecipeDSL.Source source
        ) {
            //? if <1.21.3 {
            /*super(
                    base.getGroup(),
                    base.getIngredients().get(0),
                    base.getResultItem(RegistryAccess.EMPTY)
            );
            *///?} else {
            super(
                    base.group(),
                    base.input(),
                    base.assemble(new SingleRecipeInput(ItemStack.EMPTY), RegistryAccess.EMPTY)
            );
            //?}
            this.base = base;
            this.source = source;
        }

        public StonecutterRecipe base() {
            return this.base;
        }

        public ComponentRecipeDSL.Source source() {
            return this.source;
        }

        @Override
        public boolean matches(SingleRecipeInput input, Level level) {
            return super.matches(input, level)
                    && ComponentRecipeDSL.requirementsMatch(
                            this.source,
                            input.getItem(0),
                            level.registryAccess()
                    );
        }

        @Override
        public ItemStack assemble(SingleRecipeInput input, HolderLookup.Provider registries) {
            ItemStack result = super.assemble(input, registries);
            ComponentRecipeDSL.transfer(this.source, input.getItem(0), result, registries);
            return result;
        }

        @Override
        @SuppressWarnings({"unchecked", "rawtypes"})
        public RecipeSerializer<StonecutterRecipe> getSerializer() {
            return (RecipeSerializer) serializer.get();
        }
    }

    private static final class ComponentSerializer implements RecipeSerializer<ComponentRecipe> {
        private static final MapCodec<ComponentRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                RecipeSerializer.STONECUTTER.codec().codec().fieldOf("base").forGetter(ComponentRecipe::base),
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

    private ComponentStonecutting() {}
}




