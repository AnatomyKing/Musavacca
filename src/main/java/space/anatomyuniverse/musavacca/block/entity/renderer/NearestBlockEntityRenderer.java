package space.anatomyuniverse.musavacca.block.entity.renderer;

//? if >=1.21.11 {
/*import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
*///?}
//? if >=26.1 {
/*import com.mojang.blaze3d.vertex.QuadInstance;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.resources.model.geometry.BakedQuad;
*///?} else if >=1.21.11 <26.1 {
/*import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.state.CameraRenderState;
*///?}
//? if >=26.1 {
/*import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
*///?}
//? if >=26.2 {
/*import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
*///?} else if >=1.21.11 <26.1 {
/*import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.texture.OverlayTexture;
*///?}
//? if >=1.21.11 <26.1 || >=26.2 {
/*import net.minecraft.util.RandomSource;
*///?}

public final class NearestBlockEntityRenderer
        //? if >=1.21.11 {
        /*<T extends BlockEntity> implements BlockEntityRenderer<T, NearestBlockEntityRenderer.State>
        *///?}
{
    //? if >=1.21.11 {
    /*private final double boundsInflation;
    private ModelBlockRenderer tessellator;
    //? if >=26.1 {
    /^private boolean ambientOcclusion;

    ^///?} else {
    private final RandomSource random = RandomSource.create();
    private final List<BlockModelPart> parts = new ArrayList<>();

    //?}
    public NearestBlockEntityRenderer(BlockEntityRendererProvider.Context context) { this(context, 0); }

    public NearestBlockEntityRenderer(BlockEntityRendererProvider.Context context, double boundsInflation) {
        if (!Double.isFinite(boundsInflation) || boundsInflation < 0) throw new IllegalArgumentException("Invalid bounds inflation");
        this.boundsInflation = boundsInflation;
    }

    public static class State extends BlockEntityRenderState {
        private List<Batch> batches = List.of();
        //? if >=26.1 {
        /^private List<BreakBatch> breaking = List.of();
        private int breakingProgress = -1;
        ^///?}
    }

    private record Batch(RenderType type, Matrix4f transform, List<LitQuad> quads) {}

    //? if >=26.1 {
    /^//? if >=26.2 {
    /^¹private record BreakBatch(int dy, List<BlockStateModelPart> parts, boolean translucent) {}

    ¹^///?} else {
    private record BreakBatch(int dy, BlockStateModel model, long seed) {}

    //?}
    ^///?}
    @Override
    public State createRenderState() { return new State(); }

    @Override
    public void extractRenderState(T entity, State state, float partialTick, Vec3 camera, ModelFeatureRenderer.CrumblingOverlay overlay) {
        BlockEntityRenderer.super.extractRenderState(entity, state, partialTick, camera, overlay);
        state.batches = List.of();
        //? if >=26.1 {
        /^state.breaking = List.of();
        state.breakingProgress = -1;
        ^///?}
        if (!(entity.getLevel() instanceof ClientLevel level)) return;
        List<Batch> batches = new ArrayList<>();
        BlockPos pos = entity.getBlockPos();
        BlockState block = entity.getBlockState();
        extractModel(level, pos, block, 0, batches);
        //? if >=26.1 {
        /^int progress = state.breakProgress != null ? state.breakProgress.progress() : -1;
        ^///?}
        BlockState upperDoor = null;
        if (isLowerDoor(block)) {
            BlockPos above = pos.above();
            BlockState upper = level.getBlockState(above);
            if (upper.is(block.getBlock()) && upper.getValue(DoorBlock.HALF) == DoubleBlockHalf.UPPER && level.getBlockEntity(above) == null) {
                upperDoor = upper;
                extractModel(level, above, upper, 1, batches);
                //? if >=26.1 {
                /^//? if >=26.3 {
                /^¹var upperProgress = level.destructionProgress().get(above.asLong());
                if (upperProgress != null && !upperProgress.isEmpty()) {
                ¹^///?} else {
                Minecraft mc = Minecraft.getInstance();
                if (mc.gameMode != null && mc.gameMode.isDestroying() && mc.hitResult instanceof net.minecraft.world.phys.BlockHitResult hit
                        && hit.getBlockPos().equals(above)) {
                //?}
                    //? if >=26.3 {
                    /^¹progress = Math.max(progress, upperProgress.last().getProgress());
                    ¹^///?} else {
                    progress = Math.max(progress, mc.gameMode.getDestroyStage());
                    //?}
                }
                ^///?}
            }
        }
        state.batches = List.copyOf(batches);
        //? if >=26.1 {
        /^state.breakingProgress = progress;
        if (progress >= 0) {
            List<BreakBatch> breaking = new ArrayList<>();
            collectBreaking(level, pos, block, 0, breaking);
            if (upperDoor != null) collectBreaking(level, pos.above(), upperDoor, 1, breaking);
            state.breaking = List.copyOf(breaking);
        }
        ^///?}
    }

    private void extractModel(ClientLevel level, BlockPos pos, BlockState block, int dy, List<Batch> output) {
        Minecraft mc = Minecraft.getInstance();
        Capture capture = new Capture();
        //? if >=26.1 {
        /^boolean ao = mc.options.ambientOcclusion().get();
        if (tessellator == null || ambientOcclusion != ao) {
            ambientOcclusion = ao;
            tessellator = new ModelBlockRenderer(ao, true, mc.getBlockColors());
        }
        ^///?} else {
        if (tessellator == null) tessellator = new ModelBlockRenderer(mc.getBlockColors());
        random.setSeed(block.getSeed(pos));
        parts.clear();
        mc.getBlockRenderer().getBlockModel(block).collectParts(level, pos, block, random, parts);
        PoseStack localPose = new PoseStack();
        localPose.translate(0, dy, 0);
        try {
            tessellator.tesselateBlock(level, parts, block, pos, localPose, capture::forLayer, true, OverlayTexture.NO_OVERLAY);
        } finally {
            parts.clear();
        }
        //?}
        //? if >=26.1 {
        /^var model = mc.getModelManager().getBlockStateModelSet().get(block);
        tessellator.tesselateBlock((x, y, z, quad, lighting) -> {
            if (capture.transform == null) capture.transform = new Matrix4f().translation(x, y, z);
            capture.add(quad, quad.materialInfo().layer(), new LitQuad(quad, lighting));
        }, 0, dy, 0, level, pos, block, model, block.getSeed(pos));
        ^///?}
        capture.groups.forEach((type, quads) -> output.add(new Batch(type, capture.transform, List.copyOf(quads))));
    }

    //? if >=26.1 {
    /^private static void collectBreaking(ClientLevel level, BlockPos pos, BlockState block, int dy, List<BreakBatch> output) {
        BlockStateModel model = Minecraft.getInstance().getModelManager().getBlockStateModelSet().get(block);
        //? if >=26.2 {
        /^¹List<BlockStateModelPart> parts = new ArrayList<>();
        RandomSource random = RandomSource.create();
        random.setSeed(block.getSeed(pos));
        model.collectParts(level, pos, block, random, parts);
        if (!parts.isEmpty()) {
            //? if >=26.3 {
            /^²boolean translucent = model.hasMaterialFlag(level, pos, block, BakedQuad.FLAG_TRANSLUCENT);
            ²^///?} else {
            boolean translucent = false;
            //?}
            output.add(new BreakBatch(dy, List.copyOf(parts), translucent));
        }
        ¹^///?} else {
        output.add(new BreakBatch(dy, model, block.getSeed(pos)));
        //?}
    }

    ^///?}
    @Override
    public void submit(State state, PoseStack pose, SubmitNodeCollector collector, CameraRenderState camera) {
        for (Batch batch : state.batches) {
            pose.pushPose();
            pose.mulPose(batch.transform());
            collector.submitCustomGeometry(pose, batch.type(), (p, vertices) -> {
                for (LitQuad quad : batch.quads()) quad.draw(p, vertices);
            });
            pose.popPose();
        }
        //? if >=26.1 {
        /^if (state.breakingProgress >= 0) {
            for (BreakBatch batch : state.breaking) {
                pose.pushPose();
                if (batch.dy() != 0) pose.translate(0.0D, batch.dy(), 0.0D);
                //? if >=26.3 {
                /^¹collector.submitBreakingBlockModel(pose, batch.parts(), state.breakingProgress, batch.translucent());
                ¹^///?} else if >=26.2 <26.3 {
                /^¹collector.submitBreakingBlockModel(pose, batch.parts(), state.breakingProgress);
                ¹^///?} else {
                collector.submitBreakingBlockModel(pose, batch.model(), batch.seed(), state.breakingProgress);
                //?}
                pose.popPose();
            }
        }
        ^///?}
    }

    private static boolean isLowerDoor(BlockState state) {
        return state.getBlock() instanceof DoorBlock && state.getValue(DoorBlock.HALF) == DoubleBlockHalf.LOWER;
    }

    @Override
    public AABB getRenderBoundingBox(T entity) {
        AABB box = new AABB(entity.getBlockPos());
        return (isLowerDoor(entity.getBlockState()) ? box.expandTowards(0, 1, 0) : box).inflate(boundsInflation);
    }

    @Override
    public int getViewDistance() { return Minecraft.getInstance().options.getEffectiveRenderDistance() * 16; }

    //? if >=26.1 {
    /^private record LitQuad(BakedQuad quad, QuadInstance lighting) {
        LitQuad {
            QuadInstance copy = new QuadInstance();
            for (int i = 0; i < 4; i++) {
                copy.setColor(i, lighting.getColor(i));
                copy.setLightCoords(i, lighting.getLightCoords(i));
            }
            copy.setOverlayCoords(lighting.overlayCoords());
            lighting = copy;
        }

        void draw(PoseStack.Pose pose, VertexConsumer vertices) { vertices.putBakedQuad(pose, quad, lighting); }
    ^///?}
    //? if >=1.21.11 <26.1 {
    /^private record LitQuad(BakedQuad quad, float[] brightness, float r, float g, float b, float a, int[] light, int overlay) {
        LitQuad {
            brightness = brightness.clone();
            light = light.clone();
        }

        void draw(PoseStack.Pose pose, VertexConsumer vertices) {
            vertices.putBulkData(pose, quad, brightness, r, g, b, a, light, overlay);
        }
    ^///?}
    }

    private static final class Capture
            //? if >=1.21.11 <26.1 {
            /^implements VertexConsumer
            ^///?}
    {
        final Map<RenderType, List<LitQuad>> groups = new LinkedHashMap<>();
        Matrix4f transform;

        void add(BakedQuad quad, ChunkSectionLayer layer, LitQuad lit) {
            //? if >=26.1 {
            /^var sprite = quad.materialInfo().sprite();
            ^///?} else {
            var sprite = quad.sprite();
            //?}
            groups.computeIfAbsent(NearestBlockRenderTypes.forAtlas(sprite.atlasLocation(), layer), ignored -> new ArrayList<>()).add(lit);
        }

        //? if >=1.21.11 <26.1 {
        /^private ChunkSectionLayer layer;

        VertexConsumer forLayer(ChunkSectionLayer layer) { this.layer = layer; return this; }

        @Override
        public void putBulkData(PoseStack.Pose pose, BakedQuad quad, float[] brightness, float r, float g, float b, float a, int[] light, int overlay) {
            if (transform == null) transform = new Matrix4f(pose.pose());
            add(quad, layer, new LitQuad(quad, brightness, r, g, b, a, light, overlay));
        }

        private static UnsupportedOperationException looseVertex() { return new UnsupportedOperationException("Expected a baked quad"); }

        @Override
        public VertexConsumer addVertex(float x, float y, float z) { throw looseVertex(); }

        @Override
        public VertexConsumer setColor(int color) { throw looseVertex(); }

        @Override
        public VertexConsumer setColor(int r, int g, int b, int a) { throw looseVertex(); }

        @Override
        public VertexConsumer setUv(float u, float v) { throw looseVertex(); }

        @Override
        public VertexConsumer setUv1(int u, int v) { throw looseVertex(); }

        @Override
        public VertexConsumer setUv2(int u, int v) { throw looseVertex(); }

        @Override
        public VertexConsumer setNormal(float x, float y, float z) { throw looseVertex(); }

        @Override
        public VertexConsumer setLineWidth(float width) { throw looseVertex(); }
        ^///?}
    }
    *///?}
}
