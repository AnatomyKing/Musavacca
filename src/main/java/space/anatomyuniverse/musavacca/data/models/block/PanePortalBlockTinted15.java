// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/data/models/block/PanePortalBlockTinted15.java
package space.anatomyuniverse.musavacca.data.models.block;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
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

    /**
     * Generates vanilla-nether-portal-style pane models with 15 split texture layers.
     *
     * For block id pearl_portal and Entry.auto("pearl_portal"), this generates:
     *
     * assets/musavacca/models/block/pearl_portal_ns.json
     * assets/musavacca/models/block/pearl_portal_ew.json
     * assets/musavacca/blockstates/pearl_portal.json
     *
     * Expected split textures:
     *
     * assets/musavacca/textures/block/pearl_portal/pearl_portal_0.png
     * assets/musavacca/textures/block/pearl_portal/pearl_portal_1.png
     * ...
     * assets/musavacca/textures/block/pearl_portal/pearl_portal_14.png
     *
     * Tint index mapping:
     *
     * portal_0  -> tintindex 0
     * portal_1  -> tintindex 1
     * ...
     * portal_14 -> tintindex 14
     */
    public record Entry(String modelStem, String textureStem) {
        public static Entry auto(String stem) {
            return new Entry(stem, stem);
        }

        public static Entry of(String modelStem, String textureStem) {
            return new Entry(modelStem, textureStem);
        }
    }

    public static void generate(BlockModelGenerators gen, Map<Block, Entry> entries) {
        if (entries == null || entries.isEmpty()) return;

        entries.forEach((block, entry) -> {
            if (block == null || entry == null) return;
            if (entry.modelStem() == null || entry.modelStem().isBlank()) return;
            if (entry.textureStem() == null || entry.textureStem().isBlank()) return;

            generateOne(gen, block, entry);
        });
    }

    private static void generateOne(BlockModelGenerators gen, Block block, Entry entry) {
        ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(block);
        String namespace = blockId.getNamespace();

        TextureMapping mapping = portalTextureMapping(namespace, entry.textureStem());

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

        /*
         * Same axis mapping as vanilla nether_portal:
         *
         * axis=x -> *_ns
         * axis=z -> *_ew
         */
        gen.blockStateOutput.accept(
                MultiVariantGenerator.dispatch(block).with(
                        PropertyDispatch.initial(BlockStateProperties.HORIZONTAL_AXIS)
                                .select(Direction.Axis.X, BlockModelGenerators.plainVariant(nsModel))
                                .select(Direction.Axis.Z, BlockModelGenerators.plainVariant(ewModel))
                )
        );

        /*
         * No item model here.
         * Pearl portal should stay in ModBlocks.SKIP_BLOCK_ITEMS.
         */
    }

    private static TextureMapping portalTextureMapping(String namespace, String textureStem) {
        TextureMapping mapping = new TextureMapping();

        /*
         * Particle uses layer 0 as fallback.
         */
        mapping.put(TextureSlot.PARTICLE, portalTexture(namespace, textureStem, 0));

        for (int layer = 0; layer < PORTAL_LAYER_COUNT; ++layer) {
            mapping.put(
                    PORTAL_SLOTS[layer],
                    portalTexture(namespace, textureStem, layer)
            );
        }

        return mapping;
    }

    private static ExtendedModelTemplate nsTemplate() {
        ExtendedModelTemplateBuilder builder = ExtendedModelTemplateBuilder.builder()
                .requiredTextureSlot(TextureSlot.PARTICLE);

        for (TextureSlot slot : PORTAL_SLOTS) {
            builder.requiredTextureSlot(slot);
        }

        for (int layer = 0; layer < PORTAL_LAYER_COUNT; ++layer) {
            final int tintIndex = layer;
            final TextureSlot slot = PORTAL_SLOTS[layer];

            builder.element(element -> element
                    .from(0, 0, 6)
                    .to(16, 16, 10)
                    .face(Direction.NORTH, face -> face
                            .uvs(0, 0, 16, 16)
                            .texture(slot)
                            .tintindex(tintIndex)
                    )
                    .face(Direction.SOUTH, face -> face
                            .uvs(0, 0, 16, 16)
                            .texture(slot)
                            .tintindex(tintIndex)
                    )
            );
        }

        return builder.build();
    }

    private static ExtendedModelTemplate ewTemplate() {
        ExtendedModelTemplateBuilder builder = ExtendedModelTemplateBuilder.builder()
                .requiredTextureSlot(TextureSlot.PARTICLE);

        for (TextureSlot slot : PORTAL_SLOTS) {
            builder.requiredTextureSlot(slot);
        }

        for (int layer = 0; layer < PORTAL_LAYER_COUNT; ++layer) {
            final int tintIndex = layer;
            final TextureSlot slot = PORTAL_SLOTS[layer];

            builder.element(element -> element
                    .from(6, 0, 0)
                    .to(10, 16, 16)
                    .face(Direction.EAST, face -> face
                            .uvs(0, 0, 16, 16)
                            .texture(slot)
                            .tintindex(tintIndex)
                    )
                    .face(Direction.WEST, face -> face
                            .uvs(0, 0, 16, 16)
                            .texture(slot)
                            .tintindex(tintIndex)
                    )
            );
        }

        return builder.build();
    }

    private static TextureSlot[] createPortalSlots() {
        TextureSlot[] slots = new TextureSlot[PORTAL_LAYER_COUNT];

        for (int layer = 0; layer < PORTAL_LAYER_COUNT; ++layer) {
            slots[layer] = TextureSlot.create("portal_" + layer);
        }

        return slots;
    }

    private static ResourceLocation portalTexture(String namespace, String textureStem, int layer) {
        return ResourceLocation.fromNamespaceAndPath(
                namespace,
                "block/" + textureStem + "/" + textureStem + "_" + layer
        );
    }
}