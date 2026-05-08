// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/block/entity/renderer/VocoTableBlockEntityRenderer.java
package space.anatomyuniverse.musavacca.block.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.phys.Vec3;
import space.anatomyuniverse.musavacca.block.entity.custom.VocoTableBlockEntity;

public class VocoTableBlockEntityRenderer implements BlockEntityRenderer<VocoTableBlockEntity> {

    private final VocoTableBlockEntityCandleRenderer candleRenderer;
    private final VocoTableBlockEntityItemDisplayRenderer itemRenderer;

    public VocoTableBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.candleRenderer = new VocoTableBlockEntityCandleRenderer(context);
        this.itemRenderer = new VocoTableBlockEntityItemDisplayRenderer(context);
    }

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

}