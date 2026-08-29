package space.anatomyuniverse.musavacca.block.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
//? if >=1.21.5
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
//? if <1.21.5
//import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.Entity;
//? if >=1.21.2
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
//? if >=1.21.6
import net.minecraft.world.level.storage.ValueInput;
//? if >=1.21.6
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import space.anatomyuniverse.musavacca.block.custom.PearlCandleBlock;
import space.anatomyuniverse.musavacca.block.custom.VocoTableBlock;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoReceptorLogic;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoReceptorLogic.ReceptorPosition;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoTeleportLogic;
import space.anatomyuniverse.musavacca.block.entity.ModBlockEntities;
import space.anatomyuniverse.musavacca.component.ModDataComponents;
import space.anatomyuniverse.musavacca.entity.ModEntities;
import space.anatomyuniverse.musavacca.entity.mob.basuke.Basuke;
import space.anatomyuniverse.musavacca.item.custom.FlintAndPearlItem;

import java.util.UUID;

public class VocoTableBlockEntity extends BlockEntity {
    private static final String TAG_BASUKE_VISIBLE = "basuke_visible";
    private static final String TAG_BASUKE_UUID = "basuke_uuid";
    private static final String TAG_DISPLAYED_ITEM_COUNT = "displayed_item_count";

    private static final String TAG_LATEST_HEX_COLOR = "latest_hex_color";
    private static final String TAG_LATEST_HEX_RECEPTOR_ID = "latest_hex_receptor_id";

    private static final String[] TAG_CANDLE_BLOCK_IDS = {
            "candle_block_north_east",
            "candle_block_north_west",
            "candle_block_south_east",
            "candle_block_south_west"
    };

    private static final String[] TAG_CANDLE_COUNTS = {
            "candle_count_north_east",
            "candle_count_north_west",
            "candle_count_south_east",
            "candle_count_south_west"
    };

    private static final String[] TAG_CANDLE_LIT = {
            "candle_lit_north_east",
            "candle_lit_north_west",
            "candle_lit_south_east",
            "candle_lit_south_west"
    };

    private static final String[] TAG_CANDLE_HAS_HEX_COLORS = {
            "candle_has_hex_north_east",
            "candle_has_hex_north_west",
            "candle_has_hex_south_east",
            "candle_has_hex_south_west"
    };

    private static final String[] TAG_CANDLE_HEX_COLORS = {
            "candle_hex_north_east",
            "candle_hex_north_west",
            "candle_hex_south_east",
            "candle_hex_south_west"
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

    private static final String[] TAG_CUSTOM_TARGET = {
            "custom_target_north_east",
            "custom_target_north_west",
            "custom_target_south_east",
            "custom_target_south_west"
    };

    private static final String[] TAG_TARGET_X = {
            "target_x_north_east",
            "target_x_north_west",
            "target_x_south_east",
            "target_x_south_west"
    };

    private static final String[] TAG_TARGET_Y = {
            "target_y_north_east",
            "target_y_north_west",
            "target_y_south_east",
            "target_y_south_west"
    };

    private static final String[] TAG_TARGET_Z = {
            "target_z_north_east",
            "target_z_north_west",
            "target_z_south_east",
            "target_z_south_west"
    };

    public static final int DEFAULT_HEX_COLOR = FlintAndPearlItem.DEFAULT_HEX_COLOR;
    public static final int UNSET_HEX_COLOR = VocoReceptorLogic.UNSET_HEX_COLOR;

    private final NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);
    private int displayedItemCount = 0;

    private final int[] yawDegrees = new int[ReceptorPosition.COUNT];
    private final int[] pitchDegrees = new int[ReceptorPosition.COUNT];

    private final boolean[] customTargetEnabled = new boolean[ReceptorPosition.COUNT];
    private final double[] targetX = new double[ReceptorPosition.COUNT];
    private final double[] targetY = new double[ReceptorPosition.COUNT];
    private final double[] targetZ = new double[ReceptorPosition.COUNT];

    private final CandleSlot[] candleSlots = new CandleSlot[ReceptorPosition.COUNT];

    private int latestHexColor = UNSET_HEX_COLOR;
    private int latestHexReceptorId = ReceptorPosition.NORTH_EAST.id();

    private boolean basukeVisible = false;

    @Nullable
    private UUID basukeUuid = null;

    public VocoTableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.VOCO_TABLE_BLOCK_ENTITY.get(), pos, state);

        for (ReceptorPosition receptor : ReceptorPosition.values()) {
            this.candleSlots[receptor.id()] = new CandleSlot();
        }

        this.resetFacingDefaults();
        this.resetTargetDefaults();
    }

    public ItemStack getDisplayedItem() {
        return this.items.get(0);
    }

    public int getDisplayedItemCount() {
        return this.hasDisplayedItem()
                ? this.displayedItemCount
                : 0;
    }

    public boolean hasDisplayedItem() {
        return !this.getDisplayedItem().isEmpty()
                && this.displayedItemCount > 0;
    }

    public void setDisplayedItem(ItemStack stack) {
        this.setDisplayedItem(
                stack,
                stack.isEmpty()
                        ? 0
                        : stack.getCount()
        );
    }

    public void setDisplayedItem(
            ItemStack stack,
            int count
    ) {
        if (stack.isEmpty() || count <= 0) {
            this.items.set(0, ItemStack.EMPTY);
            this.displayedItemCount = 0;
            this.markChangedAndSync();
            return;
        }

        ItemStack copy =
                stack.copyWithCount(1);

        this.items.set(0, copy);
        this.displayedItemCount =
                copy.getMaxStackSize() <= 1
                        ? 1
                        : count;

        this.markChangedAndSync();
    }

    public boolean canMergeDisplayedItem(ItemStack stack) {
        ItemStack displayed =
                this.getDisplayedItem();

        return this.hasDisplayedItem()
                && !stack.isEmpty()
                && displayed.getMaxStackSize() > 1
                && ItemStack.isSameItemSameComponents(
                displayed,
                stack
        );
    }

    public int addDisplayedItems(
            ItemStack stack,
            int requestedAmount
    ) {
        if (stack.isEmpty() || requestedAmount <= 0) {
            return 0;
        }

        ItemStack displayed =
                this.getDisplayedItem();

        if (!this.hasDisplayedItem()) {
            int moved =
                    stack.getMaxStackSize() <= 1
                            ? 1
                            : Math.min(
                            requestedAmount,
                            stack.getCount()
                    );

            this.items.set(
                    0,
                    stack.copyWithCount(1)
            );
            this.displayedItemCount = moved;
            this.markChangedAndSync();
            return moved;
        }

        if (!this.canMergeDisplayedItem(stack)) {
            return 0;
        }

        long capacity =
                (long) Integer.MAX_VALUE
                        - this.displayedItemCount;

        if (capacity <= 0L) {
            return 0;
        }

        int moved = (int) Math.min(
                Math.min(
                        (long) requestedAmount,
                        stack.getCount()
                ),
                capacity
        );

        if (moved <= 0) {
            return 0;
        }

        this.displayedItemCount += moved;
        this.markChangedAndSync();
        return moved;
    }

    public ItemStack removeDisplayedItems(
            int requestedAmount
    ) {
        ItemStack displayed =
                this.getDisplayedItem();

        if (
                !this.hasDisplayedItem()
                        || requestedAmount <= 0
        ) {
            return ItemStack.EMPTY;
        }

        int legalStackSize =
                Math.max(
                        1,
                        displayed.getMaxStackSize()
                );

        int removedCount =
                Math.min(
                        Math.min(
                                requestedAmount,
                                legalStackSize
                        ),
                        this.displayedItemCount
                );

        ItemStack removed =
                displayed.copyWithCount(
                        removedCount
                );

        this.displayedItemCount -= removedCount;

        if (this.displayedItemCount <= 0) {
            this.items.set(0, ItemStack.EMPTY);
            this.displayedItemCount = 0;
        }

        this.markChangedAndSync();
        return removed;
    }

    public boolean consumeDisplayedItems(
            int amount
    ) {
        if (
                amount <= 0
                        || !this.hasDisplayedItem()
                        || this.displayedItemCount < amount
        ) {
            return false;
        }

        this.displayedItemCount -= amount;

        if (this.displayedItemCount <= 0) {
            this.items.set(0, ItemStack.EMPTY);
            this.displayedItemCount = 0;
        }

        this.markChangedAndSync();
        return true;
    }

    public ItemStack removeDisplayedItem() {
        ItemStack displayed =
                this.getDisplayedItem();

        if (displayed.isEmpty()) {
            return ItemStack.EMPTY;
        }

        return this.removeDisplayedItems(
                displayed.getMaxStackSize()
        );
    }

    public int getYawDegrees(ReceptorPosition receptor) {
        return this.yawDegrees[receptor.id()];
    }

    public int getPitchDegrees(ReceptorPosition receptor) {
        return this.pitchDegrees[receptor.id()];
    }

    public void setYawDegrees(ReceptorPosition receptor, int yawDegrees) {
        int index = receptor.id();
        int clamped = VocoReceptorLogic.clampYaw(yawDegrees);

        if (this.yawDegrees[index] == clamped) {
            return;
        }

        this.yawDegrees[index] = clamped;
        this.markChangedAndSync();
        this.resyncEndpoint(receptor);
    }

    public void setPitchDegrees(ReceptorPosition receptor, int pitchDegrees) {
        int index = receptor.id();
        int clamped = VocoReceptorLogic.clampPitch(pitchDegrees);

        if (this.pitchDegrees[index] == clamped) {
            return;
        }

        this.pitchDegrees[index] = clamped;
        this.markChangedAndSync();
        this.resyncEndpoint(receptor);
    }

    public boolean isCustomTargetEnabled(ReceptorPosition receptor) {
        return this.customTargetEnabled[receptor.id()];
    }

    public Vec3 getCustomTarget(ReceptorPosition receptor) {
        int index = receptor.id();

        if (!this.customTargetEnabled[index]) {
            return VocoTeleportLogic.getDefaultTeleportPosition(this.getBlockPos(), receptor);
        }

        return new Vec3(
                this.targetX[index],
                this.targetY[index],
                this.targetZ[index]
        );
    }

    public void setCustomTargetEnabled(ReceptorPosition receptor, boolean enabled) {
        int index = receptor.id();

        if (this.customTargetEnabled[index] == enabled) {
            return;
        }

        this.customTargetEnabled[index] = enabled;

        if (enabled && this.targetY[index] == 0.0D) {
            Vec3 fallback = VocoTeleportLogic.getDefaultTeleportPosition(this.getBlockPos(), receptor);
            this.targetX[index] = fallback.x;
            this.targetY[index] = fallback.y;
            this.targetZ[index] = fallback.z;
        }

        this.markChangedAndSync();
        this.resyncEndpoint(receptor);
    }

    public void setCustomTarget(ReceptorPosition receptor, Vec3 target, int yawDegrees, int pitchDegrees) {
        int index = receptor.id();

        this.customTargetEnabled[index] = true;
        this.targetX[index] = target.x;
        this.targetY[index] = target.y;
        this.targetZ[index] = target.z;
        this.yawDegrees[index] = VocoReceptorLogic.clampYaw(yawDegrees);
        this.pitchDegrees[index] = VocoReceptorLogic.clampPitch(pitchDegrees);

        this.markChangedAndSync();
        this.resyncEndpoint(receptor);
    }

    public void resetCustomTarget(ReceptorPosition receptor) {
        int index = receptor.id();
        Vec3 fallback = VocoTeleportLogic.getDefaultTeleportPosition(this.getBlockPos(), receptor);

        this.customTargetEnabled[index] = false;
        this.targetX[index] = fallback.x;
        this.targetY[index] = fallback.y;
        this.targetZ[index] = fallback.z;
        this.yawDegrees[index] = receptor.defaultYawDegrees();
        this.pitchDegrees[index] = receptor.defaultPitchDegrees();

        this.markChangedAndSync();
        this.resyncEndpoint(receptor);
    }

    public boolean hasLatestHexColor() {
        return this.latestHexColor != UNSET_HEX_COLOR;
    }

    public int getLatestHexColor() {
        return this.latestHexColor;
    }

    public ReceptorPosition getLatestHexReceptor() {
        return ReceptorPosition.byId(this.latestHexReceptorId);
    }

    public int getCornerHexColor(ReceptorPosition receptor) {
        CandleSlot slot = this.slot(receptor);
        return slot.hasHexColor ? slot.hexColor : UNSET_HEX_COLOR;
    }

    public int getPortalHexColorOrUnset(ReceptorPosition receptor) {
        CandleSlot slot = this.slot(receptor);
        return slot.hasCandle() && slot.lit && slot.hasHexColor
                ? slot.hexColor
                : UNSET_HEX_COLOR;
    }

    public void activatePortal(ReceptorPosition receptor) {
        CandleSlot slot = this.slot(receptor);

        if (!slot.hasCandle() || !slot.lit || !slot.hasHexColor) {
            this.refreshLatestHexFromLitCandles();
            return;
        }

        this.setLatest(receptor, slot.hexColor);
        this.markChangedAndSync();
    }

    public void refreshLatestHexFromLitCandles() {
        ReceptorPosition current = ReceptorPosition.byId(this.latestHexReceptorId);
        CandleSlot currentSlot = this.slot(current);

        if (currentSlot.hasCandle() && currentSlot.lit && currentSlot.hasHexColor) {
            this.latestHexColor = currentSlot.hexColor;
            return;
        }

        for (ReceptorPosition receptor : ReceptorPosition.values()) {
            CandleSlot slot = this.slot(receptor);

            if (slot.hasCandle() && slot.lit && slot.hasHexColor) {
                this.setLatest(receptor, slot.hexColor);
                return;
            }
        }

        this.latestHexColor = UNSET_HEX_COLOR;
        this.latestHexReceptorId = ReceptorPosition.NORTH_EAST.id();
    }

    public boolean hasCandle(ReceptorPosition receptor) {
        return this.slot(receptor).hasCandle();
    }

    @Nullable
    public Block getCandleBlock(ReceptorPosition receptor) {
        return this.slot(receptor).block;
    }

    public int getCandleCount(ReceptorPosition receptor) {
        return this.slot(receptor).count;
    }

    public boolean isCandleLit(ReceptorPosition receptor) {
        CandleSlot slot = this.slot(receptor);
        return slot.hasCandle() && slot.lit;
    }

    public boolean isPearlCandleLit(ReceptorPosition receptor) {
        CandleSlot slot = this.slot(receptor);
        return slot.hasCandle() && slot.lit && slot.hasHexColor;
    }

    public int getCandleHexColorOrFallback(ReceptorPosition receptor) {
        CandleSlot slot = this.slot(receptor);
        return slot.hasHexColor ? slot.hexColor : DEFAULT_HEX_COLOR;
    }

    public boolean canAddCandle(ReceptorPosition receptor, Block candleBlock) {
        Block normalized = normalizeCandleBlock(candleBlock);
        if (!isValidCandleBlock(normalized)) {
            return false;
        }

        CandleSlot slot = this.slot(receptor);

        return !slot.hasCandle()
                || slot.block == normalized && slot.count < CandleBlock.MAX_CANDLES;
    }

    public boolean addCandle(ReceptorPosition receptor, Block candleBlock) {
        Block normalized = normalizeCandleBlock(candleBlock);
        if (!this.canAddCandle(receptor, normalized)) {
            return false;
        }

        CandleSlot slot = this.slot(receptor);

        if (!slot.hasCandle()) {
            slot.block = normalized;
            slot.count = 1;
            slot.lit = false;
            slot.hasHexColor = false;
            slot.hexColor = UNSET_HEX_COLOR;
        } else {
            slot.count = Math.min(CandleBlock.MAX_CANDLES, slot.count + 1);
        }

        this.markChangedAndSync();
        return true;
    }

    public boolean lightPearlCandle(ReceptorPosition receptor, int hexColor) {
        CandleSlot slot = this.slot(receptor);
        if (!slot.hasCandle() || slot.lit) {
            return false;
        }

        slot.lit = true;
        slot.hasHexColor = true;
        slot.hexColor = normalizeHex(hexColor);

        this.markChangedAndSync();
        return true;
    }

    public boolean lightVanillaCandle(ReceptorPosition receptor) {
        CandleSlot slot = this.slot(receptor);
        if (!slot.hasCandle() || slot.lit) {
            return false;
        }

        slot.lit = true;
        slot.hasHexColor = false;
        slot.hexColor = UNSET_HEX_COLOR;

        this.markChangedAndSync();
        return true;
    }

    public boolean lightCandle(ReceptorPosition receptor, int hexColor) {
        return this.lightPearlCandle(receptor, hexColor);
    }

    public boolean extinguishCandle(ReceptorPosition receptor) {
        CandleSlot slot = this.slot(receptor);

        if (!slot.hasCandle() || !slot.lit) {
            return false;
        }

        slot.lit = false;
        slot.hasHexColor = false;
        slot.hexColor = UNSET_HEX_COLOR;

        this.refreshLatestHexFromLitCandles();
        this.markChangedAndSync();
        this.resyncEndpoint(receptor);

        return true;
    }

    public void extinguishAllCandlesForSummon() {
        boolean changed = false;

        for (ReceptorPosition receptor : ReceptorPosition.values()) {
            CandleSlot slot = this.slot(receptor);

            if (!slot.hasCandle() || !slot.lit) {
                continue;
            }

            slot.lit = false;
            slot.hasHexColor = false;
            slot.hexColor = UNSET_HEX_COLOR;

            this.removeEndpoint(receptor);
            changed = true;
        }

        if (!changed) {
            return;
        }

        this.clearPortalPropertiesForUnlitCandles();
        this.refreshLatestHexFromLitCandles();
        this.markChangedAndSync();
    }

    public int countLitReceptors() {
        return this.countLitReceptors(this.getBlockState());
    }

    public boolean hasLitReceptorCost(int cost) {
        return cost <= 0 || this.countLitReceptors() >= cost;
    }

    public boolean consumeLitReceptorsForCrafting(ServerLevel level, int cost) {
        if (cost <= 0) {
            return true;
        }

        BlockPos pos = this.getBlockPos();
        BlockState state = level.getBlockState(pos);

        if (!(state.getBlock() instanceof VocoTableBlock) || this.countLitReceptors(state) < cost) {
            return false;
        }

        BlockState newState = state;
        ReceptorPosition[] consumed = new ReceptorPosition[cost];
        int consumedCount = 0;

        for (ReceptorPosition receptor : ReceptorPosition.values()) {
            if (consumedCount >= cost) {
                break;
            }

            if (!newState.getValue(VocoTableBlock.lightProperty(receptor))) {
                continue;
            }

            newState = newState.setValue(VocoTableBlock.lightProperty(receptor), false);

            if (newState.hasProperty(VocoTableBlock.portalProperty(receptor))) {
                newState = newState.setValue(VocoTableBlock.portalProperty(receptor), false);
            }

            consumed[consumedCount] = receptor;
            consumedCount++;
        }

        if (consumedCount < cost) {
            return false;
        }

        level.setBlock(pos, newState, VocoReceptorLogic.UPDATE_FLAGS);

        for (int i = 0; i < consumedCount; i++) {
            this.removeEndpoint(consumed[i]);
        }

        this.refreshLatestHexFromLitCandles();
        this.markChangedAndSync();

        return true;
    }

    private int countLitReceptors(BlockState state) {
        if (!(state.getBlock() instanceof VocoTableBlock)) {
            return 0;
        }

        int count = 0;

        for (ReceptorPosition receptor : ReceptorPosition.values()) {
            if (state.getValue(VocoTableBlock.lightProperty(receptor))) {
                count++;
            }
        }

        return count;
    }

    public ItemStack removeOneCandle(ReceptorPosition receptor) {
        CandleSlot slot = this.slot(receptor);

        if (!slot.hasCandle()) {
            return ItemStack.EMPTY;
        }

        ItemStack removed = new ItemStack(slot.block.asItem());

        slot.count--;

        if (slot.count <= 0) {
            slot.clear();
            this.refreshLatestHexFromLitCandles();
        }

        this.markChangedAndSync();
        this.resyncEndpoint(receptor);

        return removed;
    }

    public boolean hasOtherLitCandleWithHex(ReceptorPosition ignoredReceptor, int hexColor) {
        int normalized = normalizeHex(hexColor);

        for (ReceptorPosition receptor : ReceptorPosition.values()) {
            if (receptor == ignoredReceptor) {
                continue;
            }

            CandleSlot slot = this.slot(receptor);
            if (slot.hasCandle()
                    && slot.lit
                    && slot.hasHexColor
                    && normalizeHex(slot.hexColor) == normalized) {
                return true;
            }
        }

        return false;
    }

    public boolean isBasukeVisible() {
        return this.basukeVisible;
    }

    public void toggleBasuke(ServerLevel level) {
        BlockState state = this.getBlockState();

        if (!state.hasProperty(VocoTableBlock.ROTARY_DIALERS)) {
            return;
        }

        boolean active = !state.getValue(VocoTableBlock.ROTARY_DIALERS);

        level.setBlock(
                this.getBlockPos(),
                state.setValue(VocoTableBlock.ROTARY_DIALERS, active),
                VocoReceptorLogic.UPDATE_FLAGS
        );

        if (active) {
            this.activateBasukeFromRotaryDialers(level);
        } else {
            this.deactivateBasukeFromRotaryDialers(level);
        }
    }

    public void activateBasukeFromRotaryDialers(ServerLevel level) {
        this.basukeVisible = true;

        if (!this.ensureBasukeExists(level)) {
            this.basukeVisible = false;
            this.forceRotaryDialers(level, false);
        }

        this.markChangedAndSync();
    }

    public void deactivateBasukeFromRotaryDialers(ServerLevel level) {
        this.basukeVisible = false;
        this.removeBasuke(level);
        this.markChangedAndSync();
    }

    private void forceRotaryDialers(ServerLevel level, boolean active) {
        BlockState state = level.getBlockState(this.getBlockPos());

        if (!(state.getBlock() instanceof VocoTableBlock)
                || !state.hasProperty(VocoTableBlock.ROTARY_DIALERS)
                || state.getValue(VocoTableBlock.ROTARY_DIALERS) == active) {
            return;
        }

        level.setBlock(
                this.getBlockPos(),
                state.setValue(VocoTableBlock.ROTARY_DIALERS, active),
                VocoReceptorLogic.UPDATE_FLAGS
        );
    }

    private void setLatest(ReceptorPosition receptor, int hexColor) {
        this.latestHexReceptorId = receptor.id();
        this.latestHexColor = normalizeHex(hexColor);
    }

    private CandleSlot slot(ReceptorPosition receptor) {
        return this.candleSlots[receptor.id()];
    }

    private void resetFacingDefaults() {
        for (ReceptorPosition receptor : ReceptorPosition.values()) {
            int index = receptor.id();
            this.yawDegrees[index] = receptor.defaultYawDegrees();
            this.pitchDegrees[index] = receptor.defaultPitchDegrees();
        }
    }

    private void resetTargetDefaults() {
        for (ReceptorPosition receptor : ReceptorPosition.values()) {
            int index = receptor.id();
            Vec3 fallback = VocoTeleportLogic.getDefaultTeleportPosition(this.getBlockPos(), receptor);

            this.customTargetEnabled[index] = false;
            this.targetX[index] = fallback.x;
            this.targetY[index] = fallback.y;
            this.targetZ[index] = fallback.z;
        }
    }

    private void resyncEndpoint(ReceptorPosition receptor) {
        Level level = this.getLevel();

        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        BlockState state = this.getBlockState();

        boolean portalActive = state.hasProperty(VocoTableBlock.portalProperty(receptor))
                && state.getValue(VocoTableBlock.portalProperty(receptor));

        if (!portalActive) {
            return;
        }

        int hexColor = this.getPortalHexColorOrUnset(receptor);

        if (hexColor == UNSET_HEX_COLOR) {
            this.removeEndpoint(receptor);
            return;
        }

        VocoTeleportLogic.syncEndpointDetailed(
                serverLevel,
                this.getBlockPos(),
                receptor,
                true,
                hexColor
        );
    }

    private void removeEndpoint(ReceptorPosition receptor) {
        Level level = this.getLevel();

        if (level instanceof ServerLevel serverLevel) {
            VocoTeleportLogic.syncEndpointDetailed(
                    serverLevel,
                    this.getBlockPos(),
                    receptor,
                    false,
                    VocoReceptorLogic.UNSET_HEX_COLOR
            );
        }
    }

    private void clearPortalPropertiesForUnlitCandles() {
        Level level = this.getLevel();

        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        BlockPos pos = this.getBlockPos();
        BlockState state = serverLevel.getBlockState(pos);

        if (!(state.getBlock() instanceof VocoTableBlock)) {
            return;
        }

        BlockState newState = state;

        for (ReceptorPosition receptor : ReceptorPosition.values()) {
            CandleSlot slot = this.slot(receptor);
            if (slot.hasCandle() && slot.lit && slot.hasHexColor) {
                continue;
            }

            if (newState.hasProperty(VocoTableBlock.portalProperty(receptor))
                    && newState.getValue(VocoTableBlock.portalProperty(receptor))) {
                newState = newState.setValue(VocoTableBlock.portalProperty(receptor), false);
            }
        }

        if (newState != state) {
            serverLevel.setBlock(pos, newState, VocoReceptorLogic.UPDATE_FLAGS);
        }
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
        //? if <1.21.2 {
        /*Basuke basuke = ModEntities.BASUKE.get().create(level);
        *///?} else {
        Basuke basuke = ModEntities.BASUKE.get().create(level, EntitySpawnReason.TRIGGERED);
        //?}
        if (basuke == null) {
            this.basukeUuid = null;
            return false;
        }

        BlockPos pos = this.getBlockPos();
        float yaw = level.random.nextFloat() * 360.0F;

        //? if >=1.21.5 {
        basuke.snapTo(
                pos.getX() + 0.5D,
                pos.getY() + 1.42D,
                pos.getZ() + 0.5D,
                yaw,
                0.0F
        );
        //?} else {
        /*basuke.moveTo(
                pos.getX() + 0.5D,
                pos.getY() + 1.42D,
                pos.getZ() + 0.5D,
                yaw,
                0.0F
        );
        *///?}

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

    public void cleanupBeforeRemoval() {
        Level level = this.getLevel();

        if (level instanceof ServerLevel serverLevel) {
            BlockPos pos = this.getBlockPos();

            for (ReceptorPosition receptor : ReceptorPosition.values()) {
                VocoTeleportLogic.removeOwnerAndPromote(
                        serverLevel,
                        pos,
                        receptor
                );
            }

            this.removeBasuke(serverLevel);
        }
    }

    //? if >=1.21.5 {
    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        this.cleanupBeforeRemoval();
        super.preRemoveSideEffects(pos, state);
    }
    //?}

    //? if >=1.21.6 {
    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);

        this.items.clear();
        ContainerHelper.loadAllItems(input, this.items);

        int loadedDisplayedItemCount =
                input.getIntOr(
                        TAG_DISPLAYED_ITEM_COUNT,
                        this.items.get(0).getCount()
                );

        this.normalizeDisplayedItem(
                loadedDisplayedItemCount
        );

        this.basukeVisible = input.getBooleanOr(TAG_BASUKE_VISIBLE, false);
        this.basukeUuid = readUuid(input.getStringOr(TAG_BASUKE_UUID, ""));

        this.resetFacingDefaults();
        this.resetTargetDefaults();
        this.clearAllCandleSlots();

        for (ReceptorPosition receptor : ReceptorPosition.values()) {
            int index = receptor.id();

            this.yawDegrees[index] = VocoReceptorLogic.clampYaw(
                    input.getIntOr(TAG_YAW_DEGREES[index], receptor.defaultYawDegrees())
            );

            this.pitchDegrees[index] = VocoReceptorLogic.clampPitch(
                    input.getIntOr(TAG_PITCH_DEGREES[index], receptor.defaultPitchDegrees())
            );

            Vec3 fallbackTarget = VocoTeleportLogic.getDefaultTeleportPosition(this.getBlockPos(), receptor);

            this.customTargetEnabled[index] = input.getBooleanOr(TAG_CUSTOM_TARGET[index], false);
            this.targetX[index] = input.getDoubleOr(TAG_TARGET_X[index], fallbackTarget.x);
            this.targetY[index] = input.getDoubleOr(TAG_TARGET_Y[index], fallbackTarget.y);
            this.targetZ[index] = input.getDoubleOr(TAG_TARGET_Z[index], fallbackTarget.z);

            CandleSlot slot = this.slot(receptor);

            Block candleBlock = readCandleBlock(input.getStringOr(TAG_CANDLE_BLOCK_IDS[index], ""));
            int candleCount = input.getIntOr(TAG_CANDLE_COUNTS[index], 0);

            if (candleBlock != null && candleCount > 0) {
                slot.block = candleBlock;
                slot.count = Math.max(1, Math.min(CandleBlock.MAX_CANDLES, candleCount));
                slot.lit = input.getBooleanOr(TAG_CANDLE_LIT[index], false);

                slot.hasHexColor = input.getBooleanOr(TAG_CANDLE_HAS_HEX_COLORS[index], slot.lit);
                slot.hexColor = slot.hasHexColor
                        ? normalizeHex(input.getIntOr(TAG_CANDLE_HEX_COLORS[index], DEFAULT_HEX_COLOR))
                        : UNSET_HEX_COLOR;
            }
        }

        this.latestHexReceptorId = VocoReceptorLogic.clampReceptorId(
                input.getIntOr(TAG_LATEST_HEX_RECEPTOR_ID, ReceptorPosition.NORTH_EAST.id())
        );

        this.latestHexColor = readHexOrUnset(input, TAG_LATEST_HEX_COLOR);

        if (!this.hasLatestHexColor()) {
            this.refreshLatestHexFromLitCandles();
        }
    }

    //?} else {
    /*@Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);

        this.items.clear();
        ContainerHelper.loadAllItems(tag, this.items, provider);

        int loadedDisplayedItemCount =
                readInt(
                        tag,
                        TAG_DISPLAYED_ITEM_COUNT,
                        this.items.get(0).getCount()
                );

        this.normalizeDisplayedItem(
                loadedDisplayedItemCount
        );

        this.basukeVisible = readBoolean(tag, TAG_BASUKE_VISIBLE, false);
        this.basukeUuid = readUuid(readString(tag, TAG_BASUKE_UUID, ""));

        this.resetFacingDefaults();
        this.resetTargetDefaults();
        this.clearAllCandleSlots();

        for (ReceptorPosition receptor : ReceptorPosition.values()) {
            int index = receptor.id();

            this.yawDegrees[index] = VocoReceptorLogic.clampYaw(
                    readInt(tag, TAG_YAW_DEGREES[index], receptor.defaultYawDegrees())
            );

            this.pitchDegrees[index] = VocoReceptorLogic.clampPitch(
                    readInt(tag, TAG_PITCH_DEGREES[index], receptor.defaultPitchDegrees())
            );

            Vec3 fallbackTarget = VocoTeleportLogic.getDefaultTeleportPosition(this.getBlockPos(), receptor);

            this.customTargetEnabled[index] = readBoolean(tag, TAG_CUSTOM_TARGET[index], false);
            this.targetX[index] = readDouble(tag, TAG_TARGET_X[index], fallbackTarget.x);
            this.targetY[index] = readDouble(tag, TAG_TARGET_Y[index], fallbackTarget.y);
            this.targetZ[index] = readDouble(tag, TAG_TARGET_Z[index], fallbackTarget.z);

            CandleSlot slot = this.slot(receptor);

            Block candleBlock = readCandleBlock(readString(tag, TAG_CANDLE_BLOCK_IDS[index], ""));
            int candleCount = readInt(tag, TAG_CANDLE_COUNTS[index], 0);

            if (candleBlock != null && candleCount > 0) {
                slot.block = candleBlock;
                slot.count = Math.max(1, Math.min(CandleBlock.MAX_CANDLES, candleCount));
                slot.lit = readBoolean(tag, TAG_CANDLE_LIT[index], false);

                slot.hasHexColor = readBoolean(tag, TAG_CANDLE_HAS_HEX_COLORS[index], slot.lit);
                slot.hexColor = slot.hasHexColor
                        ? normalizeHex(readInt(tag, TAG_CANDLE_HEX_COLORS[index], DEFAULT_HEX_COLOR))
                        : UNSET_HEX_COLOR;
            }
        }

        this.latestHexReceptorId = VocoReceptorLogic.clampReceptorId(
                readInt(tag, TAG_LATEST_HEX_RECEPTOR_ID, ReceptorPosition.NORTH_EAST.id())
        );

        this.latestHexColor = readHexOrUnset(tag, TAG_LATEST_HEX_COLOR);

        if (!this.hasLatestHexColor()) {
            this.refreshLatestHexFromLitCandles();
        }
    }


    *///?}

    //? if >=1.21.6 {
    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);

        ContainerHelper.saveAllItems(output, this.items, true);

        if (this.hasDisplayedItem()) {
            output.putInt(
                    TAG_DISPLAYED_ITEM_COUNT,
                    this.displayedItemCount
            );
        }

        output.putBoolean(TAG_BASUKE_VISIBLE, this.basukeVisible);

        if (this.basukeUuid != null) {
            output.putString(TAG_BASUKE_UUID, this.basukeUuid.toString());
        }

        if (this.hasLatestHexColor()) {
            output.putInt(TAG_LATEST_HEX_COLOR, this.latestHexColor);
            output.putInt(TAG_LATEST_HEX_RECEPTOR_ID, this.latestHexReceptorId);
        }

        for (ReceptorPosition receptor : ReceptorPosition.values()) {
            int index = receptor.id();
            CandleSlot slot = this.slot(receptor);

            output.putInt(TAG_YAW_DEGREES[index], this.yawDegrees[index]);
            output.putInt(TAG_PITCH_DEGREES[index], this.pitchDegrees[index]);

            output.putBoolean(TAG_CUSTOM_TARGET[index], this.customTargetEnabled[index]);
            output.putDouble(TAG_TARGET_X[index], this.targetX[index]);
            output.putDouble(TAG_TARGET_Y[index], this.targetY[index]);
            output.putDouble(TAG_TARGET_Z[index], this.targetZ[index]);

            if (slot.hasCandle()) {
                ResourceLocation candleId = BuiltInRegistries.BLOCK.getKey(slot.block);

                output.putString(TAG_CANDLE_BLOCK_IDS[index], candleId.toString());
                output.putInt(TAG_CANDLE_COUNTS[index], slot.count);
                output.putBoolean(TAG_CANDLE_LIT[index], slot.lit);
                output.putBoolean(TAG_CANDLE_HAS_HEX_COLORS[index], slot.hasHexColor);

                if (slot.hasHexColor) {
                    output.putInt(TAG_CANDLE_HEX_COLORS[index], slot.hexColor);
                }
            }
        }
    }

    //?} else {
    /*@Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);

        ContainerHelper.saveAllItems(tag, this.items, provider);

        if (this.hasDisplayedItem()) {
            tag.putInt(
                    TAG_DISPLAYED_ITEM_COUNT,
                    this.displayedItemCount
            );
        }

        tag.putBoolean(TAG_BASUKE_VISIBLE, this.basukeVisible);

        if (this.basukeUuid != null) {
            tag.putString(TAG_BASUKE_UUID, this.basukeUuid.toString());
        }

        if (this.hasLatestHexColor()) {
            tag.putInt(TAG_LATEST_HEX_COLOR, this.latestHexColor);
            tag.putInt(TAG_LATEST_HEX_RECEPTOR_ID, this.latestHexReceptorId);
        }

        for (ReceptorPosition receptor : ReceptorPosition.values()) {
            int index = receptor.id();
            CandleSlot slot = this.slot(receptor);

            tag.putInt(TAG_YAW_DEGREES[index], this.yawDegrees[index]);
            tag.putInt(TAG_PITCH_DEGREES[index], this.pitchDegrees[index]);

            tag.putBoolean(TAG_CUSTOM_TARGET[index], this.customTargetEnabled[index]);
            tag.putDouble(TAG_TARGET_X[index], this.targetX[index]);
            tag.putDouble(TAG_TARGET_Y[index], this.targetY[index]);
            tag.putDouble(TAG_TARGET_Z[index], this.targetZ[index]);

            if (slot.hasCandle()) {
                ResourceLocation candleId = BuiltInRegistries.BLOCK.getKey(slot.block);

                tag.putString(TAG_CANDLE_BLOCK_IDS[index], candleId.toString());
                tag.putInt(TAG_CANDLE_COUNTS[index], slot.count);
                tag.putBoolean(TAG_CANDLE_LIT[index], slot.lit);
                tag.putBoolean(TAG_CANDLE_HAS_HEX_COLORS[index], slot.hasHexColor);

                if (slot.hasHexColor) {
                    tag.putInt(TAG_CANDLE_HEX_COLORS[index], slot.hexColor);
                }
            }
        }
    }


    *///?}

    //? if >=1.21.5 {
    @Override
    protected void applyImplicitComponents(DataComponentGetter input) {
        super.applyImplicitComponents(input);

        input.getOrDefault(
                DataComponents.CONTAINER,
                ItemContainerContents.EMPTY
        ).copyInto(this.items);

        this.normalizeDisplayedItem(
                this.items.get(0).getCount()
        );

        Integer savedHex = input.get(ModDataComponents.HEX_COLOR.get());
        this.latestHexColor = savedHex == null ? UNSET_HEX_COLOR : normalizeHex(savedHex);
    }
    //?} else {
    /*@Override
    protected void applyImplicitComponents(DataComponentInput input) {
        super.applyImplicitComponents(input);

        input.getOrDefault(
                DataComponents.CONTAINER,
                ItemContainerContents.EMPTY
        ).copyInto(this.items);

        this.normalizeDisplayedItem(
                this.items.get(0).getCount()
        );

        Integer savedHex = input.get(ModDataComponents.HEX_COLOR.get());
        this.latestHexColor = savedHex == null ? UNSET_HEX_COLOR : normalizeHex(savedHex);
    }
    *///?}

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        components.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(this.items));

        if (this.hasLatestHexColor()) {
            components.set(ModDataComponents.HEX_COLOR.get(), this.latestHexColor);
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

    private void normalizeDisplayedItem(
            int requestedCount
    ) {
        ItemStack displayed =
                this.items.get(0);

        if (displayed.isEmpty() || requestedCount <= 0) {
            this.items.set(0, ItemStack.EMPTY);
            this.displayedItemCount = 0;
            return;
        }

        displayed.setCount(1);

        this.displayedItemCount =
                displayed.getMaxStackSize() <= 1
                        ? 1
                        : requestedCount;
    }

    private void clearAllCandleSlots() {
        for (CandleSlot slot : this.candleSlots) {
            slot.clear();
        }
    }

    public static int normalizeHex(int hexColor) {
        return VocoReceptorLogic.normalizeHex(hexColor);
    }

    //? if >=1.21.6 {
    private static int readHexOrUnset(ValueInput input, String tag) {
        int loaded = input.getIntOr(tag, UNSET_HEX_COLOR);
        return loaded == UNSET_HEX_COLOR ? UNSET_HEX_COLOR : normalizeHex(loaded);
    }

    //?} else {
    /*private static int readHexOrUnset(CompoundTag input, String tag) {
        int loaded = readInt(input, tag, UNSET_HEX_COLOR);
        return loaded == UNSET_HEX_COLOR ? UNSET_HEX_COLOR : normalizeHex(loaded);
    }
    *///?}

    private static int readInt(CompoundTag tag, String key, int fallback) {
        //? if >=1.21.5 {
        return tag.getIntOr(key, fallback);
        //?} else {
        /*return tag.contains(key, Tag.TAG_ANY_NUMERIC) ? tag.getInt(key) : fallback;
        *///?}
    }

    private static double readDouble(CompoundTag tag, String key, double fallback) {
        //? if >=1.21.5 {
        return tag.getDoubleOr(key, fallback);
        //?} else {
        /*return tag.contains(key, Tag.TAG_ANY_NUMERIC) ? tag.getDouble(key) : fallback;
        *///?}
    }

    private static boolean readBoolean(CompoundTag tag, String key, boolean fallback) {
        //? if >=1.21.5 {
        return tag.getBooleanOr(key, fallback);
        //?} else {
        /*return tag.contains(key, Tag.TAG_ANY_NUMERIC) ? tag.getBoolean(key) : fallback;
        *///?}
    }

    private static String readString(CompoundTag tag, String key, String fallback) {
        //? if >=1.21.5 {
        return tag.getStringOr(key, fallback);
        //?} else {
        /*return tag.contains(key, Tag.TAG_STRING) ? tag.getString(key) : fallback;
        *///?}
    }

    private static boolean isValidCandleBlock(@Nullable Block block) {
        return block instanceof CandleBlock;
    }

    private static Block normalizeCandleBlock(Block block) {
        if (block instanceof PearlCandleBlock pearlCandleBlock) {
            return pearlCandleBlock.getVanillaCandleBlock();
        }

        return block;
    }

    @Nullable
    private static Block readCandleBlock(String idString) {
        if (idString.isEmpty()) {
            return null;
        }

        ResourceLocation id;
        try {
            id = ResourceLocation.parse(idString);
        } catch (Exception ignored) {
            return null;
        }

        //? if <1.21.2 {
        /*Block block = BuiltInRegistries.BLOCK.get(id);
        *///?} else {
        Block block = BuiltInRegistries.BLOCK.getValue(id);
        //?}
        if (block == null || block == Blocks.AIR) {
            return null;
        }

        block = normalizeCandleBlock(block);

        return isValidCandleBlock(block) ? block : null;
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

    private static final class CandleSlot {
        @Nullable
        private Block block = null;
        private int count = 0;
        private boolean lit = false;
        private boolean hasHexColor = false;
        private int hexColor = UNSET_HEX_COLOR;

        private boolean hasCandle() {
            return this.block != null && this.count > 0;
        }

        private void clear() {
            this.block = null;
            this.count = 0;
            this.lit = false;
            this.hasHexColor = false;
            this.hexColor = UNSET_HEX_COLOR;
        }
    }
}
