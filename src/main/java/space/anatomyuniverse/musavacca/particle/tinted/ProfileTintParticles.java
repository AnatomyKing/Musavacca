package space.anatomyuniverse.musavacca.particle.tinted;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

public final class ProfileTintParticles {
    private ProfileTintParticles() {}

    public static void spawn(Level level, RandomSource random, ProfileTintParticleType renderType, int color, double x, double y, double z, double xSpeed,
            double ySpeed, double zSpeed) {
        if (level == null || !level.isClientSide()) return;
        RandomSource rng = random == null ? level.getRandom() : random;
        int layerCount = renderType.layerCount();
        int seed = rng.nextInt();
        for (int layer = 0; layer < layerCount; layer++) {
            level.addParticle(new ProfileTintParticleOptions(renderType, color, layer, layerCount, seed), x, y, z, xSpeed, ySpeed, zSpeed);
        }
    }

    public static void send(ServerLevel level, RandomSource random, ProfileTintParticleType renderType, int color, double x, double y, double z, int count,
            double xOffset, double yOffset, double zOffset, double speed) {
        if (level == null || level.isClientSide()) return;
        RandomSource rng = random == null ? level.getRandom() : random;
        if (count <= 0) {
            sendExact(level, rng, renderType, color, x, y, z, xOffset, yOffset, zOffset);
            return;
        }
        for (int i = 0; i < count; i++) {
            double particleX = x + rng.nextGaussian() * xOffset;
            double particleY = y + rng.nextGaussian() * yOffset;
            double particleZ = z + rng.nextGaussian() * zOffset;
            double particleXd = rng.nextGaussian() * speed;
            double particleYd = rng.nextGaussian() * speed;
            double particleZd = rng.nextGaussian() * speed;
            sendExact(level, rng, renderType, color, particleX, particleY, particleZ, particleXd, particleYd, particleZd);
        }
    }

    public static void sendExact(ServerLevel level, RandomSource random, ProfileTintParticleType renderType, int color, double x, double y, double z,
            double xSpeed, double ySpeed, double zSpeed) {
        if (level == null || level.isClientSide()) return;
        RandomSource rng = random == null ? level.getRandom() : random;
        int layerCount = renderType.layerCount();
        int seed = rng.nextInt();
        for (int layer = 0; layer < layerCount; layer++) {
            level.sendParticles(new ProfileTintParticleOptions(renderType, color, layer, layerCount, seed), x, y, z, 0, xSpeed, ySpeed, zSpeed, 1.0D);
        }
    }

    public static void spawnRandomVariant(Level level, RandomSource random, int color, double x, double y, double z, double xSpeed, double ySpeed,
            double zSpeed, ProfileTintParticleType... renderTypes) {
        if (level == null || !level.isClientSide()) return;
        if (renderTypes == null || renderTypes.length == 0) return;
        RandomSource rng = random == null ? level.getRandom() : random;
        ProfileTintParticleType renderType = renderTypes[rng.nextInt(renderTypes.length)];
        spawn(level, rng, renderType, color, x, y, z, xSpeed, ySpeed, zSpeed);
    }
}
