package space.anatomyuniverse.musavacca.data.models.engine.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import space.anatomyuniverse.musavacca.data.models.engine.core.EngineIds;
import space.anatomyuniverse.musavacca.data.models.unified.BiomeTint;
import space.anatomyuniverse.musavacca.data.models.unified.HexColorTint;
import space.anatomyuniverse.musavacca.data.models.unified.ItemTint;
import space.anatomyuniverse.musavacca.data.models.unified.PearlTint;

public record EngineItemEntry(
        ItemLike item,
        EngineItemModelKind kind,
        String modelId,
        String baseTextureStem,
        String tintedTextureStem,
        ItemTint itemTint
) {
    public static EngineItemEntry flat(ItemLike item) {
        return new EngineItemEntry(item, EngineItemModelKind.FLAT, null, null, null, null);
    }

    public static EngineItemEntry handheld(ItemLike item) {
        return new EngineItemEntry(item, EngineItemModelKind.HANDHELD, null, null, null, null);
    }

    public static EngineItemEntry model(ItemLike item, String modelId) {
        return new EngineItemEntry(item, EngineItemModelKind.MODEL, modelId, null, null, null);
    }

    public EngineItemEntry itemTint(ItemTint itemTint) {
        return new EngineItemEntry(item, kind, modelId, baseTextureStem, tintedTextureStem, itemTint);
    }

    public EngineItemEntry biomeTint(BiomeTint biomeTint) {
        return itemTint(biomeTint);
    }

    public EngineItemEntry hexColorTint(HexColorTint hexColorTint) {
        return itemTint(hexColorTint);
    }

    public EngineItemEntry pearlTint(PearlTint pearlTint) {
        return itemTint(pearlTint);
    }

    public EngineItemEntry textures(String baseTextureStem, String tintedTextureStem) {
        return new EngineItemEntry(item, kind, modelId, baseTextureStem, tintedTextureStem, itemTint);
    }

    public ResourceLocation modelLocation() {
        if (modelId != null && !modelId.isBlank()) {
            return EngineIds.parse(modelId);
        }

        return EngineIds.itemModel(item);
    }

    public String baseTextureStemOrDefault() {
        return baseTextureStem != null && !baseTextureStem.isBlank()
                ? baseTextureStem
                : EngineIds.itemId(item).getPath();
    }

    public String tintedTextureStemOrDefault() {
        return tintedTextureStem != null && !tintedTextureStem.isBlank()
                ? tintedTextureStem
                : EngineIds.itemId(item).getPath();
    }

    public PearlTint pearlTintOrNull() {
        return itemTint instanceof PearlTint pearlTint ? pearlTint : null;
    }

    public boolean isGeneratedKind() {
        return kind == EngineItemModelKind.FLAT || kind == EngineItemModelKind.HANDHELD;
    }
}
