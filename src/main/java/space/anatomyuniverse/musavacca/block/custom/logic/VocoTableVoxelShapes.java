package space.anatomyuniverse.musavacca.block.custom.logic;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import space.anatomyuniverse.musavacca.block.custom.VocoTableBlock;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoReceptorLogic.ReceptorPosition;
import space.anatomyuniverse.musavacca.block.entity.custom.VocoTableBlockEntity;

public final class VocoTableVoxelShapes {
    private static final VoxelShape BODY_SHAPE = Shapes.or(Block.box(0.0D, 0.0D, 0.0D, 5.0D, 4.0D, 5.0D), Block.box(0.0D, 0.0D, 11.0D, 5.0D, 4.0D, 16.0D),
            Block.box(11.0D, 0.0D, 11.0D, 16.0D, 4.0D, 16.0D), Block.box(11.0D, 0.0D, 0.0D, 16.0D, 4.0D, 5.0D), Block.box(2.0D, 0.0D, 2.0D, 14.0D, 14.0D, 14.0D));
    public static final VoxelShape BASE_SHAPE = Shapes.or(BODY_SHAPE, VocoTableReceptorHitboxes.SHAPE);
    public static final VoxelShape ROTARY_DIALERS_SHAPE = VocoTableDialerHitboxes.SHAPE;
    private static final VoxelShape BASE_WITH_DIALERS = Shapes.or(BASE_SHAPE, ROTARY_DIALERS_SHAPE);

    private VocoTableVoxelShapes() {}

    public static VoxelShape shape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.or(withCandles(state, level, pos, context, false), VocoTableItemDisplayHitboxes.SHAPE);
    }

    public static VoxelShape collisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return withCandles(state, level, pos, context, true);
    }

    private static VoxelShape withCandles(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context, boolean collision) {
        VoxelShape shape = state.getValue(VocoTableBlock.ROTARY_DIALERS) ? BASE_WITH_DIALERS : BASE_SHAPE;
        if (!(level.getBlockEntity(pos) instanceof VocoTableBlockEntity table)) return shape;
        for (var receptor : ReceptorPosition.values()) {
            var candle = table.candle(receptor);
            if (candle != null) shape = Shapes.or(shape, VocoTableCandleVoxelShapes.shape(candle, level, pos, receptor, context, collision));
        }
        return shape;
    }
}
