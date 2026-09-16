package space.anatomyuniverse.musavacca.data.models.newgen;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

import java.util.Objects;

public final class ModelLocations {
    private ModelLocations() {}

    public static ResourceLocation blockId(Block block) {
        Objects.requireNonNull(block, "block");
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(block);
        if (id == null) throw new IllegalStateException("Cannot use an unregistered block");
        return id;
    }

    public static ResourceLocation itemId(ItemLike item) {
        Objects.requireNonNull(item, "item");
        return itemId(item.asItem());
    }

    public static ResourceLocation itemId(Item item) {
        Objects.requireNonNull(item, "item");
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
        if (id == null) throw new IllegalStateException("Cannot use an unregistered item");
        return id;
    }

    public static String blockPath(Block block) {
        return blockId(block).getPath();
    }

    public static String itemPath(ItemLike item) {
        return itemId(item).getPath();
    }

    public static ResourceLocation blockModel(Block block) {
        return blockModel(block, "");
    }

    public static ResourceLocation blockModel(Block block, String suffix) {
        ResourceLocation id = blockId(block);
        String cleanSuffix = suffix == null || suffix.isBlank()
                ? ""
                : suffix.startsWith("_") ? suffix : "_" + suffix;

        return ResourceLocation.fromNamespaceAndPath(
                id.getNamespace(),
                "block/" + id.getPath() + cleanSuffix
        );
    }

    public static String blockModelPath(ResourceLocation model) {
        Objects.requireNonNull(model, "model");
        String path = model.getPath();
        return path.startsWith("block/") ? path.substring("block/".length()) : path;
    }

    public static ResourceLocation itemModel(ItemLike item) {
        ResourceLocation id = itemId(item);
        return ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "item/" + id.getPath());
    }

    public static ResourceLocation itemModel(Item item) {
        return itemModel((ItemLike) item);
    }

    public static ResourceLocation itemModel(ItemLike item, String suffix) {
        ResourceLocation id = itemId(item);
        String cleanSuffix = suffix == null || suffix.isBlank()
                ? ""
                : suffix.startsWith("_") ? suffix : "_" + suffix;
        return ResourceLocation.fromNamespaceAndPath(
                id.getNamespace(),
                "item/" + id.getPath() + cleanSuffix
        );
    }
}

