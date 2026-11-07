package space.anatomyuniverse.musavacca.block.custom.logic;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoReceptorLogic.ReceptorPosition;

public final class VocoTableCandleVoxelShapes {
    private static final double SHIFT = 5.0D / 16.0D;
    private static final Vec3[] CORNERS = {
            new Vec3(SHIFT, 1, -SHIFT), new Vec3(-SHIFT, 1, -SHIFT),
            new Vec3(SHIFT, 1, SHIFT), new Vec3(-SHIFT, 1, SHIFT)
    };

    private VocoTableCandleVoxelShapes() {}

    public static Vec3 renderTranslation(ReceptorPosition receptor) {
        return CORNERS[receptor.id()];
    }

    public static VoxelShape shape(VocoTableCandle candle, BlockGetter level, BlockPos pos, ReceptorPosition receptor,
                                   CollisionContext context, boolean collision) {
        BlockPos ground = pos.above();
        var state = candle.state();
        VoxelShape shape = collision ? state.getCollisionShape(level, ground, context) : state.getShape(level, ground, context);
        Vec3 corner = renderTranslation(receptor);
        return shape.move(corner.x, corner.y, corner.z);
    }

    public static Vec3 dropPosition(BlockGetter level, BlockPos pos, ReceptorPosition receptor, VocoTableCandle candle) {
        var shape = shape(candle, level, pos, receptor, CollisionContext.empty(), false);
        Vec3 local = shape.isEmpty() ? renderTranslation(receptor).add(0.5D, 0, 0.5D) : shape.bounds().getCenter();
        return local.add(pos.getX(), pos.getY(), pos.getZ());
    }
}
