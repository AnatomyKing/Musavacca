
package space.anatomyuniverse.musavacca.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
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
        if (mc.level == null || mc.hitResult == null) {
            return;
        }

        HitResult hit = mc.hitResult;
        if (!(hit instanceof BlockHitResult blockHit)) {
            return;
        }

        BlockEntity be = mc.level.getBlockEntity(blockHit.getBlockPos());

        int hex;
        if (be instanceof HexBlockEntity hexBe) {
            hex = hexBe.getHexColor();
        } else if (be instanceof HardHexBlockEntity hardHexBe) {
            hex = hardHexBe.getHexColor();
        } else {
            return;
        }

        String hexText = String.format("#%06X", hex & 0xFFFFFF);

        event.getRight().add("hex_color: " + hex);
        event.getRight().add("HexColorDisplay: " + hexText);
    }
}