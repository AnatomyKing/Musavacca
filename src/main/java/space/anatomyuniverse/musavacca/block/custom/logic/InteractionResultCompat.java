package space.anatomyuniverse.musavacca.block.custom.logic;

import net.minecraft.world.InteractionResult;
//? if <1.21.2
//import net.minecraft.world.ItemInteractionResult;

public final class InteractionResultCompat {
    private InteractionResultCompat() {}

    //? if <1.21.2 {
    /*public static ItemInteractionResult forItemUse(InteractionResult result) {
        if (result == InteractionResult.FAIL) {
            return ItemInteractionResult.FAIL;
        }

        if (result == InteractionResult.PASS) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (result == InteractionResult.CONSUME) {
            return ItemInteractionResult.CONSUME;
        }

        if (result == InteractionResult.CONSUME_PARTIAL) {
            return ItemInteractionResult.CONSUME_PARTIAL;
        }

        return ItemInteractionResult.SUCCESS;
    }
    *///?}
}
