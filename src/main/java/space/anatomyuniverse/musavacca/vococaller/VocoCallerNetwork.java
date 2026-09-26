package space.anatomyuniverse.musavacca.vococaller;

import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

import space.anatomyuniverse.musavacca.component.ModDataComponents;
import space.anatomyuniverse.musavacca.effect.ModMobEffects;
import space.anatomyuniverse.musavacca.effect.playerstatus.PlayerStatusCallerPayload;
import space.anatomyuniverse.musavacca.economy.EconomyConfig;
import space.anatomyuniverse.musavacca.economy.TeleportEconomy;
import space.anatomyuniverse.musavacca.item.custom.OpenVocoCallerItem;
import space.anatomyuniverse.musavacca.item.custom.SimCardItem;
import space.anatomyuniverse.musavacca.teleport.HexTeleportAddressNetwork;
import space.anatomyuniverse.musavacca.teleport.HexTeleportDirectory;
import space.anatomyuniverse.musavacca.teleport.HexTeleportResolver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class VocoCallerNetwork {

    private static final int PLAYER_STATUS_DURATION_TICKS = 30 * 20;

    private static final Map<UUID, PendingCall> PENDING_CALLS = new HashMap<>();

    private VocoCallerNetwork() {}

    public static HexTeleportDirectory.Result activate(
            ServerPlayer player,
            ItemStack sim
    ) {
        if (
                player == null
                        || sim.isEmpty()
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
                        || sim.isEmpty()
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
                .getPhoneRegistrationByHex(
                        hex
                )
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
                        || sim.isEmpty()
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
                OpenVocoCallerItem.getSim(
                        phone
                );

        if (
                sim.isEmpty()
                        || !SimCardItem.hasStoredHex(
                        sim
                )
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
                        != (expectedPhoneHex
                        & 0xFFFFFF)
        ) {
            return false;
        }

        sim.set(
                ModDataComponents
                        .VOCO_CALLER_PHONEBOOK
                        .get(),
                phonebook == null
                        ? VocoCallerPhonebook
                          .EMPTY_PHONEBOOK
                        : phonebook
        );

        OpenVocoCallerItem.setSim(
                phone,
                sim
        );

        player.getInventory()
                .setChanged();

        return true;
    }

    public static boolean teleportToPhone(
            ServerPlayer caller,
            HexTeleportDirectory.PhoneRegistration registration
    ) {
        return teleportToPhone(caller, registration, EconomyConfig.vocoCallerTeleportCost());
    }

    public static boolean teleportToPhone(
            ServerPlayer caller,
            HexTeleportDirectory.PhoneRegistration registration,
            int cost
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

        if (PENDING_CALLS.values().stream()
                .anyMatch(call -> call.callerId().equals(caller.getUUID()))) {
            return false;
        }

        for (
                ServerPlayer candidate
                : server.getPlayerList()
                .getPlayers()
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

            if (!caller.isAlive() || !candidate.isAlive()
                    || PENDING_CALLS.containsKey(candidate.getUUID())
                    || candidate.hasEffect(ModMobEffects.PLAYER_STATUS)) {
                return false;
            }

            if (!candidate.addEffect(
                    new MobEffectInstance(
                            ModMobEffects.PLAYER_STATUS,
                            PLAYER_STATUS_DURATION_TICKS,
                            0,
                            false,
                            false,
                            true
                    )
            ) || !candidate.hasEffect(ModMobEffects.PLAYER_STATUS)) {
                return false;
            }

            PENDING_CALLS.put(
                    candidate.getUUID(),
                    new PendingCall(
                            caller.getUUID(),
                            registration.hexColor(),
                            cost,
                            server.overworld().getGameTime() + PLAYER_STATUS_DURATION_TICKS,
                            candidate.getEffect(ModMobEffects.PLAYER_STATUS)
                    )
            );

            PacketDistributor.sendToPlayer(
                    candidate,
                    new PlayerStatusCallerPayload(
                            caller.getUUID(),
                            caller.getName().getString()
                    )
            );

            return false;
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

    public static boolean answerCall(ServerPlayer recipient, boolean accepted) {
        PendingCall call = PENDING_CALLS.remove(recipient.getUUID());
        boolean valid = accepted && call != null && isValidCall(recipient, call);

        recipient.removeEffect(ModMobEffects.PLAYER_STATUS);
        PacketDistributor.sendToPlayer(recipient, PlayerStatusCallerPayload.CLEAR);

        if (!valid) {
            return false;
        }

        MinecraftServer server = recipient.level().getServer();
        ServerPlayer caller = server.getPlayerList().getPlayer(call.callerId());
        ServerLevel targetLevel = server.getLevel(recipient.level().dimension());

        if (caller == null || targetLevel == null) {
            return false;
        }

        return TeleportEconomy.tryTeleport(caller, call.cost(), () -> {
            HexTeleportResolver.teleportToTarget(
                    caller,
                    targetLevel,
                    recipient.position(),
                    recipient.getYRot(),
                    recipient.getXRot()
            );
            return true;
        });
    }

    public static boolean tickCall(ServerPlayer recipient) {
        PendingCall call = PENDING_CALLS.get(recipient.getUUID());

        if (call == null) {
            return false;
        }

        if (!isValidCall(recipient, call)) {
            answerCall(recipient, false);
        }

        return true;
    }

    private static boolean isValidCall(ServerPlayer recipient, PendingCall call) {
        MinecraftServer server = recipient.level().getServer();

        if (server == null || !recipient.isAlive()
                || recipient.getEffect(ModMobEffects.PLAYER_STATUS) != call.effect()
                || server.overworld().getGameTime() >= call.expiresAt()) {
            return false;
        }

        ServerPlayer caller = server.getPlayerList().getPlayer(call.callerId());

        return caller != null && caller.isAlive()
                && isActive(server, call.phoneHex())
                && carriesPhone(recipient, call.phoneHex());
    }

    public static void cancelCalls(ServerPlayer player) {
        MinecraftServer server = player.level().getServer();

        if (server == null) {
            return;
        }

        for (var entry : new ArrayList<>(PENDING_CALLS.entrySet())) {
            if (entry.getKey().equals(player.getUUID())
                    || entry.getValue().callerId().equals(player.getUUID())) {
                PENDING_CALLS.remove(entry.getKey());
                ServerPlayer recipient = server.getPlayerList().getPlayer(entry.getKey());

                if (recipient != null) {
                    recipient.removeEffect(ModMobEffects.PLAYER_STATUS);
                    PacketDistributor.sendToPlayer(recipient, PlayerStatusCallerPayload.CLEAR);
                }
            }
        }
    }

    public static void clearCalls(MinecraftServer server) {
        var recipients = new ArrayList<>(PENDING_CALLS.keySet());
        PENDING_CALLS.clear();

        for (UUID id : recipients) {
            ServerPlayer recipient = server.getPlayerList().getPlayer(id);

            if (recipient != null) {
                recipient.removeEffect(ModMobEffects.PLAYER_STATUS);
                PacketDistributor.sendToPlayer(recipient, PlayerStatusCallerPayload.CLEAR);
            }
        }
    }

    private record PendingCall(
            UUID callerId,
            int phoneHex,
            int cost,
            long expiresAt,
            MobEffectInstance effect
    ) {}
}
