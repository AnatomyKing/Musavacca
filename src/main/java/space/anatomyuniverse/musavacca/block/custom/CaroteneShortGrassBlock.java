package space.anatomyuniverse.musavacca.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.TallGrassBlock;
import net.minecraft.world.level.block.state.BlockState;
import space.anatomyuniverse.musavacca.block.ModBlocks;

public final class CaroteneShortGrassBlock extends TallGrassBlock {
    public CaroteneShortGrassBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        BlockState tall = ModBlocks.CAROTENE_TALL_GRASS.get().defaultBlockState();

        if (level.isEmptyBlock(pos.above()) && tall.canSurvive(level, pos)) {
            DoublePlantBlock.placeAt(level, tall, pos, Block.UPDATE_ALL);
        }
    }
}
