package space.anatomyuniverse.musavacca.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.HumanoidModel;
//? if >=1.21.9 {
/*import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
*///?} else {
import net.minecraft.client.renderer.MultiBufferSource;
import org.spongepowered.asm.mixin.Unique;
//?}
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
//? if >=1.21.2 {
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
//?}
//? if >=1.21.4 {
import net.minecraft.client.resources.model.EquipmentClientInfo;
//?} else if >=1.21.2 {
/*import net.minecraft.world.item.equipment.EquipmentModel;
*///?}
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
//? if <1.21.2 {
/*import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import org.spongepowered.asm.mixin.gen.Invoker;
*///?}
//? if >=1.21.2 <1.21.9 {
import net.minecraft.world.item.equipment.trim.ArmorTrim;
//?} else if <1.21.2 {
/*import net.minecraft.world.item.armortrim.ArmorTrim;
*///?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import space.anatomyuniverse.musavacca.client.helmet.CustomHelmetModels;
import space.anatomyuniverse.musavacca.item.CustomHelmetArmorTrims;

@Mixin(HumanoidArmorLayer.class)
public abstract class HumanoidArmorLayerMixin {
    //? if >=1.21.2 {
    @Shadow
    @Final
    private EquipmentLayerRenderer equipmentRenderer;

    //? if >=1.21.9 {
    /*@Shadow
    protected abstract HumanoidModel<HumanoidRenderState> getArmorModel(HumanoidRenderState state, EquipmentSlot slot);

    @Inject(method = "renderArmorPiece", at = @At("HEAD"), cancellable = true)
    private void musavacca$submitCustomHelmet(PoseStack pose, SubmitNodeCollector collector, ItemStack helmet, EquipmentSlot slot, int light,
            HumanoidRenderState state, CallbackInfo callback) {
        if (slot != EquipmentSlot.HEAD || !CustomHelmetModels.hasCustomHeadModel(helmet)) return;
        HumanoidModel<HumanoidRenderState> model = getArmorModel(state, slot);
        model.setupAnim(state);
        pose.pushPose();
        model.head.translateAndRotate(pose);
        pose.translate(0.0F, -0.25F, 0.0F);
        //? if >=26.3 {
        /^pose.rotate(Axis.YP.rotationDegrees(180.0F));
        ^///?} else {
        pose.mulPose(Axis.YP.rotationDegrees(180.0F));
        //?}
        pose.scale(0.625F, -0.625F, -0.625F);
        CustomHelmetModels.render(helmet, pose, collector, light, state.outlineColor);
        pose.popPose();
        var trim = helmet.get(DataComponents.TRIM);
        if (trim != null) {
            ItemStack carrier = new ItemStack(Items.STICK);
            carrier.set(DataComponents.TRIM, trim);
            equipmentRenderer.renderLayers(EquipmentClientInfo.LayerType.HUMANOID, CustomHelmetArmorTrims.TRIM_CARRIER_ASSET, model, state, carrier, pose,
                    collector, light, state.outlineColor);
        }
        callback.cancel();
    }
    *///?}
    //? if >=1.21.2 <1.21.9 {
    @Unique
    private final ItemStack musavacca$trimCarrier = new ItemStack(Items.STICK);

    @Inject(method = "renderArmorPiece", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/layers/HumanoidArmorLayer;setPartVisibility(Lnet/minecraft/client/model/HumanoidModel;Lnet/minecraft/world/entity/EquipmentSlot;)V", shift = At.Shift.AFTER), cancellable = true)
    private void musavacca$renderCustomHelmet1212Plus(PoseStack poseStack, MultiBufferSource bufferSource, ItemStack helmet, EquipmentSlot slot,
            int packedLight, HumanoidModel<?> armorModel, CallbackInfo callbackInfo) {
        if (slot != EquipmentSlot.HEAD) return;
        musavacca$renderCustomHelmet(helmet, packedLight, armorModel, poseStack, bufferSource, callbackInfo);
    }
    //?}
    //?}
    //? if <1.21.9 {

    //? if <1.21.2 {
    /*@Inject(method = "renderArmorPiece(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/EquipmentSlot;ILnet/minecraft/client/model/HumanoidModel;FFFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/layers/HumanoidArmorLayer;setPartVisibility(Lnet/minecraft/client/model/HumanoidModel;Lnet/minecraft/world/entity/EquipmentSlot;)V", shift = At.Shift.AFTER), cancellable = true)
    private void musavacca$renderCustomHelmet1211(PoseStack poseStack, MultiBufferSource bufferSource, LivingEntity entity, EquipmentSlot slot, int packedLight,
            HumanoidModel<?> armorModel, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch,
            CallbackInfo callbackInfo) {
        if (slot != EquipmentSlot.HEAD) return;
        musavacca$renderCustomHelmet(entity.getItemBySlot(EquipmentSlot.HEAD), packedLight, armorModel, poseStack, bufferSource, callbackInfo);
    }

    *///?}
    @Unique
    private void musavacca$renderCustomHelmet(ItemStack helmet, int packedLight, HumanoidModel<?> armorModel, PoseStack poseStack,
            MultiBufferSource bufferSource, CallbackInfo callbackInfo) {
        if (helmet.isEmpty() || !CustomHelmetModels.hasCustomHeadModel(helmet)) return;
        poseStack.pushPose();
        armorModel.head.translateAndRotate(poseStack);
        //? if >=1.21.2 <1.21.9 {
        poseStack.translate(0.0F, -0.25F, 0.0F);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
        poseStack.scale(0.625F, -0.625F, -0.625F);
        //?} else {
        /*CustomHeadLayer.translateToHead(poseStack, false);
        *///?}
        CustomHelmetModels.render(helmet, poseStack, bufferSource, packedLight);
        poseStack.popPose();
        musavacca$renderTrim(helmet, armorModel, poseStack, bufferSource, packedLight);
        callbackInfo.cancel();
    }

    @Unique
    private void musavacca$renderTrim(ItemStack helmet, HumanoidModel<?> armorModel, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        ArmorTrim trim = helmet.get(DataComponents.TRIM);
        //? if >=1.21.2 <1.21.9 {
        if (trim == null) return;
        this.musavacca$trimCarrier.set(DataComponents.TRIM, trim);
        //? if >=1.21.4 <1.21.9 {
        this.equipmentRenderer.renderLayers(EquipmentClientInfo.LayerType.HUMANOID, CustomHelmetArmorTrims.TRIM_CARRIER_ASSET,
        //?} else {
        /*this.equipmentRenderer.renderLayers(EquipmentModel.LayerType.HUMANOID, CustomHelmetArmorTrims.TRIM_CARRIER_ID,
        *///?}
                armorModel, this.musavacca$trimCarrier, poseStack, bufferSource, packedLight);
        //?}
        //? if <1.21.2 {
        /*if (trim == null || !(helmet.getItem() instanceof ArmorItem armorItem)) {
            return;
        }
        float oldXScale = armorModel.head.xScale;
        float oldYScale = armorModel.head.yScale;
        float oldZScale = armorModel.head.zScale;
        armorModel.head.xScale *= 1.01F;
        armorModel.head.yScale *= 1.01F;
        armorModel.head.zScale *= 1.01F;
        try {
            musavacca$invokeVanillaRenderTrim(armorItem.getMaterial(), poseStack, bufferSource, packedLight, trim, armorModel, false);
        } finally {
            armorModel.head.xScale = oldXScale;
            armorModel.head.yScale = oldYScale;
            armorModel.head.zScale = oldZScale;
        }
        *///?}
    }

    //? if <1.21.2 {
    /*@Invoker("renderTrim")
    protected abstract void musavacca$invokeVanillaRenderTrim(Holder<ArmorMaterial> armorMaterial, PoseStack poseStack, MultiBufferSource bufferSource,
            int packedLight, ArmorTrim trim, HumanoidModel<?> armorModel, boolean leggings);
    *///?}
    //?}
}
