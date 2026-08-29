package space.anatomyuniverse.musavacca.item.custom.potassium;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
//? if <1.21.6
//import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
//? if >=1.21.2
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

public final class PotassiumItemBehavior {
    private static final float EAT_DURABILITY_DAMAGE_PERCENT = 0.02F;

    private PotassiumItemBehavior() {
    }

    public static boolean isLookingAtBlock(Player player) {
        HitResult hitResult = player.pick(player.blockInteractionRange(), 0.0F, false);
        return hitResult.getType() == HitResult.Type.BLOCK;
    }

    public static boolean canStartEating(ItemStack stack, Player player) {
        if (player.getAbilities().instabuild) {
            return true;
        }

        if (!stack.isDamageableItem()) {
            return true;
        }

        int remainingDurability = stack.getMaxDamage() - stack.getDamageValue();
        int eatDurabilityDamage = getEatDurabilityDamage(stack);

        return remainingDurability > eatDurabilityDamage;
    }

    public static ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        FoodProperties food = stack.get(DataComponents.FOOD);
        //? if <1.21.2 {
        /*if (!level.isClientSide() && food != null) {
            entity.eat(level, stack.copyWithCount(1), food);

            if (!(entity instanceof Player player) || !player.getAbilities().instabuild) {
                InteractionHand hand = entity.getUsedItemHand();
                stack.hurtAndBreak(getEatDurabilityDamage(stack), entity, slotForHand(hand));
            }
        }
        *///?} else {
        Consumable consumable = stack.get(DataComponents.CONSUMABLE);

        if (!level.isClientSide() && food != null && consumable != null) {
            food.onConsume(level, entity, stack, consumable);

            if (!(entity instanceof Player player) || !player.getAbilities().instabuild) {
                InteractionHand hand = entity.getUsedItemHand();
                int eatDurabilityDamage = getEatDurabilityDamage(stack);

                //? if >=1.21.6 {
                stack.hurtAndBreak(eatDurabilityDamage, entity, hand);
                //?} else {
                /*stack.hurtAndBreak(eatDurabilityDamage, entity, slotForHand(hand));
                 *///?}
            }
        }
        //?}

        return stack;
    }

    private static int getEatDurabilityDamage(ItemStack stack) {
        if (!stack.isDamageableItem()) {
            return 0;
        }

        return Math.max(1, (int) Math.ceil(stack.getMaxDamage() * EAT_DURABILITY_DAMAGE_PERCENT));
    }

    //? if <1.21.6 {
    /*private static EquipmentSlot slotForHand(InteractionHand hand) {
        return hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
    }
    *///?}
}
