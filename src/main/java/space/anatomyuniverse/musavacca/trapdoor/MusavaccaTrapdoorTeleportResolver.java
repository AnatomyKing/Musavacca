package space.anatomyuniverse.musavacca.trapdoor;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import space.anatomyuniverse.musavacca.block.custom.MusavaccaPortalTrapdoorBlock;
import space.anatomyuniverse.musavacca.block.custom.logic.MusavaccaPortalTrapdoorHitboxes;
import space.anatomyuniverse.musavacca.block.entity.custom.MusavaccaPortalTrapdoorBlockEntity;
import space.anatomyuniverse.musavacca.teleport.HexTeleportDirectory;
import space.anatomyuniverse.musavacca.teleport.HexTeleportResolver;

import java.util.Optional;

public final class MusavaccaTrapdoorTeleportResolver {

    private MusavaccaTrapdoorTeleportResolver() {}

    public record ResolvedTrapdoor(
            String ownerKey,
            ServerLevel level,
            BlockPos pos,
            int hexColor,
            Direction facing,
            Half half,
            Vec3 portalCenter
    ) {}

    public static Optional<ResolvedTrapdoor> resolveSourceTrapdoor(
            ServerLevel level,
            BlockPos entryPos
    ) {
        BlockState state =
                level.getBlockState(
                        entryPos
                );

        if (
                !isOpenPortalTrapdoor(
                        state
                )
        ) {
            return Optional.empty();
        }

        if (
                !(level.getBlockEntity(
                        entryPos
                )
                        instanceof MusavaccaPortalTrapdoorBlockEntity trapdoorBe)
                        || !trapdoorBe.hasHexColor()
        ) {
            return Optional.empty();
        }

        MusavaccaTrapdoorTeleportNetwork
                .refresh(
                        trapdoorBe
                );

        String ownerKey =
                HexTeleportDirectory
                        .trapdoorOwnerKey(
                                level.dimension()
                                        .location(),
                                entryPos
                        );

        HexTeleportDirectory directory =
                HexTeleportDirectory.get(
                        level.getServer()
                );

        HexTeleportDirectory.DoorEndpoint registered =
                directory
                        .getTrapdoorEndpointByOwner(
                                ownerKey
                        )
                        .orElse(null);

        if (
                registered == null
                        || registered.hexColor()
                        != HexTeleportDirectory
                        .normalizeHex(
                                trapdoorBe.getHexColor()
                        )
        ) {
            return Optional.empty();
        }

        return Optional.of(
                resolvedTrapdoor(
                        ownerKey,
                        level,
                        entryPos,
                        state,
                        trapdoorBe.getHexColor()
                )
        );
    }

    public static Optional<ResolvedTrapdoor> resolveLinkedTrapdoor(
            ResolvedTrapdoor source
    ) {
        HexTeleportResolver.ResolvedTrapdoorEndpoint loaded =
                HexTeleportResolver
                        .resolveLinkedTrapdoor(
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
                        instanceof MusavaccaPortalTrapdoorBlockEntity targetBe)
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
            MusavaccaTrapdoorTeleportNetwork
                    .refresh(
                            targetBe
                    );

            return Optional.empty();
        }


        if (
                !isOpenPortalTrapdoor(
                        targetState
                )
        ) {
            return Optional.empty();
        }

        return Optional.of(
                resolvedTrapdoor(
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

    private static ResolvedTrapdoor resolvedTrapdoor(
            String ownerKey,
            ServerLevel level,
            BlockPos pos,
            BlockState state,
            int hexColor
    ) {
        return new ResolvedTrapdoor(
                ownerKey,
                level,
                pos,
                HexTeleportDirectory.normalizeHex(
                        hexColor
                ),
                state.getValue(
                        MusavaccaPortalTrapdoorBlock.FACING
                ),
                state.getValue(
                        MusavaccaPortalTrapdoorBlock.HALF
                ),
                portalCenter(
                        pos,
                        state
                )
        );
    }

    private static boolean isOpenPortalTrapdoor(
            BlockState state
    ) {
        return state.getBlock()
                instanceof MusavaccaPortalTrapdoorBlock
                && state.getValue(
                MusavaccaPortalTrapdoorBlock.PORTAL
        )
                && state.getValue(
                MusavaccaPortalTrapdoorBlock.OPEN
        );
    }

    private static Vec3 portalCenter(
            BlockPos pos,
            BlockState state
    ) {
        VoxelShape panel =
                MusavaccaPortalTrapdoorHitboxes
                        .portalPanel(
                                state
                        );

        if (panel.isEmpty()) {
            throw new IllegalStateException(
                    "Active Musavacca portal trapdoor has no portal panel."
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
                pos.getY()
                        + (
                        bounds.minY
                                + bounds.maxY
                )
                        * 0.5D,
                pos.getZ()
                        + (
                        bounds.minZ
                                + bounds.maxZ
                )
                        * 0.5D
        );
    }
}


