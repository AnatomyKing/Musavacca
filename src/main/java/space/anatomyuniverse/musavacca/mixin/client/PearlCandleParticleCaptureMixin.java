package space.anatomyuniverse.musavacca.mixin.client;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.particle.CampfireSmokeParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.SmokeParticle;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import space.anatomyuniverse.musavacca.particle.tinted.OriginalCandleParticle;
import space.anatomyuniverse.musavacca.particle.tinted.OriginalCandleParticles;

@Mixin(ParticleEngine.class)
public abstract class PearlCandleParticleCaptureMixin {
    @WrapMethod(method = "createParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)Lnet/minecraft/client/particle/Particle;", require = 1, allow = 1)
    private Particle musavacca$placeMountedParticle(ParticleOptions options, double x, double y, double z, double vx, double vy, double vz,
            Operation<Particle> create) {
        Vec3 offset = OriginalCandleParticles.mountOffset();
        if (offset == Vec3.ZERO) return create.call(options, x, y, z, vx, vy, vz);
        OriginalCandleParticles.setMountOffset(Vec3.ZERO);
        try {
            return create.call(options, x + offset.x, y, z + offset.z, vx, vy, vz);
        } finally {
            OriginalCandleParticles.setMountOffset(offset);
        }
    }

    @ModifyVariable(method = "add(Lnet/minecraft/client/particle/Particle;)V", at = @At("HEAD"), argsOnly = true, ordinal = 0, require = 1)
    private Particle musavacca$wrapCandleParticle(Particle particle) {
        Integer color = OriginalCandleParticles.color();
        if (color == null || !particle.isAlive() || particle instanceof OriginalCandleParticle || particle instanceof SmokeParticle
                || particle instanceof CampfireSmokeParticle)
            return particle;
        return particle instanceof OriginalCandleParticle.Source source ? source.musavacca$wrap(color) : particle;
    }
}
