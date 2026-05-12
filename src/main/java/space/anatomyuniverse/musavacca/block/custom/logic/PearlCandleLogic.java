// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/block/custom/logic/PearlCandleLogic.java
package space.anatomyuniverse.musavacca.block.custom.logic;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
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
import net.minecraft.world.phys.Vec3;
import space.anatomyuniverse.musavacca.block.custom.PearlCandleBlock;
import space.anatomyuniverse.musavacca.block.entity.custom.PearlCandleBlockEntity;
import space.anatomyuniverse.musavacca.item.custom.FlintAndPearlItem;

public final class PearlCandleLogic {
    private PearlCandleLogic() {}

    public static boolean canPearlLight(BlockState state) {
        return state.hasProperty(CandleBlock.LIT)
                && state.hasProperty(CandleBlock.WATERLOGGED)
                && !state.getValue(CandleBlock.LIT)
                && !state.getValue(CandleBlock.WATERLOGGED);
    }

    public static void onPlace(
            PearlCandleBlock block,
            BlockState state,
            Level level,
            BlockPos pos,
            BlockState oldState,
            boolean movedByPiston
    ) {
        if (!level.isClientSide() && !state.getValue(CandleBlock.LIT)) {
            level.setBlock(
                    pos,
                    block.toVanillaCandleState(state, false),
                    Block.UPDATE_ALL | Block.UPDATE_IMMEDIATE
            );
        }
    }

    public static InteractionResult useItemOn(
            PearlCandleBlock block,
            ItemStack stack,
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hitResult
    ) {
        if (!canAddMatchingCandle(block, stack, state, player)) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide()) {
            BlockState newState = state.cycle(CandleBlock.CANDLES);
            level.setBlock(pos, newState, Block.UPDATE_ALL | Block.UPDATE_IMMEDIATE);

            SoundType soundType = newState.getSoundType();
            level.playSound(
                    null,
                    pos,
                    soundType.getPlaceSound(),
                    SoundSource.BLOCKS,
                    (soundType.getVolume() + 1.0F) / 2.0F,
                    soundType.getPitch() * 0.8F
            );

            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }

            level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
        }

        return InteractionResult.SUCCESS;
    }

    public static void animateTick(
            PearlCandleBlock block,
            BlockState state,
            Level level,
            BlockPos pos,
            RandomSource random
    ) {
        if (!state.getValue(CandleBlock.LIT) || state.getValue(CandleBlock.WATERLOGGED)) {
            return;
        }

        int hexColor = level.getBlockEntity(pos) instanceof PearlCandleBlockEntity pearlCandleBe
                ? pearlCandleBe.getHexColorOrFallback()
                : FlintAndPearlItem.DEFAULT_HEX_COLOR;

        for (Vec3 offset : block.pearlParticleOffsets(state)) {
            CandleParticleEffects.spawnPearlVanillaStyle(
                    level,
                    random,
                    offset.add(pos.getX(), pos.getY(), pos.getZ()),
                    hexColor
            );
        }
    }

    public static int candleLightLevel(BlockState state) {
        return state.getValue(CandleBlock.LIT) ? 3 * state.getValue(CandleBlock.CANDLES) : 0;
    }

    private static boolean canAddMatchingCandle(
            PearlCandleBlock block,
            ItemStack stack,
            BlockState state,
            Player player
    ) {
        return stack.is(block.getVanillaCandleBlock().asItem())
                && player.getAbilities().mayBuild
                && !player.isSecondaryUseActive()
                && state.getValue(CandleBlock.LIT)
                && !state.getValue(CandleBlock.WATERLOGGED)
                && state.getValue(CandleBlock.CANDLES) < CandleBlock.MAX_CANDLES;
    }
}