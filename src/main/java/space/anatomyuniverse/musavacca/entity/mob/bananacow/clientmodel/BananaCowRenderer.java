package space.anatomyuniverse.musavacca.entity.mob.bananacow.clientmodel;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import space.anatomyuniverse.musavacca.MusaCore;
import space.anatomyuniverse.musavacca.entity.mob.bananacow.BananaCow;

//? if <1.21.2 {
/*public final class BananaCowRenderer extends MobRenderer<BananaCow, BananaCowModel> {
 *///?} else {
public final class BananaCowRenderer extends MobRenderer<BananaCow, BananaCowModel.State, BananaCowModel> {
//?}

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(MusaCore.MOD_ID, "textures/entity/cow/banana_cow.png");

    private static final float SHADOW = 0.7F;

    public BananaCowRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new BananaCowModel(ctx.bakeLayer(BananaCowModel.LAYER_LOCATION)), SHADOW);
    }

    //? if >=1.21.2 {
    @Override
    public @NotNull BananaCowModel.State createRenderState() {
        return new BananaCowModel.State();
    }

    @Override
    public void extractRenderState(@NotNull BananaCow entity,
                                   @NotNull BananaCowModel.State s,
                                   float partialTick) {
        super.extractRenderState(entity, s, partialTick);

        float bodyYaw = Mth.rotLerp(partialTick, entity.yBodyRotO, entity.yBodyRot);
        float headYaw = Mth.rotLerp(partialTick, entity.yHeadRotO, entity.yHeadRot);
        float netHeadYawDeg = Mth.wrapDegrees(headYaw - bodyYaw);

        float headPitchDeg = Mth.lerp(partialTick, entity.xRotO, entity.getXRot());

        netHeadYawDeg = Mth.clamp(netHeadYawDeg, -90.0F, 90.0F);
        headPitchDeg = Mth.clamp(headPitchDeg, -45.0F, 45.0F);

        s.headYawRad = netHeadYawDeg * Mth.DEG_TO_RAD;
        s.headPitchRad = headPitchDeg * Mth.DEG_TO_RAD;

        s.limbSwing = entity.walkAnimation.position(partialTick);
        s.limbSwingAmount = entity.walkAnimation.speed();
        s.ageTicks = entity.tickCount + partialTick;
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull BananaCowModel.State s) {
        return TEXTURE;
    }
    //?} else {
    /*@Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull BananaCow entity) {
        return TEXTURE;
    }
    *///?}
}