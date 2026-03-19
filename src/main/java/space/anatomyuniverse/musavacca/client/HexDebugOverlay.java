package space.anatomyuniverse.musavacca.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.CustomizeGuiOverlayEvent;
import space.anatomyuniverse.musavacca.MusaCore;
import space.anatomyuniverse.musavacca.block.entity.HardHexBlockEntity;
import space.anatomyuniverse.musavacca.block.entity.HexBlockEntity;

@EventBusSubscriber(modid = MusaCore.MOD_ID, value = Dist.CLIENT)
public final class HexDebugOverlay {
    private HexDebugOverlay() {}

    @SubscribeEvent
    public static void onDebugText(CustomizeGuiOverlayEvent.DebugText event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || !(mc.hitResult instanceof BlockHitResult hit)) {
            return;
        }

        int hex;
        if (mc.level.getBlockEntity(hit.getBlockPos()) instanceof HexBlockEntity be) {
            if (!be.hasHexColor()) return;
            hex = be.getHexColor();
        } else if (mc.level.getBlockEntity(hit.getBlockPos()) instanceof HardHexBlockEntity be) {
            hex = be.getHexColor();
        } else {
            return;
        }

        event.getRight().add("hex_color: " + hex);
        event.getRight().add(String.format("HexColorDisplay: #%06X", hex & 0xFFFFFF));
    }
}