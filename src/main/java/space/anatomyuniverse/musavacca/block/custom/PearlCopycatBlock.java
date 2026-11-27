package space.anatomyuniverse.musavacca.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import space.anatomyuniverse.musavacca.block.entity.custom.PearlCopycatBlockEntity;

import java.util.List;

public interface PearlCopycatBlock extends EntityBlock {
    BlockState fallbackState(BlockState state);

    default BlockState resolveOriginalState(BlockState original, BlockState carrier) {
        return original;
    }

    default BlockState originalState(BlockState state, BlockGetter level, BlockPos pos) {
        return level.getBlockEntity(pos) instanceof PearlCopycatBlockEntity copycat
                ? copycat.originalState() : fallbackState(state);
    }

    default List<ItemStack> copycatDrops(LootParams.Builder builder) {
        if (!(builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY) instanceof PearlCopycatBlockEntity copycat))
            return List.of();
        BlockState original = copycat.originalState();
        builder.withParameter(LootContextParams.BLOCK_STATE, original);
        builder.withOptionalParameter(LootContextParams.BLOCK_ENTITY,
                original.hasBlockEntity() ? copycat.originalBlockEntity() : null);
        return original.getDrops(builder);
    }
}
