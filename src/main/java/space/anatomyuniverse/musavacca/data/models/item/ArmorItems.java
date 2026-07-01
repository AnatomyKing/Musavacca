// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/data/models/item/ArmorItems.java
package space.anatomyuniverse.musavacca.data.models.item;

import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.renderer.item.BlockModelWrapper;
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
import space.anatomyuniverse.musavacca.data.models.engine.item.EngineItemEntry;
import space.anatomyuniverse.musavacca.data.models.engine.item.EngineItemModels;
import space.anatomyuniverse.musavacca.data.models.unified.BiomeTint;
import space.anatomyuniverse.musavacca.data.models.unified.HexColorTint;
import space.anatomyuniverse.musavacca.data.models.unified.ItemTint;
import space.anatomyuniverse.musavacca.data.models.unified.PearlTint;
import space.anatomyuniverse.musavacca.tint.TintColorUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public final class ArmorItems {
    private ArmorItems() {}

    public record Entry(
            ItemLike helmet,
            ItemLike chestplate,
            ItemLike leggings,
            ItemLike boots,
            String equipmentId,
            String equipmentTexture,
            String customHelmetHeadModel,
            ItemTint itemTint,
            boolean wornArmorTint,
            int wornArmorFallbackTint
    ) {
        public boolean hasCustomHelmetHeadModel() {
            return customHelmetHeadModel != null && !customHelmetHeadModel.isBlank();
        }

        public boolean hasItemTint() {
            return itemTint != null;
        }

        public ResourceLocation equipmentLocation() {
            return ResourceLocation.parse(equipmentId);
        }

        public ResourceLocation equipmentTextureLocation() {
            return ResourceLocation.parse(equipmentTexture);
        }

        public ResourceLocation customHelmetHeadModelLocation() {
            return ResourceLocation.parse(customHelmetHeadModel);
        }

        public Entry itemTint(ItemTint itemTint) {
            return new Entry(
                    helmet,
                    chestplate,
                    leggings,
                    boots,
                    equipmentId,
                    equipmentTexture,
                    customHelmetHeadModel,
                    itemTint,
                    itemTint != null,
                    wornArmorFallbackTint
            );
        }

        public Entry biomeTint(BiomeTint biomeTint) {
            return itemTint(biomeTint);
        }

        public Entry hexColorTint(HexColorTint hexColorTint) {
            return itemTint(hexColorTint);
        }

        public Entry pearlTint(PearlTint pearlTint) {
            return itemTint(pearlTint);
        }

        /**
         * Enables or disables vanilla equipment-layer dyeing for the worn armor body.
         *
         * This is separate from itemTint(...): item tint affects inventory/hand item models,
         * while wornArmorTint affects the equipment texture rendered on the entity body.
         */
        public Entry wornArmorTint(boolean wornArmorTint) {
            return new Entry(
                    helmet,
                    chestplate,
                    leggings,
                    boots,
                    equipmentId,
                    equipmentTexture,
                    customHelmetHeadModel,
                    itemTint,
                    wornArmorTint,
                    wornArmorFallbackTint
            );
        }

        /**
         * Default color used by the worn armor layer when the stack has no vanilla DYED_COLOR component yet.
         */
        public Entry wornArmorFallbackTint(int rgb) {
            return new Entry(
                    helmet,
                    chestplate,
                    leggings,
                    boots,
                    equipmentId,
                    equipmentTexture,
                    customHelmetHeadModel,
                    itemTint,
                    wornArmorTint,
                    TintColorUtil.rgb(rgb)
            );
        }
    }

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
                null,
                null,
                false,
                TintColorUtil.HARD_HEX
        );
    }

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
                customHelmetHeadModel,
                null,
                false,
                TintColorUtil.HARD_HEX
        );
    }

    public static void generate(ItemModelGenerators gen, Entry... entries) {
        if (entries == null) return;

        for (Entry entry : entries) {
            if (entry == null) continue;

            if (entry.helmet() != null) {
                if (entry.hasCustomHelmetHeadModel()) generateCustomHeadHelmet(gen, entry);
                else generateFlatItem(gen, entry.helmet(), entry.itemTint());
            }

            generateFlatItem(gen, entry.chestplate(), entry.itemTint());
            generateFlatItem(gen, entry.leggings(), entry.itemTint());
            generateFlatItem(gen, entry.boots(), entry.itemTint());
        }
    }

    private static void generateFlatItem(ItemModelGenerators gen, ItemLike itemLike, ItemTint itemTint) {
        if (itemLike == null) return;
        EngineItemModels.generate(gen, EngineItemEntry.flat(itemLike).itemTint(itemTint));
    }

    private static void generateCustomHeadHelmet(ItemModelGenerators gen, Entry entry) {
        Item helmet = entry.helmet().asItem();
        ResourceLocation flatModel = createFlatItemModel(gen, helmet);

        ItemModel.Unbaked flat = new BlockModelWrapper.Unbaked(
                flatModel,
                EngineItemModels.tintSources(EngineItemEntry.flat(helmet).itemTint(entry.itemTint()))
        );

        ItemModel.Unbaked head = ItemModelUtils.plainModel(entry.customHelmetHeadModelLocation());

        gen.itemModelOutput.accept(
                helmet,
                ItemModelUtils.select(
                        new DisplayContext(),
                        flat,
                        List.of(new SelectItemModel.SwitchCase<>(List.of(ItemDisplayContext.HEAD), head))
                )
        );
    }

    private static ResourceLocation createFlatItemModel(ItemModelGenerators gen, Item item) {
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(item);
        ResourceLocation modelId = ResourceLocation.fromNamespaceAndPath(itemId.getNamespace(), "item/" + itemId.getPath());
        ResourceLocation textureId = ResourceLocation.fromNamespaceAndPath(itemId.getNamespace(), "item/" + itemId.getPath());
        ModelTemplates.FLAT_ITEM.create(modelId, new TextureMapping().put(TextureSlot.LAYER0, textureId), gen.modelOutput);
        return modelId;
    }

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
            return DataProvider.saveAll(cache, EquipmentClientInfo.CODEC, this.equipmentPathProvider, equipmentInfos);
        }

        private void addEquipmentInfos(Map<ResourceLocation, EquipmentClientInfo> map) {
            if (entries == null) return;

            for (Entry entry : entries) {
                if (entry == null) continue;

                putUnique(
                        map,
                        entry.equipmentLocation(),
                        EquipmentClientInfo.builder()
                                .addLayers(EquipmentClientInfo.LayerType.HUMANOID, wornLayer(entry))
                                .addLayers(EquipmentClientInfo.LayerType.HUMANOID_LEGGINGS, wornLayer(entry))
                                .build()
                );
            }
        }

        private static EquipmentClientInfo.Layer wornLayer(Entry entry) {
            return new EquipmentClientInfo.Layer(
                    entry.equipmentTextureLocation(),
                    dyeable(entry),
                    false
            );
        }

        private static Optional<EquipmentClientInfo.Dyeable> dyeable(Entry entry) {
            if (entry == null || !entry.wornArmorTint()) {
                return Optional.empty();
            }

            return Optional.of(new EquipmentClientInfo.Dyeable(
                    Optional.of(TintColorUtil.rgb(entry.wornArmorFallbackTint()))
            ));
        }

        private static void putUnique(Map<ResourceLocation, EquipmentClientInfo> map, ResourceLocation id, EquipmentClientInfo info) {
            if (map.putIfAbsent(id, info) != null) throw new IllegalStateException("Duplicate equipment client info: " + id);
        }

        @Override
        public String getName() {
            return "Armor Items: Musavacca";
        }
    }
}
