package space.anatomyuniverse.musavacca.data.loot;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import space.anatomyuniverse.musavacca.block.ModBlocks;
import space.anatomyuniverse.musavacca.component.ModDataComponents;
import space.anatomyuniverse.musavacca.item.ModItems;

import java.util.Set;

public final class ModBlockLootProvider extends BlockLootSubProvider {

    public ModBlockLootProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        dropSelfAll(
                ModBlocks.BANANA_PEARL_BLOCK.get(),
                ModBlocks.BANANA_PEARL_BRICKS.get(),
                ModBlocks.BANANA_PEARL_CHAPITER.get(),
                ModBlocks.BANANA_PEARL_PILLAR.get(),
                ModBlocks.MUSAVACCA_STEM.get(),
                ModBlocks.STRIPPED_MUSAVACCA_STEM.get(),
                ModBlocks.MUSAVACCA_PLANKS.get(),
                ModBlocks.MUSAVACCA_LEAVES.get(),
                ModBlocks.MUSAVACCA_EGG.get()
        );

        dropSelfWithCopiedComponents(ModBlocks.HEX_BLOCK.get());
        dropSelfWithCopiedComponents(ModBlocks.HARD_HEX_BLOCK.get());

        this.add(
                ModBlocks.BANANA_PEARL_CHALICE.get(),
                this.createSingleItemTable(
                        ModItems.SMALL_BANANA_PEARL.get(),
                        ConstantValue.exactly(6.0F)
                )
        );

        dropOther(ModBlocks.SMALL_BANANA_PEARL_BLOCK.get(), ModItems.SMALL_BANANA_PEARL.get());
    }

    private void dropSelfAll(Block... blocks) {
        for (Block b : blocks) {
            dropSelf(b);
        }
    }

    private void dropSelfWithCopiedComponents(Block block) {
        this.add(
                block,
                LootTable.lootTable()
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ConstantValue.exactly(1.0F))
                                        .add(
                                                this.applyExplosionCondition(
                                                        block,
                                                        LootItem.lootTableItem(block)
                                                                .apply(
                                                                        CopyComponentsFunction
                                                                                .copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY)
                                                                                .include(ModDataComponents.HEX_COLOR.get())
                                                                )
                                                )
                                        )
                        )
        );
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream()
                .map(h -> (Block) h.get())
                .toList();
    }
}