package space.anatomyuniverse.musavacca.block.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import space.anatomyuniverse.musavacca.block.entity.ModBlockEntities;
import space.anatomyuniverse.musavacca.entity.ModEntities;
import space.anatomyuniverse.musavacca.entity.mob.basuke.Basuke;

import java.util.UUID;

public class VocoTableBlockEntity extends BlockEntity {

    private static final String TAG_BASUKE_VISIBLE = "basuke_visible";
    private static final String TAG_BASUKE_UUID = "basuke_uuid";

    private final NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);

    private boolean basukeVisible = false;
    private UUID basukeUuid = null;

    public VocoTableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.VOCO_TABLE_BLOCK_ENTITY.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, VocoTableBlockEntity blockEntity) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        if (serverLevel.getGameTime() % 10L != 0L) {
            return;
        }

        blockEntity.updateBasuke(serverLevel);
    }

    public ItemStack getDisplayedItem() {
        return this.items.get(0);
    }

    public boolean hasDisplayedItem() {
        return !this.items.get(0).isEmpty();
    }

    public void setDisplayedItem(ItemStack stack) {
        ItemStack copy = stack.copy();
        copy.setCount(1);

        this.items.set(0, copy);
        this.setChanged();
        this.syncToClientAndRerender();
    }

    public ItemStack removeDisplayedItem() {
        ItemStack removed = this.items.get(0).copy();
        this.items.set(0, ItemStack.EMPTY);
        this.setChanged();
        this.syncToClientAndRerender();
        return removed;
    }

    public boolean isBasukeVisible() {
        return this.basukeVisible;
    }

    public boolean toggleBasukeVisible() {
        this.basukeVisible = !this.basukeVisible;
        this.setChanged();
        this.syncToClientAndRerender();
        return this.basukeVisible;
    }

    public void updateBasuke(ServerLevel level) {
        if (!this.basukeVisible) {
            this.removeBasuke(level);
            return;
        }

        Basuke basuke = this.getBasuke(level);
        if (basuke == null) {
            this.spawnBasuke(level);
            return;
        }

        if (!basuke.isBoundToTable(this.getBlockPos())) {
            basuke.bindToVocoTable(this.getBlockPos());
        }
    }

    private Basuke getBasuke(ServerLevel level) {
        if (this.basukeUuid == null) {
            return null;
        }

        Entity entity = level.getEntity(this.basukeUuid);
        if (entity instanceof Basuke basuke && basuke.isAlive()) {
            return basuke;
        }

        return null;
    }

    private void spawnBasuke(ServerLevel level) {
        Basuke basuke = ModEntities.BASUKE.get().create(level, EntitySpawnReason.TRIGGERED);
        if (basuke == null) {
            return;
        }

        double x = this.getBlockPos().getX() + 0.5D;
        double y = this.getBlockPos().getY() + 1.45D;
        double z = this.getBlockPos().getZ() + 0.5D;

        basuke.snapTo(x, y, z, level.random.nextFloat() * 360.0F, 0.0F);
        basuke.bindToVocoTable(this.getBlockPos());

        level.addFreshEntity(basuke);

        this.basukeUuid = basuke.getUUID();
        this.setChanged();
        this.syncToClientAndRerender();
    }

    private void removeBasuke(ServerLevel level) {
        if (this.basukeUuid != null) {
            Entity entity = level.getEntity(this.basukeUuid);
            if (entity instanceof Basuke basuke && basuke.isAlive()) {
                basuke.discard();
            }
        }

        if (this.basukeUuid != null) {
            this.basukeUuid = null;
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

        this.items.clear();
        ContainerHelper.loadAllItems(input, this.items);

        this.basukeVisible = input.getBooleanOr(TAG_BASUKE_VISIBLE, false);

        String uuidString = input.getStringOr(TAG_BASUKE_UUID, "");
        if (!uuidString.isEmpty()) {
            try {
                this.basukeUuid = UUID.fromString(uuidString);
            } catch (IllegalArgumentException ignored) {
                this.basukeUuid = null;
            }
        } else {
            this.basukeUuid = null;
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);

        ContainerHelper.saveAllItems(output, this.items, true);
        output.putBoolean(TAG_BASUKE_VISIBLE, this.basukeVisible);

        if (this.basukeUuid != null) {
            output.putString(TAG_BASUKE_UUID, this.basukeUuid.toString());
        }
    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter input) {
        super.applyImplicitComponents(input);
        input.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyInto(this.items);
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        components.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(this.items));
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
}