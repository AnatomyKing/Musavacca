package space.anatomyuniverse.musavacca.data.models.newgen;

//? if >=1.21.11 {
/*import net.minecraft.resources.Identifier;
*///?} else {
import net.minecraft.resources.ResourceLocation;
//?}
import net.minecraft.world.level.block.Block;

import java.util.Objects;
import java.util.function.Consumer;

public final class Models {
    private Models() {}

    public interface Source {
    }

    //? if >=1.21.11 {
    /*public record Existing(Identifier model) implements Source {
    *///?} else {
    public record Existing(ResourceLocation model) implements Source {
    //?}

        public Existing {
            Objects.requireNonNull(model, "model");
        }
    }

    //? if >=1.21.11 {
    /*public record Generated(Identifier model, Textures.Set textures) implements Source {
    *///?} else {
    public record Generated(ResourceLocation model, Textures.Set textures) implements Source {
    //?}

        public Generated {
            Objects.requireNonNull(model, "model");

            Objects.requireNonNull(textures, "textures");
        }
    }

    public static Existing existing(String modelId) {
        if (modelId == null || modelId.isBlank()) {
            throw new IllegalArgumentException("modelId must not be blank");
        }

        //? if >=1.21.11 {
        /*return new Existing(Identifier.parse(modelId));
        *///?} else {
        return new Existing(ResourceLocation.parse(modelId));
        //?}
    }

    public static Generated generated(Block block) {
        return generated(
                block,
                "",
                textures -> {
                }
        );
    }

    public static Generated generated(Block block, Consumer<Textures.Builder> textures) {
        return generated(block, "", textures);
    }

    public static Generated generated(Block block, String suffix, Consumer<Textures.Builder> textures) {
        Objects.requireNonNull(block, "block");

        Objects.requireNonNull(textures, "textures");

        Textures.Builder builder = Textures.of(block);

        textures.accept(builder);

        return new Generated(
                ModelLocations.blockModel(block, suffix),
                builder.build()
        );
    }
}
