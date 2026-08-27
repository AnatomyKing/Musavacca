package space.anatomyuniverse.musavacca.gui.backend;

import net.minecraft.world.entity.player.Player;
import space.anatomyuniverse.musavacca.vococaller.VocoCallerPhonebook;

import java.util.Locale;

public final class VocoCallerBackend extends VocoDialerBackend {
    public static final int ROW_COUNT = VocoCallerPhonebook.ROW_COUNT;

    private final CallState state = new CallState();

    public VocoCallerBackend(VocoCallerPhonebook phonebook) {
        VocoCallerPhonebook initial =
                phonebook == null
                        ? VocoCallerPhonebook.EMPTY_PHONEBOOK
                        : phonebook;

        this.replaceCallState(
                initial.recentArray(),
                initial.savedArray()
        );
    }

    @Override
    protected void onAddressDialed(Player player, int address) {
        if (player.level().isClientSide()) {
            this.state.pendingRecentCall = formatHex(address);
            super.onAddressDialed(player, address);
            return;
        }

        /*
         * Record first. A successful teleport closes the menu, and menu close
         * is what writes this session back into the SIM component.
         */
        this.pushRecentCallToTop(address);
        super.onAddressDialed(player, address);
    }

    @Override
    protected boolean shouldClearDialedBeforeCall(Player player) {
        return !player.level().isClientSide();
    }

    public void commitPendingRecentCall() {
        String pending = this.state.pendingRecentCall;
        this.state.pendingRecentCall = null;
        if (pending != null) this.pushRecentCallToTop(pending);
    }

    public CallStateSnapshot snapshotCallState() {
        return new CallStateSnapshot(this.state.recentCalls.clone(), this.state.savedNumbers.clone());
    }

    public void restoreCallState(CallStateSnapshot snapshot) {
        System.arraycopy(snapshot.recentCalls, 0, this.state.recentCalls, 0, ROW_COUNT);
        System.arraycopy(snapshot.savedNumbers, 0, this.state.savedNumbers, 0, ROW_COUNT);
    }

    public void replaceCallState(
            int[] recent,
            int[] saved
    ) {
        fillFromAddresses(
                this.state.recentCalls,
                recent
        );

        fillFromAddresses(
                this.state.savedNumbers,
                saved
        );
    }

    public VocoCallerPhonebook toPhonebook() {
        return VocoCallerPhonebook.of(
                this.copyRecentAddresses(),
                this.copySavedAddresses()
        );
    }

    public int[] copyRecentAddresses() {
        return copyAddresses(
                this.state.recentCalls
        );
    }

    public int[] copySavedAddresses() {
        return copyAddresses(
                this.state.savedNumbers
        );
    }

    public String getRecentCall(int row) {
        checkRow(row);
        return this.state.recentCalls[row];
    }

    public boolean hasRecentCall(int row) {
        return this.getRecentCall(row) != null;
    }

    public void setRecentCall(int row, String hexCode) {
        checkRow(row);
        setUnique(this.state.recentCalls, row, normalizeHex(hexCode));
    }

    public void setRecentCall(int row, int hexColor) {
        this.setRecentCall(row, formatHex(hexColor));
    }

    public void clearRecentCall(int row) {
        checkRow(row);
        this.state.recentCalls[row] = null;
    }

    public void deleteRecentCall(int row) {
        checkRow(row);
        removeAndCompact(this.state.recentCalls, row);
    }

    public void pushRecentCallToTop(String hexCode) {
        String normalized = normalizeHex(hexCode);
        if (normalized == null) return;
        removeAll(this.state.recentCalls, normalized);
        for (int row = ROW_COUNT - 1; row > 0; row--) this.state.recentCalls[row] = this.state.recentCalls[row - 1];
        this.state.recentCalls[0] = normalized;
    }

    public void pushRecentCallToTop(int hexColor) {
        this.pushRecentCallToTop(formatHex(hexColor));
    }

    /** Appends to Recent after compacting it, or returns false when it is full. */
    public boolean appendRecentCallIfSpace(String hexCode) {
        String normalized = normalizeHex(hexCode);
        if (normalized == null) return false;
        if (indexOf(this.state.recentCalls, normalized) >= 0) return true;
        compact(this.state.recentCalls);
        for (int row = 0; row < ROW_COUNT; row++) {
            if (this.state.recentCalls[row] != null) continue;
            this.state.recentCalls[row] = normalized;
            return true;
        }
        return false;
    }

    public boolean appendRecentCallIfSpace(int hexColor) {
        return this.appendRecentCallIfSpace(formatHex(hexColor));
    }

    public String getSavedNumber(int row) {
        checkRow(row);
        return this.state.savedNumbers[row];
    }

    public boolean hasSavedNumber(int row) {
        return this.getSavedNumber(row) != null;
    }

    public void setSavedNumber(int row, String hexCode) {
        checkRow(row);
        setUnique(this.state.savedNumbers, row, normalizeHex(hexCode));
    }

    public void setSavedNumber(int row, int hexColor) {
        this.setSavedNumber(row, formatHex(hexColor));
    }

    public void clearSavedNumber(int row) {
        checkRow(row);
        this.state.savedNumbers[row] = null;
    }

    public void deleteSavedNumber(int row) {
        checkRow(row);
        removeAndCompact(this.state.savedNumbers, row);
    }

    /** Deletes from Saved and returns whether the removed number fit in Recent. */
    public boolean deleteSavedNumberToRecent(int row) {
        checkRow(row);
        String removed = this.state.savedNumbers[row];
        if (removed == null) return false;
        removeAll(this.state.savedNumbers, removed);
        return this.appendRecentCallIfSpace(removed);
    }

    /** Moves a Recent number to Saved row 0 and recycles any displaced Saved number. */
    public boolean moveRecentCallToSavedTop(int row) {
        checkRow(row);
        String moving = this.state.recentCalls[row];
        if (moving == null) return false;
        removeAll(this.state.recentCalls, moving);
        String displacedSaved = this.insertSavedNumberAtTop(moving);
        if (displacedSaved != null) this.appendRecentCallIfSpace(displacedSaved);
        return true;
    }

    /** Pushes to Saved row 0 and discards anything displaced from a full list. */
    public void pushSavedNumberToTop(String hexCode) {
        this.insertSavedNumberAtTop(hexCode);
    }

    public void pushSavedNumberToTop(int hexColor) {
        this.pushSavedNumberToTop(formatHex(hexColor));
    }

    /** Inserts at Saved row 0 and returns an entry displaced from a full list. */
    private String insertSavedNumberAtTop(String hexCode) {
        String normalized = normalizeHex(hexCode);
        if (normalized == null) return null;
        boolean alreadySaved = removeAll(this.state.savedNumbers, normalized);
        String displaced = alreadySaved ? null : this.state.savedNumbers[ROW_COUNT - 1];
        for (int row = ROW_COUNT - 1; row > 0; row--) this.state.savedNumbers[row] = this.state.savedNumbers[row - 1];
        this.state.savedNumbers[0] = normalized;
        return displaced;
    }

    /** Moves a Saved number between filled rows, wrapping at either end. */
    public int moveSavedNumber(int row, int direction) {
        checkRow(row);
        if (this.state.savedNumbers[row] == null) return row;
        int step = Integer.compare(direction, 0);
        if (step == 0) return row;
        int[] filledRows = new int[ROW_COUNT];
        int filledCount = 0;
        int currentFilledIndex = -1;
        for (int index = 0; index < ROW_COUNT; index++) {
            if (this.state.savedNumbers[index] == null) continue;
            filledRows[filledCount] = index;
            if (index == row) currentFilledIndex = filledCount;
            filledCount++;
        }
        if (filledCount <= 1 || currentFilledIndex < 0) return row;
        if (step > 0 && currentFilledIndex < filledCount - 1) {
            int targetRow = filledRows[currentFilledIndex + 1];
            swap(this.state.savedNumbers, row, targetRow);
            return targetRow;
        }
        if (step < 0 && currentFilledIndex > 0) {
            int targetRow = filledRows[currentFilledIndex - 1];
            swap(this.state.savedNumbers, row, targetRow);
            return targetRow;
        }
        String moving = this.state.savedNumbers[row];
        if (step > 0) {
            for (int index = filledCount - 1; index > 0; index--) this.state.savedNumbers[filledRows[index]] = this.state.savedNumbers[filledRows[index - 1]];
            int targetRow = filledRows[0];
            this.state.savedNumbers[targetRow] = moving;
            return targetRow;
        }
        for (int index = 0; index < filledCount - 1; index++) this.state.savedNumbers[filledRows[index]] = this.state.savedNumbers[filledRows[index + 1]];
        int targetRow = filledRows[filledCount - 1];
        this.state.savedNumbers[targetRow] = moving;
        return targetRow;
    }

    private static int[] copyAddresses(
            String[] values
    ) {
        int[] result = new int[ROW_COUNT];

        for (int row = 0; row < ROW_COUNT; row++) {
            result[row] =
                    values[row] == null
                            ? VocoCallerPhonebook.EMPTY
                            : parseHex(values[row]);
        }

        return result;
    }

    private static void fillFromAddresses(
            String[] target,
            int[] values
    ) {
        for (int row = 0; row < ROW_COUNT; row++) {
            target[row] =
                    values != null
                            && row < values.length
                            && values[row] >= 0
                            ? formatHex(values[row])
                            : null;
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

    private static void setUnique(String[] values, int row, String value) {
        values[row] = value;
        if (value == null) return;
        for (int index = 0; index < values.length; index++) if (index != row && value.equals(values[index])) values[index] = null;
        compact(values);
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

    public static String normalizeHex(String hexCode) {
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

    private static final class CallState {
        private final String[] recentCalls = new String[ROW_COUNT];
        private final String[] savedNumbers = new String[ROW_COUNT];
        private String pendingRecentCall;
    }

    public static final class CallStateSnapshot {
        private final String[] recentCalls;
        private final String[] savedNumbers;

        private CallStateSnapshot(String[] recentCalls, String[] savedNumbers) {
            this.recentCalls = recentCalls;
            this.savedNumbers = savedNumbers;
        }
    }
}
