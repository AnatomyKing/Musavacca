package space.anatomyuniverse.musavacca.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

//? if >=1.21.11 {
/*import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
*///?}

//? if >=26.3 {
/*import com.mojang.renderpearl.api.textures.FilterMode;
*///?} else if >=1.21.11 {
/*import com.mojang.blaze3d.textures.FilterMode;
*///?}

@Pseudo
@Mixin(
        //? if >=1.21.11 {
        /*value = LevelRenderer.class,
        *///?} else {
        targets = "space.anatomyuniverse.musavacca.mixin.client.DisabledNearestTerrainSamplingTarget",
        //?}
        remap = false
)
public abstract class LevelRendererNearestSamplingMixin {

    //? if >=1.21.11 {
    /*@ModifyArgs(
            // Match the unique sampler call without depending on lambda numbering.
            method = "*",
            at = @At(
                    value = "INVOKE",
                    //? if >=26.3 {
                    /^target = "Lcom/mojang/renderpearl/api/device/GpuDevice;createSampler(Lcom/mojang/renderpearl/api/textures/AddressMode;Lcom/mojang/renderpearl/api/textures/AddressMode;Lcom/mojang/renderpearl/api/textures/FilterMode;Lcom/mojang/renderpearl/api/textures/FilterMode;ILjava/util/OptionalDouble;)Lcom/mojang/renderpearl/api/textures/GpuSampler;",
                    ^///?} else {
                    target = "Lcom/mojang/blaze3d/systems/GpuDevice;createSampler(Lcom/mojang/blaze3d/textures/AddressMode;Lcom/mojang/blaze3d/textures/AddressMode;Lcom/mojang/blaze3d/textures/FilterMode;Lcom/mojang/blaze3d/textures/FilterMode;ILjava/util/OptionalDouble;)Lcom/mojang/blaze3d/textures/GpuSampler;",
                    //?}
                    remap = false
            ),
            remap = false,
            require = 1,
            allow = 1
    )
    private void musavacca$nearestTerrainSampling(Args args) {
        // createSampler(addressU, addressV, minFilter, magFilter, anisotropy, maxLod)
        args.set(2, FilterMode.NEAREST);
        args.set(3, FilterMode.NEAREST);
    }
    *///?}
}
