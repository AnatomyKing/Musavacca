package space.anatomyuniverse.musavacca.entity;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import space.anatomyuniverse.musavacca.MusaCore;
import space.anatomyuniverse.musavacca.entity.boat.musavacca.MusavaccaBoat;
import space.anatomyuniverse.musavacca.entity.mob.bananacow.BananaCow;
import space.anatomyuniverse.musavacca.entity.mob.basuke.Basuke;

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
                            //? if <1.21.2 {
                            /*.build(registryName.toString())
                    *///?} else {
                    .build(net.minecraft.resources.ResourceKey.create(Registries.ENTITY_TYPE, registryName))
                     //?}
            );

    public static final Supplier<EntityType<Basuke>> BASUKE =
            ENTITY_TYPES.register(
                    "basuke",
                    registryName -> EntityType.Builder
                            .of(Basuke::new, MobCategory.CREATURE)
                            .sized(0.35F, 0.60F)
                            //? if <1.21.2 {
                            /*.build(registryName.toString())
                    *///?} else {
                    .build(net.minecraft.resources.ResourceKey.create(Registries.ENTITY_TYPE, registryName))
                     //?}
            );

    public static final Supplier<EntityType<MusavaccaBoat>>
            MUSAVACCA_BOAT =
            ENTITY_TYPES.register(
                    "musavacca_boat",
                    registryName -> EntityType.Builder
                            .of(
                                    MusavaccaBoat::new,
                                    MobCategory.MISC
                            )
                            .sized(
                                    MusavaccaBoat.HITBOX_WIDTH,
                                    MusavaccaBoat.HITBOX_HEIGHT
                            )
                            .clientTrackingRange(10)
                            .updateInterval(3)
                            //? if <1.21.2 {
                            /*.build(registryName.toString())
                             *///?} else {
                            .build(
                                    net.minecraft.resources.ResourceKey.create(
                                            Registries.ENTITY_TYPE,
                                            registryName
                                    )
                            )
                    //?}
            );

    public static void register(IEventBus modBus) {
        ENTITY_TYPES.register(modBus);
        modBus.addListener(ModEntities::onAttributes);
    }

    private static void onAttributes(final EntityAttributeCreationEvent event) {
        event.put(BANANA_COW.get(), BananaCow.createAttributes().build());
        event.put(BASUKE.get(), Basuke.createAttributes().build());
    }
}



