package space.anatomyuniverse.musavacca.data.models;

import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.SnowyDirtBlock;

import space.anatomyuniverse.musavacca.block.ModBlocks;
import space.anatomyuniverse.musavacca.block.custom.*;
import space.anatomyuniverse.musavacca.data.models.newgen.*;
import space.anatomyuniverse.musavacca.item.ModItems;
import space.anatomyuniverse.musavacca.tint.PearlFireTintProfiles;
import space.anatomyuniverse.musavacca.tint.MusavaccaTints;

import java.util.List;

public final class NewModelSets {
    private NewModelSets() {}

    public static List<SimpleBlocks.Entry> simpleBlocks() {
        return List.of(
                SimpleBlocks.Entry.builder(ModBlocks.BANANA_PEARL_BLOCK.get())
                        .generated()
                        .textures(textures -> textures.all())
                        .build(),

                SimpleBlocks.Entry.builder(ModBlocks.BANANA_PEARL_BRICKS.get())
                        .generated()
                        .textures(textures -> textures.all())
                        .build(),

                SimpleBlocks.Entry.builder(ModBlocks.MUSAVACCA_PLANKS.get())
                        .generated()
                        .textures(textures -> textures.all())
                        .build(),

                SimpleBlocks.Entry.builder(ModBlocks.CAROTENE_GRASS.get())
                        .models(
                                SimpleBlocks.Model.when(
                                        Models.generated(
                                                ModBlocks.CAROTENE_GRASS.get(),
                                                textures -> textures
                                                        .sides("_side")
                                                        .top("_top")
                                                        .bottom("minecraft:block/dirt")
                                        ),
                                        Conditions.when(SnowyDirtBlock.SNOWY, false)
                                ).variants(Variants.randomY()),

                                SimpleBlocks.Model.when(
                                        Models.generated(
                                                ModBlocks.CAROTENE_GRASS.get(),
                                                "snowy",
                                                textures -> textures
                                                        .sides("_side_snowy")
                                                        .top("_top")
                                                        .bottom("minecraft:block/dirt")
                                        ),
                                        Conditions.when(SnowyDirtBlock.SNOWY, true)
                                )
                        )
                        .item("musavacca:block/carotene_grass")
                        .build(),

                SimpleBlocks.Entry.builder(ModBlocks.BANANA_PEARL_CHAPITER.get())
                        .generated()
                        .textures(textures -> textures
                                .all()
                                .top("_top")
                                .bottom("_bottom"))
                        .rotations(Rotations.shulkerBox())
                        .build(),

                SimpleBlocks.Entry.builder(ModBlocks.BANANA_PEARL_PILLAR.get())
                        .generated()
                        .textures(textures -> textures
                                .all()
                                .ends("_top"))
                        .rotations(Rotations.log())
                        .build(),

                SimpleBlocks.Entry.builder(ModBlocks.MUSAVACCA_STEM.get())
                        .generated()
                        .textures(textures -> textures
                                .all()
                                .ends("_top"))
                        .rotations(Rotations.log())
                        .build(),

                SimpleBlocks.Entry.builder(ModBlocks.STRIPPED_MUSAVACCA_STEM.get())
                        .generated()
                        .textures(textures -> textures
                                .all()
                                .ends("_top"))
                        .rotations(Rotations.log())
                        .build(),

                SimpleBlocks.Entry.builder(ModBlocks.EXUDATED_STRIPPED_MUSAVACCA_STEM.get())
                        .models(
                                SimpleBlocks.Model.when(
                                        Models.generated(
                                                ModBlocks.EXUDATED_STRIPPED_MUSAVACCA_STEM.get(),
                                                textures -> textures
                                                        .all("_0")
                                                        .ends("_top")),
                                        Conditions.when(ExudatedStrippedMusavaccaStemBlock.STAGE, 0)
                                ),
                                SimpleBlocks.Model.when(
                                        Models.generated(
                                                ModBlocks.EXUDATED_STRIPPED_MUSAVACCA_STEM.get(),
                                                "stage1",
                                                textures -> textures
                                                        .all("_1")
                                                        .ends("_top")),
                                        Conditions.when(ExudatedStrippedMusavaccaStemBlock.STAGE, 1)
                                ),
                                SimpleBlocks.Model.when(
                                        Models.generated(
                                                ModBlocks.EXUDATED_STRIPPED_MUSAVACCA_STEM.get(),
                                                "stage2",
                                                textures -> textures
                                                        .all("_2")
                                                        .ends("_top")),
                                        Conditions.when(ExudatedStrippedMusavaccaStemBlock.STAGE, 2)
                                )
                        )
                        .rotations(Rotations.log())
                        .build(),

                SimpleBlocks.Entry.builder(ModBlocks.MUSAVACCA_SPROUT.get())
                        .model("musavacca:block/musavacca_sprout")
                        .tint(Tints.foliage())
                        .noItem()
                        .build(),

                SimpleBlocks.Entry.builder(ModBlocks.MUSAVACCA_SUCKER.get())
                        .model("musavacca:block/musavacca_sucker")
                        .tint(Tints.foliage())
                        .noItem()
                        .build(),

                SimpleBlocks.Entry.builder(ModBlocks.MUSAVACCA_PLANT.get())
                        .model("musavacca:block/musavacca_plant")
                        .tint(Tints.foliage())
                        .noItem()
                        .build(),

                SimpleBlocks.Entry.builder(ModBlocks.MUSAVACCA_PSEUDOSTEM.get())
                        .model("musavacca:block/musavacca_pseudostem")
                        .tint(Tints.foliage())
                        .noItem()
                        .build(),

                SimpleBlocks.Entry.builder(ModBlocks.MUSAVACCA_LEAVES.get())
                        .models(
                                SimpleBlocks.Model.when(
                                        "musavacca:block/musavacca_leaves",
                                        Conditions.when(MusavaccaLeaves.AGE, 0)
                                ),
                                SimpleBlocks.Model.when(
                                        "musavacca:block/musavacca_leaves_crown",
                                        Conditions.when(MusavaccaLeaves.AGE, 1)
                                ),
                                SimpleBlocks.Model.when(
                                        "musavacca:block/musavacca_leaves_cross",
                                        Conditions.when(MusavaccaLeaves.AGE, 2)
                                )
                        )
                        .rotations(Rotations.shulkerBox())
                        .tint(Tints.foliage())
                        .build(),

                SimpleBlocks.Entry.builder(ModBlocks.SMALL_BANANA_PEARL_BLOCK.get())
                        .models(
                                SimpleBlocks.Model.when(
                                        "musavacca:block/small_banana_pearl/small_banana_pearl_one_pearl",
                                        Conditions.when(SmallBananaPearlBlock.SMALL_PEARL_AMOUNT, 1)
                                ),
                                SimpleBlocks.Model.when(
                                        "musavacca:block/small_banana_pearl/small_banana_pearl_two_pearls",
                                        Conditions.when(SmallBananaPearlBlock.SMALL_PEARL_AMOUNT, 2)
                                ),
                                SimpleBlocks.Model.when(
                                        "musavacca:block/small_banana_pearl/small_banana_pearl_three_pearls",
                                        Conditions.when(SmallBananaPearlBlock.SMALL_PEARL_AMOUNT, 3)
                                ),
                                SimpleBlocks.Model.when(
                                        "musavacca:block/small_banana_pearl/small_banana_pearl_four_pearls",
                                        Conditions.when(SmallBananaPearlBlock.SMALL_PEARL_AMOUNT, 4)
                                ),
                                SimpleBlocks.Model.when(
                                        "musavacca:block/small_banana_pearl/small_banana_pearl_five_pearls",
                                        Conditions.when(SmallBananaPearlBlock.SMALL_PEARL_AMOUNT, 5)
                                ),
                                SimpleBlocks.Model.when(
                                        "musavacca:block/small_banana_pearl/small_banana_pearl_six_pearls",
                                        Conditions.when(SmallBananaPearlBlock.SMALL_PEARL_AMOUNT, 6)
                                ),
                                SimpleBlocks.Model.when(
                                        "musavacca:block/small_banana_pearl/small_banana_pearl_seven_pearls",
                                        Conditions.when(SmallBananaPearlBlock.SMALL_PEARL_AMOUNT, 7)
                                ),
                                SimpleBlocks.Model.when(
                                        "musavacca:block/small_banana_pearl/small_banana_pearl_eight_pearls",
                                        Conditions.when(SmallBananaPearlBlock.SMALL_PEARL_AMOUNT, 8)
                                ),
                                SimpleBlocks.Model.when(
                                        "musavacca:block/small_banana_pearl/small_banana_pearl_nine_pearls",
                                        Conditions.when(SmallBananaPearlBlock.SMALL_PEARL_AMOUNT, 9)
                                ),
                                SimpleBlocks.Model.when(
                                        "musavacca:block/small_banana_pearl/small_banana_pearl_ten_pearls",
                                        Conditions.when(SmallBananaPearlBlock.SMALL_PEARL_AMOUNT, 10)
                                ),
                                SimpleBlocks.Model.when(
                                        "musavacca:block/small_banana_pearl/small_banana_pearl_eleven_pearls",
                                        Conditions.when(SmallBananaPearlBlock.SMALL_PEARL_AMOUNT, 11)
                                ),
                                SimpleBlocks.Model.when(
                                        "musavacca:block/small_banana_pearl/small_banana_pearl_twelve_pearls",
                                        Conditions.when(SmallBananaPearlBlock.SMALL_PEARL_AMOUNT, 12)
                                ),
                                SimpleBlocks.Model.when(
                                        "musavacca:block/small_banana_pearl/small_banana_pearl_thirteen_pearls",
                                        Conditions.when(SmallBananaPearlBlock.SMALL_PEARL_AMOUNT, 13)
                                ),
                                SimpleBlocks.Model.when(
                                        "musavacca:block/small_banana_pearl/small_banana_pearl_fourteen_pearls",
                                        Conditions.when(SmallBananaPearlBlock.SMALL_PEARL_AMOUNT, 14)
                                ),
                                SimpleBlocks.Model.when(
                                        "musavacca:block/small_banana_pearl/small_banana_pearl_fifteen_pearls",
                                        Conditions.when(SmallBananaPearlBlock.SMALL_PEARL_AMOUNT, 15)
                                ),
                                SimpleBlocks.Model.when(
                                        "musavacca:block/small_banana_pearl_height3/small_banana_pearl_height3",
                                        Conditions.when(SmallBananaPearlBlock.SMALL_PEARL_AMOUNT, 16)
                                ),
                                SimpleBlocks.Model.when(
                                        "musavacca:block/small_banana_pearl_height3/small_banana_pearl_height3_one_pearl",
                                        Conditions.when(SmallBananaPearlBlock.SMALL_PEARL_AMOUNT, 17)
                                ),
                                SimpleBlocks.Model.when(
                                        "musavacca:block/small_banana_pearl_height3/small_banana_pearl_height3_two_pearls",
                                        Conditions.when(SmallBananaPearlBlock.SMALL_PEARL_AMOUNT, 18)
                                ),
                                SimpleBlocks.Model.when(
                                        "musavacca:block/small_banana_pearl_height3/small_banana_pearl_height3_three_pearls",
                                        Conditions.when(SmallBananaPearlBlock.SMALL_PEARL_AMOUNT, 19)
                                ),
                                SimpleBlocks.Model.when(
                                        "musavacca:block/small_banana_pearl_height3/small_banana_pearl_height3_four_pearls",
                                        Conditions.when(SmallBananaPearlBlock.SMALL_PEARL_AMOUNT, 20)
                                ),
                                SimpleBlocks.Model.when(
                                        "musavacca:block/small_banana_pearl_height3/small_banana_pearl_height3_five_pearls",
                                        Conditions.when(SmallBananaPearlBlock.SMALL_PEARL_AMOUNT, 21)
                                ),
                                SimpleBlocks.Model.when(
                                        "musavacca:block/small_banana_pearl_height3/small_banana_pearl_height3_six_pearls",
                                        Conditions.when(SmallBananaPearlBlock.SMALL_PEARL_AMOUNT, 22)
                                ),
                                SimpleBlocks.Model.when(
                                        "musavacca:block/small_banana_pearl_height3/small_banana_pearl_height3_seven_pearls",
                                        Conditions.when(SmallBananaPearlBlock.SMALL_PEARL_AMOUNT, 23)
                                ),
                                SimpleBlocks.Model.when(
                                        "musavacca:block/small_banana_pearl_height3/small_banana_pearl_height3_eight_pearls",
                                        Conditions.when(SmallBananaPearlBlock.SMALL_PEARL_AMOUNT, 24)
                                ),
                                SimpleBlocks.Model.when(
                                        "musavacca:block/small_banana_pearl_height3/small_banana_pearl_height3_nine_pearls",
                                        Conditions.when(SmallBananaPearlBlock.SMALL_PEARL_AMOUNT, 25)
                                ),
                                SimpleBlocks.Model.when(
                                        "musavacca:block/small_banana_pearl_height3/small_banana_pearl_height3_ten_pearls",
                                        Conditions.when(SmallBananaPearlBlock.SMALL_PEARL_AMOUNT, 26)
                                ),
                                SimpleBlocks.Model.when(
                                        "musavacca:block/small_banana_pearl_height3/small_banana_pearl_height3_eleven_pearls",
                                        Conditions.when(SmallBananaPearlBlock.SMALL_PEARL_AMOUNT, 27)
                                ),
                                SimpleBlocks.Model.when(
                                        "musavacca:block/small_banana_pearl_height3/small_banana_pearl_height3_twelve_pearls",
                                        Conditions.when(SmallBananaPearlBlock.SMALL_PEARL_AMOUNT, 28)
                                ),
                                SimpleBlocks.Model.when(
                                        "musavacca:block/small_banana_pearl_height3/small_banana_pearl_height3_thirteen_pearls",
                                        Conditions.when(SmallBananaPearlBlock.SMALL_PEARL_AMOUNT, 29)
                                ),
                                SimpleBlocks.Model.when(
                                        "musavacca:block/small_banana_pearl_height3/small_banana_pearl_height3_fourteen_pearls",
                                        Conditions.when(SmallBananaPearlBlock.SMALL_PEARL_AMOUNT, 30)
                                ),
                                SimpleBlocks.Model.when(
                                        "musavacca:block/small_banana_pearl_height3/small_banana_pearl_height3_fifteen_pearls",
                                        Conditions.when(SmallBananaPearlBlock.SMALL_PEARL_AMOUNT, 31)
                                ),
                                SimpleBlocks.Model.when(
                                        "musavacca:block/small_banana_pearl_height6/small_banana_pearl_height6",
                                        Conditions.when(SmallBananaPearlBlock.SMALL_PEARL_AMOUNT, 32)
                                ),
                                SimpleBlocks.Model.when(
                                        "musavacca:block/small_banana_pearl_height6/small_banana_pearl_height6_one_pearl",
                                        Conditions.when(SmallBananaPearlBlock.SMALL_PEARL_AMOUNT, 33)
                                ),
                                SimpleBlocks.Model.when(
                                        "musavacca:block/small_banana_pearl_height6/small_banana_pearl_height6_two_pearls",
                                        Conditions.when(SmallBananaPearlBlock.SMALL_PEARL_AMOUNT, 34)
                                ),
                                SimpleBlocks.Model.when(
                                        "musavacca:block/small_banana_pearl_height6/small_banana_pearl_height6_three_pearls",
                                        Conditions.when(SmallBananaPearlBlock.SMALL_PEARL_AMOUNT, 35)
                                ),
                                SimpleBlocks.Model.when(
                                        "musavacca:block/small_banana_pearl_height6/small_banana_pearl_height6_four_pearls",
                                        Conditions.when(SmallBananaPearlBlock.SMALL_PEARL_AMOUNT, 36)
                                ),
                                SimpleBlocks.Model.when(
                                        "musavacca:block/small_banana_pearl_height6/small_banana_pearl_height6_five_pearls",
                                        Conditions.when(SmallBananaPearlBlock.SMALL_PEARL_AMOUNT, 37)
                                ),
                                SimpleBlocks.Model.when(
                                        "musavacca:block/small_banana_pearl_height6/small_banana_pearl_height6_six_pearls",
                                        Conditions.when(SmallBananaPearlBlock.SMALL_PEARL_AMOUNT, 38)
                                ),
                                SimpleBlocks.Model.when(
                                        "musavacca:block/small_banana_pearl_height6/small_banana_pearl_height6_seven_pearls",
                                        Conditions.when(SmallBananaPearlBlock.SMALL_PEARL_AMOUNT, 39)
                                ),
                                SimpleBlocks.Model.when(
                                        "musavacca:block/small_banana_pearl_height6/small_banana_pearl_height6_eight_pearls",
                                        Conditions.when(SmallBananaPearlBlock.SMALL_PEARL_AMOUNT, 40)
                                ),
                                SimpleBlocks.Model.when(
                                        "musavacca:block/small_banana_pearl_height6/small_banana_pearl_height6_nine_pearls",
                                        Conditions.when(SmallBananaPearlBlock.SMALL_PEARL_AMOUNT, 41)
                                ),
                                SimpleBlocks.Model.when(
                                        "musavacca:block/small_banana_pearl_height6/small_banana_pearl_height6_ten_pearls",
                                        Conditions.when(SmallBananaPearlBlock.SMALL_PEARL_AMOUNT, 42)
                                ),
                                SimpleBlocks.Model.when(
                                        "musavacca:block/small_banana_pearl_height6/small_banana_pearl_height6_eleven_pearls",
                                        Conditions.when(SmallBananaPearlBlock.SMALL_PEARL_AMOUNT, 43)
                                ),
                                SimpleBlocks.Model.when(
                                        "musavacca:block/small_banana_pearl_height6/small_banana_pearl_height6_twelve_pearls",
                                        Conditions.when(SmallBananaPearlBlock.SMALL_PEARL_AMOUNT, 44)
                                ),
                                SimpleBlocks.Model.when(
                                        "musavacca:block/small_banana_pearl_height6/small_banana_pearl_height6_thirteen_pearls",
                                        Conditions.when(SmallBananaPearlBlock.SMALL_PEARL_AMOUNT, 45)
                                ),
                                SimpleBlocks.Model.when(
                                        "musavacca:block/small_banana_pearl_height6/small_banana_pearl_height6_fourteen_pearls",
                                        Conditions.when(SmallBananaPearlBlock.SMALL_PEARL_AMOUNT, 46)
                                ),
                                SimpleBlocks.Model.when(
                                        "musavacca:block/small_banana_pearl_height6/small_banana_pearl_height6_fifteen_pearls",
                                        Conditions.when(SmallBananaPearlBlock.SMALL_PEARL_AMOUNT, 47)
                                ),
                                SimpleBlocks.Model.when(
                                        "musavacca:block/small_banana_pearl_height9/small_banana_pearl_height9",
                                        Conditions.when(SmallBananaPearlBlock.SMALL_PEARL_AMOUNT, 48)
                                ),
                                SimpleBlocks.Model.when(
                                        "musavacca:block/small_banana_pearl_height9/small_banana_pearl_height9_one_pearl",
                                        Conditions.when(SmallBananaPearlBlock.SMALL_PEARL_AMOUNT, 49)
                                ),
                                SimpleBlocks.Model.when(
                                        "musavacca:block/small_banana_pearl_height9/small_banana_pearl_height9_two_pearls",
                                        Conditions.when(SmallBananaPearlBlock.SMALL_PEARL_AMOUNT, 50)
                                ),
                                SimpleBlocks.Model.when(
                                        "musavacca:block/small_banana_pearl_height9/small_banana_pearl_height9_three_pearls",
                                        Conditions.when(SmallBananaPearlBlock.SMALL_PEARL_AMOUNT, 51)
                                ),
                                SimpleBlocks.Model.when(
                                        "musavacca:block/small_banana_pearl_height9/small_banana_pearl_height9_four_pearls",
                                        Conditions.when(SmallBananaPearlBlock.SMALL_PEARL_AMOUNT, 52)
                                ),
                                SimpleBlocks.Model.when(
                                        "musavacca:block/small_banana_pearl_height9/small_banana_pearl_height9_five_pearls",
                                        Conditions.when(SmallBananaPearlBlock.SMALL_PEARL_AMOUNT, 53)
                                ),
                                SimpleBlocks.Model.when(
                                        "musavacca:block/small_banana_pearl_height9/small_banana_pearl_height9_six_pearls",
                                        Conditions.when(SmallBananaPearlBlock.SMALL_PEARL_AMOUNT, 54)
                                ),
                                SimpleBlocks.Model.when(
                                        "musavacca:block/small_banana_pearl_height9/small_banana_pearl_height9_seven_pearls",
                                        Conditions.when(SmallBananaPearlBlock.SMALL_PEARL_AMOUNT, 55)
                                ),
                                SimpleBlocks.Model.when(
                                        "musavacca:block/small_banana_pearl_height9/small_banana_pearl_height9_eight_pearls",
                                        Conditions.when(SmallBananaPearlBlock.SMALL_PEARL_AMOUNT, 56)
                                ),
                                SimpleBlocks.Model.when(
                                        "musavacca:block/small_banana_pearl_height9/small_banana_pearl_height9_nine_pearls",
                                        Conditions.when(SmallBananaPearlBlock.SMALL_PEARL_AMOUNT, 57)
                                ),
                                SimpleBlocks.Model.when(
                                        "musavacca:block/small_banana_pearl_height9/small_banana_pearl_height9_ten_pearls",
                                        Conditions.when(SmallBananaPearlBlock.SMALL_PEARL_AMOUNT, 58)
                                ),
                                SimpleBlocks.Model.when(
                                        "musavacca:block/small_banana_pearl_height9/small_banana_pearl_height9_eleven_pearls",
                                        Conditions.when(SmallBananaPearlBlock.SMALL_PEARL_AMOUNT, 59)
                                ),
                                SimpleBlocks.Model.when(
                                        "musavacca:block/small_banana_pearl_height9/small_banana_pearl_height9_twelve_pearls",
                                        Conditions.when(SmallBananaPearlBlock.SMALL_PEARL_AMOUNT, 60)
                                ),
                                SimpleBlocks.Model.when(
                                        "musavacca:block/small_banana_pearl_height9/small_banana_pearl_height9_thirteen_pearls",
                                        Conditions.when(SmallBananaPearlBlock.SMALL_PEARL_AMOUNT, 61)
                                ),
                                SimpleBlocks.Model.when(
                                        "musavacca:block/small_banana_pearl_height9/small_banana_pearl_height9_fourteen_pearls",
                                        Conditions.when(SmallBananaPearlBlock.SMALL_PEARL_AMOUNT, 62)
                                ),
                                SimpleBlocks.Model.when(
                                        "musavacca:block/small_banana_pearl_height9/small_banana_pearl_height9_fifteen_pearls",
                                        Conditions.when(SmallBananaPearlBlock.SMALL_PEARL_AMOUNT, 63)
                                ),
                                SimpleBlocks.Model.when(
                                        "musavacca:block/small_banana_pearl_height12/small_banana_pearl_height12",
                                        Conditions.when(SmallBananaPearlBlock.SMALL_PEARL_AMOUNT, 64)
                                ),
                                SimpleBlocks.Model.when(
                                        "musavacca:block/small_banana_pearl_height12/small_banana_pearl_height12_one_pearl",
                                        Conditions.when(SmallBananaPearlBlock.SMALL_PEARL_AMOUNT, 65)
                                ),
                                SimpleBlocks.Model.when(
                                        "musavacca:block/small_banana_pearl_height12/small_banana_pearl_height12_two_pearls",
                                        Conditions.when(SmallBananaPearlBlock.SMALL_PEARL_AMOUNT, 66)
                                ),
                                SimpleBlocks.Model.when(
                                        "musavacca:block/small_banana_pearl_height12/small_banana_pearl_height12_three_pearls",
                                        Conditions.when(SmallBananaPearlBlock.SMALL_PEARL_AMOUNT, 67)
                                ),
                                SimpleBlocks.Model.when(
                                        "musavacca:block/small_banana_pearl_height12/small_banana_pearl_height12_four_pearls",
                                        Conditions.when(SmallBananaPearlBlock.SMALL_PEARL_AMOUNT, 68)
                                ),
                                SimpleBlocks.Model.when(
                                        "musavacca:block/small_banana_pearl_height12/small_banana_pearl_height12_five_pearls",
                                        Conditions.when(SmallBananaPearlBlock.SMALL_PEARL_AMOUNT, 69)
                                ),
                                SimpleBlocks.Model.when(
                                        "musavacca:block/small_banana_pearl_height12/small_banana_pearl_height12_six_pearls",
                                        Conditions.when(SmallBananaPearlBlock.SMALL_PEARL_AMOUNT, 70)
                                ),
                                SimpleBlocks.Model.when(
                                        "musavacca:block/small_banana_pearl_height12/small_banana_pearl_height12_seven_pearls",
                                        Conditions.when(SmallBananaPearlBlock.SMALL_PEARL_AMOUNT, 71)
                                ),
                                SimpleBlocks.Model.when(
                                        "musavacca:block/small_banana_pearl_height12/small_banana_pearl_height12_eight_pearls",
                                        Conditions.when(SmallBananaPearlBlock.SMALL_PEARL_AMOUNT, 72)
                                ),
                                SimpleBlocks.Model.when(
                                        "musavacca:block/small_banana_pearl_height12/small_banana_pearl_height12_nine_pearls",
                                        Conditions.when(SmallBananaPearlBlock.SMALL_PEARL_AMOUNT, 73)
                                ),
                                SimpleBlocks.Model.when(
                                        "musavacca:block/small_banana_pearl_height12/small_banana_pearl_height12_ten_pearls",
                                        Conditions.when(SmallBananaPearlBlock.SMALL_PEARL_AMOUNT, 74)
                                ),
                                SimpleBlocks.Model.when(
                                        "musavacca:block/small_banana_pearl_height12/small_banana_pearl_height12_eleven_pearls",
                                        Conditions.when(SmallBananaPearlBlock.SMALL_PEARL_AMOUNT, 75)
                                ),
                                SimpleBlocks.Model.when(
                                        "musavacca:block/small_banana_pearl_height12/small_banana_pearl_height12_twelve_pearls",
                                        Conditions.when(SmallBananaPearlBlock.SMALL_PEARL_AMOUNT, 76)
                                ),
                                SimpleBlocks.Model.when(
                                        "musavacca:block/small_banana_pearl_height12/small_banana_pearl_height12_thirteen_pearls",
                                        Conditions.when(SmallBananaPearlBlock.SMALL_PEARL_AMOUNT, 77)
                                ),
                                SimpleBlocks.Model.when(
                                        "musavacca:block/small_banana_pearl_height12/small_banana_pearl_height12_fourteen_pearls",
                                        Conditions.when(SmallBananaPearlBlock.SMALL_PEARL_AMOUNT, 78)
                                ),
                                SimpleBlocks.Model.when(
                                        "musavacca:block/small_banana_pearl_height12/small_banana_pearl_height12_fifteen_pearls",
                                        Conditions.when(SmallBananaPearlBlock.SMALL_PEARL_AMOUNT, 79)
                                ),
                                SimpleBlocks.Model.when(
                                        "musavacca:block/small_banana_pearl_block",
                                        Conditions.when(SmallBananaPearlBlock.SMALL_PEARL_AMOUNT, 80)
                                )
                        )
                        .rotations(Rotations.furnace())
                        .item("musavacca:block/small_banana_pearl_block")
                        .build(),

                SimpleBlocks.Entry.builder(ModBlocks.HEX_BLOCK.get())
                        .models(
                                SimpleBlocks.Model.when(
                                        "musavacca:block/lopha_blossom",
                                        Conditions.when(HexBlock.CLIPPED, false)
                                ),
                                SimpleBlocks.Model.when(
                                        "musavacca:block/clipped_lopha_blossom",
                                        Conditions.when(HexBlock.CLIPPED, true)
                                )
                        )
                        .tint(MusavaccaTints.hexColor())
                        .build(),

                SimpleBlocks.Entry.builder(ModBlocks.HARD_HEX_BLOCK.get())
                        .model("musavacca:block/hex_block")
                        .tint(Tints.constant(MusavaccaTints.DEFAULT_TINT))
                        .build(),

                SimpleBlocks.Entry.builder(ModBlocks.MUSAVACCA_EGG.get())
                        .models(
                                SimpleBlocks.Model.when(
                                        "musavacca:block/musavacca_egg/musavacca_egg_stage0",
                                        Conditions.when(BreakBlock.AGE, 0)
                                                .and(BreakBlock.ATTACHED, false)
                                ),
                                SimpleBlocks.Model.when(
                                        "musavacca:block/musavacca_egg/musavacca_egg_stage1",
                                        Conditions.when(BreakBlock.AGE, 1)
                                                .and(BreakBlock.ATTACHED, false)
                                ),
                                SimpleBlocks.Model.when(
                                        "musavacca:block/musavacca_egg/musavacca_egg_stage2",
                                        Conditions.when(BreakBlock.AGE, 2)
                                                .and(BreakBlock.ATTACHED, false)
                                ),
                                SimpleBlocks.Model.when(
                                        "musavacca:block/musavacca_egg/musavacca_egg_attached_stage0",
                                        Conditions.when(BreakBlock.AGE, 0)
                                                .and(BreakBlock.ATTACHED, true)
                                ),
                                SimpleBlocks.Model.when(
                                        "musavacca:block/musavacca_egg/musavacca_egg_attached_stage1",
                                        Conditions.when(BreakBlock.AGE, 1)
                                                .and(BreakBlock.ATTACHED, true)
                                ),
                                SimpleBlocks.Model.when(
                                        "musavacca:block/musavacca_egg/musavacca_egg_attached_stage2",
                                        Conditions.when(BreakBlock.AGE, 2)
                                                .and(BreakBlock.ATTACHED, true)
                                )
                        )
                        .noItem()
                        .build(),

                SimpleBlocks.Entry.builder(ModBlocks.VOCO_POST.get())
                        .multipart(
                                SimpleBlocks.Part.always("musavacca:block/voco_post"),
                                SimpleBlocks.Part.when(
                                        "musavacca:block/voco_post_lit_receptor_corner",
                                        Conditions.when(VocoPostBlock.LIT, true)
                                ),
                                SimpleBlocks.Part.when(
                                        "musavacca:block/voco_post_portal",
                                        Conditions.when(VocoPostBlock.PORTAL, true)
                                ).tint(MusavaccaTints.pearlFire(PearlFireTintProfiles.PORTAL_BLOCK))
                        )
                        .rotations(Rotations.furnace())
                        .build(),

                SimpleBlocks.Entry.builder(ModBlocks.PEARL_CANDLE.get())
                        .models(
                                SimpleBlocks.Model.when("minecraft:block/candle_one_candle", Conditions.when(CandleBlock.CANDLES, 1).and(CandleBlock.LIT, false)),
                                SimpleBlocks.Model.when("minecraft:block/candle_one_candle_lit", Conditions.when(CandleBlock.CANDLES, 1).and(CandleBlock.LIT, true)),
                                SimpleBlocks.Model.when("minecraft:block/candle_two_candles", Conditions.when(CandleBlock.CANDLES, 2).and(CandleBlock.LIT, false)),
                                SimpleBlocks.Model.when("minecraft:block/candle_two_candles_lit", Conditions.when(CandleBlock.CANDLES, 2).and(CandleBlock.LIT, true)),
                                SimpleBlocks.Model.when("minecraft:block/candle_three_candles", Conditions.when(CandleBlock.CANDLES, 3).and(CandleBlock.LIT, false)),
                                SimpleBlocks.Model.when("minecraft:block/candle_three_candles_lit", Conditions.when(CandleBlock.CANDLES, 3).and(CandleBlock.LIT, true)),
                                SimpleBlocks.Model.when("minecraft:block/candle_four_candles", Conditions.when(CandleBlock.CANDLES, 4).and(CandleBlock.LIT, false)),
                                SimpleBlocks.Model.when("minecraft:block/candle_four_candles_lit", Conditions.when(CandleBlock.CANDLES, 4).and(CandleBlock.LIT, true))
                        )
                        .noItem()
                        .build(),

                SimpleBlocks.Entry.builder(ModBlocks.PEARL_WHITE_CANDLE.get())
                        .models(
                                SimpleBlocks.Model.when("minecraft:block/white_candle_one_candle", Conditions.when(CandleBlock.CANDLES, 1).and(CandleBlock.LIT, false)),
                                SimpleBlocks.Model.when("minecraft:block/white_candle_one_candle_lit", Conditions.when(CandleBlock.CANDLES, 1).and(CandleBlock.LIT, true)),
                                SimpleBlocks.Model.when("minecraft:block/white_candle_two_candles", Conditions.when(CandleBlock.CANDLES, 2).and(CandleBlock.LIT, false)),
                                SimpleBlocks.Model.when("minecraft:block/white_candle_two_candles_lit", Conditions.when(CandleBlock.CANDLES, 2).and(CandleBlock.LIT, true)),
                                SimpleBlocks.Model.when("minecraft:block/white_candle_three_candles", Conditions.when(CandleBlock.CANDLES, 3).and(CandleBlock.LIT, false)),
                                SimpleBlocks.Model.when("minecraft:block/white_candle_three_candles_lit", Conditions.when(CandleBlock.CANDLES, 3).and(CandleBlock.LIT, true)),
                                SimpleBlocks.Model.when("minecraft:block/white_candle_four_candles", Conditions.when(CandleBlock.CANDLES, 4).and(CandleBlock.LIT, false)),
                                SimpleBlocks.Model.when("minecraft:block/white_candle_four_candles_lit", Conditions.when(CandleBlock.CANDLES, 4).and(CandleBlock.LIT, true))
                        )
                        .noItem()
                        .build(),

                SimpleBlocks.Entry.builder(ModBlocks.PEARL_ORANGE_CANDLE.get())
                        .models(
                                SimpleBlocks.Model.when("minecraft:block/orange_candle_one_candle", Conditions.when(CandleBlock.CANDLES, 1).and(CandleBlock.LIT, false)),
                                SimpleBlocks.Model.when("minecraft:block/orange_candle_one_candle_lit", Conditions.when(CandleBlock.CANDLES, 1).and(CandleBlock.LIT, true)),
                                SimpleBlocks.Model.when("minecraft:block/orange_candle_two_candles", Conditions.when(CandleBlock.CANDLES, 2).and(CandleBlock.LIT, false)),
                                SimpleBlocks.Model.when("minecraft:block/orange_candle_two_candles_lit", Conditions.when(CandleBlock.CANDLES, 2).and(CandleBlock.LIT, true)),
                                SimpleBlocks.Model.when("minecraft:block/orange_candle_three_candles", Conditions.when(CandleBlock.CANDLES, 3).and(CandleBlock.LIT, false)),
                                SimpleBlocks.Model.when("minecraft:block/orange_candle_three_candles_lit", Conditions.when(CandleBlock.CANDLES, 3).and(CandleBlock.LIT, true)),
                                SimpleBlocks.Model.when("minecraft:block/orange_candle_four_candles", Conditions.when(CandleBlock.CANDLES, 4).and(CandleBlock.LIT, false)),
                                SimpleBlocks.Model.when("minecraft:block/orange_candle_four_candles_lit", Conditions.when(CandleBlock.CANDLES, 4).and(CandleBlock.LIT, true))
                        )
                        .noItem()
                        .build(),

                SimpleBlocks.Entry.builder(ModBlocks.PEARL_MAGENTA_CANDLE.get())
                        .models(
                                SimpleBlocks.Model.when("minecraft:block/magenta_candle_one_candle", Conditions.when(CandleBlock.CANDLES, 1).and(CandleBlock.LIT, false)),
                                SimpleBlocks.Model.when("minecraft:block/magenta_candle_one_candle_lit", Conditions.when(CandleBlock.CANDLES, 1).and(CandleBlock.LIT, true)),
                                SimpleBlocks.Model.when("minecraft:block/magenta_candle_two_candles", Conditions.when(CandleBlock.CANDLES, 2).and(CandleBlock.LIT, false)),
                                SimpleBlocks.Model.when("minecraft:block/magenta_candle_two_candles_lit", Conditions.when(CandleBlock.CANDLES, 2).and(CandleBlock.LIT, true)),
                                SimpleBlocks.Model.when("minecraft:block/magenta_candle_three_candles", Conditions.when(CandleBlock.CANDLES, 3).and(CandleBlock.LIT, false)),
                                SimpleBlocks.Model.when("minecraft:block/magenta_candle_three_candles_lit", Conditions.when(CandleBlock.CANDLES, 3).and(CandleBlock.LIT, true)),
                                SimpleBlocks.Model.when("minecraft:block/magenta_candle_four_candles", Conditions.when(CandleBlock.CANDLES, 4).and(CandleBlock.LIT, false)),
                                SimpleBlocks.Model.when("minecraft:block/magenta_candle_four_candles_lit", Conditions.when(CandleBlock.CANDLES, 4).and(CandleBlock.LIT, true))
                        )
                        .noItem()
                        .build(),

                SimpleBlocks.Entry.builder(ModBlocks.PEARL_LIGHT_BLUE_CANDLE.get())
                        .models(
                                SimpleBlocks.Model.when("minecraft:block/light_blue_candle_one_candle", Conditions.when(CandleBlock.CANDLES, 1).and(CandleBlock.LIT, false)),
                                SimpleBlocks.Model.when("minecraft:block/light_blue_candle_one_candle_lit", Conditions.when(CandleBlock.CANDLES, 1).and(CandleBlock.LIT, true)),
                                SimpleBlocks.Model.when("minecraft:block/light_blue_candle_two_candles", Conditions.when(CandleBlock.CANDLES, 2).and(CandleBlock.LIT, false)),
                                SimpleBlocks.Model.when("minecraft:block/light_blue_candle_two_candles_lit", Conditions.when(CandleBlock.CANDLES, 2).and(CandleBlock.LIT, true)),
                                SimpleBlocks.Model.when("minecraft:block/light_blue_candle_three_candles", Conditions.when(CandleBlock.CANDLES, 3).and(CandleBlock.LIT, false)),
                                SimpleBlocks.Model.when("minecraft:block/light_blue_candle_three_candles_lit", Conditions.when(CandleBlock.CANDLES, 3).and(CandleBlock.LIT, true)),
                                SimpleBlocks.Model.when("minecraft:block/light_blue_candle_four_candles", Conditions.when(CandleBlock.CANDLES, 4).and(CandleBlock.LIT, false)),
                                SimpleBlocks.Model.when("minecraft:block/light_blue_candle_four_candles_lit", Conditions.when(CandleBlock.CANDLES, 4).and(CandleBlock.LIT, true))
                        )
                        .noItem()
                        .build(),

                SimpleBlocks.Entry.builder(ModBlocks.PEARL_YELLOW_CANDLE.get())
                        .models(
                                SimpleBlocks.Model.when("minecraft:block/yellow_candle_one_candle", Conditions.when(CandleBlock.CANDLES, 1).and(CandleBlock.LIT, false)),
                                SimpleBlocks.Model.when("minecraft:block/yellow_candle_one_candle_lit", Conditions.when(CandleBlock.CANDLES, 1).and(CandleBlock.LIT, true)),
                                SimpleBlocks.Model.when("minecraft:block/yellow_candle_two_candles", Conditions.when(CandleBlock.CANDLES, 2).and(CandleBlock.LIT, false)),
                                SimpleBlocks.Model.when("minecraft:block/yellow_candle_two_candles_lit", Conditions.when(CandleBlock.CANDLES, 2).and(CandleBlock.LIT, true)),
                                SimpleBlocks.Model.when("minecraft:block/yellow_candle_three_candles", Conditions.when(CandleBlock.CANDLES, 3).and(CandleBlock.LIT, false)),
                                SimpleBlocks.Model.when("minecraft:block/yellow_candle_three_candles_lit", Conditions.when(CandleBlock.CANDLES, 3).and(CandleBlock.LIT, true)),
                                SimpleBlocks.Model.when("minecraft:block/yellow_candle_four_candles", Conditions.when(CandleBlock.CANDLES, 4).and(CandleBlock.LIT, false)),
                                SimpleBlocks.Model.when("minecraft:block/yellow_candle_four_candles_lit", Conditions.when(CandleBlock.CANDLES, 4).and(CandleBlock.LIT, true))
                        )
                        .noItem()
                        .build(),

                SimpleBlocks.Entry.builder(ModBlocks.PEARL_LIME_CANDLE.get())
                        .models(
                                SimpleBlocks.Model.when("minecraft:block/lime_candle_one_candle", Conditions.when(CandleBlock.CANDLES, 1).and(CandleBlock.LIT, false)),
                                SimpleBlocks.Model.when("minecraft:block/lime_candle_one_candle_lit", Conditions.when(CandleBlock.CANDLES, 1).and(CandleBlock.LIT, true)),
                                SimpleBlocks.Model.when("minecraft:block/lime_candle_two_candles", Conditions.when(CandleBlock.CANDLES, 2).and(CandleBlock.LIT, false)),
                                SimpleBlocks.Model.when("minecraft:block/lime_candle_two_candles_lit", Conditions.when(CandleBlock.CANDLES, 2).and(CandleBlock.LIT, true)),
                                SimpleBlocks.Model.when("minecraft:block/lime_candle_three_candles", Conditions.when(CandleBlock.CANDLES, 3).and(CandleBlock.LIT, false)),
                                SimpleBlocks.Model.when("minecraft:block/lime_candle_three_candles_lit", Conditions.when(CandleBlock.CANDLES, 3).and(CandleBlock.LIT, true)),
                                SimpleBlocks.Model.when("minecraft:block/lime_candle_four_candles", Conditions.when(CandleBlock.CANDLES, 4).and(CandleBlock.LIT, false)),
                                SimpleBlocks.Model.when("minecraft:block/lime_candle_four_candles_lit", Conditions.when(CandleBlock.CANDLES, 4).and(CandleBlock.LIT, true))
                        )
                        .noItem()
                        .build(),

                SimpleBlocks.Entry.builder(ModBlocks.PEARL_PINK_CANDLE.get())
                        .models(
                                SimpleBlocks.Model.when("minecraft:block/pink_candle_one_candle", Conditions.when(CandleBlock.CANDLES, 1).and(CandleBlock.LIT, false)),
                                SimpleBlocks.Model.when("minecraft:block/pink_candle_one_candle_lit", Conditions.when(CandleBlock.CANDLES, 1).and(CandleBlock.LIT, true)),
                                SimpleBlocks.Model.when("minecraft:block/pink_candle_two_candles", Conditions.when(CandleBlock.CANDLES, 2).and(CandleBlock.LIT, false)),
                                SimpleBlocks.Model.when("minecraft:block/pink_candle_two_candles_lit", Conditions.when(CandleBlock.CANDLES, 2).and(CandleBlock.LIT, true)),
                                SimpleBlocks.Model.when("minecraft:block/pink_candle_three_candles", Conditions.when(CandleBlock.CANDLES, 3).and(CandleBlock.LIT, false)),
                                SimpleBlocks.Model.when("minecraft:block/pink_candle_three_candles_lit", Conditions.when(CandleBlock.CANDLES, 3).and(CandleBlock.LIT, true)),
                                SimpleBlocks.Model.when("minecraft:block/pink_candle_four_candles", Conditions.when(CandleBlock.CANDLES, 4).and(CandleBlock.LIT, false)),
                                SimpleBlocks.Model.when("minecraft:block/pink_candle_four_candles_lit", Conditions.when(CandleBlock.CANDLES, 4).and(CandleBlock.LIT, true))
                        )
                        .noItem()
                        .build(),

                SimpleBlocks.Entry.builder(ModBlocks.PEARL_GRAY_CANDLE.get())
                        .models(
                                SimpleBlocks.Model.when("minecraft:block/gray_candle_one_candle", Conditions.when(CandleBlock.CANDLES, 1).and(CandleBlock.LIT, false)),
                                SimpleBlocks.Model.when("minecraft:block/gray_candle_one_candle_lit", Conditions.when(CandleBlock.CANDLES, 1).and(CandleBlock.LIT, true)),
                                SimpleBlocks.Model.when("minecraft:block/gray_candle_two_candles", Conditions.when(CandleBlock.CANDLES, 2).and(CandleBlock.LIT, false)),
                                SimpleBlocks.Model.when("minecraft:block/gray_candle_two_candles_lit", Conditions.when(CandleBlock.CANDLES, 2).and(CandleBlock.LIT, true)),
                                SimpleBlocks.Model.when("minecraft:block/gray_candle_three_candles", Conditions.when(CandleBlock.CANDLES, 3).and(CandleBlock.LIT, false)),
                                SimpleBlocks.Model.when("minecraft:block/gray_candle_three_candles_lit", Conditions.when(CandleBlock.CANDLES, 3).and(CandleBlock.LIT, true)),
                                SimpleBlocks.Model.when("minecraft:block/gray_candle_four_candles", Conditions.when(CandleBlock.CANDLES, 4).and(CandleBlock.LIT, false)),
                                SimpleBlocks.Model.when("minecraft:block/gray_candle_four_candles_lit", Conditions.when(CandleBlock.CANDLES, 4).and(CandleBlock.LIT, true))
                        )
                        .noItem()
                        .build(),

                SimpleBlocks.Entry.builder(ModBlocks.PEARL_LIGHT_GRAY_CANDLE.get())
                        .models(
                                SimpleBlocks.Model.when("minecraft:block/light_gray_candle_one_candle", Conditions.when(CandleBlock.CANDLES, 1).and(CandleBlock.LIT, false)),
                                SimpleBlocks.Model.when("minecraft:block/light_gray_candle_one_candle_lit", Conditions.when(CandleBlock.CANDLES, 1).and(CandleBlock.LIT, true)),
                                SimpleBlocks.Model.when("minecraft:block/light_gray_candle_two_candles", Conditions.when(CandleBlock.CANDLES, 2).and(CandleBlock.LIT, false)),
                                SimpleBlocks.Model.when("minecraft:block/light_gray_candle_two_candles_lit", Conditions.when(CandleBlock.CANDLES, 2).and(CandleBlock.LIT, true)),
                                SimpleBlocks.Model.when("minecraft:block/light_gray_candle_three_candles", Conditions.when(CandleBlock.CANDLES, 3).and(CandleBlock.LIT, false)),
                                SimpleBlocks.Model.when("minecraft:block/light_gray_candle_three_candles_lit", Conditions.when(CandleBlock.CANDLES, 3).and(CandleBlock.LIT, true)),
                                SimpleBlocks.Model.when("minecraft:block/light_gray_candle_four_candles", Conditions.when(CandleBlock.CANDLES, 4).and(CandleBlock.LIT, false)),
                                SimpleBlocks.Model.when("minecraft:block/light_gray_candle_four_candles_lit", Conditions.when(CandleBlock.CANDLES, 4).and(CandleBlock.LIT, true))
                        )
                        .noItem()
                        .build(),

                SimpleBlocks.Entry.builder(ModBlocks.PEARL_CYAN_CANDLE.get())
                        .models(
                                SimpleBlocks.Model.when("minecraft:block/cyan_candle_one_candle", Conditions.when(CandleBlock.CANDLES, 1).and(CandleBlock.LIT, false)),
                                SimpleBlocks.Model.when("minecraft:block/cyan_candle_one_candle_lit", Conditions.when(CandleBlock.CANDLES, 1).and(CandleBlock.LIT, true)),
                                SimpleBlocks.Model.when("minecraft:block/cyan_candle_two_candles", Conditions.when(CandleBlock.CANDLES, 2).and(CandleBlock.LIT, false)),
                                SimpleBlocks.Model.when("minecraft:block/cyan_candle_two_candles_lit", Conditions.when(CandleBlock.CANDLES, 2).and(CandleBlock.LIT, true)),
                                SimpleBlocks.Model.when("minecraft:block/cyan_candle_three_candles", Conditions.when(CandleBlock.CANDLES, 3).and(CandleBlock.LIT, false)),
                                SimpleBlocks.Model.when("minecraft:block/cyan_candle_three_candles_lit", Conditions.when(CandleBlock.CANDLES, 3).and(CandleBlock.LIT, true)),
                                SimpleBlocks.Model.when("minecraft:block/cyan_candle_four_candles", Conditions.when(CandleBlock.CANDLES, 4).and(CandleBlock.LIT, false)),
                                SimpleBlocks.Model.when("minecraft:block/cyan_candle_four_candles_lit", Conditions.when(CandleBlock.CANDLES, 4).and(CandleBlock.LIT, true))
                        )
                        .noItem()
                        .build(),

                SimpleBlocks.Entry.builder(ModBlocks.PEARL_PURPLE_CANDLE.get())
                        .models(
                                SimpleBlocks.Model.when("minecraft:block/purple_candle_one_candle", Conditions.when(CandleBlock.CANDLES, 1).and(CandleBlock.LIT, false)),
                                SimpleBlocks.Model.when("minecraft:block/purple_candle_one_candle_lit", Conditions.when(CandleBlock.CANDLES, 1).and(CandleBlock.LIT, true)),
                                SimpleBlocks.Model.when("minecraft:block/purple_candle_two_candles", Conditions.when(CandleBlock.CANDLES, 2).and(CandleBlock.LIT, false)),
                                SimpleBlocks.Model.when("minecraft:block/purple_candle_two_candles_lit", Conditions.when(CandleBlock.CANDLES, 2).and(CandleBlock.LIT, true)),
                                SimpleBlocks.Model.when("minecraft:block/purple_candle_three_candles", Conditions.when(CandleBlock.CANDLES, 3).and(CandleBlock.LIT, false)),
                                SimpleBlocks.Model.when("minecraft:block/purple_candle_three_candles_lit", Conditions.when(CandleBlock.CANDLES, 3).and(CandleBlock.LIT, true)),
                                SimpleBlocks.Model.when("minecraft:block/purple_candle_four_candles", Conditions.when(CandleBlock.CANDLES, 4).and(CandleBlock.LIT, false)),
                                SimpleBlocks.Model.when("minecraft:block/purple_candle_four_candles_lit", Conditions.when(CandleBlock.CANDLES, 4).and(CandleBlock.LIT, true))
                        )
                        .noItem()
                        .build(),

                SimpleBlocks.Entry.builder(ModBlocks.PEARL_BLUE_CANDLE.get())
                        .models(
                                SimpleBlocks.Model.when("minecraft:block/blue_candle_one_candle", Conditions.when(CandleBlock.CANDLES, 1).and(CandleBlock.LIT, false)),
                                SimpleBlocks.Model.when("minecraft:block/blue_candle_one_candle_lit", Conditions.when(CandleBlock.CANDLES, 1).and(CandleBlock.LIT, true)),
                                SimpleBlocks.Model.when("minecraft:block/blue_candle_two_candles", Conditions.when(CandleBlock.CANDLES, 2).and(CandleBlock.LIT, false)),
                                SimpleBlocks.Model.when("minecraft:block/blue_candle_two_candles_lit", Conditions.when(CandleBlock.CANDLES, 2).and(CandleBlock.LIT, true)),
                                SimpleBlocks.Model.when("minecraft:block/blue_candle_three_candles", Conditions.when(CandleBlock.CANDLES, 3).and(CandleBlock.LIT, false)),
                                SimpleBlocks.Model.when("minecraft:block/blue_candle_three_candles_lit", Conditions.when(CandleBlock.CANDLES, 3).and(CandleBlock.LIT, true)),
                                SimpleBlocks.Model.when("minecraft:block/blue_candle_four_candles", Conditions.when(CandleBlock.CANDLES, 4).and(CandleBlock.LIT, false)),
                                SimpleBlocks.Model.when("minecraft:block/blue_candle_four_candles_lit", Conditions.when(CandleBlock.CANDLES, 4).and(CandleBlock.LIT, true))
                        )
                        .noItem()
                        .build(),

                SimpleBlocks.Entry.builder(ModBlocks.PEARL_BROWN_CANDLE.get())
                        .models(
                                SimpleBlocks.Model.when("minecraft:block/brown_candle_one_candle", Conditions.when(CandleBlock.CANDLES, 1).and(CandleBlock.LIT, false)),
                                SimpleBlocks.Model.when("minecraft:block/brown_candle_one_candle_lit", Conditions.when(CandleBlock.CANDLES, 1).and(CandleBlock.LIT, true)),
                                SimpleBlocks.Model.when("minecraft:block/brown_candle_two_candles", Conditions.when(CandleBlock.CANDLES, 2).and(CandleBlock.LIT, false)),
                                SimpleBlocks.Model.when("minecraft:block/brown_candle_two_candles_lit", Conditions.when(CandleBlock.CANDLES, 2).and(CandleBlock.LIT, true)),
                                SimpleBlocks.Model.when("minecraft:block/brown_candle_three_candles", Conditions.when(CandleBlock.CANDLES, 3).and(CandleBlock.LIT, false)),
                                SimpleBlocks.Model.when("minecraft:block/brown_candle_three_candles_lit", Conditions.when(CandleBlock.CANDLES, 3).and(CandleBlock.LIT, true)),
                                SimpleBlocks.Model.when("minecraft:block/brown_candle_four_candles", Conditions.when(CandleBlock.CANDLES, 4).and(CandleBlock.LIT, false)),
                                SimpleBlocks.Model.when("minecraft:block/brown_candle_four_candles_lit", Conditions.when(CandleBlock.CANDLES, 4).and(CandleBlock.LIT, true))
                        )
                        .noItem()
                        .build(),

                SimpleBlocks.Entry.builder(ModBlocks.PEARL_GREEN_CANDLE.get())
                        .models(
                                SimpleBlocks.Model.when("minecraft:block/green_candle_one_candle", Conditions.when(CandleBlock.CANDLES, 1).and(CandleBlock.LIT, false)),
                                SimpleBlocks.Model.when("minecraft:block/green_candle_one_candle_lit", Conditions.when(CandleBlock.CANDLES, 1).and(CandleBlock.LIT, true)),
                                SimpleBlocks.Model.when("minecraft:block/green_candle_two_candles", Conditions.when(CandleBlock.CANDLES, 2).and(CandleBlock.LIT, false)),
                                SimpleBlocks.Model.when("minecraft:block/green_candle_two_candles_lit", Conditions.when(CandleBlock.CANDLES, 2).and(CandleBlock.LIT, true)),
                                SimpleBlocks.Model.when("minecraft:block/green_candle_three_candles", Conditions.when(CandleBlock.CANDLES, 3).and(CandleBlock.LIT, false)),
                                SimpleBlocks.Model.when("minecraft:block/green_candle_three_candles_lit", Conditions.when(CandleBlock.CANDLES, 3).and(CandleBlock.LIT, true)),
                                SimpleBlocks.Model.when("minecraft:block/green_candle_four_candles", Conditions.when(CandleBlock.CANDLES, 4).and(CandleBlock.LIT, false)),
                                SimpleBlocks.Model.when("minecraft:block/green_candle_four_candles_lit", Conditions.when(CandleBlock.CANDLES, 4).and(CandleBlock.LIT, true))
                        )
                        .noItem()
                        .build(),

                SimpleBlocks.Entry.builder(ModBlocks.PEARL_RED_CANDLE.get())
                        .models(
                                SimpleBlocks.Model.when("minecraft:block/red_candle_one_candle", Conditions.when(CandleBlock.CANDLES, 1).and(CandleBlock.LIT, false)),
                                SimpleBlocks.Model.when("minecraft:block/red_candle_one_candle_lit", Conditions.when(CandleBlock.CANDLES, 1).and(CandleBlock.LIT, true)),
                                SimpleBlocks.Model.when("minecraft:block/red_candle_two_candles", Conditions.when(CandleBlock.CANDLES, 2).and(CandleBlock.LIT, false)),
                                SimpleBlocks.Model.when("minecraft:block/red_candle_two_candles_lit", Conditions.when(CandleBlock.CANDLES, 2).and(CandleBlock.LIT, true)),
                                SimpleBlocks.Model.when("minecraft:block/red_candle_three_candles", Conditions.when(CandleBlock.CANDLES, 3).and(CandleBlock.LIT, false)),
                                SimpleBlocks.Model.when("minecraft:block/red_candle_three_candles_lit", Conditions.when(CandleBlock.CANDLES, 3).and(CandleBlock.LIT, true)),
                                SimpleBlocks.Model.when("minecraft:block/red_candle_four_candles", Conditions.when(CandleBlock.CANDLES, 4).and(CandleBlock.LIT, false)),
                                SimpleBlocks.Model.when("minecraft:block/red_candle_four_candles_lit", Conditions.when(CandleBlock.CANDLES, 4).and(CandleBlock.LIT, true))
                        )
                        .noItem()
                        .build(),

                SimpleBlocks.Entry.builder(ModBlocks.PEARL_BLACK_CANDLE.get())
                        .models(
                                SimpleBlocks.Model.when("minecraft:block/black_candle_one_candle", Conditions.when(CandleBlock.CANDLES, 1).and(CandleBlock.LIT, false)),
                                SimpleBlocks.Model.when("minecraft:block/black_candle_one_candle_lit", Conditions.when(CandleBlock.CANDLES, 1).and(CandleBlock.LIT, true)),
                                SimpleBlocks.Model.when("minecraft:block/black_candle_two_candles", Conditions.when(CandleBlock.CANDLES, 2).and(CandleBlock.LIT, false)),
                                SimpleBlocks.Model.when("minecraft:block/black_candle_two_candles_lit", Conditions.when(CandleBlock.CANDLES, 2).and(CandleBlock.LIT, true)),
                                SimpleBlocks.Model.when("minecraft:block/black_candle_three_candles", Conditions.when(CandleBlock.CANDLES, 3).and(CandleBlock.LIT, false)),
                                SimpleBlocks.Model.when("minecraft:block/black_candle_three_candles_lit", Conditions.when(CandleBlock.CANDLES, 3).and(CandleBlock.LIT, true)),
                                SimpleBlocks.Model.when("minecraft:block/black_candle_four_candles", Conditions.when(CandleBlock.CANDLES, 4).and(CandleBlock.LIT, false)),
                                SimpleBlocks.Model.when("minecraft:block/black_candle_four_candles_lit", Conditions.when(CandleBlock.CANDLES, 4).and(CandleBlock.LIT, true))
                        )
                        .noItem()
                        .build(),

                SimpleBlocks.Entry.builder(ModBlocks.VOCO_TABLE.get())
                        .multipart(
                                SimpleBlocks.Part.always("musavacca:block/voco_table"),

                                SimpleBlocks.Part.when(
                                        "musavacca:block/voco_table_lit_receptor_corner",
                                        Conditions.when(VocoTableBlock.LIT_NORTH_EAST, true)
                                ),
                                SimpleBlocks.Part.when(
                                        "musavacca:block/voco_table_lit_receptor_corner",
                                        Conditions.when(VocoTableBlock.LIT_SOUTH_EAST, true)
                                ).rotateY(90),
                                SimpleBlocks.Part.when(
                                        "musavacca:block/voco_table_lit_receptor_corner",
                                        Conditions.when(VocoTableBlock.LIT_SOUTH_WEST, true)
                                ).rotateY(180),
                                SimpleBlocks.Part.when(
                                        "musavacca:block/voco_table_lit_receptor_corner",
                                        Conditions.when(VocoTableBlock.LIT_NORTH_WEST, true)
                                ).rotateY(270),

                                SimpleBlocks.Part.when(
                                        "musavacca:block/voco_table_rotary_dialers",
                                        Conditions.when(VocoTableBlock.ROTARY_DIALERS, true)
                                ),

                                SimpleBlocks.Part.when(
                                        "musavacca:block/voco_table_portal_north_east",
                                        Conditions.when(VocoTableBlock.PORTAL_NORTH_EAST, true)
                                ).tint(MusavaccaTints.pearlFire(PearlFireTintProfiles.PORTAL_BLOCK).multi(0)),

                                SimpleBlocks.Part.when(
                                        "musavacca:block/voco_table_portal_south_east",
                                        Conditions.when(VocoTableBlock.PORTAL_SOUTH_EAST, true)
                                ).tint(MusavaccaTints.pearlFire(PearlFireTintProfiles.PORTAL_BLOCK).multi(1)),

                                SimpleBlocks.Part.when(
                                        "musavacca:block/voco_table_portal_south_west",
                                        Conditions.when(VocoTableBlock.PORTAL_SOUTH_WEST, true)
                                ).tint(MusavaccaTints.pearlFire(PearlFireTintProfiles.PORTAL_BLOCK).multi(2)),

                                SimpleBlocks.Part.when(
                                        "musavacca:block/voco_table_portal_north_west",
                                        Conditions.when(VocoTableBlock.PORTAL_NORTH_WEST, true)
                                ).tint(MusavaccaTints.pearlFire(PearlFireTintProfiles.PORTAL_BLOCK).multi(3))
                        )
                        .build()
        );
    }

    public static List<CrossBlocks.Entry> crossBlocks() {
        return List.of(
                CrossBlocks.Entry.builder(ModBlocks.CAROTENE_SHORT_GRASS.get())
                        .generated()
                        .texture()
                        .build()
        );
    }

    public static List<TallCrossBlocks.Entry> tallCrossBlocks() {
        return List.of(
                TallCrossBlocks.Entry.builder(ModBlocks.CAROTENE_TALL_GRASS.get())
                        .generated()
                        .textures(textures -> textures
                                .bottom()
                                .top())
                        .build()
        );
    }

    public static List<DoorBlocks.Entry> doorBlocks() {
        return List.of(
                DoorBlocks.Entry.builder(ModBlocks.MUSAVACCA_DOOR.get())
                        .generated()
                        .textures(textures -> textures
                                .bottom()
                                .top())
                        .multipart(
                                DoorBlocks.Part.when(
                                        DoorModels.lower(
                                                "musavacca:block/musavacca_door/knob/lit_knob_door_bottom_left",
                                                "musavacca:block/musavacca_door/knob/lit_knob_door_bottom_left_open",
                                                "musavacca:block/musavacca_door/knob/lit_knob_door_bottom_right",
                                                "musavacca:block/musavacca_door/knob/lit_knob_door_bottom_right_open"
                                        ),
                                        Conditions.when(MusavaccaPortalDoorBlock.LIT, true)
                                                .and(MusavaccaPortalDoorBlock.LIT_PORTAL, false)
                                ),

                                DoorBlocks.Part.when(
                                        DoorModels.lower(
                                                "musavacca:block/musavacca_door/knob/lit_knob_door_bottom_left",
                                                "musavacca:block/musavacca_door/knob/lit_knob_door_bottom_left_open",
                                                "musavacca:block/musavacca_door/knob/lit_knob_door_bottom_right",
                                                "musavacca:block/musavacca_door/knob/lit_knob_door_bottom_right_open"
                                        ),
                                        Conditions.when(MusavaccaPortalDoorBlock.PORTAL, true)
                                ),

                                DoorBlocks.Part.when(
                                        DoorModels.lower(
                                                "musavacca:block/musavacca_door/knob/lit_portal_door_bottom_left",
                                                "musavacca:block/musavacca_door/knob/lit_portal_door_bottom_left_open",
                                                "musavacca:block/musavacca_door/knob/lit_portal_door_bottom_right",
                                                "musavacca:block/musavacca_door/knob/lit_portal_door_bottom_right_open"
                                        ),
                                        Conditions.when(MusavaccaPortalDoorBlock.LIT_PORTAL, true)
                                                .and(MusavaccaPortalDoorBlock.PORTAL, false)
                                ).tint(
                                        MusavaccaTints.pearlFire(PearlFireTintProfiles.PORTAL_BLOCK)
                                ),

                                DoorBlocks.Part.when(
                                        DoorModels.builder()
                                                .topLeft(
                                                        "musavacca:block/musavacca_door/door_portal/door_portal_top_left"
                                                )
                                                .topRight(
                                                        "musavacca:block/musavacca_door/door_portal/door_portal_top_right"
                                                )
                                                .bottomLeftOpen(
                                                        "musavacca:block/musavacca_door/portal/portal_bottom_left_open"
                                                )
                                                .bottomRightOpen(
                                                        "musavacca:block/musavacca_door/portal/portal_bottom_right_open"
                                                )
                                                .topLeftOpen(
                                                        "musavacca:block/musavacca_door/portal/portal_top_left_open"
                                                )
                                                .topRightOpen(
                                                        "musavacca:block/musavacca_door/portal/portal_top_right_open"
                                                )
                                                .build(),
                                        Conditions.when(MusavaccaPortalDoorBlock.PORTAL, true)
                                ).tint(
                                        MusavaccaTints.pearlFire(PearlFireTintProfiles.PORTAL_BLOCK)
                                )
                        )
                        .items(
                                SimpleItems.Entry.builder(ModItems.MUSAVACCA_DOOR.get())
                                        .generated()
                                        .flat()
                                        .texture()
                                        .build(),

                                SimpleItems.Entry.builder(ModItems.MUSAVACCA_CHARGED_DOOR.get())
                                        .generated()
                                        .flat()
                                        .textures(
                                                "musavacca:item/musavacca_door",
                                                "musavacca:item/musavacca_door_knob"
                                        )
                                        .build(),

                                SimpleItems.Entry.builder(ModItems.MUSAVACCA_IMBUED_DOOR.get())
                                        .generated()
                                        .flat()
                                        .textures(
                                                "musavacca:item/musavacca_door",
                                                "musavacca:item/musavacca_door_portal"
                                        )
                                        .layerTint(1, MusavaccaTints.hexColor())
                                        .build()
                        )
                        .build()
        );
    }

    public static List<TrapdoorBlocks.Entry> trapdoorBlocks() {
        TrapdoorModels litKnob = TrapdoorModels.full(
                "musavacca:block/musavacca_trapdoor/knob/lit_knob_trapdoor_bottom",
                "musavacca:block/musavacca_trapdoor/knob/lit_knob_trapdoor_top",
                "musavacca:block/musavacca_trapdoor/knob/lit_knob_trapdoor_open"
        );

        return List.of(
                TrapdoorBlocks.Entry.builder(ModBlocks.MUSAVACCA_TRAPDOOR.get())
                        .generated()
                        .texture()
                        .multipart(
                                TrapdoorBlocks.Part.when(
                                        litKnob,
                                        Conditions.when(MusavaccaPortalTrapdoorBlock.LIT, true)
                                                .and(MusavaccaPortalTrapdoorBlock.LIT_PORTAL, false)
                                ),

                                TrapdoorBlocks.Part.when(
                                        litKnob,
                                        Conditions.when(MusavaccaPortalTrapdoorBlock.PORTAL, true)
                                ),

                                TrapdoorBlocks.Part.when(
                                        TrapdoorModels.full(
                                                "musavacca:block/musavacca_trapdoor/knob/lit_portal_trapdoor_bottom",
                                                "musavacca:block/musavacca_trapdoor/knob/lit_portal_trapdoor_top",
                                                "musavacca:block/musavacca_trapdoor/knob/lit_portal_trapdoor_open"
                                        ),
                                        Conditions.when(MusavaccaPortalTrapdoorBlock.LIT_PORTAL, true)
                                                .and(MusavaccaPortalTrapdoorBlock.PORTAL, false)
                                ).tint(
                                        MusavaccaTints.pearlFire(PearlFireTintProfiles.PORTAL_BLOCK)
                                ),

                                TrapdoorBlocks.Part.when(
                                        TrapdoorModels.full(
                                                "musavacca:block/musavacca_trapdoor/trapdoor_portal/trapdoor_portal_bottom",
                                                "musavacca:block/musavacca_trapdoor/trapdoor_portal/trapdoor_portal_top",
                                                "musavacca:block/musavacca_trapdoor/portal/portal_open"
                                        ),
                                        Conditions.when(MusavaccaPortalTrapdoorBlock.PORTAL, true)
                                ).tint(
                                        MusavaccaTints.pearlFire(PearlFireTintProfiles.PORTAL_BLOCK)
                                )
                        )
                        .item()
                        .build()
        );
    }

    public static List<StairBlocks.Entry> stairBlocks() {
        return List.of(
                StairBlocks.Entry.builder(ModBlocks.MUSAVACCA_STAIRS.get())
                        .generated()
                        .texture()
                        .item()
                        .build()
        );
    }

    public static List<SlabBlocks.Entry> slabBlocks() {
        return List.of(
                SlabBlocks.Entry.builder(ModBlocks.MUSAVACCA_SLAB.get())
                        .generated()
                        .texture()
                        .item()
                        .build()
        );
    }

    public static List<FenceBlocks.Entry> fenceBlocks() {
        return List.of(
                FenceBlocks.Entry.builder(ModBlocks.MUSAVACCA_FENCE.get())
                        .generated()
                        .texture()
                        .item()
                        .build()
        );
    }

    public static List<FenceGateBlocks.Entry> fenceGateBlocks() {
        return List.of(
                FenceGateBlocks.Entry.builder(ModBlocks.MUSAVACCA_FENCE_GATE.get())
                        .generated()
                        .texture()
                        .item()
                        .build()
        );
    }

    public static List<PressurePlateBlocks.Entry> pressurePlateBlocks() {
        return List.of(
                PressurePlateBlocks.Entry.builder(ModBlocks.MUSAVACCA_PRESSURE_PLATE.get())
                        .generated()
                        .texture()
                        .item()
                        .build()
        );
    }

    public static List<ButtonBlocks.Entry> buttonBlocks() {
        return List.of(
                ButtonBlocks.Entry.builder(ModBlocks.MUSAVACCA_BUTTON.get())
                        .generated()
                        .texture()
                        .item()
                        .build()
        );
    }

    public static List<WallBlocks.Entry> wallBlocks() {
        return List.of();
    }

    public static List<PortalBlocks.Entry> portalBlocks() {
        return List.of(
                PortalBlocks.Entry.builder(ModBlocks.PEARL_PORTAL.get())
                        .generated()
                        .texture()
                        .tint(MusavaccaTints.pearlFire(PearlFireTintProfiles.PORTAL_BLOCK))
                        .noItem()
                        .build()
        );
    }

    public static List<FireBlocks.Entry> fireBlocks() {
        return List.of(
                FireBlocks.Entry.builder(ModBlocks.PEARL_FIRE.get())
                        .generated()
                        .texture()
                        .tint(MusavaccaTints.pearlFire(PearlFireTintProfiles.FIRE_BLOCK))
                        .noItem()
                        .build()
        );
    }

    public static List<DecorationBlocks.Entry> decorationBlocks() {
        return List.of(
                DecorationBlocks.Entry.builder(ModBlocks.BANANA_PEARL_CHALICE.get())
                        .models(
                                DecorationBlocks.Model.rotating(
                                        DecorationBlock.Placement.FLOOR,
                                        "musavacca:block/banana_pearl_chalice_floor"
                                ),
                                DecorationBlocks.Model.rotating(
                                        DecorationBlock.Placement.SNEAK,
                                        "musavacca:block/banana_pearl_chalice_sneak"
                                ),
                                DecorationBlocks.Model.facing(
                                        DecorationBlock.Placement.SIDE,
                                        "musavacca:block/banana_pearl_chalice_side"
                                )
                        )
                        .item(DecorationBlock.Placement.FLOOR)
                        .build()
        );
    }

    public static List<SimpleItems.Entry> simpleItems() {
        return List.of(
                SimpleItems.Entry.builder(ModItems.BANANA_PEARL.get())
                        .generated()
                        .flat()
                        .texture()
                        .build(),

                SimpleItems.Entry.builder(ModItems.BIG_BANANA_PEARL.get())
                        .generated()
                        .flat()
                        .texture()
                        .build(),

                SimpleItems.Entry.builder(ModItems.SMALL_BANANA_PEARL.get())
                        .generated()
                        .flat()
                        .texture()
                        .build(),

                SimpleItems.Entry.builder(ModItems.FLINT_AND_PEARL.get())
                        .generated()
                        .flat()
                        .texture()
                        .build(),

                SimpleItems.Entry.builder(ModItems.VACACA.get())
                        .generated()
                        .flat()
                        .texture()
                        .build(),

                SimpleItems.Entry.builder(ModItems.MUSAVACCA_EXUDATE.get())
                        .generated()
                        .flat()
                        .texture()
                        .build(),

                SimpleItems.Entry.builder(ModItems.BANAZO_GUSMA_LUMPA_GOOP.get())
                        .generated()
                        .flat()
                        .texture()
                        .build(),

                SimpleItems.Entry.builder(ModItems.BANANA_PELLIS.get())
                        .generated()
                        .flat()
                        .texture()
                        .build(),

                SimpleItems.Entry.builder(ModItems.MUSAVACCA_PUP.get())
                        .generated()
                        .flat()
                        .texture()
                        .build(),

                SimpleItems.Entry.builder(ModBlocks.UNRIPE_MUSAVACCA_EGG.get())
                        .generated()
                        .flat()
                        .texture()
                        .build(),

                SimpleItems.Entry.builder(ModBlocks.RIPENING_MUSAVACCA_EGG.get())
                        .generated()
                        .flat()
                        .texture()
                        .build(),

                SimpleItems.Entry.builder(ModBlocks.RIPE_MUSAVACCA_EGG.get())
                        .generated()
                        .flat()
                        .texture()
                        .build(),

                SimpleItems.Entry.builder(ModItems.POTASSIUM_INGOT.get())
                        .generated()
                        .flat()
                        .texture()
                        .build(),

                SimpleItems.Entry.builder(ModItems.FRACTURED_POTASSIUM_UPGRADE_SMITHING_TEMPLATE.get())
                        .generated()
                        .flat()
                        .texture()
                        .build(),

                SimpleItems.Entry.builder(ModItems.POTASSIUM_UPGRADE_SMITHING_TEMPLATE.get())
                        .generated()
                        .flat()
                        .texture()
                        .build(),

                SimpleItems.Entry.builder(ModItems.BANANA_MILK_BUCKET.get())
                        .generated()
                        .flat()
                        .texture()
                        .build(),

                SimpleItems.Entry.builder(ModItems.MUSAVACCA_BOAT.get())
                        .generated()
                        .flat()
                        .texture()
                        .build(),

                SimpleItems.Entry.builder(ModItems.POTASSIUM_AXE.get())
                        .generated()
                        .handheld()
                        .texture()
                        .build(),

                SimpleItems.Entry.builder(ModItems.POTASSIUM_PICKAXE.get())
                        .generated()
                        .handheld()
                        .texture()
                        .build(),

                SimpleItems.Entry.builder(ModItems.POTASSIUM_SHOVEL.get())
                        .generated()
                        .handheld()
                        .texture()
                        .build(),

                SimpleItems.Entry.builder(ModItems.POTASSIUM_SWORD.get())
                        .generated()
                        .handheld()
                        .texture()
                        .build(),

                SimpleItems.Entry.builder(ModItems.POTASSIUM_HOE.get())
                        .generated()
                        .handheld()
                        .texture()
                        .build(),

                SimpleItems.Entry.builder(ModItems.BANANA_PHONE.get())
                        .model("musavacca:item/banana_phone")
                        .build(),

                SimpleItems.Entry.builder(ModItems.INACTIVE_VOCO_CALLER.get())
                        .model("musavacca:item/banana_phone_off")
                        .build(),

                SimpleItems.Entry.builder(ModItems.SIM_CARD.get())
                        .generated()
                        .flat()
                        .folder()
                        .textures(
                                "sim_card",
                                "sim_card"
                        )
                        .layerTint(
                                1,
                                MusavaccaTints.pearlFire(
                                        PearlFireTintProfiles.SIM_CARD_TINT
                                )
                        )
                        .build()
        );
    }

    public static List<SpawnEggItems.Entry> spawnEggItems() {
        return List.of(
                SpawnEggItems.Entry.builder(ModItems.BANANA_COW_SPAWN_EGG.get())
                        .colors(0xE4C64A, 0x7A4A1F)
                        .texture()
                        .build(),
                SpawnEggItems.Entry.builder(ModItems.BASUKE_SPAWN_EGG.get())
                        .colors(0xE6DCC8, 0x4F3F36)
                        .texture()
                        .build()
        );
    }

    public static List<ArmorItems.Entry> armorItems() {
        return List.of(
                ArmorItems.Entry.builder()
                        .items(items -> items
                                .helmet(ModItems.POTASSIUM_HELMET.get())
                                .chestplate(ModItems.POTASSIUM_CHESTPLATE.get())
                                .leggings(ModItems.POTASSIUM_LEGGINGS.get())
                                .boots(ModItems.POTASSIUM_BOOTS.get())
                        )
                        .inventory(inventory -> inventory
                                .generated()
                                .trims()
                        )
                        .equipment(equipment -> equipment
                                .id("musavacca:potassium")
                                .texture("musavacca:potassium")
                        )
                        .helmetHeadModel("musavacca:item/potassium_helmet_model")
                        .build()
        );
    }

}

