package space.anatomyuniverse.musavacca.portal;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import space.anatomyuniverse.musavacca.teleport.HexTeleportDirectory;
import space.anatomyuniverse.musavacca.teleport.HexTeleportResolver;

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
        return HexTeleportResolver
                .resolveLinkedPortal(sourceLevel, sourcePortalId)
                .map(resolved -> {
                    HexTeleportDirectory.Endpoint endpoint = resolved.endpoint();

                    return new ResolvedPortal(
                            endpoint.endpointId(),
                            resolved.level(),
                            endpoint.portalShape(),
                            endpoint.hexColor()
                    );
                });
    }

    public static void keepDestinationAlive(Entity teleportedEntity, BlockPos destinationPos) {
        teleportedEntity.placePortalTicket(destinationPos);
    }
}