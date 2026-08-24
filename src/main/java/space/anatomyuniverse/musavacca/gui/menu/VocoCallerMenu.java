// file: src/main/java/space/anatomyuniverse/musavacca/gui/menu/VocoCallerMenu.java
package space.anatomyuniverse.musavacca.gui.menu;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import space.anatomyuniverse.musavacca.gui.ModMenuRegistries;
import space.anatomyuniverse.musavacca.gui.backend.VocoCallerBackend;
import space.anatomyuniverse.musavacca.item.custom.OpenVocoCallerItem;
import space.anatomyuniverse.musavacca.item.custom.SimCardItem;
import space.anatomyuniverse.musavacca.vococaller.VocoCallerNetwork;
import space.anatomyuniverse.musavacca.vococaller.VocoCallerPhonebook;

public final class VocoCallerMenu extends VocoDialerMenu {
    private final int phoneHex;

    public VocoCallerMenu(
            int containerId,
            Inventory playerInventory,
            RegistryFriendlyByteBuf buffer
    ) {
        this(
                containerId,
                buffer.readInt(),
                VocoCallerPhonebook.of(
                        readAddresses(buffer),
                        readAddresses(buffer)
                )
        );
    }

    private VocoCallerMenu(
            int containerId,
            int phoneHex,
            VocoCallerPhonebook phonebook
    ) {
        super(
                ModMenuRegistries.VOCO_CALLER_MENU.get(),
                containerId,
                new VocoCallerBackend(phonebook)
        );

        this.phoneHex = phoneHex & 0xFFFFFF;
    }

    public static void open(
            ServerPlayer player,
            ItemStack phone
    ) {
        ItemStack sim =
                OpenVocoCallerItem.getSim(phone);

        if (sim.isEmpty()
                || !SimCardItem.hasStoredHex(sim)) {
            return;
        }

        int phoneHex =
                OpenVocoCallerItem.getSimHex(phone);

        VocoCallerPhonebook phonebook =
                SimCardItem.getPhonebook(sim);

        player.openMenu(
                new SimpleMenuProvider(
                        (
                                containerId,
                                inventory,
                                ignoredPlayer
                        ) ->
                                new VocoCallerMenu(
                                        containerId,
                                        phoneHex,
                                        phonebook
                                ),
                        Component.literal(
                                "Voco Caller"
                        )
                ),
                buffer -> {
                    buffer.writeInt(phoneHex);

                    writeAddresses(
                            buffer,
                            phonebook.recentArray()
                    );

                    writeAddresses(
                            buffer,
                            phonebook.savedArray()
                    );
                }
        );
    }

    public int getPhoneHex() {
        return this.phoneHex;
    }

    public void applyClientState(
            ServerPlayer player,
            int[] recent,
            int[] saved
    ) {
        if (!VocoCallerNetwork.carriesPhone(
                player,
                this.phoneHex
        )) {
            return;
        }

        this.getBackend()
                .replaceCallState(
                        recent,
                        saved
                );
    }

    @Override
    public boolean clickMenuButton(
            Player player,
            int id
    ) {
        if (!player.level().isClientSide()
                && !VocoCallerNetwork.carriesPhone(
                        player,
                        this.phoneHex
                )) {
            return false;
        }

        return super.clickMenuButton(
                player,
                id
        );
    }

    @Override
    public boolean stillValid(Player player) {
        return player.level().isClientSide()
                || VocoCallerNetwork.carriesPhone(
                        player,
                        this.phoneHex
                );
    }

    @Override
    public void removed(Player player) {
        if (!player.level().isClientSide()
                && player instanceof ServerPlayer serverPlayer) {
            VocoCallerNetwork.writePhonebook(
                    serverPlayer,
                    this.phoneHex,
                    this.getBackend()
                            .toPhonebook()
            );
        }

        super.removed(player);
    }

    @Override
    public VocoCallerBackend getBackend() {
        return (VocoCallerBackend)
                super.getBackend();
    }

    private static int[] readAddresses(
            RegistryFriendlyByteBuf buffer
    ) {
        int[] result =
                new int[VocoCallerPhonebook.ROW_COUNT];

        for (int row = 0; row < result.length; row++) {
            result[row] = buffer.readInt();
        }

        return result;
    }

    private static void writeAddresses(
            RegistryFriendlyByteBuf buffer,
            int[] values
    ) {
        for (
                int row = 0;
                row < VocoCallerPhonebook.ROW_COUNT;
                row++
        ) {
            int value =
                    values != null
                            && row < values.length
                            ? values[row]
                            : VocoCallerPhonebook.EMPTY;

            buffer.writeInt(
                    value < 0
                            ? VocoCallerPhonebook.EMPTY
                            : value & 0xFFFFFF
            );
        }
    }
}
