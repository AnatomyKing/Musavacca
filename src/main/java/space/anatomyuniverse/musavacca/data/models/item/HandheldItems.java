package space.anatomyuniverse.musavacca.data.models.item;

import net.minecraft.world.level.ItemLike;

//? if <1.21.4 {
/*import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
 *///?} else {
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.world.item.Item;
//?}

public final class HandheldItems {
    private HandheldItems() {}

    //? if <1.21.4 {
    /*/^* Handheld (parent: minecraft:item/handheld). ^/
    public static void generate(ItemModelProvider itemModels, ItemLike... items) {
        for (ItemLike it : items) itemModels.handheldItem(it.asItem());
    }
    *///?} else {
    /** Handheld flat item for 1.21.4+ (parent: minecraft:item/handheld). */
    public static void generate(ItemModelGenerators gen, ItemLike... items) {
        for (ItemLike it : items) {
            Item item = it.asItem();
            gen.generateFlatItem(item, ModelTemplates.FLAT_HANDHELD_ITEM);
        }
    }
    //?}
}