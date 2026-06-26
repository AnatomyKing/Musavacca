package space.anatomyuniverse.musavacca.item;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SmithingTemplateItem;
import space.anatomyuniverse.musavacca.MusaCore;

import java.util.List;

public final class ModSmithingTemplates {

    public static final Entry IMBUED_POTASSIUM_UPGRADE = Entry.of(
            "imbued_potassium_upgrade_smithing_template",
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
            IMBUED_POTASSIUM_UPGRADE
    );

    public record Entry(
            String registryPath,
            Rarity rarity,
            String upgrade,
            String appliesTo,
            String ingredients,
            String baseSlotDescription,
            String additionsSlotDescription,
            List<ResourceLocation> emptyBaseSlotTextures,
            List<ResourceLocation> emptyAdditionsSlotTextures
    ) {
        public static Entry of(
                String registryPath,
                Rarity rarity,
                String upgrade,
                String appliesTo,
                String ingredients,
                String baseSlotDescription,
                String additionsSlotDescription,
                List<ResourceLocation> emptyBaseSlotTextures,
                List<ResourceLocation> emptyAdditionsSlotTextures
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
            //? if <1.21.4 {
            /*return new SmithingTemplateItem(
                    Component.translatable(appliesToKey()),
                    Component.translatable(ingredientsKey()),
                    Component.translatable(upgradeKey()),
                    Component.translatable(baseSlotDescriptionKey()),
                    Component.translatable(additionsSlotDescriptionKey()),
                    emptyBaseSlotTextures(),
                    emptyAdditionsSlotTextures()
            );
            *///?} else {
            return new SmithingTemplateItem(
                    Component.translatable(appliesToKey()),
                    Component.translatable(ingredientsKey()),
                    Component.translatable(baseSlotDescriptionKey()),
                    Component.translatable(additionsSlotDescriptionKey()),
                    emptyBaseSlotTextures(),
                    emptyAdditionsSlotTextures(),
                    props.rarity(rarity())
            );
            //?}
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

    private static ResourceLocation emptyHelmetSlot() {
        //? if <1.21.4 {
        /*return mc("item/empty_armor_slot_helmet");
         *///?} else {
        return mc("container/slot/helmet");
        //?}
    }

    private static ResourceLocation emptyChestplateSlot() {
        //? if <1.21.4 {
        /*return mc("item/empty_armor_slot_chestplate");
         *///?} else {
        return mc("container/slot/chestplate");
        //?}
    }

    private static ResourceLocation emptyLeggingsSlot() {
        //? if <1.21.4 {
        /*return mc("item/empty_armor_slot_leggings");
         *///?} else {
        return mc("container/slot/leggings");
        //?}
    }

    private static ResourceLocation emptyBootsSlot() {
        //? if <1.21.4 {
        /*return mc("item/empty_armor_slot_boots");
         *///?} else {
        return mc("container/slot/boots");
        //?}
    }

    private static ResourceLocation emptySwordSlot() {
        //? if <1.21.4 {
        /*return mc("item/empty_slot_sword");
         *///?} else {
        return mc("container/slot/sword");
        //?}
    }

    private static ResourceLocation emptyPickaxeSlot() {
        //? if <1.21.4 {
        /*return mc("item/empty_slot_pickaxe");
         *///?} else {
        return mc("container/slot/pickaxe");
        //?}
    }

    private static ResourceLocation emptyAxeSlot() {
        //? if <1.21.4 {
        /*return mc("item/empty_slot_axe");
         *///?} else {
        return mc("container/slot/axe");
        //?}
    }

    private static ResourceLocation emptyShovelSlot() {
        //? if <1.21.4 {
        /*return mc("item/empty_slot_shovel");
         *///?} else {
        return mc("container/slot/shovel");
        //?}
    }

    private static ResourceLocation emptyHoeSlot() {
        //? if <1.21.4 {
        /*return mc("item/empty_slot_hoe");
         *///?} else {
        return mc("container/slot/hoe");
        //?}
    }

    private static ResourceLocation emptyIngotSlot() {
        //? if <1.21.4 {
        /*return mc("item/empty_slot_ingot");
         *///?} else {
        return mc("container/slot/ingot");
        //?}
    }

    private static ResourceLocation mc(String path) {
        return ResourceLocation.fromNamespaceAndPath("minecraft", path);
    }

    private ModSmithingTemplates() {
    }
}