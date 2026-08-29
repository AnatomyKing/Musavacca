package space.anatomyuniverse.musavacca.entity;

//? if <1.21.2 {
/*import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
import space.anatomyuniverse.musavacca.MusaCore;
*///?}
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
//? if >=1.21.2 {
import space.anatomyuniverse.musavacca.entity.boat.musavacca.client.MusavaccaBoatModel;
import space.anatomyuniverse.musavacca.entity.boat.musavacca.client.MusavaccaBoatRenderer;
//?}
import space.anatomyuniverse.musavacca.entity.mob.bananacow.clientmodel.BananaCowModel;
import space.anatomyuniverse.musavacca.entity.mob.bananacow.clientmodel.BananaCowRenderer;
import space.anatomyuniverse.musavacca.entity.mob.basuke.clientmodel.BasukeModel;
import space.anatomyuniverse.musavacca.entity.mob.basuke.clientmodel.BasukeRenderer;

public final class ModEntityRenderers {

    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.BANANA_COW.get(), BananaCowRenderer::new);
        event.registerEntityRenderer(ModEntities.BASUKE.get(), BasukeRenderer::new);
        //? if <1.21.2 {
        /*event.registerEntityRenderer(
                (EntityType) ModEntities.MUSAVACCA_BOAT.get(),
                context -> new BoatRenderer(context, false) {
                    @Override
                    public ResourceLocation getTextureLocation(Boat boat) {
                        return ResourceLocation.fromNamespaceAndPath(
                                MusaCore.MOD_ID,
                                "textures/entity/boat/musavacca.png"
                        );
                    }
                }
        );
        *///?} else {
        event.registerEntityRenderer(ModEntities.MUSAVACCA_BOAT.get(), MusavaccaBoatRenderer::new);
        //?}
    }

    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(BananaCowModel.LAYER_LOCATION, BananaCowModel::createBodyLayer);
        event.registerLayerDefinition(BasukeModel.LAYER_LOCATION, BasukeModel::createBodyLayer);
        //? if >=1.21.2
        event.registerLayerDefinition(MusavaccaBoatModel.LAYER, MusavaccaBoatModel::createBodyLayer);
    }

    private ModEntityRenderers() {}
}
