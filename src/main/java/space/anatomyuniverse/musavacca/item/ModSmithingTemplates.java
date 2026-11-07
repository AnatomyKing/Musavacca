package space.anatomyuniverse.musavacca.item;

import net.minecraft.network.chat.Component;
//? if >=1.21.11 {
/*import net.minecraft.resources.Identifier;
*///?} else {
import net.minecraft.resources.ResourceLocation;
//?}
import java.util.List;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SmithingTemplateItem;
import space.anatomyuniverse.musavacca.MusaCore;

public final class ModSmithingTemplates {
    public static final Entry POTASSIUM_UPGRADE = Entry.of("potassium_upgrade_smithing_template", Rarity.RARE, "Potassium Upgrade", "Diamond Equipment",
            "Potassium Ingot", "Add diamond armor, weapon, or tool", "Add Potassium Ingot",
            List.of(emptySlot("helmet", "empty_armor_slot_helmet"), emptySlot("chestplate", "empty_armor_slot_chestplate"),
                    emptySlot("leggings", "empty_armor_slot_leggings"), emptySlot("boots", "empty_armor_slot_boots"), emptySlot("sword", "empty_slot_sword"),
                    emptySlot("pickaxe", "empty_slot_pickaxe"), emptySlot("axe", "empty_slot_axe"), emptySlot("shovel", "empty_slot_shovel"),
                    emptySlot("hoe", "empty_slot_hoe")),
            List.of(emptySlot("ingot", "empty_slot_ingot")));
    public static final List<Entry> ALL = List.of(POTASSIUM_UPGRADE);

    public record Entry(String registryPath, Rarity rarity, String upgrade, String appliesTo, String ingredients, String baseSlotDescription,
            //? if >=1.21.11 {
            /*String additionsSlotDescription, List<Identifier> emptyBaseSlotTextures, List<Identifier> emptyAdditionsSlotTextures) {
            *///?} else {
            String additionsSlotDescription, List<ResourceLocation> emptyBaseSlotTextures, List<ResourceLocation> emptyAdditionsSlotTextures) {
            //?}
        public static Entry of(String registryPath, Rarity rarity, String upgrade, String appliesTo, String ingredients, String baseSlotDescription,
                //? if >=1.21.11 {
                /*String additionsSlotDescription, List<Identifier> emptyBaseSlotTextures, List<Identifier> emptyAdditionsSlotTextures) {
                *///?} else {
                String additionsSlotDescription, List<ResourceLocation> emptyBaseSlotTextures, List<ResourceLocation> emptyAdditionsSlotTextures) {
                //?}
            return new Entry(registryPath, rarity, upgrade, appliesTo, ingredients, baseSlotDescription, additionsSlotDescription,
                    List.copyOf(emptyBaseSlotTextures), List.copyOf(emptyAdditionsSlotTextures));
        }

        public SmithingTemplateItem create(Item.Properties props) {
            return new SmithingTemplateItem(Component.translatable(appliesToKey()), Component.translatable(ingredientsKey()),
                    //? if <1.21.2 {
                    /*Component.translatable(upgradeKey()),
                    *///?}
                    Component.translatable(baseSlotDescriptionKey()), Component.translatable(additionsSlotDescriptionKey()), emptyBaseSlotTextures(),
                    //? if >=1.21.2 {
                    emptyAdditionsSlotTextures(), props.rarity(rarity()));
                    //?} else {
                    /*emptyAdditionsSlotTextures());
                    *///?}
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
    /*private static Identifier emptySlot(String name, String legacyPath) {
    *///?} else {
    private static ResourceLocation emptySlot(String name, String legacyPath) {
    //?}
        //? if >=1.21.4 {
        String path = "container/slot/" + name;
        //?} else {
        /*String path = "item/" + legacyPath;
        *///?}
        //? if >=1.21.11 {
        /*return Identifier.fromNamespaceAndPath("minecraft", path);
        *///?} else {
        return ResourceLocation.fromNamespaceAndPath("minecraft", path);
        //?}
    }

    private ModSmithingTemplates() {}
}
