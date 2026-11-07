package space.anatomyuniverse.musavacca.gui.menu;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import space.anatomyuniverse.musavacca.component.ModDataComponents;
import space.anatomyuniverse.musavacca.gui.ModMenuRegistries;
import space.anatomyuniverse.musavacca.item.PearlIgniter;
import space.anatomyuniverse.musavacca.tint.MusavaccaTints;

public class FlintAndPearlMenu extends AbstractContainerMenu {
    private final InteractionHand hand;
    private int hexColor;

    public FlintAndPearlMenu(int containerId, Inventory inventory, RegistryFriendlyByteBuf buffer) {
        this(containerId, inventory, buffer.readBoolean() ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND, buffer.readInt());
    }

    public FlintAndPearlMenu(int containerId, Inventory inventory, InteractionHand hand, int hexColor) {
        super(ModMenuRegistries.FLINT_AND_PEARL_MENU.get(), containerId);
        this.hand = hand;
        this.hexColor = MusavaccaTints.resolve(hexColor);
    }

    public InteractionHand getHand() {
        return this.hand;
    }

    public int getHexColor() {
        return this.hexColor;
    }

    public void setHexColor(int hexColor) {
        this.hexColor = MusavaccaTints.resolve(hexColor);
    }

    public boolean applyHexColor(Player player, int hexColor) {
        ItemStack stack = player.getItemInHand(this.hand);
        if (!PearlIgniter.is(stack)) return false;
        int color = MusavaccaTints.resolve(hexColor);
        stack.set(ModDataComponents.HEX_COLOR.get(), color);
        this.hexColor = color;
        return true;
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
