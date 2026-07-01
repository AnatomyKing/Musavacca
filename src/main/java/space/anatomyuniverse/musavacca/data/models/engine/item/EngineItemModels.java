package space.anatomyuniverse.musavacca.data.models.engine.item;

import net.minecraft.resources.ResourceLocation;
import space.anatomyuniverse.musavacca.data.models.engine.core.EngineIds;
import space.anatomyuniverse.musavacca.data.models.unified.PearlTint;

import java.util.ArrayList;
import java.util.List;

//? if <1.21.4 {
/*import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
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

import java.util.Optional;
//?}

public final class EngineItemModels {
    private EngineItemModels() {}

    public static void generate(
            //? if <1.21.4 {
            /*ItemModelProvider items,
             *///?} else {
            ItemModelGenerators items,
            //?}
            EngineItemEntry... entries
    ) {
        if (entries == null) return;

        for (EngineItemEntry entry : entries) {
            if (entry == null || entry.item() == null) continue;
            generateOne(items, entry);
        }
    }

    private static void generateOne(
            //? if <1.21.4 {
            /*ItemModelProvider items,
             *///?} else {
            ItemModelGenerators items,
            //?}
            EngineItemEntry entry
    ) {
        //? if <1.21.4 {
        /*switch (entry.kind()) {
            case FLAT -> items.basicItem(entry.item().asItem());
            case HANDHELD -> items.handheldItem(entry.item().asItem());
            case MODEL -> items.withExistingParent(
                    EngineIds.itemId(entry.item()).getPath(),
                    entry.modelLocation()
            );
        }
        *///?} else {
        PearlTint pearlTint = entry.pearlTintOrNull();

        /*
         * PearlTint is just an ItemTint option.
         *
         * For generated items, the engine can generate the visual layers.
         * For existing/custom models, the model JSON already controls tintindex layout,
         * so we only attach the tint sources through BlockModelWrapper.
         */
        if (pearlTint != null && entry.isGeneratedKind()) {
            generateGeneratedPearlItem(items, entry, pearlTint);
            return;
        }

        switch (entry.kind()) {
            case FLAT -> generateGeneratedItem(
                    items,
                    entry,
                    ModelTemplates.FLAT_ITEM
            );

            case HANDHELD -> generateGeneratedItem(
                    items,
                    entry,
                    ModelTemplates.FLAT_HANDHELD_ITEM
            );

            case MODEL -> items.itemModelOutput.accept(
                    entry.item().asItem(),
                    model(entry)
            );
        }
        //?}
    }

    //? if >=1.21.4 {
    public static BlockModelWrapper.Unbaked model(EngineItemEntry entry) {
        return new BlockModelWrapper.Unbaked(
                entry.modelLocation(),
                tintSources(entry)
        );
    }

    private static void generateGeneratedItem(
            ItemModelGenerators items,
            EngineItemEntry entry,
            ModelTemplate template
    ) {
        if (entry.itemTint() == null) {
            items.generateFlatItem(
                    entry.item().asItem(),
                    template
            );
            return;
        }

        ResourceLocation modelId = entry.modelLocation();

        template.create(
                modelId,
                new TextureMapping()
                        .put(
                                TextureSlot.LAYER0,
                                texture(entry, entry.baseTextureStemOrDefault())
                        ),
                items.modelOutput
        );

        items.itemModelOutput.accept(
                entry.item().asItem(),
                new BlockModelWrapper.Unbaked(
                        modelId,
                        tintSources(entry)
                )
        );
    }

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

    private static void generateGeneratedPearlItem(
            ItemModelGenerators items,
            EngineItemEntry entry,
            PearlTint pearlTint
    ) {
        if (pearlTint.offset() != 0) {
            throw new IllegalArgumentException(
                    "PearlTint offset is only valid for custom model tint-index layouts. " +
                            "Use SimpleItems.Entry.model(...).pearlTint(...) for offset item models."
            );
        }

        if (pearlTint.requiresCompositeBypass()) {
            generateCompositePearlItem(items, entry, pearlTint);
        } else {
            generateVanillaLayeredPearlItem(items, entry, pearlTint);
        }
    }

    private static void generateVanillaLayeredPearlItem(
            ItemModelGenerators items,
            EngineItemEntry entry,
            PearlTint pearlTint
    ) {
        int totalLayers = pearlTint.totalItemLayerCount();

        ModelTemplate template = new ModelTemplate(
                Optional.of(parentFor(entry)),
                Optional.empty(),
                requiredSlots(totalLayers)
        );

        TextureMapping mapping = new TextureMapping();

        int slot = 0;

        if (pearlTint.hasBaseLayer()) {
            mapping.put(
                    LAYER_SLOTS[slot++],
                    texture(entry, entry.baseTextureStemOrDefault())
            );
        }

        for (int layer = 0; layer < pearlTint.layerCount(); layer++) {
            mapping.put(
                    LAYER_SLOTS[slot++],
                    texture(
                            entry,
                            entry.tintedTextureStemOrDefault() + "_" + layer
                    )
            );
        }

        template.create(
                entry.modelLocation(),
                mapping,
                items.modelOutput
        );

        items.itemModelOutput.accept(
                entry.item().asItem(),
                new BlockModelWrapper.Unbaked(
                        entry.modelLocation(),
                        castTintSources(pearlTint.sources())
                )
        );
    }

    private static void generateCompositePearlItem(
            ItemModelGenerators items,
            EngineItemEntry entry,
            PearlTint pearlTint
    ) {
        List<ItemModel.Unbaked> children = new ArrayList<>();

        if (pearlTint.hasBaseLayer()) {
            ResourceLocation baseModel = childModel(entry, "base");

            createSingleLayerModel(
                    items,
                    entry,
                    baseModel,
                    texture(entry, entry.baseTextureStemOrDefault())
            );

            children.add(
                    new BlockModelWrapper.Unbaked(
                            baseModel,
                            List.of()
                    )
            );
        }

        for (int layer = 0; layer < pearlTint.layerCount(); layer++) {
            ResourceLocation layerModel = childModel(entry, "tinted_" + layer);

            createSingleLayerModel(
                    items,
                    entry,
                    layerModel,
                    texture(
                            entry,
                            entry.tintedTextureStemOrDefault() + "_" + layer
                    )
            );

            children.add(
                    new BlockModelWrapper.Unbaked(
                            layerModel,
                            castTintSources(List.of(pearlTint.sourceForLayer(layer)))
                    )
            );
        }

        items.itemModelOutput.accept(
                entry.item().asItem(),
                new CompositeModel.Unbaked(List.copyOf(children))
        );
    }

    private static void createSingleLayerModel(
            ItemModelGenerators items,
            EngineItemEntry entry,
            ResourceLocation modelId,
            ResourceLocation texture
    ) {
        ModelTemplate template = new ModelTemplate(
                Optional.of(parentFor(entry)),
                Optional.empty(),
                TextureSlot.LAYER0
        );

        template.create(
                modelId,
                new TextureMapping()
                        .put(TextureSlot.LAYER0, texture),
                items.modelOutput
        );
    }

    private static TextureSlot[] requiredSlots(int totalLayerCount) {
        if (totalLayerCount < 1 || totalLayerCount > PearlTint.VANILLA_MAX_TOTAL_LAYERS) {
            throw new IllegalArgumentException("Invalid total item layer count: " + totalLayerCount);
        }

        TextureSlot[] slots = new TextureSlot[totalLayerCount];
        System.arraycopy(LAYER_SLOTS, 0, slots, 0, totalLayerCount);

        return slots;
    }

    public static List<ItemTintSource> tintSources(EngineItemEntry entry) {
        if (entry.itemTint() == null) {
            return List.of();
        }

        return castTintSources(entry.itemTint().sources());
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static List<ItemTintSource> castTintSources(List<?> sources) {
        return (List) sources;
    }

    private static ResourceLocation parentFor(EngineItemEntry entry) {
        return switch (entry.kind()) {
            case HANDHELD -> ResourceLocation.fromNamespaceAndPath("minecraft", "item/handheld");
            case FLAT, MODEL -> ResourceLocation.fromNamespaceAndPath("minecraft", "item/generated");
        };
    }

    private static ResourceLocation texture(EngineItemEntry entry, String path) {
        if (path.contains(":")) {
            return EngineIds.parse(path);
        }

        ResourceLocation id = EngineIds.itemId(entry.item());

        return ResourceLocation.fromNamespaceAndPath(
                id.getNamespace(),
                path.startsWith("item/") ? path : "item/" + path
        );
    }

    private static ResourceLocation childModel(EngineItemEntry entry, String suffix) {
        ResourceLocation id = EngineIds.itemId(entry.item());

        return ResourceLocation.fromNamespaceAndPath(
                id.getNamespace(),
                "item/" + id.getPath() + "_" + suffix
        );
    }
    //?}
}
