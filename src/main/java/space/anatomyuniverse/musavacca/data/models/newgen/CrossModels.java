package space.anatomyuniverse.musavacca.data.models.newgen;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import space.anatomyuniverse.musavacca.data.models.ModelUtil;

import java.util.Objects;
import java.util.function.Consumer;

public final class CrossModels {
    private CrossModels() {}

    public record Generated(ResourceLocation model, CrossTextures.Set textures) implements Models.Source {
        public Generated {
            Objects.requireNonNull(model, "model");
            Objects.requireNonNull(textures, "textures");
        }
    }

    public static Generated generated(Block block) {
        return generated(block, "", textures -> {});
    }

    public static Generated generated(Block block, Consumer<CrossTextures.Builder> textures) {
        return generated(block, "", textures);
    }

    public static Generated generated(
            Block block,
            String suffix,
            Consumer<CrossTextures.Builder> textures
    ) {
        Objects.requireNonNull(block, "block");
        Objects.requireNonNull(textures, "textures");

        CrossTextures.Builder builder = CrossTextures.of(block);
        textures.accept(builder);

        ResourceLocation blockId = ModelUtil.idOf(block);
        String cleanSuffix = suffix == null || suffix.isBlank()
                ? ""
                : "_" + suffix;

        ResourceLocation model = ResourceLocation.fromNamespaceAndPath(
                blockId.getNamespace(),
                "block/" + blockId.getPath() + cleanSuffix
        );

        return new Generated(model, builder.build());
    }
}
