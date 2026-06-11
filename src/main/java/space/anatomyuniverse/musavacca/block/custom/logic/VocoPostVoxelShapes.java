
package space.anatomyuniverse.musavacca.block.custom.logic;

import net.minecraft.world.phys.shapes.VoxelShape;

public final class VocoPostVoxelShapes {
    public static final VoxelShape RECEPTOR_SHAPE = VocoPostReceptorHitboxes.RECEPTOR_BOX.toShape();
    public static final VoxelShape POLE_SHAPE = VocoPostReceptorHitboxes.POLE_BOX.toShape();
    public static final VoxelShape BOTTOM_SHAPE = VocoPostReceptorHitboxes.BOTTOM_BOX.toShape();
    public static final VoxelShape SHAPE = VocoPostReceptorHitboxes.SHAPE;

    private VocoPostVoxelShapes() {}
}