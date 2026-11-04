package space.anatomyuniverse.musavacca.entity.boat.musavacca.client;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
//? if >=1.21.11 {
/*import net.minecraft.resources.Identifier;
*///?} else {
import net.minecraft.resources.ResourceLocation;
//?}
import space.anatomyuniverse.musavacca.MusaCore;
//? if >=1.21.2 {
import net.minecraft.client.model.EntityModel;
//?}
//? if >=1.21.11 {
/*import net.minecraft.client.renderer.rendertype.RenderType;
*///?} else if >=1.21.2 <1.21.11 {
import net.minecraft.client.renderer.RenderType;
//?} else {
/*import com.mojang.datafixers.util.Pair;
import net.minecraft.client.model.ListModel;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.world.entity.vehicle.Boat;
*///?}
//? if >=1.21.2 {
import net.minecraft.client.renderer.entity.AbstractBoatRenderer;
import net.minecraft.client.renderer.entity.state.BoatRenderState;
//?}

public final class MusavaccaBoatRenderer extends
        //? if >=1.21.2 {
        AbstractBoatRenderer
        //?} else {
        /*BoatRenderer
        *///?}
{
    //? if >=1.21.11 {
    /*private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(MusaCore.MOD_ID, "textures/entity/boat/musavacca.png");
    *///?} else {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(MusaCore.MOD_ID, "textures/entity/boat/musavacca.png");
    //?}
    private final MusavaccaBoatModel model;
    public MusavaccaBoatRenderer(EntityRendererProvider.Context context) {
        //? if >=26.1 {
        /*super(context, TEXTURE);
        *///?} else if >=1.21.2 <26.1 {
        super(context);
        //?} else {
        /*super(context, false);
        *///?}
        this.model = new MusavaccaBoatModel(context.bakeLayer(MusavaccaBoatModel.LAYER));
        this.shadowRadius = 0.8F;
    }

    //? if >=1.21.2 {
    @Override
    protected EntityModel<BoatRenderState> model() {
        return this.model;
    }

    //? if >=1.21.2 <26.1 {
    @Override
    protected RenderType renderType() {
        //? if >=1.21.11 <26.1 {
        /*return net.minecraft.client.renderer.rendertype.RenderTypes.entityCutoutNoCull(TEXTURE);
        *///?} else {
        return RenderType.entityCutoutNoCull(TEXTURE);
        //?}
    }
    //?}
    //?}
    //? if <1.21.2 {
    /*@Override
    public Pair<ResourceLocation, ListModel<Boat>>
    getModelWithLocation(Boat boat) {
        return Pair.of(TEXTURE, this.model);
    }
    *///?}
}
