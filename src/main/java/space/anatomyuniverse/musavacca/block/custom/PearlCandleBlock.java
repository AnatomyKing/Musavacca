package space.anatomyuniverse.musavacca.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import space.anatomyuniverse.musavacca.block.custom.logic.PearlCandleLogic;
import space.anatomyuniverse.musavacca.block.entity.custom.PearlCandleBlockEntity;

public final class PearlCandleBlock extends CandleBlock implements EntityBlock {
    private final Block vanillaCandleBlock;

    public PearlCandleBlock(Block vanillaCandleBlock, Properties properties) {
        super(properties);

        if (!(vanillaCandleBlock instanceof CandleBlock)) {
            throw new IllegalArgumentException("vanillaCandleBlock must be a CandleBlock");
        }

        this.vanillaCandleBlock = vanillaCandleBlock;
    }

    public Block getVanillaCandleBlock() {
        return this.vanillaCandleBlock;
    }

    public BlockState copyStateFromVanillaCandle(BlockState vanillaState, boolean forceLit) {
        boolean waterlogged = vanillaState.getValue(WATERLOGGED);

        return this.defaultBlockState()
                .setValue(CANDLES, vanillaState.getValue(CANDLES))
                .setValue(WATERLOGGED, waterlogged)
                .setValue(LIT, forceLit && !waterlogged);
    }

    public BlockState toVanillaCandleState(BlockState pearlState, boolean lit) {
        boolean waterlogged = pearlState.getValue(WATERLOGGED);

        return this.vanillaCandleBlock.defaultBlockState()
                .setValue(CANDLES, pearlState.getValue(CANDLES))
                .setValue(WATERLOGGED, waterlogged)
                .setValue(LIT, lit && !waterlogged);
    }

    public Iterable<Vec3> pearlParticleOffsets(BlockState state) {
        return this.getParticleOffsets(state);
    }

    public static boolean canPearlLight(BlockState state) {
        return PearlCandleLogic.canPearlLight(state);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PearlCandleBlockEntity(pos, state);
    }

    @Override
    protected void onPlace(
            BlockState state,
            Level level,
            BlockPos pos,
            BlockState oldState,
            boolean movedByPiston
    ) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        PearlCandleLogic.onPlace(this, state, level, pos, oldState, movedByPiston);
    }

    @Override
    protected InteractionResult useItemOn(
            ItemStack stack,
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hitResult
    ) {
        InteractionResult result = PearlCandleLogic.useItemOn(
                this,
                stack,
                state,
                level,
                pos,
                player,
                hand,
                hitResult
        );

        return result == InteractionResult.PASS
                ? super.useItemOn(stack, state, level, pos, player, hand, hitResult)
                : result;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        PearlCandleLogic.animateTick(this, state, level, pos, random);
    }

    @Override
    protected ItemStack getCloneItemStack(
            LevelReader level,
            BlockPos pos,
            BlockState state,
            boolean includeData
    ) {
        return new ItemStack(this.vanillaCandleBlock.asItem());
    }

    public static int candleLightLevel(BlockState state) {
        return PearlCandleLogic.candleLightLevel(state);
    }
}
