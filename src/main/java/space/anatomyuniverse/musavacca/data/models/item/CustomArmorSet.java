package space.anatomyuniverse.musavacca.data.models.item;

//? if >=1.21.2 && <1.21.4
//import net.minecraft.world.item.equipment.EquipmentModel;
//? if <1.21.4 {
/*import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
*///?} else {
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
//?}
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
//? if >=1.21.4 {
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import net.minecraft.world.item.equipment.trim.TrimMaterials;
//?}
import net.minecraft.world.level.ItemLike;
import space.anatomyuniverse.musavacca.item.CustomHelmetArmorTrims;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public final class CustomArmorSet {
    private static final ResourceLocation TRIM_PREFIX_HELMET = minecraft("trims/items/helmet_trim");
    private static final ResourceLocation TRIM_PREFIX_CHESTPLATE = minecraft("trims/items/chestplate_trim");
    private static final ResourceLocation TRIM_PREFIX_LEGGINGS = minecraft("trims/items/leggings_trim");
    private static final ResourceLocation TRIM_PREFIX_BOOTS = minecraft("trims/items/boots_trim");
    private static final ResourceLocation TRIM_TYPE = minecraft("trim_type");

    private static final List<TrimMaterialModel> TRIM_MATERIAL_MODELS = List.of(
            trim("quartz", 0.1F
                    //? if >=1.21.4
                    , TrimMaterials.QUARTZ
            ),
            trim("iron", 0.2F
                    //? if >=1.21.4
                    , TrimMaterials.IRON
            ),
            trim("netherite", 0.3F
                    //? if >=1.21.4
                    , TrimMaterials.NETHERITE
            ),
            trim("redstone", 0.4F
                    //? if >=1.21.4
                    , TrimMaterials.REDSTONE
            ),
            trim("copper", 0.5F
                    //? if >=1.21.4
                    , TrimMaterials.COPPER
            ),
            trim("gold", 0.6F
                    //? if >=1.21.4
                    , TrimMaterials.GOLD
            ),
            trim("emerald", 0.7F
                    //? if >=1.21.4
                    , TrimMaterials.EMERALD
            ),
            trim("diamond", 0.8F
                    //? if >=1.21.4
                    , TrimMaterials.DIAMOND
            ),
            trim("lapis", 0.9F
                    //? if >=1.21.4
                    , TrimMaterials.LAPIS
            ),
            trim("amethyst", 1.0F
                    //? if >=1.21.4
                    , TrimMaterials.AMETHYST
            )
            //? if >=1.21.4
            , trim("resin", 1.1F, TrimMaterials.RESIN)
    );

    private CustomArmorSet() {}

    private record TrimMaterialModel(
            String assets,
            float legacyIndex
            //? if >=1.21.4
            , ResourceKey<TrimMaterial> materialKey
    ) {}

    private static TrimMaterialModel trim(
            String assets,
            float legacyIndex
            //? if >=1.21.4
            , ResourceKey<TrimMaterial> materialKey
    ) {
        return new TrimMaterialModel(
                assets,
                legacyIndex
                //? if >=1.21.4
                , materialKey
        );
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

        //? if >=1.21.4 {
        public ResourceKey<EquipmentAsset> equipmentAssetKey() {
            return ResourceKey.create(EquipmentAssets.ROOT_ID, equipmentLocation());
        }
        //?}

        public ResourceLocation equipmentTextureLocation() {
            return ResourceLocation.parse(equipmentTexture);
        }

        public ResourceLocation customHelmetHeadModelLocation() {
            return ResourceLocation.parse(customHelmetHeadModel);
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
        return new Entry(helmet, chestplate, leggings, boots, equipmentId, equipmentTexture, null);
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

    //? if <1.21.4 {
    /*public static void generate(ItemModelProvider gen, Entry... entries) {
        if (entries == null) {
            return;
        }

        for (Entry entry : entries) {
            if (entry == null) {
                continue;
            }

            generateLegacyTrimmableItem(gen, entry.helmet(), TRIM_PREFIX_HELMET);
            generateLegacyTrimmableItem(gen, entry.chestplate(), TRIM_PREFIX_CHESTPLATE);
            generateLegacyTrimmableItem(gen, entry.leggings(), TRIM_PREFIX_LEGGINGS);
            generateLegacyTrimmableItem(gen, entry.boots(), TRIM_PREFIX_BOOTS);
        }
    }

    private static void generateLegacyTrimmableItem(
            ItemModelProvider gen,
            ItemLike itemLike,
            ResourceLocation trimPrefix
    ) {
        if (itemLike == null) {
            return;
        }

        Item item = itemLike.asItem();
        String modelName = BuiltInRegistries.ITEM.getKey(item).getPath();
        ResourceLocation textureId = itemTexture(item);

        var base = gen.withExistingParent(modelName, gen.mcLoc("item/generated"))
                .texture("layer0", textureId);

        for (TrimMaterialModel trimMaterial : TRIM_MATERIAL_MODELS) {
            String trimmedPath = modelName + "_" + trimMaterial.assets() + "_trim";
            ModelFile trimmed = gen.withExistingParent(trimmedPath, gen.mcLoc("item/generated"))
                    .texture("layer0", textureId)
                    .texture("layer1", trimPrefix.withSuffix("_" + trimMaterial.assets()));

            base.override()
                    .predicate(TRIM_TYPE, trimMaterial.legacyIndex())
                    .model(trimmed)
                    .end();
        }
    }
    *///?} else {
    public static void generate(ItemModelGenerators gen, Entry... entries) {
        if (entries == null) {
            return;
        }

        for (Entry entry : entries) {
            if (entry == null) {
                continue;
            }

            if (entry.helmet() != null) {
                if (entry.hasCustomHelmetHeadModel()) {
                    generateCustomHeadHelmet(gen, entry);
                } else {
                    generateTrimmableFlatItem(
                            gen,
                            entry.helmet(),
                            entry.equipmentAssetKey(),
                            TRIM_PREFIX_HELMET
                    );
                }
            }

            generateTrimmableFlatItem(gen, entry.chestplate(), entry.equipmentAssetKey(), TRIM_PREFIX_CHESTPLATE);
            generateTrimmableFlatItem(gen, entry.leggings(), entry.equipmentAssetKey(), TRIM_PREFIX_LEGGINGS);
            generateTrimmableFlatItem(gen, entry.boots(), entry.equipmentAssetKey(), TRIM_PREFIX_BOOTS);
        }
    }

    private static void generateCustomHeadHelmet(ItemModelGenerators gen, Entry entry) {
        Item helmet = entry.helmet().asItem();
        ItemModel.Unbaked flat = createTrimmableFlatItemModel(
                gen,
                helmet,
                entry.equipmentAssetKey(),
                TRIM_PREFIX_HELMET
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
                createTrimmableFlatItemModel(gen, item, equipmentAsset, trimPrefix)
        );
    }

    private static ItemModel.Unbaked createTrimmableFlatItemModel(
            ItemModelGenerators gen,
            Item item,
            ResourceKey<EquipmentAsset> equipmentAsset,
            ResourceLocation trimPrefix
    ) {
        ResourceLocation modelId = ModelLocationUtils.getModelLocation(item);
        ResourceLocation textureId = TextureMapping.getItemTexture(item);
        ModelTemplates.FLAT_ITEM.create(modelId, TextureMapping.layer0(textureId), gen.modelOutput);

        ItemModel.Unbaked base = ItemModelUtils.plainModel(modelId);
        List<SelectItemModel.SwitchCase<ResourceKey<TrimMaterial>>> cases =
                new ArrayList<>(TRIM_MATERIAL_MODELS.size());

        for (TrimMaterialModel trimMaterial : TRIM_MATERIAL_MODELS) {
            ResourceLocation trimmedModelId = modelId.withSuffix(
                    "_" + trimMaterial.assets() + "_trim"
            );
            ResourceLocation trimTexture = trimPrefix.withSuffix("_" + trimMaterial.assets());
            ModelTemplates.TWO_LAYERED_ITEM.create(
                    trimmedModelId,
                    TextureMapping.layered(textureId, trimTexture),
                    gen.modelOutput
            );
            cases.add(ItemModelUtils.when(
                    trimMaterial.materialKey(),
                    ItemModelUtils.plainModel(trimmedModelId)
            ));
        }

        return ItemModelUtils.select(new TrimMaterialProperty(), base, cases);
    }
    //?}

    private static ResourceLocation itemModelLocation(Item item) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
        return ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "item/" + id.getPath());
    }

    private static ResourceLocation itemTexture(Item item) {
        return itemModelLocation(item);
    }

    private static ResourceLocation minecraft(String path) {
        return ResourceLocation.fromNamespaceAndPath("minecraft", path);
    }

    public static final class Provider implements DataProvider {
        private final PackOutput.PathProvider equipmentPathProvider;
        private final Entry[] entries;

        public Provider(PackOutput output, Entry... entries) {
            this.equipmentPathProvider = output.createPathProvider(
                    PackOutput.Target.RESOURCE_PACK,
                    //? if <1.21.4
                    //"models/equipment"
                    //? if >=1.21.4
                    "equipment"
            );
            this.entries = entries;
        }

        @Override
        public CompletableFuture<?> run(CachedOutput cache) {
            //? if <1.21.2 {
            /*return CompletableFuture.completedFuture(null);
            *///?} else if <1.21.4 {
            /*Map<ResourceLocation, EquipmentModel> equipmentInfos = new HashMap<>();
            addLegacyTrimCarrier(equipmentInfos);
            addLegacyEquipmentInfos(equipmentInfos);
            return DataProvider.saveAll(
                    cache,
                    EquipmentModel.CODEC,
                    this.equipmentPathProvider,
                    equipmentInfos
            );
            *///?} else {
            Map<ResourceLocation, EquipmentClientInfo> equipmentInfos = new HashMap<>();
            addTrimCarrier(equipmentInfos);
            addEquipmentInfos(equipmentInfos);
            return DataProvider.saveAll(
                    cache,
                    EquipmentClientInfo.CODEC,
                    this.equipmentPathProvider,
                    equipmentInfos
            );
            //?}
        }

        //? if >=1.21.2 && <1.21.4 {
        /*private static void addLegacyTrimCarrier(Map<ResourceLocation, EquipmentModel> map) {
            putLegacyUnique(
                    map,
                    CustomHelmetArmorTrims.TRIM_CARRIER_ID,
                    EquipmentModel.builder()
                            .addLayers(
                                    EquipmentModel.LayerType.HUMANOID,
                                    EquipmentModel.Layer.onlyIfDyed(
                                            CustomHelmetArmorTrims.TRIM_CARRIER_ID,
                                            true
                                    )
                            )
                            .build()
            );
        }

        private void addLegacyEquipmentInfos(Map<ResourceLocation, EquipmentModel> map) {
            if (entries == null) {
                return;
            }

            for (Entry entry : entries) {
                if (entry == null) {
                    continue;
                }

                EquipmentModel.Layer layer = new EquipmentModel.Layer(
                        entry.equipmentTextureLocation(),
                        Optional.empty(),
                        false
                );
                putLegacyUnique(
                        map,
                        entry.equipmentLocation(),
                        EquipmentModel.builder()
                                .addLayers(EquipmentModel.LayerType.HUMANOID, layer)
                                .addLayers(EquipmentModel.LayerType.HUMANOID_LEGGINGS, layer)
                                .build()
                );
            }
        }

        private static void putLegacyUnique(
                Map<ResourceLocation, EquipmentModel> map,
                ResourceLocation id,
                EquipmentModel info
        ) {
            if (map.putIfAbsent(id, info) != null) {
                throw new IllegalStateException("Duplicate equipment model id: " + id);
            }
        }
        *///?} else if >=1.21.4 {
        private static void addTrimCarrier(Map<ResourceLocation, EquipmentClientInfo> map) {
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

        private void addEquipmentInfos(Map<ResourceLocation, EquipmentClientInfo> map) {
            if (entries == null) {
                return;
            }

            for (Entry entry : entries) {
                if (entry == null) {
                    continue;
                }

                EquipmentClientInfo.Layer layer = new EquipmentClientInfo.Layer(
                        entry.equipmentTextureLocation(),
                        Optional.empty(),
                        false
                );
                putUnique(
                        map,
                        entry.equipmentLocation(),
                        EquipmentClientInfo.builder()
                                .addLayers(EquipmentClientInfo.LayerType.HUMANOID, layer)
                                .addLayers(EquipmentClientInfo.LayerType.HUMANOID_LEGGINGS, layer)
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
                throw new IllegalStateException("Duplicate equipment info id: " + id);
            }
        }
        //?}

        @Override
        public String getName() {
            return "Custom Armor Sets: Musavacca";
        }
    }
}
