package space.anatomyuniverse.musavacca.particle;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
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
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> GLINSTER =
            PARTICLE_TYPES.register("glinster", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> GLINSTER_BURST =
            PARTICLE_TYPES.register("glinster_burst", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, HexColorParticleType> HEX_SPORE_BLOSSOM_AIR =
            PARTICLE_TYPES.register("hex_spore_blossom_air", () -> new HexColorParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> PEARL_G =
            PARTICLE_TYPES.register("pearl_g", () -> new SimpleParticleType(false));

    public static void register(IEventBus modBus) {
        PARTICLE_TYPES.register(modBus);
    }
}
