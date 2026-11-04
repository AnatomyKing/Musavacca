package space.anatomyuniverse.musavacca.data.models.newgen;

import net.minecraft.core.registries.BuiltInRegistries;
//? if >=1.21.11 {
/*import net.minecraft.resources.Identifier;
*///?} else {
import net.minecraft.resources.ResourceLocation;
//?}
import net.minecraft.world.level.block.Block;
import java.util.Objects;

final class BlockFamilySources {
    private BlockFamilySources() {}
    static Block inferBaseBlock(Block child, String suffix) {
        Objects.requireNonNull(child, "child");
        if (suffix == null || suffix.isBlank()) throw new IllegalArgumentException("suffix must not be blank");
        //? if >=1.21.11 {
        /*Identifier id = ModelLocations.blockId(child);
        *///?} else {
        ResourceLocation id = ModelLocations.blockId(child);
        //?}
        String path = id.getPath();
        if (!path.endsWith(suffix)) throw new IllegalStateException("Cannot infer a base block for " + id + ": registry path does not end with " + suffix);
        String stem = path.substring(0, path.length() - suffix.length());
        //? if >=1.21.11 {
        /*Block planks = getBlockIfPresent(Identifier.fromNamespaceAndPath(id.getNamespace(), stem + "_planks"));
        *///?} else {
        Block planks = getBlockIfPresent(ResourceLocation.fromNamespaceAndPath(id.getNamespace(), stem + "_planks"));
        //?}
        if (planks != null) return planks;
        //? if >=1.21.11 {
        /*Block fullBlock = getBlockIfPresent(Identifier.fromNamespaceAndPath(id.getNamespace(), stem + "_block"));
        *///?} else {
        Block fullBlock = getBlockIfPresent(ResourceLocation.fromNamespaceAndPath(id.getNamespace(), stem + "_block"));
        //?}
        if (fullBlock != null) return fullBlock;
        //? if >=1.21.11 {
        /*Block plain = getBlockIfPresent(Identifier.fromNamespaceAndPath(id.getNamespace(), stem));
        *///?} else {
        Block plain = getBlockIfPresent(ResourceLocation.fromNamespaceAndPath(id.getNamespace(), stem));
        //?}
        if (plain != null) return plain;
        throw new IllegalStateException(
                "Could not infer a full/base block for " + id
                        + ". Expected a matching <stem>_planks, "
                        + "<stem>_block, or <stem> block."
        );
    }

    //? if >=1.21.11 {
    /*private static Block getBlockIfPresent(Identifier id) {
    *///?} else {
    private static Block getBlockIfPresent(ResourceLocation id) {
    //?}
        return BuiltInRegistries.BLOCK.getOptional(id).orElse(null);
    }
}
