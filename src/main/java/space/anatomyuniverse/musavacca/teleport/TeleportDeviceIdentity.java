package space.anatomyuniverse.musavacca.teleport;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import space.anatomyuniverse.musavacca.block.entity.custom.MusavaccaPortalDoorBlockEntity;
import space.anatomyuniverse.musavacca.block.entity.custom.MusavaccaPortalTrapdoorBlockEntity;
import space.anatomyuniverse.musavacca.block.entity.custom.VocoPostBlockEntity;
import space.anatomyuniverse.musavacca.block.entity.custom.VocoTableBlockEntity;
import java.util.UUID;

public final class TeleportDeviceIdentity {
    private TeleportDeviceIdentity() {}

    public static UUID read(String saved) {
        if (saved != null && !saved.isEmpty()) {
            try { return UUID.fromString(saved); }
            catch (IllegalArgumentException ignored) {}
        }
        return UUID.randomUUID();
    }

    public static UUID unique(ServerLevel level, BlockPos pos, UUID deviceId) {
        HexTeleportDirectory.Endpoint existing = HexTeleportDirectory.get(level.getServer())
                .getPhysicalDevice(deviceId).orElse(null);
        if (existing == null) return deviceId;
        //? if >=1.21.11 {
        /*if (existing.dimension().equals(level.dimension().identifier().toString()) && existing.ownerPos().equals(pos)) return deviceId;
        *///?} else {
        if (existing.dimension().equals(level.dimension().location().toString()) && existing.ownerPos().equals(pos)) return deviceId;
        //?}
        if (existing.dimensionId() == null) return deviceId;
        ServerLevel oldLevel = level.getServer().getLevel(ResourceKey.create(Registries.DIMENSION, existing.dimensionId()));
        if (oldLevel == null) return deviceId;
        BlockPos oldPos = existing.ownerPos();
        if (!oldLevel.getChunkSource().hasChunk(oldPos.getX() >> 4, oldPos.getZ() >> 4)) return deviceId;
        BlockEntity previous = oldLevel.getBlockEntity(oldPos);
        if (previous instanceof MusavaccaPortalDoorBlockEntity door && deviceId.equals(door.getDeviceId())
                || previous instanceof MusavaccaPortalTrapdoorBlockEntity trapdoor && deviceId.equals(trapdoor.getDeviceId())
                || previous instanceof VocoTableBlockEntity table && deviceId.equals(table.getDeviceId())
                || previous instanceof VocoPostBlockEntity post && deviceId.equals(post.getDeviceId())) return UUID.randomUUID();
        return deviceId;
    }
}
