package space.anatomyuniverse.musavacca.entity;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import space.anatomyuniverse.musavacca.MusaCore;
import space.anatomyuniverse.musavacca.entity.mob.bananacow.BananaCow;

import java.util.function.Supplier;

public final class ModEntities {

    private ModEntities() {}

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, MusaCore.MOD_ID);

    public static final Supplier<EntityType<BananaCow>> BANANA_COW =
            ENTITY_TYPES.register(
                    "banana_cow",
                    registryName -> EntityType.Builder
                            .of(BananaCow::new, MobCategory.CREATURE)
                            .sized(0.9F, 1.4F)
                            .build(ResourceKey.create(Registries.ENTITY_TYPE, registryName))
            );

    public static void register(IEventBus modBus) {
        ENTITY_TYPES.register(modBus);
        modBus.addListener(ModEntities::onAttributes);
    }

    private static void onAttributes(final EntityAttributeCreationEvent event) {
        event.put(BANANA_COW.get(), BananaCow.createAttributes().build());
    }
}