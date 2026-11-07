package space.anatomyuniverse.musavacca.portal;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import org.jetbrains.annotations.Nullable;
import space.anatomyuniverse.musavacca.block.custom.PearlPortalBlock;
import space.anatomyuniverse.musavacca.block.entity.custom.PearlPortalBlockEntity;
import space.anatomyuniverse.musavacca.teleport.HexTeleportAddressNetwork;
import space.anatomyuniverse.musavacca.teleport.HexTeleportDirectory;
import space.anatomyuniverse.musavacca.teleport.HexTeleportResolver;

public final class PearlPortalNetwork {
    private PearlPortalNetwork() {}

    private static final Map<UUID, Portal> PORTALS_BY_ID = new HashMap<>();
    private static final Map<BlockKey, UUID> PORTAL_ID_BY_BLOCK = new HashMap<>();
    private static final Map<UUID, Set<BlockKey>> LOADED_BLOCKS_BY_PORTAL_ID = new HashMap<>();
    private static final ThreadLocal<Boolean> DESTROYING = ThreadLocal.withInitial(() -> false);

    private record BlockKey(ResourceKey<Level> dimension, BlockPos pos) {}

    public record Portal(UUID portalId, ServerLevel level, PearlPortalFrame.Shape shape, int hexColor) {
        public Vec3 center() {
            return this.shape.center();
        }
    }

    public static void registerPortalBlock(PearlPortalBlockEntity portalBlockEntity) {
        if (!(portalBlockEntity.getLevel() instanceof ServerLevel serverLevel) || !portalBlockEntity.isValidPortalTile()) {
            return;
        }
        UUID portalId = portalBlockEntity.getPortalId();
        BlockKey key = blockKey(serverLevel, portalBlockEntity.getBlockPos());
        UUID oldPortalId = PORTAL_ID_BY_BLOCK.put(key, portalId);
        if (oldPortalId != null && !oldPortalId.equals(portalId)) {
            removeLoadedBlockKey(oldPortalId, key);
        }
        LOADED_BLOCKS_BY_PORTAL_ID.computeIfAbsent(portalId, ignored -> new HashSet<>()).add(key);
        PORTALS_BY_ID.put(portalId, new Portal(portalId, serverLevel, portalBlockEntity.getPortalShape(), portalBlockEntity.getHexColor()));
        if (portalBlockEntity.isOriginBlock()) {
            HexTeleportDirectory.get(serverLevel.getServer())
                    //? if >=1.21.11 {
                    /*.registerPortalEndpoint(portalId, portalBlockEntity.getHexColor(), serverLevel.dimension().identifier(),
                            portalBlockEntity.getPortalShape());
                    *///?} else {
                    .registerPortalEndpoint(portalId, portalBlockEntity.getHexColor(), serverLevel.dimension().location(), portalBlockEntity.getPortalShape());
                    //?}
        }
    }

    public static void unregisterPortalBlock(PearlPortalBlockEntity portalBlockEntity) {
        if (!(portalBlockEntity.getLevel() instanceof ServerLevel serverLevel) || !portalBlockEntity.hasPortalData()) {
            return;
        }
        UUID portalId = portalBlockEntity.getPortalId();
        BlockKey key = blockKey(serverLevel, portalBlockEntity.getBlockPos());
        PORTAL_ID_BY_BLOCK.remove(key, portalId);
        removeLoadedBlockKey(portalId, key);
    }

    public static Optional<Portal> getLoadedPortalAt(ServerLevel level, BlockPos pos) {
        UUID portalId = PORTAL_ID_BY_BLOCK.get(blockKey(level, pos));
        return portalId == null ? Optional.empty() : Optional.ofNullable(PORTALS_BY_ID.get(portalId));
    }

    public static void removePortal(ServerLevel level, UUID portalId) {
        if (level == null || portalId == null) {
            return;
        }
        removeLoadedPortal(portalId);
        HexTeleportAddressNetwork.releaseEndpoint(level.getServer(), portalId);
    }

    public static void onServerStopping(ServerStoppingEvent event) {
        PORTALS_BY_ID.clear();
        PORTAL_ID_BY_BLOCK.clear();
        LOADED_BLOCKS_BY_PORTAL_ID.clear();
    }

    public static boolean isDestroyingPortal() {
        return DESTROYING.get();
    }

    public static void destroyPortal(ServerLevel level, UUID portalId) {
        if (portalId == null || DESTROYING.get()) return;
        DESTROYING.set(true);
        try {
            removePortal(level, portalId);
        } finally {
            DESTROYING.set(false);
        }
    }

    public static Optional<Portal> resolveLinkedPortal(ServerLevel sourceLevel, UUID sourcePortalId) {
        return HexTeleportResolver.resolveLinkedPortal(sourceLevel, sourcePortalId).flatMap(resolved -> {
            HexTeleportDirectory.Endpoint endpoint = resolved.endpoint();
            if (!(resolved.level().getBlockEntity(endpoint.ownerPos()) instanceof PearlPortalBlockEntity portal)) return Optional.empty();
            return Optional.of(new Portal(endpoint.endpointId(), resolved.level(), portal.getPortalShape(), endpoint.hexColor()));
        });
    }

    @Nullable
    public static Portal findSourcePortal(ServerLevel level, BlockPos entryPos) {
        if (level.getBlockEntity(entryPos) instanceof PearlPortalBlockEntity portalBlockEntity && portalBlockEntity.isValidPortalTile()) {
            registerPortalBlock(portalBlockEntity);
            return fromBlockEntity(level, portalBlockEntity);
        }
        Portal loadedPortal = getLoadedPortalAt(level, entryPos).orElse(null);
        if (loadedPortal != null) return loadedPortal;
        BlockState state = level.getBlockState(entryPos);
        if (!(state.getBlock() instanceof PearlPortalBlock)) {
            return null;
        }
        Direction.Axis axis = state.getValue(PearlPortalBlock.AXIS);
        PearlPortalFrame.Shape shape = PearlPortalFrame.findExistingShape(level, entryPos, axis).orElse(null);
        if (shape == null) return null;
        PearlPortalBlockEntity foundTile = findAnyValidPortalTile(level, shape);
        if (foundTile == null) return null;
        registerPortalBlock(foundTile);
        return fromBlockEntity(level, foundTile);
    }

    @Nullable
    private static PearlPortalBlockEntity findAnyValidPortalTile(ServerLevel level, PearlPortalFrame.Shape shape) {
        PearlPortalBlockEntity[] found = { null };
        shape.forEachInteriorBlock(pos -> {
            if (found[0] != null) return;
            if (level.getBlockEntity(pos) instanceof PearlPortalBlockEntity portalBlockEntity && portalBlockEntity.isValidPortalTile()) {
                found[0] = portalBlockEntity;
            }
        });
        return found[0];
    }

    private static Portal fromBlockEntity(ServerLevel level, PearlPortalBlockEntity portalBlockEntity) {
        return new Portal(portalBlockEntity.getPortalId(), level, portalBlockEntity.getPortalShape(), portalBlockEntity.getHexColor());
    }

    private static void removeLoadedPortal(UUID portalId) {
        PORTALS_BY_ID.remove(portalId);
        Set<BlockKey> keys = LOADED_BLOCKS_BY_PORTAL_ID.remove(portalId);
        if (keys == null) return;
        for (BlockKey key : keys) {
            PORTAL_ID_BY_BLOCK.remove(key, portalId);
        }
    }

    private static void removeLoadedBlockKey(UUID portalId, BlockKey key) {
        Set<BlockKey> keys = LOADED_BLOCKS_BY_PORTAL_ID.get(portalId);
        if (keys == null) return;
        keys.remove(key);
        if (keys.isEmpty()) {
            LOADED_BLOCKS_BY_PORTAL_ID.remove(portalId);
            PORTALS_BY_ID.remove(portalId);
        }
    }

    private static BlockKey blockKey(ServerLevel level, BlockPos pos) {
        return new BlockKey(level.dimension(), pos.immutable());
    }
}
