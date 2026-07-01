package space.anatomyuniverse.musavacca.data.models.unified;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import space.anatomyuniverse.musavacca.tint.TintColorUtil;

import java.util.List;

/**
 * One tint contract for the whole model system:
 * - 1.21.4+ returns vanilla item tint sources only.
 * - block color handler behavior remains runtime, because block entity colors are not vanilla JSON data.
 */
public interface ItemTint {
    boolean useTintedGeneratedBlockModel();

    default boolean hasBlockTint() {
        return false;
    }

    default int blockTint(BlockState state, BlockAndTintGetter level, BlockPos pos, int tintIndex) {
        return TintColorUtil.NO_TINT;
    }

    //? if >=1.21.4 {
    List<?> sources();
    //?}
}
