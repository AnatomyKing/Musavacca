// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/block/entity/custom/VocoPostBlockEntity.java
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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import space.anatomyuniverse.musavacca.block.custom.VocoPostBlock;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoReceptorLogic;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoReceptorLogic.ReceptorPosition;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoTeleportLogic;
import space.anatomyuniverse.musavacca.block.entity.ModBlockEntities;
import space.anatomyuniverse.musavacca.component.ModDataComponents;
import space.anatomyuniverse.musavacca.teleport.HexTeleportDirectory;

public class VocoPostBlockEntity extends BlockEntity {

    private static final String TAG_YAW_DEGREES = "yaw_degrees";
    private static final String TAG_PITCH_DEGREES = "pitch_degrees";
    private static final String TAG_HEX_COLOR = "hex_color";

    private static final String TAG_CUSTOM_TARGET = "custom_target";
    private static final String TAG_TARGET_X = "target_x";
    private static final String TAG_TARGET_Y = "target_y";
    private static final String TAG_TARGET_Z = "target_z";

    public static final int UNSET_HEX_COLOR = VocoReceptorLogic.UNSET_HEX_COLOR;

    public static final int DEFAULT_YAW_DEGREES = ReceptorPosition.NORTH_EAST.defaultYawDegrees();
    public static final int DEFAULT_PITCH_DEGREES = ReceptorPosition.NORTH_EAST.defaultPitchDegrees();

    private int yawDegrees = DEFAULT_YAW_DEGREES;
    private int pitchDegrees = DEFAULT_PITCH_DEGREES;
    private int hexColor = UNSET_HEX_COLOR;

    private boolean customTargetEnabled = false;
    private double targetX;
    private double targetY;
    private double targetZ;

    public VocoPostBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.VOCO_POST_BLOCK_ENTITY.get(), pos, state);

        Vec3 fallback = VocoTeleportLogic.getDefaultTeleportPosition(pos, ReceptorPosition.NORTH_EAST);
        this.targetX = fallback.x;
        this.targetY = fallback.y;
        this.targetZ = fallback.z;
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

    public boolean isCustomTargetEnabled() {
        return this.customTargetEnabled;
    }

    public Vec3 getCustomTarget() {
        if (!this.customTargetEnabled) {
            return VocoTeleportLogic.getDefaultTeleportPosition(
                    this.getBlockPos(),
                    ReceptorPosition.NORTH_EAST
            );
        }

        return new Vec3(this.targetX, this.targetY, this.targetZ);
    }

    public void setCustomTargetEnabled(boolean enabled) {
        if (this.customTargetEnabled == enabled) {
            return;
        }

        this.customTargetEnabled = enabled;

        if (enabled && this.targetY == 0.0D) {
            Vec3 fallback = VocoTeleportLogic.getDefaultTeleportPosition(
                    this.getBlockPos(),
                    ReceptorPosition.NORTH_EAST
            );

            this.targetX = fallback.x;
            this.targetY = fallback.y;
            this.targetZ = fallback.z;
        }

        this.markChangedAndSync();
        this.resyncEndpoint();
    }

    public void setCustomTarget(Vec3 target, int yawDegrees, int pitchDegrees) {
        this.customTargetEnabled = true;
        this.targetX = target.x;
        this.targetY = target.y;
        this.targetZ = target.z;

        this.yawDegrees = clampYaw(yawDegrees);
        this.pitchDegrees = clampPitch(pitchDegrees);

        this.markChangedAndSync();
        this.resyncEndpoint();
    }

    public void setYawDegrees(int yawDegrees) {
        int clamped = clampYaw(yawDegrees);
        if (this.yawDegrees == clamped) {
            return;
        }

        this.yawDegrees = clamped;
        this.markChangedAndSync();
        this.resyncEndpoint();
    }

    public void setPitchDegrees(int pitchDegrees) {
        int clamped = clampPitch(pitchDegrees);
        if (this.pitchDegrees == clamped) {
            return;
        }

        this.pitchDegrees = clamped;
        this.markChangedAndSync();
        this.resyncEndpoint();
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
        this.resyncEndpoint();
    }

    public boolean setHexColor(int hexColor) {
        int normalized = normalizeHex(hexColor);

        if (this.hexColor == normalized) {
            return true;
        }

        this.hexColor = normalized;

        this.markChangedAndSync();
        this.resyncEndpoint();

        return true;
    }

    public void clearHexColor() {
        if (this.hexColor == UNSET_HEX_COLOR) {
            return;
        }

        this.hexColor = UNSET_HEX_COLOR;

        this.markChangedAndSync();
        this.resyncEndpoint();
    }

    public void releaseHexClaim() {
        Level level = this.getLevel();
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        HexTeleportDirectory.get(serverLevel.getServer()).removeOwner(
                HexTeleportDirectory.vocoPostReceptorCornerOwnerKey(
                        serverLevel.dimension().location(),
                        this.getBlockPos()
                )
        );
    }

    private void resyncEndpoint() {
        Level level = this.getLevel();
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        BlockState state = this.getBlockState();

        boolean portalActive = state.hasProperty(VocoPostBlock.PORTAL)
                && state.getValue(VocoPostBlock.PORTAL);

        if (!portalActive || !this.hasHexColor()) {
            VocoTeleportLogic.syncEndpoint(
                    serverLevel,
                    this.getBlockPos(),
                    ReceptorPosition.NORTH_EAST,
                    false,
                    VocoReceptorLogic.UNSET_HEX_COLOR
            );
            return;
        }

        VocoTeleportLogic.syncEndpoint(
                serverLevel,
                this.getBlockPos(),
                ReceptorPosition.NORTH_EAST,
                true,
                this.hexColor
        );
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
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        this.releaseHexClaim();
        super.preRemoveSideEffects(pos, state);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);

        this.yawDegrees = clampYaw(input.getIntOr(TAG_YAW_DEGREES, DEFAULT_YAW_DEGREES));
        this.pitchDegrees = clampPitch(input.getIntOr(TAG_PITCH_DEGREES, DEFAULT_PITCH_DEGREES));
        this.hexColor = readHexOrUnset(input);

        Vec3 fallback = VocoTeleportLogic.getDefaultTeleportPosition(
                this.getBlockPos(),
                ReceptorPosition.NORTH_EAST
        );

        this.customTargetEnabled = input.getBooleanOr(TAG_CUSTOM_TARGET, false);
        this.targetX = input.getDoubleOr(TAG_TARGET_X, fallback.x);
        this.targetY = input.getDoubleOr(TAG_TARGET_Y, fallback.y);
        this.targetZ = input.getDoubleOr(TAG_TARGET_Z, fallback.z);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);

        output.putInt(TAG_YAW_DEGREES, this.yawDegrees);
        output.putInt(TAG_PITCH_DEGREES, this.pitchDegrees);

        if (this.hasHexColor()) {
            output.putInt(TAG_HEX_COLOR, this.hexColor);
        }

        output.putBoolean(TAG_CUSTOM_TARGET, this.customTargetEnabled);
        output.putDouble(TAG_TARGET_X, this.targetX);
        output.putDouble(TAG_TARGET_Y, this.targetY);
        output.putDouble(TAG_TARGET_Z, this.targetZ);
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
        return VocoReceptorLogic.clampYaw(yawDegrees);
    }

    public static int clampPitch(int pitchDegrees) {
        return VocoReceptorLogic.clampPitch(pitchDegrees);
    }

    private static int readHexOrUnset(ValueInput input) {
        int loaded = input.getIntOr(TAG_HEX_COLOR, UNSET_HEX_COLOR);
        return loaded == UNSET_HEX_COLOR ? UNSET_HEX_COLOR : normalizeHex(loaded);
    }

    private static int normalizeHex(int hexColor) {
        return VocoReceptorLogic.normalizeHex(hexColor);
    }
}