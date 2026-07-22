package space.anatomyuniverse.musavacca.data.models.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;

//? if <1.21.4 {
/*import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import space.anatomyuniverse.musavacca.data.models.ModelUtil;
*///?} else {
import net.minecraft.client.data.models.BlockModelGenerators;
//?}

public final class Doors {
    private Doors() {}

    //? if <1.21.4 {
    /*public static void generate(
            BlockStateProvider gen,
            Block... doorBlocks
    ) {
        if (doorBlocks == null) {
            return;
        }

        for (Block block : doorBlocks) {
            if (!(block instanceof DoorBlock door)) {
                continue;
            }

            net.minecraft.resources.ResourceLocation id =
                    ModelUtil.idOf(door);

            gen.doorBlock(
                    door,
                    net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(
                            id.getNamespace(),
                            "block/" + id.getPath() + "_bottom"
                    ),
                    net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(
                            id.getNamespace(),
                            "block/" + id.getPath() + "_top"
                    )
            );
        }
    }
    *///?} else {
    public static void generate(
            BlockModelGenerators gen,
            Block... doorBlocks
    ) {
        if (doorBlocks == null) {
            return;
        }

        for (Block door : doorBlocks) {
            gen.createDoor(door);
        }
    }
    //?}
}
