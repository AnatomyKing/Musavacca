// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/block/ModBlockTags.java
package space.anatomyuniverse.musavacca.block;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import space.anatomyuniverse.musavacca.MusaCore;

public final class ModBlockTags {
    private ModBlockTags() {}

    public static final TagKey<Block> PEARL_PORTAL_FRAME = create("pearl_portal_frame");

    private static TagKey<Block> create(String name) {
        return TagKey.create(
                Registries.BLOCK,
                ResourceLocation.fromNamespaceAndPath(MusaCore.MOD_ID, name)
        );
    }
}