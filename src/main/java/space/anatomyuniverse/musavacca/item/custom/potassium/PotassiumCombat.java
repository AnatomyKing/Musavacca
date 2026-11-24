package space.anatomyuniverse.musavacca.item.custom.potassium;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.CriticalHitEvent;
import net.neoforged.neoforge.event.entity.player.SweepAttackEvent;

public final class PotassiumCombat {
    private static final Field ATTACK_TICKER = field("attackStrengthTicker");
    //? if >=1.21.11 {
    /*private static final Field SWAP_TICKER = field("itemSwapTicker");
    *///?}
    private static final Method EQUIPMENT_UPDATE = equipmentUpdate();
    private static final ThreadLocal<Attack> ATTACK = new ThreadLocal<>();

    static void register() {
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, PotassiumCombat::onCriticalHit);
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, true, PotassiumCombat::onSweepAttack);
    }

    public static boolean attack(ServerPlayer owner, LivingEntity target, int slot, long deadline) {
        if (!owner.isAlive() || owner.isSpectator() || owner.isAutoSpinAttack() || !target.isAlive() || ATTACK.get() != null) return false;
        if (target instanceof Player player && !owner.canHarmPlayer(player)) return false;
        var stored = PotassiumThrow.getStored(owner, slot, deadline);
        if (stored == null) return false;
        Inventory inventory = owner.getInventory();
        int selected = PotassiumThrow.selectedSlot(owner);
        ItemStack placeholder = inventory.getItem(slot);
        ItemStack tool = stored.tool().copy();
        int ticker = read(ATTACK_TICKER, owner);
        //? if >=1.21.11 {
        /*int swapTicker = read(SWAP_TICKER, owner);
        *///?}
        float health = target.getHealth() + target.getAbsorptionAmount();
        if (slot != selected) inventory.setItem(slot, inventory.getItem(selected));
        inventory.setItem(selected, tool);
        try {
            refreshEquipment(owner);
            int charged = Math.max(1, (int) Math.ceil(owner.getCurrentItemAttackStrengthDelay()));
            write(ATTACK_TICKER, owner, charged);
            //? if >=1.21.11 {
            /*write(SWAP_TICKER, owner, charged);
            *///?}
            ATTACK.set(new Attack(owner, target));
            owner.attack(target);
        } finally {
            ATTACK.remove();
            if (owner.isAlive()) {
                ItemStack remaining = inventory.getItem(selected).copy();
                if (slot != selected) inventory.setItem(selected, inventory.getItem(slot));
                if (remaining.isEmpty()) inventory.setItem(slot, ItemStack.EMPTY);
                else {
                    placeholder.set(PotassiumThrow.STORED_TOOL.get(), new PotassiumThrow.StoredTool(remaining, deadline));
                    inventory.setItem(slot, placeholder);
                }
                inventory.setChanged();
            }
            try {
                if (owner.isAlive()) refreshEquipment(owner);
            } finally {
                write(ATTACK_TICKER, owner, ticker);
                //? if >=1.21.11 {
                /*write(SWAP_TICKER, owner, swapTicker);
                *///?}
            }
        }
        return !target.isAlive() || target.getHealth() + target.getAbsorptionAmount() < health;
    }

    private static boolean isThrow(Player player, Entity target) {
        Attack attack = ATTACK.get();
        return attack != null && attack.owner() == player && attack.target() == target;
    }

    private static void onCriticalHit(CriticalHitEvent event) {
        if (!isThrow(event.getEntity(), event.getTarget())) return;
        event.setCriticalHit(false);
        event.setDamageMultiplier(1.0F);
    }

    private static void onSweepAttack(SweepAttackEvent event) {
        if (!isThrow(event.getEntity(), event.getTarget())) return;
        event.setSweeping(false);
    }

    private static Field field(String name) {
        try {
            Field field = LivingEntity.class.getDeclaredField(name);
            field.setAccessible(true);
            return field;
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(name, e);
        }
    }

    private static Method equipmentUpdate() {
        try {
            Method method = LivingEntity.class.getDeclaredMethod("detectEquipmentUpdates");
            method.setAccessible(true);
            return method;
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }

    private static int read(Field field, ServerPlayer player) {
        try {
            return field.getInt(player);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    private static void write(Field field, ServerPlayer player, int value) {
        try {
            field.setInt(player, value);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    private static void refreshEquipment(ServerPlayer player) {
        try {
            EQUIPMENT_UPDATE.invoke(player);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }

    private record Attack(ServerPlayer owner, LivingEntity target) {}

    private PotassiumCombat() {}
}
