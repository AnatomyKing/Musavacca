package space.anatomyuniverse.musavacca.block.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
//? if >=1.21.9 {
/*import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.world.level.material.FluidState;
*///?} else {
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.util.RandomSource;
import net.neoforged.neoforge.client.RenderTypeHelper;
//?}
//? if <1.21.5 {
/*import net.neoforged.neoforge.client.model.data.ModelData;
*///?} else if >=1.21.5 <1.21.9 {
import net.minecraft.client.renderer.block.model.BlockModelPart;
import java.util.ArrayList;
import java.util.List;
//?}

public final class PearlCandleRenderer {
    //? if >=1.21.9 {
    /*private PearlCandleRenderer() {}

    public static final class Model extends MovingBlockRenderState {
        private final BlockState[] neighbors = new BlockState[27];
        private Vec3 translation = Vec3.ZERO;
        private int minY;
        private int height;

        public void extract(ClientLevel level, BlockPos pos, BlockState original, Vec3 translation) {
            this.translation = translation;
            this.blockPos = pos;
            this.randomSeedPos = pos;
            this.blockState = original;
            this.biome = level.getBiome(pos);
            this.minY = level.getMinY();
            this.height = level.getHeight();
            //? if >=26.1 {
            /^this.cardinalLighting = level.cardinalLighting();
            this.lightEngine = level.getLightEngine();
            ^///?} else {
            this.level = level;
            //?}
            BlockPos.MutableBlockPos sample = new BlockPos.MutableBlockPos();
            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = -1; z <= 1; z++) {
                        neighbors[index(x, y, z)] = x == 0 && y == 0 && z == 0
                                ? original : level.getBlockState(sample.setWithOffset(pos, x, y, z));
                    }
                }
            }
        }

        public void clear() {
            this.blockState = null;
        }

        public void submit(PoseStack pose, SubmitNodeCollector collector) {
            if (blockState == null) return;
            pose.pushPose();
            try {
                pose.translate(translation.x, translation.y, translation.z);
                //? if >=26.2 {
                /^collector.submitMovingBlock(pose, this, 0);
                ^///?} else {
                collector.submitMovingBlock(pose, this);
                //?}
            } finally {
                pose.popPose();
            }
        }

        private static int index(int x, int y, int z) {
            return (x + 1) * 9 + (y + 1) * 3 + z + 1;
        }

        @Override
        public BlockState getBlockState(BlockPos pos) {
            int x = pos.getX() - blockPos.getX();
            int y = pos.getY() - blockPos.getY();
            int z = pos.getZ() - blockPos.getZ();
            if (Math.abs(x) > 1 || Math.abs(y) > 1 || Math.abs(z) > 1) return super.getBlockState(pos);
            return neighbors[index(x, y, z)];
        }

        @Override
        public FluidState getFluidState(BlockPos pos) {
            return getBlockState(pos).getFluidState();
        }

        @Override
        public int getMinY() {
            return minY;
        }

        @Override
        public int getHeight() {
            return height;
        }
    }

    *///?} else {
    private final BlockRenderDispatcher dispatcher;
    private final RandomSource random = RandomSource.create();
    //? if >=1.21.5 {
    private final List<BlockModelPart> parts = new ArrayList<>();
    //?}

    public PearlCandleRenderer(BlockEntityRendererProvider.Context context) {
        this.dispatcher = context.getBlockRenderDispatcher();
    }

    public void render(ClientLevel level, BlockPos pos, BlockState original, Vec3 translation, PoseStack pose, MultiBufferSource buffers, int overlay) {
        long seed = original.getSeed(pos);
        var model = dispatcher.getBlockModel(original);
        random.setSeed(seed);
        boolean checkSides = translation.equals(Vec3.ZERO);
        //? if >=1.21.5 {
        parts.clear();
        model.collectParts(level, pos, original, random, parts);
        pose.pushPose();
        try {
            pose.translate(translation.x, translation.y, translation.z);
            dispatcher.getModelRenderer().tesselateBlock(
                    level, parts, original, pos, pose,
                    layer -> buffers.getBuffer(RenderTypeHelper.getMovingBlockRenderType(layer)), checkSides, overlay);
        } finally {
            pose.popPose();
            parts.clear();
        }
        //?} else {
        /*ModelData data = model.getModelData(level, pos, original, ModelData.EMPTY);
        for (var layer : model.getRenderTypes(original, random, data)) {
            pose.pushPose();
            try {
                pose.translate(translation.x, translation.y, translation.z);
                dispatcher.getModelRenderer().tesselateBlock(
                        level, model, original, pos, pose,
                        buffers.getBuffer(RenderTypeHelper.getMovingBlockRenderType(layer)),
                        checkSides, random, seed, overlay, data, layer);
            } finally {
                pose.popPose();
            }
        }
        *///?}
    }
    //?}
}
