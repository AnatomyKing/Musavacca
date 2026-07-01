package space.anatomyuniverse.musavacca.data.models.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import space.anatomyuniverse.musavacca.data.models.engine.block.EngineBlockEntry;
import space.anatomyuniverse.musavacca.data.models.engine.block.EngineBlockModel;
import space.anatomyuniverse.musavacca.data.models.engine.block.EngineBlockPart;
import space.anatomyuniverse.musavacca.data.models.engine.block.EngineBlockStates;
import space.anatomyuniverse.musavacca.data.models.engine.core.EngineContext;
import space.anatomyuniverse.musavacca.data.models.unified.*;

import java.util.Arrays;
import java.util.Map;

//? if <1.21.4 {
/*import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
*///?} else {
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
//?}

public final class AgeBlocks {
    private AgeBlocks() {}

    public record Entry(EngineBlockEntry data) {
        public static Entry of(Block block, IntegerProperty ageProperty, Model... models) {
            return new Entry(EngineBlockEntry.models(
                    block,
                    Arrays.stream(models).filter(m -> m != null).map(m -> m.toEngine(ageProperty)).toArray(EngineBlockModel[]::new)
            ).noItem());
        }

        public static Entry multipart(Block block, IntegerProperty ageProperty, Part... parts) {
            return new Entry(EngineBlockEntry.multipart(
                    block,
                    Arrays.stream(parts).filter(p -> p != null).map(p -> p.toEngine(ageProperty)).toArray(EngineBlockPart[]::new)
            ).noItem());
        }

        public Entry rotations(Rotations rotations) { return new Entry(data.rotations(rotations)); }
        public Entry item() { return new Entry(data.item()); }
        public Entry item(String itemModelId) { return new Entry(data.item(itemModelId)); }
        public Entry noItem() { return new Entry(data.noItem()); }
        public Entry itemTint(ItemTint itemTint) { return new Entry(data.itemTint(itemTint)); }
        public Entry biomeTint(BiomeTint biomeTint) { return new Entry(data.biomeTint(biomeTint)); }
        public Entry hexColorTint(HexColorTint hexColorTint) { return new Entry(data.hexColorTint(hexColorTint)); }
        public Entry pearlTint(PearlTint pearlTint) { return new Entry(data.pearlTint(pearlTint)); }
    }

    public record Model(int age, String modelId, Conditions conditions) {
        public static Model age(int age, String modelId) { return new Model(age, modelId, null); }
        public static Model when(int age, String modelId, Conditions conditions) { return new Model(age, modelId, conditions); }

        private EngineBlockModel toEngine(IntegerProperty ageProperty) {
            Conditions ageCondition = Conditions.when(ageProperty, age);
            return EngineBlockModel.existing(modelId, conditions == null ? ageCondition : ageCondition.and(conditions));
        }
    }

    public record Part(int age, String modelId, Conditions conditions, int xDeg, int yDeg, ItemTint blockTint) {
        public static Part always(int age, String modelId) { return new Part(age, modelId, null, 0, 0, null); }
        public static Part when(int age, String modelId, Conditions conditions) { return new Part(age, modelId, conditions, 0, 0, null); }
        public Part rotateY(int yDeg) { return rotate(0, yDeg); }
        public Part rotate(int xDeg, int yDeg) { return new Part(age, modelId, conditions, xDeg, yDeg, blockTint); }
        public Part itemTint(ItemTint itemTint) { return new Part(age, modelId, conditions, xDeg, yDeg, itemTint); }
        public Part biomeTint(BiomeTint biomeTint) { return itemTint(biomeTint); }
        public Part hexColorTint(HexColorTint hexColorTint) { return itemTint(hexColorTint); }
        public Part pearlTint(PearlTint pearlTint) { return itemTint(pearlTint); }

        private EngineBlockPart toEngine(IntegerProperty ageProperty) {
            Conditions ageCondition = Conditions.when(ageProperty, age);
            Conditions finalConditions = conditions == null ? ageCondition : ageCondition.and(conditions);
            return EngineBlockPart.when(modelId, finalConditions).rotate(xDeg, yDeg).itemTint(blockTint);
        }
    }

    public static void generate(
            //? if <1.21.4 {
            /*BlockStateProvider blocks, ItemModelProvider items,
            *///?} else {
            BlockModelGenerators blocks, ItemModelGenerators items,
            //?}
            Entry... entries
    ) {
        if (entries == null) return;
        EngineBlockStates.generate(
                EngineContext.of(blocks, items),
                Arrays.stream(entries).filter(e -> e != null).map(Entry::data).toArray(EngineBlockEntry[]::new)
        );
    }

    public static void generate(
            //? if <1.21.4 {
            /*BlockStateProvider blocks, ItemModelProvider items,
            *///?} else {
            BlockModelGenerators blocks, ItemModelGenerators items,
            //?}
            Map<Block, Entry> entries
    ) {
        if (entries == null || entries.isEmpty()) return;
        generate(blocks, items, entries.values().toArray(Entry[]::new));
    }
}
