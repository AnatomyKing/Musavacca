// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/data/models/block/PanePortalBlockTinted15.java
package space.anatomyuniverse.musavacca.data.models.block;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.blockstates.MultiPartGenerator;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.renderer.block.model.Variant;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.generators.template.ExtendedModelTemplate;
import net.neoforged.neoforge.client.model.generators.template.ExtendedModelTemplateBuilder;

import java.util.Map;

public final class PanePortalBlockTinted15 {
    private static final int PORTAL_LAYER_COUNT = 15;
    private static final TextureSlot[] PORTAL_SLOTS = createPortalSlots();

    private PanePortalBlockTinted15() {}

    /*
     * Generates:
     *
     * assets/<namespace>/models/block/<modelStem>_ns.json
     * assets/<namespace>/models/block/<modelStem>_ew.json
     * assets/<namespace>/models/block/<modelStem>_ud.json
     * assets/<namespace>/blockstates/<block_id>.json
     *
     * Default texture layout for Entry.auto("pearl_portal"):
     *
     * assets/<namespace>/textures/block/pearl_portal/pearl_portal_0.png
     * ...
     * assets/<namespace>/textures/block/pearl_portal/pearl_portal_14.png
     *
     * Blockstate:
     *
     * axis=x -> *_ns model
     * axis=z -> *_ew model
     * axis=y -> *_ud model
     */
    public enum TextureLayout {
        FOLDER,
        ROOT
    }

    public record Entry(String modelStem, String textureStem, TextureLayout textureLayout) {
        public Entry {
            if (textureLayout == null) {
                textureLayout = TextureLayout.FOLDER;
            }
        }

        public static Entry auto(String stem) {
            return new Entry(stem, stem, TextureLayout.FOLDER);
        }

        /*
         * Use this only if your textures are here instead:
         *
         * assets/<namespace>/textures/block/pearl_portal_0.png
         * ...
         * assets/<namespace>/textures/block/pearl_portal_14.png
         */
        public static Entry root(String stem) {
            return new Entry(stem, stem, TextureLayout.ROOT);
        }

        public static Entry folder(String stem) {
            return new Entry(stem, stem, TextureLayout.FOLDER);
        }

        public static Entry of(String modelStem, String textureStem) {
            return new Entry(modelStem, textureStem, TextureLayout.FOLDER);
        }

        public static Entry of(String modelStem, String textureStem, TextureLayout textureLayout) {
            return new Entry(modelStem, textureStem, textureLayout);
        }
    }

    public static void generate(BlockModelGenerators gen, Map<Block, Entry> entries) {
        if (entries == null || entries.isEmpty()) {
            return;
        }

        entries.forEach((block, entry) -> {
            if (block == null || entry == null) {
                return;
            }

            if (isBlank(entry.modelStem()) || isBlank(entry.textureStem())) {
                return;
            }

            generateOne(gen, block, entry);
        });
    }

    private static void generateOne(BlockModelGenerators gen, Block block, Entry entry) {
        ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(block);
        String namespace = blockId.getNamespace();

        TextureMapping mapping = portalTextureMapping(
                namespace,
                entry.textureStem(),
                entry.textureLayout()
        );

        ResourceLocation nsModel = nsTemplate().create(
                ResourceLocation.fromNamespaceAndPath(namespace, "block/" + entry.modelStem() + "_ns"),
                mapping,
                gen.modelOutput
        );

        ResourceLocation ewModel = ewTemplate().create(
                ResourceLocation.fromNamespaceAndPath(namespace, "block/" + entry.modelStem() + "_ew"),
                mapping,
                gen.modelOutput
        );

        ResourceLocation udModel = udTemplate().create(
                ResourceLocation.fromNamespaceAndPath(namespace, "block/" + entry.modelStem() + "_ud"),
                mapping,
                gen.modelOutput
        );

        /*
         * 1.21.8-safe style.
         *
         * This matches your other wrappers:
         * - CubeVocoTable
         * - CubeVocoPost
         * - BreakBlockOwn
         * - SmallBananaPearlOwn
         *
         * It generates a multipart blockstate where exactly one model applies
         * for each axis value.
         */
        MultiPartGenerator multi = MultiPartGenerator.multiPart(block);

        multi = addAxisModel(multi, Direction.Axis.X, nsModel);
        multi = addAxisModel(multi, Direction.Axis.Z, ewModel);
        multi = addAxisModel(multi, Direction.Axis.Y, udModel);

        gen.blockStateOutput.accept(multi);

        /*
         * No item model here.
         * Pearl portal should stay hidden / skipped as a block item.
         */
    }

    private static MultiPartGenerator addAxisModel(
            MultiPartGenerator multi,
            Direction.Axis axis,
            ResourceLocation model
    ) {
        return multi.with(
                BlockModelGenerators.condition()
                        .term(BlockStateProperties.AXIS, axis),
                BlockModelGenerators.variant(new Variant(model))
        );
    }

    private static TextureMapping portalTextureMapping(
            String namespace,
            String textureStem,
            TextureLayout textureLayout
    ) {
        TextureMapping mapping = new TextureMapping();

        mapping.put(
                TextureSlot.PARTICLE,
                portalTexture(namespace, textureStem, textureLayout, 0)
        );

        for (int layer = 0; layer < PORTAL_LAYER_COUNT; ++layer) {
            mapping.put(
                    PORTAL_SLOTS[layer],
                    portalTexture(namespace, textureStem, textureLayout, layer)
            );
        }

        return mapping;
    }

    private static ExtendedModelTemplate nsTemplate() {
        ExtendedModelTemplateBuilder builder = baseTemplateBuilder();

        for (int layer = 0; layer < PORTAL_LAYER_COUNT; ++layer) {
            addPaneLayer(
                    builder,
                    layer,
                    0, 0, 6,
                    16, 16, 10,
                    Direction.NORTH,
                    Direction.SOUTH
            );
        }

        return builder.build();
    }

    private static ExtendedModelTemplate ewTemplate() {
        ExtendedModelTemplateBuilder builder = baseTemplateBuilder();

        for (int layer = 0; layer < PORTAL_LAYER_COUNT; ++layer) {
            addPaneLayer(
                    builder,
                    layer,
                    6, 0, 0,
                    10, 16, 16,
                    Direction.EAST,
                    Direction.WEST
            );
        }

        return builder.build();
    }

    private static ExtendedModelTemplate udTemplate() {
        ExtendedModelTemplateBuilder builder = baseTemplateBuilder();

        for (int layer = 0; layer < PORTAL_LAYER_COUNT; ++layer) {
            addPaneLayer(
                    builder,
                    layer,
                    0, 6, 0,
                    16, 10, 16,
                    Direction.UP,
                    Direction.DOWN
            );
        }

        return builder.build();
    }

    private static ExtendedModelTemplateBuilder baseTemplateBuilder() {
        ExtendedModelTemplateBuilder builder = ExtendedModelTemplateBuilder.builder()
                .parent(ResourceLocation.fromNamespaceAndPath("minecraft", "block/block"))
                .requiredTextureSlot(TextureSlot.PARTICLE);

        for (TextureSlot slot : PORTAL_SLOTS) {
            builder.requiredTextureSlot(slot);
        }

        return builder;
    }

    private static void addPaneLayer(
            ExtendedModelTemplateBuilder builder,
            int layer,
            int fromX,
            int fromY,
            int fromZ,
            int toX,
            int toY,
            int toZ,
            Direction faceA,
            Direction faceB
    ) {
        TextureSlot slot = PORTAL_SLOTS[layer];

        builder.element(element -> element
                .from(fromX, fromY, fromZ)
                .to(toX, toY, toZ)
                .face(faceA, face -> face
                        .uvs(0, 0, 16, 16)
                        .texture(slot)
                        .tintindex(layer)
                )
                .face(faceB, face -> face
                        .uvs(0, 0, 16, 16)
                        .texture(slot)
                        .tintindex(layer)
                )
        );
    }

    private static TextureSlot[] createPortalSlots() {
        TextureSlot[] slots = new TextureSlot[PORTAL_LAYER_COUNT];

        for (int layer = 0; layer < PORTAL_LAYER_COUNT; ++layer) {
            slots[layer] = TextureSlot.create("portal_" + layer);
        }

        return slots;
    }

    private static ResourceLocation portalTexture(
            String namespace,
            String textureStem,
            TextureLayout textureLayout,
            int layer
    ) {
        String path = switch (textureLayout) {
            case FOLDER -> "block/" + textureStem + "/" + textureStem + "_" + layer;
            case ROOT -> "block/" + textureStem + "_" + layer;
        };

        return ResourceLocation.fromNamespaceAndPath(namespace, path);
    }

    private static boolean isBlank(String text) {
        return text == null || text.isBlank();
    }
}