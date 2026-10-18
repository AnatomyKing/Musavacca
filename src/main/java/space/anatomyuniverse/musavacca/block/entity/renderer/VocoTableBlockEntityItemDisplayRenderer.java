package space.anatomyuniverse.musavacca.block.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
//? if >=1.21.9 {
/*import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.item.ItemModelResolver;
*///?} else if >=1.21.4 <1.21.9 {
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

import net.minecraft.client.renderer.item.ItemStackRenderState;

//?} else {
/*import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
*///?}
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
//? if <1.21.9 {
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
//?}
import space.anatomyuniverse.musavacca.block.entity.custom.VocoTableBlockEntity;

public final class VocoTableBlockEntityItemDisplayRenderer {
    //? if >=26.3 {
    /*private final ItemModelResolver resolver;
    public VocoTableBlockEntityItemDisplayRenderer(BlockEntityRendererProvider.Context context) {
        resolver = context.itemModelResolver();
    }
    public void extract(VocoTableBlockEntity entity, VocoTableBlockEntityRenderer.State state, float partialTick) {
        state.item.clear();
        if (entity.getLevel() == null || entity.getDisplayedItem().isEmpty()) return;
        resolver.updateForTopItem(state.item, entity.getDisplayedItem(), ItemDisplayContext.GROUND,
                                 entity.getLevel(), null, 0);
        state.itemRotation = ((entity.getLevel().getGameTime() + partialTick) * 2.7F) % 360.0F;
    }
    public static void submit(VocoTableBlockEntityRenderer.State state, PoseStack pose, SubmitNodeCollector collector) {
        if (state.item.isEmpty()) return;
        pose.pushPose();
        pose.translate(0.5D, 1.20D, 0.5D);
        pose.rotate(Axis.YP.rotationDegrees(state.itemRotation));
        pose.scale(0.82F, 0.82F, 0.82F);
        state.item.submit(pose, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
        pose.popPose();
    }
    *///?} else if >=1.21.9 <26.3 {
    /*private final ItemModelResolver resolver;
    public VocoTableBlockEntityItemDisplayRenderer(BlockEntityRendererProvider.Context context) {
        resolver = context.itemModelResolver();
    }
    public void extract(VocoTableBlockEntity entity, VocoTableBlockEntityRenderer.State state, float partialTick) {
        state.item.clear();
        if (entity.getLevel() == null || entity.getDisplayedItem().isEmpty()) return;
        resolver.updateForTopItem(state.item, entity.getDisplayedItem(), ItemDisplayContext.GROUND,
                                 entity.getLevel(), null, 0);
        state.itemRotation = ((entity.getLevel().getGameTime() + partialTick) * 2.7F) % 360.0F;
    }
    public static void submit(VocoTableBlockEntityRenderer.State state, PoseStack pose, SubmitNodeCollector collector) {
        if (state.item.isEmpty()) return;
        pose.pushPose();
        pose.translate(0.5D, 1.20D, 0.5D);
        pose.mulPose(Axis.YP.rotationDegrees(state.itemRotation));
        pose.scale(0.82F, 0.82F, 0.82F);
        state.item.submit(pose, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
        pose.popPose();
    }
    *///?} else if >=1.21.5 <1.21.9 {

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
    //?} else if >=1.21.4 <1.21.5 {
    /*private static final double ITEM_X = 0.5D;
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
                false,
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
    *///?} else {
    /*private static final double ITEM_X = 0.5D;
    private static final double ITEM_Y = 1.20D;
    private static final double ITEM_Z = 0.5D;
    private static final float ITEM_SCALE = 0.82F;
    private static final float ITEM_ROTATION_SPEED = 2.7F;

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

        Minecraft.getInstance().getItemRenderer().renderStatic(
                stack,
                ItemDisplayContext.GROUND,
                packedLight,
                OverlayTexture.NO_OVERLAY,
                poseStack,
                bufferSource,
                blockEntity.getLevel(),
                0
        );

        poseStack.popPose();
    }
    *///?}
}
