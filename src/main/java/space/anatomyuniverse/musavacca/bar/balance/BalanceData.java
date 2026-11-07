package space.anatomyuniverse.musavacca.bar.balance;

//? if <1.21.6 {
/*import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
*///?}
//? if >=1.21.6 {
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
//?} else {
/*import net.neoforged.neoforge.common.util.INBTSerializable;
*///?}

@SuppressWarnings({ "unused", "CommentedOutCode", "NullableProblems" })
//? if >=1.21.6 {
public final class BalanceData implements ValueIOSerializable {
//?} else {
/*public final class BalanceData implements INBTSerializable<CompoundTag> {
*///?}
    private int balance = 0;

    @Override
    //? if >=1.21.6 {
    public void serialize(ValueOutput output) {
    //?} else {
    /*public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag output = new CompoundTag();
    *///?}
        output.putInt("balance", this.balance);
        //? if <1.21.6 {
        /*return output;
        *///?}
    }

    @Override
    //? if >=1.21.6 {
    public void deserialize(ValueInput input) {
    //?} else {
    /*public void deserializeNBT(HolderLookup.Provider provider, CompoundTag input) {
    *///?}
        //? if >=1.21.5 {
        this.balance = Math.max(0, input.getIntOr("balance", 0));
        //?} else {
        /*this.balance = Math.max(0, input.contains("balance") ? input.getInt("balance") : 0);
        *///?}
    }

    public int getBalance() {
        return this.balance;
    }

    public void setBalance(int balance) {
        this.balance = Math.max(0, balance);
    }

    public boolean isVisible() {
        return this.balance > 0;
    }
}
