package space.anatomyuniverse.musavacca.data.loot;

//? if >=26.3 {
/*import net.minecraft.core.Holder;
*///?}
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.loot.AddTableLootModifier;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;
import space.anatomyuniverse.musavacca.MusaCore;

public final class ModGlobalLootModifierProvider extends GlobalLootModifierProvider {
    public ModGlobalLootModifierProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, MusaCore.MOD_ID);
    }

    @Override
    protected void start() {
        inject("inject_musavacca_pup_into_sniffer_digging", ModLootTables.VANILLA_SNIFFER_DIGGING, ModLootTables.SNIFFER_DIGGING_MUSAVACCA_PUP);
        inject("inject_fractured_potassium_template_into_jungle_temple", ModLootTables.VANILLA_JUNGLE_TEMPLE,
                ModLootTables.JUNGLE_TEMPLE_FRACTURED_POTASSIUM_TEMPLATE);
    }

    private void inject(String name, ResourceKey<LootTable> target, ResourceKey<LootTable> loot) {
        //? if >=1.21.11 {
        /*var condition = LootTableIdCondition.builder(target.identifier()).build();
        *///?} else {
        var condition = LootTableIdCondition.builder(target.location()).build();
        //?}
        add(name, new AddTableLootModifier(
                //? if >=26.3 {
                /*java.util.Optional.of(Holder.direct(condition)),
                *///?} else {
                new LootItemCondition[] { condition },
                //?}
                //? if >=26.1 {
                /*0,
                *///?}
                loot));
    }
}
