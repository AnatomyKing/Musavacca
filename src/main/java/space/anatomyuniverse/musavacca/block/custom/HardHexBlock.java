package space.anatomyuniverse.musavacca.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import space.anatomyuniverse.musavacca.block.entity.custom.HardHexBlockEntity;
import space.anatomyuniverse.musavacca.component.ModDataComponents;

public class HardHexBlock extends Block implements EntityBlock {

    public HardHexBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new HardHexBlockEntity(pos, state);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);

        if (level.isClientSide()) {
            return;
        }

        if (oldState.is(state.getBlock())) {
            return;
        }

        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof HardHexBlockEntity hardHexBe) {
            hardHexBe.setHexColor(HardHexBlockEntity.HARD_HEX_COLOR);
        }
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);

        if (level.isClientSide()) {
            return;
        }

        Integer savedHex = stack.get(ModDataComponents.HEX_COLOR.get());

        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof HardHexBlockEntity hardHexBe) {
            if (savedHex != null) {
                hardHexBe.setHexColor(savedHex);
            } else {
                hardHexBe.setHexColor(HardHexBlockEntity.HARD_HEX_COLOR);
            }
        }
    }
}
