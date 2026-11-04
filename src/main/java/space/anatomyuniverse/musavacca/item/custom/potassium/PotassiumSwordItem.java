package space.anatomyuniverse.musavacca.item.custom.potassium;

//? if >=1.21.2 {
import net.minecraft.world.item.ToolMaterial;
//?} else {
/*import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
*///?}

public final class PotassiumSwordItem
        //? if >=1.21.2 {
        extends PotassiumItem
        //?} else {
        /*extends SwordItem
        *///?}
{
    //? if >=1.21.2 {
    public PotassiumSwordItem(ToolMaterial material, float attackDamage, float attackSpeed, Properties properties) {
    //?} else {
    /*public PotassiumSwordItem(Tier material, float attackDamage, float attackSpeed, Properties properties) {
    *///?}
        //? if >=1.21.2 {
        super(swordProperties(properties, material, attackDamage, attackSpeed));
        //?} else {
        /*super(material, properties.attributes(SwordItem.createAttributes(material, attackDamage, attackSpeed)));
        *///?}
    }

    //? if >=1.21.2 {
    private static Properties swordProperties(Properties properties, ToolMaterial material, float attackDamage, float attackSpeed) {
        //? if >=1.21.5 {
        return properties.sword(material, attackDamage, attackSpeed);
        //?} else {
        /*return material.applySwordProperties(properties, attackDamage, attackSpeed);
        *///?}
    }
    //?}
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
    *///?}
}
