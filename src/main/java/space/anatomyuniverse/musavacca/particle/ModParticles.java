package space.anatomyuniverse.musavacca.particle;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import space.anatomyuniverse.musavacca.MusaCore;

public final class ModParticles {
    private ModParticles() {}

//    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
//            DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, MusaCore.MOD_ID);
//
//    // IMPORTANT: registry name must match the particle json name: particles/small_pearl.json
//    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SMALL_PEARL =
//            PARTICLE_TYPES.register("small_pearl", () -> new SimpleParticleType(false));
//
//    public static void register(IEventBus bus) {
//        PARTICLE_TYPES.register(bus);
//    }
}