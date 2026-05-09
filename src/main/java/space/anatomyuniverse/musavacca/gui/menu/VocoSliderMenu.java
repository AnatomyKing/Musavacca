// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/gui/menu/VocoSliderMenu.java
package space.anatomyuniverse.musavacca.gui.menu;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import space.anatomyuniverse.musavacca.block.custom.VocoPostBlock;
import space.anatomyuniverse.musavacca.block.custom.VocoTableBlock;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoReceptorLogic;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoReceptorLogic.ReceptorPosition;
import space.anatomyuniverse.musavacca.block.entity.custom.VocoPostBlockEntity;
import space.anatomyuniverse.musavacca.block.entity.custom.VocoTableBlockEntity;
import space.anatomyuniverse.musavacca.gui.ModMenus;

public class VocoSliderMenu extends AbstractContainerMenu {

    private static final int BUTTON_YAW_BASE = 1000;
    private static final int BUTTON_PITCH_BASE = 2000;

    public static final int BUTTON_TOGGLE_CUSTOM_TARGET = 3000;
    public static final int BUTTON_USE_PLAYER_POSITION = 3001;

    private final Player player;
    private final BlockPos pos;
    private final ReceptorPosition receptor;

    private int yawDegrees;
    private int pitchDegrees;
    private boolean customTargetEnabled;

    public VocoSliderMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf extraData) {
        this(
                containerId,
                playerInventory,
                extraData.readBlockPos(),
                ReceptorPosition.byId(extraData.readInt()),
                extraData.readInt(),
                extraData.readInt(),
                extraData.readBoolean()
        );
    }

    public VocoSliderMenu(int containerId, Inventory playerInventory, BlockPos pos, ReceptorPosition receptor) {
        this(
                containerId,
                playerInventory,
                pos,
                receptor,
                readYaw(playerInventory.player.level(), pos, receptor),
                readPitch(playerInventory.player.level(), pos, receptor),
                readCustomTargetEnabled(playerInventory.player.level(), pos, receptor)
        );
    }

    private VocoSliderMenu(
            int containerId,
            Inventory playerInventory,
            BlockPos pos,
            ReceptorPosition receptor,
            int initialYawDegrees,
            int initialPitchDegrees,
            boolean initialCustomTargetEnabled
    ) {
        super(ModMenus.VOCO_SLIDER_MENU.get(), containerId);

        this.player = playerInventory.player;
        this.pos = pos;
        this.receptor = receptor;

        this.yawDegrees = clampYaw(initialYawDegrees);
        this.pitchDegrees = clampPitch(initialPitchDegrees);
        this.customTargetEnabled = initialCustomTargetEnabled;

        this.addSyncSlots();
    }

    public static void open(ServerPlayer player, BlockPos pos, ReceptorPosition receptor) {
        int yaw = readYaw(player.level(), pos, receptor);
        int pitch = readPitch(player.level(), pos, receptor);
        boolean customTargetEnabled = readCustomTargetEnabled(player.level(), pos, receptor);

        player.openMenu(
                new SimpleMenuProvider(
                        (containerId, inventory, ignoredPlayer) -> new VocoSliderMenu(
                                containerId,
                                inventory,
                                pos,
                                receptor
                        ),
                        Component.literal("Voco Receptor Corner: " + receptor.displayName())
                ),
                buffer -> {
                    buffer.writeBlockPos(pos);
                    buffer.writeInt(receptor.id());
                    buffer.writeInt(yaw);
                    buffer.writeInt(pitch);
                    buffer.writeBoolean(customTargetEnabled);
                }
        );
    }

    private void addSyncSlots() {
        this.addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return readYaw(
                        VocoSliderMenu.this.player.level(),
                        VocoSliderMenu.this.pos,
                        VocoSliderMenu.this.receptor
                );
            }

            @Override
            public void set(int value) {
                VocoSliderMenu.this.yawDegrees = clampYaw(value);
            }
        });

        this.addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return readPitch(
                        VocoSliderMenu.this.player.level(),
                        VocoSliderMenu.this.pos,
                        VocoSliderMenu.this.receptor
                );
            }

            @Override
            public void set(int value) {
                VocoSliderMenu.this.pitchDegrees = clampPitch(value);
            }
        });

        this.addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return readCustomTargetEnabled(
                        VocoSliderMenu.this.player.level(),
                        VocoSliderMenu.this.pos,
                        VocoSliderMenu.this.receptor
                ) ? 1 : 0;
            }

            @Override
            public void set(int value) {
                VocoSliderMenu.this.customTargetEnabled = value != 0;
            }
        });
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public ReceptorPosition getReceptor() {
        return this.receptor;
    }

    public String getReceptorDisplayName() {
        return this.receptor.displayName();
    }

    public int getYawDegrees() {
        return this.yawDegrees;
    }

    public int getPitchDegrees() {
        return this.pitchDegrees;
    }

    public boolean isCustomTargetEnabled() {
        return this.customTargetEnabled;
    }

    public String getTargetModeDisplayName() {
        return this.customTargetEnabled
                ? "Custom position"
                : "Default corner";
    }

    public static int buttonIdForYaw(int yawDegrees) {
        int clamped = clampYaw(yawDegrees);
        return BUTTON_YAW_BASE + (clamped - VocoReceptorLogic.MIN_YAW_DEGREES);
    }

    public static int buttonIdForPitch(int pitchDegrees) {
        int clamped = clampPitch(pitchDegrees);
        return BUTTON_PITCH_BASE + (clamped - VocoReceptorLogic.MIN_PITCH_DEGREES);
    }

    private static boolean isYawButton(int id) {
        return id >= BUTTON_YAW_BASE
                && id <= BUTTON_YAW_BASE + yawRange();
    }

    private static boolean isPitchButton(int id) {
        return id >= BUTTON_PITCH_BASE
                && id <= BUTTON_PITCH_BASE + pitchRange();
    }

    private static int yawFromButtonId(int id) {
        return VocoReceptorLogic.MIN_YAW_DEGREES + (id - BUTTON_YAW_BASE);
    }

    private static int pitchFromButtonId(int id) {
        return VocoReceptorLogic.MIN_PITCH_DEGREES + (id - BUTTON_PITCH_BASE);
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (isYawButton(id)) {
            int yaw = clampYaw(yawFromButtonId(id));
            this.yawDegrees = yaw;

            if (!player.level().isClientSide()) {
                this.writeYaw(yaw);
                this.broadcastChanges();
            }

            return true;
        }

        if (isPitchButton(id)) {
            int pitch = clampPitch(pitchFromButtonId(id));
            this.pitchDegrees = pitch;

            if (!player.level().isClientSide()) {
                this.writePitch(pitch);
                this.broadcastChanges();
            }

            return true;
        }

        if (id == BUTTON_TOGGLE_CUSTOM_TARGET) {
            this.customTargetEnabled = !this.customTargetEnabled;

            if (!player.level().isClientSide()) {
                this.writeCustomTargetEnabled(this.customTargetEnabled);
                this.broadcastChanges();
            }

            return true;
        }

        if (id == BUTTON_USE_PLAYER_POSITION) {
            this.customTargetEnabled = true;
            this.yawDegrees = clampYaw(Math.round(player.getYRot()));
            this.pitchDegrees = clampPitch(Math.round(player.getXRot()));

            if (!player.level().isClientSide()) {
                this.writeCustomTargetFromPlayer(player);
                this.broadcastChanges();
            }

            return true;
        }

        return false;
    }

    private void writeYaw(int yawDegrees) {
        BlockEntity be = this.player.level().getBlockEntity(this.pos);

        if (be instanceof VocoTableBlockEntity tableBe) {
            tableBe.setYawDegrees(this.receptor, yawDegrees);
            return;
        }

        if (be instanceof VocoPostBlockEntity postBe) {
            postBe.setYawDegrees(yawDegrees);
        }
    }

    private void writePitch(int pitchDegrees) {
        BlockEntity be = this.player.level().getBlockEntity(this.pos);

        if (be instanceof VocoTableBlockEntity tableBe) {
            tableBe.setPitchDegrees(this.receptor, pitchDegrees);
            return;
        }

        if (be instanceof VocoPostBlockEntity postBe) {
            postBe.setPitchDegrees(pitchDegrees);
        }
    }

    private void writeCustomTargetEnabled(boolean enabled) {
        BlockEntity be = this.player.level().getBlockEntity(this.pos);

        if (be instanceof VocoTableBlockEntity tableBe) {
            tableBe.setCustomTargetEnabled(this.receptor, enabled);
            return;
        }

        if (be instanceof VocoPostBlockEntity postBe) {
            postBe.setCustomTargetEnabled(enabled);
        }
    }

    private void writeCustomTargetFromPlayer(Player player) {
        Vec3 target = player.position();
        int yaw = clampYaw(Math.round(player.getYRot()));
        int pitch = clampPitch(Math.round(player.getXRot()));

        BlockEntity be = player.level().getBlockEntity(this.pos);

        if (be instanceof VocoTableBlockEntity tableBe) {
            tableBe.setCustomTarget(this.receptor, target, yaw, pitch);
            return;
        }

        if (be instanceof VocoPostBlockEntity postBe) {
            postBe.setCustomTarget(target, yaw, pitch);
        }
    }

    private static int readYaw(Level level, BlockPos pos, ReceptorPosition receptor) {
        BlockEntity be = level.getBlockEntity(pos);

        if (be instanceof VocoTableBlockEntity tableBe) {
            return tableBe.getYawDegrees(receptor);
        }

        if (be instanceof VocoPostBlockEntity postBe) {
            return postBe.getYawDegrees();
        }

        return receptor.defaultYawDegrees();
    }

    private static int readPitch(Level level, BlockPos pos, ReceptorPosition receptor) {
        BlockEntity be = level.getBlockEntity(pos);

        if (be instanceof VocoTableBlockEntity tableBe) {
            return tableBe.getPitchDegrees(receptor);
        }

        if (be instanceof VocoPostBlockEntity postBe) {
            return postBe.getPitchDegrees();
        }

        return receptor.defaultPitchDegrees();
    }

    private static boolean readCustomTargetEnabled(Level level, BlockPos pos, ReceptorPosition receptor) {
        BlockEntity be = level.getBlockEntity(pos);

        if (be instanceof VocoTableBlockEntity tableBe) {
            return tableBe.isCustomTargetEnabled(receptor);
        }

        if (be instanceof VocoPostBlockEntity postBe) {
            return postBe.isCustomTargetEnabled();
        }

        return false;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        if (player.level().isClientSide()) {
            return true;
        }

        Block block = player.level().getBlockState(this.pos).getBlock();
        if (!(block instanceof VocoPostBlock) && !(block instanceof VocoTableBlock)) {
            return false;
        }

        return player.distanceToSqr(
                this.pos.getX() + 0.5D,
                this.pos.getY() + 0.5D,
                this.pos.getZ() + 0.5D
        ) <= 64.0D;
    }

    private static int clampYaw(int yawDegrees) {
        return VocoReceptorLogic.clampYaw(yawDegrees);
    }

    private static int clampPitch(int pitchDegrees) {
        return VocoReceptorLogic.clampPitch(pitchDegrees);
    }

    private static int yawRange() {
        return VocoReceptorLogic.MAX_YAW_DEGREES
                - VocoReceptorLogic.MIN_YAW_DEGREES;
    }

    private static int pitchRange() {
        return VocoReceptorLogic.MAX_PITCH_DEGREES
                - VocoReceptorLogic.MIN_PITCH_DEGREES;
    }
}