package space.anatomyuniverse.musavacca.item;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;
import space.anatomyuniverse.musavacca.block.custom.logic.InteractionResultCompat;
import space.anatomyuniverse.musavacca.block.custom.logic.PearlCandleConversion;
import space.anatomyuniverse.musavacca.item.custom.PearlIgniter;

public final class PearlIgniterEvents {
    private PearlIgniterEvents() {}

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (!PearlIgniter.is(event.getItemStack()) || event.getEntity().isSpectator()) return;
        InteractionResult result = PearlIgniter.tryPickBlockColor(event.getItemStack(),
                new UseOnContext(event.getEntity(), event.getHand(), event.getHitVec()));
        if (result == InteractionResult.PASS) return;
        event.setCancellationResult(result);
        event.setCanceled(true);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onUseItemOnBlock(UseItemOnBlockEvent event) {
        if (event.getUsePhase() != UseItemOnBlockEvent.UsePhase.ITEM_AFTER_BLOCK || !PearlIgniter.is(event.getItemStack())) return;
        var state = event.getLevel().getBlockState(event.getPos());
        if (PearlCandleConversion.isCandle(state) && !PearlCandleConversion.value(state, "lit")
                && !PearlCandleConversion.value(state, "waterlogged")) return;
        InteractionResult result = PearlIgniter.useOn(event.getUseOnContext());
        //? if >=1.21.2 {
        event.cancelWithResult(result);
        //?} else {
        /*event.cancelWithResult(InteractionResultCompat.forItemUse(result));
        *///?}
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        var player = event.getEntity();
        if (!PearlIgniter.is(event.getItemStack()) || player.isSpectator()
                || !PearlIgniter.shouldOpenGui(event.getLevel(), player)) return;
        PearlIgniter.ensureDefaultColorComponent(event.getItemStack());
        if (player instanceof ServerPlayer serverPlayer) PearlIgniter.openGui(serverPlayer, event.getHand(), event.getItemStack());
        //? if >=1.21.2 {
        event.setCancellationResult(event.getLevel().isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER);
        //?} else {
        /*event.setCancellationResult(InteractionResult.sidedSuccess(event.getLevel().isClientSide()));
        *///?}
        event.setCanceled(true);
    }
}
