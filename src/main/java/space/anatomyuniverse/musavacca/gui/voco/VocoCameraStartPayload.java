package space.anatomyuniverse.musavacca.gui.voco;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import space.anatomyuniverse.musavacca.MusaCore;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoReceptorLogic;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoReceptorLogic.ReceptorPosition;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoTeleportLogic;

public record VocoCameraStartPayload(
        BlockPos pos,
        int receptorId,
        double cameraX,
        double cameraY,
        double cameraZ,
        float previewYaw,
        float previewPitch
) implements CustomPacketPayload {
    public static final Type<VocoCameraStartPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(MusaCore.MOD_ID, "voco_camera_start"));

    public static final StreamCodec<FriendlyByteBuf, VocoCameraStartPayload> STREAM_CODEC =
            new StreamCodec<>() {
                @Override
                public VocoCameraStartPayload decode(FriendlyByteBuf buffer) {
                    return new VocoCameraStartPayload(
                            buffer.readBlockPos(),
                            buffer.readVarInt(),
                            buffer.readDouble(),
                            buffer.readDouble(),
                            buffer.readDouble(),
                            buffer.readFloat(),
                            buffer.readFloat()
                    );
                }

                @Override
                public void encode(FriendlyByteBuf buffer, VocoCameraStartPayload payload) {
                    buffer.writeBlockPos(payload.pos());
                    buffer.writeVarInt(payload.receptorId());
                    buffer.writeDouble(payload.cameraX());
                    buffer.writeDouble(payload.cameraY());
                    buffer.writeDouble(payload.cameraZ());
                    buffer.writeFloat(payload.previewYaw());
                    buffer.writeFloat(payload.previewPitch());
                }
            };

    public static void open(
            ServerPlayer player,
            BlockPos pos,
            ReceptorPosition receptor
    ) {
        if (!VocoReceptorLogic.isReceptorLit(player.level(), pos, receptor)) {
            return;
        }

        VocoCameraServerSessions.begin(player, pos, receptor);

        Vec3 cameraPosition = VocoTeleportLogic.getCameraEditorPosition(pos, receptor);
        VocoTeleportLogic.Facing facing =
                VocoTeleportLogic.getEndpointTarget(player.level(), pos, receptor).facing();

        PacketDistributor.sendToPlayer(
                player,
                new VocoCameraStartPayload(
                        pos.immutable(),
                        receptor.id(),
                        cameraPosition.x,
                        cameraPosition.y,
                        cameraPosition.z,
                        facing.yawDegrees(),
                        facing.pitchDegrees()
                )
        );
    }

    public Vec3 cameraPosition() {
        return new Vec3(this.cameraX, this.cameraY, this.cameraZ);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

