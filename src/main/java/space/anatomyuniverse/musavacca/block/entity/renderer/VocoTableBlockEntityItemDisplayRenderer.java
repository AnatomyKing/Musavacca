
package space.anatomyuniverse.musavacca.block.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import space.anatomyuniverse.musavacca.block.entity.custom.VocoTableBlockEntity;

//? if >=1.21.9 {
/*import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.item.ItemModelResolver;
*///?} else {
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemStack;
//?}

//? if >=1.21.4 <1.21.9 {
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.phys.Vec3;
//?}

public final class VocoTableBlockEntityItemDisplayRenderer {

    //? if >=1.21.9 {
    /*private final ItemModelResolver resolver;
    *///?} else if >=1.21.4 {
    private final ItemStackRenderState itemRenderState = new ItemStackRenderState();
     //?}

    public VocoTableBlockEntityItemDisplayRenderer(BlockEntityRendererProvider.Context context) {
        //? if >=1.21.9 {
        /*resolver = context.itemModelResolver();
        *///?}
    }

    //? if >=1.21.9 {
    /*public void extract(VocoTableBlockEntity entity, VocoTableBlockEntityRenderer.State state, float partialTick) {
        state.item.clear();
        if (entity.getLevel() == null || entity.getDisplayedItem().isEmpty()) return;

        resolver.updateForTopItem(state.item, entity.getDisplayedItem(), ItemDisplayContext.GROUND,
                entity.getLevel(), null, 0);

        state.itemRotation = (((entity.getLevel().getGameTime() % 400L) + partialTick) * 2.7F) % 360.0F;
    }

    public static void submit(VocoTableBlockEntityRenderer.State state, PoseStack pose, SubmitNodeCollector collector) {
        if (state.item.isEmpty()) return;
        float rotation = state.itemRotation;

        *///?} else if >=1.21.4 {
    public void render(VocoTableBlockEntity entity, float partialTick, PoseStack pose,
                       MultiBufferSource buffer, int light, int overlay, Vec3 cameraPos) {
        ItemStack stack = entity.getDisplayedItem();
        if (stack.isEmpty() || entity.getLevel() == null) return;
        float rotation = (((entity.getLevel().getGameTime() % 400L) + partialTick) * 2.7F) % 360.0F;

    //?} else {
    /*public void render(VocoTableBlockEntity entity, float partialTick, PoseStack pose,
                       MultiBufferSource buffer, int light, int overlay) {
        ItemStack stack = entity.getDisplayedItem();
        if (stack.isEmpty() || entity.getLevel() == null) return;
        float rotation = (((entity.getLevel().getGameTime() % 400L) + partialTick) * 2.7F) % 360.0F;

    *///?}

        pose.pushPose();
        pose.translate(0.5D, 1.20D, 0.5D);

        //? if >=26.3 {
        /*pose.rotate(Axis.YP.rotationDegrees(rotation));
        *///?} else {
        pose.mulPose(Axis.YP.rotationDegrees(rotation));
         //?}

        pose.scale(0.82F, 0.82F, 0.82F);

        //? if >=1.21.9 {
        /*state.item.submit(pose, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);

        *///?} else if >=1.21.5 {
        Minecraft.getInstance().getItemModelResolver().updateForTopItem(
                itemRenderState, stack, ItemDisplayContext.GROUND,
                entity.getLevel(), null, 0);
        itemRenderState.render(pose, buffer, light, OverlayTexture.NO_OVERLAY);

        //?} else if >=1.21.4 {
        /*Minecraft.getInstance().getItemModelResolver().updateForTopItem(
                itemRenderState, stack, ItemDisplayContext.GROUND,
                false, entity.getLevel(), null, 0);
        itemRenderState.render(pose, buffer, light, OverlayTexture.NO_OVERLAY);

        *///?} else {
        /*Minecraft.getInstance().getItemRenderer().renderStatic(
                stack, ItemDisplayContext.GROUND, light, OverlayTexture.NO_OVERLAY,
                pose, buffer, entity.getLevel(), 0);

        *///?}

        pose.popPose();
    }
}
