package space.anatomyuniverse.musavacca.particle;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import space.anatomyuniverse.musavacca.particle.particles.GlitherParticle;
import space.anatomyuniverse.musavacca.particle.particles.HexFallingSporeBlossomProvider;
import space.anatomyuniverse.musavacca.particle.particles.HexSporeBlossomAirParticle;
import space.anatomyuniverse.musavacca.particle.particles.PearlFlameParticle;
import space.anatomyuniverse.musavacca.particle.particles.PearlGlyphPortalParticle;

public final class ModParticleProviders {
    private ModParticleProviders() {}

    public static void register(IEventBus modBus) {
        modBus.addListener(ModParticleProviders::registerParticleProviders);
    }

    private static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(
                ModParticleTypes.GLITHER.get(),
                GlitherParticle.Provider::new
        );

        event.registerSpriteSet(
                ModParticleTypes.HEX_FALLING_SPORE_BLOSSOM.get(),
                HexFallingSporeBlossomProvider::new
        );

        event.registerSpriteSet(
                ModParticleTypes.HEX_SPORE_BLOSSOM_AIR.get(),
                HexSporeBlossomAirParticle.Provider::new
        );

        event.registerSpriteSet(
                ModParticleTypes.PEARL_FLAME.get(),
                PearlFlameParticle.Provider::new
        );

        event.registerSpriteSet(
                ModParticleTypes.PEARL_G_TINTED.get(),
                PearlGlyphPortalParticle.Provider::new
        );

        event.registerSpriteSet(
                ModParticleTypes.PEARL_2_TINTED.get(),
                PearlGlyphPortalParticle.Provider::new
        );

        event.registerSpriteSet(
                ModParticleTypes.PEARL_C_TINTED.get(),
                PearlGlyphPortalParticle.Provider::new
        );

        event.registerSpriteSet(
                ModParticleTypes.PEARL_H_TINTED.get(),
                PearlGlyphPortalParticle.Provider::new
        );
    }
}