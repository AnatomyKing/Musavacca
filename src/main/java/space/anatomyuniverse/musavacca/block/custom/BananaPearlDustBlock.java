package space.anatomyuniverse.musavacca.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.RedstoneSide;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.EAST_REDSTONE;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.NORTH_REDSTONE;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.SOUTH_REDSTONE;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.WEST_REDSTONE;

//? if >=26.3 {
/*import static net.minecraft.world.level.block.RedstoneWireBlock.PROPERTY_BY_DIRECTION;
*///?} else {
import static net.minecraft.world.level.block.RedStoneWireBlock.PROPERTY_BY_DIRECTION;
//?}
//? if >=1.21.2 {
import net.minecraft.world.level.ScheduledTickAccess;
//?} else {
/*import net.minecraft.world.level.LevelAccessor;
*///?}

public final class BananaPearlDustBlock extends Block {
    //? if <26.3 {
    public static final MapCodec<BananaPearlDustBlock> CODEC = simpleCodec(BananaPearlDustBlock::new);
    //?}

    public BananaPearlDustBlock(Properties properties) {
        super(properties);
        registerDefaultState(
                stateDefinition
                        .any()
                        .setValue(NORTH_REDSTONE, RedstoneSide.NONE)
                        .setValue(EAST_REDSTONE, RedstoneSide.NONE)
                        .setValue(SOUTH_REDSTONE, RedstoneSide.NONE)
                        .setValue(WEST_REDSTONE, RedstoneSide.NONE));
    }

    //? if <26.3 {
    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }
    //?}

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH_REDSTONE, EAST_REDSTONE, SOUTH_REDSTONE, WEST_REDSTONE);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return connections(defaultBlockState(), context.getLevel(), context.getClickedPos());
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        if (level.isClientSide() || oldState.is(this)) return;
        BlockState updated = connections(state, level, pos);
        if (updated != state) level.setBlock(pos, updated, Block.UPDATE_CLIENTS);
        refreshNearby(level, pos);
    }

    //? if >=1.21.5 {
    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean movedByPiston) {
        super.affectNeighborsAfterRemoval(state, level, pos, movedByPiston);
        refreshNearby(level, pos);
    }
    //?} else {
    /*@Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        super.onRemove(state, level, pos, newState, movedByPiston);
        if (!level.isClientSide() && !newState.is(this)) refreshNearby(level, pos);
    }
    *///?}

    @Override
    //? if >=1.21.2 {
    protected BlockState updateShape(
            BlockState state,
            LevelReader level,
            ScheduledTickAccess ticks,
            BlockPos pos,
            Direction direction,
            BlockPos neighborPos,
            BlockState neighborState,
            RandomSource random) {
    //?} else {
    /*protected BlockState updateShape(
            BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
    *///?}
        return connections(state, level, pos);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Blocks.TRIPWIRE.defaultBlockState().getShape(level, pos, context);
    }

    public static EnumProperty<RedstoneSide> side(Direction direction) {
        var side = PROPERTY_BY_DIRECTION.get(direction);
        if (side == null) throw new IllegalArgumentException("Dust direction must be horizontal");
        return side;
    }

    public static BlockPos connected(LevelReader level, BlockPos pos, Direction direction) {
        BlockPos adjacent = pos.relative(direction);
        BlockState state = level.getBlockState(adjacent);
        if (state.getBlock() instanceof BananaPearlDustBlock || state.getBlock() instanceof VocoPostBlock) return adjacent;
        BlockPos above = pos.above();
        if (canSupportDust(level, adjacent, state) && !level.getBlockState(above).isRedstoneConductor(level, above)) {
            BlockPos higher = adjacent.above();
            if (level.getBlockState(higher).getBlock() instanceof BananaPearlDustBlock) return higher;
        }
        if (state.isRedstoneConductor(level, adjacent)) return null;
        BlockPos lower = adjacent.below();
        if (!(level.getBlockState(lower).getBlock() instanceof BananaPearlDustBlock)) return null;
        BlockPos support = pos.below();
        return canSupportDust(level, support, level.getBlockState(support)) ? lower : null;
    }

    private static boolean canSupportDust(LevelReader level, BlockPos pos, BlockState state) {
        return state.isFaceSturdy(level, pos, Direction.UP) || state.is(Blocks.HOPPER);
    }

    private static void refreshNearby(Level level, BlockPos pos) {
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos side = pos.relative(direction);
            for (int height = -1; height <= 1; height += 2) {
                BlockPos neighbor = side.offset(0, height, 0);
                BlockState state = level.getBlockState(neighbor);
                if (!(state.getBlock() instanceof BananaPearlDustBlock)) continue;
                BlockState updated = connections(state, level, neighbor);
                if (updated != state) level.setBlock(neighbor, updated, Block.UPDATE_ALL);
            }
        }
    }

    private static BlockState connections(BlockState state, LevelReader level, BlockPos pos) {
        int count = 0;
        Direction end = null;
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos next = connected(level, pos, direction);
            RedstoneSide side = next == null ? RedstoneSide.NONE : next.getY() > pos.getY() ? RedstoneSide.UP : RedstoneSide.SIDE;
            state = state.setValue(side(direction), side);
            if (next != null) {
                end = direction.getOpposite();
                count++;
            }
        }
        return count == 1 ? state.setValue(side(end), RedstoneSide.SIDE) : state;
    }
}
