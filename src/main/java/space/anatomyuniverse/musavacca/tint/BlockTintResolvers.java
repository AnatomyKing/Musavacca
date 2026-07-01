package space.anatomyuniverse.musavacca.tint;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import space.anatomyuniverse.musavacca.component.HexColorComponent;
import space.anatomyuniverse.musavacca.component.HexColorSource;
import space.anatomyuniverse.musavacca.data.models.unified.BlockTintColorResolver;

public final class BlockTintResolvers {
    private BlockTintResolvers() {}

    public static BlockTintColorResolver none() {
        return (state, level, pos) -> null;
    }

    public static BlockTintColorResolver hexSlot(String slot) {
        String cleanedSlot = HexColorComponent.cleanSlot(slot);
        return (state, level, pos) -> readHexSlot(level, pos, cleanedSlot);
    }

    public static BlockTintColorResolver hexSlotWithPlacementMemory(String slot) {
        String cleanedSlot = HexColorComponent.cleanSlot(slot);
        return (state, level, pos) -> {
            Integer stored = readHexSlot(level, pos, cleanedSlot);
            return stored != null ? stored : PearlPlacementColorMemory.get(level, pos);
        };
    }

    public static Integer readHexSlot(BlockAndTintGetter level, BlockPos pos, String slot) {
        if (level == null || pos == null) {
            return null;
        }

        return readHexSlot(level.getBlockEntity(pos), slot);
    }

    public static Integer readHexSlot(BlockEntity blockEntity, String slot) {
        if (!(blockEntity instanceof HexColorSource hexSource)) {
            return null;
        }

        return TintColorUtil.nullableHex(hexSource.getHexColorOrUnset(HexColorComponent.cleanSlot(slot)));
    }

    public static Integer fromPlacementMemory(BlockState state, BlockAndTintGetter level, BlockPos pos) {
        return PearlPlacementColorMemory.get(level, pos);
    }
}
