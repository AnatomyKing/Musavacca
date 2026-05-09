// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/block/custom/MusavaccaCropBlock.java
package space.anatomyuniverse.musavacca.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import space.anatomyuniverse.musavacca.block.ModBlocks;
import space.anatomyuniverse.musavacca.item.ModItems;

public class MusavaccaCropBlock extends CropBlock {
    public static final IntegerProperty AGE = BlockStateProperties.AGE_3;

    private final int stageAge;

    public MusavaccaCropBlock(int stageAge, Properties properties) {
        super(properties);

        if (stageAge < 0 || stageAge > 2) {
            throw new IllegalArgumentException("MusavaccaCropBlock stageAge must be 0, 1, or 2.");
        }

        this.stageAge = stageAge;
        this.registerDefaultState(this.stateDefinition.any().setValue(AGE, stageAge));
    }

    public int getStageAge() {
        return stageAge;
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(Blocks.FARMLAND);
    }

    @Override
    protected IntegerProperty getAgeProperty() {
        return AGE;
    }

    @Override
    public int getMaxAge() {
        return 3;
    }

    @Override
    public BlockState getStateForAge(int age) {
        int clampedAge = Math.max(0, Math.min(age, getMaxAge()));

        return switch (clampedAge) {
            case 0 -> ModBlocks.MUSAVACCA_SPROUT.get().defaultBlockState().setValue(AGE, 0);
            case 1 -> ModBlocks.MUSAVACCA_SUCKER.get().defaultBlockState().setValue(AGE, 1);
            case 2 -> ModBlocks.MUSAVACCA_PLANT.get().defaultBlockState().setValue(AGE, 2);
            default -> ModBlocks.MUSAVACCA_PSEUDOSTEM.get().defaultBlockState();
        };
    }

    @Override
    public void growCrops(Level level, BlockPos pos, BlockState state) {
        int nextAge = Math.min(this.getAge(state) + 1, this.getMaxAge());
        level.setBlock(pos, this.getStateForAge(nextAge), Block.UPDATE_CLIENTS);
    }

    @Override
    protected ItemLike getBaseSeedId() {
        return ModItems.MUSAVACCA_PUP;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }
}