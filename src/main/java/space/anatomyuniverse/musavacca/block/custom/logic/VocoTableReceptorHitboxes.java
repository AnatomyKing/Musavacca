
package space.anatomyuniverse.musavacca.block.custom.logic;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoReceptorLogic.ReceptorPosition;

import java.util.List;

public final class VocoTableReceptorHitboxes {
    private static final List<VocoHitboxes.Part<HitPart>> PARTS = List.of(
            new VocoHitboxes.Part<>(HitPart.RECEPTOR_NORTH_EAST, new VocoHitboxes.Box(10.0D, 12.0D, 0.0D, 16.0D, 16.0D, 6.0D)),
            new VocoHitboxes.Part<>(HitPart.RECEPTOR_NORTH_WEST, new VocoHitboxes.Box(0.0D, 12.0D, 0.0D, 6.0D, 16.0D, 6.0D)),
            new VocoHitboxes.Part<>(HitPart.RECEPTOR_SOUTH_EAST, new VocoHitboxes.Box(10.0D, 12.0D, 10.0D, 16.0D, 16.0D, 16.0D)),
            new VocoHitboxes.Part<>(HitPart.RECEPTOR_SOUTH_WEST, new VocoHitboxes.Box(0.0D, 12.0D, 10.0D, 6.0D, 16.0D, 16.0D))
    );

    public static final VoxelShape SHAPE = VocoHitboxes.shapeOf(PARTS);

    private VocoTableReceptorHitboxes() {}

    public static HitPart detectHitPart(BlockPos pos, BlockHitResult hit) {
        return VocoHitboxes.detect(PARTS, pos, hit, HitPart.NONE);
    }

    public enum HitPart {
        NONE(null),
        RECEPTOR_NORTH_EAST(ReceptorPosition.NORTH_EAST),
        RECEPTOR_NORTH_WEST(ReceptorPosition.NORTH_WEST),
        RECEPTOR_SOUTH_EAST(ReceptorPosition.SOUTH_EAST),
        RECEPTOR_SOUTH_WEST(ReceptorPosition.SOUTH_WEST);

        @Nullable
        private final ReceptorPosition receptor;

        HitPart(@Nullable ReceptorPosition receptor) {
            this.receptor = receptor;
        }

        @Nullable
        public ReceptorPosition receptor() {
            return this.receptor;
        }
    }
}

