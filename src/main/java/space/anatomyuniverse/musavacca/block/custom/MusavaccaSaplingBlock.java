// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/block/custom/MusavaccaSaplingBlock.java
package space.anatomyuniverse.musavacca.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.state.BlockState;
import space.anatomyuniverse.musavacca.worldgen.ModTreeGrowers;

public class MusavaccaSaplingBlock extends SaplingBlock {

    public MusavaccaSaplingBlock(Properties properties) {
        super(ModTreeGrowers.MUSAVACCA, properties);
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(Blocks.FARMLAND) || super.mayPlaceOn(state, level, pos);
    }

    @Override
    public void advanceTree(ServerLevel level, BlockPos pos, BlockState state, RandomSource random) {
        super.advanceTree(level, pos, state, random);

        if (!level.getBlockState(pos).is(this)) {
            level.setBlock(pos.below(), Blocks.ROOTED_DIRT.defaultBlockState(), 3);
        }
    }
}