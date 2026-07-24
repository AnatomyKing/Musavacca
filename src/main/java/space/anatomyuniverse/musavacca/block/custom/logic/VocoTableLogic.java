package space.anatomyuniverse.musavacca.block.custom.logic;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import space.anatomyuniverse.musavacca.block.custom.PearlCandleBlock;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoReceptorLogic.ReceptorPosition;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoTableDialerHitboxes.HitPart;
import space.anatomyuniverse.musavacca.block.entity.custom.VocoTableBlockEntity;
import space.anatomyuniverse.musavacca.basuke.summon.BasukeSummon;
import space.anatomyuniverse.musavacca.gui.menu.TestInventoryMenu;
import space.anatomyuniverse.musavacca.item.custom.FlintAndPearlItem;

public final class VocoTableLogic {
    public static final BooleanProperty LIT_NORTH_EAST = BooleanProperty.create("lit_north_east");
    public static final BooleanProperty LIT_NORTH_WEST = BooleanProperty.create("lit_north_west");
    public static final BooleanProperty LIT_SOUTH_EAST = BooleanProperty.create("lit_south_east");
    public static final BooleanProperty LIT_SOUTH_WEST = BooleanProperty.create("lit_south_west");

    public static final BooleanProperty PORTAL_NORTH_EAST = BooleanProperty.create("portal_north_east");
    public static final BooleanProperty PORTAL_NORTH_WEST = BooleanProperty.create("portal_north_west");
    public static final BooleanProperty PORTAL_SOUTH_EAST = BooleanProperty.create("portal_south_east");
    public static final BooleanProperty PORTAL_SOUTH_WEST = BooleanProperty.create("portal_south_west");

    public static final BooleanProperty ROTARY_DIALERS = BooleanProperty.create("rotary_dialers");

    public static final BooleanProperty[] RECEPTOR_LIGHTS = {
            LIT_NORTH_EAST,
            LIT_NORTH_WEST,
            LIT_SOUTH_EAST,
            LIT_SOUTH_WEST
    };

    public static final BooleanProperty[] RECEPTOR_PORTALS = {
            PORTAL_NORTH_EAST,
            PORTAL_NORTH_WEST,
            PORTAL_SOUTH_EAST,
            PORTAL_SOUTH_WEST
    };

    private VocoTableLogic() {}

    public static BlockState defaultState(BlockState state) {
        for (BooleanProperty property : RECEPTOR_LIGHTS) {
            state = state.setValue(property, false);
        }

        for (BooleanProperty property : RECEPTOR_PORTALS) {
            state = state.setValue(property, false);
        }

        return state.setValue(ROTARY_DIALERS, false);
    }

    public static void addProperties(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(RECEPTOR_LIGHTS);
        builder.add(RECEPTOR_PORTALS);
        builder.add(ROTARY_DIALERS);
    }

    public static boolean hasAnyReceptorLit(BlockState state) {
        for (BooleanProperty property : RECEPTOR_LIGHTS) {
            if (state.getValue(property)) {
                return true;
            }
        }

        return false;
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

    public static InteractionResult useWithoutItem(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            BlockHitResult hit
    ) {
        ReceptorPosition candleHit = VocoTableCandleLogic.detectExistingCandleHit(level, pos, hit);
        VocoTableReceptorHitboxes.HitPart receptorPart = VocoTableReceptorHitboxes.detectHitPart(pos, hit);
        VocoTableItemDisplayHitboxes.HitPart itemDisplayPart = VocoTableItemDisplayHitboxes.detectHitPart(pos, hit);
        HitPart dialerPart = detectDialerHitIfActive(state, pos, hit);

        if (dialerPart.isDialer()) {
            return openDialerMenu(level, pos, player);
        }

        if (player.isShiftKeyDown()) {
            ReceptorPosition receptor = candleHit != null ? candleHit : receptorPart.receptor();

            if (receptor != null) {
                if (VocoReceptorLogic.tryOpenSliderMenu(level, pos, player, receptor)) {
                    return InteractionResult.SUCCESS;
                }

                return InteractionResult.SUCCESS;
            }

            if (!itemDisplayPart.isItemDisplay()) {
                return InteractionResult.PASS;
            }

            return VocoTableItemDisplayLogic.useWithoutItem(
                    level,
                    pos,
                    player,
                    true
            );
        }

        if (candleHit != null) {
            if (!player.getMainHandItem().isEmpty()) {
                return InteractionResult.PASS;
            }

            if (!(level.getBlockEntity(pos) instanceof VocoTableBlockEntity tableBe)) {
                return InteractionResult.PASS;
            }

            if (!tableBe.isCandleLit(candleHit)) {
                return InteractionResult.PASS;
            }

            if (!level.isClientSide()) {
                VocoTableCandleLogic.extinguishCandleSlot(level, pos, player, candleHit);
            }

            return InteractionResult.SUCCESS;
        }

        if (!VocoReceptorLogic.isCompletelyEmptyHanded(player)) {
            return InteractionResult.PASS;
        }

        ReceptorPosition receptorHit = receptorPart.receptor();

        if (receptorHit != null) {
            BooleanProperty litProperty =
                    lightProperty(receptorHit);

            BooleanProperty portalProperty =
                    portalProperty(receptorHit);

            PearlSlotIgnition.Slot pearlSlot =
                    VocoReceptorLogic.pearlSlot(
                            litProperty,
                            portalProperty,
                            receptorHit
                    );

            if (!PearlSlotIgnition.isLit(state, pearlSlot)) {
                if (
                        !level.isClientSide()
                                && PearlSlotIgnition.igniteFromBalance(
                                state,
                                level,
                                pos,
                                player,
                                pearlSlot
                        )
                ) {
                    VocoTableCandleLogic.syncPortalStateFromCandles(
                            level,
                            pos,
                            receptorHit
                    );

                    BasukeSummon.trySummonFromVocoTable(
                            level,
                            pos
                    );
                }

                return InteractionResult.SUCCESS;
            }

            if (
                    !state.getValue(portalProperty)
                            && level.isClientSide()
            ) {
                VocoReceptorLogic.showNeedsPortalMessage(player);
            }

            return InteractionResult.SUCCESS;
        }

        if (!itemDisplayPart.isItemDisplay()) {
            return InteractionResult.PASS;
        }

        return VocoTableItemDisplayLogic.useWithoutItem(
                level,
                pos,
                player,
                false
        );
    }

    public static InteractionResult useItemOn(
            ItemStack stack,
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hit
    ) {
        ReceptorPosition candleHit = VocoTableCandleLogic.detectExistingCandleHit(level, pos, hit);
        VocoTableReceptorHitboxes.HitPart receptorPart = VocoTableReceptorHitboxes.detectHitPart(pos, hit);
        VocoTableItemDisplayHitboxes.HitPart itemDisplayPart = VocoTableItemDisplayHitboxes.detectHitPart(pos, hit);
        HitPart dialerPart = detectDialerHitIfActive(state, pos, hit);

        if (dialerPart.isDialer()) {
            return openDialerMenu(level, pos, player);
        }

        if (stack.isEmpty()) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }

        if (player.isShiftKeyDown()) {
            ReceptorPosition receptor = candleHit != null ? candleHit : receptorPart.receptor();

            if (receptor != null) {
                if (VocoReceptorLogic.tryOpenSliderMenu(level, pos, player, receptor)) {
                    return InteractionResult.SUCCESS;
                }

                return InteractionResult.SUCCESS;
            }

            if (!itemDisplayPart.isItemDisplay()) {
                return InteractionResult.PASS;
            }

            return VocoTableItemDisplayLogic.useItemOn(
                    stack,
                    level,
                    pos,
                    player,
                    hand,
                    true
            );
        }

        if (candleHit != null) {
            Block candleBlock = candleBlockFromStack(stack);

            if (candleBlock != null) {
                if (!level.isClientSide()) {
                    VocoTableCandleLogic.addCandleToSlot(
                            stack,
                            level,
                            pos,
                            player,
                            candleBlock,
                            candleHit
                    );
                }

                return InteractionResult.SUCCESS;
            }

            if (stack.getItem() instanceof FlintAndPearlItem) {
                if (!(level.getBlockEntity(pos) instanceof VocoTableBlockEntity tableBe)) {
                    return InteractionResult.PASS;
                }

                if (!tableBe.hasCandle(candleHit)) {
                    return InteractionResult.PASS;
                }

                if (!level.isClientSide()) {
                    VocoTableCandleLogic.lightCandleSlot(
                            stack,
                            level,
                            pos,
                            player,
                            hand,
                            tableBe,
                            candleHit
                    );

                    BasukeSummon.trySummonFromVocoTable(level, pos);
                }

                return InteractionResult.SUCCESS;
            }

            if (stack.is(Items.FLINT_AND_STEEL)) {
                if (!(level.getBlockEntity(pos) instanceof VocoTableBlockEntity tableBe)) {
                    return InteractionResult.PASS;
                }

                if (!tableBe.hasCandle(candleHit)) {
                    return InteractionResult.PASS;
                }

                if (!level.isClientSide()) {
                    VocoTableCandleLogic.lightVanillaCandleSlot(
                            stack,
                            level,
                            pos,
                            player,
                            hand,
                            tableBe,
                            candleHit
                    );

                    BasukeSummon.trySummonFromVocoTable(level, pos);
                }

                return InteractionResult.SUCCESS;
            }

            return InteractionResult.PASS;
        }

        ReceptorPosition receptorHit = receptorPart.receptor();

        if (receptorHit != null) {
            Block candleBlock = candleBlockFromStack(stack);

            if (candleBlock != null) {
                if (!level.isClientSide()) {
                    VocoTableCandleLogic.addCandleToSlot(
                            stack,
                            level,
                            pos,
                            player,
                            candleBlock,
                            receptorHit
                    );
                }

                return InteractionResult.SUCCESS;
            }

            return useReceptorCornerItem(
                    stack,
                    state,
                    level,
                    pos,
                    player,
                    hand,
                    receptorHit
            );
        }

        if (!itemDisplayPart.isItemDisplay()) {
            return InteractionResult.PASS;
        }

        return VocoTableItemDisplayLogic.useItemOn(
                stack,
                level,
                pos,
                player,
                hand,
                false
        );
    }

    private static InteractionResult useReceptorCornerItem(
            ItemStack stack,
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            ReceptorPosition receptor
    ) {
        BooleanProperty litProperty =
                lightProperty(receptor);

        BooleanProperty portalProperty =
                portalProperty(receptor);

        PearlSlotIgnition.Slot pearlSlot =
                VocoReceptorLogic.pearlSlot(
                        litProperty,
                        portalProperty,
                        receptor
                );

        InteractionResult result =
                PearlSlotIgnition.handleHeldItemUse(
                        stack,
                        state,
                        level,
                        pos,
                        player,
                        hand,
                        pearlSlot
                );

        if (result == InteractionResult.SUCCESS && !level.isClientSide()) {
            VocoTableCandleLogic.syncPortalStateFromCandles(level, pos, receptor);
            BasukeSummon.trySummonFromVocoTable(level, pos);
        }

        return result;
    }

    private static HitPart detectDialerHitIfActive(BlockState state, BlockPos pos, BlockHitResult hit) {
        if (!state.getValue(ROTARY_DIALERS)) {
            return HitPart.NONE;
        }

        return VocoTableDialerHitboxes.detectHitPart(pos, hit);
    }

    private static InteractionResult openDialerMenu(Level level, BlockPos pos, Player player) {
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            serverPlayer.openMenu(
                    new SimpleMenuProvider(
                            (containerId, playerInventory, ignoredPlayer) ->
                                    new TestInventoryMenu(containerId, playerInventory),
                            Component.literal("Voco Table Dialer")
                    ),
                    buffer -> buffer.writeBlockPos(pos)
            );

            VocoReceptorLogic.playUiClick(level, pos);
        }

        return InteractionResult.SUCCESS;
    }

    @Nullable
    private static Block candleBlockFromStack(ItemStack stack) {
        if (!(stack.getItem() instanceof BlockItem blockItem)) {
            return null;
        }

        Block block = blockItem.getBlock();

        if (block instanceof PearlCandleBlock pearlCandleBlock) {
            block = pearlCandleBlock.getVanillaCandleBlock();
        }

        return block instanceof CandleBlock ? block : null;
    }

}
