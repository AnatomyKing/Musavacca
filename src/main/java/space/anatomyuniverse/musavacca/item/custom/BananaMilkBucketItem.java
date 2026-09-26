package space.anatomyuniverse.musavacca.item.custom;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import space.anatomyuniverse.musavacca.effect.ModMobEffects;

//? if <1.21.2 {
/*import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MilkBucketItem;
*///?} else {
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.Consumables;

import java.util.List;
//?}

public final class BananaMilkBucketItem extends
        //? if <1.21.2 {
        /*MilkBucketItem
        *///?} else {
        Item
        //?}
{
    private static final int BLESSING_DURATION = 20 * 90;

    public BananaMilkBucketItem(Properties properties) {
        super(properties
                //? if >=1.21.2 {
                .component(DataComponents.CONSUMABLE, new Consumable(
                        Consumables.MILK_BUCKET.consumeSeconds(), Consumables.MILK_BUCKET.animation(),
                        Consumables.MILK_BUCKET.sound(), Consumables.MILK_BUCKET.hasConsumeParticles(), List.of()))
                //?}
        );
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        //? if <1.21.2 {
        /*if (entity instanceof ServerPlayer player) {
            CriteriaTriggers.CONSUME_ITEM.trigger(player, stack);
            player.awardStat(Stats.ITEM_USED.get(this));
        }
        *///?} else {
        ItemStack result = super.finishUsingItem(stack, level, entity);
        //?}

        if (!level.isClientSide()) {
            entity.addEffect(new MobEffectInstance(ModMobEffects.BANANA_COW_BLESSING, BLESSING_DURATION));
        }

        //? if <1.21.2 {
        /*if (entity instanceof Player player) {
            return ItemUtils.createFilledResult(stack, player, new ItemStack(Items.BUCKET), false);
        }
        stack.consume(1, entity);
        return stack;
        *///?} else {
        return result;
        //?}
    }
}
