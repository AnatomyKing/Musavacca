package space.anatomyuniverse.musavacca.block.custom.logic;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class MusavaccaPortalTrapdoorVoxelShapes {

    private MusavaccaPortalTrapdoorVoxelShapes() {}

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

    public static VoxelShape collisionShape(
            VoxelShape trapdoorShape
    ) {
        return trapdoorShape;
    }
}

