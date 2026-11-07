package space.anatomyuniverse.musavacca.data.models.newgen;

import net.minecraft.core.registries.BuiltInRegistries;
//? if >=1.21.11 {
/*import net.minecraft.resources.Identifier;
*///?} else {
import net.minecraft.resources.ResourceLocation;
//?}
import java.util.Objects;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

public final class ModelLocations {
    private ModelLocations() {}

    //? if >=1.21.11 {
    /*public static Identifier blockId(Block block) {
    *///?} else {
    public static ResourceLocation blockId(Block block) {
    //?}
        Objects.requireNonNull(block, "block");
        var id = BuiltInRegistries.BLOCK.getKey(block);
        if (id == null) throw new IllegalStateException("Cannot use an unregistered block");
        return id;
    }

    //? if >=1.21.11 {
    /*public static Identifier itemId(ItemLike item) {
    *///?} else {
    public static ResourceLocation itemId(ItemLike item) {
    //?}
        Objects.requireNonNull(item, "item");
        return itemId(item.asItem());
    }

    //? if >=1.21.11 {
    /*public static Identifier itemId(Item item) {
    *///?} else {
    public static ResourceLocation itemId(Item item) {
    //?}
        Objects.requireNonNull(item, "item");
        var id = BuiltInRegistries.ITEM.getKey(item);
        if (id == null) throw new IllegalStateException("Cannot use an unregistered item");
        return id;
    }

    //? if >=1.21.11 {
    /*public static Identifier blockModel(Block block, String suffix) {
    *///?} else {
    public static ResourceLocation blockModel(Block block, String suffix) {
    //?}
        var id = blockId(block);
        String cleanSuffix = suffix == null || suffix.isBlank() ? "" : suffix.startsWith("_") ? suffix : "_" + suffix;
        //? if >=1.21.11 {
        /*return Identifier.fromNamespaceAndPath(id.getNamespace(), "block/" + id.getPath() + cleanSuffix);
        *///?} else {
        return ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "block/" + id.getPath() + cleanSuffix);
        //?}
    }

    //? if >=1.21.11 {
    /*public static Identifier itemModel(Identifier id) {
    *///?} else {
    public static ResourceLocation itemModel(ResourceLocation id) {
    //?}
        return itemModel(id, "");
    }

    //? if >=1.21.11 {
    /*public static Identifier itemModel(Identifier id, String suffix) {
    *///?} else {
    public static ResourceLocation itemModel(ResourceLocation id, String suffix) {
    //?}
        String path = "item/" + id.getPath() + (suffix == null || suffix.isBlank() ? "" : suffix.startsWith("_") ? suffix : "_" + suffix);
        //? if >=1.21.11 {
        /*return Identifier.fromNamespaceAndPath(id.getNamespace(), path);
        *///?} else {
        return ResourceLocation.fromNamespaceAndPath(id.getNamespace(), path);
        //?}
    }

    //? if >=1.21.11 {
    /*public static Identifier itemModel(ItemLike item) {
    *///?} else {
    public static ResourceLocation itemModel(ItemLike item) {
    //?}
        return itemModel(itemId(item));
    }

    //? if >=1.21.11 {
    /*public static Identifier itemModel(Item item) {
    *///?} else {
    public static ResourceLocation itemModel(Item item) {
    //?}
        return itemModel((ItemLike) item);
    }

    //? if >=1.21.11 {
    /*public static Identifier itemModel(ItemLike item, String suffix) {
    *///?} else {
    public static ResourceLocation itemModel(ItemLike item, String suffix) {
    //?}
        return itemModel(itemId(item), suffix);
    }

    static Block inferBaseBlock(Block child, String suffix) {
        Objects.requireNonNull(child, "child");
        if (suffix == null || suffix.isBlank()) throw new IllegalArgumentException("suffix must not be blank");
        var id = blockId(child);
        String path = id.getPath();
        if (!path.endsWith(suffix)) throw new IllegalStateException("Cannot infer a base block for " + id + ": registry path does not end with " + suffix);
        String stem = path.substring(0, path.length() - suffix.length());
        Block planks = getBlockIfPresent(id.getNamespace(), stem + "_planks");
        if (planks != null) return planks;
        Block fullBlock = getBlockIfPresent(id.getNamespace(), stem + "_block");
        if (fullBlock != null) return fullBlock;
        Block plain = getBlockIfPresent(id.getNamespace(), stem);
        if (plain != null) return plain;
        throw new IllegalStateException(
                "Could not infer a full/base block for " + id + ". Expected a matching <stem>_planks, " + "<stem>_block, or <stem> block.");
    }

    private static Block getBlockIfPresent(String namespace, String path) {
        //? if >=1.21.11 {
        /*var id = Identifier.fromNamespaceAndPath(namespace, path);
        *///?} else {
        var id = ResourceLocation.fromNamespaceAndPath(namespace, path);
        //?}
        return BuiltInRegistries.BLOCK.getOptional(id).orElse(null);
    }
}
