package space.anatomyuniverse.musavacca.block;

import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import space.anatomyuniverse.musavacca.MusaCore;

public final class ModWoodTypes {

    private static final String MUSAVACCA_NAME =
            MusaCore.MOD_ID + ":musavacca";

    /**
     * Shared by:
     * - door
     * - trapdoor
     * - pressure plate
     * - button
     */
    public static final BlockSetType MUSAVACCA_BLOCK_SET =
            BlockSetType.register(
                    new BlockSetType(MUSAVACCA_NAME)
            );

    /**
     * Shared by:
     * - fence gate
     * - signs later
     * - hanging signs later
     */
    public static final WoodType MUSAVACCA =
            WoodType.register(
                    new WoodType(
                            MUSAVACCA_NAME,
                            MUSAVACCA_BLOCK_SET
                    )
            );

    private ModWoodTypes() {}
}
