package space.anatomyuniverse.musavacca.block.custom.logic;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import space.anatomyuniverse.musavacca.block.custom.VocoTableBlock;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoReceptorLogic.ReceptorPosition;
import space.anatomyuniverse.musavacca.block.entity.custom.VocoTableBlockEntity;

public final class VocoTableVoxelShapes {
    private static final VoxelShape BODY_SHAPE =
            Shapes.or(
                    Block.box(0.0D, 0.0D, 0.0D, 5.0D, 4.0D, 5.0D),
                    Block.box(0.0D, 0.0D, 11.0D, 5.0D, 4.0D, 16.0D),
                    Block.box(11.0D, 0.0D, 11.0D, 16.0D, 4.0D, 16.0D),
                    Block.box(11.0D, 0.0D, 0.0D, 16.0D, 4.0D, 5.0D),
                    Block.box(2.0D, 0.0D, 2.0D, 14.0D, 14.0D, 14.0D));
    public static final VoxelShape BASE_SHAPE = Shapes.or(BODY_SHAPE, VocoTableHitboxes.RECEPTOR_SHAPE);
    public static final VoxelShape ROTARY_DIALERS_SHAPE = VocoTableHitboxes.DIALER_SHAPE;
    private static final VoxelShape BASE_WITH_DIALERS = Shapes.or(BASE_SHAPE, ROTARY_DIALERS_SHAPE);

    private VocoTableVoxelShapes() {}

    public static VoxelShape shape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return withCandles(state, level, pos, context, false);
    }

    public static VoxelShape collisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return withCandles(state, level, pos, context, true);
    }

    private static VoxelShape withCandles(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context, boolean collision) {
        VoxelShape base = state.getValue(VocoTableBlock.ROTARY_DIALERS) ? BASE_WITH_DIALERS : BASE_SHAPE;
        if (level.getBlockEntity(pos) instanceof VocoTableBlockEntity table) {
            return table.getShapeCache().get(base, table, level, pos, context, collision);
        }
        return collision ? base : Shapes.or(base, VocoTableHitboxes.ITEM_DISPLAY_SHAPE);
    }

    public static final class Cache {
        private volatile Entry outline;
        private volatile Entry collision;

        private VoxelShape get(
                VoxelShape base, VocoTableBlockEntity table, BlockGetter level, BlockPos pos, CollisionContext context, boolean collision) {
            Entry cached = collision ? this.collision : this.outline;
            VoxelShape[] candles = cached == null ? new VoxelShape[ReceptorPosition.COUNT] : null;
            BlockPos ground = pos.above();
            for (int i = 0; i < ReceptorPosition.COUNT; i++) {
                var candle = table.candle(ReceptorPosition.byId(i));
                VoxelShape shape = Shapes.empty();
                if (candle != null) {
                    var state = candle.state();
                    shape = collision ? state.getCollisionShape(level, ground, context) : state.getShape(level, ground, context);
                }
                if (cached != null && (shape == cached.candles[i] || !Shapes.joinIsNotEmpty(shape, cached.candles[i], BooleanOp.NOT_SAME)))
                    continue;
                if (candles == null) candles = cached.candles.clone();
                candles[i] = shape;
            }
            if (cached != null && candles == null && cached.base == base) return cached.shape;
            if (candles == null) candles = cached.candles;
            VoxelShape shape = base;
            for (int i = 0; i < candles.length; i++) {
                if (candles[i].isEmpty()) continue;
                var corner = VocoTableCandleVoxelShapes.renderTranslation(ReceptorPosition.byId(i));
                shape = Shapes.or(shape, candles[i].move(corner.x, corner.y, corner.z));
            }
            if (!collision) shape = Shapes.or(shape, VocoTableHitboxes.ITEM_DISPLAY_SHAPE);
            Entry entry = new Entry(base, candles, shape);
            if (collision) this.collision = entry;
            else this.outline = entry;
            return shape;
        }

        private record Entry(VoxelShape base, VoxelShape[] candles, VoxelShape shape) {}
    }
}
