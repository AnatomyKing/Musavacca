package space.anatomyuniverse.musavacca.particle.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
//? if >=1.21.4 {
import net.minecraft.client.particle.SuspendedParticle;
//?}
//? if >=1.21.9 {
/*import net.minecraft.util.RandomSource;
*///?} else if <1.21.4 {
/*import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
*///?}
//? if <1.21.9 {
import net.minecraft.core.particles.ParticleGroup;
//?}
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import space.anatomyuniverse.musavacca.particle.utils.HexColorParticleOptions;
import java.util.Optional;
    //? if >=1.21.4 {

    public class HexSporeBlossomAirParticle extends SuspendedParticle {
    private final SpriteSet sprites;
    //? if >=1.21.9 {
    /*private static final net.minecraft.core.particles.ParticleLimit SPORE_BLOSSOM_LIMIT = new net.minecraft.core.particles.ParticleLimit(1000);
    *///?}
    //?}
//? if <1.21.4 {
/*public class HexSporeBlossomAirParticle extends TextureSheetParticle {
*///?}
    //? if >=1.21.4 {
    protected HexSporeBlossomAirParticle(ClientLevel level, SpriteSet sprite, double x, double y, double z, HexColorParticleOptions options) {
    //?} else {
    /*private HexSporeBlossomAirParticle(ClientLevel level, SpriteSet sprite, double x, double y, double z, double xd, double yd, double zd) {
    *///?}
        //? if >=1.21.9 {
        /*super(level, x, y, z, 0.0D, -0.8D, 0.0D, sprite.first());
        *///?} else if >=1.21.4 <1.21.9 {
        super(level, sprite, x, y, z, 0.0D, -0.8D, 0.0D);
        //?} else {
        /*super(level, x, y - 0.125D, z, xd, yd, zd);
        this.setSize(0.01F, 0.01F);
        this.pickSprite(sprite);
        this.quadSize *= this.random.nextFloat() * 0.6F + 0.6F;
        this.lifetime = (int) (16.0D / (Math.random() * 0.8D + 0.2D));
        this.hasPhysics = false;
        this.friction = 1.0F;
        *///?}
        //? if >=1.21.4 {
        this.sprites = sprite;
        this.lifetime = Mth.randomBetweenInclusive(level.getRandom(), 500, 1000);
        this.gravity = 0.01F;
        //?} else {
        /*this.gravity = 0.0F;
        *///?}
        //? if >=1.21.4 {
        this.setColor(options.red(), options.green(), options.blue());
        this.setSpriteFromAge(this.sprites);
        //?}
    }

    //? if >=1.21.4 {
    @Override
    public void tick() {
        this.setSpriteFromAge(this.sprites);
        super.tick();
    }
    //?}
    //? if <1.21.4 || >=1.21.9 {
    /*//? if <1.21.4 {
    /^protected HexSporeBlossomAirParticle(ClientLevel level, SpriteSet sprite, double x, double y, double z, HexColorParticleOptions options) {
        this(level, sprite, x, y, z, 0.0D, -0.8D, 0.0D);
        this.lifetime = Mth.randomBetweenInclusive(level.getRandom(), 500, 1000);
        this.gravity = 0.01F;
        this.setColor(options.red(), options.green(), options.blue());
    }
    ^///?}
    //? if >=1.21.9 {
    /^@Override
    public Optional<net.minecraft.core.particles.ParticleLimit> getParticleLimit() {
        return Optional.of(SPORE_BLOSSOM_LIMIT);
    }
    ^///?}
    //? if <1.21.4 {
    /^@Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }
    ^///?}
    *///?}
    //? if <1.21.9 {
    @Override
    public Optional<ParticleGroup> getParticleGroup() {
        return Optional.of(ParticleGroup.SPORE_BLOSSOM);
    }
    //?}
    public static final class Provider implements ParticleProvider<HexColorParticleOptions> {
        private final SpriteSet sprites;
        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        private Particle createInternal(HexColorParticleOptions options, ClientLevel level, double x, double y, double z) {
            return new HexSporeBlossomAirParticle(level, this.sprites, x, y, z, options);
        }

        @Override
        public @Nullable Particle createParticle(HexColorParticleOptions options, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed,
                //? if >=1.21.9 {
                /*double zSpeed, RandomSource random) {
                *///?} else {
                double zSpeed) {
                //?}
            return this.createInternal(options, level, x, y, z);
        }
    }
}
