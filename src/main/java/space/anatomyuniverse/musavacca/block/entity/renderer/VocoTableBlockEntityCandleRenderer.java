package space.anatomyuniverse.musavacca.block.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
//? if >=26.1 {
/*import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
*///?}
//? if >=1.21.9 {
/*import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.texture.OverlayTexture;
*///?} else {
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
//?}
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
//? if <1.21.5 {
/*import net.neoforged.neoforge.client.model.data.ModelData;
*///?}
import space.anatomyuniverse.musavacca.block.custom.logic.VocoReceptorLogic.ReceptorPosition;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoTableCandleVoxelShapes;
import space.anatomyuniverse.musavacca.block.entity.custom.VocoTableBlockEntity;

public final class VocoTableBlockEntityCandleRenderer {
    //? if >=1.21.9 {
    /*//? if >=26.1 {
    /^public record Candle(BlockModelRenderState block, Vec3 translation) {}

    ^///?} else {
    public record Candle(BlockState block, Vec3 translation) {}

    //?}
    *///?}
    //? if <1.21.9 {
    private final BlockRenderDispatcher blockRenderer;

    public VocoTableBlockEntityCandleRenderer(BlockEntityRendererProvider.Context context) {
        this.blockRenderer = context.getBlockRenderDispatcher();
    }

    public void render(VocoTableBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight,
            int packedOverlay, Vec3 cameraPos) {
        BlockAndTintGetter level = blockEntity.getLevel();
        if (level == null) return;
        BlockPos blockPos = blockEntity.getBlockPos();
        for (ReceptorPosition receptor : ReceptorPosition.values()) {
            renderCorner(blockEntity, receptor, poseStack, bufferSource, packedLight, packedOverlay, level, blockPos);
        }
    }

    private void renderCorner(VocoTableBlockEntity blockEntity, ReceptorPosition receptor, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight,
            int packedOverlay, BlockAndTintGetter level, BlockPos blockPos) {
        var candle = blockEntity.candle(receptor);
        if (candle == null) return;
        BlockState candleState = candle.state();
        Vec3 translation = VocoTableCandleVoxelShapes.renderTranslation(receptor);
        poseStack.pushPose();
        poseStack.translate(translation.x, translation.y, translation.z);
        //? if >=1.21.5 <1.21.9 {
        this.blockRenderer.renderSingleBlock(candleState, poseStack, bufferSource, packedLight, packedOverlay, level, blockPos.above());
        //?} else {
        /*this.blockRenderer.renderSingleBlock(candleState, poseStack, bufferSource, packedLight, packedOverlay, ModelData.EMPTY, null);
        *///?}
        poseStack.popPose();
    }
    //?}
    //? if >=1.21.9 {
    /*public static void extract(VocoTableBlockEntity entity, VocoTableBlockEntityRenderer.State state, BlockEntityRendererProvider.Context context) {
        state.candles.clear();
        if (entity.getLevel() == null) return;
        for (ReceptorPosition receptor : ReceptorPosition.values()) {
            var slot = entity.candle(receptor);
            if (slot == null) continue;
            //? if >=26.1 {
            /^var model = new BlockModelRenderState();
            context.blockModelResolver().update(model, slot.state(), BlockDisplayContext.create());
            ^///?} else {
            var model = slot.state();
            //?}
            state.candles.add(new Candle(model, VocoTableCandleVoxelShapes.renderTranslation(receptor)));
        }
    }

    public static void submit(VocoTableBlockEntityRenderer.State state, PoseStack pose, SubmitNodeCollector collector) {
        for (Candle candle : state.candles) {
            pose.pushPose();
            pose.translate(candle.translation.x, candle.translation.y, candle.translation.z);
            //? if >=26.1 {
            /^candle.block.submit(pose, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
            ^///?} else {
            collector.submitBlock(pose, candle.block, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
            //?}
            pose.popPose();
        }
    }
    *///?}
}
