package space.anatomyuniverse.musavacca.item.custom;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.level.block.Block;

public final class BlockStateItem extends BlockItem {
    private final BlockItemStateProperties blockState;

    public BlockStateItem(Block block, Properties properties, BlockItemStateProperties blockState) {
        super(block, properties.component(DataComponents.BLOCK_STATE, blockState));
        this.blockState = blockState;
    }

    public BlockItemStateProperties blockState() {
        return blockState;
    }

    //? if <1.21.2 {
    /*@Override public String getDescriptionId() { return getOrCreateDescriptionId(); }
    *///?}
}
