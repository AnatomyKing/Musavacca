package space.anatomyuniverse.musavacca.block.entity;

import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import space.anatomyuniverse.musavacca.block.entity.renderer.PearlCandleBlockEntityRenderer;
import space.anatomyuniverse.musavacca.block.entity.renderer.VocoTableBlockEntityRenderer;
//? if >=1.21.11 {
/*import space.anatomyuniverse.musavacca.block.entity.renderer.NearestBlockEntityRenderer;
*///?}

public final class ModBlockEntityRenderers {
    private ModBlockEntityRenderers() {}
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.VOCO_TABLE_BLOCK_ENTITY.get(), VocoTableBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.PEARL_CANDLE_BLOCK_ENTITY.get(), PearlCandleBlockEntityRenderer::new);
        //? if >=1.21.11 {
        /*event.registerBlockEntityRenderer(ModBlockEntities.PEARL_PORTAL_BLOCK_ENTITY.get(), NearestBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.PEARL_FIRE_BLOCK_ENTITY.get(), context -> new NearestBlockEntityRenderer<>(context, 0.5D));
        event.registerBlockEntityRenderer(ModBlockEntities.VOCO_POST_BLOCK_ENTITY.get(), context -> new NearestBlockEntityRenderer<>(context, 0.5D));
        event.registerBlockEntityRenderer(ModBlockEntities.MUSAVACCA_DOOR_BLOCK_ENTITY.get(), NearestBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.MUSAVACCA_TRAPDOOR_BLOCK_ENTITY.get(), NearestBlockEntityRenderer::new);
        *///?}
    }
}
