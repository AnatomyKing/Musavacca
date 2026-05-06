package space.anatomyuniverse.musavacca.particle;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import space.anatomyuniverse.musavacca.particle.particles.HexFallingSporeBlossomProvider;
import space.anatomyuniverse.musavacca.particle.particles.HexSporeBlossomAirParticle;
import space.anatomyuniverse.musavacca.particle.particles.PearlGlyphParticle;

public final class ModParticleProviders {
    private ModParticleProviders() {}

    public static void register(IEventBus modBus) {
        modBus.addListener(ModParticleProviders::registerParticleProviders);
    }

    private static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(
                ModParticleTypes.HEX_FALLING_SPORE_BLOSSOM.get(),
                HexFallingSporeBlossomProvider::new
        );

        event.registerSpriteSet(
                ModParticleTypes.HEX_SPORE_BLOSSOM_AIR.get(),
                HexSporeBlossomAirParticle.Provider::new
        );

        event.registerSpriteSet(
                ModParticleTypes.PEARL_GLYPHS.get(),
                PearlGlyphParticle.Provider::new
        );

        event.registerSpriteSet(
                ModParticleTypes.PEARL_GLYPHS_TINT.get(),
                PearlGlyphParticle.TintedProvider::new
        );
    }
}