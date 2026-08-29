package space.anatomyuniverse.musavacca.door;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import space.anatomyuniverse.musavacca.block.custom.MusavaccaPortalDoorBlock;
import space.anatomyuniverse.musavacca.block.custom.logic.MusavaccaPortalDoorHitboxes;
import space.anatomyuniverse.musavacca.block.entity.custom.MusavaccaPortalDoorBlockEntity;
import space.anatomyuniverse.musavacca.teleport.HexTeleportDirectory;
import space.anatomyuniverse.musavacca.teleport.HexTeleportResolver;

import java.util.Optional;

public final class MusavaccaDoorTeleportResolver {

    private MusavaccaDoorTeleportResolver() {}

    public record ResolvedDoor(
            String ownerKey,
            ServerLevel level,
            BlockPos pos,
            int hexColor,
            Direction facing,
            Vec3 portalCenter
    ) {}

    public static Optional<ResolvedDoor> resolveSourceDoor(
            ServerLevel level,
            BlockPos entryPos
    ) {
        BlockState entryState =
                level.getBlockState(
                        entryPos
                );

        if (
                !(entryState.getBlock()
                        instanceof MusavaccaPortalDoorBlock)
        ) {
            return Optional.empty();
        }

        BlockPos lowerPos =
                MusavaccaPortalDoorBlock
                        .lowerDoorPos(
                                entryState,
                                entryPos
                        );

        BlockState lowerState =
                level.getBlockState(
                        lowerPos
                );

        if (
                !isOpenPortalDoor(
                        lowerState
                )
        ) {
            return Optional.empty();
        }

        if (
                !(level.getBlockEntity(
                        lowerPos
                )
                        instanceof MusavaccaPortalDoorBlockEntity doorBe)
                        || !doorBe.hasHexColor()
        ) {
            return Optional.empty();
        }

        /*
         * Refreshing the source is cheap and makes its persisted claim
         * authoritative before we resolve the pair.
         */
        MusavaccaDoorTeleportNetwork.refresh(
                doorBe
        );

        String ownerKey =
                HexTeleportDirectory
                        .doorOwnerKey(
                                level.dimension()
                                        .location(),
                                lowerPos
                        );

        HexTeleportDirectory directory =
                HexTeleportDirectory.get(
                        level.getServer()
                );

        HexTeleportDirectory.DoorEndpoint registered =
                directory
                        .getDoorEndpointByOwner(
                                ownerKey
                        )
                        .orElse(null);

        if (
                registered == null
                        || registered.hexColor()
                        != HexTeleportDirectory
                        .normalizeHex(
                                doorBe.getHexColor()
                        )
        ) {
            return Optional.empty();
        }

        return Optional.of(
                resolvedDoor(
                        ownerKey,
                        level,
                        lowerPos,
                        lowerState,
                        doorBe.getHexColor()
                )
        );
    }

    public static Optional<ResolvedDoor> resolveLinkedDoor(
            ResolvedDoor source
    ) {
        /*
         * The shared HexTeleportResolver now owns:
         *
         * - persistent linked-door lookup
         * - remote dimension resolution
         * - destination chunk ticket/load
         * - stale endpoint cleanup through the shared address network
         */
        HexTeleportResolver.ResolvedDoorEndpoint loaded =
                HexTeleportResolver
                        .resolveLinkedDoor(
                                source.level(),
                                source.ownerKey()
                        )
                        .orElse(null);

        if (loaded == null) {
            return Optional.empty();
        }

        ServerLevel targetLevel =
                loaded.level();

        HexTeleportDirectory.DoorEndpoint targetEndpoint =
                loaded.endpoint();

        BlockPos targetPos =
                targetEndpoint.ownerPos();

        BlockState targetState =
                targetLevel.getBlockState(
                        targetPos
                );

        if (
                !(targetLevel.getBlockEntity(
                        targetPos
                )
                        instanceof MusavaccaPortalDoorBlockEntity targetBe)
                        || !targetBe.hasHexColor()
        ) {
            return Optional.empty();
        }

        if (
                HexTeleportDirectory.normalizeHex(
                        targetBe.getHexColor()
                )
                        != source.hexColor()
        ) {
            /*
             * The live block entity changed after persistent resolution.
             * Refresh it so the directory immediately reflects reality.
             */
            MusavaccaDoorTeleportNetwork
                    .refresh(
                            targetBe
                    );

            return Optional.empty();
        }

        /*
         * A linked door may remain registered while physically closed.
         * Registration represents its address relationship.
         * Actual traversal still requires the target door portal to be open.
         */
        if (
                !isOpenPortalDoor(
                        targetState
                )
        ) {
            return Optional.empty();
        }

        return Optional.of(
                resolvedDoor(
                        targetEndpoint.ownerKey(),
                        targetLevel,
                        targetPos,
                        targetState,
                        targetBe.getHexColor()
                )
        );
    }

    public static void keepDestinationAlive(
            Entity teleportedEntity,
            BlockPos destinationPos
    ) {
        teleportedEntity.placePortalTicket(
                destinationPos
        );
    }

    private static ResolvedDoor resolvedDoor(
            String ownerKey,
            ServerLevel level,
            BlockPos lowerPos,
            BlockState state,
            int hexColor
    ) {
        return new ResolvedDoor(
                ownerKey,
                level,
                lowerPos,
                HexTeleportDirectory.normalizeHex(
                        hexColor
                ),
                state.getValue(
                        MusavaccaPortalDoorBlock.FACING
                ),
                portalCenter(
                        lowerPos,
                        state
                )
        );
    }

    private static boolean isOpenPortalDoor(
            BlockState state
    ) {
        return state.getBlock()
                instanceof MusavaccaPortalDoorBlock
                && state.getValue(
                MusavaccaPortalDoorBlock.HALF
        )
                == DoubleBlockHalf.LOWER
                && state.getValue(
                MusavaccaPortalDoorBlock.PORTAL
        )
                && state.getValue(
                MusavaccaPortalDoorBlock.OPEN
        );
    }

    private static Vec3 portalCenter(
            BlockPos pos,
            BlockState state
    ) {
        VoxelShape panel =
                MusavaccaPortalDoorHitboxes
                        .portalPanel(
                                state
                        );

        if (panel.isEmpty()) {
            throw new IllegalStateException(
                    "Active Musavacca portal door has no portal panel."
            );
        }

        AABB bounds =
                panel.bounds();

        return new Vec3(
                pos.getX()
                        + (
                        bounds.minX
                                + bounds.maxX
                )
                        * 0.5D,
                pos.getY(),
                pos.getZ()
                        + (
                        bounds.minZ
                                + bounds.maxZ
                )
                        * 0.5D
        );
    }
}


