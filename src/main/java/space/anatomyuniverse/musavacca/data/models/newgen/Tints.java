package space.anatomyuniverse.musavacca.data.models.newgen;

import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Objects;

//? if >=1.21.4 {
import com.mojang.serialization.MapCodec;
import net.minecraft.client.color.item.ItemTintSource;
//?}

public final class Tints {
    public static final int NO_TINT = 0xFFFFFFFF;

    private static final int RGB_MASK = 0xFFFFFF;

    private Tints() {}

    public interface Tint {
        int tintIndex();

        default boolean tinted() {
            return tintIndex() >= 0;
        }

        default List<GeneratedLayer> generatedLayers() {
            return List.of(new GeneratedLayer(0, tintIndex()));
        }

        default Tint physicalLayer(GeneratedLayer layer) {
            return this;
        }

        default int blockColor(
                BlockState state,
                BlockAndTintGetter level,
                BlockPos pos,
                int tintIndex
        ) {
            return NO_TINT;
        }

        default int itemColor(ItemStack stack) {
            return NO_TINT;
        }

        //? if >=1.21.4 {
        default ItemTintSource itemTintSource() {
            return new net.minecraft.client.color.item.Constant(NO_TINT);
        }

        default ItemTintType itemTintType() {
            return null;
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
            rgb &= RGB_MASK;
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
            return level != null && pos != null
                    ? BiomeColors.getAverageFoliageColor(level, pos)
                    : defaultFoliageColor();
        }

        @Override
        public int itemColor(ItemStack stack) {
            return defaultFoliageColor();
        }

        //? if >=1.21.4 {
        @Override
        public ItemTintSource itemTintSource() {
            return new net.minecraft.client.color.item.Constant(defaultFoliageColor());
        }
        //?}
    }

    public record GeneratedLayer(int sourceLayer, int tintIndex) {
        public GeneratedLayer {
            if (sourceLayer < 0) {
                throw new IllegalArgumentException("sourceLayer must be >= 0");
            }
            if (tintIndex < -1) {
                throw new IllegalArgumentException("tintIndex must be >= -1");
            }
        }
    }

    public record GeneratedPlan(List<GeneratedLayer> layers) {
        public GeneratedPlan {
            Objects.requireNonNull(layers, "layers");
            if (layers.isEmpty()) {
                throw new IllegalArgumentException("layers must not be empty");
            }
            layers = List.copyOf(layers);
        }

        public int layerCount() {
            return layers.size();
        }

        public boolean layered() {
            return layers.size() > 1;
        }

        public GeneratedLayer singleLayer(String family) {
            if (layers.size() != 1) {
                throw new IllegalStateException(
                        family + " generates one texture carrier per model, but tint requires "
                                + layers.size() + " generated layers"
                );
            }
            return layers.get(0);
        }
    }

    public record ModelKey(List<GeneratedLayer> layers) {
        public ModelKey {
            layers = List.copyOf(layers);
        }
    }

    //? if >=1.21.4 {
    public record ItemTintType(
            ResourceLocation id,
            MapCodec<? extends ItemTintSource> codec
    ) {
        public ItemTintType {
            Objects.requireNonNull(id, "id");
            Objects.requireNonNull(codec, "codec");
        }
    }
    //?}

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

    public static GeneratedPlan generatedPlan(Tint tint) {
        Objects.requireNonNull(tint, "tint");
        return new GeneratedPlan(tint.generatedLayers());
    }

    public static List<GeneratedLayer> generatedLayers(Tint tint) {
        return generatedPlan(tint).layers();
    }

    public static int generatedLayerCount(Tint tint) {
        return generatedPlan(tint).layerCount();
    }

    public static GeneratedLayer singleGeneratedLayer(Tint tint, String family) {
        return generatedPlan(tint).singleLayer(family);
    }

    public static Tint effective(Tint inherited, Tint local) {
        Objects.requireNonNull(inherited, "inherited");
        return local != null ? local : inherited;
    }

    public static ModelKey modelKey(Tint tint) {
        return new ModelKey(generatedLayers(tint));
    }

    private static int opaque(int rgb) {
        return 0xFF000000 | (rgb & RGB_MASK);
    }

    private static int defaultFoliageColor() {
        //? if >1.21.3 {
        return FoliageColor.FOLIAGE_DEFAULT;
        //?} else {
        /*return FoliageColor.getDefaultColor();
        *///?}
    }
}
