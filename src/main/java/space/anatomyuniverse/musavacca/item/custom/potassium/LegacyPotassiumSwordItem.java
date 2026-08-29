package space.anatomyuniverse.musavacca.item.custom.potassium;

//? if <1.21.2 {
/*import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;

public final class LegacyPotassiumSwordItem extends SwordItem {
    public LegacyPotassiumSwordItem(Tier tier, Properties properties) {
        super(tier, properties.attributes(SwordItem.createAttributes(tier, 3.0F, -2.4F)));
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
