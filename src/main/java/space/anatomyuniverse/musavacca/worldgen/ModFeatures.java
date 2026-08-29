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

    public static final DeferredRegister<Feature<?>> FEATURES =
            DeferredRegister.create(BuiltInRegistries.FEATURE, MusaCore.MOD_ID);

    public static final Supplier<Feature<MusavaccaTemplateTreeConfiguration>> MUSAVACCA_TEMPLATE_TREE =
            FEATURES.register(
                    "musavacca_template_tree",
                    () -> new MusavaccaTemplateTreeFeature(MusavaccaTemplateTreeConfiguration.CODEC)
            );

    public static void register(IEventBus modBus) {
        FEATURES.register(modBus);
    }

    private ModFeatures() {}
}

