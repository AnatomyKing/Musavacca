package space.anatomyuniverse.musavacca.block.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
//? if >=1.21.5 {

import net.minecraft.core.component.DataComponentGetter;
//?}

import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
//? if >=1.21.6 {

import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
//?}

import space.anatomyuniverse.musavacca.block.entity.ModBlockEntities;
import space.anatomyuniverse.musavacca.component.ModDataComponents;
import space.anatomyuniverse.musavacca.tint.MusavaccaTints.HexSource;
import space.anatomyuniverse.musavacca.tint.MusavaccaTints;

public class HardHexBlockEntity extends BlockEntity implements HexSource {
    public HardHexBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.HARD_HEX_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public int getHexColor() {
        return MusavaccaTints.DEFAULT_TINT;
    }

    public void setHexColor(int ignoredHexColor) {
    }

    @Override
    //? if >=1.21.6 {
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
    //?} else {
    /*protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
    *///?}
    }

    @Override
    //? if >=1.21.6 {
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt(MusavaccaTints.HEX_COLOR_KEY, MusavaccaTints.DEFAULT_TINT);
    //?} else {
    /*protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt(MusavaccaTints.HEX_COLOR_KEY, MusavaccaTints.DEFAULT_TINT);
    *///?}
    }

    @Override
    //? if >=1.21.5 {
    protected void applyImplicitComponents(DataComponentGetter input) {
    //?} else {
    /*protected void applyImplicitComponents(BlockEntity.DataComponentInput input) {
    *///?}
        super.applyImplicitComponents(input);
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        components.set(ModDataComponents.HEX_COLOR.get(), MusavaccaTints.DEFAULT_TINT);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        return this.saveWithoutMetadata(provider);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
