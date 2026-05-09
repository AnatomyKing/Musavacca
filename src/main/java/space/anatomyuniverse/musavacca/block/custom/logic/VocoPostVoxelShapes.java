// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/block/custom/logic/VocoPostVoxelShapes.java
package space.anatomyuniverse.musavacca.block.custom.logic;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class VocoPostVoxelShapes {
    public static final VoxelShape RECEPTOR_SHAPE = Block.box(
            5.0D, 12.0D, 5.0D,
            11.0D, 16.0D, 11.0D
    );

    public static final VoxelShape POLE_SHAPE = Block.box(
            6.0D, 4.0D, 6.0D,
            10.0D, 12.0D, 10.0D
    );

    public static final VoxelShape BOTTOM_SHAPE = Block.box(
            5.0D, 0.0D, 5.0D,
            11.0D, 4.0D, 11.0D
    );

    public static final VoxelShape SHAPE = Shapes.or(
            RECEPTOR_SHAPE,
            POLE_SHAPE,
            BOTTOM_SHAPE
    );

    private VocoPostVoxelShapes() {}
}