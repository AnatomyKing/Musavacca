package space.anatomyuniverse.musavacca.data.models.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import space.anatomyuniverse.musavacca.data.models.engine.block.EngineBlockEntry;
import space.anatomyuniverse.musavacca.data.models.engine.block.EngineBlockModel;
import space.anatomyuniverse.musavacca.data.models.engine.block.EngineBlockStates;
import space.anatomyuniverse.musavacca.data.models.engine.core.EngineContext;
import space.anatomyuniverse.musavacca.data.models.unified.*;

import java.util.Arrays;

//? if <1.21.4 {
/*import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
*///?} else {
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
//?}

public final class ColumnBlocks {
    private ColumnBlocks() {}

    public enum Kind {
        COLUMN,
        BOTTOM_TOP_FACING
    }

    public record Entry(EngineBlockEntry data, Kind kind) {
        public static Entry column(Block block) {
            return new Entry(
                    EngineBlockEntry.models(block, EngineBlockModel.column())
                            .rotations(Rotations.axis(BlockStateProperties.AXIS)),
                    Kind.COLUMN
            );
        }

        public static Entry bottomTopFacing(Block block) {
            return new Entry(
                    EngineBlockEntry.models(block, EngineBlockModel.bottomTop())
                            .rotations(Rotations.facing(BlockStateProperties.FACING)),
                    Kind.BOTTOM_TOP_FACING
            );
        }

        public Entry item() { return new Entry(data.item(), kind); }
        public Entry item(String itemModelId) { return new Entry(data.item(itemModelId), kind); }
        public Entry noItem() { return new Entry(data.noItem(), kind); }
        public Entry itemTint(ItemTint itemTint) { return new Entry(data.itemTint(itemTint), kind); }
        public Entry biomeTint(BiomeTint biomeTint) { return new Entry(data.biomeTint(biomeTint), kind); }
        public Entry hexColorTint(HexColorTint hexColorTint) { return new Entry(data.hexColorTint(hexColorTint), kind); }
        public Entry pearlTint(PearlTint pearlTint) { return new Entry(data.pearlTint(pearlTint), kind); }
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
                Arrays.stream(entries)
                        .filter(entry -> entry != null)
                        .map(Entry::data)
                        .toArray(EngineBlockEntry[]::new)
        );
    }
}
