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

    public static final MapCodec<
            ProfileHexColorItemTintSource
            > MAP_CODEC =
            RecordCodecBuilder.mapCodec(
                    instance -> instance.group(
                            Codec.INT
                                    .fieldOf("layer")
                                    .forGetter(
                                            ProfileHexColorItemTintSource
                                                    ::layerIndex
                                    ),
                            Codec.FLOAT
                                    .fieldOf(
                                            "core_to_tail_lightness"
                                    )
                                    .forGetter(
                                            ProfileHexColorItemTintSource
                                                    ::coreToTailLightness
                                    ),
                            Codec.FLOAT
                                    .fieldOf(
                                            "color_jumpiness"
                                    )
                                    .forGetter(
                                            ProfileHexColorItemTintSource
                                                    ::colorJumpiness
                                    ),
                            Codec.FLOAT
                                    .fieldOf(
                                            "color_amount_take_over"
                                    )
                                    .forGetter(
                                            ProfileHexColorItemTintSource
                                                    ::colorAmountTakeOver
                                    ),
                            Codec.FLOAT
                                    .optionalFieldOf(
                                            "vibrancy_darkening",
                                            0.0F
                                    )
                                    .forGetter(
                                            ProfileHexColorItemTintSource
                                                    ::vibrancyDarkening
                                    ),
                            Codec.FLOAT
                                    .fieldOf(
                                            "layer_contrast"
                                    )
                                    .forGetter(
                                            ProfileHexColorItemTintSource
                                                    ::layerContrast
                                    ),
                            Codec.FLOAT
                                    .listOf()
                                    .fieldOf("gray_factors")
                                    .forGetter(
                                            ProfileHexColorItemTintSource
                                                    ::grayFactors
                                    ),
                            Codec.BOOL
                                    .fieldOf("foil_carrier")
                                    .forGetter(
                                            ProfileHexColorItemTintSource
                                                    ::isFoilCarrier
                                    )
                    ).apply(
                            instance,
                            ProfileHexColorItemTintSource::new
                    )
            );

    private final int layerIndex;

    private final PearlFireTintProfiles.Profile
            profile;

    private final List<Float> grayFactors;

    private final boolean foilCarrier;

    public static ProfileHexColorItemTintSource
    noTint(
            PearlFireTintProfiles.Profile profile,
            boolean foilCarrier
    ) {
        return new ProfileHexColorItemTintSource(
                PASSTHROUGH_LAYER,
                profile,
                foilCarrier
        );
    }

    public static ProfileHexColorItemTintSource of(
            int layerIndex,
            PearlFireTintProfiles.Profile profile,
            boolean foilCarrier
    ) {
        return new ProfileHexColorItemTintSource(
                layerIndex,
                profile,
                foilCarrier
        );
    }

    private ProfileHexColorItemTintSource(
            int layerIndex,
            PearlFireTintProfiles.Profile profile,
            boolean foilCarrier
    ) {
        if (profile == null) {
            throw new IllegalArgumentException(
                    "profile must not be null"
            );
        }

        this.layerIndex = layerIndex;
        this.profile = profile;

        this.grayFactors =
                grayFactorsAsList(profile);

        this.foilCarrier = foilCarrier;
    }

    private ProfileHexColorItemTintSource(
            int layerIndex,
            float coreToTailLightness,
            float colorJumpiness,
            float colorAmountTakeOver,
            float vibrancyDarkening,
            float layerContrast,
            List<Float> grayFactors,
            boolean foilCarrier
    ) {
        this.layerIndex = layerIndex;

        this.grayFactors =
                sanitizeGrayFactors(grayFactors);

        this.foilCarrier = foilCarrier;

        this.profile =
                new PearlFireTintProfiles.Profile(
                        new PearlFireTintProfiles.Settings(
                                coreToTailLightness,
                                colorJumpiness,
                                colorAmountTakeOver,
                                vibrancyDarkening,
                                layerContrast
                        ),
                        grayFactorsAsArray(
                                this.grayFactors
                        )
                );
    }

    public int layerIndex() {
        return this.layerIndex;
    }

    public boolean isFoilCarrier() {
        return this.foilCarrier;
    }

    private float coreToTailLightness() {
        return this.profile
                .coreToTailLightness();
    }

    private float colorJumpiness() {
        return this.profile
                .colorJumpiness();
    }

    private float colorAmountTakeOver() {
        return this.profile
                .colorAmountTakeOver();
    }

    private float vibrancyDarkening() {
        return this.profile
                .vibrancyDarkening();
    }

    private float layerContrast() {
        return this.profile
                .layerContrast();
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
        if (this.layerIndex < 0) {
            return TintColorUtil.NO_TINT;
        }

        Integer savedHex =
                stack.get(
                        ModDataComponents
                                .HEX_COLOR
                                .get()
                );

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
    public MapCodec<
            ProfileHexColorItemTintSource
            > type() {
        return MAP_CODEC;
    }

    private static List<Float>
    grayFactorsAsList(
            PearlFireTintProfiles.Profile profile
    ) {
        float[] factors = profile.grayFactors();

        List<Float> list =
                new ArrayList<>(factors.length);

        for (float factor : factors) {
            list.add(clamp01(factor));
        }

        return List.copyOf(list);
    }

    private static List<Float>
    sanitizeGrayFactors(
            List<Float> input
    ) {
        if (input == null || input.isEmpty()) {
            throw new IllegalArgumentException(
                    "grayFactors must not be empty"
            );
        }

        List<Float> list =
                new ArrayList<>(input.size());

        for (Float value : input) {
            if (value == null) {
                throw new IllegalArgumentException(
                        "grayFactors must not contain null"
                );
            }

            list.add(clamp01(value));
        }

        return List.copyOf(list);
    }

    private static float[] grayFactorsAsArray(
            List<Float> input
    ) {
        float[] array =
                new float[input.size()];

        for (
                int index = 0;
                index < input.size();
                ++index
        ) {
            array[index] =
                    clamp01(input.get(index));
        }

        return array;
    }

    private static float clamp01(float value) {
        return Math.max(
                0.0F,
                Math.min(1.0F, value)
        );
    }
    //?}
}