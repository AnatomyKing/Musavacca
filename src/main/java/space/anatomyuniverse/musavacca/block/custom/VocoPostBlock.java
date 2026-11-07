package space.anatomyuniverse.musavacca.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
//? if <1.21.2 {
/*import net.minecraft.world.ItemInteractionResult;
*///?}
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
//? if >=1.21.2 {
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
//?} else {
/*import net.minecraft.world.level.LevelAccessor;
*///?}
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
//? if >=1.21.11 {
/*import net.minecraft.world.level.block.RenderShape;
*///?}
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import space.anatomyuniverse.musavacca.block.custom.logic.InteractionResultCompat;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoPostCandleLogic;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoPostHitboxes;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoReceptorLogic.ReceptorPosition;
import space.anatomyuniverse.musavacca.block.entity.custom.VocoPostBlockEntity;

import space.anatomyuniverse.musavacca.block.custom.logic.PearlSlotIgnition;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoReceptorLogic;

public final class VocoPostBlock extends HorizontalDirectionalBlock implements EntityBlock {
    //? if <26.3 {
    public static final MapCodec<VocoPostBlock> CODEC = simpleCodec(VocoPostBlock::new);
    //?}
    public static final BooleanProperty LIT = BooleanProperty.create("lit");
    public static final BooleanProperty PORTAL = BooleanProperty.create("portal");

    public VocoPostBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(LIT, false).setValue(PORTAL, false));
    }

    //? if <26.3 {
    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    //?}
    //? if >=1.21.11 {
    /*@Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    *///?}
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new VocoPostBlockEntity(pos, state);
    }

    //? if <1.21.5 {
    /*@Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (state.getBlock() != newState.getBlock() && level.getBlockEntity(pos) instanceof VocoPostBlockEntity blockEntity) {
            blockEntity.cleanupBeforeRemoval();
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    *///?}
    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, LIT, PORTAL);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return VocoPostHitboxes.SHAPE;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        ReceptorPosition receptor = receptorHit(state, pos, hit);
        if (receptor == null) return InteractionResult.PASS;
        if (VocoReceptorLogic.tryOpenCameraEditor(level, pos, player, receptor)) return InteractionResult.SUCCESS;
        if (!VocoReceptorLogic.isCompletelyEmptyHanded(player)) return InteractionResult.PASS;
        PearlSlotIgnition.Slot pearlSlot = VocoReceptorLogic.pearlSlot(VocoPostBlock.LIT, VocoPostBlock.PORTAL, receptor);
        if (!PearlSlotIgnition.isLit(state, pearlSlot)) {
            if (!level.isClientSide() && PearlSlotIgnition.igniteFromBalance(state, level, pos, player, pearlSlot)) {
                VocoPostCandleLogic.refreshPortalAt(level, pos);
            }
            return InteractionResult.SUCCESS;
        }
        if (!state.getValue(VocoPostBlock.PORTAL) && level.isClientSide()) {
            VocoReceptorLogic.showNeedsPortalMessage(player);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    //? if >=1.21.2 {
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
            BlockHitResult hit) {
    //?} else {
    /*protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
            BlockHitResult hit) {
    *///?}
        InteractionResult result = this.handleItemUse(stack, state, level, pos, player, hand, hit);
        //? if >=1.21.2 {
        return result;
        //?} else {
        /*return InteractionResultCompat.forItemUse(result);
        *///?}
    }

    private InteractionResult handleItemUse(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
            BlockHitResult hit) {
        //? if >=1.21.2 {
        if (stack.isEmpty()) return InteractionResult.TRY_WITH_EMPTY_HAND;
        //?} else {
        /*if (stack.isEmpty()) return InteractionResult.PASS;
        *///?}
        ReceptorPosition receptor = receptorHit(state, pos, hit);
        if (receptor == null) return InteractionResult.PASS;
        if (VocoReceptorLogic.tryOpenCameraEditor(level, pos, player, receptor)) return InteractionResult.SUCCESS;
        PearlSlotIgnition.Slot pearlSlot = VocoReceptorLogic.pearlSlot(VocoPostBlock.LIT, VocoPostBlock.PORTAL, receptor);
        InteractionResult result = PearlSlotIgnition.handleHeldItemUse(stack, state, level, pos, player, hand, pearlSlot);
        if (result == InteractionResult.SUCCESS && !level.isClientSide()) {
            VocoPostCandleLogic.refreshPortalAt(level, pos);
        }
        return result;
    }

    @Nullable
    private static ReceptorPosition receptorHit(BlockState state, BlockPos pos, BlockHitResult hit) {
        return VocoPostHitboxes.isReceptor(pos, hit) ? VocoPostBlock.receptorPosition(state) : null;
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        if (!level.isClientSide()) {
            VocoPostCandleLogic.refreshPortalAt(level, pos);
        }
    }

    @Override
    //? if >=1.21.2 {
    protected BlockState updateShape(BlockState state, LevelReader levelReader, ScheduledTickAccess scheduledTickAccess, BlockPos pos, Direction direction,
            BlockPos neighborPos, BlockState neighborState, RandomSource random) {
    //?} else {
    /*protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor levelReader, BlockPos pos,
            BlockPos neighborPos) {
    *///?}
        return VocoPostCandleLogic.updateShape(state, levelReader, pos, direction);
    }

    public static ReceptorPosition receptorPosition(BlockState state) {
        Direction facing = state.hasProperty(FACING) ? state.getValue(FACING) : Direction.NORTH;
        return switch (facing) {
        case NORTH -> ReceptorPosition.NORTH_EAST;
        case EAST -> ReceptorPosition.SOUTH_EAST;
        case SOUTH -> ReceptorPosition.SOUTH_WEST;
        case WEST -> ReceptorPosition.NORTH_WEST;
        default -> ReceptorPosition.NORTH_EAST;
        };
    }
}
