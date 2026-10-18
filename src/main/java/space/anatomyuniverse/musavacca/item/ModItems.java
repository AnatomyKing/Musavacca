package space.anatomyuniverse.musavacca.item;

//? if >=1.21.11 {
/*import net.minecraft.util.Util;
*///?} else {
import net.minecraft.Util;
//?}

//? if <1.21.2 {
/*import net.minecraft.core.Holder;
*///?}
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
//? if >=1.21.11 {
/*import net.minecraft.resources.Identifier;
*///?} else {
import net.minecraft.resources.ResourceLocation;
//?}
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.item.component.BundleContents;

//? if >=1.21.4 {
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;
//?} else if >=1.21.2 <1.21.4 {
/*import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
*///?} else {
/*import net.minecraft.world.item.crafting.Ingredient;
*///?}

import net.neoforged.bus.api.IEventBus;

//? if <1.21.2 {
/*import net.neoforged.neoforge.registries.DeferredHolder;

import net.neoforged.neoforge.common.SimpleTier;
*///?}
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import space.anatomyuniverse.musavacca.MusaCore;
import space.anatomyuniverse.musavacca.block.ModBlocks;
import space.anatomyuniverse.musavacca.block.custom.MusavaccaPortalDoorBlock;
import space.anatomyuniverse.musavacca.component.ModDataComponents;
import space.anatomyuniverse.musavacca.entity.ModEntities;
import space.anatomyuniverse.musavacca.item.custom.*;
import space.anatomyuniverse.musavacca.item.custom.potassium.PotassiumArmorItem;
import space.anatomyuniverse.musavacca.item.custom.potassium.PotassiumAxeItem;
import space.anatomyuniverse.musavacca.item.custom.potassium.PotassiumHoeItem;
import space.anatomyuniverse.musavacca.item.custom.potassium.PotassiumPickaxeItem;
import space.anatomyuniverse.musavacca.item.custom.potassium.PotassiumShovelItem;
import space.anatomyuniverse.musavacca.item.custom.potassium.PotassiumSwordItem;
import space.anatomyuniverse.musavacca.tint.MusavaccaTints;

//? if <1.21.2 {
/*import java.util.EnumMap;
import java.util.List;
*///?}
public final class ModItems {
    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(MusaCore.MOD_ID);
    //? if <1.21.2 {
    /*public static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS =
            DeferredRegister.create(Registries.ARMOR_MATERIAL, MusaCore.MOD_ID);
    *///?}

    public static final DeferredItem<MusavaccaBoatItem>
            MUSAVACCA_BOAT =
            ITEMS.registerItem(
                    "musavacca_boat",
                    properties -> new MusavaccaBoatItem(
                            ModEntities.MUSAVACCA_BOAT.get(),
                            properties.stacksTo(1)
                    )
            );

    public static final TagKey<Item> REPAIRS_POTASSIUM_ARMOR =
            TagKey.create(
                    Registries.ITEM,
                    //? if >=1.21.11 {
                    /*Identifier.fromNamespaceAndPath(MusaCore.MOD_ID, "repairs_potassium_armor")
                    *///?} else {
                    ResourceLocation.fromNamespaceAndPath(MusaCore.MOD_ID, "repairs_potassium_armor")
                    //?}
            );

    public static final TagKey<Item> REPAIRS_POTASSIUM_TOOLS =
            TagKey.create(
                    Registries.ITEM,
                    //? if >=1.21.11 {
                    /*Identifier.fromNamespaceAndPath(MusaCore.MOD_ID, "repairs_potassium_tools")
                    *///?} else {
                    ResourceLocation.fromNamespaceAndPath(MusaCore.MOD_ID, "repairs_potassium_tools")
                    //?}
            );

    public static final

            //? if >=1.21.2 {
            ToolMaterial
            //?} else {
            /*Tier
            *///?}

            POTASSIUM_TOOL_MATERIAL =

            //? if >=1.21.2 {
            new ToolMaterial(
            //?} else {
            /*new SimpleTier(
            *///?}
                    BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
                    1850,
                    8.5F,
                    3.5F,
                    12,
                    //? if >=1.21.2 {
                    REPAIRS_POTASSIUM_TOOLS
                    //?} else {
                    /*() -> Ingredient.of(REPAIRS_POTASSIUM_TOOLS)
                    *///?}
            );

    private static final FoodProperties POTASSIUM_FOOD =
            new FoodProperties.Builder()
                    .nutrition(8)
                    .saturationModifier(0.8F)
                    .build();

    //? if >=1.21.4 {
    private static final Consumable POTASSIUM_CONSUMABLE =
            Consumables.defaultFood()
                    .consumeSeconds(1.6F)
                    .build();

    public static final

            ResourceKey<EquipmentAsset>

            POTASSIUM_EQUIPMENT_ASSET =
            equipmentAsset("potassium");

    public static final ArmorMaterial POTASSIUM_ARMOR_MATERIAL =
            potassiumArmorMaterial(
                    POTASSIUM_EQUIPMENT_ASSET
    //?} else if >=1.21.2 <1.21.4 {
    /*private static final Consumable POTASSIUM_CONSUMABLE =
            Consumables.defaultFood()
                    .consumeSeconds(1.6F)
                    .build();

    public static final

            ResourceLocation

            POTASSIUM_EQUIPMENT_ASSET =
            equipmentAsset("potassium");

    public static final ArmorMaterial POTASSIUM_ARMOR_MATERIAL =
            potassiumArmorMaterial(
                    POTASSIUM_EQUIPMENT_ASSET
    *///?} else {
    /*public static final DeferredHolder<ArmorMaterial, ArmorMaterial>
            POTASSIUM_ARMOR_MATERIAL =
            ARMOR_MATERIALS.register(
                    "potassium",
                    () -> potassiumArmorMaterial1211("potassium")
    *///?}
            );

    private static DeferredItem<BlockItem> MusavaccaDoorItems(
            String name,
            boolean lit,
            boolean litPortal,
            boolean portal
    ) {
        return ITEMS.registerItem(
                name,
                props -> new DoubleHighBlockItem(
                        ModBlocks.MUSAVACCA_DOOR.get(),
                        props.component(
                                DataComponents.BLOCK_STATE,
                                BlockItemStateProperties.EMPTY
                                        .with(
                                                MusavaccaPortalDoorBlock.LIT,
                                                lit
                                        )
                                        .with(
                                                MusavaccaPortalDoorBlock.LIT_PORTAL,
                                                litPortal
                                        )
                                        .with(
                                                MusavaccaPortalDoorBlock.PORTAL,
                                                portal
                                        )
                        )
                )
        );
    }

    public static final DeferredItem<BlockItem> MUSAVACCA_DOOR =
            MusavaccaDoorItems(
                    "musavacca_door",
                    false,
                    false,
                    false
            );

    public static final DeferredItem<BlockItem> MUSAVACCA_CHARGED_DOOR =
            MusavaccaDoorItems(
                    "musavacca_charged_door",
                    true,
                    false,
                    false
            );

    public static final DeferredItem<BlockItem> MUSAVACCA_IMBUED_DOOR =
            MusavaccaDoorItems(
                    "musavacca_imbued_door",
                    false,
                    true,
                    false
            );

    public static final DeferredItem<BlockItem> MUSAVACCA_PUP =
            ITEMS.registerItem(
                    "musavacca_pup",
                    props -> new BlockItem(
                            ModBlocks.MUSAVACCA_SPROUT.get(),
                            props.component(
                                    DataComponents.ITEM_NAME,
                                    Component.translatable(
                                            "item.musavacca.musavacca_pup"
                                    )
                            )
                    )
            );

    public static final DeferredItem<Item> BANANA_PEARL =
            ITEMS.registerItem(
                    "banana_pearl",
                    props -> new BananaPearlItem(
                            bananaPearlProperties(
                                    props.rarity(Rarity.RARE)
                            )
                    )
            );

    public static final DeferredItem<Item>
            BANAZO_GUSMA_LUMPA_GOOP =
            ITEMS.registerItem(
                    "banazo_gusma_lumpa_goop",
                    props -> new Item(
                            props.rarity(Rarity.EPIC)
                    )
            );

    public static final DeferredItem<Item>
            INACTIVE_VOCO_CALLER =
            ITEMS.registerItem(
                    "inactive_voco_caller",
                    props -> new Item(
                            props.rarity(Rarity.EPIC)
                                    .stacksTo(1)
                    )
            );

    public static final DeferredItem<Item> BANANA_PELLIS =
            ITEMS.registerItem(
                    "banana_pellis",
                    props -> new Item(
                            props.rarity(Rarity.COMMON)
                    )
            );

    public static final DeferredItem<Item>
            FRACTURED_POTASSIUM_UPGRADE_SMITHING_TEMPLATE =
            ITEMS.registerItem(
                    "fractured_potassium_upgrade_smithing_template",
                    props -> new Item(
                            props.rarity(Rarity.RARE)
                    )
            );

    public static final DeferredItem<SmithingTemplateItem>
            POTASSIUM_UPGRADE_SMITHING_TEMPLATE =
            ITEMS.registerItem(
                    ModSmithingTemplates.POTASSIUM_UPGRADE
                            .registryPath(),
                    props -> ModSmithingTemplates
                            .POTASSIUM_UPGRADE
                            .create(props)
            );

    public static final DeferredItem<Item> MUSAVACCA_EXUDATE =
            ITEMS.registerItem(
                    "musavacca_exudate",
                    props -> new Item(
                            exudateProperties(
                                    props.stacksTo(16)
                                            .craftRemainder(Items.GLASS_BOTTLE)
                                            .rarity(Rarity.UNCOMMON)
                            )
                    )
            );

    public static final DeferredItem<Item> BANANA_PHONE =
            ITEMS.registerItem(
                    "banana_phone",
                    props -> new OpenVocoCallerItem(
                            props.rarity(Rarity.RARE)
                                    .stacksTo(1)
                                    .component(
                                            DataComponents.BUNDLE_CONTENTS,
                                            BundleContents.EMPTY
                                    )
                    )
            );

    public static final DeferredItem<Item> BIG_BANANA_PEARL =
            ITEMS.registerItem(
                    "big_banana_pearl",
                    props -> new Item(
                            props.rarity(Rarity.RARE)
                    )
            );

    public static final DeferredItem<Item> SIM_CARD =
            ITEMS.registerItem(
                    "sim_card",
                    props -> new SimCardItem(
                            props.rarity(Rarity.RARE)
                                    .fireResistant()
                                    .stacksTo(1)
                    )
            );

    public static final DeferredItem<Item> SMALL_BANANA_PEARL =
            ITEMS.registerItem(
                    "small_banana_pearl",
                    props -> new SmallBananaPearlItem(
                            props.rarity(Rarity.RARE)
                    )
            );

    public static final DeferredItem<SpawnEggItem>
            BANANA_COW_SPAWN_EGG =
            ITEMS.registerItem(
                    "banana_cow_spawn_egg",
                    props ->

                    //? if >=1.21.9 {
                    /*new SpawnEggItem(
                            props.spawnEgg(
                                    ModEntities.BANANA_COW.get()
                            )
                    )
                    *///?} else if >=1.21.4 <1.21.9 {
                            new SpawnEggItem(
                                    ModEntities.BANANA_COW.get(),
                                    props
                            )
                    //?} else {
                            /*new SpawnEggItem(
                                    ModEntities.BANANA_COW.get(),
                                    0xE4C64A,
                                    0x7A4A1F,
                                    props
                            )
                    *///?}

            );

    public static final DeferredItem<SpawnEggItem>
            BASUKE_SPAWN_EGG =
            ITEMS.registerItem(
                    "basuke_spawn_egg",
                    props ->

                    //? if >=1.21.9 {
                    /*new SpawnEggItem(
                            props.spawnEgg(
                                    ModEntities.BASUKE.get()
                            )
                    )
                    *///?} else if >=1.21.4 <1.21.9 {
                            new SpawnEggItem(
                                    ModEntities.BASUKE.get(),
                                    props
                            )
                    //?} else {
                            /*new SpawnEggItem(
                                    ModEntities.BASUKE.get(),
                                    0xE6DCC8,
                                    0x4F3F36,
                                    props
                            )
                    *///?}

            );

    public static final DeferredItem<Item> VACACA =
            ITEMS.registerItem(
                    "vacaca",
                    props -> new Item(
                            props.food(Foods.APPLE)
                    )
            );

    public static final DeferredItem<Item> FLINT_AND_PEARL =
            ITEMS.registerItem(
                    "flint_and_pearl",
                    props -> new FlintAndPearlItem(
                            props.durability(64)
                                    .component(
                                            ModDataComponents
                                                    .HEX_COLOR
                                                    .get(),
                                            MusavaccaTints.DEFAULT_TINT
                                    )
                    )
            );

    public static final DeferredItem<BananaMilkBucketItem>
            BANANA_MILK_BUCKET =
            ITEMS.registerItem(
                    "banana_milk_bucket",
                    props ->
                            new BananaMilkBucketItem(
                                    props.stacksTo(1)
                                            .craftRemainder(
                                                    Items.BUCKET
                                            //? if >=1.21.2 {
                                            )

                                            .usingConvertsTo(
                                                    Items.BUCKET
                                            )
                                            .component(
                                                    DataComponents.CONSUMABLE,
                                                    Consumables.MILK_BUCKET
                                            //?}
                                            )

                            )
            );

    public static final DeferredItem<Item> POTASSIUM_INGOT =
            ITEMS.registerItem(
                    "potassium_ingot",
                    Item::new
            );

    public static final DeferredItem<Item> POTASSIUM_SWORD =
            ITEMS.registerItem(
                    "potassium_sword",
                    ModItems::createPotassiumSword
            );

    public static final DeferredItem<Item> POTASSIUM_SHOVEL =
            ITEMS.registerItem(
                    "potassium_shovel",
                    ModItems::createPotassiumShovel
            );

    public static final DeferredItem<Item> POTASSIUM_PICKAXE =
            ITEMS.registerItem(
                    "potassium_pickaxe",
                    ModItems::createPotassiumPickaxe
            );

    public static final DeferredItem<Item> POTASSIUM_AXE =
            ITEMS.registerItem(
                    "potassium_axe",
                    ModItems::createPotassiumAxe
            );

    public static final DeferredItem<Item> POTASSIUM_HOE =
            ITEMS.registerItem(
                    "potassium_hoe",
                    ModItems::createPotassiumHoe
            );

    public static final DeferredItem<Item> POTASSIUM_HELMET =
            ITEMS.registerItem(
                    "potassium_helmet",
                    props -> createPotassiumHelmet(
                            props,
                            POTASSIUM_ARMOR_MATERIAL
                    )
            );

    public static final DeferredItem<Item> POTASSIUM_CHESTPLATE =
            ITEMS.registerItem(
                    "potassium_chestplate",
                    props -> createPotassiumChestplate(
                            props,
                            POTASSIUM_ARMOR_MATERIAL
                    )
            );

    public static final DeferredItem<Item> POTASSIUM_LEGGINGS =
            ITEMS.registerItem(
                    "potassium_leggings",
                    props -> createPotassiumLeggings(
                            props,
                            POTASSIUM_ARMOR_MATERIAL
                    )
            );

    public static final DeferredItem<Item> POTASSIUM_BOOTS =
            ITEMS.registerItem(
                    "potassium_boots",
                    props -> createPotassiumBoots(
                            props,
                            POTASSIUM_ARMOR_MATERIAL
                    )
            );

    private static Item.Properties bananaPearlProperties(Item.Properties props) {
        FoodProperties food = new FoodProperties.Builder()
                .nutrition(1)
                .saturationModifier(0.3F)
                .alwaysEdible()

                //? if <1.21.2 {
                /*.fast()
                *///?}
                .build();

        //? if >=1.21.2 {
        return props.food(
                food,
                Consumables.defaultFood()
                        .consumeSeconds(0.8F)
                        .build()
        );
        //?} else {
        /*return props.food(food);
        *///?}

    }

    private static Item.Properties exudateProperties(Item.Properties props) {

        //? if >=1.21.2 {
        return props.usingConvertsTo(Items.GLASS_BOTTLE)
                .food(Foods.HONEY_BOTTLE, Consumables.HONEY_BOTTLE);
        //?} else {
        /*return props.food(Foods.HONEY_BOTTLE);
        *///?}

    }

    private static Item.Properties potassiumFoodProperties(Item.Properties props) {

        //? if >=1.21.2 {
        return props.food(POTASSIUM_FOOD, POTASSIUM_CONSUMABLE);
        //?} else {
        /*return props.food(POTASSIUM_FOOD);
        *///?}

    }

    private static Item createPotassiumSword(Item.Properties props) {
        return new PotassiumSwordItem(
                POTASSIUM_TOOL_MATERIAL,
                3.0F,
                -2.4F,
                potassiumFoodProperties(props)
        );
    }

    private static Item createPotassiumPickaxe(Item.Properties props) {
        return new PotassiumPickaxeItem(
                POTASSIUM_TOOL_MATERIAL,
                1.0F,
                -2.8F,
                potassiumFoodProperties(props)
        );
    }

    private static Item createPotassiumShovel(Item.Properties props) {
        return new PotassiumShovelItem(
                POTASSIUM_TOOL_MATERIAL,
                1.5F,
                -3.0F,
                potassiumFoodProperties(props)
        );
    }

    private static Item createPotassiumAxe(Item.Properties props) {
        return new PotassiumAxeItem(
                POTASSIUM_TOOL_MATERIAL,
                5.0F,
                -3.0F,
                potassiumFoodProperties(props)
        );
    }

    private static Item createPotassiumHoe(Item.Properties props) {
        return new PotassiumHoeItem(
                POTASSIUM_TOOL_MATERIAL,
                -3.5F,
                0.0F,
                potassiumFoodProperties(props)
        );
    }

    private static Item createPotassiumHelmet(
            Item.Properties props,

            //? if >=1.21.2 {
            ArmorMaterial armorMaterial
            //?} else {
            /*Holder<ArmorMaterial> armorMaterial
            *///?}

    ) {

        return new PotassiumArmorItem(
                //? if >=1.21.2 {
                customHeadHelmetProperties(props, armorMaterial)
                //?} else {
                /*armorMaterial,
                ArmorItem.Type.HELMET,
                potassiumFoodProperties(props)
                *///?}
        );

    }

    private static Item createPotassiumChestplate(
            Item.Properties props,

            //? if >=1.21.2 {
            ArmorMaterial armorMaterial
            //?} else {
            /*Holder<ArmorMaterial> armorMaterial
            *///?}

    ) {

        return new PotassiumArmorItem(
                //? if >=1.21.2 {
                potassiumFoodProperties(
                        humanoidArmorProperties(props, armorMaterial, ArmorType.CHESTPLATE)
                )
                //?} else {
                /*armorMaterial,
                ArmorItem.Type.CHESTPLATE,
                potassiumFoodProperties(props)
                *///?}
        );

    }

    private static Item createPotassiumLeggings(
            Item.Properties props,

            //? if >=1.21.2 {
            ArmorMaterial armorMaterial
            //?} else {
            /*Holder<ArmorMaterial> armorMaterial
            *///?}

    ) {

        return new PotassiumArmorItem(
                //? if >=1.21.2 {
                potassiumFoodProperties(
                        humanoidArmorProperties(props, armorMaterial, ArmorType.LEGGINGS)
                )
                //?} else {
                /*armorMaterial,
                ArmorItem.Type.LEGGINGS,
                potassiumFoodProperties(props)
                *///?}
        );

    }

    private static Item createPotassiumBoots(
            Item.Properties props,

            //? if >=1.21.2 {
            ArmorMaterial armorMaterial
            //?} else {
            /*Holder<ArmorMaterial> armorMaterial
            *///?}

    ) {

        return new PotassiumArmorItem(
                //? if >=1.21.2 {
                potassiumFoodProperties(
                        humanoidArmorProperties(props, armorMaterial, ArmorType.BOOTS)
                )
                //?} else {
                /*armorMaterial,
                ArmorItem.Type.BOOTS,
                potassiumFoodProperties(props)
                *///?}
        );

    }

    //? if >=1.21.11 {
    /*private static Item.Properties
    customHeadHelmetProperties(
            Item.Properties props,
            ArmorMaterial armorMaterial
    ) {

        return potassiumFoodProperties(
                humanoidArmorProperties(
                        props,
                        armorMaterial,
                        ArmorType.HELMET
                )
        );
    }

    private static Item.Properties humanoidArmorProperties(
            Item.Properties props,
            ArmorMaterial armorMaterial,
            ArmorType armorType
    ) {

        return props.humanoidArmor(armorMaterial, armorType);

    }

    private static ArmorMaterial potassiumArmorMaterial(

            ResourceKey<EquipmentAsset> equipmentAsset

    ) {
        return new ArmorMaterial(
                35,
                Util.make(
                        new java.util.EnumMap<>(
                                ArmorType.class
                        ),
                        map -> {
                            map.put(ArmorType.BOOTS, 3);
                            map.put(ArmorType.LEGGINGS, 6);
                            map.put(ArmorType.CHESTPLATE, 8);
                            map.put(ArmorType.HELMET, 3);
                            map.put(ArmorType.BODY, 11);
                        }
                ),
                12,
                SoundEvents.ARMOR_EQUIP_DIAMOND,
                2.5F,
                0.0F,
                REPAIRS_POTASSIUM_ARMOR,
                equipmentAsset
        );
    }

    private static

            ResourceKey<EquipmentAsset>

    equipmentAsset(
            String path
    ) {

        return ResourceKey.create(
                EquipmentAssets.ROOT_ID,
                Identifier.fromNamespaceAndPath(MusaCore.MOD_ID, path)
        );

    }
    *///?} else if >=1.21.5 <1.21.11 {
    private static Item.Properties
    customHeadHelmetProperties(
            Item.Properties props,
            ArmorMaterial armorMaterial
    ) {

        return potassiumFoodProperties(
                humanoidArmorProperties(
                        props,
                        armorMaterial,
                        ArmorType.HELMET
                )
        );
    }

    private static Item.Properties humanoidArmorProperties(
            Item.Properties props,
            ArmorMaterial armorMaterial,
            ArmorType armorType
    ) {

        return props.humanoidArmor(armorMaterial, armorType);

    }

    private static ArmorMaterial potassiumArmorMaterial(

            ResourceKey<EquipmentAsset> equipmentAsset

    ) {
        return new ArmorMaterial(
                35,
                Util.make(
                        new java.util.EnumMap<>(
                                ArmorType.class
                        ),
                        map -> {
                            map.put(ArmorType.BOOTS, 3);
                            map.put(ArmorType.LEGGINGS, 6);
                            map.put(ArmorType.CHESTPLATE, 8);
                            map.put(ArmorType.HELMET, 3);
                            map.put(ArmorType.BODY, 11);
                        }
                ),
                12,
                SoundEvents.ARMOR_EQUIP_DIAMOND,
                2.5F,
                0.0F,
                REPAIRS_POTASSIUM_ARMOR,
                equipmentAsset
        );
    }

    private static

            ResourceKey<EquipmentAsset>

    equipmentAsset(
            String path
    ) {

        return ResourceKey.create(
                EquipmentAssets.ROOT_ID,
                ResourceLocation.fromNamespaceAndPath(MusaCore.MOD_ID, path)
        );

    }
    //?} else if >=1.21.4 <1.21.5 {
    /*private static Item.Properties
    customHeadHelmetProperties(
            Item.Properties props,
            ArmorMaterial armorMaterial
    ) {

        return potassiumFoodProperties(
                humanoidArmorProperties(
                        props,
                        armorMaterial,
                        ArmorType.HELMET
                )
        );
    }

    private static Item.Properties humanoidArmorProperties(
            Item.Properties props,
            ArmorMaterial armorMaterial,
            ArmorType armorType
    ) {

        return armorMaterial.humanoidProperties(props, armorType);

    }

    private static ArmorMaterial potassiumArmorMaterial(

            ResourceKey<EquipmentAsset> equipmentAsset

    ) {
        return new ArmorMaterial(
                35,
                Util.make(
                        new java.util.EnumMap<>(
                                ArmorType.class
                        ),
                        map -> {
                            map.put(ArmorType.BOOTS, 3);
                            map.put(ArmorType.LEGGINGS, 6);
                            map.put(ArmorType.CHESTPLATE, 8);
                            map.put(ArmorType.HELMET, 3);
                            map.put(ArmorType.BODY, 11);
                        }
                ),
                12,
                SoundEvents.ARMOR_EQUIP_DIAMOND,
                2.5F,
                0.0F,
                REPAIRS_POTASSIUM_ARMOR,
                equipmentAsset
        );
    }

    private static

            ResourceKey<EquipmentAsset>

    equipmentAsset(
            String path
    ) {

        return ResourceKey.create(
                EquipmentAssets.ROOT_ID,
                ResourceLocation.fromNamespaceAndPath(MusaCore.MOD_ID, path)
        );

    }
    *///?} else if >=1.21.2 <1.21.4 {
    /*private static Item.Properties
    customHeadHelmetProperties(
            Item.Properties props,
            ArmorMaterial armorMaterial
    ) {

        return potassiumFoodProperties(
                humanoidArmorProperties(
                        props,
                        armorMaterial,
                        ArmorType.HELMET
                )
        );
    }

    private static Item.Properties humanoidArmorProperties(
            Item.Properties props,
            ArmorMaterial armorMaterial,
            ArmorType armorType
    ) {

        return armorMaterial.humanoidProperties(props, armorType);

    }

    private static ArmorMaterial potassiumArmorMaterial(

            ResourceLocation equipmentAsset

    ) {
        return new ArmorMaterial(
                35,
                Util.make(
                        new java.util.EnumMap<>(
                                ArmorType.class
                        ),
                        map -> {
                            map.put(ArmorType.BOOTS, 3);
                            map.put(ArmorType.LEGGINGS, 6);
                            map.put(ArmorType.CHESTPLATE, 8);
                            map.put(ArmorType.HELMET, 3);
                            map.put(ArmorType.BODY, 11);
                        }
                ),
                12,
                SoundEvents.ARMOR_EQUIP_DIAMOND,
                2.5F,
                0.0F,
                REPAIRS_POTASSIUM_ARMOR,
                equipmentAsset
        );
    }

    private static

            ResourceLocation

    equipmentAsset(
            String path
    ) {

        return ResourceLocation.fromNamespaceAndPath(MusaCore.MOD_ID, path);

    }
    *///?} else {
    /*private static ArmorMaterial potassiumArmorMaterial1211(String texturePath) {
        return new ArmorMaterial(
                Util.make(
                        new EnumMap<>(ArmorItem.Type.class),
                        map -> {
                            map.put(ArmorItem.Type.BOOTS, 3);
                            map.put(ArmorItem.Type.LEGGINGS, 6);
                            map.put(ArmorItem.Type.CHESTPLATE, 8);
                            map.put(ArmorItem.Type.HELMET, 3);
                            map.put(ArmorItem.Type.BODY, 11);
                        }
                ),
                12,
                SoundEvents.ARMOR_EQUIP_DIAMOND,
                () -> Ingredient.of(REPAIRS_POTASSIUM_ARMOR),
                List.of(
                        new ArmorMaterial.Layer(
                                ResourceLocation.fromNamespaceAndPath(MusaCore.MOD_ID, texturePath)
                        )
                ),
                2.5F,
                0.0F
        );
    }
    *///?}

    public static void register(IEventBus bus) {

        //? if <1.21.2 {
        /*ARMOR_MATERIALS.register(bus);
        *///?}
        ITEMS.register(bus);
    }

    private ModItems() {
    }
}
