package space.anatomyuniverse.musavacca.mixin.client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
//? if <1.21.9 {
import net.minecraft.client.particle.TextureSheetParticle;
//?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import space.anatomyuniverse.musavacca.particle.tinted.OriginalCandleParticle;

//? if >=1.21.9 {
/*@Mixin(SingleQuadParticle.class)
*///?} else {
@Mixin(TextureSheetParticle.class)
//?}
public abstract class PearlCandleParticleTintMixin extends Particle implements OriginalCandleParticle.Source {
    //? if >=1.21.9 {
    /*@Shadow protected float alpha;
    @Shadow protected float roll;
    @Shadow protected float oRoll;
    *///?}
    @Shadow protected TextureAtlasSprite sprite;
    @Shadow protected abstract float getU0();
    @Shadow protected abstract float getU1();
    @Shadow protected abstract float getV0();
    @Shadow protected abstract float getV1();

    protected PearlCandleParticleTintMixin(ClientLevel level, double x, double y, double z) {
        super(level, x, y, z);
    }

    @Override
    public Particle musavacca$wrap(int color) {
        return this.sprite == null ? this
                : new OriginalCandleParticle(this.level, (SingleQuadParticle) (Object) this, this.sprite, color);
    }

    @Override
    public void musavacca$copyTo(OriginalCandleParticle target) {
        target.copyState(this.x, this.y, this.z, this.xo, this.yo, this.zo, this.roll, this.oRoll, this.alpha, this.age, this.lifetime, this.sprite);
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
