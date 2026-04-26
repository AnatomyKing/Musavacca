package space.anatomyuniverse.musavacca.data.recipes;

import net.minecraft.advancements.Criterion;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import space.anatomyuniverse.musavacca.block.ModBlocks;
import space.anatomyuniverse.musavacca.item.ModItems;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public final class ModRecipeProvider extends RecipeProvider {

    public static final String MOD_ID = "musavacca";

    //? if <1.21.3 {
    /*private final CompletableFuture<HolderLookup.Provider> lookupFuture;

    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
        this.lookupFuture = registries;
    }

    @Override
    protected void buildRecipes(@NotNull RecipeOutput output) {
        // join() is safe inside datagen execution
        final HolderLookup.Provider registries = lookupFuture.join();
        buildAll(output, registries);
    }
    
    *///?} else {
    private final HolderLookup.Provider registries;
    private final RecipeOutput output;

    public ModRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
        this.registries = registries;
        this.output = output;
    }

    @Override
    protected void buildRecipes() {
        buildAll(this.output, this.registries);
    }

    public static final class Runner extends RecipeProvider.Runner {
        public Runner(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
            super(output, registries);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
            return new ModRecipeProvider(registries, output);
        }

        @Override
        public String getName() {
            return MOD_ID + " recipes";
        }
    }
    //?}

    private void buildAll(RecipeOutput output, HolderLookup.Provider registries) {
        final RecipeDSL.Unlocker unlocker = new RecipeDSL.Unlocker() {
            @Override
            public Criterion<?> item(net.minecraft.world.level.ItemLike i) {
                return has(i);
            }

            @Override
            public Criterion<?> tag(TagKey<Item> t) {
                return has(t);
            }
        };

        final RecipeDSL dsl;
        //? if <1.21.3 {
        /*dsl = new RecipeDSL(output, MOD_ID, unlocker);
         *///?} else
        dsl = new RecipeDSL(output, MOD_ID, unlocker, registries);

         dsl.shapelessCountToCount(
                 RecipeCategory.MISC,
                         ModItems.BANANA_PEARL.get(), 1,
                 ModItems.SMALL_BANANA_PEARL.get(), 4
         )
         .unlockedByHas(ModItems.BANANA_PEARL.get())
         .save("misc/banana_pearl_to_small_banana_pearl");

        dsl.shapelessCountToCount(
                        RecipeCategory.MISC,
                        ModItems.SMALL_BANANA_PEARL.get(), 4,
                        ModItems.BANANA_PEARL.get(), 1
                )
                .unlockedByHas(ModItems.BANANA_PEARL.get())
                .save("misc/small_banana_pearl_to_banana_pearl");

        dsl.shapelessCountToCount(
                        RecipeCategory.MISC,
                        ModItems.BIG_BANANA_PEARL.get(), 1,
                        ModItems.BANANA_PEARL.get(), 4
                )
                .unlockedByHas(ModItems.BANANA_PEARL.get())
                .save("misc/banana_pearl_to_big_banana_pearl");

        dsl.shapelessCountToCount(
                        RecipeCategory.MISC,
                        ModItems.BANANA_PEARL.get(), 4,
                        ModItems.BIG_BANANA_PEARL.get(), 1
                )
                .unlockedByHas(ModItems.BANANA_PEARL.get())
                .save("misc/big_banana_pearl_to_banana_pearl");

        dsl.shaped(RecipeCategory.MISC, ModItems.BANANA_PEARL.get(), 1)
                .pattern("aaa")
                .pattern("ava")
                .pattern("aaa")
                .define('a', Items.AMETHYST_SHARD)
                .define('v', ModItems.VACACA.get())
                .unlockedByHas(ModItems.VACACA.get(), Items.AMETHYST_SHARD)
                .save("misc/banana_pearl_from_vacaca");

//        dsl.shapeless(RecipeCategory.MISC, ModItems.RAW_ANYTOMITHIUM.get(), 1)
//                .requires(Items.AMETHYST_SHARD, Items.RAW_IRON, Items.PRISMARINE_CRYSTALS)
//                // NOTE: multiple unlockedBy criteria unlock as OR in vanilla recipe advancements
//                .unlockedByHas(Items.AMETHYST_SHARD, Items.RAW_IRON, Items.PRISMARINE_CRYSTALS)
//                .save("misc/raw_anytomithium");
//
//        dsl.cook().smelt(Ingredient.of(ModItems.RAW_ANYTOMITHIUM.get()),
//                        RecipeCategory.MISC, ModItems.PURPISH_ANYTOMITHIUM_INGOT.get(), 0.7F, 300)
//                .unlockedByHas(ModItems.RAW_ANYTOMITHIUM.get())
//                .save("smelting/purpish_anytomithium_ingot");
//
//        dsl.cook().blast(Ingredient.of(ModItems.RAW_ANYTOMITHIUM.get()),
//                        RecipeCategory.MISC, ModItems.TEALISH_ANYTOMITHIUM_INGOT.get(), 0.7F, 150)
//                .unlockedByHas(ModItems.RAW_ANYTOMITHIUM.get())
//                .save("smelting/tealish_anytomithium_ingot");
//
//        // =========================
//        // Blocks: 9 ingots <-> block
//        // =========================
//
//        // Purpish block
//        dsl.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.BANANA_PEARL_BLOCK.get(), 1)
//                .pattern("xxx").pattern("xxx").pattern("xxx")
//                .define('x', ModItems.PURPISH_ANYTOMITHIUM_INGOT.get())
//                .unlockedByHas(ModItems.PURPISH_ANYTOMITHIUM_INGOT.get())
//                .save("blocks/purpish_anytomithium_block");
//
//        dsl.shapeless(RecipeCategory.MISC, ModItems.PURPISH_ANYTOMITHIUM_INGOT.get(), 9)
//                .requires(ModBlocks.BANANA_PEARL_BLOCK.get())
//                .unlockedByHas(ModBlocks.BANANA_PEARL_BLOCK.get())
//                .save("blocks/purpish_anytomithium_ingot_from_block");
//
//        // Tealish block
//        dsl.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.TEALISH_ANYTOMITHIUM_BLOCK.get(), 1)
//                .pattern("xxx").pattern("xxx").pattern("xxx")
//                .define('x', ModItems.TEALISH_ANYTOMITHIUM_INGOT.get())
//                .unlockedByHas(ModItems.TEALISH_ANYTOMITHIUM_INGOT.get())
//                .save("blocks/tealish_anytomithium_block");
//
//        dsl.shapeless(RecipeCategory.MISC, ModItems.TEALISH_ANYTOMITHIUM_INGOT.get(), 9)
//                .requires(ModBlocks.TEALISH_ANYTOMITHIUM_BLOCK.get())
//                .unlockedByHas(ModBlocks.TEALISH_ANYTOMITHIUM_BLOCK.get())
//                .save("blocks/tealish_anytomithium_ingot_from_block");
//
//        // =========================
//        // Variants (crafting)
//        // =========================
//
//        // CUT: 2x2 blocks -> 4 cut
//        dsl.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CUT_PURPISH_ANYTOMITHIUM.get(), 4)
//                .pattern("xx").pattern("xx")
//                .define('x', ModBlocks.BANANA_PEARL_BLOCK.get())
//                .unlockedByHas(ModBlocks.BANANA_PEARL_BLOCK.get())
//                .save("blocks/cut_purpish_anytomithium");
//
//        dsl.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CUT_TEALISH_ANYTOMITHIUM.get(), 4)
//                .pattern("xx").pattern("xx")
//                .define('x', ModBlocks.TEALISH_ANYTOMITHIUM_BLOCK.get())
//                .unlockedByHas(ModBlocks.TEALISH_ANYTOMITHIUM_BLOCK.get())
//                .save("blocks/cut_tealish_anytomithium");
//
//        // POLISHED: 2x2 -> 4
//        dsl.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.POLISHED_PURPISH_ANYTOMITHIUM.get(), 4)
//                .pattern("xx").pattern("xx")
//                .define('x', ModBlocks.BANANA_PEARL_BLOCK.get())
//                .unlockedByHas(ModBlocks.BANANA_PEARL_BLOCK.get())
//                .save("blocks/polished_purpish_anytomithium");
//
//        dsl.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.POLISHED_TEALISH_ANYTOMITHIUM.get(), 4)
//                .pattern("xx").pattern("xx")
//                .define('x', ModBlocks.TEALISH_ANYTOMITHIUM_BLOCK.get())
//                .unlockedByHas(ModBlocks.TEALISH_ANYTOMITHIUM_BLOCK.get())
//                .save("blocks/polished_tealish_anytomithium");
//
//        // BRICKS: 2x2 -> 4
//        dsl.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.BANANA_PEARL_BRICKS.get(), 4)
//                .pattern("xx").pattern("xx")
//                .define('x', ModBlocks.BANANA_PEARL_BLOCK.get())
//                .unlockedByHas(ModBlocks.BANANA_PEARL_BLOCK.get())
//                .save("blocks/purpish_anytomithium_bricks");
//
//        dsl.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.TEALISH_ANYTOMITHIUM_BRICKS.get(), 4)
//                .pattern("xx").pattern("xx")
//                .define('x', ModBlocks.TEALISH_ANYTOMITHIUM_BLOCK.get())
//                .unlockedByHas(ModBlocks.TEALISH_ANYTOMITHIUM_BLOCK.get())
//                .save("blocks/tealish_anytomithium_bricks");
//
//        // GRATE: 4 output
//        dsl.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.PURPISH_ANYTOMITHIUM_GRATE.get(), 4)
//                .pattern("x x").pattern(" x ").pattern("x x")
//                .define('x', ModBlocks.BANANA_PEARL_BLOCK.get())
//                .unlockedByHas(ModBlocks.BANANA_PEARL_BLOCK.get())
//                .save("blocks/purpish_anytomithium_grate");
//
//        dsl.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.TEALISH_ANYTOMITHIUM_GRATE.get(), 4)
//                .pattern("x x").pattern(" x ").pattern("x x")
//                .define('x', ModBlocks.TEALISH_ANYTOMITHIUM_BLOCK.get())
//                .unlockedByHas(ModBlocks.TEALISH_ANYTOMITHIUM_BLOCK.get())
//                .save("blocks/tealish_anytomithium_grate");
//
//        // CHISELED: 2 cut stacked -> 1
//        dsl.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CHISELED_PURPISH_ANYTOMITHIUM.get(), 1)
//                .pattern("x").pattern("x")
//                .define('x', ModBlocks.CUT_PURPISH_ANYTOMITHIUM.get())
//                .unlockedByHas(ModBlocks.CUT_PURPISH_ANYTOMITHIUM.get())
//                .save("blocks/chiseled_purpish_anytomithium");
//
//        dsl.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CHISELED_TEALISH_ANYTOMITHIUM.get(), 1)
//                .pattern("x").pattern("x")
//                .define('x', ModBlocks.CUT_TEALISH_ANYTOMITHIUM.get())
//                .unlockedByHas(ModBlocks.CUT_TEALISH_ANYTOMITHIUM.get())
//                .save("blocks/chiseled_tealish_anytomithium");
//
//        // CHISELED BRICKS: 2 bricks stacked -> 1
//        dsl.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CHISELED_PURPISH_ANYTOMITHIUM_BRICKS.get(), 1)
//                .pattern("x").pattern("x")
//                .define('x', ModBlocks.BANANA_PEARL_BRICKS.get())
//                .unlockedByHas(ModBlocks.BANANA_PEARL_BRICKS.get())
//                .save("blocks/chiseled_purpish_anytomithium_bricks");
//
//        dsl.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CHISELED_TEALISH_ANYTOMITHIUM_BRICKS.get(), 1)
//                .pattern("x").pattern("x")
//                .define('x', ModBlocks.TEALISH_ANYTOMITHIUM_BRICKS.get())
//                .unlockedByHas(ModBlocks.TEALISH_ANYTOMITHIUM_BRICKS.get())
//                .save("blocks/chiseled_tealish_anytomithium_bricks");
//
//        // =========================
//        // Stonecutter recipes
//        // =========================
//
//        // Purpish: all from base block
//        dsl.stonecutFamily(RecipeCategory.BUILDING_BLOCKS,
//                ModBlocks.BANANA_PEARL_BLOCK.get(),
//                "stonecutting", "from_block",
//                ModBlocks.CUT_PURPISH_ANYTOMITHIUM.get(), 1,
//                ModBlocks.POLISHED_PURPISH_ANYTOMITHIUM.get(), 1,
//                ModBlocks.BANANA_PEARL_BRICKS.get(), 1,
//                ModBlocks.PURPISH_ANYTOMITHIUM_GRATE.get(), 4,
//                ModBlocks.CHISELED_PURPISH_ANYTOMITHIUM.get(), 1
//        );
//
//        // Purpish: from bricks
//        dsl.stonecutFamily(RecipeCategory.BUILDING_BLOCKS,
//                ModBlocks.BANANA_PEARL_BRICKS.get(),
//                "stonecutting", "from_bricks",
//                ModBlocks.CHISELED_PURPISH_ANYTOMITHIUM_BRICKS.get(), 1
//        );
//
//        // Tealish: all from base block
//        dsl.stonecutFamily(RecipeCategory.BUILDING_BLOCKS,
//                ModBlocks.TEALISH_ANYTOMITHIUM_BLOCK.get(),
//                "stonecutting", "from_block",
//                ModBlocks.CUT_TEALISH_ANYTOMITHIUM.get(), 1,
//                ModBlocks.POLISHED_TEALISH_ANYTOMITHIUM.get(), 1,
//                ModBlocks.TEALISH_ANYTOMITHIUM_BRICKS.get(), 1,
//                ModBlocks.TEALISH_ANYTOMITHIUM_GRATE.get(), 4,
//                ModBlocks.CHISELED_TEALISH_ANYTOMITHIUM.get(), 1
//        );
//
//        // Tealish: from bricks
//        dsl.stonecutFamily(RecipeCategory.BUILDING_BLOCKS,
//                ModBlocks.TEALISH_ANYTOMITHIUM_BRICKS.get(),
//                "stonecutting", "from_bricks",
//                ModBlocks.CHISELED_TEALISH_ANYTOMITHIUM_BRICKS.get(), 1
//        );
//
//    }


        /*
         * =========================
         * Examples / how to use DSL
         * =========================
         */

        // --- Basic shapeless:
        // dsl.shapeless(RecipeCategory.MISC, ModItems.SOME_ITEM.get(), 2)
        //         .requires(Items.STICK, Items.IRON_INGOT)
        //         .unlockedByHas(Items.IRON_INGOT)
        //         .save("misc/some_item");

        // --- Basic shaped:
        // dsl.shaped(RecipeCategory.MISC, ModBlocks.SOME_BLOCK.get())
        //         .pattern("xyx")
        //         .pattern(" y ")
        //         .pattern("xyx")
        //         .define('x', Items.IRON_INGOT)
        //         .define('y', Items.REDSTONE)
        //         .unlockedByHas(Items.REDSTONE)
        //         .save("blocks/some_block");

        // --- “N input → M output” shapeless helper (NO AUTO UNLOCK):
        // dsl.shapelessCountToCount(
        //         RecipeCategory.MISC,
        //         ModBlocks.SOME_BLOCK.get(), 1,
        //         ModItems.SOME_ITEM.get(), 9
        // )
        // .unlockedByHas(Items.REDSTONE)
        // .save("misc/some_item_to_block");

        // --- Multi-count shapeless helper (NO AUTO UNLOCK):
        // dsl.shapelessCounts(
        //         RecipeCategory.MISC,
        //         ModItems.MIXED_DUST.get(), 3,
        //         Items.REDSTONE, 4,
        //         Items.GLOWSTONE_DUST, 2
        // )
        // .unlockedByHas(Items.REDSTONE)
        // .save("misc/mixed_dust");

        // --- Smelting / blasting:
        // dsl.cook().smelt(Ingredient.of(ModItems.RAW_SOMETHING.get()),
        //         RecipeCategory.MISC, ModItems.SOMETHING_INGOT.get(), 0.7F, 200)
        //         .unlockedByHas(ModItems.RAW_SOMETHING.get())
        //         .save("smelting/something_ingot");

        // --- Stonecutting:
        // dsl.stonecut().of(Ingredient.of(ModBlocks.SOME_STONE.get()),
        //         RecipeCategory.BUILDING_BLOCKS, ModBlocks.SOME_SLAB.get(), 2)
        //         .unlockedByHas(ModBlocks.SOME_STONE.get())
        //         .save("stonecutting/some_slab");

        // --- Smithing transform:
        // dsl.transform().of(
        //         Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
        //         Ingredient.of(Items.DIAMOND_SWORD),
        //         Ingredient.of(ModItems.MY_INGOT.get()),
        //         RecipeCategory.COMBAT,
        //         ModItems.MY_SWORD.get()
        // ).unlocksHas(ModItems.MY_INGOT.get())
        //  .save("smithing/my_sword_upgrade");

        // --- Smithing trim (1.21.1: NO pattern arg; only template/base/addition/category):
        // dsl.trim().of(
        //         Ingredient.of(Items.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE),
        //         Ingredient.of(Items.DIAMOND_CHESTPLATE),
        //         Ingredient.of(Items.AMETHYST_SHARD),
        //         RecipeCategory.MISC
        // ).unlocksHas(Items.AMETHYST_SHARD)
        //  .save("smithing/trim_sentry_demo");

        /*
         * =========================
         * Put your real recipes here
         * =========================
         */

        // Demo real recipe:
//        dsl.shapeless(RecipeCategory.MISC, Items.PAPER, 1)
//                .requiresCount(Items.SUGAR_CANE, 3)
//                .unlockedByHas(Items.SUGAR_CANE)
//                .save("demo/paper_from_3_cane");
    }
}
