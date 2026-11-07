package space.anatomyuniverse.musavacca.mixin.client;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.metadata.animation.FrameSize;
//? if >=1.21.11 {
/*import net.minecraft.client.resources.metadata.texture.TextureMetadataSection;
import net.minecraft.resources.Identifier;
*///?} else {
import net.minecraft.resources.ResourceLocation;
//?}
//? if >=1.21.9 {
/*import java.util.List;
import java.util.Optional;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
*///?}
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import space.anatomyuniverse.musavacca.particle.tinted.CandleSpriteLayers;

@Mixin(SpriteContents.class)
public abstract class PearlCandleSpriteContentsMixin implements CandleSpriteLayers.Source {
    @Shadow
    @Final
    private NativeImage originalImage;
    @Unique
    private TextureAtlasSprite[] musavacca$layers;
    //? if >=1.21.9 {
    /*@Unique
    private Optional<AnimationMetadataSection> musavacca$animation = Optional.empty();
    @Shadow
    @Final
    private List<MetadataSectionType.WithValue<?>> additionalMetadata;
    *///?}
    //? if >=1.21.11 {
    /*@Unique
    private Optional<TextureMetadataSection> musavacca$texture = Optional.empty();
    *///?}

    //? if >=1.21.9 {
    /*@Inject(method =
            //? if >=1.21.11 {
            /^"<init>(Lnet/minecraft/resources/Identifier;Lnet/minecraft/client/resources/metadata/animation/FrameSize;Lcom/mojang/blaze3d/platform/NativeImage;Ljava/util/Optional;Ljava/util/List;Ljava/util/Optional;)V",
            ^///?} else {
            "<init>(Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/client/resources/metadata/animation/FrameSize;Lcom/mojang/blaze3d/platform/NativeImage;Ljava/util/Optional;Ljava/util/List;)V",
            //?}
            at = @At("RETURN"), require = 1)
    private void musavacca$rememberMetadata(
            //? if >=1.21.11 {
            /^Identifier id,
            ^///?} else {
            ResourceLocation id,
            //?}
            FrameSize size, NativeImage image, Optional<AnimationMetadataSection> animation, List<MetadataSectionType.WithValue<?>> metadata,
            //? if >=1.21.11 {
            /^Optional<TextureMetadataSection> texture,
            ^///?}
            CallbackInfo ci) {
        this.musavacca$animation = animation;
        //? if >=1.21.11 {
        /^this.musavacca$texture = texture;
        ^///?}
    }
    *///?}

    @Override
    public SpriteContents musavacca$layer(
            //? if >=1.21.11 {
            /*Identifier id,
            *///?} else {
            ResourceLocation id,
            //?}
            NativeImage image) {
        SpriteContents source = (SpriteContents) (Object) this;
        return new SpriteContents(id, new FrameSize(source.width(), source.height()), image,
                //? if >=1.21.9 {
                /*this.musavacca$animation, this.additionalMetadata
                *///?} else {
                source.metadata()
                //?}
                //? if >=1.21.11 {
                /*, this.musavacca$texture
                *///?}
        );
    }

    @Override
    public NativeImage musavacca$image() { return this.originalImage; }

    @Override
    public TextureAtlasSprite[] musavacca$layers() { return this.musavacca$layers; }

    @Override
    public void musavacca$layers(TextureAtlasSprite[] layers) { this.musavacca$layers = layers; }
}
