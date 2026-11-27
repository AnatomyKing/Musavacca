package space.anatomyuniverse.musavacca.block.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.Nullable;
import net.minecraft.world.level.block.state.BlockState;
import space.anatomyuniverse.musavacca.block.custom.PearlCopycatBlock;
import space.anatomyuniverse.musavacca.block.custom.PearlFlameBlock;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoPostCandleLogic;
import space.anatomyuniverse.musavacca.component.ModDataComponents;
import space.anatomyuniverse.musavacca.tint.MusavaccaTints.HexSource;
import space.anatomyuniverse.musavacca.tint.MusavaccaTints;

//? if <1.21.5 {
/*import net.minecraft.nbt.Tag;
*///?}
//? if >=1.21.5 {
import net.minecraft.core.component.DataComponentGetter;
//?}
//? if >=1.21.6 {
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
//?}

public class PearlCopycatBlockEntity extends BlockEntity implements HexSource {
    private static final String ORIGINAL_STATE_KEY = "original_state";
    private BlockState originalState;
    private int hexColor = MusavaccaTints.NO_TINT;

    public PearlCopycatBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Nullable
    public BlockEntity originalBlockEntity() {
        return null;
    }

    public boolean restoreOriginal(Level level, BlockState original) {
        return level.setBlock(getBlockPos(), original, Block.UPDATE_ALL);
    }

    public BlockState originalState() {
        BlockState carrier = getBlockState();
        PearlCopycatBlock block = (PearlCopycatBlock) carrier.getBlock();
        return block.resolveOriginalState(originalState != null ? originalState : block.fallbackState(carrier), carrier);
    }

    public void setOriginalState(BlockState state) {
        this.setCopycat(state, this.hexColor);
    }

    public void setCopycat(BlockState state, int hexColor) {
        if (state.getBlock() instanceof PearlCopycatBlock) return;
        this.originalState = state;
        this.hexColor = MusavaccaTints.resolve(hexColor);
        if (getLevel() != null) getLevel().getChunkSource().getLightEngine().checkBlock(getBlockPos());
        this.markChangedAndSync();
    }

    private void loadOriginal(BlockState state) {
        this.originalState = state != null && !(state.getBlock() instanceof PearlCopycatBlock) ? state : null;
    }

    @Override
    public int getHexColor() {
        return this.hexColor;
    }

    public void setHexColor(int hexColor) {
        int normalized = MusavaccaTints.resolve(hexColor);
        if (this.hexColor == normalized) return;
        this.hexColor = normalized;
        this.markChangedAndSync();
        if (getBlockState().getBlock() instanceof PearlFlameBlock)
            VocoPostCandleLogic.refreshPostBelowCandle(getLevel(), getBlockPos());
    }

    protected void markChangedAndSync() {
        this.setChanged();
        Level level = this.getLevel();
        if (level == null) return;
        BlockPos pos = this.getBlockPos();
        BlockState state = this.getBlockState();
        level.sendBlockUpdated(pos, state, state, Block.UPDATE_CLIENTS | Block.UPDATE_IMMEDIATE);
    }

    private void rerenderClientNow() {
        Level level = this.getLevel();
        if (level == null || !level.isClientSide()) return;
        BlockPos pos = this.getBlockPos();
        BlockState state = this.getBlockState();
        level.getChunkSource().getLightEngine().checkBlock(pos);
        level.sendBlockUpdated(pos, state, state, Block.UPDATE_CLIENTS | Block.UPDATE_IMMEDIATE);
    }

    @Override
    //? if >=1.21.6 {
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.hexColor = readHexOrUnset(input);
        loadOriginal(input.read(ORIGINAL_STATE_KEY, BlockState.CODEC).orElse(null));
    //?} else {
    /*protected void loadAdditional(CompoundTag input, HolderLookup.Provider provider) {
        super.loadAdditional(input, provider);
        this.hexColor = readHexOrUnset(input);
        loadOriginal(input.get(ORIGINAL_STATE_KEY) == null ? null
                : BlockState.CODEC.parse(NbtOps.INSTANCE, input.get(ORIGINAL_STATE_KEY)).result().orElse(null));
    *///?}
    }

    @Override
    //? if >=1.21.6 {
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
    //?} else {
    /*protected void saveAdditional(CompoundTag output, HolderLookup.Provider provider) {
        super.saveAdditional(output, provider);
    *///?}
        if (originalState != null) {
            //? if >=1.21.6 {
            output.store(ORIGINAL_STATE_KEY, BlockState.CODEC, originalState);
            //?} else {
            /*BlockState.CODEC.encodeStart(NbtOps.INSTANCE, originalState).result().ifPresent(value -> output.put(ORIGINAL_STATE_KEY, value));
            *///?}
        }
        if (this.hasHexColor()) {
            output.putInt(MusavaccaTints.HEX_COLOR_KEY, this.hexColor);
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
        super.handleUpdateTag(input);
    //?} else {
    /*public void handleUpdateTag(CompoundTag input, HolderLookup.Provider provider) {
        super.handleUpdateTag(input, provider);
    *///?}
        this.rerenderClientNow();
    }

    @Override
    //? if >=1.21.6 {
    public void onDataPacket(Connection connection, ValueInput input) {
        super.onDataPacket(connection, input);
    //?} else {
    /*public void onDataPacket(Connection connection, ClientboundBlockEntityDataPacket packet, HolderLookup.Provider provider) {
        super.onDataPacket(connection, packet, provider);
    *///?}
        this.rerenderClientNow();
    }

    //? if >=1.21.6 {
    private static int readHexOrUnset(ValueInput input) {
    //?} else {
    /*private static int readHexOrUnset(CompoundTag input) {
    *///?}
        //? if >=1.21.5 {
        int loaded = input.getIntOr(MusavaccaTints.HEX_COLOR_KEY, MusavaccaTints.NO_TINT);
        //?} else {
        /*int loaded =
                (input.contains(MusavaccaTints.HEX_COLOR_KEY, Tag.TAG_ANY_NUMERIC)
                        ? input.getInt(MusavaccaTints.HEX_COLOR_KEY)
                        : MusavaccaTints.NO_TINT);
        *///?}
        return MusavaccaTints.stored(loaded);
    }
}
