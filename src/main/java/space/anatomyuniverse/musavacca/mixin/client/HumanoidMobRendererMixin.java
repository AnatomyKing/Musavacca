package space.anatomyuniverse.musavacca.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

//? if >=1.21.2 {
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
//? if >=1.21.4 {
import net.minecraft.client.renderer.item.ItemModelResolver;
//?}
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import space.anatomyuniverse.musavacca.item.CustomHelmetArmorTrims;
//?}

//? if >=1.21.2 {
@Mixin(HumanoidMobRenderer.class)
 //?} else {
/*@Pseudo
@Mixin(targets = "net.minecraft.client.renderer.entity.HumanoidMobRenderer")
*///?}
public abstract class HumanoidMobRendererMixin {

    //? if >=1.21.2 {
    @Inject(
            method = "extractHumanoidRenderState",
            at = @At("TAIL")
    )
    private static void musavacca$preserveCustomHelmetStack(
            LivingEntity entity,
            HumanoidRenderState renderState,
            float partialTick,
            //? if >=1.21.4 {
            ItemModelResolver itemModelResolver,
            //?}
            CallbackInfo callbackInfo
    ) {
        ItemStack helmet = entity.getItemBySlot(
                EquipmentSlot.HEAD
        );

        if (
                helmet.is(
                        CustomHelmetArmorTrims.CUSTOM_HEAD_HELMETS
                )
        ) {
            //? if <1.21.4 {
            /*renderState.headItem = helmet.copy();
            *///?} else {
            renderState.headEquipment = helmet.copy();
            //?}
        }
    }
    //?}
}
