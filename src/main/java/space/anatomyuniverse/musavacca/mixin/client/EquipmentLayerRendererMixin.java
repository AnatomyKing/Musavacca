package space.anatomyuniverse.musavacca.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.EquipmentAsset;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import space.anatomyuniverse.musavacca.data.models.item.CustomArmorSetTintedLayers;
import space.anatomyuniverse.musavacca.tint.ProfileHexColorItemTintSource;

import java.util.ArrayDeque;
import java.util.Deque;

@Mixin(EquipmentLayerRenderer.class)
public abstract class EquipmentLayerRendererMixin {
    @Unique
    private static final ThreadLocal<Deque<TintContext>>
            MUSAVACCA$TINT_CONTEXTS =
            ThreadLocal.withInitial(ArrayDeque::new);

    @Inject(
            method =
                    "renderLayers("
                            + "Lnet/minecraft/client/resources/model/"
                            + "EquipmentClientInfo$LayerType;"
                            + "Lnet/minecraft/resources/ResourceKey;"
                            + "Lnet/minecraft/client/model/Model;"
                            + "Lnet/minecraft/world/item/ItemStack;"
                            + "Lcom/mojang/blaze3d/vertex/PoseStack;"
                            + "Lnet/minecraft/client/renderer/"
                            + "MultiBufferSource;"
                            + "I"
                            + "Lnet/minecraft/resources/ResourceLocation;"
                            + ")V",
            at = @At("HEAD")
    )
    private void musavacca$beginTintedEquipment(
            EquipmentClientInfo.LayerType layerType,
            ResourceKey<EquipmentAsset> equipmentAsset,
            Model model,
            ItemStack stack,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            ResourceLocation playerTexture,
            CallbackInfo callbackInfo
    ) {
        MUSAVACCA$TINT_CONTEXTS.get().push(
                new TintContext(
                        CustomArmorSetTintedLayers
                                .equipmentTintEntry(
                                        equipmentAsset
                                ),
                        stack
                )
        );
    }

    @Inject(
            method =
                    "renderLayers("
                            + "Lnet/minecraft/client/resources/model/"
                            + "EquipmentClientInfo$LayerType;"
                            + "Lnet/minecraft/resources/ResourceKey;"
                            + "Lnet/minecraft/client/model/Model;"
                            + "Lnet/minecraft/world/item/ItemStack;"
                            + "Lcom/mojang/blaze3d/vertex/PoseStack;"
                            + "Lnet/minecraft/client/renderer/"
                            + "MultiBufferSource;"
                            + "I"
                            + "Lnet/minecraft/resources/ResourceLocation;"
                            + ")V",
            at = @At("RETURN")
    )
    private void musavacca$endTintedEquipment(
            EquipmentClientInfo.LayerType layerType,
            ResourceKey<EquipmentAsset> equipmentAsset,
            Model model,
            ItemStack stack,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            ResourceLocation playerTexture,
            CallbackInfo callbackInfo
    ) {
        Deque<TintContext> contexts =
                MUSAVACCA$TINT_CONTEXTS.get();

        contexts.pop();

        if (contexts.isEmpty()) {
            MUSAVACCA$TINT_CONTEXTS.remove();
        }
    }

    @Inject(
            method = "getColorForLayer",
            at = @At("RETURN"),
            cancellable = true
    )
    private static void musavacca$getTintedEquipmentColor(
            EquipmentClientInfo.Layer layer,
            int dyedColor,
            CallbackInfoReturnable<Integer> callbackInfo
    ) {
        Deque<TintContext> contexts =
                MUSAVACCA$TINT_CONTEXTS.get();

        if (contexts.isEmpty()) {
            return;
        }

        TintContext context = contexts.peek();

        if (context.entry() == null) {
            return;
        }

        int modelLayer = context.entry()
                .equipmentModelLayer(
                        layer.textureId()
                );

        if (
                modelLayer < 0
                        || context.entry()
                        .isUntintedEquipmentLayer(
                                modelLayer
                        )
        ) {
            return;
        }

        int color = ProfileHexColorItemTintSource.of(
                context.entry().equipmentProfileLayer(
                        modelLayer
                ),
                context.entry().equipmentProfile(),
                false
        ).calculate(
                context.stack(),
                Minecraft.getInstance().level,
                null
        );

        callbackInfo.setReturnValue(color);
    }

    @Unique
    private record TintContext(
            CustomArmorSetTintedLayers.Entry entry,
            ItemStack stack
    ) {
    }
}
