package space.anatomyuniverse.musavacca.gui.menu;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;
import space.anatomyuniverse.musavacca.component.HexColorComponent;
import space.anatomyuniverse.musavacca.gui.ModMenus;
import space.anatomyuniverse.musavacca.item.custom.FlintAndPearlItem;
import space.anatomyuniverse.musavacca.tint.TintColorUtil;

import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

public class ItemInteractMenu extends AbstractContainerMenu {
    public static final int BUTTON_HEX_0 = 0;
    public static final int BUTTON_HEX_F = 15;

    public static final int BUTTON_BACKSPACE = 100;
    public static final int BUTTON_CLEAR = 101;
    public static final int BUTTON_DEFAULT = 102;
    public static final int BUTTON_RANDOM = 103;

    private static final String HEX = "0123456789ABCDEF";

    private final Player player;
    private final InteractionHand hand;
    private final int[] digits = new int[6];

    private int cursor = 6;

    public ItemInteractMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf extraData) {
        this(
                containerId,
                playerInventory,
                extraData.readBoolean() ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND,
                extraData.readInt()
        );
    }

    public ItemInteractMenu(int containerId, Inventory playerInventory, InteractionHand hand, int initialHex) {
        super(ModMenus.ITEM_INTERACT_MENU.get(), containerId);
        this.player = playerInventory.player;
        this.hand = hand;

        this.loadFromHex(initialHex);
        this.cursor = 6;

        this.addSyncSlots();
    }

    private void addSyncSlots() {
        this.addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return ItemInteractMenu.this.cursor;
            }

            @Override
            public void set(int value) {
                ItemInteractMenu.this.cursor = Math.max(0, Math.min(6, value));
            }
        });

        for (int i = 0; i < 6; i++) {
            final int index = i;
            this.addDataSlot(new DataSlot() {
                @Override
                public int get() {
                    return ItemInteractMenu.this.digits[index];
                }

                @Override
                public void set(int value) {
                    ItemInteractMenu.this.digits[index] = Math.max(0, Math.min(15, value));
                }
            });
        }
    }

    public static boolean isKnownButton(int id) {
        return (id >= BUTTON_HEX_0 && id <= BUTTON_HEX_F)
                || id == BUTTON_BACKSPACE
                || id == BUTTON_CLEAR
                || id == BUTTON_DEFAULT
                || id == BUTTON_RANDOM;
    }

    public static int buttonIdForHexNibble(int nibble) {
        return Math.max(0, Math.min(15, nibble));
    }

    public InteractionHand getHand() {
        return this.hand;
    }

    public int getCursor() {
        return this.cursor;
    }

    public String getDisplayedCode() {
        StringBuilder builder = new StringBuilder(6);
        for (int digit : this.digits) {
            builder.append(HEX.charAt(digit));
        }
        return builder.toString();
    }

    public int getDisplayedHexColor() {
        return this.packDigits();
    }

    private void loadFromHex(int rgb) {
        int normalized = TintColorUtil.rgb(rgb);
        String hex = String.format(Locale.ROOT, "%06X", normalized);

        for (int i = 0; i < 6; i++) {
            this.digits[i] = Character.digit(hex.charAt(i), 16);
        }
    }

    private int packDigits() {
        int value = 0;
        for (int i = 0; i < 6; i++) {
            value = (value << 4) | (this.digits[i] & 0xF);
        }
        return TintColorUtil.rgb(value);
    }

    private int randomHexColor() {
        return ThreadLocalRandom.current().nextInt(0x1000000);
    }

    public ItemStack getTargetStack() {
        return this.player.getItemInHand(this.hand);
    }

    public boolean isTargetStillValid() {
        ItemStack stack = this.getTargetStack();
        return !stack.isEmpty() && stack.getItem() instanceof FlintAndPearlItem;
    }

    private void writeCurrentDigitsToHeldStack() {
        ItemStack stack = this.getTargetStack();
        if (stack.isEmpty() || !(stack.getItem() instanceof FlintAndPearlItem)) {
            return;
        }

        HexColorComponent.setSlot(stack, FlintAndPearlItem.HEX_SLOT, this.packDigits());
        this.player.getInventory().setChanged();

        if (this.player instanceof ServerPlayer serverPlayer) {
            serverPlayer.inventoryMenu.broadcastFullState();
        }
    }

    private void applyButtonEdit(int id) {
        if (id >= BUTTON_HEX_0 && id <= BUTTON_HEX_F) {
            if (this.cursor < 6) {
                this.digits[this.cursor] = id;
                this.cursor++;
            }
            return;
        }

        if (id == BUTTON_BACKSPACE) {
            if (this.cursor > 0) {
                this.cursor--;
                this.digits[this.cursor] = 0;
            }
            return;
        }

        if (id == BUTTON_CLEAR) {
            for (int i = 0; i < 6; i++) {
                this.digits[i] = 0;
            }
            this.cursor = 0;
            return;
        }

        if (id == BUTTON_DEFAULT) {
            this.loadFromHex(FlintAndPearlItem.DEFAULT_HEX_COLOR);
            this.cursor = 6;
            return;
        }

        if (id == BUTTON_RANDOM) {
            this.loadFromHex(this.randomHexColor());
            this.cursor = 6;
        }
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (!isKnownButton(id)) {
            return false;
        }

        if (!this.isTargetStillValid()) {
            return false;
        }

        if (id == BUTTON_RANDOM && player.level().isClientSide()) {
            return true;
        }

        this.applyButtonEdit(id);

        if (!player.level().isClientSide()) {
            this.writeCurrentDigitsToHeldStack();
            this.broadcastChanges();
        }

        return true;
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

        return this.isTargetStillValid();
    }
}
