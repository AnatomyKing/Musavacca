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
import net.minecraft.tags.BlockTags;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.Containers;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
//? if >=1.21.2 {
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.RecipePropertySet;
import net.minecraft.world.item.crafting.SingleRecipeInput;
//?}
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import space.anatomyuniverse.musavacca.block.custom.PearlCandleBlock;
import space.anatomyuniverse.musavacca.block.custom.logic.PearlCandleConversion;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoPostCandleLogic;
import space.anatomyuniverse.musavacca.block.entity.ModBlockEntities;
import space.anatomyuniverse.musavacca.component.ModDataComponents;
import space.anatomyuniverse.musavacca.particle.tinted.OriginalCandleParticles;
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
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.util.ProblemReporter;
//?}

public class PearlCandleBlockEntity extends BlockEntity implements HexSource {
    private BlockState originalState;
    private int hexColor = MusavaccaTints.NO_TINT;
    private CampfireBlockEntity campfire;
    //? if >=1.21.2 {
    private RecipeManager.CachedCheck<SingleRecipeInput, CampfireCookingRecipe> campfireRecipes;
    //?}

    public PearlCandleBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PEARL_CANDLE_BLOCK_ENTITY.get(), pos, state);
    }

    // The Pearl Candle is the only block entity registered in the world.
    // This private vanilla object owns the four slots, cooking timers and native logic.
    @org.jetbrains.annotations.Nullable
    public CampfireBlockEntity campfire() {
        if (originalState == null || !originalState.is(BlockTags.CAMPFIRES)) return null;
        BlockState state = campfireState();
        if (campfire == null) campfire = new CampfireBlockEntity(getBlockPos(), state);
        else if (campfire.getBlockState() != state) campfire.setBlockState(state);
        if (getLevel() != null && campfire.getLevel() != getLevel()) campfire.setLevel(getLevel());
        return campfire;
    }

    private BlockState campfireState() {
        BlockState original = originalState();
        // A valid vanilla state is required by the vanilla CampfireBlockEntity type.
        Direction facing = original.hasProperty(BlockStateProperties.HORIZONTAL_FACING)
                ? original.getValue(BlockStateProperties.HORIZONTAL_FACING) : Direction.NORTH;
        return Blocks.CAMPFIRE.defaultBlockState()
                .setValue(CampfireBlock.FACING, facing)
                .setValue(CampfireBlock.LIT, PearlCandleConversion.value(original, "lit"))
                .setValue(CampfireBlock.WATERLOGGED, PearlCandleConversion.value(original, "waterlogged"))
                .setValue(CampfireBlock.SIGNAL_FIRE, PearlCandleConversion.value(original, "signal_fire"));
    }

    public static void particleTick(Level level, BlockPos pos, BlockState state, PearlCandleBlockEntity candle) {
        CampfireBlockEntity campfire = candle.campfire();
        if (campfire == null || !candle.hasHexColor() || !state.getValue(CandleBlock.LIT)
                || state.getValue(CandleBlock.WATERLOGGED)) return;
        var previous = OriginalCandleParticles.swap(OriginalCandleParticles.context(candle.originalState(), candle.getHexColor()));
        try {
            CampfireBlockEntity.particleTick(level, pos, campfire.getBlockState(), campfire);
        } finally {
            OriginalCandleParticles.swap(previous);
        }
    }

    public static void cookTick(Level level, BlockPos pos, BlockState state, PearlCandleBlockEntity candle) {
        if (!(level instanceof ServerLevel server)) return;
        CampfireBlockEntity campfire = candle.campfire();
        if (campfire == null || !state.getValue(CandleBlock.LIT) || state.getValue(CandleBlock.WATERLOGGED)) return;
        int occupied = occupiedSlots(campfire);
        if (occupied == 0) return;
        // Pass the actual world state for native world notifications.
        //? if >=1.21.2 {
        if (candle.campfireRecipes == null) candle.campfireRecipes = RecipeManager.createCheck(RecipeType.CAMPFIRE_COOKING);
        CampfireBlockEntity.cookTick(server, pos, state, campfire, candle.campfireRecipes);
        //?} else {
        /*CampfireBlockEntity.cookTick(server, pos, state, campfire);
        *///?}
        if (occupied != occupiedSlots(campfire)) candle.markChangedAndSync();
    }

    private static int occupiedSlots(CampfireBlockEntity campfire) {
        int count = 0;
        for (ItemStack stack : campfire.getItems()) if (!stack.isEmpty()) count++;
        return count;
    }

    public InteractionResult placeCampfireFood(Player player, ItemStack stack) {
        CampfireBlockEntity campfire = campfire();
        Level level = getLevel();
        if (campfire == null || level == null || stack.isEmpty()
                || occupiedSlots(campfire) >= campfire.getItems().size()) return InteractionResult.PASS;
        //? if >=1.21.2 {
        if (!level.recipeAccess().propertySet(RecipePropertySet.CAMPFIRE_INPUT).test(stack)) return InteractionResult.PASS;
        if (!(level instanceof ServerLevel server)) return InteractionResult.SUCCESS;
        if (!campfire.placeFood(server, player, stack)) return InteractionResult.PASS;
        //?} else {
        /*var recipe = campfire.getCookableRecipe(stack);
        if (recipe.isEmpty()) return InteractionResult.PASS;
        if (level.isClientSide()) return InteractionResult.SUCCESS;
        if (!campfire.placeFood(player, stack, recipe.get().value().getCookingTime())) return InteractionResult.PASS;
        *///?}
        player.awardStat(Stats.INTERACT_WITH_CAMPFIRE);
        markChangedAndSync();
        return InteractionResult.SUCCESS;
    }

    public static void loadCampfireData(CampfireBlockEntity target, CompoundTag data, Level level) {
        //? if >=1.21.6 {
        target.loadWithComponents(TagValueInput.create(ProblemReporter.DISCARDING, level.registryAccess(), data));
        //?} else {
        /*target.loadWithComponents(data, level.registryAccess());
        *///?}
    }

    public void importCampfireData(CompoundTag data) {
        CampfireBlockEntity campfire = campfire();
        if (campfire == null || getLevel() == null) return;
        loadCampfireData(campfire, data, getLevel());
        markChangedAndSync();
    }

    public void dropCampfireFood() {
        if (!(getLevel() instanceof ServerLevel server) || campfire == null) return;
        Containers.dropContents(server, getBlockPos(), campfire.getItems());
        campfire.clearContent();
    }

    public boolean restoreOriginal(Level level, BlockState original) {
        CampfireBlockEntity campfire = campfire();
        CompoundTag data = campfire == null ? null : campfire.saveWithoutMetadata(level.registryAccess());
        // Prevent removal hooks from dropping food that is being transferred.
        if (campfire != null) campfire.clearContent();
        if (!level.setBlock(getBlockPos(), original, Block.UPDATE_ALL)) {
            if (data != null) loadCampfireData(campfire, data, level);
            return false;
        }
        if (data != null && level.getBlockEntity(getBlockPos()) instanceof CampfireBlockEntity restored) {
            loadCampfireData(restored, data, level);
            restored.setChanged();
            level.sendBlockUpdated(getBlockPos(), original, original, Block.UPDATE_CLIENTS);
        } else if (data != null && level instanceof ServerLevel server) {
            // A tagged campfire without a native inventory cannot receive the four slots.
            loadCampfireData(campfire, data, level);
            Containers.dropContents(server, getBlockPos(), campfire.getItems());
            campfire.clearContent();
        }
        return true;
    }

    //? if >=1.21.5 {
    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        dropCampfireFood();
        super.preRemoveSideEffects(pos, state);
    }
    //?}

    public BlockState originalState() {
        BlockState state = originalState != null ? originalState : PearlCandleBlock.fallbackState(getBlockState());
        state = PearlCandleConversion.withFlag(state, "lit", getBlockState().getValue(CandleBlock.LIT));
        state = PearlCandleConversion.withFlag(state, "waterlogged", getBlockState().getValue(CandleBlock.WATERLOGGED));
        if (state.is(BlockTags.CAMPFIRES) && (state.is(Blocks.CAMPFIRE) || state.is(Blocks.SOUL_CAMPFIRE)) && getLevel() != null)
            state = PearlCandleConversion.withFlag(state, "signal_fire", getLevel().getBlockState(getBlockPos().below()).is(Blocks.HAY_BLOCK));
        return state;
    }

    public void setOriginalState(BlockState state) {
        this.setCandle(state, this.hexColor);
    }

    public void setCandle(BlockState state, int hexColor) {
        if (state.getBlock() instanceof PearlCandleBlock) return;
        this.originalState = state;
        if (!state.is(BlockTags.CAMPFIRES)) this.campfire = null;
        this.hexColor = MusavaccaTints.resolve(hexColor);
        if (getLevel() != null) getLevel().getChunkSource().getLightEngine().checkBlock(getBlockPos());
        this.markChangedAndSync();
    }

    private void loadOriginal(BlockState state) {
        this.originalState = state != null && !(state.getBlock() instanceof PearlCandleBlock) ? state : null;
        this.campfire = null;
    }

    public boolean hasHexColor() {
        return this.hexColor != MusavaccaTints.NO_TINT;
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
        this.refreshPostBelow();
    }

    private void refreshPostBelow() {
        Level level = this.getLevel();
        if (level == null || level.isClientSide()) return;
        VocoPostCandleLogic.refreshPostBelowCandle(level, this.getBlockPos());
    }

    private void markChangedAndSync() {
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
    //?} else {
    /*protected void loadAdditional(CompoundTag input, HolderLookup.Provider provider) {
    *///?}
        //? if >=1.21.6 {
        super.loadAdditional(input);
        this.hexColor = readHexOrUnset(input);
        loadOriginal(input.read("OriginalCandle", BlockState.CODEC).orElse(null));
        if (campfire() != null) input.child("Campfire").ifPresent(campfire::loadWithComponents);
        //?} else {
        /*super.loadAdditional(input, provider);
        this.hexColor = readHexOrUnset(input);
        loadOriginal(
                input.get("OriginalCandle") == null
                        ? null
                        : BlockState.CODEC.parse(NbtOps.INSTANCE, input.get("OriginalCandle")).result().orElse(null));
        if (campfire() != null && input.get("Campfire") instanceof CompoundTag data) campfire.loadWithComponents(data, provider);
        *///?}
    }

    @Override
    //? if >=1.21.6 {
    protected void saveAdditional(ValueOutput output) {
    //?} else {
    /*protected void saveAdditional(CompoundTag output, HolderLookup.Provider provider) {
    *///?}
        //? if >=1.21.6 {
        super.saveAdditional(output);
        //?} else {
        /*super.saveAdditional(output, provider);
        *///?}
        if (originalState != null) {
            //? if >=1.21.6 {
            output.store("OriginalCandle", BlockState.CODEC, originalState);
            //?} else {
            /*BlockState.CODEC.encodeStart(NbtOps.INSTANCE, originalState).result().ifPresent(value -> output.put("OriginalCandle", value));
            *///?}
        }
        if (campfire != null) {
            //? if >=1.21.6 {
            campfire.saveWithoutMetadata(output.child("Campfire"));
            //?} else {
            /*output.put("Campfire", campfire.saveWithoutMetadata(provider));
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
    //?} else {
    /*public void handleUpdateTag(CompoundTag input, HolderLookup.Provider provider) {
    *///?}
        //? if >=1.21.6 {
        super.handleUpdateTag(input);
        //?} else {
        /*super.handleUpdateTag(input, provider);
        *///?}
        this.rerenderClientNow();
    }

    @Override
    //? if >=1.21.6 {
    public void onDataPacket(Connection connection, ValueInput input) {
    //?} else {
    /*public void onDataPacket(Connection connection, ClientboundBlockEntityDataPacket packet, HolderLookup.Provider provider) {
    *///?}
        //? if >=1.21.6 {
        super.onDataPacket(connection, input);
        //?} else {
        /*super.onDataPacket(connection, packet, provider);
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
