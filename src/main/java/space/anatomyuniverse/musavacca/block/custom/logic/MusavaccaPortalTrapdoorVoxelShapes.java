package space.anatomyuniverse.musavacca.block.custom.logic;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class MusavaccaPortalTrapdoorVoxelShapes {

    private MusavaccaPortalTrapdoorVoxelShapes() {}

    /*
     * Adds the flat horizontal 2px portal panel to the visible selection outline.
     *
     * This lets the player target the portal pane with the crosshair while
     * preserving the ordinary vanilla trapdoor outline.
     */
    public static VoxelShape outlineShape(
            BlockState state,
            VoxelShape trapdoorShape
    ) {
        if (
                !MusavaccaPortalTrapdoorHitboxes
                        .hasOpenPortal(state)
        ) {
            return trapdoorShape;
        }

        return Shapes.or(
                trapdoorShape,
                MusavaccaPortalTrapdoorHitboxes
                        .portalPanel(state)
        );
    }

    /*
     * The portal pane is deliberately excluded from collision.
     *
     * Only the normal vanilla trapdoor collision remains.
     *
     * The flat portal aperture itself has no collision and can later become
     * the teleport trigger without changing this shape setup.
     */
    public static VoxelShape collisionShape(
            VoxelShape trapdoorShape
    ) {
        return trapdoorShape;
    }
}
