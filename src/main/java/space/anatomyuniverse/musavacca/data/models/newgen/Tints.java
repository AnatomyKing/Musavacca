package space.anatomyuniverse.musavacca.data.models.newgen;

import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.block.state.BlockState;
import space.anatomyuniverse.musavacca.MusaCore;
import space.anatomyuniverse.musavacca.component.ModDataComponents;
import space.anatomyuniverse.musavacca.tint.MusavaccaTints;

import java.util.Objects;
//? if >=26.1 {
/*import net.minecraft.client.renderer.block.BlockAndTintGetter;
*///?} else {
import net.minecraft.world.level.BlockAndTintGetter;
//?}

//? if >=1.21.11 {
/*import net.minecraft.resources.Identifier;
*///?} else if >=1.21.4 {
import net.minecraft.resources.ResourceLocation;
//?}
//? if >=1.21.4 {
import com.mojang.serialization.MapCodec;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
//?}

public final class Tints {
    public static final int NO_TINT = 0xFFFFFFFF;

    private Tints() {}

    public interface Tint {
        int tintIndex();

        default boolean tinted() {
            return tintIndex() >= 0;
        }

        default int blockColor(BlockState state, BlockAndTintGetter level, BlockPos pos, int tintIndex) {
            return NO_TINT;
        }

        default int itemColor(ItemStack stack) {
            return NO_TINT;
        }

        //? if >=1.21.4 {
        default ItemTintSource itemTintSource() {
            return new net.minecraft.client.color.item.Constant(NO_TINT);
        }
        //?}
    }

    public record None() implements Tint {
        @Override
        public int tintIndex() {
            return -1;
        }
    }

    public record Constant(int rgb) implements Tint {
        public Constant {
            rgb &= 0xFFFFFF;
        }

        @Override
        public int tintIndex() {
            return 0;
        }

        @Override
        public int blockColor(BlockState state, BlockAndTintGetter level, BlockPos pos, int tintIndex) {
            return opaque(rgb);
        }

        @Override
        public int itemColor(ItemStack stack) {
            return opaque(rgb);
        }

        //? if >=1.21.4 {
        @Override
        public ItemTintSource itemTintSource() {
            return new net.minecraft.client.color.item.Constant(rgb);
        }
        //?}
    }

    public record BiomeFoliage() implements Tint {
        @Override
        public int tintIndex() {
            return 0;
        }

        @Override
        public int blockColor(BlockState state, BlockAndTintGetter level, BlockPos pos, int tintIndex) {
            return opaque(level != null && pos != null ? BiomeColors.getAverageFoliageColor(level, pos) : defaultFoliageColor());
        }

        @Override
        public int itemColor(ItemStack stack) {
            return opaque(defaultFoliageColor());
        }

        //? if >=1.21.4 {
        @Override
        public ItemTintSource itemTintSource() {
            return new net.minecraft.client.color.item.Constant(defaultFoliageColor());
        }
        //?}
    }

    public record Hex() implements Tint
            //? if >=1.21.4 {
            , ItemTintSource
            //?}
    {
        private static final Hex INSTANCE = new Hex();

        //? if >=1.21.11 {
        /*public static final Identifier TINT_ID = Identifier.fromNamespaceAndPath(MusaCore.MOD_ID, MusavaccaTints.HEX_COLOR_KEY);
        *///?} else if >=1.21.4 {
        public static final ResourceLocation TINT_ID = ResourceLocation.fromNamespaceAndPath(MusaCore.MOD_ID, MusavaccaTints.HEX_COLOR_KEY);
        //?}
        //? if >=1.21.4 {
        public static final MapCodec<Hex> MAP_CODEC = MapCodec.unit(INSTANCE);
        //?}

        @Override
        public int tintIndex() {
            return 0;
        }

        @Override
        public int blockColor(BlockState state, BlockAndTintGetter level, BlockPos pos, int tintIndex) {
            if (level == null || pos == null) return NO_TINT;
            return MusavaccaTints.opaqueRgb(MusavaccaTints.blockEntityHexColor(level.getBlockEntity(pos)));
        }

        @Override
        public int itemColor(ItemStack stack) {
            return MusavaccaTints.opaqueRgb(stack.getOrDefault(ModDataComponents.HEX_COLOR.get(), MusavaccaTints.NO_TINT));
        }

        //? if >=1.21.4 {
        @Override
        public ItemTintSource itemTintSource() {
            return this;
        }

        @Override
        public int calculate(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity) {
            return itemColor(stack);
        }

        @Override
        public MapCodec<Hex> type() {
            return MAP_CODEC;
        }
        //?}
    }

    private static final None NONE = new None();
    private static final BiomeFoliage FOLIAGE = new BiomeFoliage();

    public static Tint none() {
        return NONE;
    }

    public static Constant constant(int rgb) {
        return new Constant(rgb);
    }

    public static BiomeFoliage foliage() {
        return FOLIAGE;
    }

    public static Hex hex() {
        return Hex.INSTANCE;
    }

    public static Tint effective(Tint inherited, Tint local) {
        Objects.requireNonNull(inherited, "inherited");
        return local != null ? local : inherited;
    }

    private static int opaque(int rgb) {
        return 0xFF000000 | (rgb & 0xFFFFFF);
    }

    private static int defaultFoliageColor() {
        //? if >=1.21.4 {
        return FoliageColor.FOLIAGE_DEFAULT;
        //?} else {
        /*return FoliageColor.getDefaultColor();
        *///?}
    }
}
