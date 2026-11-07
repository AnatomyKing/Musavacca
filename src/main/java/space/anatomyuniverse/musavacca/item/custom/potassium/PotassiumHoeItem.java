package space.anatomyuniverse.musavacca.item.custom.potassium;

import net.minecraft.world.InteractionHand;
//? if >=1.21.2 {
import net.minecraft.world.InteractionResult;
//?} else {
/*import net.minecraft.world.InteractionResultHolder;
*///?}
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
//? if <26.3 {
import net.minecraft.world.item.HoeItem;
//?}
import net.minecraft.world.item.ItemStack;
//? if >=1.21.2 {
import net.minecraft.world.item.ToolMaterial;
//?} else {
/*import net.minecraft.world.item.Tier;
*///?}
import net.minecraft.world.level.Level;

//? if >=26.3 {
/*public class PotassiumHoeItem extends PotassiumItem {
*///?} else {
public class PotassiumHoeItem extends HoeItem {
//?}
    //? if >=1.21.2 {
    public PotassiumHoeItem(ToolMaterial material, float attackDamage, float attackSpeed, Properties properties) {
    //?} else {
    /*public PotassiumHoeItem(Tier material, float attackDamage, float attackSpeed, Properties properties) {
    *///?}
        //? if >=26.3 {
        /*super(properties.hoe(material, attackDamage, attackSpeed));
        *///?} else if >=1.21.2 <26.3 {
        super(material, attackDamage, attackSpeed, properties);
        //?} else {
        /*super(material, properties.attributes(HoeItem.createAttributes(material, attackDamage, attackSpeed)));
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
