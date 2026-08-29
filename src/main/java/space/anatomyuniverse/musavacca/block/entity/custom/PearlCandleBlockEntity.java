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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
//? if >=1.21.6
import net.minecraft.world.level.storage.ValueInput;
//? if >=1.21.6
import net.minecraft.world.level.storage.ValueOutput;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoPostCandleLogic;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoReceptorLogic;
import space.anatomyuniverse.musavacca.block.entity.ModBlockEntities;
import space.anatomyuniverse.musavacca.component.ModDataComponents;
import space.anatomyuniverse.musavacca.item.custom.FlintAndPearlItem;

public class PearlCandleBlockEntity extends BlockEntity {
    public static final String TAG_HEX_COLOR = "hex_color";

    public static final int UNSET_HEX_COLOR = VocoReceptorLogic.UNSET_HEX_COLOR;

    private int hexColor = UNSET_HEX_COLOR;

    public PearlCandleBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PEARL_CANDLE_BLOCK_ENTITY.get(), pos, state);
    }

    public boolean hasHexColor() {
        return this.hexColor != UNSET_HEX_COLOR;
    }

    public int getHexColor() {
        return this.hexColor;
    }

    public int getHexColorOrFallback() {
        return this.hasHexColor()
                ? this.hexColor
                : FlintAndPearlItem.DEFAULT_HEX_COLOR;
    }

    public void setHexColor(int hexColor) {
        int normalized = normalizeHex(hexColor);

        if (this.hexColor == normalized) {
            return;
        }

        this.hexColor = normalized;
        this.markChangedAndSync();
        this.refreshPostBelow();
    }

    private void refreshPostBelow() {
        Level level = this.getLevel();
        if (level == null || level.isClientSide()) {
            return;
        }

        VocoPostCandleLogic.refreshPostBelowCandle(level, this.getBlockPos());
    }

    private void markChangedAndSync() {
        this.setChanged();

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

    //? if >=1.21.6 {
    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.hexColor = readHexOrUnset(input);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);

        if (this.hasHexColor()) {
            output.putInt(TAG_HEX_COLOR, this.hexColor);
        }
    }
    //?} else {
    /*@Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        this.hexColor = readHexOrUnset(tag);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);

        if (this.hasHexColor()) {
            tag.putInt(TAG_HEX_COLOR, this.hexColor);
        }
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

    //? if >=1.21.6 {
    private static int readHexOrUnset(ValueInput input) {
        int loaded = input.getIntOr(TAG_HEX_COLOR, UNSET_HEX_COLOR);
        return loaded == UNSET_HEX_COLOR ? UNSET_HEX_COLOR : normalizeHex(loaded);
    }
    //?} else {
    /*private static int readHexOrUnset(CompoundTag tag) {
        int loaded = getIntOr(tag, TAG_HEX_COLOR, UNSET_HEX_COLOR);
        return loaded == UNSET_HEX_COLOR ? UNSET_HEX_COLOR : normalizeHex(loaded);
    }

    private static int getIntOr(CompoundTag tag, String key, int fallback) {
        //? if >=1.21.5
        return tag.getIntOr(key, fallback);
        //? if <1.21.5
        //return tag.contains(key) ? tag.getInt(key) : fallback;
    }
    *///?}

    private static int normalizeHex(int hexColor) {
        return VocoReceptorLogic.normalizeHex(hexColor);
    }
}



