package space.anatomyuniverse.musavacca.basuke.eating;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import space.anatomyuniverse.musavacca.entity.mob.basuke.Basuke;

public interface VocoTableEatingAction {
    int eatingTimeTicks();

    boolean complete(
            Basuke basuke,
            ServerLevel level,
            ItemStack heldStack
    );
}


