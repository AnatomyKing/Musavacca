package space.anatomyuniverse.musavacca.block.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
//? if >=1.21.9 {
/*import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.joml.Matrix4f;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
*///?}
//? if >=26.1 {
/*import com.mojang.blaze3d.vertex.QuadInstance;
import net.minecraft.client.resources.model.geometry.BakedQuad;
*///?} else {
import net.minecraft.client.renderer.block.model.BakedQuad;
//?}
//? if >=1.21.11 {
/*import net.minecraft.client.renderer.rendertype.RenderType;
*///?} else {
import net.minecraft.client.renderer.RenderType;
import net.neoforged.neoforge.client.RenderTypeHelper;
//?}
//? if >=1.21.9 <26.1 {
/*import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.util.RandomSource;
*///?}
//? if >=1.21.6 <1.21.9 {
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
//?}
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import space.anatomyuniverse.musavacca.particle.tinted.OriginalCandleParticles.Context;
//? if >=1.21.9 {
/*import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.world.level.material.FluidState;
*///?} else {
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.util.RandomSource;
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
        private List<Batch> batches;

        public void extract(ClientLevel level, BlockPos pos, BlockState original, int hex, Vec3 translation) {
            this.translation = translation;
            this.batches = null;
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
            Context palette = PearlCandleFlames.palette(original, hex);
            if (palette != null) this.batches = capture(this, level, original, pos, palette);
        }

        public void clear() {
            this.blockState = null;
            this.batches = null;
        }

        public void submit(PoseStack pose, SubmitNodeCollector collector) {
            if (blockState == null) return;
            pose.pushPose();
            try {
                pose.translate(translation.x, translation.y, translation.z);
                if (batches != null) {
                    for (Batch batch : batches) {
                        pose.pushPose();
                        try {
                            pose.mulPose(batch.transform());
                            collector.submitCustomGeometry(pose, batch.type(), (p, vertices) -> {
                                for (Draw draw : batch.quads()) draw.render(p, vertices);
                            });
                        } finally {
                            pose.popPose();
                        }
                    }
                    return;
                }
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

    private interface Draw {
        void render(PoseStack.Pose pose, VertexConsumer vertices);
    }

    private record Batch(RenderType type, Matrix4f transform, List<Draw> quads) {}

    private static List<Batch> capture(Model world, ClientLevel level, BlockState original, BlockPos pos, Context palette) {
        Minecraft mc = Minecraft.getInstance();
        Map<RenderType, List<Draw>> groups = new LinkedHashMap<>();
        Matrix4f transform = new Matrix4f();
        //? if >=26.1 {
        /^var model = mc.getModelManager().getBlockStateModelSet().get(original);
        renderer(mc).tesselateBlock((x, y, z, quad, lighting) -> {
            transform.translation(x, y, z);
            PearlCandleFlames.emit(quad, palette, (face, tint) -> {
                QuadInstance copy = new QuadInstance();
                for (int i = 0; i < 4; i++) {
                    copy.setColor(i, PearlCandleFlames.multiply(lighting.getColor(i), tint));
                    copy.setLightCoords(i, lighting.getLightCoords(i));
                }
                copy.setOverlayCoords(lighting.overlayCoords());
                RenderType type = NearestBlockRenderTypes.forAtlas(face.materialInfo().sprite().atlasLocation(), face.materialInfo().layer());
                groups.computeIfAbsent(type, ignored -> new ArrayList<>()).add((p, out) -> out.putBakedQuad(p, face, copy));
            });
        }, 0, 0, 0, world, pos, original, model, original.getSeed(pos));
        ^///?} else {
        var dispatcher = mc.getBlockRenderer();
        List<BlockModelPart> parts = new ArrayList<>();
        dispatcher.getBlockModel(original).collectParts(level, pos, original, RandomSource.create(original.getSeed(pos)), parts);
        dispatcher.getModelRenderer().tesselateBlock(world, parts, original, pos, new PoseStack(),
                layer -> new PearlCandleFlames.QuadOutput(palette) {
                    @Override
                    void write(PoseStack.Pose p, BakedQuad quad, float[] brightness, float r, float g, float b,
                               float a, int[] light, int overlay, boolean colored
                               //? if <1.21.11 {
                               /^, boolean readColor
                               ^///?}
                    ) {
                        transform.set(p.pose());
                        var renderLayer = colored && layer == ChunkSectionLayer.SOLID ? ChunkSectionLayer.CUTOUT : layer;
                        //? if >=1.21.11 {
                        /^RenderType type = NearestBlockRenderTypes.forAtlas(PearlCandleFlames.sprite(quad).atlasLocation(), renderLayer);
                        ^///?} else {
                        RenderType type = RenderTypeHelper.getMovingBlockRenderType(renderLayer);
                        //?}
                        float[] shades = brightness.clone();
                        int[] lights = light.clone();
                        groups.computeIfAbsent(type, ignored -> new ArrayList<>()).add((pose, vertices) ->
                                vertices.putBulkData(pose, quad, shades, r, g, b, a, lights, overlay
                                        //? if <1.21.11 {
                                        /^, readColor
                                        ^///?}
                                ));
                    }
                }, false, OverlayTexture.NO_OVERLAY);
        //?}
        List<Batch> batches = new ArrayList<>(groups.size());
        groups.forEach((type, quads) -> batches.add(new Batch(type, new Matrix4f(transform), List.copyOf(quads))));
        return List.copyOf(batches);
    }

    //? if >=26.1 {
    /^private record Tessellator(boolean ao, ModelBlockRenderer renderer) {}
    private static final ThreadLocal<Tessellator> TESSELLATOR = new ThreadLocal<>();

    private static ModelBlockRenderer renderer(Minecraft mc) {
        boolean ao = mc.options.ambientOcclusion().get();
        Tessellator current = TESSELLATOR.get();
        if (current == null || current.ao() != ao) {
            current = new Tessellator(ao, new ModelBlockRenderer(ao, true, mc.getBlockColors()));
            TESSELLATOR.set(current);
        }
        return current.renderer();
    }
    ^///?}

    *///?} else {
    private final BlockRenderDispatcher dispatcher;
    private final RandomSource random = RandomSource.create();
    //? if >=1.21.5 {
    private final List<BlockModelPart> parts = new ArrayList<>();
    //?}

    public PearlCandleRenderer(BlockEntityRendererProvider.Context context) {
        this.dispatcher = context.getBlockRenderDispatcher();
    }

    public void render(ClientLevel level, BlockPos pos, BlockState original, int hex, Vec3 translation, PoseStack pose, MultiBufferSource buffers, int overlay) {
        Context palette = PearlCandleFlames.palette(original, hex);
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
                    layer -> buffer(buffers, layer, palette), checkSides, overlay);
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
                        buffer(buffers, layer, palette),
                        checkSides, random, seed, overlay, data, layer);
            } finally {
                pose.popPose();
            }
        }
        *///?}
    }

    private static VertexConsumer buffer(MultiBufferSource buffers,
            //? if >=1.21.6 {
            ChunkSectionLayer layer,
            //?} else {
            /*RenderType layer,
            *///?}
            Context palette) {
        RenderType base = RenderTypeHelper.getMovingBlockRenderType(layer);
        if (palette == null) return buffers.getBuffer(base);
        //? if >=1.21.6 {
        RenderType flame = layer == ChunkSectionLayer.SOLID ? RenderTypeHelper.getMovingBlockRenderType(ChunkSectionLayer.CUTOUT) : base;
        //?} else {
        /*RenderType flame = layer == RenderType.solid() ? RenderTypeHelper.getMovingBlockRenderType(RenderType.cutout()) : base;
        *///?}
        return new PearlCandleFlames.QuadOutput(palette) {
            @Override
            void write(PoseStack.Pose pose, BakedQuad quad, float[] brightness, float r, float g, float b,
                       float a, int[] light, int overlay, boolean colored, boolean readColor) {
                buffers.getBuffer(colored ? flame : base).putBulkData(pose, quad, brightness, r, g, b, a, light, overlay, readColor);
            }

            @Override
            VertexConsumer delegate() {
                return buffers.getBuffer(base);
            }
        };
    }
    //?}
}


