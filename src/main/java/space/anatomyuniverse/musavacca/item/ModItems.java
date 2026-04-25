package space.anatomyuniverse.musavacca.item;

import net.minecraft.world.food.Foods;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import space.anatomyuniverse.musavacca.MusaCore;
import space.anatomyuniverse.musavacca.component.ModDataComponents;
import space.anatomyuniverse.musavacca.entity.ModEntities;
import space.anatomyuniverse.musavacca.item.custom.FlintAndPearlItem;
import space.anatomyuniverse.musavacca.item.custom.OpenTestInventoryItem;
import space.anatomyuniverse.musavacca.item.custom.potassium.PotassiumAxeItem;
import space.anatomyuniverse.musavacca.item.custom.potassium.PotassiumHoeItem;
import space.anatomyuniverse.musavacca.item.custom.potassium.PotassiumShovelItem;
import space.anatomyuniverse.musavacca.item.custom.potassium.PotassiumToolItem;
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
    public static final DeferredItem<Item> OPEN_INVO_TEST_ITEM =
            ITEMS.registerItem("open_invo_test_item",
                    props -> new OpenTestInventoryItem(
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

    public static final DeferredItem<Item> VACACA =
            ITEMS.registerItem("vacaca",
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

    public static final DeferredItem<Item> POTASSIUM_INGOT =
            ITEMS.registerItem("potassium_ingot",
                    Item::new);

    public static final DeferredItem<Item> POTASSIUM_SWORD =
            ITEMS.registerItem("potassium_sword",
                    props -> new PotassiumToolItem(
                            props
                                    .sword(ToolMaterial.DIAMOND, 3.0F, -2.4F)
                                    .food(Foods.APPLE)
                    ));

    public static final DeferredItem<Item> POTASSIUM_SHOVEL =
            ITEMS.registerItem("potassium_shovel",
                    props -> new PotassiumShovelItem(
                            ToolMaterial.DIAMOND,
                            1.5F,
                            -3.0F,
                            props.food(Foods.APPLE)
                    ));

    public static final DeferredItem<Item> POTASSIUM_PICKAXE =
            ITEMS.registerItem("potassium_pickaxe",
                    props -> new PotassiumToolItem(
                            props
                                    .pickaxe(ToolMaterial.DIAMOND, 1.0F, -2.8F)
                                    .food(Foods.APPLE)
                    ));

    public static final DeferredItem<Item> POTASSIUM_AXE =
            ITEMS.registerItem("potassium_axe",
                    props -> new PotassiumAxeItem(
                            ToolMaterial.DIAMOND,
                            5.0F,
                            -3.0F,
                            props.food(Foods.APPLE)
                    ));

    public static final DeferredItem<Item> POTASSIUM_HOE =
            ITEMS.registerItem("potassium_hoe",
                    props -> new PotassiumHoeItem(
                            ToolMaterial.DIAMOND,
                            -3.0F,
                            0.0F,
                            props.food(Foods.APPLE)
                    ));


    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }

    private ModItems() {}
}
