package space.anatomyuniverse.musavacca.effect.playerstatus;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//? if <1.21.11 {
import net.minecraft.resources.ResourceLocation;
//?} else {
/*import net.minecraft.resources.Identifier;
*///?}
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import space.anatomyuniverse.musavacca.MusaCore;
import space.anatomyuniverse.musavacca.effect.ModMobEffects;

public record PlayerStatusActionPayload(
        boolean clearEffect
) implements CustomPacketPayload {

    private static final int EXTEND_TICKS = 30 * 20;

    public static final Type<PlayerStatusActionPayload> TYPE =
            new Type<>(
                    /*? if <1.21.11 {*/ ResourceLocation
                    /*?} else {*//*Identifier*//*?}*/
                            .fromNamespaceAndPath(
                                    MusaCore.MOD_ID,
                                    "player_status_action"
                            )
            );

    public static final StreamCodec<
            ByteBuf,
            PlayerStatusActionPayload
            > STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BOOL,
                    PlayerStatusActionPayload::clearEffect,
                    PlayerStatusActionPayload::new
            );

    public static PlayerStatusActionPayload extend() {
        return new PlayerStatusActionPayload(false);
    }

    public static PlayerStatusActionPayload clear() {
        return new PlayerStatusActionPayload(true);
    }

    public static void handle(
            PlayerStatusActionPayload payload,
            IPayloadContext context
    ) {
        if (!(context.player() instanceof ServerPlayer player)) {
            return;
        }

        MobEffectInstance current =
                player.getEffect(
                        ModMobEffects.PLAYER_STATUS
                );

        if (current == null) {
            return;
        }

        if (payload.clearEffect()) {
            player.removeEffect(
                    ModMobEffects.PLAYER_STATUS
            );
            return;
        }

        if (current.isInfiniteDuration()) {
            return;
        }

        int duration =
                current.getDuration()
                        > Integer.MAX_VALUE - EXTEND_TICKS
                        ? Integer.MAX_VALUE
                        : current.getDuration()
                        + EXTEND_TICKS;

        player.addEffect(
                new MobEffectInstance(
                        current.getEffect(),
                        duration,
                        current.getAmplifier(),
                        current.isAmbient(),
                        current.isVisible(),
                        current.showIcon()
                )
        );
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
