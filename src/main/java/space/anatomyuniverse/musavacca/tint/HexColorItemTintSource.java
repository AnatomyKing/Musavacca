package space.anatomyuniverse.musavacca.tint;

import net.minecraft.world.item.ItemStack;
import space.anatomyuniverse.musavacca.component.ModDataComponents;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

//? if >=1.21.4 {
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
//?}

/**
 * The one dynamic HEX item tint source used by Musavacca.
 *
 * <p>Raw mode simply returns the ItemStack's HEX_COLOR. PearlFire mode reads
 * that exact same HEX_COLOR and derives one requested profile layer through
 * {@link PearlFireTintSource}. No mirrored render component or cached palette
 * is stored on the stack.</p>
 */
public final class HexColorItemTintSource
        //? if <1.21.4 {
        /*{
         *///?} else {
        implements ItemTintSource {
    //?}

    /** Plain dynamic HEX_COLOR mode. */
    public static final HexColorItemTintSource INSTANCE =
            new HexColorItemTintSource(null, 0);

    private final PearlFireTintProfiles.Profile profile;
    private final int layer;

    //? if >=1.21.4 {
    private static final Codec<PearlFireTintProfiles.Profile> PROFILE_CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    Codec.FLOAT.fieldOf("core_to_tail_lightness")
                            .forGetter(PearlFireTintProfiles.Profile::coreToTailLightness),
                    Codec.FLOAT.fieldOf("color_jumpiness")
                            .forGetter(PearlFireTintProfiles.Profile::colorJumpiness),
                    Codec.FLOAT.fieldOf("color_amount_take_over")
                            .forGetter(PearlFireTintProfiles.Profile::colorAmountTakeOver),
                    Codec.FLOAT.fieldOf("vibrancy_darkening")
                            .forGetter(PearlFireTintProfiles.Profile::vibrancyDarkening),
                    Codec.FLOAT.fieldOf("layer_contrast")
                            .forGetter(PearlFireTintProfiles.Profile::layerContrast),
                    Codec.FLOAT.listOf().fieldOf("gray_factors")
                            .forGetter(profile -> boxed(profile.grayFactors()))
            ).apply(instance, HexColorItemTintSource::decodeProfile));

    /**
     * One codec/type handles both raw HEX and profiled PearlFire layers.
     * Raw output serializes without a profile; profiled output carries only
     * the profile data plus the one requested layer number.
     */
    public static final MapCodec<HexColorItemTintSource> MAP_CODEC =
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                    PROFILE_CODEC.optionalFieldOf("profile")
                            .forGetter(source -> Optional.ofNullable(source.profile)),
                    Codec.INT.optionalFieldOf("layer", 0)
                            .forGetter(HexColorItemTintSource::layer)
            ).apply(instance, HexColorItemTintSource::decode));
    //?}

    private HexColorItemTintSource(
            PearlFireTintProfiles.Profile profile,
            int layer
    ) {
        if (layer < 0) {
            throw new IllegalArgumentException("layer must be >= 0");
        }

        if (profile != null && !profile.supports(layer)) {
            throw new IllegalArgumentException(
                    "PearlFire item layer " + layer
                            + " is outside profile layer count " + profile.layerCount()
            );
        }

        this.profile = profile;
        this.layer = layer;
    }

    /** Creates one dynamic PearlFire layer backed by the stack's HEX_COLOR. */
    public static HexColorItemTintSource pearlFire(
            PearlFireTintProfiles.Profile profile,
            int layer
    ) {
        return new HexColorItemTintSource(
                Objects.requireNonNull(profile, "profile"),
                layer
        );
    }

    public int layer() {
        return layer;
    }

    /** Plain dynamic HEX_COLOR, used by SIM card and other one-color items. */
    public static int color(ItemStack stack) {
        Integer savedHex = stack.get(ModDataComponents.HEX_COLOR.get());

        return TintColorUtil.opaqueRgb(
                savedHex != null
                        ? savedHex
                        : TintColorUtil.defaultHexBlockItemTint()
        );
    }

    /**
     * Dynamic PearlFire layer: same stored HEX_COLOR, same shared profile math
     * used by blocks, but with this item's requested physical layer.
     */
    public static int color(
            ItemStack stack,
            PearlFireTintProfiles.Profile profile,
            int layer
    ) {
        if (profile == null || !profile.supports(layer)) {
            return TintColorUtil.NO_TINT;
        }

        return PearlFireTintSource.profileTint(
                color(stack),
                layer,
                profile
        );
    }

    //? if >=1.21.4 {
    @Override
    public int calculate(
            ItemStack stack,
            @Nullable ClientLevel level,
            @Nullable LivingEntity entity
    ) {
        return profile == null
                ? color(stack)
                : color(stack, profile, layer);
    }

    @Override
    public MapCodec<HexColorItemTintSource> type() {
        return MAP_CODEC;
    }

    private static HexColorItemTintSource decode(
            Optional<PearlFireTintProfiles.Profile> profile,
            int layer
    ) {
        return profile
                .map(value -> pearlFire(value, layer))
                .orElse(INSTANCE);
    }

    private static PearlFireTintProfiles.Profile decodeProfile(
            float coreToTailLightness,
            float colorJumpiness,
            float colorAmountTakeOver,
            float vibrancyDarkening,
            float layerContrast,
            List<Float> grayFactors
    ) {
        if (grayFactors == null || grayFactors.isEmpty()) {
            throw new IllegalArgumentException("gray_factors must not be empty");
        }

        float[] grays = new float[grayFactors.size()];
        for (int i = 0; i < grayFactors.size(); i++) {
            grays[i] = grayFactors.get(i);
        }

        return new PearlFireTintProfiles.Profile(
                new PearlFireTintProfiles.Settings(
                        coreToTailLightness,
                        colorJumpiness,
                        colorAmountTakeOver,
                        vibrancyDarkening,
                        layerContrast
                ),
                grays
        );
    }

    private static List<Float> boxed(float[] values) {
        java.util.ArrayList<Float> result = new java.util.ArrayList<>(values.length);
        for (float value : values) result.add(value);
        return List.copyOf(result);
    }
    //?}
}
