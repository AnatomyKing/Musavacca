package space.anatomyuniverse.musavacca.block.custom.logic;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import space.anatomyuniverse.musavacca.bar.balance.BalanceApi;
import space.anatomyuniverse.musavacca.item.ModItems;

import java.util.Objects;

public final class PearlSlotIgnition {
    public static final int UPDATE_FLAGS =
            Block.UPDATE_ALL | Block.UPDATE_IMMEDIATE;

    public static final int DEFAULT_BALANCE_COST = 1;

    private PearlSlotIgnition() {}

    public record Slot(
            BooleanProperty litProperty,
            @Nullable BooleanProperty linkedProperty,
            Vec3 pearlPopOffset,
            Vec3 pearlPopMotion,
            int balanceCost
    ) {
        public Slot {
            Objects.requireNonNull(
                    litProperty,
                    "litProperty"
            );
            Objects.requireNonNull(
                    pearlPopOffset,
                    "pearlPopOffset"
            );
            Objects.requireNonNull(
                    pearlPopMotion,
                    "pearlPopMotion"
            );

            if (balanceCost <= 0) {
                throw new IllegalArgumentException(
                        "balanceCost must be greater than zero"
                );
            }
        }

        public static Slot of(
                BooleanProperty litProperty,
                @Nullable BooleanProperty linkedProperty,
                Vec3 pearlPopOffset,
                Vec3 pearlPopMotion
        ) {
            return new Slot(
                    litProperty,
                    linkedProperty,
                    pearlPopOffset,
                    pearlPopMotion,
                    DEFAULT_BALANCE_COST
            );
        }

        public static Slot of(
                BooleanProperty litProperty,
                @Nullable BooleanProperty linkedProperty,
                Vec3 pearlPopOffset,
                Vec3 pearlPopMotion,
                int balanceCost
        ) {
            return new Slot(
                    litProperty,
                    linkedProperty,
                    pearlPopOffset,
                    pearlPopMotion,
                    balanceCost
            );
        }

        public Vec3 pearlPopPosition(BlockPos pos) {
            return new Vec3(
                    pos.getX() + this.pearlPopOffset.x,
                    pos.getY() + this.pearlPopOffset.y,
                    pos.getZ() + this.pearlPopOffset.z
            );
        }
    }

    public static InteractionResult handleHeldItemUse(
            ItemStack stack,
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            Slot slot
    ) {
        validateState(state, slot);

        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();

        if (
                hand == InteractionHand.OFF_HAND
                        && !mainHand.isEmpty()
        ) {
            return InteractionResult.PASS;
        }

        if (stack.isEmpty()) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }

        if (stack.is(ModItems.BANANA_PEARL.get())) {
            return useBananaPearl(
                    stack,
                    offHand,
                    hand,
                    state,
                    level,
                    pos,
                    player,
                    slot
            );
        }

        if (stack.is(Items.SHEARS)) {
            if (!level.isClientSide()) {
                if (isLit(state, slot)) {
                    shearPearl(
                            stack,
                            state,
                            level,
                            pos,
                            player,
                            hand,
                            slot
                    );
                } else {
                    igniteFromBalance(
                            state,
                            level,
                            pos,
                            player,
                            slot
                    );
                }
            }

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    private static InteractionResult useBananaPearl(
            ItemStack pearl,
            ItemStack offHand,
            InteractionHand hand,
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            Slot slot
    ) {
        if (!isLit(state, slot)) {
            if (!level.isClientSide()) {
                igniteFromPearl(
                        pearl,
                        state,
                        level,
                        pos,
                        player,
                        slot
                );
            }

            return InteractionResult.SUCCESS;
        }

        if (
                hand == InteractionHand.MAIN_HAND
                        && offHand.is(Items.SHEARS)
        ) {
            if (!level.isClientSide()) {
                shearPearl(
                        offHand,
                        state,
                        level,
                        pos,
                        player,
                        InteractionHand.OFF_HAND,
                        slot
                );
            }

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    public static boolean isLit(
            BlockState state,
            Slot slot
    ) {
        validateState(state, slot);
        return state.getValue(slot.litProperty());
    }

    public static boolean igniteFromBalance(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            Slot slot
    ) {
        validateState(state, slot);

        if (isLit(state, slot)) {
            return false;
        }

        if (level.isClientSide()) {
            return true;
        }

        if (!(player instanceof ServerPlayer serverPlayer)) {
            return false;
        }

        if (!BalanceApi.deductBalance(
                serverPlayer,
                slot.balanceCost()
        )) {
            showNeedsBalanceMessage(
                    player,
                    slot.balanceCost()
            );
            return false;
        }

        ignite(
                state,
                level,
                pos,
                slot
        );
        return true;
    }

    public static boolean igniteFromPearl(
            ItemStack stack,
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            Slot slot
    ) {
        validateState(state, slot);

        if (
                !stack.is(ModItems.BANANA_PEARL.get())
                        || isLit(state, slot)
        ) {
            return false;
        }

        if (level.isClientSide()) {
            return true;
        }

        ignite(
                state,
                level,
                pos,
                slot
        );
        stack.consume(1, player);
        return true;
    }

    /**
     * Extinguishes a lit pearl slot, clears its optional linked property,
     * drops one Banana Pearl, and damages the shears.
     */
    public static boolean shearPearl(
            ItemStack shears,
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            Slot slot
    ) {
        validateState(state, slot);

        if (
                !shears.is(Items.SHEARS)
                        || !isLit(state, slot)
        ) {
            return false;
        }

        if (level.isClientSide()) {
            return true;
        }

        BooleanProperty linkedProperty =
                slot.linkedProperty();

        boolean wasLinked =
                linkedProperty != null
                        && state.getValue(linkedProperty);

        BlockState newState = state.setValue(
                slot.litProperty(),
                false
        );

        if (linkedProperty != null) {
            newState = newState.setValue(
                    linkedProperty,
                    false
            );
        }

        level.setBlock(
                pos,
                newState,
                UPDATE_FLAGS
        );

        playPortalStateChangeSound(
                level,
                pos,
                wasLinked,
                false
        );

        playShearEffects(level, pos, slot);
        popBananaPearl(level, pos, slot);
        damageItem(shears, player, hand);

        return true;
    }

    private static void ignite(
            BlockState state,
            Level level,
            BlockPos pos,
            Slot slot
    ) {
        level.setBlock(
                pos,
                state.setValue(
                        slot.litProperty(),
                        true
                ),
                UPDATE_FLAGS
        );

        level.playSound(
                null,
                pos,
                SoundEvents.RESPAWN_ANCHOR_CHARGE,
                SoundSource.BLOCKS,
                0.65F,
                1.54F
        );
    }

    public static void playPortalStateChangeSound(
            Level level,
            BlockPos pos,
            boolean wasPortal,
            boolean portal
    ) {
        if (
                level.isClientSide()
                        || wasPortal == portal
        ) {
            return;
        }

        level.playSound(
                null,
                pos,
                portal
                        ? SoundEvents.BEACON_ACTIVATE
                        : SoundEvents.BEACON_DEACTIVATE,
                SoundSource.BLOCKS,
                0.65F,
                1.25F
        );
    }

    private static void playShearEffects(
            Level level,
            BlockPos pos,
            Slot slot
    ) {
        Vec3 popPos = slot.pearlPopPosition(pos);

        level.playSound(
                null,
                popPos.x,
                popPos.y,
                popPos.z,
                SoundEvents.SHEEP_SHEAR,
                SoundSource.BLOCKS,
                1.0F,
                1.0F
        );

        level.playSound(
                null,
                popPos.x,
                popPos.y,
                popPos.z,
                SoundEvents.AMETHYST_CLUSTER_BREAK,
                SoundSource.BLOCKS,
                1.0F,
                1.0F
        );
    }

    private static void popBananaPearl(
            Level level,
            BlockPos pos,
            Slot slot
    ) {
        if (level.isClientSide()) {
            return;
        }

        Vec3 popPos = slot.pearlPopPosition(pos);

        ItemEntity item = new ItemEntity(
                level,
                popPos.x,
                popPos.y,
                popPos.z,
                new ItemStack(
                        ModItems.BANANA_PEARL.get()
                )
        );

        item.setDeltaMovement(slot.pearlPopMotion());
        level.addFreshEntity(item);
    }

    private static void damageItem(
            ItemStack stack,
            Player player,
            InteractionHand hand
    ) {
        if (player.getAbilities().instabuild) {
            return;
        }

        stack.hurtAndBreak(
                1,
                player,
                hand == InteractionHand.MAIN_HAND
                        ? EquipmentSlot.MAINHAND
                        : EquipmentSlot.OFFHAND
        );
    }

    private static void showNeedsBalanceMessage(
            Player player,
            int balanceCost
    ) {
        player.displayClientMessage(
                Component.literal(
                        "You need "
                                + balanceCost
                                + " balance to charge this receptor."
                ),
                true
        );
    }

    private static void validateState(
            BlockState state,
            Slot slot
    ) {
        if (!state.hasProperty(slot.litProperty())) {
            throw new IllegalArgumentException(
                    "Block state for "
                            + state.getBlock()
                            + " does not contain pearl-slot lit property "
                            + slot.litProperty().getName()
            );
        }

        BooleanProperty linkedProperty =
                slot.linkedProperty();

        if (
                linkedProperty != null
                        && !state.hasProperty(linkedProperty)
        ) {
            throw new IllegalArgumentException(
                    "Block state for "
                            + state.getBlock()
                            + " does not contain pearl-slot linked property "
                            + linkedProperty.getName()
            );
        }
    }
}