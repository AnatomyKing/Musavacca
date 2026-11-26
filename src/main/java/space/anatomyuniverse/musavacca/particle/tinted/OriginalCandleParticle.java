package space.anatomyuniverse.musavacca.particle.tinted;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.joml.Quaternionf;
import space.anatomyuniverse.musavacca.tint.MusavaccaTints;
import space.anatomyuniverse.musavacca.particle.tinted.OriginalCandleParticles.Context;

import java.util.Optional;

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
    private final Context context;
    private TextureAtlasSprite paletteSprite;
    private TextureAtlasSprite[] layers;
    private int[] colors;
    private boolean rendering;
    private float renderU0, renderU1, renderV0, renderV1, renderSize;
    private int renderLight;
    //? if <1.21.9 {
    private TextureAtlasSprite sprite;
    //?}

    public OriginalCandleParticle(ClientLevel level, SingleQuadParticle original, TextureAtlasSprite sprite, Context context) {
        super(level, 0, 0, 0
                //? if >=1.21.9 {
                /*, sprite
                *///?}
        );
        this.original = original;
        this.source = (Source) original;
        this.source.musavacca$copyTo(this);
        this.context = context;
    }

    private void updatePalette() {
        if (paletteSprite == sprite) return;
        paletteSprite = sprite;
        var data = (Object) sprite.contents() instanceof CandleSpriteLayers.Source source ? source.musavacca$layers() : null;
        layers = data == null ? null : data.sprites();
        colors = layers == null ? null : CandleSpriteLayers.palette(context, data.tones());
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

    public void copyState(
            double x,
            double y,
            double z,
            double xo,
            double yo,
            double zo,
            float roll,
            float oRoll,
            float alpha,
            int age,
            int lifetime,
            TextureAtlasSprite sprite) {
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

    @Override
    public float getQuadSize(float partialTick) {
        return this.rendering ? this.renderSize : this.original.getQuadSize(partialTick);
    }

    @Override
    public FacingCameraMode getFacingCameraMode() {
        return this.original.getFacingCameraMode();
    }

    @Override
    protected float getU0() {
        return this.rendering ? this.renderU0 : this.source.musavacca$u0();
    }

    @Override
    protected float getU1() {
        return this.rendering ? this.renderU1 : this.source.musavacca$u1();
    }

    @Override
    protected float getV0() {
        return this.rendering ? this.renderV0 : this.source.musavacca$v0();
    }

    @Override
    protected float getV1() {
        return this.rendering ? this.renderV1 : this.source.musavacca$v1();
    }

    @Override
    //? if >=1.21.9 {
    /*protected void extractRotatedQuad(QuadParticleRenderState output, Quaternionf rotation, float x, float y, float z, float partialTick) {
    *///?} else {
    protected void renderRotatedQuad(VertexConsumer output, Quaternionf rotation, float x, float y, float z, float partialTick) {
    //?}
        updatePalette();
        float u0 = this.source.musavacca$u0(), u1 = this.source.musavacca$u1();
        float v0 = this.source.musavacca$v0(), v1 = this.source.musavacca$v1();
        if (layers != null) {
            float minU = this.sprite.getU0(), minV = this.sprite.getV0();
            float width = this.sprite.getU1() - minU, height = this.sprite.getV1() - minV;
            u0 = (u0 - minU) / width;
            u1 = (u1 - minU) / width;
            v0 = (v0 - minV) / height;
            v1 = (v1 - minV) / height;
        } else {
            this.renderU0 = u0;
            this.renderU1 = u1;
            this.renderV0 = v0;
            this.renderV1 = v1;
        }
        this.renderSize = this.original.getQuadSize(partialTick);
        this.renderLight = this.source.musavacca$light(partialTick);
        this.rendering = true;
        try {
            for (int i = 0; i < (layers == null ? 1 : layers.length); i++) {
                if (layers != null) {
                    TextureAtlasSprite layer = layers[i];
                    if (layer == null) continue;
                    float minU = layer.getU0(), minV = layer.getV0();
                    float width = layer.getU1() - minU, height = layer.getV1() - minV;
                    this.renderU0 = minU + u0 * width;
                    this.renderU1 = minU + u1 * width;
                    this.renderV0 = minV + v0 * height;
                    this.renderV1 = minV + v1 * height;
                }
                int rgb = layers == null ? MusavaccaTints.rgb(context.color()) : colors[i];
                this.setColor(((rgb >>> 16) & 255) / 255.0F, ((rgb >>> 8) & 255) / 255.0F, (rgb & 255) / 255.0F);
                //? if >=1.21.9 {
                /*super.extractRotatedQuad(output, rotation, x, y, z, partialTick);
                *///?} else {
                super.renderRotatedQuad(output, rotation, x, y, z, partialTick);
                //?}
            }
        } finally {
            this.rendering = false;
        }
    }

    @Override
    //? if >=26.1 {
    /*protected int getLightCoords(float partialTick) {
    *///?} else {
    protected int getLightColor(float partialTick) {
    //?}
        return this.rendering ? this.renderLight : this.source.musavacca$light(partialTick);
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
                ? ParticleRenderType.TERRAIN_SHEET
                : ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public Optional<ParticleGroup> getParticleGroup() {
        return this.original.getParticleGroup();
    }
    //?}

    public interface Source {
        Particle musavacca$wrap(Context context);

        void musavacca$copyTo(OriginalCandleParticle target);

        float musavacca$u0();

        float musavacca$u1();

        float musavacca$v0();

        float musavacca$v1();

        int musavacca$light(float partialTick);
    }
}
