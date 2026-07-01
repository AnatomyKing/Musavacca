package space.anatomyuniverse.musavacca.tint;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
//? if <1.21.4 {
/*import net.minecraft.world.item.Item;
 *///?}
import space.anatomyuniverse.musavacca.data.models.engine.tint.EngineTintRules;

public final class ModTints {
    private static BlockTintRegistry blockTints;

    private ModTints() {}

    public static void register(IEventBus modBus) {
        modBus.addListener(ModTints::registerBlockColorHandlers);

        //? if <1.21.4 {
        /*modBus.addListener(ModTints::registerItemColorHandlers);
         *///?}
    }

    public static void registerBlockColorHandlers(RegisterColorHandlersEvent.Block event) {
        BlockTintRegistry tints = blockTints();

        if (!tints.isEmpty()) {
            event.register(ModTints::getModelTint, tints.blocks());
        }
    }

    private static int getModelTint(
            BlockState state,
            BlockAndTintGetter level,
            BlockPos pos,
            int tintIndex
    ) {
        return blockTints().color(state, level, pos, tintIndex);
    }

    private static BlockTintRegistry blockTints() {
        if (blockTints == null) {
            blockTints = BlockTintRegistry.of(EngineTintRules.blockTintRules());
        }

        return blockTints;
    }

    //? if <1.21.4 {
    /*public static void registerItemColorHandlers(RegisterColorHandlersEvent.Item event) {
        Item[] items = EngineTintRules.legacyItemTintItems();
        if (items.length > 0) {
            event.register(EngineTintRules::legacyItemTint, items);
        }
    }
    *///?}
}
