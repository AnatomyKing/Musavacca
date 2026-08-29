package space.anatomyuniverse.musavacca.portal;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import space.anatomyuniverse.musavacca.block.entity.custom.PearlPortalBlockEntity;
import space.anatomyuniverse.musavacca.teleport.HexTeleportAddressNetwork;
import space.anatomyuniverse.musavacca.teleport.HexTeleportDirectory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public final class PearlPortalNetwork {
    private PearlPortalNetwork() {}

    private static final SideCache SERVER_CACHE =
            new SideCache();

    private record BlockKey(
            ResourceKey<Level> dimension,
            BlockPos pos
    ) {}

    private static final class SideCache {
        private final Map<UUID, LoadedPortal> portalsById =
                new HashMap<>();

        private final Map<BlockKey, UUID> portalIdByBlock =
                new HashMap<>();

        private final Map<UUID, Set<BlockKey>> loadedBlocksByPortalId =
                new HashMap<>();

        private void clear() {
            this.portalsById.clear();
            this.portalIdByBlock.clear();
            this.loadedBlocksByPortalId.clear();
        }
    }

    public record LoadedPortal(
            UUID portalId,
            ServerLevel level,
            PearlPortalFrame.Shape shape,
            int hexColor
    ) {}

    public static void registerPortalBlock(
            PearlPortalBlockEntity portalBlockEntity
    ) {
        if (
                !(portalBlockEntity.getLevel()
                        instanceof ServerLevel serverLevel)
                        || !portalBlockEntity.isValidPortalTile()
        ) {
            return;
        }

        UUID portalId =
                portalBlockEntity.getPortalId();

        BlockKey key =
                blockKey(
                        serverLevel,
                        portalBlockEntity.getBlockPos()
                );

        UUID oldPortalId =
                SERVER_CACHE.portalIdByBlock.put(
                        key,
                        portalId
                );

        if (
                oldPortalId != null
                        && !oldPortalId.equals(
                        portalId
                )
        ) {
            removeLoadedBlockKey(
                    SERVER_CACHE,
                    oldPortalId,
                    key
            );
        }

        SERVER_CACHE.loadedBlocksByPortalId
                .computeIfAbsent(
                        portalId,
                        ignored ->
                                new HashSet<>()
                )
                .add(
                        key
                );

        SERVER_CACHE.portalsById.put(
                portalId,
                new LoadedPortal(
                        portalId,
                        serverLevel,
                        portalBlockEntity.getPortalShape(),
                        portalBlockEntity.getHexColor()
                )
        );

        /*
         * Only the logical origin writes the persistent directory entry.
         * Other portal tiles only participate in the loaded cache.
         */
        if (
                portalBlockEntity.isOriginBlock()
        ) {
            HexTeleportDirectory.get(
                    serverLevel.getServer()
            )
                    .registerPortalEndpoint(
                            portalId,
                            portalBlockEntity.getHexColor(),
                            serverLevel.dimension()
                                    .location(),
                            portalBlockEntity.getPortalShape()
                    );
        }
    }

    public static void unregisterPortalBlock(
            PearlPortalBlockEntity portalBlockEntity
    ) {
        if (
                !(portalBlockEntity.getLevel()
                        instanceof ServerLevel serverLevel)
                        || !portalBlockEntity.hasPortalData()
        ) {
            return;
        }

        UUID portalId =
                portalBlockEntity.getPortalId();

        BlockKey key =
                blockKey(
                        serverLevel,
                        portalBlockEntity.getBlockPos()
                );

        UUID indexedPortalId =
                SERVER_CACHE.portalIdByBlock.get(
                        key
                );

        if (
                portalId.equals(
                        indexedPortalId
                )
        ) {
            SERVER_CACHE.portalIdByBlock.remove(
                    key
            );
        }

        removeLoadedBlockKey(
                SERVER_CACHE,
                portalId,
                key
        );
    }

    public static Optional<LoadedPortal> getLoadedServerPortal(
            UUID portalId
    ) {
        return portalId == null
                ? Optional.empty()
                : Optional.ofNullable(
                SERVER_CACHE.portalsById.get(
                        portalId
                )
        );
    }

    public static Optional<LoadedPortal> getLoadedPortalAt(
            ServerLevel level,
            BlockPos pos
    ) {
        UUID portalId =
                SERVER_CACHE.portalIdByBlock.get(
                        blockKey(
                                level,
                                pos
                        )
                );

        return portalId == null
                ? Optional.empty()
                : Optional.ofNullable(
                SERVER_CACHE.portalsById.get(
                        portalId
                )
        );
    }

    public static void removePortal(
            ServerLevel level,
            UUID portalId
    ) {
        if (
                level == null
                        || portalId == null
        ) {
            return;
        }

        removeLoadedPortal(
                SERVER_CACHE,
                portalId
        );

        /*
         * Pearl Portal code no longer knows anything about Voco
         * promotion. It simply releases its persistent address claim.
         *
         * The shared address network then decides whether:
         *
         * - another Pearl Portal still reserves the hex,
         * - a door still reserves it,
         * - or the address became free and a queued claim can wake.
         */
        HexTeleportAddressNetwork
                .releaseEndpoint(
                        level.getServer(),
                        portalId
                );
    }

    public static void clear() {
        SERVER_CACHE.clear();
    }

    private static void removeLoadedPortal(
            SideCache cache,
            UUID portalId
    ) {
        cache.portalsById.remove(
                portalId
        );

        Set<BlockKey> keys =
                cache.loadedBlocksByPortalId.remove(
                        portalId
                );

        if (
                keys == null
                        || keys.isEmpty()
        ) {
            return;
        }

        for (
                BlockKey key :
                keys
        ) {
            UUID indexedPortalId =
                    cache.portalIdByBlock.get(
                            key
                    );

            if (
                    portalId.equals(
                            indexedPortalId
                    )
            ) {
                cache.portalIdByBlock.remove(
                        key
                );
            }
        }
    }

    private static void removeLoadedBlockKey(
            SideCache cache,
            UUID portalId,
            BlockKey key
    ) {
        Set<BlockKey> keys =
                cache.loadedBlocksByPortalId.get(
                        portalId
                );

        if (keys == null) {
            return;
        }

        keys.remove(
                key
        );

        if (keys.isEmpty()) {
            cache.loadedBlocksByPortalId.remove(
                    portalId
            );

            cache.portalsById.remove(
                    portalId
            );
        }
    }

    private static BlockKey blockKey(
            ServerLevel level,
            BlockPos pos
    ) {
        return new BlockKey(
                level.dimension(),
                pos.immutable()
        );
    }
}


