package space.anatomyuniverse.musavacca.item;

import net.minecraft.network.chat.Component;
//? if >=1.21.11 {
/*import net.minecraft.resources.Identifier;
*///?} else {
import net.minecraft.resources.ResourceLocation;
//?}
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SmithingTemplateItem;
import space.anatomyuniverse.musavacca.MusaCore;

import java.util.List;

public final class ModSmithingTemplates {

    public static final Entry POTASSIUM_UPGRADE = Entry.of(
            "potassium_upgrade_smithing_template",
            Rarity.RARE,
            "Potassium Upgrade",
            "Diamond Equipment",
            "Potassium Ingot",
            "Add diamond armor, weapon, or tool",
            "Add Potassium Ingot",
            List.of(
                    emptyHelmetSlot(),
                    emptyChestplateSlot(),
                    emptyLeggingsSlot(),
                    emptyBootsSlot(),
                    emptySwordSlot(),
                    emptyPickaxeSlot(),
                    emptyAxeSlot(),
                    emptyShovelSlot(),
                    emptyHoeSlot()
            ),
            List.of(
                    emptyIngotSlot()
            )
    );

    public static final List<Entry> ALL = List.of(
            POTASSIUM_UPGRADE
    );

    public record Entry(
            String registryPath,
            Rarity rarity,
            String upgrade,
            String appliesTo,
            String ingredients,
            String baseSlotDescription,
            String additionsSlotDescription,
            //? if >=1.21.11 {
            /*List<Identifier> emptyBaseSlotTextures,
            List<Identifier> emptyAdditionsSlotTextures
            *///?} else {
            List<ResourceLocation> emptyBaseSlotTextures,
            List<ResourceLocation> emptyAdditionsSlotTextures
            //?}
    ) {
        public static Entry of(
                String registryPath,
                Rarity rarity,
                String upgrade,
                String appliesTo,
                String ingredients,
                String baseSlotDescription,
                String additionsSlotDescription,
                //? if >=1.21.11 {
                /*List<Identifier> emptyBaseSlotTextures,
                List<Identifier> emptyAdditionsSlotTextures
                *///?} else {
                List<ResourceLocation> emptyBaseSlotTextures,
                List<ResourceLocation> emptyAdditionsSlotTextures
                //?}
        ) {
            return new Entry(
                    registryPath,
                    rarity,
                    upgrade,
                    appliesTo,
                    ingredients,
                    baseSlotDescription,
                    additionsSlotDescription,
                    List.copyOf(emptyBaseSlotTextures),
                    List.copyOf(emptyAdditionsSlotTextures)
            );
        }

        public SmithingTemplateItem create(Item.Properties props) {

            return new SmithingTemplateItem(
                    Component.translatable(appliesToKey()),
                    Component.translatable(ingredientsKey()),
                    //? if <1.21.2 {
                    /*Component.translatable(upgradeKey()),
                    *///?}
                    Component.translatable(baseSlotDescriptionKey()),
                    Component.translatable(additionsSlotDescriptionKey()),
                    emptyBaseSlotTextures(),
                    //? if >=1.21.2 {
                    emptyAdditionsSlotTextures(),
                    props.rarity(rarity())
                    //?} else {
                    /*emptyAdditionsSlotTextures()
                    *///?}
            );

        }

        public String itemKey() {
            return "item." + MusaCore.MOD_ID + "." + registryPath();
        }

        public String upgradeKey() {
            return itemKey() + ".upgrade";
        }

        public String appliesToKey() {
            return itemKey() + ".applies_to";
        }

        public String ingredientsKey() {
            return itemKey() + ".ingredients";
        }

        public String baseSlotDescriptionKey() {
            return itemKey() + ".base_slot_description";
        }

        public String additionsSlotDescriptionKey() {
            return itemKey() + ".additions_slot_description";
        }
    }

    //? if >=1.21.11 {
    /*private static Identifier emptyHelmetSlot() {
    *///?} else {
    private static ResourceLocation emptyHelmetSlot() {
    //?}

        //? if >=1.21.4 {
        return mc("container/slot/helmet");
        //?} else {
        /*return mc("item/empty_armor_slot_helmet");
        *///?}

    }

    //? if >=1.21.11 {
    /*private static Identifier emptyChestplateSlot() {
    *///?} else {
    private static ResourceLocation emptyChestplateSlot() {
    //?}

        //? if >=1.21.4 {
        return mc("container/slot/chestplate");
        //?} else {
        /*return mc("item/empty_armor_slot_chestplate");
        *///?}

    }

    //? if >=1.21.11 {
    /*private static Identifier emptyLeggingsSlot() {
    *///?} else {
    private static ResourceLocation emptyLeggingsSlot() {
    //?}

        //? if >=1.21.4 {
        return mc("container/slot/leggings");
        //?} else {
        /*return mc("item/empty_armor_slot_leggings");
        *///?}

    }

    //? if >=1.21.11 {
    /*private static Identifier emptyBootsSlot() {
    *///?} else {
    private static ResourceLocation emptyBootsSlot() {
    //?}

        //? if >=1.21.4 {
        return mc("container/slot/boots");
        //?} else {
        /*return mc("item/empty_armor_slot_boots");
        *///?}

    }

    //? if >=1.21.11 {
    /*private static Identifier emptySwordSlot() {
    *///?} else {
    private static ResourceLocation emptySwordSlot() {
    //?}

        //? if >=1.21.4 {
        return mc("container/slot/sword");
        //?} else {
        /*return mc("item/empty_slot_sword");
        *///?}

    }

    //? if >=1.21.11 {
    /*private static Identifier emptyPickaxeSlot() {
    *///?} else {
    private static ResourceLocation emptyPickaxeSlot() {
    //?}

        //? if >=1.21.4 {
        return mc("container/slot/pickaxe");
        //?} else {
        /*return mc("item/empty_slot_pickaxe");
        *///?}

    }

    //? if >=1.21.11 {
    /*private static Identifier emptyAxeSlot() {
    *///?} else {
    private static ResourceLocation emptyAxeSlot() {
    //?}

        //? if >=1.21.4 {
        return mc("container/slot/axe");
        //?} else {
        /*return mc("item/empty_slot_axe");
        *///?}

    }

    //? if >=1.21.11 {
    /*private static Identifier emptyShovelSlot() {
    *///?} else {
    private static ResourceLocation emptyShovelSlot() {
    //?}

        //? if >=1.21.4 {
        return mc("container/slot/shovel");
        //?} else {
        /*return mc("item/empty_slot_shovel");
        *///?}

    }

    //? if >=1.21.11 {
    /*private static Identifier emptyHoeSlot() {
    *///?} else {
    private static ResourceLocation emptyHoeSlot() {
    //?}

        //? if >=1.21.4 {
        return mc("container/slot/hoe");
        //?} else {
        /*return mc("item/empty_slot_hoe");
        *///?}

    }

    //? if >=1.21.11 {
    /*private static Identifier emptyIngotSlot() {
    *///?} else {
    private static ResourceLocation emptyIngotSlot() {
    //?}

        //? if >=1.21.4 {
        return mc("container/slot/ingot");
        //?} else {
        /*return mc("item/empty_slot_ingot");
        *///?}

    }

    //? if >=1.21.11 {
    /*private static Identifier mc(String path) {
        return Identifier.fromNamespaceAndPath("minecraft", path);
    *///?} else {
    private static ResourceLocation mc(String path) {
        return ResourceLocation.fromNamespaceAndPath("minecraft", path);
    //?}
    }

    private ModSmithingTemplates() {
    }
}
