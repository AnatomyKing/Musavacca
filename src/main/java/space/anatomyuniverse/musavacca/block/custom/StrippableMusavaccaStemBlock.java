package space.anatomyuniverse.musavacca.block.custom;

import java.util.function.Supplier;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class StrippableMusavaccaStemBlock extends StrippableLogBlock {
    private final Supplier<? extends Block> exudatedStripped;

    public StrippableMusavaccaStemBlock(Properties properties, Supplier<? extends Block> stripped, Supplier<? extends Block> exudatedStripped) {
        super(properties, stripped);
        this.exudatedStripped = exudatedStripped;
    }

    @Override
    protected BlockState strippedState(BlockState state, UseOnContext context, boolean simulate) {
        if (!simulate && !context.getLevel().isClientSide() && context.getLevel().getRandom().nextFloat() < 0.095F) {
            return this.exudatedStripped.get().withPropertiesOf(state);
        }
        return super.strippedState(state, context, simulate);
    }
}
