package space.anatomyuniverse.musavacca.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import space.anatomyuniverse.musavacca.tint.SpriteTintLayers;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

//? if >=1.21.11 {
/*import net.minecraft.resources.Identifier;
*///?} else {
import net.minecraft.resources.ResourceLocation;
//?}

@Mixin(SpriteLoader.class)
public abstract class SpriteLoaderMixin {
    //? if >=1.21.11 {
    /*@Shadow @Final private Identifier location;
    *///?} else {
    @Shadow @Final private ResourceLocation location;
    //?}

    @WrapOperation(
            method = "loadAndStitch*",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Ljava/util/concurrent/CompletableFuture;thenApply(Ljava/util/function/Function;)Ljava/util/concurrent/CompletableFuture;",
                            remap = false),
            require = 1,
            allow = 1)
    private CompletableFuture<SpriteLoader.Preparations> musavacca$prepareSpriteLayers(
            CompletableFuture<List<SpriteContents>> future,
            Function<List<SpriteContents>, SpriteLoader.Preparations> stitch,
            Operation<CompletableFuture<SpriteLoader.Preparations>> original,
            @Local(argsOnly = true) ResourceManager resources) {
        var atlas = SpriteTintLayers.atlas(this.location);
        if (atlas == null) return original.call(future, stitch);
        return original.call(
                future,
                (Function<List<SpriteContents>, SpriteLoader.Preparations>)
                        sprites -> {
                            var prepared = stitch.apply(SpriteTintLayers.prepare(sprites, resources, atlas));
                            SpriteTintLayers.link(prepared.regions());
                            return prepared;
                        });
    }
}
