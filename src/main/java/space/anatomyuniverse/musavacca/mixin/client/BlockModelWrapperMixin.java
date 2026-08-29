package space.anatomyuniverse.musavacca.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

//? if >=1.21.4 {
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.renderer.item.BlockModelWrapper;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import space.anatomyuniverse.musavacca.tint.ProfileHexColorItemTintSource;

import java.util.List;
//?}

//? if >=1.21.4 {
@Mixin(BlockModelWrapper.class)
//?} else {
/*@Pseudo
@Mixin(targets = "net.minecraft.client.renderer.item.BlockModelWrapper")
*///?}
public abstract class BlockModelWrapperMixin {
    //? if >=1.21.4 {
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
        if (!stack.hasFoil()) {
            return false;
        }

        for (ItemTintSource tint : this.tints) {
            if (
                    tint
                            instanceof ProfileHexColorItemTintSource
                            profileTint
            ) {
                return profileTint.isFoilCarrier();
            }
        }

        return true;
    }
    //?}
}


