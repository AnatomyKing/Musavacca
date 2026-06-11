package space.anatomyuniverse.musavacca.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoReceptorLogic.ReceptorPosition;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoTableCandleLogic;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoTableLogic;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoTableVoxelShapes;
import space.anatomyuniverse.musavacca.block.entity.custom.VocoTableBlockEntity;

public final class VocoTableBlock extends Block implements EntityBlock {
    public static final BooleanProperty LIT_NORTH_EAST = VocoTableLogic.LIT_NORTH_EAST;
    public static final BooleanProperty LIT_NORTH_WEST = VocoTableLogic.LIT_NORTH_WEST;
    public static final BooleanProperty LIT_SOUTH_EAST = VocoTableLogic.LIT_SOUTH_EAST;
    public static final BooleanProperty LIT_SOUTH_WEST = VocoTableLogic.LIT_SOUTH_WEST;

    public static final BooleanProperty PORTAL_NORTH_EAST = VocoTableLogic.PORTAL_NORTH_EAST;
    public static final BooleanProperty PORTAL_NORTH_WEST = VocoTableLogic.PORTAL_NORTH_WEST;
    public static final BooleanProperty PORTAL_SOUTH_EAST = VocoTableLogic.PORTAL_SOUTH_EAST;
    public static final BooleanProperty PORTAL_SOUTH_WEST = VocoTableLogic.PORTAL_SOUTH_WEST;

    public static final BooleanProperty ROTARY_DIALERS = VocoTableLogic.ROTARY_DIALERS;

    public static final BooleanProperty[] RECEPTOR_LIGHTS = VocoTableLogic.RECEPTOR_LIGHTS;
    public static final BooleanProperty[] RECEPTOR_PORTALS = VocoTableLogic.RECEPTOR_PORTALS;

    public VocoTableBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(VocoTableLogic.defaultState(this.stateDefinition.any()));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new VocoTableBlockEntity(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        VocoTableLogic.addProperties(builder);
    }

    public static boolean hasAnyReceptorLit(BlockState state) {
        return VocoTableLogic.hasAnyReceptorLit(state);
    }

    public static BooleanProperty lightProperty(ReceptorPosition receptor) {
        return VocoTableLogic.lightProperty(receptor);
    }

    public static BooleanProperty portalProperty(ReceptorPosition receptor) {
        return VocoTableLogic.portalProperty(receptor);
    }

    public static void syncPortalStateFromCandles(Level level, BlockPos pos, ReceptorPosition receptor) {
        VocoTableCandleLogic.syncPortalStateFromCandles(level, pos, receptor);
    }

    @Override
    protected void attack(BlockState state, Level level, BlockPos pos, Player player) {
        if (!level.isClientSide() && VocoTableCandleLogic.breakLookedAtCandle(level, pos, player)) {
            return;
        }

        super.attack(state, level, pos, player);
    }

    @Override
    protected InteractionResult useWithoutItem(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            BlockHitResult hit
    ) {
        return VocoTableLogic.useWithoutItem(state, level, pos, player, hit);
    }

    @Override
    protected InteractionResult useItemOn(
            ItemStack stack,
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hit
    ) {
        return VocoTableLogic.useItemOn(stack, state, level, pos, player, hand, hit);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        VocoTableCandleLogic.animateTick(level, pos, random);
    }

    @Override
    protected VoxelShape getShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        return VocoTableVoxelShapes.shape(state, level, pos);
    }

    @Override
    protected VoxelShape getCollisionShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        return VocoTableVoxelShapes.shape(state, level, pos);
    }
}