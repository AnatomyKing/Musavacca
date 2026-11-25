package space.anatomyuniverse.musavacca.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import space.anatomyuniverse.musavacca.item.custom.potassium.PotassiumThrow;
//? if <1.21.4 {
/*import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemDisplayContext;
*///?}
//? if >=1.21.2 {
import net.minecraft.client.renderer.entity.state.ThrownItemRenderState;
//?}
//? if >=1.21.9 {
/*import net.minecraft.client.renderer.SubmitNodeCollector;
//? if >=26.1 {
/^import net.minecraft.client.renderer.state.level.CameraRenderState;
^///?} else {
import net.minecraft.client.renderer.state.CameraRenderState;
//?}
*///?} else {
import net.minecraft.client.renderer.MultiBufferSource;
//?}

public final class PotassiumProjectileRenderer extends ThrownItemRenderer<PotassiumProjectile> {
    //? if <1.21.4 {
    /*private final ItemRenderer itemRenderer;
    *///?}

    public PotassiumProjectileRenderer(EntityRendererProvider.Context context) {
        super(context);
        //? if <1.21.4 {
        /*itemRenderer = context.getItemRenderer();
        *///?}
    }

    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(PotassiumThrow.PROJECTILE.get(), PotassiumProjectileRenderer::new);
    }

    private static float heading(PotassiumProjectile entity) {
        Vec3 movement = entity.getDeltaMovement();
        if (movement.horizontalDistanceSqr() < 1.0E-6) return entity.getYRot() - 90.0F;
        return (float) Math.toDegrees(Math.atan2(-movement.z, movement.x));
    }

    private static void rotate(PoseStack pose, float age, float heading) {
        pose.translate(0, 0.25F, 0);
        pose.scale(2.0F, 2.0F, 2.0F);
        pose.mulPose(Axis.YP.rotationDegrees(heading));
        pose.mulPose(Axis.ZP.rotationDegrees(-age * 40.0F));
    }

    //? if >=1.21.2 {
    private static final class ToolRenderState extends ThrownItemRenderState {
        float heading;
    }

    @Override
    public ThrownItemRenderState createRenderState() {
        return new ToolRenderState();
    }

    @Override
    public void extractRenderState(PotassiumProjectile entity, ThrownItemRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        ((ToolRenderState) state).heading = heading(entity);
    }
    //?}

    @Override
    //? if >=1.21.9 {
    /*public void submit(ThrownItemRenderState state, PoseStack pose, SubmitNodeCollector collector, CameraRenderState camera) {
        pose.pushPose();
        rotate(pose, state.ageInTicks, ((ToolRenderState) state).heading);
        state.item.submit(pose, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);
        pose.popPose();
    }
    *///?} else if >=1.21.4 {
    public void render(ThrownItemRenderState state, PoseStack pose, MultiBufferSource buffers, int light) {
        pose.pushPose();
        rotate(pose, state.ageInTicks, ((ToolRenderState) state).heading);
        state.item.render(pose, buffers, light, OverlayTexture.NO_OVERLAY);
        pose.popPose();
    }
    //?} else if >=1.21.2 {
    /*public void render(ThrownItemRenderState state, PoseStack pose, MultiBufferSource buffers, int light) {
        pose.pushPose();
        rotate(pose, state.ageInTicks, ((ToolRenderState) state).heading);
        itemRenderer.render(state.item, ItemDisplayContext.GROUND, false, pose, buffers, light, OverlayTexture.NO_OVERLAY, state.itemModel);
        pose.popPose();
    }
    *///?} else {
    /*public void render(PotassiumProjectile entity, float yaw, float partialTick, PoseStack pose, MultiBufferSource buffers, int light) {
        pose.pushPose();
        rotate(pose, entity.tickCount + partialTick, heading(entity));
        itemRenderer.renderStatic(
                entity.getItem(),
                ItemDisplayContext.GROUND,
                light,
                OverlayTexture.NO_OVERLAY,
                pose,
                buffers,
                entity.level(),
                entity.getId());
        pose.popPose();
    }
    *///?}
}
