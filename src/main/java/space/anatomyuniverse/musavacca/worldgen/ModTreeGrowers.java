package space.anatomyuniverse.musavacca.worldgen;

import net.minecraft.world.level.block.grower.TreeGrower;

import java.util.Optional;

public final class ModTreeGrowers {

    public static final TreeGrower MUSAVACCA = new TreeGrower(
            "musavacca",
            //? if >=26.3 {
            /*net.minecraft.util.random.WeightedList.of(ModConfiguredFeatures.MUSAVACCA_TREE),
            net.minecraft.util.random.WeightedList.of(),
            net.minecraft.util.random.WeightedList.of(),
            ModConfiguredFeatures.MUSAVACCA_TREE
            *///?} else {
            Optional.empty(),
            Optional.of(ModConfiguredFeatures.MUSAVACCA_TREE),
            Optional.empty()
            //?}
    );

    private ModTreeGrowers() {}
}

