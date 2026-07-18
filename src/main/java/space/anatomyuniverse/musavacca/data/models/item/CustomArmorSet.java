package space.anatomyuniverse.musavacca.data.models.item;

import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.SelectItemModel;
import net.minecraft.client.renderer.item.properties.select.DisplayContext;
import net.minecraft.client.renderer.item.properties.select.TrimMaterialProperty;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;
import net.minecraft.world.item.equipment.trim.MaterialAssetGroup;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import net.minecraft.world.item.equipment.trim.TrimMaterials;
import net.minecraft.world.level.ItemLike;
import space.anatomyuniverse.musavacca.item.CustomHelmetArmorTrims;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public final class CustomArmorSet {
    private static final List<TrimMaterialModel>
            TRIM_MATERIAL_MODELS = List.of(
            new TrimMaterialModel(
                    MaterialAssetGroup.QUARTZ,
                    TrimMaterials.QUARTZ
            ),
            new TrimMaterialModel(
                    MaterialAssetGroup.IRON,
                    TrimMaterials.IRON
            ),
            new TrimMaterialModel(
                    MaterialAssetGroup.NETHERITE,
                    TrimMaterials.NETHERITE
            ),
            new TrimMaterialModel(
                    MaterialAssetGroup.REDSTONE,
                    TrimMaterials.REDSTONE
            ),
            new TrimMaterialModel(
                    MaterialAssetGroup.COPPER,
                    TrimMaterials.COPPER
            ),
            new TrimMaterialModel(
                    MaterialAssetGroup.GOLD,
                    TrimMaterials.GOLD
            ),
            new TrimMaterialModel(
                    MaterialAssetGroup.EMERALD,
                    TrimMaterials.EMERALD
            ),
            new TrimMaterialModel(
                    MaterialAssetGroup.DIAMOND,
                    TrimMaterials.DIAMOND
            ),
            new TrimMaterialModel(
                    MaterialAssetGroup.LAPIS,
                    TrimMaterials.LAPIS
            ),
            new TrimMaterialModel(
                    MaterialAssetGroup.AMETHYST,
                    TrimMaterials.AMETHYST
            ),
            new TrimMaterialModel(
                    MaterialAssetGroup.RESIN,
                    TrimMaterials.RESIN
            )
    );

    private CustomArmorSet() {
    }

    private record TrimMaterialModel(
            MaterialAssetGroup assets,
            ResourceKey<TrimMaterial> materialKey
    ) {
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
            return customHelmetHeadModel != null
                    && !customHelmetHeadModel.isBlank();
        }

        public ResourceLocation equipmentLocation() {
            return ResourceLocation.parse(equipmentId);
        }

        public ResourceKey<EquipmentAsset>
        equipmentAssetKey() {
            return ResourceKey.create(
                    EquipmentAssets.ROOT_ID,
                    equipmentLocation()
            );
        }

        public ResourceLocation
        equipmentTextureLocation() {
            return ResourceLocation.parse(
                    equipmentTexture
            );
        }

        public ResourceLocation
        customHelmetHeadModelLocation() {
            return ResourceLocation.parse(
                    customHelmetHeadModel
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
                null
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
                customHelmetHeadModel
        );
    }

    public static void generate(
            ItemModelGenerators gen,
            Entry... entries
    ) {
        if (entries == null) {
            return;
        }

        for (Entry entry : entries) {
            if (entry == null) {
                continue;
            }

            if (entry.helmet() != null) {
                if (entry.hasCustomHelmetHeadModel()) {
                    generateCustomHeadHelmet(
                            gen,
                            entry
                    );
                } else {
                    generateTrimmableFlatItem(
                            gen,
                            entry.helmet(),
                            entry.equipmentAssetKey(),
                            ItemModelGenerators
                                    .TRIM_PREFIX_HELMET
                    );
                }
            }

            generateTrimmableFlatItem(
                    gen,
                    entry.chestplate(),
                    entry.equipmentAssetKey(),
                    ItemModelGenerators
                            .TRIM_PREFIX_CHESTPLATE
            );

            generateTrimmableFlatItem(
                    gen,
                    entry.leggings(),
                    entry.equipmentAssetKey(),
                    ItemModelGenerators
                            .TRIM_PREFIX_LEGGINGS
            );

            generateTrimmableFlatItem(
                    gen,
                    entry.boots(),
                    entry.equipmentAssetKey(),
                    ItemModelGenerators
                            .TRIM_PREFIX_BOOTS
            );
        }
    }

    private static void generateCustomHeadHelmet(
            ItemModelGenerators gen,
            Entry entry
    ) {
        Item helmet = entry.helmet().asItem();

        ItemModel.Unbaked flat =
                createTrimmableFlatItemModel(
                        gen,
                        helmet,
                        entry.equipmentAssetKey(),
                        ItemModelGenerators
                                .TRIM_PREFIX_HELMET
                );

        ItemModel.Unbaked head =
                ItemModelUtils.plainModel(
                        entry.customHelmetHeadModelLocation()
                );

        gen.itemModelOutput.accept(
                helmet,
                ItemModelUtils.select(
                        new DisplayContext(),
                        flat,
                        List.of(
                                new SelectItemModel.SwitchCase<>(
                                        List.of(
                                                ItemDisplayContext.HEAD
                                        ),
                                        head
                                )
                        )
                )
        );
    }

    private static void generateTrimmableFlatItem(
            ItemModelGenerators gen,
            ItemLike itemLike,
            ResourceKey<EquipmentAsset> equipmentAsset,
            ResourceLocation trimPrefix
    ) {
        if (itemLike == null) {
            return;
        }

        Item item = itemLike.asItem();

        gen.itemModelOutput.accept(
                item,
                createTrimmableFlatItemModel(
                        gen,
                        item,
                        equipmentAsset,
                        trimPrefix
                )
        );
    }

    private static ItemModel.Unbaked
    createTrimmableFlatItemModel(
            ItemModelGenerators gen,
            Item item,
            ResourceKey<EquipmentAsset> equipmentAsset,
            ResourceLocation trimPrefix
    ) {
        ResourceLocation modelId =
                ModelLocationUtils.getModelLocation(item);

        ResourceLocation textureId =
                TextureMapping.getItemTexture(item);

        ModelTemplates.FLAT_ITEM.create(
                modelId,
                TextureMapping.layer0(textureId),
                gen.modelOutput
        );

        ItemModel.Unbaked base =
                ItemModelUtils.plainModel(modelId);

        List<SelectItemModel.SwitchCase<
                ResourceKey<TrimMaterial>>> cases =
                new ArrayList<>(
                        TRIM_MATERIAL_MODELS.size()
                );

        for (
                TrimMaterialModel trimMaterial
                : TRIM_MATERIAL_MODELS
        ) {
            ResourceLocation trimmedModelId =
                    modelId.withSuffix(
                            "_"
                                    + trimMaterial
                                    .assets()
                                    .base()
                                    .suffix()
                                    + "_trim"
                    );

            ResourceLocation trimTexture =
                    trimPrefix.withSuffix(
                            "_"
                                    + trimMaterial
                                    .assets()
                                    .assetId(
                                            equipmentAsset
                                    )
                                    .suffix()
                    );

            ModelTemplates.TWO_LAYERED_ITEM.create(
                    trimmedModelId,
                    TextureMapping.layered(
                            textureId,
                            trimTexture
                    ),
                    gen.modelOutput
            );

            cases.add(
                    ItemModelUtils.when(
                            trimMaterial.materialKey(),
                            ItemModelUtils.plainModel(
                                    trimmedModelId
                            )
                    )
            );
        }

        return ItemModelUtils.select(
                new TrimMaterialProperty(),
                base,
                cases
        );
    }

    public static final class Provider
            implements DataProvider {

        private final PackOutput.PathProvider
                equipmentPathProvider;

        private final Entry[] entries;

        public Provider(
                PackOutput output,
                Entry... entries
        ) {
            this.equipmentPathProvider =
                    output.createPathProvider(
                            PackOutput.Target.RESOURCE_PACK,
                            "equipment"
                    );

            this.entries = entries;
        }

        @Override
        public CompletableFuture<?> run(
                CachedOutput cache
        ) {
            Map<ResourceLocation, EquipmentClientInfo>
                    equipmentInfos = new HashMap<>();

            addTrimCarrier(equipmentInfos);
            addEquipmentInfos(equipmentInfos);

            return DataProvider.saveAll(
                    cache,
                    EquipmentClientInfo.CODEC,
                    this.equipmentPathProvider,
                    equipmentInfos
            );
        }

        private static void addTrimCarrier(
                Map<ResourceLocation, EquipmentClientInfo> map
        ) {
            putUnique(
                    map,
                    CustomHelmetArmorTrims.TRIM_CARRIER_ID,
                    EquipmentClientInfo.builder()
                            .addLayers(
                                    EquipmentClientInfo.LayerType.HUMANOID,
                                    EquipmentClientInfo.Layer.onlyIfDyed(
                                            CustomHelmetArmorTrims.TRIM_CARRIER_ID,
                                            true
                                    )
                            )
                            .build()
            );
        }

        private void addEquipmentInfos(
                Map<ResourceLocation,
                        EquipmentClientInfo> map
        ) {
            if (entries == null) {
                return;
            }

            for (Entry entry : entries) {
                if (entry == null) {
                    continue;
                }

                putUnique(
                        map,
                        entry.equipmentLocation(),
                        EquipmentClientInfo.builder()
                                .addLayers(
                                        EquipmentClientInfo
                                                .LayerType
                                                .HUMANOID,
                                        new EquipmentClientInfo.Layer(
                                                entry.equipmentTextureLocation(),
                                                Optional.empty(),
                                                false
                                        )
                                )
                                .addLayers(
                                        EquipmentClientInfo
                                                .LayerType
                                                .HUMANOID_LEGGINGS,
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
                Map<ResourceLocation,
                        EquipmentClientInfo> map,
                ResourceLocation id,
                EquipmentClientInfo info
        ) {
            if (map.putIfAbsent(id, info) != null) {
                throw new IllegalStateException(
                        "Tried to register equipment "
                                + "client info twice for id: "
                                + id
                );
            }
        }

        @Override
        public String getName() {
            return "Custom Armor Sets: Musavacca";
        }
    }
}