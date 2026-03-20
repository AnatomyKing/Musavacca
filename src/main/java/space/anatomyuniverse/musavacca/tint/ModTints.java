// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/tint/ModTints.java
package space.anatomyuniverse.musavacca.tint;

import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
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
        event.register(ModTints::getMusavaccaLeavesTint, ModBlocks.MUSAVACCA_LEAVES.get());
        event.register(ModTints::getHexBlockTint, ModBlocks.HEX_BLOCK.get());
        event.register(ModTints::getHardHexBlockTint, ModBlocks.HARD_HEX_BLOCK.get());
    }

    private static int getMusavaccaLeavesTint(BlockState state, BlockAndTintGetter level, BlockPos pos, int tintIndex) {
        if (tintIndex != 0) {
            return TintColorUtil.NO_TINT;
        }

        if (level != null && pos != null) {
            return BiomeColors.getAverageFoliageColor(level, pos);
        }

        return TintColorUtil.defaultFoliageItemTint();
    }

    private static int getHexBlockTint(BlockState state, BlockAndTintGetter level, BlockPos pos, int tintIndex) {
        if (tintIndex != 0) {
            return TintColorUtil.NO_TINT;
        }

        if (level != null && pos != null
                && level.getBlockEntity(pos) instanceof HexBlockEntity hexBe
                && hexBe.hasHexColor()) {
            return TintColorUtil.opaqueRgb(hexBe.getHexColor());
        }

        return TintColorUtil.defaultHexBlockTint();
    }

    private static int getHardHexBlockTint(BlockState state, BlockAndTintGetter level, BlockPos pos, int tintIndex) {
        if (tintIndex != 0) {
            return TintColorUtil.NO_TINT;
        }

        return TintColorUtil.opaqueRgb(HardHexBlockEntity.HARD_HEX_COLOR);
    }

    //? if <1.21.4 {
    /*public static void registerItemColorHandlers(RegisterColorHandlersEvent.Item event) {
        event.register((stack, tintIndex) -> {
                    if (tintIndex == 0) {
                        return TintColorUtil.defaultFoliageItemTint();
                    }
                    return TintColorUtil.NO_TINT;
                },
                ModBlocks.MUSAVACCA_LEAVES.get()
        );
    }
    *///?}
}