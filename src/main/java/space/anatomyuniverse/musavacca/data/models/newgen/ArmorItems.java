package space.anatomyuniverse.musavacca.data.models.newgen;

import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import space.anatomyuniverse.musavacca.item.CustomHelmetArmorTrims;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

//? if >=1.21.2 && <1.21.4
//import net.minecraft.world.item.equipment.EquipmentModel;
//? if >=1.21.4 {
import net.minecraft.client.resources.model.EquipmentClientInfo;
//?}

/** NewGen sibling for armor inventory models + worn equipment assets. */
public final class ArmorItems {
    private ArmorItems() {}

    private static final ResourceLocation TRIM_ICON_HELMET = minecraft("trims/items/helmet_trim_quartz");
    private static final ResourceLocation TRIM_ICON_CHESTPLATE = minecraft("trims/items/chestplate_trim_quartz");
    private static final ResourceLocation TRIM_ICON_LEGGINGS = minecraft("trims/items/leggings_trim_quartz");
    private static final ResourceLocation TRIM_ICON_BOOTS = minecraft("trims/items/boots_trim_quartz");

    static ResourceLocation trimTexture(Piece piece) {
        return switch (piece) {
            case HELMET -> TRIM_ICON_HELMET;
            case CHESTPLATE -> TRIM_ICON_CHESTPLATE;
            case LEGGINGS -> TRIM_ICON_LEGGINGS;
            case BOOTS -> TRIM_ICON_BOOTS;
        };
    }

    private static ResourceLocation minecraft(String path) {
        return ResourceLocation.fromNamespaceAndPath("minecraft", path);
    }

    public enum Piece {
        HELMET,
        CHESTPLATE,
        LEGGINGS,
        BOOTS
    }

    enum InventoryMode {
        GENERATED,
        EXISTING
    }

    public static final class Entry {
        private final ItemSet items;
        private final Inventory inventory;
        private final Equipment equipment;
        private final ResourceLocation helmetHeadModel;

        private Entry(Builder builder) {
            items = Objects.requireNonNull(builder.items, "items");
            inventory = Objects.requireNonNull(builder.inventory, "inventory");
            equipment = Objects.requireNonNull(builder.equipment, "equipment");
            helmetHeadModel = builder.helmetHeadModel;
            validate();
        }

        public static Builder builder() {
            return new Builder();
        }

        public ItemLike helmet() { return items.helmet(); }
        public ItemLike chestplate() { return items.chestplate(); }
        public ItemLike leggings() { return items.leggings(); }
        public ItemLike boots() { return items.boots(); }
        public ItemSet items() { return items; }
        public Inventory inventory() { return inventory; }
        public Equipment equipment() { return equipment; }
        public ResourceLocation helmetHeadModel() { return helmetHeadModel; }
        public boolean hasHelmetHeadModel() { return helmetHeadModel != null; }

        public ItemLike item(Piece piece) {
            return items.item(piece);
        }

        public ResourceLocation inventoryTexture(Piece piece) {
            ItemLike item = item(piece);
            String override = inventory.texture(piece);
            return override == null ? TextureTokens.item(item) : TextureTokens.resolveItem(item, override);
        }

        public ResourceLocation existingInventoryModel(Piece piece) {
            String model = inventory.model(piece);
            return model == null ? null : ResourceLocation.parse(model);
        }

        private void validate() {
            if (inventory.mode() == InventoryMode.EXISTING && inventory.trims()) {
                throw new IllegalStateException(
                        "ArmorItems .trims() currently requires generated inventory models; "
                                + "use .generated() or provide a dedicated future trim model family"
                );
            }
        }
    }

    public static final class Builder {
        private ItemSet items;
        private Inventory inventory;
        private Equipment equipment;
        private ResourceLocation helmetHeadModel;

        public Builder items(Consumer<ItemSet.Builder> config) {
            Objects.requireNonNull(config, "config");
            ItemSet.Builder builder = ItemSet.builder();
            config.accept(builder);
            items = builder.build();
            return this;
        }

        public Builder inventory(Consumer<Inventory.Builder> config) {
            Objects.requireNonNull(config, "config");
            Inventory.Builder builder = Inventory.builder();
            config.accept(builder);
            inventory = builder.build();
            return this;
        }

        public Builder equipment(Consumer<Equipment.Builder> config) {
            Objects.requireNonNull(config, "config");
            Equipment.Builder builder = Equipment.builder();
            config.accept(builder);
            equipment = builder.build();
            return this;
        }

        public Builder helmetHeadModel(String model) {
            if (model == null || model.isBlank()) throw new IllegalArgumentException("helmet head model must not be blank");
            helmetHeadModel = ResourceLocation.parse(model);
            return this;
        }

        public Entry build() {
            if (items == null) throw new IllegalStateException("ArmorItems requires .items(...)");
            if (inventory == null) throw new IllegalStateException("ArmorItems requires .inventory(...)");
            if (equipment == null) throw new IllegalStateException("ArmorItems requires .equipment(...)");
            return new Entry(this);
        }
    }

    public record ItemSet(ItemLike helmet, ItemLike chestplate, ItemLike leggings, ItemLike boots) {
        public ItemSet {
            Objects.requireNonNull(helmet, "helmet");
            Objects.requireNonNull(chestplate, "chestplate");
            Objects.requireNonNull(leggings, "leggings");
            Objects.requireNonNull(boots, "boots");
        }

        public static Builder builder() { return new Builder(); }

        public ItemLike item(Piece piece) {
            return switch (piece) {
                case HELMET -> helmet;
                case CHESTPLATE -> chestplate;
                case LEGGINGS -> leggings;
                case BOOTS -> boots;
            };
        }

        public List<ItemLike> all() {
            return List.of(helmet, chestplate, leggings, boots);
        }

        public static final class Builder {
            private ItemLike helmet;
            private ItemLike chestplate;
            private ItemLike leggings;
            private ItemLike boots;

            public Builder helmet(ItemLike item) { helmet = Objects.requireNonNull(item, "item"); return this; }
            public Builder chestplate(ItemLike item) { chestplate = Objects.requireNonNull(item, "item"); return this; }
            public Builder leggings(ItemLike item) { leggings = Objects.requireNonNull(item, "item"); return this; }
            public Builder boots(ItemLike item) { boots = Objects.requireNonNull(item, "item"); return this; }

            public ItemSet build() { return new ItemSet(helmet, chestplate, leggings, boots); }
        }
    }

    public static final class Inventory {
        private final InventoryMode mode;
        private final boolean trims;
        private final Map<Piece, String> textures;
        private final Map<Piece, String> models;

        private Inventory(Builder builder) {
            mode = builder.mode;
            trims = builder.trims;
            textures = Map.copyOf(builder.textures);
            models = Map.copyOf(builder.models);
        }

        public static Builder builder() { return new Builder(); }
        InventoryMode mode() { return mode; }
        public boolean generated() { return mode == InventoryMode.GENERATED; }
        public boolean existing() { return mode == InventoryMode.EXISTING; }
        public boolean trims() { return trims; }
        String texture(Piece piece) { return textures.get(piece); }
        String model(Piece piece) { return models.get(piece); }

        public static final class Builder {
            private InventoryMode mode;
            private boolean trims;
            private final Map<Piece, String> textures = new EnumMap<>(Piece.class);
            private final Map<Piece, String> models = new EnumMap<>(Piece.class);

            public Builder generated() {
                select(InventoryMode.GENERATED);
                return this;
            }

            public Builder textures(Consumer<PieceTextures> config) {
                if (mode != InventoryMode.GENERATED) {
                    throw new IllegalStateException("inventory.textures(...) requires .generated() first");
                }
                PieceTextures values = new PieceTextures(textures);
                config.accept(values);
                return this;
            }

            public Builder models(Consumer<PieceModels> config) {
                select(InventoryMode.EXISTING);
                PieceModels values = new PieceModels(models);
                config.accept(values);
                return this;
            }

            public Builder trims() {
                trims = true;
                return this;
            }

            public Inventory build() {
                if (mode == null) throw new IllegalStateException("Armor inventory requires .generated() or .models(...)");
                if (mode == InventoryMode.EXISTING && models.size() != Piece.values().length) {
                    throw new IllegalStateException("Existing armor inventory requires helmet/chestplate/leggings/boots models");
                }
                return new Inventory(this);
            }

            private void select(InventoryMode requested) {
                if (mode != null && mode != requested) {
                    throw new IllegalStateException("Armor inventory mode is already " + mode);
                }
                mode = requested;
            }
        }
    }

    public static final class PieceTextures {
        private final Map<Piece, String> values;
        private PieceTextures(Map<Piece, String> values) { this.values = values; }
        public PieceTextures helmet(String texture) { put(Piece.HELMET, texture); return this; }
        public PieceTextures chestplate(String texture) { put(Piece.CHESTPLATE, texture); return this; }
        public PieceTextures leggings(String texture) { put(Piece.LEGGINGS, texture); return this; }
        public PieceTextures boots(String texture) { put(Piece.BOOTS, texture); return this; }
        private void put(Piece piece, String value) {
            if (value == null || value.isBlank()) throw new IllegalArgumentException("armor texture must not be blank");
            values.put(piece, value);
        }
    }

    public static final class PieceModels {
        private final Map<Piece, String> values;
        private PieceModels(Map<Piece, String> values) { this.values = values; }
        public PieceModels helmet(String model) { put(Piece.HELMET, model); return this; }
        public PieceModels chestplate(String model) { put(Piece.CHESTPLATE, model); return this; }
        public PieceModels leggings(String model) { put(Piece.LEGGINGS, model); return this; }
        public PieceModels boots(String model) { put(Piece.BOOTS, model); return this; }
        private void put(Piece piece, String value) {
            if (value == null || value.isBlank()) throw new IllegalArgumentException("armor model must not be blank");
            ResourceLocation.parse(value);
            values.put(piece, value);
        }
    }

    public record Equipment(ResourceLocation id, ResourceLocation texture) {
        public Equipment {
            Objects.requireNonNull(id, "id");
            Objects.requireNonNull(texture, "texture");
        }
        public static Builder builder() { return new Builder(); }
        public static final class Builder {
            private ResourceLocation id;
            private ResourceLocation texture;
            public Builder id(String id) { this.id = ResourceLocation.parse(id); return this; }
            public Builder texture(String texture) { this.texture = ResourceLocation.parse(texture); return this; }
            public Equipment build() {
                if (id == null) throw new IllegalStateException("equipment.id(...) is required");
                if (texture == null) throw new IllegalStateException("equipment.texture(...) is required");
                return new Equipment(id, texture);
            }
        }
    }

    public static final class Provider implements DataProvider {
        private final PackOutput.PathProvider equipmentPathProvider;
        private final List<Entry> entries;

        public Provider(PackOutput output, List<Entry> entries) {
            this.equipmentPathProvider = output.createPathProvider(
                    PackOutput.Target.RESOURCE_PACK,
                    //? if <1.21.4
                    //"models/equipment"
                    //? if >=1.21.4
                    "equipment"
            );
            this.entries = entries == null ? List.of() : List.copyOf(entries);
        }

        @Override
        public CompletableFuture<?> run(CachedOutput cache) {
            //? if <1.21.2 {
            /*return CompletableFuture.completedFuture(null);
            *///?} else if <1.21.4 {
            /*Map<ResourceLocation, EquipmentModel> equipmentInfos = new HashMap<>();
            addLegacyTrimCarrier(equipmentInfos);
            addLegacyEquipmentInfos(equipmentInfos);
            return DataProvider.saveAll(cache, EquipmentModel.CODEC, equipmentPathProvider, equipmentInfos);
            *///?} else {
            Map<ResourceLocation, EquipmentClientInfo> equipmentInfos = new HashMap<>();
            addTrimCarrier(equipmentInfos);
            addEquipmentInfos(equipmentInfos);
            return DataProvider.saveAll(cache, EquipmentClientInfo.CODEC, equipmentPathProvider, equipmentInfos);
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
                                    EquipmentModel.Layer.onlyIfDyed(CustomHelmetArmorTrims.TRIM_CARRIER_ID, true)
                            )
                            .build()
            );
        }

        private void addLegacyEquipmentInfos(Map<ResourceLocation, EquipmentModel> map) {
            for (Entry entry : entries) {
                EquipmentModel.Layer layer = new EquipmentModel.Layer(
                        entry.equipment().texture(),
                        Optional.empty(),
                        false
                );
                putLegacyUnique(
                        map,
                        entry.equipment().id(),
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
                                    EquipmentClientInfo.Layer.onlyIfDyed(CustomHelmetArmorTrims.TRIM_CARRIER_ID, true)
                            )
                            .build()
            );
        }

        private void addEquipmentInfos(Map<ResourceLocation, EquipmentClientInfo> map) {
            for (Entry entry : entries) {
                EquipmentClientInfo.Layer layer = new EquipmentClientInfo.Layer(
                        entry.equipment().texture(),
                        Optional.empty(),
                        false
                );
                putUnique(
                        map,
                        entry.equipment().id(),
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
            return "NewGen Armor Equipment: Musavacca";
        }
    }
}
