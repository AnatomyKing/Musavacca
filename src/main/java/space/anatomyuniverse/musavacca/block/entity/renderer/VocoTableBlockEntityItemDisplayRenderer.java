package space.anatomyuniverse.musavacca.block.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import space.anatomyuniverse.musavacca.block.entity.custom.VocoTableBlockEntity;

public final class VocoTableBlockEntityItemDisplayRenderer {

    private static final double ITEM_X = 0.5D;
    private static final double ITEM_Y = 1.20D;
    private static final double ITEM_Z = 0.5D;
    private static final float ITEM_SCALE = 0.82F;
    private static final float ITEM_ROTATION_SPEED = 2.7F;

    private final ItemStackRenderState itemRenderState = new ItemStackRenderState();

    public VocoTableBlockEntityItemDisplayRenderer(BlockEntityRendererProvider.Context context) {
    }

    private static float rotationDegrees(long gameTime, float partialTick) {
        return ((gameTime + partialTick) * ITEM_ROTATION_SPEED) % 360.0F;
    }

    public void render(
            VocoTableBlockEntity blockEntity,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            int packedOverlay,
            Vec3 cameraPos
    ) {
        ItemStack stack = blockEntity.getDisplayedItem();
        if (stack.isEmpty() || blockEntity.getLevel() == null) {
            return;
        }

        long gameTime = blockEntity.getLevel().getGameTime();

        poseStack.pushPose();
        poseStack.translate(ITEM_X, ITEM_Y, ITEM_Z);
        poseStack.mulPose(Axis.YP.rotationDegrees(rotationDegrees(gameTime, partialTick)));
        poseStack.scale(ITEM_SCALE, ITEM_SCALE, ITEM_SCALE);

        Minecraft.getInstance().getItemModelResolver().updateForTopItem(
                this.itemRenderState,
                stack,
                ItemDisplayContext.GROUND,
                blockEntity.getLevel(),
                null,
                0
        );

        this.itemRenderState.render(
                poseStack,
                bufferSource,
                packedLight,
                OverlayTexture.NO_OVERLAY
        );

        poseStack.popPose();
    }
}
