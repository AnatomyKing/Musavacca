package space.anatomyuniverse.musavacca.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
//? if <1.21.2 {
/*import net.minecraft.world.ItemInteractionResult;
*///?}

public final class PearlCandleCakeBlock extends PearlFlameBlock {
    public PearlCandleCakeBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockState fallbackState(BlockState state) {
        return Blocks.CANDLE_CAKE.defaultBlockState().setValue(LIT, state.getValue(LIT));
    }

    @Override
    //? if >=1.21.2 {
    protected InteractionResult useItemOn(
    //?} else {
    /*protected ItemInteractionResult useItemOn(
    *///?}
            ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        return originalState(state, level, pos).useItemOn(stack, level, player, hand, hit);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        return originalState(state, level, pos).useWithoutItem(level, player, hit);
    }

    public static int candleLightLevel(BlockState state) {
        return state.getValue(LIT) ? 3 : 0;
    }
}
