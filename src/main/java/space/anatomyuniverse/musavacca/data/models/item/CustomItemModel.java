package space.anatomyuniverse.musavacca.data.models.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import space.anatomyuniverse.musavacca.data.models.ModelUtil;

//? if <1.21.4 {
/*import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
 *///?}

//? if >=1.21.4 {
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ItemModelUtils;
//?}

public final class CustomItemModel {

    private CustomItemModel() {}

    public record Entry(
            ItemLike item,
            String modelId
    ) {
        public ResourceLocation model() {
            return ResourceLocation.parse(
                    modelId
            );
        }
    }

    public static Entry of(
            ItemLike item,
            String modelId
    ) {
        return new Entry(
                item,
                modelId
        );
    }

    //? if <1.21.4 {
    /*public static void generate(
            ItemModelProvider itemModels,
            Entry... entries
    ) {
        if (entries == null) {
            return;
        }

        for (Entry entry : entries) {
            if (
                    entry == null
                            || entry.item() == null
                            || entry.modelId() == null
                            || entry.modelId().isBlank()
            ) {
                continue;
            }

            ResourceLocation itemId =
                    ModelUtil.idOf(
                            entry.item()
                    );

            ResourceLocation naturalItemModel =
                    ResourceLocation.fromNamespaceAndPath(
                            itemId.getNamespace(),
                            "item/" + itemId.getPath()
                    );

            // Pre-1.21.4 items already look directly for
            // models/item/<registry_name>.json.
            //
            // If that is also our requested model, generating
            // another wrapper would create:
            //
            // banana_phone -> banana_phone
            //
            // which is a circular parent and causes the
            // missing-model appearance.
            if (
                    naturalItemModel.equals(
                            entry.model()
                    )
            ) {
                continue;
            }

            // A genuinely different model still needs a
            // normal legacy parent redirect.
            //
            // Example:
            // inactive_voco_caller -> banana_phone_off
            itemModels.withExistingParent(
                    itemId.getPath(),
                    entry.model()
            );
        }
    }

    public static void generate(
            ItemModelProvider itemModels,
            ItemLike item,
            String modelId
    ) {
        generate(
                itemModels,
                of(
                        item,
                        modelId
                )
        );
    }
    *///?}

    //? if >=1.21.4 {
    public static void generate(
            ItemModelGenerators itemModels,
            Entry... entries
    ) {
        if (entries == null) {
            return;
        }

        for (Entry entry : entries) {
            if (
                    entry == null
                            || entry.item() == null
                            || entry.modelId() == null
                            || entry.modelId().isBlank()
            ) {
                continue;
            }

            itemModels.itemModelOutput.accept(
                    entry.item().asItem(),
                    ItemModelUtils.plainModel(
                            entry.model()
                    )
            );
        }
    }

    public static void generate(
            ItemModelGenerators itemModels,
            ItemLike item,
            String modelId
    ) {
        generate(
                itemModels,
                of(
                        item,
                        modelId
                )
        );
    }
    //?}
}