package space.anatomyuniverse.musavacca.mixin.client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SingleQuadParticle;
//? if >=1.21.9 {
/*import net.minecraft.client.renderer.texture.TextureAtlasSprite;
*///?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import space.anatomyuniverse.musavacca.particle.tinted.CandleParticleSource;
import space.anatomyuniverse.musavacca.particle.tinted.OriginalCandleParticle;

@Mixin(SingleQuadParticle.class)
public abstract class PearlCandleParticleTintMixin extends Particle implements CandleParticleSource {
    //? if >=1.21.9 {
    /*@Shadow protected float alpha;
    @Shadow protected float roll;
    @Shadow protected float oRoll;
    @Shadow protected TextureAtlasSprite sprite;
    *///?}
    @Shadow protected abstract float getU0();
    @Shadow protected abstract float getU1();
    @Shadow protected abstract float getV0();
    @Shadow protected abstract float getV1();

    protected PearlCandleParticleTintMixin(ClientLevel level, double x, double y, double z) {
        super(level, x, y, z);
    }

    @Override
    public OriginalCandleParticle musavacca$wrap() {
        return new OriginalCandleParticle(this.level, (SingleQuadParticle) (Object) this
                //? if >=1.21.9 {
                /*, this.sprite
                *///?}
        );
    }

    @Override
    public void musavacca$copyTo(OriginalCandleParticle target) {
        target.copyState(this.x, this.y, this.z, this.xo, this.yo, this.zo, this.roll, this.oRoll, this.alpha, this.age, this.lifetime
                //? if >=1.21.9 {
                /*, this.sprite
                *///?}
        );
        target.setBoundingBox(this.getBoundingBox());
    }

    @Override public float musavacca$u0() { return this.getU0(); }
    @Override public float musavacca$u1() { return this.getU1(); }
    @Override public float musavacca$v0() { return this.getV0(); }
    @Override public float musavacca$v1() { return this.getV1(); }

    @Override
    public int musavacca$light(float partialTick) {
        //? if >=26.1 {
        /*return this.getLightCoords(partialTick);
        *///?} else {
        return this.getLightColor(partialTick);
        //?}
    }
}
