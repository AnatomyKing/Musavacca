package space.anatomyuniverse.musavacca.portal;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import space.anatomyuniverse.musavacca.teleport.HexTeleportDirectory;
import space.anatomyuniverse.musavacca.teleport.HexTeleportResolver;
import space.anatomyuniverse.musavacca.block.entity.custom.PearlPortalBlockEntity;
import java.util.Optional;
import java.util.UUID;

public final class PearlPortalResolver {
    private PearlPortalResolver() {}
    public record ResolvedPortal(UUID portalId, ServerLevel level, PearlPortalFrame.Shape shape, int hexColor) {
        public Vec3 center() {
            return this.shape.center();
        }
    }

    public static Optional<ResolvedPortal> resolveLinkedPortal(ServerLevel sourceLevel, UUID sourcePortalId) {
        return HexTeleportResolver
                .resolveLinkedPortal(sourceLevel, sourcePortalId)
                .flatMap(resolved -> {
                    HexTeleportDirectory.Endpoint endpoint = resolved.endpoint();
                    if (!(resolved.level().getBlockEntity(endpoint.ownerPos()) instanceof PearlPortalBlockEntity portal))
                        return Optional.empty();
                    return Optional.of(new ResolvedPortal(endpoint.endpointId(), resolved.level(), portal.getPortalShape(), endpoint.hexColor()));
                });
    }

    public static void keepDestinationAlive(Entity teleportedEntity, BlockPos destinationPos) {
        teleportedEntity.placePortalTicket(destinationPos);
    }
}
