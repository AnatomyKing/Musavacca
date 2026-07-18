package space.anatomyuniverse.musavacca.client.renderer.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import space.anatomyuniverse.musavacca.item.CustomHelmetArmorTrims;

public final class CustomHelmetTrimLayer<
        S extends HumanoidRenderState,
        M extends HumanoidModel<S>,
        A extends HumanoidModel<S>
        > extends RenderLayer<S, M> {

    private final A helmetModel;
    private final EquipmentLayerRenderer equipmentRenderer;
    private final ItemStack trimCarrier = new ItemStack(Items.STICK);

    public CustomHelmetTrimLayer(
            RenderLayerParent<S, M> renderer,
            A helmetModel,
            EquipmentLayerRenderer equipmentRenderer
    ) {
        super(renderer);
        this.helmetModel = helmetModel;
        this.equipmentRenderer = equipmentRenderer;
    }

    @Override
    public void render(
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            S renderState,
            float yRot,
            float xRot
    ) {
        ItemStack helmet = renderState.headEquipment;

        if (
                helmet.isEmpty()
                        || !helmet.is(
                        CustomHelmetArmorTrims.CUSTOM_HEAD_HELMETS
                )
        ) {
            return;
        }

        ArmorTrim trim = helmet.get(DataComponents.TRIM);

        if (trim == null) {
            return;
        }

        this.getParentModel().copyPropertiesTo(
                this.helmetModel
        );

        this.helmetModel.setAllVisible(false);
        this.helmetModel.head.visible = true;
        this.helmetModel.hat.visible = true;

        this.trimCarrier.set(
                DataComponents.TRIM,
                trim
        );

        this.equipmentRenderer.renderLayers(
                EquipmentClientInfo.LayerType.HUMANOID,
                CustomHelmetArmorTrims.TRIM_CARRIER_ASSET,
                this.helmetModel,
                this.trimCarrier,
                poseStack,
                bufferSource,
                packedLight
        );
    }
}