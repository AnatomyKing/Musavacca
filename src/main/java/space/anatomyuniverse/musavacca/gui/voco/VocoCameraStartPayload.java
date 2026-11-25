package space.anatomyuniverse.musavacca.gui.voco;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import space.anatomyuniverse.musavacca.MusaCore;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoReceptorLogic.ReceptorPosition;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoReceptorLogic;
//? if >=1.21.11 {
/*import net.minecraft.resources.Identifier;
*///?} else {
import net.minecraft.resources.ResourceLocation;
//?}

public record VocoCameraStartPayload(BlockPos pos, int receptorId) implements CustomPacketPayload {
    //? if >=1.21.11 {
    /*public static final Type<VocoCameraStartPayload> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(MusaCore.MOD_ID, "voco_camera_open"));
    *///?} else {
    public static final Type<VocoCameraStartPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(MusaCore.MOD_ID, "voco_camera_open"));
    //?}
    public static final StreamCodec<FriendlyByteBuf, VocoCameraStartPayload> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC,
                    VocoCameraStartPayload::pos,
                    ByteBufCodecs.VAR_INT,
                    VocoCameraStartPayload::receptorId,
                    VocoCameraStartPayload::new);

    public static void open(ServerPlayer player, BlockPos pos, ReceptorPosition receptor) {
        if (!VocoReceptorLogic.isReceptorLit(player.level(), pos, receptor)) return;
        VocoCameraServerSessions.begin(player, pos, receptor);
        player.connection.send(new VocoCameraStartPayload(pos.immutable(), receptor.id()));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
