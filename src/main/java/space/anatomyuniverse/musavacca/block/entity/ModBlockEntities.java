package space.anatomyuniverse.musavacca.block.entity;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import space.anatomyuniverse.musavacca.MusaCore;
import space.anatomyuniverse.musavacca.block.ModBlocks;
import space.anatomyuniverse.musavacca.block.entity.custom.*;

import java.util.function.Supplier;

public final class ModBlockEntities {
    private ModBlockEntities() {}

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MusaCore.MOD_ID);

    public static final Supplier<BlockEntityType<HexBlockEntity>> HEX_BLOCK_ENTITY =
            BLOCK_ENTITY_TYPES.register(
                    "hex_block_entity",
                    () ->
                            //? if <1.21.2 {
                            /*BlockEntityType.Builder.of(
                                    HexBlockEntity::new,
                                    ModBlocks.HEX_BLOCK.get()
                            ).build(null)
                            *///?} else if <1.21.4 {
                            /*new BlockEntityType<>(
                                    HexBlockEntity::new,
                                    ModBlocks.HEX_BLOCK.get()
                            )
                            *///?} else {
                            new BlockEntityType<>(
                                    HexBlockEntity::new,
                                    false,
                                    ModBlocks.HEX_BLOCK.get()
                            )
                    //?}
            );

    public static final Supplier<BlockEntityType<HardHexBlockEntity>> HARD_HEX_BLOCK_ENTITY =
            BLOCK_ENTITY_TYPES.register(
                    "hard_hex_block_entity",
                    () ->
                            //? if <1.21.2 {
                            /*BlockEntityType.Builder.of(
                                    HardHexBlockEntity::new,
                                    ModBlocks.HARD_HEX_BLOCK.get()
                            ).build(null)
                            *///?} else if <1.21.4 {
                            /*new BlockEntityType<>(
                                    HardHexBlockEntity::new,
                                    ModBlocks.HARD_HEX_BLOCK.get()
                            )
                            *///?} else {
                            new BlockEntityType<>(
                                    HardHexBlockEntity::new,
                                    false,
                                    ModBlocks.HARD_HEX_BLOCK.get()
                            )
                    //?}
            );

    public static final Supplier<BlockEntityType<PearlFireBlockEntity>> PEARL_FIRE_BLOCK_ENTITY =
            BLOCK_ENTITY_TYPES.register(
                    "pearl_fire_block_entity",
                    () ->
                            //? if <1.21.2 {
                            /*BlockEntityType.Builder.of(
                                    PearlFireBlockEntity::new,
                                    ModBlocks.PEARL_FIRE.get()
                            ).build(null)
                            *///?} else if <1.21.4 {
                            /*new BlockEntityType<>(
                                    PearlFireBlockEntity::new,
                                    ModBlocks.PEARL_FIRE.get()
                            )
                            *///?} else {
                            new BlockEntityType<>(
                                    PearlFireBlockEntity::new,
                                    false,
                                    ModBlocks.PEARL_FIRE.get()
                            )
                    //?}
            );

    public static final Supplier<
            BlockEntityType<
                    MusavaccaPortalDoorBlockEntity
                    >
            >
            MUSAVACCA_DOOR_BLOCK_ENTITY =
            BLOCK_ENTITY_TYPES.register(
                    "musavacca_door_block_entity",
                    () ->
                            //? if <1.21.2 {
                        /*BlockEntityType.Builder.of(
                                MusavaccaPortalDoorBlockEntity::new,
                                ModBlocks
                                        .MUSAVACCA_DOOR
                                        .get()
                        ).build(null)
                        *///?} else if <1.21.4 {
                        /*new BlockEntityType<>(
                                MusavaccaPortalDoorBlockEntity::new,
                                ModBlocks
                                        .MUSAVACCA_DOOR
                                        .get()
                        )
                        *///?} else {
                            new BlockEntityType<>(
                                    MusavaccaPortalDoorBlockEntity::new,
                                    false,
                                    ModBlocks
                                            .MUSAVACCA_DOOR
                                            .get()
                            )
                    //?}
            );

    public static final Supplier<BlockEntityType<PearlCandleBlockEntity>> PEARL_CANDLE_BLOCK_ENTITY =
            BLOCK_ENTITY_TYPES.register(
                    "pearl_candle_block_entity",
                    () ->
                            //? if <1.21.2 {
                            /*BlockEntityType.Builder.of(
                                    PearlCandleBlockEntity::new,
                                    pearlCandleBlocks()
                            ).build(null)
                            *///?} else if <1.21.4 {
                            /*new BlockEntityType<>(
                                    PearlCandleBlockEntity::new,
                                    pearlCandleBlocks()
                            )
                            *///?} else {
                            new BlockEntityType<>(
                                    PearlCandleBlockEntity::new,
                                    false,
                                    pearlCandleBlocks()
                            )
                    //?}
            );

    public static final Supplier<BlockEntityType<VocoPostBlockEntity>> VOCO_POST_BLOCK_ENTITY =
            BLOCK_ENTITY_TYPES.register(
                    "voco_post_block_entity",
                    () ->
                            //? if <1.21.2 {
                            /*BlockEntityType.Builder.of(
                                    VocoPostBlockEntity::new,
                                    ModBlocks.VOCO_POST.get()
                            ).build(null)
                            *///?} else if <1.21.4 {
                            /*new BlockEntityType<>(
                                    VocoPostBlockEntity::new,
                                    ModBlocks.VOCO_POST.get()
                            )
                            *///?} else {
                            new BlockEntityType<>(
                                    VocoPostBlockEntity::new,
                                    false,
                                    ModBlocks.VOCO_POST.get()
                            )
                    //?}
            );

    public static final Supplier<BlockEntityType<VocoTableBlockEntity>> VOCO_TABLE_BLOCK_ENTITY =
            BLOCK_ENTITY_TYPES.register(
                    "voco_table_block_entity",
                    () ->
                            //? if <1.21.2 {
                            /*BlockEntityType.Builder.of(
                                    VocoTableBlockEntity::new,
                                    ModBlocks.VOCO_TABLE.get()
                            ).build(null)
                            *///?} else if <1.21.4 {
                            /*new BlockEntityType<>(
                                    VocoTableBlockEntity::new,
                                    ModBlocks.VOCO_TABLE.get()
                            )
                            *///?} else {
                            new BlockEntityType<>(
                                    VocoTableBlockEntity::new,
                                    false,
                                    ModBlocks.VOCO_TABLE.get()
                            )
                    //?}
            );

    public static final Supplier<BlockEntityType<PearlPortalBlockEntity>> PEARL_PORTAL_BLOCK_ENTITY =
            BLOCK_ENTITY_TYPES.register(
                    "pearl_portal_block_entity",
                    () ->
                            //? if <1.21.2 {
                            /*BlockEntityType.Builder.of(
                                    PearlPortalBlockEntity::new,
                                    ModBlocks.PEARL_PORTAL.get()
                            ).build(null)
                            *///?} else if <1.21.4 {
                            /*new BlockEntityType<>(
                                    PearlPortalBlockEntity::new,
                                    ModBlocks.PEARL_PORTAL.get()
                            )
                            *///?} else {
                            new BlockEntityType<>(
                                    PearlPortalBlockEntity::new,
                                    false,
                                    ModBlocks.PEARL_PORTAL.get()
                            )
                    //?}
            );

    private static Block[] pearlCandleBlocks() {
        return ModBlocks.PEARL_CANDLES.stream()
                .map(DeferredHolder::get)
                .toArray(Block[]::new);
    }

    public static void register(IEventBus modBus) {
        BLOCK_ENTITY_TYPES.register(modBus);
    }
}