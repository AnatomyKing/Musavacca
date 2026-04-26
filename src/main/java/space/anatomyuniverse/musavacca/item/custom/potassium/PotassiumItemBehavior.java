package space.anatomyuniverse.musavacca.item.custom.potassium;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

public final class PotassiumItemBehavior {
    private static final int EAT_DURABILITY_DAMAGE = 27;

    private PotassiumItemBehavior() {
    }

    public static boolean isLookingAtBlock(Player player) {
        HitResult hitResult = player.pick(player.blockInteractionRange(), 0.0F, false);
        return hitResult.getType() == HitResult.Type.BLOCK;
    }

    public static ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        FoodProperties food = stack.get(DataComponents.FOOD);
        Consumable consumable = stack.get(DataComponents.CONSUMABLE);

        if (!level.isClientSide() && food != null && consumable != null) {
            food.onConsume(level, entity, stack, consumable);

            if (!(entity instanceof Player player) || !player.getAbilities().instabuild) {
                InteractionHand hand = entity.getUsedItemHand();
                stack.hurtAndBreak(EAT_DURABILITY_DAMAGE, entity, hand);
            }
        }

        return stack;
    }
}