package space.anatomyuniverse.musavacca.block.custom.logic;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.game.ServerboundTeleportToEntityPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import space.anatomyuniverse.musavacca.MusaCore;
import space.anatomyuniverse.musavacca.gui.voco.VocoDustRideCancelPayload;
//? if >=26.2 {
/*import net.minecraft.network.protocol.game.ServerboundSpectatorActionPacket;
*///?}

public final class VocoDustInputs extends ChannelInboundHandlerAdapter {
    private static final String NAME = "musavacca_dust_controls";
    private final Connection connection;
    private volatile boolean riding;

    private VocoDustInputs(Connection connection) {
        this.connection = connection;
    }

    static VocoDustInputs install(ServerPlayer player) {
        Connection connection = player.connection.getConnection();
        var channel = connection.channel();
        if (channel == null) return null;
        var pipeline = channel.pipeline();
        var controls = pipeline.get(VocoDustInputs.class);
        if (controls != null) return controls;
        var handler = pipeline.context(connection);
        if (handler == null) return null;
        controls = new VocoDustInputs(connection);
        pipeline.addBefore(handler.name(), NAME, controls);
        return controls;
    }

    void setRiding(boolean value) {
        riding = value;
    }

    @Override
    public void channelRead(ChannelHandlerContext context, Object message) throws Exception {
        boolean spectatorControl = message instanceof ServerboundTeleportToEntityPacket;
        //? if >=26.2 {
        /*spectatorControl |= message instanceof ServerboundSpectatorActionPacket;
        *///?}
        if (spectatorControl
                && (riding
                        || !(connection.getPacketListener() instanceof ServerGamePacketListenerImpl listener)
                        || !listener.player.isSpectator())) return;
        if (riding && message instanceof Packet<?> packet) {
            if (packet instanceof ServerboundCustomPayloadPacket custom
                    && custom.payload().type().id().getNamespace().equals(MusaCore.MOD_ID)
                    && !(custom.payload() instanceof VocoDustRideCancelPayload)) return;
            if (switch (packet.type().id().getPath()) {
                case "attack",
                        "interact",
                        "punch",
                        "swing",
                        "use_item",
                        "use_item_on",
                        "player_action",
                        "player_input",
                        "player_command",
                        "move_player_pos",
                        "move_player_pos_rot",
                        "move_player_rot",
                        "move_player_status_only",
                        "move_vehicle",
                        "player_abilities",
                        "pick_item",
                        "pick_item_from_block",
                        "pick_item_from_entity",
                        "container_click",
                        "container_button_click",
                        "set_carried_item",
                        "set_creative_mode_slot",
                        "bundle_item_selected" ->
                        true;
                default -> false;
            }) return;
        }
        super.channelRead(context, message);
    }

    public static void onUseBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getEntity() instanceof ServerPlayer player && VocoDustRide.active(player)) event.setCanceled(true);
    }
}
