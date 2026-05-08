// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/gui/menu/HexTeleportMenu.java
package space.anatomyuniverse.musavacca.gui.menu;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;
import space.anatomyuniverse.musavacca.gui.ModMenus;
import space.anatomyuniverse.musavacca.item.custom.FlintAndPearlItem;
import space.anatomyuniverse.musavacca.teleport.HexTeleportDirectory;
import space.anatomyuniverse.musavacca.teleport.HexTeleportResolver;

import java.util.Locale;

public class HexTeleportMenu extends AbstractContainerMenu {
    public static final int BUTTON_HEX_0 = 0;
    public static final int BUTTON_HEX_F = 15;

    public static final int BUTTON_BACKSPACE = 100;
    public static final int BUTTON_CLEAR = 101;
    public static final int BUTTON_DEFAULT = 102;
    public static final int BUTTON_TELEPORT = 103;

    private static final String HEX = "0123456789ABCDEF";

    private final Player player;
    private final int[] digits = new int[6];

    private int cursor = 6;

    public HexTeleportMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf ignored) {
        this(containerId, playerInventory, FlintAndPearlItem.DEFAULT_HEX_COLOR);
    }

    public HexTeleportMenu(int containerId, Inventory playerInventory, int initialHex) {
        super(ModMenus.HEX_TELEPORT_MENU.get(), containerId);
        this.player = playerInventory.player;

        this.loadFromHex(initialHex);
        this.cursor = 6;

        this.addSyncSlots();
    }

    public static void open(ServerPlayer player) {
        player.openMenu(
                new SimpleMenuProvider(
                        (containerId, inventory, ignoredPlayer) -> new HexTeleportMenu(
                                containerId,
                                inventory,
                                FlintAndPearlItem.DEFAULT_HEX_COLOR
                        ),
                        Component.literal("Pearl Address Dialer")
                )
        );
    }

    private void addSyncSlots() {
        this.addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return HexTeleportMenu.this.cursor;
            }

            @Override
            public void set(int value) {
                HexTeleportMenu.this.cursor = Math.max(0, Math.min(6, value));
            }
        });

        for (int i = 0; i < 6; i++) {
            final int index = i;
            this.addDataSlot(new DataSlot() {
                @Override
                public int get() {
                    return HexTeleportMenu.this.digits[index];
                }

                @Override
                public void set(int value) {
                    HexTeleportMenu.this.digits[index] = Math.max(0, Math.min(15, value));
                }
            });
        }
    }

    public static boolean isKnownButton(int id) {
        return (id >= BUTTON_HEX_0 && id <= BUTTON_HEX_F)
                || id == BUTTON_BACKSPACE
                || id == BUTTON_CLEAR
                || id == BUTTON_DEFAULT
                || id == BUTTON_TELEPORT;
    }

    public static int buttonIdForHexNibble(int nibble) {
        return Math.max(0, Math.min(15, nibble));
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
        int normalized = HexTeleportDirectory.normalizeHex(rgb);
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
        return HexTeleportDirectory.normalizeHex(value);
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
        }
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (!isKnownButton(id)) {
            return false;
        }

        if (id == BUTTON_TELEPORT) {
            if (!player.level().isClientSide() && player instanceof ServerPlayer serverPlayer) {
                HexTeleportResolver.teleportToHex(serverPlayer, this.packDigits());
            }

            return true;
        }

        this.applyButtonEdit(id);

        if (!player.level().isClientSide()) {
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
        return true;
    }
}