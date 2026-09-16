package space.anatomyuniverse.musavacca.block.custom.logic;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class MusavaccaPortalDoorVoxelShapes {

    private MusavaccaPortalDoorVoxelShapes() {}

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

    public static VoxelShape collisionShape(
            VoxelShape doorShape
    ) {
        return doorShape;
    }
}

