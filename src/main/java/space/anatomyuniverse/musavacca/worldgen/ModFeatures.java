package space.anatomyuniverse.musavacca.worldgen;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import space.anatomyuniverse.musavacca.MusaCore;
import space.anatomyuniverse.musavacca.worldgen.feature.MusavaccaTemplateTreeConfiguration;
import space.anatomyuniverse.musavacca.worldgen.feature.MusavaccaTemplateTreeFeature;
import java.util.function.Supplier;

public final class ModFeatures {
    //? if >=26.3 {
    /*public static final DeferredRegister<com.mojang.serialization.MapCodec<? extends Feature>> FEATURES =
            DeferredRegister.create(BuiltInRegistries.FEATURE_TYPE, MusaCore.MOD_ID);
    *///?} else {
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(BuiltInRegistries.FEATURE, MusaCore.MOD_ID);
    //?}
    //? if >=26.3 {
    /*public static final Supplier<com.mojang.serialization.MapCodec<MusavaccaTemplateTreeFeature>> MUSAVACCA_TEMPLATE_TREE =
    *///?} else {
    public static final Supplier<Feature<MusavaccaTemplateTreeConfiguration>> MUSAVACCA_TEMPLATE_TREE =
    //?}
            //? if >=26.3 {
            /*FEATURES.register("musavacca_template_tree", () -> MusavaccaTemplateTreeFeature.CODEC);
            *///?} else {
            FEATURES.register(
                    "musavacca_template_tree",
                    () -> new MusavaccaTemplateTreeFeature(MusavaccaTemplateTreeConfiguration.CODEC)
            );
            //?}
    public static void register(IEventBus modBus) {
        FEATURES.register(modBus);
    }

    private ModFeatures() {}
}
