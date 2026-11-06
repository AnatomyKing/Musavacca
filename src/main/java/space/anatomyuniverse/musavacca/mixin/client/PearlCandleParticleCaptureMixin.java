package space.anatomyuniverse.musavacca.mixin.client;

import net.minecraft.client.particle.CampfireSmokeParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.SmokeParticle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import space.anatomyuniverse.musavacca.particle.tinted.OriginalCandleParticle;
import space.anatomyuniverse.musavacca.particle.tinted.OriginalCandleParticles;

@Mixin(ParticleEngine.class)
public abstract class PearlCandleParticleCaptureMixin {
    @ModifyVariable(method = "add(Lnet/minecraft/client/particle/Particle;)V", at = @At("HEAD"), argsOnly = true, ordinal = 0, require = 1)
    private Particle musavacca$wrapCandleParticle(Particle particle) {
        Integer color = OriginalCandleParticles.color();
        if (color == null || !particle.isAlive() || particle instanceof OriginalCandleParticle
                || particle instanceof SmokeParticle || particle instanceof CampfireSmokeParticle) return particle;
        return particle instanceof OriginalCandleParticle.Source source ? source.musavacca$wrap(color) : particle;
    }
}
