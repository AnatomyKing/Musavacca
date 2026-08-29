package space.anatomyuniverse.musavacca.bar.hunger;

//? if <1.21.6
//import net.minecraft.core.HolderLookup;
//? if <1.21.11
import net.minecraft.nbt.CompoundTag;

import net.minecraft.util.Mth;
//? if >=1.21.6
import net.minecraft.world.level.storage.ValueInput;
//? if >=1.21.6
import net.minecraft.world.level.storage.ValueOutput;
//? if <1.21.6
//import net.neoforged.neoforge.common.util.INBTSerializable;
//? if >=1.21.6
import net.neoforged.neoforge.common.util.ValueIOSerializable;

@SuppressWarnings({"unused", "CommentedOutCode", "NullableProblems"})
//? if >=1.21.6 {
public final class BonusHungerData implements ValueIOSerializable {
    //?} else
    //public final class BonusHungerData implements INBTSerializable<CompoundTag> {
    public static final int MAX_FOOD = 20;

    private static final float EXHAUSTION_STEP = 4.0F;
    private static final float MAX_STORED_EXHAUSTION = 40.0F;

    private int food = MAX_FOOD;
    private float saturation = MAX_FOOD;
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

    //? if >=1.21.6 {
    @Override
    public void serialize(ValueOutput output) {
        output.putInt("food", this.food);
        output.putFloat("saturation", this.saturation);
        output.putFloat("exhaustion", this.exhaustion);
        output.putInt("tick_timer", this.tickTimer);
    }

    @Override
    public void deserialize(ValueInput input) {
        this.food = Mth.clamp(input.getIntOr("food", MAX_FOOD), 0, MAX_FOOD);
        this.saturation = Mth.clamp(input.getFloatOr("saturation", (float) MAX_FOOD), 0.0F, this.food);
        this.exhaustion = Mth.clamp(input.getFloatOr("exhaustion", 0.0F), 0.0F, MAX_STORED_EXHAUSTION);
        this.tickTimer = Math.max(0, input.getIntOr("tick_timer", 0));

        resetTransientState();
        clampToValidState();
    }
    //?} else {
    /*@Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("food", this.food);
        tag.putFloat("saturation", this.saturation);
        tag.putFloat("exhaustion", this.exhaustion);
        tag.putInt("tick_timer", this.tickTimer);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
        this.food = Mth.clamp(readInt(tag, "food", MAX_FOOD), 0, MAX_FOOD);
        this.saturation = Mth.clamp(readFloat(tag, "saturation", (float) MAX_FOOD), 0.0F, this.food);
        this.exhaustion = Mth.clamp(readFloat(tag, "exhaustion", 0.0F), 0.0F, MAX_STORED_EXHAUSTION);
        this.tickTimer = Math.max(0, readInt(tag, "tick_timer", 0));

        resetTransientState();
        clampToValidState();
    }
    *///?}

    private static int readInt(CompoundTag tag, String key, int fallback) {
        //? if >=1.21.5
        return tag.getIntOr(key, fallback);
        //? if <1.21.5
        //return tag.contains(key) ? tag.getInt(key) : fallback;
    }

    private static float readFloat(CompoundTag tag, String key, float fallback) {
        //? if >=1.21.5
        return tag.getFloatOr(key, fallback);
        //? if <1.21.5
        //return tag.contains(key) ? tag.getFloat(key) : fallback;
    }

    private void resetTransientState() {
        this.lastBaseFood = MAX_FOOD;
        this.lastBaseSaturation = 0.0F;
        this.baseSnapshotReady = false;

        this.forceBonusEating = false;
        this.hasPendingFinishedFood = false;
        this.pendingFoodNutrition = 0;
        this.pendingFoodSaturationModifier = 0.0F;
        this.pendingForceBonusFood = false;
        this.pendingCanAlwaysEat = false;

        this.lastSyncedFood = Integer.MIN_VALUE;
        this.lastSyncedSaturation = Float.NaN;
        this.lastSyncedActive = false;
    }

    public int getFood() {
        return this.food;
    }

    public float getSaturation() {
        return this.saturation;
    }

    public float getExhaustion() {
        return this.exhaustion;
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
        if (nutrition <= 0) {
            return;
        }

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
        return this.food < MAX_FOOD;
    }

    public boolean needsSaturation() {
        return this.saturation < this.food;
    }

    public boolean hasUsableEnergy() {
        return this.food > 0 || this.saturation > 0.0F;
    }

    public void setFood(int food) {
        this.food = Mth.clamp(food, 0, MAX_FOOD);
        clampToValidState();
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
        if (nutrition <= 0) {
            return;
        }

        this.food = Mth.clamp(this.food + nutrition, 0, MAX_FOOD);
        this.saturation = Mth.clamp(
                this.saturation + (float) nutrition * saturationModifier * 2.0F,
                0.0F,
                this.food
        );

        clampToValidState();
    }

    public void addExhaustion(float amount, boolean canDrainFood) {
        if (amount <= 0.0F) {
            return;
        }

        this.exhaustion = Math.min(this.exhaustion + amount, MAX_STORED_EXHAUSTION);

        while (this.exhaustion > EXHAUSTION_STEP) {
            this.exhaustion -= EXHAUSTION_STEP;

            if (this.saturation > 0.0F) {
                this.saturation = Math.max(this.saturation - 1.0F, 0.0F);
            } else if (canDrainFood && this.food > 0) {
                this.food = Math.max(this.food - 1, 0);
            }
        }

        clampToValidState();
    }

    public float drainSaturation(float requested) {
        if (requested <= 0.0F || this.saturation <= 0.0F) {
            return 0.0F;
        }

        float drained = Math.min(this.saturation, requested);
        this.saturation -= drained;
        clampToValidState();
        return drained;
    }

    public int drainFood(int requested) {
        if (requested <= 0 || this.food <= 0) {
            return 0;
        }

        int drained = Math.min(this.food, requested);
        this.food -= drained;
        clampToValidState();
        return drained;
    }

    public boolean shouldSync(boolean active) {
        return active != this.lastSyncedActive
                || this.food != this.lastSyncedFood
                || Float.compare(this.saturation, this.lastSyncedSaturation) != 0;
    }

    public void markSynced(boolean active) {
        this.lastSyncedActive = active;
        this.lastSyncedFood = this.food;
        this.lastSyncedSaturation = this.saturation;
    }

    private void clampToValidState() {
        this.food = Mth.clamp(this.food, 0, MAX_FOOD);
        this.saturation = Mth.clamp(this.saturation, 0.0F, this.food);
        this.exhaustion = Mth.clamp(this.exhaustion, 0.0F, MAX_STORED_EXHAUSTION);
        this.tickTimer = Math.max(0, this.tickTimer);
    }
}
