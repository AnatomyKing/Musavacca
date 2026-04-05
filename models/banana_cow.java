// Made with Blockbench 5.1.1
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


public class banana_cow - Converted<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("modid", "banana_cow_- converted"), "main");
	private final ModelPart bananacow;
	private final ModelPart h_head;
	private final ModelPart torso;
	private final ModelPart body;
	private final ModelPart tail;
	private final ModelPart tip;
	private final ModelPart right_back_leg;
	private final ModelPart left_back_leg;
	private final ModelPart right_front_leg;
	private final ModelPart left_front_leg;

	public banana_cow - Converted(ModelPart root) {
		this.bananacow = root.getChild("bananacow");
		this.h_head = this.bananacow.getChild("h_head");
		this.torso = this.bananacow.getChild("torso");
		this.body = this.torso.getChild("body");
		this.tail = this.torso.getChild("tail");
		this.tip = this.tail.getChild("tip");
		this.right_back_leg = this.bananacow.getChild("right_back_leg");
		this.left_back_leg = this.bananacow.getChild("left_back_leg");
		this.right_front_leg = this.bananacow.getChild("right_front_leg");
		this.left_front_leg = this.bananacow.getChild("left_front_leg");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition bananacow = partdefinition.addOrReplaceChild("bananacow", CubeListBuilder.create(), PartPose.offset(0.0F, 25.0F, -1.0F));

		PartDefinition h_head = bananacow.addOrReplaceChild("h_head", CubeListBuilder.create().texOffs(39, 31).addBox(-4.0F, -4.6159F, -7.0936F, 8.0F, 8.0F, 9.0F, new CubeDeformation(0.0F))
		.texOffs(5, 57).addBox(-3.0F, 0.3841F, -8.0936F, 6.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 57).addBox(4.0F, -4.6159F, -5.0936F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 57).mirror().addBox(-5.0F, -4.6159F, -5.0936F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, -13.5183F, -8.3348F, -0.3927F, 0.0F, 0.0F));

		PartDefinition torso = bananacow.addOrReplaceChild("torso", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.0F, -19.0F, 2.0F, 1.5708F, 0.0F, 0.0F));

		PartDefinition body = torso.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-5.5F, -9.0F, -5.0F, 11.0F, 19.0F, 11.0F, new CubeDeformation(0.0F))
		.texOffs(20, 57).addBox(-1.25F, 3.0F, -6.0F, 2.5F, 7.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(1.0F, -1.0F, -6.0F));

		PartDefinition tail = torso.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(0, 31).addBox(-4.5F, -1.0965F, -4.75F, 9.0F, 15.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 7.25F, -5.75F, 0.3927F, 0.0F, 0.0F));

		PartDefinition tip = tail.addOrReplaceChild("tip", CubeListBuilder.create(), PartPose.offsetAndRotation(-2.0F, 14.2535F, 0.5186F, 0.6981F, 0.0F, 0.0F));

		PartDefinition cube_r1 = tip.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(45, 21).addBox(-4.0F, 4.6543F, -0.7758F, 7.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.5F, 1.8463F, 4.1942F, -0.3927F, 0.0F, 0.0F));

		PartDefinition cube_r2 = tip.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(45, 0).addBox(-4.0F, -2.3457F, -3.7758F, 7.0F, 12.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.5F, -0.0671F, -0.4252F, -0.3927F, 0.0F, 0.0F));

		PartDefinition right_back_leg = bananacow.addOrReplaceChild("right_back_leg", CubeListBuilder.create().texOffs(39, 49).mirror().addBox(-2.248F, -1.75F, -2.0F, 4.0F, 8.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-3.0F, -7.25F, 7.0F));

		PartDefinition left_back_leg = bananacow.addOrReplaceChild("left_back_leg", CubeListBuilder.create().texOffs(39, 49).addBox(-1.75F, -1.5F, -2.0F, 4.0F, 8.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(2.998F, -7.5F, 7.0F));

		PartDefinition right_front_leg = bananacow.addOrReplaceChild("right_front_leg", CubeListBuilder.create().texOffs(39, 49).mirror().addBox(-2.25F, -1.5F, -2.0F, 4.0F, 8.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-2.998F, -7.5F, -5.0F));

		PartDefinition left_front_leg = bananacow.addOrReplaceChild("left_front_leg", CubeListBuilder.create().texOffs(39, 49).addBox(-1.502F, -1.5F, -2.0F, 4.0F, 8.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(2.75F, -7.5F, -5.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		bananacow.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}