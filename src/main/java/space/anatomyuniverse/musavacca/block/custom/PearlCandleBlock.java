package space.anatomyuniverse.musavacca.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import space.anatomyuniverse.musavacca.block.custom.logic.PearlFlameConversion;
import space.anatomyuniverse.musavacca.block.entity.custom.PearlCopycatBlockEntity;
//? if <1.21.2 {
/*import net.minecraft.world.ItemInteractionResult;
import space.anatomyuniverse.musavacca.block.custom.logic.InteractionResultCompat;
*///?}

public final class PearlCandleBlock extends PearlFlameBlock.Waterlogged {
    public static final IntegerProperty CANDLES = BlockStateProperties.CANDLES;

    public PearlCandleBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(CANDLES, 1));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(CANDLES);
    }

    @Override
    public BlockState wrapState(BlockState original) {
        var count = PearlFlameConversion.countProperty(original);
        return super.wrapState(original).setValue(CANDLES, count == null ? 1 : Math.clamp(original.getValue(count), 1, 4));
    }

    @Override
    public BlockState fallbackState(BlockState state) {
        return Blocks.CANDLE.defaultBlockState()
                .setValue(CANDLES, state.getValue(CANDLES))
                .setValue(WATERLOGGED, state.getValue(WATERLOGGED))
                .setValue(LIT, isLit(state));
    }

    @Override
    //? if >=1.21.2 {
    protected InteractionResult useItemOn(
            ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
    //?} else {
    /*protected ItemInteractionResult useItemOn(
            ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
    *///?}
            BlockHitResult hitResult) {
        InteractionResult result = this.handleInteraction(stack, state, level, pos, player);
        //? if >=1.21.2 {
        return result == InteractionResult.PASS ? super.useItemOn(stack, state, level, pos, player, hand, hitResult) : result;
        //?} else {
        /*return result == InteractionResult.PASS
                ? super.useItemOn(stack, state, level, pos, player, hand, hitResult)
                : InteractionResultCompat.forItemUse(result);
        *///?}
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        return this.handleInteraction(ItemStack.EMPTY, state, level, pos, player);
    }

    private InteractionResult handleInteraction(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player) {
        if (!player.getAbilities().mayBuild || player.isSecondaryUseActive()) return InteractionResult.PASS;
        if (stack.isEmpty() && isLit(state)) {
            if (!level.isClientSide()) {
                BlockState original = PearlFlameConversion.withFlag(this.originalState(state, level, pos), "lit", false);
                if (restoreOriginal(level, pos, original)) extinguishEffects(level, pos, player);
            }
            return InteractionResult.SUCCESS;
        }
        BlockState original = this.originalState(state, level, pos);
        if (!stack.is(original.getBlock().asItem()) || !isLit(state))
            return InteractionResult.PASS;
        var count = PearlFlameConversion.countProperty(original);
        if (count == null) return InteractionResult.PASS;
        int next = original.getValue(count) + 1;
        if (!count.getPossibleValues().contains(next)) return InteractionResult.PASS;
        if (!level.isClientSide()) {
            BlockState updated = original.setValue(count, next);
            level.setBlock(pos, state.setValue(CANDLES, Math.min(4, next)), Block.UPDATE_ALL);
            if (level.getBlockEntity(pos) instanceof PearlCopycatBlockEntity candle) candle.setOriginalState(updated);
            SoundType sound = original.getSoundType();
            level.playSound(null, pos, sound.getPlaceSound(), SoundSource.BLOCKS, (sound.getVolume() + 1) / 2, sound.getPitch() * 0.8F);
            if (!player.getAbilities().instabuild) stack.shrink(1);
            level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
        }
        return InteractionResult.SUCCESS;
    }

    public static int candleLightLevel(BlockState state) {
        return state.getValue(LIT) ? 3 * state.getValue(CANDLES) : 0;
    }
}
