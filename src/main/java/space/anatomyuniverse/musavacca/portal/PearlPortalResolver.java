// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/portal/PearlPortalResolver.java
package space.anatomyuniverse.musavacca.portal;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import space.anatomyuniverse.musavacca.block.ModBlocks;
import space.anatomyuniverse.musavacca.block.entity.custom.PearlPortalBlockEntity;

import java.util.Optional;
import java.util.UUID;

public final class PearlPortalResolver {
    private PearlPortalResolver() {}

    public record ResolvedPortal(
            UUID portalId,
            ServerLevel level,
            PearlPortalFrame.Shape shape,
            int hexColor
    ) {
        public Vec3 center() {
            return this.shape.center();
        }

        public BlockPos centerBlockPos() {
            return BlockPos.containing(this.center());
        }
    }

    public static Optional<ResolvedPortal> resolveLinkedPortal(ServerLevel sourceLevel, UUID sourcePortalId) {
        MinecraftServer server = sourceLevel.getServer();
        PearlPortalDirectory directory = PearlPortalDirectory.get(server);

        UUID targetPortalId = directory.getLinkedPortalId(sourcePortalId).orElse(null);
        if (targetPortalId == null) {
            return Optional.empty();
        }

        Optional<ResolvedPortal> loadedPortal = resolveLoadedPortal(targetPortalId);
        if (loadedPortal.isPresent()) {
            return loadedPortal;
        }

        PearlPortalDirectory.Endpoint endpoint = directory.getEndpoint(targetPortalId).orElse(null);
        if (endpoint == null) {
            return Optional.empty();
        }

        ResourceKey<Level> targetDimension = ResourceKey.create(Registries.DIMENSION, endpoint.dimensionId());
        ServerLevel targetLevel = server.getLevel(targetDimension);
        if (targetLevel == null) {
            return Optional.empty();
        }

        BlockPos originPos = endpoint.originPos();

        keepPortalChunkLoaded(targetLevel, originPos);

        Optional<ResolvedPortal> resolvedFromOrigin = resolveFromOriginBlockEntity(
                targetLevel,
                targetPortalId,
                originPos
        );

        if (resolvedFromOrigin.isPresent()) {
            return resolvedFromOrigin;
        }

        PearlPortalNetwork.removePortal(targetLevel, targetPortalId);

        return Optional.empty();
    }

    private static Optional<ResolvedPortal> resolveLoadedPortal(UUID targetPortalId) {
        PearlPortalNetwork.LoadedPortal portal = PearlPortalNetwork
                .getLoadedServerPortal(targetPortalId)
                .orElse(null);

        if (portal == null) {
            return Optional.empty();
        }

        BlockPos originPos = portal.shape().minCorner();

        keepPortalChunkLoaded(portal.level(), originPos);

        Optional<ResolvedPortal> resolvedFromOrigin = resolveFromOriginBlockEntity(
                portal.level(),
                targetPortalId,
                originPos
        );

        if (resolvedFromOrigin.isPresent()) {
            return resolvedFromOrigin;
        }

        PearlPortalNetwork.removePortal(portal.level(), targetPortalId);

        return Optional.empty();
    }

    private static Optional<ResolvedPortal> resolveFromOriginBlockEntity(
            ServerLevel level,
            UUID expectedPortalId,
            BlockPos originPos
    ) {
        if (!level.getBlockState(originPos).is(ModBlocks.PEARL_PORTAL.get())) {
            return Optional.empty();
        }

        if (!(level.getBlockEntity(originPos) instanceof PearlPortalBlockEntity portalBlockEntity)) {
            return Optional.empty();
        }

        if (!portalBlockEntity.isValidPortalTile()) {
            return Optional.empty();
        }

        if (!expectedPortalId.equals(portalBlockEntity.getPortalId())) {
            return Optional.empty();
        }

        PearlPortalNetwork.registerPortalBlock(portalBlockEntity);

        return Optional.of(fromBlockEntity(level, portalBlockEntity));
    }

    private static void keepPortalChunkLoaded(ServerLevel level, BlockPos pos) {
        ChunkPos chunkPos = new ChunkPos(pos);

        level.getChunkSource().addTicketWithRadius(
                TicketType.PORTAL,
                chunkPos,
                3
        );

        level.getChunk(chunkPos.x, chunkPos.z);
    }

    private static ResolvedPortal fromBlockEntity(ServerLevel level, PearlPortalBlockEntity portalBlockEntity) {
        return new ResolvedPortal(
                portalBlockEntity.getPortalId(),
                level,
                portalBlockEntity.getPortalShape(),
                portalBlockEntity.getHexColor()
        );
    }

    public static void keepDestinationAlive(Entity teleportedEntity, BlockPos destinationPos) {
        teleportedEntity.placePortalTicket(destinationPos);
    }
}