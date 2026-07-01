package space.anatomyuniverse.musavacca.data.models.engine.block;

import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredBlock;
import space.anatomyuniverse.musavacca.block.ModBlocks;
import space.anatomyuniverse.musavacca.block.custom.PearlCandleBlock;

public final class EngineBlockItems {
    private EngineBlockItems() {}

    public static boolean shouldSkip(Block block) {
        if (block == null) return true;
        for (DeferredBlock<? extends Block> skipped : ModBlocks.SKIP_BLOCK_ITEMS) {
            if (skipped.get() == block) return true;
        }
        for (DeferredBlock<PearlCandleBlock> candle : ModBlocks.PEARL_CANDLES) {
            if (candle.get() == block) return true;
        }
        return false;
    }
}
