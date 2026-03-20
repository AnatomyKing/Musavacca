// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/item/custom/HexBlockItem.java
package space.anatomyuniverse.musavacca.item.custom;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;

public class HexBlockItem extends BlockItem {

    private static final ThreadLocal<Boolean> FORCE_WHITE_PLACEMENT =
            ThreadLocal.withInitial(() -> false);

    public HexBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    public static boolean isForcingWhitePlacement() {
        return FORCE_WHITE_PLACEMENT.get();
    }

    @Override
    public InteractionResult place(BlockPlaceContext context) {
        FORCE_WHITE_PLACEMENT.set(true);
        try {
            return super.place(context);
        } finally {
            FORCE_WHITE_PLACEMENT.remove();
        }
    }
}