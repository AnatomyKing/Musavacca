package space.anatomyuniverse.musavacca.worldgen;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
//? if >=1.21.11 {
/*import net.minecraft.resources.Identifier;
*///?} else {
import net.minecraft.resources.ResourceLocation;
//?}

//? if >=26.3 {
/*import net.minecraft.world.level.levelgen.feature.Feature;
*///?} else {
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
//?}

import space.anatomyuniverse.musavacca.MusaCore;

public final class ModConfiguredFeatures {

    //? if >=26.3 {
    /*public static final ResourceKey<Feature> MUSAVACCA_TREE =
    *///?} else {
    public static final ResourceKey<ConfiguredFeature<?, ?>> MUSAVACCA_TREE =
    //?}

            ResourceKey.create(

                    //? if >=26.3 {
                    /*Registries.FEATURE,
                    *///?} else {
                    Registries.CONFIGURED_FEATURE,
                    //?}

                    //? if >=1.21.11 {
                    /*Identifier.fromNamespaceAndPath(MusaCore.MOD_ID, "musavacca_tree")
                    *///?} else {
                    ResourceLocation.fromNamespaceAndPath(MusaCore.MOD_ID, "musavacca_tree")
                    //?}
            );

    private ModConfiguredFeatures() {}
}
