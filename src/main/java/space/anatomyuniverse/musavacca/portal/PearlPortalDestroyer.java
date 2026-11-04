package space.anatomyuniverse.musavacca.portal;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

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
}
