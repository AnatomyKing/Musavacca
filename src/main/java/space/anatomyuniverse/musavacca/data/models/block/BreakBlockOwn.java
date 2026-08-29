package space.anatomyuniverse.musavacca.data.models.block;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import space.anatomyuniverse.musavacca.block.custom.BreakBlock;

import java.util.Map;

//? if <1.21.4 {
/*import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
*///?} else {
import net.minecraft.client.data.models.BlockModelGenerators;

//? if <1.21.5 {
/*import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.blockstates.Variant;
import net.minecraft.client.data.models.blockstates.VariantProperties;
*///?} else {
import net.minecraft.client.data.models.blockstates.MultiPartGenerator;
import net.minecraft.client.renderer.block.model.Variant;
//?}
//?}

public final class BreakBlockOwn {
    private BreakBlockOwn() {}

    /**
     * Explicit existing model ids for all BreakBlock states.
     *
     * age0 / age1 / age2           -> ATTACHED = false
     * attachedAge0/1/2             -> ATTACHED = true
     *
     * No item model is generated here.
     * This is intentional so blocks with multiple custom BlockItems
     * (like unripe/ripening/ripe variants) do not collide in item datagen.
     */
    public record AgeModels(
            String age0,
            String age1,
            String age2,
            String attachedAge0,
            String attachedAge1,
            String attachedAge2
    ) {
        public ResourceLocation forState(boolean attached, int age) {
            return ResourceLocation.parse(
                    switch (age) {
                        case 0 -> attached ? attachedAge0 : age0;
                        case 1 -> attached ? attachedAge1 : age1;
                        case 2 -> attached ? attachedAge2 : age2;
                        default -> throw new IllegalArgumentException("Unsupported age: " + age);
                    }
            );
        }
    }

    //? if <1.21.4 {
    /*public static void generate(BlockStateProvider gen, Map<Block, AgeModels> models) {
        if (models == null || models.isEmpty()) return;

        models.forEach((block, ageModels) -> {
            if (!(block instanceof BreakBlock) || ageModels == null) return;

            gen.getVariantBuilder(block).forAllStates(state -> {
                int age = state.getValue(BreakBlock.AGE);
                boolean attached = state.getValue(BreakBlock.ATTACHED);

                ModelFile model = gen.models().getExistingFile(
                        ageModels.forState(attached, age)
                );

                return ConfiguredModel.builder()
                        .modelFile(model)
                        .build();
            });

            // Intentionally no simpleBlockItem(...) here.
        });
    }
    *///?} else {
    public static void generate(BlockModelGenerators gen, Map<Block, AgeModels> models) {
        if (models == null || models.isEmpty()) return;

        models.forEach((block, ageModels) -> {
            if (!(block instanceof BreakBlock) || ageModels == null) return;

            ResourceLocation age0 = ageModels.forState(false, 0);
            ResourceLocation age1 = ageModels.forState(false, 1);
            ResourceLocation age2 = ageModels.forState(false, 2);

            ResourceLocation attachedAge0 = ageModels.forState(true, 0);
            ResourceLocation attachedAge1 = ageModels.forState(true, 1);
            ResourceLocation attachedAge2 = ageModels.forState(true, 2);

            //? if <1.21.5 {
            /*gen.blockStateOutput.accept(
                    MultiVariantGenerator.multiVariant(block).with(
                            PropertyDispatch.properties(BreakBlock.AGE, BreakBlock.ATTACHED)
                                    .select(0, false, variant(age0))
                                    .select(1, false, variant(age1))
                                    .select(2, false, variant(age2))
                                    .select(0, true,  variant(attachedAge0))
                                    .select(1, true,  variant(attachedAge1))
                                    .select(2, true,  variant(attachedAge2))
                    )
            );
            *///?} else {
            MultiPartGenerator multi = MultiPartGenerator.multiPart(block);

            multi = add(multi, 0, false, age0);
            multi = add(multi, 1, false, age1);
            multi = add(multi, 2, false, age2);

            multi = add(multi, 0, true, attachedAge0);
            multi = add(multi, 1, true, attachedAge1);
            multi = add(multi, 2, true, attachedAge2);

            gen.blockStateOutput.accept(multi);
            //?}

            // Intentionally no registerSimpleItemModel(...) here.
        });
    }

    //? if <1.21.5 {
    /*private static Variant variant(ResourceLocation modelId) {
        return Variant.variant().with(VariantProperties.MODEL, modelId);
    }
    *///?} else {
    private static MultiPartGenerator add(
            MultiPartGenerator gen,
            int age,
            boolean attached,
            ResourceLocation model
    ) {
        return gen.with(
                BlockModelGenerators.condition()
                        .term(BreakBlock.AGE, age)
                        .term(BreakBlock.ATTACHED, attached),
                BlockModelGenerators.variant(new Variant(model))
        );
    }
    //?}
    //?}
}
