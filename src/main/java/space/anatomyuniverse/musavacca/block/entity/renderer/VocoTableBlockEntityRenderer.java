package space.anatomyuniverse.musavacca.block.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.phys.Vec3;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoReceptorLogic.ReceptorPosition;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoTableCandleVoxelShapes;
import space.anatomyuniverse.musavacca.block.entity.custom.VocoTableBlockEntity;
//? if >=1.21.9 {
/*import net.minecraft.client.renderer.SubmitNodeCollector;
*///?} else {
import net.minecraft.client.renderer.MultiBufferSource;
//?}
//? if >=1.21.9 {
/*import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
*///?}
//? if >=26.1 {
/*import net.minecraft.client.renderer.state.level.CameraRenderState;
*///?} else if >=1.21.9 <26.1 {
/*import net.minecraft.client.renderer.state.CameraRenderState;
*///?}
//? if >=1.21.9 {
/*import net.minecraft.client.renderer.item.ItemStackRenderState;
*///?}
//? if >=1.21.9 {
/*import java.util.Arrays;
*///?}
//? if >=1.21.9 {

/*public class VocoTableBlockEntityRenderer implements BlockEntityRenderer<VocoTableBlockEntity, VocoTableBlockEntityRenderer.State> {
*///?} else {
public class VocoTableBlockEntityRenderer implements BlockEntityRenderer<VocoTableBlockEntity> {
//?}
    //? if <1.21.9 {
    private final PearlCandleRenderer candleRenderer;
    //?}
    private final VocoTableBlockEntityItemDisplayRenderer itemRenderer;

    //? if >=1.21.11 {
    /*private final NearestBlockEntityRenderer<VocoTableBlockEntity> nearest;
    *///?}
    public VocoTableBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        //? if >=1.21.11 {
        /*this.nearest = new NearestBlockEntityRenderer<>(context, 0.5D);
        *///?}
        //? if <1.21.9 {
        this.candleRenderer = new PearlCandleRenderer(context);
        //?}
        this.itemRenderer = new VocoTableBlockEntityItemDisplayRenderer(context);
    }

    //? if >=1.21.9 {
    /*public static final class State
            //? if >=1.21.11 {
            /^extends NearestBlockEntityRenderer.State
            ^///?} else {
            extends BlockEntityRenderState
            //?}
    {
        public final ItemStackRenderState item = new ItemStackRenderState();
        public final PearlCandleRenderer.Model[] candles = new PearlCandleRenderer.Model[ReceptorPosition.COUNT];
        public float itemRotation;
    }

    *///?}
    //? if <1.21.9 {
    @Override
    public void render(
            VocoTableBlockEntity blockEntity,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            //? if >=1.21.5 <1.21.9 {
            int packedOverlay,
            Vec3 cameraPos) {
            //?} else {
            /*int packedOverlay) {
            *///?}
        //? if >=1.21.5 <1.21.9 {
        this.renderContents(blockEntity, partialTick, poseStack, bufferSource, packedLight, packedOverlay, cameraPos);
        //?} else {
        /*this.renderContents(blockEntity, partialTick, poseStack, bufferSource, packedLight, packedOverlay, Vec3.ZERO);
        *///?}
    }

    //?}
    //? if >=1.21.9 {
    /*@Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(
            VocoTableBlockEntity entity, State state, float partialTick, Vec3 camera, ModelFeatureRenderer.CrumblingOverlay overlay) {
        //? if >=1.21.11 {
        /^nearest.extractRenderState(entity, state, partialTick, camera, overlay);
        ^///?} else {
        BlockEntityRenderer.super.extractRenderState(entity, state, partialTick, camera, overlay);
        //?}
        extractCandles(entity, state);
        itemRenderer.extract(entity, state, partialTick);
    }

    private static void extractCandles(VocoTableBlockEntity entity, State state) {
        if (!(entity.getLevel() instanceof ClientLevel level)) {
            Arrays.fill(state.candles, null);
            return;
        }
        var pos = entity.getBlockPos().above();
        for (var receptor : ReceptorPosition.values()) {
            int index = receptor.id();
            var candle = entity.candle(receptor);
            if (candle == null) {
                state.candles[index] = null;
                continue;
            }
            var model = state.candles[index];
            if (model == null) state.candles[index] = model = new PearlCandleRenderer.Model();
            model.extract(level, pos, candle.state(), VocoTableCandleVoxelShapes.renderTranslation(receptor));
        }
    }

    *///?}
    //? if <1.21.9 {
    private void renderContents(
            VocoTableBlockEntity blockEntity,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            int packedOverlay,
            Vec3 cameraPos) {
        if (blockEntity.getLevel() instanceof ClientLevel level) {
            var pos = blockEntity.getBlockPos().above();
            for (var receptor : ReceptorPosition.values()) {
                var candle = blockEntity.candle(receptor);
                if (candle == null) continue;
                candleRenderer.render(level, pos, candle.state(), VocoTableCandleVoxelShapes.renderTranslation(receptor),
                        poseStack, bufferSource, packedOverlay);
            }
        }
        this.itemRenderer.render(blockEntity, partialTick, poseStack, bufferSource, packedLight, packedOverlay, cameraPos);
    }
    //?}
    //? if >=1.21.9 {
    /*@Override
    public void submit(State state, PoseStack pose, SubmitNodeCollector collector, CameraRenderState camera) {
        //? if >=1.21.11 {
        /^nearest.submit(state, pose, collector, camera);
        ^///?}
        for (var candle : state.candles) {
            if (candle != null) candle.submit(pose, collector);
        }
        VocoTableBlockEntityItemDisplayRenderer.submit(state, pose, collector);
    }

    //? if >=1.21.11 {
    /^@Override
    public net.minecraft.world.phys.AABB getRenderBoundingBox(VocoTableBlockEntity entity) {
        return nearest.getRenderBoundingBox(entity);
    }

    @Override
    public int getViewDistance() {
        return nearest.getViewDistance();
    }
    ^///?}
    *///?}
}
