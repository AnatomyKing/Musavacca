package space.anatomyuniverse.musavacca.door;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
//? if <1.21.2 {
/*import net.minecraft.world.level.portal.DimensionTransition;
*///?} else {
import net.minecraft.world.level.portal.TeleportTransition;
//?}
import org.jetbrains.annotations.Nullable;
import space.anatomyuniverse.musavacca.teleport.HexTeleportPreloader;

//? if >=1.21.2
import java.util.Set;

public final class MusavaccaDoorTeleportEvent {

    private MusavaccaDoorTeleportEvent() {}

    @Nullable
    public static
    //? if <1.21.2 {
    /*DimensionTransition
    *///?} else {
    TeleportTransition
    //?}
    getPortalDestination(
            ServerLevel currentLevel,
            Entity entity,
            BlockPos entryPos
    ) {
        MusavaccaDoorTeleportResolver.ResolvedDoor source =
                MusavaccaDoorTeleportResolver
                        .resolveSourceDoor(
                                currentLevel,
                                entryPos
                        )
                        .orElse(null);

        if (source == null) {
            return null;
        }

        MusavaccaDoorTeleportResolver.ResolvedDoor target =
                MusavaccaDoorTeleportResolver
                        .resolveLinkedDoor(
                                source
                        )
                        .orElse(null);

        if (target == null) {
            return null;
        }

        MusavaccaDoorTeleportTransform.Result transform =
                MusavaccaDoorTeleportTransform
                        .calculate(
                                entity,
                                source,
                                target
                        );

        HexTeleportPreloader.prepare(
                entity,
                target.level(),
                transform.position()
        );

        BlockPos targetTicketPos =
                BlockPos.containing(
                        transform.position()
                );

        var postTeleport =
                //? if <1.21.2 {
                /*DimensionTransition
                *///?} else {
                TeleportTransition
                //?}
                        .PLAY_PORTAL_SOUND
                        .then(
                                teleportedEntity -> {
                                    teleportedEntity
                                            .setPortalCooldown();

                                    teleportedEntity
                                            .setDeltaMovement(
                                                    transform
                                                            .deltaMovement()
                                            );

                                    MusavaccaDoorTeleportResolver
                                            .keepDestinationAlive(
                                                    teleportedEntity,
                                                    targetTicketPos
                                            );
                                }
                        );

        return new
                //? if <1.21.2 {
                /*DimensionTransition(
                *///?} else {
                TeleportTransition(
                //?}
                target.level(),
                transform.position(),
                transform.deltaMovement(),
                transform.yRot(),
                transform.xRot(),
                //? if >=1.21.2
                Set.of(),
                postTeleport
        );
    }
}


