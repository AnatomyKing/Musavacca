// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/block/custom/VocoReceptorBlock.java
package space.anatomyuniverse.musavacca.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
//? if <1.21.2 {
/*import net.minecraft.world.ItemInteractionResult;
 *///?}
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class VocoReceptorBlock extends Block {

    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    private static final int UPDATE_FLAGS = Block.UPDATE_ALL | Block.UPDATE_IMMEDIATE;
    private static final VoxelShape SHAPE = Block.box(
            5.0D, 0.0D, 5.0D,
            11.0D, 16.0D, 11.0D
    );

    public VocoReceptorBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(LIT, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(LIT);
    }

    private static void toggle(BlockState state, Level level, BlockPos pos) {
        if (!level.isClientSide()) {
            level.setBlock(pos, state.cycle(LIT), UPDATE_FLAGS);
        }
    }

    @Override
    protected InteractionResult useWithoutItem(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            BlockHitResult hit
    ) {
        toggle(state, level, pos);
        return InteractionResult.SUCCESS;
    }

    @Override
            //? if <1.21.2 {
    /*protected ItemInteractionResult useItemOn(
     *///?} else {
    protected InteractionResult useItemOn(
            //?}
            ItemStack stack,
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hit
    ) {
        if (hand == InteractionHand.OFF_HAND) {
            //? if <1.21.2 {
            /*return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
             *///?} else {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
            //?}
        }

        toggle(state, level, pos);

        //? if <1.21.2 {
        /*return ItemInteractionResult.SUCCESS;
         *///?} else {
        return InteractionResult.SUCCESS;
        //?}
    }

    @Override
    protected VoxelShape getShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        return SHAPE;
    }

    @Override
    protected VoxelShape getCollisionShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        return SHAPE;
    }
}