// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/block/entity/custom/VocoPearlCandleBlockEntity.java
package space.anatomyuniverse.musavacca.block.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;
import space.anatomyuniverse.musavacca.block.entity.ModBlockEntities;
import space.anatomyuniverse.musavacca.component.ModDataComponents;
import space.anatomyuniverse.musavacca.item.custom.FlintAndPearlItem;

public class VocoPearlCandleBlockEntity extends BlockEntity {
    private static final String TAG_HEX_COLOR = "hex_color";
    private static final String TAG_CANDLE_BLOCK = "candle_block";
    private static final String TAG_TABLE_X = "table_x";
    private static final String TAG_TABLE_Y = "table_y";
    private static final String TAG_TABLE_Z = "table_z";
    private static final String TAG_HAS_TABLE_POS = "has_table_pos";

    private int hexColor = FlintAndPearlItem.DEFAULT_HEX_COLOR;
    private ResourceLocation candleBlockId = BuiltInRegistries.BLOCK.getKey(Blocks.CANDLE);

    private boolean hasTablePos = false;
    private BlockPos tablePos = BlockPos.ZERO;

    public VocoPearlCandleBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.VOCO_PEARL_CANDLE_BLOCK_ENTITY.get(), pos, state);
    }

    public boolean hasHexColor() {
        return true;
    }

    public int getHexColor() {
        return this.hexColor;
    }

    public void setHexColor(int hexColor) {
        int normalized = normalizeHex(hexColor);
        if (this.hexColor == normalized) {
            return;
        }

        this.hexColor = normalized;
        this.markChangedAndSync();
    }

    public Block getCandleBlock() {
        if (this.candleBlockId == null) {
            return Blocks.CANDLE;
        }

        Block block = BuiltInRegistries.BLOCK.getValue(this.candleBlockId);
        return block == null ? Blocks.CANDLE : block;
    }

    public void setCandleBlock(Block candleBlock) {
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(candleBlock);

        if (id == null) {
            id = BuiltInRegistries.BLOCK.getKey(Blocks.CANDLE);
        }

        if (id != null && id.equals(this.candleBlockId)) {
            return;
        }

        this.candleBlockId = id;
        this.markChangedAndSync();
    }

    public boolean isSameCandleBlock(Block candleBlock) {
        return this.getCandleBlock() == candleBlock;
    }

    public boolean hasTablePos() {
        return this.hasTablePos;
    }

    public BlockPos getTablePos() {
        return this.tablePos;
    }

    public void setTablePos(BlockPos tablePos) {
        if (tablePos == null) {
            return;
        }

        if (this.hasTablePos && this.tablePos.equals(tablePos)) {
            return;
        }

        this.hasTablePos = true;
        this.tablePos = tablePos.immutable();
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

        this.hexColor = normalizeHex(input.getIntOr(TAG_HEX_COLOR, FlintAndPearlItem.DEFAULT_HEX_COLOR));
        this.candleBlockId = readIdOrDefault(
                input.getStringOr(TAG_CANDLE_BLOCK, ""),
                BuiltInRegistries.BLOCK.getKey(Blocks.CANDLE)
        );

        this.hasTablePos = input.getBooleanOr(TAG_HAS_TABLE_POS, false);
        if (this.hasTablePos) {
            this.tablePos = new BlockPos(
                    input.getIntOr(TAG_TABLE_X, 0),
                    input.getIntOr(TAG_TABLE_Y, 0),
                    input.getIntOr(TAG_TABLE_Z, 0)
            );
        } else {
            this.tablePos = BlockPos.ZERO;
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);

        output.putInt(TAG_HEX_COLOR, this.hexColor);

        if (this.candleBlockId != null) {
            output.putString(TAG_CANDLE_BLOCK, this.candleBlockId.toString());
        }

        output.putBoolean(TAG_HAS_TABLE_POS, this.hasTablePos);

        if (this.hasTablePos) {
            output.putInt(TAG_TABLE_X, this.tablePos.getX());
            output.putInt(TAG_TABLE_Y, this.tablePos.getY());
            output.putInt(TAG_TABLE_Z, this.tablePos.getZ());
        }
    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter input) {
        super.applyImplicitComponents(input);

        Integer savedHex = input.get(ModDataComponents.HEX_COLOR.get());
        if (savedHex != null) {
            this.hexColor = normalizeHex(savedHex);
        }
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        components.set(ModDataComponents.HEX_COLOR.get(), this.hexColor);
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

    @Nullable
    private static ResourceLocation readIdOrDefault(String raw, ResourceLocation fallback) {
        if (raw == null || raw.isBlank()) {
            return fallback;
        }

        ResourceLocation parsed = ResourceLocation.tryParse(raw);
        return parsed == null ? fallback : parsed;
    }

    private static int normalizeHex(int hexColor) {
        return hexColor & 0xFFFFFF;
    }
}