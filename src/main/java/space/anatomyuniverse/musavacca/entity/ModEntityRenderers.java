package space.anatomyuniverse.musavacca.entity;

//? if <1.21.2 {
/*import net.minecraft.world.entity.EntityType;
*///?}
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import space.anatomyuniverse.musavacca.entity.boat.musavacca.client.MusavaccaBoatModel;
import space.anatomyuniverse.musavacca.entity.boat.musavacca.client.MusavaccaBoatRenderer;
import space.anatomyuniverse.musavacca.entity.mob.bananacow.clientmodel.BananaCowModel;
import space.anatomyuniverse.musavacca.entity.mob.bananacow.clientmodel.BananaCowRenderer;
import space.anatomyuniverse.musavacca.entity.mob.basuke.clientmodel.BasukeModel;
import space.anatomyuniverse.musavacca.entity.mob.basuke.clientmodel.BasukeRenderer;

public final class ModEntityRenderers {
    private ModEntityRenderers() {}

    public static void register(IEventBus modBus) {
        modBus.addListener(ModEntityRenderers::registerRenderers);
        modBus.addListener(ModEntityRenderers::registerLayerDefinitions);
    }

    private static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.BANANA_COW.get(), BananaCowRenderer::new);
        event.registerEntityRenderer(ModEntities.BASUKE.get(), BasukeRenderer::new);
        //? if >=1.21.2 {
        event.registerEntityRenderer(ModEntities.MUSAVACCA_BOAT.get(), MusavaccaBoatRenderer::new);
        //?} else {
        /*event.registerEntityRenderer((EntityType) ModEntities.MUSAVACCA_BOAT.get(), context -> new MusavaccaBoatRenderer(context));
        *///?}
    }

    private static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(BananaCowModel.LAYER_LOCATION, BananaCowModel::createBodyLayer);
        event.registerLayerDefinition(BasukeModel.LAYER_LOCATION, BasukeModel::createBodyLayer);
        event.registerLayerDefinition(MusavaccaBoatModel.LAYER, MusavaccaBoatModel::createBodyLayer);
    }
}
