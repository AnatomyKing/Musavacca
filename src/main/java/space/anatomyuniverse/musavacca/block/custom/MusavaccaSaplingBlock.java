// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/block/custom/MusavaccaSaplingBlock.java
package space.anatomyuniverse.musavacca.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockState;

public class MusavaccaSaplingBlock extends SaplingBlock {
    public MusavaccaSaplingBlock(Properties properties) {
        super(TreeGrower.OAK, properties);
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(Blocks.FARMLAND) || super.mayPlaceOn(state, level, pos);
    }
}