package space.anatomyuniverse.musavacca.block.custom.logic;

import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import space.anatomyuniverse.musavacca.block.custom.VocoPostBlock;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoReceptorLogic.ReceptorPosition;
import space.anatomyuniverse.musavacca.block.entity.custom.VocoPostBlockEntity;
import space.anatomyuniverse.musavacca.block.entity.custom.VocoTableBlockEntity;
import space.anatomyuniverse.musavacca.teleport.HexTeleportAddressNetwork;
import space.anatomyuniverse.musavacca.teleport.HexTeleportDirectory;
import space.anatomyuniverse.musavacca.teleport.HexTeleportResolver;

public final class VocoTeleportLogic {
    private static final double TELEPORT_CLEARANCE = 0.35D;

    private VocoTeleportLogic() {}

    public enum SyncResult {
        ACTIVE,
        QUEUED,
        INACTIVE
    }

    public static void teleportToReceptorCorner(
            Level level,
            BlockPos pos,
            Player player,
            ReceptorPosition receptor
    ) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        ReceptorPosition actualReceptor =
                actualReceptor(
                        level,
                        pos,
                        receptor
                );

        String ownerKey =
                ownerKey(
                        level,
                        pos,
                        actualReceptor
                );

        if (
                HexTeleportResolver.teleportToOwner(
                        serverPlayer,
                        ownerKey
                )
        ) {
            return;
        }

        /*
         * Directory lookup normally performs the teleport. If the owner lookup
         * is unavailable for any reason, the local fallback must still honor
         * the exact same endpoint target instead of silently falling back to a
         * default position with a possibly custom angle.
         */
        EndpointTarget endpointTarget =
                getEndpointTarget(
                        level,
                        pos,
                        actualReceptor
                );

        Vec3 target = endpointTarget.pos();
        Facing facing = endpointTarget.facing();

        serverPlayer.connection.teleport(
                target.x,
                target.y,
                target.z,
                facing.yawDegrees(),
                facing.pitchDegrees()
        );

        serverPlayer.setYHeadRot(
                facing.yawDegrees()
        );

        serverPlayer.setYBodyRot(
                facing.yawDegrees()
        );
    }

    public static boolean syncEndpoint(
            ServerLevel level,
            BlockPos pos,
            ReceptorPosition receptor,
            boolean active,
            int hexColor
    ) {
        SyncResult result =
                syncEndpointDetailed(
                        level,
                        pos,
                        receptor,
                        active,
                        hexColor
                );

        return !active
                || result == SyncResult.ACTIVE;
    }

    public static SyncResult syncEndpointDetailed(
            ServerLevel level,
            BlockPos pos,
            ReceptorPosition receptor,
            boolean active,
            int hexColor
    ) {
        MinecraftServer server =
                level.getServer();

        HexTeleportDirectory directory =
                HexTeleportDirectory.get(
                        server
                );

        ReceptorPosition actualReceptor =
                actualReceptor(
                        level,
                        pos,
                        receptor
                );

        String ownerKey =
                ownerKey(
                        level,
                        pos,
                        actualReceptor
                );

        if (!active) {
            /*
             * Voco no longer owns promotion logic.
             *
             * It releases its claim into the shared address network,
             * which decides whether the hex is actually free and which
             * queued claim, if any, should wake next.
             */
            HexTeleportAddressNetwork
                    .releaseOwner(
                            server,
                            ownerKey
                    );

            return SyncResult.INACTIVE;
        }

        EndpointTarget target =
                getEndpointTarget(
                        level,
                        pos,
                        actualReceptor
                );

        HexTeleportDirectory.Kind kind =
                level.getBlockEntity(pos)
                        instanceof VocoTableBlockEntity
                        ? HexTeleportDirectory.Kind
                        .VOCO_TABLE_RECEPTOR_CORNER

                        : HexTeleportDirectory.Kind
                        .VOCO_POST_RECEPTOR_CORNER;

        int normalizedHex =
                HexTeleportDirectory.normalizeHex(
                        hexColor
                );

        HexTeleportDirectory.VocoRegistration registration =
                directory.registerOrQueueVocoEndpoint(
                        ownerKey,
                        kind,
                        normalizedHex,
                        level.dimension()
                                .location(),
                        pos,
                        target.pos(),
                        target.facing()
                                .yawDegrees(),
                        target.facing()
                                .pitchDegrees(),
                        target.custom(),
                        actualReceptor.id()
                );

        /*
         * registerOrQueueVocoEndpoint can move an already-active Voco
         * owner from one hex to another. If that happened, the old
         * address may now be available to another waiting claim.
         */
        HexTeleportDirectory.Endpoint removed =
                registration.removedActiveEndpoint();

        if (
                removed != null
                        && removed.hexColor()
                        != normalizedHex
        ) {
            HexTeleportAddressNetwork
                    .addressChanged(
                            server,
                            removed.hexColor(),
                            normalizedHex
                    );
        }

        if (registration.result().success()) {
            return SyncResult.ACTIVE;
        }

        return registration.result()
                == HexTeleportDirectory.Result.HEX_OCCUPIED
                ? SyncResult.QUEUED
                : SyncResult.INACTIVE;
    }

    /*
     * Kept as a Voco-facing convenience method because existing Voco
     * callers may naturally think in Endpoint objects.
     *
     * The actual removal/promotion policy now lives entirely in the
     * shared HexTeleportAddressNetwork.
     */
    public static void removeEndpointAndPromote(
            MinecraftServer server,
            HexTeleportDirectory.Endpoint endpoint
    ) {
        if (
                server == null
                        || endpoint == null
        ) {
            return;
        }

        HexTeleportAddressNetwork
                .releaseEndpoint(
                        server,
                        endpoint.endpointId()
                );
    }

    /*
     * Same idea for Voco owner-based block entities.
     *
     * Door and Pearl Portal code never call this method.
     */
    public static void removeOwnerAndPromote(
            ServerLevel level,
            BlockPos pos,
            ReceptorPosition receptor
    ) {
        if (level == null) {
            return;
        }

        HexTeleportAddressNetwork
                .releaseOwner(
                        level.getServer(),
                        ownerKey(
                                level,
                                pos,
                                actualReceptor(
                                        level,
                                        pos,
                                        receptor
                                )
                        )
                );
    }

    public static String ownerKey(
            Level level,
            BlockPos pos,
            ReceptorPosition receptor
    ) {
        if (
                level.getBlockEntity(
                        pos
                )
                        instanceof VocoTableBlockEntity
        ) {
            return HexTeleportDirectory
                    .vocoTableReceptorCornerOwnerKey(
                            level.dimension()
                                    .location(),
                            pos,
                            receptor
                    );
        }

        return HexTeleportDirectory
                .vocoPostReceptorCornerOwnerKey(
                        level.dimension()
                                .location(),
                        pos
                );
    }

    public static EndpointTarget getEndpointTarget(
            Level level,
            BlockPos pos,
            ReceptorPosition receptor
    ) {
        BlockEntity be = level.getBlockEntity(pos);

        if (be instanceof VocoTableBlockEntity tableBe) {
            if (tableBe.isCustomTargetEnabled(receptor)) {
                return new EndpointTarget(
                        tableBe.getCustomTarget(receptor),
                        new Facing(
                                tableBe.getYawDegrees(receptor),
                                tableBe.getPitchDegrees(receptor)
                        ),
                        true
                );
            }

            return defaultEndpointTarget(pos, receptor);
        }

        if (be instanceof VocoPostBlockEntity postBe) {
            ReceptorPosition postReceptor = actualReceptor(level, pos, receptor);

            if (postBe.isCustomTargetEnabled()) {
                return new EndpointTarget(
                        postBe.getCustomTarget(),
                        new Facing(
                                postBe.getYawDegrees(),
                                postBe.getPitchDegrees()
                        ),
                        true
                );
            }

            return defaultEndpointTarget(pos, postReceptor);
        }

        return defaultEndpointTarget(pos, receptor);
    }

    public static Facing getFacing(
            Level level,
            BlockPos pos,
            ReceptorPosition receptor
    ) {
        BlockEntity be = level.getBlockEntity(pos);

        if (be instanceof VocoTableBlockEntity tableBe
                && tableBe.isCustomTargetEnabled(receptor)) {
            return new Facing(
                    tableBe.getYawDegrees(receptor),
                    tableBe.getPitchDegrees(receptor)
            );
        }

        if (be instanceof VocoPostBlockEntity postBe
                && postBe.isCustomTargetEnabled()) {
            return new Facing(
                    postBe.getYawDegrees(),
                    postBe.getPitchDegrees()
            );
        }

        ReceptorPosition actual = actualReceptor(level, pos, receptor);
        return defaultFacing(actual);
    }

    public static Vec3 getCameraEditorPosition(
            BlockPos pos,
            ReceptorPosition receptor
    ) {
        /*
         * The detached camera uses a fake player entity. Returning the same
         * feet position as the normal safe receptor arrival means the camera
         * automatically sits at normal player eye height while previewing.
         */
        return getDefaultTeleportPosition(pos, receptor);
    }

    private static EndpointTarget defaultEndpointTarget(
            BlockPos pos,
            ReceptorPosition receptor
    ) {
        return new EndpointTarget(
                getDefaultTeleportPosition(pos, receptor),
                defaultFacing(receptor),
                false
        );
    }

    private static Facing defaultFacing(ReceptorPosition receptor) {
        return new Facing(
                receptor.defaultYawDegrees(),
                receptor.defaultPitchDegrees()
        );
    }

    public static Vec3 getDefaultTeleportPosition(
            BlockPos pos,
            ReceptorPosition receptor
    ) {
        return switch (receptor) {
            case NORTH_EAST ->
                    new Vec3(
                            pos.getX()
                                    + 1.0D
                                    + TELEPORT_CLEARANCE,
                            pos.getY(),
                            pos.getZ()
                                    - TELEPORT_CLEARANCE
                    );

            case NORTH_WEST ->
                    new Vec3(
                            pos.getX()
                                    - TELEPORT_CLEARANCE,
                            pos.getY(),
                            pos.getZ()
                                    - TELEPORT_CLEARANCE
                    );

            case SOUTH_EAST ->
                    new Vec3(
                            pos.getX()
                                    + 1.0D
                                    + TELEPORT_CLEARANCE,
                            pos.getY(),
                            pos.getZ()
                                    + 1.0D
                                    + TELEPORT_CLEARANCE
                    );

            case SOUTH_WEST ->
                    new Vec3(
                            pos.getX()
                                    - TELEPORT_CLEARANCE,
                            pos.getY(),
                            pos.getZ()
                                    + 1.0D
                                    + TELEPORT_CLEARANCE
                    );
        };
    }

    private static ReceptorPosition actualReceptor(
            Level level,
            BlockPos pos,
            ReceptorPosition fallback
    ) {
        BlockState state =
                level.getBlockState(
                        pos
                );

        return state.getBlock()
                instanceof VocoPostBlock
                ? VocoPostBlock
                .receptorPosition(
                        state
                )
                : fallback;
    }

    public record Facing(
            int yawDegrees,
            int pitchDegrees
    ) {}

    public record EndpointTarget(
            Vec3 pos,
            Facing facing,
            boolean custom
    ) {}
}


