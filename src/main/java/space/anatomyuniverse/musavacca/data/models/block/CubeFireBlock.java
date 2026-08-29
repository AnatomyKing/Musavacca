package space.anatomyuniverse.musavacca.data.models.block;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FireBlock;

import java.util.Map;
import java.util.Optional;

//? if <1.21.4 {
/*import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.MultiPartBlockStateBuilder;
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

public final class CubeFireBlock {
    private CubeFireBlock() {}

    public record Entry(
            String modelStem,
            String textureStem,
            String upParent,
            String sideParent,
            String sideAltParent,
            String floorParent,
            String upAltParent
    ) {
        public static Entry auto(
                String stem,
                String upParent,
                String sideParent,
                String sideAltParent,
                String floorParent,
                String upAltParent
        ) {
            return new Entry(stem, stem, upParent, sideParent, sideAltParent, floorParent, upAltParent);
        }

        public static Entry of(
                String modelStem,
                String textureStem,
                String upParent,
                String sideParent,
                String sideAltParent,
                String floorParent,
                String upAltParent
        ) {
            return new Entry(modelStem, textureStem, upParent, sideParent, sideAltParent, floorParent, upAltParent);
        }
    }

    //? if <1.21.4 {
    /*public static void generate(BlockStateProvider gen, Map<Block, Entry> entries) {
        if (entries == null || entries.isEmpty()) return;

        entries.forEach((block, entry) -> {
            if (block == null || entry == null) return;

            ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(block);
            String ns = blockId.getNamespace();

            ResourceLocation tex0 = texture(ns, entry.textureStem(), 0);
            ResourceLocation tex1 = texture(ns, entry.textureStem(), 1);

            ModelFile floor0 = fireModel(gen, entry.modelStem() + "_floor0", entry.floorParent(), tex0);
            ModelFile floor1 = fireModel(gen, entry.modelStem() + "_floor1", entry.floorParent(), tex1);

            ModelFile side0 = fireModel(gen, entry.modelStem() + "_side0", entry.sideParent(), tex0);
            ModelFile side1 = fireModel(gen, entry.modelStem() + "_side1", entry.sideParent(), tex1);

            ModelFile sideAlt0 = fireModel(gen, entry.modelStem() + "_side_alt0", entry.sideAltParent(), tex0);
            ModelFile sideAlt1 = fireModel(gen, entry.modelStem() + "_side_alt1", entry.sideAltParent(), tex1);

            ModelFile up0 = fireModel(gen, entry.modelStem() + "_up0", entry.upParent(), tex0);
            ModelFile up1 = fireModel(gen, entry.modelStem() + "_up1", entry.upParent(), tex1);

            ModelFile upAlt0 = fireModel(gen, entry.modelStem() + "_up_alt0", entry.upAltParent(), tex0);
            ModelFile upAlt1 = fireModel(gen, entry.modelStem() + "_up_alt1", entry.upAltParent(), tex1);

            MultiPartBlockStateBuilder multipart = gen.getMultipartBuilder(block);

            addNoFaces(multipart, floor0, 0);
            addNoFaces(multipart, floor1, 0);

            addSideOrNoFaces(multipart, FireBlock.NORTH, side0, 0);
            addSideOrNoFaces(multipart, FireBlock.NORTH, side1, 0);
            addSideOrNoFaces(multipart, FireBlock.NORTH, sideAlt0, 0);
            addSideOrNoFaces(multipart, FireBlock.NORTH, sideAlt1, 0);

            addSideOrNoFaces(multipart, FireBlock.EAST, side0, 90);
            addSideOrNoFaces(multipart, FireBlock.EAST, side1, 90);
            addSideOrNoFaces(multipart, FireBlock.EAST, sideAlt0, 90);
            addSideOrNoFaces(multipart, FireBlock.EAST, sideAlt1, 90);

            addSideOrNoFaces(multipart, FireBlock.SOUTH, side0, 180);
            addSideOrNoFaces(multipart, FireBlock.SOUTH, side1, 180);
            addSideOrNoFaces(multipart, FireBlock.SOUTH, sideAlt0, 180);
            addSideOrNoFaces(multipart, FireBlock.SOUTH, sideAlt1, 180);

            addSideOrNoFaces(multipart, FireBlock.WEST, side0, 270);
            addSideOrNoFaces(multipart, FireBlock.WEST, side1, 270);
            addSideOrNoFaces(multipart, FireBlock.WEST, sideAlt0, 270);
            addSideOrNoFaces(multipart, FireBlock.WEST, sideAlt1, 270);

            addWhen(multipart, FireBlock.UP, up0, 0);
            addWhen(multipart, FireBlock.UP, up1, 0);
            addWhen(multipart, FireBlock.UP, upAlt0, 0);
            addWhen(multipart, FireBlock.UP, upAlt1, 0);
        });
    }

    private static ModelFile fireModel(BlockStateProvider gen, String name, String parentModelId, ResourceLocation fireTex) {
        return gen.models()
                .withExistingParent(name, ResourceLocation.parse(parentModelId))
                .texture("fire", fireTex);
    }

    private static ResourceLocation texture(String namespace, String textureStem, int frame) {
        return ResourceLocation.fromNamespaceAndPath(namespace, "block/" + textureStem + "_" + frame);
    }

    private static void addWhen(
            MultiPartBlockStateBuilder multipart,
            BooleanProperty property,
            ModelFile model,
            int yRot
    ) {
        var part = multipart.part().modelFile(model);
        if (yRot != 0) {
            part = part.rotationY(yRot);
        }
        part.addModel()
                .condition(property, true)
                .end();
    }

    private static void addNoFaces(
            MultiPartBlockStateBuilder multipart,
            ModelFile model,
            int yRot
    ) {
        var part = multipart.part().modelFile(model);
        if (yRot != 0) {
            part = part.rotationY(yRot);
        }
        part.addModel()
                .condition(FireBlock.EAST, false)
                .condition(FireBlock.NORTH, false)
                .condition(FireBlock.SOUTH, false)
                .condition(FireBlock.UP, false)
                .condition(FireBlock.WEST, false)
                .end();
    }

    private static void addSideOrNoFaces(
            MultiPartBlockStateBuilder multipart,
            BooleanProperty property,
            ModelFile model,
            int yRot
    ) {
        addWhen(multipart, property, model, yRot);
        addNoFaces(multipart, model, yRot);
    }
    *///?} else {
    public static void generate(BlockModelGenerators gen, Map<Block, Entry> entries) {
        if (entries == null || entries.isEmpty()) return;

        entries.forEach((block, entry) -> {
            if (block == null || entry == null) return;

            ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(block);
            String ns = blockId.getNamespace();

            ModelTemplate upTemplate = template(entry.upParent());
            ModelTemplate sideTemplate = template(entry.sideParent());
            ModelTemplate sideAltTemplate = template(entry.sideAltParent());
            ModelTemplate floorTemplate = template(entry.floorParent());
            ModelTemplate upAltTemplate = template(entry.upAltParent());

            TextureMapping tex0 = fireTexture(ns, entry.textureStem(), 0);
            TextureMapping tex1 = fireTexture(ns, entry.textureStem(), 1);

            ResourceLocation floor0 = floorTemplate.create(modelId(ns, entry.modelStem(), "floor0"), tex0, gen.modelOutput);
            ResourceLocation floor1 = floorTemplate.create(modelId(ns, entry.modelStem(), "floor1"), tex1, gen.modelOutput);

            ResourceLocation side0 = sideTemplate.create(modelId(ns, entry.modelStem(), "side0"), tex0, gen.modelOutput);
            ResourceLocation side1 = sideTemplate.create(modelId(ns, entry.modelStem(), "side1"), tex1, gen.modelOutput);

            ResourceLocation sideAlt0 = sideAltTemplate.create(modelId(ns, entry.modelStem(), "side_alt0"), tex0, gen.modelOutput);
            ResourceLocation sideAlt1 = sideAltTemplate.create(modelId(ns, entry.modelStem(), "side_alt1"), tex1, gen.modelOutput);

            ResourceLocation up0 = upTemplate.create(modelId(ns, entry.modelStem(), "up0"), tex0, gen.modelOutput);
            ResourceLocation up1 = upTemplate.create(modelId(ns, entry.modelStem(), "up1"), tex1, gen.modelOutput);

            ResourceLocation upAlt0 = upAltTemplate.create(modelId(ns, entry.modelStem(), "up_alt0"), tex0, gen.modelOutput);
            ResourceLocation upAlt1 = upAltTemplate.create(modelId(ns, entry.modelStem(), "up_alt1"), tex1, gen.modelOutput);

            MultiPartGenerator multipart = MultiPartGenerator.multiPart(block);

            //? if <1.21.5 {
            /*multipart = multipart.with(
                    noFaces(),
                    variant(floor0),
                    variant(floor1)
            );

            multipart = multipart.with(
                    Condition.or(
                            Condition.condition().term(FireBlock.NORTH, true),
                            noFaces()
                    ),
                    variant(side0),
                    variant(side1),
                    variant(sideAlt0),
                    variant(sideAlt1)
            );

            multipart = multipart.with(
                    Condition.or(
                            Condition.condition().term(FireBlock.EAST, true),
                            noFaces()
                    ),
                    yRot(side0, 90),
                    yRot(side1, 90),
                    yRot(sideAlt0, 90),
                    yRot(sideAlt1, 90)
            );

            multipart = multipart.with(
                    Condition.or(
                            Condition.condition().term(FireBlock.SOUTH, true),
                            noFaces()
                    ),
                    yRot(side0, 180),
                    yRot(side1, 180),
                    yRot(sideAlt0, 180),
                    yRot(sideAlt1, 180)
            );

            multipart = multipart.with(
                    Condition.or(
                            Condition.condition().term(FireBlock.WEST, true),
                            noFaces()
                    ),
                    yRot(side0, 270),
                    yRot(side1, 270),
                    yRot(sideAlt0, 270),
                    yRot(sideAlt1, 270)
            );

            multipart = multipart.with(
                    Condition.condition().term(FireBlock.UP, true),
                    variant(up0),
                    variant(up1),
                    variant(upAlt0),
                    variant(upAlt1)
            );
            *///?} else {
            multipart = multipart.with(
                    noFaces(),
                    BlockModelGenerators.variants(
                            new Variant(floor0),
                            new Variant(floor1)
                    )
            );

            multipart = multipart.with(
                    BlockModelGenerators.or(
                            BlockModelGenerators.condition().term(FireBlock.NORTH, true),
                            noFaces()
                    ),
                    BlockModelGenerators.variants(
                            new Variant(side0),
                            new Variant(side1),
                            new Variant(sideAlt0),
                            new Variant(sideAlt1)
                    )
            );

            multipart = multipart.with(
                    BlockModelGenerators.or(
                            BlockModelGenerators.condition().term(FireBlock.EAST, true),
                            noFaces()
                    ),
                    BlockModelGenerators.variants(
                            yRot(side0, 90),
                            yRot(side1, 90),
                            yRot(sideAlt0, 90),
                            yRot(sideAlt1, 90)
                    )
            );

            multipart = multipart.with(
                    BlockModelGenerators.or(
                            BlockModelGenerators.condition().term(FireBlock.SOUTH, true),
                            noFaces()
                    ),
                    BlockModelGenerators.variants(
                            yRot(side0, 180),
                            yRot(side1, 180),
                            yRot(sideAlt0, 180),
                            yRot(sideAlt1, 180)
                    )
            );

            multipart = multipart.with(
                    BlockModelGenerators.or(
                            BlockModelGenerators.condition().term(FireBlock.WEST, true),
                            noFaces()
                    ),
                    BlockModelGenerators.variants(
                            yRot(side0, 270),
                            yRot(side1, 270),
                            yRot(sideAlt0, 270),
                            yRot(sideAlt1, 270)
                    )
            );

            multipart = multipart.with(
                    BlockModelGenerators.condition().term(FireBlock.UP, true),
                    BlockModelGenerators.variants(
                            new Variant(up0),
                            new Variant(up1),
                            new Variant(upAlt0),
                            new Variant(upAlt1)
                    )
            );
            //?}

            gen.blockStateOutput.accept(multipart);
        });
    }

    private static ModelTemplate template(String parentModelId) {
        return new ModelTemplate(
                Optional.of(ResourceLocation.parse(parentModelId)),
                Optional.empty(),
                TextureSlot.FIRE
        );
    }

    private static TextureMapping fireTexture(String namespace, String textureStem, int frame) {
        return new TextureMapping().put(
                TextureSlot.FIRE,
                ResourceLocation.fromNamespaceAndPath(namespace, "block/" + textureStem + "_" + frame)
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

        if (degrees != 0) {
            variant = variant.with(VariantProperties.Y_ROT, rot(degrees));
        }

        return variant;
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

