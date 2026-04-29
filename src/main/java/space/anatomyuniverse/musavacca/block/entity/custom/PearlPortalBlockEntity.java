// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/block/entity/custom/PearlPortalBlockEntity.java
package space.anatomyuniverse.musavacca.block.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import space.anatomyuniverse.musavacca.block.custom.PearlPortalBlock;
import space.anatomyuniverse.musavacca.block.entity.ModBlockEntities;
import space.anatomyuniverse.musavacca.component.ModDataComponents;
import space.anatomyuniverse.musavacca.portal.PearlPortalDestroyer;
import space.anatomyuniverse.musavacca.portal.PearlPortalFrame;
import space.anatomyuniverse.musavacca.portal.PearlPortalNetwork;

import java.util.UUID;

public class PearlPortalBlockEntity extends BlockEntity {
    private static final String TAG_PORTAL_ID = "portal_id";
    private static final String TAG_HEX_COLOR = "hex_color";
    private static final String TAG_ORIGIN_X = "origin_x";
    private static final String TAG_ORIGIN_Y = "origin_y";
    private static final String TAG_ORIGIN_Z = "origin_z";
    private static final String TAG_AXIS = "axis";
    private static final String TAG_WIDTH = "width";
    private static final String TAG_HEIGHT = "height";

    private static final int DEFAULT_HEX_COLOR = 0xD5CD49;

    private UUID portalId = UUID.randomUUID();
    private int hexColor = DEFAULT_HEX_COLOR;

    private BlockPos originPos = null;
    private Direction.Axis axis = Direction.Axis.X;
    private int width = PearlPortalFrame.MIN_WIDTH;
    private int height = PearlPortalFrame.MIN_HEIGHT;

    public PearlPortalBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PEARL_PORTAL_BLOCK_ENTITY.get(), pos, state);
    }

    public void initializePortal(UUID portalId, int hexColor, PearlPortalFrame.Shape shape) {
        this.portalId = portalId;
        this.hexColor = normalizeHex(hexColor);
        this.originPos = shape.minCorner().immutable();
        this.axis = normalizeAxis(shape.axis());
        this.width = clampWidth(shape.width());
        this.height = clampHeight(shape.height());

        this.setChangedAndSync();
        PearlPortalNetwork.registerPortalBlock(this);
    }

    public boolean hasPortalData() {
        return this.portalId != null
                && this.originPos != null
                && this.width >= PearlPortalFrame.MIN_WIDTH
                && this.height >= PearlPortalFrame.MIN_HEIGHT;
    }

    public boolean isPortalBlockEntityForPortalBlock() {
        return this.getBlockState().getBlock() instanceof PearlPortalBlock;
    }

    public boolean isValidPortalTile() {
        return this.hasPortalData() && this.isPortalBlockEntityForPortalBlock();
    }

    public boolean isOriginBlock() {
        return this.hasPortalData() && this.getBlockPos().equals(this.getOriginPos());
    }

    public UUID getPortalId() {
        return this.portalId;
    }

    public int getHexColor() {
        return this.hexColor;
    }

    public BlockPos getOriginPos() {
        return this.originPos == null ? this.getBlockPos() : this.originPos;
    }

    public Direction.Axis getPortalAxis() {
        return normalizeAxis(this.axis);
    }

    public int getPortalWidth() {
        return this.width;
    }

    public int getPortalHeight() {
        return this.height;
    }

    public PearlPortalFrame.Shape getPortalShape() {
        return new PearlPortalFrame.Shape(
                this.getPortalAxis(),
                this.getOriginPos(),
                this.getPortalWidth(),
                this.getPortalHeight()
        );
    }

    @Override
    public void onLoad() {
        super.onLoad();

        if (this.isValidPortalTile()) {
            PearlPortalNetwork.registerPortalBlock(this);
        }
    }

    @Override
    public void setRemoved() {
        PearlPortalNetwork.unregisterPortalBlock(this);
        super.setRemoved();
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        if (this.level instanceof ServerLevel serverLevel
                && !PearlPortalDestroyer.isDestroyingPortal()
                && this.isValidPortalTile()) {
            PearlPortalDestroyer.destroyPortalFromAnyTile(serverLevel, pos, this.portalId);
        }

        super.preRemoveSideEffects(pos, state);
    }

    private void setChangedAndSync() {
        this.setChanged();

        Level level = this.getLevel();
        if (level == null) {
            return;
        }

        if (level.isClientSide()) {
            this.rerenderClientNow();
            return;
        }

        this.sendBlockUpdate(false);
    }

    private void rerenderClientNow() {
        Level level = this.getLevel();
        if (level == null || !level.isClientSide()) {
            return;
        }

        this.sendBlockUpdate(true);

        if (this.isValidPortalTile()) {
            PearlPortalNetwork.registerPortalBlock(this);
        }
    }

    private void sendBlockUpdate(boolean clientOnly) {
        Level level = this.getLevel();
        if (level == null) return;
        if (clientOnly && !level.isClientSide()) return;

        BlockPos pos = this.getBlockPos();
        BlockState state = this.getBlockState();

        level.sendBlockUpdated(
                pos,
                state,
                state,
                Block.UPDATE_CLIENTS | Block.UPDATE_IMMEDIATE
        );
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);

        this.portalId = readUuid(input, TAG_PORTAL_ID, UUID.randomUUID());
        this.hexColor = normalizeHex(input.getIntOr(TAG_HEX_COLOR, DEFAULT_HEX_COLOR));
        this.originPos = readBlockPos(input, this.getBlockPos());
        this.axis = readAxis(input);
        this.width = clampWidth(input.getIntOr(TAG_WIDTH, PearlPortalFrame.MIN_WIDTH));
        this.height = clampHeight(input.getIntOr(TAG_HEIGHT, PearlPortalFrame.MIN_HEIGHT));
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);

        output.putString(TAG_PORTAL_ID, this.portalId.toString());
        output.putInt(TAG_HEX_COLOR, this.hexColor);

        BlockPos origin = this.getOriginPos();
        output.putInt(TAG_ORIGIN_X, origin.getX());
        output.putInt(TAG_ORIGIN_Y, origin.getY());
        output.putInt(TAG_ORIGIN_Z, origin.getZ());

        output.putString(TAG_AXIS, this.getPortalAxis() == Direction.Axis.Z ? "z" : "x");
        output.putInt(TAG_WIDTH, this.width);
        output.putInt(TAG_HEIGHT, this.height);
    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter input) {
        super.applyImplicitComponents(input);

        Integer savedHex = input.get(ModDataComponents.HEX_COLOR.get());
        if (savedHex != null) {
            this.hexColor = normalizeHex(savedHex);
        }
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        components.set(ModDataComponents.HEX_COLOR.get(), this.hexColor);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        return this.saveWithoutMetadata(provider);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void handleUpdateTag(ValueInput input) {
        super.handleUpdateTag(input);
        this.rerenderClientNow();
    }

    @Override
    public void onDataPacket(Connection connection, ValueInput input) {
        super.onDataPacket(connection, input);
        this.rerenderClientNow();
    }

    private static BlockPos readBlockPos(ValueInput input, BlockPos fallback) {
        return new BlockPos(
                input.getIntOr(TAG_ORIGIN_X, fallback.getX()),
                input.getIntOr(TAG_ORIGIN_Y, fallback.getY()),
                input.getIntOr(TAG_ORIGIN_Z, fallback.getZ())
        );
    }

    private static Direction.Axis readAxis(ValueInput input) {
        return "z".equalsIgnoreCase(input.getStringOr(TAG_AXIS, "x"))
                ? Direction.Axis.Z
                : Direction.Axis.X;
    }

    private static UUID readUuid(ValueInput input, String key, UUID fallback) {
        String text = input.getStringOr(key, "");
        if (text.isEmpty()) return fallback;

        try {
            return UUID.fromString(text);
        } catch (IllegalArgumentException ignored) {
            return fallback;
        }
    }

    private static Direction.Axis normalizeAxis(Direction.Axis axis) {
        return axis == Direction.Axis.Z ? Direction.Axis.Z : Direction.Axis.X;
    }

    private static int normalizeHex(int hexColor) {
        return hexColor & 0xFFFFFF;
    }

    private static int clampWidth(int value) {
        return clamp(value, PearlPortalFrame.MIN_WIDTH, PearlPortalFrame.MAX_WIDTH);
    }

    private static int clampHeight(int value) {
        return clamp(value, PearlPortalFrame.MIN_HEIGHT, PearlPortalFrame.MAX_HEIGHT);
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}