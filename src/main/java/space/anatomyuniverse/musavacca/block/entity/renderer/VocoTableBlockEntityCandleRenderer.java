package space.anatomyuniverse.musavacca.block.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
//? if <1.21.5
//import net.neoforged.neoforge.client.model.data.ModelData;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoReceptorLogic.ReceptorPosition;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoTableCandleVoxelShapes;
import space.anatomyuniverse.musavacca.block.entity.custom.VocoTableBlockEntity;

public final class VocoTableBlockEntityCandleRenderer {

    private final BlockRenderDispatcher blockRenderer;

    public VocoTableBlockEntityCandleRenderer(BlockEntityRendererProvider.Context context) {
        this.blockRenderer = context.getBlockRenderDispatcher();
    }

    public void render(
            VocoTableBlockEntity blockEntity,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            int packedOverlay,
            Vec3 cameraPos
    ) {
        BlockAndTintGetter level = blockEntity.getLevel();
        if (level == null) {
            return;
        }

        BlockPos blockPos = blockEntity.getBlockPos();

        for (ReceptorPosition receptor : ReceptorPosition.values()) {
            renderCorner(
                    blockEntity,
                    receptor,
                    poseStack,
                    bufferSource,
                    packedLight,
                    packedOverlay,
                    level,
                    blockPos
            );
        }
    }

    private void renderCorner(
            VocoTableBlockEntity blockEntity,
            ReceptorPosition receptor,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            int packedOverlay,
            BlockAndTintGetter level,
            BlockPos blockPos
    ) {
        Block candleBlock = blockEntity.getCandleBlock(receptor);
        if (!(candleBlock instanceof CandleBlock)) {
            return;
        }

        int candleCount = blockEntity.getCandleCount(receptor);
        if (candleCount <= 0) {
            return;
        }

        BlockState candleState = candleBlock.defaultBlockState()
                .setValue(CandleBlock.CANDLES, Math.max(1, Math.min(4, candleCount)))
                .setValue(CandleBlock.LIT, blockEntity.isCandleLit(receptor))
                .setValue(CandleBlock.WATERLOGGED, false);

        Vec3 translation = VocoTableCandleVoxelShapes.renderTranslation(receptor);

        poseStack.pushPose();
        poseStack.translate(translation.x, translation.y, translation.z);

        //? if >=1.21.5 {
        this.blockRenderer.renderSingleBlock(
                candleState,
                poseStack,
                bufferSource,
                packedLight,
                packedOverlay,
                level,
                blockPos
        );
        //?} else {
        /*this.blockRenderer.renderSingleBlock(
                candleState,
                poseStack,
                bufferSource,
                packedLight,
                packedOverlay,
                ModelData.EMPTY,
                null
        );
        *///?}

        poseStack.popPose();
    }
}
