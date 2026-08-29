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
//?}

/**
 * 1.21.4+ bridge from Minecraft's ItemTintSource system into the same exact
 * layer calculation used by pre-1.21.4 ItemColors.
 */
public final class ProfileHexColorItemTintSource
        //? if <1.21.4 {
        /*{
         *///?} else {
        implements ItemTintSource {
    //?}

    //? if >=1.21.4 {
    public static final MapCodec<ProfileHexColorItemTintSource> MAP_CODEC =
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Codec.STRING.fieldOf("profile")
                            .forGetter(ProfileHexColorItemTintSource::profileId),
                    Codec.INT.fieldOf("layer")
                            .forGetter(ProfileHexColorItemTintSource::layerIndex),
                    Codec.BOOL.optionalFieldOf("foil_carrier", true)
                            .forGetter(ProfileHexColorItemTintSource::isFoilCarrier)
            ).apply(instance, ProfileHexColorItemTintSource::new));

    private final String profileId;
    private final int layerIndex;
    private final boolean foilCarrier;
    private final PearlFireTintProfiles.Profile profile;

    public ProfileHexColorItemTintSource(
            String profileId,
            int layerIndex,
            boolean foilCarrier
    ) {
        this.profileId = profileId;
        this.profile = PearlFireTintProfiles.byId(profileId);
        this.layerIndex = layerIndex;
        this.foilCarrier = foilCarrier;

        if (!this.profile.supports(layerIndex)) {
            throw new IllegalArgumentException(
                    "Profile '" + profileId
                            + "' does not contain layer "
                            + layerIndex
            );
        }
    }

    public static ProfileHexColorItemTintSource of(
            PearlFireTintProfiles.Profile profile,
            int profileLayer,
            boolean foilCarrier
    ) {
        return new ProfileHexColorItemTintSource(
                PearlFireTintProfiles.idOf(profile),
                profileLayer,
                foilCarrier
        );
    }

    public String profileId() {
        return profileId;
    }

    public int layerIndex() {
        return layerIndex;
    }

    public boolean isFoilCarrier() {
        return foilCarrier;
    }

    @Override
    public int calculate(
            ItemStack stack,
            @Nullable ClientLevel level,
            @Nullable LivingEntity entity
    ) {
        return LayeredItemTint.tintProfileLayer(
                stack,
                layerIndex,
                profile
        );
    }

    @Override
    public MapCodec<ProfileHexColorItemTintSource> type() {
        return MAP_CODEC;
    }
    //?}
}
