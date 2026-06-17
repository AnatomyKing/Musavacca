package space.anatomyuniverse.musavacca.gui.menu;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import space.anatomyuniverse.musavacca.gui.ModMenus;
import space.anatomyuniverse.musavacca.item.custom.FlintAndPearlItem;
import space.anatomyuniverse.musavacca.tint.TintColorUtil;

public class FlintAndPearlMenu extends AbstractContainerMenu {
    private final InteractionHand hand;
    private final int hexColor;

    public FlintAndPearlMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buffer) {
        this(
                containerId,
                playerInventory,
                buffer.readBoolean() ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND,
                buffer.readInt()
        );
    }

    public FlintAndPearlMenu(int containerId, Inventory playerInventory, InteractionHand hand, int initialHex) {
        super(ModMenus.FLINT_AND_PEARL_MENU.get(), containerId);

        this.hand = hand;
        this.hexColor = TintColorUtil.rgb(initialHex);
    }

    public InteractionHand getHand() {
        return this.hand;
    }

    public int getHexColor() {
        return this.hexColor;
    }

    public static int getDefaultHexColor() {
        return TintColorUtil.rgb(FlintAndPearlItem.DEFAULT_HEX_COLOR);
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