// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/portal/PearlPortalCreator.java
package space.anatomyuniverse.musavacca.portal;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import space.anatomyuniverse.hex.PearlHexNetwork;
import space.anatomyuniverse.musavacca.block.ModBlocks;
import space.anatomyuniverse.musavacca.block.entity.custom.PearlPortalBlockEntity;

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
        if (optionalShape.isEmpty()) return false;

        if (level.isClientSide()) return true;
        if (!(level instanceof ServerLevel serverLevel)) return false;

        int normalizedHex = normalizeHex(hexColor);

        PearlPortalFrame.Shape detectedShape = optionalShape.get();
        Direction frontDirection = determineFrontDirection(detectedShape, ignitionFace, player);
        PearlPortalFrame.Shape shape = detectedShape.withFrontDirection(frontDirection);

        if (containsExistingPortalBlock(serverLevel, shape)) {
            sendActionBar(player, "This Pearl portal is already active.");
            return false;
        }

        PearlHexNetwork hexNetwork = PearlHexNetwork.get(serverLevel.getServer());
        if (!hexNetwork.canCreatePortalWithHex(serverLevel, normalizedHex)) {
            sendActionBar(player, "Pearl address #" + toHex(normalizedHex) + " is already occupied.");
            return false;
        }

        PearlPortalDirectory directory = PearlPortalDirectory.get(serverLevel.getServer());

        UUID portalId = UUID.randomUUID();

        placePortalBlocks(serverLevel, shape);

        if (!initializePortalBlockEntities(serverLevel, shape, portalId, normalizedHex)) {
            removePortalBlocksOnly(serverLevel, shape);
            PearlPortalNetwork.removePortal(serverLevel, portalId);
            return false;
        }

        directory.upsertEndpoint(
                portalId,
                serverLevel.dimension().location(),
                shape,
                normalizedHex
        );

        PearlPortalDirectory.LinkResult linkResult = directory.linkOrWait(portalId, normalizedHex);

        if (linkResult == PearlPortalDirectory.LinkResult.HEX_ALREADY_USED) {
            removePortalBlocksOnly(serverLevel, shape);
            PearlPortalNetwork.removePortal(serverLevel, portalId);
            sendActionBar(player, "Pearl address #" + toHex(normalizedHex) + " is already linked.");
            return false;
        }

        sendCreationMessage(player, linkResult, normalizedHex);
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

        if (player != null) {
            return shape.frontDirectionFromPosition(player.position());
        }

        return PearlPortalFrame.defaultFrontDirection(shape.axis());
    }

    private static void placePortalBlocks(ServerLevel level, PearlPortalFrame.Shape shape) {
        BlockState portalState = ModBlocks.PEARL_PORTAL.get()
                .defaultBlockState()
                .setValue(ModBlocks.PEARL_PORTAL.get().getAxisProperty(), shape.axis());

        shape.forEachInteriorBlock(pos -> level.setBlock(
                pos,
                portalState,
                PORTAL_SET_FLAGS
        ));
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

    private static void removePortalBlocksOnly(ServerLevel level, PearlPortalFrame.Shape shape) {
        shape.forEachInteriorBlock(pos -> {
            if (level.getBlockState(pos).is(ModBlocks.PEARL_PORTAL.get())) {
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), PORTAL_SET_FLAGS);
            }

            level.removeBlockEntity(pos);
        });
    }

    private static void sendCreationMessage(
            @Nullable Player player,
            PearlPortalDirectory.LinkResult linkResult,
            int hexColor
    ) {
        if (linkResult == PearlPortalDirectory.LinkResult.LINKED_TO_EXISTING_PORTAL) {
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