package space.anatomyuniverse.musavacca.data.models.newgen;

//? if >=1.21.11 {
/*import net.minecraft.resources.Identifier;
*///?} else {
import net.minecraft.resources.ResourceLocation;
//?}
import net.minecraft.world.level.block.Block;

import java.util.Objects;
import java.util.function.Consumer;

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

    public record Generated(
            CrossModels.Generated lower,
            CrossModels.Generated upper,
            TallCrossTextures.Set textures
    ) implements Source {
        public Generated {
            Objects.requireNonNull(lower, "lower");
            Objects.requireNonNull(upper, "upper");
            Objects.requireNonNull(textures, "textures");
        }
    }

    public static Existing existing(String lowerModel, String upperModel) {
        return new Existing(
                Models.existing(lowerModel),
                Models.existing(upperModel)
        );
    }

    public static Generated generated(Block block) {
        return generated(block, "", textures -> {});
    }

    public static Generated generated(Block block, Consumer<TallCrossTextures.Builder> textures) {
        return generated(block, "", textures);
    }

    public static Generated generated(
            Block block,
            String suffix,
            Consumer<TallCrossTextures.Builder> textures
    ) {
        Objects.requireNonNull(block, "block");
        Objects.requireNonNull(textures, "textures");

        TallCrossTextures.Builder builder = TallCrossTextures.of(block);
        textures.accept(builder);
        TallCrossTextures.Set set = builder.build();

        String cleanSuffix = suffix == null || suffix.isBlank()
                ? ""
                : suffix;

        //? if >=1.21.11 {
        /*Identifier lowerModel = ModelLocations.blockModel(
        *///?} else {
        ResourceLocation lowerModel = ModelLocations.blockModel(
        //?}
                block,
                cleanSuffix + "_bottom"
        );

        //? if >=1.21.11 {
        /*Identifier upperModel = ModelLocations.blockModel(
        *///?} else {
        ResourceLocation upperModel = ModelLocations.blockModel(
        //?}
                block,
                cleanSuffix + "_top"
        );

        return new Generated(
                new CrossModels.Generated(
                        lowerModel,
                        new CrossTextures.Set(set.bottom())
                ),
                new CrossModels.Generated(
                        upperModel,
                        new CrossTextures.Set(set.top())
                ),
                set
        );
    }
}
