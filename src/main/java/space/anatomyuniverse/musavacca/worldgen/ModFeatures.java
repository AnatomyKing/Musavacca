package space.anatomyuniverse.musavacca.worldgen;

import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.grower.TreeGrower;
//? if >=1.21.11 {
/*import net.minecraft.resources.Identifier;
*///?} else {
import net.minecraft.resources.ResourceLocation;
//?}
//? if >=26.3 {
/*import net.minecraft.util.random.WeightedList;
*///?} else {
import java.util.Optional;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
//?}
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import space.anatomyuniverse.musavacca.MusaCore;
import space.anatomyuniverse.musavacca.worldgen.feature.MusavaccaTemplateTreeConfiguration;
import space.anatomyuniverse.musavacca.worldgen.feature.MusavaccaTemplateTreeFeature;

public final class ModFeatures {
    //? if >=26.3 {
    /*public static final DeferredRegister<com.mojang.serialization.MapCodec<? extends Feature>> FEATURES =
            DeferredRegister.create(BuiltInRegistries.FEATURE_TYPE, MusaCore.MOD_ID);
    *///?} else {
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(BuiltInRegistries.FEATURE, MusaCore.MOD_ID);
    //?}
    //? if >=26.3 {
    /*public static final Supplier<com.mojang.serialization.MapCodec<MusavaccaTemplateTreeFeature>> MUSAVACCA_TEMPLATE_TREE =
            FEATURES.register("musavacca_template_tree", () -> MusavaccaTemplateTreeFeature.CODEC);
    *///?} else {
    public static final Supplier<Feature<MusavaccaTemplateTreeConfiguration>> MUSAVACCA_TEMPLATE_TREE = FEATURES.register("musavacca_template_tree",
            () -> new MusavaccaTemplateTreeFeature(MusavaccaTemplateTreeConfiguration.CODEC));
    //?}

    //? if >=26.3 {
    /*public static final ResourceKey<Feature> MUSAVACCA_TREE =
    *///?} else {
    public static final ResourceKey<ConfiguredFeature<?, ?>> MUSAVACCA_TREE =
    //?}
            //? if >=26.3 {
            /*ResourceKey.create(Registries.FEATURE, Identifier.fromNamespaceAndPath(MusaCore.MOD_ID, "musavacca_tree"));
            *///?} else if >=1.21.11 <26.3 {
            /*ResourceKey.create(Registries.CONFIGURED_FEATURE, Identifier.fromNamespaceAndPath(MusaCore.MOD_ID, "musavacca_tree"));
            *///?} else {
            ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.fromNamespaceAndPath(MusaCore.MOD_ID, "musavacca_tree"));
            //?}

    //? if >=26.3 {
    /*public static final TreeGrower MUSAVACCA_TREE_GROWER = new TreeGrower("musavacca", WeightedList.of(MUSAVACCA_TREE),
            WeightedList.of(), WeightedList.of(), MUSAVACCA_TREE);
    *///?} else {
    public static final TreeGrower MUSAVACCA_TREE_GROWER = new TreeGrower("musavacca", Optional.empty(), Optional.of(MUSAVACCA_TREE),
            Optional.empty());
    //?}

    public static void register(IEventBus modBus) {
        FEATURES.register(modBus);
    }

    private ModFeatures() {}
}
