package space.anatomyuniverse.musavacca.gui.backend;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import space.anatomyuniverse.musavacca.teleport.HexTeleportDirectory;
import space.anatomyuniverse.musavacca.teleport.HexTeleportResolver;

import java.util.Arrays;

public class VocoDialerBackend {
    public static final int BUTTON_HEX_0 = 0;
    public static final int BUTTON_HEX_F = 15;
    public static final int BUTTON_CLEAR = 100;

    private static final int BUTTON_CALL_BASE = 0x100;
    private static final int CODE_LENGTH = 6;
    private static final int MAX_ADDRESS = 0xFFFFFF;

    private final int[] nibbles = new int[CODE_LENGTH];
    private int cursor = 0;

    public static boolean isKnownButton(int id) {
        return id >= BUTTON_HEX_0 && id <= BUTTON_HEX_F || id == BUTTON_CLEAR || isCallButton(id);
    }

    public static int buttonForAddress(int address) {
        return BUTTON_CALL_BASE + HexTeleportDirectory.normalizeHex(address);
    }

    private static boolean isCallButton(int id) {
        return id >= BUTTON_CALL_BASE && id <= BUTTON_CALL_BASE + MAX_ADDRESS;
    }

    public boolean handleButton(Player player, int id) {
        if (!isKnownButton(id)) return false;
        if (id == BUTTON_CLEAR) clear();
        else if (isCallButton(id)) callAddress(player, id - BUTTON_CALL_BASE);
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
        if (this.cursor >= CODE_LENGTH) return;
        this.nibbles[this.cursor++] = clampNibble(nibble);
        if (this.cursor < CODE_LENGTH) return;
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
        HexTeleportResolver.teleportToHex(serverPlayer, address);
        serverPlayer.closeContainer();
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


