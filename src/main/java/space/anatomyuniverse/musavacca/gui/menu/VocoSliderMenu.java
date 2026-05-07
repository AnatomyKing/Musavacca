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
import net.minecraft.world.level.block.entity.BlockEntity;
import space.anatomyuniverse.musavacca.block.custom.VocoReceptorBlock;
import space.anatomyuniverse.musavacca.block.custom.VocoTableBlock;
import space.anatomyuniverse.musavacca.block.entity.custom.VocoReceptorBlockEntity;
import space.anatomyuniverse.musavacca.block.entity.custom.VocoTableBlockEntity;
import space.anatomyuniverse.musavacca.gui.ModMenus;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoInteractLogic.ReceptorPosition;

public class VocoSliderMenu extends AbstractContainerMenu {

    private static final int BUTTON_YAW_BASE = 1000;
    private static final int BUTTON_PITCH_BASE = 2000;

    private final Player player;
    private final BlockPos pos;
    private final ReceptorPosition receptor;

    private int yawDegrees;
    private int pitchDegrees;

    public VocoSliderMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf extraData) {
        this(
                containerId,
                playerInventory,
                extraData.readBlockPos(),
                ReceptorPosition.byId(extraData.readInt()),
                extraData.readInt(),
                extraData.readInt()
        );
    }

    public VocoSliderMenu(int containerId, Inventory playerInventory, BlockPos pos, ReceptorPosition receptor) {
        this(
                containerId,
                playerInventory,
                pos,
                receptor,
                readYaw(playerInventory.player.level(), pos, receptor),
                readPitch(playerInventory.player.level(), pos, receptor)
        );
    }

    private VocoSliderMenu(
            int containerId,
            Inventory playerInventory,
            BlockPos pos,
            ReceptorPosition receptor,
            int initialYawDegrees,
            int initialPitchDegrees
    ) {
        super(ModMenus.VOCO_SLIDER_MENU.get(), containerId);

        this.player = playerInventory.player;
        this.pos = pos;
        this.receptor = receptor;

        this.yawDegrees = VocoReceptorBlockEntity.clampYaw(initialYawDegrees);
        this.pitchDegrees = VocoReceptorBlockEntity.clampPitch(initialPitchDegrees);

        this.addSyncSlots();
    }

    public static void open(ServerPlayer player, BlockPos pos, ReceptorPosition receptor) {
        int yaw = readYaw(player.level(), pos, receptor);
        int pitch = readPitch(player.level(), pos, receptor);

        player.openMenu(
                new SimpleMenuProvider(
                        (containerId, inventory, ignoredPlayer) -> new VocoSliderMenu(containerId, inventory, pos, receptor),
                        Component.literal("Voco Facing: " + receptor.displayName())
                ),
                buffer -> {
                    buffer.writeBlockPos(pos);
                    buffer.writeInt(receptor.id());
                    buffer.writeInt(yaw);
                    buffer.writeInt(pitch);
                }
        );
    }

    private void addSyncSlots() {
        this.addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return readYaw(VocoSliderMenu.this.player.level(), VocoSliderMenu.this.pos, VocoSliderMenu.this.receptor);
            }

            @Override
            public void set(int value) {
                VocoSliderMenu.this.yawDegrees = VocoReceptorBlockEntity.clampYaw(value);
            }
        });

        this.addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return readPitch(VocoSliderMenu.this.player.level(), VocoSliderMenu.this.pos, VocoSliderMenu.this.receptor);
            }

            @Override
            public void set(int value) {
                VocoSliderMenu.this.pitchDegrees = VocoReceptorBlockEntity.clampPitch(value);
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

    public static int buttonIdForYaw(int yawDegrees) {
        int clamped = VocoReceptorBlockEntity.clampYaw(yawDegrees);
        return BUTTON_YAW_BASE + (clamped - VocoReceptorBlockEntity.MIN_YAW_DEGREES);
    }

    public static int buttonIdForPitch(int pitchDegrees) {
        int clamped = VocoReceptorBlockEntity.clampPitch(pitchDegrees);
        return BUTTON_PITCH_BASE + (clamped - VocoReceptorBlockEntity.MIN_PITCH_DEGREES);
    }

    private static boolean isYawButton(int id) {
        return id >= BUTTON_YAW_BASE
                && id <= BUTTON_YAW_BASE + (VocoReceptorBlockEntity.MAX_YAW_DEGREES - VocoReceptorBlockEntity.MIN_YAW_DEGREES);
    }

    private static boolean isPitchButton(int id) {
        return id >= BUTTON_PITCH_BASE
                && id <= BUTTON_PITCH_BASE + (VocoReceptorBlockEntity.MAX_PITCH_DEGREES - VocoReceptorBlockEntity.MIN_PITCH_DEGREES);
    }

    private static int yawFromButtonId(int id) {
        return VocoReceptorBlockEntity.MIN_YAW_DEGREES + (id - BUTTON_YAW_BASE);
    }

    private static int pitchFromButtonId(int id) {
        return VocoReceptorBlockEntity.MIN_PITCH_DEGREES + (id - BUTTON_PITCH_BASE);
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (isYawButton(id)) {
            int yaw = VocoReceptorBlockEntity.clampYaw(yawFromButtonId(id));
            this.yawDegrees = yaw;

            if (!player.level().isClientSide()) {
                this.writeYaw(yaw);
                this.broadcastChanges();
            }

            return true;
        }

        if (isPitchButton(id)) {
            int pitch = VocoReceptorBlockEntity.clampPitch(pitchFromButtonId(id));
            this.pitchDegrees = pitch;

            if (!player.level().isClientSide()) {
                this.writePitch(pitch);
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

        if (be instanceof VocoReceptorBlockEntity receptorBe) {
            receptorBe.setYawDegrees(yawDegrees);
        }
    }

    private void writePitch(int pitchDegrees) {
        BlockEntity be = this.player.level().getBlockEntity(this.pos);

        if (be instanceof VocoTableBlockEntity tableBe) {
            tableBe.setPitchDegrees(this.receptor, pitchDegrees);
            return;
        }

        if (be instanceof VocoReceptorBlockEntity receptorBe) {
            receptorBe.setPitchDegrees(pitchDegrees);
        }
    }

    private static int readYaw(Level level, BlockPos pos, ReceptorPosition receptor) {
        BlockEntity be = level.getBlockEntity(pos);

        if (be instanceof VocoTableBlockEntity tableBe) {
            return tableBe.getYawDegrees(receptor);
        }

        if (be instanceof VocoReceptorBlockEntity receptorBe) {
            return receptorBe.getYawDegrees();
        }

        return receptor.defaultYawDegrees();
    }

    private static int readPitch(Level level, BlockPos pos, ReceptorPosition receptor) {
        BlockEntity be = level.getBlockEntity(pos);

        if (be instanceof VocoTableBlockEntity tableBe) {
            return tableBe.getPitchDegrees(receptor);
        }

        if (be instanceof VocoReceptorBlockEntity receptorBe) {
            return receptorBe.getPitchDegrees();
        }

        return receptor.defaultPitchDegrees();
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

        if (!(player.level().getBlockState(this.pos).getBlock() instanceof VocoReceptorBlock)
                && !(player.level().getBlockState(this.pos).getBlock() instanceof VocoTableBlock)) {
            return false;
        }

        return player.distanceToSqr(
                this.pos.getX() + 0.5D,
                this.pos.getY() + 0.5D,
                this.pos.getZ() + 0.5D
        ) <= 64.0D;
    }
}