package space.anatomyuniverse.musavacca.item.custom.potassium;

import net.minecraft.world.InteractionHand;
//? if <1.21.2 {
/*import net.minecraft.world.InteractionResultHolder;
*///?} else {
import net.minecraft.world.InteractionResult;
//?}
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
//? if <1.21.2 {
/*import net.minecraft.world.item.Tier;
*///?} else {
import net.minecraft.world.item.ToolMaterial;
//?}
import net.minecraft.world.level.Level;

public class PotassiumHoeItem extends HoeItem {
    public PotassiumHoeItem(
            //? if <1.21.2 {
            /*Tier material,
            *///?} else {
            ToolMaterial material,
            //?}
            float attackDamage,
            float attackSpeed,
            Properties properties
    ) {
        //? if <1.21.2 {
        /*super(material, properties.attributes(HoeItem.createAttributes(material, attackDamage, attackSpeed)));
        *///?} else {
        super(material, attackDamage, attackSpeed, properties);
        //?}
    }

    @Override
    //? if <1.21.2 {
    /*public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (PotassiumItemBehavior.isLookingAtBlock(player)) {
            return InteractionResultHolder.pass(stack);
        }

        if (!PotassiumItemBehavior.canStartEating(stack, player)) {
            return InteractionResultHolder.fail(stack);
        }

        return super.use(level, player, hand);
    }
    *///?} else {
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (PotassiumItemBehavior.isLookingAtBlock(player)) {
            return InteractionResult.PASS;
        }

        ItemStack stack = player.getItemInHand(hand);

        if (!PotassiumItemBehavior.canStartEating(stack, player)) {
            return InteractionResult.FAIL;
        }

        return super.use(level, player, hand);
    }
    //?}

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        return PotassiumItemBehavior.finishUsingItem(stack, level, entity);
    }
}


