package space.anatomyuniverse.musavacca.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import space.anatomyuniverse.musavacca.block.custom.logic.PearlFlameConversion;
import space.anatomyuniverse.musavacca.block.entity.ModBlockEntities;
import space.anatomyuniverse.musavacca.block.entity.custom.PearlCampfireBlockEntity;
//? if >=1.21.5 {
import net.minecraft.world.entity.InsideBlockEffectApplier;
//?}
//? if <1.21.2 {
/*import net.minecraft.world.ItemInteractionResult;
import space.anatomyuniverse.musavacca.block.custom.logic.InteractionResultCompat;
*///?}

public final class PearlCampfireBlock extends PearlFlameBlock.Waterlogged {
    public PearlCampfireBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockState fallbackState(BlockState state) {
        return Blocks.CAMPFIRE.defaultBlockState()
                .setValue(CampfireBlock.LIT, isLit(state))
                .setValue(CampfireBlock.WATERLOGGED, state.getValue(WATERLOGGED));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PearlCampfireBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (!isLit(state) || type != ModBlockEntities.PEARL_CAMPFIRE_BLOCK_ENTITY.get()) return null;
        if (level.isClientSide()) return (tickLevel, pos, tickState, entity) ->
                PearlCampfireBlockEntity.particleTick(tickLevel, pos, tickState, (PearlCampfireBlockEntity) entity);
        return (tickLevel, pos, tickState, entity) ->
                PearlCampfireBlockEntity.cookTick(tickLevel, pos, tickState, (PearlCampfireBlockEntity) entity);
    }

    @Override
    //? if >=1.21.2 {
    protected InteractionResult useItemOn(
            ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
    //?} else {
    /*protected ItemInteractionResult useItemOn(
            ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
    *///?}
            BlockHitResult hitResult) {
        BlockState original = originalState(state, level, pos);
        InteractionResult result = useCampfire(stack, level, pos, player, hand, original);
        //? if >=1.21.2 {
        return result;
        //?} else {
        /*return InteractionResultCompat.forItemUse(result);
        *///?}
    }

    private InteractionResult useCampfire(ItemStack stack, Level level, BlockPos pos, Player player, InteractionHand hand, BlockState original) {
        if (!(level.getBlockEntity(pos) instanceof PearlCampfireBlockEntity fire)
                || !player.getAbilities().mayBuild || player.isSpectator()) return InteractionResult.PASS;
        if (stack.is(ItemTags.SHOVELS) && PearlFlameConversion.value(original, "lit")) {
            if (!level.isClientSide() && restoreOriginal(level, pos, PearlFlameConversion.withFlag(original, "lit", false))) {
                extinguishEffects(level, pos, player);
                if (!player.getAbilities().instabuild)
                    stack.hurtAndBreak(1, player, hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
            }
            return InteractionResult.SUCCESS;
        }
        return PearlFlameConversion.value(original, "lit") ? fire.placeCampfireFood(player, stack) : InteractionResult.PASS;
    }

    @Override
    protected void extinguishEffects(LevelAccessor level, BlockPos pos, @Nullable Player player) {
        level.levelEvent(1009, pos, 0);
        level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
    }

    //? if <1.21.5 {
    /*@Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof PearlCampfireBlockEntity fire)
            fire.dropCampfireFood();
        super.onRemove(state, level, pos, newState, movedByPiston);
    }
    *///?}

    @Override
    //? if >=1.21.9 {
    /*protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier effects, boolean intersects) {
        originalState(state, level, pos).entityInside(level, pos, entity, effects, intersects);
    *///?} else if >=1.21.5 <1.21.9 {
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier effects) {
        originalState(state, level, pos).entityInside(level, pos, entity, effects);
    //?} else {
    /*protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        originalState(state, level, pos).entityInside(level, pos, entity);
    *///?}
    }

    public static int campfireLightLevel(BlockState state) {
        return state.getValue(LIT) ? 15 : 0;
    }
}
