// file: src/main/java/space/anatomyuniverse/musavacca/gui/menu/payloads/VocoCallerStatePayload.java
package space.anatomyuniverse.musavacca.gui.menu.payloads;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import space.anatomyuniverse.musavacca.MusaCore;
import space.anatomyuniverse.musavacca.gui.menu.VocoCallerMenu;
import space.anatomyuniverse.musavacca.vococaller.VocoCallerPhonebook;

public record VocoCallerStatePayload(
        int containerId,
        int phoneHex,
        int[] recent,
        int[] saved
) implements CustomPacketPayload {

    public static final Type<VocoCallerStatePayload> TYPE =
            new Type<>(
                    ResourceLocation.fromNamespaceAndPath(
                            MusaCore.MOD_ID,
                            "voco_caller_state"
                    )
            );

    public static final StreamCodec<ByteBuf, VocoCallerStatePayload>
            STREAM_CODEC =
            new StreamCodec<>() {
                @Override
                public VocoCallerStatePayload decode(ByteBuf buffer) {
                    return new VocoCallerStatePayload(
                            buffer.readInt(),
                            buffer.readInt(),
                            readAddresses(buffer),
                            readAddresses(buffer)
                    );
                }

                @Override
                public void encode(
                        ByteBuf buffer,
                        VocoCallerStatePayload payload
                ) {
                    buffer.writeInt(payload.containerId());
                    buffer.writeInt(payload.phoneHex());
                    writeAddresses(buffer, payload.recent());
                    writeAddresses(buffer, payload.saved());
                }
            };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(
            VocoCallerStatePayload payload,
            IPayloadContext context
    ) {
        if (!(context.player() instanceof ServerPlayer player)
                || !(player.containerMenu instanceof VocoCallerMenu menu)
                || player.containerMenu.containerId
                != payload.containerId()
                || menu.getPhoneHex()
                != (payload.phoneHex() & 0xFFFFFF)) {
            return;
        }

        menu.applyClientState(
                player,
                payload.recent(),
                payload.saved()
        );
    }

    private static int[] readAddresses(
            ByteBuf buffer
    ) {
        int[] result =
                new int[VocoCallerPhonebook.ROW_COUNT];

        for (int row = 0; row < result.length; row++) {
            result[row] = buffer.readInt();
        }

        return result;
    }

    private static void writeAddresses(
            ByteBuf buffer,
            int[] values
    ) {
        for (
                int row = 0;
                row < VocoCallerPhonebook.ROW_COUNT;
                row++
        ) {
            int value =
                    values != null
                            && row < values.length
                            ? values[row]
                            : VocoCallerPhonebook.EMPTY;

            buffer.writeInt(
                    value < 0
                            ? VocoCallerPhonebook.EMPTY
                            : value & 0xFFFFFF
            );
        }
    }
}
