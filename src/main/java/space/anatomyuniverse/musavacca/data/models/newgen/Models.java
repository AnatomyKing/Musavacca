package space.anatomyuniverse.musavacca.data.models.newgen;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import space.anatomyuniverse.musavacca.data.models.ModelUtil;

import java.util.Objects;
import java.util.function.Consumer;

public final class Models {
    private Models() {}

    public interface Source {
    }

    public record Existing(ResourceLocation model) implements Source {

        public Existing {
            Objects.requireNonNull(model, "model");
        }
    }

    public record Generated(ResourceLocation model, Textures.Set textures) implements Source {

        public Generated {
            Objects.requireNonNull(model, "model");

            Objects.requireNonNull(textures, "textures");
        }
    }

    public static Existing existing(String modelId) {
        if (modelId == null || modelId.isBlank()) {
            throw new IllegalArgumentException("modelId must not be blank");
        }

        return new Existing(ResourceLocation.parse(modelId));
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

        ResourceLocation blockId = ModelUtil.idOf(block);

        String cleanSuffix = suffix == null
                        || suffix.isBlank()
                        ? ""
                        : "_"
                                + suffix;

        ResourceLocation modelId = ResourceLocation
                        .fromNamespaceAndPath(blockId.getNamespace(), "block/" + blockId.getPath() + cleanSuffix);

        return new Generated(modelId, builder.build());
    }
}
