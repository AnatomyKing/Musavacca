package space.anatomyuniverse.musavacca.data.models.newgen;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

import java.util.Objects;

public final class TextureTokens {
    private TextureTokens() {}

    public static ResourceLocation block(Block block) {
        ResourceLocation id = ModelLocations.blockId(block);
        return ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "block/" + id.getPath());
    }

    public static ResourceLocation block(Block block, String suffix) {
        Objects.requireNonNull(suffix, "suffix");
        ResourceLocation id = ModelLocations.blockId(block);
        return ResourceLocation.fromNamespaceAndPath(id.getNamespace(), block(block).getPath() + suffix);
    }

    public static ResourceLocation resolveBlock(Block block, String token) {
        Objects.requireNonNull(block, "block");
        if (token == null || token.isBlank()) throw new IllegalArgumentException("texture token must not be blank");
        if (token.indexOf(':') >= 0) return ResourceLocation.parse(token);
        if (token.startsWith("_")) return block(block, token);
        ResourceLocation id = ModelLocations.blockId(block);
        return ResourceLocation.fromNamespaceAndPath(
                id.getNamespace(),
                token.startsWith("block/") ? token : "block/" + token
        );
    }

    public static ResourceLocation item(ItemLike item) {
        ResourceLocation id = ModelLocations.itemId(item);
        return ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "item/" + id.getPath());
    }

    public static ResourceLocation itemFolder(ItemLike item) {
        ResourceLocation id = ModelLocations.itemId(item);
        return ResourceLocation.fromNamespaceAndPath(
                id.getNamespace(),
                "item/" + id.getPath() + "/" + id.getPath()
        );
    }

    public static ResourceLocation itemLayer(ResourceLocation base, int layer) {
        if (layer < 0) throw new IllegalArgumentException("layer must be >= 0");
        return ResourceLocation.fromNamespaceAndPath(base.getNamespace(), base.getPath() + "_" + layer);
    }

    public static ResourceLocation resolveItem(ItemLike item, String token) {
        return resolveItem(ModelLocations.itemId(item), token);
    }

    public static ResourceLocation resolveItem(ResourceLocation itemId, String token) {
        Objects.requireNonNull(itemId, "itemId");
        if (token == null || token.isBlank()) throw new IllegalArgumentException("item texture token must not be blank");
        if (token.indexOf(':') >= 0) return ResourceLocation.parse(token);
        return ResourceLocation.fromNamespaceAndPath(
                itemId.getNamespace(),
                token.startsWith("item/") ? token : "item/" + token
        );
    }
}
