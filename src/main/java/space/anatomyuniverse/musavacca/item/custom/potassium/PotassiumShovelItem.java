package space.anatomyuniverse.musavacca.item.custom.potassium;

import net.minecraft.world.InteractionHand;

//? if >=1.21.2 {
import net.minecraft.world.InteractionResult;
//?} else {
/*import net.minecraft.world.InteractionResultHolder;
*///?}

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

//? if >=26.3 {
/*import net.minecraft.world.item.Item;
*///?} else {
import net.minecraft.world.item.ShovelItem;
//?}

//? if >=1.21.2 {
import net.minecraft.world.item.ToolMaterial;
//?} else {
/*import net.minecraft.world.item.Tier;
*///?}

import net.minecraft.world.level.Level;

//? if >=26.3 {
/*public class PotassiumShovelItem extends Item {
*///?} else {
public class PotassiumShovelItem extends ShovelItem {
//?}

    public PotassiumShovelItem(

            //? if >=1.21.2 {
            ToolMaterial material,
            //?} else {
            /*Tier material,
            *///?}

            float attackDamage,
            float attackSpeed,
            Properties properties
    ) {

        //? if >=26.3 {
        /*super(properties.shovel(material, attackDamage, attackSpeed));
        *///?} else if >=1.21.2 <26.3 {
        super(material, attackDamage, attackSpeed, properties);
        //?} else {
        /*super(material, properties.attributes(ShovelItem.createAttributes(material, attackDamage, attackSpeed)));
        *///?}

    }

    @Override

    //? if >=1.21.2 {
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (PotassiumItemBehavior.isLookingAtBlock(player)) {
            return InteractionResult.PASS;
    //?} else {
    /*public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (PotassiumItemBehavior.isLookingAtBlock(player)) {
            return InteractionResultHolder.pass(stack);
    *///?}
        }

        //? if >=1.21.2 {
        ItemStack stack = player.getItemInHand(hand);

        if (!PotassiumItemBehavior.canStartEating(stack, player)) {
            return InteractionResult.FAIL;
        //?} else {
        /*if (!PotassiumItemBehavior.canStartEating(stack, player)) {
            return InteractionResultHolder.fail(stack);
        *///?}
        }

        return super.use(level, player, hand);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        return PotassiumItemBehavior.finishUsingItem(stack, level, entity);
    }
}
