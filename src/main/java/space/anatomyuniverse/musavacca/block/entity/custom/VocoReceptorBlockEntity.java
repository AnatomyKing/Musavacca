package space.anatomyuniverse.musavacca.block.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import space.anatomyuniverse.musavacca.block.entity.ModBlockEntities;
import space.anatomyuniverse.musavacca.component.ModDataComponents;

public class VocoReceptorBlockEntity extends BlockEntity {

    private static final String TAG_YAW_DEGREES = "yaw_degrees";
    private static final String TAG_PITCH_DEGREES = "pitch_degrees";
    private static final String TAG_HEX_COLOR = "hex_color";

    public static final int UNSET_HEX_COLOR = -1;

    public static final int MIN_YAW_DEGREES = -180;
    public static final int MAX_YAW_DEGREES = 180;

    public static final int MIN_PITCH_DEGREES = -90;
    public static final int MAX_PITCH_DEGREES = 90;

    public static final int DEFAULT_YAW_DEGREES = -135;
    public static final int DEFAULT_PITCH_DEGREES = 0;

    private int yawDegrees = DEFAULT_YAW_DEGREES;
    private int pitchDegrees = DEFAULT_PITCH_DEGREES;

    /*
     * Starts unset.
     *
     * This means the receptor has no HEX_COLOR data component until
     * VocoPearlPortalLogic finds a valid lit pearl candle above it.
     */
    private int hexColor = UNSET_HEX_COLOR;

    public VocoReceptorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.VOCO_RECEPTOR_BLOCK_ENTITY.get(), pos, state);
    }

    public int getYawDegrees() {
        return this.yawDegrees;
    }

    public int getPitchDegrees() {
        return this.pitchDegrees;
    }

    public boolean hasHexColor() {
        return this.hexColor != UNSET_HEX_COLOR;
    }

    public int getHexColor() {
        return this.hexColor;
    }

    public void setYawDegrees(int yawDegrees) {
        int clamped = clampYaw(yawDegrees);
        if (this.yawDegrees == clamped) {
            return;
        }

        this.yawDegrees = clamped;
        this.markChangedAndSync();
    }

    public void setPitchDegrees(int pitchDegrees) {
        int clamped = clampPitch(pitchDegrees);
        if (this.pitchDegrees == clamped) {
            return;
        }

        this.pitchDegrees = clamped;
        this.markChangedAndSync();
    }

    public void setFacingDegrees(int yawDegrees, int pitchDegrees) {
        int clampedYaw = clampYaw(yawDegrees);
        int clampedPitch = clampPitch(pitchDegrees);

        if (this.yawDegrees == clampedYaw && this.pitchDegrees == clampedPitch) {
            return;
        }

        this.yawDegrees = clampedYaw;
        this.pitchDegrees = clampedPitch;
        this.markChangedAndSync();
    }

    public void setHexColor(int hexColor) {
        int normalized = normalizeHex(hexColor);
        if (this.hexColor == normalized) {
            return;
        }

        this.hexColor = normalized;
        this.markChangedAndSync();
    }

    public void clearHexColor() {
        if (this.hexColor == UNSET_HEX_COLOR) {
            return;
        }

        this.hexColor = UNSET_HEX_COLOR;
        this.markChangedAndSync();
    }

    private void markChangedAndSync() {
        this.setChanged();
        this.syncToClientAndRerender();
    }

    private void syncToClientAndRerender() {
        Level level = this.getLevel();
        if (level == null) {
            return;
        }

        BlockPos pos = this.getBlockPos();
        BlockState state = this.getBlockState();

        level.sendBlockUpdated(
                pos,
                state,
                state,
                Block.UPDATE_CLIENTS | Block.UPDATE_IMMEDIATE
        );
    }

    private void rerenderClientNow() {
        Level level = this.getLevel();
        if (level == null || !level.isClientSide()) {
            return;
        }

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

        this.yawDegrees = clampYaw(input.getIntOr(TAG_YAW_DEGREES, DEFAULT_YAW_DEGREES));
        this.pitchDegrees = clampPitch(input.getIntOr(TAG_PITCH_DEGREES, DEFAULT_PITCH_DEGREES));
        this.hexColor = readHexOrUnset(input);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);

        output.putInt(TAG_YAW_DEGREES, this.yawDegrees);
        output.putInt(TAG_PITCH_DEGREES, this.pitchDegrees);

        if (this.hasHexColor()) {
            output.putInt(TAG_HEX_COLOR, this.hexColor);
        }
    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter input) {
        super.applyImplicitComponents(input);

        Integer savedHex = input.get(ModDataComponents.HEX_COLOR.get());
        this.hexColor = savedHex == null ? UNSET_HEX_COLOR : normalizeHex(savedHex);
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);

        if (this.hasHexColor()) {
            components.set(ModDataComponents.HEX_COLOR.get(), this.hexColor);
        }
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

    public static int clampYaw(int yawDegrees) {
        return clamp(yawDegrees, MIN_YAW_DEGREES, MAX_YAW_DEGREES);
    }

    public static int clampPitch(int pitchDegrees) {
        return clamp(pitchDegrees, MIN_PITCH_DEGREES, MAX_PITCH_DEGREES);
    }

    private static int readHexOrUnset(ValueInput input) {
        int loaded = input.getIntOr(TAG_HEX_COLOR, UNSET_HEX_COLOR);
        return loaded == UNSET_HEX_COLOR ? UNSET_HEX_COLOR : normalizeHex(loaded);
    }

    private static int normalizeHex(int hexColor) {
        return hexColor & 0xFFFFFF;
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}