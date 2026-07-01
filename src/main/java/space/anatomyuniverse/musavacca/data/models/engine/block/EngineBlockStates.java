package space.anatomyuniverse.musavacca.data.models.engine.block;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import space.anatomyuniverse.musavacca.data.models.engine.core.EngineContext;
import space.anatomyuniverse.musavacca.data.models.engine.core.EngineVariants;
import space.anatomyuniverse.musavacca.data.models.unified.Conditions;

//? if <1.21.4 {
/*import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
*///?} else {
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
//? if <1.21.5 {
/*import net.minecraft.client.data.models.blockstates.PropertyDispatch;
*///?} else {
import net.minecraft.client.data.models.blockstates.MultiPartGenerator;
//?}
//?}

public final class EngineBlockStates {
    private EngineBlockStates() {}

    public static void generate(EngineContext ctx, EngineBlockEntry... entries) {
        if (ctx == null || entries == null) return;

        for (EngineBlockEntry entry : entries) {
            if (entry == null || entry.block() == null) continue;
            if (!entry.isMultipart() && entry.models().length == 0) continue;
            if (entry.isMultipart() && entry.parts().length == 0) continue;

            validateNoFallbackMix(entry);
            EngineBlockGeneratedModels.generate(ctx, entry);
            generateBlockState(ctx, entry);

            if (entry.hasItemModel()) {
                EngineBlockItemModels.generate(ctx, entry.block(), entry.itemModelLocation(), entry.itemTint());
            }
        }
    }

    private static void validateNoFallbackMix(EngineBlockEntry entry) {
        if (entry.isMultipart()) return;

        boolean conditioned = false;
        boolean unconditioned = false;

        for (EngineBlockModel model : entry.models()) {
            if (model == null) continue;
            if (model.hasConditions()) conditioned = true;
            else unconditioned = true;
        }

        if (conditioned && unconditioned) {
            throw new IllegalStateException("EngineBlockStates mixes conditional and unconditional models for " + entry.block());
        }
    }

    private static void generateBlockState(EngineContext ctx, EngineBlockEntry entry) {
        //? if <1.21.4 {
        /*ctx.blocks().getVariantBuilder(entry.block()).forAllStates(state -> {
            EngineBlockModel selected = modelForState(entry, state);
            ResourceLocation modelId = selected.modelLocation(entry.block());
            ModelFile model = ctx.blocks().models().getExistingFile(modelId);
            ConfiguredModel.Builder<?> builder = ConfiguredModel.builder().modelFile(model);
            if (entry.hasRotations()) {
                var rotation = entry.rotations().forState(state);
                builder.rotationX(rotation.xDeg());
                builder.rotationY(rotation.yDeg());
            }
            return builder.build();
        });
        *///?} else {
        //? if <1.21.5 {
        /*generateBlock1214(ctx, entry);
        *///?} else {
        generateBlock1215(ctx, entry);
        //?}
        //?}
    }

    private static EngineBlockModel modelForState(EngineBlockEntry entry, BlockState state) {
        EngineBlockModel fallback = null;

        for (EngineBlockModel model : entry.models()) {
            if (model == null) continue;

            if (!model.hasConditions()) {
                if (fallback == null) fallback = model;
                continue;
            }

            if (model.matches(state)) return model;
        }

        if (fallback != null) return fallback;
        throw new IllegalStateException("No model matched state " + state + " for block " + entry.block());
    }

    //? if >=1.21.4 <1.21.5 {
    /*private static void generateBlock1214(EngineContext ctx, EngineBlockEntry entry) {
        if (entry.isMultipart()) throw new IllegalStateException("Engine multipart currently requires 1.21.5+.");
        EngineBlockModel model = firstModel(entry);
        if (entry.hasRotations()) {
            var dispatch = PropertyDispatch.property(entry.rotations().property());
            for (var rule : entry.rotations().rules()) {
                dispatch = dispatch.select(rule.value(), EngineVariants.variant(model.modelLocation(entry.block()), rule.xDeg(), rule.yDeg()));
            }
            ctx.blocks().blockStateOutput.accept(MultiVariantGenerator.multiVariant(entry.block()).with(dispatch));
        } else {
            ctx.blocks().blockStateOutput.accept(MultiVariantGenerator.multiVariant(entry.block(), EngineVariants.variant(model.modelLocation(entry.block()), 0, 0)));
        }
    }

    private static EngineBlockModel firstModel(EngineBlockEntry entry) {
        for (EngineBlockModel model : entry.models()) if (model != null) return model;
        throw new IllegalStateException("No model for " + entry.block());
    }
    *///?}

    //? if >=1.21.5 {
    private static void generateBlock1215(EngineContext ctx, EngineBlockEntry entry) {
        MultiPartGenerator multi = MultiPartGenerator.multiPart(entry.block());

        if (entry.isMultipart()) {
            for (EngineBlockPart part : entry.parts()) {
                multi = addEntryPart(multi, entry, part);
            }
        } else {
            for (EngineBlockModel model : entry.models()) {
                multi = addEntryPart(multi, entry, EngineBlockPart.model(model, model.conditions()));
            }
        }

        ctx.blocks().blockStateOutput.accept(multi);
    }

    private static MultiPartGenerator addEntryPart(MultiPartGenerator multi, EngineBlockEntry entry, EngineBlockPart part) {
        if (part == null) return multi;

        if (entry.hasRotations()) {
            for (var rule : entry.rotations().rules()) {
                if (rule == null) continue;
                multi = addPart1215(multi, entry, part, rule.value(), rule.xDeg(), rule.yDeg());
            }

            return multi;
        }

        return addPart1215(multi, entry, part, null, 0, 0);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static MultiPartGenerator addPart1215(
            MultiPartGenerator multi,
            EngineBlockEntry entry,
            EngineBlockPart part,
            Comparable<?> rotationValue,
            int rotationX,
            int rotationY
    ) {
        int xDeg = Math.floorMod(rotationX + part.xDeg(), 360);
        int yDeg = Math.floorMod(rotationY + part.yDeg(), 360);

        var variant = BlockModelGenerators.variant(
                EngineVariants.variant(part.modelLocation(entry.block()), xDeg, yDeg)
        );

        boolean hasCondition = rotationValue != null || part.hasConditions();
        if (!hasCondition) return multi.with(variant);

        var condition = BlockModelGenerators.condition();

        if (rotationValue != null) {
            condition = condition.term((Property) entry.rotations().property(), (Comparable) rotationValue);
        }

        if (part.hasConditions()) {
            for (Conditions.Entry<?> extra : part.conditions().entries()) {
                if (extra == null) continue;
                condition = condition.term((Property) extra.property(), (Comparable) extra.value());
            }
        }

        return multi.with(condition, variant);
    }
    //?}
}
