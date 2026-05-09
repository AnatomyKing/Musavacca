// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/bar/balance/BalanceData.java
package space.anatomyuniverse.musavacca.bar.balance;

import net.minecraft.util.Mth;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;

public final class BalanceData implements ValueIOSerializable {
    private int balance = 0;

    @Override
    public void serialize(ValueOutput output) {
        output.putInt("balance", this.balance);
    }

    @Override
    public void deserialize(ValueInput input) {
        this.balance = sanitizeBalance(input.getIntOr("balance", 0));
    }

    public int getBalance() {
        return this.balance;
    }

    public void setBalance(int balance) {
        this.balance = sanitizeBalance(balance);
    }

    public boolean isVisible() {
        return this.balance > 0;
    }

    private static int sanitizeBalance(int balance) {
        return Mth.clamp(balance, 0, Integer.MAX_VALUE);
    }
}