package space.anatomyuniverse.musavacca.mixin.compat.gleam;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

//? if <1.21.2 {
/*import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.thatmaidenjaden.gleam.client.lighting.GleamLight;
import org.spongepowered.asm.mixin.injection.At;
import space.anatomyuniverse.musavacca.compat.gleam.GleamHexLight;
*///?}

@Pseudo
//? if <1.21.2 {
/*@Mixin(
        targets = "net.caffeinemc.mods.sodium.client.render.chunk.compile.tasks.ChunkBuilderMeshingTask",
        priority = 1100,
        remap = false
)
*///?}
//? if >=1.21.2 {
@Mixin(
        targets = "space.anatomyuniverse.musavacca.compat.gleam.DisabledGleamCompatTarget",
        remap = false
)
//?}
public abstract class GleamSodiumMixin {

    //? if <1.21.2 {
    /*@WrapOperation(
            method = "gleam$scanChunk",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/thatmaidenjaden/gleam/client/lighting/GleamEmitterRegistry;createLight(Lnet/minecraft/world/level/block/state/BlockState;III)Lnet/thatmaidenjaden/gleam/client/lighting/GleamLight;",
                    remap = false
            ),
            remap = false,
            require = 1
    )
    private static GleamLight musavacca$dynamicLight(
            BlockState state,
            int x,
            int y,
            int z,
            Operation<GleamLight> original,
            @Local BlockGetter slice
    ) {
        if (!GleamHexLight.handles(state)) {
            return original.call(state, x, y, z);
        }

        GleamLight light = GleamHexLight.create(
                slice,
                x,
                y,
                z
        );

        return light != null
                ? light
                : original.call(state, x, y, z);
    }
    *///?}
}