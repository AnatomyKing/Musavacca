// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/tint/ModTints.java
package space.anatomyuniverse.musavacca.tint;

import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import space.anatomyuniverse.musavacca.MusaCore;
import space.anatomyuniverse.musavacca.block.ModBlocks;
import space.anatomyuniverse.musavacca.block.custom.VocoPostBlock;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoReceptorLogic.ReceptorPosition;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoTableLogic;
import space.anatomyuniverse.musavacca.block.entity.custom.HardHexBlockEntity;
import space.anatomyuniverse.musavacca.block.entity.custom.HexBlockEntity;
import space.anatomyuniverse.musavacca.block.entity.custom.PearlFireBlockEntity;
import space.anatomyuniverse.musavacca.block.entity.custom.PearlPortalBlockEntity;
import space.anatomyuniverse.musavacca.block.entity.custom.VocoPostBlockEntity;
import space.anatomyuniverse.musavacca.block.entity.custom.VocoTableBlockEntity;

//? if <1.21.4 {
/*import space.anatomyuniverse.musavacca.component.ModDataComponents;
 *///?} else {
import net.minecraft.resources.ResourceLocation;
//?}

public final class ModTints {
    private static final PearlFireTintProfiles.Profile PEARL_FIRE_PROFILE = PearlFireTintProfiles.FIRE_BLOCK;
    private static final PearlFireTintProfiles.Profile PEARL_PORTAL_PROFILE = PearlFireTintProfiles.PORTAL_BLOCK;
    private static final PearlFireTintProfiles.Profile VOCO_POST_PORTAL_PROFILE = PearlFireTintProfiles.PORTAL_BLOCK;
    private static final PearlFireTintProfiles.Profile VOCO_TABLE_PORTAL_PROFILE = PearlFireTintProfiles.PORTAL_BLOCK;

    private static final VocoTableTintRange[] VOCO_TABLE_TINT_RANGES = {
            new VocoTableTintRange(ReceptorPosition.NORTH_EAST, 0),
            new VocoTableTintRange(ReceptorPosition.SOUTH_EAST, 100),
            new VocoTableTintRange(ReceptorPosition.SOUTH_WEST, 200),
            new VocoTableTintRange(ReceptorPosition.NORTH_WEST, 300)
    };

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
        event.register(
                ModTints::getMusavaccaFoliageTint,
                ModBlocks.MUSAVACCA_LEAVES.get(),
                ModBlocks.MUSAVACCA_SPROUT.get(),
                ModBlocks.MUSAVACCA_SUCKER.get(),
                ModBlocks.MUSAVACCA_PLANT.get(),
                ModBlocks.MUSAVACCA_PSEUDOSTEM.get()
        );

        event.register(ModTints::getHexBlockTint, ModBlocks.HEX_BLOCK.get());
        event.register(ModTints::getHardHexBlockTint, ModBlocks.HARD_HEX_BLOCK.get());

        event.register(ModTints::getPearlFireTint, ModBlocks.PEARL_FIRE.get());
        event.register(ModTints::getPearlPortalTint, ModBlocks.PEARL_PORTAL.get());

        event.register(ModTints::getVocoPostPortalTint, ModBlocks.VOCO_POST.get());
        event.register(ModTints::getVocoTablePortalTint, ModBlocks.VOCO_TABLE.get());
    }

    private static int getMusavaccaFoliageTint(
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

    private static int getVocoPostPortalTint(
            BlockState state,
            BlockAndTintGetter level,
            BlockPos pos,
            int tintIndex
    ) {
        if (!state.hasProperty(VocoPostBlock.PORTAL) || !state.getValue(VocoPostBlock.PORTAL)) {
            return TintColorUtil.NO_TINT;
        }

        if (!PearlFireTintSource.supportsLayer(VOCO_POST_PORTAL_PROFILE, tintIndex)) {
            return TintColorUtil.NO_TINT;
        }

        if (level != null && pos != null
                && level.getBlockEntity(pos) instanceof VocoPostBlockEntity postBe
                && postBe.hasHexColor()) {
            return PearlFireTintSource.blockTint(
                    postBe.getHexColor(),
                    tintIndex,
                    VOCO_POST_PORTAL_PROFILE
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

        BooleanProperty portalProperty = VocoTableLogic.portalProperty(layer.receptor());
        if (!state.hasProperty(portalProperty) || !state.getValue(portalProperty)) {
            return TintColorUtil.NO_TINT;
        }

        if (level == null || pos == null
                || !(level.getBlockEntity(pos) instanceof VocoTableBlockEntity tableBe)) {
            return TintColorUtil.NO_TINT;
        }

        int hexColor = tableBe.getPortalHexColorOrUnset(layer.receptor());
        if (hexColor == VocoTableBlockEntity.UNSET_HEX_COLOR) {
            return TintColorUtil.NO_TINT;
        }

        return PearlFireTintSource.blockTint(
                hexColor,
                layer.layerIndex(),
                VOCO_TABLE_PORTAL_PROFILE
        );
    }

    private static VocoTableTintLayer tableTintLayer(int tintIndex) {
        int layerCount = VOCO_TABLE_PORTAL_PROFILE.layerCount();

        for (VocoTableTintRange range : VOCO_TABLE_TINT_RANGES) {
            if (tintIndex >= range.offset() && tintIndex < range.offset() + layerCount) {
                return new VocoTableTintLayer(
                        range.receptor(),
                        tintIndex - range.offset()
                );
            }
        }

        return null;
    }

    private record VocoTableTintRange(ReceptorPosition receptor, int offset) {}

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