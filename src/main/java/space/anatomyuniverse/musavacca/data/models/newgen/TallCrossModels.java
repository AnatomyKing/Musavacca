package space.anatomyuniverse.musavacca.data.models.newgen;

import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.world.level.block.Block;

public final class TallCrossModels {
    private TallCrossModels() {}

    public interface Source {
        Models.Source lower();

        Models.Source upper();
    }

    public record Existing(Models.Existing lower, Models.Existing upper) implements Source {
        public Existing {
            Objects.requireNonNull(lower, "lower");
            Objects.requireNonNull(upper, "upper");
        }
    }

    public record Generated(CrossModels.Generated lower, CrossModels.Generated upper, FamilyTextures.Halves textures) implements Source {
        public Generated {
            Objects.requireNonNull(lower, "lower");
            Objects.requireNonNull(upper, "upper");
            Objects.requireNonNull(textures, "textures");
        }
    }

    public static Existing existing(String lowerModel, String upperModel) {
        return new Existing(Models.existing(lowerModel), Models.existing(upperModel));
    }

    public static Generated generated(Block block, Consumer<FamilyTextures.HalvesBuilder> textures) {
        return generated(block, "", textures);
    }

    public static Generated generated(Block block, String suffix, Consumer<FamilyTextures.HalvesBuilder> textures) {
        Objects.requireNonNull(block, "block");
        Objects.requireNonNull(textures, "textures");
        FamilyTextures.HalvesBuilder builder = FamilyTextures.halves(block);
        textures.accept(builder);
        FamilyTextures.Halves set = builder.build();
        String cleanSuffix = suffix == null || suffix.isBlank() ? "" : suffix;
        var lowerModel = ModelLocations.blockModel(block, cleanSuffix + "_bottom");
        var upperModel = ModelLocations.blockModel(block, cleanSuffix + "_top");
        return new Generated(new CrossModels.Generated(lowerModel, new FamilyTextures.Set(set.bottom())),
                new CrossModels.Generated(upperModel, new FamilyTextures.Set(set.top())), set);
    }
}
