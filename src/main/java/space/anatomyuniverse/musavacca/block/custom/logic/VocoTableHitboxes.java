package space.anatomyuniverse.musavacca.block.custom.logic;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoHitboxes.Box;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoReceptorLogic.ReceptorPosition;

public final class VocoTableHitboxes {
    private static final List<Box> RECEPTORS = List.of(
            new Box(10.0D, 12.0D, 0.0D, 16.0D, 16.0D, 6.0D),
            new Box(0.0D, 12.0D, 0.0D, 6.0D, 16.0D, 6.0D),
            new Box(10.0D, 12.0D, 10.0D, 16.0D, 16.0D, 16.0D),
            new Box(0.0D, 12.0D, 10.0D, 6.0D, 16.0D, 16.0D));
    private static final List<Box> DIALERS = List.of(
            new Box(6.0D, 10.0D, -1.0D, 10.0D, 13.0D, 2.0D),
            new Box(14.0D, 10.0D, 6.0D, 17.0D, 13.0D, 10.0D),
            new Box(6.0D, 10.0D, 14.0D, 10.0D, 13.0D, 17.0D),
            new Box(-1.0D, 10.0D, 6.0D, 2.0D, 13.0D, 10.0D));
    private static final List<Box> ITEM_DISPLAY = List.of(
            new Box(6.0D, 14.0D, 3.0D, 10.0D, 16.0D, 6.0D),
            new Box(6.0D, 14.0D, 6.0D, 10.0D, 16.0D, 10.0D),
            new Box(3.0D, 14.0D, 6.0D, 6.0D, 16.0D, 10.0D),
            new Box(6.0D, 14.0D, 10.0D, 10.0D, 16.0D, 13.0D),
            new Box(10.0D, 14.0D, 6.0D, 13.0D, 16.0D, 10.0D));
    public static final VoxelShape RECEPTOR_SHAPE = VocoHitboxes.shapeOf(RECEPTORS);
    public static final VoxelShape DIALER_SHAPE = VocoHitboxes.shapeOf(DIALERS);
    public static final VoxelShape ITEM_DISPLAY_SHAPE = VocoHitboxes.shapeOf(ITEM_DISPLAY);

    private VocoTableHitboxes() {}

    @Nullable
    public static ReceptorPosition receptorAt(BlockPos pos, BlockHitResult hit) {
        int index = VocoHitboxes.findHit(RECEPTORS, pos, hit);
        return index < 0 ? null : ReceptorPosition.byId(index);
    }

    public static boolean isDialer(BlockPos pos, BlockHitResult hit) {
        return VocoHitboxes.findHit(DIALERS, pos, hit) >= 0;
    }

    public static boolean isItemDisplay(BlockPos pos, BlockHitResult hit) {
        return VocoHitboxes.findHit(ITEM_DISPLAY, pos, hit) >= 0;
    }
}
