// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/block/custom/logic/VocoTableDialerHitboxes.java
package space.anatomyuniverse.musavacca.block.custom.logic;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

public final class VocoTableDialerHitboxes {
    private static final List<VocoHitboxes.Part<HitPart>> PARTS = List.of(
            new VocoHitboxes.Part<>(HitPart.DIALER_NORTH, new VocoHitboxes.Box(6.0D, 10.0D, -1.0D, 10.0D, 13.0D, 2.0D)),
            new VocoHitboxes.Part<>(HitPart.DIALER_EAST, new VocoHitboxes.Box(14.0D, 10.0D, 6.0D, 17.0D, 13.0D, 10.0D)),
            new VocoHitboxes.Part<>(HitPart.DIALER_SOUTH, new VocoHitboxes.Box(6.0D, 10.0D, 14.0D, 10.0D, 13.0D, 17.0D)),
            new VocoHitboxes.Part<>(HitPart.DIALER_WEST, new VocoHitboxes.Box(-1.0D, 10.0D, 6.0D, 2.0D, 13.0D, 10.0D))
    );

    public static final VoxelShape SHAPE = VocoHitboxes.shapeOf(PARTS);

    private VocoTableDialerHitboxes() {}

    public static HitPart detectHitPart(BlockPos pos, BlockHitResult hit) {
        return VocoHitboxes.detect(PARTS, pos, hit, HitPart.NONE);
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