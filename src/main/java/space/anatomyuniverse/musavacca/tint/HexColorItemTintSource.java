
package space.anatomyuniverse.musavacca.tint;

//? if >=1.21.4 {
import com.mojang.serialization.MapCodec;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import space.anatomyuniverse.musavacca.component.ModDataComponents;
//?}

public final class HexColorItemTintSource
        //? if <1.21.4 {
        /*{
         *///?} else {
        implements ItemTintSource {
    //?}

    public static final HexColorItemTintSource INSTANCE = new HexColorItemTintSource();

    //? if >=1.21.4 {
    public static final MapCodec<HexColorItemTintSource> MAP_CODEC = MapCodec.unit(INSTANCE);
    //?}

    private HexColorItemTintSource() {}

    //? if >=1.21.4 {
    @Override
    public int calculate(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity) {
        Integer savedHex = stack.get(ModDataComponents.HEX_COLOR.get());
        if (savedHex != null) {
            return TintColorUtil.opaqueRgb(savedHex);
        }

        return TintColorUtil.opaqueRgb(TintColorUtil.defaultHexBlockItemTint());
    }

    @Override
    public MapCodec<HexColorItemTintSource> type() {
        return MAP_CODEC;
    }
    //?}
}