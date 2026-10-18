package space.anatomyuniverse.musavacca.client.helmet;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;

//? if >=1.21.11 {
/*import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
*///?} else if >=1.21.9 <1.21.11 {
/*import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
*///?} else {
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
//?}
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import space.anatomyuniverse.musavacca.data.models.NewModelSets;
import space.anatomyuniverse.musavacca.data.models.newgen.ArmorItems;

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

public final class CustomHelmetModels {
    private CustomHelmetModels() {
    }

    public static boolean hasCustomHeadModel(ItemStack stack) {
        return headModel(stack) != null;
    }

    //? if >=1.21.11 {
    /*public static Identifier headModel(ItemStack stack) {
    *///?} else {
    public static ResourceLocation headModel(ItemStack stack) {
    //?}
        if (stack == null || stack.isEmpty()) {
            return null;
        }

        return Index.HEAD_MODELS.get(stack.getItem());
    }

    //? if >=1.21.9 {
    /*public static void render(ItemStack stack, PoseStack pose,
                              net.minecraft.client.renderer.SubmitNodeCollector collector,
                              int light, int outlineColor) {
        if (!hasCustomHeadModel(stack)) return;
        var minecraft = Minecraft.getInstance();
        var state = new net.minecraft.client.renderer.item.ItemStackRenderState();
        minecraft.getItemModelResolver().updateForTopItem(state, stack, ItemDisplayContext.HEAD,
                minecraft.level, null, 0);
        state.submit(pose, collector, light, OverlayTexture.NO_OVERLAY, outlineColor);
    }
    *///?} else if >=1.21.4 <1.21.9 {
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

    }
    //?} else {
    /*public static void render(
            ItemStack stack,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight
    ) {
        if (!hasCustomHeadModel(stack)) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();

        BakedModel model = minecraft.getModelManager().getModel(
                ModelResourceLocation.standalone(headModel(stack))
        );

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

    }

    public static void registerAdditionalModels(
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

    //? if >=1.21.11 {
    /*private static Map<Item, Identifier> createIndex() {
        IdentityHashMap<Item, Identifier> models =
    *///?} else {
    private static Map<Item, ResourceLocation> createIndex() {
        IdentityHashMap<Item, ResourceLocation> models =
    //?}
                new IdentityHashMap<>();

        for (ArmorItems.Entry entry : NewModelSets.armorItems()) {
            if (entry == null
                    || entry.helmet() == null
                    || !entry.hasHelmetHeadModel()) {
                continue;
            }

            Item helmet = entry.helmet().asItem();

            //? if >=1.21.11 {
            /*Identifier previous = models.put(
            *///?} else {
            ResourceLocation previous = models.put(
            //?}
                    helmet,
                    entry.helmetHeadModel()
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
        //? if >=1.21.11 {
        /*private static final Map<Item, Identifier> HEAD_MODELS =
        *///?} else {
        private static final Map<Item, ResourceLocation> HEAD_MODELS =
        //?}
                createIndex();

        private Index() {
        }
    }
}
