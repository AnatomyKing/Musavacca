// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/block/custom/logic/VocoTableReceptorHitboxes.java
package space.anatomyuniverse.musavacca.block.custom.logic;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoReceptorLogic.ReceptorPosition;

public final class VocoTableReceptorHitboxes {
    private static final HitBox[] HIT_BOXES = {
            new HitBox(HitPart.RECEPTOR_NORTH_EAST, 10.0D, 12.0D, 0.0D, 16.0D, 16.0D, 6.0D),
            new HitBox(HitPart.RECEPTOR_NORTH_WEST, 0.0D, 12.0D, 0.0D, 6.0D, 16.0D, 6.0D),
            new HitBox(HitPart.RECEPTOR_SOUTH_EAST, 10.0D, 12.0D, 10.0D, 16.0D, 16.0D, 16.0D),
            new HitBox(HitPart.RECEPTOR_SOUTH_WEST, 0.0D, 12.0D, 10.0D, 6.0D, 16.0D, 16.0D),

            new HitBox(HitPart.DIALER_NORTH, 6.0D, 10.0D, -1.0D, 10.0D, 13.0D, 2.0D),
            new HitBox(HitPart.DIALER_EAST, 14.0D, 10.0D, 6.0D, 17.0D, 13.0D, 10.0D),
            new HitBox(HitPart.DIALER_SOUTH, 6.0D, 10.0D, 14.0D, 10.0D, 13.0D, 17.0D),
            new HitBox(HitPart.DIALER_WEST, -1.0D, 10.0D, 6.0D, 2.0D, 13.0D, 10.0D)
    };

    private VocoTableReceptorHitboxes() {}

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
        NONE(null, false),

        RECEPTOR_NORTH_EAST(ReceptorPosition.NORTH_EAST, false),
        RECEPTOR_NORTH_WEST(ReceptorPosition.NORTH_WEST, false),
        RECEPTOR_SOUTH_EAST(ReceptorPosition.SOUTH_EAST, false),
        RECEPTOR_SOUTH_WEST(ReceptorPosition.SOUTH_WEST, false),

        DIALER_NORTH(null, true),
        DIALER_EAST(null, true),
        DIALER_SOUTH(null, true),
        DIALER_WEST(null, true);

        @Nullable
        private final ReceptorPosition receptor;
        private final boolean togglesBasuke;

        HitPart(@Nullable ReceptorPosition receptor, boolean togglesBasuke) {
            this.receptor = receptor;
            this.togglesBasuke = togglesBasuke;
        }

        @Nullable
        public ReceptorPosition receptor() {
            return this.receptor;
        }

        public boolean togglesBasuke() {
            return this.togglesBasuke;
        }
    }
}