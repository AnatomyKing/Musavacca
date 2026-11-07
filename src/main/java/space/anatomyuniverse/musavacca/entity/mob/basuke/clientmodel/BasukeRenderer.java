package space.anatomyuniverse.musavacca.entity.mob.basuke.clientmodel;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
//? if >=1.21.4 {
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
//?}
import net.minecraft.core.BlockPos;
//? if >=1.21.11 {
/*import net.minecraft.resources.Identifier;
*///?} else {
import net.minecraft.resources.ResourceLocation;
//?}
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import space.anatomyuniverse.musavacca.MusaCore;
import space.anatomyuniverse.musavacca.entity.mob.basuke.Basuke;

public final class BasukeRenderer extends
        //? if >=1.21.2 {
        MobRenderer<Basuke, BasukeModel.State, BasukeModel>
        //?} else {
        /*MobRenderer<Basuke, BasukeModel>
        *///?}
{
    //? if >=1.21.11 {
    /*private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(MusaCore.MOD_ID, "textures/entity/basuke/basuke.png");
    *///?} else {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(MusaCore.MOD_ID, "textures/entity/basuke/basuke.png");

    //?}
    //? if >=1.21.4 {
    private final ItemModelResolver itemModelResolver;

    //?}
    public BasukeRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new BasukeModel(ctx.bakeLayer(BasukeModel.LAYER_LOCATION)), 0.35F);
        //? if >=1.21.4 {
        this.itemModelResolver = ctx.getItemModelResolver();
        this.addLayer(new ItemInHandLayer<>(this));
        //?} else if >=1.21.2 <1.21.4 {
        /*this.addLayer(new ItemInHandLayer<>(this, ctx.getItemRenderer()));
        *///?} else {
        /*this.addLayer(new ItemInHandLayer<>(this, ctx.getItemInHandRenderer()));
        *///?}
    }

    @Override
    protected int getBlockLightLevel(@NotNull Basuke entity, @NotNull BlockPos pos) {
        return 15;
    }

    //? if >=1.21.2 {
    @Override
    public @NotNull BasukeModel.State createRenderState() {
        return new BasukeModel.State();
    }

    @Override
    public void extractRenderState(@NotNull Basuke entity, @NotNull BasukeModel.State state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        //? if >=1.21.11 {
        /*ArmedEntityRenderState.extractArmedEntityRenderState(entity, state, this.itemModelResolver, partialTick);
        *///?} else if >=1.21.4 {
        ArmedEntityRenderState.extractArmedEntityRenderState(entity, state, this.itemModelResolver);
        //?}
        float bodyYaw = Mth.rotLerp(partialTick, entity.yBodyRotO, entity.yBodyRot);
        float headYaw = Mth.rotLerp(partialTick, entity.yHeadRotO, entity.yHeadRot);
        float netHeadYawDeg = Mth.wrapDegrees(headYaw - bodyYaw);
        float headPitchDeg = Mth.lerp(partialTick, entity.xRotO, entity.getXRot());
        netHeadYawDeg = Mth.clamp(netHeadYawDeg, -35.0F, 35.0F);
        headPitchDeg = Mth.clamp(headPitchDeg, -25.0F, 25.0F);
        state.headYawRad = netHeadYawDeg * Mth.DEG_TO_RAD;
        state.headPitchRad = headPitchDeg * Mth.DEG_TO_RAD;
        state.limbSwing = entity.walkAnimation.position(partialTick);
        state.ageTicks = entity.tickCount + partialTick;
        state.flyAmount = Mth.clamp((float) (entity.getDeltaMovement().length() * 6.0D), 0.0F, 1.0F);
        state.holdingAnimationProgress = entity.getHoldingItemAnimationProgress(partialTick);
        state.eatingTicks = entity.getBasukeEatingTicks();
        state.eatingTotalTicks = entity.getBasukeEatingTotalTicks();
    }

    //?}
    @Override
    //? if >=1.21.11 {
    /*public @NotNull Identifier getTextureLocation(@NotNull BasukeModel.State state) {
    *///?} else if >=1.21.2 <1.21.11 {
    public @NotNull ResourceLocation getTextureLocation(@NotNull BasukeModel.State state) {
    //?} else {
    /*public @NotNull ResourceLocation getTextureLocation(@NotNull Basuke entity) {
    *///?}
        return TEXTURE;
    }
}
