package space.anatomyuniverse.musavacca.data.models.unified;

import space.anatomyuniverse.musavacca.data.models.engine.block.EngineBlockEntry;

import java.util.ArrayList;
import java.util.List;

public final class BlockTintRules {
    private BlockTintRules() {}

    public static BlockTintRule[] fromEntries(EngineBlockEntry... entries) {
        List<BlockTintRule> result = new ArrayList<>();

        if (entries != null) {
            for (EngineBlockEntry entry : entries) {
                if (entry == null) continue;
                addAll(result, entry.blockTintRules());
            }
        }

        return result.toArray(BlockTintRule[]::new);
    }

    public static BlockTintRule[] combine(BlockTintRule[]... groups) {
        List<BlockTintRule> result = new ArrayList<>();

        if (groups != null) {
            for (BlockTintRule[] group : groups) {
                addAll(result, group);
            }
        }

        return result.toArray(BlockTintRule[]::new);
    }

    public static void addAll(List<BlockTintRule> result, BlockTintRule[] rules) {
        if (result == null || rules == null) return;

        for (BlockTintRule rule : rules) {
            if (rule != null && rule.enabled()) {
                result.add(rule);
            }
        }
    }
}
