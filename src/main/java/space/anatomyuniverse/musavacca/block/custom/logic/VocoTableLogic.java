// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/block/custom/logic/VocoTableLogic.java
package space.anatomyuniverse.musavacca.block.custom.logic;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
import space.anatomyuniverse.musavacca.block.custom.logic.VocoTableReceptorHitboxes.HitPart;
import space.anatomyuniverse.musavacca.block.entity.custom.VocoTableBlockEntity;
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

        return state;
    }

    public static void addProperties(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(RECEPTOR_LIGHTS);
        builder.add(RECEPTOR_PORTALS);
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
        HitPart part = VocoTableReceptorHitboxes.detectHitPart(pos, hit);

        if (player.isShiftKeyDown()) {
            ReceptorPosition receptor = candleHit != null ? candleHit : part.receptor();

            if (receptor != null
                    && VocoReceptorLogic.tryOpenSliderMenu(level, pos, player, receptor)) {
                return InteractionResult.SUCCESS;
            }

            return InteractionResult.SUCCESS;
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

        ReceptorPosition receptorHit = part.receptor();

        if (receptorHit != null) {
            BooleanProperty litProperty = lightProperty(receptorHit);

            if (!state.getValue(litProperty)) {
                if (!level.isClientSide()) {
                    boolean lit = VocoReceptorLogic.lightReceptorWithBalance(
                            state,
                            level,
                            pos,
                            player,
                            litProperty
                    );

                    if (lit) {
                        VocoTableCandleLogic.syncPortalStateFromCandles(level, pos, receptorHit);
                    }
                }

                return InteractionResult.SUCCESS;
            }

            if (!state.getValue(portalProperty(receptorHit)) && level.isClientSide()) {
                VocoReceptorLogic.showNeedsPortalMessage(player);
            }

            return InteractionResult.SUCCESS;
        }

        if (part.togglesBasuke()) {
            toggleBasuke(level, pos);
            return InteractionResult.SUCCESS;
        }

        if (!level.isClientSide()) {
            removeDisplayedItem(level, pos, player);
        }

        return InteractionResult.SUCCESS;
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
        HitPart part = VocoTableReceptorHitboxes.detectHitPart(pos, hit);

        if (player.isShiftKeyDown()) {
            ReceptorPosition receptor = candleHit != null ? candleHit : part.receptor();

            if (receptor != null
                    && VocoReceptorLogic.tryOpenSliderMenu(level, pos, player, receptor)) {
                return InteractionResult.SUCCESS;
            }

            return InteractionResult.SUCCESS;
        }

        if (stack.isEmpty()) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
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
                }

                return InteractionResult.SUCCESS;
            }

            return InteractionResult.PASS;
        }

        ReceptorPosition receptorHit = part.receptor();

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

        if (part.togglesBasuke()) {
            return InteractionResult.PASS;
        }

        if (hand == InteractionHand.OFF_HAND) {
            return InteractionResult.PASS;
        }

        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        return insertDisplayedItem(stack, level, pos, player)
                ? InteractionResult.SUCCESS
                : InteractionResult.PASS;
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
        BooleanProperty litProperty = lightProperty(receptor);
        BooleanProperty portalProperty = portalProperty(receptor);

        InteractionResult result = VocoReceptorLogic.handleReceptorHeldItemUse(
                stack,
                state,
                level,
                pos,
                player,
                hand,
                litProperty,
                portalProperty,
                receptor
        );

        if (result == InteractionResult.SUCCESS && !level.isClientSide()) {
            VocoTableCandleLogic.syncPortalStateFromCandles(level, pos, receptor);
        }

        return result;
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

    private static void toggleBasuke(Level level, BlockPos pos) {
        if (level.isClientSide()) {
            return;
        }

        if (level instanceof ServerLevel serverLevel
                && level.getBlockEntity(pos) instanceof VocoTableBlockEntity tableBe) {
            tableBe.toggleBasuke(serverLevel);
        }
    }

    private static boolean removeDisplayedItem(Level level, BlockPos pos, Player player) {
        if (!(level.getBlockEntity(pos) instanceof VocoTableBlockEntity tableBe)
                || !tableBe.hasDisplayedItem()) {
            return false;
        }

        ItemStack removed = tableBe.removeDisplayedItem();
        if (!player.addItem(removed)) {
            player.drop(removed, false);
        }

        return true;
    }

    private static boolean insertDisplayedItem(
            ItemStack stack,
            Level level,
            BlockPos pos,
            Player player
    ) {
        if (!(level.getBlockEntity(pos) instanceof VocoTableBlockEntity tableBe)
                || tableBe.hasDisplayedItem()) {
            return false;
        }

        ItemStack inserted = stack.copy();
        inserted.setCount(1);
        tableBe.setDisplayedItem(inserted);

        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }

        return true;
    }
}