package space.anatomyuniverse.musavacca.data.models.unified;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import space.anatomyuniverse.musavacca.component.HexColorComponent;
import space.anatomyuniverse.musavacca.tint.BlockTintResolvers;
import space.anatomyuniverse.musavacca.tint.TintColorUtil;

import java.util.List;

public final class HexColorTint implements ItemTint {
    private final boolean dynamicHexItemTint;
    private final int constantItemTint;
    private final int blockTintIndex;
    private final String blockHexSlot;
    private final BlockTintColorResolver blockResolver;

    private HexColorTint(
            boolean dynamicHexItemTint,
            int constantItemTint,
            int blockTintIndex,
            String blockHexSlot,
            BlockTintColorResolver blockResolver
    ) {
        this.dynamicHexItemTint = dynamicHexItemTint;
        this.constantItemTint = TintColorUtil.rgb(constantItemTint);
        this.blockTintIndex = Math.max(0, blockTintIndex);
        this.blockHexSlot = blockHexSlot == null ? null : HexColorComponent.cleanSlot(blockHexSlot);
        this.blockResolver = blockResolver;
    }

    /**
     * Item side now uses vanilla minecraft:dye.
     * HexColorComponent mirrors the selected color into minecraft:dyed_color.
     */
    public static HexColorTint slot(String slot) {
        return new HexColorTint(true, TintColorUtil.defaultHexItemTint(), 0, slot, null);
    }

    public static HexColorTint constant(int constantItemTint) {
        return new HexColorTint(false, constantItemTint, 0, null, (state, level, pos) -> constantItemTint);
    }

    public HexColorTint tintIndex(int blockTintIndex) {
        return new HexColorTint(dynamicHexItemTint, constantItemTint, blockTintIndex, blockHexSlot, blockResolver);
    }

    public HexColorTint hexSlot(String slot) {
        return new HexColorTint(dynamicHexItemTint, constantItemTint, blockTintIndex, slot, blockResolver);
    }

    public HexColorTint resolver(BlockTintColorResolver blockResolver) {
        return new HexColorTint(dynamicHexItemTint, constantItemTint, blockTintIndex, blockHexSlot, blockResolver);
    }

    @Override
    public boolean useTintedGeneratedBlockModel() {
        return true;
    }

    @Override
    public boolean hasBlockTint() {
        return blockResolver != null || blockHexSlot != null || !dynamicHexItemTint;
    }

    @Override
    public int blockTint(BlockState state, BlockAndTintGetter level, BlockPos pos, int tintIndex) {
        if (tintIndex != blockTintIndex) {
            return TintColorUtil.NO_TINT;
        }

        Integer rgb = blockResolver != null
                ? blockResolver.resolve(state, level, pos)
                : blockHexSlot != null
                ? BlockTintResolvers.readHexSlot(level, pos, blockHexSlot)
                : constantItemTint;

        return rgb == null ? TintColorUtil.defaultHexBlockTint() : TintColorUtil.opaqueRgb(rgb);
    }

    //? if >=1.21.4 {
    @Override
    public List<?> sources() {
        return List.of(
                dynamicHexItemTint
                        ? VanillaItemTintSources.dye(constantItemTint)
                        : VanillaItemTintSources.constant(constantItemTint)
        );
    }
    //?}
}
