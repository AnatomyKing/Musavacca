package space.anatomyuniverse.musavacca.block.entity;

import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import space.anatomyuniverse.musavacca.block.entity.renderer.VocoTableBlockEntityRenderer;

public final class ModBlockEntityRenderers {
    private ModBlockEntityRenderers() {}

    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(
                ModBlockEntities.VOCO_TABLE_BLOCK_ENTITY.get(),
                VocoTableBlockEntityRenderer::new
        );
    }
}

