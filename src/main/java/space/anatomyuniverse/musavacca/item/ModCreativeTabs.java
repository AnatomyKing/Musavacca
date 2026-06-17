package space.anatomyuniverse.musavacca.item;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MusaCore.MOD_ID);

    // Optional: skip specific registry paths from showing up in the tab
    private static final Set<String> BLACKLIST = Set.of(
            "open_invo_test_item",
            "open_teleport_test_item",
            "basuke_spawn_egg"
    );

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MUSAVACCA_TAB =
            TABS.register("musavacca_tab", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup." + MusaCore.MOD_ID + ".musavacca_tab"))
                    .icon(() -> new ItemStack(ModItems.BANANA_PEARL.get()))
                    .displayItems((params, output) -> {
                        List<Item> mine = new ArrayList<>();
                        for (Item item : BuiltInRegistries.ITEM) {
                            ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
                            if (id != null
                                    && MusaCore.MOD_ID.equals(id.getNamespace())
                                    && !BLACKLIST.contains(id.getPath())) {
                                mine.add(item);
                            }
                        }
                        mine.sort(Comparator.comparing(i -> BuiltInRegistries.ITEM.getKey(i).getPath()));
                        mine.forEach(output::accept);
                    })
                    .build()
            );

    public static void register(IEventBus bus) {
        TABS.register(bus);
    }

    private ModCreativeTabs() {}
}
