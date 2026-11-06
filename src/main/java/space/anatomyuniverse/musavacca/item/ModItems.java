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
//? if >=1.21.2 {
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
//?}
//? if >=1.21.4 {
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;
//?} else if <1.21.2 {
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
import space.anatomyuniverse.musavacca.block.custom.VocoTableBlock;
import java.util.List;
import space.anatomyuniverse.musavacca.entity.ModEntities;
import space.anatomyuniverse.musavacca.item.custom.*;
import space.anatomyuniverse.musavacca.item.custom.potassium.PotassiumArmorItem;
import space.anatomyuniverse.musavacca.item.custom.potassium.PotassiumAxeItem;
import space.anatomyuniverse.musavacca.item.custom.potassium.PotassiumHoeItem;
import space.anatomyuniverse.musavacca.item.custom.potassium.PotassiumPickaxeItem;
import space.anatomyuniverse.musavacca.item.custom.potassium.PotassiumShovelItem;
import space.anatomyuniverse.musavacca.item.custom.potassium.PotassiumSwordItem;
import space.anatomyuniverse.musavacca.item.custom.BananaMilkBucketItem;
//? if <1.21.2 {
/*import java.util.EnumMap;
*///?}

public final class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MusaCore.MOD_ID);
    //? if <1.21.2 {
    /*public static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS = DeferredRegister.create(Registries.ARMOR_MATERIAL, MusaCore.MOD_ID);
    *///?}
    public static final DeferredItem<MusavaccaBoatItem>
            MUSAVACCA_BOAT =
            ITEMS.registerItem(
                    "musavacca_boat",
                    properties -> new MusavaccaBoatItem(ModEntities.MUSAVACCA_BOAT.get(), properties.stacksTo(1))
            );
    public static final TagKey<Item> REPAIRS_POTASSIUM_ARMOR =
            //? if >=1.21.11 {
            /*TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(MusaCore.MOD_ID, "repairs_potassium_armor"));
            *///?} else {
            TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(MusaCore.MOD_ID, "repairs_potassium_armor"));
            //?}
    public static final TagKey<Item> REPAIRS_POTASSIUM_TOOLS =
            //? if >=1.21.11 {
            /*TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(MusaCore.MOD_ID, "repairs_potassium_tools"));
            *///?} else {
            TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(MusaCore.MOD_ID, "repairs_potassium_tools"));
            //?}
    public static final
            //? if >=1.21.2 {
            ToolMaterial
            //?} else {
            /*Tier
            *///?}
            POTASSIUM_TOOL_MATERIAL =
            //? if >=1.21.2 {
            new ToolMaterial(BlockTags.INCORRECT_FOR_NETHERITE_TOOL, 1850, 8.5F, 3.5F, 12, REPAIRS_POTASSIUM_TOOLS);
            //?} else {
            /*new SimpleTier(
                    BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
                    1850,
                    8.5F,
                    3.5F,
                    12,
                    () -> Ingredient.of(REPAIRS_POTASSIUM_TOOLS)
            );
            *///?}
    private static final FoodProperties POTASSIUM_FOOD = new FoodProperties.Builder().nutrition(8).saturationModifier(0.8F).build();
    //? if >=1.21.2 {
    private static final Consumable POTASSIUM_CONSUMABLE = Consumables.defaultFood().consumeSeconds(1.6F).build();
    //? if >=1.21.4 {
    public static final ResourceKey<EquipmentAsset> POTASSIUM_EQUIPMENT_ASSET = equipmentAsset("potassium");
    //?} else {
    /*public static final ResourceLocation POTASSIUM_EQUIPMENT_ASSET = equipmentAsset("potassium");
    *///?}
    //?}
    //? if >=1.21.2 {
    public static final ArmorMaterial POTASSIUM_ARMOR_MATERIAL = potassiumArmorMaterial(POTASSIUM_EQUIPMENT_ASSET);
    //?} else {
    /*public static final DeferredHolder<ArmorMaterial, ArmorMaterial>
            POTASSIUM_ARMOR_MATERIAL =
            ARMOR_MATERIALS.register(
                    "potassium",
                    () -> potassiumArmorMaterial1211("potassium")
            );
    *///?}
    private static DeferredItem<BlockItem> MusavaccaDoorItems(String name, boolean lit, boolean litPortal, boolean portal) {
        return ITEMS.registerItem(
                name,
                props -> new DoubleHighBlockItem(
                        ModBlocks.MUSAVACCA_DOOR.get(),
                        props.component(
                                DataComponents.BLOCK_STATE,
                                BlockItemStateProperties.EMPTY
                                        .with(MusavaccaPortalDoorBlock.LIT, lit)
                                        .with(MusavaccaPortalDoorBlock.LIT_PORTAL, litPortal)
                                        .with(MusavaccaPortalDoorBlock.PORTAL, portal)
                        )
                )
                //? if <1.21.2 {
                /*{ @Override public String getDescriptionId() { return getOrCreateDescriptionId(); } }
                *///?}
        );
    }

    public static final DeferredItem<BlockItem> MUSAVACCA_DOOR = MusavaccaDoorItems("musavacca_door", false, false, false);
    public static final DeferredItem<BlockItem> MUSAVACCA_CHARGED_DOOR = MusavaccaDoorItems("musavacca_charged_door", true, false, false);
    public static final DeferredItem<BlockItem> MUSAVACCA_IMBUED_DOOR = MusavaccaDoorItems("musavacca_imbued_door", false, true, false);
    private static DeferredItem<BlockItem> VocoTableItems(String name, int corners) {
        return ITEMS.registerItem(name, props -> new BlockStateItem(ModBlocks.VOCO_TABLE.get(), props,
                BlockItemStateProperties.EMPTY
                        .with(VocoTableBlock.LIT_NORTH_EAST, (corners & 1) != 0)
                        .with(VocoTableBlock.LIT_NORTH_WEST, (corners & 2) != 0)
                        .with(VocoTableBlock.LIT_SOUTH_EAST, (corners & 4) != 0)
                        .with(VocoTableBlock.LIT_SOUTH_WEST, (corners & 8) != 0)));
    }

    public static final DeferredItem<BlockItem> VOCO_TABLE = VocoTableItems("voco_table", 0);
    public static final DeferredItem<BlockItem> VOCO_CHARGED_TABLE_NORTH_EAST = VocoTableItems("voco_charged_table_north_east", 1);
    public static final DeferredItem<BlockItem> VOCO_CHARGED_TABLE_NORTH_WEST = VocoTableItems("voco_charged_table_north_west", 2);
    public static final DeferredItem<BlockItem> VOCO_CHARGED_TABLE_NORTH = VocoTableItems("voco_charged_table_north", 3);
    public static final DeferredItem<BlockItem> VOCO_CHARGED_TABLE_SOUTH_EAST = VocoTableItems("voco_charged_table_south_east", 4);
    public static final DeferredItem<BlockItem> VOCO_CHARGED_TABLE_EAST = VocoTableItems("voco_charged_table_east", 5);
    public static final DeferredItem<BlockItem> VOCO_CHARGED_TABLE_NORTH_WEST_AND_SOUTH_EAST = VocoTableItems("voco_charged_table_north_west_and_south_east", 6);
    public static final DeferredItem<BlockItem> VOCO_CHARGED_TABLE_NORTH_AND_SOUTH_EAST = VocoTableItems("voco_charged_table_north_and_south_east", 7);
    public static final DeferredItem<BlockItem> VOCO_CHARGED_TABLE_SOUTH_WEST = VocoTableItems("voco_charged_table_south_west", 8);
    public static final DeferredItem<BlockItem> VOCO_CHARGED_TABLE_NORTH_EAST_AND_SOUTH_WEST = VocoTableItems("voco_charged_table_north_east_and_south_west", 9);
    public static final DeferredItem<BlockItem> VOCO_CHARGED_TABLE_WEST = VocoTableItems("voco_charged_table_west", 10);
    public static final DeferredItem<BlockItem> VOCO_CHARGED_TABLE_NORTH_AND_SOUTH_WEST = VocoTableItems("voco_charged_table_north_and_south_west", 11);
    public static final DeferredItem<BlockItem> VOCO_CHARGED_TABLE_SOUTH = VocoTableItems("voco_charged_table_south", 12);
    public static final DeferredItem<BlockItem> VOCO_CHARGED_TABLE_SOUTH_AND_NORTH_EAST = VocoTableItems("voco_charged_table_south_and_north_east", 13);
    public static final DeferredItem<BlockItem> VOCO_CHARGED_TABLE_SOUTH_AND_NORTH_WEST = VocoTableItems("voco_charged_table_south_and_north_west", 14);
    public static final DeferredItem<BlockItem> VOCO_CHARGED_TABLE = VocoTableItems("voco_charged_table", 15);

    public static final List<DeferredItem<BlockItem>> VOCO_TABLE_ITEMS = List.of(
            VOCO_TABLE,
            VOCO_CHARGED_TABLE_NORTH_EAST,
            VOCO_CHARGED_TABLE_NORTH_WEST,
            VOCO_CHARGED_TABLE_NORTH,
            VOCO_CHARGED_TABLE_SOUTH_EAST,
            VOCO_CHARGED_TABLE_EAST,
            VOCO_CHARGED_TABLE_NORTH_WEST_AND_SOUTH_EAST,
            VOCO_CHARGED_TABLE_NORTH_AND_SOUTH_EAST,
            VOCO_CHARGED_TABLE_SOUTH_WEST,
            VOCO_CHARGED_TABLE_NORTH_EAST_AND_SOUTH_WEST,
            VOCO_CHARGED_TABLE_WEST,
            VOCO_CHARGED_TABLE_NORTH_AND_SOUTH_WEST,
            VOCO_CHARGED_TABLE_SOUTH,
            VOCO_CHARGED_TABLE_SOUTH_AND_NORTH_EAST,
            VOCO_CHARGED_TABLE_SOUTH_AND_NORTH_WEST,
            VOCO_CHARGED_TABLE
    );

    public static final DeferredItem<BlockItem> MUSAVACCA_PUP =
            ITEMS.registerItem(
                    "musavacca_pup",
                    props -> new BlockItem(ModBlocks.MUSAVACCA_SPROUT.get(),
                            props.component(DataComponents.ITEM_NAME, Component.translatable("item.musavacca.musavacca_pup")))
            );
    public static final DeferredItem<Item> BANANA_PEARL =
            ITEMS.registerItem(
                    "banana_pearl",
                    props -> new BananaPearlItem(bananaPearlProperties(props.rarity(Rarity.RARE)))
            );
    public static final DeferredItem<Item>
            BANAZO_GUSMA_LUMPA_GOOP =
            ITEMS.registerItem(
                    "banazo_gusma_lumpa_goop",
                    props -> new Item(props.rarity(Rarity.EPIC))
            );
    public static final DeferredItem<Item>
            INACTIVE_VOCO_CALLER =
            ITEMS.registerItem(
                    "inactive_voco_caller",
                    props -> new Item(props.rarity(Rarity.EPIC).stacksTo(1))
            );
    public static final DeferredItem<Item> BANANA_PELLIS = ITEMS.registerItem("banana_pellis", props -> new Item(props.rarity(Rarity.COMMON)));
    public static final DeferredItem<Item>
            FRACTURED_POTASSIUM_UPGRADE_SMITHING_TEMPLATE =
            ITEMS.registerItem(
                    "fractured_potassium_upgrade_smithing_template",
                    props -> new Item(props.rarity(Rarity.RARE))
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
                    props -> new Item(exudateProperties(props.stacksTo(16).craftRemainder(Items.GLASS_BOTTLE).rarity(Rarity.UNCOMMON)))
            );
    public static final DeferredItem<Item> BANANA_PHONE =
            ITEMS.registerItem(
                    "banana_phone",
                    props -> new OpenVocoCallerItem(props.rarity(Rarity.RARE).stacksTo(1).component(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY))
            );
    public static final DeferredItem<Item> BIG_BANANA_PEARL = ITEMS.registerItem("big_banana_pearl", props -> new Item(props.rarity(Rarity.RARE)));
    public static final DeferredItem<Item> SIM_CARD =
            ITEMS.registerItem(
                    "sim_card",
                    props -> new SimCardItem(props.rarity(Rarity.RARE).fireResistant().stacksTo(1))
            );
    public static final DeferredItem<Item> SMALL_BANANA_PEARL =
            ITEMS.registerItem(
                    "small_banana_pearl",
                    props -> new SmallBananaPearlItem(props.rarity(Rarity.RARE))
            );
    public static final DeferredItem<SpawnEggItem>
            BANANA_COW_SPAWN_EGG =
            ITEMS.registerItem(
                    "banana_cow_spawn_egg",
                    props ->
                    //? if >=1.21.9 {
                    /*new SpawnEggItem(props.spawnEgg(ModEntities.BANANA_COW.get()))
                    *///?} else if >=1.21.4 <1.21.9 {
                            new SpawnEggItem(ModEntities.BANANA_COW.get(), props)
                            //?} else {
                            /*new SpawnEggItem(ModEntities.BANANA_COW.get(), 0xE4C64A, 0x7A4A1F, props)
                            *///?}
            );
    public static final DeferredItem<SpawnEggItem>
            BASUKE_SPAWN_EGG =
            ITEMS.registerItem(
                    "basuke_spawn_egg",
                    props ->
                    //? if >=1.21.9 {
                    /*new SpawnEggItem(props.spawnEgg(ModEntities.BASUKE.get()))
                    *///?} else if >=1.21.4 <1.21.9 {
                            new SpawnEggItem(ModEntities.BASUKE.get(), props)
                            //?} else {
                            /*new SpawnEggItem(ModEntities.BASUKE.get(), 0xE6DCC8, 0x4F3F36, props)
                            *///?}
            );
    public static final DeferredItem<Item> VACACA = ITEMS.registerItem("vacaca", props -> new Item(props.food(Foods.APPLE)));
    public static final DeferredItem<BananaMilkBucketItem>
            BANANA_MILK_BUCKET =
            ITEMS.registerItem(
                    "banana_milk_bucket",
                    props ->
                            //? if >=1.21.2 {
                            new BananaMilkBucketItem(
                                    props.stacksTo(1)
                                            .craftRemainder(Items.BUCKET)
                                            .usingConvertsTo(Items.BUCKET)
                                            .component(DataComponents.CONSUMABLE, Consumables.MILK_BUCKET)
                            )
                            //?} else {
                            /*new BananaMilkBucketItem(props.stacksTo(1).craftRemainder(Items.BUCKET))
                            *///?}
            );
    public static final DeferredItem<Item> POTASSIUM_INGOT = ITEMS.registerItem("potassium_ingot", Item::new);
    public static final DeferredItem<Item> POTASSIUM_SWORD = ITEMS.registerItem("potassium_sword", ModItems::createPotassiumSword);
    public static final DeferredItem<Item> POTASSIUM_SHOVEL = ITEMS.registerItem("potassium_shovel", ModItems::createPotassiumShovel);
    public static final DeferredItem<Item> POTASSIUM_PICKAXE = ITEMS.registerItem("potassium_pickaxe", ModItems::createPotassiumPickaxe);
    public static final DeferredItem<Item> POTASSIUM_AXE = ITEMS.registerItem("potassium_axe", ModItems::createPotassiumAxe);
    public static final DeferredItem<Item> POTASSIUM_HOE = ITEMS.registerItem("potassium_hoe", ModItems::createPotassiumHoe);
    public static final DeferredItem<Item> POTASSIUM_HELMET =
            ITEMS.registerItem(
                    "potassium_helmet",
                    props -> createPotassiumHelmet(props, POTASSIUM_ARMOR_MATERIAL)
            );
    public static final DeferredItem<Item> POTASSIUM_CHESTPLATE =
            ITEMS.registerItem(
                    "potassium_chestplate",
                    props -> createPotassiumChestplate(props, POTASSIUM_ARMOR_MATERIAL)
            );
    public static final DeferredItem<Item> POTASSIUM_LEGGINGS =
            ITEMS.registerItem(
                    "potassium_leggings",
                    props -> createPotassiumLeggings(props, POTASSIUM_ARMOR_MATERIAL)
            );
    public static final DeferredItem<Item> POTASSIUM_BOOTS =
            ITEMS.registerItem(
                    "potassium_boots",
                    props -> createPotassiumBoots(props, POTASSIUM_ARMOR_MATERIAL)
            );
    private static Item.Properties bananaPearlProperties(Item.Properties props) {
        //? if >=1.21.2 {
        FoodProperties food = new FoodProperties.Builder().nutrition(1).saturationModifier(0.3F).alwaysEdible().build();
        //?} else {
        /*FoodProperties food = new FoodProperties.Builder().nutrition(1).saturationModifier(0.3F).alwaysEdible().fast().build();
        *///?}
        //? if >=1.21.2 {
        return props.food(food, Consumables.defaultFood().consumeSeconds(0.8F).build());
        //?} else {
        /*return props.food(food);
        *///?}
    }

    private static Item.Properties exudateProperties(Item.Properties props) {
        //? if >=1.21.2 {
        return props.usingConvertsTo(Items.GLASS_BOTTLE).food(Foods.HONEY_BOTTLE, Consumables.HONEY_BOTTLE);
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
        return new PotassiumSwordItem(POTASSIUM_TOOL_MATERIAL, 3.0F, -2.4F, potassiumFoodProperties(props));
    }

    private static Item createPotassiumPickaxe(Item.Properties props) {
        return new PotassiumPickaxeItem(POTASSIUM_TOOL_MATERIAL, 1.0F, -2.8F, potassiumFoodProperties(props));
    }

    private static Item createPotassiumShovel(Item.Properties props) {
        return new PotassiumShovelItem(POTASSIUM_TOOL_MATERIAL, 1.5F, -3.0F, potassiumFoodProperties(props));
    }

    private static Item createPotassiumAxe(Item.Properties props) {
        return new PotassiumAxeItem(POTASSIUM_TOOL_MATERIAL, 5.0F, -3.0F, potassiumFoodProperties(props));
    }

    private static Item createPotassiumHoe(Item.Properties props) {
        return new PotassiumHoeItem(POTASSIUM_TOOL_MATERIAL, -3.5F, 0.0F, potassiumFoodProperties(props));
    }

    //? if >=1.21.2 {
    private static Item createPotassiumHelmet(Item.Properties props, ArmorMaterial armorMaterial) {
    //?} else {
    /*private static Item createPotassiumHelmet(Item.Properties props, Holder<ArmorMaterial> armorMaterial) {
    *///?}
        //? if >=1.21.2 {
        return new PotassiumArmorItem(customHeadHelmetProperties(props, armorMaterial));
        //?} else {
        /*return new PotassiumArmorItem(armorMaterial, ArmorItem.Type.HELMET, potassiumFoodProperties(props));
        *///?}
    }

    //? if >=1.21.2 {
    private static Item createPotassiumChestplate(Item.Properties props, ArmorMaterial armorMaterial) {
    //?} else {
    /*private static Item createPotassiumChestplate(Item.Properties props, Holder<ArmorMaterial> armorMaterial) {
    *///?}
        //? if >=1.21.2 {
        return new PotassiumArmorItem(potassiumFoodProperties(humanoidArmorProperties(props, armorMaterial, ArmorType.CHESTPLATE)));
        //?} else {
        /*return new PotassiumArmorItem(armorMaterial, ArmorItem.Type.CHESTPLATE, potassiumFoodProperties(props));
        *///?}
    }

    //? if >=1.21.2 {
    private static Item createPotassiumLeggings(Item.Properties props, ArmorMaterial armorMaterial) {
    //?} else {
    /*private static Item createPotassiumLeggings(Item.Properties props, Holder<ArmorMaterial> armorMaterial) {
    *///?}
        //? if >=1.21.2 {
        return new PotassiumArmorItem(potassiumFoodProperties(humanoidArmorProperties(props, armorMaterial, ArmorType.LEGGINGS)));
        //?} else {
        /*return new PotassiumArmorItem(armorMaterial, ArmorItem.Type.LEGGINGS, potassiumFoodProperties(props));
        *///?}
    }

    //? if >=1.21.2 {
    private static Item createPotassiumBoots(Item.Properties props, ArmorMaterial armorMaterial) {
    //?} else {
    /*private static Item createPotassiumBoots(Item.Properties props, Holder<ArmorMaterial> armorMaterial) {
    *///?}
        //? if >=1.21.2 {
        return new PotassiumArmorItem(potassiumFoodProperties(humanoidArmorProperties(props, armorMaterial, ArmorType.BOOTS)));
        //?} else {
        /*return new PotassiumArmorItem(armorMaterial, ArmorItem.Type.BOOTS, potassiumFoodProperties(props));
        *///?}
    }

    //? if >=1.21.2 {
    private static Item.Properties
    customHeadHelmetProperties(Item.Properties props, ArmorMaterial armorMaterial) {
        return potassiumFoodProperties(humanoidArmorProperties(props, armorMaterial, ArmorType.HELMET));
    }

    private static Item.Properties humanoidArmorProperties(Item.Properties props, ArmorMaterial armorMaterial, ArmorType armorType) {
        //? if >=1.21.5 {
        return props.humanoidArmor(armorMaterial, armorType);
        //?} else {
        /*return armorMaterial.humanoidProperties(props, armorType);
        *///?}
    }
    //?}
    //? if <1.21.2 || >=1.21.4 {
    //? if <1.21.2 {
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
                List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(MusaCore.MOD_ID, texturePath))),
                2.5F,
                0.0F
        );
    }
    *///?}
    //? if >=1.21.4 {
    private static ArmorMaterial potassiumArmorMaterial(ResourceKey<EquipmentAsset> equipmentAsset) {
    //?}
    //?}
    //? if >=1.21.2 {
    //? if >=1.21.2 <1.21.4 {
    /*private static ArmorMaterial potassiumArmorMaterial(ResourceLocation equipmentAsset) {
    *///?}
        return new ArmorMaterial(
                35,
                Util.make(
                        new java.util.EnumMap<>(ArmorType.class),
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
            //? if >=1.21.4 {
            ResourceKey<EquipmentAsset>
            //?} else {
            /*ResourceLocation
            *///?}
    equipmentAsset(String path) {
        //? if >=1.21.11 {
        /*return ResourceKey.create(EquipmentAssets.ROOT_ID, Identifier.fromNamespaceAndPath(MusaCore.MOD_ID, path));
        *///?} else if >=1.21.4 <1.21.11 {
        return ResourceKey.create(EquipmentAssets.ROOT_ID, ResourceLocation.fromNamespaceAndPath(MusaCore.MOD_ID, path));
        //?} else {
        /*return ResourceLocation.fromNamespaceAndPath(MusaCore.MOD_ID, path);
        *///?}
    }
    //?}
    public static void register(IEventBus bus) {
        //? if <1.21.2 {
        /*ARMOR_MATERIALS.register(bus);
        *///?}
        ITEMS.register(bus);
    }

    private ModItems() {
    }
}


