package space.anatomyuniverse.musavacca.data.models.engine.core;

//? if <1.21.4 {
/*import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
*///?} else {
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
//?}

public record EngineContext(
        //? if <1.21.4 {
        /*BlockStateProvider blocks, ItemModelProvider items
        *///?} else {
        BlockModelGenerators blocks, ItemModelGenerators items
        //?}
) {
    public static EngineContext of(
            //? if <1.21.4 {
            /*BlockStateProvider blocks, ItemModelProvider items
            *///?} else {
            BlockModelGenerators blocks, ItemModelGenerators items
            //?}
    ) {
        return new EngineContext(blocks, items);
    }
}
