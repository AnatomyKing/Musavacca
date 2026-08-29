package space.anatomyuniverse.musavacca.block.custom;

import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class StrippableMusavaccaStemBlock extends RotatedPillarBlock {
    private static final float EXUDATED_CHANCE = 0.095F; // 9.5%

    private final Supplier<? extends Block> stripped;
    private final Supplier<? extends Block> exudatedStripped;

    public StrippableMusavaccaStemBlock(
            Properties props,
            Supplier<? extends Block> stripped,
            Supplier<? extends Block> exudatedStripped
    ) {
        super(props);
        this.stripped = stripped;
        this.exudatedStripped = exudatedStripped;
    }

    @Override
    public @Nullable BlockState getToolModifiedState(BlockState state, UseOnContext ctx, ItemAbility ability, boolean simulate) {
        if (ability == ItemAbilities.AXE_STRIP) {
            Block resultBlock = shouldBecomeExudated(ctx, simulate)
                    ? exudatedStripped.get()
                    : stripped.get();

            return resultBlock.withPropertiesOf(state);
        }

        return super.getToolModifiedState(state, ctx, ability, simulate);
    }

    private boolean shouldBecomeExudated(UseOnContext ctx, boolean simulate) {
        if (simulate || ctx.getLevel().isClientSide()) {
            return false;
        }

        return ctx.getLevel().getRandom().nextFloat() < EXUDATED_CHANCE;
    }
}

