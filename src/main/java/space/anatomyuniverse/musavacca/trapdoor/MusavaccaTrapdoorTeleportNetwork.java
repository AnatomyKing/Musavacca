package space.anatomyuniverse.musavacca.trapdoor;

import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import space.anatomyuniverse.musavacca.block.custom.MusavaccaPortalTrapdoorBlock;
import space.anatomyuniverse.musavacca.block.entity.custom.MusavaccaPortalTrapdoorBlockEntity;
import space.anatomyuniverse.musavacca.teleport.HexTeleportAddressNetwork;
import space.anatomyuniverse.musavacca.teleport.HexTeleportDirectory;

public final class MusavaccaTrapdoorTeleportNetwork {

    private MusavaccaTrapdoorTeleportNetwork() {}

    public static void refresh(
            MusavaccaPortalTrapdoorBlockEntity trapdoor
    ) {
        if (
                !(trapdoor.getLevel()
                        instanceof ServerLevel level)
        ) {
            return;
        }

        BlockPos trapdoorPos =
                trapdoor.getBlockPos();

        BlockState state =
                level.getBlockState(
                        trapdoorPos
                );

        if (
                !trapdoor.hasHexColor()
                        || !(state.getBlock()
                        instanceof MusavaccaPortalTrapdoorBlock)
                        || !state.getValue(
                        MusavaccaPortalTrapdoorBlock.LIT
                )
                        || !state.getValue(
                        MusavaccaPortalTrapdoorBlock.LIT_PORTAL
                )
                        || !state.getValue(
                        MusavaccaPortalTrapdoorBlock.PORTAL
                )
        ) {
            removeTrapdoor(
                    level,
                    trapdoorPos
            );

            return;
        }

        MinecraftServer server =
                level.getServer();

        HexTeleportDirectory directory =
                HexTeleportDirectory.get(
                        server
                );

        String ownerKey =
                HexTeleportDirectory
                        .trapdoorOwnerKey(
                                level.dimension()
                                        .location(),
                                trapdoorPos
                        );

        HexTeleportDirectory.DoorEndpoint previous =
                directory
                        .getTrapdoorEndpointByOwner(
                                ownerKey
                        )
                        .orElse(null);

        int previousHex =
                previous == null
                        ? -1
                        : previous.hexColor();

        int resolvedHex =
                HexTeleportDirectory
                        .normalizeHex(
                                trapdoor.getHexColor()
                        );

        directory.registerTrapdoorEndpoint(
                ownerKey,
                resolvedHex,
                level.dimension()
                        .location(),
                trapdoorPos
        );

        if (
                previousHex >= 0
                        && previousHex != resolvedHex
        ) {
            HexTeleportAddressNetwork
                    .addressChanged(
                            server,
                            previousHex,
                            resolvedHex
                    );
        }
    }

    public static void removeTrapdoor(
            ServerLevel level,
            BlockPos trapdoorPos
    ) {
        if (
                level == null
                        || trapdoorPos == null
        ) {
            return;
        }

        String ownerKey =
                HexTeleportDirectory
                        .trapdoorOwnerKey(
                                level.dimension()
                                        .location(),
                                trapdoorPos
                        );

        HexTeleportAddressNetwork
                .releaseOwner(
                        level.getServer(),
                        ownerKey
                );
    }

    public static void removeStaleEndpoint(
            MinecraftServer server,
            HexTeleportDirectory.DoorEndpoint endpoint
    ) {
        if (
                server == null
                        || endpoint == null
        ) {
            return;
        }

        HexTeleportDirectory directory =
                HexTeleportDirectory.get(
                        server
                );

        HexTeleportDirectory.DoorEndpoint current =
                directory
                        .getTrapdoorEndpointByOwner(
                                endpoint.ownerKey()
                        )
                        .orElse(null);

        if (
                current == null
                        || current.hexColor()
                        != endpoint.hexColor()
                        || !current.dimensionId()
                        .equals(
                                endpoint.dimensionId()
                        )
                        || !current.ownerPos()
                        .equals(
                                endpoint.ownerPos()
                        )
        ) {
            return;
        }

        HexTeleportAddressNetwork
                .releaseOwner(
                        server,
                        endpoint.ownerKey()
                );
    }
}
