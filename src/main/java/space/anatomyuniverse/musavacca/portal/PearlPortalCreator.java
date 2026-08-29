package space.anatomyuniverse.musavacca.portal;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import space.anatomyuniverse.musavacca.block.ModBlocks;
import space.anatomyuniverse.musavacca.block.entity.custom.PearlPortalBlockEntity;
import space.anatomyuniverse.musavacca.teleport.HexTeleportDirectory;

import java.util.UUID;

public final class PearlPortalCreator {
    private PearlPortalCreator() {}

    private static final int PORTAL_SET_FLAGS = Block.UPDATE_NEIGHBORS | Block.UPDATE_CLIENTS;

    public static boolean tryCreatePortal(Level level, BlockPos insidePos, int hexColor, @Nullable Player player) {
        return tryCreatePortal(level, insidePos, hexColor, player, null);
    }

    public static boolean tryCreatePortal(
            Level level,
            BlockPos insidePos,
            int hexColor,
            @Nullable Player player,
            @Nullable Direction ignitionFace
    ) {
        var optionalShape = PearlPortalFrame.findIgnitableShape(level, insidePos);
        if (optionalShape.isEmpty()) {
            return false;
        }

        if (level.isClientSide()) {
            return true;
        }

        if (!(level instanceof ServerLevel serverLevel)) {
            return false;
        }

        int normalizedHex = normalizeHex(hexColor);
        PearlPortalFrame.Shape detectedShape = optionalShape.get();

        Direction frontDirection = determineFrontDirection(detectedShape, ignitionFace, player);
        PearlPortalFrame.Shape frontOrientedShape = detectedShape.withFrontDirection(frontDirection);

        BlockPos exitAnchor = determineExitAnchor(frontOrientedShape, insidePos, ignitionFace, player);
        Direction upDirection = determineUpDirection(frontOrientedShape, exitAnchor, insidePos, player);

        PearlPortalFrame.Shape shape = frontOrientedShape
                .withUpDirection(upDirection)
                .withExitAnchor(exitAnchor);

        if (containsExistingPortalBlock(serverLevel, shape)) {
            sendActionBar(player, "This Pearl portal is already active.");
            return false;
        }

        UUID portalId = UUID.randomUUID();
        HexTeleportDirectory directory = HexTeleportDirectory.get(serverLevel.getServer());

        HexTeleportDirectory.Result preCheck = directory.checkPortalEndpoint(portalId, normalizedHex);
        if (!preCheck.success()) {
            sendActionBar(player, "Pearl address #" + toHex(normalizedHex) + " is already occupied.");
            return false;
        }

        placePortalBlocks(serverLevel, shape);

        if (!initializePortalBlockEntities(serverLevel, shape, portalId, normalizedHex)) {
            rollbackPlacedPortal(serverLevel, shape, portalId);
            return false;
        }

        HexTeleportDirectory.Result result = directory.registerPortalEndpoint(
                portalId,
                normalizedHex,
                serverLevel.dimension().location(),
                shape
        );

        if (!result.success()) {
            rollbackPlacedPortal(serverLevel, shape, portalId);
            sendActionBar(player, "Pearl address #" + toHex(normalizedHex) + " is already occupied.");
            return false;
        }

        sendCreationMessage(player, result, normalizedHex);
        return true;
    }

    private static Direction determineFrontDirection(
            PearlPortalFrame.Shape shape,
            @Nullable Direction ignitionFace,
            @Nullable Player player
    ) {
        if (PearlPortalFrame.isValidFrontDirection(shape.axis(), ignitionFace)) {
            return ignitionFace;
        }

        return player == null
                ? PearlPortalFrame.defaultFrontDirection(shape.axis())
                : shape.frontDirectionFromPosition(player.position());
    }

    private static Direction determineUpDirection(
            PearlPortalFrame.Shape shape,
            BlockPos exitAnchor,
            BlockPos insidePos,
            @Nullable Player player
    ) {
        if (!shape.isFlat()) {
            return Direction.UP;
        }

        Direction fromAnchor = shape.upDirectionFromAnchor(exitAnchor);
        if (PearlPortalFrame.isValidUpDirection(shape.axis(), shape.frontDirection(), fromAnchor)) {
            return fromAnchor;
        }

        Vec3 reference = player == null
                ? Vec3.atCenterOf(insidePos)
                : player.position();

        Direction fromReference = shape.upDirectionFromReference(reference);
        if (PearlPortalFrame.isValidUpDirection(shape.axis(), shape.frontDirection(), fromReference)) {
            return fromReference;
        }

        return PearlPortalFrame.defaultUpDirection(shape.axis());
    }

    private static BlockPos determineExitAnchor(
            PearlPortalFrame.Shape shape,
            BlockPos insidePos,
            @Nullable Direction ignitionFace,
            @Nullable Player player
    ) {
        Vec3 reference;

        if (player != null) {
            reference = player.position();
        } else if (ignitionFace != null) {
            reference = Vec3.atCenterOf(insidePos.relative(ignitionFace.getOpposite()));
        } else {
            reference = Vec3.atCenterOf(insidePos);
        }

        return shape.closestFrameAnchor(reference);
    }

    private static void placePortalBlocks(ServerLevel level, PearlPortalFrame.Shape shape) {
        BlockState portalState = ModBlocks.PEARL_PORTAL.get()
                .defaultBlockState()
                .setValue(ModBlocks.PEARL_PORTAL.get().getAxisProperty(), shape.axis());

        shape.forEachInteriorBlock(pos -> level.setBlock(pos, portalState, PORTAL_SET_FLAGS));
    }

    private static boolean initializePortalBlockEntities(
            ServerLevel level,
            PearlPortalFrame.Shape shape,
            UUID portalId,
            int hexColor
    ) {
        boolean[] missingBlockEntity = {false};

        shape.forEachInteriorBlock(pos -> {
            if (level.getBlockEntity(pos) instanceof PearlPortalBlockEntity portalBlockEntity) {
                portalBlockEntity.initializePortal(portalId, hexColor, shape);
            } else {
                missingBlockEntity[0] = true;
            }
        });

        return !missingBlockEntity[0];
    }

    private static boolean containsExistingPortalBlock(ServerLevel level, PearlPortalFrame.Shape shape) {
        boolean[] foundExistingPortal = {false};

        shape.forEachInteriorBlock(pos -> {
            if (level.getBlockState(pos).is(ModBlocks.PEARL_PORTAL.get())) {
                foundExistingPortal[0] = true;
            }
        });

        return foundExistingPortal[0];
    }

    private static void rollbackPlacedPortal(ServerLevel level, PearlPortalFrame.Shape shape, UUID portalId) {
        boolean[] triggeredPhysicalCollapse = {false};

        shape.forEachInteriorBlock(pos -> {
            if (triggeredPhysicalCollapse[0]) {
                return;
            }

            if (level.getBlockState(pos).is(ModBlocks.PEARL_PORTAL.get())) {
                triggeredPhysicalCollapse[0] = true;
                level.destroyBlock(pos, false);
            }
        });

        PearlPortalNetwork.removePortal(level, portalId);
    }

    private static void sendCreationMessage(
            @Nullable Player player,
            HexTeleportDirectory.Result result,
            int hexColor
    ) {
        if (result == HexTeleportDirectory.Result.LINKED_TO_EXISTING_PORTAL) {
            sendActionBar(player, "Pearl portals linked with address #" + toHex(hexColor) + ".");
            return;
        }

        sendActionBar(player, "Pearl portal address #" + toHex(hexColor) + " is waiting for its pair.");
    }

    private static void sendActionBar(@Nullable Player player, String message) {
        if (player != null) {
            player.displayClientMessage(Component.literal(message), true);
        }
    }

    private static int normalizeHex(int color) {
        return color & 0xFFFFFF;
    }

    private static String toHex(int color) {
        return String.format("%06X", normalizeHex(color));
    }
}

