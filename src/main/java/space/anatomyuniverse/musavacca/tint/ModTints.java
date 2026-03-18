// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/tint/ModTints.java
package space.anatomyuniverse.musavacca.tint;

import net.minecraft.client.renderer.BiomeColors;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import space.anatomyuniverse.musavacca.block.ModBlocks;
import space.anatomyuniverse.musavacca.block.entity.HardHexBlockEntity;
import space.anatomyuniverse.musavacca.block.entity.HexBlockEntity;

public final class ModTints {
    private ModTints() {}

    public static void register(IEventBus modBus) {
        modBus.addListener(ModTints::registerBlockColorHandlers);

        //? if <1.21.4 {
        /*modBus.addListener(ModTints::registerItemColorHandlers);
         *///?}
    }

    public static void registerBlockColorHandlers(RegisterColorHandlersEvent.Block event) {
        event.register((state, level, pos, tintIndex) -> {
                    if (tintIndex != 0) {
                        return 0xFFFFFFFF;
                    }

                    if (level != null && pos != null) {
                        return BiomeColors.getAverageFoliageColor(level, pos);
                    }

                    return TintColorUtil.defaultFoliageItemTint();
                },
                ModBlocks.MUSAVACCA_LEAVES.get()
        );

        event.register((state, level, pos, tintIndex) -> {
                    if (tintIndex != 0) {
                        return 0xFFFFFFFF;
                    }

                    if (level != null && pos != null) {
                        if (level.getBlockEntity(pos) instanceof HexBlockEntity hexBe && hexBe.hasHexColor()) {
                            return TintColorUtil.opaqueRgb(hexBe.getHexColor());
                        }

                        int predicted = HexColorLcg.getClientPlacementPrediction(pos);
                        if (predicted != HexColorLcg.NO_COLOR) {
                            return TintColorUtil.opaqueRgb(predicted);
                        }
                    }

                    return TintColorUtil.defaultHexBlockTint();
                },
                ModBlocks.HEX_BLOCK.get()
        );

        event.register((state, level, pos, tintIndex) -> {
                    if (tintIndex != 0) {
                        return 0xFFFFFFFF;
                    }

                    if (level != null && pos != null && level.getBlockEntity(pos) instanceof HardHexBlockEntity hardHexBe) {
                        return TintColorUtil.opaqueRgb(hardHexBe.getHexColor());
                    }

                    return TintColorUtil.opaqueRgb(HardHexBlockEntity.HARD_HEX_COLOR);
                },
                ModBlocks.HARD_HEX_BLOCK.get()
        );
    }

    //? if <1.21.4 {
    /*public static void registerItemColorHandlers(RegisterColorHandlersEvent.Item event) {
        event.register((stack, tintIndex) -> {
                    if (tintIndex == 0) {
                        return TintColorUtil.defaultFoliageItemTint();
                    }
                    return 0xFFFFFFFF;
                },
                ModBlocks.MUSAVACCA_LEAVES.get()
        );
    }
    *///?}
}