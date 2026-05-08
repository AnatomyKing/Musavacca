// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/tint/ModTints.java
package space.anatomyuniverse.musavacca.tint;

import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import space.anatomyuniverse.musavacca.MusaCore;
import space.anatomyuniverse.musavacca.block.ModBlocks;
import space.anatomyuniverse.musavacca.block.custom.VocoReceptorBlock;
import space.anatomyuniverse.musavacca.block.custom.VocoTableBlock;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoInteractLogic.ReceptorPosition;
import space.anatomyuniverse.musavacca.block.entity.custom.HardHexBlockEntity;
import space.anatomyuniverse.musavacca.block.entity.custom.HexBlockEntity;
import space.anatomyuniverse.musavacca.block.entity.custom.PearlFireBlockEntity;
import space.anatomyuniverse.musavacca.block.entity.custom.PearlPortalBlockEntity;
import space.anatomyuniverse.musavacca.block.entity.custom.VocoReceptorBlockEntity;
import space.anatomyuniverse.musavacca.block.entity.custom.VocoTableBlockEntity;

//? if <1.21.4 {
/*import space.anatomyuniverse.musavacca.component.ModDataComponents;
 *///?} else {
import net.minecraft.resources.ResourceLocation;
//?}

public final class ModTints {
    private static final PearlFireTintProfiles.Profile PEARL_FIRE_PROFILE = PearlFireTintProfiles.FIRE_BLOCK;
    private static final PearlFireTintProfiles.Profile PEARL_PORTAL_PROFILE = PearlFireTintProfiles.PORTAL_BLOCK;
    private static final PearlFireTintProfiles.Profile VOCO_RECEPTOR_PORTAL_PROFILE = PearlFireTintProfiles.PORTAL_BLOCK;
    private static final PearlFireTintProfiles.Profile VOCO_TABLE_PORTAL_PROFILE = PearlFireTintProfiles.PORTAL_BLOCK;

    private static final int VOCO_TABLE_NORTH_EAST_TINT_OFFSET = 0;
    private static final int VOCO_TABLE_SOUTH_EAST_TINT_OFFSET = 100;
    private static final int VOCO_TABLE_SOUTH_WEST_TINT_OFFSET = 200;
    private static final int VOCO_TABLE_NORTH_WEST_TINT_OFFSET = 300;

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
        event.register(ModTints::getPearlPortalTint, ModBlocks.PEARL_PORTAL.get());

        event.register(ModTints::getVocoReceptorPortalTint, ModBlocks.VOCO_RECEPTOR.get());
        event.register(ModTints::getVocoTablePortalTint, ModBlocks.VOCO_TABLE.get());
    }

    private static int getMusavaccaLeavesTint(
            BlockState state,
            BlockAndTintGetter level,
            BlockPos pos,
            int tintIndex
    ) {
        if (tintIndex != 0) {
            return TintColorUtil.NO_TINT;
        }

        if (level != null && pos != null) {
            return BiomeColors.getAverageFoliageColor(level, pos);
        }

        return TintColorUtil.defaultFoliageItemTint();
    }

    private static int getHexBlockTint(
            BlockState state,
            BlockAndTintGetter level,
            BlockPos pos,
            int tintIndex
    ) {
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

    private static int getHardHexBlockTint(
            BlockState state,
            BlockAndTintGetter level,
            BlockPos pos,
            int tintIndex
    ) {
        if (tintIndex != 0) {
            return TintColorUtil.NO_TINT;
        }

        return TintColorUtil.opaqueRgb(HardHexBlockEntity.HARD_HEX_COLOR);
    }

    private static int getPearlFireTint(
            BlockState state,
            BlockAndTintGetter level,
            BlockPos pos,
            int tintIndex
    ) {
        if (!PearlFireTintSource.supportsLayer(PEARL_FIRE_PROFILE, tintIndex)) {
            return TintColorUtil.NO_TINT;
        }

        if (level != null && pos != null) {
            if (level.getBlockEntity(pos) instanceof PearlFireBlockEntity pearlFireBe
                    && pearlFireBe.hasHexColor()) {
                return PearlFireTintSource.blockTint(
                        pearlFireBe.getHexColor(),
                        tintIndex,
                        PEARL_FIRE_PROFILE
                );
            }

            Integer predictedRgb = PearlPlacementColorMemory.get(level, pos);
            if (predictedRgb != null) {
                return PearlFireTintSource.blockTint(
                        predictedRgb,
                        tintIndex,
                        PEARL_FIRE_PROFILE
                );
            }
        }

        return TintColorUtil.NO_TINT;
    }

    private static int getPearlPortalTint(
            BlockState state,
            BlockAndTintGetter level,
            BlockPos pos,
            int tintIndex
    ) {
        if (!PearlFireTintSource.supportsLayer(PEARL_PORTAL_PROFILE, tintIndex)) {
            return TintColorUtil.NO_TINT;
        }

        if (level != null && pos != null) {
            if (level.getBlockEntity(pos) instanceof PearlPortalBlockEntity pearlPortalBe
                    && pearlPortalBe.isValidPortalTile()) {
                return PearlFireTintSource.blockTint(
                        pearlPortalBe.getHexColor(),
                        tintIndex,
                        PEARL_PORTAL_PROFILE
                );
            }

            Integer predictedRgb = PearlPlacementColorMemory.get(level, pos);
            if (predictedRgb != null) {
                return PearlFireTintSource.blockTint(
                        predictedRgb,
                        tintIndex,
                        PEARL_PORTAL_PROFILE
                );
            }
        }

        return TintColorUtil.NO_TINT;
    }

    private static int getVocoReceptorPortalTint(
            BlockState state,
            BlockAndTintGetter level,
            BlockPos pos,
            int tintIndex
    ) {
        if (!state.hasProperty(VocoReceptorBlock.PORTAL) || !state.getValue(VocoReceptorBlock.PORTAL)) {
            return TintColorUtil.NO_TINT;
        }

        if (!PearlFireTintSource.supportsLayer(VOCO_RECEPTOR_PORTAL_PROFILE, tintIndex)) {
            return TintColorUtil.NO_TINT;
        }

        if (level != null && pos != null
                && level.getBlockEntity(pos) instanceof VocoReceptorBlockEntity receptorBe
                && receptorBe.hasHexColor()) {
            return PearlFireTintSource.blockTint(
                    receptorBe.getHexColor(),
                    tintIndex,
                    VOCO_RECEPTOR_PORTAL_PROFILE
            );
        }

        return TintColorUtil.NO_TINT;
    }

    private static int getVocoTablePortalTint(
            BlockState state,
            BlockAndTintGetter level,
            BlockPos pos,
            int tintIndex
    ) {
        VocoTableTintLayer layer = tableTintLayer(tintIndex);
        if (layer == null) {
            return TintColorUtil.NO_TINT;
        }

        if (!PearlFireTintSource.supportsLayer(VOCO_TABLE_PORTAL_PROFILE, layer.layerIndex())) {
            return TintColorUtil.NO_TINT;
        }

        if (!state.hasProperty(VocoTableBlock.portalProperty(layer.receptor()))
                || !state.getValue(VocoTableBlock.portalProperty(layer.receptor()))) {
            return TintColorUtil.NO_TINT;
        }

        if (level != null && pos != null
                && level.getBlockEntity(pos) instanceof VocoTableBlockEntity tableBe) {
            return PearlFireTintSource.blockTint(
                    tableBe.getCornerHexColor(layer.receptor()),
                    layer.layerIndex(),
                    VOCO_TABLE_PORTAL_PROFILE
            );
        }

        return PearlFireTintSource.blockTint(
                VocoTableBlockEntity.defaultHexColor(layer.receptor()),
                layer.layerIndex(),
                VOCO_TABLE_PORTAL_PROFILE
        );
    }

    private static VocoTableTintLayer tableTintLayer(int tintIndex) {
        int layerCount = VOCO_TABLE_PORTAL_PROFILE.layerCount();

        if (isInsideTintRange(tintIndex, VOCO_TABLE_NORTH_EAST_TINT_OFFSET, layerCount)) {
            return new VocoTableTintLayer(
                    ReceptorPosition.NORTH_EAST,
                    tintIndex - VOCO_TABLE_NORTH_EAST_TINT_OFFSET
            );
        }

        if (isInsideTintRange(tintIndex, VOCO_TABLE_SOUTH_EAST_TINT_OFFSET, layerCount)) {
            return new VocoTableTintLayer(
                    ReceptorPosition.SOUTH_EAST,
                    tintIndex - VOCO_TABLE_SOUTH_EAST_TINT_OFFSET
            );
        }

        if (isInsideTintRange(tintIndex, VOCO_TABLE_SOUTH_WEST_TINT_OFFSET, layerCount)) {
            return new VocoTableTintLayer(
                    ReceptorPosition.SOUTH_WEST,
                    tintIndex - VOCO_TABLE_SOUTH_WEST_TINT_OFFSET
            );
        }

        if (isInsideTintRange(tintIndex, VOCO_TABLE_NORTH_WEST_TINT_OFFSET, layerCount)) {
            return new VocoTableTintLayer(
                    ReceptorPosition.NORTH_WEST,
                    tintIndex - VOCO_TABLE_NORTH_WEST_TINT_OFFSET
            );
        }

        return null;
    }

    private static boolean isInsideTintRange(int tintIndex, int offset, int layerCount) {
        return tintIndex >= offset && tintIndex < offset + layerCount;
    }

    private record VocoTableTintLayer(ReceptorPosition receptor, int layerIndex) {}

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
                        return TintColorUtil.opaqueRgb(savedHex);
                    }

                    return TintColorUtil.opaqueRgb(TintColorUtil.defaultHexBlockItemTint());
                },
                ModBlocks.HEX_BLOCK.get()
        );

        event.register((stack, tintIndex) -> {
                    if (tintIndex != 0) {
                        return TintColorUtil.NO_TINT;
                    }

                    return TintColorUtil.opaqueRgb(HardHexBlockEntity.HARD_HEX_COLOR);
                },
                ModBlocks.HARD_HEX_BLOCK.get()
        );

        event.register((stack, tintIndex) -> {
                    if (!PearlFireTintSource.supportsLayer(PEARL_FIRE_PROFILE, tintIndex)) {
                        return TintColorUtil.NO_TINT;
                    }

                    Integer savedHex = stack.get(ModDataComponents.HEX_COLOR.get());
                    if (savedHex == null) {
                        return TintColorUtil.NO_TINT;
                    }

                    return PearlFireTintSource.blockTint(
                            savedHex,
                            tintIndex,
                            PEARL_FIRE_PROFILE
                    );
                },
                ModBlocks.PEARL_FIRE.get()
        );

        event.register((stack, tintIndex) -> {
                    if (!PearlFireTintSource.supportsLayer(PEARL_PORTAL_PROFILE, tintIndex)) {
                        return TintColorUtil.NO_TINT;
                    }

                    Integer savedHex = stack.get(ModDataComponents.HEX_COLOR.get());
                    if (savedHex == null) {
                        return TintColorUtil.NO_TINT;
                    }

                    return PearlFireTintSource.blockTint(
                            savedHex,
                            tintIndex,
                            PEARL_PORTAL_PROFILE
                    );
                },
                ModBlocks.PEARL_PORTAL.get()
        );
    }
    *///?} else {
    public static void registerItemTintSources(RegisterColorHandlersEvent.ItemTintSources event) {
        event.register(
                ResourceLocation.fromNamespaceAndPath(MusaCore.MOD_ID, "hex_color"),
                HexColorItemTintSource.MAP_CODEC
        );

        event.register(
                ResourceLocation.fromNamespaceAndPath(MusaCore.MOD_ID, "profile_hex_color"),
                ProfileHexColorItemTintSource.MAP_CODEC
        );
    }
    //?}
}