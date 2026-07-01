package space.anatomyuniverse.musavacca.data.models.unified;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import space.anatomyuniverse.musavacca.tint.TintColorUtil;

public record BlockTintRule(Block block, Conditions conditions, ItemTint tint) {
    public static BlockTintRule of(Block block, ItemTint tint) {
        return new BlockTintRule(block, null, tint);
    }

    public static BlockTintRule when(Block block, Conditions conditions, ItemTint tint) {
        return new BlockTintRule(block, conditions, tint);
    }

    public boolean enabled() {
        return block != null && tint != null && tint.hasBlockTint();
    }

    public boolean matches(BlockState state) {
        if (!enabled() || state == null || !state.is(block)) {
            return false;
        }

        return conditions == null || conditions.isEmpty() || conditions.matches(state);
    }

    public int calculate(BlockState state, BlockAndTintGetter level, BlockPos pos, int tintIndex) {
        if (!matches(state)) {
            return TintColorUtil.NO_TINT;
        }

        return tint.blockTint(state, level, pos, tintIndex);
    }
}
