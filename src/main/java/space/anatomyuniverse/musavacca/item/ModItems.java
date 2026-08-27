package space.anatomyuniverse.musavacca.item;

import net.minecraft.Util;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;
import net.minecraft.world.item.equipment.Equippable;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import space.anatomyuniverse.musavacca.MusaCore;
import space.anatomyuniverse.musavacca.block.ModBlocks;
import space.anatomyuniverse.musavacca.block.custom.MusavaccaPortalDoorBlock;
import space.anatomyuniverse.musavacca.component.ModDataComponents;
import space.anatomyuniverse.musavacca.entity.ModEntities;
import space.anatomyuniverse.musavacca.item.custom.*;
import space.anatomyuniverse.musavacca.item.custom.potassium.PotassiumAxeItem;
import space.anatomyuniverse.musavacca.item.custom.potassium.PotassiumHoeItem;
import space.anatomyuniverse.musavacca.item.custom.potassium.PotassiumItem;
import space.anatomyuniverse.musavacca.item.custom.potassium.PotassiumShovelItem;

public final class ModItems {
    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(MusaCore.MOD_ID);

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
                    ResourceLocation.fromNamespaceAndPath(
                            MusaCore.MOD_ID,
                            "repairs_potassium_armor"
                    )
            );

    public static final TagKey<Item> REPAIRS_POTASSIUM_TOOLS =
            TagKey.create(
                    Registries.ITEM,
                    ResourceLocation.fromNamespaceAndPath(
                            MusaCore.MOD_ID,
                            "repairs_potassium_tools"
                    )
            );

    public static final ToolMaterial POTASSIUM_TOOL_MATERIAL =
            new ToolMaterial(
                    BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
                    1850,
                    8.5F,
                    3.5F,
                    12,
                    REPAIRS_POTASSIUM_TOOLS
            );

    private static final FoodProperties POTASSIUM_FOOD =
            new FoodProperties.Builder()
                    .nutrition(8)
                    .saturationModifier(0.8F)
                    .build();

    private static final Consumable POTASSIUM_CONSUMABLE =
            Consumables.defaultFood()
                    .consumeSeconds(1.6F)
                    .build();

    public static final ResourceKey<EquipmentAsset>
            POTASSIUM_EQUIPMENT_ASSET =
            equipmentAsset("potassium");

    public static final ResourceKey<EquipmentAsset>
            IMBUED_POTASSIUM_EQUIPMENT_ASSET =
            equipmentAsset("imbued_potassium");

    public static final ArmorMaterial POTASSIUM_ARMOR_MATERIAL =
            potassiumArmorMaterial(
                    POTASSIUM_EQUIPMENT_ASSET
            );

    public static final ArmorMaterial
            IMBUED_POTASSIUM_ARMOR_MATERIAL =
            potassiumArmorMaterial(
                    IMBUED_POTASSIUM_EQUIPMENT_ASSET
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
                    props -> new Item(
                            props.rarity(Rarity.RARE)
                                    .food(
                                            new FoodProperties.Builder()
                                                    .nutrition(1)
                                                    .saturationModifier(0.3F)
                                                    .alwaysEdible()
                                                    .build(),
                                            Consumables.defaultFood()
                                                    .consumeSeconds(0.8F)
                                                    .build()
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

    public static final DeferredItem<SmithingTemplateItem>
            IMBUED_POTASSIUM_UPGRADE_SMITHING_TEMPLATE =
            ITEMS.registerItem(
                    ModSmithingTemplates.IMBUED_POTASSIUM_UPGRADE
                            .registryPath(),
                    props -> ModSmithingTemplates
                            .IMBUED_POTASSIUM_UPGRADE
                            .create(props)
            );

    public static final DeferredItem<Item> MUSAVACCA_EXUDATE =
            ITEMS.registerItem(
                    "musavacca_exudate",
                    props -> new Item(
                            props.stacksTo(16)
                                    .craftRemainder(Items.GLASS_BOTTLE)
                                    .usingConvertsTo(Items.GLASS_BOTTLE)
                                    .food(
                                            Foods.HONEY_BOTTLE,
                                            Consumables.HONEY_BOTTLE
                                    )
                                    .rarity(Rarity.UNCOMMON)
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
                            //? if <1.21.4 {
                            /*new SpawnEggItem(
                                    ModEntities.BANANA_COW.get(),
                                    0xE4C64A,
                                    0x7A4A1F,
                                    props
                            )
                            *///?} else if <1.21.9 {
                            new SpawnEggItem(
                                    ModEntities.BANANA_COW.get(),
                                    props
                            )
                    //?} else {
                    /*new SpawnEggItem(
                            props.spawnEgg(
                                    ModEntities.BANANA_COW.get()
                            )
                    )
                    *///?}
            );

    public static final DeferredItem<SpawnEggItem>
            BASUKE_SPAWN_EGG =
            ITEMS.registerItem(
                    "basuke_spawn_egg",
                    props ->
                            //? if <1.21.4 {
                            /*new SpawnEggItem(
                                    ModEntities.BASUKE.get(),
                                    0xE6DCC8,
                                    0x4F3F36,
                                    props
                            )
                            *///?} else if <1.21.9 {
                            new SpawnEggItem(
                                    ModEntities.BASUKE.get(),
                                    props
                            )
                    //?} else {
                    /*new SpawnEggItem(
                            props.spawnEgg(
                                    ModEntities.BASUKE.get()
                            )
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
                                            FlintAndPearlItem
                                                    .DEFAULT_HEX_COLOR
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
                                            )
                                            .usingConvertsTo(
                                                    Items.BUCKET
                                            )
                                            .component(
                                                    DataComponents.CONSUMABLE,
                                                    Consumables.MILK_BUCKET
                                            )
                            )
            );

    public static final DeferredItem<Item>
            IMBUED_POTASSIUM_SWORD =
            ITEMS.registerItem(
                    "imbued_potassium_sword",
                    props -> new PotassiumItem(
                            props.sword(
                                            POTASSIUM_TOOL_MATERIAL,
                                            3.0F,
                                            -2.4F
                                    )
                                    .food(
                                            POTASSIUM_FOOD,
                                            POTASSIUM_CONSUMABLE
                                    )
                    )
            );

    public static final DeferredItem<Item>
            IMBUED_POTASSIUM_SHOVEL =
            ITEMS.registerItem(
                    "imbued_potassium_shovel",
                    props -> new PotassiumShovelItem(
                            POTASSIUM_TOOL_MATERIAL,
                            1.5F,
                            -3.0F,
                            props.food(
                                    POTASSIUM_FOOD,
                                    POTASSIUM_CONSUMABLE
                            )
                    )
            );

    public static final DeferredItem<Item>
            IMBUED_POTASSIUM_PICKAXE =
            ITEMS.registerItem(
                    "imbued_potassium_pickaxe",
                    props -> new PotassiumItem(
                            props.pickaxe(
                                            POTASSIUM_TOOL_MATERIAL,
                                            1.0F,
                                            -2.8F
                                    )
                                    .food(
                                            POTASSIUM_FOOD,
                                            POTASSIUM_CONSUMABLE
                                    )
                    )
            );

    public static final DeferredItem<Item>
            IMBUED_POTASSIUM_AXE =
            ITEMS.registerItem(
                    "imbued_potassium_axe",
                    props -> new PotassiumAxeItem(
                            POTASSIUM_TOOL_MATERIAL,
                            5.0F,
                            -3.0F,
                            props.food(
                                    POTASSIUM_FOOD,
                                    POTASSIUM_CONSUMABLE
                            )
                    )
            );

    public static final DeferredItem<Item>
            IMBUED_POTASSIUM_HOE =
            ITEMS.registerItem(
                    "imbued_potassium_hoe",
                    props -> new PotassiumHoeItem(
                            POTASSIUM_TOOL_MATERIAL,
                            -3.5F,
                            0.0F,
                            props.food(
                                    POTASSIUM_FOOD,
                                    POTASSIUM_CONSUMABLE
                            )
                    )
            );

    public static final DeferredItem<Item>
            IMBUED_POTASSIUM_HELMET =
            ITEMS.registerItem(
                    "imbued_potassium_helmet",
                    props -> new PotassiumItem(
                            potassiumCustomHeadHelmetProperties(
                                    props,
                                    IMBUED_POTASSIUM_ARMOR_MATERIAL
                            )
                    )
            );

    public static final DeferredItem<Item>
            IMBUED_POTASSIUM_CHESTPLATE =
            ITEMS.registerItem(
                    "imbued_potassium_chestplate",
                    props -> new PotassiumItem(
                            props.humanoidArmor(
                                            IMBUED_POTASSIUM_ARMOR_MATERIAL,
                                            ArmorType.CHESTPLATE
                                    )
                                    .food(
                                            POTASSIUM_FOOD,
                                            POTASSIUM_CONSUMABLE
                                    )
                    )
            );

    public static final DeferredItem<Item>
            IMBUED_POTASSIUM_LEGGINGS =
            ITEMS.registerItem(
                    "imbued_potassium_leggings",
                    props -> new PotassiumItem(
                            props.humanoidArmor(
                                            IMBUED_POTASSIUM_ARMOR_MATERIAL,
                                            ArmorType.LEGGINGS
                                    )
                                    .food(
                                            POTASSIUM_FOOD,
                                            POTASSIUM_CONSUMABLE
                                    )
                    )
            );

    public static final DeferredItem<Item>
            IMBUED_POTASSIUM_BOOTS =
            ITEMS.registerItem(
                    "imbued_potassium_boots",
                    props -> new PotassiumItem(
                            props.humanoidArmor(
                                            IMBUED_POTASSIUM_ARMOR_MATERIAL,
                                            ArmorType.BOOTS
                                    )
                                    .food(
                                            POTASSIUM_FOOD,
                                            POTASSIUM_CONSUMABLE
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
                    props -> new PotassiumItem(
                            props.sword(
                                            POTASSIUM_TOOL_MATERIAL,
                                            3.0F,
                                            -2.4F
                                    )
                                    .food(
                                            POTASSIUM_FOOD,
                                            POTASSIUM_CONSUMABLE
                                    )
                    )
            );

    public static final DeferredItem<Item> POTASSIUM_SHOVEL =
            ITEMS.registerItem(
                    "potassium_shovel",
                    props -> new PotassiumShovelItem(
                            POTASSIUM_TOOL_MATERIAL,
                            1.5F,
                            -3.0F,
                            props.food(
                                    POTASSIUM_FOOD,
                                    POTASSIUM_CONSUMABLE
                            )
                    )
            );

    public static final DeferredItem<Item> POTASSIUM_PICKAXE =
            ITEMS.registerItem(
                    "potassium_pickaxe",
                    props -> new PotassiumItem(
                            props.pickaxe(
                                            POTASSIUM_TOOL_MATERIAL,
                                            1.0F,
                                            -2.8F
                                    )
                                    .food(
                                            POTASSIUM_FOOD,
                                            POTASSIUM_CONSUMABLE
                                    )
                    )
            );

    public static final DeferredItem<Item> POTASSIUM_AXE =
            ITEMS.registerItem(
                    "potassium_axe",
                    props -> new PotassiumAxeItem(
                            POTASSIUM_TOOL_MATERIAL,
                            5.0F,
                            -3.0F,
                            props.food(
                                    POTASSIUM_FOOD,
                                    POTASSIUM_CONSUMABLE
                            )
                    )
            );

    public static final DeferredItem<Item> POTASSIUM_HOE =
            ITEMS.registerItem(
                    "potassium_hoe",
                    props -> new PotassiumHoeItem(
                            POTASSIUM_TOOL_MATERIAL,
                            -3.5F,
                            0.0F,
                            props.food(
                                    POTASSIUM_FOOD,
                                    POTASSIUM_CONSUMABLE
                            )
                    )
            );

    public static final DeferredItem<Item> POTASSIUM_HELMET =
            ITEMS.registerItem(
                    "potassium_helmet",
                    props -> new PotassiumItem(
                            potassiumCustomHeadHelmetProperties(
                                    props,
                                    POTASSIUM_ARMOR_MATERIAL
                            )
                    )
            );

    public static final DeferredItem<Item> POTASSIUM_CHESTPLATE =
            ITEMS.registerItem(
                    "potassium_chestplate",
                    props -> new PotassiumItem(
                            props.humanoidArmor(
                                            POTASSIUM_ARMOR_MATERIAL,
                                            ArmorType.CHESTPLATE
                                    )
                                    .food(
                                            POTASSIUM_FOOD,
                                            POTASSIUM_CONSUMABLE
                                    )
                    )
            );

    public static final DeferredItem<Item> POTASSIUM_LEGGINGS =
            ITEMS.registerItem(
                    "potassium_leggings",
                    props -> new PotassiumItem(
                            props.humanoidArmor(
                                            POTASSIUM_ARMOR_MATERIAL,
                                            ArmorType.LEGGINGS
                                    )
                                    .food(
                                            POTASSIUM_FOOD,
                                            POTASSIUM_CONSUMABLE
                                    )
                    )
            );

    public static final DeferredItem<Item> POTASSIUM_BOOTS =
            ITEMS.registerItem(
                    "potassium_boots",
                    props -> new PotassiumItem(
                            props.humanoidArmor(
                                            POTASSIUM_ARMOR_MATERIAL,
                                            ArmorType.BOOTS
                                    )
                                    .food(
                                            POTASSIUM_FOOD,
                                            POTASSIUM_CONSUMABLE
                                    )
                    )
            );

    private static Item.Properties
    potassiumCustomHeadHelmetProperties(
            Item.Properties props,
            ArmorMaterial armorMaterial
    ) {
        return props
                .humanoidArmor(
                        armorMaterial,
                        ArmorType.HELMET
                )
                .component(
                        DataComponents.EQUIPPABLE,
                        Equippable.builder(EquipmentSlot.HEAD)
                                .setEquipSound(
                                        SoundEvents.ARMOR_EQUIP_DIAMOND
                                )
                                .setDamageOnHurt(true)
                                .build()
                )
                .food(
                        POTASSIUM_FOOD,
                        POTASSIUM_CONSUMABLE
                );
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

    private static ResourceKey<EquipmentAsset> equipmentAsset(
            String path
    ) {
        return ResourceKey.create(
                EquipmentAssets.ROOT_ID,
                ResourceLocation.fromNamespaceAndPath(
                        MusaCore.MOD_ID,
                        path
                )
        );
    }

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }

    private ModItems() {
    }
}
