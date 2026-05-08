// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/block/entity/custom/VocoTableBlockEntity.java
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
import space.anatomyuniverse.musavacca.block.custom.logic.VocoInteractLogic.ReceptorPosition;
import space.anatomyuniverse.musavacca.block.entity.ModBlockEntities;
import space.anatomyuniverse.musavacca.component.ModDataComponents;
import space.anatomyuniverse.musavacca.entity.ModEntities;
import space.anatomyuniverse.musavacca.entity.mob.basuke.Basuke;

import java.util.UUID;

public class VocoTableBlockEntity extends BlockEntity {

    private static final String TAG_BASUKE_VISIBLE = "basuke_visible";
    private static final String TAG_BASUKE_UUID = "basuke_uuid";

    private static final String TAG_LATEST_HEX_COLOR = "latest_hex_color";
    private static final String TAG_LATEST_HEX_RECEPTOR_ID = "latest_hex_receptor_id";

    private static final String[] TAG_CORNER_HEX_COLORS = {
            "hex_north_east",
            "hex_north_west",
            "hex_south_east",
            "hex_south_west"
    };

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

    public static final int DEFAULT_HEX_NORTH_EAST = 0xE74E8C;
    public static final int DEFAULT_HEX_SOUTH_EAST = 0x49D5CD;
    public static final int DEFAULT_HEX_SOUTH_WEST = 0xFFF000;
    public static final int DEFAULT_HEX_NORTH_WEST = 0x7B61FF;

    private final NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);

    private final int[] yawDegrees = new int[ReceptorPosition.COUNT];
    private final int[] pitchDegrees = new int[ReceptorPosition.COUNT];

    private final int[] cornerHexColors = new int[ReceptorPosition.COUNT];

    private int latestHexColor = DEFAULT_HEX_NORTH_EAST;
    private int latestHexReceptorId = ReceptorPosition.NORTH_EAST.id();

    private boolean basukeVisible = false;
    @Nullable
    private UUID basukeUuid = null;

    public VocoTableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.VOCO_TABLE_BLOCK_ENTITY.get(), pos, state);
        this.resetFacingDefaults();
        this.resetCornerHexDefaults();
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

    public int getLatestHexColor() {
        return this.latestHexColor;
    }

    public ReceptorPosition getLatestHexReceptor() {
        return ReceptorPosition.byId(this.latestHexReceptorId);
    }

    public int getCornerHexColor(ReceptorPosition receptor) {
        return this.cornerHexColors[receptor.id()];
    }

    public void setCornerHexColor(ReceptorPosition receptor, int hexColor) {
        int index = receptor.id();
        int normalized = normalizeHex(hexColor);

        boolean changed = this.cornerHexColors[index] != normalized
                || this.latestHexColor != normalized
                || this.latestHexReceptorId != index;

        if (!changed) {
            return;
        }

        this.cornerHexColors[index] = normalized;
        this.latestHexColor = normalized;
        this.latestHexReceptorId = index;
        this.markChangedAndSync();
    }

    public void setLatestActiveReceptor(ReceptorPosition receptor) {
        int index = receptor.id();
        int color = this.cornerHexColors[index];

        if (this.latestHexReceptorId == index && this.latestHexColor == color) {
            return;
        }

        this.latestHexReceptorId = index;
        this.latestHexColor = color;
        this.markChangedAndSync();
    }

    public ReceptorPosition cycleLatestActiveHexClockwise() {
        ReceptorPosition next = this.getLatestHexReceptor().nextClockwise();
        this.setLatestActiveReceptor(next);
        return next;
    }

    public void activatePortal(ReceptorPosition receptor) {
        this.setLatestActiveReceptor(receptor);
    }

    private void resetCornerHexDefaults() {
        for (ReceptorPosition receptor : ReceptorPosition.values()) {
            this.cornerHexColors[receptor.id()] = defaultHexColor(receptor);
        }

        this.latestHexReceptorId = ReceptorPosition.NORTH_EAST.id();
        this.latestHexColor = this.cornerHexColors[this.latestHexReceptorId];
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

        this.resetCornerHexDefaults();

        for (ReceptorPosition receptor : ReceptorPosition.values()) {
            int index = receptor.id();

            this.cornerHexColors[index] = normalizeHex(
                    input.getIntOr(TAG_CORNER_HEX_COLORS[index], defaultHexColor(receptor))
            );

            this.yawDegrees[index] = VocoReceptorBlockEntity.clampYaw(
                    input.getIntOr(TAG_YAW_DEGREES[index], receptor.defaultYawDegrees())
            );

            this.pitchDegrees[index] = VocoReceptorBlockEntity.clampPitch(
                    input.getIntOr(TAG_PITCH_DEGREES[index], receptor.defaultPitchDegrees())
            );
        }

        this.latestHexReceptorId = clampReceptorId(
                input.getIntOr(TAG_LATEST_HEX_RECEPTOR_ID, ReceptorPosition.NORTH_EAST.id())
        );

        this.latestHexColor = normalizeHex(
                input.getIntOr(
                        TAG_LATEST_HEX_COLOR,
                        this.cornerHexColors[this.latestHexReceptorId]
                )
        );
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);

        ContainerHelper.saveAllItems(output, this.items, true);
        output.putBoolean(TAG_BASUKE_VISIBLE, this.basukeVisible);

        if (this.basukeUuid != null) {
            output.putString(TAG_BASUKE_UUID, this.basukeUuid.toString());
        }

        output.putInt(TAG_LATEST_HEX_COLOR, this.latestHexColor);
        output.putInt(TAG_LATEST_HEX_RECEPTOR_ID, this.latestHexReceptorId);

        for (ReceptorPosition receptor : ReceptorPosition.values()) {
            int index = receptor.id();

            output.putInt(TAG_CORNER_HEX_COLORS[index], this.cornerHexColors[index]);
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

        Integer savedHex = input.get(ModDataComponents.HEX_COLOR.get());
        if (savedHex != null) {
            this.latestHexColor = normalizeHex(savedHex);
        }
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        components.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(this.items));
        components.set(ModDataComponents.HEX_COLOR.get(), this.latestHexColor);
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

    public static int normalizeHex(int hexColor) {
        return hexColor & 0xFFFFFF;
    }

    public static int defaultHexColor(ReceptorPosition receptor) {
        return switch (receptor) {
            case NORTH_EAST -> DEFAULT_HEX_NORTH_EAST;
            case SOUTH_EAST -> DEFAULT_HEX_SOUTH_EAST;
            case SOUTH_WEST -> DEFAULT_HEX_SOUTH_WEST;
            case NORTH_WEST -> DEFAULT_HEX_NORTH_WEST;
        };
    }

    private static int clampReceptorId(int id) {
        return Math.max(0, Math.min(ReceptorPosition.COUNT - 1, id));
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