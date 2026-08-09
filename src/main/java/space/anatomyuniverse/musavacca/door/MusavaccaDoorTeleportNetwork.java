package space.anatomyuniverse.musavacca.door;

import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import space.anatomyuniverse.musavacca.block.custom.MusavaccaPortalDoorBlock;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoTeleportLogic;
import space.anatomyuniverse.musavacca.block.entity.custom.MusavaccaPortalDoorBlockEntity;
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

        if (
                !door.hasHexColor()
                        || !(state.getBlock()
                        instanceof MusavaccaPortalDoorBlock)
                        || state.getValue(
                        MusavaccaPortalDoorBlock.HALF
                )
                        != DoubleBlockHalf.LOWER
        ) {
            removeDoor(
                    level,
                    lowerPos
            );

            return;
        }

        HexTeleportDirectory directory =
                HexTeleportDirectory.get(
                        level.getServer()
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
            promoteVocoIfAddressBecameFree(
                    level,
                    previousHex
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

        HexTeleportDirectory directory =
                HexTeleportDirectory.get(
                        level.getServer()
                );

        String ownerKey =
                HexTeleportDirectory
                        .doorOwnerKey(
                                level.dimension()
                                        .location(),
                                lowerPos
                        );

        HexTeleportDirectory.DoorEndpoint removed =
                directory
                        .removeDoorOwner(
                                ownerKey
                        )
                        .orElse(null);

        if (removed == null) {
            return;
        }

        promoteVocoIfAddressBecameFree(
                level,
                removed.hexColor()
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

        HexTeleportDirectory.DoorEndpoint removed =
                directory
                        .removeDoorOwner(
                                endpoint.ownerKey()
                        )
                        .orElse(null);

        if (removed == null) {
            return;
        }

        promoteVocoIfAddressBecameFree(
                server.overworld(),
                removed.hexColor()
        );
    }

    private static void promoteVocoIfAddressBecameFree(
            ServerLevel level,
            int hexColor
    ) {
        HexTeleportDirectory directory =
                HexTeleportDirectory.get(
                        level.getServer()
                );

        int hex =
                HexTeleportDirectory.normalizeHex(
                        hexColor
                );

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

        VocoTeleportLogic.promotePendingForHex(
                level,
                hex
        );
    }
}
