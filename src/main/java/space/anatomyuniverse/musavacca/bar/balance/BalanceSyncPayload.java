package space.anatomyuniverse.musavacca.bar.balance;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//? if >=1.21.11 {
/*import net.minecraft.resources.Identifier;
*///?} else {
import net.minecraft.resources.ResourceLocation;
//?}
import space.anatomyuniverse.musavacca.MusaCore;

public record BalanceSyncPayload(
        int balance,
        boolean active
) implements CustomPacketPayload {
    public static final Type<BalanceSyncPayload> TYPE =
            new Type<>(
                    //? if >=1.21.11 {
                    /*Identifier.fromNamespaceAndPath(MusaCore.MOD_ID, "balance_sync")
                    *///?} else {
                    ResourceLocation.fromNamespaceAndPath(MusaCore.MOD_ID, "balance_sync")
                    //?}
            );

    public static final StreamCodec<ByteBuf, BalanceSyncPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT,
                    BalanceSyncPayload::balance,
                    ByteBufCodecs.BOOL,
                    BalanceSyncPayload::active,
                    BalanceSyncPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
