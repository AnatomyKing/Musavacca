package space.anatomyuniverse.musavacca.data.models.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import space.anatomyuniverse.musavacca.data.models.ModelUtil;
import space.anatomyuniverse.musavacca.tint.PearlFireTintProfiles;

//? if <1.21.4 {
/*import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.loaders.ItemLayerModelBuilder;
*///?} else {
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

public final class ItemTintedLayers {

    public static final int VANILLA_MAX_LAYERS = 5;

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
            if (item == null) {
                throw new IllegalArgumentException(
                        "item must not be null"
                );
            }

            if (modelStem == null || modelStem.isBlank()) {
                throw new IllegalArgumentException(
                        "modelStem must not be blank"
                );
            }

            if (textureStem == null || textureStem.isBlank()) {
                throw new IllegalArgumentException(
                        "textureStem must not be blank"
                );
            }

            if (profile == null) {
                throw new IllegalArgumentException(
                        "profile must not be null"
                );
            }

            if (layout == null) {
                throw new IllegalArgumentException(
                        "layout must not be null"
                );
            }
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

        public ResourceLocation layerModelId(
                int modelLayer
        ) {
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
            return tintedLayerCount()
                    + (hasUntintedBase() ? 1 : 0);
        }

        public boolean usesVanillaLayers() {
            return totalLayerCount() <= VANILLA_MAX_LAYERS;
        }

        public boolean usesLayerBypass() {
            return !usesVanillaLayers();
        }

        public int firstTintedModelLayer() {
            return hasUntintedBase() ? 1 : 0;
        }

        public boolean isUntintedBaseLayer(
                int modelLayer
        ) {
            validateModelLayer(modelLayer);

            return hasUntintedBase()
                    && modelLayer == 0;
        }

        public int profileLayerForModelLayer(
                int modelLayer
        ) {
            validateModelLayer(modelLayer);

            if (isUntintedBaseLayer(modelLayer)) {
                throw new IllegalArgumentException(
                        "Model layer 0 is the untinted base "
                                + "and has no profile layer"
                );
            }

            return modelLayer - firstTintedModelLayer();
        }

        public int profileLayerForTintIndex(
                int tintIndex
        ) {
            return profileLayerForModelLayer(tintIndex);
        }

        public ResourceLocation texture(
                int modelLayer
        ) {
            validateModelLayer(modelLayer);

            String texturePath =
                    isUntintedBaseLayer(modelLayer)
                            ? textureStem
                            : textureStem
                            + "_"
                            + profileLayerForModelLayer(
                            modelLayer
                    );

            ResourceLocation id = itemId();

            return ResourceLocation.fromNamespaceAndPath(
                    id.getNamespace(),
                    "item/" + texturePath
            );
        }

        public ResourceLocation untintedBaseTexture() {
            if (!hasUntintedBase()) {
                throw new IllegalStateException(
                        "This entry is FULLY_TINTED and has "
                                + "no untinted base texture"
                );
            }

            return texture(0);
        }

        public ResourceLocation tintedTexture(
                int profileLayer
        ) {
            validateProfileLayer(profileLayer);

            ResourceLocation id = itemId();

            return ResourceLocation.fromNamespaceAndPath(
                    id.getNamespace(),
                    "item/"
                            + textureStem
                            + "_"
                            + profileLayer
            );
        }

        private void validateModelLayer(
                int modelLayer
        ) {
            if (
                    modelLayer < 0
                            || modelLayer >= totalLayerCount()
            ) {
                throw new IllegalArgumentException(
                        "modelLayer must be between 0 and "
                                + (totalLayerCount() - 1)
                                + ", got "
                                + modelLayer
                );
            }
        }

        private void validateProfileLayer(
                int profileLayer
        ) {
            if (
                    profileLayer < 0
                            || profileLayer >= tintedLayerCount()
            ) {
                throw new IllegalArgumentException(
                        "profileLayer must be between 0 and "
                                + (tintedLayerCount() - 1)
                                + ", got "
                                + profileLayer
                );
            }
        }
    }

    public static Entry root(
            ItemLike item,
            PearlFireTintProfiles.Profile profile
    ) {
        String stem = ModelUtil.pathOf(item);

        return new Entry(
                item,
                stem,
                stem,
                profile,
                LayerLayout.WITH_UNTINTED_BASE
        );
    }

    public static Entry fullyTintedRoot(
            ItemLike item,
            PearlFireTintProfiles.Profile profile
    ) {
        String stem = ModelUtil.pathOf(item);

        return new Entry(
                item,
                stem,
                stem,
                profile,
                LayerLayout.FULLY_TINTED
        );
    }

    public static Entry folder(
            ItemLike item,
            PearlFireTintProfiles.Profile profile
    ) {
        String modelStem = ModelUtil.pathOf(item);

        return new Entry(
                item,
                modelStem,
                folderTextureStem(modelStem),
                profile,
                LayerLayout.WITH_UNTINTED_BASE
        );
    }

    public static Entry fullyTintedFolder(
            ItemLike item,
            PearlFireTintProfiles.Profile profile
    ) {
        String modelStem = ModelUtil.pathOf(item);

        return new Entry(
                item,
                modelStem,
                folderTextureStem(modelStem),
                profile,
                LayerLayout.FULLY_TINTED
        );
    }

    public static Entry of(
            ItemLike item,
            String modelStem,
            String textureStem,
            PearlFireTintProfiles.Profile profile
    ) {
        return new Entry(
                item,
                modelStem,
                textureStem,
                profile,
                LayerLayout.WITH_UNTINTED_BASE
        );
    }

    public static Entry fullyTintedOf(
            ItemLike item,
            String modelStem,
            String textureStem,
            PearlFireTintProfiles.Profile profile
    ) {
        return new Entry(
                item,
                modelStem,
                textureStem,
                profile,
                LayerLayout.FULLY_TINTED
        );
    }

    private static String folderTextureStem(
            String modelStem
    ) {
        int lastSlash = modelStem.lastIndexOf('/');

        String fileName = lastSlash >= 0
                ? modelStem.substring(lastSlash + 1)
                : modelStem;

        return modelStem + "/" + fileName;
    }

    //? if <1.21.4 {
    /*public static void generate(
            ItemModelProvider itemModels,
            Entry... entries
    ) {
        if (entries == null) {
            return;
        }

        for (Entry entry : entries) {
            if (entry != null) {
                generateOne(itemModels, entry);
            }
        }
    }

    private static void generateOne(
            ItemModelProvider itemModels,
            Entry entry
    ) {
        var builder = itemModels.withExistingParent(
                entry.modelStem(),
                itemModels.mcLoc("item/generated")
        );

        if (entry.usesLayerBypass()) {
            builder.customLoader(
                    ItemLayerModelBuilder::begin
            );
        }

        for (
                int modelLayer = 0;
                modelLayer < entry.totalLayerCount();
                ++modelLayer
        ) {
            builder.texture(
                    "layer" + modelLayer,
                    entry.texture(modelLayer)
            );
        }
    }
    *///?} else {
    private static final TextureSlot LAYER1 =
            TextureSlot.create("layer1");

    private static final TextureSlot LAYER2 =
            TextureSlot.create("layer2");

    private static final TextureSlot LAYER3 =
            TextureSlot.create("layer3");

    private static final TextureSlot LAYER4 =
            TextureSlot.create("layer4");

    private static final TextureSlot[] VANILLA_LAYER_SLOTS = {
            TextureSlot.LAYER0,
            LAYER1,
            LAYER2,
            LAYER3,
            LAYER4
    };

    public static void generate(
            ItemModelGenerators items,
            Entry... entries
    ) {
        if (entries == null) {
            return;
        }

        for (Entry entry : entries) {
            if (entry != null) {
                generateOne(items, entry);
            }
        }
    }

    private static void generateOne(
            ItemModelGenerators items,
            Entry entry
    ) {
        if (entry.usesVanillaLayers()) {
            generateVanillaLayeredItem(
                    items,
                    entry
            );
        } else {
            generateCompositeLayeredItem(
                    items,
                    entry
            );
        }
    }

    private static void generateVanillaLayeredItem(
            ItemModelGenerators items,
            Entry entry
    ) {
        ModelTemplate template = new ModelTemplate(
                Optional.of(
                        ResourceLocation.fromNamespaceAndPath(
                                "minecraft",
                                "item/generated"
                        )
                ),
                Optional.empty(),
                requiredVanillaSlots(
                        entry.totalLayerCount()
                )
        );

        TextureMapping mapping = new TextureMapping();

        for (
                int modelLayer = 0;
                modelLayer < entry.totalLayerCount();
                ++modelLayer
        ) {
            mapping.put(
                    VANILLA_LAYER_SLOTS[modelLayer],
                    entry.texture(modelLayer)
            );
        }

        template.create(
                entry.modelId(),
                mapping,
                items.modelOutput
        );

        items.itemModelOutput.accept(
                entry.item().asItem(),
                new BlockModelWrapper.Unbaked(
                        entry.modelId(),
                        tintSources(entry)
                )
        );
    }

    private static TextureSlot[] requiredVanillaSlots(
            int totalLayerCount
    ) {
        if (
                totalLayerCount <= 0
                        || totalLayerCount > VANILLA_MAX_LAYERS
        ) {
            throw new IllegalArgumentException(
                    "Vanilla layered items require between 1 and "
                            + VANILLA_MAX_LAYERS
                            + " total layers, got "
                            + totalLayerCount
            );
        }

        TextureSlot[] slots =
                new TextureSlot[totalLayerCount];

        System.arraycopy(
                VANILLA_LAYER_SLOTS,
                0,
                slots,
                0,
                totalLayerCount
        );

        return slots;
    }

    private static List<ItemTintSource> tintSources(
            Entry entry
    ) {
        List<ItemTintSource> sources =
                new ArrayList<>(
                        entry.totalLayerCount()
                );

        for (
                int modelLayer = 0;
                modelLayer < entry.totalLayerCount();
                ++modelLayer
        ) {
            sources.add(
                    tintSource(
                            entry,
                            modelLayer,
                            true
                    )
            );
        }

        return List.copyOf(sources);
    }

    private static void generateCompositeLayeredItem(
            ItemModelGenerators items,
            Entry entry
    ) {
        for (
                int modelLayer = 0;
                modelLayer < entry.totalLayerCount();
                ++modelLayer
        ) {
            ModelTemplates.FLAT_ITEM.create(
                    entry.layerModelId(modelLayer),
                    new TextureMapping().put(
                            TextureSlot.LAYER0,
                            entry.texture(modelLayer)
                    ),
                    items.modelOutput
            );
        }

        items.itemModelOutput.accept(
                entry.item().asItem(),
                new CompositeModel.Unbaked(
                        compositeChildren(entry)
                )
        );
    }

    private static List<ItemModel.Unbaked> compositeChildren(
            Entry entry
    ) {
        List<ItemModel.Unbaked> children =
                new ArrayList<>(
                        entry.totalLayerCount()
                );

        for (
                int modelLayer = 0;
                modelLayer < entry.totalLayerCount();
                ++modelLayer
        ) {
            children.add(
                    new BlockModelWrapper.Unbaked(
                            entry.layerModelId(modelLayer),
                            List.of(
                                    tintSource(
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

    private static ItemTintSource tintSource(
            Entry entry,
            int modelLayer,
            boolean foilCarrier
    ) {
        if (entry.isUntintedBaseLayer(modelLayer)) {
            return ProfileHexColorItemTintSource.noTint(
                    entry.profile(),
                    foilCarrier
            );
        }

        return ProfileHexColorItemTintSource.of(
                entry.profileLayerForModelLayer(
                        modelLayer
                ),
                entry.profile(),
                foilCarrier
        );
    }
    //?}
}
