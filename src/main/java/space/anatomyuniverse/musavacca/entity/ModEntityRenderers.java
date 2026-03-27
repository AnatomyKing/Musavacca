
package space.anatomyuniverse.musavacca.entity;

import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import space.anatomyuniverse.musavacca.entity.ModEntities;
import space.anatomyuniverse.musavacca.entity.mob.bananacow.clientmodel.BananaCowModel;
import space.anatomyuniverse.musavacca.entity.mob.bananacow.clientmodel.BananaCowRenderer;

public final class ModEntityRenderers {

    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.BANANA_COW.get(), BananaCowRenderer::new);
    }

    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(BananaCowModel.LAYER_LOCATION, BananaCowModel::createBodyLayer);
    }

    private ModEntityRenderers() {}
}