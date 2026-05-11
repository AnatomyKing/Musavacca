package space.anatomyuniverse.musavacca.data;

import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import space.anatomyuniverse.musavacca.data.language.ModLanguageProvider;
import space.anatomyuniverse.musavacca.data.loot.ModBlockLootProvider;
import space.anatomyuniverse.musavacca.data.loot.ModGlobalLootModifierProvider;
import space.anatomyuniverse.musavacca.data.loot.ModSnifferDiggingLootProvider;
import space.anatomyuniverse.musavacca.data.models.ModModelProvider;
import space.anatomyuniverse.musavacca.data.recipes.ModRecipeProvider;
import space.anatomyuniverse.musavacca.data.tags.ModBlockTagsProvider;
import space.anatomyuniverse.musavacca.data.tags.ModItemTagsProvider;
import space.anatomyuniverse.musavacca.data.models.ModelSets;
import space.anatomyuniverse.musavacca.data.models.item.CustomArmorSet;
import space.anatomyuniverse.musavacca.data.worldgen.MusavaccaTreeFeatureProvider;

import java.util.List;
import java.util.Set;

//? if <1.21.4 {
/*import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
*///?}

public final class ModDataGenerators {

    //? if <1.21.4 {
    /*public static void gatherData(final GatherDataEvent event) {
        final PackOutput output = event.getGenerator().getPackOutput();
        final ExistingFileHelper efh = event.getExistingFileHelper();

        if (event.includeClient()) {
            event.getGenerator().addProvider(true, new ModLanguageProvider(output, "en_us"));
            event.getGenerator().addProvider(true, new ModModelProvider(output, efh));
        }

        if (event.includeServer()) {
            // Recipes
            //? if <1.21.3 {
            /^event.getGenerator().addProvider(true, new ModRecipeProvider(output, event.getLookupProvider()));
            ^///?}
            //? if >=1.21.3 {
            event.getGenerator().addProvider(true, new ModRecipeProvider.Runner(output, event.getLookupProvider()));
            //?}

            final ModBlockTagsProvider blockTagsProvider =
                    new ModBlockTagsProvider(output, event.getLookupProvider(), efh);

            event.createProvider(output -> new CustomArmorSet.Provider(
                output,
                ModelSets.customArmorSets()
            ));

            event.getGenerator().addProvider(true, blockTagsProvider);

            event.getGenerator().addProvider(true,
                    new ModItemTagsProvider(
                            output,
                            event.getLookupProvider(),
                            blockTagsProvider.contentsGetter(),
                            efh
                    )
            );

            event.getGenerator().addProvider(true, new MusavaccaTreeFeatureProvider(output));

            event.getGenerator().addProvider(true,
                    new ModGlobalLootModifierProvider(output, event.getLookupProvider())
            );

            event.getGenerator().addProvider(true,
                    new LootTableProvider(
                            output,
                            Set.of(),
                            List.of(
                                    new LootTableProvider.SubProviderEntry(
                                            ModBlockLootProvider::new,
                                            LootContextParamSets.BLOCK
                                    ),
                                    new LootTableProvider.SubProviderEntry(
                                            ModSnifferDiggingLootProvider::new,
                                            LootContextParamSets.GIFT
                                    )
                            ),
                            event.getLookupProvider()
                    )
            );
        }
    }
    *///?}

    //? if >=1.21.4 {
    /**
     * 1.21.4+: unified model datagen through ModModelProvider.
     */
    public static void gatherData(final GatherDataEvent.Client event) {
        // Client-side generators
        event.createProvider(output -> new ModLanguageProvider(output, "en_us"));
        event.createProvider(ModModelProvider::new);

        // Server/datapack generators
        event.createProvider(ModRecipeProvider.Runner::new);
        event.createProvider(ModBlockTagsProvider::new);
        event.createProvider(ModItemTagsProvider::new);

        event.createProvider(output -> new CustomArmorSet.Provider(
                output,
                ModelSets.customArmorSets()
        ));

        event.createProvider(MusavaccaTreeFeatureProvider::new);
        event.createProvider(ModGlobalLootModifierProvider::new);

        event.createProvider((output, lookup) -> new LootTableProvider(
                output,
                Set.of(),
                List.of(
                        new LootTableProvider.SubProviderEntry(
                                ModBlockLootProvider::new,
                                LootContextParamSets.BLOCK
                        ),
                        new LootTableProvider.SubProviderEntry(
                                ModSnifferDiggingLootProvider::new,
                                LootContextParamSets.GIFT
                        )
                ),
                lookup
        ));
    }
    //?}

    private ModDataGenerators() {}
}