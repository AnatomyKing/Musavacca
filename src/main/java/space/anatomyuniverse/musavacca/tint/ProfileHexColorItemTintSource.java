package space.anatomyuniverse.musavacca.tint;

//? if >=1.21.4 {
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import space.anatomyuniverse.musavacca.component.ModDataComponents;

import java.util.ArrayList;
import java.util.List;
//?}

public final class ProfileHexColorItemTintSource
        //? if <1.21.4 {
        /*{
         *///?} else {
        implements ItemTintSource {
    //?}

    //? if >=1.21.4 {
    public static final int PASSTHROUGH_LAYER = -1;

    public static final MapCodec<ProfileHexColorItemTintSource> MAP_CODEC =
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Codec.INT.fieldOf("layer").forGetter(ProfileHexColorItemTintSource::layerIndex),
                    Codec.FLOAT.fieldOf("core_to_tail_lightness").forGetter(ProfileHexColorItemTintSource::coreToTailLightness),
                    Codec.FLOAT.fieldOf("color_jumpiness").forGetter(ProfileHexColorItemTintSource::colorJumpiness),
                    Codec.FLOAT.fieldOf("layer_contrast").forGetter(ProfileHexColorItemTintSource::layerContrast),
                    Codec.FLOAT.listOf().fieldOf("gray_factors").forGetter(ProfileHexColorItemTintSource::grayFactors)
            ).apply(instance, ProfileHexColorItemTintSource::new));

    private final int layerIndex;
    private final PearlFireTintProfiles.Profile profile;
    private final List<Float> grayFactors;

    /*
     * Use this for a model layer that needs a tint source entry,
     * but should visually stay unchanged.
     *
     * Example:
     * - ItemTintedMaxLayer5 layer0/base uses tintindex 0.
     * - To make layer1 use tintindex 1, we still need a tint source at index 0.
     * - This passthrough source returns white/no-tint.
     */
    public static ProfileHexColorItemTintSource noTint(PearlFireTintProfiles.Profile profile) {
        return new ProfileHexColorItemTintSource(PASSTHROUGH_LAYER, profile);
    }

    /*
     * Use this for actual tinted profile layers.
     *
     * layerIndex here is the profile layer:
     * - 0 = first profile layer
     * - 1 = second profile layer
     * - etc.
     */
    public static ProfileHexColorItemTintSource of(
            int layerIndex,
            PearlFireTintProfiles.Profile profile
    ) {
        return new ProfileHexColorItemTintSource(layerIndex, profile);
    }

    private ProfileHexColorItemTintSource(
            int layerIndex,
            PearlFireTintProfiles.Profile profile
    ) {
        if (profile == null) {
            throw new IllegalArgumentException("profile must not be null");
        }

        this.layerIndex = layerIndex;
        this.profile = profile;
        this.grayFactors = grayFactorsAsList(profile);
    }

    private ProfileHexColorItemTintSource(
            int layerIndex,
            float coreToTailLightness,
            float colorJumpiness,
            float layerContrast,
            List<Float> grayFactors
    ) {
        this.layerIndex = layerIndex;
        this.grayFactors = sanitizeGrayFactors(grayFactors);
        this.profile = new PearlFireTintProfiles.Profile(
                new PearlFireTintProfiles.Settings(
                        coreToTailLightness,
                        colorJumpiness,
                        layerContrast
                ),
                grayFactorsAsArray(this.grayFactors)
        );
    }

    public int layerIndex() {
        return this.layerIndex;
    }

    private float coreToTailLightness() {
        return this.profile.coreToTailLightness();
    }

    private float colorJumpiness() {
        return this.profile.colorJumpiness();
    }

    private float layerContrast() {
        return this.profile.layerContrast();
    }

    private List<Float> grayFactors() {
        return this.grayFactors;
    }

    @Override
    public int calculate(
            ItemStack stack,
            @Nullable ClientLevel level,
            @Nullable LivingEntity entity
    ) {
        /*
         * Negative layer = passthrough/no visual tint.
         *
         * Returning opaque white keeps the texture unchanged.
         */
        if (this.layerIndex < 0) {
            return TintColorUtil.NO_TINT;
        }

        /*
         * Dynamic item color source.
         *
         * No HEX_COLOR component = no tint.
         * This means the item can exist normally without color until Voco crafting injects one.
         */
        Integer savedHex = stack.get(ModDataComponents.HEX_COLOR.get());
        if (savedHex == null) {
            return TintColorUtil.NO_TINT;
        }

        return PearlFireTintSource.profileTint(
                savedHex,
                this.layerIndex,
                this.profile
        );
    }

    @Override
    public MapCodec<ProfileHexColorItemTintSource> type() {
        return MAP_CODEC;
    }

    private static List<Float> grayFactorsAsList(PearlFireTintProfiles.Profile profile) {
        float[] factors = profile.grayFactors();
        List<Float> list = new ArrayList<>(factors.length);

        for (float factor : factors) {
            list.add(clamp01(factor));
        }

        return List.copyOf(list);
    }

    private static List<Float> sanitizeGrayFactors(List<Float> input) {
        if (input == null || input.isEmpty()) {
            throw new IllegalArgumentException("grayFactors must not be empty");
        }

        List<Float> list = new ArrayList<>(input.size());

        for (Float value : input) {
            list.add(clamp01(value == null ? 1.0F : value));
        }

        return List.copyOf(list);
    }

    private static float[] grayFactorsAsArray(List<Float> input) {
        float[] array = new float[input.size()];

        for (int i = 0; i < input.size(); ++i) {
            array[i] = clamp01(input.get(i));
        }

        return array;
    }

    private static float clamp01(float value) {
        return Math.max(0.0F, Math.min(1.0F, value));
    }
    //?}
}