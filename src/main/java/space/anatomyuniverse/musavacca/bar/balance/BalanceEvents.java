package space.anatomyuniverse.musavacca.bar.balance;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import space.anatomyuniverse.musavacca.item.ModItems;
//? if >=26.3 {
/*import net.minecraft.util.Prediction;
*///?}

public final class BalanceEvents {
    private BalanceEvents() {}

    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        event.registrar("1").playToServer(BalanceWithdrawPayload.TYPE, BalanceWithdrawPayload.STREAM_CODEC, BalanceEvents::onWithdraw);
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            BalanceApi.refreshPlayer(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            BalanceApi.refreshPlayer(player);
        }
    }

    private static void onWithdraw(BalanceWithdrawPayload payload, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player) || !player.isAlive() || player.isSpectator()) return;
        int amount = Math.min(BalanceApi.getBalance(player), payload.bulk() ? 64 : 1);
        if (amount == 0 || !BalanceApi.deductBalance(player, amount)) return;
        //? if >=26.3 {
        /*ItemEntity dropped = player.drop(new ItemStack(ModItems.BANANA_PEARL.get(), amount), false, Prediction.SERVER_ONLY);
        *///?} else {
        ItemEntity dropped = player.drop(new ItemStack(ModItems.BANANA_PEARL.get(), amount), false);
        //?}
        if (dropped == null) {
            BalanceApi.addBalance(player, amount);
            return;
        }
        player.level()
                .playSound(null, player.getX(), player.getEyeY(), player.getZ(), SoundEvents.PLAYER_BURP, SoundSource.PLAYERS, 0.8F, 1.0F);
    }
}
