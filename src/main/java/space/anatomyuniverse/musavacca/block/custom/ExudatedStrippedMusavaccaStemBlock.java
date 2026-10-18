package space.anatomyuniverse.musavacca.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import space.anatomyuniverse.musavacca.item.ModItems;

import java.util.function.Supplier;

//? if >=1.21.2 {
import net.minecraft.world.InteractionResult;
//?} else {
/*import net.minecraft.world.ItemInteractionResult;
*///?}

public class ExudatedStrippedMusavaccaStemBlock extends RotatedPillarBlock {
    public static final IntegerProperty STAGE =
            IntegerProperty.create("stage", 0, 2);

    private final Supplier<? extends Block> strippedStem;

    public ExudatedStrippedMusavaccaStemBlock(
            Properties properties,
            Supplier<? extends Block> strippedStem
    ) {
        super(properties);
        this.strippedStem = strippedStem;

        registerDefaultState(
                defaultBlockState()
                        .setValue(STAGE, 0)
        );
    }

    @Override
    //? if >=1.21.2 {
    protected InteractionResult useItemOn(
    //?} else {
    /*protected ItemInteractionResult useItemOn(
    *///?}
            ItemStack stack,
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hitResult
    ) {
        if (!stack.is(Items.GLASS_BOTTLE)) {
            //? if >=1.21.2 {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
            //?} else {
            /*return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            *///?}
        }

        if (!level.isClientSide()) {
            collectExudate(stack, state, level, pos, player, hand);
        }

        //? if >=1.21.2 {
        return InteractionResult.SUCCESS;
        //?} else {
        /*return ItemInteractionResult.sidedSuccess(level.isClientSide());
        *///?}
    }

    private void collectExudate(
            ItemStack glassBottles,
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand
    ) {
        ItemStack filledResult = ItemUtils.createFilledResult(
                glassBottles,
                player,
                new ItemStack(ModItems.MUSAVACCA_EXUDATE.get())
        );

        player.setItemInHand(hand, filledResult);

        int currentStage = state.getValue(STAGE);

        BlockState nextState;

        if (currentStage < 2) {
            nextState = state.setValue(STAGE, currentStage + 1);
        } else {

            nextState = strippedStem.get().withPropertiesOf(state);
        }

        level.setBlock(pos, nextState, Block.UPDATE_ALL);

        level.playSound(
                null,
                pos,
                SoundEvents.BOTTLE_FILL,
                SoundSource.BLOCKS,
                1.0F,
                1.0F
        );
    }

    @Override
    protected void createBlockStateDefinition(
            StateDefinition.Builder<Block, BlockState> builder
    ) {
        super.createBlockStateDefinition(builder);
        builder.add(STAGE);
    }
}
