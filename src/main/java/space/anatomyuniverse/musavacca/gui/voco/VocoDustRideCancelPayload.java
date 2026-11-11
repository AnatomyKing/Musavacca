package space.anatomyuniverse.musavacca.gui.voco;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//? if >=1.21.11 {
/*import net.minecraft.resources.Identifier;
*///?} else {
import net.minecraft.resources.ResourceLocation;
//?}
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import space.anatomyuniverse.musavacca.MusaCore;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoDustRide;

public enum VocoDustRideCancelPayload implements CustomPacketPayload {
    INSTANCE;

    //? if >=1.21.11 {
    /*public static final Type<VocoDustRideCancelPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(MusaCore.MOD_ID, "voco_dust_ride_cancel"));
    *///?} else {
    public static final Type<VocoDustRideCancelPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MusaCore.MOD_ID, "voco_dust_ride_cancel"));
    //?}
    public static final StreamCodec<FriendlyByteBuf, VocoDustRideCancelPayload> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(VocoDustRideCancelPayload payload, IPayloadContext context) {
        if (context.player() instanceof ServerPlayer player) VocoDustRide.cancel(player);
    }
}
