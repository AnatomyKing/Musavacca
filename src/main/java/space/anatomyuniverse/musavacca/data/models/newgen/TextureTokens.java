package space.anatomyuniverse.musavacca.data.models.newgen;

//? if >=1.21.11 {
/*import net.minecraft.resources.Identifier;
*///?} else {
import net.minecraft.resources.ResourceLocation;
//?}
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

import java.util.Objects;

public final class TextureTokens {
    private TextureTokens() {}

    //? if >=1.21.11 {
    /*public static Identifier block(Block block) {
        Identifier id = ModelLocations.blockId(block);
        return Identifier.fromNamespaceAndPath(id.getNamespace(), "block/" + id.getPath());
    *///?} else {
    public static ResourceLocation block(Block block) {
        ResourceLocation id = ModelLocations.blockId(block);
        return ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "block/" + id.getPath());
    //?}
    }

    //? if >=1.21.11 {
    /*public static Identifier block(Block block, String suffix) {
        Objects.requireNonNull(suffix, "suffix");
        Identifier id = ModelLocations.blockId(block);
        return Identifier.fromNamespaceAndPath(id.getNamespace(), block(block).getPath() + suffix);
    *///?} else {
    public static ResourceLocation block(Block block, String suffix) {
        Objects.requireNonNull(suffix, "suffix");
        ResourceLocation id = ModelLocations.blockId(block);
        return ResourceLocation.fromNamespaceAndPath(id.getNamespace(), block(block).getPath() + suffix);
    //?}
    }

    //? if >=1.21.11 {
    /*public static Identifier resolveBlock(Block block, String token) {
    *///?} else {
    public static ResourceLocation resolveBlock(Block block, String token) {
    //?}
        Objects.requireNonNull(block, "block");
        if (token == null || token.isBlank()) throw new IllegalArgumentException("texture token must not be blank");
        //? if >=1.21.11 {
        /*if (token.indexOf(':') >= 0) return Identifier.parse(token);
        if (token.startsWith("_")) return block(block, token);
        Identifier id = ModelLocations.blockId(block);
        return Identifier.fromNamespaceAndPath(id.getNamespace(), token.startsWith("block/") ? token : "block/" + token);
        *///?} else {
        if (token.indexOf(':') >= 0) return ResourceLocation.parse(token);
        if (token.startsWith("_")) return block(block, token);
        ResourceLocation id = ModelLocations.blockId(block);
        return ResourceLocation.fromNamespaceAndPath(id.getNamespace(), token.startsWith("block/") ? token : "block/" + token);
        //?}
    }

    //? if >=1.21.11 {
    /*public static Identifier item(ItemLike item) {
        Identifier id = ModelLocations.itemId(item);
        return Identifier.fromNamespaceAndPath(id.getNamespace(), "item/" + id.getPath());
    *///?} else {
    public static ResourceLocation item(ItemLike item) {
        ResourceLocation id = ModelLocations.itemId(item);
        return ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "item/" + id.getPath());
    //?}
    }

    //? if >=1.21.11 {
    /*public static Identifier itemFolder(ItemLike item) {
        Identifier id = ModelLocations.itemId(item);
        return Identifier.fromNamespaceAndPath(id.getNamespace(), "item/" + id.getPath() + "/" + id.getPath());
    *///?} else {
    public static ResourceLocation itemFolder(ItemLike item) {
        ResourceLocation id = ModelLocations.itemId(item);
        return ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "item/" + id.getPath() + "/" + id.getPath());
    //?}
    }

    //? if >=1.21.11 {
    /*public static Identifier itemLayer(Identifier base, int layer) {
        if (layer < 0) throw new IllegalArgumentException("layer must be >= 0");
        return Identifier.fromNamespaceAndPath(base.getNamespace(), base.getPath() + "_" + layer);
    *///?} else {
    public static ResourceLocation itemLayer(ResourceLocation base, int layer) {
        if (layer < 0) throw new IllegalArgumentException("layer must be >= 0");
        return ResourceLocation.fromNamespaceAndPath(base.getNamespace(), base.getPath() + "_" + layer);
    //?}
    }

    //? if >=1.21.11 {
    /*public static Identifier resolveItem(ItemLike item, String token) {
    *///?} else {
    public static ResourceLocation resolveItem(ItemLike item, String token) {
    //?}
        return resolveItem(ModelLocations.itemId(item), token);
    }

    //? if >=1.21.11 {
    /*public static Identifier resolveItem(Identifier itemId, String token) {
    *///?} else {
    public static ResourceLocation resolveItem(ResourceLocation itemId, String token) {
    //?}
        Objects.requireNonNull(itemId, "itemId");
        if (token == null || token.isBlank()) throw new IllegalArgumentException("item texture token must not be blank");
        //? if >=1.21.11 {
        /*if (token.indexOf(':') >= 0) return Identifier.parse(token);
        return Identifier.fromNamespaceAndPath(itemId.getNamespace(), token.startsWith("item/") ? token : "item/" + token);
        *///?} else {
        if (token.indexOf(':') >= 0) return ResourceLocation.parse(token);
        return ResourceLocation.fromNamespaceAndPath(itemId.getNamespace(), token.startsWith("item/") ? token : "item/" + token);
        //?}
    }
}
