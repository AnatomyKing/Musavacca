package space.anatomyuniverse.musavacca.data.models.engine.block;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import space.anatomyuniverse.musavacca.data.models.engine.core.EngineConditions;
import space.anatomyuniverse.musavacca.data.models.engine.core.EngineIds;
import space.anatomyuniverse.musavacca.data.models.unified.Conditions;

public record EngineBlockModel(
        EngineBlockModelKind kind,
        String modelId,
        String textureId,
        Conditions conditions
) {
    public static EngineBlockModel cube() {
        return new EngineBlockModel(EngineBlockModelKind.CUBE, null, null, null);
    }

    public static EngineBlockModel cube(Conditions conditions) {
        return new EngineBlockModel(EngineBlockModelKind.CUBE, null, null, conditions);
    }

    public static EngineBlockModel column() {
        return new EngineBlockModel(EngineBlockModelKind.COLUMN, null, null, null);
    }

    public static EngineBlockModel column(Conditions conditions) {
        return new EngineBlockModel(EngineBlockModelKind.COLUMN, null, null, conditions);
    }

    public static EngineBlockModel bottomTop() {
        return new EngineBlockModel(EngineBlockModelKind.BOTTOM_TOP, null, null, null);
    }

    public static EngineBlockModel bottomTop(Conditions conditions) {
        return new EngineBlockModel(EngineBlockModelKind.BOTTOM_TOP, null, null, conditions);
    }

    public static EngineBlockModel existing(String modelId) {
        return new EngineBlockModel(EngineBlockModelKind.EXISTING, modelId, null, null);
    }

    public static EngineBlockModel existing(String modelId, Conditions conditions) {
        return new EngineBlockModel(EngineBlockModelKind.EXISTING, modelId, null, conditions);
    }

    public static EngineBlockModel cross() {
        return new EngineBlockModel(EngineBlockModelKind.CROSS, null, null, null);
    }

    public static EngineBlockModel crossTexture(String textureId) {
        return new EngineBlockModel(EngineBlockModelKind.CROSS, null, textureId, null);
    }

    public static EngineBlockModel crossModel(String modelId, String textureId) {
        return new EngineBlockModel(EngineBlockModelKind.CROSS, modelId, textureId, null);
    }

    public static EngineBlockModel crossModel(String modelId, String textureId, Conditions conditions) {
        return new EngineBlockModel(EngineBlockModelKind.CROSS, modelId, textureId, conditions);
    }

    public boolean hasConditions() {
        return EngineConditions.has(conditions);
    }

    public boolean matches(BlockState state) {
        return EngineConditions.matches(state, conditions);
    }

    public boolean generatesModelJson() {
        return kind == EngineBlockModelKind.CUBE
                || kind == EngineBlockModelKind.COLUMN
                || kind == EngineBlockModelKind.BOTTOM_TOP
                || kind == EngineBlockModelKind.CROSS;
    }

    public ResourceLocation modelLocation(Block block) {
        return modelId != null && !modelId.isBlank() ? EngineIds.parse(modelId) : EngineIds.blockModel(block);
    }

    public ResourceLocation textureLocation(Block block) {
        return textureId != null && !textureId.isBlank() ? EngineIds.parse(textureId) : EngineIds.blockTexture(block);
    }

    public ResourceLocation textureLocation(Block block, String suffix) {
        if (textureId != null && !textureId.isBlank()) {
            ResourceLocation base = EngineIds.parse(textureId);
            return ResourceLocation.fromNamespaceAndPath(base.getNamespace(), base.getPath() + suffix);
        }

        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(block);
        return ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "block/" + id.getPath() + suffix);
    }
}
