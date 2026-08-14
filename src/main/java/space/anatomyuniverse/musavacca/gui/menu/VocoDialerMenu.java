package space.anatomyuniverse.musavacca.gui.menu;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import space.anatomyuniverse.musavacca.gui.ModMenuRegistries;
import space.anatomyuniverse.musavacca.gui.backend.VocoDialerBackend;

public class VocoDialerMenu extends AbstractContainerMenu {
    private final VocoDialerBackend backend =
            new VocoDialerBackend();

    public VocoDialerMenu(
            int containerId,
            Inventory playerInventory,
            RegistryFriendlyByteBuf ignored
    ) {
        this(
                containerId,
                playerInventory
        );
    }

    public VocoDialerMenu(
            int containerId,
            Inventory playerInventory
    ) {
        super(
                ModMenuRegistries.VOCO_DIALER_MENU.get(),
                containerId
        );
    }

    public static void open(ServerPlayer player) {
        player.openMenu(
                new SimpleMenuProvider(
                        (
                                containerId,
                                inventory,
                                ignoredPlayer
                        ) ->
                                new VocoDialerMenu(
                                        containerId,
                                        inventory
                                ),
                        Component.literal(
                                "Rotary Test GUI"
                        )
                )
        );
    }

    @Override
    public boolean clickMenuButton(
            Player player,
            int id
    ) {
        boolean handled =
                this.backend.handleButton(
                        player,
                        id
                );

        if (handled
                && !player.level().isClientSide()) {
            this.broadcastChanges();
        }

        return handled;
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