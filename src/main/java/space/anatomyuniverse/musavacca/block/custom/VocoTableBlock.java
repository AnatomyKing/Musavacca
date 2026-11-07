package space.anatomyuniverse.musavacca.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
//? if <1.21.2 {
/*import net.minecraft.world.ItemInteractionResult;
*///?}
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
//? if >=1.21.11 {
/*import net.minecraft.world.level.block.RenderShape;
*///?}
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import space.anatomyuniverse.musavacca.block.custom.logic.InteractionResultCompat;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoReceptorLogic.ReceptorPosition;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoTableCandleLogic;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoTableVoxelShapes;
import space.anatomyuniverse.musavacca.block.entity.custom.VocoTableBlockEntity;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.BlockItem;
import space.anatomyuniverse.musavacca.basuke.summon.BasukeSummon;
import space.anatomyuniverse.musavacca.block.custom.logic.PearlSlotIgnition;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoReceptorLogic;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoTableCandle;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoTableHitboxes;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoTableItemDisplayLogic;
import space.anatomyuniverse.musavacca.gui.menu.VocoDialerMenu;

public final class VocoTableBlock extends Block implements EntityBlock {
    public static final BooleanProperty LIT_NORTH_EAST = BooleanProperty.create("lit_north_east");
    public static final BooleanProperty LIT_NORTH_WEST = BooleanProperty.create("lit_north_west");
    public static final BooleanProperty LIT_SOUTH_EAST = BooleanProperty.create("lit_south_east");
    public static final BooleanProperty LIT_SOUTH_WEST = BooleanProperty.create("lit_south_west");
    public static final BooleanProperty PORTAL_NORTH_EAST = BooleanProperty.create("portal_north_east");
    public static final BooleanProperty PORTAL_NORTH_WEST = BooleanProperty.create("portal_north_west");
    public static final BooleanProperty PORTAL_SOUTH_EAST = BooleanProperty.create("portal_south_east");
    public static final BooleanProperty PORTAL_SOUTH_WEST = BooleanProperty.create("portal_south_west");
    public static final BooleanProperty ROTARY_DIALERS = BooleanProperty.create("rotary_dialers");
    public static final IntegerProperty CANDLE_LIGHT = IntegerProperty.create("candle_light", 0, 5);
    public static final BooleanProperty[] RECEPTOR_LIGHTS = { LIT_NORTH_EAST, LIT_NORTH_WEST, LIT_SOUTH_EAST, LIT_SOUTH_WEST };
    public static final BooleanProperty[] RECEPTOR_PORTALS = { PORTAL_NORTH_EAST, PORTAL_NORTH_WEST, PORTAL_SOUTH_EAST, PORTAL_SOUTH_WEST };

    public VocoTableBlock(Properties properties) {
        super(properties.dynamicShape());
        BlockState state = this.stateDefinition.any();
        for (BooleanProperty property : RECEPTOR_LIGHTS) state = state.setValue(property, false);
        for (BooleanProperty property : RECEPTOR_PORTALS) state = state.setValue(property, false);
        this.registerDefaultState(state.setValue(ROTARY_DIALERS, false).setValue(CANDLE_LIGHT, 0));
    }

    //? if >=1.21.11 {
    /*@Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    *///?}
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new VocoTableBlockEntity(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(RECEPTOR_LIGHTS);
        builder.add(RECEPTOR_PORTALS);
        builder.add(ROTARY_DIALERS, CANDLE_LIGHT);
    }

    public static BooleanProperty lightProperty(ReceptorPosition receptor) {
        return switch (receptor) {
        case NORTH_EAST -> LIT_NORTH_EAST;
        case NORTH_WEST -> LIT_NORTH_WEST;
        case SOUTH_EAST -> LIT_SOUTH_EAST;
        case SOUTH_WEST -> LIT_SOUTH_WEST;
        };
    }

    public static BooleanProperty portalProperty(ReceptorPosition receptor) {
        return switch (receptor) {
        case NORTH_EAST -> PORTAL_NORTH_EAST;
        case NORTH_WEST -> PORTAL_NORTH_WEST;
        case SOUTH_EAST -> PORTAL_SOUTH_EAST;
        case SOUTH_WEST -> PORTAL_SOUTH_WEST;
        };
    }

    @Override
    protected void attack(BlockState state, Level level, BlockPos pos, Player player) {
        if (!level.isClientSide() && VocoTableCandleLogic.breakLookedAtCandle(level, pos, player)) return;
        super.attack(state, level, pos, player);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        ReceptorPosition candleHit = VocoTableCandleLogic.detectExistingCandleHit(level, pos, hit, player);
        ReceptorPosition receptorHit = VocoTableHitboxes.receptorAt(pos, hit);
        boolean itemDisplayHit = VocoTableHitboxes.isItemDisplay(pos, hit);
        if (state.getValue(ROTARY_DIALERS) && VocoTableHitboxes.isDialer(pos, hit)) return openDialerMenu(level, pos, player);
        if (player.isShiftKeyDown()) {
            ReceptorPosition receptor = candleHit != null ? candleHit : receptorHit;
            if (receptor != null) {
                VocoReceptorLogic.tryOpenCameraEditor(level, pos, player, receptor);
                return InteractionResult.SUCCESS;
            }
            if (!itemDisplayHit) return InteractionResult.PASS;
            return VocoTableItemDisplayLogic.useWithoutItem(level, pos, player, true);
        }
        if (candleHit != null) {
            if (!player.getMainHandItem().isEmpty() || !player.getAbilities().mayBuild || player.isSpectator()) return InteractionResult.PASS;
            if (!(level.getBlockEntity(pos) instanceof VocoTableBlockEntity tableBe)) return InteractionResult.PASS;
            if (!tableBe.isCandleLit(candleHit)) return InteractionResult.PASS;
            if (!level.isClientSide()) {
                VocoTableCandleLogic.extinguishCandleSlot(level, pos, player, candleHit);
            }
            return InteractionResult.SUCCESS;
        }
        if (!VocoReceptorLogic.isCompletelyEmptyHanded(player)) return InteractionResult.PASS;
        if (receptorHit != null) {
            BooleanProperty litProperty = lightProperty(receptorHit);
            BooleanProperty portalProperty = portalProperty(receptorHit);
            PearlSlotIgnition.Slot pearlSlot = VocoReceptorLogic.pearlSlot(litProperty, portalProperty, receptorHit);
            if (!PearlSlotIgnition.isLit(state, pearlSlot)) {
                if (!level.isClientSide() && PearlSlotIgnition.igniteFromBalance(state, level, pos, player, pearlSlot)) {
                    VocoTableCandleLogic.syncPortalStateFromCandles(level, pos, receptorHit);
                    BasukeSummon.trySummonFromVocoTable(level, pos);
                }
                return InteractionResult.SUCCESS;
            }
            if (!state.getValue(portalProperty) && level.isClientSide()) {
                VocoReceptorLogic.showNeedsPortalMessage(player);
            }
            return InteractionResult.SUCCESS;
        }
        if (!itemDisplayHit) return InteractionResult.PASS;
        return VocoTableItemDisplayLogic.useWithoutItem(level, pos, player, false);
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
        ReceptorPosition candleHit = VocoTableCandleLogic.detectExistingCandleHit(level, pos, hit, player);
        ReceptorPosition receptorHit = VocoTableHitboxes.receptorAt(pos, hit);
        boolean itemDisplayHit = VocoTableHitboxes.isItemDisplay(pos, hit);
        if (state.getValue(ROTARY_DIALERS) && VocoTableHitboxes.isDialer(pos, hit)) return openDialerMenu(level, pos, player);
        //? if >=1.21.2 {
        if (stack.isEmpty()) return InteractionResult.TRY_WITH_EMPTY_HAND;
        //?} else {
        /*if (stack.isEmpty()) return InteractionResult.PASS;
        *///?}
        if (player.isShiftKeyDown()) {
            ReceptorPosition receptor = candleHit != null ? candleHit : receptorHit;
            if (receptor != null) {
                VocoReceptorLogic.tryOpenCameraEditor(level, pos, player, receptor);
                return InteractionResult.SUCCESS;
            }
            if (!itemDisplayHit) return InteractionResult.PASS;
            return VocoTableItemDisplayLogic.useItemOn(stack, level, pos, player, hand, true);
        }
        var corner = candleHit != null ? candleHit : receptorHit;
        if (corner != null && stack.getItem() instanceof BlockItem item && VocoTableCandle.accepts(item.getBlock().defaultBlockState())) {
            return VocoTableCandleLogic.place(level, pos, corner, player, hand, stack, item, hit);
        }
        if (corner != null) {
            var ignition = VocoTableCandleLogic.ignite(level, pos, corner, player, hand, stack);
            if (ignition != InteractionResult.PASS || candleHit != null) return ignition;
            return useReceptorCornerItem(stack, state, level, pos, player, hand, corner);
        }
        if (!itemDisplayHit) return InteractionResult.PASS;
        return VocoTableItemDisplayLogic.useItemOn(stack, level, pos, player, hand, false);
    }

    private static InteractionResult useReceptorCornerItem(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
            ReceptorPosition receptor) {
        BooleanProperty litProperty = lightProperty(receptor);
        BooleanProperty portalProperty = portalProperty(receptor);
        PearlSlotIgnition.Slot pearlSlot = VocoReceptorLogic.pearlSlot(litProperty, portalProperty, receptor);
        InteractionResult result = PearlSlotIgnition.handleHeldItemUse(stack, state, level, pos, player, hand, pearlSlot);
        if (result == InteractionResult.SUCCESS && !level.isClientSide()) {
            VocoTableCandleLogic.syncPortalStateFromCandles(level, pos, receptor);
            BasukeSummon.trySummonFromVocoTable(level, pos);
        }
        return result;
    }

    private static InteractionResult openDialerMenu(Level level, BlockPos pos, Player player) {
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            serverPlayer.openMenu(new SimpleMenuProvider((containerId, playerInventory, ignoredPlayer) -> new VocoDialerMenu(containerId, playerInventory),
                    Component.literal("Voco Table Dialer")), buffer -> buffer.writeBlockPos(pos));
            VocoReceptorLogic.playUiClick(level, pos);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        VocoTableCandleLogic.animateTick(level, pos, random);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return VocoTableVoxelShapes.shape(state, level, pos, context);
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return VocoTableVoxelShapes.collisionShape(state, level, pos, context);
    }
}
