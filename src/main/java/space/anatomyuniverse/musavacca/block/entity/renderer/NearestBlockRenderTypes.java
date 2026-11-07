package space.anatomyuniverse.musavacca.block.entity.renderer;

//? if >=1.21.11 {
/*import com.mojang.blaze3d.systems.RenderSystem;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
*///?}
//? if >=26.3 {
/*import com.mojang.renderpearl.api.textures.FilterMode;
*///?} else if >=1.21.11 <26.3 {
/*import com.mojang.blaze3d.textures.FilterMode;
*///?}
//? if >=1.21.11 <26.1 {
/*import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterRenderPipelinesEvent;
*///?}
//? if >=1.21.11 <26.1 {

/*@EventBusSubscriber(modid = "musavacca", value = Dist.CLIENT)
*///?}
public final class NearestBlockRenderTypes {
    private NearestBlockRenderTypes() {}

    //? if >=1.21.11 {
    /*private static final Map<ChunkSectionLayer, Map<Identifier, RenderType>> TYPES = new EnumMap<>(ChunkSectionLayer.class);
    static {
        for (ChunkSectionLayer layer : ChunkSectionLayer.values()) TYPES.put(layer, new HashMap<>());
    }

    //? if >=1.21.11 <26.1 {
    /^private static final RenderPipeline TRANSLUCENT_PIPELINE = RenderPipeline.builder(RenderPipelines.BLOCK_SNIPPET)
            .withLocation(Identifier.fromNamespaceAndPath("musavacca", "pipeline/nearest_translucent_block")).withBlend(BlendFunction.TRANSLUCENT)
            .withShaderDefine("ALPHA_CUTOUT", 0.01F).build();

    @SubscribeEvent
    public static void registerPipeline(RegisterRenderPipelinesEvent event) {
        event.registerPipeline(TRANSLUCENT_PIPELINE);
    }

    ^///?}
    public static RenderType forAtlas(Identifier atlas, ChunkSectionLayer layer) {
        return TYPES.get(layer).computeIfAbsent(atlas, key -> create(key, layer));
    }

    private static RenderType create(Identifier atlas, ChunkSectionLayer layer) {
        var pipeline = switch (layer) {
        case SOLID -> RenderPipelines.SOLID_BLOCK;
        case CUTOUT -> RenderPipelines.CUTOUT_BLOCK;
        //? if >=26.1 {
        /^case TRANSLUCENT -> RenderPipelines.TRANSLUCENT_BLOCK;
        ^///?} else {
        case TRANSLUCENT -> TRANSLUCENT_PIPELINE;
        case TRIPWIRE -> RenderPipelines.TRIPWIRE_BLOCK;
        //?}
        };
        var setup = RenderSetup.builder(pipeline)
                .withTexture("Sampler0", atlas, () -> RenderSystem.getSamplerCache().getClampToEdge(FilterMode.NEAREST, false))
                .useLightmap();
        boolean translucent = layer == ChunkSectionLayer.TRANSLUCENT;
        //? if >=1.21.11 <26.1 {
        /^translucent |= layer == ChunkSectionLayer.TRIPWIRE;
        ^///?}
        if (translucent) {
            setup.sortOnUpload();
            //? if >=26.3 {
            /^setup.setOitPipelines(RenderPipelines.OIT_TRANSLUCENT_BLOCK);
            ^///?}
        }
        return RenderType.create("musavacca_nearest_" + layer.label(), setup.createRenderSetup());
    }
    *///?}
}
