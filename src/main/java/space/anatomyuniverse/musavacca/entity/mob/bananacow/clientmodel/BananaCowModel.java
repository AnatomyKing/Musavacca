package space.anatomyuniverse.musavacca.entity.mob.bananacow.clientmodel;

//? if <1.21.2 {
/*import net.minecraft.client.model.HierarchicalModel;
 *///?} else {
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
//?}

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import space.anatomyuniverse.musavacca.MusaCore;
import space.anatomyuniverse.musavacca.entity.mob.bananacow.BananaCow;

//? if <1.21.2 {
/*public class BananaCowModel
        extends HierarchicalModel<BananaCow> {
 *///?} else {
public class BananaCowModel
        extends EntityModel<BananaCowModel.State> {
//?}

    private static final float EAT_HEAD_DROP = 4.0F;

    //? if >=1.21.2 {
    public static class State extends LivingEntityRenderState {
        public float headYawRad;
        public float headPitchRad;

        public float limbSwing;
        public float limbSwingAmount;
        public float ageTicks;

        public float headEatPositionScale;
        public float headEatAngleRad;

        public int peelStage;
        public int eatenBites;
    }
    //?}

    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(
                    ResourceLocation.fromNamespaceAndPath(
                            MusaCore.MOD_ID,
                            "banana_cow"
                    ),
                    "main"
            );

    //? if <1.21.2 {
    /*private final ModelPart root;
     *///?}

    private final ModelPart hHead;
    private final ModelPart body;

    private final ModelPart bananaPeelFlaps;
    private final ModelPart flapRight;
    private final ModelPart flapFront;
    private final ModelPart flapBack;
    private final ModelPart flapLeft;

    private final ModelPart tail;

    private final ModelPart rightFrontLeg;
    private final ModelPart leftFrontLeg;
    private final ModelPart rightBackLeg;
    private final ModelPart leftBackLeg;

    private final float headBaseXRot;
    private final float headBaseY;

    private final float tailBaseXRot;

    private final float flapRightBaseZRot;
    private final float flapLeftBaseZRot;
    private final float flapFrontBaseXRot;
    private final float flapBackBaseXRot;

    public BananaCowModel(ModelPart bakedRoot) {
        //? if >=1.21.2
        super(bakedRoot);

        //? if <1.21.2 {
        /*this.root = bakedRoot;
         *///?}

        ModelPart bananaCow =
                bakedRoot.getChild("bananacow");

        this.hHead =
                bananaCow.getChild("h_head");

        ModelPart torso =
                bananaCow.getChild("torso");

        this.body =
                torso.getChild("body");

        this.bananaPeelFlaps =
                this.body.getChild("banana_peel_flaps");

        this.flapRight =
                this.bananaPeelFlaps.getChild("cube_r1");

        this.flapFront =
                this.bananaPeelFlaps.getChild("cube_r2");

        this.flapBack =
                this.bananaPeelFlaps.getChild("cube_r3");

        this.flapLeft =
                this.bananaPeelFlaps.getChild("cube_r4");

        this.tail =
                torso.getChild("tail");

        this.rightBackLeg =
                bananaCow.getChild("right_back_leg");

        this.leftBackLeg =
                bananaCow.getChild("left_back_leg");

        this.rightFrontLeg =
                bananaCow.getChild("right_front_leg");

        this.leftFrontLeg =
                bananaCow.getChild("left_front_leg");

        this.headBaseXRot = this.hHead.xRot;
        this.headBaseY = this.hHead.y;

        this.tailBaseXRot = this.tail.xRot;

        this.flapRightBaseZRot =
                this.flapRight.zRot;

        this.flapLeftBaseZRot =
                this.flapLeft.zRot;

        this.flapFrontBaseXRot =
                this.flapFront.xRot;

        this.flapBackBaseXRot =
                this.flapBack.xRot;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshDefinition =
                new MeshDefinition();

        PartDefinition partDefinition =
                meshDefinition.getRoot();

        PartDefinition bananaCow =
                partDefinition.addOrReplaceChild(
                        "bananacow",
                        CubeListBuilder.create(),
                        PartPose.offset(
                                0.0F,
                                25.0F,
                                -1.0F
                        )
                );

        bananaCow.addOrReplaceChild(
                "h_head",
                CubeListBuilder.create()
                        .texOffs(39, 31)
                        .addBox(
                                -4.0F,
                                -4.6159F,
                                -7.0936F,
                                8.0F,
                                8.0F,
                                9.0F,
                                new CubeDeformation(0.0F)
                        )
                        .texOffs(5, 57)
                        .addBox(
                                -3.0F,
                                0.3841F,
                                -8.0936F,
                                6.0F,
                                3.0F,
                                1.0F,
                                new CubeDeformation(0.0F)
                        )
                        .texOffs(0, 57)
                        .addBox(
                                4.0F,
                                -4.6159F,
                                -5.0936F,
                                1.0F,
                                3.0F,
                                1.0F,
                                new CubeDeformation(0.0F)
                        )
                        .texOffs(0, 57)
                        .mirror()
                        .addBox(
                                -5.0F,
                                -4.6159F,
                                -5.0936F,
                                1.0F,
                                3.0F,
                                1.0F,
                                new CubeDeformation(0.0F)
                        )
                        .mirror(false),
                PartPose.offsetAndRotation(
                        0.0F,
                        -13.5183F,
                        -8.3348F,
                        -0.3927F,
                        0.0F,
                        0.0F
                )
        );

        PartDefinition torso =
                bananaCow.addOrReplaceChild(
                        "torso",
                        CubeListBuilder.create(),
                        PartPose.offsetAndRotation(
                                -1.0F,
                                -19.0F,
                                2.0F,
                                1.5708F,
                                0.0F,
                                0.0F
                        )
                );

        PartDefinition body =
                torso.addOrReplaceChild(
                        "body",
                        CubeListBuilder.create()
                                .texOffs(0, 0)
                                .addBox(
                                        -5.5F,
                                        -9.0F,
                                        -5.0F,
                                        11.0F,
                                        19.0F,
                                        11.0F,
                                        new CubeDeformation(0.0F)
                                )
                                .texOffs(20, 57)
                                .addBox(
                                        -1.25F,
                                        3.0F,
                                        -6.0F,
                                        2.5F,
                                        7.0F,
                                        1.0F,
                                        new CubeDeformation(0.0F)
                                ),
                        PartPose.offset(
                                1.0F,
                                -1.0F,
                                -6.0F
                        )
                );

        PartDefinition bananaPeelFlaps =
                body.addOrReplaceChild(
                        "banana_peel_flaps",
                        CubeListBuilder.create(),
                        PartPose.offset(
                                -5.499F,
                                1.0F,
                                0.5F
                        )
                );

        bananaPeelFlaps.addOrReplaceChild(
                "cube_r1",
                CubeListBuilder.create()
                        .texOffs(1, 65)
                        .addBox(
                                0.0F,
                                0.0F,
                                -5.5F,
                                0.0F,
                                9.0F,
                                11.0F,
                                new CubeDeformation(0.003F)
                        ),
                PartPose.offsetAndRotation(
                        10.999F,
                        0.0F,
                        0.0F,
                        0.0F,
                        0.0F,
                        -0.3927F
                )
        );

        bananaPeelFlaps.addOrReplaceChild(
                "cube_r2",
                CubeListBuilder.create()
                        .texOffs(1, 86)
                        .addBox(
                                -5.5F,
                                0.0F,
                                0.0F,
                                11.0F,
                                3.0F,
                                0.0F,
                                new CubeDeformation(0.003F)
                        ),
                PartPose.offsetAndRotation(
                        5.499F,
                        0.0F,
                        -5.5F,
                        -1.1781F,
                        0.0F,
                        0.0F
                )
        );

        bananaPeelFlaps.addOrReplaceChild(
                "cube_r3",
                CubeListBuilder.create()
                        .texOffs(1, 66)
                        .addBox(
                                -5.5F,
                                0.0F,
                                0.0F,
                                11.0F,
                                9.0F,
                                0.0F,
                                new CubeDeformation(0.003F)
                        ),
                PartPose.offsetAndRotation(
                        5.499F,
                        0.0F,
                        5.499F,
                        0.7854F,
                        0.0F,
                        0.0F
                )
        );

        bananaPeelFlaps.addOrReplaceChild(
                "cube_r4",
                CubeListBuilder.create()
                        .texOffs(1, 79)
                        .addBox(
                                0.0F,
                                0.0F,
                                -5.5F,
                                0.0F,
                                9.0F,
                                11.0F,
                                new CubeDeformation(0.003F)
                        ),
                PartPose.offsetAndRotation(
                        0.0F,
                        0.0F,
                        0.0F,
                        0.0F,
                        0.0F,
                        0.3927F
                )
        );

        PartDefinition tail =
                torso.addOrReplaceChild(
                        "tail",
                        CubeListBuilder.create()
                                .texOffs(0, 31)
                                .addBox(
                                        -4.5F,
                                        -1.0965F,
                                        -4.75F,
                                        9.0F,
                                        15.0F,
                                        10.0F,
                                        new CubeDeformation(0.0F)
                                ),
                        PartPose.offsetAndRotation(
                                1.0F,
                                7.25F,
                                -5.75F,
                                0.3927F,
                                0.0F,
                                0.0F
                        )
                );

        PartDefinition tip =
                tail.addOrReplaceChild(
                        "tip",
                        CubeListBuilder.create(),
                        PartPose.offsetAndRotation(
                                -2.0F,
                                14.2535F,
                                0.5186F,
                                0.6981F,
                                0.0F,
                                0.0F
                        )
                );

        tip.addOrReplaceChild(
                "cube_r5",
                CubeListBuilder.create()
                        .texOffs(45, 0)
                        .addBox(
                                -4.0F,
                                -2.3457F,
                                -3.7758F,
                                7.0F,
                                7.0F,
                                8.0F,
                                new CubeDeformation(0.0F)
                        )
                        .texOffs(78, 0)
                        .addBox(
                                -4.0F,
                                4.6543F,
                                -3.7758F,
                                7.0F,
                                5.0F,
                                12.0F,
                                new CubeDeformation(0.0F)
                        ),
                PartPose.offsetAndRotation(
                        2.5F,
                        -0.0671F,
                        -0.4252F,
                        -0.3927F,
                        0.0F,
                        0.0F
                )
        );

        bananaCow.addOrReplaceChild(
                "right_back_leg",
                CubeListBuilder.create()
                        .texOffs(58, 49)
                        .mirror()
                        .addBox(
                                -2.248F,
                                -1.75F,
                                -2.0F,
                                4.0F,
                                8.0F,
                                5.0F,
                                new CubeDeformation(0.0F)
                        )
                        .mirror(false),
                PartPose.offset(
                        -3.0F,
                        -7.25F,
                        7.0F
                )
        );

        bananaCow.addOrReplaceChild(
                "left_back_leg",
                CubeListBuilder.create()
                        .texOffs(58, 49)
                        .addBox(
                                -1.75F,
                                -1.5F,
                                -2.0F,
                                4.0F,
                                8.0F,
                                5.0F,
                                new CubeDeformation(0.0F)
                        ),
                PartPose.offset(
                        2.998F,
                        -7.5F,
                        7.0F
                )
        );

        bananaCow.addOrReplaceChild(
                "right_front_leg",
                CubeListBuilder.create()
                        .texOffs(39, 49)
                        .mirror()
                        .addBox(
                                -2.25F,
                                -1.5F,
                                -2.0F,
                                4.0F,
                                8.0F,
                                5.0F,
                                new CubeDeformation(0.0F)
                        )
                        .mirror(false),
                PartPose.offset(
                        -2.998F,
                        -7.5F,
                        -5.0F
                )
        );

        bananaCow.addOrReplaceChild(
                "left_front_leg",
                CubeListBuilder.create()
                        .texOffs(39, 49)
                        .addBox(
                                -1.502F,
                                -1.5F,
                                -2.0F,
                                4.0F,
                                8.0F,
                                5.0F,
                                new CubeDeformation(0.0F)
                        ),
                PartPose.offset(
                        2.75F,
                        -7.5F,
                        -5.0F
                )
        );

        return LayerDefinition.create(
                meshDefinition,
                128,
                128
        );
    }

    private void animateFlaps(
            float limbSwing,
            float limbSwingAmount,
            float ageInTicks
    ) {
        float walkAmount = Mth.clamp(
                limbSwingAmount,
                0.0F,
                1.0F
        );

        float walkFlap =
                Mth.sin(limbSwing * 0.6662F)
                        * 0.10F
                        * walkAmount;

        float oppositeWalkFlap =
                Mth.sin(
                        limbSwing * 0.6662F
                                + Mth.PI
                )
                        * 0.10F
                        * walkAmount;

        float idleFlap =
                Mth.sin(ageInTicks * 0.12F)
                        * 0.015F;

        this.flapRight.zRot =
                this.flapRightBaseZRot
                        - walkFlap
                        - idleFlap;

        this.flapLeft.zRot =
                this.flapLeftBaseZRot
                        + walkFlap
                        + idleFlap;

        this.flapFront.xRot =
                this.flapFrontBaseXRot
                        - oppositeWalkFlap
                        - idleFlap;

        this.flapBack.xRot =
                this.flapBackBaseXRot
                        + walkFlap
                        + idleFlap;
    }

    private void animateEatingHead(
            float normalHeadXRot,
            float eatPositionScale,
            float eatHeadXRot
    ) {
        float eatProgress = Mth.clamp(
                eatPositionScale,
                0.0F,
                1.0F
        );

        this.hHead.y =
                this.headBaseY
                        + EAT_HEAD_DROP
                        * eatProgress;

        this.hHead.xRot = Mth.lerp(
                eatProgress,
                normalHeadXRot,
                eatHeadXRot
        );
    }

    //? if <1.21.2 {
    /*@Override
    public ModelPart root() {
        return this.root;
    }
    *///?}

    //? if <1.21.2 {
    /*@Override
    public void setupAnim(
            @NotNull BananaCow entity,
            float limbSwing,
            float limbSwingAmount,
            float ageInTicks,
            float netHeadYaw,
            float headPitch
    ) {
        float clampedYaw = Mth.clamp(
                netHeadYaw,
                -90.0F,
                90.0F
        );

        float clampedPitch = Mth.clamp(
                headPitch,
                -45.0F,
                45.0F
        );

        this.hHead.yRot =
                clampedYaw * Mth.DEG_TO_RAD;

        float normalHeadXRot =
                this.headBaseXRot
                        + clampedPitch
                        * Mth.DEG_TO_RAD;

        float partialTick = Mth.clamp(
                ageInTicks - entity.tickCount,
                0.0F,
                1.0F
        );

        this.animateEatingHead(
                normalHeadXRot,
                entity.getHeadEatPositionScale(
                        partialTick
                ),
                entity.getHeadEatAngleScale(
                        partialTick
                )
        );

        if (entity.isBaby()) {
            this.hHead.xScale = 1.40F;
            this.hHead.yScale = 1.40F;
            this.hHead.zScale = 1.40F;
        } else {
            this.hHead.xScale = 1.0F;
            this.hHead.yScale = 1.0F;
            this.hHead.zScale = 1.0F;
        }

        float walk = limbSwing;
        float amount = limbSwingAmount;

        this.rightFrontLeg.xRot =
                Mth.cos(walk * 0.6662F)
                        * 1.4F
                        * amount;

        this.leftBackLeg.xRot =
                Mth.cos(walk * 0.6662F)
                        * 1.4F
                        * amount;

        this.leftFrontLeg.xRot =
                Mth.cos(
                        walk * 0.6662F
                                + Mth.PI
                )
                        * 1.4F
                        * amount;

        this.rightBackLeg.xRot =
                Mth.cos(
                        walk * 0.6662F
                                + Mth.PI
                )
                        * 1.4F
                        * amount;

        this.tail.xRot =
                this.tailBaseXRot
                        + Mth.cos(
                                ageInTicks * 0.2F
                        )
                        * 0.05F;

        this.animateFlaps(
                walk,
                amount,
                ageInTicks
        );
    }
    *///?} else {
    @Override
    public void setupAnim(@NotNull State state) {
        this.hHead.yRot = state.headYawRad;

        float normalHeadXRot =
                this.headBaseXRot
                        + state.headPitchRad;

        this.animateEatingHead(
                normalHeadXRot,
                state.headEatPositionScale,
                state.headEatAngleRad
        );

        if (state.isBaby) {
            this.hHead.xScale = 1.40F;
            this.hHead.yScale = 1.40F;
            this.hHead.zScale = 1.40F;
        } else {
            this.hHead.xScale = 1.0F;
            this.hHead.yScale = 1.0F;
            this.hHead.zScale = 1.0F;
        }

        float walk = state.limbSwing;
        float amount = state.limbSwingAmount;

        this.rightFrontLeg.xRot =
                Mth.cos(walk * 0.6662F)
                        * 1.4F
                        * amount;

        this.leftBackLeg.xRot =
                Mth.cos(walk * 0.6662F)
                        * 1.4F
                        * amount;

        this.leftFrontLeg.xRot =
                Mth.cos(
                        walk * 0.6662F
                                + Mth.PI
                )
                        * 1.4F
                        * amount;

        this.rightBackLeg.xRot =
                Mth.cos(
                        walk * 0.6662F
                                + Mth.PI
                )
                        * 1.4F
                        * amount;

        this.tail.xRot =
                this.tailBaseXRot
                        + Mth.cos(
                        state.ageTicks * 0.2F
                )
                        * 0.05F;

        this.animateFlaps(
                walk,
                amount,
                state.ageTicks
        );
    }
    //?}
}



