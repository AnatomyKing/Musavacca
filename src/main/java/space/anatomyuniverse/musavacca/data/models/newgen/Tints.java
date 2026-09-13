package space.anatomyuniverse.musavacca.data.models.newgen;

import space.anatomyuniverse.musavacca.tint.PearlFireTintProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class Tints {
    private Tints() {}

    public enum Kind {
        NONE,
        CONSTANT,
        BIOME_FOLIAGE,
        HEX_COLOR,
        PEARL_FIRE
    }

    public sealed interface Tint
            permits None,
                    Constant,
                    BiomeFoliage,
                    HexColor,
                    PearlFire {

        Kind kind();

        int tintIndex();

        default boolean tinted() {
            return kind()
                    != Kind.NONE;
        }
    }

    public record None()
            implements Tint {

        @Override
        public Kind kind() {
            return Kind.NONE;
        }

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
        public Kind kind() {
            return Kind.CONSTANT;
        }

        @Override
        public int tintIndex() {
            return 0;
        }
    }

    public record BiomeFoliage()
            implements Tint {

        @Override
        public Kind kind() {
            return Kind.BIOME_FOLIAGE;
        }

        @Override
        public int tintIndex() {
            return 0;
        }
    }

    public record HexColor()
            implements Tint {

        @Override
        public Kind kind() {
            return Kind.HEX_COLOR;
        }

        @Override
        public int tintIndex() {
            return 0;
        }
    }

    public record PearlFire(PearlFireTintProfiles.Profile profile, int offset) implements Tint {

        public PearlFire {
            Objects.requireNonNull(profile, "profile");

            if (offset < 0) {
                throw new IllegalArgumentException("offset must be >= 0");
            }
        }

        public PearlFire offset(int newOffset) {
            return new PearlFire(profile, newOffset);
        }

        @Override
        public Kind kind() {
            return Kind.PEARL_FIRE;
        }

        @Override
        public int tintIndex() {
            return offset;
        }
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

    public static List<GeneratedLayer> generatedLayers(Tint tint) {
        Objects.requireNonNull(tint, "tint");

        if (tint instanceof PearlFire pearlFire) {
            int count = pearlFire.profile().layerCount();
            List<GeneratedLayer> layers = new ArrayList<>(count);

            for (int layer = 0; layer < count; layer++) {
                layers.add(new GeneratedLayer(layer, pearlFire.offset() + layer));
            }

            return List.copyOf(layers);
        }

        return List.of(new GeneratedLayer(0, tint.tintIndex()));
    }

    public static int generatedLayerCount(Tint tint) {
        return generatedLayers(tint).size();
    }

    private static final None NONE = new None();

    private static final BiomeFoliage FOLIAGE = new BiomeFoliage();

    private static final HexColor HEX_COLOR = new HexColor();

    public static Tint none() {
        return NONE;
    }

    public static Constant constant(int rgb) {
        return new Constant(rgb);
    }

    public static BiomeFoliage foliage() {
        return FOLIAGE;
    }

    public static HexColor hexColor() {
        return HEX_COLOR;
    }

    public static PearlFire pearlFire(PearlFireTintProfiles.Profile profile) {
        return new PearlFire(profile, 0);
    }
}
