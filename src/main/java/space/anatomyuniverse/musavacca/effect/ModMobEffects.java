package space.anatomyuniverse.musavacca.effect;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import space.anatomyuniverse.musavacca.MusaCore;

public final class ModMobEffects {

    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(
                    Registries.MOB_EFFECT,
                    MusaCore.MOD_ID
            );

    public static final DeferredHolder<
            MobEffect,
            BananaCowBlessingEffect
            > BANANA_COW_BLESSING =
            MOB_EFFECTS.register(
                    "cow_blessing",
                    BananaCowBlessingEffect::new
            );

    public static void register(IEventBus modBus) {
        MOB_EFFECTS.register(modBus);
    }

    private ModMobEffects() {}
}
