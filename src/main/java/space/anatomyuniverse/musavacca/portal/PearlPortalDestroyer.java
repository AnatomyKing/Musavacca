package space.anatomyuniverse.musavacca.portal;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import space.anatomyuniverse.musavacca.block.entity.custom.PearlPortalBlockEntity;

import java.util.UUID;

public final class PearlPortalDestroyer {
    private PearlPortalDestroyer() {}

    private static final ThreadLocal<Boolean> DESTROYING = ThreadLocal.withInitial(() -> false);

    public static boolean isDestroyingPortal() {
        return DESTROYING.get();
    }

    public static void destroyPortalFromAnyTile(ServerLevel level, BlockPos startPos, UUID portalId) {
        if (portalId == null || DESTROYING.get()) return;

        DESTROYING.set(true);

        try {
            PearlPortalNetwork.removePortal(level, portalId);
        } finally {
            DESTROYING.set(false);
        }
    }

    public static void destroyPortalFromAnyLoadedBlock(ServerLevel level, BlockPos startPos) {
        if (DESTROYING.get()) return;

        if (level.getBlockEntity(startPos) instanceof PearlPortalBlockEntity portalBlockEntity
                && portalBlockEntity.isValidPortalTile()) {
            destroyPortalFromAnyTile(level, startPos, portalBlockEntity.getPortalId());
            return;
        }

        PearlPortalNetwork.LoadedPortal loadedPortal = PearlPortalNetwork
                .getLoadedPortalAt(level, startPos)
                .orElse(null);

        if (loadedPortal != null) {
            destroyPortalFromAnyTile(level, startPos, loadedPortal.portalId());
        }
    }
}