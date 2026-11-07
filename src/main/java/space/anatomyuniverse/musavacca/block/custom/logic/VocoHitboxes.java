package space.anatomyuniverse.musavacca.block.custom.logic;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class VocoHitboxes {
    private VocoHitboxes() {}

    public static Vec3 local16(BlockPos pos, BlockHitResult hit) {
        Vec3 point = hit.getLocation();
        return new Vec3((point.x - pos.getX()) * 16.0D, (point.y - pos.getY()) * 16.0D, (point.z - pos.getZ()) * 16.0D);
    }

    public static int findHit(List<Box> boxes, BlockPos pos, BlockHitResult hit) {
        Vec3 local = local16(pos, hit);
        for (int i = 0; i < boxes.size(); i++) {
            if (boxes.get(i).contains(local)) return i;
        }
        return -1;
    }

    public static VoxelShape shapeOf(List<Box> boxes) {
        VoxelShape shape = Shapes.empty();
        for (Box box : boxes) {
            shape = Shapes.or(shape, box.toShape());
        }
        return shape;
    }

    public record Box(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        public boolean contains(Vec3 point) {
            return point.x >= this.minX && point.x <= this.maxX && point.y >= this.minY && point.y <= this.maxY
                    && point.z >= this.minZ && point.z <= this.maxZ;
        }

        public VoxelShape toShape() {
            return Block.box(this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ);
        }
    }
}
