package space.anatomyuniverse.musavacca.block.custom.logic;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoHitboxes.Box;

public final class VocoPostHitboxes {
    private static final Box RECEPTOR = new Box(5.0D, 12.0D, 5.0D, 11.0D, 16.0D, 11.0D);
    public static final VoxelShape SHAPE = VocoHitboxes.shapeOf(List.of(RECEPTOR,
            new Box(6.0D, 4.0D, 6.0D, 10.0D, 12.0D, 10.0D), new Box(5.0D, 0.0D, 5.0D, 11.0D, 4.0D, 11.0D)));

    private VocoPostHitboxes() {}

    public static boolean isReceptor(BlockPos pos, BlockHitResult hit) {
        return RECEPTOR.contains(VocoHitboxes.local16(pos, hit));
    }
}
