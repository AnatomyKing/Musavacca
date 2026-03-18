package space.anatomyuniverse.musavacca.block.entity;

import net.minecraft.core.registries.BuiltInRegistries;
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
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, MusaCore.MOD_ID);

    public static final Supplier<BlockEntityType<FurnitureBlockEntity>> FURNITURE =
            BLOCK_ENTITY_TYPES.register(
                    "furniture",
                    () -> new BlockEntityType<>(
                            FurnitureBlockEntity::new,
                            Set.of(ModBlocks.BANANA_PEARL_CHALICE.get()),
                            false
                    )
            );

    public static void register(IEventBus modBus) {
        BLOCK_ENTITY_TYPES.register(modBus);
    }
}