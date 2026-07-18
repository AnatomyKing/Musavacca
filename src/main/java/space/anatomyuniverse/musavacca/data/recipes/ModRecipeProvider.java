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
import space.anatomyuniverse.musavacca.component.ModDataComponents;
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
        /*dsl = new RecipeDSL(output, MOD_ID, unlocker, registries);
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



        dsl.shapeless(RecipeCategory.MISC, ModItems.FLINT_AND_PEARL.get(), 1)
                .requires(Items.FLINT, ModItems.BANANA_PEARL.get())
                .unlockedByHas(Items.FLINT, ModItems.BANANA_PEARL.get())
                .save("misc/flint_and_pearl");


        dsl.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.VOCO_POST.get(), 1)
                .pattern("s")
                .pattern("f")
                .pattern("s")
                .define('s', ModBlocks.MUSAVACCA_SLAB.get())
                .define('f', ModBlocks.MUSAVACCA_FENCE.get())
                .unlockedByHas(ModBlocks.MUSAVACCA_SLAB.get(), ModBlocks.MUSAVACCA_FENCE.get())
                .save("blocks/voco_post");


        dsl.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.VOCO_TABLE.get(), 1)
                .pattern("p p")
                .pattern(" m ")
                .pattern("p p")
                .define('p', ModBlocks.VOCO_POST.get())
                .define('m', ModBlocks.MUSAVACCA_PLANKS.get())
                .unlockedByHas(ModBlocks.VOCO_POST.get(), ModBlocks.MUSAVACCA_PLANKS.get())
                .save("blocks/voco_table");


        dsl.shapelessCounts(
                        RecipeCategory.MISC,
                        ModItems.BANAZO_GUSMA_LUMPA_GOOP.get(), 1,
                        ModItems.MUSAVACCA_EXUDATE.get(), 4,
                        ModItems.BANANA_PELLIS.get(), 4
                )
                .unlockedByHas(ModItems.MUSAVACCA_EXUDATE.get(), ModItems.BANANA_PELLIS.get())
                .save("misc/banazo_gusma_lumpa_goop");


        dsl.shapelessCounts(
                        RecipeCategory.MISC,
                        ModItems.POTASSIUM_INGOT.get(), 1,
                        ModItems.BANAZO_GUSMA_LUMPA_GOOP.get(), 4,
                        ModItems.VACACA.get(), 4
                )
                .unlockedByHas(ModItems.BANAZO_GUSMA_LUMPA_GOOP.get(), ModItems.VACACA.get())
                .save("misc/potassium_ingot");


        dsl.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.MUSAVACCA_SLAB.get(), 6)
                .pattern("ppp")
                .define('p', ModBlocks.MUSAVACCA_PLANKS.get())
                .unlockedByHas(ModBlocks.MUSAVACCA_PLANKS.get())
                .save("blocks/musavacca_slab");


        dsl.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.MUSAVACCA_FENCE.get(), 3)
                .pattern("psp")
                .pattern("psp")
                .define('p', ModBlocks.MUSAVACCA_PLANKS.get())
                .define('s', Items.STICK)
                .unlockedByHas(ModBlocks.MUSAVACCA_PLANKS.get(), Items.STICK)
                .save("blocks/musavacca_fence");


        /*
         * NORMAL POTASSIUM TOOLS
         *
         * Potassium template + diamond tool + potassium ingot
         * -> normal potassium tool
         */

        dsl.transform().of(
                        Ingredient.of(ModItems.POTASSIUM_UPGRADE_SMITHING_TEMPLATE.get()),
                        Ingredient.of(Items.DIAMOND_SWORD),
                        Ingredient.of(ModItems.POTASSIUM_INGOT.get()),
                        RecipeCategory.COMBAT,
                        ModItems.POTASSIUM_SWORD.get()
                )
                .unlocksHas(
                        ModItems.POTASSIUM_UPGRADE_SMITHING_TEMPLATE.get(),
                        ModItems.POTASSIUM_INGOT.get()
                )
                .save("smithing/potassium_sword_upgrade");


        dsl.transform().of(
                        Ingredient.of(ModItems.POTASSIUM_UPGRADE_SMITHING_TEMPLATE.get()),
                        Ingredient.of(Items.DIAMOND_PICKAXE),
                        Ingredient.of(ModItems.POTASSIUM_INGOT.get()),
                        RecipeCategory.TOOLS,
                        ModItems.POTASSIUM_PICKAXE.get()
                )
                .unlocksHas(
                        ModItems.POTASSIUM_UPGRADE_SMITHING_TEMPLATE.get(),
                        ModItems.POTASSIUM_INGOT.get()
                )
                .save("smithing/potassium_pickaxe_upgrade");


        dsl.transform().of(
                        Ingredient.of(ModItems.POTASSIUM_UPGRADE_SMITHING_TEMPLATE.get()),
                        Ingredient.of(Items.DIAMOND_AXE),
                        Ingredient.of(ModItems.POTASSIUM_INGOT.get()),
                        RecipeCategory.TOOLS,
                        ModItems.POTASSIUM_AXE.get()
                )
                .unlocksHas(
                        ModItems.POTASSIUM_UPGRADE_SMITHING_TEMPLATE.get(),
                        ModItems.POTASSIUM_INGOT.get()
                )
                .save("smithing/potassium_axe_upgrade");


        dsl.transform().of(
                        Ingredient.of(ModItems.POTASSIUM_UPGRADE_SMITHING_TEMPLATE.get()),
                        Ingredient.of(Items.DIAMOND_SHOVEL),
                        Ingredient.of(ModItems.POTASSIUM_INGOT.get()),
                        RecipeCategory.TOOLS,
                        ModItems.POTASSIUM_SHOVEL.get()
                )
                .unlocksHas(
                        ModItems.POTASSIUM_UPGRADE_SMITHING_TEMPLATE.get(),
                        ModItems.POTASSIUM_INGOT.get()
                )
                .save("smithing/potassium_shovel_upgrade");


        dsl.transform().of(
                        Ingredient.of(ModItems.POTASSIUM_UPGRADE_SMITHING_TEMPLATE.get()),
                        Ingredient.of(Items.DIAMOND_HOE),
                        Ingredient.of(ModItems.POTASSIUM_INGOT.get()),
                        RecipeCategory.TOOLS,
                        ModItems.POTASSIUM_HOE.get()
                )
                .unlocksHas(
                        ModItems.POTASSIUM_UPGRADE_SMITHING_TEMPLATE.get(),
                        ModItems.POTASSIUM_INGOT.get()
                )
                .save("smithing/potassium_hoe_upgrade");


        /*
         * IMBUED POTASSIUM TOOLS
         *
         * Imbued template containing HEX_COLOR + diamond tool + potassium ingot
         * -> imbued potassium tool containing the transferred HEX_COLOR
         */

        dsl.transform().of(
                        ComponentRecipeDSL.source(ModItems.IMBUED_POTASSIUM_UPGRADE_SMITHING_TEMPLATE.get())
                                .require(ModDataComponents.HEX_COLOR)
                                .transfer(ModDataComponents.HEX_COLOR),
                        Ingredient.of(Items.DIAMOND_SWORD),
                        Ingredient.of(ModItems.POTASSIUM_INGOT.get()),
                        RecipeCategory.COMBAT,
                        ModItems.IMBUED_POTASSIUM_SWORD.get()
                )
                .unlocksHas(
                        ModItems.IMBUED_POTASSIUM_UPGRADE_SMITHING_TEMPLATE.get(),
                        ModItems.POTASSIUM_INGOT.get()
                )
                .save("smithing/imbued_potassium_sword_upgrade");


        dsl.transform().of(
                        ComponentRecipeDSL.source(ModItems.IMBUED_POTASSIUM_UPGRADE_SMITHING_TEMPLATE.get())
                                .require(ModDataComponents.HEX_COLOR)
                                .transfer(ModDataComponents.HEX_COLOR),
                        Ingredient.of(Items.DIAMOND_PICKAXE),
                        Ingredient.of(ModItems.POTASSIUM_INGOT.get()),
                        RecipeCategory.TOOLS,
                        ModItems.IMBUED_POTASSIUM_PICKAXE.get()
                )
                .unlocksHas(
                        ModItems.IMBUED_POTASSIUM_UPGRADE_SMITHING_TEMPLATE.get(),
                        ModItems.POTASSIUM_INGOT.get()
                )
                .save("smithing/imbued_potassium_pickaxe_upgrade");


        dsl.transform().of(
                        ComponentRecipeDSL.source(ModItems.IMBUED_POTASSIUM_UPGRADE_SMITHING_TEMPLATE.get())
                                .require(ModDataComponents.HEX_COLOR)
                                .transfer(ModDataComponents.HEX_COLOR),
                        Ingredient.of(Items.DIAMOND_AXE),
                        Ingredient.of(ModItems.POTASSIUM_INGOT.get()),
                        RecipeCategory.TOOLS,
                        ModItems.IMBUED_POTASSIUM_AXE.get()
                )
                .unlocksHas(
                        ModItems.IMBUED_POTASSIUM_UPGRADE_SMITHING_TEMPLATE.get(),
                        ModItems.POTASSIUM_INGOT.get()
                )
                .save("smithing/imbued_potassium_axe_upgrade");


        dsl.transform().of(
                        ComponentRecipeDSL.source(ModItems.IMBUED_POTASSIUM_UPGRADE_SMITHING_TEMPLATE.get())
                                .require(ModDataComponents.HEX_COLOR)
                                .transfer(ModDataComponents.HEX_COLOR),
                        Ingredient.of(Items.DIAMOND_SHOVEL),
                        Ingredient.of(ModItems.POTASSIUM_INGOT.get()),
                        RecipeCategory.TOOLS,
                        ModItems.IMBUED_POTASSIUM_SHOVEL.get()
                )
                .unlocksHas(
                        ModItems.IMBUED_POTASSIUM_UPGRADE_SMITHING_TEMPLATE.get(),
                        ModItems.POTASSIUM_INGOT.get()
                )
                .save("smithing/imbued_potassium_shovel_upgrade");


        dsl.transform().of(
                        ComponentRecipeDSL.source(ModItems.IMBUED_POTASSIUM_UPGRADE_SMITHING_TEMPLATE.get())
                                .require(ModDataComponents.HEX_COLOR)
                                .transfer(ModDataComponents.HEX_COLOR),
                        Ingredient.of(Items.DIAMOND_HOE),
                        Ingredient.of(ModItems.POTASSIUM_INGOT.get()),
                        RecipeCategory.TOOLS,
                        ModItems.IMBUED_POTASSIUM_HOE.get()
                )
                .unlocksHas(
                        ModItems.IMBUED_POTASSIUM_UPGRADE_SMITHING_TEMPLATE.get(),
                        ModItems.POTASSIUM_INGOT.get()
                )
                .save("smithing/imbued_potassium_hoe_upgrade");


        /*
         * NORMAL POTASSIUM ARMOR
         *
         * There are no imbued armor variants yet.
         */

        dsl.transform().of(
                        Ingredient.of(ModItems.POTASSIUM_UPGRADE_SMITHING_TEMPLATE.get()),
                        Ingredient.of(Items.DIAMOND_HELMET),
                        Ingredient.of(ModItems.POTASSIUM_INGOT.get()),
                        RecipeCategory.COMBAT,
                        ModItems.POTASSIUM_HELMET.get()
                )
                .unlocksHas(
                        ModItems.POTASSIUM_UPGRADE_SMITHING_TEMPLATE.get(),
                        ModItems.POTASSIUM_INGOT.get()
                )
                .save("smithing/potassium_helmet_upgrade");


        dsl.transform().of(
                        Ingredient.of(ModItems.POTASSIUM_UPGRADE_SMITHING_TEMPLATE.get()),
                        Ingredient.of(Items.DIAMOND_CHESTPLATE),
                        Ingredient.of(ModItems.POTASSIUM_INGOT.get()),
                        RecipeCategory.COMBAT,
                        ModItems.POTASSIUM_CHESTPLATE.get()
                )
                .unlocksHas(
                        ModItems.POTASSIUM_UPGRADE_SMITHING_TEMPLATE.get(),
                        ModItems.POTASSIUM_INGOT.get()
                )
                .save("smithing/potassium_chestplate_upgrade");


        dsl.transform().of(
                        Ingredient.of(ModItems.POTASSIUM_UPGRADE_SMITHING_TEMPLATE.get()),
                        Ingredient.of(Items.DIAMOND_LEGGINGS),
                        Ingredient.of(ModItems.POTASSIUM_INGOT.get()),
                        RecipeCategory.COMBAT,
                        ModItems.POTASSIUM_LEGGINGS.get()
                )
                .unlocksHas(
                        ModItems.POTASSIUM_UPGRADE_SMITHING_TEMPLATE.get(),
                        ModItems.POTASSIUM_INGOT.get()
                )
                .save("smithing/potassium_leggings_upgrade");


        dsl.transform().of(
                        Ingredient.of(ModItems.POTASSIUM_UPGRADE_SMITHING_TEMPLATE.get()),
                        Ingredient.of(Items.DIAMOND_BOOTS),
                        Ingredient.of(ModItems.POTASSIUM_INGOT.get()),
                        RecipeCategory.COMBAT,
                        ModItems.POTASSIUM_BOOTS.get()
                )
                .unlocksHas(
                        ModItems.POTASSIUM_UPGRADE_SMITHING_TEMPLATE.get(),
                        ModItems.POTASSIUM_INGOT.get()
                )
                .save("smithing/potassium_boots_upgrade");


        dsl.shaped(RecipeCategory.MISC, ModItems.POTASSIUM_UPGRADE_SMITHING_TEMPLATE.get(), 2)
                .pattern("xAx")
                .pattern("xPx")
                .pattern("xxx")
                .define('x', Items.DIAMOND)
                .define('A', ModItems.POTASSIUM_UPGRADE_SMITHING_TEMPLATE.get())
                .define('P', ModBlocks.MUSAVACCA_PLANKS.get())
                .unlockedByHas(
                        ModItems.POTASSIUM_UPGRADE_SMITHING_TEMPLATE.get(),
                        Items.DIAMOND,
                        ModBlocks.MUSAVACCA_PLANKS.get()
                )
                .save("smithing/potassium_upgrade_smithing_template_duplication");

        dsl.shaped(RecipeCategory.MISC, ModItems.FRACTURED_POTASSIUM_UPGRADE_SMITHING_TEMPLATE.get(), 2)
                .pattern("xAx")
                .pattern("xPx")
                .pattern("xxx")
                .define('x', Items.DIAMOND)
                .define('A', ModItems.FRACTURED_POTASSIUM_UPGRADE_SMITHING_TEMPLATE.get())
                .define('P', ModBlocks.MUSAVACCA_PLANKS.get())
                .unlockedByHas(
                        ModItems.FRACTURED_POTASSIUM_UPGRADE_SMITHING_TEMPLATE.get(),
                        Items.DIAMOND,
                        ModBlocks.MUSAVACCA_PLANKS.get()
                )
                .save("smithing/fractured_potassium_upgrade_smithing_template_duplication");


        dsl.shaped(RecipeCategory.MISC, ModItems.IMBUED_POTASSIUM_UPGRADE_SMITHING_TEMPLATE.get(), 2)
                .pattern("xAx")
                .pattern("xPx")
                .pattern("xxx")
                .define('x', Items.DIAMOND)
                .define('A', ComponentRecipeDSL.source(ModItems.IMBUED_POTASSIUM_UPGRADE_SMITHING_TEMPLATE.get())
                                .require(ModDataComponents.HEX_COLOR)
                                .transfer(ModDataComponents.HEX_COLOR))
                .define('P', ModBlocks.MUSAVACCA_PLANKS.get())
                .unlockedByHas(
                        ModItems.IMBUED_POTASSIUM_UPGRADE_SMITHING_TEMPLATE.get(),
                        Items.DIAMOND,
                        ModBlocks.MUSAVACCA_PLANKS.get()
                )
                .save("smithing/imbued_potassium_upgrade_smithing_template_duplication");
    }

}
