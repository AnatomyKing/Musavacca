package space.anatomyuniverse.musavacca.worldgen;

import net.minecraft.world.level.block.grower.TreeGrower;
import java.util.Optional;

public final class ModTreeGrowers {
    //? if >=26.3 {
    /*public static final TreeGrower MUSAVACCA = new TreeGrower("musavacca", net.minecraft.util.random.WeightedList.of(ModConfiguredFeatures.MUSAVACCA_TREE),
    *///?} else {
    public static final TreeGrower MUSAVACCA = new TreeGrower("musavacca", Optional.empty(), Optional.of(ModConfiguredFeatures.MUSAVACCA_TREE), Optional.empty());
    //?}
            //? if >=26.3 {
            /*net.minecraft.util.random.WeightedList.of(), net.minecraft.util.random.WeightedList.of(), ModConfiguredFeatures.MUSAVACCA_TREE);
            *///?}
    private ModTreeGrowers() {}
}
