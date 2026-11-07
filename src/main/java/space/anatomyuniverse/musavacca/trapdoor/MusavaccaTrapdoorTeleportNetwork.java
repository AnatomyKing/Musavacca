package space.anatomyuniverse.musavacca.trapdoor;

import java.util.UUID;
import space.anatomyuniverse.musavacca.teleport.TeleportDeviceIdentity;

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

    public static void refresh(MusavaccaPortalTrapdoorBlockEntity trapdoor) {
        if (!(trapdoor.getLevel() instanceof ServerLevel level)) {
            return;
        }
        BlockPos trapdoorPos = trapdoor.getBlockPos();
        BlockState state = level.getBlockState(trapdoorPos);
        if (!trapdoor.hasHexColor() || !(state.getBlock() instanceof MusavaccaPortalTrapdoorBlock) || !state.getValue(MusavaccaPortalTrapdoorBlock.LIT)
                || !state.getValue(MusavaccaPortalTrapdoorBlock.LIT_PORTAL) || !state.getValue(MusavaccaPortalTrapdoorBlock.PORTAL)) {
            removeTrapdoor(level, trapdoor.getDeviceId());
            return;
        }
        trapdoor.setDeviceId(TeleportDeviceIdentity.unique(level, trapdoorPos, trapdoor.getDeviceId()));
        MinecraftServer server = level.getServer();
        HexTeleportDirectory directory = HexTeleportDirectory.get(server);
        String ownerKey = HexTeleportDirectory.trapdoorOwnerKey(trapdoor.getDeviceId());
        HexTeleportDirectory.DoorEndpoint previous = directory.getTrapdoorEndpointByOwner(ownerKey).orElse(null);
        int previousHex = previous == null ? -1 : previous.hexColor();
        int resolvedHex = HexTeleportDirectory.normalizeHex(trapdoor.getHexColor());
        //? if >=1.21.11 {
        /*HexTeleportDirectory.Result result = directory.registerTrapdoorEndpoint(trapdoor.getDeviceId(), resolvedHex, level.dimension().identifier(),
                trapdoorPos);
        *///?} else {
        HexTeleportDirectory.Result result = directory.registerTrapdoorEndpoint(trapdoor.getDeviceId(), resolvedHex, level.dimension().location(), trapdoorPos);
        //?}
        if (!result.success() && previousHex >= 0 && previousHex != resolvedHex) {
            trapdoor.setHexColor(previousHex);
            return;
        }
        if (result.success() && previousHex >= 0 && previousHex != resolvedHex) {
            HexTeleportAddressNetwork.addressChanged(server, previousHex, resolvedHex);
        }
    }

    public static void removeTrapdoor(ServerLevel level, UUID deviceId) {
        if (level == null || deviceId == null) return;
        HexTeleportAddressNetwork.releaseOwner(level.getServer(), HexTeleportDirectory.trapdoorOwnerKey(deviceId));
    }
}
