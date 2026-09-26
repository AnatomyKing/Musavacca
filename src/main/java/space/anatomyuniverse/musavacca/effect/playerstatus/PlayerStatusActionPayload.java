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
import net.neoforged.neoforge.network.handling.IPayloadContext;
import space.anatomyuniverse.musavacca.MusaCore;
import space.anatomyuniverse.musavacca.vococaller.VocoCallerNetwork;

public record PlayerStatusActionPayload(
        boolean accepted
) implements CustomPacketPayload {

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
                    PlayerStatusActionPayload::accepted,
                    PlayerStatusActionPayload::new
            );

    public static PlayerStatusActionPayload accept() {
        return new PlayerStatusActionPayload(true);
    }

    public static PlayerStatusActionPayload cancel() {
        return new PlayerStatusActionPayload(false);
    }

    public static void handle(
            PlayerStatusActionPayload payload,
            IPayloadContext context
    ) {
        if (!(context.player() instanceof ServerPlayer player)) {
            return;
        }

        VocoCallerNetwork.answerCall(
                player,
                payload.accepted()
        );
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
