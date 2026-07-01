package space.anatomyuniverse.musavacca.data.models.unified;

import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import space.anatomyuniverse.musavacca.tint.TintColorUtil;

import java.util.List;

//? if >=1.21.4 {
import net.minecraft.client.color.item.Constant;
//?}

public final class BiomeTint implements ItemTint {
    private final Integer fixedItemTint;
    private final int blockTintIndex;

    private BiomeTint(Integer fixedItemTint, int blockTintIndex) {
        this.fixedItemTint = fixedItemTint;
        this.blockTintIndex = Math.max(0, blockTintIndex);
    }

    public static BiomeTint foliage() {
        return new BiomeTint(null, 0);
    }

    public static BiomeTint fixed(int rgb) {
        return new BiomeTint(rgb, 0);
    }

    public BiomeTint tintIndex(int blockTintIndex) {
        return new BiomeTint(fixedItemTint, blockTintIndex);
    }

    public int itemTint() {
        return fixedItemTint != null ? fixedItemTint : TintColorUtil.defaultFoliageItemTint();
    }

    @Override
    public boolean useTintedGeneratedBlockModel() {
        return true;
    }

    @Override
    public boolean hasBlockTint() {
        return true;
    }

    @Override
    public int blockTint(BlockState state, BlockAndTintGetter level, BlockPos pos, int tintIndex) {
        if (tintIndex != blockTintIndex) {
            return TintColorUtil.NO_TINT;
        }

        if (level != null && pos != null) {
            return BiomeColors.getAverageFoliageColor(level, pos);
        }

        return itemTint();
    }

    //? if >=1.21.4 {
    @Override
    public List<?> sources() {
        return List.of(new Constant(itemTint()));
    }
    //?}
}
