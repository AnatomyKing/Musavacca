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
import space.anatomyuniverse.musavacca.gui.backend.VocoCallerBackend;

public final class VocoCallerMenu extends AbstractContainerMenu {

    private final VocoCallerBackend backend =
            new VocoCallerBackend();

    public VocoCallerMenu(
            int containerId,
            Inventory playerInventory,
            RegistryFriendlyByteBuf ignored
    ) {
        this(
                containerId,
                playerInventory
        );
    }

    public VocoCallerMenu(
            int containerId,
            Inventory playerInventory
    ) {
        super(
                ModMenuRegistries.VOCO_CALLER_MENU.get(),
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
                                new VocoCallerMenu(
                                        containerId,
                                        inventory
                                ),
                        Component.literal("Voco Caller")
                )
        );
    }

    public VocoCallerBackend getBackend() {
        return this.backend;
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