package space.anatomyuniverse.musavacca.gui;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;
import space.anatomyuniverse.musavacca.MusaCore;
import space.anatomyuniverse.musavacca.gui.menu.*;

import java.util.function.Supplier;

public final class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, MusaCore.MOD_ID);

    public static final Supplier<MenuType<ItemInteractMenu>> ITEM_INTERACT_MENU =
            MENUS.register(
                    "item_interact_menu",
                    () -> IMenuTypeExtension.create(ItemInteractMenu::new)
            );

    public static final Supplier<MenuType<TestInventoryMenu>> TEST_INVENTORY_MENU =
            MENUS.register(
                    "test_inventory_menu",
                    () -> IMenuTypeExtension.create(TestInventoryMenu::new)
            );

    public static final Supplier<MenuType<VocoSliderMenu>> VOCO_SLIDER_MENU =
            MENUS.register(
                    "voco_slider_menu",
                    () -> IMenuTypeExtension.create(VocoSliderMenu::new)
            );

    public static final Supplier<MenuType<HexTeleportMenu>> HEX_TELEPORT_MENU =
            MENUS.register(
                    "hex_teleport_menu",
                    () -> IMenuTypeExtension.create(HexTeleportMenu::new)
            );

    public static final Supplier<MenuType<FlintAndPearlMenu>> FLINT_AND_PEARL_MENU =
            MENUS.register("flint_and_pearl_menu", () -> IMenuTypeExtension.create(FlintAndPearlMenu::new));

    public static void register(IEventBus bus) {
        MENUS.register(bus);
    }

    private ModMenus() {}
}