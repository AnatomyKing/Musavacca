package space.anatomyuniverse.musavacca.bar.balance;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import space.anatomyuniverse.musavacca.item.ModItems;

public final class BalanceEvents {
    private static final int BANANA_PEARL_BALANCE_REWARD = 1;

    private BalanceEvents() {
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

    @SubscribeEvent
    public static void onFinishUsingItem(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        ItemStack stack = event.getItem();

        if (stack.is(ModItems.BANANA_PEARL.get())) {
            BalanceApi.addBalance(player, BANANA_PEARL_BALANCE_REWARD);
        }
    }
}

