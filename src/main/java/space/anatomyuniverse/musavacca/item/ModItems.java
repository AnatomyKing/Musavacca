package space.anatomyuniverse.musavacca.item;

import net.minecraft.world.food.Foods;
import net.minecraft.world.item.FlintAndSteelItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SpawnEggItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import space.anatomyuniverse.musavacca.MusaCore;
import space.anatomyuniverse.musavacca.component.ModDataComponents;
import space.anatomyuniverse.musavacca.entity.ModEntities;
import space.anatomyuniverse.musavacca.item.custom.FlintAndPearlItem;
import space.anatomyuniverse.musavacca.item.custom.SmallBananaPearlItem;

public final class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MusaCore.MOD_ID);

    public static final DeferredItem<Item> BANANA_PEARL =
            ITEMS.registerItem("banana_pearl", props -> new Item(props.rarity(Rarity.RARE)));

    public static final DeferredItem<Item> BIG_BANANA_PEARL =
            ITEMS.registerItem("big_banana_pearl", props -> new Item(props.rarity(Rarity.RARE)));

    public static final DeferredItem<Item> SMALL_BANANA_PEARL =
            ITEMS.registerItem("small_banana_pearl",
                    props -> new SmallBananaPearlItem(
                            props.rarity(Rarity.RARE)
                    ));

    public static final DeferredItem<SpawnEggItem> BANANA_COW_SPAWN_EGG =
            ITEMS.registerItem("banana_cow_spawn_egg",
                    props ->
                            //? if <1.21.4 {
                        /*new SpawnEggItem(
                                ModEntities.BANANA_COW.get(),
                                0xE4C64A,
                                0x7A4A1F,
                                props
                        )
                            *///?} else if <1.21.9 {
                            new SpawnEggItem(ModEntities.BANANA_COW.get(), props)
                    //?} else {
                    /*new SpawnEggItem(props.spawnEgg(ModEntities.BANANA_COW.get()))
                     *///?}
            );

    public static final DeferredItem<SpawnEggItem> BASUKE_SPAWN_EGG =
            ITEMS.registerItem("basuke_spawn_egg",
                    props ->
                            //? if <1.21.4 {
                            /*new SpawnEggItem(
                                    ModEntities.BASUKE.get(),
                                    0xE6DCC8,
                                    0x4F3F36,
                                    props
                            )
                    *///?} else if <1.21.9 {
                    new SpawnEggItem(ModEntities.BASUKE.get(), props)
                     //?} else {
                    /*new SpawnEggItem(props.spawnEgg(ModEntities.BASUKE.get()))
                     *///?}
            );

    public static final DeferredItem<Item> BANANA =
            ITEMS.registerItem("banana",
                    props -> new Item(props.food(Foods.APPLE)));

//    public static final DeferredItem<Item> ITEM_INTERACT =
//            ITEMS.registerItem("item_interact",
//                    props -> new ItemInteract(
//                            props.durability(64).rarity(Rarity.COMMON)
//                    ));


    public static final DeferredItem<Item> FLINT_AND_PEARL =
            ITEMS.registerItem("flint_and_pearl",
                    props -> new FlintAndPearlItem(
                            props
                                    .durability(64)
                                    .component(ModDataComponents.HEX_COLOR.get(), FlintAndPearlItem.DEFAULT_HEX_COLOR)
                    ));


    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }

    private ModItems() {}
}
