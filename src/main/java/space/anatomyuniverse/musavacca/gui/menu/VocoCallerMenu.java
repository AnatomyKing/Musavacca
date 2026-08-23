package space.anatomyuniverse.musavacca.gui.menu;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import space.anatomyuniverse.musavacca.gui.ModMenuRegistries;
import space.anatomyuniverse.musavacca.gui.backend.VocoCallerBackend;

public final class VocoCallerMenu extends VocoDialerMenu {
    public VocoCallerMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf ignored) {
        this(containerId, playerInventory);
    }

    public VocoCallerMenu(int containerId, Inventory playerInventory) {
        super(ModMenuRegistries.VOCO_CALLER_MENU.get(), containerId, new VocoCallerBackend(playerInventory.player));
    }

    public static void open(ServerPlayer player) {
        player.openMenu(new SimpleMenuProvider(
                (containerId, inventory, ignoredPlayer) -> new VocoCallerMenu(containerId, inventory),
                Component.literal("Voco Caller")
        ));
    }

    @Override
    public VocoCallerBackend getBackend() {
        return (VocoCallerBackend) super.getBackend();
    }
}
