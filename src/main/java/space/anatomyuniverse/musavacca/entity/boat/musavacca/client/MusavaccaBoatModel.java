package space.anatomyuniverse.musavacca.entity.boat.musavacca.client;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.BoatRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import space.anatomyuniverse.musavacca.MusaCore;

public final class MusavaccaBoatModel
        extends EntityModel<BoatRenderState> {

    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(
                    ResourceLocation.fromNamespaceAndPath(
                            MusaCore.MOD_ID,
                            "musavacca_boat"
                    ),
                    "main"
            );

    private static final float ROOT_Y = 6.0F;

    private static final float MODEL_YAW =
            -Mth.HALF_PI;

    private static final float PADDLE_BASE_X =
            2.1368F;

    private static final float PADDLE_BASE_Y =
            0.8362F;

    private static final float PADDLE_BASE_Z =
            2.8434F;

    public static float PADDLE_YAW_AMPLITUDE =
            0.40F;

    public static float PADDLE_PITCH_AMPLITUDE =
            0.55F;

    public static float PADDLE_PITCH_PHASE_OFFSET =
            1.20F;

    private static final float ACTIVE_EPSILON =
            1.0E-6F;

    private final ModelPart paddleLeft;
    private final ModelPart paddleRight;

    public MusavaccaBoatModel(ModelPart bakedRoot) {
        super(bakedRoot);

        ModelPart root =
                bakedRoot.getChild("root");

        this.paddleLeft =
                root.getChild("paddle_left");

        this.paddleRight =
                root.getChild("paddle_right");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh =
                new MeshDefinition();

        PartDefinition meshRoot =
                mesh.getRoot();

        PartDefinition root =
                meshRoot.addOrReplaceChild(
                        "root",
                        CubeListBuilder.create(),
                        PartPose.offsetAndRotation(
                                0.0F,
                                ROOT_Y,
                                0.0F,
                                0.0F,
                                MODEL_YAW,
                                0.0F
                        )
                );

        root.addOrReplaceChild(
                "bottom",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(
                                -8.0F,
                                0.0F,
                                -22.0F,
                                16.0F,
                                14.0F,
                                44.0F
                        ),
                PartPose.offset(
                        0.0F,
                        -14.0F,
                        0.0F
                )
        );

        root.addOrReplaceChild(
                "right",
                CubeListBuilder.create()
                        .texOffs(11, 75)
                        .mirror()
                        .addBox(
                                -3.5F,
                                -3.5F,
                                -22.0F,
                                7.0F,
                                7.0F,
                                44.0F
                        )
                        .mirror(false),
                PartPose.offset(
                        -11.5F,
                        -3.5F,
                        0.0F
                )
        );

        root.addOrReplaceChild(
                "left",
                CubeListBuilder.create()
                        .texOffs(11, 75)
                        .addBox(
                                -3.5F,
                                -3.5F,
                                -22.0F,
                                7.0F,
                                7.0F,
                                44.0F
                        ),
                PartPose.offset(
                        11.5F,
                        -3.5F,
                        0.0F
                )
        );

        PartDefinition front =
                root.addOrReplaceChild(
                        "front",
                        CubeListBuilder.create(),
                        PartPose.offset(
                                0.0F,
                                -17.0352F,
                                -34.0538F
                        )
                );

        front.addOrReplaceChild(
                "front_r1",
                CubeListBuilder.create()
                        .texOffs(0, 88)
                        .addBox(
                                -4.0F,
                                -9.5F,
                                -6.5F,
                                12.0F,
                                17.0F,
                                13.0F
                        ),
                PartPose.offsetAndRotation(
                        -2.0F,
                        8.1599F,
                        7.6122F,
                        1.1781F,
                        0.0F,
                        0.0F
                )
        );

        front.addOrReplaceChild(
                "front_r2",
                CubeListBuilder.create()
                        .texOffs(82, 101)
                        .addBox(
                                -3.0F,
                                -3.0F,
                                -0.5F,
                                10.0F,
                                7.0F,
                                7.0F
                        ),
                PartPose.offsetAndRotation(
                        -2.0F,
                        -7.4565F,
                        -4.1989F,
                        0.7854F,
                        0.0F,
                        0.0F
                )
        );

        front.addOrReplaceChild(
                "front_r3",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(
                                -5.0F,
                                -10.0F,
                                -7.0F,
                                10.0F,
                                16.0F,
                                11.0F
                        ),
                PartPose.offsetAndRotation(
                        0.0F,
                        0.6752F,
                        -2.4311F,
                        0.7854F,
                        0.0F,
                        0.0F
                )
        );

        PartDefinition back =
                root.addOrReplaceChild(
                        "back",
                        CubeListBuilder.create(),
                        PartPose.offset(
                                0.0F,
                                -9.1327F,
                                27.4239F
                        )
                );

        back.addOrReplaceChild(
                "back_r1",
                CubeListBuilder.create()
                        .texOffs(0, 58)
                        .addBox(
                                -4.0F,
                                -9.5F,
                                -6.5F,
                                12.0F,
                                17.0F,
                                13.0F
                        ),
                PartPose.offsetAndRotation(
                        -2.0F,
                        0.2573F,
                        -0.9822F,
                        -1.1781F,
                        0.0F,
                        0.0F
                )
        );

        root.addOrReplaceChild(
                "paddle_left",
                CubeListBuilder.create()
                        .texOffs(77, 70)
                        .addBox(
                                -1.0F,
                                0.0F,
                                -10.0F,
                                2.0F,
                                2.0F,
                                23.0F
                        )
                        .texOffs(82, 75)
                        .addBox(
                                -1.001F,
                                -3.0F,
                                8.0F,
                                1.0F,
                                6.0F,
                                7.0F
                        ),
                PartPose.offsetAndRotation(
                        12.25F,
                        -13.0F,
                        -8.0F,
                        PADDLE_BASE_X,
                        PADDLE_BASE_Y,
                        PADDLE_BASE_Z
                )
        );

        root.addOrReplaceChild(
                "paddle_right",
                CubeListBuilder.create()
                        .texOffs(77, 70)
                        .mirror()
                        .addBox(
                                -1.0F,
                                0.0F,
                                -10.0F,
                                2.0F,
                                2.0F,
                                23.0F
                        )
                        .mirror(false)
                        .texOffs(82, 75)
                        .mirror()
                        .addBox(
                                0.001F,
                                -3.0F,
                                8.0F,
                                1.0F,
                                6.0F,
                                7.0F
                        )
                        .mirror(false),
                PartPose.offsetAndRotation(
                        -12.25F,
                        -13.0F,
                        -8.0F,
                        PADDLE_BASE_X,
                        -PADDLE_BASE_Y,
                        -PADDLE_BASE_Z
                )
        );

        root.addOrReplaceChild(
                "seat_driver",
                CubeListBuilder.create(),
                PartPose.offset(
                        0.0F,
                        -14.0F,
                        0.0F
                )
        );

        root.addOrReplaceChild(
                "seat_passenger_1",
                CubeListBuilder.create(),
                PartPose.offset(
                        0.0F,
                        -14.0F,
                        11.0F
                )
        );

        root.addOrReplaceChild(
                "seat_passenger_2",
                CubeListBuilder.create(),
                PartPose.offset(
                        0.0F,
                        -14.0F,
                        -11.0F
                )
        );

        return LayerDefinition.create(
                mesh,
                128,
                128
        );
    }

    @Override
    public void setupAnim(
            @NotNull BoatRenderState state
    ) {
        super.setupAnim(state);

        animatePaddle(
                this.paddleLeft,
                true,
                state.rowingTimeLeft
        );

        animatePaddle(
                this.paddleRight,
                false,
                state.rowingTimeRight
        );
    }

    private static void animatePaddle(
            ModelPart paddle,
            boolean left,
            float rowingTime
    ) {
        float side =
                left ? 1.0F : -1.0F;

        paddle.xRot =
                PADDLE_BASE_X;

        paddle.yRot =
                side * PADDLE_BASE_Y;

        paddle.zRot =
                side * PADDLE_BASE_Z;

        if (Math.abs(rowingTime)
                <= ACTIVE_EPSILON) {
            return;
        }

        paddle.xRot +=
                PADDLE_PITCH_AMPLITUDE
                        * Mth.sin(
                        rowingTime
                                + PADDLE_PITCH_PHASE_OFFSET
                );

        paddle.yRot +=
                PADDLE_YAW_AMPLITUDE
                        * Mth.sin(rowingTime)
                        * side;
    }
}