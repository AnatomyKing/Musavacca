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
import space.anatomyuniverse.musavacca.gui.menu.VocoSliderMenu;
import space.anatomyuniverse.musavacca.item.ModItems;

public final class VocoInteractLogic {
    private static final int UPDATE_FLAGS = Block.UPDATE_ALL | Block.UPDATE_IMMEDIATE;

    private VocoInteractLogic() {}

    public enum ReceptorPosition {
        NORTH_EAST(0, "north-east", -135, 0),
        NORTH_WEST(1, "north-west", 135, 0),
        SOUTH_EAST(2, "south-east", -45, 0),
        SOUTH_WEST(3, "south-west", 45, 0);

        public static final int COUNT = 4;

        private final int id;
        private final String displayName;
        private final int defaultYawDegrees;
        private final int defaultPitchDegrees;

        ReceptorPosition(int id, String displayName, int defaultYawDegrees, int defaultPitchDegrees) {
            this.id = id;
            this.displayName = displayName;
            this.defaultYawDegrees = defaultYawDegrees;
            this.defaultPitchDegrees = defaultPitchDegrees;
        }

        public int id() {
            return this.id;
        }

        public String displayName() {
            return this.displayName;
        }

        public int defaultYawDegrees() {
            return this.defaultYawDegrees;
        }

        public int defaultPitchDegrees() {
            return this.defaultPitchDegrees;
        }

        public static ReceptorPosition byId(int id) {
            for (ReceptorPosition position : values()) {
                if (position.id == id) {
                    return position;
                }
            }

            return NORTH_EAST;
        }
    }

    /**
     * Generic receptor behavior.
     *
     * Used by Voco Table receptors.
     * Teleport condition: LIT only.
     */
    public static InteractionResult useReceptorWithoutItem(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            BooleanProperty litProperty,
            ReceptorPosition receptor
    ) {
        return useReceptorWithoutItem(
                state,
                level,
                pos,
                player,
                litProperty,
                null,
                receptor
        );
    }

    /**
     * Portal-aware receptor behavior.
     *
     * Used by the standalone Voco Receptor.
     * Teleport condition: LIT + PORTAL.
     */
    public static InteractionResult useReceptorWithoutItem(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            BooleanProperty litProperty,
            BooleanProperty portalProperty,
            ReceptorPosition receptor
    ) {
        if (tryOpenSliderMenu(level, pos, player, receptor)) {
            return InteractionResult.SUCCESS;
        }

        if (!state.getValue(litProperty)) {
            if (level.isClientSide()) {
                showNeedsPearlMessage(player);
            }

            return InteractionResult.SUCCESS;
        }

        if (!canTeleport(state, litProperty, portalProperty)) {
            if (level.isClientSide()) {
                showNeedsPortalMessage(player);
            }

            return InteractionResult.SUCCESS;
        }

        if (!level.isClientSide()) {
            VocoTeleportLogic.teleportToReceptor(level, pos, player, receptor);
        }

        return InteractionResult.SUCCESS;
    }

    /**
     * Generic receptor behavior.
     *
     * Used by Voco Table receptors.
     * Teleport condition: LIT only.
     */
    public static InteractionResult useReceptorItem(
            ItemStack stack,
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BooleanProperty litProperty,
            ReceptorPosition receptor
    ) {
        return useReceptorItem(
                stack,
                state,
                level,
                pos,
                player,
                hand,
                litProperty,
                null,
                receptor
        );
    }

    /**
     * Portal-aware receptor behavior.
     *
     * Used by the standalone Voco Receptor.
     * Teleport condition: LIT + PORTAL.
     */
    public static InteractionResult useReceptorItem(
            ItemStack stack,
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BooleanProperty litProperty,
            BooleanProperty portalProperty,
            ReceptorPosition receptor
    ) {
        if (tryOpenSliderMenu(level, pos, player, receptor)) {
            return InteractionResult.SUCCESS;
        }

        if (!state.getValue(litProperty)) {
            return useUnlitReceptor(stack, state, level, pos, player, litProperty);
        }

        if (stack.is(Items.SHEARS)) {
            if (!level.isClientSide()) {
                depleteReceptor(stack, state, level, pos, player, hand, litProperty, receptor);
                VocoPearlPortalLogic.refreshReceptorAt(level, pos);
            }

            return InteractionResult.SUCCESS;
        }

        if (!canTeleport(state, litProperty, portalProperty)) {
            if (level.isClientSide()) {
                showNeedsPortalMessage(player);
            }

            return InteractionResult.SUCCESS;
        }

        if (!level.isClientSide()) {
            VocoTeleportLogic.teleportToReceptor(level, pos, player, receptor);
        }

        return InteractionResult.SUCCESS;
    }

    public static boolean tryOpenSliderMenu(
            Level level,
            BlockPos pos,
            Player player,
            ReceptorPosition receptor
    ) {
        if (!player.isShiftKeyDown()) {
            return false;
        }

        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            VocoSliderMenu.open(serverPlayer, pos, receptor);
        }

        return true;
    }

    private static boolean canTeleport(
            BlockState state,
            BooleanProperty litProperty,
            BooleanProperty portalProperty
    ) {
        if (!state.getValue(litProperty)) {
            return false;
        }

        return portalProperty == null || state.getValue(portalProperty);
    }

    private static InteractionResult useUnlitReceptor(
            ItemStack stack,
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            BooleanProperty litProperty
    ) {
        if (!stack.is(ModItems.BANANA_PEARL.get())) {
            if (level.isClientSide()) {
                showNeedsPearlMessage(player);
            }

            return InteractionResult.SUCCESS;
        }

        if (!level.isClientSide()) {
            lightReceptor(stack, state, level, pos, player, litProperty);
            VocoPearlPortalLogic.refreshReceptorAt(level, pos);
        }

        return InteractionResult.SUCCESS;
    }

    private static void lightReceptor(
            ItemStack stack,
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            BooleanProperty litProperty
    ) {
        level.setBlock(pos, state.setValue(litProperty, true), UPDATE_FLAGS);

        stack.consume(1, player);

        level.playSound(
                null,
                pos,
                SoundEvents.RESPAWN_ANCHOR_CHARGE,
                SoundSource.BLOCKS,
                1.0F,
                1.0F
        );
    }

    private static void depleteReceptor(
            ItemStack shears,
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BooleanProperty litProperty,
            ReceptorPosition receptor
    ) {
        level.setBlock(pos, state.setValue(litProperty, false), UPDATE_FLAGS);

        Vec3 popPos = getPearlPopPosition(pos, receptor);

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
                SoundEvents.RESPAWN_ANCHOR_DEPLETE,
                SoundSource.BLOCKS,
                1.0F,
                1.0F
        );

        popBananaPearl(level, pos, receptor);

        if (!player.getAbilities().instabuild) {
            shears.hurtAndBreak(
                    1,
                    player,
                    hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND
            );
        }
    }

    private static void popBananaPearl(Level level, BlockPos pos, ReceptorPosition receptor) {
        if (level.isClientSide()) {
            return;
        }

        Vec3 spawnPos = getPearlPopPosition(pos, receptor);
        Vec3 motion = getPearlPopMotion(receptor);

        ItemEntity item = new ItemEntity(
                level,
                spawnPos.x,
                spawnPos.y,
                spawnPos.z,
                new ItemStack(ModItems.BANANA_PEARL.get())
        );

        item.setDeltaMovement(motion);
        level.addFreshEntity(item);
    }

    public static Vec3 getPearlPopPosition(BlockPos pos, ReceptorPosition receptor) {
        return switch (receptor) {
            case NORTH_EAST -> new Vec3(pos.getX() + 0.8125D, pos.getY() + 1.05D, pos.getZ() + 0.1875D);
            case NORTH_WEST -> new Vec3(pos.getX() + 0.1875D, pos.getY() + 1.05D, pos.getZ() + 0.1875D);
            case SOUTH_EAST -> new Vec3(pos.getX() + 0.8125D, pos.getY() + 1.05D, pos.getZ() + 0.8125D);
            case SOUTH_WEST -> new Vec3(pos.getX() + 0.1875D, pos.getY() + 1.05D, pos.getZ() + 0.8125D);
        };
    }

    private static Vec3 getPearlPopMotion(ReceptorPosition receptor) {
        return switch (receptor) {
            case NORTH_EAST -> new Vec3(0.08D, 0.18D, -0.08D);
            case NORTH_WEST -> new Vec3(-0.08D, 0.18D, -0.08D);
            case SOUTH_EAST -> new Vec3(0.08D, 0.18D, 0.08D);
            case SOUTH_WEST -> new Vec3(-0.08D, 0.18D, 0.08D);
        };
    }

    private static void showNeedsPearlMessage(Player player) {
        player.displayClientMessage(Component.literal("This receptor needs a Banana Pearl."), false);
    }

    private static void showNeedsPortalMessage(Player player) {
        player.displayClientMessage(Component.literal("This receptor needs a lit Pearl Candle above it."), false);
    }
}