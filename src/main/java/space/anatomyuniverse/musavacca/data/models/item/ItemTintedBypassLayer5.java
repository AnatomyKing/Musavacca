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

public final class ItemTintedBypassLayer5 {
    private ItemTintedBypassLayer5() {}

    /*
     * This helper bypasses the vanilla item/generated layer0-layer4 cap.
     *
     * How:
     * - base texture becomes its own generated item model
     * - every tinted layer becomes its own generated item model
     * - final item is a CompositeModel that draws them stacked
     *
     * This is for items with many layers, like SIM_CARD.
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

    /*
     * Root texture layout:
     *
     * textures/item/example.png
     * textures/item/example_0.png
     * textures/item/example_1.png
     * textures/item/example_2.png
     * ...
     */
    public static Entry root(ItemLike item, PearlFireTintProfiles.Profile profile) {
        String stem = ModelUtil.pathOf(item);
        return new Entry(item, stem, stem, stem, profile);
    }

    /*
     * Folder texture layout:
     *
     * textures/item/example.png
     * textures/item/example/example_0.png
     * textures/item/example/example_1.png
     * textures/item/example/example_2.png
     * ...
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
         * Base model:
         * normal generated model, no tint.
         */
        createGeneratedLayerModel(
                items,
                entry.baseModelId(),
                entry.baseTexture()
        );

        /*
         * Every tinted visual layer gets its own generated model.
         * Each child model only uses layer0, so every child avoids the vanilla layer cap.
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
         * draw base first, then every tinted child model on top.
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

        children.add(
                new BlockModelWrapper.Unbaked(
                        entry.baseModelId(),
                        List.of()
                )
        );

        for (int layer = 0; layer < entry.tintedLayerCount(); ++layer) {
            children.add(
                    new BlockModelWrapper.Unbaked(
                            entry.tintedModelId(layer),
                            tintSources(entry, layer)
                    )
            );
        }

        return List.copyOf(children);
    }

    private static List<ItemTintSource> tintSources(Entry entry, int profileLayer) {
        return List.of(
                ProfileHexColorItemTintSource.of(
                        profileLayer,
                        entry.profile()
                )
        );
    }
    //?}
}