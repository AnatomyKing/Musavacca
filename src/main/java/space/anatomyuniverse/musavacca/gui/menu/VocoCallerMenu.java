package space.anatomyuniverse.musavacca.gui.menu;

import java.util.Locale;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import space.anatomyuniverse.musavacca.economy.EconomyConfig;
import space.anatomyuniverse.musavacca.gui.ModMenuRegistries;
import space.anatomyuniverse.musavacca.item.custom.OpenVocoCallerItem;
import space.anatomyuniverse.musavacca.item.custom.SimCardItem;
import space.anatomyuniverse.musavacca.vococaller.VocoCallerNetwork;
import space.anatomyuniverse.musavacca.vococaller.VocoCallerPhonebook;

public final class VocoCallerMenu extends VocoDialerMenu {
    public static final int ROW_COUNT = VocoCallerPhonebook.ROW_COUNT;
    private final String[] recentCalls = new String[ROW_COUNT];
    private final String[] savedNumbers = new String[ROW_COUNT];
    private String pendingRecentCall;
    private final int phoneHex;
    private final int openingSlot;
    private final ItemStack openingPhoneReference;

    public VocoCallerMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buffer) {
        this(containerId, buffer.readInt(), VocoCallerPhonebook.of(readAddresses(buffer), readAddresses(buffer)), -1, ItemStack.EMPTY);
    }

    private VocoCallerMenu(int containerId, int phoneHex, VocoCallerPhonebook phonebook, int openingSlot, ItemStack openingPhoneReference) {
        super(ModMenuRegistries.VOCO_CALLER_MENU.get(), containerId);
        VocoCallerPhonebook initial = phonebook == null ? VocoCallerPhonebook.EMPTY_PHONEBOOK : phonebook;
        this.replaceCallState(initial.recentArray(), initial.savedArray());
        this.phoneHex = phoneHex & 0xFFFFFF;
        this.openingSlot = openingSlot;
        this.openingPhoneReference = openingPhoneReference == null ? ItemStack.EMPTY : openingPhoneReference;
    }

    public static void open(ServerPlayer player, ItemStack phone) {
        ItemStack sim = OpenVocoCallerItem.getSim(phone);
        if (sim.isEmpty() || !SimCardItem.hasStoredHex(sim)) {
            return;
        }
        int openingSlot = findExactInventorySlot(player, phone);
        if (openingSlot < 0) return;
        int phoneHex = OpenVocoCallerItem.getSimHex(phone);
        VocoCallerPhonebook phonebook = SimCardItem.getPhonebook(sim);
        player.openMenu(
                new SimpleMenuProvider((containerId, inventory, ignoredPlayer) -> new VocoCallerMenu(containerId, phoneHex, phonebook, openingSlot, phone),
                        Component.literal("Voco Caller")),
                buffer -> {
                    buffer.writeInt(phoneHex);
                    writeAddresses(buffer, phonebook.recentArray());
                    writeAddresses(buffer, phonebook.savedArray());
                });
    }

    public int getPhoneHex() {
        return this.phoneHex;
    }

    public void applyClientState(ServerPlayer player, int[] recent, int[] saved) {
        if (!this.hasOpeningPhone(player)) return;
        this.replaceCallState(recent, saved);
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (!player.level().isClientSide() && !this.hasOpeningPhone(player)) {
            return false;
        }
        return super.clickMenuButton(player, id);
    }

    @Override
    public boolean stillValid(Player player) {
        return player.level().isClientSide() || this.hasOpeningPhone(player);
    }

    @Override
    public void removed(Player player) {
        if (!player.level().isClientSide() && player instanceof ServerPlayer serverPlayer) {
            ItemStack openingPhone = this.getOpeningPhone(serverPlayer);
            if (!openingPhone.isEmpty()) {
                VocoCallerNetwork.writePhonebook(serverPlayer, openingPhone, this.phoneHex, this.toPhonebook());
            }
        }
        super.removed(player);
    }

    private boolean hasOpeningPhone(Player player) {
        return !this.getOpeningPhone(player).isEmpty();
    }

    private ItemStack getOpeningPhone(Player player) {
        Inventory inventory = player.getInventory();
        if (this.openingSlot < 0 || this.openingSlot >= inventory.getContainerSize() || this.openingPhoneReference.isEmpty()) {
            return ItemStack.EMPTY;
        }
        ItemStack phone = inventory.getItem(this.openingSlot);
        if (phone != this.openingPhoneReference) return ItemStack.EMPTY;
        if (!(phone.getItem() instanceof OpenVocoCallerItem)) {
            return ItemStack.EMPTY;
        }
        ItemStack sim = OpenVocoCallerItem.getSim(phone);
        if (sim.isEmpty() || !SimCardItem.hasStoredHex(sim) || OpenVocoCallerItem.getSimHex(phone) != this.phoneHex) {
            return ItemStack.EMPTY;
        }
        return phone;
    }

    private static int findExactInventorySlot(ServerPlayer player, ItemStack phone) {
        Inventory inventory = player.getInventory();
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            if (inventory.getItem(slot) == phone) return slot;
        }
        return -1;
    }

    private static int[] readAddresses(RegistryFriendlyByteBuf buffer) {
        int[] result = new int[VocoCallerPhonebook.ROW_COUNT];
        for (int row = 0; row < result.length; row++) {
            result[row] = buffer.readInt();
        }
        return result;
    }

    private static void writeAddresses(RegistryFriendlyByteBuf buffer, int[] values) {
        for (int row = 0; row < VocoCallerPhonebook.ROW_COUNT; row++) {
            int value = values != null && row < values.length ? values[row] : VocoCallerPhonebook.EMPTY;
            buffer.writeInt(value < 0 ? VocoCallerPhonebook.EMPTY : value & 0xFFFFFF);
        }
    }

    @Override
    protected void onAddressDialed(Player player, int address) {
        if (player.level().isClientSide()) {
            this.pendingRecentCall = formatHex(address);
        } else {
            this.pushRecentCallToTop(address);
        }
        super.onAddressDialed(player, address);
    }

    @Override
    protected boolean shouldClearDialedBeforeCall(Player player) {
        return !player.level().isClientSide();
    }

    @Override
    protected int teleportCost() {
        return EconomyConfig.vocoCallerTeleportCost();
    }

    public void commitPendingRecentCall() {
        String pending = this.pendingRecentCall;
        this.pendingRecentCall = null;
        if (pending != null) this.pushRecentCallToTop(pending);
    }

    public void replaceCallState(int[] recent, int[] saved) {
        fillFromAddresses(this.recentCalls, recent);
        fillFromAddresses(this.savedNumbers, saved);
    }

    public VocoCallerPhonebook toPhonebook() {
        return VocoCallerPhonebook.of(this.copyRecentAddresses(), this.copySavedAddresses());
    }

    public int[] copyRecentAddresses() {
        return copyAddresses(this.recentCalls);
    }

    public int[] copySavedAddresses() {
        return copyAddresses(this.savedNumbers);
    }

    public String getRecentCall(int row) {
        checkRow(row);
        return this.recentCalls[row];
    }

    public boolean hasRecentCall(int row) {
        return this.getRecentCall(row) != null;
    }

    public void deleteRecentCall(int row) {
        checkRow(row);
        removeAndCompact(this.recentCalls, row);
    }

    private void pushRecentCallToTop(String hexCode) {
        String normalized = normalizeHex(hexCode);
        if (normalized == null) return;
        removeAll(this.recentCalls, normalized);
        for (int row = ROW_COUNT - 1; row > 0; row--) this.recentCalls[row] = this.recentCalls[row - 1];
        this.recentCalls[0] = normalized;
    }

    private void pushRecentCallToTop(int hexColor) {
        this.pushRecentCallToTop(formatHex(hexColor));
    }

    private boolean appendRecentCallIfSpace(String hexCode) {
        String normalized = normalizeHex(hexCode);
        if (normalized == null) return false;
        if (indexOf(this.recentCalls, normalized) >= 0) return true;
        compact(this.recentCalls);
        for (int row = 0; row < ROW_COUNT; row++) {
            if (this.recentCalls[row] != null) continue;
            this.recentCalls[row] = normalized;
            return true;
        }
        return false;
    }

    public String getSavedNumber(int row) {
        checkRow(row);
        return this.savedNumbers[row];
    }

    public boolean hasSavedNumber(int row) {
        return this.getSavedNumber(row) != null;
    }

    public boolean deleteSavedNumberToRecent(int row) {
        checkRow(row);
        String removed = this.savedNumbers[row];
        if (removed == null) return false;
        removeAll(this.savedNumbers, removed);
        return this.appendRecentCallIfSpace(removed);
    }

    public boolean moveRecentCallToSavedTop(int row) {
        checkRow(row);
        String moving = this.recentCalls[row];
        if (moving == null) return false;
        removeAll(this.recentCalls, moving);
        String displacedSaved = this.insertSavedNumberAtTop(moving);
        if (displacedSaved != null) this.appendRecentCallIfSpace(displacedSaved);
        return true;
    }

    private String insertSavedNumberAtTop(String hexCode) {
        String normalized = normalizeHex(hexCode);
        if (normalized == null) return null;
        boolean alreadySaved = removeAll(this.savedNumbers, normalized);
        String displaced = alreadySaved ? null : this.savedNumbers[ROW_COUNT - 1];
        for (int row = ROW_COUNT - 1; row > 0; row--) this.savedNumbers[row] = this.savedNumbers[row - 1];
        this.savedNumbers[0] = normalized;
        return displaced;
    }

    public int moveSavedNumber(int row, int direction) {
        checkRow(row);
        if (this.savedNumbers[row] == null) return row;
        int step = Integer.compare(direction, 0);
        if (step == 0) return row;
        int[] filledRows = new int[ROW_COUNT];
        int filledCount = 0;
        int currentFilledIndex = -1;
        for (int index = 0; index < ROW_COUNT; index++) {
            if (this.savedNumbers[index] == null) continue;
            filledRows[filledCount] = index;
            if (index == row) currentFilledIndex = filledCount;
            filledCount++;
        }
        if (filledCount <= 1 || currentFilledIndex < 0) return row;
        int nextIndex = currentFilledIndex + step;
        if (nextIndex >= 0 && nextIndex < filledCount) {
            int targetRow = filledRows[nextIndex];
            swap(this.savedNumbers, row, targetRow);
            return targetRow;
        }
        String moving = this.savedNumbers[row];
        if (step > 0) {
            for (int index = filledCount - 1; index > 0; index--) this.savedNumbers[filledRows[index]] = this.savedNumbers[filledRows[index - 1]];
            int targetRow = filledRows[0];
            this.savedNumbers[targetRow] = moving;
            return targetRow;
        }
        for (int index = 0; index < filledCount - 1; index++) this.savedNumbers[filledRows[index]] = this.savedNumbers[filledRows[index + 1]];
        int targetRow = filledRows[filledCount - 1];
        this.savedNumbers[targetRow] = moving;
        return targetRow;
    }

    private static int[] copyAddresses(String[] values) {
        int[] result = new int[ROW_COUNT];
        for (int row = 0; row < ROW_COUNT; row++) {
            result[row] = values[row] == null ? VocoCallerPhonebook.EMPTY : parseHex(values[row]);
        }
        return result;
    }

    private static void fillFromAddresses(String[] target, int[] values) {
        for (int row = 0; row < ROW_COUNT; row++) {
            target[row] = values != null && row < values.length && values[row] >= 0 ? formatHex(values[row]) : null;
        }
        compact(target);
    }

    private static void swap(String[] values, int first, int second) {
        String temporary = values[first];
        values[first] = values[second];
        values[second] = temporary;
    }

    private static void removeAndCompact(String[] values, int row) {
        if (values[row] == null) return;
        for (int index = row; index < values.length - 1; index++) values[index] = values[index + 1];
        values[values.length - 1] = null;
    }

    private static void compact(String[] values) {
        int write = 0;
        for (String value : values) if (value != null) values[write++] = value;
        while (write < values.length) values[write++] = null;
    }

    private static boolean removeAll(String[] values, String value) {
        boolean removed = false;
        for (int index = 0; index < values.length; index++) {
            if (!value.equals(values[index])) continue;
            values[index] = null;
            removed = true;
        }
        compact(values);
        return removed;
    }

    private static int indexOf(String[] values, String value) {
        for (int index = 0; index < values.length; index++) if (value.equals(values[index])) return index;
        return -1;
    }

    public static int parseHex(String hexCode) {
        String normalized = normalizeHex(hexCode);
        return normalized == null ? -1 : Integer.parseInt(normalized, 16);
    }

    public static String formatHex(int hexColor) {
        return String.format(Locale.ROOT, "%06X", hexColor & 0xFFFFFF);
    }

    private static String normalizeHex(String hexCode) {
        if (hexCode == null) return null;
        String value = hexCode.trim();
        if (value.startsWith("#")) value = value.substring(1);
        if (value.length() != 6) throw new IllegalArgumentException("Hex code must contain exactly 6 hexadecimal digits: " + hexCode);
        try {
            return formatHex(Integer.parseInt(value, 16));
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Invalid hexadecimal code: " + hexCode, exception);
        }
    }

    private static void checkRow(int row) {
        if (row < 0 || row >= ROW_COUNT) throw new IndexOutOfBoundsException("Voco Caller row must be between 0 and " + (ROW_COUNT - 1) + ", got " + row);
    }
}
