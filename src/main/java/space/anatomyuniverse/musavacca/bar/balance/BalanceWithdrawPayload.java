package space.anatomyuniverse.musavacca.bar.balance;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import space.anatomyuniverse.musavacca.MusaCore;
//? if >=1.21.11 {
/*import net.minecraft.resources.Identifier;
*///?} else {
import net.minecraft.resources.ResourceLocation;
//?}

public record BalanceWithdrawPayload(boolean bulk) implements CustomPacketPayload {
    //? if >=1.21.11 {
    /*public static final Type<BalanceWithdrawPayload> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(MusaCore.MOD_ID, "balance_withdraw"));
    *///?} else {
    public static final Type<BalanceWithdrawPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(MusaCore.MOD_ID, "balance_withdraw"));
    //?}
    public static final StreamCodec<ByteBuf, BalanceWithdrawPayload> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.BOOL, BalanceWithdrawPayload::bulk, BalanceWithdrawPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
