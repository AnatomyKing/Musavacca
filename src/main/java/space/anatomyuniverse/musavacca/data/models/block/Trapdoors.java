package space.anatomyuniverse.musavacca.data.models.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.TrapDoorBlock;

//? if <1.21.4 {
/*import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import space.anatomyuniverse.musavacca.data.models.ModelUtil;
*///?} else {
import net.minecraft.client.data.models.BlockModelGenerators;
//?}

public final class Trapdoors {
    private Trapdoors() {}

    //? if <1.21.4 {
    /*public static void generate(
            BlockStateProvider gen,
            Block... trapdoorBlocks
    ) {
        if (trapdoorBlocks == null) {
            return;
        }

        for (Block block : trapdoorBlocks) {
            if (!(block instanceof TrapDoorBlock trapdoor)) {
                continue;
            }

            gen.trapdoorBlock(
                    trapdoor,
                    ModelUtil.blockTex(trapdoor),
                    true
            );

            gen.simpleBlockItem(
                    trapdoor,
                    gen.models().getExistingFile(
                            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(
                                    ModelUtil.idOf(trapdoor).getNamespace(),
                                    "block/"
                                            + ModelUtil.idOf(trapdoor).getPath()
                                            + "_bottom"
                            )
                    )
            );
        }
    }
    *///?} else {
    public static void generate(
            BlockModelGenerators gen,
            Block... trapdoorBlocks
    ) {
        if (trapdoorBlocks == null) {
            return;
        }

        for (Block trapdoor : trapdoorBlocks) {
            /*
             * Orientable = the texture rotates with FACING.
             * This is the normal choice for an asymmetrical wood texture.
             */
            gen.createOrientableTrapdoor(trapdoor);
        }
    }
    //?}
}
