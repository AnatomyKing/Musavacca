package space.anatomyuniverse.musavacca.data.models.unified;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

@FunctionalInterface
public interface BlockTintColorResolver {
    Integer resolve(BlockState state, BlockAndTintGetter level, BlockPos pos);
}
