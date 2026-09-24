package space.anatomyuniverse.musavacca.entity.mob.bananacow;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.neoforged.neoforge.event.EventHooks;
import space.anatomyuniverse.musavacca.block.ModBlockTags;
import space.anatomyuniverse.musavacca.block.ModBlocks;

import java.util.EnumSet;

final class BananaCowGrazeGoal extends Goal {

    private static final int EAT_ANIMATION_TICKS = 40;
    private static final int ADULT_EAT_CHANCE = 1000;
    private static final byte EAT_ANIMATION_EVENT = 10;

    private final BananaCow cow;
    private int eatAnimationTick;

    BananaCowGrazeGoal(BananaCow cow) {
        this.cow = cow;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
    }

    @Override
    public boolean canUse() {
        if (!this.cow.isBananaSheared()) {
            return false;
        }

        if (this.cow.getRandom().nextInt(ADULT_EAT_CHANCE) != 0) {
            return false;
        }

        BlockPos pos = this.cow.blockPosition();

        return isEdibleVegetation(this.cow.level().getBlockState(pos))
                || this.cow.level().getBlockState(pos.below())
                .is(ModBlocks.CAROTENE_GRASS.get());
    }

    @Override
    public void start() {
        this.eatAnimationTick =
                this.adjustedTickDelay(EAT_ANIMATION_TICKS);

        this.cow.level().broadcastEntityEvent(
                this.cow,
                EAT_ANIMATION_EVENT
        );

        this.cow.getNavigation().stop();
    }

    @Override
    public void stop() {
        this.eatAnimationTick = 0;
    }

    @Override
    public boolean canContinueToUse() {
        return this.eatAnimationTick > 0;
    }

    @Override
    public void tick() {
        this.eatAnimationTick = Math.max(
                0,
                this.eatAnimationTick - 1
        );

        if (this.eatAnimationTick != this.adjustedTickDelay(4)) {
            return;
        }

        if (!(this.cow.level() instanceof ServerLevel level)) {
            return;
        }

        BlockPos pos = this.cow.blockPosition();
        BlockState state = level.getBlockState(pos);

        if (isEdibleVegetation(state)) {
            if (EventHooks.canEntityGrief(level, this.cow)) {
                eatVegetation(level, pos, state);
            }

            this.cow.ate();
            return;
        }

        BlockPos groundPos = pos.below();
        BlockState groundState = level.getBlockState(groundPos);

        if (!groundState.is(ModBlocks.CAROTENE_GRASS.get())) {
            return;
        }

        if (EventHooks.canEntityGrief(level, this.cow)) {
            level.levelEvent(
                    LevelEvent.PARTICLES_DESTROY_BLOCK,
                    groundPos,
                    Block.getId(groundState)
            );

            level.setBlockAndUpdate(
                    groundPos,
                    Blocks.ROOTED_DIRT.defaultBlockState()
            );
        }

        this.cow.ate();
    }

    private static void eatVegetation(
            ServerLevel level,
            BlockPos pos,
            BlockState state
    ) {
        if (state.is(ModBlocks.CAROTENE_TALL_GRASS.get())) {
            eatTallGrass(level, pos, state);
            return;
        }

        if (state.is(Blocks.PINK_PETALS)) {
            eatPinkPetal(level, pos, state);
            return;
        }

        level.destroyBlock(pos, false);
    }

    private static void eatTallGrass(
            ServerLevel level,
            BlockPos pos,
            BlockState state
    ) {
        BlockPos lowerPos =
                state.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.UPPER
                        ? pos.below()
                        : pos;

        level.levelEvent(
                LevelEvent.PARTICLES_DESTROY_BLOCK,
                pos,
                Block.getId(state)
        );

        level.setBlockAndUpdate(
                lowerPos,
                ModBlocks.CAROTENE_SHORT_GRASS.get()
                        .defaultBlockState()
        );
    }

    private static void eatPinkPetal(
            ServerLevel level,
            BlockPos pos,
            BlockState state
    ) {
        int amount =
                state.getValue(
                        BlockStateProperties.FLOWER_AMOUNT
                );

        if (amount <= 1) {
            level.destroyBlock(pos, false);
            return;
        }

        level.levelEvent(
                LevelEvent.PARTICLES_DESTROY_BLOCK,
                pos,
                Block.getId(state)
        );

        level.setBlockAndUpdate(
                pos,
                state.setValue(
                        BlockStateProperties.FLOWER_AMOUNT,
                        amount - 1
                )
        );
    }

    int getEatAnimationTick() {
        return this.eatAnimationTick;
    }

    private static boolean isEdibleVegetation(BlockState state) {
        return state.is(
                ModBlockTags.EDIBLE_FOR_BANANA_COW
        );
    }
}