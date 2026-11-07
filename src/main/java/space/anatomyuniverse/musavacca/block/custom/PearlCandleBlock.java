package space.anatomyuniverse.musavacca.block.custom;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoPostCandleLogic;
//? if <1.21.2 {
/*import net.minecraft.world.ItemInteractionResult;
*///?}
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import space.anatomyuniverse.musavacca.block.custom.logic.InteractionResultCompat;
import space.anatomyuniverse.musavacca.block.custom.logic.PearlCandleConversion;
import space.anatomyuniverse.musavacca.block.entity.custom.PearlCandleBlockEntity;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import space.anatomyuniverse.musavacca.particle.tinted.OriginalCandleParticles;

public final class PearlCandleBlock extends CandleBlock implements EntityBlock {
    public PearlCandleBlock(Properties properties) { super(properties); }

    public BlockState wrapState(BlockState original) {
        return defaultBlockState().setValue(CANDLES, Math.clamp(PearlCandleConversion.count(original), 1, 4)).setValue(LIT, true);
    }

    public static BlockState fallbackState(BlockState state) {
        return Blocks.CANDLE.defaultBlockState().setValue(CANDLES, state.getValue(CANDLES)).setValue(WATERLOGGED, state.getValue(WATERLOGGED)).setValue(LIT,
                state.getValue(LIT) && !state.getValue(WATERLOGGED));
    }

    public BlockState originalState(BlockState state, BlockGetter level, BlockPos pos) {
        return level.getBlockEntity(pos) instanceof PearlCandleBlockEntity candle ? candle.originalState() : fallbackState(state);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return originalState(state, level, pos).getShape(level, pos, context);
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return originalState(state, level, pos).getCollisionShape(level, pos, context);
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        return originalState(state, level, pos).getLightEmission(level, pos);
    }

    @Override
    public SoundType getSoundType(BlockState state, LevelReader level, BlockPos pos, @Nullable Entity entity) {
        return originalState(state, level, pos).getSoundType(level, pos, entity);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return originalState(state, level, pos).canSurvive(level, pos);
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        var candle = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (!(candle instanceof PearlCandleBlockEntity pearl)) return List.of();
        BlockState original = pearl.originalState();
        builder.withParameter(LootContextParams.BLOCK_STATE, original);
        return original.getDrops(builder);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(LIT)) return;
        level.setBlock(pos, originalState(state, level, pos), Block.UPDATE_ALL);
        VocoPostCandleLogic.refreshPostBelowCandle(level, pos);
    }

    @Override
    //? if >=1.21.2 {
    protected BlockState updateShape(BlockState state, LevelReader level, net.minecraft.world.level.ScheduledTickAccess ticks, BlockPos pos,
            Direction direction, BlockPos neighborPos, BlockState neighbor, RandomSource random) {
        if (!state.getValue(LIT)) ticks.scheduleTick(pos, this, 1);
        return super.updateShape(state, level, ticks, pos, direction, neighborPos, neighbor, random);
    //?} else {
    /*protected BlockState updateShape(BlockState state, Direction direction, BlockState neighbor, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (!state.getValue(LIT)) level.scheduleTick(pos, this, 1);
        return super.updateShape(state, direction, neighbor, level, pos, neighborPos);
    *///?}
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PearlCandleBlockEntity(pos, state);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        if (!level.isClientSide() && !state.getValue(LIT)) level.scheduleTick(pos, this, 1);
    }

    @Override
    //? if >=1.21.2 {
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
    //?} else {
    /*protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
    *///?}
            BlockHitResult hitResult) {
        InteractionResult result = this.handleInteraction(stack, state, level, pos, player);
        //? if >=1.21.2 {
        return result == InteractionResult.PASS ? super.useItemOn(stack, state, level, pos, player, hand, hitResult) : result;
        //?} else {
        /*return result == InteractionResult.PASS ? super.useItemOn(stack, state, level, pos, player, hand, hitResult)
                : InteractionResultCompat.forItemUse(result);
        *///?}
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        return this.handleInteraction(ItemStack.EMPTY, state, level, pos, player);
    }

    private InteractionResult handleInteraction(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player) {
        if (!player.getAbilities().mayBuild || player.isSecondaryUseActive()) return InteractionResult.PASS;
        if (stack.isEmpty() && state.getValue(CandleBlock.LIT)) {
            if (!level.isClientSide()) {
                BlockState original = PearlCandleConversion.withFlag(this.originalState(state, level, pos), "lit", false);
                level.setBlock(pos, original, Block.UPDATE_ALL);
                level.playSound(null, pos, SoundEvents.CANDLE_EXTINGUISH, SoundSource.BLOCKS, 1, 1);
                level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                VocoPostCandleLogic.refreshPostBelowCandle(level, pos);
            }
            return InteractionResult.SUCCESS;
        }
        BlockState original = this.originalState(state, level, pos);
        if (!stack.is(original.getBlock().asItem()) || !state.getValue(CandleBlock.LIT) || state.getValue(CandleBlock.WATERLOGGED))
            return InteractionResult.PASS;
        if (!(original.getBlock().getStateDefinition()
                .getProperty("candles") instanceof IntegerProperty count))
            return InteractionResult.PASS;
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

    @Override
    public boolean placeLiquid(LevelAccessor level, BlockPos pos, BlockState state, FluidState fluid) {
        if (PearlCandleConversion.flag(originalState(state, level, pos), "waterlogged") == null) return false;
        boolean placed = super.placeLiquid(level, pos, state, fluid);
        if (placed && !level.isClientSide()) level.scheduleTick(pos, this, 1);
        return placed;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!state.getValue(CandleBlock.LIT) || state.getValue(CandleBlock.WATERLOGGED)) return;
        if (!(level.getBlockEntity(pos) instanceof PearlCandleBlockEntity candle) || !candle.hasHexColor()) return;
        BlockState original = candle.originalState();
        if (!PearlCandleConversion.isCandle(original)) return;
        OriginalCandleParticles.animate(original, level, pos, random, candle.getHexColor());
    }

    @Override
    //? if >=1.21.4 {
    protected ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData) {
    //?} else {
    /*public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
    *///?}
        return new ItemStack(originalState(state, level, pos).getBlock().asItem());
    }

    public static int candleLightLevel(BlockState state) {
        return state.getValue(CandleBlock.LIT) ? 3 * state.getValue(CandleBlock.CANDLES) : 0;
    }
}
