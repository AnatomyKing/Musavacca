package space.anatomyuniverse.musavacca.data.models.item;

import net.minecraft.world.level.ItemLike;

//? if <1.21.4 {
/*import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
*///?} else {
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.world.item.Item;
//?}

public final class FlatItems {
    private FlatItems() {}

    //? if <1.21.4 {
    /*/^* Flat generated item (parent: minecraft:item/generated). ^/
    public static void generate(ItemModelProvider itemModels, ItemLike... items) {
        for (ItemLike it : items) itemModels.basicItem(it.asItem());
    }
    *///?} else {
    /** Flat generated item for 1.21.4+ via ItemModelGenerators. */
    public static void generate(ItemModelGenerators gen, ItemLike... items) {
        for (ItemLike it : items) {
            Item item = it.asItem();
            gen.generateFlatItem(item, ModelTemplates.FLAT_ITEM);
        }
    }
    //?}
}
