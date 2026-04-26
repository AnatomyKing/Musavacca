// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/data/models/item/CustomArmorSet.java
package space.anatomyuniverse.musavacca.data.models.item;

import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.SelectItemModel;
import net.minecraft.client.renderer.item.properties.select.DisplayContext;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.ItemLike;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public final class CustomArmorSet {
    private CustomArmorSet() {
    }

    public record Entry(
            ItemLike helmet,
            ItemLike chestplate,
            ItemLike leggings,
            ItemLike boots,
            String equipmentId,
            String equipmentTexture,
            String customHelmetHeadModel
    ) {
        public boolean hasCustomHelmetHeadModel() {
            return customHelmetHeadModel != null && !customHelmetHeadModel.isBlank();
        }

        public ResourceLocation equipmentLocation() {
            return ResourceLocation.parse(equipmentId);
        }

        /**
         * Example:
         * "musavacca:potassium"
         *
         * HUMANOID resolves to:
         * assets/musavacca/textures/entity/equipment/humanoid/potassium.png
         *
         * HUMANOID_LEGGINGS resolves to:
         * assets/musavacca/textures/entity/equipment/humanoid_leggings/potassium.png
         */
        public ResourceLocation equipmentTextureLocation() {
            return ResourceLocation.parse(equipmentTexture);
        }

        public ResourceLocation customHelmetHeadModelLocation() {
            return ResourceLocation.parse(customHelmetHeadModel);
        }
    }

    /**
     * Normal armor set.
     *
     * All four armor pieces use the same equipment texture id.
     */
    public static Entry of(
            ItemLike helmet,
            ItemLike chestplate,
            ItemLike leggings,
            ItemLike boots,
            String equipmentId,
            String equipmentTexture
    ) {
        return new Entry(
                helmet,
                chestplate,
                leggings,
                boots,
                equipmentId,
                equipmentTexture,
                null
        );
    }

    /**
     * Armor set with a custom 3D model only for the head display context.
     *
     * Helmet:
     * - GUI / hand / ground / item frame: flat generated item
     * - equipped on head: custom item model
     *
     * Important:
     * This class only generates the item model selector.
     * The helmet item itself must have an EQUIPPABLE component without asset_id.
     * That part is handled in ModItems#potassiumCustomHeadHelmetProperties.
     */
    public static Entry of(
            ItemLike helmet,
            ItemLike chestplate,
            ItemLike leggings,
            ItemLike boots,
            String equipmentId,
            String equipmentTexture,
            String customHelmetHeadModel
    ) {
        return new Entry(
                helmet,
                chestplate,
                leggings,
                boots,
                equipmentId,
                equipmentTexture,
                customHelmetHeadModel
        );
    }

    /**
     * Generates normal flat item models and client item definitions.
     */
    public static void generate(ItemModelGenerators gen, Entry... entries) {
        if (entries == null) return;

        for (Entry entry : entries) {
            if (entry == null) continue;

            if (entry.helmet() != null) {
                if (entry.hasCustomHelmetHeadModel()) {
                    generateCustomHeadHelmet(gen, entry);
                } else {
                    generateFlatItem(gen, entry.helmet());
                }
            }

            generateFlatItem(gen, entry.chestplate());
            generateFlatItem(gen, entry.leggings());
            generateFlatItem(gen, entry.boots());
        }
    }

    private static void generateFlatItem(ItemModelGenerators gen, ItemLike itemLike) {
        if (itemLike == null) return;

        gen.generateFlatItem(itemLike.asItem(), ModelTemplates.FLAT_ITEM);
    }

    private static void generateCustomHeadHelmet(ItemModelGenerators gen, Entry entry) {
        Item helmet = entry.helmet().asItem();

        ResourceLocation flatModel = createFlatItemModel(gen, helmet);
        ResourceLocation headModel = entry.customHelmetHeadModelLocation();

        ItemModel.Unbaked flat = ItemModelUtils.plainModel(flatModel);
        ItemModel.Unbaked head = ItemModelUtils.plainModel(headModel);

        gen.itemModelOutput.accept(
                helmet,
                ItemModelUtils.select(
                        new DisplayContext(),
                        flat,
                        List.of(
                                new SelectItemModel.SwitchCase<>(
                                        List.of(ItemDisplayContext.HEAD),
                                        head
                                )
                        )
                )
        );
    }

    private static ResourceLocation createFlatItemModel(ItemModelGenerators gen, Item item) {
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(item);

        ResourceLocation modelId = ResourceLocation.fromNamespaceAndPath(
                itemId.getNamespace(),
                "item/" + itemId.getPath()
        );

        ResourceLocation textureId = ResourceLocation.fromNamespaceAndPath(
                itemId.getNamespace(),
                "item/" + itemId.getPath()
        );

        ModelTemplates.FLAT_ITEM.create(
                modelId,
                new TextureMapping().put(TextureSlot.LAYER0, textureId),
                gen.modelOutput
        );

        return modelId;
    }

    /**
     * Generates armor equipment JSON:
     *
     * assets/<modid>/equipment/<equipment>.json
     *
     * This intentionally does NOT generate:
     *
     * assets/<modid>/equipment/<equipment>_helmet.json
     *
     * because custom head helmets should not use an equipment asset.
     * They should render as item models on the head slot.
     */
    public static final class Provider implements DataProvider {
        private final PackOutput.PathProvider equipmentPathProvider;
        private final Entry[] entries;

        public Provider(PackOutput output, Entry... entries) {
            this.equipmentPathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "equipment");
            this.entries = entries;
        }

        @Override
        public CompletableFuture<?> run(CachedOutput cache) {
            Map<ResourceLocation, EquipmentClientInfo> equipmentInfos = new HashMap<>();
            addEquipmentInfos(equipmentInfos);

            return DataProvider.saveAll(
                    cache,
                    EquipmentClientInfo.CODEC,
                    this.equipmentPathProvider,
                    equipmentInfos
            );
        }

        private void addEquipmentInfos(Map<ResourceLocation, EquipmentClientInfo> map) {
            if (entries == null) return;

            for (Entry entry : entries) {
                if (entry == null) continue;

                /*
                 * This equipment asset is still used by chestplate, leggings, and boots.
                 *
                 * The custom helmet does NOT use this asset, because ModItems overwrites
                 * its EQUIPPABLE component to have no asset_id.
                 */
                putUnique(
                        map,
                        entry.equipmentLocation(),
                        EquipmentClientInfo.builder()
                                .addLayers(
                                        EquipmentClientInfo.LayerType.HUMANOID,
                                        new EquipmentClientInfo.Layer(
                                                entry.equipmentTextureLocation(),
                                                Optional.empty(),
                                                false
                                        )
                                )
                                .addLayers(
                                        EquipmentClientInfo.LayerType.HUMANOID_LEGGINGS,
                                        new EquipmentClientInfo.Layer(
                                                entry.equipmentTextureLocation(),
                                                Optional.empty(),
                                                false
                                        )
                                )
                                .build()
                );
            }
        }

        private static void putUnique(
                Map<ResourceLocation, EquipmentClientInfo> map,
                ResourceLocation id,
                EquipmentClientInfo info
        ) {
            if (map.putIfAbsent(id, info) != null) {
                throw new IllegalStateException("Tried to register equipment client info twice for id: " + id);
            }
        }

        @Override
        public String getName() {
            return "Custom Armor Sets: Musavacca";
        }
    }
}