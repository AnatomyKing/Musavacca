package space.anatomyuniverse.musavacca.block.custom.logic;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

public final class VocoTableItemDisplayHitboxes {
    private static final List<VocoHitboxes.Part<HitPart>> PARTS = List.of(
            new VocoHitboxes.Part<>(HitPart.ITEM_DISPLAY, new VocoHitboxes.Box(6.0D, 14.0D, 3.0D, 10.0D, 16.0D, 6.0D)),
            new VocoHitboxes.Part<>(HitPart.ITEM_DISPLAY, new VocoHitboxes.Box(6.0D, 14.0D, 6.0D, 10.0D, 16.0D, 10.0D)),
            new VocoHitboxes.Part<>(HitPart.ITEM_DISPLAY, new VocoHitboxes.Box(3.0D, 14.0D, 6.0D, 6.0D, 16.0D, 10.0D)),
            new VocoHitboxes.Part<>(HitPart.ITEM_DISPLAY, new VocoHitboxes.Box(6.0D, 14.0D, 10.0D, 10.0D, 16.0D, 13.0D)),
            new VocoHitboxes.Part<>(HitPart.ITEM_DISPLAY, new VocoHitboxes.Box(10.0D, 14.0D, 6.0D, 13.0D, 16.0D, 10.0D))
    );

    public static final VoxelShape SHAPE = VocoHitboxes.shapeOf(PARTS);

    private VocoTableItemDisplayHitboxes() {}

    public static HitPart detectHitPart(BlockPos pos, BlockHitResult hit) {
        return VocoHitboxes.detect(PARTS, pos, hit, HitPart.NONE);
    }

    public enum HitPart {
        NONE(false),
        ITEM_DISPLAY(true);

        private final boolean itemDisplay;

        HitPart(boolean itemDisplay) {
            this.itemDisplay = itemDisplay;
        }

        public boolean isItemDisplay() {
            return this.itemDisplay;
        }
    }
}
