// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/data/models/item/ItemTintedLayered.java
package space.anatomyuniverse.musavacca.data.models.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import space.anatomyuniverse.musavacca.data.models.ModelUtil;
import space.anatomyuniverse.musavacca.tint.PearlFireTintProfiles;

//? if <1.21.4 {
/*import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
 *///?} else {
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.renderer.item.BlockModelWrapper;
import net.minecraft.client.renderer.item.CompositeModel;
import net.minecraft.client.renderer.item.ItemModel;
import space.anatomyuniverse.musavacca.tint.ProfileHexColorItemTintSource;

import java.util.ArrayList;
import java.util.List;
//?}

public final class ItemTintedLayered {
    private ItemTintedLayered() {}

    /**
     * This helper uses a composite client item model.
     *
     * Important:
     * - Each visual layer is generated as its own normal minecraft:item/generated model.
     * - Because every child model only uses layer0, every child keeps vanilla generated-item extrusion.
     * - The final item is a CompositeModel that draws:
     *      base model first,
     *      tinted layer 0 second,
     *      tinted layer 1 third,
     *      etc.
     *
     * For SIM_CARD with ItemTintedLayered.root(...), expected textures are:
     *
     * assets/musavacca/textures/item/sim_card.png
     * assets/musavacca/textures/item/sim_card_0.png
     * assets/musavacca/textures/item/sim_card_1.png
     * assets/musavacca/textures/item/sim_card_2.png
     * assets/musavacca/textures/item/sim_card_3.png
     * assets/musavacca/textures/item/sim_card_4.png
     */
    public record Entry(
            ItemLike item,
            String modelStem,
            String baseTextureStem,
            String tintedTextureStem,
            PearlFireTintProfiles.Profile profile
    ) {
        public Entry {
            if (item == null) {
                throw new IllegalArgumentException("item must not be null");
            }

            if (modelStem == null || modelStem.isBlank()) {
                throw new IllegalArgumentException("modelStem must not be blank");
            }

            if (baseTextureStem == null || baseTextureStem.isBlank()) {
                throw new IllegalArgumentException("baseTextureStem must not be blank");
            }

            if (tintedTextureStem == null || tintedTextureStem.isBlank()) {
                throw new IllegalArgumentException("tintedTextureStem must not be blank");
            }

            if (profile == null) {
                throw new IllegalArgumentException("profile must not be null");
            }
        }

        public int tintedLayerCount() {
            return profile.layerCount();
        }

        public ResourceLocation itemId() {
            return ModelUtil.idOf(item);
        }

        public ResourceLocation baseModelId() {
            ResourceLocation id = itemId();

            return ResourceLocation.fromNamespaceAndPath(
                    id.getNamespace(),
                    "item/" + modelStem
            );
        }

        public ResourceLocation tintedModelId(int layer) {
            ResourceLocation id = itemId();

            return ResourceLocation.fromNamespaceAndPath(
                    id.getNamespace(),
                    "item/" + modelStem + "_tinted_" + layer
            );
        }

        public ResourceLocation baseTexture() {
            ResourceLocation id = itemId();

            return ResourceLocation.fromNamespaceAndPath(
                    id.getNamespace(),
                    "item/" + baseTextureStem
            );
        }

        public ResourceLocation tintedTexture(int layer) {
            ResourceLocation id = itemId();

            return ResourceLocation.fromNamespaceAndPath(
                    id.getNamespace(),
                    "item/" + tintedTextureStem + "_" + layer
            );
        }
    }

    /**
     * Root texture layout.
     *
     * For item sim_card:
     *   textures/item/sim_card.png
     *   textures/item/sim_card_0.png
     *   textures/item/sim_card_1.png
     *   ...
     */
    public static Entry root(ItemLike item, PearlFireTintProfiles.Profile profile) {
        String stem = ModelUtil.pathOf(item);
        return new Entry(item, stem, stem, stem, profile);
    }

    /**
     * Folder texture layout.
     *
     * For item sim_card:
     *   textures/item/sim_card.png
     *   textures/item/sim_card/sim_card_0.png
     *   textures/item/sim_card/sim_card_1.png
     *   ...
     */
    public static Entry folder(ItemLike item, PearlFireTintProfiles.Profile profile) {
        String stem = ModelUtil.pathOf(item);
        return new Entry(item, stem, stem, stem + "/" + stem, profile);
    }

    public static Entry of(
            ItemLike item,
            String modelStem,
            String baseTextureStem,
            String tintedTextureStem,
            PearlFireTintProfiles.Profile profile
    ) {
        return new Entry(item, modelStem, baseTextureStem, tintedTextureStem, profile);
    }

    //? if <1.21.4 {
    /*public static void generate(ItemModelProvider itemModels, Entry... entries) {
        if (entries == null) return;

        for (Entry entry : entries) {
            if (entry == null) continue;

            var builder = itemModels
                    .withExistingParent(entry.modelStem(), itemModels.mcLoc("item/generated"))
                    .texture("layer0", entry.baseTexture());

            for (int layer = 0; layer < entry.tintedLayerCount(); ++layer) {
                builder.texture("layer" + (layer + 1), entry.tintedTexture(layer));
            }
        }
    }
    *///?} else {
    public static void generate(ItemModelGenerators items, Entry... entries) {
        if (entries == null) return;

        for (Entry entry : entries) {
            if (entry == null) continue;
            generateOne(items, entry);
        }
    }

    private static void generateOne(ItemModelGenerators items, Entry entry) {
        /*
         * Create a normal vanilla-generated item model for the base.
         * This keeps item/generated extrusion.
         */
        createGeneratedLayerModel(
                items,
                entry.baseModelId(),
                entry.baseTexture()
        );

        /*
         * Create a normal vanilla-generated item model for every tinted layer.
         * Each one only uses layer0, so it avoids the vanilla generated-item layer cap.
         */
        for (int layer = 0; layer < entry.tintedLayerCount(); ++layer) {
            createGeneratedLayerModel(
                    items,
                    entry.tintedModelId(layer),
                    entry.tintedTexture(layer)
            );
        }

        /*
         * Final client item:
         * Draw base first, then every tinted generated-item model on top.
         */
        items.itemModelOutput.accept(
                entry.item().asItem(),
                new CompositeModel.Unbaked(compositeChildren(entry))
        );
    }

    private static void createGeneratedLayerModel(
            ItemModelGenerators items,
            ResourceLocation modelId,
            ResourceLocation texture
    ) {
        ModelTemplates.FLAT_ITEM.create(
                modelId,
                new TextureMapping()
                        .put(TextureSlot.LAYER0, texture),
                items.modelOutput
        );
    }

    private static List<ItemModel.Unbaked> compositeChildren(Entry entry) {
        List<ItemModel.Unbaked> children = new ArrayList<>();

        /*
         * Base model:
         * No tints. This stays untouched.
         */
        children.add(
                new BlockModelWrapper.Unbaked(
                        entry.baseModelId(),
                        List.of()
                )
        );

        /*
         * Tinted models:
         * Every child model uses vanilla layer0, so each child's tintindex is 0.
         * The child gets exactly one tint source, pointing to the wanted profile layer.
         */
        for (int layer = 0; layer < entry.tintedLayerCount(); ++layer) {
            children.add(
                    new BlockModelWrapper.Unbaked(
                            entry.tintedModelId(layer),
                            tintSources(entry, layer)
                    )
            );
        }

        return children;
    }

    private static List<ItemTintSource> tintSources(Entry entry, int profileLayer) {
        return List.of(
                ProfileHexColorItemTintSource.of(profileLayer, entry.profile())
        );
    }
    //?}
}