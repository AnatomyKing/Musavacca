// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/data/models/item/CustomItemModel.java

package space.anatomyuniverse.musavacca.data.models.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import space.anatomyuniverse.musavacca.data.models.ModelUtil;

//? if <1.21.4 {
/*import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
 *///?} else {
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ItemModelUtils;
//?}

public final class CustomItemModel {
    private CustomItemModel() {}

    /**
     * Makes an item use an existing model.
     *
     * Example model id:
     * "musavacca:item/voco_connector"
     *
     * That means you already have:
     * assets/musavacca/models/item/voco_connector.json
     */
    public record Entry(ItemLike item, String modelId) {
        public ResourceLocation model() {
            return ResourceLocation.parse(modelId);
        }
    }

    public static Entry of(ItemLike item, String modelId) {
        return new Entry(item, modelId);
    }

    //? if <1.21.4 {
    /*public static void generate(ItemModelProvider itemModels, Entry... entries) {
        if (entries == null) return;

        for (Entry entry : entries) {
            if (entry == null || entry.item() == null) continue;
            if (entry.modelId() == null || entry.modelId().isBlank()) continue;

            itemModels.withExistingParent(
                    ModelUtil.pathOf(entry.item()),
                    entry.model()
            );
        }
    }

    public static void generate(ItemModelProvider itemModels, ItemLike item, String modelId) {
        generate(itemModels, of(item, modelId));
    }
    *///?} else {
    public static void generate(ItemModelGenerators itemModels, Entry... entries) {
        if (entries == null) return;

        for (Entry entry : entries) {
            if (entry == null || entry.item() == null) continue;
            if (entry.modelId() == null || entry.modelId().isBlank()) continue;

            itemModels.itemModelOutput.accept(
                    entry.item().asItem(),
                    ItemModelUtils.plainModel(entry.model())
            );
        }
    }

    public static void generate(ItemModelGenerators itemModels, ItemLike item, String modelId) {
        generate(itemModels, of(item, modelId));
    }
    //?}
}