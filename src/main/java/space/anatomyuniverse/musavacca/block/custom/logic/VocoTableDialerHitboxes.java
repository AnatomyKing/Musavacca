// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/block/custom/logic/VocoTableDialerHitboxes.java
package space.anatomyuniverse.musavacca.block.custom.logic;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public final class VocoTableDialerHitboxes {
    private static final HitBox[] HIT_BOXES = {
            new HitBox(HitPart.DIALER_NORTH, 6.0D, 10.0D, -1.0D, 10.0D, 13.0D, 2.0D),
            new HitBox(HitPart.DIALER_EAST, 14.0D, 10.0D, 6.0D, 17.0D, 13.0D, 10.0D),
            new HitBox(HitPart.DIALER_SOUTH, 6.0D, 10.0D, 14.0D, 10.0D, 13.0D, 17.0D),
            new HitBox(HitPart.DIALER_WEST, -1.0D, 10.0D, 6.0D, 2.0D, 13.0D, 10.0D)
    };

    private VocoTableDialerHitboxes() {}

    public static HitPart detectHitPart(BlockPos pos, BlockHitResult hit) {
        Vec3 location = hit.getLocation();

        double x = (location.x - pos.getX()) * 16.0D;
        double y = (location.y - pos.getY()) * 16.0D;
        double z = (location.z - pos.getZ()) * 16.0D;

        for (HitBox box : HIT_BOXES) {
            if (box.contains(x, y, z)) {
                return box.part;
            }
        }

        return HitPart.NONE;
    }

    private record HitBox(
            HitPart part,
            double minX,
            double minY,
            double minZ,
            double maxX,
            double maxY,
            double maxZ
    ) {
        private boolean contains(double x, double y, double z) {
            return x >= this.minX && x <= this.maxX
                    && y >= this.minY && y <= this.maxY
                    && z >= this.minZ && z <= this.maxZ;
        }
    }

    public enum HitPart {
        NONE(false),
        DIALER_NORTH(true),
        DIALER_EAST(true),
        DIALER_SOUTH(true),
        DIALER_WEST(true);

        private final boolean dialer;

        HitPart(boolean dialer) {
            this.dialer = dialer;
        }

        public boolean isDialer() {
            return this.dialer;
        }
    }
}