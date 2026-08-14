package space.anatomyuniverse.musavacca.gui.backend;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import space.anatomyuniverse.musavacca.teleport.HexTeleportDirectory;
import space.anatomyuniverse.musavacca.teleport.HexTeleportResolver;

import java.util.Arrays;

public final class VocoDialerBackend {
    public static final int BUTTON_HEX_0 = 0;
    public static final int BUTTON_HEX_F = 15;
    public static final int BUTTON_CLEAR = 100;

    private static final int CODE_LENGTH = 6;

    private final int[] nibbles = new int[CODE_LENGTH];

    private int cursor = 0;

    public static boolean isKnownButton(int id) {
        return (id >= BUTTON_HEX_0 && id <= BUTTON_HEX_F) || id == BUTTON_CLEAR;
    }

    public boolean handleButton(Player player, int id) {
        if (!isKnownButton(id)) {
            return false;
        }

        if (id == BUTTON_CLEAR) {
            this.clear();
            return true;
        }

        this.dial(player, id);
        return true;
    }

    private void dial(Player player, int nibble) {
        if (this.cursor >= CODE_LENGTH) {
            return;
        }

        this.nibbles[this.cursor] = clampNibble(nibble);
        this.cursor++;

        if (this.cursor >= CODE_LENGTH) {
            this.teleportAndReset(player);
        }
    }

    private void teleportAndReset(Player player) {
        int address = this.packNibbles();

        this.clear();

        if (!player.level().isClientSide() && player instanceof ServerPlayer serverPlayer) {
            HexTeleportResolver.teleportToHex(serverPlayer, address);
            serverPlayer.closeContainer();
        }
    }

    public void clear() {
        Arrays.fill(this.nibbles, 0);
        this.cursor = 0;
    }

    private int packNibbles() {
        int value = 0;

        for (int nibble : this.nibbles) {
            value = (value << 4) | (nibble & 0xF);
        }

        return HexTeleportDirectory.normalizeHex(value);
    }

    private static int clampNibble(int value) {
        return Math.max(0, Math.min(15, value));
    }
}