package space.anatomyuniverse.musavacca.data.models.engine.block;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import space.anatomyuniverse.musavacca.data.models.engine.core.EngineConditions;
import space.anatomyuniverse.musavacca.data.models.unified.BiomeTint;
import space.anatomyuniverse.musavacca.data.models.unified.Conditions;
import space.anatomyuniverse.musavacca.data.models.unified.HexColorTint;
import space.anatomyuniverse.musavacca.data.models.unified.ItemTint;
import space.anatomyuniverse.musavacca.data.models.unified.PearlTint;

public record EngineBlockPart(
        EngineBlockModel model,
        Conditions conditions,
        int xDeg,
        int yDeg,
        ItemTint blockTint
) {
    public static EngineBlockPart always(String modelId) {
        return new EngineBlockPart(EngineBlockModel.existing(modelId), null, 0, 0, null);
    }

    public static EngineBlockPart when(String modelId, Conditions conditions) {
        return new EngineBlockPart(EngineBlockModel.existing(modelId), conditions, 0, 0, null);
    }

    public static EngineBlockPart model(EngineBlockModel model, Conditions conditions) {
        return new EngineBlockPart(model, conditions, 0, 0, null);
    }

    public EngineBlockPart rotateY(int yDeg) {
        return rotate(0, yDeg);
    }

    public EngineBlockPart rotate(int xDeg, int yDeg) {
        return new EngineBlockPart(model, conditions, normalize(xDeg), normalize(yDeg), blockTint);
    }

    public EngineBlockPart itemTint(ItemTint itemTint) {
        return new EngineBlockPart(model, conditions, xDeg, yDeg, itemTint);
    }

    public EngineBlockPart biomeTint(BiomeTint biomeTint) {
        return itemTint(biomeTint);
    }

    public EngineBlockPart hexColorTint(HexColorTint hexColorTint) {
        return itemTint(hexColorTint);
    }

    public EngineBlockPart pearlTint(PearlTint pearlTint) {
        return itemTint(pearlTint);
    }

    public boolean hasConditions() {
        return EngineConditions.has(conditions);
    }

    public boolean useTintedGeneratedBlockModel() {
        return blockTint != null && blockTint.useTintedGeneratedBlockModel();
    }

    public boolean hasBlockTint() {
        return blockTint != null && blockTint.hasBlockTint();
    }

    public ResourceLocation modelLocation(Block block) {
        return model.modelLocation(block);
    }

    private static int normalize(int deg) {
        int normalized = Math.floorMod(deg, 360);
        return switch (normalized) {
            case 0, 90, 180, 270 -> normalized;
            default -> throw new IllegalArgumentException("Unsupported model rotation: " + deg);
        };
    }
}
