package space.anatomyuniverse.musavacca.teleport;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoPostCandleLogic;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoReceptorLogic.ReceptorPosition;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoTableCandleLogic;

import java.util.UUID;

public final class HexTeleportAddressNetwork {

    private HexTeleportAddressNetwork() {}

    public static void releaseEndpoint(
            MinecraftServer server,
            UUID endpointId
    ) {
        if (
                server == null
                        || endpointId == null
        ) {
            return;
        }

        HexTeleportDirectory directory =
                HexTeleportDirectory.get(
                        server
                );

        HexTeleportDirectory.Endpoint endpoint =
                directory
                        .getEndpoint(
                                endpointId
                        )
                        .orElse(null);

        directory.removeEndpoint(
                endpointId
        );

        if (endpoint != null) {
            promotePendingForHex(
                    server,
                    endpoint.hexColor()
            );
        }
    }

    public static void releaseOwner(
            MinecraftServer server,
            String ownerKey
    ) {
        if (
                server == null
                        || ownerKey == null
                        || ownerKey.isBlank()
        ) {
            return;
        }

        HexTeleportDirectory directory =
                HexTeleportDirectory.get(
                        server
                );

        HexTeleportDirectory.Endpoint activeEndpoint =
                directory
                        .getEndpointByOwner(
                                ownerKey
                        )
                        .orElse(null);

        HexTeleportDirectory.DoorEndpoint doorEndpoint =
                directory
                        .getDoorEndpointByOwner(
                                ownerKey
                        )
                        .orElse(null);

        /*
         * removeOwner also removes a queued Voco claim.
         *
         * removeDoorOwner handles either an active linked door
         * or the single waiting door for an address.
         */
        directory.removeOwner(
                ownerKey
        );

        directory.removeDoorOwner(
                ownerKey
        );

        if (activeEndpoint != null) {
            promotePendingForHex(
                    server,
                    activeEndpoint.hexColor()
            );
        }

        if (doorEndpoint != null) {
            promotePendingForHex(
                    server,
                    doorEndpoint.hexColor()
            );
        }
    }

    public static void addressChanged(
            MinecraftServer server,
            int previousHex,
            int newHex
    ) {
        if (server == null) {
            return;
        }

        int previous =
                HexTeleportDirectory
                        .normalizeHex(
                                previousHex
                        );

        int next =
                HexTeleportDirectory
                        .normalizeHex(
                                newHex
                        );

        if (previous == next) {
            return;
        }

        promotePendingForHex(
                server,
                previous
        );
    }

    public static void promotePendingForHex(
            MinecraftServer server,
            int hexColor
    ) {
        if (server == null) {
            return;
        }

        HexTeleportDirectory directory =
                HexTeleportDirectory.get(
                        server
                );

        int hex =
                HexTeleportDirectory
                        .normalizeHex(
                                hexColor
                        );

        /*
         * A linked/waiting door owns the address.
         *
         * Any active endpoint also owns the address.
         */
        if (
                directory.isDoorHexReserved(
                        hex
                )
                        || !directory
                        .getEndpointsByHex(
                                hex
                        )
                        .isEmpty()
        ) {
            return;
        }

        while (true) {
            HexTeleportDirectory.Endpoint pending =
                    directory
                            .getFirstPendingEndpointByHex(
                                    hex
                            )
                            .orElse(null);

            if (pending == null) {
                return;
            }

            /*
             * Pull it from the queue first.
             *
             * Refreshing the actual block then performs a fresh
             * registration if that block is still valid.
             */
            directory.removePendingEndpoint(
                    pending.endpointId()
            );

            ServerLevel level =
                    levelFor(
                            server,
                            pending
                    );

            if (level == null) {
                continue;
            }

            HexTeleportResolver
                    .keepChunkLoaded(
                            level,
                            pending.ownerPos()
                    );

            refreshPendingCandidate(
                    level,
                    pending
            );

            /*
             * If refreshing successfully claimed the address,
             * promotion is complete.
             *
             * Otherwise the candidate was stale and we continue
             * to the next waiting endpoint.
             */
            if (
                    directory.isDoorHexReserved(
                            hex
                    )
                            || !directory
                            .getEndpointsByHex(
                                    hex
                            )
                            .isEmpty()
            ) {
                return;
            }
        }
    }

    private static ServerLevel levelFor(
            MinecraftServer server,
            HexTeleportDirectory.Endpoint endpoint
    ) {
        if (
                endpoint.dimensionId()
                        == null
        ) {
            return null;
        }

        return server.getLevel(
                ResourceKey.create(
                        Registries.DIMENSION,
                        endpoint.dimensionId()
                )
        );
    }

    private static void refreshPendingCandidate(
            ServerLevel level,
            HexTeleportDirectory.Endpoint endpoint
    ) {
        switch (endpoint.kind()) {
            case VOCO_TABLE_RECEPTOR_CORNER ->
                    VocoTableCandleLogic
                            .syncPortalStateFromCandles(
                                    level,
                                    endpoint.ownerPos(),
                                    ReceptorPosition.byId(
                                            endpoint.slotId()
                                    )
                            );

            case VOCO_POST_RECEPTOR_CORNER ->
                    VocoPostCandleLogic
                            .refreshPortalAt(
                                    level,
                                    endpoint.ownerPos()
                            );

            case PEARL_PORTAL -> {
                /*
                 * Pearl portals never live in the Voco waiting queue.
                 */
            }
        }
    }
}
