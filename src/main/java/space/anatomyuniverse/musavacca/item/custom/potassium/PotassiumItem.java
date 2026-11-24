package space.anatomyuniverse.musavacca.item.custom.potassium;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
//? if >=1.21.2 {
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.component.Consumable;
//?} else {
/*import net.minecraft.world.InteractionResultHolder;
*///?}
//? if <1.21.6 {
/*import net.minecraft.world.entity.EquipmentSlot;
*///?}
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

public class PotassiumItem extends Item {
    public PotassiumItem(Properties properties) {
        super(properties);
    }

    @Override
    //? if >=1.21.2 {
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
    //?} else {
    /*public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
    *///?}
        return useTool(level, player, hand);
    }

    //? if >=1.21.2 {
    static InteractionResult useTool(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.isSpectator()) return InteractionResult.PASS;
        if (player.getCooldowns().isOnCooldown(stack)) return InteractionResult.FAIL;
        if (player.isShiftKeyDown()) return startEating(player, hand);
        return PotassiumThrow.throwTool(player, hand, stack) ? InteractionResult.SUCCESS : InteractionResult.FAIL;
    }

    static InteractionResult startEating(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (isLookingAtBlock(player)) return InteractionResult.PASS;
        if (!canStartEating(stack, player)) return InteractionResult.FAIL;
        Consumable consumable = stack.get(DataComponents.CONSUMABLE);
        return consumable == null ? InteractionResult.PASS : consumable.startConsuming(player, stack, hand);
    }
    //?} else {
    /*static InteractionResultHolder<ItemStack> useTool(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.isSpectator()) return InteractionResultHolder.pass(stack);
        if (player.getCooldowns().isOnCooldown(stack.getItem())) return InteractionResultHolder.fail(stack);
        if (player.isShiftKeyDown()) return startEating(player, hand);
        if (!PotassiumThrow.throwTool(player, hand, stack)) return InteractionResultHolder.fail(stack);
        return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide());
    }

    static InteractionResultHolder<ItemStack> startEating(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (isLookingAtBlock(player)) return InteractionResultHolder.pass(stack);
        if (!canStartEating(stack, player)) return InteractionResultHolder.fail(stack);
        FoodProperties food = stack.get(DataComponents.FOOD);
        if (food == null) return InteractionResultHolder.pass(stack);
        if (!player.canEat(food.canAlwaysEat())) return InteractionResultHolder.fail(stack);
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }
    *///?}

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        return finishEating(stack, level, entity);
    }

    private static boolean isLookingAtBlock(Player player) {
        return player.pick(player.blockInteractionRange(), 0.0F, false).getType() == HitResult.Type.BLOCK;
    }

    private static boolean canStartEating(ItemStack stack, Player player) {
        return player.getAbilities().instabuild || !stack.isDamageableItem() || stack.getMaxDamage() - stack.getDamageValue() > getEatDurabilityDamage(stack);
    }

    static ItemStack finishEating(ItemStack stack, Level level, LivingEntity entity) {
        FoodProperties food = stack.get(DataComponents.FOOD);
        //? if >=1.21.2 {
        Consumable consumable = stack.get(DataComponents.CONSUMABLE);
        if (!level.isClientSide() && food != null && consumable != null) {
        //?} else {
        /*if (!level.isClientSide() && food != null) {
        *///?}
            //? if >=1.21.2 {
            food.onConsume(level, entity, stack, consumable);
            //?} else {
            /*entity.eat(level, stack.copyWithCount(1), food);
            *///?}
            if (!(entity instanceof Player player) || !player.getAbilities().instabuild) {
                InteractionHand hand = entity.getUsedItemHand();
                int eatDurabilityDamage = getEatDurabilityDamage(stack);
                //? if >=1.21.6 {
                stack.hurtAndBreak(eatDurabilityDamage, entity, hand);
                //?} else {
                /*stack.hurtAndBreak(eatDurabilityDamage, entity, hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
                *///?}
            }
        }
        return stack;
    }

    private static int getEatDurabilityDamage(ItemStack stack) {
        if (!stack.isDamageableItem()) return 0;
        return Math.max(1, (int) Math.ceil(stack.getMaxDamage() * 0.02F));
    }
}
