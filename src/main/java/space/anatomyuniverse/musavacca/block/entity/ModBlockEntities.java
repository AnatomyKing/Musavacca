package space.anatomyuniverse.musavacca.block.entity;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import space.anatomyuniverse.musavacca.MusaCore;
import space.anatomyuniverse.musavacca.block.ModBlocks;

import java.util.Set;
import java.util.function.Supplier;

public final class ModBlockEntities {
    private ModBlockEntities() {}

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MusaCore.MOD_ID);

    public static final Supplier<BlockEntityType<HexBlockEntity>> HEX_BLOCK_ENTITY =
            BLOCK_ENTITY_TYPES.register(
                    "hex_block_entity",
                    () -> new BlockEntityType<>(
                            HexBlockEntity::new,
                            false,
                            ModBlocks.HEX_BLOCK.get()
                    )
            );

    public static final Supplier<BlockEntityType<HardHexBlockEntity>> HARD_HEX_BLOCK_ENTITY =
            BLOCK_ENTITY_TYPES.register(
                    "hard_hex_block_entity",
                    () -> new BlockEntityType<>(
                            HardHexBlockEntity::new,
                            false,
                            ModBlocks.HARD_HEX_BLOCK.get()
                    )
            );



    public static void register(IEventBus modBus) {
        BLOCK_ENTITY_TYPES.register(modBus);
    }
}