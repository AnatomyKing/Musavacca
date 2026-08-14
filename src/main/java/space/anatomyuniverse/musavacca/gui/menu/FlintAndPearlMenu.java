package space.anatomyuniverse.musavacca.gui.menu;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import space.anatomyuniverse.musavacca.gui.ModMenuRegistries;
import space.anatomyuniverse.musavacca.gui.backend.FlintAndPearlBackend;

public class FlintAndPearlMenu extends AbstractContainerMenu {
    private final FlintAndPearlBackend backend;

    public FlintAndPearlMenu(
            int containerId,
            Inventory inventory,
            RegistryFriendlyByteBuf buffer
    ) {
        this(
                containerId,
                inventory,
                buffer.readBoolean()
                        ? InteractionHand.OFF_HAND
                        : InteractionHand.MAIN_HAND,
                buffer.readInt()
        );
    }

    public FlintAndPearlMenu(
            int containerId,
            Inventory inventory,
            InteractionHand hand,
            int hexColor
    ) {
        super(
                ModMenuRegistries.FLINT_AND_PEARL_MENU.get(),
                containerId
        );

        this.backend =
                new FlintAndPearlBackend(
                        hand,
                        hexColor
                );
    }

    public InteractionHand getHand() {
        return this.backend.getHand();
    }

    public int getHexColor() {
        return this.backend.getHexColor();
    }

    public void setHexColor(int hexColor) {
        this.backend.setHexColor(hexColor);
    }

    public boolean applyHexColor(
            Player player,
            int hexColor
    ) {
        return this.backend.applyHexColor(
                player,
                hexColor
        );
    }

    public static int getDefaultHexColor() {
        return FlintAndPearlBackend.getDefaultHexColor();
    }

    @Override
    public ItemStack quickMoveStack(
            Player player,
            int index
    ) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}