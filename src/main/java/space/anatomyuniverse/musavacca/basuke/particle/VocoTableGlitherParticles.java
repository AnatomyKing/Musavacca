package space.anatomyuniverse.musavacca.basuke.particle;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import space.anatomyuniverse.musavacca.block.custom.VocoTableBlock;
import space.anatomyuniverse.musavacca.block.custom.logic.PearlSlotIgnition;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoReceptorLogic;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoReceptorLogic.ReceptorPosition;
import space.anatomyuniverse.musavacca.particle.ModParticleTypes;
import space.anatomyuniverse.musavacca.particle.tinted.ProfileTintParticles;

final class VocoTableGlitherParticles {
    private VocoTableGlitherParticles() {}
    static void spawnTransformation(@NotNull ServerLevel level, @NotNull Vec3 center, int glitherColor) {
        ProfileTintParticles.send(level, level.getRandom(), ModParticleTypes.GLITHER.get(), glitherColor, center.x, center.y, center.z, 3, 0.22D, 0.16D, 0.22D,
                0.045D);
    }

    static void spawnReceptorDirectional(@NotNull ServerLevel level, @NotNull BlockPos tablePos, @NotNull ReceptorPosition receptor, int glitherColor) {
        PearlSlotIgnition.Slot pearlSlot =
                VocoReceptorLogic.pearlSlot(VocoTableBlock.lightProperty(receptor), VocoTableBlock.portalProperty(receptor), receptor);
        Vec3 source = pearlSlot.pearlPopPosition(tablePos);
        Vec3 tableCenter = VocoTableParticles.itemDisplayCenter(tablePos);
        Vec3 outwardDirection = new Vec3(source.x - tableCenter.x, 0.0D, source.z - tableCenter.z).normalize();
        Vec3 sidewaysDirection = new Vec3(-outwardDirection.z, 0.0D, outwardDirection.x);
        for (int index = 0; index < 3; ++index) {
            double forwardSpeed = Math.max(0.055D, 0.085D + level.getRandom().nextGaussian() * 0.018D);
            double sidewaysSpeed = level.getRandom().nextGaussian() * 0.030D;
            double upwardSpeed = Math.max((-0.018D), (-0.055D) + level.getRandom().nextGaussian() * (-0.016D));
            double particleX = source.x + level.getRandom().nextGaussian() * 0.025D;
            double particleY = source.y + (-0.13D) + level.getRandom().nextGaussian() * 0.025D;
            double particleZ = source.z + level.getRandom().nextGaussian() * 0.025D;
            double particleXd = outwardDirection.x * forwardSpeed + sidewaysDirection.x * sidewaysSpeed;
            double particleZd = outwardDirection.z * forwardSpeed + sidewaysDirection.z * sidewaysSpeed;
            ProfileTintParticles.sendExact(level, level.getRandom(), ModParticleTypes.GLITHER.get(), glitherColor, particleX, particleY, particleZ, particleXd,
                    upwardSpeed, particleZd);
        }
    }

    static void spawnOutwardFromCenter(@NotNull ServerLevel level, @NotNull Vec3 center, int glitherColor) {
        for (int index = 0; index < 6; ++index) {
            double angle = fullCircleAngle(level, index);
            Vec3 direction = new Vec3(Math.cos(angle), 0.0D, Math.sin(angle));
            Vec3 sideways = new Vec3(-direction.z, 0.0D, direction.x);
            double forwardSpeed = Math.max(0.025D, 0.070D + level.getRandom().nextGaussian() * 0.014D);
            double sidewaysSpeed = level.getRandom().nextGaussian() * 0.030D;
            double upwardSpeed = Math.max(0.018D, 0.045D + level.getRandom().nextGaussian() * 0.010D);
            ProfileTintParticles.sendExact(level, level.getRandom(), ModParticleTypes.GLITHER.get(), glitherColor,
                    center.x + level.getRandom().nextGaussian() * 0.025D, center.y + level.getRandom().nextGaussian() * 0.025D,
                    center.z + level.getRandom().nextGaussian() * 0.025D, direction.x * forwardSpeed + sideways.x * sidewaysSpeed, upwardSpeed,
                    direction.z * forwardSpeed + sideways.z * sidewaysSpeed);
        }
    }

    static void spawnInwardToCenter(@NotNull ServerLevel level, @NotNull Vec3 center, int glitherColor) {
        for (int index = 0; index < 6; ++index) {
            double angle = fullCircleAngle(level, index);
            double radius = Math.max(0.12D, 0.26D + level.getRandom().nextGaussian() * 0.035D);
            Vec3 source =
                    new Vec3(center.x + Math.cos(angle) * radius, center.y + 0.16D + level.getRandom().nextGaussian() * 0.025D,
                            center.z + Math.sin(angle) * radius);
            Vec3 direction = center.subtract(source);
            if (direction.lengthSqr() <= 1.0E-8D) {
                direction = new Vec3(-Math.cos(angle), -0.20D, -Math.sin(angle));
            }
            direction = direction.normalize();
            double inwardSpeed = Math.max(0.030D, 0.072D + level.getRandom().nextGaussian() * 0.012D);
            ProfileTintParticles.sendExact(level, level.getRandom(), ModParticleTypes.GLITHER.get(), glitherColor, source.x, source.y, source.z,
                    direction.x * inwardSpeed, direction.y * inwardSpeed, direction.z * inwardSpeed);
        }
    }

    private static double fullCircleAngle(@NotNull ServerLevel level, int index) {
        return (Math.PI * 2.0D * index / 6) + level.getRandom().nextGaussian() * 0.10D;
    }
}
