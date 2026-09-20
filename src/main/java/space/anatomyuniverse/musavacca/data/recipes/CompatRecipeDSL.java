package space.anatomyuniverse.musavacca.data.recipes;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;

import java.util.Objects;

public final class CompatRecipeDSL extends RecipeDSL {
    private final String compatModId;

    CompatRecipeDSL(RecipeDSL parent, String compatModId) {
        super(
                parent.output().withConditions(
                        new ModLoadedCondition(requireModId(compatModId))
                ),
                parent.modId(),
                parent.unlocker(),
                parent.registries(),
                joinFolder(
                        parent.recipePathPrefix(),
                        "compat/" + requireModId(compatModId)
                )
        );

        this.compatModId = requireModId(compatModId);
    }

    public String compatModId() {
        return this.compatModId;
    }

    public ResourceLocation compatId(String path) {
        Objects.requireNonNull(path, "path");

        if (path.isBlank()) {
            throw new IllegalArgumentException("Compat resource path must not be blank");
        }

        return ResourceLocation.fromNamespaceAndPath(this.compatModId, path);
    }

    public Item item(String path) {
        ResourceLocation id = compatId(path);

        return BuiltInRegistries.ITEM.getOptional(id)
                .orElseThrow(() -> missing("item", id));
    }

    public Block block(String path) {
        ResourceLocation id = compatId(path);

        return BuiltInRegistries.BLOCK.getOptional(id)
                .orElseThrow(() -> missing("block", id));
    }

    public TagKey<Item> itemTag(String path) {
        return TagKey.create(Registries.ITEM, compatId(path));
    }

    private static String requireModId(String modId) {
        Objects.requireNonNull(modId, "modId");

        if (modId.isBlank()) {
            throw new IllegalArgumentException("Compat mod id must not be blank");
        }

        return modId;
    }

    private static IllegalStateException missing(String type, ResourceLocation id) {
        return new IllegalStateException(
                "Missing compat " + type + " '" + id +
                        "' during datagen. Add the target mod to this version's compat runtime."
        );
    }
}
