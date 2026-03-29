package space.anatomyuniverse.musavacca.item.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
//? if <1.21.2 {
/*import net.minecraft.world.InteractionResultHolder;
 *///?}
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FlintAndSteelItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.common.ItemAbilities;
import org.jetbrains.annotations.NotNull;
import space.anatomyuniverse.musavacca.block.ModBlocks;
import space.anatomyuniverse.musavacca.menu.ItemInteractMenu;

public class ItemInteract extends FlintAndSteelItem {

    private static final Component TITLE = Component.literal("Simple GUI");

    public ItemInteract(Properties properties) {
        super(properties);
    }

    private static EquipmentSlot slotForHand(InteractionHand hand) {
        return hand == InteractionHand.MAIN_HAND
                ? EquipmentSlot.MAINHAND
                : EquipmentSlot.OFFHAND;
    }

    private static void openGui(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.openMenu(new SimpleMenuProvider(
                    (containerId, playerInventory, openingPlayer) ->
                            new ItemInteractMenu(containerId, playerInventory),
                    TITLE
            ));
        }
    }

    private static boolean shouldOpenGui(Level level, Player player) {
        BlockHitResult hitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE);
        return hitResult.getType() == HitResult.Type.MISS;
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();

        BlockPos clickedPos = context.getClickedPos();
        BlockState clickedState = level.getBlockState(clickedPos);

        BlockState modifiedState = clickedState.getToolModifiedState(
                context,
                ItemAbilities.FIRESTARTER_LIGHT,
                false
        );

        if (modifiedState != null) {
            if (!level.isClientSide()) {
                level.setBlockAndUpdate(clickedPos, modifiedState);

                if (player != null) {
                    stack.hurtAndBreak(1, player, slotForHand(context.getHand()));
                }
            }
            return InteractionResult.SUCCESS;
        }

        BlockPos placePos = clickedPos.relative(context.getClickedFace());

        if (!level.getBlockState(placePos).canBeReplaced()) {
            return InteractionResult.FAIL;
        }

        BlockState placeState = ModBlocks.HEX_BLOCK.get().defaultBlockState();

        if (!level.isClientSide()) {
            boolean placed = level.setBlockAndUpdate(placePos, placeState);
            if (!placed) {
                return InteractionResult.FAIL;
            }

            placeState.getBlock().setPlacedBy(level, placePos, placeState, player, stack);

            if (player != null) {
                stack.hurtAndBreak(1, player, slotForHand(context.getHand()));
            }
        }

        return InteractionResult.SUCCESS;
    }

    //? if <1.21.2 {
    /*@Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (shouldOpenGui(level, player)) {
            if (!level.isClientSide()) {
                openGui(player);
            }
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
        }

        return InteractionResultHolder.pass(stack);
    }
    *///?} else {
    @Override
    public @NotNull InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (shouldOpenGui(level, player)) {
            if (!level.isClientSide()) {
                openGui(player);
            }
            return InteractionResult.SUCCESS_SERVER;
        }

        return InteractionResult.PASS;
    }
    //?}
}