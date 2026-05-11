// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/worldgen/ModTreeGrowers.java
package space.anatomyuniverse.musavacca.worldgen;

import net.minecraft.world.level.block.grower.TreeGrower;

import java.util.Optional;

public final class ModTreeGrowers {

    public static final TreeGrower MUSAVACCA = new TreeGrower(
            "musavacca",
            Optional.empty(),
            Optional.of(ModConfiguredFeatures.MUSAVACCA_TREE),
            Optional.empty()
    );

    private ModTreeGrowers() {}
}