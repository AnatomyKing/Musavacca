package space.anatomyuniverse.musavacca.mixin.compat.gleam;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
//? if <1.21.2 {
/*import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.thatmaidenjaden.gleam.client.lighting.GleamLight;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import space.anatomyuniverse.musavacca.compat.gleam.GleamHexLight;
*///?}

@Pseudo
@Mixin(
        //? if >=1.21.2 {
        targets = "space.anatomyuniverse.musavacca.compat.gleam.DisabledGleamCompatTarget",
        //?} else {
        /*targets = "net.thatmaidenjaden.gleam.client.lighting.GleamEmitterRegistry",
        *///?}
        remap = false
)
public abstract class GleamEmitterRegistryMixin {
    //? if <1.21.2 {
    /*@Inject(method = "createLight", at = @At("HEAD"), cancellable = true, remap = false, require = 1)
    private static void musavacca$dynamicLight(BlockState state, int x, int y, int z, CallbackInfoReturnable<GleamLight> cir) {
        if (!GleamHexLight.handles(state)) return;
        cir.setReturnValue(null);
        var level = Minecraft.getInstance().level;
        if (level == null) return;
        LevelChunk chunk = level.getChunkSource().getChunkNow(x >> 4, z >> 4);
        if (chunk == null) return;
        BlockPos pos = new BlockPos(x, y, z);
        if (chunk.getBlockState(pos) != state) return;
        GleamLight light = GleamHexLight.create(chunk.getBlockEntities().get(pos), x, y, z);
        cir.setReturnValue(light);
    }
    *///?}
}
