// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/bar/balance/BalanceEvents.java
package space.anatomyuniverse.musavacca.bar.balance;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import space.anatomyuniverse.musavacca.bar.ModAttachments;

public final class BalanceEvents {
    private static final int GOLDEN_APPLE_BALANCE_REWARD = 1;

    private BalanceEvents() {
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            BalanceData data = player.getData(ModAttachments.BALANCE);
            sendSync(player, data, hasBalanceHud(player));
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            BalanceData data = player.getData(ModAttachments.BALANCE);
            sendSync(player, data, hasBalanceHud(player));
        }
    }

    @SubscribeEvent
    public static void onFinishUsingItem(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        if (!hasBalanceHud(player)) {
            return;
        }

        ItemStack stack = event.getItem();

        if (!isBalanceApple(stack)) {
            return;
        }

        BalanceData data = player.getData(ModAttachments.BALANCE);
        boolean changed = data.addBalance(GOLDEN_APPLE_BALANCE_REWARD);

        if (changed) {
            player.setData(ModAttachments.BALANCE, data);
            sendSync(player, data, true);
        }
    }

    @SubscribeEvent
    public static void onPlayerTickPost(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        BalanceData data = player.getData(ModAttachments.BALANCE);
        boolean active = hasBalanceHud(player);

        if (data.shouldSync(active) || player.tickCount % 20 == 0) {
            sendSync(player, data, active);
        }
    }

    private static boolean isBalanceApple(ItemStack stack) {
        return stack.is(Items.GOLDEN_APPLE);
    }

    private static boolean hasBalanceHud(Player player) {
        return player.getItemBySlot(EquipmentSlot.HEAD).is(Items.GOLDEN_HELMET);
    }

    private static void sendSync(ServerPlayer player, BalanceData data, boolean active) {
        PacketDistributor.sendToPlayer(
                player,
                new BalanceSyncPayload(data.getBalance(), active)
        );
        data.markSynced(active);
    }
}