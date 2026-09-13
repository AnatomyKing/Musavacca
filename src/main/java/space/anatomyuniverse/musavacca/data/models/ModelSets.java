package space.anatomyuniverse.musavacca.data.models;

import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredBlock;
import space.anatomyuniverse.musavacca.block.ModBlocks;
import space.anatomyuniverse.musavacca.block.custom.PearlCandleBlock;
import space.anatomyuniverse.musavacca.data.models.block.*;
import space.anatomyuniverse.musavacca.data.models.item.*;
import space.anatomyuniverse.musavacca.item.ModItems;

import java.util.List;
import java.util.Map;

public final class ModelSets {
    private ModelSets() {}


    public static Map<Block, String> cubeOwnModels() {
        return Map.of();
    }

    public static Map<Block, DecorationModelBlocks.Models> decorationBlockModels() {
        return Map.of(
                ModBlocks.BANANA_PEARL_CHALICE.get(),
                DecorationModelBlocks.Models.auto()
        );
    }





    public static List<DeferredBlock<PearlCandleBlock>> pearlCandleBlocks() {
        return ModBlocks.PEARL_CANDLES;
    }

    public static Block[] stairsBlocks() {
        return new Block[] {
                ModBlocks.MUSAVACCA_STAIRS.get()
        };
    }

    public static Block[] slabBlocks() {
        return new Block[] {
                ModBlocks.MUSAVACCA_SLAB.get()
        };
    }

    public static Block[] fenceBlocks() {
        return new Block[] {
                ModBlocks.MUSAVACCA_FENCE.get()
        };
    }

    public static Block[] fenceGateBlocks() {
        return new Block[] {
                ModBlocks.MUSAVACCA_FENCE_GATE.get()
        };
    }

    public static Block[] doorBlocks() {
        return new Block[] {
        };
    }

    public static Block[] trapdoorBlocks() {
        return new Block[] {
        };
    }

    public static Block[] pressurePlateBlocks() {
        return new Block[] {
                ModBlocks.MUSAVACCA_PRESSURE_PLATE.get()
        };
    }

    public static Block[] buttonBlocks() {
        return new Block[] {
                ModBlocks.MUSAVACCA_BUTTON.get()
        };
    }

    public static Block[] cubeCrafterLikeBlocks() {
        return new Block[] {};
    }

    public static Block[] CubeCrafterLikeBlocks() {
        return cubeCrafterLikeBlocks();
    }

    public static SpawnEggItems.Entry[] spawnEggItems() {
        return new SpawnEggItems.Entry[] {
                SpawnEggItems.of(
                        ModItems.BANANA_COW_SPAWN_EGG.get(),
                        0xE4C64A,
                        0x7A4A1F
                ),
                SpawnEggItems.of(
                        ModItems.BASUKE_SPAWN_EGG.get(),
                        0xE6DCC8,
                        0x4F3F36
                )
        };
    }

    public static CustomArmorSet.Entry[] customArmorSets() {
        return new CustomArmorSet.Entry[] {
                CustomArmorSet.of(
                        ModItems.POTASSIUM_HELMET.get(),
                        ModItems.POTASSIUM_CHESTPLATE.get(),
                        ModItems.POTASSIUM_LEGGINGS.get(),
                        ModItems.POTASSIUM_BOOTS.get(),
                        "musavacca:potassium",
                        "musavacca:potassium",
                        "musavacca:item/potassium_helmet_model"
                )
        };
    }


    public static Map<Block, PanePortalBlockTinted15.Entry> panePortalBlockTinted15Models() {
        return Map.of(
                ModBlocks.PEARL_PORTAL.get(),
                PanePortalBlockTinted15.Entry.auto("pearl_portal")
        );
    }

    public static ItemLike[] flatItems() {
        return new ItemLike[] {
                ModItems.BANANA_PEARL.get(),
                ModItems.BIG_BANANA_PEARL.get(),
                ModItems.SMALL_BANANA_PEARL.get(),
                ModItems.FLINT_AND_PEARL.get(),
                ModItems.VACACA.get(),
                ModItems.MUSAVACCA_EXUDATE.get(),
                ModItems.BANAZO_GUSMA_LUMPA_GOOP.get(),
                ModItems.BANANA_PELLIS.get(),
                ModItems.MUSAVACCA_PUP.get(),
                ModBlocks.UNRIPE_MUSAVACCA_EGG.get(),
                ModBlocks.RIPENING_MUSAVACCA_EGG.get(),
                ModBlocks.RIPE_MUSAVACCA_EGG.get(),
                ModItems.POTASSIUM_INGOT.get(),
                ModItems.FRACTURED_POTASSIUM_UPGRADE_SMITHING_TEMPLATE.get(),
                ModItems.POTASSIUM_UPGRADE_SMITHING_TEMPLATE.get(),
                ModItems.BANANA_MILK_BUCKET.get(),
                ModItems.MUSAVACCA_BOAT.get(),
        };
    }

    public static ItemLike[] handheldItems() {
        return new ItemLike[] {
                ModItems.POTASSIUM_AXE.get(),
                ModItems.POTASSIUM_PICKAXE.get(),
                ModItems.POTASSIUM_SHOVEL.get(),
                ModItems.POTASSIUM_SWORD.get(),
                ModItems.POTASSIUM_HOE.get()
        };
    }


    public static CustomItemModel.Entry[] customItemModels() {
        return new CustomItemModel.Entry[] {
                CustomItemModel.of(
                        ModItems.BANANA_PHONE.get(),
                        "musavacca:item/banana_phone"
                ),
                CustomItemModel.of(
                        ModItems.INACTIVE_VOCO_CALLER.get(),
                        "musavacca:item/banana_phone_off"
                )
        };
    }

    public static FlatItems.HexTinted[] hexTintedFlatItems() {
        return new FlatItems.HexTinted[] {
                FlatItems.hexTintedFolder(
                        ModItems.SIM_CARD.get()
                )
        };
    }
}
