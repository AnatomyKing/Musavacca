
package space.anatomyuniverse.musavacca.block.custom.logic;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

public final class VocoPostReceptorHitboxes {
    public static final VocoHitboxes.Box RECEPTOR_BOX =
            new VocoHitboxes.Box(5.0D, 12.0D, 5.0D, 11.0D, 16.0D, 11.0D);

    public static final VocoHitboxes.Box POLE_BOX =
            new VocoHitboxes.Box(6.0D, 4.0D, 6.0D, 10.0D, 12.0D, 10.0D);

    public static final VocoHitboxes.Box BOTTOM_BOX =
            new VocoHitboxes.Box(5.0D, 0.0D, 5.0D, 11.0D, 4.0D, 11.0D);

    private static final List<VocoHitboxes.Part<HitPart>> PARTS = List.of(
            new VocoHitboxes.Part<>(HitPart.RECEPTOR, RECEPTOR_BOX),
            new VocoHitboxes.Part<>(HitPart.POLE, POLE_BOX),
            new VocoHitboxes.Part<>(HitPart.BOTTOM, BOTTOM_BOX)
    );

    public static final VoxelShape SHAPE = VocoHitboxes.shapeOf(PARTS);

    private VocoPostReceptorHitboxes() {}

    public static HitPart detectHitPart(BlockPos pos, BlockHitResult hit) {
        return VocoHitboxes.detect(PARTS, pos, hit, HitPart.NONE);
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

