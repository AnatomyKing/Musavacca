package space.anatomyuniverse.musavacca.bar.hunger;

import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import space.anatomyuniverse.musavacca.bar.ModAttachments;
import space.anatomyuniverse.musavacca.item.ModItems;

public final class BonusHungerEvents {
    private static final int VANILLA_FULL_FOOD = 20;
    private static final int VANILLA_NATURAL_REGEN_MIN_FOOD = 18;

    private static final int SATURATED_REGEN_TICKS = 10;
    private static final int UNSATURATED_REGEN_TICKS = 80;

    private static final float FAST_REGEN_SATURATION_SLICE = 6.0F;
    private static final float UNSATURATED_REGEN_EXHAUSTION = 6.0F;

    private static final float EPSILON = 0.0001F;

    private BonusHungerEvents() {
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            BonusHungerData data = player.getData(ModAttachments.BONUS_HUNGER);
            data.rememberBaseSnapshot(
                    player.getFoodData().getFoodLevel(),
                    player.getFoodData().getSaturationLevel()
            );
            sendSync(player, data, hasBonusHunger(player));
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            BonusHungerData data = player.getData(ModAttachments.BONUS_HUNGER);
            data.rememberBaseSnapshot(
                    player.getFoodData().getFoodLevel(),
                    player.getFoodData().getSaturationLevel()
            );
            sendSync(player, data, hasBonusHunger(player));
        }
    }

    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();

        if (!hasBonusHunger(player)) {
            return;
        }

        ItemStack stack = event.getItemStack();
        FoodProperties food = getFoodProperties(stack);

        if (food == null) {
            return;
        }

        if (player.getFoodData().needsFood()) {
            return;
        }

        if (food.canAlwaysEat()) {
            return;
        }

        if (player.level().isClientSide()) {
            if (!ClientBonusHungerData.needsFood()) {
                return;
            }

            player.startUsingItem(event.getHand());
            event.setCancellationResult(InteractionResult.CONSUME);
            event.setCanceled(true);
            return;
        }

        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        BonusHungerData data = serverPlayer.getData(ModAttachments.BONUS_HUNGER);

        if (!data.needsFood()) {
            return;
        }

        data.setForceBonusEating(true);
        serverPlayer.startUsingItem(event.getHand());

        event.setCancellationResult(InteractionResult.CONSUME);
        event.setCanceled(true);

        sendSync(serverPlayer, data, true);
    }

    @SubscribeEvent
    public static void onFinishUsingItem(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        if (!hasBonusHunger(player)) {
            return;
        }

        ItemStack stack = event.getItem();
        FoodProperties food = getFoodProperties(stack);

        if (food == null) {
            return;
        }

        BonusHungerData data = player.getData(ModAttachments.BONUS_HUNGER);

        data.queueFinishedFood(
                food.nutrition(),
                food.saturation(),
                data.isForceBonusEating(),
                food.canAlwaysEat()
        );

        data.setForceBonusEating(false);
        player.setData(ModAttachments.BONUS_HUNGER, data);
    }

    @SubscribeEvent
    public static void onStopUsingItem(LivingEntityUseItemEvent.Stop event) {
        if (event.getEntity() instanceof Player player) {
            BonusHungerData data = player.getData(ModAttachments.BONUS_HUNGER);
            data.setForceBonusEating(false);
        }
    }

    @SubscribeEvent
    public static void onPlayerTickPost(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        BonusHungerData data = player.getData(ModAttachments.BONUS_HUNGER);
        boolean active = hasBonusHunger(player);

        if (!data.isBaseSnapshotReady()) {
            data.rememberBaseSnapshot(
                    player.getFoodData().getFoodLevel(),
                    player.getFoodData().getSaturationLevel()
            );
            sendSync(player, data, active);
            return;
        }

        boolean changed = false;

        if (active) {
            changed |= applyPendingFinishedFood(player, data);
            changed |= absorbVanillaFoodDrainIntoBonus(player, data);
            changed |= tickBonusNaturalRegeneration(player, data);
        } else {
            data.resetTickTimer();
            data.setForceBonusEating(false);
            data.clearPendingFinishedFood();
        }

        data.rememberBaseSnapshot(
                player.getFoodData().getFoodLevel(),
                player.getFoodData().getSaturationLevel()
        );

        if (changed) {
            player.setData(ModAttachments.BONUS_HUNGER, data);
        }

        if (changed || data.shouldSync(active) || player.tickCount % 20 == 0) {
            sendSync(player, data, active);
        }
    }

    private static FoodProperties getFoodProperties(ItemStack stack) {
        //? if >=1.21.2 {
        if (!stack.has(DataComponents.CONSUMABLE)) {
            return null;
        }
        //?} else {
        /*if (!stack.has(DataComponents.FOOD)) {
            return null;
        }
        *///?}

        return stack.get(DataComponents.FOOD);
    }

    private static boolean applyPendingFinishedFood(ServerPlayer player, BonusHungerData data) {
        if (!data.hasPendingFinishedFood()) {
            return false;
        }

        int nutrition = data.getPendingFoodNutrition();
        float saturationModifier = data.getPendingFoodSaturationModifier();
        boolean forceBonusFood = data.isPendingForceBonusFood();
        boolean canAlwaysEat = data.isPendingCanAlwaysEat();

        data.clearPendingFinishedFood();

        if (nutrition <= 0) {
            return true;
        }

        int nutritionForBonus = nutrition;

        if (!forceBonusFood) {
            int vanillaFoodBeforeEating = data.getLastBaseFood();
            int vanillaFoodAfterEating = player.getFoodData().getFoodLevel();
            int vanillaFoodGain = Math.max(0, vanillaFoodAfterEating - vanillaFoodBeforeEating);

            nutritionForBonus = Math.max(0, nutrition - vanillaFoodGain);
        }

        if (nutritionForBonus <= 0) {
            return true;
        }

        if (!data.needsFood() && !data.needsSaturation() && !canAlwaysEat) {
            return true;
        }

        if (!data.needsFood() && !canAlwaysEat && player.getFoodData().needsFood()) {
            return true;
        }

        data.eat(nutritionForBonus, saturationModifier);
        return true;
    }

    private static boolean absorbVanillaFoodDrainIntoBonus(ServerPlayer player, BonusHungerData data) {
        FoodData vanillaFood = player.getFoodData();

        int currentBaseFood = vanillaFood.getFoodLevel();
        float currentBaseSaturation = vanillaFood.getSaturationLevel();

        int previousBaseFood = data.getLastBaseFood();
        float previousBaseSaturation = data.getLastBaseSaturation();

        boolean changed = false;

        if (currentBaseSaturation + EPSILON < previousBaseSaturation && data.hasUsableEnergy()) {
            float missingSaturation = previousBaseSaturation - currentBaseSaturation;
            float restoredSaturation = data.drainSaturation(missingSaturation);

            if (restoredSaturation > 0.0F) {
                vanillaFood.setSaturation(Math.min(previousBaseSaturation, currentBaseSaturation + restoredSaturation));
                changed = true;
            }
        }

        currentBaseFood = vanillaFood.getFoodLevel();

        if (currentBaseFood < previousBaseFood && data.hasUsableEnergy()) {
            int missingFood = previousBaseFood - currentBaseFood;
            int restoredFood = data.drainFood(missingFood);

            if (restoredFood > 0) {
                vanillaFood.setFoodLevel(Math.min(previousBaseFood, currentBaseFood + restoredFood));
                vanillaFood.setSaturation(Math.min(vanillaFood.getSaturationLevel(), vanillaFood.getFoodLevel()));
                changed = true;
            }
        }

        return changed;
    }

    private static boolean tickBonusNaturalRegeneration(ServerPlayer player, BonusHungerData data) {
        if (!player.getServer().getGameRules().getBoolean(GameRules.RULE_NATURAL_REGENERATION)) {
            data.resetTickTimer();
            return false;
        }

        if (player.getHealth() >= player.getMaxHealth()) {
            data.resetTickTimer();
            return false;
        }

        boolean canDrainFood = player.level().getDifficulty() != Difficulty.PEACEFUL;

        if (data.getFood() >= VANILLA_FULL_FOOD && data.getSaturation() > 0.0F) {
            data.increaseTickTimer();

            if (data.getTickTimer() >= SATURATED_REGEN_TICKS) {
                float saturationSlice = Math.min(data.getSaturation(), FAST_REGEN_SATURATION_SLICE);
                float healAmount = saturationSlice / FAST_REGEN_SATURATION_SLICE;

                if (healAmount > 0.0F) {
                    player.heal(healAmount);
                    data.addExhaustion(saturationSlice, canDrainFood);
                }

                data.resetTickTimer();
                return true;
            }

            return false;
        }

        if (data.getFood() >= VANILLA_NATURAL_REGEN_MIN_FOOD) {
            data.increaseTickTimer();

            if (data.getTickTimer() >= UNSATURATED_REGEN_TICKS) {
                player.heal(1.0F);
                data.addExhaustion(UNSATURATED_REGEN_EXHAUSTION, canDrainFood);
                data.resetTickTimer();
                return true;
            }

            return false;
        }

        data.resetTickTimer();
        return false;
    }

    private static boolean hasBonusHunger(Player player) {
        return player.getItemBySlot(EquipmentSlot.HEAD).is(ModItems.POTASSIUM_HELMET)
                && player.getItemBySlot(EquipmentSlot.CHEST).is(ModItems.POTASSIUM_CHESTPLATE)
                && player.getItemBySlot(EquipmentSlot.LEGS).is(ModItems.POTASSIUM_LEGGINGS)
                && player.getItemBySlot(EquipmentSlot.FEET).is(ModItems.POTASSIUM_BOOTS);
    }

    private static void sendSync(ServerPlayer player, BonusHungerData data, boolean active) {
        PacketDistributor.sendToPlayer(
                player,
                new BonusHungerSyncPayload(data.getFood(), data.getSaturation(), active)
        );
        data.markSynced(active);
    }
}


