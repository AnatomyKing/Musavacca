package space.anatomyuniverse.musavacca.data.models.item;

import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.renderer.item.BlockModelWrapper;
import net.minecraft.client.renderer.item.CompositeModel;
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
import space.anatomyuniverse.musavacca.tint.PearlFireTintProfiles;
import space.anatomyuniverse.musavacca.tint.ProfileHexColorItemTintSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public final class CustomArmorSetTintedLayers {
    private static final TextureSlot CUSTOM_HELMET_TEXTURE =
            TextureSlot.create("1");

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

    private static final Map<ResourceLocation, Entry>
            EQUIPMENT_TINT_ENTRIES = new ConcurrentHashMap<>();

    private CustomArmorSetTintedLayers() {
    }

    public enum LayerLayout {
        WITH_UNTINTED_BASE,
        FULLY_TINTED
    }

    public enum TextureLayout {
        ROOT,
        FOLDER
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
            String equipmentTextureStem,
            String customHelmetHeadModel,
            PearlFireTintProfiles.Profile itemProfile,
            PearlFireTintProfiles.Profile helmetProfile,
            PearlFireTintProfiles.Profile equipmentProfile,
            LayerLayout itemLayout,
            LayerLayout helmetLayout,
            LayerLayout equipmentLayout,
            TextureLayout textureLayout
    ) {
        public Entry {
            if (helmet == null) {
                throw new IllegalArgumentException(
                        "helmet must not be null"
                );
            }

            if (chestplate == null) {
                throw new IllegalArgumentException(
                        "chestplate must not be null"
                );
            }

            if (leggings == null) {
                throw new IllegalArgumentException(
                        "leggings must not be null"
                );
            }

            if (boots == null) {
                throw new IllegalArgumentException(
                        "boots must not be null"
                );
            }

            if (equipmentId == null || equipmentId.isBlank()) {
                throw new IllegalArgumentException(
                        "equipmentId must not be blank"
                );
            }

            if (
                    equipmentTextureStem == null
                            || equipmentTextureStem.isBlank()
            ) {
                throw new IllegalArgumentException(
                        "equipmentTextureStem must not be blank"
                );
            }

            if (
                    customHelmetHeadModel == null
                            || customHelmetHeadModel.isBlank()
            ) {
                throw new IllegalArgumentException(
                        "customHelmetHeadModel must not be blank"
                );
            }

            if (itemProfile == null) {
                throw new IllegalArgumentException(
                        "itemProfile must not be null"
                );
            }

            if (helmetProfile == null) {
                throw new IllegalArgumentException(
                        "helmetProfile must not be null"
                );
            }

            if (equipmentProfile == null) {
                throw new IllegalArgumentException(
                        "equipmentProfile must not be null"
                );
            }

            if (itemLayout == null) {
                throw new IllegalArgumentException(
                        "itemLayout must not be null"
                );
            }

            if (helmetLayout == null) {
                throw new IllegalArgumentException(
                        "helmetLayout must not be null"
                );
            }

            if (equipmentLayout == null) {
                throw new IllegalArgumentException(
                        "equipmentLayout must not be null"
                );
            }

            if (textureLayout == null) {
                throw new IllegalArgumentException(
                        "textureLayout must not be null"
                );
            }
        }

        public ResourceLocation equipmentLocation() {
            return ResourceLocation.parse(equipmentId);
        }

        public ResourceKey<EquipmentAsset> equipmentAssetKey() {
            return ResourceKey.create(
                    EquipmentAssets.ROOT_ID,
                    equipmentLocation()
            );
        }

        public ResourceLocation customHelmetHeadModelLocation() {
            return ResourceLocation.parse(
                    customHelmetHeadModel
            );
        }

        public int itemLayerCount() {
            return totalLayerCount(
                    itemProfile,
                    itemLayout
            );
        }

        public int helmetLayerCount() {
            return totalLayerCount(
                    helmetProfile,
                    helmetLayout
            );
        }

        public int equipmentLayerCount() {
            return totalLayerCount(
                    equipmentProfile,
                    equipmentLayout
            );
        }

        public boolean isUntintedItemLayer(
                int modelLayer
        ) {
            validateModelLayer(
                    modelLayer,
                    itemLayerCount(),
                    "item"
            );

            return isUntintedBaseLayer(
                    itemLayout,
                    modelLayer
            );
        }

        public boolean isUntintedHelmetLayer(
                int modelLayer
        ) {
            validateModelLayer(
                    modelLayer,
                    helmetLayerCount(),
                    "helmet"
            );

            return isUntintedBaseLayer(
                    helmetLayout,
                    modelLayer
            );
        }

        public boolean isUntintedEquipmentLayer(
                int modelLayer
        ) {
            validateModelLayer(
                    modelLayer,
                    equipmentLayerCount(),
                    "equipment"
            );

            return isUntintedBaseLayer(
                    equipmentLayout,
                    modelLayer
            );
        }

        public int itemProfileLayer(
                int modelLayer
        ) {
            validateModelLayer(
                    modelLayer,
                    itemLayerCount(),
                    "item"
            );

            return profileLayer(
                    itemLayout,
                    modelLayer,
                    "item"
            );
        }

        public int helmetProfileLayer(
                int modelLayer
        ) {
            validateModelLayer(
                    modelLayer,
                    helmetLayerCount(),
                    "helmet"
            );

            return profileLayer(
                    helmetLayout,
                    modelLayer,
                    "helmet"
            );
        }

        public int equipmentProfileLayer(
                int modelLayer
        ) {
            validateModelLayer(
                    modelLayer,
                    equipmentLayerCount(),
                    "equipment"
            );

            return profileLayer(
                    equipmentLayout,
                    modelLayer,
                    "equipment"
            );
        }

        public ResourceLocation itemTexture(
                ItemLike itemLike,
                int modelLayer
        ) {
            validateModelLayer(
                    modelLayer,
                    itemLayerCount(),
                    "item"
            );

            ResourceLocation itemId =
                    ModelLocationUtils.getModelLocation(
                            itemLike.asItem()
                    );

            String basePath = itemId.getPath();
            String texturePath = textureStem(
                    basePath,
                    textureLayout
            );

            return ResourceLocation.fromNamespaceAndPath(
                    itemId.getNamespace(),
                    texturePath + layerSuffix(
                            itemLayout,
                            modelLayer
                    )
            );
        }

        public ResourceLocation itemLayerModel(
                ItemLike itemLike,
                int modelLayer
        ) {
            validateModelLayer(
                    modelLayer,
                    itemLayerCount(),
                    "item"
            );

            return ModelLocationUtils.getModelLocation(
                    itemLike.asItem()
            ).withSuffix(
                    "_layer_" + modelLayer
            );
        }

        public ResourceLocation customHelmetTexture(
                int modelLayer
        ) {
            validateModelLayer(
                    modelLayer,
                    helmetLayerCount(),
                    "helmet"
            );

            ResourceLocation model =
                    customHelmetHeadModelLocation();

            String texturePath = textureStem(
                    model.getPath(),
                    textureLayout
            );

            return ResourceLocation.fromNamespaceAndPath(
                    model.getNamespace(),
                    texturePath + layerSuffix(
                            helmetLayout,
                            modelLayer
                    )
            );
        }

        public ResourceLocation customHelmetLayerModel(
                int modelLayer
        ) {
            validateModelLayer(
                    modelLayer,
                    helmetLayerCount(),
                    "helmet"
            );

            return customHelmetHeadModelLocation()
                    .withSuffix(
                            "_layer_" + modelLayer
                    );
        }

        public ResourceLocation equipmentTexture(
                int modelLayer
        ) {
            validateModelLayer(
                    modelLayer,
                    equipmentLayerCount(),
                    "equipment"
            );

            ResourceLocation stem = ResourceLocation.parse(
                    equipmentTextureStem
            );

            return stem.withSuffix(
                    layerSuffix(
                            equipmentLayout,
                            modelLayer
                    )
            );
        }

        public int equipmentModelLayer(
                ResourceLocation texture
        ) {
            for (
                    int modelLayer = 0;
                    modelLayer < equipmentLayerCount();
                    ++modelLayer
            ) {
                if (
                        equipmentTexture(modelLayer)
                                .equals(texture)
                ) {
                    return modelLayer;
                }
            }

            return -1;
        }
    }

    public static Entry root(
            ItemLike helmet,
            ItemLike chestplate,
            ItemLike leggings,
            ItemLike boots,
            String equipmentId,
            String equipmentTextureStem,
            String customHelmetHeadModel,
            PearlFireTintProfiles.Profile itemProfile,
            PearlFireTintProfiles.Profile helmetProfile,
            PearlFireTintProfiles.Profile equipmentProfile
    ) {
        return of(
                helmet,
                chestplate,
                leggings,
                boots,
                equipmentId,
                equipmentTextureStem,
                customHelmetHeadModel,
                itemProfile,
                helmetProfile,
                equipmentProfile,
                LayerLayout.WITH_UNTINTED_BASE,
                LayerLayout.WITH_UNTINTED_BASE,
                LayerLayout.WITH_UNTINTED_BASE,
                TextureLayout.ROOT
        );
    }

    public static Entry fullyTintedRoot(
            ItemLike helmet,
            ItemLike chestplate,
            ItemLike leggings,
            ItemLike boots,
            String equipmentId,
            String equipmentTextureStem,
            String customHelmetHeadModel,
            PearlFireTintProfiles.Profile itemProfile,
            PearlFireTintProfiles.Profile helmetProfile,
            PearlFireTintProfiles.Profile equipmentProfile
    ) {
        return of(
                helmet,
                chestplate,
                leggings,
                boots,
                equipmentId,
                equipmentTextureStem,
                customHelmetHeadModel,
                itemProfile,
                helmetProfile,
                equipmentProfile,
                LayerLayout.FULLY_TINTED,
                LayerLayout.FULLY_TINTED,
                LayerLayout.FULLY_TINTED,
                TextureLayout.ROOT
        );
    }

    public static Entry folder(
            ItemLike helmet,
            ItemLike chestplate,
            ItemLike leggings,
            ItemLike boots,
            String equipmentId,
            String equipmentTextureStem,
            String customHelmetHeadModel,
            PearlFireTintProfiles.Profile itemProfile,
            PearlFireTintProfiles.Profile helmetProfile,
            PearlFireTintProfiles.Profile equipmentProfile
    ) {
        return of(
                helmet,
                chestplate,
                leggings,
                boots,
                equipmentId,
                equipmentTextureStem,
                customHelmetHeadModel,
                itemProfile,
                helmetProfile,
                equipmentProfile,
                LayerLayout.WITH_UNTINTED_BASE,
                LayerLayout.WITH_UNTINTED_BASE,
                LayerLayout.WITH_UNTINTED_BASE,
                TextureLayout.FOLDER
        );
    }

    public static Entry fullyTintedFolder(
            ItemLike helmet,
            ItemLike chestplate,
            ItemLike leggings,
            ItemLike boots,
            String equipmentId,
            String equipmentTextureStem,
            String customHelmetHeadModel,
            PearlFireTintProfiles.Profile itemProfile,
            PearlFireTintProfiles.Profile helmetProfile,
            PearlFireTintProfiles.Profile equipmentProfile
    ) {
        return of(
                helmet,
                chestplate,
                leggings,
                boots,
                equipmentId,
                equipmentTextureStem,
                customHelmetHeadModel,
                itemProfile,
                helmetProfile,
                equipmentProfile,
                LayerLayout.FULLY_TINTED,
                LayerLayout.FULLY_TINTED,
                LayerLayout.FULLY_TINTED,
                TextureLayout.FOLDER
        );
    }

    public static Entry of(
            ItemLike helmet,
            ItemLike chestplate,
            ItemLike leggings,
            ItemLike boots,
            String equipmentId,
            String equipmentTextureStem,
            String customHelmetHeadModel,
            PearlFireTintProfiles.Profile itemProfile,
            PearlFireTintProfiles.Profile helmetProfile,
            PearlFireTintProfiles.Profile equipmentProfile,
            LayerLayout itemLayout,
            LayerLayout helmetLayout,
            LayerLayout equipmentLayout,
            TextureLayout textureLayout
    ) {
        return new Entry(
                helmet,
                chestplate,
                leggings,
                boots,
                equipmentId,
                equipmentTextureStem,
                customHelmetHeadModel,
                itemProfile,
                helmetProfile,
                equipmentProfile,
                itemLayout,
                helmetLayout,
                equipmentLayout,
                textureLayout
        );
    }

    public static void registerEquipmentTints(
            Entry... entries
    ) {
        if (entries == null) {
            return;
        }

        for (Entry entry : entries) {
            if (entry != null) {
                EQUIPMENT_TINT_ENTRIES.put(
                        entry.equipmentLocation(),
                        entry
                );
            }
        }
    }

    public static Entry equipmentTintEntry(
            ResourceKey<EquipmentAsset> equipmentAsset
    ) {
        if (equipmentAsset == null) {
            return null;
        }

        return EQUIPMENT_TINT_ENTRIES.get(
                equipmentAsset.location()
        );
    }

    public static void generate(
            ItemModelGenerators items,
            Entry... entries
    ) {
        if (entries == null) {
            return;
        }

        for (Entry entry : entries) {
            if (entry == null) {
                continue;
            }

            generateHelmet(
                    items,
                    entry
            );

            generateArmorItem(
                    items,
                    entry,
                    entry.chestplate(),
                    ItemModelGenerators.TRIM_PREFIX_CHESTPLATE
            );

            generateArmorItem(
                    items,
                    entry,
                    entry.leggings(),
                    ItemModelGenerators.TRIM_PREFIX_LEGGINGS
            );

            generateArmorItem(
                    items,
                    entry,
                    entry.boots(),
                    ItemModelGenerators.TRIM_PREFIX_BOOTS
            );
        }
    }

    private static void generateHelmet(
            ItemModelGenerators items,
            Entry entry
    ) {
        Item helmet = entry.helmet().asItem();

        ItemModel.Unbaked flat =
                createTrimmableItemModel(
                        items,
                        entry,
                        entry.helmet(),
                        ItemModelGenerators.TRIM_PREFIX_HELMET
                );

        ItemModel.Unbaked head =
                createCustomHelmetModel(
                        items,
                        entry
                );

        items.itemModelOutput.accept(
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

    private static void generateArmorItem(
            ItemModelGenerators items,
            Entry entry,
            ItemLike itemLike,
            ResourceLocation trimPrefix
    ) {
        items.itemModelOutput.accept(
                itemLike.asItem(),
                createTrimmableItemModel(
                        items,
                        entry,
                        itemLike,
                        trimPrefix
                )
        );
    }

    private static ItemModel.Unbaked
    createTrimmableItemModel(
            ItemModelGenerators items,
            Entry entry,
            ItemLike itemLike,
            ResourceLocation trimPrefix
    ) {
        List<ItemModel.Unbaked> baseChildren =
                createItemLayerModels(
                        items,
                        entry,
                        itemLike
                );

        ItemModel.Unbaked base =
                new CompositeModel.Unbaked(
                        baseChildren
                );

        List<SelectItemModel.SwitchCase<
                ResourceKey<TrimMaterial>>> cases =
                new ArrayList<>(
                        TRIM_MATERIAL_MODELS.size()
                );

        ResourceLocation modelId =
                ModelLocationUtils.getModelLocation(
                        itemLike.asItem()
                );

        for (
                TrimMaterialModel trimMaterial
                : TRIM_MATERIAL_MODELS
        ) {
            ResourceLocation trimModelId =
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
                                            entry.equipmentAssetKey()
                                    )
                                    .suffix()
                    );

            ModelTemplates.FLAT_ITEM.create(
                    trimModelId,
                    TextureMapping.layer0(
                            trimTexture
                    ),
                    items.modelOutput
            );

            List<ItemModel.Unbaked> trimmedChildren =
                    new ArrayList<>(
                            baseChildren.size() + 1
                    );

            trimmedChildren.addAll(
                    baseChildren
            );

            trimmedChildren.add(
                    new BlockModelWrapper.Unbaked(
                            trimModelId,
                            List.of(
                                    ProfileHexColorItemTintSource
                                            .noTint(
                                                    entry.itemProfile(),
                                                    false
                                            )
                            )
                    )
            );

            cases.add(
                    ItemModelUtils.when(
                            trimMaterial.materialKey(),
                            new CompositeModel.Unbaked(
                                    List.copyOf(
                                            trimmedChildren
                                    )
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

    private static List<ItemModel.Unbaked>
    createItemLayerModels(
            ItemModelGenerators items,
            Entry entry,
            ItemLike itemLike
    ) {
        List<ItemModel.Unbaked> children =
                new ArrayList<>(
                        entry.itemLayerCount()
                );

        for (
                int modelLayer = 0;
                modelLayer < entry.itemLayerCount();
                ++modelLayer
        ) {
            ResourceLocation layerModel =
                    entry.itemLayerModel(
                            itemLike,
                            modelLayer
                    );

            ModelTemplates.FLAT_ITEM.create(
                    layerModel,
                    TextureMapping.layer0(
                            entry.itemTexture(
                                    itemLike,
                                    modelLayer
                            )
                    ),
                    items.modelOutput
            );

            children.add(
                    new BlockModelWrapper.Unbaked(
                            layerModel,
                            List.of(
                                    itemTintSource(
                                            entry,
                                            modelLayer,
                                            modelLayer == 0
                                    )
                            )
                    )
            );
        }

        return List.copyOf(children);
    }

    private static ItemModel.Unbaked
    createCustomHelmetModel(
            ItemModelGenerators items,
            Entry entry
    ) {
        ModelTemplate layerTemplate =
                new ModelTemplate(
                        Optional.of(
                                entry.customHelmetHeadModelLocation()
                        ),
                        Optional.empty(),
                        CUSTOM_HELMET_TEXTURE,
                        TextureSlot.PARTICLE
                );

        List<ItemModel.Unbaked> children =
                new ArrayList<>(
                        entry.helmetLayerCount()
                );

        for (
                int modelLayer = 0;
                modelLayer < entry.helmetLayerCount();
                ++modelLayer
        ) {
            ResourceLocation texture =
                    entry.customHelmetTexture(
                            modelLayer
                    );

            ResourceLocation layerModel =
                    entry.customHelmetLayerModel(
                            modelLayer
                    );

            layerTemplate.create(
                    layerModel,
                    new TextureMapping()
                            .put(
                                    CUSTOM_HELMET_TEXTURE,
                                    texture
                            )
                            .put(
                                    TextureSlot.PARTICLE,
                                    texture
                            ),
                    items.modelOutput
            );

            children.add(
                    new BlockModelWrapper.Unbaked(
                            layerModel,
                            List.of(
                                    helmetTintSource(
                                            entry,
                                            modelLayer,
                                            modelLayer == 0
                                    )
                            )
                    )
            );
        }

        return new CompositeModel.Unbaked(
                List.copyOf(children)
        );
    }

    private static ItemTintSource itemTintSource(
            Entry entry,
            int modelLayer,
            boolean foilCarrier
    ) {
        if (entry.isUntintedItemLayer(modelLayer)) {
            return ProfileHexColorItemTintSource.noTint(
                    entry.itemProfile(),
                    foilCarrier
            );
        }

        return ProfileHexColorItemTintSource.of(
                entry.itemProfileLayer(modelLayer),
                entry.itemProfile(),
                foilCarrier
        );
    }

    private static ItemTintSource helmetTintSource(
            Entry entry,
            int modelLayer,
            boolean foilCarrier
    ) {
        if (entry.isUntintedHelmetLayer(modelLayer)) {
            return ProfileHexColorItemTintSource.noTint(
                    entry.helmetProfile(),
                    foilCarrier
            );
        }

        return ProfileHexColorItemTintSource.of(
                entry.helmetProfileLayer(modelLayer),
                entry.helmetProfile(),
                foilCarrier
        );
    }

    private static int totalLayerCount(
            PearlFireTintProfiles.Profile profile,
            LayerLayout layout
    ) {
        return profile.layerCount()
                + (
                layout == LayerLayout.WITH_UNTINTED_BASE
                        ? 1
                        : 0
        );
    }

    private static boolean isUntintedBaseLayer(
            LayerLayout layout,
            int modelLayer
    ) {
        return layout == LayerLayout.WITH_UNTINTED_BASE
                && modelLayer == 0;
    }

    private static int profileLayer(
            LayerLayout layout,
            int modelLayer,
            String type
    ) {
        if (isUntintedBaseLayer(layout, modelLayer)) {
            throw new IllegalArgumentException(
                    type
                            + " model layer 0 is the untinted base "
                            + "and has no profile layer"
            );
        }

        return modelLayer
                - (
                layout == LayerLayout.WITH_UNTINTED_BASE
                        ? 1
                        : 0
        );
    }

    private static String layerSuffix(
            LayerLayout layout,
            int modelLayer
    ) {
        if (isUntintedBaseLayer(layout, modelLayer)) {
            return "";
        }

        return "_" + profileLayer(
                layout,
                modelLayer,
                "texture"
        );
    }

    private static String textureStem(
            String rootPath,
            TextureLayout layout
    ) {
        if (layout == TextureLayout.ROOT) {
            return rootPath;
        }

        int lastSlash = rootPath.lastIndexOf('/');

        String fileName = lastSlash >= 0
                ? rootPath.substring(lastSlash + 1)
                : rootPath;

        return rootPath + "/" + fileName;
    }

    private static void validateModelLayer(
            int modelLayer,
            int layerCount,
            String type
    ) {
        if (
                modelLayer < 0
                        || modelLayer >= layerCount
        ) {
            throw new IllegalArgumentException(
                    type
                            + " modelLayer must be between 0 and "
                            + (layerCount - 1)
                            + ", got "
                            + modelLayer
            );
        }
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

            if (entries != null) {
                for (Entry entry : entries) {
                    if (entry != null) {
                        putUnique(
                                equipmentInfos,
                                entry.equipmentLocation(),
                                createEquipmentInfo(entry)
                        );
                    }
                }
            }

            return DataProvider.saveAll(
                    cache,
                    EquipmentClientInfo.CODEC,
                    this.equipmentPathProvider,
                    equipmentInfos
            );
        }

        private static EquipmentClientInfo
        createEquipmentInfo(
                Entry entry
        ) {
            EquipmentClientInfo.Layer[] layers =
                    new EquipmentClientInfo.Layer[
                            entry.equipmentLayerCount()
                            ];

            for (
                    int modelLayer = 0;
                    modelLayer < entry.equipmentLayerCount();
                    ++modelLayer
            ) {
                Optional<EquipmentClientInfo.Dyeable>
                        dyeable =
                        entry.isUntintedEquipmentLayer(
                                modelLayer
                        )
                                ? Optional.empty()
                                : Optional.of(
                                new EquipmentClientInfo.Dyeable(
                                        Optional.of(
                                                0xFFFFFF
                                        )
                                )
                        );

                layers[modelLayer] =
                        new EquipmentClientInfo.Layer(
                                entry.equipmentTexture(
                                        modelLayer
                                ),
                                dyeable,
                                false
                        );
            }

            return EquipmentClientInfo.builder()
                    .addLayers(
                            EquipmentClientInfo.LayerType.HUMANOID,
                            layers
                    )
                    .addLayers(
                            EquipmentClientInfo.LayerType.HUMANOID_LEGGINGS,
                            layers
                    )
                    .build();
        }

        private static void putUnique(
                Map<ResourceLocation, EquipmentClientInfo> map,
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
            return "Tinted Custom Armor Sets: Musavacca";
        }
    }
}
