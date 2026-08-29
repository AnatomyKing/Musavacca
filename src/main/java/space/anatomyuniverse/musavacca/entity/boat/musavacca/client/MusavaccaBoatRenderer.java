package space.anatomyuniverse.musavacca.entity.boat.musavacca.client;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import space.anatomyuniverse.musavacca.MusaCore;

//? if <1.21.2 {
/*import com.mojang.datafixers.util.Pair;
import net.minecraft.client.model.ListModel;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.world.entity.vehicle.Boat;
*///?} else {
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.AbstractBoatRenderer;
import net.minecraft.client.renderer.entity.state.BoatRenderState;
//?}

public final class MusavaccaBoatRenderer extends
        //? if <1.21.2 {
        /*BoatRenderer
        *///?} else {
        AbstractBoatRenderer
         //?}
{

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(
                    MusaCore.MOD_ID,
                    "textures/entity/boat/musavacca.png"
            );

    private final MusavaccaBoatModel model;

    public MusavaccaBoatRenderer(
            EntityRendererProvider.Context context
    ) {
        //? if <1.21.2 {
        /*super(context, false);
        *///?} else {
        super(context);
         //?}

        this.model =
                new MusavaccaBoatModel(
                        context.bakeLayer(
                                MusavaccaBoatModel.LAYER
                        )
                );

        this.shadowRadius =
                0.8F;
    }

    //? if <1.21.2 {
    /*@Override
    public Pair<ResourceLocation, ListModel<Boat>>
    getModelWithLocation(
            Boat boat
    ) {
        return Pair.of(
                TEXTURE,
                this.model
        );
    }
    *///?} else {
    @Override
    protected EntityModel<BoatRenderState> model() {
        return this.model;
    }

    @Override
    protected RenderType renderType() {
        return RenderType.entityCutoutNoCull(
                TEXTURE
        );
    }
    //?}
}