package space.anatomyuniverse.musavacca.tint;

import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import space.anatomyuniverse.musavacca.MusaCore;
import space.anatomyuniverse.musavacca.block.ModBlocks;
import space.anatomyuniverse.musavacca.block.entity.custom.HardHexBlockEntity;
import space.anatomyuniverse.musavacca.block.entity.custom.HexBlockEntity;
import space.anatomyuniverse.musavacca.block.entity.custom.PearlFireBlockEntity;

//? if >=1.21.4 {
import net.minecraft.resources.ResourceLocation;
//?}

public final class ModTints {
    private ModTints() {}

    public static void register(IEventBus modBus) {
        modBus.addListener(ModTints::registerBlockColorHandlers);

        //? if <1.21.4 {
        /*modBus.addListener(ModTints::registerItemColorHandlers);
         *///?} else {
        modBus.addListener(ModTints::registerItemTintSources);
        //?}
    }

    public static void registerBlockColorHandlers(RegisterColorHandlersEvent.Block event) {
        event.register(ModTints::getMusavaccaLeavesTint, ModBlocks.MUSAVACCA_LEAVES.get());
        event.register(ModTints::getHexBlockTint, ModBlocks.HEX_BLOCK.get());
        event.register(ModTints::getHardHexBlockTint, ModBlocks.HARD_HEX_BLOCK.get());
        event.register(ModTints::getPearlFireTint, ModBlocks.PEARL_FIRE.get());
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

    private static int getPearlFireTint(BlockState state, BlockAndTintGetter level, BlockPos pos, int tintIndex) {
        if (tintIndex < 0 || tintIndex >= PearlFireTintSource.LAYER_COUNT) {
            return TintColorUtil.NO_TINT;
        }

        if (level != null && pos != null) {
            if (level.getBlockEntity(pos) instanceof PearlFireBlockEntity pearlFireBe
                    && pearlFireBe.hasHexColor()) {
                PearlFirePlacementColorMemory.clear(pos);
                return PearlFireTintSource.blockTint(pearlFireBe.getHexColor(), tintIndex);
            }

            Integer predictedRgb = PearlFirePlacementColorMemory.get(pos);
            if (predictedRgb != null) {
                return PearlFireTintSource.blockTint(predictedRgb, tintIndex);
            }
        }

        return TintColorUtil.NO_TINT;
    }

    //? if <1.21.4 {
    /*public static void registerItemColorHandlers(RegisterColorHandlersEvent.Item event) {
        event.register((stack, tintIndex) -> {
                    if (tintIndex != 0) {
                        return TintColorUtil.NO_TINT;
                    }
                    return TintColorUtil.defaultFoliageItemTint();
                },
                ModBlocks.MUSAVACCA_LEAVES.get()
        );

        event.register((stack, tintIndex) -> {
                    if (tintIndex != 0) {
                        return TintColorUtil.NO_TINT;
                    }

                    Integer savedHex = stack.get(ModDataComponents.HEX_COLOR.get());
                    if (savedHex != null) {
                        return TintColorUtil.rgb(savedHex);
                    }

                    return TintColorUtil.defaultHexBlockItemTint();
                },
                ModBlocks.HEX_BLOCK.get()
        );

        event.register((stack, tintIndex) -> {
                    if (tintIndex != 0) {
                        return TintColorUtil.NO_TINT;
                    }

                    return TintColorUtil.rgb(HardHexBlockEntity.HARD_HEX_COLOR);
                },
                ModBlocks.HARD_HEX_BLOCK.get()
        );

        event.register((stack, tintIndex) -> {
                    if (tintIndex < 0 || tintIndex >= PearlFireTintSource.LAYER_COUNT) {
                        return TintColorUtil.NO_TINT;
                    }

                    Integer savedHex = stack.get(ModDataComponents.HEX_COLOR.get());
                    if (savedHex == null) {
                        return TintColorUtil.NO_TINT;
                    }

                    return PearlFireTintSource.itemTint(savedHex, tintIndex);
                },
                ModBlocks.PEARL_FIRE.get()
        );
    }
    *///?} else {
    public static void registerItemTintSources(RegisterColorHandlersEvent.ItemTintSources event) {
        event.register(
                ResourceLocation.fromNamespaceAndPath(MusaCore.MOD_ID, "hex_color"),
                HexColorItemTintSource.MAP_CODEC
        );
    }
    //?}
}
