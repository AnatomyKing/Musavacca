// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/data/models/block/CubeFireBlockTinted.java
package space.anatomyuniverse.musavacca.data.models.block;

import com.mojang.math.Quadrant;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.blockstates.ConditionBuilder;
import net.minecraft.client.data.models.blockstates.MultiPartGenerator;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.renderer.block.model.Variant;
import net.minecraft.client.renderer.block.model.VariantMutator;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FireBlock;

import java.util.Map;
import java.util.Optional;

public final class CubeFireBlockTinted {
    private CubeFireBlockTinted() {}

    /**
     * Extra texture:
     *   assets/<ns>/textures/block/<textureStem>_0_white.png
     *   assets/<ns>/textures/block/<textureStem>_1_white.png
     */
    private static final TextureSlot WHITE = TextureSlot.create("white");

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

            gen.blockStateOutput.accept(multipart);
        });
    }

    private static ModelTemplate template(String parentModelId) {
        return new ModelTemplate(
                Optional.of(ResourceLocation.parse(parentModelId)),
                Optional.empty(),
                TextureSlot.FIRE,
                WHITE
        );
    }

    private static TextureMapping fireTexture(String namespace, String textureStem, int frame) {
        return new TextureMapping()
                .put(
                        TextureSlot.FIRE,
                        ResourceLocation.fromNamespaceAndPath(namespace, "block/" + textureStem + "_" + frame)
                )
                .put(
                        WHITE,
                        ResourceLocation.fromNamespaceAndPath(namespace, "block/" + textureStem + "_" + frame + "_white")
                );
    }

    private static ResourceLocation modelId(String namespace, String stem, String suffix) {
        return ResourceLocation.fromNamespaceAndPath(namespace, "block/" + stem + "_" + suffix);
    }

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
}