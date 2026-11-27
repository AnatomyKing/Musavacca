package space.anatomyuniverse.musavacca.block.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.CampfireRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoReceptorLogic.ReceptorPosition;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoTableCandleVoxelShapes;
import space.anatomyuniverse.musavacca.block.entity.custom.PearlCampfireBlockEntity;
import space.anatomyuniverse.musavacca.block.entity.custom.PearlCopycatBlockEntity;
import space.anatomyuniverse.musavacca.block.entity.custom.VocoTableBlockEntity;
import space.anatomyuniverse.musavacca.block.ModBlocks;
import space.anatomyuniverse.musavacca.block.entity.custom.MusavaccaPortalDoorBlockEntity;
import space.anatomyuniverse.musavacca.tint.MusavaccaTints;
//? if >=1.21.9 {
/*import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.blockentity.state.CampfireRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;

import java.util.Arrays;
*///?} else {
import net.minecraft.client.renderer.MultiBufferSource;
//?}
//? if >=26.1 {
/*import net.minecraft.client.renderer.state.level.CameraRenderState;
*///?} else if >=1.21.9 {
/*import net.minecraft.client.renderer.state.CameraRenderState;
*///?}

//? if >=1.21.9 {
/*public final class LayeredBlockEntityRenderer<T extends BlockEntity> implements BlockEntityRenderer<T, LayeredBlockEntityRenderer.State> {
*///?} else {
public final class LayeredBlockEntityRenderer<T extends BlockEntity> implements BlockEntityRenderer<T> {
//?}
    private static final Vec3 UPPER_DOOR_OFFSET = new Vec3(0, 1, 0);
    private final boolean extendedView;
    private final int modelCount;
    @Nullable private final CampfireRenderer campfireRenderer;
    @Nullable private final VocoTableBlockEntityItemDisplayRenderer itemRenderer;
    private final double boundsPadding;
    //? if <1.21.9 {
    private final DynamicBlockRenderer blockRenderer;
    //?}

    public LayeredBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this(context, false, 0.0D);
    }

    public LayeredBlockEntityRenderer(BlockEntityRendererProvider.Context context, boolean extendedView, double boundsPadding) {
        this(context, extendedView, boundsPadding, 1, false);
    }

    public static LayeredBlockEntityRenderer<VocoTableBlockEntity> forTable(BlockEntityRendererProvider.Context context) {
        //? if >=1.21.11 {
        /*return new LayeredBlockEntityRenderer<>(context, true, 0.5D, ReceptorPosition.COUNT + 1, true);
        *///?} else {
        return new LayeredBlockEntityRenderer<>(context, false, 0.0D, ReceptorPosition.COUNT + 1, true);
        //?}
    }

    public static LayeredBlockEntityRenderer<MusavaccaPortalDoorBlockEntity> forDoor(BlockEntityRendererProvider.Context context) {
        return new LayeredBlockEntityRenderer<>(context, true, 0.0625D, 2, false);
    }

    private LayeredBlockEntityRenderer(BlockEntityRendererProvider.Context context, boolean extendedView, double boundsPadding,
                                       int modelCount, boolean table) {
        this.extendedView = extendedView;
        this.modelCount = modelCount;
        this.campfireRenderer = extendedView || table ? null : new CampfireRenderer(context);
        this.itemRenderer = table ? new VocoTableBlockEntityItemDisplayRenderer(context) : null;
        this.boundsPadding = boundsPadding;
        //? if <1.21.9 {
        this.blockRenderer = new DynamicBlockRenderer(context);
        //?}
    }

    private BlockState model(T entity) {
        return entity instanceof PearlCopycatBlockEntity copycat ? copycat.originalState() : entity.getBlockState();
    }

    @Nullable
    private static BlockState upperDoorModel(ClientLevel level, BlockPos pos) {
        BlockState upper = level.getBlockState(pos.above());
        return upper.is(ModBlocks.MUSAVACCA_DOOR.get()) && upper.getValue(DoorBlock.HALF) == DoubleBlockHalf.UPPER
                ? upper : null;
    }

    @Override
    public AABB getRenderBoundingBox(T entity) {
        AABB bounds = new AABB(entity.getBlockPos()).inflate(boundsPadding);
        return entity instanceof MusavaccaPortalDoorBlockEntity ? bounds.expandTowards(0, 1, 0) : bounds;
    }

    @Override
    public int getViewDistance() {
        return extendedView ? Minecraft.getInstance().options.getEffectiveRenderDistance() * 16
                : 64;
    }

    //? if >=1.21.9 {
    /*public static final class State extends BlockEntityRenderState {
        private final DynamicBlockRenderer.Model[] models;
        @Nullable private final VocoTableBlockEntityItemDisplayRenderer.State item;
        @Nullable private CampfireRenderState campfire;

        private State(int modelCount, boolean table) {
            models = new DynamicBlockRenderer.Model[modelCount];
            item = table ? new VocoTableBlockEntityItemDisplayRenderer.State() : null;
        }

        private void extractModel(int index, ClientLevel level, BlockPos pos, BlockState block, @Nullable LayeredBlockTint tint, Vec3 offset) {
            var model = models[index];
            if (model == null) models[index] = model = new DynamicBlockRenderer.Model();
            model.extract(level, pos, block, tint, offset);
        }

        private void extractPortal(int index, ClientLevel level, BlockPos pos, @Nullable BlockState block, int hex, Vec3 offset) {
            var tint = block == null ? null : LayeredBlockTint.forBlock(block, hex);
            if (tint == null) models[index] = null;
            else extractModel(index, level, pos, block, tint, offset);
        }
    }

    @Override
    public State createRenderState() {
        return new State(modelCount, itemRenderer != null);
    }

    @Override
    public void extractRenderState(T entity, State state, float partialTick, Vec3 camera,
                                   ModelFeatureRenderer.CrumblingOverlay overlay) {
        BlockEntityRenderState.extractBase(entity, state, overlay);
        if (!(entity.getLevel() instanceof ClientLevel level)) {
            Arrays.fill(state.models, null);
            if (state.item != null) state.item.clear();
            state.campfire = null;
            return;
        }
        if (entity instanceof VocoTableBlockEntity table && itemRenderer != null && state.item != null) {
            var pos = table.getBlockPos().above();
            for (var receptor : ReceptorPosition.values()) {
                int index = receptor.id();
                var candle = table.candle(receptor);
                if (candle == null) state.models[index] = null;
                else state.extractModel(index, level, pos, candle.state(), LayeredBlockTint.forBlock(candle.state(), candle.hex()),
                        VocoTableCandleVoxelShapes.renderTranslation(receptor));
            }
            var portalTint = LayeredBlockTint.forVocoTable(table);
            int portalIndex = ReceptorPosition.COUNT;
            if (portalTint == null) state.models[portalIndex] = null;
            else state.extractModel(portalIndex, level, table.getBlockPos(), table.getBlockState(), portalTint, Vec3.ZERO);
            itemRenderer.extract(table, state.item, partialTick);
            return;
        }
        BlockState model = model(entity);
        int hex = MusavaccaTints.blockEntityHexColor(entity);
        if (entity instanceof MusavaccaPortalDoorBlockEntity) {
            BlockPos pos = entity.getBlockPos();
            state.extractPortal(0, level, pos, model, hex, Vec3.ZERO);
            state.extractPortal(1, level, pos.above(), upperDoorModel(level, pos), hex, UPPER_DOOR_OFFSET);
            return;
        }
        var tint = LayeredBlockTint.forBlock(model, hex);
        if (LayeredBlockTint.isPortalCarrier(model) && tint == null) {
            state.models[0] = null;
            state.campfire = null;
            return;
        }
        state.extractModel(0, level, entity.getBlockPos(), model, tint, Vec3.ZERO);
        var campfire = entity instanceof PearlCampfireBlockEntity fire ? fire.originalBlockEntity() : null;
        if (campfireRenderer == null || campfire == null) state.campfire = null;
        else {
            if (state.campfire == null) state.campfire = campfireRenderer.createRenderState();
            campfireRenderer.extractRenderState(campfire, state.campfire, partialTick, camera, overlay);
        }
    }

    @Override
    public void submit(State state, PoseStack pose, SubmitNodeCollector collector, CameraRenderState camera) {
        for (var model : state.models) {
            if (model != null) model.submit(pose, collector);
        }
        if (state.item != null) VocoTableBlockEntityItemDisplayRenderer.submit(state.item, pose, collector, state.lightCoords);
        if (campfireRenderer != null && state.campfire != null) campfireRenderer.submit(state.campfire, pose, collector, camera);
    }
    *///?} else {
    @Override
    public void render(T entity, float partialTick, PoseStack pose, MultiBufferSource buffers, int light,
                       //? if >=1.21.5 {
                       int overlay, Vec3 camera) {
                       //?} else {
                       /*int overlay) {
                       *///?}
        if (!(entity.getLevel() instanceof ClientLevel level)) return;
        if (entity instanceof VocoTableBlockEntity table && itemRenderer != null) {
            var pos = table.getBlockPos().above();
            for (var receptor : ReceptorPosition.values()) {
                var candle = table.candle(receptor);
                if (candle == null) continue;
                blockRenderer.render(level, pos, candle.state(), LayeredBlockTint.forBlock(candle.state(), candle.hex()),
                        VocoTableCandleVoxelShapes.renderTranslation(receptor), pose, buffers, overlay);
            }
            var portalTint = LayeredBlockTint.forVocoTable(table);
            if (portalTint != null) blockRenderer.render(level, table.getBlockPos(), table.getBlockState(), portalTint,
                    Vec3.ZERO, pose, buffers, overlay);
            itemRenderer.render(table, partialTick, pose, buffers, light, overlay);
            return;
        }
        BlockState model = model(entity);
        int hex = MusavaccaTints.blockEntityHexColor(entity);
        var tint = LayeredBlockTint.forBlock(model, hex);
        if (entity instanceof MusavaccaPortalDoorBlockEntity) {
            BlockPos pos = entity.getBlockPos();
            if (tint != null) blockRenderer.render(level, pos, model, tint, Vec3.ZERO, pose, buffers, overlay);
            var upper = upperDoorModel(level, pos);
            var upperTint = upper == null ? null : LayeredBlockTint.forBlock(upper, hex);
            if (upperTint != null) blockRenderer.render(level, pos.above(), upper, upperTint, UPPER_DOOR_OFFSET, pose, buffers, overlay);
            return;
        }
        if (LayeredBlockTint.isPortalCarrier(model) && tint == null) return;
        blockRenderer.render(level, entity.getBlockPos(), model, tint, Vec3.ZERO, pose, buffers, overlay);
        var campfire = entity instanceof PearlCampfireBlockEntity fire ? fire.originalBlockEntity() : null;
        if (campfireRenderer != null && campfire != null) {
            //? if >=1.21.5 {
            campfireRenderer.render(campfire, partialTick, pose, buffers, light, overlay, camera);
            //?} else {
            /*campfireRenderer.render(campfire, partialTick, pose, buffers, light, overlay);
            *///?}
        }
    }
    //?}
}
