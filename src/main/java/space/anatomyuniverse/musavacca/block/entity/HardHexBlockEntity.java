
package space.anatomyuniverse.musavacca.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import space.anatomyuniverse.musavacca.component.ModDataComponents;

public class HardHexBlockEntity extends BlockEntity {

    public static final String TAG_HEX_COLOR = "hex_color";
    public static final int HARD_HEX_COLOR = 0xD5CD49;

    private int hexColor = HARD_HEX_COLOR;

    public HardHexBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.HARD_HEX_BLOCK_ENTITY.get(), pos, state);
    }

    public int getHexColor() {
        return this.hexColor;
    }

    public boolean hasHexColor() {
        return true;
    }

    public void setHexColor(int ignoredHexColor) {
        int normalized = HARD_HEX_COLOR;
        if (this.hexColor != normalized) {
            this.hexColor = normalized;
            this.setChanged();
            this.syncToClientAndRerender();
        }
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

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);

        // Even if loaded from disk, this block always resolves to the hardcoded color.
        input.getIntOr(TAG_HEX_COLOR, HARD_HEX_COLOR);
        this.hexColor = HARD_HEX_COLOR;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt(TAG_HEX_COLOR, HARD_HEX_COLOR);
    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter input) {
        super.applyImplicitComponents(input);

        // We still read the component path so the BE -> item -> BE flow stays the same,
        // but this block always resolves to the same fixed color.
        input.get(ModDataComponents.HEX_COLOR.get());
        this.hexColor = HARD_HEX_COLOR;
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        components.set(ModDataComponents.HEX_COLOR.get(), HARD_HEX_COLOR);
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