package space.anatomyuniverse.musavacca.client;

import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import space.anatomyuniverse.musavacca.MusaCore;
import space.anatomyuniverse.musavacca.component.ModDataComponents;

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

    private static Integer getLookedAtHexColor() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || !(mc.hitResult instanceof BlockHitResult hit)) {
            return null;
        }

        BlockEntity be = mc.level.getBlockEntity(hit.getBlockPos());
        if (be == null) {
            return null;
        }

        DataComponentMap components = be.collectComponents();
        return components.get(ModDataComponents.HEX_COLOR.get());
    }

    //? if <1.21.9 {
    @SubscribeEvent
    public static void onDebugText(CustomizeGuiOverlayEvent.DebugText event) {
        Integer hex = getLookedAtHexColor();
        if (hex == null) {
            return;
        }

        event.getRight().add("hex_color: " + hex);
        event.getRight().add(String.format("HexColorDisplay: #%06X", hex & 0xFFFFFF));
    }
    //?} else {
    /*public static void registerDebugEntries(RegisterDebugEntriesEvent event) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(MusaCore.MOD_ID, "hex_debug_overlay");

        event.register(id, new DebugScreenEntry() {
            @Override
            public void display(DebugScreenDisplayer displayer, Level level, LevelChunk clientChunk, LevelChunk serverChunk) {
                Integer hex = getLookedAtHexColor();
                if (hex == null) {
                    return;
                }

                displayer.addLine("hex_color: " + hex);
                displayer.addLine(String.format("HexColorDisplay: #%06X", hex & 0xFFFFFF));
            }

            @Override
            public boolean isAllowed(boolean reducedDebugInfo) {
                return true;
            }
        });
    }
    *///?}
}
