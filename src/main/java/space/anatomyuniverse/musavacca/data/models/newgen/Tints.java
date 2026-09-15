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
            return layerCount() > 1;
        }

        public GeneratedLayer singleLayer(String family) {
            if (layers.size() != 1) {
                throw new IllegalStateException(
                        family + " generates one texture carrier per model, but tint "
                                + "requires " + layers.size() + " generated layers. "
                                + "Use a layered-capable model family or an existing authored model."
                );
            }

            return layers.get(0);
        }
    }

    public static GeneratedPlan generatedPlan(Tint tint) {
        Objects.requireNonNull(tint, "tint");

        if (tint instanceof PearlFire pearlFire) {
            int count = pearlFire.profile().layerCount();
            List<GeneratedLayer> layers = new ArrayList<>(count);

            for (int layer = 0; layer < count; layer++) {
                layers.add(new GeneratedLayer(layer, pearlFire.offset() + layer));
            }

            return new GeneratedPlan(layers);
        }

        return new GeneratedPlan(
                List.of(new GeneratedLayer(0, tint.tintIndex()))
        );
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

    public record ModelKey(List<GeneratedLayer> layers) {
        public ModelKey {
            layers = List.copyOf(layers);
        }
    }

    public static Tint effective(Tint inherited, Tint local) {
        Objects.requireNonNull(inherited, "inherited");
        return local != null ? local : inherited;
    }

    public static ModelKey modelKey(Tint tint) {
        return new ModelKey(generatedPlan(tint).layers());
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
