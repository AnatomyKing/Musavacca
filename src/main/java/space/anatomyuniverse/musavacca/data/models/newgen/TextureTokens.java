package space.anatomyuniverse.musavacca.data.models.newgen;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import space.anatomyuniverse.musavacca.data.models.ModelUtil;

import java.util.Objects;

public final class TextureTokens {
    private TextureTokens() {}

    public static ResourceLocation block(Block block) {
        Objects.requireNonNull(block, "block");
        return ModelUtil.blockTex(block);
    }

    public static ResourceLocation block(Block block, String suffix) {
        Objects.requireNonNull(block, "block");
        Objects.requireNonNull(suffix, "suffix");

        ResourceLocation id = ModelUtil.idOf(block);
        ResourceLocation base = ModelUtil.blockTex(block);

        return ResourceLocation.fromNamespaceAndPath(
                id.getNamespace(),
                base.getPath() + suffix
        );
    }

    public static ResourceLocation resolveBlock(Block block, String token) {
        Objects.requireNonNull(block, "block");

        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("texture token must not be blank");
        }

        if (token.indexOf(':') >= 0) {
            return ResourceLocation.parse(token);
        }

        if (token.startsWith("_")) {
            return block(block, token);
        }

        ResourceLocation id = ModelUtil.idOf(block);
        return ResourceLocation.fromNamespaceAndPath(
                id.getNamespace(),
                token.startsWith("block/") ? token : "block/" + token
        );
    }

    public static ResourceLocation item(ItemLike item) {
        ResourceLocation id = ModelLocations.itemId(item);
        return ResourceLocation.fromNamespaceAndPath(
                id.getNamespace(),
                "item/" + id.getPath()
        );
    }

    public static ResourceLocation resolveItem(ItemLike item, String token) {
        return resolveItem(ModelLocations.itemId(item), token);
    }

    public static ResourceLocation resolveItem(ResourceLocation itemId, String token) {
        Objects.requireNonNull(itemId, "itemId");

        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("item texture token must not be blank");
        }

        if (token.indexOf(':') >= 0) {
            return ResourceLocation.parse(token);
        }

        return ResourceLocation.fromNamespaceAndPath(
                itemId.getNamespace(),
                token.startsWith("item/") ? token : "item/" + token
        );
    }
}
