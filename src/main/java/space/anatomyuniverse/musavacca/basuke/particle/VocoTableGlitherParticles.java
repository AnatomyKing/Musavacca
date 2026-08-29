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
    private static final int CRAFTING_PARTICLE_COUNT = 3;
    private static final int SENDING_PARTICLE_COUNT = 6;

    private static final double FORWARD_SPEED = 0.085D;
    private static final double FORWARD_SPEED_SPREAD = 0.018D;
    private static final double MIN_FORWARD_SPEED = 0.055D;

    private static final double SIDEWAYS_SPEED_SPREAD = 0.030D;

    private static final double UPWARD_SPEED = -0.055D;
    private static final double UPWARD_SPEED_SPREAD = -0.016D;
    private static final double MIN_UPWARD_SPEED = -0.018D;

    private static final double SPAWN_SPREAD = 0.025D;
    private static final double START_Y_OFFSET = -0.13D;

    private static final double SENDING_OUTWARD_SPEED = 0.070D;
    private static final double SENDING_OUTWARD_SPEED_SPREAD = 0.014D;
    private static final double SENDING_UPWARD_SPEED = 0.045D;
    private static final double SENDING_UPWARD_SPEED_SPREAD = 0.010D;

    private static final double DEDUCTION_RING_RADIUS = 0.26D;
    private static final double DEDUCTION_RING_RADIUS_SPREAD = 0.035D;
    private static final double DEDUCTION_RING_Y_OFFSET = 0.16D;
    private static final double DEDUCTION_INWARD_SPEED = 0.072D;
    private static final double DEDUCTION_INWARD_SPEED_SPREAD = 0.012D;

    private VocoTableGlitherParticles() {}

    static void spawnTransformation(
            @NotNull ServerLevel level,
            @NotNull Vec3 center,
            int glitherColor
    ) {
        ProfileTintParticles.send(
                level,
                level.random,
                ModParticleTypes.GLITHER.get(),
                glitherColor,
                center.x,
                center.y,
                center.z,
                CRAFTING_PARTICLE_COUNT,
                0.22D,
                0.16D,
                0.22D,
                0.045D
        );
    }

    static void spawnReceptorDirectional(
            @NotNull ServerLevel level,
            @NotNull BlockPos tablePos,
            @NotNull ReceptorPosition receptor,
            int glitherColor
    ) {
        PearlSlotIgnition.Slot pearlSlot =
                VocoReceptorLogic.pearlSlot(
                        VocoTableBlock.lightProperty(
                                receptor
                        ),
                        VocoTableBlock.portalProperty(
                                receptor
                        ),
                        receptor
                );

        Vec3 source =
                pearlSlot.pearlPopPosition(tablePos);

        Vec3 tableCenter =
                VocoTableParticles
                        .itemDisplayCenter(tablePos);

        Vec3 outwardDirection =
                new Vec3(
                        source.x - tableCenter.x,
                        0.0D,
                        source.z - tableCenter.z
                ).normalize();

        Vec3 sidewaysDirection =
                new Vec3(
                        -outwardDirection.z,
                        0.0D,
                        outwardDirection.x
                );

        for (
                int index = 0;
                index < CRAFTING_PARTICLE_COUNT;
                ++index
        ) {
            double forwardSpeed =
                    Math.max(
                            MIN_FORWARD_SPEED,
                            FORWARD_SPEED
                                    + level.random.nextGaussian()
                                    * FORWARD_SPEED_SPREAD
                    );

            double sidewaysSpeed =
                    level.random.nextGaussian()
                            * SIDEWAYS_SPEED_SPREAD;

            double upwardSpeed =
                    Math.max(
                            MIN_UPWARD_SPEED,
                            UPWARD_SPEED
                                    + level.random.nextGaussian()
                                    * UPWARD_SPEED_SPREAD
                    );

            double particleX =
                    source.x
                            + level.random.nextGaussian()
                            * SPAWN_SPREAD;

            double particleY =
                    source.y
                            + START_Y_OFFSET
                            + level.random.nextGaussian()
                            * SPAWN_SPREAD;

            double particleZ =
                    source.z
                            + level.random.nextGaussian()
                            * SPAWN_SPREAD;

            double particleXd =
                    outwardDirection.x
                            * forwardSpeed
                            + sidewaysDirection.x
                            * sidewaysSpeed;

            double particleZd =
                    outwardDirection.z
                            * forwardSpeed
                            + sidewaysDirection.z
                            * sidewaysSpeed;

            ProfileTintParticles.sendExact(
                    level,
                    level.random,
                    ModParticleTypes.GLITHER.get(),
                    glitherColor,
                    particleX,
                    particleY,
                    particleZ,
                    particleXd,
                    upwardSpeed,
                    particleZd
            );
        }
    }

    static void spawnOutwardFromCenter(
            @NotNull ServerLevel level,
            @NotNull Vec3 center,
            int glitherColor
    ) {
        for (
                int index = 0;
                index < SENDING_PARTICLE_COUNT;
                ++index
        ) {
            double angle =
                    fullCircleAngle(
                            level,
                            index
                    );

            Vec3 direction =
                    new Vec3(
                            Math.cos(angle),
                            0.0D,
                            Math.sin(angle)
                    );

            Vec3 sideways =
                    new Vec3(
                            -direction.z,
                            0.0D,
                            direction.x
                    );

            double forwardSpeed =
                    Math.max(
                            0.025D,
                            SENDING_OUTWARD_SPEED
                                    + level.random.nextGaussian()
                                    * SENDING_OUTWARD_SPEED_SPREAD
                    );

            double sidewaysSpeed =
                    level.random.nextGaussian()
                            * SIDEWAYS_SPEED_SPREAD;

            double upwardSpeed =
                    Math.max(
                            0.018D,
                            SENDING_UPWARD_SPEED
                                    + level.random.nextGaussian()
                                    * SENDING_UPWARD_SPEED_SPREAD
                    );

            ProfileTintParticles.sendExact(
                    level,
                    level.random,
                    ModParticleTypes.GLITHER.get(),
                    glitherColor,
                    center.x
                            + level.random.nextGaussian()
                            * SPAWN_SPREAD,
                    center.y
                            + level.random.nextGaussian()
                            * SPAWN_SPREAD,
                    center.z
                            + level.random.nextGaussian()
                            * SPAWN_SPREAD,
                    direction.x
                            * forwardSpeed
                            + sideways.x
                            * sidewaysSpeed,
                    upwardSpeed,
                    direction.z
                            * forwardSpeed
                            + sideways.z
                            * sidewaysSpeed
            );
        }
    }

    static void spawnInwardToCenter(
            @NotNull ServerLevel level,
            @NotNull Vec3 center,
            int glitherColor
    ) {
        for (
                int index = 0;
                index < SENDING_PARTICLE_COUNT;
                ++index
        ) {
            double angle =
                    fullCircleAngle(
                            level,
                            index
                    );

            double radius =
                    Math.max(
                            0.12D,
                            DEDUCTION_RING_RADIUS
                                    + level.random.nextGaussian()
                                    * DEDUCTION_RING_RADIUS_SPREAD
                    );

            Vec3 source =
                    new Vec3(
                            center.x
                                    + Math.cos(angle)
                                    * radius,
                            center.y
                                    + DEDUCTION_RING_Y_OFFSET
                                    + level.random.nextGaussian()
                                    * SPAWN_SPREAD,
                            center.z
                                    + Math.sin(angle)
                                    * radius
                    );

            Vec3 direction =
                    center.subtract(source);

            if (direction.lengthSqr() <= 1.0E-8D) {
                direction =
                        new Vec3(
                                -Math.cos(angle),
                                -0.20D,
                                -Math.sin(angle)
                        );
            }

            direction = direction.normalize();

            double inwardSpeed =
                    Math.max(
                            0.030D,
                            DEDUCTION_INWARD_SPEED
                                    + level.random.nextGaussian()
                                    * DEDUCTION_INWARD_SPEED_SPREAD
                    );

            ProfileTintParticles.sendExact(
                    level,
                    level.random,
                    ModParticleTypes.GLITHER.get(),
                    glitherColor,
                    source.x,
                    source.y,
                    source.z,
                    direction.x
                            * inwardSpeed,
                    direction.y
                            * inwardSpeed,
                    direction.z
                            * inwardSpeed
            );
        }
    }

    private static double fullCircleAngle(
            @NotNull ServerLevel level,
            int index
    ) {
        return (
                Math.PI
                        * 2.0D
                        * index
                        / SENDING_PARTICLE_COUNT
        )
                + level.random.nextGaussian()
                * 0.10D;
    }
}


