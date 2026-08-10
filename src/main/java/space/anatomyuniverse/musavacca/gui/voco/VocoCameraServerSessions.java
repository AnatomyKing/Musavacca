package space.anatomyuniverse.musavacca.gui.voco;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoReceptorLogic.ReceptorPosition;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class VocoCameraServerSessions {
    private static final long SESSION_LIFETIME_TICKS = 20L * 60L * 10L;

    private static final Map<UUID, Session> SESSIONS = new HashMap<>();

    private VocoCameraServerSessions() {}

    public static Session begin(
            ServerPlayer player,
            BlockPos pos,
            ReceptorPosition receptor
    ) {
        Session session = new Session(
                player.level().dimension(),
                pos.immutable(),
                receptor.id(),
                player.position(),
                player.level().getGameTime() + SESSION_LIFETIME_TICKS
        );

        SESSIONS.put(player.getUUID(), session);
        return session;
    }

    public static Session consume(
            ServerPlayer player,
            BlockPos pos,
            int receptorId
    ) {
        Session session = SESSIONS.remove(player.getUUID());

        if (session == null) {
            return null;
        }

        if (player.level().getGameTime() > session.expiresAtGameTime()) {
            return null;
        }

        if (!session.dimension().equals(player.level().dimension())
                || !session.pos().equals(pos)
                || session.receptorId() != receptorId) {
            return null;
        }

        return session;
    }

    public record Session(
            ResourceKey<Level> dimension,
            BlockPos pos,
            int receptorId,
            Vec3 capturedTarget,
            long expiresAtGameTime
    ) {}
}
