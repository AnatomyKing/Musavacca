// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/block/custom/logic/VocoPostReceptorHitboxes.java
package space.anatomyuniverse.musavacca.block.custom.logic;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public final class VocoPostReceptorHitboxes {
    private static final HitBox[] HIT_BOXES = {
            new HitBox(HitPart.RECEPTOR, 5.0D, 12.0D, 5.0D, 11.0D, 16.0D, 11.0D),
            new HitBox(HitPart.POLE, 6.0D, 4.0D, 6.0D, 10.0D, 12.0D, 10.0D),
            new HitBox(HitPart.BOTTOM, 5.0D, 0.0D, 5.0D, 11.0D, 4.0D, 11.0D)
    };

    private VocoPostReceptorHitboxes() {}

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
        RECEPTOR(true),
        POLE(false),
        BOTTOM(false);

        private final boolean receptor;

        HitPart(boolean receptor) {
            this.receptor = receptor;
        }

        public boolean isReceptor() {
            return this.receptor;
        }
    }
}