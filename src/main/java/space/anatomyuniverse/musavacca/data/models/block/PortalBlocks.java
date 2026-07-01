package space.anatomyuniverse.musavacca.data.models.block;

import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import space.anatomyuniverse.musavacca.data.models.unified.BlockTintRule;
import space.anatomyuniverse.musavacca.data.models.unified.ItemTint;
import space.anatomyuniverse.musavacca.data.models.unified.PearlTint;

import java.util.Arrays;

//? if <1.21.4 {
/*import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
*///?} else {
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.blockstates.MultiPartGenerator;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.renderer.block.model.Variant;
import net.neoforged.neoforge.client.model.generators.template.ExtendedModelTemplate;
import net.neoforged.neoforge.client.model.generators.template.ExtendedModelTemplateBuilder;
//?}

public final class PortalBlocks {
    private PortalBlocks() {}

    public enum TextureLayout {
        FOLDER,
        ROOT
    }

    public record Entry(
            Block block,
            String modelStem,
            String textureStem,
            TextureLayout textureLayout,
            ItemTint blockTint
    ) {
        public Entry {
            if (textureLayout == null) {
                textureLayout = TextureLayout.FOLDER;
            }
        }

        public static Entry auto(Block block, String stem, PearlTint pearlTint) {
            return new Entry(block, stem, stem, TextureLayout.FOLDER, pearlTint);
        }

        public static Entry root(Block block, String stem, PearlTint pearlTint) {
            return new Entry(block, stem, stem, TextureLayout.ROOT, pearlTint);
        }

        public static Entry folder(Block block, String stem, PearlTint pearlTint) {
            return new Entry(block, stem, stem, TextureLayout.FOLDER, pearlTint);
        }

        public Entry itemTint(ItemTint itemTint) {
            return new Entry(block, modelStem, textureStem, textureLayout, itemTint);
        }

        public Entry pearlTint(PearlTint pearlTint) {
            return itemTint(pearlTint);
        }

        public int layerCount() {
            return blockTint instanceof PearlTint pearlTint ? pearlTint.layerCount() : 1;
        }

        public int tintIndex(int layer) {
            return blockTint instanceof PearlTint pearlTint ? pearlTint.offset() + layer : layer;
        }

        public BlockTintRule[] blockTintRules() {
            return blockTint != null && blockTint.hasBlockTint()
                    ? new BlockTintRule[] { BlockTintRule.of(block, blockTint) }
                    : new BlockTintRule[0];
        }
    }

    public static void generate(
            //? if <1.21.4 {
            /*BlockStateProvider gen,
            *///?} else {
            BlockModelGenerators gen,
            //?}
            Entry... entries
    ) {
        if (entries == null) return;

        //? if <1.21.4 {
        /*return;
        *///?} else {
        Arrays.stream(entries)
                .filter(entry -> entry != null && entry.block() != null)
                .forEach(entry -> generateOne(gen, entry));
        //?}
    }

    //? if >=1.21.4 {
    private static void generateOne(BlockModelGenerators gen, Entry entry) {
        ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(entry.block());
        String namespace = blockId.getNamespace();

        TextureSlot[] slots = portalSlots(entry.layerCount());
        TextureMapping mapping = portalTextureMapping(namespace, entry, slots);

        ResourceLocation nsModel = nsTemplate(entry, slots).create(
                ResourceLocation.fromNamespaceAndPath(namespace, "block/" + entry.modelStem() + "_ns"),
                mapping,
                gen.modelOutput
        );

        ResourceLocation ewModel = ewTemplate(entry, slots).create(
                ResourceLocation.fromNamespaceAndPath(namespace, "block/" + entry.modelStem() + "_ew"),
                mapping,
                gen.modelOutput
        );

        ResourceLocation udModel = udTemplate(entry, slots).create(
                ResourceLocation.fromNamespaceAndPath(namespace, "block/" + entry.modelStem() + "_ud"),
                mapping,
                gen.modelOutput
        );

        MultiPartGenerator multi = MultiPartGenerator.multiPart(entry.block());
        multi = addAxisModel(multi, Direction.Axis.X, nsModel);
        multi = addAxisModel(multi, Direction.Axis.Z, ewModel);
        multi = addAxisModel(multi, Direction.Axis.Y, udModel);

        gen.blockStateOutput.accept(multi);
    }

    private static MultiPartGenerator addAxisModel(MultiPartGenerator multi, Direction.Axis axis, ResourceLocation model) {
        return multi.with(
                BlockModelGenerators.condition().term(BlockStateProperties.AXIS, axis),
                BlockModelGenerators.variant(new Variant(model))
        );
    }

    private static TextureMapping portalTextureMapping(String namespace, Entry entry, TextureSlot[] slots) {
        TextureMapping mapping = new TextureMapping();
        mapping.put(TextureSlot.PARTICLE, portalTexture(namespace, entry, 0));

        for (int layer = 0; layer < slots.length; ++layer) {
            mapping.put(slots[layer], portalTexture(namespace, entry, layer));
        }

        return mapping;
    }

    private static ExtendedModelTemplate nsTemplate(Entry entry, TextureSlot[] slots) {
        ExtendedModelTemplateBuilder builder = baseTemplateBuilder(slots);

        for (int layer = 0; layer < slots.length; ++layer) {
            addPaneLayer(builder, slots[layer], entry.tintIndex(layer), 0, 0, 6, 16, 16, 10, Direction.NORTH, Direction.SOUTH);
        }

        return builder.build();
    }

    private static ExtendedModelTemplate ewTemplate(Entry entry, TextureSlot[] slots) {
        ExtendedModelTemplateBuilder builder = baseTemplateBuilder(slots);

        for (int layer = 0; layer < slots.length; ++layer) {
            addPaneLayer(builder, slots[layer], entry.tintIndex(layer), 6, 0, 0, 10, 16, 16, Direction.EAST, Direction.WEST);
        }

        return builder.build();
    }

    private static ExtendedModelTemplate udTemplate(Entry entry, TextureSlot[] slots) {
        ExtendedModelTemplateBuilder builder = baseTemplateBuilder(slots);

        for (int layer = 0; layer < slots.length; ++layer) {
            addPaneLayer(builder, slots[layer], entry.tintIndex(layer), 0, 6, 0, 16, 10, 16, Direction.UP, Direction.DOWN);
        }

        return builder.build();
    }

    private static ExtendedModelTemplateBuilder baseTemplateBuilder(TextureSlot[] slots) {
        ExtendedModelTemplateBuilder builder = ExtendedModelTemplateBuilder.builder()
                .parent(ResourceLocation.fromNamespaceAndPath("minecraft", "block/block"))
                .requiredTextureSlot(TextureSlot.PARTICLE);

        for (TextureSlot slot : slots) {
            builder.requiredTextureSlot(slot);
        }

        return builder;
    }

    private static void addPaneLayer(
            ExtendedModelTemplateBuilder builder,
            TextureSlot slot,
            int tintIndex,
            int fromX,
            int fromY,
            int fromZ,
            int toX,
            int toY,
            int toZ,
            Direction faceA,
            Direction faceB
    ) {
        builder.element(element -> element
                .from(fromX, fromY, fromZ)
                .to(toX, toY, toZ)
                .face(faceA, face -> face.uvs(0, 0, 16, 16).texture(slot).tintindex(tintIndex))
                .face(faceB, face -> face.uvs(0, 0, 16, 16).texture(slot).tintindex(tintIndex))
        );
    }

    private static TextureSlot[] portalSlots(int layerCount) {
        TextureSlot[] slots = new TextureSlot[Math.max(1, layerCount)];

        for (int layer = 0; layer < slots.length; ++layer) {
            slots[layer] = TextureSlot.create("portal_" + layer);
        }

        return slots;
    }

    private static ResourceLocation portalTexture(String namespace, Entry entry, int layer) {
        String path = switch (entry.textureLayout()) {
            case FOLDER -> "block/" + entry.textureStem() + "/" + entry.textureStem() + "_" + layer;
            case ROOT -> "block/" + entry.textureStem() + "_" + layer;
        };

        return ResourceLocation.fromNamespaceAndPath(namespace, path);
    }
    //?}
}
