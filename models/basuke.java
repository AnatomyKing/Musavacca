// Made with Blockbench 5.1.3
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


public class basuke<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("modid", "basuke"), "main");
	private final ModelPart basuke;
	private final ModelPart h_head;
	private final ModelPart left_wing;
	private final ModelPart right_wing;
	private final ModelPart left_arm;
	private final ModelPart right_arm;

	public basuke(ModelPart root) {
		this.basuke = root.getChild("basuke");
		this.h_head = this.basuke.getChild("h_head");
		this.left_wing = this.h_head.getChild("left_wing");
		this.right_wing = this.h_head.getChild("right_wing");
		this.left_arm = this.h_head.getChild("left_arm");
		this.right_arm = this.h_head.getChild("right_arm");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition basuke = partdefinition.addOrReplaceChild("basuke", CubeListBuilder.create(), PartPose.offset(-0.0007F, 16.3706F, 0.0736F));

		PartDefinition h_head = basuke.addOrReplaceChild("h_head", CubeListBuilder.create().texOffs(0, 14).addBox(-2.5F, -2.5F, -1.5F, 5.0F, 5.0F, 5.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-2.5F, -6.5F, -5.5F, 5.0F, 4.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0007F, 0.1294F, -1.0736F));

		PartDefinition left_wing = h_head.addOrReplaceChild("left_wing", CubeListBuilder.create(), PartPose.offset(0.7F, -3.5F, 3.5F));

		PartDefinition cube_r1 = left_wing.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(1, 19).addBox(0.0F, -2.5F, -3.0F, 0.0F, 5.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 2.6F, 1.9F, 0.7854F, 0.0F, 0.0F));

		PartDefinition right_wing = h_head.addOrReplaceChild("right_wing", CubeListBuilder.create(), PartPose.offset(-0.8F, -3.5F, 3.5F));

		PartDefinition cube_r2 = right_wing.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(1, 19).addBox(0.0F, -2.5F, -3.0F, 0.0F, 5.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 2.6F, 1.9F, 0.7854F, 0.0F, 0.0F));

		PartDefinition left_arm = h_head.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(21, 14).addBox(4.75F, 0.5F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.75F, 0.5F, 1.0F));

		PartDefinition right_arm = h_head.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(21, 14).mirror().addBox(-6.75F, 0.5F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(2.75F, 0.5F, 1.0F));

		return LayerDefinition.create(meshdefinition, 32, 32);
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		basuke.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}