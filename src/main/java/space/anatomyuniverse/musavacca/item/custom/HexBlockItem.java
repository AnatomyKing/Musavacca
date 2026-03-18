// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/item/custom/HexBlockItem.java
package space.anatomyuniverse.musavacca.item.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Block;
import space.anatomyuniverse.musavacca.tint.HexColorLcg;

public class HexBlockItem extends BlockItem {

    public HexBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();

        if (!level.isClientSide()) {
            return super.useOn(context);
        }

        BlockPlaceContext placeContext = new BlockPlaceContext(context);
        BlockPlaceContext updatedContext = this.updatePlacementContext(placeContext);

        if (updatedContext == null) {
            return super.useOn(context);
        }

        BlockState placementState = this.getPlacementState(updatedContext);
        if (placementState == null || !this.canPlace(updatedContext, placementState)) {
            return super.useOn(context);
        }

        BlockPos pos = updatedContext.getClickedPos();

        long snapshot = HexColorLcg.snapshotClientState();
        HexColorLcg.reserveClientPlacementPrediction(pos);

        InteractionResult result = super.useOn(context);

        if (result == InteractionResult.FAIL) {
            HexColorLcg.restoreClientState(snapshot);
            HexColorLcg.clearClientPrediction(pos);
        }

        return result;
    }
}