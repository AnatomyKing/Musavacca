package space.anatomyuniverse.musavacca.data.tags;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
//? if >=1.21.6
//import net.minecraft.data.tags.KeyTagProvider;
//? if <1.21.6
import net.minecraft.data.tags.ItemTagsProvider;
//? if <1.21.6
import net.minecraft.data.tags.TagsProvider;
//? if >=1.21.6
//import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
//? if <1.21.6
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredItem;
import space.anatomyuniverse.musavacca.MusaCore;
import space.anatomyuniverse.musavacca.item.ModItems;

import java.util.concurrent.CompletableFuture;

//? if >=1.21.6 {
/*public final class ModItemTagsProvider extends KeyTagProvider<Item> {
*///?} else
    public final class ModItemTagsProvider extends ItemTagsProvider {

    //? if >=1.21.6 {
    /*public ModItemTagsProvider(
            PackOutput output,
            CompletableFuture<HolderLookup.Provider> lookupProvider
    ) {
        super(output, Registries.ITEM, lookupProvider, MusaCore.MOD_ID);
    }
    *///?} else {
    public ModItemTagsProvider(
            PackOutput output,
            CompletableFuture<HolderLookup.Provider> lookupProvider,
            CompletableFuture<TagsProvider.TagLookup<Block>> blockTagsProvider
    ) {
        super(output, lookupProvider, blockTagsProvider);
    }
    //?}

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(ItemTags.SWORDS)
                .add(key(ModItems.POTASSIUM_SWORD));

        tag(ItemTags.PICKAXES)
                .add(key(ModItems.POTASSIUM_PICKAXE));

        tag(ItemTags.AXES)
                .add(key(ModItems.POTASSIUM_AXE));

        tag(ItemTags.SHOVELS)
                .add(key(ModItems.POTASSIUM_SHOVEL));

        tag(ItemTags.HOES)
                .add(key(ModItems.POTASSIUM_HOE));

        tag(ItemTags.DURABILITY_ENCHANTABLE)
                .add(key(ModItems.POTASSIUM_SWORD))
                .add(key(ModItems.POTASSIUM_PICKAXE))
                .add(key(ModItems.POTASSIUM_AXE))
                .add(key(ModItems.POTASSIUM_SHOVEL))
                .add(key(ModItems.POTASSIUM_HOE))
                .add(key(ModItems.POTASSIUM_HELMET))
                .add(key(ModItems.POTASSIUM_CHESTPLATE))
                .add(key(ModItems.POTASSIUM_LEGGINGS))
                .add(key(ModItems.POTASSIUM_BOOTS));

        tag(ItemTags.VANISHING_ENCHANTABLE)
                .add(key(ModItems.POTASSIUM_SWORD))
                .add(key(ModItems.POTASSIUM_PICKAXE))
                .add(key(ModItems.POTASSIUM_AXE))
                .add(key(ModItems.POTASSIUM_SHOVEL))
                .add(key(ModItems.POTASSIUM_HOE))
                .add(key(ModItems.POTASSIUM_HELMET))
                .add(key(ModItems.POTASSIUM_CHESTPLATE))
                .add(key(ModItems.POTASSIUM_LEGGINGS))
                .add(key(ModItems.POTASSIUM_BOOTS));

        tag(ItemTags.MINING_ENCHANTABLE)
                .add(key(ModItems.POTASSIUM_PICKAXE))
                .add(key(ModItems.POTASSIUM_AXE))
                .add(key(ModItems.POTASSIUM_SHOVEL))
                .add(key(ModItems.POTASSIUM_HOE));

        tag(ItemTags.MINING_LOOT_ENCHANTABLE)
                .add(key(ModItems.POTASSIUM_PICKAXE))
                .add(key(ModItems.POTASSIUM_AXE))
                .add(key(ModItems.POTASSIUM_SHOVEL))
                .add(key(ModItems.POTASSIUM_HOE));

        tag(ItemTags.SWORD_ENCHANTABLE)
                .add(key(ModItems.POTASSIUM_SWORD));

        tag(ItemTags.WEAPON_ENCHANTABLE)
                .add(key(ModItems.POTASSIUM_SWORD));

        tag(ItemTags.SHARP_WEAPON_ENCHANTABLE)
                .add(key(ModItems.POTASSIUM_SWORD))
                .add(key(ModItems.POTASSIUM_AXE));

        tag(ItemTags.FIRE_ASPECT_ENCHANTABLE)
                .add(key(ModItems.POTASSIUM_SWORD));

        tag(ModItems.REPAIRS_POTASSIUM_ARMOR)
                .add(key(ModItems.POTASSIUM_INGOT));

        tag(ItemTags.HEAD_ARMOR)
                .add(key(ModItems.POTASSIUM_HELMET));

        tag(ItemTags.CHEST_ARMOR)
                .add(key(ModItems.POTASSIUM_CHESTPLATE));

        tag(ItemTags.LEG_ARMOR)
                .add(key(ModItems.POTASSIUM_LEGGINGS));

        tag(ItemTags.FOOT_ARMOR)
                .add(key(ModItems.POTASSIUM_BOOTS));

        tag(ItemTags.TRIMMABLE_ARMOR)
                .add(key(ModItems.POTASSIUM_HELMET))
                .add(key(ModItems.POTASSIUM_CHESTPLATE))
                .add(key(ModItems.POTASSIUM_LEGGINGS))
                .add(key(ModItems.POTASSIUM_BOOTS));

        tag(ItemTags.HEAD_ARMOR_ENCHANTABLE)
                .add(key(ModItems.POTASSIUM_HELMET));

        tag(ItemTags.CHEST_ARMOR_ENCHANTABLE)
                .add(key(ModItems.POTASSIUM_CHESTPLATE));

        tag(ItemTags.LEG_ARMOR_ENCHANTABLE)
                .add(key(ModItems.POTASSIUM_LEGGINGS));

        tag(ItemTags.FOOT_ARMOR_ENCHANTABLE)
                .add(key(ModItems.POTASSIUM_BOOTS));

        tag(ItemTags.ARMOR_ENCHANTABLE)
                .add(key(ModItems.POTASSIUM_HELMET))
                .add(key(ModItems.POTASSIUM_CHESTPLATE))
                .add(key(ModItems.POTASSIUM_LEGGINGS))
                .add(key(ModItems.POTASSIUM_BOOTS));

        tag(ItemTags.EQUIPPABLE_ENCHANTABLE)
                .add(key(ModItems.POTASSIUM_HELMET))
                .add(key(ModItems.POTASSIUM_CHESTPLATE))
                .add(key(ModItems.POTASSIUM_LEGGINGS))
                .add(key(ModItems.POTASSIUM_BOOTS));
    }

    //? if >=1.21.6 {
    /*private static ResourceKey<Item> key(DeferredItem<? extends Item> item) {
        return ResourceKey.create(Registries.ITEM, item.getId());
    }
    *///?} else {
    private static Item key(DeferredItem<? extends Item> item) {
        return item.get();
    }
    //?}
}