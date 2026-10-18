package space.anatomyuniverse.musavacca.bar.hunger;

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

public record BonusHungerSyncPayload(
        int food,
        float saturation,
        boolean active
) implements CustomPacketPayload {
    public static final Type<BonusHungerSyncPayload> TYPE =
            new Type<>(
                    //? if >=1.21.11 {
                    /*Identifier.fromNamespaceAndPath(MusaCore.MOD_ID, "bonus_hunger_sync")
                    *///?} else {
                    ResourceLocation.fromNamespaceAndPath(MusaCore.MOD_ID, "bonus_hunger_sync")
                    //?}
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
