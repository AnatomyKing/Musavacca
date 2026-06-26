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
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.renderer.item.BlockModelWrapper;
import space.anatomyuniverse.musavacca.tint.ProfileHexColorItemTintSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
//?}

public final class ItemTintedMaxLayer5 {
    private ItemTintedMaxLayer5() {}

    /*
     * Vanilla item/generated has:
     * layer0
     * layer1
     * layer2
     * layer3
     * layer4
     *
     * In this helper:
     * layer0 = untinted base
     * layer1 = profile layer 0
     * layer2 = profile layer 1
     * layer3 = profile layer 2
     * layer4 = profile layer 3
     */
    public static final int MAX_TOTAL_LAYERS = 5;
    public static final int MAX_TINTED_LAYERS = MAX_TOTAL_LAYERS - 1;

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

            if (profile.layerCount() > MAX_TINTED_LAYERS) {
                throw new IllegalArgumentException(
                        "ItemTintedMaxLayer5 supports max "
                                + MAX_TINTED_LAYERS
                                + " tinted layers because layer0 is the base. Got: "
                                + profile.layerCount()
                );
            }
        }

        public int tintedLayerCount() {
            return profile.layerCount();
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
    private static final TextureSlot LAYER1 = TextureSlot.create("layer1");
    private static final TextureSlot LAYER2 = TextureSlot.create("layer2");
    private static final TextureSlot LAYER3 = TextureSlot.create("layer3");
    private static final TextureSlot LAYER4 = TextureSlot.create("layer4");

    private static final TextureSlot[] LAYER_SLOTS = {
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
            generateOne(items, entry);
        }
    }

    private static void generateOne(ItemModelGenerators items, Entry entry) {
        createGeneratedLayeredModel(items, entry);

        items.itemModelOutput.accept(
                entry.item().asItem(),
                new BlockModelWrapper.Unbaked(
                        entry.modelId(),
                        tintSources(entry)
                )
        );
    }

    private static void createGeneratedLayeredModel(
            ItemModelGenerators items,
            Entry entry
    ) {
        int totalLayerCount = 1 + entry.tintedLayerCount();

        ModelTemplate template = new ModelTemplate(
                Optional.of(ResourceLocation.fromNamespaceAndPath("minecraft", "item/generated")),
                Optional.empty(),
                requiredSlots(totalLayerCount)
        );

        TextureMapping mapping = new TextureMapping()
                .put(TextureSlot.LAYER0, entry.baseTexture());

        for (int layer = 0; layer < entry.tintedLayerCount(); ++layer) {
            mapping.put(
                    LAYER_SLOTS[layer + 1],
                    entry.tintedTexture(layer)
            );
        }

        template.create(
                entry.modelId(),
                mapping,
                items.modelOutput
        );
    }

    private static TextureSlot[] requiredSlots(int totalLayerCount) {
        if (totalLayerCount <= 0 || totalLayerCount > MAX_TOTAL_LAYERS) {
            throw new IllegalArgumentException(
                    "totalLayerCount must be between 1 and "
                            + MAX_TOTAL_LAYERS
                            + ", got "
                            + totalLayerCount
            );
        }

        TextureSlot[] slots = new TextureSlot[totalLayerCount];

        for (int i = 0; i < totalLayerCount; ++i) {
            slots[i] = LAYER_SLOTS[i];
        }

        return slots;
    }

    private static List<ItemTintSource> tintSources(Entry entry) {
        List<ItemTintSource> sources = new ArrayList<>();

        /*
         * tintindex 0 belongs to layer0/base.
         * Keep it visually unchanged.
         */
        sources.add(ProfileHexColorItemTintSource.noTint(entry.profile()));

        /*
         * layer1 uses tintindex 1, but should use profile layer 0.
         * layer2 uses tintindex 2, but should use profile layer 1.
         * etc.
         */
        for (int layer = 0; layer < entry.tintedLayerCount(); ++layer) {
            sources.add(
                    ProfileHexColorItemTintSource.of(
                            layer,
                            entry.profile()
                    )
            );
        }

        return List.copyOf(sources);
    }
    //?}
}