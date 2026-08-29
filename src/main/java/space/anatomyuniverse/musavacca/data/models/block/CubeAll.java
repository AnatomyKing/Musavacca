package space.anatomyuniverse.musavacca.data.models.block;

import net.minecraft.world.level.block.Block;

//? if <1.21.4 {
/*import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import space.anatomyuniverse.musavacca.data.models.ModelUtil;
*///?} else {
import net.minecraft.client.data.models.BlockModelGenerators;
//?}

public final class CubeAll {
    private CubeAll() {}

    //? if <1.21.4 {
    /*/^* Standard cube_all with matching block item model. ^/
    public static void generate(BlockStateProvider gen, Block... blocks) {
        for (Block b : blocks) {
            BlockModelBuilder model = gen.models().cubeAll(ModelUtil.pathOf(b), ModelUtil.blockTex(b));
            gen.simpleBlock(b, model);
            gen.simpleBlockItem(b, model);
        }
    }
    *///?} else {
    /** Standard cube_all. (Block item models auto-generate in 1.21.4+ unless you define custom client items.) */
    public static void generate(BlockModelGenerators gen, Block... blocks) {
        for (Block b : blocks) gen.createTrivialCube(b);
    }
    //?}
}
