package space.anatomyuniverse.musavacca.mixin.client;

//? if >=1.21.4 {
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.renderer.item.BlockModelWrapper;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import space.anatomyuniverse.musavacca.tint.ProfileHexColorItemTintSource;

import java.util.List;

@Mixin(BlockModelWrapper.class)
public abstract class BlockModelWrapperMixin {

    @Shadow
    @Final
    private List<ItemTintSource> tints;

    @Redirect(
            method = "update",
            at = @At(
                    value = "INVOKE",
                    target =
                            "Lnet/minecraft/world/item/ItemStack;"
                                    + "hasFoil()Z"
            )
    )
    private boolean musavacca$controlProfileLayerFoil(
            ItemStack stack
    ) {
        /*
         * Preserve the normal ItemStack enchantment check.
         */
        if (!stack.hasFoil()) {
            return false;
        }

        /*
         * Profile-tinted model children explicitly decide
         * whether they own the foil pass.
         */
        for (ItemTintSource tint : this.tints) {
            if (
                    tint
                            instanceof ProfileHexColorItemTintSource
                            profileTint
            ) {
                return profileTint.isFoilCarrier();
            }
        }

        /*
         * Completely unrelated vanilla and modded models
         * retain normal enchantment-glint behaviour.
         */
        return true;
    }
}
//?}