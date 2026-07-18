package space.anatomyuniverse.musavacca.mixin.client;

import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import space.anatomyuniverse.musavacca.item.CustomHelmetArmorTrims;

@Mixin(HumanoidMobRenderer.class)
public abstract class HumanoidMobRendererMixin {

    @Inject(
            method = "extractHumanoidRenderState",
            at = @At("TAIL")
    )
    private static void musavacca$preserveCustomHelmetStack(
            LivingEntity entity,
            HumanoidRenderState renderState,
            float partialTick,
            ItemModelResolver itemModelResolver,
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
            renderState.headEquipment = helmet.copy();
        }
    }
}