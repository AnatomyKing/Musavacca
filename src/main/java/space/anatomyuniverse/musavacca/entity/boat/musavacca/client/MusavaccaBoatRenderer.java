package space.anatomyuniverse.musavacca.entity.boat.musavacca.client;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
//? if >=1.21.11 {
/*import net.minecraft.resources.Identifier;
*///?} else {
import net.minecraft.resources.ResourceLocation;
//?}
import space.anatomyuniverse.musavacca.MusaCore;

//? if >=1.21.11 {
/*import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.entity.AbstractBoatRenderer;
import net.minecraft.client.renderer.entity.state.BoatRenderState;
*///?} else if >=1.21.2 <1.21.11 {
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.AbstractBoatRenderer;
import net.minecraft.client.renderer.entity.state.BoatRenderState;
//?} else {
/*import com.mojang.datafixers.util.Pair;
import net.minecraft.client.model.ListModel;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.world.entity.vehicle.Boat;
*///?}

public final class MusavaccaBoatRenderer extends

        //? if >=1.21.2 {
        AbstractBoatRenderer
        //?} else {
        /*BoatRenderer
        *///?}

{

    //? if >=1.21.11 {
    /*private static final Identifier TEXTURE =
            Identifier.fromNamespaceAndPath(MusaCore.MOD_ID, "textures/entity/boat/musavacca.png");
    *///?} else {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(MusaCore.MOD_ID, "textures/entity/boat/musavacca.png");
    //?}

    private final MusavaccaBoatModel model;

    public MusavaccaBoatRenderer(
            EntityRendererProvider.Context context
    ) {

        //? if >=26.1 {
        /*super(context

                , TEXTURE
        );
        *///?} else if >=1.21.2 <26.1 {
        super(context

        );
        //?} else {
        /*super(context, false);
        *///?}

        this.model =
                new MusavaccaBoatModel(
                        context.bakeLayer(
                                MusavaccaBoatModel.LAYER
                        )
                );

        this.shadowRadius =
                0.8F;
    }

    //? if >=26.1 {
    /*@Override
    protected EntityModel<BoatRenderState> model() {
        return this.model;
    }
    *///?} else if >=1.21.11 <26.1 {
    /*@Override
    protected EntityModel<BoatRenderState> model() {
        return this.model;
    }

    @Override
    protected RenderType renderType() {
        return

                net.minecraft.client.renderer.rendertype.RenderTypes.entityCutoutNoCull(

                TEXTURE
        );
    }
    *///?} else if >=1.21.2 <1.21.11 {
    @Override
    protected EntityModel<BoatRenderState> model() {
        return this.model;
    }

    @Override
    protected RenderType renderType() {
        return

                RenderType.entityCutoutNoCull(

                TEXTURE
        );
    }

    //?} else {
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
    *///?}
}
