package space.anatomyuniverse.musavacca.data.models.newgen;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import space.anatomyuniverse.musavacca.data.models.NewModelSets;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class NewgenBlockTintCatalog {
    private NewgenBlockTintCatalog() {}

    public record Rule(
            Conditions.Match conditions,
            Tints.Tint tint,
            List<Tints.GeneratedLayer> generatedLayers
    ) {
        public Rule(Conditions.Match conditions, Tints.Tint tint) {
            this(conditions, tint, Tints.generatedLayers(tint));
        }

        public Rule {
            if (conditions == null) throw new NullPointerException("conditions");
            if (tint == null) throw new NullPointerException("tint");
            generatedLayers = List.copyOf(generatedLayers);
        }

        boolean handles(BlockState state, int tintIndex) {
            if (!conditions.matches(state)) {
                return false;
            }

            for (Tints.GeneratedLayer layer : generatedLayers) {
                if (layer.tintIndex() == tintIndex) {
                    return true;
                }
            }

            return false;
        }
    }

    public record Binding(Block block, List<Rule> rules) {
        public Binding {
            if (block == null) throw new NullPointerException("block");
            rules = List.copyOf(rules);
        }

        @Nullable
        public Tints.Tint resolve(BlockState state, int tintIndex) {
            Tints.Tint resolved = null;

            for (Rule rule : rules) {
                if (!rule.handles(state, tintIndex)) {
                    continue;
                }

                if (resolved != null && !resolved.equals(rule.tint())) {
                    throw new IllegalStateException(
                            "Conflicting active NewGen tints for " + ModelLocations.blockId(block)
                                    + " tintIndex=" + tintIndex
                                    + ": " + resolved + " vs " + rule.tint()
                    );
                }

                resolved = rule.tint();
            }

            return resolved;
        }
    }

    public static List<Binding> bindings() {
        return Holder.BINDINGS;
    }

    public static Block[] blocks() {
        return Holder.BLOCKS.clone();
    }

    @Nullable
    public static Binding binding(Block block) {
        return Holder.BY_BLOCK.get(block);
    }

    private static List<Binding> buildBindings() {
        LinkedHashMap<Block, List<Rule>> rulesByBlock = new LinkedHashMap<>();

        addModelFamilies(rulesByBlock, NewModelSets.simpleBlocks());
        addModelFamilies(rulesByBlock, NewModelSets.crossBlocks());
        addModelFamilies(rulesByBlock, NewModelSets.tallCrossBlocks());
        addModelFamilies(rulesByBlock, NewModelSets.portalBlocks());

        addStateFamilies(rulesByBlock, NewModelSets.doorBlocks());
        addStateFamilies(rulesByBlock, NewModelSets.trapdoorBlocks());
        addStateFamilies(rulesByBlock, NewModelSets.stairBlocks());
        addStateFamilies(rulesByBlock, NewModelSets.slabBlocks());
        addStateFamilies(rulesByBlock, NewModelSets.fenceBlocks());
        addStateFamilies(rulesByBlock, NewModelSets.fenceGateBlocks());
        addStateFamilies(rulesByBlock, NewModelSets.pressurePlateBlocks());
        addStateFamilies(rulesByBlock, NewModelSets.buttonBlocks());
        addStateFamilies(rulesByBlock, NewModelSets.wallBlocks());

        for (FireBlocks.Entry entry : NewModelSets.fireBlocks()) {
            addRule(rulesByBlock, entry.block(), Conditions.always(), entry.tint());
        }

        for (DecorationBlocks.Entry entry : NewModelSets.decorationBlocks()) {
            for (DecorationBlocks.Model model : entry.models()) {
                addRule(
                        rulesByBlock,
                        entry.block(),
                        model.conditions(),
                        Tints.effective(entry.tint(), model.tint())
                );
            }
        }

        ArrayList<Binding> bindings = new ArrayList<>(rulesByBlock.size());
        rulesByBlock.forEach((block, rules) -> {
            if (!rules.isEmpty()) {
                bindings.add(new Binding(block, rules));
            }
        });

        return List.copyOf(bindings);
    }

    private static void addModelFamilies(
            Map<Block, List<Rule>> result,
            List<? extends BlockFamily.ModelFamilyEntry<?, ?>> entries
    ) {
        for (BlockFamily.ModelFamilyEntry<?, ?> entry : entries) {
            List<? extends BlockFamily.ModelRule<?, ?>> rules =
                    entry.mode() == ModelMode.MODELS
                            ? entry.models()
                            : entry.parts();

            for (BlockFamily.ModelRule<?, ?> rule : rules) {
                addRule(
                        result,
                        entry.block(),
                        rule.conditions(),
                        Tints.effective(entry.tint(), rule.tint())
                );
            }
        }
    }

    private static void addStateFamilies(
            Map<Block, List<Rule>> result,
            List<? extends BlockFamily.StateFamilyEntry<?>> entries
    ) {
        for (BlockFamily.StateFamilyEntry<?> entry : entries) {
            for (BlockFamily.ModelRule<?, ?> part : entry.parts()) {
                if (part.tint() != null) {
                    addRule(result, entry.block(), part.conditions(), part.tint());
                }
            }
        }
    }

    private static void addRule(
            Map<Block, List<Rule>> result,
            Block block,
            Conditions.Match conditions,
            Tints.Tint tint
    ) {
        if (tint == null || !tint.tinted()) {
            return;
        }

        result.computeIfAbsent(block, ignored -> new ArrayList<>())
                .add(new Rule(conditions, tint));
    }

    private static final class Holder {
        private static final List<Binding> BINDINGS = buildBindings();
        private static final Map<Block, Binding> BY_BLOCK = buildIndex(BINDINGS);
        private static final Block[] BLOCKS = BINDINGS.stream()
                .map(Binding::block)
                .toArray(Block[]::new);

        private static Map<Block, Binding> buildIndex(List<Binding> bindings) {
            LinkedHashMap<Block, Binding> result = new LinkedHashMap<>();

            for (Binding binding : bindings) {
                Binding previous = result.put(binding.block(), binding);
                if (previous != null) {
                    throw new IllegalStateException(
                            "NewGen block tint binding declared twice for "
                                    + ModelLocations.blockId(binding.block())
                    );
                }
            }

            return Map.copyOf(result);
        }
    }
}
