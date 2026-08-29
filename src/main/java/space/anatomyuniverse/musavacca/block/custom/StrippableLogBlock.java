package space.anatomyuniverse.musavacca.block.custom;

import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class StrippableLogBlock extends RotatedPillarBlock {
    private final Supplier<? extends Block> stripped;

    public StrippableLogBlock(Properties props, Supplier<? extends Block> stripped) {
        super(props);
        this.stripped = stripped;
    }

    @Override
    public @Nullable BlockState getToolModifiedState(BlockState state, UseOnContext ctx, ItemAbility ability, boolean simulate) {
        if (ability == ItemAbilities.AXE_STRIP) {

            return stripped.get().withPropertiesOf(state);
        }
        return super.getToolModifiedState(state, ctx, ability, simulate);
    }
}
