package space.anatomyuniverse.musavacca.effect;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import space.anatomyuniverse.musavacca.MusaCore;

public final class ModMobEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, MusaCore.MOD_ID);
    public static final DeferredHolder<MobEffect, MobEffect> BANANA_COW_BLESSING = MOB_EFFECTS.register("cow_blessing",
            () -> new MobEffect(MobEffectCategory.BENEFICIAL, 0xF6D84A) {});
    public static final DeferredHolder<MobEffect, MobEffect> PLAYER_STATUS = MOB_EFFECTS.register("incoming_call",
            () -> new MobEffect(MobEffectCategory.NEUTRAL, 0x6FA8FF) {});

    public static void register(IEventBus modBus) {
        MOB_EFFECTS.register(modBus);
    }

    private ModMobEffects() {}
}
