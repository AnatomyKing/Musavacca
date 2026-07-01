package space.anatomyuniverse.musavacca.gui.menu;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import space.anatomyuniverse.musavacca.MusaCore;
import space.anatomyuniverse.musavacca.component.HexColorComponent;
import space.anatomyuniverse.musavacca.item.custom.FlintAndPearlItem;
import space.anatomyuniverse.musavacca.tint.TintColorUtil;

public record FlintAndPearlColorPayload(int hexColor) implements CustomPacketPayload {
    public static final Type<FlintAndPearlColorPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(MusaCore.MOD_ID, "flint_and_pearl_color")
    );

    public static final StreamCodec<ByteBuf, FlintAndPearlColorPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            FlintAndPearlColorPayload::hexColor,
            FlintAndPearlColorPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(FlintAndPearlColorPayload payload, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player)) {
            return;
        }

        if (!(player.containerMenu instanceof FlintAndPearlMenu menu)) {
            return;
        }

        InteractionHand hand = menu.getHand();
        ItemStack stack = player.getItemInHand(hand);

        if (!(stack.getItem() instanceof FlintAndPearlItem)) {
            return;
        }

        int color = TintColorUtil.rgb(payload.hexColor());

        HexColorComponent.setSlot(stack, FlintAndPearlItem.HEX_SLOT, color);
        menu.setHexColor(color);

        player.containerMenu.broadcastChanges();
        player.inventoryMenu.broadcastChanges();
    }
}
