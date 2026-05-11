// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/entity/mob/basuke/clientmodel/BasukeModel.java
package space.anatomyuniverse.musavacca.entity.mob.basuke.clientmodel;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.AllayRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import org.jetbrains.annotations.NotNull;
import space.anatomyuniverse.musavacca.MusaCore;
import space.anatomyuniverse.musavacca.entity.mob.basuke.Basuke;

public class BasukeModel extends EntityModel<BasukeModel.State> implements ArmedModel {

    public static class State extends AllayRenderState {
        public float headYawRad;
        public float headPitchRad;
        public float limbSwing;
        public float ageTicks;
        public float flyAmount;
        public int eatingTicks;
    }

    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(
                    ResourceLocation.fromNamespaceAndPath(MusaCore.MOD_ID, "basuke"),
                    "main"
            );

    private static final float HELD_ITEM_OFFSET_X = 0.047F;
    private static final float HELD_ITEM_OFFSET_Y = -0.24F;
    private static final float HELD_ITEM_OFFSET_Z = 0.43F;
    private static final float HELD_ITEM_SCALE = 0.7F;

    private static final float EATING_ARM_BOB_STRENGTH = 0.145F;

    private final ModelPart basuke;
    private final ModelPart hHead;
    private final ModelPart leftWing;
    private final ModelPart rightWing;
    private final ModelPart leftArm;
    private final ModelPart rightArm;
    private final ModelPart itemAnchor;

    private final float headBaseXRot;
    private final float headBaseYRot;
    private final float headBaseZRot;

    private final float leftWingBaseXRot;
    private final float leftWingBaseYRot;
    private final float leftWingBaseZRot;

    private final float rightWingBaseXRot;
    private final float rightWingBaseYRot;
    private final float rightWingBaseZRot;

    private final float leftArmBaseXRot;
    private final float leftArmBaseYRot;
    private final float leftArmBaseZRot;

    private final float rightArmBaseXRot;
    private final float rightArmBaseYRot;
    private final float rightArmBaseZRot;

    private final float leftArmBaseY;
    private final float rightArmBaseY;

    public BasukeModel(ModelPart bakedRoot) {
        super(bakedRoot);

        this.basuke = bakedRoot.getChild("basuke");
        this.hHead = this.basuke.getChild("h_head");
        this.leftWing = this.hHead.getChild("left_wing");
        this.rightWing = this.hHead.getChild("right_wing");
        this.leftArm = this.hHead.getChild("left_arm");
        this.rightArm = this.hHead.getChild("right_arm");
        this.itemAnchor = this.rightArm.getChild("item");

        this.headBaseXRot = this.hHead.xRot;
        this.headBaseYRot = this.hHead.yRot;
        this.headBaseZRot = this.hHead.zRot;

        this.leftWingBaseXRot = this.leftWing.xRot;
        this.leftWingBaseYRot = this.leftWing.yRot;
        this.leftWingBaseZRot = this.leftWing.zRot;

        this.rightWingBaseXRot = this.rightWing.xRot;
        this.rightWingBaseYRot = this.rightWing.yRot;
        this.rightWingBaseZRot = this.rightWing.zRot;

        this.leftArmBaseXRot = this.leftArm.xRot;
        this.leftArmBaseYRot = this.leftArm.yRot;
        this.leftArmBaseZRot = this.leftArm.zRot;

        this.rightArmBaseXRot = this.rightArm.xRot;
        this.rightArmBaseYRot = this.rightArm.yRot;
        this.rightArmBaseZRot = this.rightArm.zRot;

        this.leftArmBaseY = this.leftArm.y;
        this.rightArmBaseY = this.rightArm.y;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition basuke = partdefinition.addOrReplaceChild(
                "basuke",
                CubeListBuilder.create(),
                PartPose.offset(-0.0007F, 16.3706F, 0.0736F)
        );

        PartDefinition hHead = basuke.addOrReplaceChild(
                "h_head",
                CubeListBuilder.create()
                        .texOffs(0, 14).addBox(-2.5F, -2.5F, -1.5F, 5.0F, 5.0F, 5.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 0).addBox(-2.5F, -6.5F, -5.5F, 5.0F, 4.0F, 9.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0007F, 0.1294F, -1.0736F)
        );

        PartDefinition leftWing = hHead.addOrReplaceChild(
                "left_wing",
                CubeListBuilder.create(),
                PartPose.offset(0.7F, -3.5F, 3.5F)
        );

        leftWing.addOrReplaceChild(
                "cube_r1",
                CubeListBuilder.create()
                        .texOffs(1, 19).addBox(0.0F, -2.5F, -3.0F, 0.0F, 5.0F, 6.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, 2.6F, 1.9F, 0.7854F, 0.0F, 0.0F)
        );

        PartDefinition rightWing = hHead.addOrReplaceChild(
                "right_wing",
                CubeListBuilder.create(),
                PartPose.offset(-0.8F, -3.5F, 3.5F)
        );

        rightWing.addOrReplaceChild(
                "cube_r2",
                CubeListBuilder.create()
                        .texOffs(1, 19).addBox(0.0F, -2.5F, -3.0F, 0.0F, 5.0F, 6.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, 2.6F, 1.9F, 0.7854F, 0.0F, 0.0F)
        );

        hHead.addOrReplaceChild(
                "left_arm",
                CubeListBuilder.create()
                        .texOffs(21, 14).addBox(4.75F, 0.5F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offset(-2.75F, 0.5F, 1.0F)
        );

        PartDefinition rightArm = hHead.addOrReplaceChild(
                "right_arm",
                CubeListBuilder.create()
                        .texOffs(21, 14).mirror().addBox(-6.75F, 0.5F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false),
                PartPose.offset(2.75F, 0.5F, 1.0F)
        );

        rightArm.addOrReplaceChild(
                "item",
                CubeListBuilder.create(),
                PartPose.offset(-2.75F, 3.0F, -4.0F)
        );

        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    private void applyAnimation(
            float limbSwing,
            float ageTicks,
            float headYawRad,
            float headPitchRad,
            float flyAmount,
            float holdingAnimationProgress,
            int eatingTicks
    ) {
        this.hHead.yRot = this.headBaseYRot + headYawRad;
        this.hHead.xRot = this.headBaseXRot + headPitchRad;
        this.hHead.zRot = this.headBaseZRot + (Mth.sin(ageTicks * 0.08F) * 0.02F);

        float wingTime = ageTicks * 20.0F * Mth.DEG_TO_RAD + limbSwing * 0.15F;
        float wingSwing = 0.35F + Mth.cos(wingTime) * Mth.PI * 0.08F;

        this.leftWing.xRot = this.leftWingBaseXRot;
        this.leftWing.yRot = this.leftWingBaseYRot + wingSwing;
        this.leftWing.zRot = this.leftWingBaseZRot;

        this.rightWing.xRot = this.rightWingBaseXRot;
        this.rightWing.yRot = this.rightWingBaseYRot - wingSwing;
        this.rightWing.zRot = this.rightWingBaseZRot;

        float fly = Mth.clamp(flyAmount, 0.0F, 1.0F);
        float smoothFly = fly * fly * (3.0F - 2.0F * fly);
        float hold = Mth.clamp(holdingAnimationProgress, 0.0F, 1.0F);

        float armPitchBase = Mth.lerp(smoothFly, -0.30F, 0.12F);
        float armPitchLife = Mth.sin(ageTicks * 0.14F) * 0.045F;
        float armSpread = Mth.lerp(smoothFly, 0.15F, 0.08F) + Mth.cos(ageTicks * 0.12F) * 0.02F;

        float armLiftCycle = (Mth.sin(ageTicks * 0.18F) * 0.5F + 0.5F);
        float armDrop = armLiftCycle * 0.55F + smoothFly * 0.08F;

        float holdForward = hold * 0.85F;
        float holdMirrorTuck = hold * 0.12F;
        float holdLower = hold * 0.05F;

        float sharedHoldXRot = armPitchBase - holdForward;
        float sharedHoldY = armDrop + holdLower;

        float idleLeftXRot = this.leftArmBaseXRot + armPitchBase + armPitchLife;
        float idleRightXRot = this.rightArmBaseXRot + armPitchBase - armPitchLife;

        float holdLeftXRot = this.leftArmBaseXRot + sharedHoldXRot;
        float holdRightXRot = this.rightArmBaseXRot + sharedHoldXRot;

        this.leftArm.xRot = Mth.lerp(hold, idleLeftXRot, holdLeftXRot);
        this.rightArm.xRot = Mth.lerp(hold, idleRightXRot, holdRightXRot);

        this.leftArm.yRot = this.leftArmBaseYRot;
        this.rightArm.yRot = this.rightArmBaseYRot;

        this.leftArm.zRot = this.leftArmBaseZRot - armSpread + holdMirrorTuck;
        this.rightArm.zRot = this.rightArmBaseZRot + armSpread - holdMirrorTuck;

        this.leftArm.y = this.leftArmBaseY + Mth.lerp(hold, armDrop, sharedHoldY);
        this.rightArm.y = this.rightArmBaseY + Mth.lerp(hold, armDrop, sharedHoldY);

        this.applyBeatSyncedArmEatingAnimation(ageTicks, hold, eatingTicks);
    }

    private void applyBeatSyncedArmEatingAnimation(float ageTicks, float holdingAnimationProgress, int eatingTicks) {
        if (eatingTicks <= 0) {
            return;
        }

        float hold = Mth.clamp(holdingAnimationProgress, 0.0F, 1.0F);
        if (hold <= 0.0F) {
            return;
        }

        float partialTick = ageTicks - Mth.floor(ageTicks);
        float elapsedEatingTicks = (Basuke.EATING_CYCLE_TICKS - eatingTicks) + partialTick;

        float fadeIn = Mth.clamp(elapsedEatingTicks / 4.0F, 0.0F, 1.0F);
        float fadeOut = Mth.clamp(eatingTicks / 4.0F, 0.0F, 1.0F);
        float eat = hold * fadeIn * fadeOut;

        if (eat <= 0.0F) {
            return;
        }

        float beatPhase = (elapsedEatingTicks % Basuke.EATING_CHEW_BEAT_TICKS) / (float) Basuke.EATING_CHEW_BEAT_TICKS;
        float armYOffset = Mth.sin(beatPhase * Mth.TWO_PI) * EATING_ARM_BOB_STRENGTH * eat;

        this.leftArm.y += armYOffset;
        this.rightArm.y += armYOffset;
    }

    @Override
    public void setupAnim(@NotNull State s) {
        this.applyAnimation(
                s.limbSwing,
                s.ageTicks,
                s.headYawRad,
                s.headPitchRad,
                s.flyAmount,
                s.holdingAnimationProgress,
                s.eatingTicks
        );
    }

    @Override
    public void translateToHand(HumanoidArm arm, PoseStack poseStack) {
        this.basuke.translateAndRotate(poseStack);
        this.hHead.translateAndRotate(poseStack);

        if (arm == HumanoidArm.RIGHT) {
            this.rightArm.translateAndRotate(poseStack);
            this.itemAnchor.translateAndRotate(poseStack);
        } else {
            this.leftArm.translateAndRotate(poseStack);
            poseStack.translate(0.0F, 0.1875F, -0.25F);
        }

        poseStack.translate(
                HELD_ITEM_OFFSET_X,
                HELD_ITEM_OFFSET_Y,
                HELD_ITEM_OFFSET_Z
        );

        poseStack.scale(
                HELD_ITEM_SCALE,
                HELD_ITEM_SCALE,
                HELD_ITEM_SCALE
        );
    }
}