package space.anatomyuniverse.musavacca.data.models.newgen;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import space.anatomyuniverse.musavacca.block.custom.DecorationBlock;
import space.anatomyuniverse.musavacca.data.models.NewModelSets;
import space.anatomyuniverse.musavacca.data.models.newgen.BlockFamily.ModelMode;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class NewgenBlockTintCatalog {
    private NewgenBlockTintCatalog() {}

    public record Rule(Conditions.Match conditions, Tints.Tint tint) {
        boolean handles(BlockState state, int tintIndex) {
            return tint.tintIndex() == tintIndex && conditions.matches(state);
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
                if (!rule.handles(state, tintIndex)) continue;
                if (resolved != null && !resolved.equals(rule.tint())) {
                    throw new IllegalStateException(
                            "Conflicting active NewGen tints for "
                                    + ModelLocations.blockId(block)
                                    + " tintIndex="
                                    + tintIndex
                                    + ": "
                                    + resolved
                                    + " vs "
                                    + rule.tint());
                }
                resolved = rule.tint();
            }
            return resolved;
        }
    }

    public static List<Binding> bindings() {
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
                addRule(rulesByBlock, entry.block(), model.conditions().and(DecorationBlock.PLACEMENT, model.placement()),
                        Tints.effective(entry.tint(), model.tint()));
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

    private static void addModelFamilies(Map<Block, List<Rule>> result, List<? extends BlockFamily.ModelFamilyEntry<?, ?>> entries) {
        for (BlockFamily.ModelFamilyEntry<?, ?> entry : entries) {
            List<? extends BlockFamily.ModelRule<?, ?>> rules = entry.mode() == ModelMode.MODELS ? entry.models() : entry.parts();
            for (BlockFamily.ModelRule<?, ?> rule : rules) {
                addRule(result, entry.block(), rule.conditions(), Tints.effective(entry.tint(), rule.tint()));
            }
        }
    }

    private static void addStateFamilies(Map<Block, List<Rule>> result, List<? extends BlockFamily.StateFamilyEntry<?>> entries) {
        for (BlockFamily.StateFamilyEntry<?> entry : entries) {
            for (BlockFamily.ModelRule<?, ?> part : entry.parts()) {
                if (part.tint() != null) {
                    addRule(result, entry.block(), part.conditions(), part.tint());
                }
            }
        }
    }

    private static void addRule(Map<Block, List<Rule>> result, Block block, Conditions.Match conditions, Tints.Tint tint) {
        if (tint == null || !tint.tinted()) return;
        result.computeIfAbsent(block, ignored -> new ArrayList<>()).add(new Rule(conditions, tint));
    }
}
