package space.anatomyuniverse.musavacca.data.models.unified;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import space.anatomyuniverse.musavacca.component.HexColorComponent;
import space.anatomyuniverse.musavacca.tint.BlockTintResolvers;
import space.anatomyuniverse.musavacca.tint.PearlFireTintProfiles;
import space.anatomyuniverse.musavacca.tint.PearlFireTintSource;
import space.anatomyuniverse.musavacca.tint.TintColorUtil;

import java.util.ArrayList;
import java.util.List;

public final class PearlTint implements ItemTint {
    public static final int VANILLA_MAX_TOTAL_LAYERS = 5;

    private final PearlFireTintProfiles.Profile profile;
    private final int offset;
    private final boolean baseLayer;
    private final boolean itemSources;
    private final int fallbackRgb;
    private final String itemHexSlot;
    private final BlockTintColorResolver blockResolver;

    private PearlTint(
            PearlFireTintProfiles.Profile profile,
            int offset,
            boolean baseLayer,
            boolean itemSources,
            int fallbackRgb,
            String itemHexSlot,
            BlockTintColorResolver blockResolver
    ) {
        if (profile == null) {
            throw new IllegalArgumentException("PearlTint profile cannot be null.");
        }

        this.profile = profile;
        this.offset = Math.max(0, offset);
        this.baseLayer = baseLayer;
        this.itemSources = itemSources;
        this.fallbackRgb = TintColorUtil.rgb(fallbackRgb);
        this.itemHexSlot = itemHexSlot == null || itemHexSlot.isBlank() ? null : HexColorComponent.cleanSlot(itemHexSlot);
        this.blockResolver = blockResolver;
    }

    public static PearlTint dynamic(PearlFireTintProfiles.Profile profile) {
        return new PearlTint(profile, 0, true, true, TintColorUtil.defaultHexItemTint(), null, null);
    }

    public static PearlTint block(PearlFireTintProfiles.Profile profile) {
        return new PearlTint(profile, 0, false, false, TintColorUtil.defaultHexItemTint(), null, null);
    }

    public PearlTint offset(int offset) {
        return new PearlTint(profile, offset, baseLayer, itemSources, fallbackRgb, itemHexSlot, blockResolver);
    }

    public PearlTint noBaseLayer() {
        return new PearlTint(profile, offset, false, itemSources, fallbackRgb, itemHexSlot, blockResolver);
    }

    public PearlTint withBaseLayer() {
        return new PearlTint(profile, offset, true, itemSources, fallbackRgb, itemHexSlot, blockResolver);
    }

    public PearlTint asItemTint() {
        return new PearlTint(profile, offset, baseLayer, true, fallbackRgb, itemHexSlot, blockResolver);
    }

    public PearlTint asBlockTintOnly() {
        return new PearlTint(profile, offset, baseLayer, false, fallbackRgb, itemHexSlot, blockResolver);
    }

    public PearlTint resolver(BlockTintColorResolver blockResolver) {
        return new PearlTint(profile, offset, baseLayer, itemSources, fallbackRgb, itemHexSlot, blockResolver);
    }

    public PearlTint hexSlot(String slot) {
        return this.resolver(BlockTintResolvers.hexSlot(HexColorComponent.cleanSlot(slot)));
    }

    public PearlTint hexSlotWithPlacementMemory(String slot) {
        return this.resolver(BlockTintResolvers.hexSlotWithPlacementMemory(HexColorComponent.cleanSlot(slot)));
    }


    public PearlTint fallback(int fallbackRgb) {
        return new PearlTint(profile, offset, baseLayer, itemSources, fallbackRgb, itemHexSlot, blockResolver);
    }

    public PearlTint itemSlot(String slot) {
        return new PearlTint(profile, offset, baseLayer, itemSources, fallbackRgb, HexColorComponent.cleanSlot(slot), blockResolver);
    }

    public boolean hasItemSources() {
        return itemSources;
    }

    public int fallbackRgb() {
        return fallbackRgb;
    }

    public String itemHexSlot() {
        return itemHexSlot;
    }

    public Integer resolveItemHex(java.util.Map<String, Integer> colors) {
        if (itemHexSlot != null) {
            Integer color = HexColorComponent.getSlot(colors, itemHexSlot);
            if (color != null) {
                return TintColorUtil.rgb(color);
            }
        }

        return HexColorComponent.first(colors);
    }

    public PearlFireTintProfiles.Profile profile() {
        return profile;
    }

    public int offset() {
        return offset;
    }

    public boolean hasBaseLayer() {
        return baseLayer;
    }

    public int layerCount() {
        return profile.layerCount();
    }

    public int totalItemLayerCount() {
        return (baseLayer ? 1 : 0) + profile.layerCount();
    }

    public boolean requiresCompositeBypass() {
        return totalItemLayerCount() > VANILLA_MAX_TOTAL_LAYERS;
    }

    public int firstTintIndex() {
        return offset + (baseLayer ? 1 : 0);
    }

    public int tintIndexForLayer(int layer) {
        if (layer < 0 || layer >= profile.layerCount()) {
            throw new IllegalArgumentException("Unsupported pearl tint layer: " + layer);
        }

        return firstTintIndex() + layer;
    }

    public int defaultLayerTint(int layer) {
        return TintColorUtil.rgb(PearlFireTintSource.profileTint(
                fallbackRgb,
                layer,
                profile
        ));
    }

    //? if >=1.21.4 {
    public Object sourceForLayer(int layer) {
        return VanillaItemTintSources.customModelData(layer, defaultLayerTint(layer));
    }
    //?}

    @Override
    public boolean useTintedGeneratedBlockModel() {
        return true;
    }

    @Override
    public boolean hasBlockTint() {
        return blockResolver != null;
    }

    @Override
    public int blockTint(BlockState state, BlockAndTintGetter level, BlockPos pos, int tintIndex) {
        if (blockResolver == null) {
            return TintColorUtil.NO_TINT;
        }

        int layer = tintIndex - offset;
        if (!profile.supports(layer)) {
            return TintColorUtil.NO_TINT;
        }

        Integer hexColor = blockResolver.resolve(state, level, pos);
        if (hexColor == null) {
            return TintColorUtil.NO_TINT;
        }

        return PearlFireTintSource.blockTint(hexColor, layer, profile);
    }

    //? if >=1.21.4 {
    @Override
    public List<?> sources() {
        if (!itemSources) {
            return List.of();
        }

        List<Object> result = new ArrayList<>();

        for (int i = 0; i < offset; i++) {
            result.add(VanillaItemTintSources.none());
        }

        if (baseLayer) {
            result.add(VanillaItemTintSources.none());
        }

        for (int layer = 0; layer < profile.layerCount(); layer++) {
            result.add(sourceForLayer(layer));
        }

        return List.copyOf(result);
    }
    //?}
}
