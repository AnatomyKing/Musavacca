package space.anatomyuniverse.musavacca.mixin.client;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.client.renderer.texture.TextureAtlas;
//? if >=1.21.11 {
/*import net.minecraft.resources.Identifier;
*///?} else {
import net.minecraft.resources.ResourceLocation;
//?}
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import space.anatomyuniverse.musavacca.particle.tinted.CandleSpriteLayers;

@Mixin(SpriteLoader.class)
public abstract class PearlCandleSpriteLoaderMixin {
    //? if >=1.21.11 {
    /*@Shadow @Final private Identifier location;
    *///?} else {
    @Shadow @Final private ResourceLocation location;
    //?}

    @WrapOperation(method = "loadAndStitch*", at = @At(value = "INVOKE",
            target = "Ljava/util/concurrent/CompletableFuture;thenApply(Ljava/util/function/Function;)Ljava/util/concurrent/CompletableFuture;",
            remap = false), require = 1, allow = 1)
    private CompletableFuture<SpriteLoader.Preparations> musavacca$prepareCandleLayers(
            CompletableFuture<List<SpriteContents>> future, Function<List<SpriteContents>, SpriteLoader.Preparations> stitch,
            Operation<CompletableFuture<SpriteLoader.Preparations>> original, @Local(argsOnly = true) ResourceManager resources) {
        if (!this.location.equals(TextureAtlas.LOCATION_PARTICLES)) return original.call(future, stitch);
        return original.call(future, (Function<List<SpriteContents>, SpriteLoader.Preparations>) sprites -> {
            var prepared = stitch.apply(CandleSpriteLayers.prepare(sprites, resources));
            CandleSpriteLayers.link(prepared.regions());
            return prepared;
        });
    }
}
