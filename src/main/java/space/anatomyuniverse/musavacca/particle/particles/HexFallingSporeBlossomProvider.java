package space.anatomyuniverse.musavacca.particle.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.DripParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.ParticleTypes;
import org.jetbrains.annotations.Nullable;
import space.anatomyuniverse.musavacca.particle.utils.HexColorParticleOptions;

public final class HexFallingSporeBlossomProvider implements ParticleProvider<HexColorParticleOptions> {
    private final SpriteSet sprites;

    public HexFallingSporeBlossomProvider(SpriteSet sprites) {
        this.sprites = sprites;
    }

    @Override
    public @Nullable Particle createParticle(
            HexColorParticleOptions options,
            ClientLevel level,
            double x,
            double y,
            double z,
            double xSpeed,
            double ySpeed,
            double zSpeed
    ) {
        TextureSheetParticle particle = DripParticle.createSporeBlossomFallParticle(
                ParticleTypes.FALLING_SPORE_BLOSSOM,
                level,
                x,
                y,
                z,
                xSpeed,
                ySpeed,
                zSpeed
        );

        particle.pickSprite(this.sprites);
        particle.setColor(options.red(), options.green(), options.blue());
        return particle;
    }
}