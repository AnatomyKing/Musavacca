package space.anatomyuniverse.musavacca.client;

import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import space.anatomyuniverse.musavacca.MusaCore;
import space.anatomyuniverse.musavacca.component.HexColorComponent;
import space.anatomyuniverse.musavacca.component.ModDataComponents;

import java.util.Map;

//? if <1.21.9 {
import net.neoforged.neoforge.client.event.CustomizeGuiOverlayEvent;
//?} else {
/*import net.minecraft.client.gui.components.debug.DebugScreenDisplayer;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.neoforge.client.event.RegisterDebugEntriesEvent;
*///?}

@EventBusSubscriber(modid = MusaCore.MOD_ID, value = Dist.CLIENT)
public final class HexDebugOverlay {
    private HexDebugOverlay() {}

    private static Map<String, Integer> getLookedAtHexColors() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || !(mc.hitResult instanceof BlockHitResult hit)) {
            return Map.of();
        }

        BlockEntity be = mc.level.getBlockEntity(hit.getBlockPos());
        if (be == null) {
            return Map.of();
        }

        DataComponentMap components = be.collectComponents();
        return HexColorComponent.normalizeMap(components.get(ModDataComponents.HEX_COLOR.get()));
    }

    //? if <1.21.9 {
    @SubscribeEvent
    public static void onDebugText(CustomizeGuiOverlayEvent.DebugText event) {
        Map<String, Integer> hexColors = getLookedAtHexColors();
        if (hexColors.isEmpty()) {
            return;
        }

        for (Map.Entry<String, Integer> entry : hexColors.entrySet()) {
            event.getRight().add(String.format("hex_color[%s]: #%06X", entry.getKey(), entry.getValue() & 0xFFFFFF));
        }
    }
    //?} else {
    /*public static void registerDebugEntries(RegisterDebugEntriesEvent event) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(MusaCore.MOD_ID, "hex_debug_overlay");

        event.register(id, new DebugScreenEntry() {
            @Override
            public void display(DebugScreenDisplayer displayer, Level level, LevelChunk clientChunk, LevelChunk serverChunk) {
                Map<String, Integer> hexColors = getLookedAtHexColors();
                for (Map.Entry<String, Integer> entry : hexColors.entrySet()) {
                    displayer.addLine(String.format("hex_color[%s]: #%06X", entry.getKey(), entry.getValue() & 0xFFFFFF));
                }
            }

            @Override
            public boolean isAllowed(boolean reducedDebugInfo) {
                return true;
            }
        });
    }
    *///?}
}
