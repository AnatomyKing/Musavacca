package space.anatomyuniverse.musavacca.block.custom.logic;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import space.anatomyuniverse.musavacca.block.custom.MusavaccaPortalTrapdoorBlock;

public final class MusavaccaPortalTrapdoorHitboxes {

    /*
     * The portal aperture stays FLAT/HORIZONTAL even while the wooden
     * trapdoor itself is physically OPEN and therefore upright.
     *
     * HALF=BOTTOM:
     *     Y 0..2
     *
     * HALF=TOP:
     *     Y 14..16
     */
    private static final VoxelShape BOTTOM =
            Block.box(
                    0.0D,
                    0.0D,
                    0.0D,
                    16.0D,
                    2.0D,
                    16.0D
            );

    private static final VoxelShape TOP =
            Block.box(
                    0.0D,
                    14.0D,
                    0.0D,
                    16.0D,
                    16.0D,
                    16.0D
            );

    private MusavaccaPortalTrapdoorHitboxes() {}

    public static boolean hasOpenPortal(
            BlockState state
    ) {
        return state.hasProperty(
                MusavaccaPortalTrapdoorBlock.PORTAL
        )
                && state.getValue(
                MusavaccaPortalTrapdoorBlock.PORTAL
        )
                && state.hasProperty(
                MusavaccaPortalTrapdoorBlock.OPEN
        )
                && state.getValue(
                MusavaccaPortalTrapdoorBlock.OPEN
        );
    }

    public static VoxelShape portalPanel(
            BlockState state
    ) {
        if (!hasOpenPortal(state)) {
            return Shapes.empty();
        }

        return state.getValue(
                MusavaccaPortalTrapdoorBlock.HALF
        )
                == Half.TOP
                ? TOP
                : BOTTOM;
    }
}


