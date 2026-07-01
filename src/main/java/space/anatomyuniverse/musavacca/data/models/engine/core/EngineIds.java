package space.anatomyuniverse.musavacca.data.models.engine.core;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

public final class EngineIds {
    private EngineIds() {}

    public static ResourceLocation parse(String id) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("id cannot be blank");
        return ResourceLocation.parse(id);
    }

    public static ResourceLocation blockId(Block block) {
        if (block == null) throw new IllegalArgumentException("block cannot be null");
        return BuiltInRegistries.BLOCK.getKey(block);
    }

    public static ResourceLocation itemId(ItemLike itemLike) {
        if (itemLike == null) throw new IllegalArgumentException("item cannot be null");
        Item item = itemLike.asItem();
        return BuiltInRegistries.ITEM.getKey(item);
    }

    public static ResourceLocation blockModel(Block block) {
        ResourceLocation id = blockId(block);
        return ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "block/" + id.getPath());
    }

    public static ResourceLocation blockTexture(Block block) {
        ResourceLocation id = blockId(block);
        return ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "block/" + id.getPath());
    }

    public static ResourceLocation itemModel(ItemLike itemLike) {
        ResourceLocation id = itemId(itemLike);
        return ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "item/" + id.getPath());
    }

    public static ResourceLocation itemTexture(ItemLike itemLike) {
        ResourceLocation id = itemId(itemLike);
        return ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "item/" + id.getPath());
    }
}
