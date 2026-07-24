package space.anatomyuniverse.musavacca.block.custom.logic;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import space.anatomyuniverse.musavacca.block.custom.VocoTableBlock;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoReceptorLogic.ReceptorPosition;
import space.anatomyuniverse.musavacca.block.entity.custom.VocoTableBlockEntity;

public final class VocoTableVoxelShapes {
    private static final VoxelShape BODY_SHAPE = Shapes.or(
            Block.box(0.0D, 0.0D, 0.0D, 5.0D, 4.0D, 5.0D),
            Block.box(0.0D, 0.0D, 11.0D, 5.0D, 4.0D, 16.0D),
            Block.box(11.0D, 0.0D, 11.0D, 16.0D, 4.0D, 16.0D),
            Block.box(11.0D, 0.0D, 0.0D, 16.0D, 4.0D, 5.0D),
            Block.box(2.0D, 0.0D, 2.0D, 14.0D, 14.0D, 14.0D)
    );

    public static final VoxelShape BASE_SHAPE = Shapes.or(
            BODY_SHAPE,
            VocoTableReceptorHitboxes.SHAPE
    );

    public static final VoxelShape ROTARY_DIALERS_SHAPE = VocoTableDialerHitboxes.SHAPE;

    private static final int SHAPE_CACHE_SIZE = 5 * 5 * 5 * 5 * 2;
    private static final VoxelShape[] SHAPE_CACHE = new VoxelShape[SHAPE_CACHE_SIZE];

    private VocoTableVoxelShapes() {}

    public static VoxelShape shape(BlockState state, BlockGetter level, BlockPos pos) {
        return Shapes.or(
                collisionShape(state, level, pos),
                VocoTableItemDisplayHitboxes.SHAPE
        );
    }

    public static VoxelShape collisionShape(BlockState state, BlockGetter level, BlockPos pos) {
        boolean rotaryDialers = state.hasProperty(VocoTableBlock.ROTARY_DIALERS)
                && state.getValue(VocoTableBlock.ROTARY_DIALERS);

        if (!(level.getBlockEntity(pos) instanceof VocoTableBlockEntity tableBe)) {
            return rotaryDialers ? Shapes.or(BASE_SHAPE, ROTARY_DIALERS_SHAPE) : BASE_SHAPE;
        }

        int northEast = shapeCount(tableBe, ReceptorPosition.NORTH_EAST);
        int northWest = shapeCount(tableBe, ReceptorPosition.NORTH_WEST);
        int southEast = shapeCount(tableBe, ReceptorPosition.SOUTH_EAST);
        int southWest = shapeCount(tableBe, ReceptorPosition.SOUTH_WEST);
        int key = shapeKey(northEast, northWest, southEast, southWest, rotaryDialers);

        VoxelShape cached = SHAPE_CACHE[key];
        if (cached != null) {
            return cached;
        }

        VoxelShape shape = rotaryDialers ? Shapes.or(BASE_SHAPE, ROTARY_DIALERS_SHAPE) : BASE_SHAPE;

        if (northEast > 0) {
            shape = Shapes.or(shape, VocoTableCandleVoxelShapes.shape(ReceptorPosition.NORTH_EAST, northEast));
        }

        if (northWest > 0) {
            shape = Shapes.or(shape, VocoTableCandleVoxelShapes.shape(ReceptorPosition.NORTH_WEST, northWest));
        }

        if (southEast > 0) {
            shape = Shapes.or(shape, VocoTableCandleVoxelShapes.shape(ReceptorPosition.SOUTH_EAST, southEast));
        }

        if (southWest > 0) {
            shape = Shapes.or(shape, VocoTableCandleVoxelShapes.shape(ReceptorPosition.SOUTH_WEST, southWest));
        }

        SHAPE_CACHE[key] = shape;
        return shape;
    }

    private static int shapeCount(VocoTableBlockEntity tableBe, ReceptorPosition receptor) {
        return tableBe.hasCandle(receptor)
                ? Math.max(1, Math.min(4, tableBe.getCandleCount(receptor)))
                : 0;
    }

    private static int shapeKey(
            int northEast,
            int northWest,
            int southEast,
            int southWest,
            boolean rotaryDialers
    ) {
        int candleKey = northEast
                + northWest * 5
                + southEast * 25
                + southWest * 125;

        return candleKey + (rotaryDialers ? 625 : 0);
    }
}
