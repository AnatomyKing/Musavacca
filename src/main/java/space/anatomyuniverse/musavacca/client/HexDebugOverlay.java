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
import space.anatomyuniverse.musavacca.tint.MusavaccaTints;

import java.util.List;

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

    private static DataComponentMap getLookedAtComponents() {
        Minecraft mc = Minecraft.getInstance();

        if (mc.level == null || !(mc.hitResult instanceof BlockHitResult hit)) {
            return null;
        }

        BlockEntity blockEntity = mc.level.getBlockEntity(hit.getBlockPos());
        return blockEntity == null ? null : blockEntity.collectComponents();
    }

    private static void addHexDebugLines(LineSink sink) {
        DataComponentMap components = getLookedAtComponents();

        if (components == null) {
            return;
        }

        Integer hex = components.get(ModDataComponents.HEX_COLOR.get());

        if (hex != null) {
            sink.add("hex_color: " + MusavaccaTints.formatHex(hex));
        }

        List<Integer> multi = components.get(ModDataComponents.MULTI_HEX_COLOR.get());

        if (multi == null) {
            return;
        }

        sink.add("multi_hex_color:");

        for (int index = 0; index < multi.size(); index++) {
            int color = MusavaccaTints.multiColor(multi, index);

            sink.add(
                    "  [" + index + "]: "
                            + (color == MusavaccaTints.NO_TINT
                            ? "none"
                            : MusavaccaTints.formatHex(color))
            );
        }
    }

    @FunctionalInterface
    private interface LineSink {
        void add(String line);
    }

    //? if <1.21.9 {
    @SubscribeEvent
    public static void onDebugText(CustomizeGuiOverlayEvent.DebugText event) {
        addHexDebugLines(event.getRight()::add);
    }
    //?} else {
    /*@SubscribeEvent
    public static void registerDebugEntries(RegisterDebugEntriesEvent event) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(
                MusaCore.MOD_ID,
                "hex_debug_overlay"
        );

        event.register(id, new DebugScreenEntry() {
            @Override
            public void display(
                    DebugScreenDisplayer displayer,
                    Level level,
                    LevelChunk clientChunk,
                    LevelChunk serverChunk
            ) {
                addHexDebugLines(displayer::addLine);
            }

            @Override
            public boolean isAllowed(boolean reducedDebugInfo) {
                return true;
            }
        });
    }
    *///?}

}