package space.anatomyuniverse.musavacca.mixin.client;

import java.util.List;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.client.renderer.texture.TextureAtlas;
//? if >=1.21.11 {
/*import net.minecraft.resources.Identifier;
*///?} else {
import net.minecraft.resources.ResourceLocation;
//?}
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import space.anatomyuniverse.musavacca.particle.tinted.CandleSpriteLayers;

@Mixin(SpriteLoader.class)
public abstract class PearlCandleSpriteLoaderMixin {
    //? if >=1.21.11 {
    /*@Shadow @Final private Identifier location;
    *///?} else {
    @Shadow @Final private ResourceLocation location;
    //?}

    @ModifyVariable(method = "stitch", at = @At("HEAD"), argsOnly = true, ordinal = 0, require = 1)
    private List<SpriteContents> musavacca$prepareCandleLayers(List<SpriteContents> sprites) {
        return this.location.equals(TextureAtlas.LOCATION_PARTICLES) ? CandleSpriteLayers.prepare(sprites) : sprites;
    }

    @Inject(method = "stitch", at = @At("RETURN"), require = 1)
    private void musavacca$linkCandleLayers(CallbackInfoReturnable<SpriteLoader.Preparations> cir) {
        if (this.location.equals(TextureAtlas.LOCATION_PARTICLES)) CandleSpriteLayers.link(cir.getReturnValue().regions());
    }
}
