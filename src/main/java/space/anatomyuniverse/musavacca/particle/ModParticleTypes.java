package space.anatomyuniverse.musavacca.particle;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import space.anatomyuniverse.musavacca.MusaCore;
import space.anatomyuniverse.musavacca.particle.utils.HexColorParticleType;

public final class ModParticleTypes {
    private ModParticleTypes() {}

    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, MusaCore.MOD_ID);

    public static final DeferredHolder<ParticleType<?>, HexColorParticleType> HEX_FALLING_SPORE_BLOSSOM =
            PARTICLE_TYPES.register("hex_falling_spore_blossom", () -> new HexColorParticleType(false));

    public static final DeferredHolder<ParticleType<?>, HexColorParticleType> HEX_SPORE_BLOSSOM_AIR =
            PARTICLE_TYPES.register("hex_spore_blossom_air", () -> new HexColorParticleType(false));

    public static void register(IEventBus modBus) {
        PARTICLE_TYPES.register(modBus);
    }
}