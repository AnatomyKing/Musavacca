package space.anatomyuniverse.musavacca.block.entity.custom;

import java.util.UUID;

//? if <1.21.5 {
/*import net.minecraft.nbt.Tag;
*///?}
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
//? if >=1.21.5 {
import net.minecraft.core.component.DataComponentGetter;
//?}
import net.minecraft.server.level.ServerLevel;
import space.anatomyuniverse.musavacca.trapdoor.MusavaccaTrapdoorTeleportNetwork;
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
//? if >=1.21.6 {
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
//?}
import space.anatomyuniverse.musavacca.block.custom.MusavaccaPortalTrapdoorBlock;
import space.anatomyuniverse.musavacca.block.entity.ModBlockEntities;
import space.anatomyuniverse.musavacca.component.ModDataComponents;
import space.anatomyuniverse.musavacca.tint.MusavaccaTints.HexSource;
import space.anatomyuniverse.musavacca.tint.MusavaccaTints;

public final class MusavaccaPortalTrapdoorBlockEntity
        extends BlockEntity implements HexSource {
    private UUID deviceId = UUID.randomUUID();
    public UUID getDeviceId() { return this.deviceId; }
    public void setDeviceId(UUID deviceId) {
        if (deviceId == null || deviceId.equals(this.deviceId)) return;
        this.deviceId = deviceId;
        this.setChanged();
    }

    private int hexColor = MusavaccaTints.NO_TINT;
    public MusavaccaPortalTrapdoorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MUSAVACCA_TRAPDOOR_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public int getHexColor() {
        return this.hexColor;
    }

    public boolean hasHexColor() {
        return this.hexColor != MusavaccaTints.NO_TINT;
    }

    public void setHexColor(int hexColor) {
        int resolved = hexColor == MusavaccaTints.NO_TINT ? MusavaccaTints.NO_TINT : MusavaccaTints.resolve(hexColor);
        if (this.hexColor == resolved) return;
        this.hexColor = resolved;
        this.setChanged();
        this.syncTrapdoor(true);
    }

    public void clearHexColor() {
        this.setHexColor(MusavaccaTints.NO_TINT);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        this.syncTrapdoor(false);
    }

    public void cleanupBeforeRemoval() {
        if (
                this.level
                        instanceof ServerLevel serverLevel
        ) {
            MusavaccaTrapdoorTeleportNetwork.removeTrapdoor(serverLevel, this.getDeviceId());
        }
    }

    //? if >=1.21.5 {
    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        this.cleanupBeforeRemoval();
        super.preRemoveSideEffects(pos, state);
    }
    //?}
    private void syncTrapdoor(boolean playPortalSound) {
        Level level = this.getLevel();
        if (level == null) return;
        if (!level.isClientSide()) {
            MusavaccaPortalTrapdoorBlock.synchronizePearlState(level, this.getBlockPos(), playPortalSound);
            return;
        }
        this.rerenderClientTrapdoor();
    }

    private void rerenderClientTrapdoor() {
        Level level = this.getLevel();
        if (
                level == null
                        || !level.isClientSide()
        ) {
            return;
        }
        rerenderPosition(level, this.getBlockPos());
    }

    private void rerenderPosition(Level level, BlockPos pos) {
        BlockState targetState = level.getBlockState(pos);
        if (
                targetState.getBlock()
                        != this.getBlockState().getBlock()
        ) {
            return;
        }
        level.sendBlockUpdated(pos, targetState, targetState, Block.UPDATE_CLIENTS | Block.UPDATE_IMMEDIATE);
    }

    @Override
    //? if >=1.21.6 {
    protected void loadAdditional(ValueInput input) {
    //?} else {
    /*protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
    *///?}
        //? if >=1.21.6 {
        super.loadAdditional(input);
        //?} else {
        /*super.loadAdditional(tag, registries);
        *///?}
        //? if >=1.21.6 {
        this.deviceId = space.anatomyuniverse.musavacca.teleport.TeleportDeviceIdentity.read(input.getStringOr("musavacca_device_id", ""));
        //?} else if >=1.21.5 <1.21.6 {
        /*this.deviceId = space.anatomyuniverse.musavacca.teleport.TeleportDeviceIdentity.read(tag.getStringOr("musavacca_device_id", ""));
        *///?} else {
        /*this.deviceId = space.anatomyuniverse.musavacca.teleport.TeleportDeviceIdentity.read(tag.contains("musavacca_device_id", Tag.TAG_STRING) ? tag.getString("musavacca_device_id") : "");
        *///?}
        //? if >=1.21.6 {
        int loaded = input.getIntOr(MusavaccaTints.HEX_COLOR_KEY, MusavaccaTints.NO_TINT);
        //?} else if >=1.21.5 <1.21.6 {
        /*int loaded = tag.getIntOr(MusavaccaTints.HEX_COLOR_KEY, MusavaccaTints.NO_TINT);
        *///?} else {
        /*int loaded = (tag.contains(MusavaccaTints.HEX_COLOR_KEY, Tag.TAG_ANY_NUMERIC) ? tag.getInt(MusavaccaTints.HEX_COLOR_KEY) : MusavaccaTints.NO_TINT);
        *///?}
        this.hexColor = loaded == MusavaccaTints.NO_TINT ? MusavaccaTints.NO_TINT : MusavaccaTints.stored(loaded);
    }

    @Override
    //? if >=1.21.6 {
    protected void saveAdditional(ValueOutput output) {
    //?} else {
    /*protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
    *///?}
        //? if >=1.21.6 {
        super.saveAdditional(output);
        //?} else {
        /*super.saveAdditional(tag, registries);
        *///?}
        //? if >=1.21.6 {
        output.putString("musavacca_device_id", this.deviceId.toString());
        //?} else {
        /*tag.putString("musavacca_device_id", this.deviceId.toString());
        *///?}
        if (this.hasHexColor()) {
            //? if >=1.21.6 {
            output.putInt(MusavaccaTints.HEX_COLOR_KEY, this.hexColor);
            //?} else {
            /*tag.putInt(MusavaccaTints.HEX_COLOR_KEY, this.hexColor);
            *///?}
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
    public Packet<ClientGamePacketListener>
    getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    //? if >=1.21.6 {
    public void handleUpdateTag(ValueInput input) {
    //?} else {
    /*public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider registries) {
    *///?}
        //? if >=1.21.6 {
        super.handleUpdateTag(input);
        //?} else {
        /*super.handleUpdateTag(tag, registries);
        *///?}
        this.rerenderClientTrapdoor();
    }

    @Override
    //? if >=1.21.6 {
    public void onDataPacket(Connection connection, ValueInput input) {
    //?} else {
    /*public void onDataPacket(Connection connection, ClientboundBlockEntityDataPacket packet, HolderLookup.Provider registries) {
    *///?}
        //? if >=1.21.6 {
        super.onDataPacket(connection, input);
        //?} else {
        /*super.onDataPacket(connection, packet, registries);
        *///?}
        this.rerenderClientTrapdoor();
    }
}
