package space.anatomyuniverse.musavacca.door;

import java.util.Optional;
//? if >=1.21.2 {
import java.util.Set;
//?}
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
//? if >=1.21.2 {
import net.minecraft.world.level.portal.TeleportTransition;
//?} else {
/*import net.minecraft.world.level.portal.DimensionTransition;
*///?}
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import space.anatomyuniverse.musavacca.block.custom.MusavaccaPortalDoorBlock;
import space.anatomyuniverse.musavacca.block.custom.logic.MusavaccaPortalDoorHitboxes;
import space.anatomyuniverse.musavacca.block.entity.custom.MusavaccaPortalDoorBlockEntity;
import space.anatomyuniverse.musavacca.economy.EconomyConfig;
import space.anatomyuniverse.musavacca.economy.TeleportEconomy;
import space.anatomyuniverse.musavacca.teleport.HexTeleportDirectory;
import space.anatomyuniverse.musavacca.teleport.HexTeleportPreloader;
import space.anatomyuniverse.musavacca.teleport.HexTeleportResolver;

public final class MusavaccaDoorTeleport {
    private MusavaccaDoorTeleport() {}

    public record ResolvedDoor(String ownerKey, ServerLevel level, BlockPos pos, int hexColor, Direction facing, Vec3 portalCenter) {}

    @Nullable
    public static
            //? if >=1.21.2 {
            TeleportTransition
            //?} else {
            /*DimensionTransition
            *///?}
            getPortalDestination(ServerLevel currentLevel, Entity entity, BlockPos entryPos) {
        ResolvedDoor source = resolveSourceDoor(currentLevel, entryPos).orElse(null);
        if (source == null) return null;
        ResolvedDoor target = resolveLinkedDoor(source).orElse(null);
        if (target == null) return null;
        MusavaccaDoorTeleportTransform.Result transform = MusavaccaDoorTeleportTransform.calculate(entity, source, target);
        if (!TeleportEconomy.tryCharge(entity, EconomyConfig.musavaccaDoorTeleportCost())) return null;
        HexTeleportPreloader.prepare(entity, target.level(), transform.position());
        BlockPos targetTicketPos = BlockPos.containing(transform.position());
        var postTeleport =
                //? if >=1.21.2 {
                TeleportTransition
                //?} else {
                /*DimensionTransition
                *///?}
                        .PLAY_PORTAL_SOUND.then(teleportedEntity -> {
                            teleportedEntity.setPortalCooldown();
                            teleportedEntity.setDeltaMovement(transform.deltaMovement());
                            teleportedEntity.placePortalTicket(targetTicketPos);
                        });
        //? if >=1.21.2 {
        return new TeleportTransition(target.level(), transform.position(), transform.deltaMovement(), transform.yRot(), transform.xRot(), Set.of(),
                postTeleport);
        //?} else {
        /*return new DimensionTransition(target.level(), transform.position(), transform.deltaMovement(), transform.yRot(), transform.xRot(), postTeleport);
        *///?}
    }

    private static Optional<ResolvedDoor> resolveSourceDoor(ServerLevel level, BlockPos entryPos) {
        BlockState entryState = level.getBlockState(entryPos);
        if (!(entryState.getBlock() instanceof MusavaccaPortalDoorBlock)) {
            return Optional.empty();
        }
        BlockPos lowerPos = MusavaccaPortalDoorBlock.lowerDoorPos(entryState, entryPos);
        BlockState lowerState = level.getBlockState(lowerPos);
        if (!isOpenPortalDoor(lowerState)) {
            return Optional.empty();
        }
        if (!(level.getBlockEntity(lowerPos) instanceof MusavaccaPortalDoorBlockEntity doorBe) || !doorBe.hasHexColor()) {
            return Optional.empty();
        }
        MusavaccaDoorTeleportNetwork.refresh(doorBe);
        String ownerKey = HexTeleportDirectory.doorOwnerKey(doorBe.getDeviceId());
        HexTeleportDirectory directory = HexTeleportDirectory.get(level.getServer());
        HexTeleportDirectory.DoorEndpoint registered = directory.getDoorEndpointByOwner(ownerKey).orElse(null);
        if (registered == null || registered.hexColor() != HexTeleportDirectory.normalizeHex(doorBe.getHexColor())) {
            return Optional.empty();
        }
        return Optional.of(resolvedDoor(ownerKey, level, lowerPos, lowerState, doorBe.getHexColor()));
    }

    private static Optional<ResolvedDoor> resolveLinkedDoor(ResolvedDoor source) {
        HexTeleportResolver.ResolvedDoorEndpoint loaded = HexTeleportResolver.resolveLinkedDoor(source.level(), source.ownerKey()).orElse(null);
        if (loaded == null) return Optional.empty();
        ServerLevel targetLevel = loaded.level();
        HexTeleportDirectory.DoorEndpoint targetEndpoint = loaded.endpoint();
        BlockPos targetPos = targetEndpoint.ownerPos();
        BlockState targetState = targetLevel.getBlockState(targetPos);
        if (!(targetLevel.getBlockEntity(targetPos) instanceof MusavaccaPortalDoorBlockEntity targetBe) || !targetBe.hasHexColor()) {
            return Optional.empty();
        }
        if (HexTeleportDirectory.normalizeHex(targetBe.getHexColor()) != source.hexColor()) {
            MusavaccaDoorTeleportNetwork.refresh(targetBe);
            return Optional.empty();
        }
        if (!isOpenPortalDoor(targetState)) {
            return Optional.empty();
        }
        return Optional.of(resolvedDoor(targetEndpoint.ownerKey(), targetLevel, targetPos, targetState, targetBe.getHexColor()));
    }

    private static ResolvedDoor resolvedDoor(String ownerKey, ServerLevel level, BlockPos lowerPos, BlockState state, int hexColor) {
        return new ResolvedDoor(ownerKey, level, lowerPos, HexTeleportDirectory.normalizeHex(hexColor), state.getValue(MusavaccaPortalDoorBlock.FACING),
                portalCenter(lowerPos, state));
    }

    private static boolean isOpenPortalDoor(BlockState state) {
        return state.getBlock() instanceof MusavaccaPortalDoorBlock && state.getValue(MusavaccaPortalDoorBlock.HALF) == DoubleBlockHalf.LOWER
                && state.getValue(MusavaccaPortalDoorBlock.PORTAL) && state.getValue(MusavaccaPortalDoorBlock.OPEN);
    }

    private static Vec3 portalCenter(BlockPos pos, BlockState state) {
        VoxelShape panel = MusavaccaPortalDoorHitboxes.portalPanel(state);
        if (panel.isEmpty()) throw new IllegalStateException("Active Musavacca portal door has no portal panel.");
        AABB bounds = panel.bounds();
        return new Vec3(pos.getX() + (bounds.minX + bounds.maxX) * 0.5D, pos.getY(), pos.getZ() + (bounds.minZ + bounds.maxZ) * 0.5D);
    }
}
