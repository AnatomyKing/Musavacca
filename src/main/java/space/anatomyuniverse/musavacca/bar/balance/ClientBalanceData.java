package space.anatomyuniverse.musavacca.bar.balance;

public final class ClientBalanceData {
    private static int balance = 0;
    private static boolean active = false;

    private ClientBalanceData() {
    }

    public static void set(int newBalance, boolean newActive) {
        balance = Math.max(0, newBalance);
        active = newActive && balance > 0;
    }

    public static int getBalance() {
        return balance;
    }

    public static boolean isActive() {
        return active;
    }
}
