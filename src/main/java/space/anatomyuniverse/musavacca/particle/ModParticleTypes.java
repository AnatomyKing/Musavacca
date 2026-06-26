package space.anatomyuniverse.musavacca.particle;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import space.anatomyuniverse.musavacca.MusaCore;
import space.anatomyuniverse.musavacca.particle.tinted.ProfileTintParticleType;
import space.anatomyuniverse.musavacca.particle.utils.HexColorParticleType;
import space.anatomyuniverse.musavacca.tint.PearlFireTintProfiles;

public final class ModParticleTypes {
    private ModParticleTypes() {}

    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, MusaCore.MOD_ID);

    public static final DeferredHolder<ParticleType<?>, ProfileTintParticleType> GLITHER =
            PARTICLE_TYPES.register("glither",
                    () -> new ProfileTintParticleType(false, () -> PearlFireTintProfiles.GLITHER_PARTICLE));

    public static final DeferredHolder<ParticleType<?>, HexColorParticleType> HEX_FALLING_SPORE_BLOSSOM =
            PARTICLE_TYPES.register("hex_falling_spore_blossom", () -> new HexColorParticleType(false));

    public static final DeferredHolder<ParticleType<?>, HexColorParticleType> HEX_SPORE_BLOSSOM_AIR =
            PARTICLE_TYPES.register("hex_spore_blossom_air", () -> new HexColorParticleType(false));

    /*
     * Real render particles.
     * These are registered with registerSpriteSet and require JSON files.
     */

    public static final DeferredHolder<ParticleType<?>, ProfileTintParticleType> PEARL_FLAME =
            PARTICLE_TYPES.register("pearl_flame",
                    () -> new ProfileTintParticleType(false, () -> PearlFireTintProfiles.PEARL_FLAME));

    public static final DeferredHolder<ParticleType<?>, ProfileTintParticleType> PEARL_G_TINTED =
            PARTICLE_TYPES.register("pearl_g_tinted",
                    () -> new ProfileTintParticleType(false, () -> PearlFireTintProfiles.PORTAL_GLYPH_PARTICLE));

    public static final DeferredHolder<ParticleType<?>, ProfileTintParticleType> PEARL_2_TINTED =
            PARTICLE_TYPES.register("pearl_2_tinted",
                    () -> new ProfileTintParticleType(false, () -> PearlFireTintProfiles.PORTAL_GLYPH_PARTICLE));

    public static final DeferredHolder<ParticleType<?>, ProfileTintParticleType> PEARL_C_TINTED =
            PARTICLE_TYPES.register("pearl_c_tinted",
                    () -> new ProfileTintParticleType(false, () -> PearlFireTintProfiles.PORTAL_GLYPH_PARTICLE));

    public static final DeferredHolder<ParticleType<?>, ProfileTintParticleType> PEARL_H_TINTED =
            PARTICLE_TYPES.register("pearl_h_tinted",
                    () -> new ProfileTintParticleType(false, () -> PearlFireTintProfiles.PORTAL_GLYPH_PARTICLE));

    public static void register(IEventBus modBus) {
        PARTICLE_TYPES.register(modBus);
    }
}