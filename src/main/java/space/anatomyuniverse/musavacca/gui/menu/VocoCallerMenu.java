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

    private final int openingSlot;
    private final ItemStack openingPhoneReference;

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
                ),
                -1,
                ItemStack.EMPTY
        );
    }

    private VocoCallerMenu(
            int containerId,
            int phoneHex,
            VocoCallerPhonebook phonebook,
            int openingSlot,
            ItemStack openingPhoneReference
    ) {
        super(
                ModMenuRegistries.VOCO_CALLER_MENU.get(),
                containerId,
                new VocoCallerBackend(phonebook)
        );

        this.phoneHex =
                phoneHex & 0xFFFFFF;

        this.openingSlot =
                openingSlot;

        this.openingPhoneReference =
                openingPhoneReference == null
                        ? ItemStack.EMPTY
                        : openingPhoneReference;
    }

    public static void open(
            ServerPlayer player,
            ItemStack phone
    ) {
        ItemStack sim =
                OpenVocoCallerItem.getSim(phone);

        if (
                sim.isEmpty()
                        || !SimCardItem.hasStoredHex(sim)
        ) {
            return;
        }

        int openingSlot =
                findExactInventorySlot(
                        player,
                        phone
                );

        if (openingSlot < 0) {
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
                                        phonebook,
                                        openingSlot,
                                        phone
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
        if (!this.hasOpeningPhone(player)) {
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
        if (
                !player.level().isClientSide()
                        && !this.hasOpeningPhone(player)
        ) {
            return false;
        }

        return super.clickMenuButton(
                player,
                id
        );
    }

    @Override
    public boolean stillValid(
            Player player
    ) {
        return player.level().isClientSide()
                || this.hasOpeningPhone(player);
    }

    @Override
    public void removed(
            Player player
    ) {
        if (
                !player.level().isClientSide()
                        && player
                        instanceof ServerPlayer serverPlayer
        ) {
            ItemStack openingPhone =
                    this.getOpeningPhone(
                            serverPlayer
                    );

            if (!openingPhone.isEmpty()) {
                VocoCallerNetwork.writePhonebook(
                        serverPlayer,
                        openingPhone,
                        this.phoneHex,
                        this.getBackend()
                                .toPhonebook()
                );
            }
        }

        super.removed(player);
    }

    @Override
    public VocoCallerBackend getBackend() {
        return (VocoCallerBackend)
                super.getBackend();
    }

    private boolean hasOpeningPhone(
            Player player
    ) {
        return !this.getOpeningPhone(player)
                .isEmpty();
    }

    private ItemStack getOpeningPhone(
            Player player
    ) {
        Inventory inventory =
                player.getInventory();

        if (
                this.openingSlot < 0
                        || this.openingSlot
                        >= inventory.getContainerSize()
                        || this.openingPhoneReference.isEmpty()
        ) {
            return ItemStack.EMPTY;
        }

        ItemStack phone =
                inventory.getItem(
                        this.openingSlot
                );

        if (phone != this.openingPhoneReference) {
            return ItemStack.EMPTY;
        }

        if (
                !(phone.getItem()
                        instanceof OpenVocoCallerItem)
        ) {
            return ItemStack.EMPTY;
        }

        ItemStack sim =
                OpenVocoCallerItem.getSim(phone);

        if (
                sim.isEmpty()
                        || !SimCardItem.hasStoredHex(sim)
                        || OpenVocoCallerItem.getSimHex(phone)
                        != this.phoneHex
        ) {
            return ItemStack.EMPTY;
        }

        return phone;
    }

    private static int findExactInventorySlot(
            ServerPlayer player,
            ItemStack phone
    ) {
        Inventory inventory =
                player.getInventory();

        for (
                int slot = 0;
                slot < inventory.getContainerSize();
                slot++
        ) {
            if (inventory.getItem(slot) == phone) {
                return slot;
            }
        }

        return -1;
    }

    private static int[] readAddresses(
            RegistryFriendlyByteBuf buffer
    ) {
        int[] result =
                new int[VocoCallerPhonebook.ROW_COUNT];

        for (
                int row = 0;
                row < result.length;
                row++
        ) {
            result[row] =
                    buffer.readInt();
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