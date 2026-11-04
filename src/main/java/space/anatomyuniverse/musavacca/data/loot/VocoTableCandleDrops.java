package space.anatomyuniverse.musavacca.data.loot;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import space.anatomyuniverse.musavacca.MusaCore;
import space.anatomyuniverse.musavacca.block.ModBlocks;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoReceptorLogic.ReceptorPosition;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoTableCandleVoxelShapes;
import space.anatomyuniverse.musavacca.block.entity.custom.VocoTableBlockEntity;

@EventBusSubscriber(modid = MusaCore.MOD_ID)
public final class VocoTableCandleDrops {
    private VocoTableCandleDrops() {}

    @SubscribeEvent
    public static void onBlockDrops(BlockDropsEvent event) {
        if (!event.getState().is(ModBlocks.VOCO_TABLE.get())
                || !(event.getBlockEntity() instanceof VocoTableBlockEntity table)) return;

        for (ReceptorPosition receptor : ReceptorPosition.values()) {
            Block candle = table.getCandleBlock(receptor);
            int count = table.getCandleCount(receptor);
            if (candle == null || count <= 0) continue;

            var offset = VocoTableCandleVoxelShapes.dropPosition(receptor, count);
            var pos = event.getPos();
            ItemEntity drop = new ItemEntity(event.getLevel(), pos.getX() + offset.x,
                    pos.getY() + offset.y, pos.getZ() + offset.z, new ItemStack(candle.asItem(), count));
            event.getDrops().add(drop);
        }
    }
}
