// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/menu/TestInventoryMenu.java
package space.anatomyuniverse.musavacca.gui.menu;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import space.anatomyuniverse.musavacca.gui.ModMenus;

public class TestInventoryMenu extends AbstractContainerMenu {
    public static final int CHEST_ROWS = 6;
    public static final int CHEST_COLUMNS = 9;
    public static final int CHEST_SLOT_COUNT = CHEST_ROWS * CHEST_COLUMNS;

    private static final int CHEST_START_X = 48;
    private static final int CHEST_START_Y = 50;

    private static final int PLAYER_INV_START_X = 48;
    private static final int PLAYER_INV_START_Y = 171;

    private static final int HOTBAR_START_X = 48;
    private static final int HOTBAR_START_Y = 229;

    private final SimpleContainer fakeChest = new SimpleContainer(CHEST_SLOT_COUNT);

    public TestInventoryMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf ignored) {
        this(containerId, playerInventory);
    }

    public TestInventoryMenu(int containerId, Inventory playerInventory) {
        super(ModMenus.TEST_INVENTORY_MENU.get(), containerId);

        for (int row = 0; row < CHEST_ROWS; row++) {
            for (int col = 0; col < CHEST_COLUMNS; col++) {
                int slotIndex = col + row * CHEST_COLUMNS;
                int x = CHEST_START_X + col * 18;
                int y = CHEST_START_Y + row * 18;
                this.addSlot(new LockedSlot(this.fakeChest, slotIndex, x, y));
            }
        }

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                int slotIndex = col + row * 9 + 9;
                int x = PLAYER_INV_START_X + col * 18;
                int y = PLAYER_INV_START_Y + row * 18;
                this.addSlot(new Slot(playerInventory, slotIndex, x, y));
            }
        }

        for (int col = 0; col < 9; col++) {
            int x = HOTBAR_START_X + col * 18;
            int y = HOTBAR_START_Y;
            this.addSlot(new Slot(playerInventory, col, x, y));
        }
    }

    public static void open(ServerPlayer player) {
        player.openMenu(
                new SimpleMenuProvider(
                        (containerId, inventory, ignoredPlayer) -> new TestInventoryMenu(containerId, inventory),
                        Component.literal("Test Inventory")
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

    private static final class LockedSlot extends Slot {
        public LockedSlot(SimpleContainer container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }

        @Override
        public boolean mayPickup(Player player) {
            return false;
        }
    }
}