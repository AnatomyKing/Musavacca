package space.anatomyuniverse.musavacca.data.models.engine.block;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import space.anatomyuniverse.musavacca.data.models.engine.core.EngineContext;

import java.util.HashSet;
import java.util.Set;

//? if <1.21.4 {
/*import space.anatomyuniverse.musavacca.data.models.ModelUtil;
*///?} else {
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.data.models.model.TexturedModel;
import net.neoforged.neoforge.client.model.generators.template.ExtendedModelTemplate;
import net.neoforged.neoforge.client.model.generators.template.ExtendedModelTemplateBuilder;
//?}

public final class EngineBlockGeneratedModels {
    private EngineBlockGeneratedModels() {}

    public static void generate(EngineContext ctx, EngineBlockEntry entry) {
        if (ctx == null || entry == null || entry.block() == null) return;

        Set<ResourceLocation> generated = new HashSet<>();

        for (EngineBlockModel model : entry.models()) {
            generateOne(ctx, entry.block(), model, entry.useTintedGeneratedBlockModel(), generated);
        }

        for (EngineBlockPart part : entry.parts()) {
            if (part == null) continue;
            generateOne(
                    ctx,
                    entry.block(),
                    part.model(),
                    entry.useTintedGeneratedBlockModel() || part.useTintedGeneratedBlockModel(),
                    generated
            );
        }
    }

    private static void generateOne(
            EngineContext ctx,
            Block block,
            EngineBlockModel model,
            boolean tinted,
            Set<ResourceLocation> generated
    ) {
        if (model == null || !model.generatesModelJson()) return;

        ResourceLocation modelId = model.modelLocation(block);
        if (!generated.add(modelId)) return;

        switch (model.kind()) {
            case CUBE -> cube(ctx, block, modelId, model.textureLocation(block), tinted);
            case COLUMN -> column(ctx, block, modelId, model.textureLocation(block), model.textureLocation(block, "_top"), tinted);
            case BOTTOM_TOP -> bottomTop(
                    ctx,
                    block,
                    modelId,
                    model.textureLocation(block),
                    model.textureLocation(block, "_bottom"),
                    model.textureLocation(block, "_top"),
                    tinted
            );
            case CROSS -> cross(ctx, modelId, model.textureLocation(block), tinted);
            case EXISTING -> {}
        }
    }

    public static void cube(EngineContext ctx, Block block) {
        cube(ctx, block, null, null, false);
    }

    private static void cube(
            EngineContext ctx,
            Block block,
            ResourceLocation modelId,
            ResourceLocation textureId,
            boolean tinted
    ) {
        //? if <1.21.4 {
        /*ctx.blocks().models().cubeAll(ModelUtil.pathOf(block), ModelUtil.blockTex(block));
        *///?} else {
        if (!tinted) {
            TexturedModel.CUBE.create(block, ctx.blocks().modelOutput);
            return;
        }

        ResourceLocation finalModelId = modelId == null ? blockModel(block) : modelId;
        ResourceLocation finalTextureId = textureId == null ? blockTexture(block) : textureId;

        tintedCubeAllTemplate().create(
                finalModelId,
                new TextureMapping()
                        .put(TextureSlot.PARTICLE, finalTextureId)
                        .put(TextureSlot.ALL, finalTextureId),
                ctx.blocks().modelOutput
        );
        //?}
    }

    public static void cross(EngineContext ctx, ResourceLocation modelId, ResourceLocation textureId, boolean tinted) {
        //? if <1.21.4 {
        /*var parent = ResourceLocation.parse(tinted ? "minecraft:block/tinted_cross" : "minecraft:block/cross");
        ctx.blocks().models().withExistingParent(modelId.getPath(), parent).texture("cross", textureId);
        *///?} else {
        if (tinted) ModelTemplates.TINTED_CROSS.create(modelId, TextureMapping.cross(textureId), ctx.blocks().modelOutput);
        else ModelTemplates.CROSS.create(modelId, TextureMapping.cross(textureId), ctx.blocks().modelOutput);
        //?}
    }

    //? if >=1.21.4 {
    private static void column(
            EngineContext ctx,
            Block block,
            ResourceLocation modelId,
            ResourceLocation sideTexture,
            ResourceLocation endTexture,
            boolean tinted
    ) {
        TextureMapping mapping = new TextureMapping()
                .put(TextureSlot.PARTICLE, sideTexture)
                .put(TextureSlot.SIDE, sideTexture)
                .put(TextureSlot.END, endTexture);

        if (tinted) {
            tintedColumnTemplate().create(modelId, mapping, ctx.blocks().modelOutput);
        } else {
            ModelTemplates.CUBE_COLUMN.create(modelId, mapping, ctx.blocks().modelOutput);
        }
    }

    private static void bottomTop(
            EngineContext ctx,
            Block block,
            ResourceLocation modelId,
            ResourceLocation sideTexture,
            ResourceLocation bottomTexture,
            ResourceLocation topTexture,
            boolean tinted
    ) {
        TextureMapping mapping = new TextureMapping()
                .put(TextureSlot.PARTICLE, sideTexture)
                .put(TextureSlot.SIDE, sideTexture)
                .put(TextureSlot.BOTTOM, bottomTexture)
                .put(TextureSlot.TOP, topTexture);

        if (tinted) {
            tintedBottomTopTemplate().create(modelId, mapping, ctx.blocks().modelOutput);
        } else {
            ModelTemplates.CUBE_BOTTOM_TOP.create(modelId, mapping, ctx.blocks().modelOutput);
        }
    }

    private static ExtendedModelTemplate tintedCubeAllTemplate() {
        return ExtendedModelTemplateBuilder.builder()
                .parent(ResourceLocation.fromNamespaceAndPath("minecraft", "block/block"))
                .requiredTextureSlot(TextureSlot.PARTICLE)
                .requiredTextureSlot(TextureSlot.ALL)
                .element(element -> element
                        .from(0, 0, 0).to(16, 16, 16)
                        .face(Direction.DOWN, face -> face.uvs(0, 0, 16, 16).texture(TextureSlot.ALL).cullface(Direction.DOWN).tintindex(0))
                        .face(Direction.UP, face -> face.uvs(0, 0, 16, 16).texture(TextureSlot.ALL).cullface(Direction.UP).tintindex(0))
                        .face(Direction.NORTH, face -> face.uvs(0, 0, 16, 16).texture(TextureSlot.ALL).cullface(Direction.NORTH).tintindex(0))
                        .face(Direction.SOUTH, face -> face.uvs(0, 0, 16, 16).texture(TextureSlot.ALL).cullface(Direction.SOUTH).tintindex(0))
                        .face(Direction.WEST, face -> face.uvs(0, 0, 16, 16).texture(TextureSlot.ALL).cullface(Direction.WEST).tintindex(0))
                        .face(Direction.EAST, face -> face.uvs(0, 0, 16, 16).texture(TextureSlot.ALL).cullface(Direction.EAST).tintindex(0))
                )
                .build();
    }

    private static ExtendedModelTemplate tintedColumnTemplate() {
        return ExtendedModelTemplateBuilder.builder()
                .parent(ResourceLocation.fromNamespaceAndPath("minecraft", "block/block"))
                .requiredTextureSlot(TextureSlot.PARTICLE)
                .requiredTextureSlot(TextureSlot.SIDE)
                .requiredTextureSlot(TextureSlot.END)
                .element(element -> element
                        .from(0, 0, 0).to(16, 16, 16)
                        .face(Direction.DOWN, face -> face.uvs(0, 0, 16, 16).texture(TextureSlot.END).cullface(Direction.DOWN).tintindex(0))
                        .face(Direction.UP, face -> face.uvs(0, 0, 16, 16).texture(TextureSlot.END).cullface(Direction.UP).tintindex(0))
                        .face(Direction.NORTH, face -> face.uvs(0, 0, 16, 16).texture(TextureSlot.SIDE).cullface(Direction.NORTH).tintindex(0))
                        .face(Direction.SOUTH, face -> face.uvs(0, 0, 16, 16).texture(TextureSlot.SIDE).cullface(Direction.SOUTH).tintindex(0))
                        .face(Direction.WEST, face -> face.uvs(0, 0, 16, 16).texture(TextureSlot.SIDE).cullface(Direction.WEST).tintindex(0))
                        .face(Direction.EAST, face -> face.uvs(0, 0, 16, 16).texture(TextureSlot.SIDE).cullface(Direction.EAST).tintindex(0))
                )
                .build();
    }

    private static ExtendedModelTemplate tintedBottomTopTemplate() {
        return ExtendedModelTemplateBuilder.builder()
                .parent(ResourceLocation.fromNamespaceAndPath("minecraft", "block/block"))
                .requiredTextureSlot(TextureSlot.PARTICLE)
                .requiredTextureSlot(TextureSlot.SIDE)
                .requiredTextureSlot(TextureSlot.BOTTOM)
                .requiredTextureSlot(TextureSlot.TOP)
                .element(element -> element
                        .from(0, 0, 0).to(16, 16, 16)
                        .face(Direction.DOWN, face -> face.uvs(0, 0, 16, 16).texture(TextureSlot.BOTTOM).cullface(Direction.DOWN).tintindex(0))
                        .face(Direction.UP, face -> face.uvs(0, 0, 16, 16).texture(TextureSlot.TOP).cullface(Direction.UP).tintindex(0))
                        .face(Direction.NORTH, face -> face.uvs(0, 0, 16, 16).texture(TextureSlot.SIDE).cullface(Direction.NORTH).tintindex(0))
                        .face(Direction.SOUTH, face -> face.uvs(0, 0, 16, 16).texture(TextureSlot.SIDE).cullface(Direction.SOUTH).tintindex(0))
                        .face(Direction.WEST, face -> face.uvs(0, 0, 16, 16).texture(TextureSlot.SIDE).cullface(Direction.WEST).tintindex(0))
                        .face(Direction.EAST, face -> face.uvs(0, 0, 16, 16).texture(TextureSlot.SIDE).cullface(Direction.EAST).tintindex(0))
                )
                .build();
    }

    private static ResourceLocation blockModel(Block block) {
        ResourceLocation id = net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(block);
        return ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "block/" + id.getPath());
    }

    private static ResourceLocation blockTexture(Block block) {
        ResourceLocation id = net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(block);
        return ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "block/" + id.getPath());
    }
    //?}
}
