package space.anatomyuniverse.musavacca.block.entity.custom;

import space.anatomyuniverse.musavacca.block.custom.logic.VocoPostCandleLogic;
import space.anatomyuniverse.musavacca.teleport.HexTeleportAddressNetwork;
import space.anatomyuniverse.musavacca.teleport.HexTeleportDirectory;
import space.anatomyuniverse.musavacca.teleport.TeleportDeviceIdentity;

import java.util.UUID;

//? if <1.21.5 {
/*import net.minecraft.nbt.Tag;
*///?}
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
//? if >=1.21.5 {
import net.minecraft.core.component.DataComponentGetter;
//?}
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
//? if >=1.21.6 {
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
//?}
import net.minecraft.world.phys.Vec3;
import space.anatomyuniverse.musavacca.block.custom.VocoPostBlock;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoReceptorLogic.ReceptorPosition;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoReceptorLogic;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoTeleportLogic;
import space.anatomyuniverse.musavacca.block.entity.ModBlockEntities;
import space.anatomyuniverse.musavacca.component.ModDataComponents;
import space.anatomyuniverse.musavacca.tint.MusavaccaTints.HexSource;
import space.anatomyuniverse.musavacca.tint.MusavaccaTints;

public class VocoPostBlockEntity extends BlockEntity implements HexSource {
    private static final String TAG_YAW_DEGREES = "yaw_degrees";
    private static final String TAG_PITCH_DEGREES = "pitch_degrees";
    private static final String TAG_CUSTOM_TARGET = "custom_target";
    private static final String TAG_TARGET_X = "target_x";
    private static final String TAG_TARGET_Y = "target_y";
    private static final String TAG_TARGET_Z = "target_z";
    public static final int DEFAULT_YAW_DEGREES = ReceptorPosition.NORTH_EAST.defaultYawDegrees();
    public static final int DEFAULT_PITCH_DEGREES = ReceptorPosition.NORTH_EAST.defaultPitchDegrees();
    private int yawDegrees = DEFAULT_YAW_DEGREES;
    private int pitchDegrees = DEFAULT_PITCH_DEGREES;
    private UUID deviceId = UUID.randomUUID();

    public UUID getDeviceId() { return this.deviceId; }

    public void setDeviceId(UUID deviceId) {
        if (deviceId == null || deviceId.equals(this.deviceId)) return;
        this.deviceId = deviceId;
        this.setChanged();
    }

    private int hexColor = MusavaccaTints.NO_TINT;
    private boolean customTargetEnabled = false;
    private String customTargetDimension = "";
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
        return this.hexColor != MusavaccaTints.NO_TINT;
    }

    @Override
    public int getHexColor() {
        return this.hexColor;
    }

    public boolean isCustomTargetEnabled() {
        return this.customTargetEnabled;
    }

    public String getCustomTargetDimension() {
        if (!this.customTargetDimension.isEmpty()) return this.customTargetDimension;
        if (this.level == null) return "minecraft:overworld";
        //? if >=1.21.11 {
        /*return this.level.dimension().identifier().toString();
        *///?} else {
        return this.level.dimension().location().toString();
        //?}
    }

    public Vec3 getCustomTarget() {
        if (!this.customTargetEnabled) return VocoTeleportLogic.getDefaultTeleportPosition(this.getBlockPos(), this.getPostReceptor());
        return new Vec3(this.targetX, this.targetY, this.targetZ);
    }

    public void setCustomTarget(Vec3 target, int yawDegrees, int pitchDegrees) {
        this.customTargetDimension = "";
        this.customTargetEnabled = true;
        this.customTargetDimension = this.getCustomTargetDimension();
        this.setTarget(target);
        this.setFacingRaw(yawDegrees, pitchDegrees);
        this.changed();
    }

    public void resetCustomTarget() {
        ReceptorPosition receptor = this.getPostReceptor();
        Vec3 fallback = VocoTeleportLogic.getDefaultTeleportPosition(this.getBlockPos(), receptor);
        this.customTargetEnabled = false;
        this.customTargetDimension = "";
        this.setTarget(fallback);
        this.setFacingRaw(receptor.defaultYawDegrees(), receptor.defaultPitchDegrees());
        this.changed();
    }

    public boolean setHexColor(int hexColor) {
        int normalized = MusavaccaTints.resolve(hexColor);
        if (this.hexColor == normalized) return true;
        this.hexColor = normalized;
        this.changed();
        return true;
    }

    public void clearHexColor() {
        if (this.hexColor == MusavaccaTints.NO_TINT) return;
        this.hexColor = MusavaccaTints.NO_TINT;
        this.markChangedAndSync();
        this.releaseHexClaim();
    }

    public void releaseHexClaim() {
        Level level = this.getLevel();
        if (level instanceof ServerLevel serverLevel) {
            HexTeleportAddressNetwork.releaseOwner(serverLevel.getServer(), HexTeleportDirectory.vocoPostReceptorCornerOwnerKey(this.deviceId));
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
        if (!(level instanceof ServerLevel serverLevel) || !this.hasHexColor()) return;
        BlockState state = this.getBlockState();
        if (!state.hasProperty(VocoPostBlock.PORTAL) || !state.getValue(VocoPostBlock.PORTAL)) return;
        VocoTeleportLogic.syncEndpointDetailed(serverLevel, this.getBlockPos(), this.getPostReceptor(), true, this.hexColor);
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
        if (level == null) return;
        BlockPos pos = this.getBlockPos();
        BlockState state = this.getBlockState();
        level.sendBlockUpdated(pos, state, state, Block.UPDATE_CLIENTS | Block.UPDATE_IMMEDIATE);
    }

    private void rerenderClientNow() {
        Level level = this.getLevel();
        if (level != null && level.isClientSide()) this.syncToClientAndRerender();
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (this.level instanceof ServerLevel serverLevel) {
            VocoPostCandleLogic.refreshPortalAt(serverLevel, this.getBlockPos());
        }
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
    @Override
    //? if >=1.21.6 {
    protected void loadAdditional(ValueInput input) {
    //?} else {
    /*protected void loadAdditional(CompoundTag input, HolderLookup.Provider provider) {
    *///?}
        //? if >=1.21.6 {
        super.loadAdditional(input);
        //?} else {
        /*super.loadAdditional(input, provider);
        *///?}
        //? if >=1.21.5 {
        this.deviceId = TeleportDeviceIdentity.read(input.getStringOr("musavacca_device_id", ""));
        //?} else {
        /*this.deviceId = TeleportDeviceIdentity.read(input.contains("musavacca_device_id", Tag.TAG_STRING) ? input.getString("musavacca_device_id") : "");
        *///?}
        ReceptorPosition receptor = this.getPostReceptor();
        Vec3 fallback = VocoTeleportLogic.getDefaultTeleportPosition(this.getBlockPos(), receptor);
        //? if >=1.21.5 {
        this.yawDegrees = clampYaw(input.getIntOr(TAG_YAW_DEGREES, receptor.defaultYawDegrees()));
        this.pitchDegrees = clampPitch(input.getIntOr(TAG_PITCH_DEGREES, receptor.defaultPitchDegrees()));
        //?} else {
        /*this.yawDegrees = clampYaw((input.contains(TAG_YAW_DEGREES, Tag.TAG_ANY_NUMERIC) ? input.getInt(TAG_YAW_DEGREES) : receptor.defaultYawDegrees()));
        this.pitchDegrees = clampPitch(
                (input.contains(TAG_PITCH_DEGREES, Tag.TAG_ANY_NUMERIC) ? input.getInt(TAG_PITCH_DEGREES) : receptor.defaultPitchDegrees()));
        *///?}
        this.hexColor = readHexOrUnset(input);
        //? if >=1.21.5 {
        this.customTargetEnabled = input.getBooleanOr(TAG_CUSTOM_TARGET, false);
        this.targetX = input.getDoubleOr(TAG_TARGET_X, fallback.x);
        this.targetY = input.getDoubleOr(TAG_TARGET_Y, fallback.y);
        this.targetZ = input.getDoubleOr(TAG_TARGET_Z, fallback.z);
        //?} else {
        /*this.customTargetEnabled = (input.contains(TAG_CUSTOM_TARGET, Tag.TAG_ANY_NUMERIC) ? input.getBoolean(TAG_CUSTOM_TARGET) : false);
        this.targetX = (input.contains(TAG_TARGET_X, Tag.TAG_ANY_NUMERIC) ? input.getDouble(TAG_TARGET_X) : fallback.x);
        this.targetY = (input.contains(TAG_TARGET_Y, Tag.TAG_ANY_NUMERIC) ? input.getDouble(TAG_TARGET_Y) : fallback.y);
        this.targetZ = (input.contains(TAG_TARGET_Z, Tag.TAG_ANY_NUMERIC) ? input.getDouble(TAG_TARGET_Z) : fallback.z);
        *///?}
        //? if >=1.21.5 {
        this.customTargetDimension = input.getStringOr("custom_target_dimension", "");
        //?} else {
        /*this.customTargetDimension = input.contains("custom_target_dimension", Tag.TAG_STRING) ? input.getString("custom_target_dimension") : "";
        *///?}
    }

    @Override
    //? if >=1.21.6 {
    protected void saveAdditional(ValueOutput output) {
    //?} else {
    /*protected void saveAdditional(CompoundTag output, HolderLookup.Provider provider) {
    *///?}
        //? if >=1.21.6 {
        super.saveAdditional(output);
        //?} else {
        /*super.saveAdditional(output, provider);
        *///?}
        output.putString("musavacca_device_id", this.deviceId.toString());
        if (this.hasHexColor()) {
            output.putInt(MusavaccaTints.HEX_COLOR_KEY, this.hexColor);
        }
        if (this.customTargetEnabled) {
            output.putInt(TAG_YAW_DEGREES, this.yawDegrees);
            output.putInt(TAG_PITCH_DEGREES, this.pitchDegrees);
            output.putBoolean(TAG_CUSTOM_TARGET, true);
            output.putDouble(TAG_TARGET_X, this.targetX);
            output.putDouble(TAG_TARGET_Y, this.targetY);
            output.putDouble(TAG_TARGET_Z, this.targetZ);
            output.putString("custom_target_dimension", this.getCustomTargetDimension());
        }
    }

    @Override
    //? if >=1.21.5 {
    protected void applyImplicitComponents(DataComponentGetter input) {
    //?} else {
    /*protected void applyImplicitComponents(DataComponentInput input) {
    *///?}
        super.applyImplicitComponents(input);
        Integer savedHex = input.get(ModDataComponents.HEX_COLOR.get());
        this.hexColor = savedHex == null ? MusavaccaTints.NO_TINT : MusavaccaTints.stored(savedHex);
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
    //? if >=1.21.6 {
    public void handleUpdateTag(ValueInput input) {
    //?} else {
    /*public void handleUpdateTag(CompoundTag input, HolderLookup.Provider provider) {
    *///?}
        //? if >=1.21.6 {
        super.handleUpdateTag(input);
        //?} else {
        /*super.handleUpdateTag(input, provider);
        *///?}
        this.rerenderClientNow();
    }

    @Override
    //? if >=1.21.6 {
    public void onDataPacket(Connection connection, ValueInput input) {
    //?} else {
    /*public void onDataPacket(Connection connection, ClientboundBlockEntityDataPacket packet, HolderLookup.Provider provider) {
    *///?}
        //? if >=1.21.6 {
        super.onDataPacket(connection, input);
        //?} else {
        /*super.onDataPacket(connection, packet, provider);
        *///?}
        this.rerenderClientNow();
    }

    public static int clampYaw(int yawDegrees) {
        return VocoReceptorLogic.clampYaw(yawDegrees);
    }

    public static int clampPitch(int pitchDegrees) {
        return VocoReceptorLogic.clampPitch(pitchDegrees);
    }

    //? if >=1.21.6 {
    private static int readHexOrUnset(ValueInput input) {
    //?} else {
    /*private static int readHexOrUnset(CompoundTag input) {
    *///?}
        //? if >=1.21.5 {
        int loaded = input.getIntOr(MusavaccaTints.HEX_COLOR_KEY, MusavaccaTints.NO_TINT);
        //?} else {
        /*int loaded = (input.contains(MusavaccaTints.HEX_COLOR_KEY, Tag.TAG_ANY_NUMERIC) ? input.getInt(MusavaccaTints.HEX_COLOR_KEY) : MusavaccaTints.NO_TINT);
        *///?}
        return MusavaccaTints.stored(loaded);
    }
}
