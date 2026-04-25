package space.anatomyuniverse.musavacca.hunger;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import space.anatomyuniverse.musavacca.MusaCore;

public record BonusHungerSyncPayload(
        int food,
        float saturation,
        boolean active
) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<BonusHungerSyncPayload> TYPE =
            new CustomPacketPayload.Type<>(
                    ResourceLocation.fromNamespaceAndPath(MusaCore.MOD_ID, "bonus_hunger_sync")
            );

    public static final StreamCodec<ByteBuf, BonusHungerSyncPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT,
                    BonusHungerSyncPayload::food,
                    ByteBufCodecs.FLOAT,
                    BonusHungerSyncPayload::saturation,
                    ByteBufCodecs.BOOL,
                    BonusHungerSyncPayload::active,
                    BonusHungerSyncPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}