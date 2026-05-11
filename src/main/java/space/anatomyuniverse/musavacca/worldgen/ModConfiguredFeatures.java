package space.anatomyuniverse.musavacca.worldgen;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import space.anatomyuniverse.musavacca.MusaCore;

public final class ModConfiguredFeatures {

    public static final ResourceKey<ConfiguredFeature<?, ?>> MUSAVACCA_TREE =
            ResourceKey.create(
                    Registries.CONFIGURED_FEATURE,
                    ResourceLocation.fromNamespaceAndPath(MusaCore.MOD_ID, "musavacca_tree")
            );

    private ModConfiguredFeatures() {}
}