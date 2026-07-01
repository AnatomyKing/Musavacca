package space.anatomyuniverse.musavacca.data.models.engine.block;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import space.anatomyuniverse.musavacca.data.models.engine.core.EngineIds;
import space.anatomyuniverse.musavacca.data.models.unified.BiomeTint;
import space.anatomyuniverse.musavacca.data.models.unified.BlockTintRule;
import space.anatomyuniverse.musavacca.data.models.unified.HexColorTint;
import space.anatomyuniverse.musavacca.data.models.unified.ItemTint;
import space.anatomyuniverse.musavacca.data.models.unified.PearlTint;
import space.anatomyuniverse.musavacca.data.models.unified.Rotations;

import java.util.ArrayList;
import java.util.List;

public record EngineBlockEntry(
        Block block,
        Rotations rotations,
        boolean generateItemModel,
        String itemModelId,
        ItemTint itemTint,
        EngineBlockModel[] models,
        EngineBlockPart[] parts
) {
    public EngineBlockEntry {
        if (models == null) models = new EngineBlockModel[0];
        if (parts == null) parts = new EngineBlockPart[0];
    }

    public static EngineBlockEntry models(Block block, EngineBlockModel... models) {
        return new EngineBlockEntry(block, null, true, null, null, models, new EngineBlockPart[0]);
    }

    public static EngineBlockEntry multipart(Block block, EngineBlockPart... parts) {
        return new EngineBlockEntry(block, null, true, null, null, new EngineBlockModel[0], parts);
    }

    public EngineBlockEntry rotations(Rotations rotations) {
        return new EngineBlockEntry(block, rotations, generateItemModel, itemModelId, itemTint, models, parts);
    }

    public EngineBlockEntry item() {
        return item(null);
    }

    public EngineBlockEntry item(String itemModelId) {
        return new EngineBlockEntry(block, rotations, true, itemModelId, itemTint, models, parts);
    }

    public EngineBlockEntry noItem() {
        return new EngineBlockEntry(block, rotations, false, null, itemTint, models, parts);
    }

    public EngineBlockEntry itemTint(ItemTint itemTint) {
        return new EngineBlockEntry(block, rotations, true, itemModelId, itemTint, models, parts);
    }

    public EngineBlockEntry biomeTint(BiomeTint biomeTint) {
        return itemTint(biomeTint);
    }

    public EngineBlockEntry hexColorTint(HexColorTint hexColorTint) {
        return itemTint(hexColorTint);
    }

    public EngineBlockEntry pearlTint(PearlTint pearlTint) {
        return itemTint(pearlTint);
    }

    public boolean isMultipart() {
        return parts.length > 0;
    }

    public boolean hasRotations() {
        return rotations != null && rotations.enabled();
    }

    public boolean hasItemModel() {
        return generateItemModel || itemTint != null;
    }

    public boolean useTintedGeneratedBlockModel() {
        if (itemTint != null && itemTint.useTintedGeneratedBlockModel()) {
            return true;
        }

        for (EngineBlockPart part : parts) {
            if (part != null && part.useTintedGeneratedBlockModel()) {
                return true;
            }
        }

        return false;
    }

    public BlockTintRule[] blockTintRules() {
        List<BlockTintRule> result = new ArrayList<>();

        if (itemTint != null && itemTint.hasBlockTint()) {
            result.add(BlockTintRule.of(block, itemTint));
        }

        for (EngineBlockPart part : parts) {
            if (part != null && part.hasBlockTint()) {
                result.add(BlockTintRule.when(block, part.conditions(), part.blockTint()));
            }
        }

        return result.toArray(BlockTintRule[]::new);
    }

    public ResourceLocation itemModelLocation() {
        if (itemModelId != null && !itemModelId.isBlank()) return EngineIds.parse(itemModelId);
        if (models.length > 0 && models[0] != null) return models[0].modelLocation(block);
        if (parts.length > 0 && parts[0] != null) return parts[0].modelLocation(block);
        return EngineIds.blockModel(block);
    }
}
