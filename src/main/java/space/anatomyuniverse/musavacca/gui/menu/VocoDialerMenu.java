package space.anatomyuniverse.musavacca.gui.menu;

import java.util.Arrays;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import space.anatomyuniverse.musavacca.economy.EconomyConfig;
import space.anatomyuniverse.musavacca.gui.ModMenuRegistries;
import space.anatomyuniverse.musavacca.teleport.HexTeleportDirectory;
import space.anatomyuniverse.musavacca.teleport.HexTeleportResolver;

public class VocoDialerMenu extends AbstractContainerMenu {
    public static final int BUTTON_HEX_0 = 0;
    public static final int BUTTON_HEX_F = 15;
    public static final int BUTTON_CLEAR = 100;
    private final int[] nibbles = new int[6];
    private int cursor = 0;

    public VocoDialerMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf ignored) {
        this(containerId, playerInventory);
    }

    public VocoDialerMenu(int containerId, Inventory playerInventory) {
        this(ModMenuRegistries.VOCO_DIALER_MENU.get(), containerId);
    }

    protected VocoDialerMenu(MenuType<?> menuType, int containerId) {
        super(menuType, containerId);
    }

    public static void open(ServerPlayer player) {
        player.openMenu(new SimpleMenuProvider((containerId, inventory, ignoredPlayer) -> new VocoDialerMenu(containerId, inventory),
                Component.literal("Rotary Test GUI")));
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        boolean handled = this.handleButton(player, id);
        if (handled && !player.level().isClientSide()) this.broadcastChanges();
        return handled;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    public static boolean isKnownButton(int id) {
        return id >= BUTTON_HEX_0 && id <= BUTTON_HEX_F || id == BUTTON_CLEAR || isCallButton(id);
    }

    public static int buttonForAddress(int address) {
        return 0x100 + HexTeleportDirectory.normalizeHex(address);
    }

    private static boolean isCallButton(int id) {
        return id >= 0x100 && id <= 0x100 + 0xFFFFFF;
    }

    private boolean handleButton(Player player, int id) {
        if (!isKnownButton(id)) return false;
        if (id == BUTTON_CLEAR) clear();
        else if (isCallButton(id)) callAddress(player, id - 0x100);
        else dial(player, id);
        return true;
    }

    public String getCurrentDialed() {
        if (this.cursor == 0) return null;
        StringBuilder code = new StringBuilder(this.cursor);
        for (int index = 0; index < this.cursor; index++) code.append(Character.toUpperCase(Character.forDigit(this.nibbles[index], 16)));
        return code.toString();
    }

    private void dial(Player player, int nibble) {
        if (this.cursor >= 6) return;
        this.nibbles[this.cursor++] = clampNibble(nibble);
        if (this.cursor < 6) return;
        int address = this.packNibbles();
        if (this.shouldClearDialedBeforeCall(player)) this.clear();
        this.onAddressDialed(player, address);
    }

    private void callAddress(Player player, int address) {
        if (this.shouldClearDialedBeforeCall(player)) this.clear();
        this.onAddressDialed(player, HexTeleportDirectory.normalizeHex(address));
    }

    protected boolean shouldClearDialedBeforeCall(Player player) {
        return true;
    }

    protected void onAddressDialed(Player player, int address) {
        if (player.level().isClientSide() || !(player instanceof ServerPlayer serverPlayer)) return;
        HexTeleportResolver.teleportToHex(serverPlayer, address, this.teleportCost());
        serverPlayer.closeContainer();
    }

    protected int teleportCost() {
        return EconomyConfig.vocoTableTeleportCost();
    }

    public void clear() {
        Arrays.fill(this.nibbles, 0);
        this.cursor = 0;
    }

    private int packNibbles() {
        int value = 0;
        for (int nibble : this.nibbles) value = value << 4 | nibble & 0xF;
        return HexTeleportDirectory.normalizeHex(value);
    }

    private static int clampNibble(int value) {
        return Math.max(0, Math.min(15, value));
    }
}
