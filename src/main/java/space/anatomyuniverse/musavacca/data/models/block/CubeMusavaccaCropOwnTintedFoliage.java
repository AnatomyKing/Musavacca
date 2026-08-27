package space.anatomyuniverse.musavacca.data.models.block;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SaplingBlock;
import space.anatomyuniverse.musavacca.block.custom.MusavaccaCropBlock;

import java.util.Map;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.blockstates.MultiPartGenerator;
import net.minecraft.client.renderer.block.model.Variant;

public final class CubeMusavaccaCropOwnTintedFoliage {
    private CubeMusavaccaCropOwnTintedFoliage() {}

    /**
     * Existing custom model for one crop/sapling block.
     *
     * Example:
     * "musavacca:block/musavacca_sprout"
     *
     * This file only generates blockstates.
     * The model JSON itself must already exist in:
     * assets/musavacca/models/block/musavacca_sprout.json
     *
     * For foliage tinting, faces in that model need:
     * "tintindex": 0
     */
    public record Models(String modelId) {
        public ResourceLocation model() {
            return ResourceLocation.parse(modelId);
        }
    }

    public static Models of(String modelId) {
        return new Models(modelId);
    }

    public static void generate(BlockModelGenerators blocks, Map<Block, Models> models) {
        if (models == null || models.isEmpty()) return;

        models.forEach((block, modelEntry) -> {
            if (block == null || modelEntry == null) return;
            if (modelEntry.modelId() == null || modelEntry.modelId().isBlank()) return;

            ResourceLocation model = modelEntry.model();

            if (block.defaultBlockState().hasProperty(MusavaccaCropBlock.AGE)) {
                generateCropBlock(blocks, block, model);
                return;
            }

            if (block.defaultBlockState().hasProperty(SaplingBlock.STAGE)) {
                generateSaplingBlock(blocks, block, model);
                return;
            }

            generatePlainBlock(blocks, block, model);
        });
    }

    private static void generateCropBlock(
            BlockModelGenerators blocks,
            Block block,
            ResourceLocation model
    ) {
        MultiPartGenerator multi = MultiPartGenerator.multiPart(block);

        /*
         * Every internal AGE value points to the same model for THIS block.
         *
         * Your real visual stage switch happens because MusavaccaCropBlock#getStateForAge()
         * swaps to a different registered block:
         *
         * age 0 -> MUSAVACCA_SPROUT
         * age 1 -> MUSAVACCA_SUCKER
         * age 2 -> MUSAVACCA_PLANT
         * age 3 -> MUSAVACCA_PSEUDOSTEM
         */
        multi = addAge(multi, 0, model);
        multi = addAge(multi, 1, model);
        multi = addAge(multi, 2, model);
        multi = addAge(multi, 3, model);

        blocks.blockStateOutput.accept(multi);
    }

    private static void generateSaplingBlock(
            BlockModelGenerators blocks,
            Block block,
            ResourceLocation model
    ) {
        MultiPartGenerator multi = MultiPartGenerator.multiPart(block);

        /*
         * SaplingBlock has STAGE 0 and STAGE 1.
         * Both point to the same pseudostem model.
         */
        multi = addSaplingStage(multi, 0, model);
        multi = addSaplingStage(multi, 1, model);

        blocks.blockStateOutput.accept(multi);
    }

    private static void generatePlainBlock(
            BlockModelGenerators blocks,
            Block block,
            ResourceLocation model
    ) {
        blocks.blockStateOutput.accept(
                MultiPartGenerator.multiPart(block)
                        .with(BlockModelGenerators.variant(new Variant(model)))
        );
    }

    private static MultiPartGenerator addAge(
            MultiPartGenerator multi,
            int age,
            ResourceLocation model
    ) {
        return multi.with(
                BlockModelGenerators.condition()
                        .term(MusavaccaCropBlock.AGE, age),
                BlockModelGenerators.variant(new Variant(model))
        );
    }

    private static MultiPartGenerator addSaplingStage(
            MultiPartGenerator multi,
            int stage,
            ResourceLocation model
    ) {
        return multi.with(
                BlockModelGenerators.condition()
                        .term(SaplingBlock.STAGE, stage),
                BlockModelGenerators.variant(new Variant(model))
        );
    }
}