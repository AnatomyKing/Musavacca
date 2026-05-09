// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/block/custom/logic/VocoTableVoxelShapes.java
package space.anatomyuniverse.musavacca.block.custom.logic;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoReceptorLogic.ReceptorPosition;
import space.anatomyuniverse.musavacca.block.entity.custom.VocoTableBlockEntity;

public final class VocoTableVoxelShapes {
    public static final VoxelShape BASE_SHAPE = Shapes.or(
            Block.box(0.0D, 0.0D, 0.0D, 5.0D, 4.0D, 5.0D),
            Block.box(0.0D, 0.0D, 11.0D, 5.0D, 4.0D, 16.0D),
            Block.box(11.0D, 0.0D, 11.0D, 16.0D, 4.0D, 16.0D),
            Block.box(11.0D, 0.0D, 0.0D, 16.0D, 4.0D, 5.0D),

            Block.box(2.0D, 0.0D, 2.0D, 14.0D, 14.0D, 14.0D),

            Block.box(10.0D, 12.0D, 0.0D, 16.0D, 16.0D, 6.0D),
            Block.box(0.0D, 12.0D, 0.0D, 6.0D, 16.0D, 6.0D),
            Block.box(10.0D, 12.0D, 10.0D, 16.0D, 16.0D, 16.0D),
            Block.box(0.0D, 12.0D, 10.0D, 6.0D, 16.0D, 16.0D),

            Block.box(6.0D, 10.0D, -1.0D, 10.0D, 13.0D, 2.0D),
            Block.box(14.0D, 10.0D, 6.0D, 17.0D, 13.0D, 10.0D),
            Block.box(6.0D, 10.0D, 14.0D, 10.0D, 13.0D, 17.0D),
            Block.box(-1.0D, 10.0D, 6.0D, 2.0D, 13.0D, 10.0D)
    );

    private static final int SHAPE_CACHE_SIZE = 5 * 5 * 5 * 5;
    private static final VoxelShape[] SHAPE_CACHE = new VoxelShape[SHAPE_CACHE_SIZE];

    private VocoTableVoxelShapes() {}

    public static VoxelShape shape(BlockGetter level, BlockPos pos) {
        if (!(level.getBlockEntity(pos) instanceof VocoTableBlockEntity tableBe)) {
            return BASE_SHAPE;
        }

        int northEast = shapeCount(tableBe, ReceptorPosition.NORTH_EAST);
        int northWest = shapeCount(tableBe, ReceptorPosition.NORTH_WEST);
        int southEast = shapeCount(tableBe, ReceptorPosition.SOUTH_EAST);
        int southWest = shapeCount(tableBe, ReceptorPosition.SOUTH_WEST);

        int key = shapeKey(northEast, northWest, southEast, southWest);

        VoxelShape cached = SHAPE_CACHE[key];
        if (cached != null) {
            return cached;
        }

        VoxelShape shape = BASE_SHAPE;

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
        if (!tableBe.hasCandle(receptor)) {
            return 0;
        }

        return Math.max(1, Math.min(4, tableBe.getCandleCount(receptor)));
    }

    private static int shapeKey(int northEast, int northWest, int southEast, int southWest) {
        return northEast
                + northWest * 5
                + southEast * 25
                + southWest * 125;
    }
}