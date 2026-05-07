package space.anatomyuniverse.musavacca.data.models;
import net.neoforged.neoforge.registries.DeferredBlock;
import space.anatomyuniverse.musavacca.block.custom.PearlCandleBlock;
import space.anatomyuniverse.musavacca.data.models.item.CustomArmorSet;

import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import space.anatomyuniverse.musavacca.block.ModBlocks;
import space.anatomyuniverse.musavacca.block.entity.custom.HardHexBlockEntity;
import space.anatomyuniverse.musavacca.data.models.block.*;
import space.anatomyuniverse.musavacca.data.models.item.CustomItemModel;
import space.anatomyuniverse.musavacca.data.models.item.ItemTintedLayered;
import space.anatomyuniverse.musavacca.data.models.item.SpawnEggItems;
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
        return Map.of(
        );
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
                        "musavacca:block/voco_mensa",
                        "musavacca:block/voco_mensa_lit_receptor"
                )
        );
    }

    public static Map<Block, CubeVocoReceptor.Models> cubeVocoReceptorModels() {
        return Map.of(
                ModBlocks.VOCO_RECEPTOR.get(),
                new CubeVocoReceptor.Models(
                        "musavacca:block/voco_receptor",
                        "musavacca:block/voco_lit_receptor"
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

    public static Block[] bananaPearlChaliceBlocks() {
        return new Block[] {
                ModBlocks.BANANA_PEARL_CHALICE.get()
        };
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
                ModBlocks.STRIPPED_MUSAVACCA_STEM.get(),
                ModBlocks.EXUDATED_STRIPPED_MUSAVACCA_STEM.get()
        };
    }

    public static List<DeferredBlock<PearlCandleBlock>> pearlCandleBlocks() {
        return ModBlocks.PEARL_CANDLES;
    }

    public static Block[] stairsBlocks() {
        return new Block[] {
        };
    }

    public static Block[] cubeCrafterLikeBlocks() {
        return new Block[] {
        };
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

                        // Generates:
                        // assets/musavacca/equipment/potassium.json
                        "musavacca:potassium",

                        // Vanilla-style armor texture id.
                        //
                        // HUMANOID resolves to:
                        // assets/musavacca/textures/entity/equipment/humanoid/potassium.png
                        //
                        // HUMANOID_LEGGINGS resolves to:
                        // assets/musavacca/textures/entity/equipment/humanoid_leggings/potassium.png
                        "musavacca:potassium",

                        // Used ONLY when the helmet is rendered in display_context=head.
                        //
                        // This references your existing file:
                        // assets/musavacca/models/item/potassium_helmet_model.json
                        //
                        // The helmet item itself must have EQUIPPABLE without asset_id.
                        // That is handled in ModItems.
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
                ModItems.OPEN_INVO_TEST_ITEM.get(),
                ModBlocks.UNRIPE_MUSAVACCA_EGG.get(),
                ModBlocks.RIPENING_MUSAVACCA_EGG.get(),
                ModBlocks.RIPE_MUSAVACCA_EGG.get(),
                ModItems.POTASSIUM_INGOT.get()
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
                 )
        };
    }

    public static ItemTintedLayered.Entry[] itemTintedLayeredItems() {
        return new ItemTintedLayered.Entry[] {
                ItemTintedLayered.root(
                        ModItems.SIM_CARD.get(),
                        PearlFireTintProfiles.SIM_CARD_TINT
                )
        };
    }
}