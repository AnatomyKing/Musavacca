package space.anatomyuniverse.musavacca.block.custom;

import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jetbrains.annotations.Nullable;

public class BananaPearlMascaron extends Block {
    public static final EnumProperty<FrontAndTop> ORIENTATION = BlockStateProperties.ORIENTATION;

    public BananaPearlMascaron(Properties properties) {
        super(properties);

        this.registerDefaultState(this.stateDefinition.any()
                .setValue(ORIENTATION, FrontAndTop.NORTH_UP));
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction front = context.getNearestLookingDirection().getOpposite();

        Direction top = front.getAxis().isVertical()
                ? context.getHorizontalDirection().getOpposite()
                : Direction.UP;

        return this.defaultBlockState()
                .setValue(ORIENTATION, FrontAndTop.fromFrontAndTop(front, top));
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(
                ORIENTATION,
                rotation.rotation().rotate(state.getValue(ORIENTATION))
        );
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.setValue(
                ORIENTATION,
                mirror.rotation().rotate(state.getValue(ORIENTATION))
        );
    }

    @Override
    protected void createBlockStateDefinition(
            StateDefinition.Builder<Block, BlockState> builder
    ) {
        builder.add(ORIENTATION);
    }
}