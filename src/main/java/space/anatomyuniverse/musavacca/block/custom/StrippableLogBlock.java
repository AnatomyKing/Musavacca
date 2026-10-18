package space.anatomyuniverse.musavacca.block.custom;
//? if >=26.3 {
/*import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;

import net.minecraft.world.level.gameevent.GameEvent;

import net.minecraft.world.item.component.BlockTransformers;

import net.minecraft.world.InteractionHand;

import net.minecraft.world.entity.EquipmentSlot;

import net.minecraft.world.InteractionResult;

import net.minecraft.sounds.SoundSource;

import net.minecraft.sounds.SoundEvents;

import net.minecraft.core.component.DataComponents;
*///?}

import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class StrippableLogBlock extends RotatedPillarBlock {
    private final Supplier<? extends Block> stripped;

    public StrippableLogBlock(Properties props, Supplier<? extends Block> stripped) {
        super(props);
        this.stripped = stripped;
    }

    //? if <26.3 {
    @Override
    public @Nullable BlockState getToolModifiedState(BlockState state, UseOnContext ctx, ItemAbility ability, boolean simulate) {
        if (ability == ItemAbilities.AXE_STRIP) {
            return strippedState(state, ctx, simulate);
        }
        return super.getToolModifiedState(state, ctx, ability, simulate);
    }

    //?}
    private BlockState strippedState(BlockState state, UseOnContext ctx, boolean simulate) {

            return stripped.get().withPropertiesOf(state);
    }
    //? if >=26.3 {
    /*// Axes use the block-transformer component starting in 26.3.
    public static void onUseItemOnBlock(UseItemOnBlockEvent event) {
        if (event.getUsePhase() != UseItemOnBlockEvent.UsePhase.ITEM_AFTER_BLOCK) return;
        var context = event.getUseOnContext();
        var stack = context.getItemInHand();
        var transformer = stack.get(DataComponents.BLOCK_TRANSFORMER);
        if (transformer == null || !transformer.is(BlockTransformers.AXE)) return;
        var level = context.getLevel();
        var pos = context.getClickedPos();
        var state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof StrippableLogBlock block)) return;
        var player = context.getPlayer();
        var stripped = block.strippedState(state, context, level.isClientSide());
        level.playSound(player, pos, SoundEvents.AXE_STRIP.value(), SoundSource.BLOCKS, 1.0F, 1.0F);
        if (!level.isClientSide()) {
            level.setBlock(pos, stripped, Block.UPDATE_ALL);
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, stripped));
            if (player != null) stack.hurtAndBreak(1, player,
                    context.getHand() == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
        }
        event.cancelWithResult(InteractionResult.SUCCESS);
    }
    *///?}
}
