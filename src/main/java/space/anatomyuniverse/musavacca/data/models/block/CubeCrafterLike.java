package space.anatomyuniverse.musavacca.data.models.block;

import net.minecraft.world.level.block.Block;

//? if <1.21.4 {
/*import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import space.anatomyuniverse.musavacca.data.models.ModelUtil;
*///?} else {
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.data.models.model.TexturedModel;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.template.ExtendedModelTemplate;
import net.neoforged.neoforge.client.model.generators.template.ExtendedModelTemplateBuilder;
//?}

public final class CubeCrafterLike {
    private CubeCrafterLike() {}

    //? if <1.21.4 {
    /*/^*
     * "Crafter-like" cube with 6 different face textures:
     *   block/<id>_bottom, _top, _north, _south, _west, _east
     * Particle is set to north.
     ^/
    public static void generate(BlockStateProvider gen, Block... blocks) {
        for (Block b : blocks) {
            ResourceLocation base = ModelUtil.blockTex(b); // "block/<id>"
            String ns = base.getNamespace();
            String p = base.getPath();

            ResourceLocation bottom = ResourceLocation.fromNamespaceAndPath(ns, p + "_bottom");
            ResourceLocation top = ResourceLocation.fromNamespaceAndPath(ns, p + "_top");
            ResourceLocation north = ResourceLocation.fromNamespaceAndPath(ns, p + "_north");
            ResourceLocation south = ResourceLocation.fromNamespaceAndPath(ns, p + "_south");
            ResourceLocation west = ResourceLocation.fromNamespaceAndPath(ns, p + "_west");
            ResourceLocation east = ResourceLocation.fromNamespaceAndPath(ns, p + "_east");

            BlockModelBuilder model = gen.models()
                    // signature: cube(name, down, up, north, south, east, west)
                    .cube(ModelUtil.pathOf(b), bottom, top, north, south, east, west)
                    .texture("particle", north);

            gen.simpleBlock(b, model);
            gen.simpleBlockItem(b, model);
        }
    }
    *///?} else {
    /**
     * 1.21.4+ Crafter-like cube template.
     * No FaceRotation enum dependency: top 180° encoded via UV flip.
     */
    private static final ExtendedModelTemplate CRAFTER_LIKE_CUBE = ExtendedModelTemplateBuilder.builder()
            .parent(ResourceLocation.fromNamespaceAndPath("minecraft", "block/block"))
            .requiredTextureSlot(TextureSlot.PARTICLE)
            .requiredTextureSlot(TextureSlot.BOTTOM)
            .requiredTextureSlot(TextureSlot.TOP)
            .requiredTextureSlot(TextureSlot.NORTH)
            .requiredTextureSlot(TextureSlot.SOUTH)
            .requiredTextureSlot(TextureSlot.WEST)
            .requiredTextureSlot(TextureSlot.EAST)
            .element(el -> el
                    .from(0, 0, 0).to(16, 16, 16)
                    .face(Direction.DOWN,  f -> f.uvs(0, 0, 16, 16).texture(TextureSlot.BOTTOM).cullface(Direction.DOWN))
                    .face(Direction.UP,    f -> f.uvs(16, 16, 0, 0).texture(TextureSlot.TOP).cullface(Direction.UP)) // 180° via UV flip
                    .face(Direction.NORTH, f -> f.uvs(0, 0, 16, 16).texture(TextureSlot.NORTH).cullface(Direction.NORTH))
                    .face(Direction.SOUTH, f -> f.uvs(0, 0, 16, 16).texture(TextureSlot.SOUTH).cullface(Direction.SOUTH))
                    .face(Direction.WEST,  f -> f.uvs(0, 0, 16, 16).texture(TextureSlot.WEST).cullface(Direction.WEST))
                    .face(Direction.EAST,  f -> f.uvs(0, 0, 16, 16).texture(TextureSlot.EAST).cullface(Direction.EAST))
            )
            .build();

    /**
     * Generates the "crafter-like" cube:
     * expects textures:
     *   block/<id>_bottom, _top, _north, _south, _west, _east
     * particle is set to north.
     */
    public static void generate(BlockModelGenerators gen, Block... blocks) {
        for (Block b : blocks) {
            TexturedModel.Provider provider = TexturedModel.createDefault(
                    blk -> new TextureMapping()
                            .put(TextureSlot.PARTICLE, TextureMapping.getBlockTexture(blk, "_north"))
                            .put(TextureSlot.BOTTOM,   TextureMapping.getBlockTexture(blk, "_bottom"))
                            .put(TextureSlot.TOP,      TextureMapping.getBlockTexture(blk, "_top"))
                            .put(TextureSlot.NORTH,    TextureMapping.getBlockTexture(blk, "_north"))
                            .put(TextureSlot.SOUTH,    TextureMapping.getBlockTexture(blk, "_south"))
                            .put(TextureSlot.WEST,     TextureMapping.getBlockTexture(blk, "_west"))
                            .put(TextureSlot.EAST,     TextureMapping.getBlockTexture(blk, "_east")),
                    CRAFTER_LIKE_CUBE
            );

            gen.createTrivialBlock(b, provider);
        }
    }
    //?}
}
