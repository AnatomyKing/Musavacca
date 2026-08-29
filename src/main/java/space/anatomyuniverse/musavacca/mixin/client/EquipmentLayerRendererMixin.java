package space.anatomyuniverse.musavacca.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

//? if >=1.21.2 {
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
//?}

/**
 * Compatibility shell kept only because existing mixin configuration may still reference this class.
 * The former custom equipment tint hooks have been removed completely.
 */
//? if >=1.21.2 {
@Mixin(EquipmentLayerRenderer.class)
//?} else {
/*@Pseudo
@Mixin(targets = "net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer")
*///?}
public abstract class EquipmentLayerRendererMixin {
}
