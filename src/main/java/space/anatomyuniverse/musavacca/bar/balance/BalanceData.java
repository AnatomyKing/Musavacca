// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/bar/balance/BalanceData.java
package space.anatomyuniverse.musavacca.bar.balance;

import net.minecraft.util.Mth;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;

public final class BalanceData implements ValueIOSerializable {
    private static final int MAX_BALANCE = Integer.MAX_VALUE;

    private int balance = 0;

    private int lastSyncedBalance = Integer.MIN_VALUE;
    private boolean lastSyncedActive = false;

    @Override
    public void serialize(ValueOutput output) {
        output.putInt("balance", this.balance);
    }

    @Override
    public void deserialize(ValueInput input) {
        this.balance = Mth.clamp(input.getIntOr("balance", 0), 0, MAX_BALANCE);

        this.lastSyncedBalance = Integer.MIN_VALUE;
        this.lastSyncedActive = false;

        clampToValidState();
    }

    public int getBalance() {
        return this.balance;
    }

    public void setBalance(int balance) {
        this.balance = Mth.clamp(balance, 0, MAX_BALANCE);
    }

    public boolean addBalance(int amount) {
        if (amount <= 0) {
            return false;
        }

        int oldBalance = this.balance;

        if (MAX_BALANCE - this.balance < amount) {
            this.balance = MAX_BALANCE;
        } else {
            this.balance += amount;
        }

        clampToValidState();
        return this.balance != oldBalance;
    }

    public boolean removeBalance(int amount) {
        if (amount <= 0) {
            return false;
        }

        int oldBalance = this.balance;
        this.balance = Math.max(0, this.balance - amount);

        clampToValidState();
        return this.balance != oldBalance;
    }

    public boolean hasAtLeast(int amount) {
        return amount <= 0 || this.balance >= amount;
    }

    public boolean shouldSync(boolean active) {
        return active != this.lastSyncedActive
                || this.balance != this.lastSyncedBalance;
    }

    public void markSynced(boolean active) {
        this.lastSyncedActive = active;
        this.lastSyncedBalance = this.balance;
    }

    private void clampToValidState() {
        this.balance = Mth.clamp(this.balance, 0, MAX_BALANCE);
    }
}