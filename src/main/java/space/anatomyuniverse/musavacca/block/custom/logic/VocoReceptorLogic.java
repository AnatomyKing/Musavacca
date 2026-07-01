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
import space.anatomyuniverse.musavacca.gui.menu.VocoSliderMenu;
import space.anatomyuniverse.musavacca.item.ModItems;
import space.anatomyuniverse.musavacca.tint.TintColorUtil;

public final class VocoReceptorLogic {
    public static final int UPDATE_FLAGS = Block.UPDATE_ALL | Block.UPDATE_IMMEDIATE;
    public static final int UNSET_HEX_COLOR = TintColorUtil.UNSET_HEX;

    public static final int RECEPTOR_LIGHT_BALANCE_COST = 1;

    public static final int MIN_YAW_DEGREES = -180;
    public static final int MAX_YAW_DEGREES = 180;
    public static final int MIN_PITCH_DEGREES = -90;
    public static final int MAX_PITCH_DEGREES = 90;

    private VocoReceptorLogic() {}

    public enum ReceptorPosition {
        NORTH_EAST(0, "north-east", -135, 0),
        NORTH_WEST(1, "north-west", 135, 0),
        SOUTH_EAST(2, "south-east", -45, 0),
        SOUTH_WEST(3, "south-west", 45, 0);

        public static final int COUNT = 4;

        private static final ReceptorPosition[] BY_ID = values();

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
            return id >= 0 && id < BY_ID.length ? BY_ID[id] : NORTH_EAST;
        }
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

    public static boolean isCompletelyEmptyHanded(Player player) {
        return player != null
                && player.getMainHandItem().isEmpty()
                && player.getOffhandItem().isEmpty();
    }

    public static InteractionResult handleReceptorHeldItemUse(
            ItemStack stack,
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BooleanProperty litProperty,
            @Nullable BooleanProperty portalProperty,
            ReceptorPosition receptor
    ) {
        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();

        if (hand == InteractionHand.OFF_HAND && !mainHand.isEmpty()) {
            return InteractionResult.PASS;
        }

        if (stack.isEmpty()) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }

        if (stack.is(ModItems.BANANA_PEARL.get())) {
            return useBananaPearlOnReceptor(
                    stack,
                    offHand,
                    hand,
                    state,
                    level,
                    pos,
                    player,
                    litProperty,
                    portalProperty,
                    receptor
            );
        }

        if (stack.is(Items.SHEARS)) {
            if (!level.isClientSide()) {
                if (state.getValue(litProperty)) {
                    depleteReceptorPearl(
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
                } else {
                    lightReceptorWithBalance(state, level, pos, player, litProperty);
                }
            }

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    private static InteractionResult useBananaPearlOnReceptor(
            ItemStack pearl,
            ItemStack offHand,
            InteractionHand hand,
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            BooleanProperty litProperty,
            @Nullable BooleanProperty portalProperty,
            ReceptorPosition receptor
    ) {
        if (!state.getValue(litProperty)) {
            if (!level.isClientSide()) {
                lightReceptorWithPearl(pearl, state, level, pos, player, litProperty);
            }

            return InteractionResult.SUCCESS;
        }

        if (hand == InteractionHand.MAIN_HAND && offHand.is(Items.SHEARS)) {
            if (!level.isClientSide()) {
                depleteReceptorPearl(
                        offHand,
                        state,
                        level,
                        pos,
                        player,
                        InteractionHand.OFF_HAND,
                        litProperty,
                        portalProperty,
                        receptor
                );
            }

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    public static boolean canTeleport(
            BlockState state,
            BooleanProperty litProperty,
            @Nullable BooleanProperty portalProperty
    ) {
        return state.getValue(litProperty)
                && (portalProperty == null || state.getValue(portalProperty));
    }

    public static boolean lightReceptorWithBalance(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            BooleanProperty litProperty
    ) {
        if (level.isClientSide()) {
            return true;
        }

        if (state.getValue(litProperty) || !(player instanceof ServerPlayer serverPlayer)) {
            return false;
        }

        if (!BalanceApi.deductBalance(serverPlayer, RECEPTOR_LIGHT_BALANCE_COST)) {
            showNeedsBalanceMessage(player);
            return false;
        }

        chargeReceptor(state, level, pos, litProperty);
        return true;
    }

    public static boolean lightReceptorWithPearl(
            ItemStack stack,
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            BooleanProperty litProperty
    ) {
        if (!stack.is(ModItems.BANANA_PEARL.get()) || state.getValue(litProperty)) {
            return false;
        }

        chargeReceptor(state, level, pos, litProperty);
        stack.consume(1, player);
        return true;
    }

    private static void chargeReceptor(
            BlockState state,
            Level level,
            BlockPos pos,
            BooleanProperty litProperty
    ) {
        level.setBlock(pos, state.setValue(litProperty, true), UPDATE_FLAGS);

        level.playSound(
                null,
                pos,
                SoundEvents.RESPAWN_ANCHOR_CHARGE,
                SoundSource.BLOCKS,
                0.65F,
                1.54F
        );
    }

    public static boolean depleteReceptorPearl(
            ItemStack shears,
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BooleanProperty litProperty,
            @Nullable BooleanProperty portalProperty,
            ReceptorPosition receptor
    ) {
        if (!shears.is(Items.SHEARS) || !state.getValue(litProperty)) {
            return false;
        }

        boolean wasPortal = portalProperty != null && state.getValue(portalProperty);

        BlockState newState = state.setValue(litProperty, false);
        if (portalProperty != null) {
            newState = newState.setValue(portalProperty, false);
        }

        level.setBlock(pos, newState, UPDATE_FLAGS);

        if (wasPortal) {
            playPortalDisappearSound(level, pos);
        }

        playDepleteEffects(level, pos, receptor);
        popBananaPearl(level, pos, receptor);
        damageItem(shears, player, hand);

        return true;
    }

    public static void playPortalAppearSound(Level level, BlockPos pos) {
        level.playSound(null, pos, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 0.65F, 1.25F);
    }

    public static void playPortalDisappearSound(Level level, BlockPos pos) {
        level.playSound(null, pos, SoundEvents.BEACON_DEACTIVATE, SoundSource.BLOCKS, 0.65F, 1.25F);
    }

    public static void playUiClick(Level level, BlockPos pos) {
        level.playSound(null, pos, SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.BLOCKS, 0.35F, 1.25F);
    }

    public static void damageItem(ItemStack stack, Player player, InteractionHand hand) {
        if (player.getAbilities().instabuild) {
            return;
        }

        stack.hurtAndBreak(
                1,
                player,
                hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND
        );
    }

    public static Vec3 pearlPopPosition(BlockPos pos, ReceptorPosition receptor) {
        return switch (receptor) {
            case NORTH_EAST -> new Vec3(pos.getX() + 0.8125D, pos.getY() + 1.05D, pos.getZ() + 0.1875D);
            case NORTH_WEST -> new Vec3(pos.getX() + 0.1875D, pos.getY() + 1.05D, pos.getZ() + 0.1875D);
            case SOUTH_EAST -> new Vec3(pos.getX() + 0.8125D, pos.getY() + 1.05D, pos.getZ() + 0.8125D);
            case SOUTH_WEST -> new Vec3(pos.getX() + 0.1875D, pos.getY() + 1.05D, pos.getZ() + 0.8125D);
        };
    }

    private static Vec3 pearlPopMotion(ReceptorPosition receptor) {
        return switch (receptor) {
            case NORTH_EAST -> new Vec3(0.08D, 0.18D, -0.08D);
            case NORTH_WEST -> new Vec3(-0.08D, 0.18D, -0.08D);
            case SOUTH_EAST -> new Vec3(0.08D, 0.18D, 0.08D);
            case SOUTH_WEST -> new Vec3(-0.08D, 0.18D, 0.08D);
        };
    }

    private static void playDepleteEffects(Level level, BlockPos pos, ReceptorPosition receptor) {
        Vec3 popPos = pearlPopPosition(pos, receptor);

        level.playSound(null, popPos.x, popPos.y, popPos.z, SoundEvents.SHEEP_SHEAR, SoundSource.BLOCKS, 1.0F, 1.0F);
        level.playSound(null, popPos.x, popPos.y, popPos.z, SoundEvents.AMETHYST_CLUSTER_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    private static void popBananaPearl(Level level, BlockPos pos, ReceptorPosition receptor) {
        if (level.isClientSide()) {
            return;
        }

        ItemEntity item = new ItemEntity(
                level,
                pearlPopPosition(pos, receptor).x,
                pearlPopPosition(pos, receptor).y,
                pearlPopPosition(pos, receptor).z,
                new ItemStack(ModItems.BANANA_PEARL.get())
        );

        item.setDeltaMovement(pearlPopMotion(receptor));
        level.addFreshEntity(item);
    }

    public static void showNeedsPearlMessage(Player player) {
        player.displayClientMessage(Component.literal("This receptor needs a Banana Pearl or 1 balance."), false);
    }

    public static void showNeedsBalanceMessage(Player player) {
        player.displayClientMessage(Component.literal("You need 1 balance to charge this receptor."), true);
    }

    public static void showNeedsPortalMessage(Player player) {
        player.displayClientMessage(Component.literal("This receptor needs a lit Pearl Candle."), false);
    }

    public static void showLatestHexMessage(Player player, ReceptorPosition receptor, int hexColor) {
        if (hexColor == UNSET_HEX_COLOR) {
            player.displayClientMessage(Component.literal("No lit Voco candle is active."), true);
            return;
        }

        player.displayClientMessage(
                Component.literal(String.format(
                        "Latest Voco hex: %s #%06X",
                        receptor.displayName(),
                        hexColor & 0xFFFFFF
                )),
                true
        );
    }

    public static int normalizeHex(int hexColor) {
        return TintColorUtil.normalizeHex(hexColor);
    }

    public static int clampYaw(int yawDegrees) {
        return clamp(yawDegrees, MIN_YAW_DEGREES, MAX_YAW_DEGREES);
    }

    public static int clampPitch(int pitchDegrees) {
        return clamp(pitchDegrees, MIN_PITCH_DEGREES, MAX_PITCH_DEGREES);
    }

    public static int clampReceptorId(int id) {
        return clamp(id, 0, ReceptorPosition.COUNT - 1);
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
