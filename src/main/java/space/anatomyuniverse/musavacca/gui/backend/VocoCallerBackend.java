package space.anatomyuniverse.musavacca.gui.backend;

import java.util.Locale;

public final class VocoCallerBackend {

    public static final int ROW_COUNT = 13;
    public static final int CURRENT_DIALED_COUNT = 1;

    private String currentDialed = "6B3FD2";

    private final String[] recentCalls = {
            "000000", "45AF10", "C8293D", "7E114B", "09FF42",
            "D481AC", "602BE7", "F19D30", "3480CE", "BDA512",
            "72C8F4", "E00391", "5FAC26"
    };

    private final String[] savedNumbers = {
            "12ABEF", "C041D8", "93E52A", "0F72BC", "DDA903",
            "48C617", "AE304F", "7591DB", "E6B820", "31FD95",
            "BC4701", "8A2EC3", "F0547D"
    };

    public String getCurrentDialed() {
        return this.currentDialed;
    }

    public void setCurrentDialed(String hexCode) {
        this.currentDialed = normalizeHex(hexCode);
    }

    public void setCurrentDialed(int hexColor) {
        this.setCurrentDialed(formatHex(hexColor));
    }

    public void clearCurrentDialed() {
        this.currentDialed = null;
    }

    public String getRecentCall(int row) {
        checkRow(row);
        return this.recentCalls[row];
    }

    public boolean hasRecentCall(int row) {
        return this.getRecentCall(row) != null;
    }

    public void setRecentCall(int row, String hexCode) {
        checkRow(row);
        this.recentCalls[row] = normalizeHex(hexCode);
    }

    public void setRecentCall(int row, int hexColor) {
        this.setRecentCall(row, formatHex(hexColor));
    }

    public void clearRecentCall(int row) {
        checkRow(row);
        this.recentCalls[row] = null;
    }

    public void deleteRecentCall(int row) {
        checkRow(row);
        removeAndCompact(this.recentCalls, row);
    }

    /** Appends to Recent after compacting it, or returns false when it is full. */
    public boolean appendRecentCallIfSpace(String hexCode) {
        String normalized = normalizeHex(hexCode);
        if (normalized == null) {
            return false;
        }
        compact(this.recentCalls);
        for (int row = 0; row < ROW_COUNT; row++) {
            if (this.recentCalls[row] != null) {
                continue;
            }
            this.recentCalls[row] = normalized;
            return true;
        }
        return false;
    }

    public boolean appendRecentCallIfSpace(int hexColor) {
        return this.appendRecentCallIfSpace(formatHex(hexColor));
    }

    public String getSavedNumber(int row) {
        checkRow(row);
        return this.savedNumbers[row];
    }

    public boolean hasSavedNumber(int row) {
        return this.getSavedNumber(row) != null;
    }

    public void setSavedNumber(int row, String hexCode) {
        checkRow(row);
        this.savedNumbers[row] = normalizeHex(hexCode);
    }

    public void setSavedNumber(int row, int hexColor) {
        this.setSavedNumber(row, formatHex(hexColor));
    }

    public void clearSavedNumber(int row) {
        checkRow(row);
        this.savedNumbers[row] = null;
    }

    public void deleteSavedNumber(int row) {
        checkRow(row);
        removeAndCompact(this.savedNumbers, row);
    }

    /** Deletes from Saved and returns whether the removed number fit in Recent. */
    public boolean deleteSavedNumberToRecent(int row) {
        checkRow(row);
        String removed = this.savedNumbers[row];
        if (removed == null) {
            return false;
        }
        this.deleteSavedNumber(row);
        return this.appendRecentCallIfSpace(removed);
    }

    /** Moves a Recent number to Saved row 0 and recycles any displaced Saved number. */
    public boolean moveRecentCallToSavedTop(int row) {
        checkRow(row);
        String moving = this.recentCalls[row];
        if (moving == null) {
            return false;
        }
        this.deleteRecentCall(row);
        String displacedSaved = this.insertSavedNumberAtTop(moving);
        if (displacedSaved == null) {
            return true;
        }
        this.appendRecentCallIfSpace(displacedSaved);
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
        if (normalized == null) {
            return null;
        }
        compact(this.savedNumbers);
        String displaced = this.savedNumbers[ROW_COUNT - 1];
        for (int row = ROW_COUNT - 1; row > 0; row--) {
            this.savedNumbers[row] = this.savedNumbers[row - 1];
        }
        this.savedNumbers[0] = normalized;
        return displaced;
    }

    /** Moves a Saved number between filled rows, wrapping at either end. */
    public int moveSavedNumber(int row, int direction) {
        checkRow(row);
        if (this.savedNumbers[row] == null) {
            return row;
        }
        int step = Integer.compare(direction, 0);
        if (step == 0) {
            return row;
        }
        int[] filledRows = new int[ROW_COUNT];
        int filledCount = 0;
        int currentFilledIndex = -1;
        for (int index = 0; index < ROW_COUNT; index++) {
            if (this.savedNumbers[index] == null) {
                continue;
            }
            filledRows[filledCount] = index;
            if (index == row) {
                currentFilledIndex = filledCount;
            }
            filledCount++;
        }
        if (filledCount <= 1 || currentFilledIndex < 0) {
            return row;
        }
        if (step > 0 && currentFilledIndex < filledCount - 1) {
            int targetRow = filledRows[currentFilledIndex + 1];
            swap(this.savedNumbers, row, targetRow);
            return targetRow;
        }
        if (step < 0 && currentFilledIndex > 0) {
            int targetRow = filledRows[currentFilledIndex - 1];
            swap(this.savedNumbers, row, targetRow);
            return targetRow;
        }
        if (step > 0) {
            String moving = this.savedNumbers[row];
            for (int index = filledCount - 1; index > 0; index--) {
                this.savedNumbers[filledRows[index]] = this.savedNumbers[filledRows[index - 1]];
            }
            int targetRow = filledRows[0];
            this.savedNumbers[targetRow] = moving;
            return targetRow;
        }
        String moving = this.savedNumbers[row];
        for (int index = 0; index < filledCount - 1; index++) {
            this.savedNumbers[filledRows[index]] = this.savedNumbers[filledRows[index + 1]];
        }
        int targetRow = filledRows[filledCount - 1];
        this.savedNumbers[targetRow] = moving;
        return targetRow;
    }

    private static void swap(String[] values, int first, int second) {
        String temporary = values[first];
        values[first] = values[second];
        values[second] = temporary;
    }

    /** Removes one entry and closes the gap below it. */
    private static void removeAndCompact(String[] values, int row) {
        if (values[row] == null) {
            return;
        }
        for (int index = row; index < values.length - 1; index++) {
            values[index] = values[index + 1];
        }
        values[values.length - 1] = null;
    }

    /** Removes empty holes without changing entry order. */
    private static void compact(String[] values) {
        int write = 0;
        for (int read = 0; read < values.length; read++) {
            String value = values[read];
            if (value == null) {
                continue;
            }
            values[write] = value;
            write++;
        }
        while (write < values.length) {
            values[write] = null;
            write++;
        }
    }

    public static int parseHex(String hexCode) {
        String normalized = normalizeHex(hexCode);
        if (normalized == null) {
            return -1;
        }
        return Integer.parseInt(normalized, 16);
    }

    public static String formatHex(int hexColor) {
        return String.format(Locale.ROOT, "%06X", hexColor & 0xFFFFFF);
    }

    public static String normalizeHex(String hexCode) {
        if (hexCode == null) {
            return null;
        }
        String value = hexCode.trim();
        if (value.startsWith("#")) {
            value = value.substring(1);
        }
        if (value.length() != 6) {
            throw new IllegalArgumentException("Hex code must contain exactly 6 hexadecimal digits: " + hexCode);
        }
        try {
            int color = Integer.parseInt(value, 16);
            return formatHex(color);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Invalid hexadecimal code: " + hexCode, exception);
        }
    }

    private static void checkRow(int row) {
        if (row < 0 || row >= ROW_COUNT) {
            throw new IndexOutOfBoundsException("Voco Caller row must be between 0 and " + (ROW_COUNT - 1) + ", got " + row);
        }
    }
}
