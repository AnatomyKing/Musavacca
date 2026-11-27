package space.anatomyuniverse.musavacca.particle.tinted;

import net.minecraft.client.particle.Particle;
import org.jetbrains.annotations.Nullable;
import space.anatomyuniverse.musavacca.tint.MusavaccaTints;
import space.anatomyuniverse.musavacca.tint.PearlFireTintProfiles.Settings;
import space.anatomyuniverse.musavacca.tint.SpriteTintLayers;

import java.util.Objects;
import java.util.function.Predicate;

public record ParticleTint(int color, Settings settings, Predicate<Particle> filter) {
    public ParticleTint {
        Objects.requireNonNull(settings);
        Objects.requireNonNull(filter);
    }

    @Nullable
    public static ParticleTint of(int color, Settings settings, Predicate<Particle> filter) {
        return MusavaccaTints.hasTint(color) ? new ParticleTint(color, settings, filter) : null;
    }

    public boolean matches(Particle particle) {
        return filter.test(particle);
    }

    public int[] palette(SpriteTintLayers.Tones tones) {
        return SpriteTintLayers.palette(color, settings, tones);
    }

    public int rgb() {
        return MusavaccaTints.rgb(color);
    }
}
