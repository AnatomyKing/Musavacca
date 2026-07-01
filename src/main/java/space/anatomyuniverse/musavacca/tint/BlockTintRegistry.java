package space.anatomyuniverse.musavacca.tint;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import space.anatomyuniverse.musavacca.data.models.unified.BlockTintRule;

import java.util.ArrayList;
import java.util.List;

public final class BlockTintRegistry {
    private final BlockTintRule[] rules;
    private final Block[] blocks;

    private BlockTintRegistry(BlockTintRule[] rules) {
        this.rules = rules == null ? new BlockTintRule[0] : rules;
        this.blocks = collectBlocks(this.rules);
    }

    public static BlockTintRegistry of(BlockTintRule... rules) {
        return new BlockTintRegistry(rules);
    }

    public Block[] blocks() {
        return blocks.clone();
    }

    public boolean isEmpty() {
        return blocks.length == 0;
    }

    public int color(BlockState state, BlockAndTintGetter level, BlockPos pos, int tintIndex) {
        if (state == null) {
            return TintColorUtil.NO_TINT;
        }

        for (BlockTintRule rule : rules) {
            if (rule == null) continue;

            int color = rule.calculate(state, level, pos, tintIndex);
            if (color != TintColorUtil.NO_TINT) {
                return color;
            }
        }

        return TintColorUtil.NO_TINT;
    }

    private static Block[] collectBlocks(BlockTintRule[] rules) {
        List<Block> result = new ArrayList<>();

        if (rules != null) {
            for (BlockTintRule rule : rules) {
                if (rule == null || !rule.enabled() || rule.block() == null) continue;
                if (!result.contains(rule.block())) {
                    result.add(rule.block());
                }
            }
        }

        return result.toArray(Block[]::new);
    }
}
