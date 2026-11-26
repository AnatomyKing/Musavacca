package space.anatomyuniverse.musavacca.block.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.CampfireRenderer;
import net.minecraft.world.phys.Vec3;
import space.anatomyuniverse.musavacca.block.entity.custom.PearlCandleBlockEntity;
//? if >=1.21.9 {
/*import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.blockentity.state.CampfireRenderState;
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
    private final CampfireRenderer campfireRenderer;
    //? if <1.21.9 {
    private final PearlCandleRenderer candleRenderer;
    //?}

    public PearlCandleBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.campfireRenderer = new CampfireRenderer(context);
        //? if <1.21.9 {
        this.candleRenderer = new PearlCandleRenderer(context);
        //?}
    }

    //? if >=1.21.9 {
    /*public static final class State extends BlockEntityRenderState {
        private final PearlCandleRenderer.Model model = new PearlCandleRenderer.Model();
        public CampfireRenderState campfire;
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(
            PearlCandleBlockEntity entity, State state, float partialTick, Vec3 camera, ModelFeatureRenderer.CrumblingOverlay overlay) {
        BlockEntityRenderer.super.extractRenderState(entity, state, partialTick, camera, overlay);
        state.model.clear();
        if (!(entity.getLevel() instanceof ClientLevel level)) {
            state.campfire = null;
            return;
        }
        state.model.extract(level, entity.getBlockPos(), entity.originalState(), Vec3.ZERO);
        var campfire = entity.campfire();
        if (campfire == null) state.campfire = null;
        else {
            if (state.campfire == null) state.campfire = campfireRenderer.createRenderState();
            campfireRenderer.extractRenderState(campfire, state.campfire, partialTick, camera, overlay);
        }
    }

    @Override
    public void submit(State state, PoseStack pose, SubmitNodeCollector collector, CameraRenderState camera) {
        state.model.submit(pose, collector);
        if (state.campfire != null) campfireRenderer.submit(state.campfire, pose, collector, camera);
    }
    *///?} else {
    @Override
    public void render(
            PearlCandleBlockEntity entity,
            float partialTick,
            PoseStack pose,
            MultiBufferSource buffers,
            int light,
            //? if >=1.21.5 {
            int overlay,
            Vec3 camera) {
            //?} else {
            /*int overlay) {
            *///?}
        if (!(entity.getLevel() instanceof ClientLevel level)) return;
        candleRenderer.render(level, entity.getBlockPos(), entity.originalState(), Vec3.ZERO, pose, buffers, overlay);
        var campfire = entity.campfire();
        if (campfire != null) {
            //? if >=1.21.5 {
            campfireRenderer.render(campfire, partialTick, pose, buffers, light, overlay, camera);
            //?} else {
            /*campfireRenderer.render(campfire, partialTick, pose, buffers, light, overlay);
            *///?}
        }
    }
    //?}
}
