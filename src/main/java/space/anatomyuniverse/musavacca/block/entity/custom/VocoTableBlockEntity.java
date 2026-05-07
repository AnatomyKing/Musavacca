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
import org.jetbrains.annotations.Nullable;
import space.anatomyuniverse.musavacca.block.entity.ModBlockEntities;
import space.anatomyuniverse.musavacca.entity.ModEntities;
import space.anatomyuniverse.musavacca.entity.mob.basuke.Basuke;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoInteractLogic.ReceptorPosition;

import java.util.UUID;

public class VocoTableBlockEntity extends BlockEntity {

    private static final String TAG_BASUKE_VISIBLE = "basuke_visible";
    private static final String TAG_BASUKE_UUID = "basuke_uuid";

    private static final String[] TAG_YAW_DEGREES = {
            "yaw_north_east",
            "yaw_north_west",
            "yaw_south_east",
            "yaw_south_west"
    };

    private static final String[] TAG_PITCH_DEGREES = {
            "pitch_north_east",
            "pitch_north_west",
            "pitch_south_east",
            "pitch_south_west"
    };

    private final NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);

    private final int[] yawDegrees = new int[ReceptorPosition.COUNT];
    private final int[] pitchDegrees = new int[ReceptorPosition.COUNT];

    private boolean basukeVisible = false;
    @Nullable
    private UUID basukeUuid = null;

    public VocoTableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.VOCO_TABLE_BLOCK_ENTITY.get(), pos, state);
        this.resetFacingDefaults();
    }

    public ItemStack getDisplayedItem() {
        return this.items.get(0);
    }

    public boolean hasDisplayedItem() {
        return !this.getDisplayedItem().isEmpty();
    }

    public void setDisplayedItem(ItemStack stack) {
        ItemStack copy = stack.copy();
        copy.setCount(1);

        this.items.set(0, copy);
        this.markChangedAndSync();
    }

    public ItemStack removeDisplayedItem() {
        ItemStack removed = this.getDisplayedItem().copy();

        this.items.set(0, ItemStack.EMPTY);
        this.markChangedAndSync();

        return removed;
    }

    public int getYawDegrees(ReceptorPosition receptor) {
        return this.yawDegrees[receptor.id()];
    }

    public int getPitchDegrees(ReceptorPosition receptor) {
        return this.pitchDegrees[receptor.id()];
    }

    public void setYawDegrees(ReceptorPosition receptor, int yawDegrees) {
        int index = receptor.id();
        int clamped = VocoReceptorBlockEntity.clampYaw(yawDegrees);

        if (this.yawDegrees[index] == clamped) {
            return;
        }

        this.yawDegrees[index] = clamped;
        this.markChangedAndSync();
    }

    public void setPitchDegrees(ReceptorPosition receptor, int pitchDegrees) {
        int index = receptor.id();
        int clamped = VocoReceptorBlockEntity.clampPitch(pitchDegrees);

        if (this.pitchDegrees[index] == clamped) {
            return;
        }

        this.pitchDegrees[index] = clamped;
        this.markChangedAndSync();
    }

    public void setFacingDegrees(ReceptorPosition receptor, int yawDegrees, int pitchDegrees) {
        int index = receptor.id();

        int clampedYaw = VocoReceptorBlockEntity.clampYaw(yawDegrees);
        int clampedPitch = VocoReceptorBlockEntity.clampPitch(pitchDegrees);

        if (this.yawDegrees[index] == clampedYaw && this.pitchDegrees[index] == clampedPitch) {
            return;
        }

        this.yawDegrees[index] = clampedYaw;
        this.pitchDegrees[index] = clampedPitch;
        this.markChangedAndSync();
    }

    private void resetFacingDefaults() {
        for (ReceptorPosition receptor : ReceptorPosition.values()) {
            int index = receptor.id();
            this.yawDegrees[index] = receptor.defaultYawDegrees();
            this.pitchDegrees[index] = receptor.defaultPitchDegrees();
        }
    }

    public boolean isBasukeVisible() {
        return this.basukeVisible;
    }

    /**
     * Called only when the player clicks the dialer.
     * No server ticking is needed for Basuke spawning/removal.
     */
    public void toggleBasuke(ServerLevel level) {
        this.basukeVisible = !this.basukeVisible;

        if (this.basukeVisible) {
            boolean spawnedOrFound = this.ensureBasukeExists(level);

            if (!spawnedOrFound) {
                this.basukeVisible = false;
            }
        } else {
            this.removeBasuke(level);
        }

        this.markChangedAndSync();
    }

    private boolean ensureBasukeExists(ServerLevel level) {
        Basuke basuke = this.getBasuke(level);

        if (basuke == null) {
            return this.spawnBasuke(level);
        }

        if (!basuke.isBoundToTable(this.getBlockPos())) {
            basuke.bindToVocoTable(this.getBlockPos());
        }

        return true;
    }

    @Nullable
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

    private boolean spawnBasuke(ServerLevel level) {
        Basuke basuke = ModEntities.BASUKE.get().create(level, EntitySpawnReason.TRIGGERED);
        if (basuke == null) {
            this.basukeUuid = null;
            return false;
        }

        BlockPos pos = this.getBlockPos();

        basuke.snapTo(
                pos.getX() + 0.5D,
                pos.getY() + 1.45D,
                pos.getZ() + 0.5D,
                level.random.nextFloat() * 360.0F,
                0.0F
        );

        basuke.bindToVocoTable(pos);
        level.addFreshEntity(basuke);

        this.basukeUuid = basuke.getUUID();
        return true;
    }

    private void removeBasuke(ServerLevel level) {
        Basuke basuke = this.getBasuke(level);
        if (basuke != null) {
            basuke.discard();
        }

        this.basukeUuid = null;
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

        this.items.clear();
        ContainerHelper.loadAllItems(input, this.items);

        this.basukeVisible = input.getBooleanOr(TAG_BASUKE_VISIBLE, false);
        this.basukeUuid = readUuid(input.getStringOr(TAG_BASUKE_UUID, ""));

        for (ReceptorPosition receptor : ReceptorPosition.values()) {
            int index = receptor.id();

            this.yawDegrees[index] = VocoReceptorBlockEntity.clampYaw(
                    input.getIntOr(TAG_YAW_DEGREES[index], receptor.defaultYawDegrees())
            );

            this.pitchDegrees[index] = VocoReceptorBlockEntity.clampPitch(
                    input.getIntOr(TAG_PITCH_DEGREES[index], receptor.defaultPitchDegrees())
            );
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

        for (ReceptorPosition receptor : ReceptorPosition.values()) {
            int index = receptor.id();

            output.putInt(TAG_YAW_DEGREES[index], this.yawDegrees[index]);
            output.putInt(TAG_PITCH_DEGREES[index], this.pitchDegrees[index]);
        }
    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter input) {
        super.applyImplicitComponents(input);

        input.getOrDefault(
                DataComponents.CONTAINER,
                ItemContainerContents.EMPTY
        ).copyInto(this.items);
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

    @Nullable
    private static UUID readUuid(String uuidString) {
        if (uuidString.isEmpty()) {
            return null;
        }

        try {
            return UUID.fromString(uuidString);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }
}