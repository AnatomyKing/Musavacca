
package space.anatomyuniverse.musavacca.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class BananaPearlChaliceBlock extends Block {
    public static final MapCodec<BananaPearlChaliceBlock> CODEC = simpleCodec(BananaPearlChaliceBlock::new);

    public static final EnumProperty<Mode> MODE = EnumProperty.create("mode", Mode.class);
    public static final IntegerProperty ROTATION = BlockStateProperties.ROTATION_16;
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;

    private static final VoxelShape SHAPE_UP = Block.box(4.0, 0.0, 4.0, 12.0, 12.0, 12.0);
    private static final VoxelShape SHAPE_GROUND = Block.box(1.75, 0.0, 2.0, 14.25, 7.0, 14.0);

    private static final VoxelShape SHAPE_TILT_NORTH = Block.box(4.5, 0.5, 2.25, 11.5, 12.5, 9.25);
    private static final VoxelShape SHAPE_TILT_SOUTH = Block.box(4.5, 0.5, 6.75, 11.5, 12.5, 13.75);
    private static final VoxelShape SHAPE_TILT_WEST  = Block.box(2.25, 0.5, 4.5, 9.25, 12.5, 11.5);
    private static final VoxelShape SHAPE_TILT_EAST  = Block.box(6.75, 0.5, 4.5, 13.75, 12.5, 11.5);

    public BananaPearlChaliceBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(
                this.stateDefinition.any()
                        .setValue(MODE, Mode.UP)
                        .setValue(ROTATION, 0)
                        .setValue(FACING, Direction.NORTH)
        );
    }

    @Override
    public MapCodec<? extends BananaPearlChaliceBlock> codec() {
        return CODEC;
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction face = context.getClickedFace();
        BlockPos pos = context.getClickedPos();

        if (face.getAxis().isHorizontal()) {
            BlockState tilt = this.defaultBlockState()
                    .setValue(MODE, Mode.TILT)
                    .setValue(FACING, face);

            if (tilt.canSurvive(context.getLevel(), pos)) {
                return tilt;
            }
        }

        BlockState standing = this.defaultBlockState()
                .setValue(
                        MODE,
                        context.getPlayer() != null && context.getPlayer().isShiftKeyDown()
                                ? Mode.GROUND
                                : Mode.UP
                )
                .setValue(ROTATION, RotationSegment.convertToSegment(context.getRotation()));

        return standing.canSurvive(context.getLevel(), pos) ? standing : null;
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        if (state.getValue(MODE) == Mode.TILT) {
            Direction facing = state.getValue(FACING);
            BlockPos supportPos = pos.relative(facing.getOpposite());
            return level.getBlockState(supportPos).isFaceSturdy(level, supportPos, facing);
        }

        return Block.canSupportCenter(level, pos.below(), Direction.UP);
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        if (state.getValue(MODE) == Mode.TILT) {
            return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
        }

        return state.setValue(ROTATION, rotation.rotate(state.getValue(ROTATION), 16));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        if (state.getValue(MODE) == Mode.TILT) {
            return state.rotate(mirror.getRotation(state.getValue(FACING)));
        }

        return state.setValue(ROTATION, mirror.mirror(state.getValue(ROTATION), 16));
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(MODE)) {
            case UP -> SHAPE_UP;
            case GROUND -> SHAPE_GROUND;
            case TILT -> switch (state.getValue(FACING)) {
                case NORTH -> SHAPE_TILT_NORTH;
                case SOUTH -> SHAPE_TILT_SOUTH;
                case WEST -> SHAPE_TILT_WEST;
                case EAST -> SHAPE_TILT_EAST;
                default -> SHAPE_TILT_NORTH;
            };
        };
    }

    //? if <1.21.2 {
    /*@Override
    protected VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return Shapes.empty();
    }
*///?} else {
@Override
protected VoxelShape getOcclusionShape(BlockState state) {
    return Shapes.empty();
}
//?}

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(MODE, ROTATION, FACING);
    }

    public enum Mode implements StringRepresentable {
        UP("up"),
        GROUND("ground"),
        TILT("tilt");

        private final String name;

        Mode(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }
}