package space.anatomyuniverse.musavacca.effect.playerstatus;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//? if >=1.21.11 {
/*import net.minecraft.resources.Identifier;
*///?} else {
import net.minecraft.resources.ResourceLocation;
//?}
import java.util.UUID;
import space.anatomyuniverse.musavacca.MusaCore;

public record PlayerStatusCallerPayload(UUID callerId, String callerName) implements CustomPacketPayload {
    public static final PlayerStatusCallerPayload CLEAR = new PlayerStatusCallerPayload(new UUID(0, 0), "");
    //? if >=1.21.11 {
    /*public static final Type<PlayerStatusCallerPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(MusaCore.MOD_ID, "player_status_caller"));
    *///?} else {
    public static final Type<PlayerStatusCallerPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MusaCore.MOD_ID, "player_status_caller"));
    //?}
    public static final StreamCodec<ByteBuf, PlayerStatusCallerPayload> STREAM_CODEC = StreamCodec.composite(UUIDUtil.STREAM_CODEC,
            PlayerStatusCallerPayload::callerId, ByteBufCodecs.STRING_UTF8, PlayerStatusCallerPayload::callerName, PlayerStatusCallerPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
