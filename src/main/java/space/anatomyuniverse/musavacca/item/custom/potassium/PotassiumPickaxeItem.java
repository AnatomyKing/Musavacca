package space.anatomyuniverse.musavacca.item.custom.potassium;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Item;
//? if <1.21.2 {
/*import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
*///?} else {
import net.minecraft.world.item.ToolMaterial;
//?}

public final class PotassiumPickaxeItem
        //? if <1.21.2 {
        /*extends PickaxeItem
        *///?} else {
        extends PotassiumItem
        //?}
{
    public PotassiumPickaxeItem(
            //? if <1.21.2 {
            /*Tier material,
            *///?} else {
            ToolMaterial material,
            //?}
            float attackDamage,
            float attackSpeed,
            Item.Properties properties
    ) {
        //? if <1.21.2 {
        /*super(material, properties.attributes(PickaxeItem.createAttributes(material, attackDamage, attackSpeed)));
        *///?} else {
        super(pickaxeProperties(properties, material, attackDamage, attackSpeed));
        //?}
    }

    //? if <1.21.2 {
    /*@Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (PotassiumItemBehavior.isLookingAtBlock(player)) return InteractionResultHolder.pass(stack);
        if (!PotassiumItemBehavior.canStartEating(stack, player)) return InteractionResultHolder.fail(stack);
        return super.use(level, player, hand);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        return PotassiumItemBehavior.finishUsingItem(stack, level, entity);
    }
    *///?} else {
    private static Item.Properties pickaxeProperties(Item.Properties properties, ToolMaterial material, float attackDamage, float attackSpeed) {
        //? if >=1.21.5 {
        return properties.pickaxe(material, attackDamage, attackSpeed);
        //?} else {
        /*return material.applyToolProperties(properties, BlockTags.MINEABLE_WITH_PICKAXE, attackDamage, attackSpeed);
        *///?}
    }
    //?}
}
