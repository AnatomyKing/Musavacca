package space.anatomyuniverse.musavacca.door;

import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import space.anatomyuniverse.musavacca.block.custom.MusavaccaPortalDoorBlock;
import space.anatomyuniverse.musavacca.block.entity.custom.MusavaccaPortalDoorBlockEntity;
import space.anatomyuniverse.musavacca.teleport.HexTeleportAddressNetwork;
import space.anatomyuniverse.musavacca.teleport.HexTeleportDirectory;

public final class MusavaccaDoorTeleportNetwork {

    private MusavaccaDoorTeleportNetwork() {}

    public static void refresh(
            MusavaccaPortalDoorBlockEntity door
    ) {
        if (
                !(door.getLevel()
                        instanceof ServerLevel level)
        ) {
            return;
        }

        BlockPos lowerPos =
                door.getBlockPos();

        BlockState state =
                level.getBlockState(
                        lowerPos
                );

        /*
         * A door with only a hex address is NOT pending.
         *
         * Registration starts only when the complete portal state is:
         *
         * LIT=true
         * LIT_PORTAL=true
         * PORTAL=true
         */
        if (
                !door.hasHexColor()
                        || !(state.getBlock()
                        instanceof MusavaccaPortalDoorBlock)
                        || state.getValue(
                        MusavaccaPortalDoorBlock.HALF
                )
                        != DoubleBlockHalf.LOWER
                        || !state.getValue(
                        MusavaccaPortalDoorBlock.LIT
                )
                        || !state.getValue(
                        MusavaccaPortalDoorBlock.LIT_PORTAL
                )
                        || !state.getValue(
                        MusavaccaPortalDoorBlock.PORTAL
                )
        ) {
            removeDoor(
                    level,
                    lowerPos
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
                        .doorOwnerKey(
                                level.dimension()
                                        .location(),
                                lowerPos
                        );

        HexTeleportDirectory.DoorEndpoint previous =
                directory
                        .getDoorEndpointByOwner(
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
                                door.getHexColor()
                        );

        directory.registerDoorEndpoint(
                ownerKey,
                resolvedHex,
                level.dimension()
                        .location(),
                lowerPos
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

    public static void removeDoor(
            ServerLevel level,
            BlockPos lowerPos
    ) {
        if (
                level == null
                        || lowerPos == null
        ) {
            return;
        }

        String ownerKey =
                HexTeleportDirectory
                        .doorOwnerKey(
                                level.dimension()
                                        .location(),
                                lowerPos
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
                        .getDoorEndpointByOwner(
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
