package space.anatomyuniverse.musavacca.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import space.anatomyuniverse.musavacca.block.custom.logic.GlinsterLogic;

public final class BananaPearlStairBlock extends StairBlock {
    public BananaPearlStairBlock(BlockState baseState, Properties properties) {
        super(baseState, properties);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        GlinsterLogic.animateTick(level, pos, random);
    }
}
