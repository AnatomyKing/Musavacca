package space.anatomyuniverse.musavacca.bar.hunger;

//? if <1.21.5 {
/*import net.minecraft.nbt.Tag;
*///?}
//? if <1.21.6 {
/*import net.minecraft.core.HolderLookup;
*///?}
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
//? if >=1.21.6 {
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
//?} else {
/*import net.neoforged.neoforge.common.util.INBTSerializable;
*///?}

@SuppressWarnings({ "unused", "CommentedOutCode", "NullableProblems" })
//? if >=1.21.6 {
public final class BonusHungerData implements ValueIOSerializable {
//?} else {
/*public final class BonusHungerData implements INBTSerializable<CompoundTag> {
*///?}
    public static final int MAX_FOOD = 20;
    public static final int FOOD_PER_PIECE = 5;
    private int capacity = MAX_FOOD;
    private int food = MAX_FOOD;
    private float saturation = MAX_FOOD;
    private final int[] storedFood = new int[MAX_FOOD / FOOD_PER_PIECE];
    private final float[] storedSaturation = new float[MAX_FOOD / FOOD_PER_PIECE];
    private float exhaustion = 0.0F;
    private int tickTimer = 0;
    private int lastBaseFood = MAX_FOOD;
    private float lastBaseSaturation = 0.0F;
    private boolean baseSnapshotReady = false;
    private boolean forceBonusEating = false;
    private boolean hasPendingFinishedFood = false;
    private int pendingFoodNutrition = 0;
    private float pendingFoodSaturationModifier = 0.0F;
    private boolean pendingForceBonusFood = false;
    private boolean pendingCanAlwaysEat = false;
    private int lastSyncedFood = Integer.MIN_VALUE;
    private float lastSyncedSaturation = Float.NaN;
    private boolean lastSyncedActive = false;
    private int lastSyncedCapacity = -1;

    @Override
    //? if >=1.21.6 {
    public void serialize(ValueOutput output) {
    //?} else {
    /*public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag output = new CompoundTag();
    *///?}
        output.putInt("capacity", this.capacity);
        output.putInt("food", this.food);
        output.putFloat("saturation", this.saturation);
        output.putFloat("exhaustion", this.exhaustion);
        output.putInt("tick_timer", this.tickTimer);
        for (int i = 0; i < this.storedFood.length; i++) {
            output.putInt("stored_food_" + i, this.storedFood[i]);
            output.putFloat("stored_saturation_" + i, this.storedSaturation[i]);
        }
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
        this.capacity = Mth.clamp(input.getIntOr("capacity", MAX_FOOD), 0, MAX_FOOD) / FOOD_PER_PIECE * FOOD_PER_PIECE;
        this.food = Mth.clamp(input.getIntOr("food", MAX_FOOD), 0, this.capacity);
        this.saturation = Mth.clamp(input.getFloatOr("saturation", (float) MAX_FOOD), 0.0F, this.food);
        this.exhaustion = Mth.clamp(input.getFloatOr("exhaustion", 0.0F), 0.0F, 40.0F);
        this.tickTimer = Math.max(0, input.getIntOr("tick_timer", 0));
        for (int i = 0; i < this.storedFood.length; i++) {
            this.storedFood[i] = i < this.capacity / FOOD_PER_PIECE ? 0 : Mth.clamp(input.getIntOr("stored_food_" + i, 0), 0, FOOD_PER_PIECE);
            this.storedSaturation[i] = Mth.clamp(input.getFloatOr("stored_saturation_" + i, 0.0F), 0.0F, this.storedFood[i]);
        }
        //?} else {
        /*this.capacity = Mth.clamp(input.contains("capacity", Tag.TAG_ANY_NUMERIC) ? input.getInt("capacity") : MAX_FOOD, 0, MAX_FOOD) / FOOD_PER_PIECE * FOOD_PER_PIECE;
        this.food = Mth.clamp(input.contains("food", Tag.TAG_ANY_NUMERIC) ? input.getInt("food") : MAX_FOOD, 0, this.capacity);
        this.saturation = Mth.clamp(input.contains("saturation", Tag.TAG_ANY_NUMERIC) ? input.getFloat("saturation") : (float) MAX_FOOD, 0.0F, this.food);
        this.exhaustion = Mth.clamp(input.contains("exhaustion", Tag.TAG_ANY_NUMERIC) ? input.getFloat("exhaustion") : 0.0F, 0.0F, 40.0F);
        this.tickTimer = Math.max(0, input.contains("tick_timer", Tag.TAG_ANY_NUMERIC) ? input.getInt("tick_timer") : 0);
        for (int i = 0; i < this.storedFood.length; i++) {
            this.storedFood[i] = i < this.capacity / FOOD_PER_PIECE ? 0 : Mth.clamp(input.getInt("stored_food_" + i), 0, FOOD_PER_PIECE);
            this.storedSaturation[i] = Mth.clamp(input.getFloat("stored_saturation_" + i), 0.0F, this.storedFood[i]);
        }
        *///?}
        resetTransientState();
        clampToValidState();
    }

    private void resetTransientState() {
        this.lastBaseFood = MAX_FOOD;
        this.lastBaseSaturation = 0.0F;
        this.baseSnapshotReady = false;
        this.forceBonusEating = false;
        clearPendingFinishedFood();
        this.lastSyncedFood = Integer.MIN_VALUE;
        this.lastSyncedSaturation = Float.NaN;
        this.lastSyncedActive = false;
        this.lastSyncedCapacity = -1;
    }

    public int getCapacity() {
        return this.capacity;
    }

    public boolean setCapacity(int capacity) {
        capacity = Mth.clamp(capacity, 0, MAX_FOOD) / FOOD_PER_PIECE * FOOD_PER_PIECE;
        if (this.capacity == capacity) return false;
        while (this.capacity > capacity) {
            this.capacity -= FOOD_PER_PIECE;
            int index = this.capacity / FOOD_PER_PIECE;
            int remainingFood = Math.min(this.food, this.capacity);
            float remainingSaturation = Math.min(this.saturation, remainingFood);
            this.storedFood[index] = this.food - remainingFood;
            this.storedSaturation[index] = this.saturation - remainingSaturation;
            this.food = remainingFood;
            this.saturation = remainingSaturation;
        }
        while (this.capacity < capacity) {
            int index = this.capacity / FOOD_PER_PIECE;
            this.food += this.storedFood[index];
            this.saturation += this.storedSaturation[index];
            this.storedFood[index] = 0;
            this.storedSaturation[index] = 0.0F;
            this.capacity += FOOD_PER_PIECE;
        }
        resetTickTimer();
        clampToValidState();
        return true;
    }

    public int getFood() {
        return this.food;
    }

    public float getSaturation() {
        return this.saturation;
    }

    public int getTickTimer() {
        return this.tickTimer;
    }

    public int getLastBaseFood() {
        return this.lastBaseFood;
    }

    public float getLastBaseSaturation() {
        return this.lastBaseSaturation;
    }

    public boolean isBaseSnapshotReady() {
        return this.baseSnapshotReady;
    }

    public boolean isForceBonusEating() {
        return this.forceBonusEating;
    }

    public void setForceBonusEating(boolean forceBonusEating) {
        this.forceBonusEating = forceBonusEating;
    }

    public boolean hasPendingFinishedFood() {
        return this.hasPendingFinishedFood;
    }

    public int getPendingFoodNutrition() {
        return this.pendingFoodNutrition;
    }

    public float getPendingFoodSaturationModifier() {
        return this.pendingFoodSaturationModifier;
    }

    public boolean isPendingForceBonusFood() {
        return this.pendingForceBonusFood;
    }

    public boolean isPendingCanAlwaysEat() {
        return this.pendingCanAlwaysEat;
    }

    public void queueFinishedFood(int nutrition, float saturationModifier, boolean forceBonusFood, boolean canAlwaysEat) {
        if (nutrition <= 0) return;
        this.hasPendingFinishedFood = true;
        this.pendingFoodNutrition = nutrition;
        this.pendingFoodSaturationModifier = saturationModifier;
        this.pendingForceBonusFood = forceBonusFood;
        this.pendingCanAlwaysEat = canAlwaysEat;
    }

    public void clearPendingFinishedFood() {
        this.hasPendingFinishedFood = false;
        this.pendingFoodNutrition = 0;
        this.pendingFoodSaturationModifier = 0.0F;
        this.pendingForceBonusFood = false;
        this.pendingCanAlwaysEat = false;
    }

    public boolean needsFood() {
        return this.food < this.capacity;
    }

    public boolean needsSaturation() {
        return this.saturation < this.food;
    }

    public boolean hasUsableEnergy() {
        return this.food > 0 || this.saturation > 0.0F;
    }

    public void setSaturation(float saturation) {
        this.saturation = Mth.clamp(saturation, 0.0F, this.food);
    }

    public void resetTickTimer() {
        this.tickTimer = 0;
    }

    public void increaseTickTimer() {
        this.tickTimer++;
    }

    public void rememberBaseSnapshot(int baseFood, float baseSaturation) {
        this.lastBaseFood = Math.max(0, baseFood);
        this.lastBaseSaturation = Math.max(0.0F, baseSaturation);
        this.baseSnapshotReady = true;
    }

    public void eat(int nutrition, float saturationModifier) {
        if (nutrition <= 0) return;
        this.food = Mth.clamp(this.food + nutrition, 0, this.capacity);
        this.saturation = Mth.clamp(this.saturation + (float) nutrition * saturationModifier * 2.0F, 0.0F, this.food);
        clampToValidState();
    }

    public void addExhaustion(float amount, boolean canDrainFood) {
        if (amount <= 0.0F) return;
        this.exhaustion = Math.min(this.exhaustion + amount, 40.0F);
        while (this.exhaustion > 4.0F) {
            this.exhaustion -= 4.0F;
            if (this.saturation > 0.0F) {
                this.saturation = Math.max(this.saturation - 1.0F, 0.0F);
            } else if (canDrainFood && this.food > 0) {
                this.food = Math.max(this.food - 1, 0);
            }
        }
        clampToValidState();
    }

    public float drainSaturation(float requested) {
        if (requested <= 0.0F || this.saturation <= 0.0F) return 0.0F;
        float drained = Math.min(this.saturation, requested);
        this.saturation -= drained;
        clampToValidState();
        return drained;
    }

    public int drainFood(int requested) {
        if (requested <= 0 || this.food <= 0) return 0;
        int drained = Math.min(this.food, requested);
        this.food -= drained;
        clampToValidState();
        return drained;
    }

    public boolean shouldSync(boolean active) {
        return active != this.lastSyncedActive || this.capacity != this.lastSyncedCapacity || this.food != this.lastSyncedFood
                || Float.compare(this.saturation, this.lastSyncedSaturation) != 0;
    }

    public void markSynced(boolean active) {
        this.lastSyncedActive = active;
        this.lastSyncedCapacity = this.capacity;
        this.lastSyncedFood = this.food;
        this.lastSyncedSaturation = this.saturation;
    }

    private void clampToValidState() {
        this.food = Mth.clamp(this.food, 0, this.capacity);
        this.saturation = Mth.clamp(this.saturation, 0.0F, this.food);
        this.exhaustion = Mth.clamp(this.exhaustion, 0.0F, 40.0F);
        this.tickTimer = Math.max(0, this.tickTimer);
    }
}
