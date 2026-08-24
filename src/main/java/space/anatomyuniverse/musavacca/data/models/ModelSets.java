package space.anatomyuniverse.musavacca.data.models;

import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredBlock;
import space.anatomyuniverse.musavacca.block.ModBlocks;
import space.anatomyuniverse.musavacca.block.custom.ExudatedStrippedMusavaccaStemBlock;
import space.anatomyuniverse.musavacca.block.custom.PearlCandleBlock;
import space.anatomyuniverse.musavacca.block.entity.custom.HardHexBlockEntity;
import space.anatomyuniverse.musavacca.data.models.block.*;
import space.anatomyuniverse.musavacca.data.models.item.*;
import space.anatomyuniverse.musavacca.item.ModItems;
import space.anatomyuniverse.musavacca.tint.PearlFireTintProfiles;

import java.util.List;
import java.util.Map;

public final class ModelSets {
    private ModelSets() {}

    public static Block[] cubeAllBlocks() {
        return new Block[] {
                ModBlocks.BANANA_PEARL_BLOCK.get(),
                ModBlocks.BANANA_PEARL_BRICKS.get(),
                ModBlocks.MUSAVACCA_PLANKS.get()
        };
    }


    public static CaroteneGrassModels.Entry caroteneGrassModels() {
        return new CaroteneGrassModels.Entry(
                ModBlocks.CAROTENE_GRASS.get(),
                ModBlocks.CAROTENE_SHORT_GRASS.get(),
                ModBlocks.CAROTENE_TALL_GRASS.get()
        );
    }

    public static Map<Block, BreakBlockOwn.AgeModels> breakBlockOwnModels() {
        return Map.of(
                ModBlocks.MUSAVACCA_EGG.get(),
                new BreakBlockOwn.AgeModels(
                        "musavacca:block/musavacca_egg/musavacca_egg_stage0",
                        "musavacca:block/musavacca_egg/musavacca_egg_stage1",
                        "musavacca:block/musavacca_egg/musavacca_egg_stage2",
                        "musavacca:block/musavacca_egg/musavacca_egg_attached_stage0",
                        "musavacca:block/musavacca_egg/musavacca_egg_attached_stage1",
                        "musavacca:block/musavacca_egg/musavacca_egg_attached_stage2"
                )
        );
    }

    public static Map<Block, String> cubeOwnModels() {
        return Map.of();
    }

    public static Map<Block, CubeOwnTintedHexColorClipped.Entry> cubeOwnTintedHexColorClippedModels() {
        return Map.of(
                ModBlocks.HEX_BLOCK.get(),
                CubeOwnTintedHexColorClipped.Entry.dynamic(
                        "musavacca:block/lopha_blossom",
                        "musavacca:block/clipped_lopha_blossom"
                )
        );
    }

    public static Map<Block, CubeVocoTable.Models> cubeVocoTableModels() {
        return Map.of(
                ModBlocks.VOCO_TABLE.get(),
                new CubeVocoTable.Models(
                        "musavacca:block/voco_table",
                        "musavacca:block/voco_table_lit_receptor_corner",
                        "musavacca:block/voco_table_rotary_dialers",
                        "musavacca:block/voco_table_portal_north_east",
                        "musavacca:block/voco_table_portal_south_east",
                        "musavacca:block/voco_table_portal_south_west",
                        "musavacca:block/voco_table_portal_north_west"
                )
        );
    }

    public static Map<
            Block,
            CubeMusavaccaPortalDoorTinted.Models
            >
    cubeMusavaccaPortalDoorTintedModels() {
        return Map.of(
                ModBlocks.MUSAVACCA_DOOR.get(),
                new CubeMusavaccaPortalDoorTinted.Models(
                        ModItems.MUSAVACCA_DOOR.get(),
                        ModItems.MUSAVACCA_CHARGED_DOOR.get(),
                        ModItems.MUSAVACCA_IMBUED_DOOR.get(),

                        new CubeMusavaccaPortalDoorTinted.LitModels(
                                "musavacca:block/musavacca_door/knob/lit_knob_door_bottom_left",
                                "musavacca:block/musavacca_door/knob/lit_knob_door_bottom_left_open",
                                "musavacca:block/musavacca_door/knob/lit_knob_door_bottom_right",
                                "musavacca:block/musavacca_door/knob/lit_knob_door_bottom_right_open"
                        ),

                        new CubeMusavaccaPortalDoorTinted.LitPortalModels(
                                "musavacca:block/musavacca_door/knob/lit_portal_door_bottom_left",
                                "musavacca:block/musavacca_door/knob/lit_portal_door_bottom_left_open",
                                "musavacca:block/musavacca_door/knob/lit_portal_door_bottom_right",
                                "musavacca:block/musavacca_door/knob/lit_portal_door_bottom_right_open"
                        ),

                        new CubeMusavaccaPortalDoorTinted.PortalModels(
                                "musavacca:block/musavacca_door/portal/portal_bottom_left_open",
                                "musavacca:block/musavacca_door/portal/portal_bottom_right_open",
                                "musavacca:block/musavacca_door/door_portal/door_portal_top_left",
                                "musavacca:block/musavacca_door/door_portal/door_portal_top_right",
                                "musavacca:block/musavacca_door/portal/portal_top_left_open",
                                "musavacca:block/musavacca_door/portal/portal_top_right_open"
                        ),

                        PearlFireTintProfiles.PORTAL_BLOCK
                )
        );
    }

    public static Map<
            Block,
            CubeMusavaccaPortalTrapdoorTinted.Models
            >
    cubeMusavaccaPortalTrapdoorTintedModels() {
        return Map.of(
                ModBlocks.MUSAVACCA_TRAPDOOR.get(),
                new CubeMusavaccaPortalTrapdoorTinted.Models(
                        new CubeMusavaccaPortalTrapdoorTinted.LitModels(
                                "musavacca:block/musavacca_trapdoor/knob/lit_knob_trapdoor_bottom",
                                "musavacca:block/musavacca_trapdoor/knob/lit_knob_trapdoor_top",
                                "musavacca:block/musavacca_trapdoor/knob/lit_knob_trapdoor_open"
                        ),

                        new CubeMusavaccaPortalTrapdoorTinted.LitPortalModels(
                                "musavacca:block/musavacca_trapdoor/knob/lit_portal_trapdoor_bottom",
                                "musavacca:block/musavacca_trapdoor/knob/lit_portal_trapdoor_top",
                                "musavacca:block/musavacca_trapdoor/knob/lit_portal_trapdoor_open"
                        ),

                        new CubeMusavaccaPortalTrapdoorTinted.PortalModels(
                                "musavacca:block/musavacca_trapdoor/trapdoor_portal/trapdoor_portal_bottom",
                                "musavacca:block/musavacca_trapdoor/trapdoor_portal/trapdoor_portal_top",
                                "musavacca:block/musavacca_trapdoor/portal/portal_open"
                        )
                )
        );
    }

    public static Map<Block, CubeVocoPost.Models> cubeVocoPostModels() {
        return Map.of(
                ModBlocks.VOCO_POST.get(),
                new CubeVocoPost.Models(
                        "musavacca:block/voco_post",
                        "musavacca:block/voco_post_lit_receptor_corner",
                        "musavacca:block/voco_post_portal"
                )
        );
    }

    public static Map<Block, CubeMusavaccaCropOwnTintedFoliage.Models> cubeMusavaccaCropOwnTintedFoliageModels() {
        return Map.of(
                ModBlocks.MUSAVACCA_SPROUT.get(),
                CubeMusavaccaCropOwnTintedFoliage.of(
                        "musavacca:block/musavacca_sprout"
                ),

                ModBlocks.MUSAVACCA_SUCKER.get(),
                CubeMusavaccaCropOwnTintedFoliage.of(
                        "musavacca:block/musavacca_sucker"
                ),

                ModBlocks.MUSAVACCA_PLANT.get(),
                CubeMusavaccaCropOwnTintedFoliage.of(
                        "musavacca:block/musavacca_plant"
                ),

                ModBlocks.MUSAVACCA_PSEUDOSTEM.get(),
                CubeMusavaccaCropOwnTintedFoliage.of(
                        "musavacca:block/musavacca_pseudostem"
                )
        );
    }

    public static Map<Block, CubeOwnTintedHexColor.Entry> cubeOwnTintedHexColorModels() {
        return Map.of(
                ModBlocks.HARD_HEX_BLOCK.get(),
                CubeOwnTintedHexColor.Entry.constant(
                        "musavacca:block/hex_block",
                        HardHexBlockEntity.HARD_HEX_COLOR
                )
        );
    }

    public static Map<Block, DecorationModelBlocks.Models> decorationBlockModels() {
        return Map.of(
                ModBlocks.BANANA_PEARL_CHALICE.get(),
                DecorationModelBlocks.Models.auto()
        );
    }

    public static Block[] smallBananaPearlBlocks() {
        return new Block[] {
                ModBlocks.SMALL_BANANA_PEARL_BLOCK.get()
        };
    }

    public static Map<Block, BarrelCropOwnTintedFoliage.AgeModels> barrelCropOwnTintedFoliageModels() {
        return Map.of(
                ModBlocks.MUSAVACCA_LEAVES.get(),
                new BarrelCropOwnTintedFoliage.AgeModels(
                        "musavacca:block/musavacca_leaves",
                        "musavacca:block/musavacca_leaves_crown",
                        "musavacca:block/musavacca_leaves_cross"
                )
        );
    }

    public static Map<Block, CubeFireBlockTinted32.Entry> cubeFireBlockTinted32Models() {
        return Map.of(
                ModBlocks.PEARL_FIRE.get(),
                CubeFireBlockTinted32.Entry.auto(
                        "pearl_fire",
                        "musavacca:block/custom_parent/tinted_template_fire_up",
                        "musavacca:block/custom_parent/tinted_template_fire_side",
                        "musavacca:block/custom_parent/tinted_template_fire_side_alt",
                        "musavacca:block/custom_parent/tinted_template_fire_floor",
                        "musavacca:block/custom_parent/tinted_template_fire_up_alt"
                )
        );
    }

    public static Block[] chapiterBlocks() {
        return new Block[] {
                ModBlocks.BANANA_PEARL_CHAPITER.get()
        };
    }

    public static Block[] logBlocks() {
        return new Block[] {
                ModBlocks.BANANA_PEARL_PILLAR.get(),
                ModBlocks.MUSAVACCA_STEM.get(),
                ModBlocks.STRIPPED_MUSAVACCA_STEM.get()
        };
    }

    public static ExudatedStrippedMusavaccaStemBlock[] exudatedLogBlocks() {
        return new ExudatedStrippedMusavaccaStemBlock[] {
                ModBlocks.EXUDATED_STRIPPED_MUSAVACCA_STEM.get()
        };
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

    public static CustomArmorSetTintedLayers.Entry[]
    customArmorSetTintedLayers() {
        return new CustomArmorSetTintedLayers.Entry[] {
                CustomArmorSetTintedLayers.fullyTintedFolder(
                        ModItems.IMBUED_POTASSIUM_HELMET.get(),
                        ModItems.IMBUED_POTASSIUM_CHESTPLATE.get(),
                        ModItems.IMBUED_POTASSIUM_LEGGINGS.get(),
                        ModItems.IMBUED_POTASSIUM_BOOTS.get(),
                        "musavacca:imbued_potassium",
                        "musavacca:imbued_potassium/imbued_potassium",
                        "musavacca:item/imbued_potassium_helmet_model",
                        PearlFireTintProfiles.IMBUED_POTASSIUM_ITEMS,
                        PearlFireTintProfiles.IMBUED_POTASSIUM_HELMET,
                        PearlFireTintProfiles.IMBUED_POTASSIUM_ARMOR
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
                ModItems.MUSAVACCA_BOAT.get()
                //? if <1.21.4 {
                /*,
                ModItems.MUSAVACCA_DOOR_ITEM.get()
                *///?}
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

    public static HandheldItemsTintedLayers.Entry[] handheldItemsTintedLayers() {
        return new HandheldItemsTintedLayers.Entry[] {
                HandheldItemsTintedLayers.fullyTintedFolder(
                        ModItems.IMBUED_POTASSIUM_AXE.get(),
                        PearlFireTintProfiles.IMBUED_POTASSIUM_ITEMS
                ),
                HandheldItemsTintedLayers.fullyTintedFolder(
                        ModItems.IMBUED_POTASSIUM_PICKAXE.get(),
                        PearlFireTintProfiles.IMBUED_POTASSIUM_ITEMS
                ),
                HandheldItemsTintedLayers.fullyTintedFolder(
                        ModItems.IMBUED_POTASSIUM_SHOVEL.get(),
                        PearlFireTintProfiles.IMBUED_POTASSIUM_ITEMS
                ),
                HandheldItemsTintedLayers.fullyTintedFolder(
                        ModItems.IMBUED_POTASSIUM_SWORD.get(),
                        PearlFireTintProfiles.IMBUED_POTASSIUM_ITEMS
                ),
                HandheldItemsTintedLayers.fullyTintedFolder(
                        ModItems.IMBUED_POTASSIUM_HOE.get(),
                        PearlFireTintProfiles.IMBUED_POTASSIUM_ITEMS
                )
        };
    }

    public static CustomItemModel.Entry[] customItemModels() {
        return new CustomItemModel.Entry[] {
                CustomItemModel.of(
                        ModItems.BANANA_PHONE.get(),
                        "musavacca:item/banana_phone"
                )
        };
    }

    public static ItemTintedLayers.Entry[] itemTintedLayers() {
        return new ItemTintedLayers.Entry[] {
                ItemTintedLayers.folder(
                        ModItems.IMBUED_POTASSIUM_UPGRADE_SMITHING_TEMPLATE.get(),
                        PearlFireTintProfiles.IMBUED_POTASSIUM
                ),

                ItemTintedLayers.folder(
                        ModItems.SIM_CARD.get(),
                        PearlFireTintProfiles.SIM_CARD_TINT
                )
        };
    }
}
