package space.anatomyuniverse.musavacca.block.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import space.anatomyuniverse.musavacca.block.custom.PearlFlameBlock;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import space.anatomyuniverse.musavacca.block.custom.logic.PearlFlameConversion;
import space.anatomyuniverse.musavacca.block.custom.logic.PearlFlameParticles;
import space.anatomyuniverse.musavacca.block.entity.ModBlockEntities;
//? if >=1.21.2 {
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.RecipePropertySet;
import net.minecraft.world.item.crafting.SingleRecipeInput;
//?}
//? if >=1.21.6 {
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.util.ProblemReporter;
//?}

public final class PearlCampfireBlockEntity extends PearlCopycatBlockEntity {
    private CampfireBlockEntity campfire;
    //? if >=1.21.2 {
    private RecipeManager.CachedCheck<SingleRecipeInput, CampfireCookingRecipe> campfireRecipes;
    //?}

    public PearlCampfireBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PEARL_CAMPFIRE_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public BlockState originalState() {
        BlockState state = super.originalState();
        if ((state.is(Blocks.CAMPFIRE) || state.is(Blocks.SOUL_CAMPFIRE)) && getLevel() != null)
            state = PearlFlameConversion.withFlag(state, "signal_fire", getLevel().getBlockState(getBlockPos().below()).is(Blocks.HAY_BLOCK));
        return state;
    }

    @Override
    public CampfireBlockEntity originalBlockEntity() {
        BlockState state = campfireState();
        if (campfire == null) campfire = new CampfireBlockEntity(getBlockPos(), state);
        else if (campfire.getBlockState() != state) campfire.setBlockState(state);
        if (getLevel() != null && campfire.getLevel() != getLevel()) campfire.setLevel(getLevel());
        return campfire;
    }

    private BlockState campfireState() {
        BlockState original = originalState();
        Direction facing = original.hasProperty(BlockStateProperties.HORIZONTAL_FACING)
                ? original.getValue(BlockStateProperties.HORIZONTAL_FACING) : Direction.NORTH;
        return Blocks.CAMPFIRE.defaultBlockState()
                .setValue(CampfireBlock.FACING, facing)
                .setValue(CampfireBlock.LIT, PearlFlameConversion.value(original, "lit"))
                .setValue(CampfireBlock.WATERLOGGED, PearlFlameConversion.value(original, "waterlogged"))
                .setValue(CampfireBlock.SIGNAL_FIRE, PearlFlameConversion.value(original, "signal_fire"));
    }

    public static void particleTick(Level level, BlockPos pos, BlockState state, PearlCampfireBlockEntity fire) {
        if (!fire.hasHexColor() || !PearlFlameBlock.isLit(state)) return;
        CampfireBlockEntity campfire = fire.originalBlockEntity();
        PearlFlameParticles.run(level, fire.originalState(), fire.getHexColor(),
                () -> CampfireBlockEntity.particleTick(level, pos, campfire.getBlockState(), campfire));
    }

    public static void cookTick(Level level, BlockPos pos, BlockState state, PearlCampfireBlockEntity fire) {
        if (!(level instanceof ServerLevel server) || !PearlFlameBlock.isLit(state)) return;
        CampfireBlockEntity campfire = fire.originalBlockEntity();
        int occupied = occupiedSlots(campfire);
        if (occupied == 0) return;

        //? if >=1.21.2 {
        if (fire.campfireRecipes == null) fire.campfireRecipes = RecipeManager.createCheck(RecipeType.CAMPFIRE_COOKING);
        CampfireBlockEntity.cookTick(server, pos, state, campfire, fire.campfireRecipes);
        //?} else {
        /*CampfireBlockEntity.cookTick(server, pos, state, campfire);
        *///?}
        if (occupied != occupiedSlots(campfire)) fire.markChangedAndSync();
    }

    private static int occupiedSlots(CampfireBlockEntity campfire) {
        int count = 0;
        for (ItemStack stack : campfire.getItems()) if (!stack.isEmpty()) count++;
        return count;
    }

    public InteractionResult placeCampfireFood(Player player, ItemStack stack) {
        CampfireBlockEntity campfire = originalBlockEntity();
        Level level = getLevel();
        if (level == null || stack.isEmpty()
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
        CampfireBlockEntity campfire = originalBlockEntity();
        if (getLevel() == null) return;
        loadCampfireData(campfire, data, getLevel());
        markChangedAndSync();
    }

    public void dropCampfireFood() {
        if (!(getLevel() instanceof ServerLevel server) || campfire == null) return;
        Containers.dropContents(server, getBlockPos(), campfire.getItems());
        campfire.clearContent();
    }

    @Override
    public boolean restoreOriginal(Level level, BlockState original) {
        CampfireBlockEntity campfire = originalBlockEntity();
        CompoundTag data = campfire.saveWithoutMetadata(level.registryAccess());
        campfire.clearContent();
        if (!level.setBlock(getBlockPos(), original, Block.UPDATE_ALL)) {
            loadCampfireData(campfire, data, level);
            return false;
        }
        if (level.getBlockEntity(getBlockPos()) instanceof CampfireBlockEntity restored) {
            loadCampfireData(restored, data, level);
            restored.setChanged();
            level.sendBlockUpdated(getBlockPos(), original, original, Block.UPDATE_CLIENTS);
        } else if (level instanceof ServerLevel server) {
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

    @Override
    //? if >=1.21.6 {
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.campfire = null;
        input.child("Campfire").ifPresent(originalBlockEntity()::loadWithComponents);
    }
    //?} else {
    /*protected void loadAdditional(CompoundTag input, HolderLookup.Provider provider) {
        super.loadAdditional(input, provider);
        this.campfire = null;
        if (input.get("Campfire") instanceof CompoundTag data) originalBlockEntity().loadWithComponents(data, provider);
    }
    *///?}

    @Override
    //? if >=1.21.6 {
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        if (campfire != null) campfire.saveWithoutMetadata(output.child("Campfire"));
    }
    //?} else {
    /*protected void saveAdditional(CompoundTag output, HolderLookup.Provider provider) {
        super.saveAdditional(output, provider);
        if (campfire != null) output.put("Campfire", campfire.saveWithoutMetadata(provider));
    }
    *///?}
}
