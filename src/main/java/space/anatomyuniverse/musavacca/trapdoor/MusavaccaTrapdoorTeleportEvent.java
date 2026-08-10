package space.anatomyuniverse.musavacca.trapdoor;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.portal.TeleportTransition;
import org.jetbrains.annotations.Nullable;
import space.anatomyuniverse.musavacca.teleport.HexTeleportPreloader;

import java.util.Set;

public final class MusavaccaTrapdoorTeleportEvent {

    private MusavaccaTrapdoorTeleportEvent() {}

    @Nullable
    public static TeleportTransition getPortalDestination(
            ServerLevel currentLevel,
            Entity entity,
            BlockPos entryPos
    ) {
        MusavaccaTrapdoorTeleportResolver.ResolvedTrapdoor source =
                MusavaccaTrapdoorTeleportResolver
                        .resolveSourceTrapdoor(
                                currentLevel,
                                entryPos
                        )
                        .orElse(null);

        if (source == null) {
            return null;
        }

        MusavaccaTrapdoorTeleportResolver.ResolvedTrapdoor target =
                MusavaccaTrapdoorTeleportResolver
                        .resolveLinkedTrapdoor(
                                source
                        )
                        .orElse(null);

        if (target == null) {
            return null;
        }

        MusavaccaTrapdoorTeleportTransform.Result transform =
                MusavaccaTrapdoorTeleportTransform
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

                                    MusavaccaTrapdoorTeleportResolver
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
