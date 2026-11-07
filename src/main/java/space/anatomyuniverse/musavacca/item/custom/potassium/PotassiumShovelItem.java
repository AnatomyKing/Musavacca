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
//? if <26.3 {
import net.minecraft.world.item.ShovelItem;
//?}
//? if >=1.21.2 {
import net.minecraft.world.item.ToolMaterial;
//?} else {
/*import net.minecraft.world.item.Tier;
*///?}
import net.minecraft.world.level.Level;

//? if >=26.3 {
/*public class PotassiumShovelItem extends PotassiumItem {
*///?} else {
public class PotassiumShovelItem extends ShovelItem {
//?}
    //? if >=1.21.2 {
    public PotassiumShovelItem(ToolMaterial material, float attackDamage, float attackSpeed, Properties properties) {
    //?} else {
    /*public PotassiumShovelItem(Tier material, float attackDamage, float attackSpeed, Properties properties) {
    *///?}
        //? if >=26.3 {
        /*super(properties.shovel(material, attackDamage, attackSpeed));
        *///?} else if >=1.21.2 <26.3 {
        super(material, attackDamage, attackSpeed, properties);
        //?} else {
        /*super(material, properties.attributes(ShovelItem.createAttributes(material, attackDamage, attackSpeed)));
        *///?}
    }

    //? if <26.3 {
    @Override
    //? if >=1.21.2 {
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
    //?} else {
    /*public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
    *///?}
        //? if >=1.21.2 {
        if (PotassiumItem.isLookingAtBlock(player)) return InteractionResult.PASS;
        //?}
        ItemStack stack = player.getItemInHand(hand);
        //? if >=1.21.2 {
        if (!PotassiumItem.canStartEating(stack, player)) return InteractionResult.FAIL;
        //?} else {
        /*if (PotassiumItem.isLookingAtBlock(player)) return InteractionResultHolder.pass(stack);
        if (!PotassiumItem.canStartEating(stack, player)) return InteractionResultHolder.fail(stack);
        *///?}
        return super.use(level, player, hand);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        return PotassiumItem.finishEating(stack, level, entity);
    }
    //?}

}
