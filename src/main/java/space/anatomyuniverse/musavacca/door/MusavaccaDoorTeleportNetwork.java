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
    public static void refresh(MusavaccaPortalDoorBlockEntity door) {
        if (
                !(door.getLevel()
                        instanceof ServerLevel level)
        ) {
            return;
        }
        BlockPos lowerPos = door.getBlockPos();
        BlockState state = level.getBlockState(lowerPos);
        if (
                !door.hasHexColor()
                        || !(state.getBlock()
                        instanceof MusavaccaPortalDoorBlock)
                        || state.getValue(MusavaccaPortalDoorBlock.HALF)
                        != DoubleBlockHalf.LOWER
                        || !state.getValue(MusavaccaPortalDoorBlock.LIT)
                        || !state.getValue(MusavaccaPortalDoorBlock.LIT_PORTAL)
                        || !state.getValue(MusavaccaPortalDoorBlock.PORTAL)
        ) {
            removeDoor(level, door.getDeviceId());
            return;
        }
        door.setDeviceId(space.anatomyuniverse.musavacca.teleport.TeleportDeviceIdentity.unique(level, lowerPos, door.getDeviceId()));
        MinecraftServer server = level.getServer();
        HexTeleportDirectory directory = HexTeleportDirectory.get(server);
        String ownerKey = HexTeleportDirectory.doorOwnerKey(door.getDeviceId());
        HexTeleportDirectory.DoorEndpoint previous = directory.getDoorEndpointByOwner(ownerKey).orElse(null);
        int previousHex = previous == null ? -1 : previous.hexColor();
        int resolvedHex = HexTeleportDirectory.normalizeHex(door.getHexColor());
        //? if >=1.21.11 {
        /*HexTeleportDirectory.Result result = directory.registerDoorEndpoint(door.getDeviceId(), resolvedHex, level.dimension().identifier(), lowerPos);
        *///?} else {
        HexTeleportDirectory.Result result = directory.registerDoorEndpoint(door.getDeviceId(), resolvedHex, level.dimension().location(), lowerPos);
        //?}
        if (!result.success() && previousHex >= 0 && previousHex != resolvedHex) {
            door.setHexColor(previousHex);
            return;
        }
        if (
                result.success()
                        && previousHex >= 0
                        && previousHex != resolvedHex
        ) {
            HexTeleportAddressNetwork.addressChanged(server, previousHex, resolvedHex);
        }
    }

    public static void removeDoor(ServerLevel level, java.util.UUID deviceId) {
        if (level == null || deviceId == null) return;
        HexTeleportAddressNetwork.releaseOwner(level.getServer(), HexTeleportDirectory.doorOwnerKey(deviceId));
    }
}
