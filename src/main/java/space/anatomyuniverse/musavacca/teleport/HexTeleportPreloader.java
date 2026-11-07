package space.anatomyuniverse.musavacca.teleport;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.network.protocol.game.ClientboundSetChunkCacheCenterPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;

public final class HexTeleportPreloader {
    public static final int PRELOAD_RADIUS = 2;
    private static final int PORTAL_TICKET_RADIUS = PRELOAD_RADIUS + 1;

    private HexTeleportPreloader() {}

    public static void prepare(Entity entity, ServerLevel targetLevel, Vec3 targetPosition) {
        if (entity == null || targetLevel == null || targetPosition == null) {
            return;
        }
        BlockPos targetBlockPos = BlockPos.containing(targetPosition);
        int chunkX = targetBlockPos.getX() >> 4;
        int chunkZ = targetBlockPos.getZ() >> 4;
        //? if >=26.1 {
        /*ChunkPos targetChunk = ChunkPos.containing(targetBlockPos);
        *///?} else {
        ChunkPos targetChunk = new ChunkPos(targetBlockPos);
        //?}
        boolean sameDimension = entity.level() == targetLevel;
        if (sameDimension && isAlreadyNearby(entity, chunkX, chunkZ)) {
            return;
        }
        //? if >=1.21.5 {
        targetLevel.getChunkSource().addTicketWithRadius(TicketType.PORTAL, targetChunk, PORTAL_TICKET_RADIUS);
        //?} else {
        /*targetLevel.getChunkSource().addRegionTicket(TicketType.PORTAL, targetChunk, PORTAL_TICKET_RADIUS, targetBlockPos);
        *///?}
        ServerPlayer clientPlayer = sameDimension && entity instanceof ServerPlayer player ? player : null;
        if (clientPlayer != null) {
            clientPlayer.connection.send(new ClientboundSetChunkCacheCenterPacket(chunkX, chunkZ));
        }
        for (int radius = 0; radius <= PRELOAD_RADIUS; radius++) {
            for (int offsetX = -radius; offsetX <= radius; offsetX++) {
                for (int offsetZ = -radius; offsetZ <= radius; offsetZ++) {
                    if (Math.max(Math.abs(offsetX), Math.abs(offsetZ)) != radius) {
                        continue;
                    }
                    LevelChunk chunk = targetLevel.getChunk(chunkX + offsetX, chunkZ + offsetZ);
                    if (clientPlayer == null) continue;
                    clientPlayer.connection.send(new ClientboundLevelChunkWithLightPacket(chunk, targetLevel.getLightEngine(), null, null));
                }
            }
        }
    }

    private static boolean isAlreadyNearby(Entity entity, int chunkX, int chunkZ) {
        BlockPos source = entity.blockPosition();
        return Math.abs((source.getX() >> 4) - chunkX) <= PRELOAD_RADIUS && Math.abs((source.getZ() >> 4) - chunkZ) <= PRELOAD_RADIUS;
    }
}
