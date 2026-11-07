
package space.anatomyuniverse.musavacca.block.custom.logic;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import space.anatomyuniverse.musavacca.MusaCore;
import space.anatomyuniverse.musavacca.block.ModBlocks;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoReceptorLogic.ReceptorPosition;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoTableCandleVoxelShapes;
import space.anatomyuniverse.musavacca.block.entity.custom.VocoTableBlockEntity;

@EventBusSubscriber(modid = MusaCore.MOD_ID)
public final class VocoTableDrops {
    private VocoTableDrops() {}

    @SubscribeEvent
    public static void onBlockDrops(BlockDropsEvent event) {
        if (!event.getState().is(ModBlocks.VOCO_TABLE.get()) || !(event.getBlockEntity() instanceof VocoTableBlockEntity table)) return;

        var pos = event.getPos();

        for (ReceptorPosition receptor : ReceptorPosition.values()) {
            var candle = table.candle(receptor);
            if (candle == null) continue;
            var drop = VocoTableCandleVoxelShapes.dropPosition(event.getLevel(), pos, receptor, candle);
            int remaining = candle.count();
            int maxStackSize = Math.max(1, candle.item().getMaxStackSize());
            while (remaining > 0) {
                int count = Math.min(remaining, maxStackSize);
                event.getDrops().add(new ItemEntity(event.getLevel(), drop.x, drop.y, drop.z, candle.item().copyWithCount(count)));
                remaining -= count;
            }
        }

        if (!table.hasDisplayedItem()) return;

        ItemStack displayed = table.getDisplayedItem();
        int remaining = table.getDisplayedItemCount();
        int maxStackSize = Math.max(1, displayed.getMaxStackSize());

        while (remaining > 0) {
            int count = Math.min(remaining, maxStackSize);

            event.getDrops().add(new ItemEntity(event.getLevel(), pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, displayed.copyWithCount(count)));

            remaining -= count;
        }
    }
}
