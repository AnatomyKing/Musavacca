package space.anatomyuniverse.musavacca.item.custom.potassium;

import net.minecraft.tags.BlockTags;

//? if >=1.21.2 {
import net.minecraft.world.item.ToolMaterial;
//?} else {
/*import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
*///?}

public final class PotassiumPickaxeItem
        //? if >=1.21.2 {
        extends PotassiumItem
        //?} else {
        /*extends PickaxeItem
        *///?}
{
    //? if >=1.21.2 {
    public PotassiumPickaxeItem(ToolMaterial material, float attackDamage, float attackSpeed, Properties properties) {
    //?} else {
    /*public PotassiumPickaxeItem(Tier material, float attackDamage, float attackSpeed, Properties properties) {
    *///?}
        //? if >=1.21.5 {
        super(properties.pickaxe(material, attackDamage, attackSpeed));
        //?} else if >=1.21.2 {
        /*super(material.applyToolProperties(properties, BlockTags.MINEABLE_WITH_PICKAXE, attackDamage, attackSpeed));
        *///?} else {
        /*super(material, properties.attributes(PickaxeItem.createAttributes(material, attackDamage, attackSpeed)));
        *///?}
    }

    //? if <1.21.2 {
    /*@Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (PotassiumItem.isLookingAtBlock(player)) return InteractionResultHolder.pass(stack);
        if (!PotassiumItem.canStartEating(stack, player)) return InteractionResultHolder.fail(stack);
        return super.use(level, player, hand);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        return PotassiumItem.finishEating(stack, level, entity);
    }
    *///?}
}
