package space.anatomyuniverse.musavacca.data.models;

import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredBlock;
import space.anatomyuniverse.musavacca.block.ModBlocks;
import space.anatomyuniverse.musavacca.block.custom.*;
import space.anatomyuniverse.musavacca.block.entity.custom.HardHexBlockEntity;
import space.anatomyuniverse.musavacca.block.entity.custom.HexBlockEntity;
import space.anatomyuniverse.musavacca.block.entity.custom.PearlFireBlockEntity;
import space.anatomyuniverse.musavacca.block.entity.custom.PearlPortalBlockEntity;
import space.anatomyuniverse.musavacca.block.entity.custom.VocoPostBlockEntity;
import space.anatomyuniverse.musavacca.block.entity.custom.VocoTableBlockEntity;
import space.anatomyuniverse.musavacca.data.models.block.*;
import space.anatomyuniverse.musavacca.data.models.item.*;
import space.anatomyuniverse.musavacca.data.models.old.*;
import space.anatomyuniverse.musavacca.data.models.unified.*;
import space.anatomyuniverse.musavacca.item.ModItems;
import space.anatomyuniverse.musavacca.item.custom.FlintAndPearlItem;
import space.anatomyuniverse.musavacca.item.custom.SimCardItem;
import space.anatomyuniverse.musavacca.crafting.craft.VocoTableCrafting;
import space.anatomyuniverse.musavacca.tint.PearlFireTintProfiles;

import java.util.List;
import java.util.Map;

public final class ModelSets {
    private ModelSets() {}

    public static SimpleBlocks.Entry[] simpleBlocks() {
        return new SimpleBlocks.Entry[] {
                SimpleBlocks.Entry.cube(ModBlocks.BANANA_PEARL_BLOCK.get()),
                SimpleBlocks.Entry.cube(ModBlocks.BANANA_PEARL_BRICKS.get()),
                SimpleBlocks.Entry.cube(ModBlocks.MUSAVACCA_PLANKS.get()),

                SimpleBlocks.Entry.models(
                                ModBlocks.HEX_BLOCK.get(),
                                SimpleBlocks.Model.when(
                                        "musavacca:block/lopha_blossom",
                                        Conditions.when(HexBlock.CLIPPED, false)
                                ),
                                SimpleBlocks.Model.when(
                                        "musavacca:block/clipped_lopha_blossom",
                                        Conditions.when(HexBlock.CLIPPED, true)
                                )
                        )
                        .hexColorTint(HexColorTint.slot(HexBlockEntity.HEX_SLOT)),

                SimpleBlocks.Entry.model(ModBlocks.HARD_HEX_BLOCK.get(), "musavacca:block/hex_block")
                        .hexColorTint(HexColorTint.constant(HardHexBlockEntity.HARD_HEX_COLOR)),

                SimpleBlocks.Entry.model(ModBlocks.MUSAVACCA_PLANT.get(), "musavacca:block/musavacca_plant")
                        .biomeTint(BiomeTint.foliage()),

                SimpleBlocks.Entry.model(ModBlocks.MUSAVACCA_PSEUDOSTEM.get(), "musavacca:block/musavacca_pseudostem")
                        .biomeTint(BiomeTint.foliage()),

                SimpleBlocks.Entry.multipart(
                                ModBlocks.VOCO_POST.get(),
                                SimpleBlocks.Part.always("musavacca:block/voco_post"),
                                SimpleBlocks.Part.when(
                                        "musavacca:block/voco_post_lit_receptor_corner",
                                        Conditions.when(VocoPostBlock.LIT, true)
                                ),
                                SimpleBlocks.Part.when(
                                                "musavacca:block/voco_post_portal",
                                                Conditions.when(VocoPostBlock.PORTAL, true)
                                        )
                                        .pearlTint(PearlTint.block(PearlFireTintProfiles.PORTAL_BLOCK)
                                                .hexSlot(VocoPostBlockEntity.HEX_SLOT_PORTAL))
                        )
                        .rotations(Rotations.horizontalFacing(VocoPostBlock.FACING))
                        .item("musavacca:block/voco_post"),

                SimpleBlocks.Entry.multipart(
                                ModBlocks.VOCO_TABLE.get(),
                                SimpleBlocks.Part.always("musavacca:block/voco_table"),
                                SimpleBlocks.Part.when("musavacca:block/voco_table_lit_receptor_corner", Conditions.when(VocoTableBlock.LIT_NORTH_EAST, true)),
                                SimpleBlocks.Part.when("musavacca:block/voco_table_lit_receptor_corner", Conditions.when(VocoTableBlock.LIT_SOUTH_EAST, true)).rotateY(90),
                                SimpleBlocks.Part.when("musavacca:block/voco_table_lit_receptor_corner", Conditions.when(VocoTableBlock.LIT_SOUTH_WEST, true)).rotateY(180),
                                SimpleBlocks.Part.when("musavacca:block/voco_table_lit_receptor_corner", Conditions.when(VocoTableBlock.LIT_NORTH_WEST, true)).rotateY(270),
                                SimpleBlocks.Part.when("musavacca:block/voco_table_rotary_dialers", Conditions.when(VocoTableBlock.ROTARY_DIALERS, true)),
                                SimpleBlocks.Part.when("musavacca:block/voco_table_portal_north_east", Conditions.when(VocoTableBlock.PORTAL_NORTH_EAST, true))
                                        .pearlTint(PearlTint.block(PearlFireTintProfiles.PORTAL_BLOCK).offset(0)
                                                .hexSlot(VocoTableBlockEntity.HEX_SLOT_PORTAL_NORTH_EAST)),
                                SimpleBlocks.Part.when("musavacca:block/voco_table_portal_south_east", Conditions.when(VocoTableBlock.PORTAL_SOUTH_EAST, true))
                                        .pearlTint(PearlTint.block(PearlFireTintProfiles.PORTAL_BLOCK).offset(100)
                                                .hexSlot(VocoTableBlockEntity.HEX_SLOT_PORTAL_SOUTH_EAST)),
                                SimpleBlocks.Part.when("musavacca:block/voco_table_portal_south_west", Conditions.when(VocoTableBlock.PORTAL_SOUTH_WEST, true))
                                        .pearlTint(PearlTint.block(PearlFireTintProfiles.PORTAL_BLOCK).offset(200)
                                                .hexSlot(VocoTableBlockEntity.HEX_SLOT_PORTAL_SOUTH_WEST)),
                                SimpleBlocks.Part.when("musavacca:block/voco_table_portal_north_west", Conditions.when(VocoTableBlock.PORTAL_NORTH_WEST, true))
                                        .pearlTint(PearlTint.block(PearlFireTintProfiles.PORTAL_BLOCK).offset(300)
                                                .hexSlot(VocoTableBlockEntity.HEX_SLOT_PORTAL_NORTH_WEST))
                        )
                        .item("musavacca:block/voco_table")
        };
    }

    public static CrossBlocks.Entry[] crossBlocks() {
        return new CrossBlocks.Entry[] {
                CrossBlocks.Entry.cross(ModBlocks.MUSAVACCA_SPROUT.get()).biomeTint(BiomeTint.foliage()),
                CrossBlocks.Entry.cross(ModBlocks.MUSAVACCA_SUCKER.get()).biomeTint(BiomeTint.foliage())
        };
    }

    public static AgeBlocks.Entry[] ageBlocks() {
        return new AgeBlocks.Entry[] {
                AgeBlocks.Entry.of(
                        ModBlocks.MUSAVACCA_EGG.get(),
                        BreakBlock.AGE,
                        AgeBlocks.Model.when(0, "musavacca:block/musavacca_egg/musavacca_egg_stage0", Conditions.when(BreakBlock.ATTACHED, false)),
                        AgeBlocks.Model.when(1, "musavacca:block/musavacca_egg/musavacca_egg_stage1", Conditions.when(BreakBlock.ATTACHED, false)),
                        AgeBlocks.Model.when(2, "musavacca:block/musavacca_egg/musavacca_egg_stage2", Conditions.when(BreakBlock.ATTACHED, false)),
                        AgeBlocks.Model.when(0, "musavacca:block/musavacca_egg/musavacca_egg_attached_stage0", Conditions.when(BreakBlock.ATTACHED, true)),
                        AgeBlocks.Model.when(1, "musavacca:block/musavacca_egg/musavacca_egg_attached_stage1", Conditions.when(BreakBlock.ATTACHED, true)),
                        AgeBlocks.Model.when(2, "musavacca:block/musavacca_egg/musavacca_egg_attached_stage2", Conditions.when(BreakBlock.ATTACHED, true))
                ),
                AgeBlocks.Entry.of(
                                ModBlocks.MUSAVACCA_LEAVES.get(),
                                MusavaccaLeaves.AGE,
                                AgeBlocks.Model.age(0, "musavacca:block/musavacca_leaves"),
                                AgeBlocks.Model.age(1, "musavacca:block/musavacca_leaves_crown"),
                                AgeBlocks.Model.age(2, "musavacca:block/musavacca_leaves_cross")
                        )
                        .rotations(Rotations.facing(MusavaccaLeaves.FACING))
                        .biomeTint(BiomeTint.foliage())
        };
    }

    public static FireBlocks.Entry[] fireBlocks() {
        return new FireBlocks.Entry[] {
                FireBlocks.Entry.auto(
                        ModBlocks.PEARL_FIRE.get(),
                        "pearl_fire",
                        PearlTint.block(PearlFireTintProfiles.FIRE_BLOCK)
                                .hexSlotWithPlacementMemory(PearlFireBlockEntity.HEX_SLOT),
                        "musavacca:block/custom_parent/tinted_template_fire_up",
                        "musavacca:block/custom_parent/tinted_template_fire_side",
                        "musavacca:block/custom_parent/tinted_template_fire_side_alt",
                        "musavacca:block/custom_parent/tinted_template_fire_floor",
                        "musavacca:block/custom_parent/tinted_template_fire_up_alt"
                )
        };
    }

    public static PortalBlocks.Entry[] portalBlocks() {
        return new PortalBlocks.Entry[] {
                PortalBlocks.Entry.auto(
                        ModBlocks.PEARL_PORTAL.get(),
                        "pearl_portal",
                        PearlTint.block(PearlFireTintProfiles.PORTAL_BLOCK)
                                .hexSlotWithPlacementMemory(PearlPortalBlockEntity.HEX_SLOT)
                )
        };
    }

    public static ColumnBlocks.Entry[] columnBlocks() {
        return new ColumnBlocks.Entry[] {
                ColumnBlocks.Entry.bottomTopFacing(ModBlocks.BANANA_PEARL_CHAPITER.get()),
                ColumnBlocks.Entry.column(ModBlocks.BANANA_PEARL_PILLAR.get()),
                ColumnBlocks.Entry.column(ModBlocks.MUSAVACCA_STEM.get()),
                ColumnBlocks.Entry.column(ModBlocks.STRIPPED_MUSAVACCA_STEM.get()),
                ColumnBlocks.Entry.column(ModBlocks.EXUDATED_STRIPPED_MUSAVACCA_STEM.get())
        };
    }

    public static SimpleItems.Entry[] simpleItems() {
        return new SimpleItems.Entry[] {
                SimpleItems.Entry.flat(ModItems.BANANA_PEARL.get()),
                SimpleItems.Entry.flat(ModItems.BIG_BANANA_PEARL.get()),
                SimpleItems.Entry.flat(ModItems.SMALL_BANANA_PEARL.get()),
                SimpleItems.Entry.flat(ModItems.FLINT_AND_PEARL.get())
                        .pearlTint(PearlTint.dynamic(PearlFireTintProfiles.FLINT_AND_PEARL)
                                .fallback(FlintAndPearlItem.DEFAULT_HEX_COLOR)
                                .itemSlot(FlintAndPearlItem.HEX_SLOT)),

                SimpleItems.Entry.flat(ModItems.VACACA.get()),
                SimpleItems.Entry.flat(ModItems.MUSAVACCA_EXUDATE.get()),
                SimpleItems.Entry.flat(ModItems.BANAZO_GUSMA_LUMPA_GOOP.get()),
                SimpleItems.Entry.flat(ModItems.BANANA_PELLIS.get()),
                SimpleItems.Entry.flat(ModItems.OPEN_TELEPORT_TEST_ITEM.get()),
                SimpleItems.Entry.flat(ModItems.OPEN_INVO_TEST_ITEM.get()),
                SimpleItems.Entry.flat(ModItems.POTASSIUM_INGOT.get()),
                SimpleItems.Entry.flat(ModItems.POTASSIUM_UPGRADE_SMITHING_TEMPLATE.get()),

                SimpleItems.Entry.flat(ModBlocks.UNRIPE_MUSAVACCA_EGG.get()),
                SimpleItems.Entry.flat(ModBlocks.RIPENING_MUSAVACCA_EGG.get()),
                SimpleItems.Entry.flat(ModBlocks.RIPE_MUSAVACCA_EGG.get()),

                SimpleItems.Entry.flat(ModItems.MUSAVACCA_PUP.get()),

                SimpleItems.Entry.handheld(ModItems.POTASSIUM_AXE.get()),
                SimpleItems.Entry.handheld(ModItems.POTASSIUM_PICKAXE.get()),
                SimpleItems.Entry.handheld(ModItems.POTASSIUM_SHOVEL.get()),
                SimpleItems.Entry.handheld(ModItems.POTASSIUM_SWORD.get()),
                SimpleItems.Entry.handheld(ModItems.POTASSIUM_HOE.get()),

                SimpleItems.Entry.model(
                        ModItems.BANANA_PHONE.get(),
                        "musavacca:item/banana_phone"
                ),

                SimpleItems.Entry.flat(ModItems.SIM_CARD.get())
                        .pearlTint(PearlTint.dynamic(PearlFireTintProfiles.SIM_CARD_TINT)
                                .itemSlot(SimCardItem.HEX_SLOT)),

                SimpleItems.Entry.flat(ModItems.IMBUED_POTASSIUM_UPGRADE_SMITHING_TEMPLATE.get())
                        .pearlTint(PearlTint.dynamic(PearlFireTintProfiles.IMBUED_POTASSIUM)
                                .itemSlot(VocoTableCrafting.HEX_SLOT_RESULT))
        };
    }

    public static ArmorItems.Entry[] armorItems() {
        return new ArmorItems.Entry[] {
                ArmorItems.of(
                        ModItems.POTASSIUM_HELMET.get(),
                        ModItems.POTASSIUM_CHESTPLATE.get(),
                        ModItems.POTASSIUM_LEGGINGS.get(),
                        ModItems.POTASSIUM_BOOTS.get(),
                        "musavacca:potassium",
                        "musavacca:potassium",
                        "musavacca:item/potassium_helmet_model"
                )
                        .pearlTint(PearlTint.dynamic(PearlFireTintProfiles.IMBUED_POTASSIUM)
                                .itemSlot(VocoTableCrafting.HEX_SLOT_RESULT))
        };
    }

    public static SpawnEggItems.Entry[] spawnEggItems() {
        return new SpawnEggItems.Entry[] {
                SpawnEggItems.of(ModItems.BANANA_COW_SPAWN_EGG.get(), 0xE4C64A, 0x7A4A1F),
                SpawnEggItems.of(ModItems.BASUKE_SPAWN_EGG.get(), 0xE6DCC8, 0x4F3F36)
        };
    }

    public static Map<Block, DecorationBlocks.Models> decorationBlockModels() {
        return Map.of(ModBlocks.BANANA_PEARL_CHALICE.get(), DecorationBlocks.Models.auto());
    }

    public static Block[] smallBananaPearlBlocks() {
        return new Block[] { ModBlocks.SMALL_BANANA_PEARL_BLOCK.get() };
    }

    public static List<DeferredBlock<PearlCandleBlock>> pearlCandleBlocks() {
        return ModBlocks.PEARL_CANDLES;
    }

    public static Block[] stairsBlocks() {
        return new Block[] {};
    }

    public static Block[] slabBlocks() {
        return new Block[] { ModBlocks.MUSAVACCA_SLAB.get() };
    }

    public static Block[] fenceBlocks() {
        return new Block[] { ModBlocks.MUSAVACCA_FENCE.get() };
    }

    public static Block[] cubeCrafterLikeBlocks() {
        return new Block[] {};
    }
}
