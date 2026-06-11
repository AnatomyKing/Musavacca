
package space.anatomyuniverse.musavacca.item.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import space.anatomyuniverse.musavacca.block.ModBlocks;
import space.anatomyuniverse.musavacca.block.custom.SmallBananaPearlBlock;

public class SmallBananaPearlItem extends Item {

    public SmallBananaPearlItem(Properties properties) {
        super(properties);
    }

    private static InteractionResult passResult() {
        return InteractionResult.PASS;
    }

    private static InteractionResult successResult() {
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();

        BlockPos clickedPos = context.getClickedPos();
        BlockState clickedState = level.getBlockState(clickedPos);

        // 1) Clicking an existing pearl pile -> increment it
        if (SmallBananaPearlBlock.canAcceptPearl(clickedState)) {
            if (!level.isClientSide()) {
                BlockState next = SmallBananaPearlBlock.increment(clickedState);
                level.setBlock(clickedPos, next, Block.UPDATE_ALL);
                playPlaceSound(level, clickedPos, next, player);

                if (player == null || !player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
            }
            return successResult();
        }

        // 2) Otherwise try to place the first pearl pile block
        BlockPlaceContext placeContext = new BlockPlaceContext(context);

        BlockPos placePos = clickedState.canBeReplaced(placeContext)
                ? clickedPos
                : clickedPos.relative(context.getClickedFace());

        BlockState placeTarget = level.getBlockState(placePos);

        if (!placeTarget.canBeReplaced(placeContext)) {
            return passResult();
        }

        Direction facing = context.getHorizontalDirection().getOpposite();

        BlockState newState = ModBlocks.SMALL_BANANA_PEARL_BLOCK.get()
                .defaultBlockState()
                .setValue(SmallBananaPearlBlock.FACING, facing)
                .setValue(SmallBananaPearlBlock.SMALL_PEARL_AMOUNT, 1);

        if (!level.isClientSide()) {
            boolean placed = level.setBlock(placePos, newState, Block.UPDATE_ALL);
            if (!placed) {
                return passResult();
            }

            playPlaceSound(level, placePos, newState, player);

            if (player == null || !player.getAbilities().instabuild) {
                stack.shrink(1);
            }
        }

        return successResult();
    }

    private static void playPlaceSound(Level level, BlockPos pos, BlockState state, Player player) {
        //? if <1.21.6 {
        /*SoundType sound = state.getSoundType();
         *///?} else {
        SoundType sound = state.getSoundType(level, pos, player);
        //?}

        level.playSound(
                player,
                pos,
                sound.getPlaceSound(),
                SoundSource.BLOCKS,
                (sound.getVolume() + 1.0F) / 2.0F,
                sound.getPitch() * 0.8F
        );
    }
}