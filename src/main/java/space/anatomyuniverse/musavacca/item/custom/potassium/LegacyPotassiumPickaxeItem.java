package space.anatomyuniverse.musavacca.item.custom.potassium;

//? if <1.21.2 {
/*import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;

public final class LegacyPotassiumPickaxeItem extends PickaxeItem {
    public LegacyPotassiumPickaxeItem(Tier tier, Properties properties) {
        super(tier, properties.attributes(PickaxeItem.createAttributes(tier, 1.0F, -2.8F)));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (PotassiumItemBehavior.isLookingAtBlock(player)) {
            return InteractionResultHolder.pass(stack);
        }

        if (!PotassiumItemBehavior.canStartEating(stack, player)) {
            return InteractionResultHolder.fail(stack);
        }

        return super.use(level, player, hand);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        return PotassiumItemBehavior.finishUsingItem(stack, level, entity);
    }
}
*///?}
