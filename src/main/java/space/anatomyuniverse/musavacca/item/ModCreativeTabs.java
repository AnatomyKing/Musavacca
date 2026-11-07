package space.anatomyuniverse.musavacca.item;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import space.anatomyuniverse.musavacca.MusaCore;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public final class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MusaCore.MOD_ID);
    private static final Set<String> BLACKLIST = Set.of("basuke_spawn_egg", "voco_charged_table_north_east",
            "voco_charged_table_north_west", "voco_charged_table_north", "voco_charged_table_south_east", "voco_charged_table_east",
            "voco_charged_table_north_west_and_south_east", "voco_charged_table_north_and_south_east", "voco_charged_table_south_west",
            "voco_charged_table_north_east_and_south_west", "voco_charged_table_west", "voco_charged_table_north_and_south_west", "voco_charged_table_south",
            "voco_charged_table_south_and_north_east", "voco_charged_table_south_and_north_west");
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MUSAVACCA_TAB = TABS.register("musavacca_tab",
            () -> CreativeModeTab.builder().title(Component.translatable("itemGroup." + MusaCore.MOD_ID + ".musavacca_tab"))
                    .icon(() -> new ItemStack(ModItems.BANANA_PEARL.get())).displayItems((params, output) -> {
                        List<Item> items = new ArrayList<>();
                        for (Item item : BuiltInRegistries.ITEM) {
                            var id = BuiltInRegistries.ITEM.getKey(item);
                            if (id != null && MusaCore.MOD_ID.equals(id.getNamespace()) && !BLACKLIST.contains(id.getPath())) {
                                items.add(item);
                            }
                        }
                        items.sort(Comparator.comparing(i -> BuiltInRegistries.ITEM.getKey(i).getPath()));
                        items.forEach(output::accept);
                        output.accept(PearlIgniter.create());
                    }).build());

    public static void register(IEventBus bus) {
        TABS.register(bus);
    }

    private ModCreativeTabs() {}
}
