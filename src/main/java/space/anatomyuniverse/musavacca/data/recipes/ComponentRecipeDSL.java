package space.anatomyuniverse.musavacca.data.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.server.MinecraftServer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import space.anatomyuniverse.musavacca.data.recipes.component.ComponentBlasting;
import space.anatomyuniverse.musavacca.data.recipes.component.ComponentCampfireCooking;
import space.anatomyuniverse.musavacca.data.recipes.component.ComponentShapedCrafting;
import space.anatomyuniverse.musavacca.data.recipes.component.ComponentShapelessCrafting;
import space.anatomyuniverse.musavacca.data.recipes.component.ComponentSmelting;
import space.anatomyuniverse.musavacca.data.recipes.component.ComponentSmithingTransforms;
import space.anatomyuniverse.musavacca.data.recipes.component.ComponentSmoking;
import space.anatomyuniverse.musavacca.data.recipes.component.ComponentStonecutting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

//? if >=1.21.3
import net.minecraft.world.item.crafting.RecipeBookCategory;

public final class ComponentRecipeDSL {
    public static final String MOD_ID = "musavacca";

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, MOD_ID);

    public static final Codec<Selection> SELECTION_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BuiltInRegistries.DATA_COMPONENT_TYPE.byNameCodec().fieldOf("component").forGetter(Selection::component),
            Codec.STRING.listOf().optionalFieldOf("path", List.of()).forGetter(Selection::path)
    ).apply(instance, Selection::new));

    public static final Codec<Rule> RULE_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            SELECTION_CODEC.fieldOf("selection").forGetter(Rule::selection),
            Codec.BOOL.fieldOf("required").forGetter(Rule::required),
            Codec.BOOL.fieldOf("transfer").forGetter(Rule::transfer)
    ).apply(instance, Rule::new));

    public static final Codec<Source> SOURCE_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Ingredient.CODEC.fieldOf("ingredient").forGetter(Source::ingredient),
            Codec.intRange(1, Integer.MAX_VALUE).optionalFieldOf("amount", 1).forGetter(Source::amount),
            RULE_CODEC.listOf().fieldOf("rules").forGetter(Source::rules)
    ).apply(instance, Source::decoded));

    public record Selection(
            DataComponentType<?> component,
            List<String> path
    ) {
        public Selection {
            Objects.requireNonNull(component, "component");
            path = List.copyOf(path);
        }

        public boolean wholeComponent() {
            return this.path.isEmpty();
        }
    }

    public record Rule(
            Selection selection,
            boolean required,
            boolean transfer
    ) {}

    public record SmithingSource(
            int slot,
            Source source
    ) {
        public static final Codec<SmithingSource> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("slot").forGetter(SmithingSource::slot),
                SOURCE_CODEC.fieldOf("source").forGetter(SmithingSource::source)
        ).apply(instance, SmithingSource::new));

        public SmithingSource {
            if (slot < 0 || slot > 2) {
                throw new IllegalArgumentException("Smithing source slot must be 0, 1, or 2");
            }
        }
    }

    public record SourceMatch(
            Source source,
            int slot,
            ItemStack stack
    ) {}

    public static final class ComponentPath {
        private final DataComponentType<?> component;
        private final List<String> path;

        private ComponentPath(DataComponentType<?> component) {
            this(component, List.of());
        }

        private ComponentPath(DataComponentType<?> component, List<String> path) {
            this.component = Objects.requireNonNull(component, "component");
            this.path = List.copyOf(path);
        }

        public ComponentPath path(String path) {
            Objects.requireNonNull(path, "path");

            List<String> result = new ArrayList<>(this.path);
            Arrays.stream(path.split("\\."))
                    .filter(part -> !part.isBlank())
                    .forEach(result::add);

            return new ComponentPath(this.component, result);
        }

        public ComponentPath entry(String entry) {
            Objects.requireNonNull(entry, "entry");
            List<String> result = new ArrayList<>(this.path);
            result.add(entry);
            return new ComponentPath(this.component, result);
        }

        public ComponentPath entry(ResourceLocation entry) {
            return entry(entry.toString());
        }

        public ComponentPath entry(ResourceKey<?> entry) {
            return entry(entry.location());
        }

        private Selection selection() {
            return new Selection(this.component, this.path);
        }
    }

    public static final class Source implements RecipeDSL.ExtendedIngredient {
        private SizedIngredient sizedIngredient;
        private final List<Rule> rules;

        private Source(Ingredient ingredient) {
            this(new SizedIngredient(ingredient, 1), new ArrayList<>());
        }

        private Source(SizedIngredient sizedIngredient, List<Rule> rules) {
            this.sizedIngredient = Objects.requireNonNull(sizedIngredient, "sizedIngredient");
            this.rules = rules;
        }

        private static Source decoded(Ingredient ingredient, int amount, List<Rule> rules) {
            return new Source(new SizedIngredient(ingredient, amount), new ArrayList<>(rules));
        }

        public Source amount(int amount) {
            if (amount < 1) {
                throw new IllegalArgumentException("amount must be >= 1");
            }

            this.sizedIngredient = new SizedIngredient(this.sizedIngredient.ingredient(), amount);
            return this;
        }

        public Source require(DataComponentType<?> component) {
            return require(new Selection(component, List.of()));
        }

        public Source require(Supplier<? extends DataComponentType<?>> component) {
            return require(component.get());
        }

        public Source require(ComponentPath component) {
            return require(component.selection());
        }

        public Source transfer(DataComponentType<?> component) {
            return transfer(new Selection(component, List.of()));
        }

        public Source transfer(Supplier<? extends DataComponentType<?>> component) {
            return transfer(component.get());
        }

        public Source transfer(ComponentPath component) {
            return transfer(component.selection());
        }

        private Source require(Selection selection) {
            this.rules.add(new Rule(selection, true, false));
            return this;
        }

        private Source transfer(Selection selection) {
            this.rules.add(new Rule(selection, false, true));
            return this;
        }

        @Override
        public Ingredient ingredient() {
            return this.sizedIngredient.ingredient();
        }

        public SizedIngredient sizedIngredient() {
            return this.sizedIngredient;
        }

        public int amount() {
            return this.sizedIngredient.count();
        }

        public List<Rule> rules() {
            return List.copyOf(this.rules);
        }

        public boolean hasAmount() {
            return amount() != 1;
        }

        public boolean hasRules() {
            return !this.rules.isEmpty();
        }

        public boolean hasFeatures() {
            return hasAmount() || hasRules();
        }

        @Override
        public RecipeOutput decorate(
                RecipeOutput output,
                RecipeDSL.RecipeKind kind,
                List<RecipeDSL.ExtendedIngredient> sources,
                HolderLookup.Provider registries
        ) {
            return ComponentRecipeDSL.decorate(output, kind, sources, registries);
        }
    }

    public abstract static class ComponentCookingRecipe extends AbstractCookingRecipe {
        private final RecipeDSL.RecipeKind kind;
        private final AbstractCookingRecipe base;
        private final Source source;

        protected ComponentCookingRecipe(
                RecipeDSL.RecipeKind kind,
                AbstractCookingRecipe base,
                Source source
        ) {
            //? if <1.21.3 {
            /*super(
                    cookingType(kind),
                    base.getGroup(),
                    base.category(),
                    base.getIngredients().get(0),
                    base.getResultItem(RegistryAccess.EMPTY),
                    base.getExperience(),
                    base.getCookingTime()
            );
            *///?} else {
            super(
                    base.group(),
                    base.category(),
                    base.input(),
                    base.assemble(new SingleRecipeInput(ItemStack.EMPTY), RegistryAccess.EMPTY),
                    base.experience(),
                    base.cookingTime()
            );
            //?}
            this.kind = kind;
            this.base = base;
            this.source = source;
        }

        public AbstractCookingRecipe base() {
            return this.base;
        }

        public Source source() {
            return this.source;
        }

        @Override
        public boolean matches(SingleRecipeInput input, Level level) {
            return super.matches(input, level)
                    && requirementsMatch(this.source, input.getItem(0), level.registryAccess());
        }

        @Override
        public ItemStack assemble(SingleRecipeInput input, HolderLookup.Provider registries) {
            ItemStack result = super.assemble(input, registries);
            transfer(this.source, input.getItem(0), result, registries);
            return result;
        }

        @Override
        public RecipeType<? extends AbstractCookingRecipe> getType() {
            return cookingType(this.kind);
        }

        //? if >=1.21.3 {
        @Override
        protected net.minecraft.world.item.Item furnaceIcon() {
            return switch (this.kind) {
                case SMELTING -> Items.FURNACE;
                case BLASTING -> Items.BLAST_FURNACE;
                case SMOKING -> Items.SMOKER;
                case CAMPFIRE -> Items.CAMPFIRE;
                default -> throw new IllegalStateException("Not a cooking recipe kind: " + this.kind);
            };
        }

        @Override
        public RecipeBookCategory recipeBookCategory() {
            return this.base.recipeBookCategory();
        }
        //?}
    }

    public static Source source(ItemLike item) {
        return new Source(Ingredient.of(item));
    }

    public static Source source(ItemLike item, int amount) {
        return source(item).amount(amount);
    }

    public static Source source(Ingredient ingredient) {
        return new Source(ingredient);
    }

    public static Source source(Ingredient ingredient, int amount) {
        return source(ingredient).amount(amount);
    }

    public static ComponentPath component(DataComponentType<?> component) {
        return new ComponentPath(component);
    }

    public static ComponentPath component(Supplier<? extends DataComponentType<?>> component) {
        return component(component.get());
    }

    public static void register(IEventBus modBus) {
        ComponentShapedCrafting.register(RECIPE_SERIALIZERS);
        ComponentShapelessCrafting.register(RECIPE_SERIALIZERS);
        ComponentSmelting.register(RECIPE_SERIALIZERS);
        ComponentBlasting.register(RECIPE_SERIALIZERS);
        ComponentSmoking.register(RECIPE_SERIALIZERS);
        ComponentCampfireCooking.register(RECIPE_SERIALIZERS);
        ComponentStonecutting.register(RECIPE_SERIALIZERS);
        ComponentSmithingTransforms.register(RECIPE_SERIALIZERS);
        RECIPE_SERIALIZERS.register(modBus);
    }

    public static RecipeOutput decorate(
            RecipeOutput output,
            RecipeDSL.RecipeKind kind,
            List<RecipeDSL.ExtendedIngredient> sources,
            HolderLookup.Provider registries
    ) {
        if (!hasFeatures(sources)) {
            return output;
        }

        if (hasAmounts(sources) && kind != RecipeDSL.RecipeKind.SHAPED && kind != RecipeDSL.RecipeKind.SHAPELESS) {
            throw new IllegalArgumentException(
                    "Ingredient amounts are only supported for shaped and shapeless crafting recipes"
            );
        }

        return switch (kind) {
            case SHAPED -> ComponentShapedCrafting.output(output, craftingSources(sources));
            case SHAPELESS -> ComponentShapelessCrafting.output(output, craftingSources(sources));
            case SMELTING -> ComponentSmelting.output(output, singleComponentSource(sources));
            case BLASTING -> ComponentBlasting.output(output, singleComponentSource(sources));
            case SMOKING -> ComponentSmoking.output(output, singleComponentSource(sources));
            case CAMPFIRE -> ComponentCampfireCooking.output(output, singleComponentSource(sources));
            case STONECUTTING -> ComponentStonecutting.output(output, singleComponentSource(sources));
            case SMITHING_TRANSFORM -> ComponentSmithingTransforms.output(output, smithingSources(sources));
            default -> output;
        };
    }

    public static List<Source> craftingSources(
            List<RecipeDSL.ExtendedIngredient> sources
    ) {
        List<Source> result = new ArrayList<>();

        for (RecipeDSL.ExtendedIngredient source : sources) {
            if (source instanceof Source componentSource && componentSource.hasFeatures()) {
                result.add(componentSource);
            }
        }

        return List.copyOf(result);
    }

    public static List<Source> componentSources(
            List<RecipeDSL.ExtendedIngredient> sources
    ) {
        List<Source> result = new ArrayList<>();

        for (RecipeDSL.ExtendedIngredient source : sources) {
            if (source instanceof Source componentSource && componentSource.hasRules()) {
                result.add(componentSource);
            }
        }

        return List.copyOf(result);
    }

    public static Source singleComponentSource(
            List<RecipeDSL.ExtendedIngredient> sources
    ) {
        List<Source> componentSources = componentSources(sources);

        if (componentSources.size() != 1) {
            throw new IllegalStateException("Single-input component recipe requires exactly one component source");
        }

        return componentSources.getFirst();
    }

    public static List<SmithingSource> smithingSources(
            List<RecipeDSL.ExtendedIngredient> sources
    ) {
        List<SmithingSource> result = new ArrayList<>();

        for (RecipeDSL.ExtendedIngredient source : sources) {
            if (source instanceof RecipeDSL.SlottedExtendedIngredient slotted
                    && slotted.delegate() instanceof Source componentSource
                    && componentSource.hasRules()) {
                result.add(new SmithingSource(slotted.slot(), componentSource));
            }
        }

        return List.copyOf(result);
    }

    public static List<SourceMatch> assignSourceMatches(
            CraftingInput input,
            List<Source> sources,
            HolderLookup.Provider registries
    ) {
        List<SourceMatch> result = new ArrayList<>();
        boolean[] usedSlots = new boolean[input.size()];

        return assignSourceMatch(input, sources, registries, 0, usedSlots, result)
                ? List.copyOf(result)
                : null;
    }

    public static List<ItemStack> assignSourceStacks(
            CraftingInput input,
            List<Source> sources,
            HolderLookup.Provider registries
    ) {
        List<SourceMatch> matches = assignSourceMatches(input, sources, registries);
        if (matches == null) {
            return null;
        }

        return matches.stream().map(SourceMatch::stack).toList();
    }

    private static boolean assignSourceMatch(
            CraftingInput input,
            List<Source> sources,
            HolderLookup.Provider registries,
            int sourceIndex,
            boolean[] usedSlots,
            List<SourceMatch> result
    ) {
        if (sourceIndex >= sources.size()) {
            return true;
        }

        Source source = sources.get(sourceIndex);

        for (int slot = 0; slot < input.size(); ++slot) {
            if (usedSlots[slot]) {
                continue;
            }

            ItemStack stack = input.getItem(slot);
            if (!requirementsMatch(source, stack, registries)) {
                continue;
            }

            usedSlots[slot] = true;
            result.add(new SourceMatch(source, slot, stack));

            if (assignSourceMatch(input, sources, registries, sourceIndex + 1, usedSlots, result)) {
                return true;
            }

            result.removeLast();
            usedSlots[slot] = false;
        }

        return false;
    }

    public static boolean requirementsMatch(
            Source source,
            ItemStack stack,
            HolderLookup.Provider registries
    ) {
        if (stack.isEmpty() || !source.sizedIngredient().test(stack)) {
            return false;
        }

        for (Rule rule : source.rules()) {
            if (rule.required() && !contains(stack, rule.selection(), registries)) {
                return false;
            }
        }

        return true;
    }

    public static NonNullList<ItemStack> craftingRemainders(
            CraftingRecipe base,
            CraftingInput input,
            List<Source> sources
    ) {
        NonNullList<ItemStack> remainders = base.getRemainingItems(input);
        if (!sources.stream().anyMatch(Source::hasAmount)) {
            return remainders;
        }

        List<SourceMatch> matches = assignSourceMatches(input, sources, runtimeRegistries());
        if (matches == null) {
            return remainders;
        }

        for (SourceMatch match : matches) {
            int amount = match.source().amount();
            if (amount <= 1) {
                continue;
            }

            ItemStack remainder = remainders.get(match.slot());
            if (!remainder.isEmpty()) {
                long scaled = (long) remainder.getCount() * amount;
                remainder.setCount((int) Math.min(Integer.MAX_VALUE, scaled));
            }

            match.stack().shrink(amount - 1);
        }

        return remainders;
    }

    public static void transfer(
            Source source,
            ItemStack from,
            ItemStack to,
            HolderLookup.Provider registries
    ) {
        for (Rule rule : source.rules()) {
            if (rule.transfer()) {
                transfer(rule.selection(), from, to, registries);
            }
        }
    }

    public static RecipeOutput wrappingOutput(
            RecipeOutput output,
            Function<Recipe<?>, Recipe<?>> wrapper
    ) {
        return new RecipeOutput() {
            //? if <1.21.3 {
            /*@Override
            public void accept(
                    ResourceLocation id,
                    Recipe<?> recipe,
                    AdvancementHolder advancement,
                    ICondition... conditions
            ) {
                output.accept(id, wrapper.apply(recipe), advancement, conditions);
            }
            *///?} else {
            @Override
            public void accept(
                    ResourceKey<Recipe<?>> id,
                    Recipe<?> recipe,
                    AdvancementHolder advancement,
                    ICondition... conditions
            ) {
                output.accept(id, wrapper.apply(recipe), advancement, conditions);
            }
            //?}

            @Override
            public Advancement.Builder advancement() {
                return output.advancement();
            }

            //? if >=1.21.3 {
            @Override
            public void includeRootAdvancement() {
                output.includeRootAdvancement();
            }
            //?}
        };
    }

    @SuppressWarnings("unchecked")
    public static Codec<AbstractCookingRecipe> cookingBaseCodec(RecipeDSL.RecipeKind kind) {
        return (Codec<AbstractCookingRecipe>) (Codec<?>) cookingVanillaSerializer(kind).codec().codec();
    }

    private static RecipeSerializer<? extends AbstractCookingRecipe> cookingVanillaSerializer(
            RecipeDSL.RecipeKind kind
    ) {
        return switch (kind) {
            case SMELTING -> RecipeSerializer.SMELTING_RECIPE;
            case BLASTING -> RecipeSerializer.BLASTING_RECIPE;
            case SMOKING -> RecipeSerializer.SMOKING_RECIPE;
            case CAMPFIRE -> RecipeSerializer.CAMPFIRE_COOKING_RECIPE;
            default -> throw new IllegalStateException("Not a cooking recipe kind: " + kind);
        };
    }

    private static RecipeType<? extends AbstractCookingRecipe> cookingType(
            RecipeDSL.RecipeKind kind
    ) {
        return switch (kind) {
            case SMELTING -> RecipeType.SMELTING;
            case BLASTING -> RecipeType.BLASTING;
            case SMOKING -> RecipeType.SMOKING;
            case CAMPFIRE -> RecipeType.CAMPFIRE_COOKING;
            default -> throw new IllegalStateException("Not a cooking recipe kind: " + kind);
        };
    }

    private static boolean hasFeatures(List<RecipeDSL.ExtendedIngredient> sources) {
        for (RecipeDSL.ExtendedIngredient source : sources) {
            if (source instanceof Source componentSource && componentSource.hasFeatures()) {
                return true;
            }

            if (source instanceof RecipeDSL.SlottedExtendedIngredient slotted
                    && slotted.delegate() instanceof Source componentSource
                    && componentSource.hasFeatures()) {
                return true;
            }
        }

        return false;
    }

    private static boolean hasAmounts(List<RecipeDSL.ExtendedIngredient> sources) {
        for (RecipeDSL.ExtendedIngredient source : sources) {
            if (source instanceof Source componentSource && componentSource.hasAmount()) {
                return true;
            }

            if (source instanceof RecipeDSL.SlottedExtendedIngredient slotted
                    && slotted.delegate() instanceof Source componentSource
                    && componentSource.hasAmount()) {
                return true;
            }
        }

        return false;
    }

    private static HolderLookup.Provider runtimeRegistries() {
        Player player = CommonHooks.getCraftingPlayer();
        if (player != null) {
            return player.level().registryAccess();
        }

        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        return server != null ? server.registryAccess() : RegistryAccess.EMPTY;
    }

    private static boolean contains(
            ItemStack stack,
            Selection selection,
            HolderLookup.Provider registries
    ) {
        Object value = getUnchecked(stack, selection.component());
        if (value == null) {
            return false;
        }

        if (selection.wholeComponent()) {
            return true;
        }

        Tag encoded = encode(selection.component(), value, registries);
        return encoded != null && getPath(encoded, selection.path()) != null;
    }

    private static void transfer(
            Selection selection,
            ItemStack from,
            ItemStack to,
            HolderLookup.Provider registries
    ) {
        Object sourceValue = getUnchecked(from, selection.component());
        if (sourceValue == null) {
            return;
        }

        if (selection.wholeComponent()) {
            setUnchecked(to, selection.component(), sourceValue);
            return;
        }

        Tag sourceEncoded = encode(selection.component(), sourceValue, registries);
        Tag selectedValue = sourceEncoded == null ? null : getPath(sourceEncoded, selection.path());
        if (selectedValue == null) {
            return;
        }

        Object targetValue = getUnchecked(to, selection.component());
        Tag targetEncoded = targetValue == null
                ? new CompoundTag()
                : encode(selection.component(), targetValue, registries);

        if (targetEncoded == null) {
            return;
        }

        Tag merged = putPath(targetEncoded.copy(), selection.path(), selectedValue.copy());
        if (merged == null) {
            return;
        }

        Object decoded = decode(selection.component(), merged, registries);
        if (decoded != null) {
            setUnchecked(to, selection.component(), decoded);
        }
    }

    private static Tag getPath(Tag root, List<String> path) {
        Tag current = root;

        for (String part : path) {
            if (!(current instanceof CompoundTag compound)) {
                return null;
            }

            current = compound.get(part);
            if (current == null) {
                return null;
            }
        }

        return current;
    }

    private static Tag putPath(Tag root, List<String> path, Tag value) {
        if (path.isEmpty()) {
            return value;
        }

        if (!(root instanceof CompoundTag rootCompound)) {
            return null;
        }

        CompoundTag current = rootCompound;

        for (int i = 0; i < path.size() - 1; ++i) {
            String part = path.get(i);
            Tag child = current.get(part);

            if (child instanceof CompoundTag childCompound) {
                current = childCompound;
            } else {
                CompoundTag created = new CompoundTag();
                current.put(part, created);
                current = created;
            }
        }

        current.put(path.getLast(), value);
        return rootCompound;
    }

    private static Tag encode(
            DataComponentType<?> type,
            Object value,
            HolderLookup.Provider registries
    ) {
        Codec<Object> codec = codec(type);
        if (codec == null) {
            return null;
        }

        DynamicOps<Tag> ops = RegistryOps.create(NbtOps.INSTANCE, registries);
        DataResult<Tag> result = codec.encodeStart(ops, value);
        return result.result().orElse(null);
    }

    private static Object decode(
            DataComponentType<?> type,
            Tag value,
            HolderLookup.Provider registries
    ) {
        Codec<Object> codec = codec(type);
        if (codec == null) {
            return null;
        }

        DynamicOps<Tag> ops = RegistryOps.create(NbtOps.INSTANCE, registries);
        return codec.parse(ops, value).result().orElse(null);
    }

    @SuppressWarnings("unchecked")
    private static Codec<Object> codec(DataComponentType<?> type) {
        return (Codec<Object>) type.codec();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Object getUnchecked(ItemStack stack, DataComponentType<?> type) {
        return stack.get((DataComponentType) type);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void setUnchecked(ItemStack stack, DataComponentType<?> type, Object value) {
        stack.set((DataComponentType) type, value);
    }

    private ComponentRecipeDSL() {}
}



