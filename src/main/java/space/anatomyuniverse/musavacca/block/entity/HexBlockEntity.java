// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/block/entity/HexBlockEntity.java
package space.anatomyuniverse.musavacca.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
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
import space.anatomyuniverse.musavacca.tint.HexColorLcg;

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

    public void applyClientPredictionIfPresent() {
        if (this.hasHexColor()) {
            return;
        }

        int predicted = HexColorLcg.getClientPlacementPrediction(this.getBlockPos());
        if (predicted != HexColorLcg.NO_COLOR) {
            this.setHexColor(predicted);
        }
    }

    public void initializeServerFallbackColorIfNeeded() {
        if (!this.hasHexColor()) {
            this.setHexColor(createFallbackRandomHexColor());
        }
    }

    public void applyServerPredictedPlacementColor() {
        this.setHexColor(HexColorLcg.nextServerHexColor());
    }

    public void setHexColor(int hexColor) {
        int normalized = normalizeHex(hexColor);
        if (this.hexColor == normalized) {
            return;
        }

        this.hexColor = normalized;
        this.setChanged();

        Level level = this.getLevel();
        if (level != null && !level.isClientSide()) {
            this.syncToClient();
        }
    }

    private void syncToClient() {
        Level level = this.getLevel();
        if (level == null || level.isClientSide()) {
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

    public static int normalizeHex(int hexColor) {
        return hexColor & 0xFFFFFF;
    }

    public static int createFallbackRandomHexColor() {
        return ThreadLocalRandom.current().nextInt(0x1000000);
    }

    @Override
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
        this.refreshClientRender();
    }

    @Override
    public void onDataPacket(Connection connection, ValueInput input) {
        super.onDataPacket(connection, input);
        this.refreshClientRender();
    }

    private void refreshClientRender() {
        Level level = this.getLevel();
        if (level == null || !level.isClientSide()) {
            return;
        }

        BlockPos pos = this.getBlockPos();
        BlockState state = this.getBlockState();

        HexColorLcg.clearClientPrediction(pos);

        level.sendBlockUpdated(
                pos,
                state,
                state,
                Block.UPDATE_CLIENTS | Block.UPDATE_IMMEDIATE
        );
    }
}