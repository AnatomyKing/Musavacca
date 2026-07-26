package space.anatomyuniverse.musavacca.data.tags;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
//? if >=1.21.6
import net.minecraft.data.tags.KeyTagProvider;
//? if <1.21.6
//import net.minecraft.data.tags.ItemTagsProvider;
//? if <1.21.6
//import net.minecraft.data.tags.TagsProvider;
//? if >=1.21.6
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import space.anatomyuniverse.musavacca.MusaCore;
import space.anatomyuniverse.musavacca.block.ModBlocks;
import space.anatomyuniverse.musavacca.item.CustomHelmetArmorTrims;
import space.anatomyuniverse.musavacca.item.ModItems;

import java.util.concurrent.CompletableFuture;

//? if >=1.21.6 {
public final class ModItemTagsProvider
        extends KeyTagProvider<Item> {
//?} else
    //public final class ModItemTagsProvider extends ItemTagsProvider {

    //? if >=1.21.6 {
    public ModItemTagsProvider(
            PackOutput output,
            CompletableFuture<HolderLookup.Provider> lookupProvider
    ) {
        super(
                output,
                Registries.ITEM,
                lookupProvider,
                MusaCore.MOD_ID
        );
    }
    //?} else {
    /*public ModItemTagsProvider(
            PackOutput output,
            CompletableFuture<HolderLookup.Provider> lookupProvider,
            CompletableFuture<TagsProvider.TagLookup<Block>> blockTagsProvider
    ) {
        super(
                output,
                lookupProvider,
                blockTagsProvider
        );
    }
    *///?}

    @Override
    protected void addTags(
            HolderLookup.Provider provider
    ) {
        /*
         * Musavacca wood-family item tags.
         */

        tag(ItemTags.PLANKS)
                .add(key(ModBlocks.MUSAVACCA_PLANKS));

        tag(ItemTags.LOGS)
                .add(key(ModBlocks.MUSAVACCA_STEM))
                .add(key(ModBlocks.STRIPPED_MUSAVACCA_STEM))
                .add(key(ModBlocks.EXUDATED_STRIPPED_MUSAVACCA_STEM));

        tag(ItemTags.LOGS_THAT_BURN)
                .add(key(ModBlocks.MUSAVACCA_STEM))
                .add(key(ModBlocks.STRIPPED_MUSAVACCA_STEM))
                .add(key(ModBlocks.EXUDATED_STRIPPED_MUSAVACCA_STEM));

        tag(ItemTags.LEAVES)
                .add(key(ModBlocks.MUSAVACCA_LEAVES));

        tag(ItemTags.WOODEN_STAIRS)
                .add(key(ModBlocks.MUSAVACCA_STAIRS));

        tag(ItemTags.WOODEN_SLABS)
                .add(key(ModBlocks.MUSAVACCA_SLAB));

        tag(ItemTags.WOODEN_FENCES)
                .add(key(ModBlocks.MUSAVACCA_FENCE));

        tag(ItemTags.FENCE_GATES)
                .add(key(ModBlocks.MUSAVACCA_FENCE_GATE));

        tag(ItemTags.WOODEN_DOORS)
                .add(key(ModBlocks.MUSAVACCA_DOOR));

        tag(ItemTags.WOODEN_TRAPDOORS)
                .add(key(ModBlocks.MUSAVACCA_TRAPDOOR));

        tag(ItemTags.WOODEN_PRESSURE_PLATES)
                .add(key(ModBlocks.MUSAVACCA_PRESSURE_PLATE));

        tag(ItemTags.WOODEN_BUTTONS)
                .add(key(ModBlocks.MUSAVACCA_BUTTON));




        /*
         * Potassium tools.
         */

        tag(ItemTags.SWORDS)
                .add(key(ModItems.POTASSIUM_SWORD))
                .add(key(ModItems.IMBUED_POTASSIUM_SWORD));

        tag(ItemTags.PICKAXES)
                .add(key(ModItems.POTASSIUM_PICKAXE))
                .add(key(ModItems.IMBUED_POTASSIUM_PICKAXE));

        tag(ItemTags.AXES)
                .add(key(ModItems.POTASSIUM_AXE))
                .add(key(ModItems.IMBUED_POTASSIUM_AXE));

        tag(ItemTags.SHOVELS)
                .add(key(ModItems.POTASSIUM_SHOVEL))
                .add(key(ModItems.IMBUED_POTASSIUM_SHOVEL));

        tag(ItemTags.HOES)
                .add(key(ModItems.POTASSIUM_HOE))
                .add(key(ModItems.IMBUED_POTASSIUM_HOE));


        /*
         * General durability enchantments.
         */

        tag(ItemTags.DURABILITY_ENCHANTABLE)
                .add(key(ModItems.POTASSIUM_SWORD))
                .add(key(ModItems.POTASSIUM_PICKAXE))
                .add(key(ModItems.POTASSIUM_AXE))
                .add(key(ModItems.POTASSIUM_SHOVEL))
                .add(key(ModItems.POTASSIUM_HOE))
                .add(key(ModItems.IMBUED_POTASSIUM_SWORD))
                .add(key(ModItems.IMBUED_POTASSIUM_PICKAXE))
                .add(key(ModItems.IMBUED_POTASSIUM_AXE))
                .add(key(ModItems.IMBUED_POTASSIUM_SHOVEL))
                .add(key(ModItems.IMBUED_POTASSIUM_HOE))
                .add(key(ModItems.POTASSIUM_HELMET))
                .add(key(ModItems.POTASSIUM_CHESTPLATE))
                .add(key(ModItems.POTASSIUM_LEGGINGS))
                .add(key(ModItems.POTASSIUM_BOOTS))
                .add(key(ModItems.IMBUED_POTASSIUM_HELMET))
                .add(key(ModItems.IMBUED_POTASSIUM_CHESTPLATE))
                .add(key(ModItems.IMBUED_POTASSIUM_LEGGINGS))
                .add(key(ModItems.IMBUED_POTASSIUM_BOOTS));

        tag(ItemTags.VANISHING_ENCHANTABLE)
                .add(key(ModItems.POTASSIUM_SWORD))
                .add(key(ModItems.POTASSIUM_PICKAXE))
                .add(key(ModItems.POTASSIUM_AXE))
                .add(key(ModItems.POTASSIUM_SHOVEL))
                .add(key(ModItems.POTASSIUM_HOE))
                .add(key(ModItems.IMBUED_POTASSIUM_SWORD))
                .add(key(ModItems.IMBUED_POTASSIUM_PICKAXE))
                .add(key(ModItems.IMBUED_POTASSIUM_AXE))
                .add(key(ModItems.IMBUED_POTASSIUM_SHOVEL))
                .add(key(ModItems.IMBUED_POTASSIUM_HOE))
                .add(key(ModItems.POTASSIUM_HELMET))
                .add(key(ModItems.POTASSIUM_CHESTPLATE))
                .add(key(ModItems.POTASSIUM_LEGGINGS))
                .add(key(ModItems.POTASSIUM_BOOTS))
                .add(key(ModItems.IMBUED_POTASSIUM_HELMET))
                .add(key(ModItems.IMBUED_POTASSIUM_CHESTPLATE))
                .add(key(ModItems.IMBUED_POTASSIUM_LEGGINGS))
                .add(key(ModItems.IMBUED_POTASSIUM_BOOTS));


        /*
         * Mining enchantment categories.
         */

        tag(ItemTags.MINING_ENCHANTABLE)
                .add(key(ModItems.POTASSIUM_PICKAXE))
                .add(key(ModItems.POTASSIUM_AXE))
                .add(key(ModItems.POTASSIUM_SHOVEL))
                .add(key(ModItems.POTASSIUM_HOE))
                .add(key(ModItems.IMBUED_POTASSIUM_PICKAXE))
                .add(key(ModItems.IMBUED_POTASSIUM_AXE))
                .add(key(ModItems.IMBUED_POTASSIUM_SHOVEL))
                .add(key(ModItems.IMBUED_POTASSIUM_HOE));

        tag(ItemTags.MINING_LOOT_ENCHANTABLE)
                .add(key(ModItems.POTASSIUM_PICKAXE))
                .add(key(ModItems.POTASSIUM_AXE))
                .add(key(ModItems.POTASSIUM_SHOVEL))
                .add(key(ModItems.POTASSIUM_HOE))
                .add(key(ModItems.IMBUED_POTASSIUM_PICKAXE))
                .add(key(ModItems.IMBUED_POTASSIUM_AXE))
                .add(key(ModItems.IMBUED_POTASSIUM_SHOVEL))
                .add(key(ModItems.IMBUED_POTASSIUM_HOE));


        /*
         * Weapon enchantment categories.
         */

        tag(ItemTags.SWORD_ENCHANTABLE)
                .add(key(ModItems.POTASSIUM_SWORD))
                .add(key(ModItems.IMBUED_POTASSIUM_SWORD));

        tag(ItemTags.WEAPON_ENCHANTABLE)
                .add(key(ModItems.POTASSIUM_SWORD))
                .add(key(ModItems.IMBUED_POTASSIUM_SWORD));

        tag(ItemTags.SHARP_WEAPON_ENCHANTABLE)
                .add(key(ModItems.POTASSIUM_SWORD))
                .add(key(ModItems.POTASSIUM_AXE))
                .add(key(ModItems.IMBUED_POTASSIUM_SWORD))
                .add(key(ModItems.IMBUED_POTASSIUM_AXE));

        tag(ItemTags.FIRE_ASPECT_ENCHANTABLE)
                .add(key(ModItems.POTASSIUM_SWORD))
                .add(key(ModItems.IMBUED_POTASSIUM_SWORD));


        /*
         * Potassium armor repair material.
         */

        tag(ModItems.REPAIRS_POTASSIUM_ARMOR)
                .add(key(ModItems.POTASSIUM_INGOT));


        /*
         * Armor slots.
         */

        tag(ItemTags.HEAD_ARMOR)
                .add(key(ModItems.POTASSIUM_HELMET))
                .add(key(ModItems.IMBUED_POTASSIUM_HELMET));

        tag(ItemTags.CHEST_ARMOR)
                .add(key(ModItems.POTASSIUM_CHESTPLATE))
                .add(key(ModItems.IMBUED_POTASSIUM_CHESTPLATE));

        tag(ItemTags.LEG_ARMOR)
                .add(key(ModItems.POTASSIUM_LEGGINGS))
                .add(key(ModItems.IMBUED_POTASSIUM_LEGGINGS));

        tag(ItemTags.FOOT_ARMOR)
                .add(key(ModItems.POTASSIUM_BOOTS))
                .add(key(ModItems.IMBUED_POTASSIUM_BOOTS));


        /*
         * Armor trims.
         */

        tag(ItemTags.TRIMMABLE_ARMOR)
                .add(key(ModItems.POTASSIUM_HELMET))
                .add(key(ModItems.POTASSIUM_CHESTPLATE))
                .add(key(ModItems.POTASSIUM_LEGGINGS))
                .add(key(ModItems.POTASSIUM_BOOTS))
                .add(key(ModItems.IMBUED_POTASSIUM_HELMET))
                .add(key(ModItems.IMBUED_POTASSIUM_CHESTPLATE))
                .add(key(ModItems.IMBUED_POTASSIUM_LEGGINGS))
                .add(key(ModItems.IMBUED_POTASSIUM_BOOTS));

        tag(CustomHelmetArmorTrims.CUSTOM_HEAD_HELMETS)
                .add(key(ModItems.POTASSIUM_HELMET))
                .add(key(ModItems.IMBUED_POTASSIUM_HELMET));


        /*
         * Armor enchantment categories.
         */

        tag(ItemTags.HEAD_ARMOR_ENCHANTABLE)
                .add(key(ModItems.POTASSIUM_HELMET))
                .add(key(ModItems.IMBUED_POTASSIUM_HELMET));

        tag(ItemTags.CHEST_ARMOR_ENCHANTABLE)
                .add(key(ModItems.POTASSIUM_CHESTPLATE))
                .add(key(ModItems.IMBUED_POTASSIUM_CHESTPLATE));

        tag(ItemTags.LEG_ARMOR_ENCHANTABLE)
                .add(key(ModItems.POTASSIUM_LEGGINGS))
                .add(key(ModItems.IMBUED_POTASSIUM_LEGGINGS));

        tag(ItemTags.FOOT_ARMOR_ENCHANTABLE)
                .add(key(ModItems.POTASSIUM_BOOTS))
                .add(key(ModItems.IMBUED_POTASSIUM_BOOTS));

        tag(ItemTags.ARMOR_ENCHANTABLE)
                .add(key(ModItems.POTASSIUM_HELMET))
                .add(key(ModItems.POTASSIUM_CHESTPLATE))
                .add(key(ModItems.POTASSIUM_LEGGINGS))
                .add(key(ModItems.POTASSIUM_BOOTS))
                .add(key(ModItems.IMBUED_POTASSIUM_HELMET))
                .add(key(ModItems.IMBUED_POTASSIUM_CHESTPLATE))
                .add(key(ModItems.IMBUED_POTASSIUM_LEGGINGS))
                .add(key(ModItems.IMBUED_POTASSIUM_BOOTS));

        tag(ItemTags.EQUIPPABLE_ENCHANTABLE)
                .add(key(ModItems.POTASSIUM_HELMET))
                .add(key(ModItems.POTASSIUM_CHESTPLATE))
                .add(key(ModItems.POTASSIUM_LEGGINGS))
                .add(key(ModItems.POTASSIUM_BOOTS))
                .add(key(ModItems.IMBUED_POTASSIUM_HELMET))
                .add(key(ModItems.IMBUED_POTASSIUM_CHESTPLATE))
                .add(key(ModItems.IMBUED_POTASSIUM_LEGGINGS))
                .add(key(ModItems.IMBUED_POTASSIUM_BOOTS));
    }

    //? if >=1.21.6 {
    private static ResourceKey<Item> key(
            DeferredItem<? extends Item> item
    ) {
        return ResourceKey.create(
                Registries.ITEM,
                item.getId()
        );
    }

    private static ResourceKey<Item> key(
            DeferredBlock<? extends Block> block
    ) {
        return ResourceKey.create(
                Registries.ITEM,
                block.getId()
        );
    }
    //?} else {
    /*private static Item key(
            DeferredItem<? extends Item> item
    ) {
        return item.get();
    }

    private static Item key(
            DeferredBlock<? extends Block> block
    ) {
        return block.get().asItem();
    }
    *///?}
}
