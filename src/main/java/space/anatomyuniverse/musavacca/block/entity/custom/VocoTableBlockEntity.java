// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/block/entity/custom/VocoTableBlockEntity.java
package space.anatomyuniverse.musavacca.block.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;
import space.anatomyuniverse.hex.PearlHexNetwork;
import space.anatomyuniverse.musavacca.block.custom.PearlCandleBlock;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoSharedBetweenTableAndReceptorLogic;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoSharedBetweenTableAndReceptorLogic.ReceptorPosition;
import space.anatomyuniverse.musavacca.block.entity.ModBlockEntities;
import space.anatomyuniverse.musavacca.component.ModDataComponents;
import space.anatomyuniverse.musavacca.entity.ModEntities;
import space.anatomyuniverse.musavacca.entity.mob.basuke.Basuke;
import space.anatomyuniverse.musavacca.item.custom.FlintAndPearlItem;

import java.util.UUID;

public class VocoTableBlockEntity extends BlockEntity {

    private static final String TAG_BASUKE_VISIBLE = "basuke_visible";
    private static final String TAG_BASUKE_UUID = "basuke_uuid";

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

    public static final int DEFAULT_HEX_COLOR = FlintAndPearlItem.DEFAULT_HEX_COLOR;
    public static final int UNSET_HEX_COLOR = VocoSharedBetweenTableAndReceptorLogic.UNSET_HEX_COLOR;

    private final NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);

    private final int[] yawDegrees = new int[ReceptorPosition.COUNT];
    private final int[] pitchDegrees = new int[ReceptorPosition.COUNT];
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
        int clamped = VocoSharedBetweenTableAndReceptorLogic.clampYaw(yawDegrees);

        if (this.yawDegrees[index] == clamped) {
            return;
        }

        this.yawDegrees[index] = clamped;
        this.markChangedAndSync();
    }

    public void setPitchDegrees(ReceptorPosition receptor, int pitchDegrees) {
        int index = receptor.id();
        int clamped = VocoSharedBetweenTableAndReceptorLogic.clampPitch(pitchDegrees);

        if (this.pitchDegrees[index] == clamped) {
            return;
        }

        this.pitchDegrees[index] = clamped;
        this.markChangedAndSync();
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
        return this.slot(receptor).hexColor;
    }

    public int getPortalHexColorOrUnset(ReceptorPosition receptor) {
        CandleSlot slot = this.slot(receptor);
        return slot.hasCandle() && slot.lit ? slot.hexColor : UNSET_HEX_COLOR;
    }

    public void activatePortal(ReceptorPosition receptor) {
        CandleSlot slot = this.slot(receptor);

        if (!slot.hasCandle() || !slot.lit) {
            this.refreshLatestHexFromLitCandles();
            return;
        }

        this.setLatest(receptor, slot.hexColor);
        this.markChangedAndSync();
    }

    public void refreshLatestHexFromLitCandles() {
        ReceptorPosition current = ReceptorPosition.byId(this.latestHexReceptorId);
        CandleSlot currentSlot = this.slot(current);

        if (currentSlot.hasCandle() && currentSlot.lit) {
            this.latestHexColor = currentSlot.hexColor;
            return;
        }

        for (ReceptorPosition receptor : ReceptorPosition.values()) {
            CandleSlot slot = this.slot(receptor);

            if (slot.hasCandle() && slot.lit) {
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

    public int getCandleHexColorOrFallback(ReceptorPosition receptor) {
        return this.slot(receptor).hexColor;
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
            slot.hexColor = DEFAULT_HEX_COLOR;
        } else {
            slot.count = Math.min(CandleBlock.MAX_CANDLES, slot.count + 1);
        }

        this.markChangedAndSync();
        return true;
    }

    public boolean lightCandle(ReceptorPosition receptor, int hexColor) {
        CandleSlot slot = this.slot(receptor);
        if (!slot.hasCandle() || slot.lit) {
            return false;
        }

        int normalized = normalizeHex(hexColor);

        if (this.reserveCandleHexClaim(receptor, normalized).success()) {
            slot.lit = true;
            slot.hexColor = normalized;
            this.setLatest(receptor, normalized);

            this.markChangedAndSync();
            return true;
        }

        return false;
    }

    public boolean extinguishCandle(ReceptorPosition receptor) {
        CandleSlot slot = this.slot(receptor);

        if (!slot.hasCandle() || !slot.lit) {
            return false;
        }

        this.releaseCandleHexClaim(receptor);

        slot.lit = false;
        this.refreshLatestHexFromLitCandles();

        this.markChangedAndSync();
        return true;
    }

    public ItemStack removeOneCandle(ReceptorPosition receptor) {
        CandleSlot slot = this.slot(receptor);

        if (!slot.hasCandle()) {
            return ItemStack.EMPTY;
        }

        ItemStack removed = new ItemStack(slot.block.asItem());

        boolean removingLastCandle = slot.count <= 1;
        if (removingLastCandle && slot.lit) {
            this.releaseCandleHexClaim(receptor);
        }

        slot.count--;

        if (slot.count <= 0) {
            slot.clear();
            this.refreshLatestHexFromLitCandles();
        }

        this.markChangedAndSync();
        return removed;
    }

    public boolean ensureCandleHexClaim(ReceptorPosition receptor) {
        CandleSlot slot = this.slot(receptor);

        if (!slot.hasCandle() || !slot.lit) {
            return true;
        }

        return this.reserveCandleHexClaim(receptor, slot.hexColor).success();
    }

    public boolean hasOtherLitCandleWithHex(ReceptorPosition ignoredReceptor, int hexColor) {
        int normalized = normalizeHex(hexColor);

        for (ReceptorPosition receptor : ReceptorPosition.values()) {
            if (receptor == ignoredReceptor) {
                continue;
            }

            CandleSlot slot = this.slot(receptor);
            if (slot.hasCandle() && slot.lit && normalizeHex(slot.hexColor) == normalized) {
                return true;
            }
        }

        return false;
    }

    public PearlHexNetwork.ClaimResult checkCandleHexClaim(ReceptorPosition receptor, int hexColor) {
        int normalized = normalizeHex(hexColor);

        if (this.hasOtherLitCandleWithHex(receptor, normalized)) {
            return PearlHexNetwork.ClaimResult.HEX_OCCUPIED_BY_VOCO;
        }

        Level level = this.getLevel();
        if (!(level instanceof ServerLevel serverLevel)) {
            return PearlHexNetwork.ClaimResult.RESERVED;
        }

        String ownerKey = PearlHexNetwork.vocoTableCandleOwnerKey(
                serverLevel,
                this.getBlockPos(),
                receptor
        );

        return PearlHexNetwork
                .get(serverLevel.getServer())
                .checkVocoHex(serverLevel, ownerKey, normalized);
    }

    private PearlHexNetwork.ClaimResult reserveCandleHexClaim(ReceptorPosition receptor, int hexColor) {
        int normalized = normalizeHex(hexColor);

        if (this.hasOtherLitCandleWithHex(receptor, normalized)) {
            return PearlHexNetwork.ClaimResult.HEX_OCCUPIED_BY_VOCO;
        }

        Level level = this.getLevel();
        if (!(level instanceof ServerLevel serverLevel)) {
            return PearlHexNetwork.ClaimResult.RESERVED;
        }

        String ownerKey = PearlHexNetwork.vocoTableCandleOwnerKey(
                serverLevel,
                this.getBlockPos(),
                receptor
        );

        return PearlHexNetwork
                .get(serverLevel.getServer())
                .reserveVocoHex(
                        serverLevel,
                        ownerKey,
                        PearlHexNetwork.OwnerKind.VOCO_TABLE_CANDLE,
                        normalized
                );
    }


    private void releaseCandleHexClaim(ReceptorPosition receptor) {
        Level level = this.getLevel();
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        PearlHexNetwork
                .get(serverLevel.getServer())
                .release(
                        serverLevel,
                        PearlHexNetwork.vocoTableCandleOwnerKey(
                                serverLevel,
                                this.getBlockPos(),
                                receptor
                        )
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

    public boolean isBasukeVisible() {
        return this.basukeVisible;
    }

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

        this.resetFacingDefaults();
        this.clearAllCandleSlots();

        for (ReceptorPosition receptor : ReceptorPosition.values()) {
            int index = receptor.id();

            this.yawDegrees[index] = VocoSharedBetweenTableAndReceptorLogic.clampYaw(
                    input.getIntOr(TAG_YAW_DEGREES[index], receptor.defaultYawDegrees())
            );

            this.pitchDegrees[index] = VocoSharedBetweenTableAndReceptorLogic.clampPitch(
                    input.getIntOr(TAG_PITCH_DEGREES[index], receptor.defaultPitchDegrees())
            );

            CandleSlot slot = this.slot(receptor);

            Block candleBlock = readCandleBlock(input.getStringOr(TAG_CANDLE_BLOCK_IDS[index], ""));
            int candleCount = input.getIntOr(TAG_CANDLE_COUNTS[index], 0);

            if (candleBlock != null && candleCount > 0) {
                slot.block = candleBlock;
                slot.count = Math.max(1, Math.min(CandleBlock.MAX_CANDLES, candleCount));
                slot.lit = input.getBooleanOr(TAG_CANDLE_LIT[index], false);
                slot.hexColor = normalizeHex(input.getIntOr(TAG_CANDLE_HEX_COLORS[index], DEFAULT_HEX_COLOR));
            }
        }

        this.latestHexReceptorId = VocoSharedBetweenTableAndReceptorLogic.clampReceptorId(
                input.getIntOr(TAG_LATEST_HEX_RECEPTOR_ID, ReceptorPosition.NORTH_EAST.id())
        );

        this.latestHexColor = readHexOrUnset(input, TAG_LATEST_HEX_COLOR);

        if (!this.hasLatestHexColor()) {
            this.refreshLatestHexFromLitCandles();
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

        if (this.hasLatestHexColor()) {
            output.putInt(TAG_LATEST_HEX_COLOR, this.latestHexColor);
            output.putInt(TAG_LATEST_HEX_RECEPTOR_ID, this.latestHexReceptorId);
        }

        for (ReceptorPosition receptor : ReceptorPosition.values()) {
            int index = receptor.id();
            CandleSlot slot = this.slot(receptor);

            output.putInt(TAG_YAW_DEGREES[index], this.yawDegrees[index]);
            output.putInt(TAG_PITCH_DEGREES[index], this.pitchDegrees[index]);

            if (slot.hasCandle()) {
                ResourceLocation candleId = BuiltInRegistries.BLOCK.getKey(slot.block);

                output.putString(TAG_CANDLE_BLOCK_IDS[index], candleId.toString());
                output.putInt(TAG_CANDLE_COUNTS[index], slot.count);
                output.putBoolean(TAG_CANDLE_LIT[index], slot.lit);
                output.putInt(TAG_CANDLE_HEX_COLORS[index], slot.hexColor);
            }
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
        this.latestHexColor = savedHex == null ? UNSET_HEX_COLOR : normalizeHex(savedHex);
    }

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

    private void clearAllCandleSlots() {
        for (CandleSlot slot : this.candleSlots) {
            slot.clear();
        }
    }

    public static int normalizeHex(int hexColor) {
        return VocoSharedBetweenTableAndReceptorLogic.normalizeHex(hexColor);
    }

    private static int readHexOrUnset(ValueInput input, String tag) {
        int loaded = input.getIntOr(tag, UNSET_HEX_COLOR);
        return loaded == UNSET_HEX_COLOR ? UNSET_HEX_COLOR : normalizeHex(loaded);
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

        Block block = BuiltInRegistries.BLOCK.getValue(id);
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
        private int hexColor = DEFAULT_HEX_COLOR;

        private boolean hasCandle() {
            return this.block != null && this.count > 0;
        }

        private void clear() {
            this.block = null;
            this.count = 0;
            this.lit = false;
            this.hexColor = DEFAULT_HEX_COLOR;
        }
    }
}