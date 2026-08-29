package space.anatomyuniverse.musavacca.data.models.item;

//? if <1.21.4 {
/*import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
*///?}

public final class SimpleBlockWithItem {
    private SimpleBlockWithItem() {}

    //? if <1.21.4 {
    /*/^* If you generate block models via BlockStateProvider, you can also generate the block item model there. ^/
    public static void generate(BlockStateProvider blockGen, Block block) {
        blockGen.simpleBlock(block);
        blockGen.simpleBlockItem(block, blockGen.cubeAll(block));
    }
    *///?}
}
