package space.anatomyuniverse.musavacca.block.custom.logic;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import space.anatomyuniverse.musavacca.block.custom.MusavaccaPortalDoorBlock;

public final class MusavaccaPortalDoorHitboxes {


    private static final VoxelShape NORTH =
            Block.box(
                    0.0D,
                    0.0D,
                    14.0D,
                    16.0D,
                    16.0D,
                    16.0D
            );

    private static final VoxelShape EAST =
            Block.box(
                    0.0D,
                    0.0D,
                    0.0D,
                    2.0D,
                    16.0D,
                    16.0D
            );

    private static final VoxelShape SOUTH =
            Block.box(
                    0.0D,
                    0.0D,
                    0.0D,
                    16.0D,
                    16.0D,
                    2.0D
            );

    private static final VoxelShape WEST =
            Block.box(
                    14.0D,
                    0.0D,
                    0.0D,
                    16.0D,
                    16.0D,
                    16.0D
            );

    private MusavaccaPortalDoorHitboxes() {}

    public static boolean hasOpenPortal(
            BlockState state
    ) {
        return state.hasProperty(
                MusavaccaPortalDoorBlock.PORTAL
        )
                && state.getValue(
                MusavaccaPortalDoorBlock.PORTAL
        )
                && state.hasProperty(
                MusavaccaPortalDoorBlock.OPEN
        )
                && state.getValue(
                MusavaccaPortalDoorBlock.OPEN
        );
    }

    public static Direction portalFacing(
            BlockState state
    ) {
        return state.getValue(
                MusavaccaPortalDoorBlock.FACING
        );
    }

    public static VoxelShape portalPanel(
            BlockState state
    ) {
        if (!hasOpenPortal(state)) {
            return Shapes.empty();
        }

        Direction facing =
                portalFacing(
                        state
                );

        return switch (facing) {
            case NORTH ->
                    NORTH;

            case EAST ->
                    EAST;

            case SOUTH ->
                    SOUTH;

            case WEST ->
                    WEST;

            default ->
                    Shapes.empty();
        };
    }
}
