package space.anatomyuniverse.musavacca.entity;

import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import space.anatomyuniverse.musavacca.entity.boat.musavacca.client.MusavaccaBoatModel;
import space.anatomyuniverse.musavacca.entity.boat.musavacca.client.MusavaccaBoatRenderer;
import space.anatomyuniverse.musavacca.entity.mob.bananacow.clientmodel.BananaCowModel;
import space.anatomyuniverse.musavacca.entity.mob.bananacow.clientmodel.BananaCowRenderer;
import space.anatomyuniverse.musavacca.entity.mob.basuke.clientmodel.BasukeModel;
import space.anatomyuniverse.musavacca.entity.mob.basuke.clientmodel.BasukeRenderer;

public final class ModEntityRenderers {

    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.BANANA_COW.get(), BananaCowRenderer::new);
        event.registerEntityRenderer(ModEntities.BASUKE.get(), BasukeRenderer::new);
        event.registerEntityRenderer(ModEntities.MUSAVACCA_BOAT.get(), MusavaccaBoatRenderer::new);
    }

    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(BananaCowModel.LAYER_LOCATION, BananaCowModel::createBodyLayer);
        event.registerLayerDefinition(BasukeModel.LAYER_LOCATION, BasukeModel::createBodyLayer);
        event.registerLayerDefinition(MusavaccaBoatModel.LAYER, MusavaccaBoatModel::createBodyLayer);
    }

    private ModEntityRenderers() {}
}