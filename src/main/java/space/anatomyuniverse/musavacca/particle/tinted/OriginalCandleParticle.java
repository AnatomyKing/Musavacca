package space.anatomyuniverse.musavacca.particle.tinted;

import java.util.Optional;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SingleQuadParticle;
//? if >=1.21.9 {
/*import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.ParticleLimit;
*///?} else {
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.core.particles.ParticleGroup;
//?}

public final class OriginalCandleParticle extends SingleQuadParticle {
    private final SingleQuadParticle original;
    private final CandleParticleSource source;

    public OriginalCandleParticle(ClientLevel level, SingleQuadParticle original
            //? if >=1.21.9 {
            /*, TextureAtlasSprite sprite
            *///?}
    ) {
        super(level, 0, 0, 0
                //? if >=1.21.9 {
                /*, sprite
                *///?}
        );
        this.original = original;
        this.source = (CandleParticleSource) original;
        this.source.musavacca$copyTo(this);
        this.setColor(0.0F, 1.0F, 0.0F);
    }

    @Override
    public void tick() {
        this.original.tick();
        if (!this.original.isAlive()) {
            this.remove();
            return;
        }
        this.source.musavacca$copyTo(this);
    }

    @Override
    public void remove() {
        super.remove();
        this.original.remove();
    }

    public void copyState(double x, double y, double z, double xo, double yo, double zo, float roll, float oRoll, float alpha, int age, int lifetime
            //? if >=1.21.9 {
            /*, TextureAtlasSprite sprite
            *///?}
    ) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.xo = xo;
        this.yo = yo;
        this.zo = zo;
        this.roll = roll;
        this.oRoll = oRoll;
        this.alpha = alpha;
        this.age = age;
        this.lifetime = lifetime;
        //? if >=1.21.9 {
        /*this.sprite = sprite;
        *///?}
    }

    @Override public float getQuadSize(float partialTick) { return this.original.getQuadSize(partialTick); }
    @Override public FacingCameraMode getFacingCameraMode() { return this.original.getFacingCameraMode(); }
    @Override protected float getU0() { return this.source.musavacca$u0(); }
    @Override protected float getU1() { return this.source.musavacca$u1(); }
    @Override protected float getV0() { return this.source.musavacca$v0(); }
    @Override protected float getV1() { return this.source.musavacca$v1(); }

    @Override
    //? if >=26.1 {
    /*protected int getLightCoords(float partialTick) {
    *///?} else {
    protected int getLightColor(float partialTick) {
    //?}
        return this.source.musavacca$light(partialTick);
    }

    //? if >=1.21.9 {
    /*@Override
    protected Layer getLayer() {
        //? if >=26.1 {
        /^return Layer.bySprite(this.sprite);
        ^///?} else {
        //? if >=1.21.11 {
        /^if (this.sprite.atlasLocation().equals(TextureAtlas.LOCATION_ITEMS)) return Layer.ITEMS;
        ^///?}
        return this.sprite.atlasLocation().equals(TextureAtlas.LOCATION_BLOCKS) ? Layer.TERRAIN : Layer.TRANSLUCENT;
        //?}
    }

    @Override
    public Optional<ParticleLimit> getParticleLimit() {
        return this.original.getParticleLimit();
    }
    *///?} else {
    @Override
    public ParticleRenderType getRenderType() {
        return this.original.getRenderType() == ParticleRenderType.TERRAIN_SHEET
                ? ParticleRenderType.TERRAIN_SHEET : ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public Optional<ParticleGroup> getParticleGroup() {
        return this.original.getParticleGroup();
    }
    //?}
}
