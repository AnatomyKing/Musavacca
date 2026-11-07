package space.anatomyuniverse.musavacca.particle.tinted;

import java.util.Optional;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.Particle;
import org.joml.Quaternionf;
import space.anatomyuniverse.musavacca.tint.PearlFireTintProfiles;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
//? if >=26.1 {
/*import net.minecraft.client.renderer.state.level.QuadParticleRenderState;
*///?} else if >=1.21.9 {
/*import net.minecraft.client.renderer.state.QuadParticleRenderState;
*///?} else {
import com.mojang.blaze3d.vertex.VertexConsumer;
//?}
//? if >=1.21.9 {
/*import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.particles.ParticleLimit;
*///?} else {
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.core.particles.ParticleGroup;
//?}

public final class OriginalCandleParticle extends SingleQuadParticle {
    private final SingleQuadParticle original;
    private final Source source;
    private final int[] colors;
    private TextureAtlasSprite layerSprite;
    //? if <1.21.9 {
    private TextureAtlasSprite sprite;
    //?}

    public OriginalCandleParticle(ClientLevel level, SingleQuadParticle original, TextureAtlasSprite sprite, int color) {
        super(level, 0, 0, 0
                //? if >=1.21.9 {
                /*, sprite
                *///?}
        );
        this.original = original;
        this.source = (Source) original;
        this.source.musavacca$copyTo(this);
        this.colors = ProfileTintSprite.colors(color, PearlFireTintProfiles.PEARL_CANDLE);
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

    public void copyState(double x, double y, double z, double xo, double yo, double zo, float roll, float oRoll, float alpha, int age, int lifetime, TextureAtlasSprite sprite) {
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
        this.sprite = sprite;
    }

    @Override public float getQuadSize(float partialTick) { return this.original.getQuadSize(partialTick); }
    @Override public FacingCameraMode getFacingCameraMode() { return this.original.getFacingCameraMode(); }
    @Override protected float getU0() { return this.remapU(this.source.musavacca$u0()); }
    @Override protected float getU1() { return this.remapU(this.source.musavacca$u1()); }
    @Override protected float getV0() { return this.remapV(this.source.musavacca$v0()); }
    @Override protected float getV1() { return this.remapV(this.source.musavacca$v1()); }

    private float remapU(float u) {
        if (this.layerSprite == null) return u;
        float t = (u - this.sprite.getU0()) / (this.sprite.getU1() - this.sprite.getU0());
        return this.layerSprite.getU0() + t * (this.layerSprite.getU1() - this.layerSprite.getU0());
    }

    private float remapV(float v) {
        if (this.layerSprite == null) return v;
        float t = (v - this.sprite.getV0()) / (this.sprite.getV1() - this.sprite.getV0());
        return this.layerSprite.getV0() + t * (this.layerSprite.getV1() - this.layerSprite.getV0());
    }

    @Override
    //? if >=1.21.9 {
    /*protected void extractRotatedQuad(QuadParticleRenderState output, Quaternionf rotation, float x, float y, float z, float partialTick) {
    *///?} else {
    protected void renderRotatedQuad(VertexConsumer output, Quaternionf rotation, float x, float y, float z, float partialTick) {
    //?}
        TextureAtlasSprite[] layers = ((CandleSpriteLayers.Source) this.sprite.contents()).musavacca$layers();
        try {
            for (int i = 0; i < (layers == null ? 1 : layers.length); i++) {
                this.layerSprite = layers == null ? null : layers[i];
                if (layers != null && this.layerSprite == null) continue;
                ProfileTintSprite.applyColor(this, this.colors[layers == null ? this.colors.length / 2 : i]);
                //? if >=1.21.9 {
                /*super.extractRotatedQuad(output, rotation, x, y, z, partialTick);
                *///?} else {
                super.renderRotatedQuad(output, rotation, x, y, z, partialTick);
                //?}
            }
        } finally {
            this.layerSprite = null;
        }
    }

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
        /^if (this.sprite.atlasLocation().equals(TextureAtlas.LOCATION_ITEMS)) return Layer.TRANSLUCENT_ITEMS;
        return this.sprite.atlasLocation().equals(TextureAtlas.LOCATION_BLOCKS) ? Layer.TRANSLUCENT_TERRAIN : Layer.TRANSLUCENT;
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

    public interface Source {
        Particle musavacca$wrap(int color);
        void musavacca$copyTo(OriginalCandleParticle target);
        float musavacca$u0();
        float musavacca$u1();
        float musavacca$v0();
        float musavacca$v1();
        int musavacca$light(float partialTick);
    }
}
