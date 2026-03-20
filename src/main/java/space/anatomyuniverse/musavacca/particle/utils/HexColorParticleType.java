package space.anatomyuniverse.musavacca.particle.utils;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public final class HexColorParticleType extends ParticleType<HexColorParticleOptions> {
    public HexColorParticleType(boolean overrideLimiter) {
        super(overrideLimiter);
    }

    @Override
    public MapCodec<HexColorParticleOptions> codec() {
        return Codec.INT
                .fieldOf("color")
                .xmap(
                        color -> new HexColorParticleOptions(this, color),
                        HexColorParticleOptions::color
                );
    }

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, HexColorParticleOptions> streamCodec() {
        return StreamCodec.composite(
                ByteBufCodecs.INT,
                HexColorParticleOptions::color,
                color -> new HexColorParticleOptions(this, color)
        );
    }
}