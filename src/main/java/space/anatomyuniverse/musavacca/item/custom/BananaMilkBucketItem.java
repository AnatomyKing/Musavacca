package space.anatomyuniverse.musavacca.item.custom;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import space.anatomyuniverse.musavacca.effect.ModMobEffects;

public final class BananaMilkBucketItem extends Item {

    private static final int BLESSING_DURATION =
            20 * 90;

    public BananaMilkBucketItem(
            Properties properties
    ) {
        super(properties);
    }

    @Override
    public ItemStack finishUsingItem(
            ItemStack stack,
            Level level,
            LivingEntity entity
    ) {
        ItemStack result =
                super.finishUsingItem(
                        stack,
                        level,
                        entity
                );

        if (!level.isClientSide()) {
            entity.addEffect(
                    new MobEffectInstance(
                            ModMobEffects.BANANA_COW_BLESSING,
                            BLESSING_DURATION
                    )
            );
        }

        return result;
    }
}