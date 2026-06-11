
package space.anatomyuniverse.musavacca.block.entity.custom;

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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
//? if <1.21.6 {
//?} else {
/*import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
*///?}
import space.anatomyuniverse.musavacca.block.entity.ModBlockEntities;
import space.anatomyuniverse.musavacca.component.ModDataComponents;

import java.util.concurrent.ThreadLocalRandom;

public class HexBlockEntity extends BlockEntity {

    public static final String TAG_HEX_COLOR = "hex_color";
    public static final int UNSET_HEX_COLOR = -1;

    private int hexColor = UNSET_HEX_COLOR;

    public HexBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.HEX_BLOCK_ENTITY.get(), pos, state);
    }

    public int getHexColor() {
        return this.hexColor;
    }

    public boolean hasHexColor() {
        return this.hexColor != UNSET_HEX_COLOR;
    }

    public void setHexColor(int hexColor) {
        int normalized = normalizeHex(hexColor);
        if (this.hexColor == normalized) {
            return;
        }

        this.hexColor = normalized;
        this.setChanged();
        this.syncToClientAndRerender();
    }

    public void ensureRandomHexColor() {
        if (!this.hasHexColor()) {
            this.setHexColor(createRandomHexColor());
        }
    }

    public static int createRandomHexColor() {
        return ThreadLocalRandom.current().nextInt(0x1000000);
    }

    private static int normalizeHex(int hexColor) {
        return hexColor & 0xFFFFFF;
    }

    private static int readIntOr(CompoundTag tag, String key, int fallback) {
        //? if <1.21.5 {
        /*return tag.contains(key) ? tag.getInt(key) : fallback;
        *///?} else {
        return tag.getIntOr(key, fallback);
        //?}
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

    //? if <1.21.6 {
    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        int loaded = readIntOr(tag, TAG_HEX_COLOR, UNSET_HEX_COLOR);
        this.hexColor = loaded == UNSET_HEX_COLOR ? UNSET_HEX_COLOR : normalizeHex(loaded);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        if (this.hasHexColor()) {
            tag.putInt(TAG_HEX_COLOR, this.hexColor);
        }
    }
    //?} else {
    /*@Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);

        int loaded = input.getIntOr(TAG_HEX_COLOR, UNSET_HEX_COLOR);
        this.hexColor = loaded == UNSET_HEX_COLOR ? UNSET_HEX_COLOR : normalizeHex(loaded);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);

        if (this.hasHexColor()) {
            output.putInt(TAG_HEX_COLOR, this.hexColor);
        }
    }
    *///?}

    //? if <1.21.5 {
    /*@Override
    protected void applyImplicitComponents(BlockEntity.DataComponentInput input) {
        super.applyImplicitComponents(input);

        Integer savedHex = input.get(ModDataComponents.HEX_COLOR.get());
        if (savedHex != null) {
            this.hexColor = normalizeHex(savedHex);
        }
    }
    *///?} else {
    @Override
    protected void applyImplicitComponents(DataComponentGetter input) {
        super.applyImplicitComponents(input);

        Integer savedHex = input.get(ModDataComponents.HEX_COLOR.get());
        if (savedHex != null) {
            this.hexColor = normalizeHex(savedHex);
        }
    }
    //?}

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

    //? if <1.21.6 {
    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider registries) {
        super.handleUpdateTag(tag, registries);
        this.rerenderClientNow();
    }

    @Override
    public void onDataPacket(Connection connection, ClientboundBlockEntityDataPacket packet, HolderLookup.Provider registries) {
        super.onDataPacket(connection, packet, registries);
        this.rerenderClientNow();
    }
    //?} else {
    /*@Override
    public void handleUpdateTag(ValueInput input) {
        super.handleUpdateTag(input);
        this.rerenderClientNow();
    }

    @Override
    public void onDataPacket(Connection connection, ValueInput input) {
        super.onDataPacket(connection, input);
        this.rerenderClientNow();
    }
    *///?}
}