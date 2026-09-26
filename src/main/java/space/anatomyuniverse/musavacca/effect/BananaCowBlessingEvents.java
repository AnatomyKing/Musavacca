package space.anatomyuniverse.musavacca.effect;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;

public final class BananaCowBlessingEvents {
    private BananaCowBlessingEvents() {}

    @SubscribeEvent
    public static void onEffectApplicable(MobEffectEvent.Applicable event) {
        if (event.getEntity().hasEffect(ModMobEffects.BANANA_COW_BLESSING)
                && !isExempt(event.getEffectInstance())) {
            event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
        }
    }

    @SubscribeEvent
    public static void onEffectAdded(MobEffectEvent.Added event) {
        LivingEntity entity = event.getEntity();
        MobEffectInstance added = event.getEffectInstance();

        if (added.is(ModMobEffects.BANANA_COW_BLESSING)) {
            removeOtherEffects(entity);
        } else if (entity.hasEffect(ModMobEffects.BANANA_COW_BLESSING) && !isExempt(added)) {
            entity.removeEffect(added.getEffect());
        }
    }

    private static boolean isExempt(MobEffectInstance effect) {
        return effect.is(ModMobEffects.BANANA_COW_BLESSING)
                || effect.getEffect().is(ModMobEffectTags.BYPASSES_COW_BLESSING);
    }

    private static void removeOtherEffects(LivingEntity entity) {
        for (MobEffectInstance effect : entity.getActiveEffects().stream()
                .filter(effect -> !isExempt(effect)).toList()) {
            entity.removeEffect(effect.getEffect());
        }
    }
}
