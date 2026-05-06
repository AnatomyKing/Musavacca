package space.anatomyuniverse.musavacca.gui.menu;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import space.anatomyuniverse.musavacca.gui.ModMenus;

public class TestInventoryMenu extends AbstractContainerMenu {

    public TestInventoryMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf ignored) {
        this(containerId, playerInventory);
    }

    public TestInventoryMenu(int containerId, Inventory playerInventory) {
        super(ModMenus.TEST_INVENTORY_MENU.get(), containerId);

    }

    public static void open(ServerPlayer player) {
        player.openMenu(
                new SimpleMenuProvider(
                        (containerId, inventory, ignoredPlayer) -> new TestInventoryMenu(containerId, inventory),
                        Component.literal("Rotary Test GUI")
                )
        );
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}