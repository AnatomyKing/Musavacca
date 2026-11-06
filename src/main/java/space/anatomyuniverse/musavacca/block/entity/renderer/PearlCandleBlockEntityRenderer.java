package space.anatomyuniverse.musavacca.block.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import space.anatomyuniverse.musavacca.block.entity.custom.PearlCandleBlockEntity;
//? if >=1.21.9 {
/*import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
*///?} else {
import net.minecraft.client.renderer.MultiBufferSource;
//?}
//? if >=26.1 {
/*import net.minecraft.client.renderer.state.level.CameraRenderState;
*///?} else if >=1.21.9 {
/*import net.minecraft.client.renderer.state.CameraRenderState;
*///?}

public final class PearlCandleBlockEntityRenderer implements BlockEntityRenderer<PearlCandleBlockEntity
        //? if >=1.21.9 {
        /*, PearlCandleBlockEntityRenderer.State
        *///?}
        > {
    private final BlockEntityRendererProvider.Context context;

    public PearlCandleBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.context = context;
    }

    //? if >=1.21.9 {
    /*public static final class State extends BlockEntityRenderState {
        //? if >=26.1 {
        /^public final net.minecraft.client.renderer.block.BlockModelRenderState model = new net.minecraft.client.renderer.block.BlockModelRenderState();
        ^///?} else {
        public BlockState model;
        //?}
    }

    @Override public State createRenderState() { return new State(); }

    @Override
    public void extractRenderState(PearlCandleBlockEntity entity, State state, float partialTick, Vec3 camera, ModelFeatureRenderer.CrumblingOverlay overlay) {
        BlockEntityRenderer.super.extractRenderState(entity, state, partialTick, camera, overlay);
        //? if >=26.1 {
        /^context.blockModelResolver().update(state.model, entity.originalState(), net.minecraft.client.renderer.block.model.BlockDisplayContext.create());
        ^///?} else {
        state.model = entity.originalState();
        //?}
    }

    @Override
    public void submit(State state, PoseStack pose, SubmitNodeCollector collector, CameraRenderState camera) {
        //? if >=26.1 {
        /^state.model.submit(pose, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
        ^///?} else {
        collector.submitBlock(pose, state.model, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
        //?}
    }
    *///?} else {
    @Override
    public void render(PearlCandleBlockEntity entity, float partialTick, PoseStack pose, MultiBufferSource buffers, int light,
            //? if >=1.21.5 {
            int overlay, Vec3 camera) {
            //?} else {
            /*int overlay) {
            *///?}
        if (entity.getLevel() == null) return;
        //? if >=1.21.5 {
        context.getBlockRenderDispatcher().renderSingleBlock(entity.originalState(), pose, buffers, light, overlay, entity.getLevel(), entity.getBlockPos());
        //?} else {
        /*context.getBlockRenderDispatcher().renderSingleBlock(entity.originalState(), pose, buffers, light, overlay, net.neoforged.neoforge.client.model.data.ModelData.EMPTY, null);
        *///?}
    }
    //?}
}
