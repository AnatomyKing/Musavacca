package space.anatomyuniverse.musavacca.data.models.item;

//? if >=1.21.2 && <1.21.4
//import net.minecraft.world.item.equipment.EquipmentModel;
//? if <1.21.4 {
/*import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
*///?} else {
import net.minecraft.client.color.item.Constant;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.renderer.item.BlockModelWrapper;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.SelectItemModel;
import net.minecraft.client.renderer.item.properties.conditional.HasComponent;
import net.minecraft.client.renderer.item.properties.select.DisplayContext;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.component.DataComponents;
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
//?}
import net.minecraft.world.level.ItemLike;
import space.anatomyuniverse.musavacca.item.CustomHelmetArmorTrims;
import space.anatomyuniverse.musavacca.tint.ArmorTrimItemTintSource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Shared model/equipment definition for custom armor sets.
 *
 * <p>Trim compatibility is intentionally material-agnostic. Item icons use
 * one slot-specific trim mask and tint it from the actual ArmorTrim stored on
 * the stack. Worn armor delegates the complete ArmorTrim back to Minecraft,
 * so patterns/material assets supplied by other mods remain Minecraft's job.</p>
 */
public final class CustomArmorSet {

    /*
     * Inventory trim geometry is fixed per armor slot. We reuse one bright
     * vanilla permutation as the mask and tint only layer1 from the stack's
     * trim material. This keeps GUI/smithing rendering to one normal generated
     * item model instead of stacking a second runtime model on top.
     */
    private static final ResourceLocation TRIM_ICON_HELMET =
            minecraft("trims/items/helmet_trim_quartz");
    private static final ResourceLocation TRIM_ICON_CHESTPLATE =
            minecraft("trims/items/chestplate_trim_quartz");
    private static final ResourceLocation TRIM_ICON_LEGGINGS =
            minecraft("trims/items/leggings_trim_quartz");
    private static final ResourceLocation TRIM_ICON_BOOTS =
            minecraft("trims/items/boots_trim_quartz");


    private CustomArmorSet() {}

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

    //? if <1.21.4 {
    /*public static void generate(ItemModelProvider gen, Entry... entries) {
        if (entries == null) {
            return;
        }

        for (Entry entry : entries) {
            if (entry == null) {
                continue;
            }

            generateLegacyDynamicTrimItem(gen, entry.helmet(), TRIM_ICON_HELMET);
            generateLegacyDynamicTrimItem(gen, entry.chestplate(), TRIM_ICON_CHESTPLATE);
            generateLegacyDynamicTrimItem(gen, entry.leggings(), TRIM_ICON_LEGGINGS);
            generateLegacyDynamicTrimItem(gen, entry.boots(), TRIM_ICON_BOOTS);
        }
    }

    private static void generateLegacyDynamicTrimItem(
            ItemModelProvider gen,
            ItemLike itemLike,
            ResourceLocation trimTexture
    ) {
        if (itemLike == null) {
            return;
        }

        Item item = itemLike.asItem();
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(item);
        String modelName = itemId.getPath();
        ResourceLocation textureId = itemTexture(item);

        var base = gen.withExistingParent(
                        modelName,
                        gen.mcLoc("item/generated")
                )
                .texture("layer0", textureId);

        ModelFile trimmed = gen.withExistingParent(
                        modelName + "_trimmed",
                        gen.mcLoc("item/generated")
                )
                .texture("layer0", textureId)
                .texture("layer1", trimTexture);

        base.override()
                .predicate(ArmorTrimItemTintSource.HAS_TRIM_PROPERTY, 1.0F)
                .model(trimmed)
                .end();
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
                ItemModel.Unbaked flat = createDynamicTrimItemModel(
                        gen,
                        entry.helmet().asItem(),
                        TRIM_ICON_HELMET
                );

                if (entry.hasCustomHelmetHeadModel()) {
                    generateCustomHeadHelmet(gen, entry, flat);
                } else {
                    gen.itemModelOutput.accept(entry.helmet().asItem(), flat);
                }
            }

            generateDynamicTrimItem(gen, entry.chestplate(), TRIM_ICON_CHESTPLATE);
            generateDynamicTrimItem(gen, entry.leggings(), TRIM_ICON_LEGGINGS);
            generateDynamicTrimItem(gen, entry.boots(), TRIM_ICON_BOOTS);
        }
    }

    private static void generateCustomHeadHelmet(
            ItemModelGenerators gen,
            Entry entry,
            ItemModel.Unbaked flat
    ) {
        Item helmet = entry.helmet().asItem();
        ItemModel.Unbaked head = ItemModelUtils.plainModel(
                entry.customHelmetHeadModelLocation()
        );

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

    private static void generateDynamicTrimItem(
            ItemModelGenerators gen,
            ItemLike itemLike,
            ResourceLocation trimTexture
    ) {
        if (itemLike == null) {
            return;
        }

        Item item = itemLike.asItem();
        gen.itemModelOutput.accept(
                item,
                createDynamicTrimItemModel(gen, item, trimTexture)
        );
    }

    private static ItemModel.Unbaked createDynamicTrimItemModel(
            ItemModelGenerators gen,
            Item item,
            ResourceLocation trimTexture
    ) {
        ResourceLocation modelId = ModelLocationUtils.getModelLocation(item);
        ResourceLocation textureId = TextureMapping.getItemTexture(item);

        ModelTemplates.FLAT_ITEM.create(
                modelId,
                TextureMapping.layer0(textureId),
                gen.modelOutput
        );

        ResourceLocation trimmedModelId = modelId.withSuffix("_trimmed");

        ModelTemplates.TWO_LAYERED_ITEM.create(
                trimmedModelId,
                TextureMapping.layered(textureId, trimTexture),
                gen.modelOutput
        );

        ItemModel.Unbaked base = ItemModelUtils.plainModel(modelId);
        ItemModel.Unbaked trimmed = new BlockModelWrapper.Unbaked(
                trimmedModelId,
                List.of(
                        new Constant(0xFFFFFFFF),
                        ArmorTrimItemTintSource.INSTANCE
                )
        );

        return ItemModelUtils.conditional(
                new HasComponent(DataComponents.TRIM, false),
                trimmed,
                base
        );
    }
    //?}

    private static ResourceLocation itemModelLocation(Item item) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
        return ResourceLocation.fromNamespaceAndPath(
                id.getNamespace(),
                "item/" + id.getPath()
        );
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
