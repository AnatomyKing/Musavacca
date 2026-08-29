
package space.anatomyuniverse.musavacca.data.models;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

public final class ModelUtil {
    private ModelUtil() {}

    public static ResourceLocation idOf(Block b) {
        return BuiltInRegistries.BLOCK.getKey(b);
    }

    public static ResourceLocation idOf(Item i) {
        return BuiltInRegistries.ITEM.getKey(i);
    }

    public static ResourceLocation idOf(ItemLike il) {
        return idOf(il.asItem());
    }

    public static String pathOf(ItemLike il) {
        return idOf(il).getPath();
    }

    public static String pathOf(Block b) {
        return idOf(b).getPath();
    }

    /** assets/<ns>/textures/block/<path>.png */
    public static ResourceLocation blockTex(Block b) {
        ResourceLocation id = idOf(b);
        return ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "block/" + id.getPath());
    }

    /** assets/<ns>/textures/item/<path>.png */
    public static ResourceLocation itemTex(ItemLike il) {
        ResourceLocation id = idOf(il);
        return ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "item/" + id.getPath());
    }
}


