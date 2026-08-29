package space.anatomyuniverse.musavacca.entity.boat.musavacca.client;

//? if >=1.21.2 {
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.AbstractBoatRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.BoatRenderState;
import net.minecraft.resources.ResourceLocation;
import space.anatomyuniverse.musavacca.MusaCore;

public final class MusavaccaBoatRenderer
        extends AbstractBoatRenderer {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(
                    MusaCore.MOD_ID,
                    "textures/entity/boat/musavacca.png"
            );

    private final MusavaccaBoatModel model;

    public MusavaccaBoatRenderer(
            EntityRendererProvider.Context context
    ) {
        super(context);

        this.model = new MusavaccaBoatModel(
                context.bakeLayer(
                        MusavaccaBoatModel.LAYER
                )
        );

        this.shadowRadius = 0.8F;
    }

    @Override
    protected EntityModel<BoatRenderState> model() {
        return this.model;
    }

    @Override
    protected RenderType renderType() {
        return RenderType.entityCutoutNoCull(TEXTURE);
    }
}
//?}
