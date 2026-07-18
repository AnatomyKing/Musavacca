package space.anatomyuniverse.musavacca.client;

import net.minecraft.client.model.ArmorStandArmorModel;
import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.ArmorStandRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import space.anatomyuniverse.musavacca.client.renderer.layer.CustomHelmetTrimLayer;

public final class ModClientRenderLayers {

    public static void addLayers(
            EntityRenderersEvent.AddLayers event
    ) {
        for (PlayerSkin.Model skin : event.getSkins()) {
            PlayerRenderer renderer = event.getSkin(skin);

            if (renderer == null) {
                continue;
            }

            ModelLayerLocation outerArmorLayer =
                    skin == PlayerSkin.Model.SLIM
                            ? ModelLayers.PLAYER_SLIM_OUTER_ARMOR
                            : ModelLayers.PLAYER_OUTER_ARMOR;

            renderer.addLayer(
                    new CustomHelmetTrimLayer<>(
                            renderer,
                            new HumanoidArmorModel<>(
                                    event.getEntityModels().bakeLayer(
                                            outerArmorLayer
                                    )
                            ),
                            event.getContext().getEquipmentRenderer()
                    )
            );
        }

        ArmorStandRenderer armorStandRenderer =
                event.getRenderer(EntityType.ARMOR_STAND);

        if (armorStandRenderer != null) {
            armorStandRenderer.addLayer(
                    new CustomHelmetTrimLayer<>(
                            armorStandRenderer,
                            new ArmorStandArmorModel(
                                    event.getEntityModels().bakeLayer(
                                            ModelLayers.ARMOR_STAND_OUTER_ARMOR
                                    )
                            ),
                            event.getContext().getEquipmentRenderer()
                    )
            );
        }
    }

    private ModClientRenderLayers() {
    }
}