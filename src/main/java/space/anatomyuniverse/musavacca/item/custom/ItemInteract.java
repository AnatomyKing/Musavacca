// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/item/custom/ItemInteract.java
package space.anatomyuniverse.musavacca.item.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
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
import space.anatomyuniverse.musavacca.tint.HexColorLcg;

public class ItemInteract extends FlintAndSteelItem {

    private static final Component TITLE = Component.literal("Simple GUI");

    public ItemInteract(Properties properties) {
        super(properties);
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

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();

        BlockPos clickedPos = context.getClickedPos();
        BlockState clickedState = level.getBlockState(clickedPos);

        // Keep normal "lightable block" behavior for campfires/candles/etc.
        BlockState modifiedState = clickedState.getToolModifiedState(
                context,
                ItemAbilities.FIRESTARTER_LIGHT,
                false
        );

        if (modifiedState != null) {
            if (!level.isClientSide()) {
                level.setBlockAndUpdate(clickedPos, modifiedState);

                if (player != null) {
                    stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(context.getHand()));
                }
            }

            return InteractionResult.SUCCESS;
        }

        // Otherwise place HexBlock in the adjacent position, like flint-and-steel would place fire.
        BlockPos placePos = clickedPos.relative(context.getClickedFace());

        if (!level.getBlockState(placePos).canBeReplaced()) {
            return InteractionResult.FAIL;
        }

        // Anti-flicker: reserve the predicted client color before the server places the block.
        if (level.isClientSide()) {
            HexColorLcg.reserveClientPlacementPrediction(placePos);
            return InteractionResult.SUCCESS;
        }

        BlockState placeState = ModBlocks.HEX_BLOCK.get().defaultBlockState();
        boolean placed = level.setBlockAndUpdate(placePos, placeState);

        if (!placed) {
            return InteractionResult.FAIL;
        }

        // Make sure your HexBlock still runs its normal placement hooks.
        placeState.getBlock().setPlacedBy(level, placePos, placeState, player, stack);

        if (player != null) {
            stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(context.getHand()));
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public @NotNull InteractionResult use(Level level, Player player, InteractionHand hand) {
        BlockHitResult hitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE);

        // Only open GUI when aiming at nothing (air / sky).
        if (hitResult.getType() == HitResult.Type.MISS) {
            if (!level.isClientSide()) {
                openGui(player);
            }
            return InteractionResult.SUCCESS_SERVER;
        }

        return InteractionResult.PASS;
    }
}