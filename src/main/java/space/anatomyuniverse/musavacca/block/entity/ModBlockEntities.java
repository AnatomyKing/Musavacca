package space.anatomyuniverse.musavacca.block.entity;

import java.util.function.BiFunction;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import space.anatomyuniverse.musavacca.MusaCore;
import space.anatomyuniverse.musavacca.block.ModBlocks;
import space.anatomyuniverse.musavacca.block.entity.custom.*;

public final class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MusaCore.MOD_ID);
    public static final Supplier<BlockEntityType<HexBlockEntity>> HEX_BLOCK_ENTITY =
            register("hex_block_entity", HexBlockEntity::new, () -> ModBlocks.HEX_BLOCK.get());
    public static final Supplier<BlockEntityType<HardHexBlockEntity>> HARD_HEX_BLOCK_ENTITY =
            register("hard_hex_block_entity", HardHexBlockEntity::new, () -> ModBlocks.HARD_HEX_BLOCK.get());
    public static final Supplier<BlockEntityType<PearlFireBlockEntity>> PEARL_FIRE_BLOCK_ENTITY =
            register("pearl_fire_block_entity", PearlFireBlockEntity::new, () -> ModBlocks.PEARL_FIRE.get());
    public static final Supplier<BlockEntityType<MusavaccaPortalDoorBlockEntity>> MUSAVACCA_DOOR_BLOCK_ENTITY =
            register("musavacca_door_block_entity", MusavaccaPortalDoorBlockEntity::new, () -> ModBlocks.MUSAVACCA_DOOR.get());
    public static final Supplier<BlockEntityType<MusavaccaPortalTrapdoorBlockEntity>> MUSAVACCA_TRAPDOOR_BLOCK_ENTITY =
            register("musavacca_trapdoor_block_entity", MusavaccaPortalTrapdoorBlockEntity::new, () -> ModBlocks.MUSAVACCA_TRAPDOOR.get());
    public static final Supplier<BlockEntityType<PearlCandleBlockEntity>> PEARL_CANDLE_BLOCK_ENTITY =
            register("pearl_candle_block_entity", PearlCandleBlockEntity::new, () -> ModBlocks.PEARL_CANDLE.get());
    public static final Supplier<BlockEntityType<VocoPostBlockEntity>> VOCO_POST_BLOCK_ENTITY =
            register("voco_post_block_entity", VocoPostBlockEntity::new, () -> ModBlocks.VOCO_POST.get());
    public static final Supplier<BlockEntityType<VocoTableBlockEntity>> VOCO_TABLE_BLOCK_ENTITY =
            register("voco_table_block_entity", VocoTableBlockEntity::new, () -> ModBlocks.VOCO_TABLE.get());
    public static final Supplier<BlockEntityType<PearlPortalBlockEntity>> PEARL_PORTAL_BLOCK_ENTITY =
            register("pearl_portal_block_entity", PearlPortalBlockEntity::new, () -> ModBlocks.PEARL_PORTAL.get());

    private ModBlockEntities() {}

    private static <T extends BlockEntity> Supplier<BlockEntityType<T>> register(String name, BiFunction<BlockPos, BlockState, T> factory,
            Supplier<? extends Block> block) {
        return BLOCK_ENTITY_TYPES.register(name, () ->
                //? if >=1.21.4 {
                new BlockEntityType<>(factory::apply, false, block.get())
                //?} else if >=1.21.2 <1.21.4 {
                /*new BlockEntityType<>(factory::apply, block.get())
                *///?} else {
                /*BlockEntityType.Builder.<T>of(factory::apply, block.get()).build(null)
                *///?}
        );
    }

    public static void register(IEventBus modBus) {
        BLOCK_ENTITY_TYPES.register(modBus);
    }
}
