package space.anatomyuniverse.musavacca.data.models.block;

import com.mojang.math.Quadrant;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.blockstates.MultiPartGenerator;
import net.minecraft.client.renderer.block.model.Variant;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import space.anatomyuniverse.musavacca.block.custom.VocoPearlCandleBlock;
import space.anatomyuniverse.musavacca.block.custom.VocoPearlCandleBlock.Corner;

import java.util.Map;

public final class CubeVocoPearlCandle {
    private CubeVocoPearlCandle() {}

    public record Models(
            String candleOneNorthEast,
            String candleTwoNorthEast,
            String candleThreeNorthEast,
            String candleFourNorthEast,
            String portalNorthEast,
            String portalSouthEast,
            String portalSouthWest,
            String portalNorthWest
    ) {
        public ResourceLocation candleModel(int candles) {
            return switch (candles) {
                case 1 -> ResourceLocation.parse(this.candleOneNorthEast);
                case 2 -> ResourceLocation.parse(this.candleTwoNorthEast);
                case 3 -> ResourceLocation.parse(this.candleThreeNorthEast);
                case 4 -> ResourceLocation.parse(this.candleFourNorthEast);
                default -> throw new IllegalArgumentException("Unsupported candle count: " + candles);
            };
        }

        public ResourceLocation portalModel(Corner corner) {
            return switch (corner) {
                case NORTH_EAST -> ResourceLocation.parse(this.portalNorthEast);
                case SOUTH_EAST -> ResourceLocation.parse(this.portalSouthEast);
                case SOUTH_WEST -> ResourceLocation.parse(this.portalSouthWest);
                case NORTH_WEST -> ResourceLocation.parse(this.portalNorthWest);
            };
        }
    }

    public static void generate(BlockModelGenerators gen, Map<Block, Models> models) {
        if (models == null || models.isEmpty()) return;

        models.forEach((block, stateModels) -> {
            if (!(block instanceof VocoPearlCandleBlock) || stateModels == null) return;

            MultiPartGenerator multi = MultiPartGenerator.multiPart(block);

            for (Corner corner : Corner.values()) {
                for (int candles = 1; candles <= 4; candles++) {
                    multi = addCandle(
                            multi,
                            corner,
                            candles,
                            stateModels.candleModel(candles)
                    );
                }

                multi = addPortal(
                        multi,
                        corner,
                        stateModels.portalModel(corner)
                );
            }

            gen.blockStateOutput.accept(multi);

            /*
             * Intentionally no item model.
             * VocoPearlCandleBlock is a hidden technical block.
             */
        });
    }

    private static MultiPartGenerator addCandle(
            MultiPartGenerator multi,
            Corner corner,
            int candles,
            ResourceLocation modelId
    ) {
        return multi.with(
                BlockModelGenerators.condition()
                        .term(VocoPearlCandleBlock.CORNER, corner)
                        .term(VocoPearlCandleBlock.CANDLES, candles),
                BlockModelGenerators.variant(variant(modelId, corner.yRotation()))
        );
    }

    private static MultiPartGenerator addPortal(
            MultiPartGenerator multi,
            Corner corner,
            ResourceLocation modelId
    ) {
        return multi.with(
                BlockModelGenerators.condition()
                        .term(VocoPearlCandleBlock.CORNER, corner)
                        .term(VocoPearlCandleBlock.PORTAL, true),
                BlockModelGenerators.variant(new Variant(modelId))
        );
    }

    private static Variant variant(ResourceLocation modelId, int yRot) {
        Variant variant = new Variant(modelId);
        Quadrant rotation = rotation(yRot);

        return rotation == Quadrant.R0
                ? variant
                : variant.withYRot(rotation);
    }

    private static Quadrant rotation(int yRot) {
        return switch (Math.floorMod(yRot, 360)) {
            case 90 -> Quadrant.R90;
            case 180 -> Quadrant.R180;
            case 270 -> Quadrant.R270;
            default -> Quadrant.R0;
        };
    }
}