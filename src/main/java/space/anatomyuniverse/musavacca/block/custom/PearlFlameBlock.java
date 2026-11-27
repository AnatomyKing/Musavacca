package space.anatomyuniverse.musavacca.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import space.anatomyuniverse.musavacca.block.custom.logic.PearlFlameConversion;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoPostCandleLogic;
import space.anatomyuniverse.musavacca.block.entity.custom.PearlCopycatBlockEntity;
import space.anatomyuniverse.musavacca.block.entity.ModBlockEntities;
import space.anatomyuniverse.musavacca.block.custom.logic.PearlFlameParticles;

import java.util.List;

public abstract class PearlFlameBlock extends Block implements PearlCopycatBlock {
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    protected PearlFlameBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(LIT, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIT);
    }

    public static boolean isLit(BlockState state) {
        return state.getValue(LIT) && (!state.hasProperty(BlockStateProperties.WATERLOGGED)
                || !state.getValue(BlockStateProperties.WATERLOGGED));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return ModBlockEntities.PEARL_CANDLE_BLOCK_ENTITY.get().create(pos, state);
    }

    public BlockState wrapState(BlockState original) {
        return defaultBlockState().setValue(LIT, true);
    }

    @Override
    public BlockState resolveOriginalState(BlockState original, BlockState carrier) {
        original = PearlFlameConversion.withFlag(original, "lit", isLit(carrier));
        return carrier.hasProperty(BlockStateProperties.WATERLOGGED)
                ? PearlFlameConversion.withFlag(original, "waterlogged", carrier.getValue(BlockStateProperties.WATERLOGGED)) : original;
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
        return copycatDrops(builder);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (isLit(state)) return;
        restoreOriginal(level, pos, originalState(state, level, pos));
    }

    protected final boolean restoreOriginal(Level level, BlockPos pos, BlockState original) {
        boolean restored = level.getBlockEntity(pos) instanceof PearlCopycatBlockEntity copycat
                ? copycat.restoreOriginal(level, original) : level.setBlock(pos, original, Block.UPDATE_ALL);
        if (restored) VocoPostCandleLogic.refreshPostBelowCandle(level, pos);
        return restored;
    }

    protected void extinguishEffects(LevelAccessor level, BlockPos pos, @Nullable Player player) {
        level.playSound(null, pos, SoundEvents.CANDLE_EXTINGUISH, SoundSource.BLOCKS, 1, 1);
        level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
    }

    @Override
    //? if >=1.21.2 {
    protected BlockState updateShape(
            BlockState state,
            LevelReader level,
            net.minecraft.world.level.ScheduledTickAccess ticks,
            BlockPos pos,
            Direction direction,
            BlockPos neighborPos,
            BlockState neighbor,
            RandomSource random) {
        if (direction == Direction.DOWN && !canSurvive(state, level, pos)) return Blocks.AIR.defaultBlockState();
        if (!isLit(state)) ticks.scheduleTick(pos, this, 1);
        if (state.hasProperty(BlockStateProperties.WATERLOGGED) && state.getValue(BlockStateProperties.WATERLOGGED))
            ticks.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        return super.updateShape(state, level, ticks, pos, direction, neighborPos, neighbor, random);
    //?} else {
    /*protected BlockState updateShape(
            BlockState state, Direction direction, BlockState neighbor, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (direction == Direction.DOWN && !canSurvive(state, level, pos)) return Blocks.AIR.defaultBlockState();
        if (!isLit(state)) level.scheduleTick(pos, this, 1);
        if (state.hasProperty(BlockStateProperties.WATERLOGGED) && state.getValue(BlockStateProperties.WATERLOGGED))
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        return super.updateShape(state, direction, neighbor, level, pos, neighborPos);
    *///?}
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        if (!level.isClientSide() && !isLit(state)) level.scheduleTick(pos, this, 1);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!isLit(state)) return;
        if (!(level.getBlockEntity(pos) instanceof PearlCopycatBlockEntity copycat) || !copycat.hasHexColor()) return;
        BlockState original = copycat.originalState();
        if (!PearlFlameConversion.isCandle(original)) return;
        PearlFlameParticles.animate(original, level, pos, random, copycat.getHexColor());
    }

    @Override
    //? if >=1.21.4 {
    protected ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData) {
    //?} else {
    /*public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
    *///?}
        return new ItemStack(originalState(state, level, pos).getBlock().asItem());
    }

    public abstract static class Waterlogged extends PearlFlameBlock implements SimpleWaterloggedBlock {
        public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

        protected Waterlogged(Properties properties) {
            super(properties);
            registerDefaultState(defaultBlockState().setValue(WATERLOGGED, false));
        }

        @Override
        protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
            super.createBlockStateDefinition(builder);
            builder.add(WATERLOGGED);
        }

        @Override
        protected FluidState getFluidState(BlockState state) {
            return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
        }

        @Override
        public boolean placeLiquid(LevelAccessor level, BlockPos pos, BlockState state, FluidState fluid) {
            if (PearlFlameConversion.flag(originalState(state, level, pos), "waterlogged") == null) return false;
            if (!SimpleWaterloggedBlock.super.placeLiquid(level, pos, state.setValue(LIT, false), fluid)) return false;
            if (!level.isClientSide()) {
                BlockState wet = level.getBlockState(pos);
                if (wet.is(this)) {
                    if (level instanceof Level world) restoreOriginal(world, pos, originalState(wet, world, pos));
                    if (level.getBlockState(pos).is(this)) level.scheduleTick(pos, this, 1);
                }
                if (isLit(state)) extinguishEffects(level, pos, null);
            }
            return true;
        }
    }
}
