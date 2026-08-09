package space.anatomyuniverse.musavacca.entity.mob.bananacow;

import net.minecraft.core.Holder;

//? if <1.21.6
//import net.minecraft.nbt.CompoundTag;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.EatBlockGoal;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

//? if >=1.21.6
import net.minecraft.world.level.storage.ValueInput;

//? if >=1.21.6
import net.minecraft.world.level.storage.ValueOutput;

import org.jetbrains.annotations.NotNull;
import space.anatomyuniverse.musavacca.item.ModItems;

public class BananaCow extends Cow {

    public static final int PEEL_STAGE_DEFAULT = 0;
    public static final int PEEL_STAGE_SHEARED = 1;
    public static final int PEEL_STAGE_PEELD = 2;

    public static final int MAX_VISIBLE_EATEN_BITES = 2;
    public static final int MAX_EATEN_BITES_BEFORE_DEATH = 3;

    /*
     * Public because ModMobLootProvider uses this exact NBT key.
     */
    public static final String TAG_PEEL_STAGE =
            "BananaCowPeelStage";

    private static final String TAG_EATEN_BITES =
            "BananaCowEatenBites";

    private static final float EAT_DAMAGE = 2.0F;
    private static final float FINAL_EAT_DAMAGE = 1000.0F;

    private static final int GOLDEN_CARROT_NUTRITION = 6;

    private static final float GOLDEN_CARROT_SATURATION_MODIFIER =
            1.2F;

    private static final byte EAT_ANIMATION_EVENT = 10;
    private static final int EAT_ANIMATION_TICKS = 40;

    private static final float EAT_HEAD_DOWN_ANGLE =
            Mth.PI / 5.0F;

    private static final float EAT_HEAD_NOD_AMOUNT =
            0.21991149F;

    private static final float EAT_HEAD_NOD_SPEED =
            28.7F;

    private static final EntityDataAccessor<Integer> DATA_PEEL_STAGE =
            SynchedEntityData.defineId(
                    BananaCow.class,
                    EntityDataSerializers.INT
            );

    private static final EntityDataAccessor<Integer> DATA_EATEN_BITES =
            SynchedEntityData.defineId(
                    BananaCow.class,
                    EntityDataSerializers.INT
            );

    private EatBlockGoal eatBlockGoal;
    private int eatAnimationTick;

    public BananaCow(
            EntityType<? extends BananaCow> type,
            Level level
    ) {
        super(type, level);
    }

    public static @NotNull AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder =
                Cow.createLivingAttributes()
                        .add(Attributes.MAX_HEALTH, 10.0D)
                        .add(Attributes.MOVEMENT_SPEED, 0.25D)
                        .add(Attributes.FOLLOW_RANGE, 16.0D);

        //? if >=1.21.2 {
        builder.add(Attributes.TEMPT_RANGE, 10.0D);
        //?}

        return builder;
    }

    @Override
    protected void defineSynchedData(
            @NotNull SynchedEntityData.Builder builder
    ) {
        super.defineSynchedData(builder);

        builder.define(
                DATA_PEEL_STAGE,
                PEEL_STAGE_DEFAULT
        );

        builder.define(
                DATA_EATEN_BITES,
                0
        );
    }

    public int getPeelStage() {
        return this.entityData.get(DATA_PEEL_STAGE);
    }

    public void setPeelStage(int peelStage) {
        this.entityData.set(
                DATA_PEEL_STAGE,
                clampInt(
                        peelStage,
                        PEEL_STAGE_DEFAULT,
                        PEEL_STAGE_PEELD
                )
        );
    }

    public int getEatenBites() {
        return this.entityData.get(DATA_EATEN_BITES);
    }

    public void setEatenBites(int eatenBites) {
        this.entityData.set(
                DATA_EATEN_BITES,
                clampInt(
                        eatenBites,
                        0,
                        MAX_EATEN_BITES_BEFORE_DEATH
                )
        );
    }

    /*
     * Both stage 1 and stage 2 count as sheared.
     */
    public boolean isBananaSheared() {
        return this.getPeelStage() >= PEEL_STAGE_SHEARED;
    }

    public boolean canBananaCowBeShearedAgain() {
        return this.isAlive()
                && this.getPeelStage() < PEEL_STAGE_PEELD;
    }

    public boolean canBananaCowBeEaten() {
        return this.isAlive()
                && this.isBananaSheared()
                && this.getEatenBites()
                < MAX_EATEN_BITES_BEFORE_DEATH;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        this.eatBlockGoal = new EatBlockGoal(this) {
            @Override
            public boolean canUse() {
                return BananaCow.this.isBananaSheared()
                        && super.canUse();
            }
        };

        this.goalSelector.addGoal(
                5,
                this.eatBlockGoal
        );
    }

    //? if >=1.21.2 {
    @Override
    protected void customServerAiStep(
            @NotNull ServerLevel level
    ) {
        this.eatAnimationTick =
                this.eatBlockGoal.getEatAnimationTick();

        super.customServerAiStep(level);
    }
    //?} else {
    /*@Override
    protected void customServerAiStep() {
        this.eatAnimationTick =
                this.eatBlockGoal.getEatAnimationTick();

        super.customServerAiStep();
    }
    *///?}

    @Override
    public void aiStep() {
        if (this.level().isClientSide) {
            this.eatAnimationTick = Math.max(
                    0,
                    this.eatAnimationTick - 1
            );
        }

        super.aiStep();
    }

    @Override
    public void handleEntityEvent(byte eventId) {
        if (eventId == EAT_ANIMATION_EVENT) {
            this.eatAnimationTick = EAT_ANIMATION_TICKS;
            return;
        }

        super.handleEntityEvent(eventId);
    }

    public float getHeadEatPositionScale(float partialTick) {
        if (this.eatAnimationTick <= 0) {
            return 0.0F;
        }

        if (this.eatAnimationTick >= 4
                && this.eatAnimationTick <= 36) {
            return 1.0F;
        }

        if (this.eatAnimationTick < 4) {
            return (
                    (float) this.eatAnimationTick - partialTick
            ) / 4.0F;
        }

        return -(
                (float) (
                        this.eatAnimationTick
                                - EAT_ANIMATION_TICKS
                ) - partialTick
        ) / 4.0F;
    }

    public float getHeadEatAngleScale(float partialTick) {
        if (this.eatAnimationTick > 4
                && this.eatAnimationTick <= 36) {
            float animationProgress =
                    (
                            (float) (
                                    this.eatAnimationTick - 4
                            ) - partialTick
                    ) / 32.0F;

            return EAT_HEAD_DOWN_ANGLE
                    + EAT_HEAD_NOD_AMOUNT
                    * Mth.sin(
                    animationProgress
                            * EAT_HEAD_NOD_SPEED
            );
        }

        return this.eatAnimationTick > 0
                ? EAT_HEAD_DOWN_ANGLE
                : 0.0F;
    }

    @Override
    public void ate() {
        super.ate();

        if (this.getEatenBites() > 0) {
            this.setEatenBites(
                    this.getEatenBites() - 1
            );
            return;
        }

        if (this.getPeelStage() > PEEL_STAGE_DEFAULT) {
            this.setPeelStage(
                    this.getPeelStage() - 1
            );
        }
    }
    @Override
    public @NotNull InteractionResult mobInteract(
            @NotNull Player player,
            @NotNull InteractionHand hand
    ) {
        ItemStack stack = player.getItemInHand(hand);

        /*
         * Milk using an empty bucket.
         */
        if (stack.is(Items.BUCKET) && !this.isBaby()) {
            player.playSound(
                    SoundEvents.COW_MILK,
                    1.0F,
                    1.0F
            );

            ItemStack bananaMilk =
                    ItemUtils.createFilledResult(
                            stack,
                            player,
                            ModItems.BANANA_MILK_BUCKET
                                    .get()
                                    .getDefaultInstance()
                    );

            player.setItemInHand(
                    hand,
                    bananaMilk
            );

            return InteractionResult.SUCCESS;
        }

        /*
         * Shift-right-click to eat the exposed banana cow.
         */
        if (player.isShiftKeyDown()
                && this.canBananaCowBeEaten()) {
            if (!this.level().isClientSide
                    && this.level()
                    instanceof ServerLevel serverLevel) {
                this.eatBananaCow(
                        serverLevel,
                        player
                );
            }

            return InteractionResult.SUCCESS;
        }

        /*
         * Shear the banana cow.
         */
        if (stack.is(Items.SHEARS)
                && this.canBananaCowBeShearedAgain()) {
            if (!this.level().isClientSide
                    && this.level()
                    instanceof ServerLevel serverLevel) {
                this.shearBananaCow(
                        serverLevel,
                        player,
                        hand,
                        stack
                );
            }

            return InteractionResult.SUCCESS;
        }

        return super.mobInteract(player, hand);
    }

    private void shearBananaCow(
            @NotNull ServerLevel level,
            @NotNull Player player,
            @NotNull InteractionHand hand,
            @NotNull ItemStack shears
    ) {
        int nextStage = clampInt(
                this.getPeelStage() + 1,
                PEEL_STAGE_DEFAULT,
                PEEL_STAGE_PEELD
        );

        this.setPeelStage(nextStage);
        this.dropBananaPellis(level);

        this.playBananaCowSound(
                level,
                SoundEvents.SHEEP_SHEAR,
                1.0F,
                1.0F
        );

        player.swing(hand, true);

        if (!player.getAbilities().instabuild) {
            shears.hurtAndBreak(
                    1,
                    player,
                    slotForHand(hand)
            );
        }
    }

    private void dropBananaPellis(
            @NotNull ServerLevel level
    ) {
        Containers.dropItemStack(
                level,
                this.getX(),
                this.getY() + 0.5D,
                this.getZ(),
                new ItemStack(
                        ModItems.BANANA_PELLIS.get()
                )
        );
    }

    private void eatBananaCow(
            @NotNull ServerLevel level,
            @NotNull Player player
    ) {
        int nextBites = clampInt(
                this.getEatenBites() + 1,
                0,
                MAX_EATEN_BITES_BEFORE_DEATH
        );

        this.setEatenBites(nextBites);

        this.feedPlayerLikeGoldenCarrot(player);
        this.playBananaCowEatingFinishSounds(level);

        float damage =
                nextBites >= MAX_EATEN_BITES_BEFORE_DEATH
                        ? FINAL_EAT_DAMAGE
                        : EAT_DAMAGE;

        this.hurtBananaCow(
                level,
                this.damageSources().playerAttack(player),
                damage
        );
    }

    private void feedPlayerLikeGoldenCarrot(
            @NotNull Player player
    ) {
        player.getFoodData().eat(
                GOLDEN_CARROT_NUTRITION,
                GOLDEN_CARROT_SATURATION_MODIFIER
        );
    }

    private void playBananaCowEatingFinishSounds(
            @NotNull ServerLevel level
    ) {
        this.playBananaCowSound(
                level,
                SoundEvents.GENERIC_EAT,
                1.0F,
                1.0F
        );

        this.playBananaCowSound(
                level,
                SoundEvents.PLAYER_BURP,
                0.55F,
                1.15F
        );
    }

    private void playBananaCowSound(
            @NotNull ServerLevel level,
            @NotNull SoundEvent sound,
            float volume,
            float pitch
    ) {
        level.playSound(
                null,
                this.getX(),
                this.getY(),
                this.getZ(),
                sound,
                SoundSource.NEUTRAL,
                volume,
                pitch
        );
    }

    private void playBananaCowSound(
            @NotNull ServerLevel level,
            @NotNull Holder<SoundEvent> sound,
            float volume,
            float pitch
    ) {
        level.playSeededSound(
                null,
                this.getX(),
                this.getY(),
                this.getZ(),
                sound,
                SoundSource.NEUTRAL,
                volume,
                pitch,
                this.getRandom().nextLong()
        );
    }

    private void hurtBananaCow(
            @NotNull ServerLevel level,
            @NotNull DamageSource source,
            float damage
    ) {
        //? if >=1.21.6 {
        this.hurtServer(
                level,
                source,
                damage
        );
        //?} else {
        /*this.hurt(
                source,
                damage
        );
        *///?}
    }

    private static EquipmentSlot slotForHand(
            @NotNull InteractionHand hand
    ) {
        return hand == InteractionHand.MAIN_HAND
                ? EquipmentSlot.MAINHAND
                : EquipmentSlot.OFFHAND;
    }

    private static int clampInt(
            int value,
            int min,
            int max
    ) {
        return Math.max(
                min,
                Math.min(max, value)
        );
    }

    //? if >=1.21.6 {
    @Override
    protected void addAdditionalSaveData(
            @NotNull ValueOutput output
    ) {
        super.addAdditionalSaveData(output);

        output.putInt(
                TAG_PEEL_STAGE,
                this.getPeelStage()
        );

        output.putInt(
                TAG_EATEN_BITES,
                this.getEatenBites()
        );
    }

    @Override
    protected void readAdditionalSaveData(
            @NotNull ValueInput input
    ) {
        super.readAdditionalSaveData(input);

        this.setPeelStage(
                input.getIntOr(
                        TAG_PEEL_STAGE,
                        PEEL_STAGE_DEFAULT
                )
        );

        this.setEatenBites(
                input.getIntOr(
                        TAG_EATEN_BITES,
                        0
                )
        );
    }
    //?} else {
    /*@Override
    public void addAdditionalSaveData(
            @NotNull CompoundTag tag
    ) {
        super.addAdditionalSaveData(tag);

        tag.putInt(
                TAG_PEEL_STAGE,
                this.getPeelStage()
        );

        tag.putInt(
                TAG_EATEN_BITES,
                this.getEatenBites()
        );
    }

    @Override
    public void readAdditionalSaveData(
            @NotNull CompoundTag tag
    ) {
        super.readAdditionalSaveData(tag);

        this.setPeelStage(
                getIntOr(
                        tag,
                        TAG_PEEL_STAGE,
                        PEEL_STAGE_DEFAULT
                )
        );

        this.setEatenBites(
                getIntOr(
                        tag,
                        TAG_EATEN_BITES,
                        0
                )
        );
    }
    *///?}

    //? if <1.21.6 {
    /*private static int getIntOr(
            CompoundTag tag,
            String key,
            int fallback
    ) {
        //? if >=1.21.5
        return tag.getIntOr(key, fallback);

        //? if <1.21.5
        //return tag.contains(key) ? tag.getInt(key) : fallback;
    }
    *///?}

    @Override
    public boolean isFood(ItemStack stack) {
        return false;
    }

    @Override
    public boolean isBaby() {
        return false;
    }

    @Override
    public void setBaby(boolean isBaby) {
        // Banana cows are always adults.
    }
}