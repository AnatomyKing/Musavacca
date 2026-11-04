package space.anatomyuniverse.musavacca.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import space.anatomyuniverse.musavacca.block.custom.logic.GlinsterLogic;

public final class BananaPearlPillarBlock extends RotatedPillarBlock {
    public BananaPearlPillarBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        GlinsterLogic.animateTick(level, pos, random);
    }
}
