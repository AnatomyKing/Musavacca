package space.anatomyuniverse.musavacca.data.models.newgen;

//? if >=1.21.11 {
/*import net.minecraft.resources.Identifier;
*///?} else {
import net.minecraft.resources.ResourceLocation;
//?}
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.world.level.block.Block;

public final class CrossModels {
    private CrossModels() {}

    //? if >=1.21.11 {
    /*public record Generated(Identifier model, FamilyTextures.Set textures) implements Models.Source {
    *///?} else {
    public record Generated(ResourceLocation model, FamilyTextures.Set textures) implements Models.Source {
    //?}
        public Generated {
            Objects.requireNonNull(model, "model");
            Objects.requireNonNull(textures, "textures");
        }
    }

    public static Generated generated(Block block, Consumer<FamilyTextures.Builder> textures) {
        return generated(block, "", textures);
    }

    public static Generated generated(Block block, String suffix, Consumer<FamilyTextures.Builder> textures) {
        Objects.requireNonNull(block, "block");
        Objects.requireNonNull(textures, "textures");
        FamilyTextures.Builder builder = FamilyTextures.builder(block);
        textures.accept(builder);
        return new Generated(ModelLocations.blockModel(block, suffix), builder.build());
    }
}
