package space.anatomyuniverse.musavacca.mixin.client;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import space.anatomyuniverse.musavacca.particle.tinted.ParticleCapture;

@Mixin(ParticleEngine.class)
public abstract class ParticleCaptureMixin {
    @WrapMethod(
            method = "createParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)Lnet/minecraft/client/particle/Particle;",
            require = 1,
            allow = 1)
    private Particle musavacca$offsetParticle(
            ParticleOptions options, double x, double y, double z, double vx, double vy, double vz, Operation<Particle> create) {
        Vec3 offset = ParticleCapture.offset();
        if (offset == Vec3.ZERO) return create.call(options, x, y, z, vx, vy, vz);
        ParticleCapture.setOffset(Vec3.ZERO);
        try {
            return create.call(options, x + offset.x, y + offset.y, z + offset.z, vx, vy, vz);
        } finally {
            ParticleCapture.setOffset(offset);
        }
    }

    @ModifyVariable(method = "add(Lnet/minecraft/client/particle/Particle;)V", at = @At("HEAD"), argsOnly = true, ordinal = 0, require = 1)
    private Particle musavacca$wrapParticle(Particle particle) {
        return ParticleCapture.wrap(particle);
    }
}
