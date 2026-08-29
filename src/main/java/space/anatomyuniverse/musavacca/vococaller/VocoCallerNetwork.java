package space.anatomyuniverse.musavacca.vococaller;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;
import space.anatomyuniverse.musavacca.component.ModDataComponents;
import space.anatomyuniverse.musavacca.item.custom.OpenVocoCallerItem;
import space.anatomyuniverse.musavacca.item.custom.SimCardItem;
import space.anatomyuniverse.musavacca.teleport.HexTeleportAddressNetwork;
import space.anatomyuniverse.musavacca.teleport.HexTeleportDirectory;
import space.anatomyuniverse.musavacca.teleport.HexTeleportResolver;

import java.util.List;

public final class VocoCallerNetwork {
    private VocoCallerNetwork() {}

    public static HexTeleportDirectory.Result activate(
            ServerPlayer player,
            ItemStack sim
    ) {
        if (
                player == null
                        || !(sim.getItem()
                        instanceof SimCardItem)
                        || !SimCardItem.hasStoredHex(sim)
        ) {
            return HexTeleportDirectory.Result.INVALID_OWNER;
        }

        MinecraftServer server =
                player.level().getServer();

        if (server == null) {
            return HexTeleportDirectory.Result.INVALID_OWNER;
        }

        return HexTeleportDirectory.get(server)
                .registerPhone(
                        SimCardItem.getStoredHexOrFallback(
                                sim,
                                0
                        ),
                        player.getUUID()
                );
    }

    public static boolean canEject(
            ServerPlayer player,
            ItemStack sim
    ) {
        if (
                player == null
                        || !(sim.getItem()
                        instanceof SimCardItem)
                        || !SimCardItem.hasStoredHex(sim)
        ) {
            return true;
        }

        MinecraftServer server =
                player.level().getServer();

        if (server == null) {
            return true;
        }

        int hex =
                SimCardItem.getStoredHexOrFallback(
                        sim,
                        0
                );

        return HexTeleportDirectory.get(server)
                .getPhoneRegistrationByHex(hex)
                .map(
                        registration ->
                                registration.ownerUuid()
                                        .equals(
                                                player.getUUID()
                                        )
                )
                .orElse(true);
    }

    public static void release(
            ServerPlayer player,
            ItemStack sim
    ) {
        if (
                player == null
                        || !(sim.getItem()
                        instanceof SimCardItem)
                        || !SimCardItem.hasStoredHex(sim)
        ) {
            return;
        }

        MinecraftServer server =
                player.level().getServer();

        if (server == null) {
            return;
        }

        int hex =
                SimCardItem.getStoredHexOrFallback(
                        sim,
                        0
                );

        HexTeleportDirectory.PhoneRegistration removed =
                HexTeleportDirectory.get(server)
                        .removePhoneRegistration(
                                hex,
                                player.getUUID()
                        )
                        .orElse(null);

        if (removed != null) {
            HexTeleportAddressNetwork
                    .promotePendingForHex(
                            server,
                            removed.hexColor()
                    );
        }
    }

    public static boolean isActive(
            MinecraftServer server,
            int hexColor
    ) {
        return server != null
                && HexTeleportDirectory.get(server)
                .getPhoneRegistrationByHex(
                        hexColor
                )
                .isPresent();
    }

    public static ItemStack findPhone(
            Player player,
            int hexColor
    ) {
        if (player == null) {
            return ItemStack.EMPTY;
        }

        int hex =
                HexTeleportDirectory.normalizeHex(
                        hexColor
                );

        for (
                int slot = 0;
                slot < player.getInventory()
                        .getContainerSize();
                slot++
        ) {
            ItemStack phone =
                    player.getInventory()
                            .getItem(slot);

            if (
                    phone.getItem()
                            instanceof OpenVocoCallerItem
                            && OpenVocoCallerItem
                            .getSimHex(phone)
                            == hex
            ) {
                return phone;
            }
        }

        return ItemStack.EMPTY;
    }

    public static boolean carriesPhone(
            Player player,
            int hexColor
    ) {
        return !findPhone(
                player,
                hexColor
        ).isEmpty();
    }

    public static boolean writePhonebook(
            ServerPlayer player,
            ItemStack phone,
            int expectedPhoneHex,
            VocoCallerPhonebook phonebook
    ) {
        if (
                player == null
                        || phone.isEmpty()
                        || !(phone.getItem()
                        instanceof OpenVocoCallerItem)
        ) {
            return false;
        }

        ItemStack sim =
                OpenVocoCallerItem.getSim(phone);

        if (
                sim.isEmpty()
                        || !SimCardItem.hasStoredHex(sim)
        ) {
            return false;
        }

        int actualPhoneHex =
                SimCardItem.getStoredHexOrFallback(
                        sim,
                        0
                ) & 0xFFFFFF;

        if (
                actualPhoneHex
                        != (expectedPhoneHex & 0xFFFFFF)
        ) {
            return false;
        }

        sim.set(
                ModDataComponents.VOCO_CALLER_PHONEBOOK.get(),
                phonebook == null
                        ? VocoCallerPhonebook.EMPTY_PHONEBOOK
                        : phonebook
        );
        phone.set(
                DataComponents.BUNDLE_CONTENTS,
                new BundleContents(
                        List.of(sim)
                )
        );

        player.getInventory()
                .setChanged();

        return true;
    }

    public static boolean teleportToPhone(
            ServerPlayer caller,
            HexTeleportDirectory.PhoneRegistration registration
    ) {
        if (
                caller == null
                        || registration == null
        ) {
            return false;
        }

        MinecraftServer server =
                caller.level().getServer();

        if (server == null) {
            return false;
        }

        for (
                ServerPlayer candidate
                : server.getPlayerList().getPlayers()
        ) {
            if (
                    !carriesPhone(
                            candidate,
                            registration.hexColor()
                    )
            ) {
                continue;
            }

            ServerLevel targetLevel =
                    server.getLevel(
                            candidate.level()
                                    .dimension()
                    );

            if (targetLevel == null) {
                continue;
            }

            HexTeleportResolver.teleportToTarget(
                    caller,
                    targetLevel,
                    candidate.position(),
                    candidate.getYRot(),
                    candidate.getXRot()
            );

            return true;
        }

        caller.displayClientMessage(
                Component.literal(
                        "Voco phone #"
                                + HexTeleportDirectory.toHex(
                                registration.hexColor()
                        )
                                + " is unreachable."
                ),
                true
        );

        return false;
    }
}
