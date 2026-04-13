package space.anatomyuniverse.musavacca.entity.mob.basuke.clientmodel;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import space.anatomyuniverse.musavacca.MusaCore;
import space.anatomyuniverse.musavacca.entity.mob.basuke.Basuke;

public final class BasukeRenderer extends MobRenderer<Basuke, BasukeModel.State, BasukeModel> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(MusaCore.MOD_ID, "textures/entity/basuke/basuke.png");

    private static final float SHADOW = 0.35F;

    private final ItemModelResolver itemModelResolver;

    public BasukeRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new BasukeModel(ctx.bakeLayer(BasukeModel.LAYER_LOCATION)), SHADOW);
        this.itemModelResolver = ctx.getItemModelResolver();
        this.addLayer(new ItemInHandLayer<>(this));
    }

    @Override
    protected int getBlockLightLevel(@NotNull Basuke entity, @NotNull BlockPos pos) {
        return 15;
    }

    @Override
    public @NotNull BasukeModel.State createRenderState() {
        return new BasukeModel.State();
    }

    @Override
    public void extractRenderState(
            @NotNull Basuke entity,
            @NotNull BasukeModel.State s,
            float partialTick
    ) {
        super.extractRenderState(entity, s, partialTick);
        ArmedEntityRenderState.extractArmedEntityRenderState(entity, s, this.itemModelResolver);

        float bodyYaw = Mth.rotLerp(partialTick, entity.yBodyRotO, entity.yBodyRot);
        float headYaw = Mth.rotLerp(partialTick, entity.yHeadRotO, entity.yHeadRot);
        float netHeadYawDeg = Mth.wrapDegrees(headYaw - bodyYaw);
        float headPitchDeg = Mth.lerp(partialTick, entity.xRotO, entity.getXRot());

        netHeadYawDeg = Mth.clamp(netHeadYawDeg, -35.0F, 35.0F);
        headPitchDeg = Mth.clamp(headPitchDeg, -25.0F, 25.0F);

        s.headYawRad = netHeadYawDeg * Mth.DEG_TO_RAD;
        s.headPitchRad = headPitchDeg * Mth.DEG_TO_RAD;
        s.limbSwing = entity.walkAnimation.position(partialTick);
        s.limbSwingAmount = entity.walkAnimation.speed();
        s.ageTicks = entity.tickCount + partialTick;
        s.flyAmount = Mth.clamp((float) (entity.getDeltaMovement().length() * 6.0D), 0.0F, 1.0F);
        s.holdingAnimationProgress = entity.getHoldingItemAnimationProgress(partialTick);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull BasukeModel.State s) {
        return TEXTURE;
    }
}