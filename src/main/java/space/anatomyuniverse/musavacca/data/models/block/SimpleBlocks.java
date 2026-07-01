package space.anatomyuniverse.musavacca.data.models.block;

import net.minecraft.world.level.block.Block;
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

public final class SimpleBlocks {
    private SimpleBlocks() {}

    public record Entry(EngineBlockEntry data) {
        public static Entry cube(Block block) {
            return new Entry(EngineBlockEntry.models(block, EngineBlockModel.cube()));
        }

        public static Entry model(Block block, String modelId) {
            return new Entry(EngineBlockEntry.models(block, EngineBlockModel.existing(modelId)));
        }

        public static Entry models(Block block, Model... models) {
            return new Entry(EngineBlockEntry.models(
                    block,
                    Arrays.stream(models).filter(m -> m != null).map(Model::toEngine).toArray(EngineBlockModel[]::new)
            ));
        }

        public static Entry multipart(Block block, Part... parts) {
            return new Entry(EngineBlockEntry.multipart(
                    block,
                    Arrays.stream(parts).filter(p -> p != null).map(Part::toEngine).toArray(EngineBlockPart[]::new)
            ));
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

    public record Model(String modelId, Conditions conditions, boolean generatedCubeModel) {
        public static Model cube() { return new Model(null, null, true); }
        public static Model cubeWhen(Conditions conditions) { return new Model(null, conditions, true); }
        public static Model model(String modelId) { return new Model(modelId, null, false); }
        public static Model when(String modelId, Conditions conditions) { return new Model(modelId, conditions, false); }

        private EngineBlockModel toEngine() {
            return generatedCubeModel
                    ? EngineBlockModel.cube(conditions)
                    : EngineBlockModel.existing(modelId, conditions);
        }
    }

    public record Part(String modelId, Conditions conditions, int xDeg, int yDeg, ItemTint blockTint) {
        public static Part always(String modelId) { return new Part(modelId, null, 0, 0, null); }
        public static Part when(String modelId, Conditions conditions) { return new Part(modelId, conditions, 0, 0, null); }
        public Part rotateY(int yDeg) { return rotate(0, yDeg); }
        public Part rotate(int xDeg, int yDeg) { return new Part(modelId, conditions, xDeg, yDeg, blockTint); }
        public Part itemTint(ItemTint itemTint) { return new Part(modelId, conditions, xDeg, yDeg, itemTint); }
        public Part biomeTint(BiomeTint biomeTint) { return itemTint(biomeTint); }
        public Part hexColorTint(HexColorTint hexColorTint) { return itemTint(hexColorTint); }
        public Part pearlTint(PearlTint pearlTint) { return itemTint(pearlTint); }

        private EngineBlockPart toEngine() {
            return EngineBlockPart.when(modelId, conditions).rotate(xDeg, yDeg).itemTint(blockTint);
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
