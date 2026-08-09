package space.anatomyuniverse.musavacca.door;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.portal.TeleportTransition;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public final class MusavaccaDoorTeleportEvent {

    private MusavaccaDoorTeleportEvent() {}

    @Nullable
    public static TeleportTransition getPortalDestination(
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

        BlockPos targetTicketPos =
                BlockPos.containing(
                        transform.position()
                );

        TeleportTransition.PostTeleportTransition postTeleport =
                TeleportTransition
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

        return new TeleportTransition(
                target.level(),
                transform.position(),
                transform.deltaMovement(),
                transform.yRot(),
                transform.xRot(),
                Set.of(),
                postTeleport
        );
    }
}
