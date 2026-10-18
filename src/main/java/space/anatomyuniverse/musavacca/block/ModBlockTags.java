package space.anatomyuniverse.musavacca.block;

import net.minecraft.core.registries.Registries;
//? if >=1.21.11 {
/*import net.minecraft.resources.Identifier;
*///?} else {
import net.minecraft.resources.ResourceLocation;
//?}
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import space.anatomyuniverse.musavacca.MusaCore;

public final class ModBlockTags {
    public static final TagKey<Block> PEARL_PORTAL_FRAME = create("pearl_portal_frame");
    public static final TagKey<Block> MUSAVACCA_STEMS = create("musavacca_stems");
    public static final TagKey<Block> EDIBLE_FOR_BANANA_COW = create("edible_for_banana_cow");

    private ModBlockTags() {}

    private static TagKey<Block> create(String name) {
        return TagKey.create(
                Registries.BLOCK,
                //? if >=1.21.11 {
                /*Identifier.fromNamespaceAndPath(MusaCore.MOD_ID, name)
                *///?} else {
                ResourceLocation.fromNamespaceAndPath(MusaCore.MOD_ID, name)
                //?}
        );
    }
}
