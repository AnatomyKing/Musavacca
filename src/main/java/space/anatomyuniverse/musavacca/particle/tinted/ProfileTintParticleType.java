// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/particle/tinted/ProfileTintParticleType.java
package space.anatomyuniverse.musavacca.particle.tinted;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import space.anatomyuniverse.musavacca.tint.PearlFireTintProfiles;

import java.util.function.Supplier;

public final class ProfileTintParticleType extends ParticleType<ProfileTintParticleOptions> {
    private final Supplier<PearlFireTintProfiles.Profile> profile;

    public ProfileTintParticleType(
            boolean overrideLimiter,
            Supplier<PearlFireTintProfiles.Profile> profile
    ) {
        super(overrideLimiter);

        if (profile == null) {
            throw new IllegalArgumentException("profile must not be null");
        }

        this.profile = profile;
    }

    public PearlFireTintProfiles.Profile profile() {
        PearlFireTintProfiles.Profile resolved = this.profile.get();

        if (resolved == null) {
            throw new IllegalStateException("profile supplier returned null");
        }

        return resolved;
    }

    public int layerCount() {
        return Math.max(1, this.profile().layerCount());
    }

    @Override
    public MapCodec<ProfileTintParticleOptions> codec() {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.INT.fieldOf("color").forGetter(ProfileTintParticleOptions::color),
                Codec.INT.optionalFieldOf("layer", 0).forGetter(ProfileTintParticleOptions::layer),
                Codec.INT.optionalFieldOf("layer_count", 1).forGetter(ProfileTintParticleOptions::layerCount),
                Codec.INT.optionalFieldOf("seed", 0).forGetter(ProfileTintParticleOptions::seed)
        ).apply(instance, (color, layer, layerCount, seed) ->
                new ProfileTintParticleOptions(
                        this,
                        color,
                        layer,
                        layerCount,
                        seed
                )
        ));
    }

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, ProfileTintParticleOptions> streamCodec() {
        return StreamCodec.composite(
                ByteBufCodecs.INT,
                ProfileTintParticleOptions::color,
                ByteBufCodecs.INT,
                ProfileTintParticleOptions::layer,
                ByteBufCodecs.INT,
                ProfileTintParticleOptions::layerCount,
                ByteBufCodecs.INT,
                ProfileTintParticleOptions::seed,
                (color, layer, layerCount, seed) ->
                        new ProfileTintParticleOptions(
                                this,
                                color,
                                layer,
                                layerCount,
                                seed
                        )
        );
    }
}