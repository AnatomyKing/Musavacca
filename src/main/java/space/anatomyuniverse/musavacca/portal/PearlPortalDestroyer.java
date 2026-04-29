package space.anatomyuniverse.musavacca.portal;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import space.anatomyuniverse.musavacca.block.ModBlocks;
import space.anatomyuniverse.musavacca.block.entity.custom.PearlPortalBlockEntity;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class PearlPortalDestroyer {
    private PearlPortalDestroyer() {}

    private static final int MAX_PORTAL_BLOCKS_TO_REMOVE = 21 * 21;
    private static final ThreadLocal<Boolean> DESTROYING = ThreadLocal.withInitial(() -> false);

    public static boolean isDestroyingPortal() {
        return DESTROYING.get();
    }

    public static void destroyPortalFromAnyTile(ServerLevel level, BlockPos startPos, UUID portalId) {
        if (portalId == null || DESTROYING.get()) return;

        DESTROYING.set(true);

        try {
            if (level.getBlockEntity(startPos) instanceof PearlPortalBlockEntity portalBlockEntity
                    && portalBlockEntity.isValidPortalTile()
                    && portalId.equals(portalBlockEntity.getPortalId())) {
                breakShape(level, portalBlockEntity.getPortalShape());
            } else {
                breakFromLoadedCacheOrFallback(level, startPos, portalId);
            }

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
            return;
        }

        breakConnectedPortalBlocksFallback(level, startPos);
    }

    private static void breakFromLoadedCacheOrFallback(ServerLevel level, BlockPos startPos, UUID portalId) {
        PearlPortalNetwork.LoadedPortal loadedPortal = PearlPortalNetwork
                .getLoadedServerPortal(portalId)
                .orElse(null);

        if (loadedPortal != null) {
            breakShape(level, loadedPortal.shape());
            return;
        }

        breakConnectedPortalBlocksFallback(level, startPos);
    }

    private static void breakShape(ServerLevel level, PearlPortalFrame.Shape shape) {
        shape.forEachInteriorBlock(pos -> breakPortalBlock(level, pos));
    }

    private static void breakConnectedPortalBlocksFallback(ServerLevel level, BlockPos startPos) {
        Set<BlockPos> visited = new HashSet<>();
        ArrayDeque<BlockPos> queue = new ArrayDeque<>();

        queue.add(startPos.immutable());

        while (!queue.isEmpty() && visited.size() < MAX_PORTAL_BLOCKS_TO_REMOVE) {
            BlockPos current = queue.removeFirst();

            if (!visited.add(current)) continue;
            if (!level.getBlockState(current).is(ModBlocks.PEARL_PORTAL.get())) continue;

            queue.add(current.north().immutable());
            queue.add(current.south().immutable());
            queue.add(current.east().immutable());
            queue.add(current.west().immutable());
            queue.add(current.above().immutable());
            queue.add(current.below().immutable());

            breakPortalBlock(level, current);
        }
    }

    private static void breakPortalBlock(ServerLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (!state.is(ModBlocks.PEARL_PORTAL.get())) return;

        boolean destroyed = level.destroyBlock(pos, false);

        if (!destroyed && level.getBlockState(pos).is(ModBlocks.PEARL_PORTAL.get())) {
            level.removeBlock(pos, false);
            level.removeBlockEntity(pos);
        }
    }
}