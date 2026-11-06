package space.anatomyuniverse.musavacca.item;

import java.util.ArrayDeque;
import java.util.Queue;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import space.anatomyuniverse.musavacca.block.custom.logic.InteractionResultCompat;
import space.anatomyuniverse.musavacca.block.custom.logic.PearlCandleConversion;
import space.anatomyuniverse.musavacca.item.custom.PearlIgniter;

public final class PearlIgniterEvents {
    private static final Queue<Ignition> pending = new ArrayDeque<>();
    private PearlIgniterEvents() {}

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (!PearlIgniter.is(event.getItemStack()) || event.getEntity().isSpectator()) return;
        InteractionResult result = PearlIgniter.tryPickBlockColor(event.getItemStack(),
                new UseOnContext(event.getEntity(), event.getHand(), event.getHitVec()));
        if (result != InteractionResult.PASS) {
            event.setCancellationResult(result);
            event.setCanceled(true);
            return;
        }
        if (!(event.getEntity() instanceof ServerPlayer player) || !player.getAbilities().mayBuild) return;
        var state = event.getLevel().getBlockState(event.getPos());
        if (PearlCandleConversion.canLight(state)) {
            pending.add(new Ignition(event, state.getBlock(), PearlIgniter.getStoredHexOrDefault(event.getItemStack())));
        }
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        for (int remaining = pending.size(); remaining > 0; remaining--) {
            var ignition = pending.remove();
            var interaction = ignition.event();
            if (interaction.isCanceled() && !interaction.getCancellationResult().consumesAction()) continue;
            var level = interaction.getLevel();
            var pos = interaction.getPos();
            if (!level.hasChunkAt(pos) || level.getBlockState(pos).getBlock() != ignition.block()) continue;
            PearlCandleConversion.convert(level, pos, ignition.hex());
        }
    }

    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event) {
        pending.clear();
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

    private record Ignition(PlayerInteractEvent.RightClickBlock event, Block block, int hex) {}
}
