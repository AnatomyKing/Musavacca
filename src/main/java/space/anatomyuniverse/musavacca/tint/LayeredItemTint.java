package space.anatomyuniverse.musavacca.tint;

import net.minecraft.world.item.ItemStack;
import space.anatomyuniverse.musavacca.component.ModDataComponents;

/**
 * Exact tint-index -> authored profile-layer mapping shared by all versions.
 * Every gray factor is a real tinted layer; nothing is sampled or discarded.
 */
public final class LayeredItemTint {
    public static final int VANILLA_MAX_MODEL_LAYERS = 5;

    private LayeredItemTint() {}

    public static int totalModelLayers(
            PearlFireTintProfiles.Profile profile,
            boolean untintedBase
    ) {
        return profile == null
                ? 0
                : profile.layerCount() + (untintedBase ? 1 : 0);
    }

    public static boolean usesVanillaModelLayers(
            PearlFireTintProfiles.Profile profile,
            boolean untintedBase
    ) {
        int count = totalModelLayers(profile, untintedBase);
        return count > 0 && count <= VANILLA_MAX_MODEL_LAYERS;
    }

    public static boolean isUntintedBase(
            int modelLayer,
            boolean untintedBase
    ) {
        return untintedBase && modelLayer == 0;
    }

    public static int profileLayerForTintIndex(
            PearlFireTintProfiles.Profile profile,
            boolean untintedBase,
            int tintIndex
    ) {
        if (profile == null
                || tintIndex < 0
                || isUntintedBase(tintIndex, untintedBase)) {
            return -1;
        }

        int profileLayer = tintIndex - (untintedBase ? 1 : 0);
        return profile.supports(profileLayer) ? profileLayer : -1;
    }

    public static int tint(
            ItemStack stack,
            int tintIndex,
            PearlFireTintProfiles.Profile profile,
            boolean untintedBase
    ) {
        int profileLayer = profileLayerForTintIndex(
                profile,
                untintedBase,
                tintIndex
        );

        return profileLayer < 0
                ? TintColorUtil.NO_TINT
                : tintProfileLayer(stack, profileLayer, profile);
    }

    public static int tintProfileLayer(
            ItemStack stack,
            int profileLayer,
            PearlFireTintProfiles.Profile profile
    ) {
        if (stack == null
                || !PearlFireTintSource.supportsLayer(profile, profileLayer)) {
            return TintColorUtil.NO_TINT;
        }

        Integer savedHex = stack.get(ModDataComponents.HEX_COLOR.get());

        return savedHex == null
                ? TintColorUtil.NO_TINT
                : PearlFireTintSource.profileTint(
                        savedHex,
                        profileLayer,
                        profile
                );
    }
}
