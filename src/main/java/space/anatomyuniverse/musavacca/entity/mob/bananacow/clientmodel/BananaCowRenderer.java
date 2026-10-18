package space.anatomyuniverse.musavacca.entity.mob.bananacow.clientmodel;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
//? if >=1.21.11 {
/*import net.minecraft.resources.Identifier;
*///?} else {
import net.minecraft.resources.ResourceLocation;
//?}
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import space.anatomyuniverse.musavacca.MusaCore;
import space.anatomyuniverse.musavacca.entity.mob.bananacow.BananaCow;

public final class BananaCowRenderer
        extends MobRenderer<
        //? if >=1.21.2 {
        BananaCow,
        BananaCowModel.State,
        BananaCowModel
        //?} else {
                /*BananaCow,
                BananaCowModel
        *///?}
        > {

    //? if >=1.21.11 {
    /*private static final Identifier DEFAULT =
    *///?} else {
    private static final ResourceLocation DEFAULT =
    //?}
            texture("banana_cow");

    //? if >=1.21.11 {
    /*private static final Identifier SHEARED =
    *///?} else {
    private static final ResourceLocation SHEARED =
    //?}
            texture("banana_cow_sheared");

    //? if >=1.21.11 {
    /*private static final Identifier SHEARED_EATEN_1 =
    *///?} else {
    private static final ResourceLocation SHEARED_EATEN_1 =
    //?}
            texture("banana_cow_sheared_eaten_1");

    //? if >=1.21.11 {
    /*private static final Identifier SHEARED_EATEN_2 =
    *///?} else {
    private static final ResourceLocation SHEARED_EATEN_2 =
    //?}
            texture("banana_cow_sheared_eaten_2");

    //? if >=1.21.11 {
    /*private static final Identifier PEELD =
    *///?} else {
    private static final ResourceLocation PEELD =
    //?}
            texture("banana_cow_peeld");

    //? if >=1.21.11 {
    /*private static final Identifier PEELD_EATEN_1 =
    *///?} else {
    private static final ResourceLocation PEELD_EATEN_1 =
    //?}
            texture("banana_cow_peeld_eaten_1");

    //? if >=1.21.11 {
    /*private static final Identifier PEELD_EATEN_2 =
    *///?} else {
    private static final ResourceLocation PEELD_EATEN_2 =
    //?}
            texture("banana_cow_peeld_eaten_2");

    private static final float SHADOW = 0.7F;

    public BananaCowRenderer(
            EntityRendererProvider.Context context
    ) {
        super(
                context,
                new BananaCowModel(
                        context.bakeLayer(
                                BananaCowModel.LAYER_LOCATION
                        )
                ),
                SHADOW
        );
    }

    //? if >=1.21.11 {
    /*private static Identifier texture(String name) {
        return Identifier.fromNamespaceAndPath(MusaCore.MOD_ID, "textures/entity/cow/"
    *///?} else {
    private static ResourceLocation texture(String name) {
        return ResourceLocation.fromNamespaceAndPath(MusaCore.MOD_ID, "textures/entity/cow/"
    //?}
                        + name
                        + ".png");
    }

    //? if >=1.21.11 {
    /*private static Identifier selectTexture(
    *///?} else {
    private static ResourceLocation selectTexture(
    //?}
            int peelStage,
            int eatenBites
    ) {
        int safePeelStage = clampInt(
                peelStage,
                BananaCow.PEEL_STAGE_DEFAULT,
                BananaCow.PEEL_STAGE_PEELD
        );

        int safeEatenBites = clampInt(
                eatenBites,
                0,
                BananaCow.MAX_VISIBLE_EATEN_BITES
        );

        if (safePeelStage
                == BananaCow.PEEL_STAGE_PEELD) {
            if (safeEatenBites >= 2) {
                return PEELD_EATEN_2;
            }

            if (safeEatenBites == 1) {
                return PEELD_EATEN_1;
            }

            return PEELD;
        }

        if (safePeelStage
                == BananaCow.PEEL_STAGE_SHEARED) {
            if (safeEatenBites >= 2) {
                return SHEARED_EATEN_2;
            }

            if (safeEatenBites == 1) {
                return SHEARED_EATEN_1;
            }

            return SHEARED;
        }

        return DEFAULT;
    }

    private static int clampInt(
            int value,
            int min,
            int max
    ) {
        return Math.max(
                min,
                Math.min(max, value)
        );
    }

    //? if >=1.21.11 {
    /*@Override
    public @NotNull BananaCowModel.State createRenderState() {
        return new BananaCowModel.State();
    }

    @Override
    public void extractRenderState(
            @NotNull BananaCow entity,
            @NotNull BananaCowModel.State state,
            float partialTick
    ) {
        super.extractRenderState(
                entity,
                state,
                partialTick
        );

        float bodyYaw = Mth.rotLerp(
                partialTick,
                entity.yBodyRotO,
                entity.yBodyRot
        );

        float headYaw = Mth.rotLerp(
                partialTick,
                entity.yHeadRotO,
                entity.yHeadRot
        );

        float netHeadYawDegrees =
                Mth.wrapDegrees(
                        headYaw - bodyYaw
                );

        float headPitchDegrees = Mth.lerp(
                partialTick,
                entity.xRotO,
                entity.getXRot()
        );

        netHeadYawDegrees = Mth.clamp(
                netHeadYawDegrees,
                -90.0F,
                90.0F
        );

        headPitchDegrees = Mth.clamp(
                headPitchDegrees,
                -45.0F,
                45.0F
        );

        state.headYawRad =
                netHeadYawDegrees
                        * Mth.DEG_TO_RAD;

        state.headPitchRad =
                headPitchDegrees
                        * Mth.DEG_TO_RAD;

        state.limbSwing =
                entity.walkAnimation.position(
                        partialTick
                );

        state.limbSwingAmount =
                entity.walkAnimation.speed();

        state.ageTicks =
                entity.tickCount + partialTick;

        state.headEatPositionScale =
                entity.getHeadEatPositionScale(
                        partialTick
                );

        state.headEatAngleRad =
                entity.getHeadEatAngleScale(
                        partialTick
                );

        state.peelStage =
                entity.getPeelStage();

        state.eatenBites =
                entity.getEatenBites();
    }

    @Override
    public @NotNull Identifier getTextureLocation(
            @NotNull BananaCowModel.State state
    *///?} else if >=1.21.2 <1.21.11 {
    @Override
    public @NotNull BananaCowModel.State createRenderState() {
        return new BananaCowModel.State();
    }

    @Override
    public void extractRenderState(
            @NotNull BananaCow entity,
            @NotNull BananaCowModel.State state,
            float partialTick
    ) {
        super.extractRenderState(
                entity,
                state,
                partialTick
        );

        float bodyYaw = Mth.rotLerp(
                partialTick,
                entity.yBodyRotO,
                entity.yBodyRot
        );

        float headYaw = Mth.rotLerp(
                partialTick,
                entity.yHeadRotO,
                entity.yHeadRot
        );

        float netHeadYawDegrees =
                Mth.wrapDegrees(
                        headYaw - bodyYaw
                );

        float headPitchDegrees = Mth.lerp(
                partialTick,
                entity.xRotO,
                entity.getXRot()
        );

        netHeadYawDegrees = Mth.clamp(
                netHeadYawDegrees,
                -90.0F,
                90.0F
        );

        headPitchDegrees = Mth.clamp(
                headPitchDegrees,
                -45.0F,
                45.0F
        );

        state.headYawRad =
                netHeadYawDegrees
                        * Mth.DEG_TO_RAD;

        state.headPitchRad =
                headPitchDegrees
                        * Mth.DEG_TO_RAD;

        state.limbSwing =
                entity.walkAnimation.position(
                        partialTick
                );

        state.limbSwingAmount =
                entity.walkAnimation.speed();

        state.ageTicks =
                entity.tickCount + partialTick;

        state.headEatPositionScale =
                entity.getHeadEatPositionScale(
                        partialTick
                );

        state.headEatAngleRad =
                entity.getHeadEatAngleScale(
                        partialTick
                );

        state.peelStage =
                entity.getPeelStage();

        state.eatenBites =
                entity.getEatenBites();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(
            @NotNull BananaCowModel.State state
    //?} else {
    /*@Override
    public @NotNull ResourceLocation getTextureLocation(
            @NotNull BananaCow entity
    *///?}
    ) {
        return selectTexture(
                //? if >=1.21.2 {
                state.peelStage,
                state.eatenBites
                //?} else {
                /*entity.getPeelStage(),
                entity.getEatenBites()
                *///?}
        );
    }

}
