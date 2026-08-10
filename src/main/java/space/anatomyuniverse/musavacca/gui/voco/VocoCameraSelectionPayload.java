package space.anatomyuniverse.musavacca.gui.voco;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import space.anatomyuniverse.musavacca.MusaCore;
import space.anatomyuniverse.musavacca.block.custom.VocoPostBlock;
import space.anatomyuniverse.musavacca.block.custom.VocoTableBlock;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoReceptorLogic;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoReceptorLogic.ReceptorPosition;
import space.anatomyuniverse.musavacca.block.entity.custom.VocoPostBlockEntity;
import space.anatomyuniverse.musavacca.block.entity.custom.VocoTableBlockEntity;

public record VocoCameraSelectionPayload(
        BlockPos pos,
        int receptorId,
        float yawDegrees,
        float pitchDegrees,
        boolean resetToDefault
) implements CustomPacketPayload {
    private static final double MAX_PLAYER_DISTANCE_SQR = 100.0D;

    public static final Type<VocoCameraSelectionPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(MusaCore.MOD_ID, "voco_camera_selection"));

    public static final StreamCodec<FriendlyByteBuf, VocoCameraSelectionPayload> STREAM_CODEC =
            new StreamCodec<>() {
                @Override
                public VocoCameraSelectionPayload decode(FriendlyByteBuf buffer) {
                    return new VocoCameraSelectionPayload(
                            buffer.readBlockPos(),
                            buffer.readVarInt(),
                            buffer.readFloat(),
                            buffer.readFloat(),
                            buffer.readBoolean()
                    );
                }

                @Override
                public void encode(FriendlyByteBuf buffer, VocoCameraSelectionPayload payload) {
                    buffer.writeBlockPos(payload.pos());
                    buffer.writeVarInt(payload.receptorId());
                    buffer.writeFloat(payload.yawDegrees());
                    buffer.writeFloat(payload.pitchDegrees());
                    buffer.writeBoolean(payload.resetToDefault());
                }
            };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(VocoCameraSelectionPayload payload, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player)) {
            return;
        }

        BlockPos pos = payload.pos();
        if (!player.level().hasChunkAt(pos)) {
            return;
        }

        if (player.distanceToSqr(
                pos.getX() + 0.5D,
                pos.getY() + 0.5D,
                pos.getZ() + 0.5D
        ) > MAX_PLAYER_DISTANCE_SQR) {
            return;
        }

        if (payload.receptorId() < 0 || payload.receptorId() >= ReceptorPosition.COUNT) {
            return;
        }

        VocoCameraServerSessions.Session session =
                VocoCameraServerSessions.consume(player, pos, payload.receptorId());

        if (session == null) {
            return;
        }

        BlockState state = player.level().getBlockState(pos);
        BlockEntity blockEntity = player.level().getBlockEntity(pos);
        ReceptorPosition receptor = ReceptorPosition.byId(payload.receptorId());

        if (state.getBlock() instanceof VocoPostBlock) {
            if (!(blockEntity instanceof VocoPostBlockEntity postBe)) {
                return;
            }

            receptor = VocoPostBlock.receptorPosition(state);

            if (receptor.id() != session.receptorId()) {
                return;
            }

            if (!VocoReceptorLogic.isReceptorLit(player.level(), pos, receptor)) {
                return;
            }

            if (payload.resetToDefault()) {
                postBe.resetCustomTarget();
                VocoReceptorLogic.playUiClick(player.level(), pos);
                return;
            }

            postBe.setCustomTarget(
                    session.capturedTarget(),
                    normalizeYaw(payload.yawDegrees()),
                    normalizePitch(payload.pitchDegrees())
            );
            VocoReceptorLogic.playUiClick(player.level(), pos);
            return;
        }

        if (!(state.getBlock() instanceof VocoTableBlock)
                || !(blockEntity instanceof VocoTableBlockEntity tableBe)) {
            return;
        }

        if (!VocoReceptorLogic.isReceptorLit(player.level(), pos, receptor)) {
            return;
        }

        if (payload.resetToDefault()) {
            tableBe.resetCustomTarget(receptor);
            VocoReceptorLogic.playUiClick(player.level(), pos);
            return;
        }

        Vec3 capturedTarget = session.capturedTarget();
        tableBe.setCustomTarget(
                receptor,
                capturedTarget,
                normalizeYaw(payload.yawDegrees()),
                normalizePitch(payload.pitchDegrees())
        );
        VocoReceptorLogic.playUiClick(player.level(), pos);
    }

    private static int normalizeYaw(float yawDegrees) {
        return VocoReceptorLogic.clampYaw(Math.round(Mth.wrapDegrees(yawDegrees)));
    }

    private static int normalizePitch(float pitchDegrees) {
        return VocoReceptorLogic.clampPitch(Math.round(pitchDegrees));
    }
}
