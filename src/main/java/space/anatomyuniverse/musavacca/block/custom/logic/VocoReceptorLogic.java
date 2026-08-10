package space.anatomyuniverse.musavacca.block.custom.logic;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import space.anatomyuniverse.musavacca.block.custom.VocoPostBlock;
import space.anatomyuniverse.musavacca.block.custom.VocoTableBlock;
import space.anatomyuniverse.musavacca.gui.voco.VocoCameraStartPayload;

public final class VocoReceptorLogic {
    public static final int UPDATE_FLAGS = Block.UPDATE_ALL | Block.UPDATE_IMMEDIATE;
    public static final int UNSET_HEX_COLOR = -1;

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

    public static boolean tryOpenCameraEditor(
            Level level,
            BlockPos pos,
            Player player,
            ReceptorPosition receptor
    ) {
        if (!player.isShiftKeyDown()) {
            return false;
        }

        if (!isReceptorLit(level, pos, receptor)) {
            return true;
        }

        //? if >=1.21.6 {
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            VocoCameraStartPayload.open(serverPlayer, pos, receptor);
        }
        //?} else {
        /*if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            serverPlayer.displayClientMessage(
                    Component.literal("Voco camera editing requires Minecraft 1.21.6 or newer."),
                    true
            );
        }
        *///?}

        return true;
    }


    public static boolean isReceptorLit(
            Level level,
            BlockPos pos,
            ReceptorPosition receptor
    ) {
        if (level == null || pos == null || receptor == null) {
            return false;
        }

        BlockState state = level.getBlockState(pos);

        if (state.getBlock() instanceof VocoPostBlock) {
            return state.hasProperty(VocoPostBlock.LIT)
                    && state.getValue(VocoPostBlock.LIT);
        }

        if (state.getBlock() instanceof VocoTableBlock) {
            BooleanProperty litProperty = VocoTableBlock.lightProperty(receptor);
            return state.hasProperty(litProperty)
                    && state.getValue(litProperty);
        }

        return false;
    }

    public static boolean isCompletelyEmptyHanded(Player player) {
        return player != null
                && player.getMainHandItem().isEmpty()
                && player.getOffhandItem().isEmpty();
    }

    public static PearlSlotIgnition.Slot pearlSlot(
            BooleanProperty litProperty,
            @Nullable BooleanProperty portalProperty,
            ReceptorPosition receptor
    ) {
        return PearlSlotIgnition.Slot.of(
                litProperty,
                portalProperty,
                pearlPopOffset(receptor),
                pearlPopMotion(receptor)
        );
    }

    private static Vec3 pearlPopOffset(
            ReceptorPosition receptor
    ) {
        return switch (receptor) {
            case NORTH_EAST ->
                    new Vec3(0.8125D, 1.05D, 0.1875D);
            case NORTH_WEST ->
                    new Vec3(0.1875D, 1.05D, 0.1875D);
            case SOUTH_EAST ->
                    new Vec3(0.8125D, 1.05D, 0.8125D);
            case SOUTH_WEST ->
                    new Vec3(0.1875D, 1.05D, 0.8125D);
        };
    }

    private static Vec3 pearlPopMotion(
            ReceptorPosition receptor
    ) {
        return switch (receptor) {
            case NORTH_EAST ->
                    new Vec3(0.08D, 0.18D, -0.08D);
            case NORTH_WEST ->
                    new Vec3(-0.08D, 0.18D, -0.08D);
            case SOUTH_EAST ->
                    new Vec3(0.08D, 0.18D, 0.08D);
            case SOUTH_WEST ->
                    new Vec3(-0.08D, 0.18D, 0.08D);
        };
    }

    public static boolean canTeleport(
            BlockState state,
            BooleanProperty litProperty,
            @Nullable BooleanProperty portalProperty
    ) {
        return state.getValue(litProperty)
                && (portalProperty == null || state.getValue(portalProperty));
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
        return hexColor & 0xFFFFFF;
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