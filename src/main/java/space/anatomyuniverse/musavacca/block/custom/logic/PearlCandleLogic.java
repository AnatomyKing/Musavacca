package space.anatomyuniverse.musavacca.block.custom.logic;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;

import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import space.anatomyuniverse.musavacca.block.custom.PearlCandleBlock;
import space.anatomyuniverse.musavacca.block.entity.custom.PearlCandleBlockEntity;
import space.anatomyuniverse.musavacca.particle.tinted.OriginalCandleParticles;

public final class PearlCandleLogic {
    private PearlCandleLogic() {}
    public static InteractionResult useItemOn(PearlCandleBlock block, ItemStack stack, BlockState state, Level level, BlockPos pos, Player player,
            InteractionHand hand, BlockHitResult hitResult) {
        if (!player.getAbilities().mayBuild || player.isSecondaryUseActive()) return InteractionResult.PASS;
        if (stack.isEmpty() && state.getValue(CandleBlock.LIT)) {
            if (!level.isClientSide()) {
                BlockState original = PearlCandleConversion.withFlag(block.originalState(state, level, pos), "lit", false);
                level.setBlock(pos, original, Block.UPDATE_ALL);
                level.playSound(null, pos, net.minecraft.sounds.SoundEvents.CANDLE_EXTINGUISH, SoundSource.BLOCKS, 1, 1);
                level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                VocoPostCandleLogic.refreshPostBelowCandle(level, pos);
            }
            return InteractionResult.SUCCESS;
        }
        BlockState original = block.originalState(state, level, pos);
        if (!stack.is(original.getBlock().asItem()) || !state.getValue(CandleBlock.LIT) || state.getValue(CandleBlock.WATERLOGGED)) return InteractionResult.PASS;
        if (!(original.getBlock().getStateDefinition().getProperty("candles") instanceof net.minecraft.world.level.block.state.properties.IntegerProperty count)) return InteractionResult.PASS;
        int next = original.getValue(count) + 1;
        if (!count.getPossibleValues().contains(next)) return InteractionResult.PASS;
        if (!level.isClientSide()) {
            BlockState updated = original.setValue(count, next);
            level.setBlock(pos, state.setValue(CandleBlock.CANDLES, Math.min(4, next)), Block.UPDATE_ALL);
            if (level.getBlockEntity(pos) instanceof PearlCandleBlockEntity candle) candle.setOriginalState(updated);
            SoundType sound = original.getSoundType();
            level.playSound(null, pos, sound.getPlaceSound(), SoundSource.BLOCKS, (sound.getVolume() + 1) / 2, sound.getPitch() * 0.8F);
            if (!player.getAbilities().instabuild) stack.shrink(1);
            level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
        }
        return InteractionResult.SUCCESS;
    }

    public static void animateTick(PearlCandleBlock block, BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!state.getValue(CandleBlock.LIT) || state.getValue(CandleBlock.WATERLOGGED)) return;
        if (!(level.getBlockEntity(pos) instanceof PearlCandleBlockEntity candle)) return;
        BlockState original = candle.originalState();
        if (!PearlCandleConversion.isCandle(original)) return;
        OriginalCandleParticles.animate(original, level, pos, random);
    }

    public static int candleLightLevel(BlockState state) {
        return state.getValue(CandleBlock.LIT) ? 3 * state.getValue(CandleBlock.CANDLES) : 0;
    }

}
