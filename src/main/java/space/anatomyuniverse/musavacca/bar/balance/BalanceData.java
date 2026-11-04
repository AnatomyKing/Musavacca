package space.anatomyuniverse.musavacca.bar.balance;

//? if <1.21.6 {
/*import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
*///?}
import net.minecraft.util.Mth;
//? if >=1.21.6 {
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
//?} else {
/*import net.neoforged.neoforge.common.util.INBTSerializable;
*///?}

@SuppressWarnings({"unused", "CommentedOutCode", "NullableProblems"})
//? if >=1.21.6 {
public final class BalanceData implements ValueIOSerializable {
//?} else {
    /*public final class BalanceData implements INBTSerializable<CompoundTag> {
    *///?}
    private int balance = 0;
    //? if >=1.21.6 {
    @Override
    public void serialize(ValueOutput output) {
        output.putInt("balance", this.balance);
    }
    //?}
    //? if <1.21.6 {
    /*@Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("balance", this.balance);
        return tag;
    }
    *///?}
    //? if >=1.21.6 {
    @Override
    public void deserialize(ValueInput input) {
        this.balance = sanitizeBalance(input.getIntOr("balance", 0));
    }
    //?}
    //? if <1.21.6 {
    /*@Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
        //? if >=1.21.5 <1.21.6 {
        this.balance = sanitizeBalance(tag.getIntOr("balance", 0));
        //?} else {
        /^this.balance = sanitizeBalance(tag.contains("balance") ? tag.getInt("balance") : 0);
        ^///?}
    }
    *///?}
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
