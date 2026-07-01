package space.anatomyuniverse.musavacca.block.custom.logic;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import space.anatomyuniverse.musavacca.particle.ModParticleTypes;
import space.anatomyuniverse.musavacca.particle.tinted.ProfileTintParticles;

public final class CandleParticleEffects {
    private CandleParticleEffects() {}

    public static void spawnPearlVanillaStyle(
            Level level,
            RandomSource random,
            Vec3 pos,
            int hexColor
    ) {
        float roll = random.nextFloat();

        if (roll < 0.30F) {
            level.addParticle(ParticleTypes.SMOKE, pos.x, pos.y, pos.z, 0.0D, 0.0D, 0.0D);

            if (roll < 0.17F) {
                level.playLocalSound(
                        pos.x + 0.5D,
                        pos.y + 0.5D,
                        pos.z + 0.5D,
                        SoundEvents.CANDLE_AMBIENT,
                        SoundSource.BLOCKS,
                        1.0F + random.nextFloat(),
                        random.nextFloat() * 0.7F + 0.3F,
                        false
                );
            }
        }

        spawnPearlFlame(level, random, pos, hexColor);
    }

    public static void spawnPearlTableStyle(
            Level level,
            RandomSource random,
            Vec3 pos,
            int hexColor
    ) {
        if (random.nextFloat() < 0.14F) {
            level.addParticle(ParticleTypes.SMOKE, pos.x, pos.y, pos.z, 0.0D, 0.0D, 0.0D);
        }

        if (random.nextFloat() < 0.025F) {
            level.playLocalSound(
                    pos.x,
                    pos.y,
                    pos.z,
                    SoundEvents.CANDLE_AMBIENT,
                    SoundSource.BLOCKS,
                    0.55F + random.nextFloat() * 0.35F,
                    0.65F + random.nextFloat() * 0.45F,
                    false
            );
        }

        spawnPearlFlame(level, random, pos, hexColor);
    }

    public static void spawnVanilla(Level level, RandomSource random, Vec3 pos) {
        float roll = random.nextFloat();

        if (roll < 0.30F) {
            level.addParticle(ParticleTypes.SMOKE, pos.x, pos.y, pos.z, 0.0D, 0.0D, 0.0D);

            if (roll < 0.17F) {
                level.playLocalSound(
                        pos.x + 0.5D,
                        pos.y + 0.5D,
                        pos.z + 0.5D,
                        SoundEvents.CANDLE_AMBIENT,
                        SoundSource.BLOCKS,
                        1.0F + random.nextFloat(),
                        random.nextFloat() * 0.7F + 0.3F,
                        false
                );
            }
        }

        level.addParticle(ParticleTypes.SMALL_FLAME, pos.x, pos.y, pos.z, 0.0D, 0.0D, 0.0D);
    }

    private static void spawnPearlFlame(
            Level level,
            RandomSource random,
            Vec3 pos,
            int hexColor
    ) {
        ProfileTintParticles.spawn(
                level,
                random,
                ModParticleTypes.PEARL_FLAME.get(),
                hexColor,
                pos.x,
                pos.y,
                pos.z,
                0.0D,
                0.0D,
                0.0D
        );
    }
}
