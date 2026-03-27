
package space.anatomyuniverse.musavacca.data.models.item;

//? if <1.21.4 {
/*import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import space.anatomyuniverse.musavacca.data.models.ModelUtil;
*///?}

public final class CustomItemModel {
    private CustomItemModel() {}

    //? if <1.21.4 {
    /*/^* Item model that points at an existing model id (e.g. "anynology:item/foo_custom"). ^/
    public static void generate(ItemModelProvider itemModels, ItemLike item, String modelId) {
        ResourceLocation parent = ResourceLocation.parse(modelId);
        itemModels.withExistingParent(ModelUtil.pathOf(item), parent);
    }
    *///?}
}
