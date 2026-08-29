package space.anatomyuniverse.musavacca.block.custom.logic;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import space.anatomyuniverse.musavacca.block.ModBlockTags;
import space.anatomyuniverse.musavacca.block.ModBlocks;
import space.anatomyuniverse.musavacca.block.custom.BreakBlock;
import space.anatomyuniverse.musavacca.block.custom.HexBlock;
import space.anatomyuniverse.musavacca.block.entity.custom.HexBlockEntity;

public final class BreakHexLogic {

    private BreakHexLogic() {}

    public static boolean canBreakBlockSurvive(BlockState state, LevelReader level, BlockPos pos) {
        if (!state.getValue(BreakBlock.ATTACHED)) {
            return true;
        }

        BlockState aboveState = level.getBlockState(pos.above());
        return Block.canSupportCenter(level, pos.above(), Direction.DOWN)
                || aboveState.getBlock() instanceof BreakBlock;
    }

    public static boolean shouldBreakBreakBlockOnNeighborChange(
            BlockState state,
            Direction direction,
            LevelReader level,
            BlockPos pos
    ) {
        if (!state.getValue(BreakBlock.ATTACHED)) {
            return false;
        }

        return direction == Direction.UP && !canBreakBlockSurvive(state, level, pos);
    }

    public static void breakBreakBlockAboveIfPresent(LevelReader level, BlockPos pos) {
        if (!(level instanceof ServerLevel serverLevel) || serverLevel.isClientSide()) {
            return;
        }

        BlockPos abovePos = pos.above();
        BlockState aboveState = serverLevel.getBlockState(abovePos);

        if (!BreakBlock.isAttachedStem(aboveState, ModBlocks.MUSAVACCA_EGG.get())) {
            return;
        }

        Block aboveBlock = aboveState.getBlock();

        boolean removed = serverLevel.destroyBlock(abovePos, false);
        if (!removed) {
            return;
        }

        if (aboveBlock instanceof BreakBlock breakBlock) {
            breakBlock.spawnAfterBreak(aboveState, serverLevel, abovePos, ItemStack.EMPTY, false);
        }
    }

    public static boolean canBonemealBreakBlock(LevelReader level, BlockPos pos) {
        return level.getBlockState(pos.below()).getBlock() instanceof HexBlock
                && isMusavaccaStem(level.getBlockState(pos.above()));
    }

    public static boolean canHexBlockSurvive(LevelReader level, BlockPos pos) {
        BlockState aboveState = level.getBlockState(pos.above());
        return BreakBlock.isAttachedStem(aboveState, ModBlocks.MUSAVACCA_EGG.get())
                || Block.canSupportCenter(level, pos.above(), Direction.DOWN);
    }

    public static boolean canGrowHexIntoEggPair(LevelReader level, BlockPos pos, BlockState state) {
        BlockState aboveState = level.getBlockState(pos.above());
        BlockPos belowPos = pos.below();

        return state.hasProperty(HexBlock.CLIPPED)
                && !state.getValue(HexBlock.CLIPPED)
                && !BreakBlock.isAttachedStem(aboveState, ModBlocks.MUSAVACCA_EGG.get())
                && isMusavaccaStem(aboveState)
                && level.getBlockState(belowPos).isAir()
                && !level.isWaterAt(belowPos);
    }

    public static void growHexIntoEggPair(ServerLevel level, BlockPos pos, BlockState hexState) {
        Integer savedHex = getStoredHexColor(level, pos);
        BlockPos belowPos = pos.below();

        level.setBlock(pos, BreakBlock.makeAttachedStem(ModBlocks.MUSAVACCA_EGG.get()), Block.UPDATE_ALL);
        level.setBlock(belowPos, hexState.setValue(HexBlock.CLIPPED, false), Block.UPDATE_ALL);

        if (savedHex == null) {
            return;
        }

        BlockEntity be = level.getBlockEntity(belowPos);
        if (be instanceof HexBlockEntity hexBe) {
            hexBe.setHexColor(savedHex);
        }
    }

    private static boolean isMusavaccaStem(BlockState state) {
        return state.is(ModBlockTags.MUSAVACCA_STEMS);
    }

    private static Integer getStoredHexColor(ServerLevel level, BlockPos pos) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof HexBlockEntity hexBe && hexBe.hasHexColor()) {
            return hexBe.getHexColor();
        }

        return null;
    }
}

