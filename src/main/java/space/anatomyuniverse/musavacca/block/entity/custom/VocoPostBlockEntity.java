package space.anatomyuniverse.musavacca.block.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
//? if >=1.21.5
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
//? if >=1.21.6
import net.minecraft.world.level.storage.ValueInput;
//? if >=1.21.6
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import space.anatomyuniverse.musavacca.block.custom.VocoPostBlock;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoReceptorLogic;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoReceptorLogic.ReceptorPosition;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoTeleportLogic;
import space.anatomyuniverse.musavacca.block.entity.ModBlockEntities;
import space.anatomyuniverse.musavacca.component.ModDataComponents;

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
        this.resetDefaults(pos, VocoPostBlock.receptorPosition(state));
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
            return VocoTeleportLogic.getDefaultTeleportPosition(this.getBlockPos(), this.getPostReceptor());
        }

        return new Vec3(this.targetX, this.targetY, this.targetZ);
    }

    public void setCustomTargetEnabled(boolean enabled) {
        if (this.customTargetEnabled == enabled) {
            return;
        }

        this.customTargetEnabled = enabled;

        if (enabled && this.targetY == 0.0D) {
            this.setTarget(VocoTeleportLogic.getDefaultTeleportPosition(this.getBlockPos(), this.getPostReceptor()));
        }

        this.changed();
    }

    public void setCustomTarget(Vec3 target, int yawDegrees, int pitchDegrees) {
        this.customTargetEnabled = true;
        this.setTarget(target);
        this.setFacingRaw(yawDegrees, pitchDegrees);
        this.changed();
    }

    public void resetCustomTarget() {
        ReceptorPosition receptor = this.getPostReceptor();
        Vec3 fallback = VocoTeleportLogic.getDefaultTeleportPosition(this.getBlockPos(), receptor);

        this.customTargetEnabled = false;
        this.setTarget(fallback);
        this.setFacingRaw(receptor.defaultYawDegrees(), receptor.defaultPitchDegrees());
        this.changed();
    }

    public void setYawDegrees(int yawDegrees) {
        int clamped = clampYaw(yawDegrees);
        if (this.yawDegrees == clamped) {
            return;
        }

        this.yawDegrees = clamped;
        this.changed();
    }

    public void setPitchDegrees(int pitchDegrees) {
        int clamped = clampPitch(pitchDegrees);
        if (this.pitchDegrees == clamped) {
            return;
        }

        this.pitchDegrees = clamped;
        this.changed();
    }

    public void setFacingDegrees(int yawDegrees, int pitchDegrees) {
        int oldYaw = this.yawDegrees;
        int oldPitch = this.pitchDegrees;

        this.setFacingRaw(yawDegrees, pitchDegrees);

        if (this.yawDegrees != oldYaw || this.pitchDegrees != oldPitch) {
            this.changed();
        }
    }

    public boolean setHexColor(int hexColor) {
        int normalized = normalizeHex(hexColor);

        if (this.hexColor == normalized) {
            return true;
        }

        this.hexColor = normalized;
        this.changed();

        return true;
    }

    public void clearHexColor() {
        if (this.hexColor == UNSET_HEX_COLOR) {
            return;
        }

        this.hexColor = UNSET_HEX_COLOR;
        this.markChangedAndSync();
        this.releaseHexClaim();
    }

    public void releaseHexClaim() {
        Level level = this.getLevel();
        if (level instanceof ServerLevel serverLevel) {
            VocoTeleportLogic.removeOwnerAndPromote(
                    serverLevel,
                    this.getBlockPos(),
                    this.getPostReceptor()
            );
        }
    }

    private void changed() {
        this.markChangedAndSync();

        if (this.hasHexColor()) {
            this.resyncEndpoint();
        }
    }

    private void resyncEndpoint() {
        Level level = this.getLevel();
        if (!(level instanceof ServerLevel serverLevel) || !this.hasHexColor()) {
            return;
        }

        BlockState state = this.getBlockState();
        if (!state.hasProperty(VocoPostBlock.PORTAL) || !state.getValue(VocoPostBlock.PORTAL)) {
            return;
        }

        VocoTeleportLogic.syncEndpointDetailed(
                serverLevel,
                this.getBlockPos(),
                this.getPostReceptor(),
                true,
                this.hexColor
        );
    }

    private void resetDefaults(BlockPos pos, ReceptorPosition receptor) {
        Vec3 fallback = VocoTeleportLogic.getDefaultTeleportPosition(pos, receptor);

        this.yawDegrees = receptor.defaultYawDegrees();
        this.pitchDegrees = receptor.defaultPitchDegrees();
        this.targetX = fallback.x;
        this.targetY = fallback.y;
        this.targetZ = fallback.z;
    }

    private void setTarget(Vec3 target) {
        this.targetX = target.x;
        this.targetY = target.y;
        this.targetZ = target.z;
    }

    private void setFacingRaw(int yawDegrees, int pitchDegrees) {
        this.yawDegrees = clampYaw(yawDegrees);
        this.pitchDegrees = clampPitch(pitchDegrees);
    }

    private ReceptorPosition getPostReceptor() {
        return VocoPostBlock.receptorPosition(this.getBlockState());
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

    public void cleanupBeforeRemoval() {
        this.releaseHexClaim();
    }

    //? if >=1.21.5 {
    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        this.cleanupBeforeRemoval();
        super.preRemoveSideEffects(pos, state);
    }
    //?}

    //? if >=1.21.6 {
    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);

        ReceptorPosition receptor = this.getPostReceptor();
        Vec3 fallback = VocoTeleportLogic.getDefaultTeleportPosition(this.getBlockPos(), receptor);

        this.yawDegrees = clampYaw(input.getIntOr(TAG_YAW_DEGREES, receptor.defaultYawDegrees()));
        this.pitchDegrees = clampPitch(input.getIntOr(TAG_PITCH_DEGREES, receptor.defaultPitchDegrees()));
        this.hexColor = readHexOrUnset(input);

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
    //?} else {
    /*@Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);

        ReceptorPosition receptor = this.getPostReceptor();
        Vec3 fallback = VocoTeleportLogic.getDefaultTeleportPosition(this.getBlockPos(), receptor);

        this.yawDegrees = clampYaw(readTagInt(tag, TAG_YAW_DEGREES, receptor.defaultYawDegrees()));
        this.pitchDegrees = clampPitch(readTagInt(tag, TAG_PITCH_DEGREES, receptor.defaultPitchDegrees()));
        this.hexColor = readHexOrUnset(tag);

        this.customTargetEnabled = readTagBoolean(tag, TAG_CUSTOM_TARGET, false);
        this.targetX = readTagDouble(tag, TAG_TARGET_X, fallback.x);
        this.targetY = readTagDouble(tag, TAG_TARGET_Y, fallback.y);
        this.targetZ = readTagDouble(tag, TAG_TARGET_Z, fallback.z);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);

        tag.putInt(TAG_YAW_DEGREES, this.yawDegrees);
        tag.putInt(TAG_PITCH_DEGREES, this.pitchDegrees);

        if (this.hasHexColor()) {
            tag.putInt(TAG_HEX_COLOR, this.hexColor);
        }

        tag.putBoolean(TAG_CUSTOM_TARGET, this.customTargetEnabled);
        tag.putDouble(TAG_TARGET_X, this.targetX);
        tag.putDouble(TAG_TARGET_Y, this.targetY);
        tag.putDouble(TAG_TARGET_Z, this.targetZ);
    }
    *///?}

    //? if >=1.21.5 {
    @Override
    protected void applyImplicitComponents(DataComponentGetter input) {
        super.applyImplicitComponents(input);

        Integer savedHex = input.get(ModDataComponents.HEX_COLOR.get());
        this.hexColor = savedHex == null ? UNSET_HEX_COLOR : normalizeHex(savedHex);
    }
    //?} else {
    /*@Override
    protected void applyImplicitComponents(DataComponentInput input) {
        super.applyImplicitComponents(input);

        Integer savedHex = input.get(ModDataComponents.HEX_COLOR.get());
        this.hexColor = savedHex == null ? UNSET_HEX_COLOR : normalizeHex(savedHex);
    }
    *///?}

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

    //? if >=1.21.6 {
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
    //?} else {
    /*@Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider provider) {
        super.handleUpdateTag(tag, provider);
        this.rerenderClientNow();
    }

    @Override
    public void onDataPacket(Connection connection, ClientboundBlockEntityDataPacket packet, HolderLookup.Provider provider) {
        super.onDataPacket(connection, packet, provider);
        this.rerenderClientNow();
    }
    *///?}

    public static int clampYaw(int yawDegrees) {
        return VocoReceptorLogic.clampYaw(yawDegrees);
    }

    public static int clampPitch(int pitchDegrees) {
        return VocoReceptorLogic.clampPitch(pitchDegrees);
    }

    //? if >=1.21.6 {
    private static int readHexOrUnset(ValueInput input) {
        int loaded = input.getIntOr(TAG_HEX_COLOR, UNSET_HEX_COLOR);
        return loaded == UNSET_HEX_COLOR ? UNSET_HEX_COLOR : normalizeHex(loaded);
    }
    //?} else {
    /*private static int readHexOrUnset(CompoundTag tag) {
        int loaded = readTagInt(tag, TAG_HEX_COLOR, UNSET_HEX_COLOR);
        return loaded == UNSET_HEX_COLOR ? UNSET_HEX_COLOR : normalizeHex(loaded);
    }
    *///?}

    private static int normalizeHex(int hexColor) {
        return VocoReceptorLogic.normalizeHex(hexColor);
    }

    private static int readTagInt(CompoundTag tag, String key, int defaultValue) {
        //? if >=1.21.5 {
        return tag.getIntOr(key, defaultValue);
        //?} else {
        /*return tag.contains(key) ? tag.getInt(key) : defaultValue;
        *///?}
    }

    private static boolean readTagBoolean(CompoundTag tag, String key, boolean defaultValue) {
        //? if >=1.21.5 {
        return tag.getBooleanOr(key, defaultValue);
        //?} else {
        /*return tag.contains(key) ? tag.getBoolean(key) : defaultValue;
        *///?}
    }

    private static double readTagDouble(CompoundTag tag, String key, double defaultValue) {
        //? if >=1.21.5 {
        return tag.getDoubleOr(key, defaultValue);
        //?} else {
        /*return tag.contains(key) ? tag.getDouble(key) : defaultValue;
        *///?}
    }
}



