package space.anatomyuniverse.musavacca.client.helmet;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import space.anatomyuniverse.musavacca.data.models.ModelSets;
import space.anatomyuniverse.musavacca.data.models.item.CustomArmorSet;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;

//? if <1.21.4 {
/*import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.neoforged.neoforge.client.event.ModelEvent;

import java.util.HashSet;
import java.util.Set;
*///?}

/**
 * Single client-side index for every armor set that declares a custom worn
 * helmet model in {@link ModelSets#customArmorSets()}.
 *
 * <p>The item -> model-id mapping is built once and reused on the render hot
 * path. Pre-1.21.4 deliberately does not cache BakedModel instances so model
 * resource reloads remain owned by Minecraft's ModelManager.</p>
 */
public final class CustomHelmetModels {
    private CustomHelmetModels() {
    }

    public static boolean hasCustomHeadModel(ItemStack stack) {
        return headModel(stack) != null;
    }

    public static ResourceLocation headModel(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return null;
        }

        return Index.HEAD_MODELS.get(stack.getItem());
    }

    /**
     * Render through Minecraft's own item renderer in HEAD context.
     *
     * <p>Before 1.21.4 we explicitly provide the standalone baked model.
     * 1.21.4+ resolves the existing client-item display-context definition,
     * which already selects the custom helmet model for HEAD.</p>
     */
    public static void render(
            ItemStack stack,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight
    ) {
        if (!hasCustomHeadModel(stack)) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();

        //? if <1.21.4 {
        /*BakedModel model = minecraft.getModelManager().getModel(
                ModelResourceLocation.standalone(headModel(stack))
        );

        // The model is registered through ModelEvent.RegisterAdditional.
        // ItemRenderer then applies the model's own HEAD display transform.
        minecraft.getItemRenderer().render(
                stack,
                ItemDisplayContext.HEAD,
                false,
                poseStack,
                bufferSource,
                packedLight,
                OverlayTexture.NO_OVERLAY,
                model
        );
        *///?} else {
        minecraft.getItemRenderer().renderStatic(
                stack,
                ItemDisplayContext.HEAD,
                packedLight,
                OverlayTexture.NO_OVERLAY,
                poseStack,
                bufferSource,
                minecraft.level,
                0
        );
        //?}
    }

    //? if <1.21.4 {
     
    /*public static void registerAdditionalModels(
            ModelEvent.RegisterAdditional event
    ) {
        Set<ModelResourceLocation> registered = new HashSet<>();

        for (ResourceLocation modelId : Index.HEAD_MODELS.values()) {
            ModelResourceLocation model =
                    ModelResourceLocation.standalone(modelId);

            if (registered.add(model)) {
                event.register(model);
            }
        }
    }
    *///?}

    private static Map<Item, ResourceLocation> createIndex() {
        IdentityHashMap<Item, ResourceLocation> models =
                new IdentityHashMap<>();

        for (CustomArmorSet.Entry entry : ModelSets.customArmorSets()) {
            if (entry == null
                    || entry.helmet() == null
                    || !entry.hasCustomHelmetHeadModel()) {
                continue;
            }

            Item helmet = entry.helmet().asItem();
            ResourceLocation previous = models.put(
                    helmet,
                    entry.customHelmetHeadModelLocation()
            );

            if (previous != null) {
                throw new IllegalStateException(
                        "Custom helmet registered more than once: " + helmet
                );
            }
        }

        return Collections.unmodifiableMap(models);
    }

    private static final class Index {
        private static final Map<Item, ResourceLocation> HEAD_MODELS =
                createIndex();

        private Index() {
        }
    }
}
