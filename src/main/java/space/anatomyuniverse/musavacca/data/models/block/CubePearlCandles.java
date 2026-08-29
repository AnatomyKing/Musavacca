package space.anatomyuniverse.musavacca.data.models.block;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CandleBlock;
import net.neoforged.neoforge.registries.DeferredBlock;
import space.anatomyuniverse.musavacca.block.custom.PearlCandleBlock;

import java.util.List;

//? if <1.21.4 {
/*import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.MultiPartBlockStateBuilder;
*///?} else {
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.blockstates.MultiPartGenerator;

//? if <1.21.5 {
/*import net.minecraft.client.data.models.blockstates.Condition;
import net.minecraft.client.data.models.blockstates.Variant;
import net.minecraft.client.data.models.blockstates.VariantProperties;
*///?} else {
import net.minecraft.client.renderer.block.model.Variant;
//?}
//?}

/**
 * Generates blockstates for Pearl Candle blocks.
 *
 * Important:
 * - No Musavacca candle models are generated.
 * - No item models are generated.
 * - Every state points directly to the matching vanilla candle model.
 * - lit=false uses vanilla unlit models.
 * - lit=true uses vanilla lit models.
 *
 * Example:
 * candles=1,lit=false -> minecraft:block/white_candle_one_candle
 * candles=1,lit=true  -> minecraft:block/white_candle_one_candle_lit
 */
public final class CubePearlCandles {
    private CubePearlCandles() {}

    //? if <1.21.4 {
    /*public static void generate(
            BlockStateProvider gen,
            List<DeferredBlock<PearlCandleBlock>> candles
    ) {
        if (candles == null || candles.isEmpty()) return;

        for (DeferredBlock<PearlCandleBlock> holder : candles) {
            if (holder == null) continue;

            PearlCandleBlock pearlCandle = holder.get();
            if (pearlCandle == null) continue;

            generateOne(gen, pearlCandle);
        }
    }

    private static void generateOne(BlockStateProvider gen, PearlCandleBlock pearlCandle) {
        MultiPartBlockStateBuilder multipart = gen.getMultipartBuilder(pearlCandle);

        for (int candles = 1; candles <= 4; candles++) {
            ResourceLocation unlitModel = vanillaCandleModel(pearlCandle, candles, false);
            ResourceLocation litModel = vanillaCandleModel(pearlCandle, candles, true);

            multipart = add(multipart, candles, false, false, unlitModel);
            multipart = add(multipart, candles, true,  false, litModel);

            multipart = add(multipart, candles, false, true,  unlitModel);
            multipart = add(multipart, candles, true,  true,  litModel);
        }

        // Intentionally no simpleBlockItem(...).
        // Pearl Candles are hidden technical blocks and should drop vanilla candles.
    }

    private static MultiPartBlockStateBuilder add(
            MultiPartBlockStateBuilder multipart,
            int candles,
            boolean lit,
            boolean waterlogged,
            ResourceLocation model
    ) {
        multipart.part()
                .modelFile(new ModelFile.UncheckedModelFile(model))
                .addModel()
                .condition(CandleBlock.CANDLES, candles)
                .condition(CandleBlock.LIT, lit)
                .condition(CandleBlock.WATERLOGGED, waterlogged)
                .end();

        return multipart;
    }
    *///?} else {
    public static void generate(
            BlockModelGenerators gen,
            List<DeferredBlock<PearlCandleBlock>> candles
    ) {
        if (candles == null || candles.isEmpty()) return;

        for (DeferredBlock<PearlCandleBlock> holder : candles) {
            if (holder == null) continue;

            PearlCandleBlock pearlCandle = holder.get();
            if (pearlCandle == null) continue;

            generateOne(gen, pearlCandle);
        }
    }

    private static void generateOne(BlockModelGenerators gen, PearlCandleBlock pearlCandle) {
        MultiPartGenerator multipart = MultiPartGenerator.multiPart(pearlCandle);

        for (int candles = 1; candles <= 4; candles++) {
            ResourceLocation unlitModel = vanillaCandleModel(pearlCandle, candles, false);
            ResourceLocation litModel = vanillaCandleModel(pearlCandle, candles, true);

            multipart = add(multipart, candles, false, false, unlitModel);
            multipart = add(multipart, candles, true,  false, litModel);

            /*
             * These should barely exist because waterlogged candles cannot stay lit,
             * but keeping them prevents missing-model weirdness during transitions.
             */
            multipart = add(multipart, candles, false, true,  unlitModel);
            multipart = add(multipart, candles, true,  true,  litModel);
        }

        gen.blockStateOutput.accept(multipart);

        // Intentionally no registerSimpleItemModel(...).
        // Pearl Candles are hidden technical blocks and should drop vanilla candles.
    }

    //? if <1.21.5 {
    /*private static MultiPartGenerator add(
            MultiPartGenerator multipart,
            int candles,
            boolean lit,
            boolean waterlogged,
            ResourceLocation model
    ) {
        return multipart.with(
                Condition.condition()
                        .term(CandleBlock.CANDLES, candles)
                        .term(CandleBlock.LIT, lit)
                        .term(CandleBlock.WATERLOGGED, waterlogged),
                variant(model)
        );
    }

    private static Variant variant(ResourceLocation model) {
        return Variant.variant().with(VariantProperties.MODEL, model);
    }
    *///?} else {
    private static MultiPartGenerator add(
            MultiPartGenerator multipart,
            int candles,
            boolean lit,
            boolean waterlogged,
            ResourceLocation model
    ) {
        return multipart.with(
                BlockModelGenerators.condition()
                        .term(CandleBlock.CANDLES, candles)
                        .term(CandleBlock.LIT, lit)
                        .term(CandleBlock.WATERLOGGED, waterlogged),
                BlockModelGenerators.variant(new Variant(model))
        );
    }
    //?}
    //?}

    private static ResourceLocation vanillaCandleModel(
            PearlCandleBlock pearlCandle,
            int candles,
            boolean lit
    ) {
        Block vanillaCandle = pearlCandle.getVanillaCandleBlock();
        ResourceLocation vanillaId = BuiltInRegistries.BLOCK.getKey(vanillaCandle);

        if (vanillaId == null) {
            throw new IllegalStateException("Pearl candle has unregistered vanilla candle block: " + vanillaCandle);
        }

        return ResourceLocation.fromNamespaceAndPath(
                vanillaId.getNamespace(),
                "block/" + vanillaId.getPath() + "_" + candleCountSuffix(candles) + (lit ? "_lit" : "")
        );
    }

    private static String candleCountSuffix(int candles) {
        return switch (candles) {
            case 1 -> "one_candle";
            case 2 -> "two_candles";
            case 3 -> "three_candles";
            case 4 -> "four_candles";
            default -> throw new IllegalArgumentException("Unsupported candle count: " + candles);
        };
    }
}
