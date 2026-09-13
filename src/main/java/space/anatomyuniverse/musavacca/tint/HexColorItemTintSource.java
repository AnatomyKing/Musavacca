// file: src/main/java/space/anatomyuniverse/musavacca/tint/HexColorItemTintSource.java
package space.anatomyuniverse.musavacca.tint;

import net.minecraft.world.item.ItemStack;
import space.anatomyuniverse.musavacca.component.ModDataComponents;

//? if >=1.21.4 {
import com.mojang.serialization.MapCodec;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
//?}

public final class HexColorItemTintSource
        //? if <1.21.4 {
        /*{
         *///?} else {
        implements ItemTintSource {
    //?}

    public static final HexColorItemTintSource INSTANCE =
            new HexColorItemTintSource();

    //? if >=1.21.4 {
    public static final MapCodec<HexColorItemTintSource> MAP_CODEC =
            MapCodec.unit(INSTANCE);
    //?}

    private HexColorItemTintSource() {}

    /**
     * One plain item tint: use the stack's HEX_COLOR, otherwise white.
     * No PearlFire profile, no per-layer color math.
     */
    public static int color(ItemStack stack) {
        Integer savedHex = stack.get(ModDataComponents.HEX_COLOR.get());

        return TintColorUtil.opaqueRgb(
                savedHex != null
                        ? savedHex
                        : TintColorUtil.defaultHexBlockItemTint()
        );
    }

    //? if >=1.21.4 {
    @Override
    public int calculate(
            ItemStack stack,
            @Nullable ClientLevel level,
            @Nullable LivingEntity entity
    ) {
        return color(stack);
    }

    @Override
    public MapCodec<HexColorItemTintSource> type() {
        return MAP_CODEC;
    }
    //?}
}
