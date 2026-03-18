// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/block/custom/HexBlock.java
package space.anatomyuniverse.musavacca.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import space.anatomyuniverse.musavacca.block.entity.HexBlockEntity;

public class HexBlock extends Block implements EntityBlock {

    public HexBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new HexBlockEntity(pos, state);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);

        if (oldState.is(state.getBlock())) {
            return;
        }

        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof HexBlockEntity hexBe)) {
            return;
        }

        if (level.isClientSide()) {
            hexBe.applyClientPredictionIfPresent();
        } else {
            hexBe.initializeServerFallbackColorIfNeeded();
        }
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);

        if (level.isClientSide()) {
            return;
        }

        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof HexBlockEntity hexBe) {
            hexBe.applyServerPredictedPlacementColor();
        }
    }
}