package space.anatomyuniverse.musavacca.data.models.newgen;

import net.minecraft.core.registries.BuiltInRegistries;
//? if >=1.21.11 {
/*import net.minecraft.resources.Identifier;
*///?} else {
import net.minecraft.resources.ResourceLocation;
//?}
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

import java.util.Objects;

public final class ModelLocations {
    private ModelLocations() {}

    //? if >=1.21.11 {
    /*public static Identifier blockId(Block block) {
        Objects.requireNonNull(block, "block");
        Identifier id = BuiltInRegistries.BLOCK.getKey(block);
    *///?} else {
    public static ResourceLocation blockId(Block block) {
        Objects.requireNonNull(block, "block");
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(block);
    //?}
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
        Objects.requireNonNull(item, "item");
        Identifier id = BuiltInRegistries.ITEM.getKey(item);
    *///?} else {
    public static ResourceLocation itemId(Item item) {
        Objects.requireNonNull(item, "item");
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
    //?}
        if (id == null) throw new IllegalStateException("Cannot use an unregistered item");
        return id;
    }

    public static String blockPath(Block block) {
        return blockId(block).getPath();
    }

    public static String itemPath(ItemLike item) {
        return itemId(item).getPath();
    }

    //? if >=1.21.11 {
    /*public static Identifier blockModel(Block block) {
    *///?} else {
    public static ResourceLocation blockModel(Block block) {
    //?}
        return blockModel(block, "");
    }

    //? if >=1.21.11 {
    /*public static Identifier blockModel(Block block, String suffix) {
        Identifier id = blockId(block);
    *///?} else {
    public static ResourceLocation blockModel(Block block, String suffix) {
        ResourceLocation id = blockId(block);
    //?}
        String cleanSuffix = suffix == null || suffix.isBlank()
                ? ""
                : suffix.startsWith("_") ? suffix : "_" + suffix;

        //? if >=1.21.11 {
        /*return Identifier.fromNamespaceAndPath(id.getNamespace(), "block/" + id.getPath() + cleanSuffix);
        *///?} else {
        return ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "block/" + id.getPath() + cleanSuffix);
        //?}
    }

    //? if >=1.21.11 {
    /*public static String blockModelPath(Identifier model) {
    *///?} else {
    public static String blockModelPath(ResourceLocation model) {
    //?}
        Objects.requireNonNull(model, "model");
        String path = model.getPath();
        return path.startsWith("block/") ? path.substring("block/".length()) : path;
    }

    //? if >=1.21.11 {
    /*public static Identifier itemModel(ItemLike item) {
        Identifier id = itemId(item);
        return Identifier.fromNamespaceAndPath(id.getNamespace(), "item/" + id.getPath());
    *///?} else {
    public static ResourceLocation itemModel(ItemLike item) {
        ResourceLocation id = itemId(item);
        return ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "item/" + id.getPath());
    //?}
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
        Identifier id = itemId(item);
    *///?} else {
    public static ResourceLocation itemModel(ItemLike item, String suffix) {
        ResourceLocation id = itemId(item);
    //?}
        String cleanSuffix = suffix == null || suffix.isBlank()
                ? ""
                : suffix.startsWith("_") ? suffix : "_" + suffix;
        //? if >=1.21.11 {
        /*return Identifier.fromNamespaceAndPath(id.getNamespace(), "item/" + id.getPath() + cleanSuffix);
        *///?} else {
        return ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "item/" + id.getPath() + cleanSuffix);
        //?}
    }
}
