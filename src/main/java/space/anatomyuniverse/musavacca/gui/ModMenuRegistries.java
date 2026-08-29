package space.anatomyuniverse.musavacca.gui;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;
import space.anatomyuniverse.musavacca.MusaCore;
import space.anatomyuniverse.musavacca.gui.menu.*;

import java.util.function.Supplier;

public final class ModMenuRegistries {

    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(
                    Registries.MENU,
                    MusaCore.MOD_ID
            );

    public static final Supplier<MenuType<VocoDialerMenu>> VOCO_DIALER_MENU =
            MENUS.register(
                    "voco_dialer_menu",
                    () ->
                            IMenuTypeExtension.create(
                                    VocoDialerMenu::new
                            )
            );

    public static final Supplier<MenuType<VocoCallerMenu>> VOCO_CALLER_MENU =
            MENUS.register(
                    "voco_caller_menu",
                    () ->
                            IMenuTypeExtension.create(
                                    VocoCallerMenu::new
                            )
            );

    public static final Supplier<MenuType<FlintAndPearlMenu>> FLINT_AND_PEARL_MENU =
            MENUS.register(
                    "flint_and_pearl_menu",
                    () ->
                            IMenuTypeExtension.create(
                                    FlintAndPearlMenu::new
                            )
            );

    public static void register(IEventBus bus) {
        MENUS.register(bus);
    }

    private ModMenuRegistries() {}
}

