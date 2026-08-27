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

public final class PanePortalTintedBlock {
    private PanePortalTintedBlock() {}

    private static final TextureSlot PORTAL = TextureSlot.create("portal");

    /**
     * Generates vanilla-nether-portal-style pane models with tintindex support.
     *
     * For block id pearl_portal and Entry.auto("pearl_portal"), this generates:
     *
     * assets/musavacca/models/block/pearl_portal_ns.json
     * assets/musavacca/models/block/pearl_portal_ew.json
     * assets/musavacca/blockstates/pearl_portal.json
     *
     * Expected texture:
     * assets/musavacca/textures/block/pearl_portal.png
     */
    public record Entry(String modelStem, String textureStem, int tintIndex) {
        public static Entry auto(String stem) {
            return new Entry(stem, stem, 0);
        }

        public static Entry of(String modelStem, String textureStem, int tintIndex) {
            return new Entry(modelStem, textureStem, tintIndex);
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

        ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(
                namespace,
                "block/" + entry.textureStem()
        );

        TextureMapping mapping = new TextureMapping()
                .put(TextureSlot.PARTICLE, texture)
                .put(PORTAL, texture);

        ResourceLocation nsModel = nsTemplate(entry.tintIndex()).create(
                ResourceLocation.fromNamespaceAndPath(namespace, "block/" + entry.modelStem() + "_ns"),
                mapping,
                gen.modelOutput
        );

        ResourceLocation ewModel = ewTemplate(entry.tintIndex()).create(
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

    private static ExtendedModelTemplate nsTemplate(int tintIndex) {
        return ExtendedModelTemplateBuilder.builder()
                .requiredTextureSlot(TextureSlot.PARTICLE)
                .requiredTextureSlot(PORTAL)
                .element(element -> element
                        .from(0, 0, 6)
                        .to(16, 16, 10)
                        .face(Direction.NORTH, face -> face
                                .uvs(0, 0, 16, 16)
                                .texture(PORTAL)
                                .tintindex(tintIndex)
                        )
                        .face(Direction.SOUTH, face -> face
                                .uvs(0, 0, 16, 16)
                                .texture(PORTAL)
                                .tintindex(tintIndex)
                        )
                )
                .build();
    }

    private static ExtendedModelTemplate ewTemplate(int tintIndex) {
        return ExtendedModelTemplateBuilder.builder()
                .requiredTextureSlot(TextureSlot.PARTICLE)
                .requiredTextureSlot(PORTAL)
                .element(element -> element
                        .from(6, 0, 0)
                        .to(10, 16, 16)
                        .face(Direction.EAST, face -> face
                                .uvs(0, 0, 16, 16)
                                .texture(PORTAL)
                                .tintindex(tintIndex)
                        )
                        .face(Direction.WEST, face -> face
                                .uvs(0, 0, 16, 16)
                                .texture(PORTAL)
                                .tintindex(tintIndex)
                        )
                )
                .build();
    }
}