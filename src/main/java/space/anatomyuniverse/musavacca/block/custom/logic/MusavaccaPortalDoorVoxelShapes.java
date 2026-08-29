package space.anatomyuniverse.musavacca.block.custom.logic;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class MusavaccaPortalDoorVoxelShapes {

    private MusavaccaPortalDoorVoxelShapes() {}

    /*
     * Adds the portal panel to the visible selection outline.
     *
     * This also lets the player target the portal panel with
     * the crosshair while preserving the ordinary door outline.
     */
    public static VoxelShape outlineShape(
            BlockState state,
            VoxelShape doorShape
    ) {
        if (
                !MusavaccaPortalDoorHitboxes
                        .hasOpenPortal(state)
        ) {
            return doorShape;
        }

        return Shapes.or(
                doorShape,
                MusavaccaPortalDoorHitboxes
                        .portalPanel(state)
        );
    }

    /*
     * The portal panel is deliberately excluded from collision.
     *
     * Only the normal vanilla door collision remains, allowing
     * players and entities to walk through the portal surface.
     */
    public static VoxelShape collisionShape(
            VoxelShape doorShape
    ) {
        return doorShape;
    }
}

