package space.anatomyuniverse.musavacca.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
//? if <1.21.5
//import net.neoforged.neoforge.common.util.TriState;
//? if >=1.21.5
import net.minecraft.util.TriState;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LightEngine;
import space.anatomyuniverse.musavacca.block.ModBlocks;

public final class CaroteneGrassBlock extends SpreadingSnowyDirtBlock implements BonemealableBlock {
    public static final MapCodec<CaroteneGrassBlock> CODEC = simpleCodec(CaroteneGrassBlock::new);

    public CaroteneGrassBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends SpreadingSnowyDirtBlock> codec() {
        return CODEC;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!canStayCarotene(level, pos, state)) {
            level.setBlockAndUpdate(pos, Blocks.DIRT.defaultBlockState());
            return;
        }

        if (level.getMaxLocalRawBrightness(pos.above()) < 9) return;

        BlockState spread = this.defaultBlockState();
        for (int i = 0; i < 4; ++i) {
            BlockPos target = pos.offset(
                    random.nextInt(3) - 1,
                    random.nextInt(5) - 3,
                    random.nextInt(3) - 1
            );

            if (level.getBlockState(target).is(Blocks.ROOTED_DIRT)
                    && canSpreadCarotene(level, target, spread)) {
                level.setBlockAndUpdate(
                        target,
                        spread.setValue(SNOWY, level.getBlockState(target.above()).is(Blocks.SNOW))
                );
            }
        }
    }

    private static boolean canStayCarotene(LevelReader level, BlockPos pos, BlockState state) {
        BlockPos abovePos = pos.above();
        BlockState above = level.getBlockState(abovePos);

        if (above.is(Blocks.SNOW) && above.getValue(SnowLayerBlock.LAYERS) == 1) return true;
        if (above.getFluidState().getAmount() == 8) return false;

        //? if <1.21.2 {
        /*int lightBlock = LightEngine.getLightBlockInto(
                level,
                state,
                pos,
                above,
                abovePos,
                Direction.UP,
                above.getLightBlock(level, abovePos)
        );
        *///?} else {
        int lightBlock = LightEngine.getLightBlockInto(
                state,
                above,
                Direction.UP,
                above.getLightBlock()
        );
        //?}

        return lightBlock < LightEngine.MAX_LEVEL;
    }

    private static boolean canSpreadCarotene(LevelReader level, BlockPos pos, BlockState state) {
        return canStayCarotene(level, pos, state)
                && !level.getFluidState(pos.above()).is(FluidTags.WATER);
    }

    @Override
    public TriState canSustainPlant(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            Direction facing,
            BlockState plant
    ) {
        Block block = plant.getBlock();
        if (facing == Direction.UP
                && (block instanceof CropBlock
                || block instanceof StemBlock
                || block instanceof AttachedStemBlock
                || block instanceof PitcherCropBlock)) {
            return TriState.TRUE;
        }
        return TriState.DEFAULT;
    }

    @Override
    public boolean isFertile(BlockState state, BlockGetter level, BlockPos pos) {
        return true;
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
        return level.getBlockState(pos.above()).isAir();
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        BlockPos start = pos.above();

        outer:
        for (int i = 0; i < 128; ++i) {
            BlockPos target = start;

            for (int j = 0; j < i / 16; ++j) {
                target = target.offset(
                        random.nextInt(3) - 1,
                        (random.nextInt(3) - 1) * random.nextInt(3) / 2,
                        random.nextInt(3) - 1
                );

                if (!level.getBlockState(target.below()).is(this)
                        || level.getBlockState(target).isCollisionShapeFullBlock(level, target)) {
                    continue outer;
                }
            }

            if (!level.getBlockState(target).isAir()) continue;

            int roll = random.nextInt(8);

            if (roll == 0) {
                BlockState petals = Blocks.PINK_PETALS.defaultBlockState().setValue(
                        //? if <1.21.5 {
                        /*PinkPetalsBlock.AMOUNT,
                         *///?} else {
                        FlowerBedBlock.AMOUNT,
                        //?}
                        random.nextInt(4) + 1
                );

                if (petals.canSurvive(level, target)) {
                    level.setBlockAndUpdate(target, petals);
                }
            } else if (roll == 1) {
                BlockState tall = ModBlocks.CAROTENE_TALL_GRASS.get().defaultBlockState();

                if (level.isEmptyBlock(target.above()) && tall.canSurvive(level, target)) {
                    DoublePlantBlock.placeAt(level, tall, target, Block.UPDATE_ALL);
                }
            } else {
                BlockState shortGrass = ModBlocks.CAROTENE_SHORT_GRASS.get().defaultBlockState();

                if (shortGrass.canSurvive(level, target)) {
                    level.setBlockAndUpdate(target, shortGrass);
                }
            }
        }
    }
}
