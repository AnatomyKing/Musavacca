package space.anatomyuniverse.musavacca.data.models.block;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FireBlock;
import space.anatomyuniverse.musavacca.data.models.unified.BlockTintRule;
import space.anatomyuniverse.musavacca.data.models.unified.ItemTint;
import space.anatomyuniverse.musavacca.data.models.unified.PearlTint;

import java.util.Arrays;
import java.util.Optional;

//? if <1.21.4 {
/*import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
*///?} else {
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.blockstates.MultiPartGenerator;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;

//? if <1.21.5 {
/*import net.minecraft.client.data.models.blockstates.Condition;
import net.minecraft.client.data.models.blockstates.Variant;
import net.minecraft.client.data.models.blockstates.VariantProperties;
*///?} else {
import com.mojang.math.Quadrant;
import net.minecraft.client.data.models.blockstates.ConditionBuilder;
import net.minecraft.client.renderer.block.model.Variant;
import net.minecraft.client.renderer.block.model.VariantMutator;
//?}
//?}

public final class FireBlocks {
    private FireBlocks() {}

    public record Entry(
            Block block,
            String modelStem,
            String textureStem,
            ItemTint blockTint,
            String upParent,
            String sideParent,
            String sideAltParent,
            String floorParent,
            String upAltParent
    ) {
        public static Entry auto(
                Block block,
                String stem,
                PearlTint pearlTint,
                String upParent,
                String sideParent,
                String sideAltParent,
                String floorParent,
                String upAltParent
        ) {
            return new Entry(block, stem, stem, pearlTint, upParent, sideParent, sideAltParent, floorParent, upAltParent);
        }

        public static Entry of(
                Block block,
                String modelStem,
                String textureStem,
                PearlTint pearlTint,
                String upParent,
                String sideParent,
                String sideAltParent,
                String floorParent,
                String upAltParent
        ) {
            return new Entry(block, modelStem, textureStem, pearlTint, upParent, sideParent, sideAltParent, floorParent, upAltParent);
        }

        public Entry itemTint(ItemTint itemTint) {
            return new Entry(block, modelStem, textureStem, itemTint, upParent, sideParent, sideAltParent, floorParent, upAltParent);
        }

        public Entry pearlTint(PearlTint pearlTint) {
            return itemTint(pearlTint);
        }

        public int layerCount() {
            return blockTint instanceof PearlTint pearlTint ? pearlTint.layerCount() : 2;
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

        TextureSlot[] fireSlots = fireSlots(entry.layerCount());

        ModelTemplate upTemplate = template(entry.upParent(), fireSlots);
        ModelTemplate sideTemplate = template(entry.sideParent(), fireSlots);
        ModelTemplate sideAltTemplate = template(entry.sideAltParent(), fireSlots);
        ModelTemplate floorTemplate = template(entry.floorParent(), fireSlots);
        ModelTemplate upAltTemplate = template(entry.upAltParent(), fireSlots);

        TextureMapping tex0 = fireTextureMapping(namespace, entry.textureStem(), 0, fireSlots);
        TextureMapping tex1 = fireTextureMapping(namespace, entry.textureStem(), 1, fireSlots);

        ResourceLocation floor0 = floorTemplate.create(modelId(namespace, entry.modelStem(), "floor0"), tex0, gen.modelOutput);
        ResourceLocation floor1 = floorTemplate.create(modelId(namespace, entry.modelStem(), "floor1"), tex1, gen.modelOutput);

        ResourceLocation side0 = sideTemplate.create(modelId(namespace, entry.modelStem(), "side0"), tex0, gen.modelOutput);
        ResourceLocation side1 = sideTemplate.create(modelId(namespace, entry.modelStem(), "side1"), tex1, gen.modelOutput);

        ResourceLocation sideAlt0 = sideAltTemplate.create(modelId(namespace, entry.modelStem(), "side_alt0"), tex0, gen.modelOutput);
        ResourceLocation sideAlt1 = sideAltTemplate.create(modelId(namespace, entry.modelStem(), "side_alt1"), tex1, gen.modelOutput);

        ResourceLocation up0 = upTemplate.create(modelId(namespace, entry.modelStem(), "up0"), tex0, gen.modelOutput);
        ResourceLocation up1 = upTemplate.create(modelId(namespace, entry.modelStem(), "up1"), tex1, gen.modelOutput);

        ResourceLocation upAlt0 = upAltTemplate.create(modelId(namespace, entry.modelStem(), "up_alt0"), tex0, gen.modelOutput);
        ResourceLocation upAlt1 = upAltTemplate.create(modelId(namespace, entry.modelStem(), "up_alt1"), tex1, gen.modelOutput);

        MultiPartGenerator multipart = MultiPartGenerator.multiPart(entry.block());

        //? if <1.21.5 {
        /*multipart = multipart.with(noFaces(), variant(floor0), variant(floor1));
        multipart = multipart.with(Condition.or(Condition.condition().term(FireBlock.NORTH, true), noFaces()), variant(side0), variant(side1), variant(sideAlt0), variant(sideAlt1));
        multipart = multipart.with(Condition.or(Condition.condition().term(FireBlock.EAST, true), noFaces()), yRot(side0, 90), yRot(side1, 90), yRot(sideAlt0, 90), yRot(sideAlt1, 90));
        multipart = multipart.with(Condition.or(Condition.condition().term(FireBlock.SOUTH, true), noFaces()), yRot(side0, 180), yRot(side1, 180), yRot(sideAlt0, 180), yRot(sideAlt1, 180));
        multipart = multipart.with(Condition.or(Condition.condition().term(FireBlock.WEST, true), noFaces()), yRot(side0, 270), yRot(side1, 270), yRot(sideAlt0, 270), yRot(sideAlt1, 270));
        multipart = multipart.with(Condition.condition().term(FireBlock.UP, true), variant(up0), variant(up1), variant(upAlt0), variant(upAlt1));
        *///?} else {
        multipart = multipart.with(noFaces(), BlockModelGenerators.variants(new Variant(floor0), new Variant(floor1)));
        multipart = multipart.with(BlockModelGenerators.or(BlockModelGenerators.condition().term(FireBlock.NORTH, true), noFaces()), BlockModelGenerators.variants(new Variant(side0), new Variant(side1), new Variant(sideAlt0), new Variant(sideAlt1)));
        multipart = multipart.with(BlockModelGenerators.or(BlockModelGenerators.condition().term(FireBlock.EAST, true), noFaces()), BlockModelGenerators.variants(yRot(side0, 90), yRot(side1, 90), yRot(sideAlt0, 90), yRot(sideAlt1, 90)));
        multipart = multipart.with(BlockModelGenerators.or(BlockModelGenerators.condition().term(FireBlock.SOUTH, true), noFaces()), BlockModelGenerators.variants(yRot(side0, 180), yRot(side1, 180), yRot(sideAlt0, 180), yRot(sideAlt1, 180)));
        multipart = multipart.with(BlockModelGenerators.or(BlockModelGenerators.condition().term(FireBlock.WEST, true), noFaces()), BlockModelGenerators.variants(yRot(side0, 270), yRot(side1, 270), yRot(sideAlt0, 270), yRot(sideAlt1, 270)));
        multipart = multipart.with(BlockModelGenerators.condition().term(FireBlock.UP, true), BlockModelGenerators.variants(new Variant(up0), new Variant(up1), new Variant(upAlt0), new Variant(upAlt1)));
        //?}

        gen.blockStateOutput.accept(multipart);
    }

    private static ModelTemplate template(String parentModelId, TextureSlot[] fireSlots) {
        return new ModelTemplate(
                Optional.of(ResourceLocation.parse(parentModelId)),
                Optional.empty(),
                fireSlots
        );
    }

    private static TextureMapping fireTextureMapping(String namespace, String textureStem, int frame, TextureSlot[] fireSlots) {
        TextureMapping mapping = new TextureMapping();

        for (int layer = 0; layer < fireSlots.length; ++layer) {
            mapping.put(fireSlots[layer], fireTexture(namespace, textureStem, frame, layer));
        }

        return mapping;
    }

    private static TextureSlot[] fireSlots(int layerCount) {
        TextureSlot[] slots = new TextureSlot[Math.max(1, layerCount)];

        for (int layer = 0; layer < slots.length; ++layer) {
            slots[layer] = TextureSlot.create("fire_" + layer);
        }

        return slots;
    }

    private static ResourceLocation fireTexture(String namespace, String textureStem, int frame, int layer) {
        String frameStem = textureStem + "_" + frame;
        return ResourceLocation.fromNamespaceAndPath(
                namespace,
                "block/" + textureStem + "/" + frameStem + "/" + frameStem + "_" + layer
        );
    }

    private static ResourceLocation modelId(String namespace, String stem, String suffix) {
        return ResourceLocation.fromNamespaceAndPath(namespace, "block/" + stem + "_" + suffix);
    }

    //? if <1.21.5 {
    /*private static Variant variant(ResourceLocation model) {
        return Variant.variant().with(VariantProperties.MODEL, model);
    }

    private static Variant yRot(ResourceLocation model, int degrees) {
        Variant variant = variant(model);
        return degrees == 0 ? variant : variant.with(VariantProperties.Y_ROT, rot(degrees));
    }

    private static VariantProperties.Rotation rot(int degrees) {
        return switch (Math.floorMod(degrees, 360)) {
            case 0 -> VariantProperties.Rotation.R0;
            case 90 -> VariantProperties.Rotation.R90;
            case 180 -> VariantProperties.Rotation.R180;
            case 270 -> VariantProperties.Rotation.R270;
            default -> throw new IllegalArgumentException("Unsupported rotation: " + degrees);
        };
    }

    private static Condition noFaces() {
        return Condition.condition()
                .term(FireBlock.EAST, false)
                .term(FireBlock.NORTH, false)
                .term(FireBlock.SOUTH, false)
                .term(FireBlock.UP, false)
                .term(FireBlock.WEST, false);
    }
    *///?} else {
    private static Variant yRot(ResourceLocation model, int degrees) {
        Variant variant = new Variant(model);

        if (degrees != 0) {
            variant = variant.with(VariantMutator.Y_ROT.withValue(quadrant(degrees)));
        }

        return variant;
    }

    private static Quadrant quadrant(int degrees) {
        return switch (Math.floorMod(degrees, 360)) {
            case 0 -> Quadrant.R0;
            case 90 -> Quadrant.R90;
            case 180 -> Quadrant.R180;
            case 270 -> Quadrant.R270;
            default -> throw new IllegalArgumentException("Unsupported rotation: " + degrees);
        };
    }

    private static ConditionBuilder noFaces() {
        return BlockModelGenerators.condition()
                .term(FireBlock.EAST, false)
                .term(FireBlock.NORTH, false)
                .term(FireBlock.SOUTH, false)
                .term(FireBlock.UP, false)
                .term(FireBlock.WEST, false);
    }
    //?}
    //?}
}
