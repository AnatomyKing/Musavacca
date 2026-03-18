package space.anatomyuniverse.musavacca.menu;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import space.anatomyuniverse.musavacca.MusaCore;

import java.util.function.Supplier;

public final class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, MusaCore.MOD_ID);

    public static final Supplier<MenuType<ItemInteractMenu>> ITEM_INTERACT_MENU =
            MENUS.register("item_interact_menu",
                    () -> new MenuType<>(ItemInteractMenu::new, FeatureFlags.DEFAULT_FLAGS));

    public static void register(IEventBus bus) {
        MENUS.register(bus);
    }

    private ModMenus() {}
}