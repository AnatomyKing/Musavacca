package space.anatomyuniverse.musavacca.effect;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import space.anatomyuniverse.musavacca.item.ModItems;
import space.anatomyuniverse.musavacca.vococaller.VocoCallerNetwork;

public final class PlayerStatusEvents {

    private PlayerStatusEvents() {}

    @SubscribeEvent
    public static void onEffectApplicable(MobEffectEvent.Applicable event) {
        if (event.getEffectInstance().is(ModMobEffects.PLAYER_STATUS)
                && event.getEntity() instanceof ServerPlayer player
                && !hasBananaPhone(player)) {
            event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
        }
    }

    @SubscribeEvent
    public static void onPlayerTickPost(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)
                || VocoCallerNetwork.tickCall(player)) {
            return;
        }

        if (player.hasEffect(ModMobEffects.PLAYER_STATUS)
                && !hasBananaPhone(player)) {
            player.removeEffect(ModMobEffects.PLAYER_STATUS);
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            VocoCallerNetwork.cancelCalls(player);
        }
    }

    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        VocoCallerNetwork.clearCalls(event.getServer());
    }

    private static boolean hasBananaPhone(ServerPlayer player) {
        Inventory inventory = player.getInventory();

        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            if (inventory.getItem(slot).is(ModItems.BANANA_PHONE.get())) {
                return true;
            }
        }

        return false;
    }
}
