package space.anatomyuniverse.musavacca.block.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
//? if >=1.21.9 {
/*import net.minecraft.client.renderer.SubmitNodeCollector;
*///?} else {
import net.minecraft.client.renderer.MultiBufferSource;
//?}
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
//? if >=26.1 {
/*import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
*///?} else if >=1.21.9 <26.1 {
/*import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
*///?}
import net.minecraft.world.phys.Vec3;
import space.anatomyuniverse.musavacca.block.entity.custom.VocoTableBlockEntity;
//? if >=1.21.9 {
/*import java.util.ArrayList;
import java.util.List;
*///?}

//? if >=1.21.9 {
/*public class VocoTableBlockEntityRenderer implements BlockEntityRenderer<VocoTableBlockEntity, VocoTableBlockEntityRenderer.State> {
    private final VocoTableBlockEntityItemDisplayRenderer itemRenderer;
    private final BlockEntityRendererProvider.Context context;
*///?} else {
public class VocoTableBlockEntityRenderer implements BlockEntityRenderer<VocoTableBlockEntity> {

    private final VocoTableBlockEntityCandleRenderer candleRenderer;
    private final VocoTableBlockEntityItemDisplayRenderer itemRenderer;
//?}

    public VocoTableBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        //? if >=1.21.9 {
        /*this.context = context;
        itemRenderer = new VocoTableBlockEntityItemDisplayRenderer(context);
        *///?} else {
        this.candleRenderer = new VocoTableBlockEntityCandleRenderer(context);
        this.itemRenderer = new VocoTableBlockEntityItemDisplayRenderer(context);
        //?}
    }

    //? if >=1.21.9 {
    /*public static final class State extends BlockEntityRenderState {
        public final ItemStackRenderState item = new ItemStackRenderState();
        public final List<VocoTableBlockEntityCandleRenderer.Candle> candles = new ArrayList<>();
        public float itemRotation;
    }

    @Override public State createRenderState() { return new State(); }

    @Override
    public void extractRenderState(VocoTableBlockEntity entity, State state, float partialTick,
                                   Vec3 camera, ModelFeatureRenderer.CrumblingOverlay overlay) {
        BlockEntityRenderer.super.extractRenderState(entity, state, partialTick, camera, overlay);
        VocoTableBlockEntityCandleRenderer.extract(entity, state, context);
        itemRenderer.extract(entity, state, partialTick);
    *///?} else if >=1.21.5 <1.21.9 {
    @Override
    public void render(
            VocoTableBlockEntity blockEntity,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            int packedOverlay,
            Vec3 cameraPos
    ) {
        this.renderContents(blockEntity, partialTick, poseStack, bufferSource, packedLight, packedOverlay, cameraPos);
    //?} else {
    /*@Override
    public void render(
            VocoTableBlockEntity blockEntity,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            int packedOverlay
    ) {
        this.renderContents(blockEntity, partialTick, poseStack, bufferSource, packedLight, packedOverlay, Vec3.ZERO);
    *///?}
    }

    //? if >=1.21.9 {
    /*@Override
    public void submit(State state, PoseStack pose, SubmitNodeCollector collector, CameraRenderState camera) {
        VocoTableBlockEntityCandleRenderer.submit(state, pose, collector);
        VocoTableBlockEntityItemDisplayRenderer.submit(state, pose, collector);
    }
    *///?} else {
    private void renderContents(
            VocoTableBlockEntity blockEntity,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            int packedOverlay,
            Vec3 cameraPos
    ) {
        this.candleRenderer.render(
                blockEntity,
                partialTick,
                poseStack,
                bufferSource,
                packedLight,
                packedOverlay,
                cameraPos
        );

        this.itemRenderer.render(
                blockEntity,
                partialTick,
                poseStack,
                bufferSource,
                packedLight,
                packedOverlay,
                cameraPos
        );
    }
    //?}
}
