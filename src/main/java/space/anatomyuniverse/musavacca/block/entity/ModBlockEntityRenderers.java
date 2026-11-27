package space.anatomyuniverse.musavacca.block.entity;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import space.anatomyuniverse.musavacca.block.entity.renderer.LayeredBlockEntityRenderer;
import space.anatomyuniverse.musavacca.block.entity.renderer.LayeredBlockTint;

public final class ModBlockEntityRenderers {
    private ModBlockEntityRenderers() {}

    public static void register(IEventBus modBus) {
        LayeredBlockTint.registerSpriteLayers();
        modBus.addListener(ModBlockEntityRenderers::registerRenderers);
    }

    private static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.VOCO_TABLE_BLOCK_ENTITY.get(), LayeredBlockEntityRenderer::forTable);
        event.registerBlockEntityRenderer(ModBlockEntities.VOCO_POST_BLOCK_ENTITY.get(),
                context -> new LayeredBlockEntityRenderer<>(context, true, 0.0D));
        event.registerBlockEntityRenderer(ModBlockEntities.PEARL_PORTAL_BLOCK_ENTITY.get(),
                context -> new LayeredBlockEntityRenderer<>(context, true, 0.0D));
        event.registerBlockEntityRenderer(ModBlockEntities.PEARL_FIRE_BLOCK_ENTITY.get(),
                context -> new LayeredBlockEntityRenderer<>(context, true, 0.5D));
        event.registerBlockEntityRenderer(ModBlockEntities.PEARL_CANDLE_BLOCK_ENTITY.get(), LayeredBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.PEARL_CAMPFIRE_BLOCK_ENTITY.get(), LayeredBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.MUSAVACCA_DOOR_BLOCK_ENTITY.get(), LayeredBlockEntityRenderer::forDoor);
        event.registerBlockEntityRenderer(ModBlockEntities.MUSAVACCA_TRAPDOOR_BLOCK_ENTITY.get(),
                context -> new LayeredBlockEntityRenderer<>(context, true, 0.0625D));
    }
}
