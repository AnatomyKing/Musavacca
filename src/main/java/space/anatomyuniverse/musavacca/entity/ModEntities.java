package space.anatomyuniverse.musavacca.entity;

import space.anatomyuniverse.musavacca.MusaCore;
import space.anatomyuniverse.musavacca.entity.mob.bananacow.BananaCow;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class ModEntities {

    public static final DeferredRegister.Entities ENTITY_TYPES =
            DeferredRegister.createEntities(MusaCore.MOD_ID);

    public static final Supplier<EntityType<BananaCow>> BANANA_COW =
            ENTITY_TYPES.registerEntityType(
                    "banana_cow",
                    BananaCow::new,
                    MobCategory.CREATURE,
                    b -> b.sized(0.9F, 1.4F)
            );

    public static void register(IEventBus modBus) {
        ENTITY_TYPES.register(modBus);
        modBus.addListener(ModEntities::onAttributes);
    }

    private static void onAttributes(final EntityAttributeCreationEvent e) {
        e.put(BANANA_COW.get(), BananaCow.createAttributes().build());
    }

    private ModEntities() {}
}
