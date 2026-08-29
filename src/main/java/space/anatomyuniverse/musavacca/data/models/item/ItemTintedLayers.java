package space.anatomyuniverse.musavacca.data.models.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import space.anatomyuniverse.musavacca.data.models.ModelUtil;
import space.anatomyuniverse.musavacca.tint.LayeredItemTint;
import space.anatomyuniverse.musavacca.tint.PearlFireTintProfiles;

//? if <1.21.4 {
/*import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.loaders.ItemLayerModelBuilder;
*///?} else {
import net.minecraft.client.color.item.Constant;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.renderer.item.BlockModelWrapper;
import net.minecraft.client.renderer.item.CompositeModel;
import net.minecraft.client.renderer.item.ItemModel;
import space.anatomyuniverse.musavacca.tint.ProfileHexColorItemTintSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
//?}

/**
 * Adaptive layered-item datagen/runtime definition.
 *
 * Every gray factor in the profile is one real tinted layer. Up to five total
 * visible layers use vanilla generated-item layers. Larger models use the
 * version-native unlimited path: NeoForge item_layers before 1.21.4 and
 * Minecraft composite client item models on 1.21.4+.
 */
public final class ItemTintedLayers {
    private ItemTintedLayers() {}

    public enum LayerLayout {
        WITH_UNTINTED_BASE,
        FULLY_TINTED
    }

    public record Entry(
            ItemLike item,
            String modelStem,
            String textureStem,
            PearlFireTintProfiles.Profile profile,
            LayerLayout layout
    ) {
        public Entry {
            if (item == null) throw new IllegalArgumentException("item must not be null");
            if (modelStem == null || modelStem.isBlank()) throw new IllegalArgumentException("modelStem must not be blank");
            if (textureStem == null || textureStem.isBlank()) throw new IllegalArgumentException("textureStem must not be blank");
            if (profile == null) throw new IllegalArgumentException("profile must not be null");
            if (layout == null) throw new IllegalArgumentException("layout must not be null");
        }

        public ResourceLocation itemId() {
            return ModelUtil.idOf(item);
        }

        public ResourceLocation modelId() {
            ResourceLocation id = itemId();
            return ResourceLocation.fromNamespaceAndPath(
                    id.getNamespace(),
                    "item/" + modelStem
            );
        }

        public ResourceLocation layerModelId(int modelLayer) {
            validateModelLayer(modelLayer);
            ResourceLocation id = itemId();
            return ResourceLocation.fromNamespaceAndPath(
                    id.getNamespace(),
                    "item/" + modelStem + "_layer_" + modelLayer
            );
        }

        public boolean hasUntintedBase() {
            return layout == LayerLayout.WITH_UNTINTED_BASE;
        }

        public int tintedLayerCount() {
            return profile.layerCount();
        }

        public int totalLayerCount() {
            return LayeredItemTint.totalModelLayers(
                    profile,
                    hasUntintedBase()
            );
        }

        public boolean usesVanillaLayers() {
            return LayeredItemTint.usesVanillaModelLayers(
                    profile,
                    hasUntintedBase()
            );
        }

        public boolean usesUnlimitedLayers() {
            return !usesVanillaLayers();
        }

        public boolean isUntintedBaseLayer(int modelLayer) {
            validateModelLayer(modelLayer);
            return LayeredItemTint.isUntintedBase(
                    modelLayer,
                    hasUntintedBase()
            );
        }

        public int profileLayerForModelLayer(int modelLayer) {
            validateModelLayer(modelLayer);
            int profileLayer = LayeredItemTint.profileLayerForTintIndex(
                    profile,
                    hasUntintedBase(),
                    modelLayer
            );
            if (profileLayer < 0) {
                throw new IllegalArgumentException(
                        "Model layer " + modelLayer
                                + " is untinted and has no profile layer"
                );
            }
            return profileLayer;
        }

        public ResourceLocation texture(int modelLayer) {
            validateModelLayer(modelLayer);
            String path = isUntintedBaseLayer(modelLayer)
                    ? textureStem
                    : textureStem + "_" + profileLayerForModelLayer(modelLayer);
            ResourceLocation id = itemId();
            return ResourceLocation.fromNamespaceAndPath(
                    id.getNamespace(),
                    "item/" + path
            );
        }

        public int tint(ItemStack stack, int tintIndex) {
            return LayeredItemTint.tint(
                    stack,
                    tintIndex,
                    profile,
                    hasUntintedBase()
            );
        }

        private void validateModelLayer(int modelLayer) {
            if (modelLayer < 0 || modelLayer >= totalLayerCount()) {
                throw new IllegalArgumentException(
                        "modelLayer must be between 0 and "
                                + (totalLayerCount() - 1)
                                + ", got "
                                + modelLayer
                );
            }
        }
    }

    public static Entry root(ItemLike item, PearlFireTintProfiles.Profile profile) {
        String stem = ModelUtil.pathOf(item);
        return new Entry(item, stem, stem, profile, LayerLayout.WITH_UNTINTED_BASE);
    }

    public static Entry fullyTintedRoot(ItemLike item, PearlFireTintProfiles.Profile profile) {
        String stem = ModelUtil.pathOf(item);
        return new Entry(item, stem, stem, profile, LayerLayout.FULLY_TINTED);
    }

    public static Entry folder(ItemLike item, PearlFireTintProfiles.Profile profile) {
        String stem = ModelUtil.pathOf(item);
        return new Entry(item, stem, folderTextureStem(stem), profile, LayerLayout.WITH_UNTINTED_BASE);
    }

    public static Entry fullyTintedFolder(ItemLike item, PearlFireTintProfiles.Profile profile) {
        String stem = ModelUtil.pathOf(item);
        return new Entry(item, stem, folderTextureStem(stem), profile, LayerLayout.FULLY_TINTED);
    }

    public static Entry of(
            ItemLike item,
            String modelStem,
            String textureStem,
            PearlFireTintProfiles.Profile profile
    ) {
        return new Entry(item, modelStem, textureStem, profile, LayerLayout.WITH_UNTINTED_BASE);
    }

    public static Entry fullyTintedOf(
            ItemLike item,
            String modelStem,
            String textureStem,
            PearlFireTintProfiles.Profile profile
    ) {
        return new Entry(item, modelStem, textureStem, profile, LayerLayout.FULLY_TINTED);
    }

    private static String folderTextureStem(String modelStem) {
        int slash = modelStem.lastIndexOf('/');
        String file = slash >= 0 ? modelStem.substring(slash + 1) : modelStem;
        return modelStem + "/" + file;
    }

    //? if <1.21.4 {
    /*public static void registerItemColors(
            RegisterColorHandlersEvent.Item event,
            Entry... entries
    ) {
        if (event == null || entries == null) return;

        for (Entry entry : entries) {
            if (entry != null) {
                event.register(entry::tint, entry.item());
            }
        }
    }

    public static void generate(ItemModelProvider items, Entry... entries) {
        if (entries == null) return;

        for (Entry entry : entries) {
            if (entry == null) continue;

            var model = items.withExistingParent(
                    entry.modelStem(),
                    items.mcLoc("item/generated")
            );

            if (entry.usesUnlimitedLayers()) {
                model.customLoader(ItemLayerModelBuilder::begin).end();
            }

            for (int layer = 0; layer < entry.totalLayerCount(); ++layer) {
                model.texture("layer" + layer, entry.texture(layer));
            }
        }
    }
    *///?} else {
    private static final TextureSlot LAYER1 = TextureSlot.create("layer1");
    private static final TextureSlot LAYER2 = TextureSlot.create("layer2");
    private static final TextureSlot LAYER3 = TextureSlot.create("layer3");
    private static final TextureSlot LAYER4 = TextureSlot.create("layer4");

    private static final TextureSlot[] VANILLA_LAYER_SLOTS = {
            TextureSlot.LAYER0,
            LAYER1,
            LAYER2,
            LAYER3,
            LAYER4
    };

    public static void generate(ItemModelGenerators items, Entry... entries) {
        if (entries == null) return;

        for (Entry entry : entries) {
            if (entry == null) continue;

            if (entry.usesVanillaLayers()) {
                generateVanillaLayeredItem(items, entry);
            } else {
                generateCompositeLayeredItem(items, entry);
            }
        }
    }

    private static void generateVanillaLayeredItem(
            ItemModelGenerators items,
            Entry entry
    ) {
        ModelTemplate template = new ModelTemplate(
                Optional.of(ResourceLocation.withDefaultNamespace("item/generated")),
                Optional.empty(),
                requiredVanillaSlots(entry.totalLayerCount())
        );

        TextureMapping textures = new TextureMapping();
        for (int layer = 0; layer < entry.totalLayerCount(); ++layer) {
            textures.put(VANILLA_LAYER_SLOTS[layer], entry.texture(layer));
        }

        template.create(entry.modelId(), textures, items.modelOutput);
        items.itemModelOutput.accept(
                entry.item().asItem(),
                new BlockModelWrapper.Unbaked(
                        entry.modelId(),
                        tintSources(entry)
                )
        );
    }

    private static TextureSlot[] requiredVanillaSlots(int count) {
        if (count < 1 || count > LayeredItemTint.VANILLA_MAX_MODEL_LAYERS) {
            throw new IllegalArgumentException(
                    "Vanilla generated items support 1-5 layers, got " + count
            );
        }

        TextureSlot[] slots = new TextureSlot[count];
        System.arraycopy(VANILLA_LAYER_SLOTS, 0, slots, 0, count);
        return slots;
    }

    private static List<ItemTintSource> tintSources(Entry entry) {
        List<ItemTintSource> sources = new ArrayList<>(entry.totalLayerCount());

        for (int layer = 0; layer < entry.totalLayerCount(); ++layer) {
            sources.add(tintSource(entry, layer, true));
        }

        return List.copyOf(sources);
    }

    private static void generateCompositeLayeredItem(
            ItemModelGenerators items,
            Entry entry
    ) {
        for (int layer = 0; layer < entry.totalLayerCount(); ++layer) {
            ModelTemplates.FLAT_ITEM.create(
                    entry.layerModelId(layer),
                    new TextureMapping().put(
                            TextureSlot.LAYER0,
                            entry.texture(layer)
                    ),
                    items.modelOutput
            );
        }

        List<ItemModel.Unbaked> children = new ArrayList<>(entry.totalLayerCount());
        for (int layer = 0; layer < entry.totalLayerCount(); ++layer) {
            children.add(new BlockModelWrapper.Unbaked(
                    entry.layerModelId(layer),
                    List.of(tintSource(entry, layer, layer == 0))
            ));
        }

        items.itemModelOutput.accept(
                entry.item().asItem(),
                new CompositeModel.Unbaked(List.copyOf(children))
        );
    }

    private static ItemTintSource tintSource(
            Entry entry,
            int modelLayer,
            boolean foilCarrier
    ) {
        if (entry.isUntintedBaseLayer(modelLayer)) {
            return new Constant(0xFFFFFFFF);
        }

        return ProfileHexColorItemTintSource.of(
                entry.profile(),
                entry.profileLayerForModelLayer(modelLayer),
                foilCarrier
        );
    }
    //?}
}
