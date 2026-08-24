package space.anatomyuniverse.musavacca.effect;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;

import java.util.List;

public final class BananaCowBlessingEvents {

    private BananaCowBlessingEvents() {}

    @SubscribeEvent
    public static void onEffectApplicable(
            MobEffectEvent.Applicable event
    ) {
        LivingEntity entity = event.getEntity();
        MobEffectInstance incoming = event.getEffectInstance();

        if (!entity.hasEffect(
                ModMobEffects.BANANA_COW_BLESSING
        )) {
            return;
        }
        if (incoming.is(
                ModMobEffects.BANANA_COW_BLESSING
        )) {
            return;
        }

        event.setResult(
                MobEffectEvent.Applicable.Result.DO_NOT_APPLY
        );
    }

    @SubscribeEvent
    public static void onEffectAdded(
            MobEffectEvent.Added event
    ) {
        LivingEntity entity = event.getEntity();
        MobEffectInstance added = event.getEffectInstance();

        if (added.is(
                ModMobEffects.BANANA_COW_BLESSING
        )) {
            removeOtherEffects(entity);
            return;
        }

        if (entity.hasEffect(
                ModMobEffects.BANANA_COW_BLESSING
        )) {
            entity.removeEffect(
                    added.getEffect()
            );
        }
    }

    private static void removeOtherEffects(
            LivingEntity entity
    ) {
        List<MobEffectInstance> effects =
                entity.getActiveEffects()
                        .stream()
                        .filter(effect ->
                                !effect.is(
                                        ModMobEffects
                                                .BANANA_COW_BLESSING
                                )
                        )
                        .toList();

        for (MobEffectInstance effect : effects) {
            entity.removeEffect(
                    effect.getEffect()
            );
        }
    }
}