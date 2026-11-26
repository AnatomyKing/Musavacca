package space.anatomyuniverse.musavacca.item.custom.potassium;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
//? if >=1.21.2 {
import net.minecraft.world.InteractionResult;
//?} else {
/*import net.minecraft.world.InteractionResultHolder;
*///?}
//? if <26.3 {
import net.minecraft.world.item.AxeItem;
//?}
//? if >=1.21.2 {
import net.minecraft.world.item.ToolMaterial;
//?} else {
/*import net.minecraft.world.item.Tier;
*///?}

//? if >=26.3 {
/*public class PotassiumAxeItem extends PotassiumItem {
*///?} else {
public class PotassiumAxeItem extends AxeItem {
//?}
    //? if >=1.21.2 {
    public PotassiumAxeItem(ToolMaterial material, float attackDamage, float attackSpeed, Properties properties) {
    //?} else {
    /*public PotassiumAxeItem(Tier material, float attackDamage, float attackSpeed, Properties properties) {
    *///?}
        //? if >=26.3 {
        /*super(properties.axe(material, attackDamage, attackSpeed));
        *///?} else if >=1.21.2 <26.3 {
        super(material, attackDamage, attackSpeed, properties);
        //?} else {
        /*super(material, properties.attributes(AxeItem.createAttributes(material, attackDamage, attackSpeed)));
        *///?}
    }

    //? if <26.3 {
    @Override
    //? if >=1.21.2 {
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
    //?} else {
    /*public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
    *///?}
        return PotassiumItem.useTool(level, player, hand);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        return PotassiumItem.finishEating(stack, level, entity);
    }
    //?}
}
