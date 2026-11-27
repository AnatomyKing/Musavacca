package space.anatomyuniverse.musavacca.block.custom.logic;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.jetbrains.annotations.Nullable;
import space.anatomyuniverse.musavacca.block.custom.PearlCopycatBlock;
import space.anatomyuniverse.musavacca.tint.MusavaccaTints;

import java.util.Collections;

public record VocoTableCandle(BlockState state, ItemStack item, int hex) {
    public static final Codec<VocoTableCandle> CODEC =
            RecordCodecBuilder.create(instance ->
                    instance.group(
                                    BlockState.CODEC.fieldOf("state").forGetter(VocoTableCandle::state),
                                    ItemStack.CODEC.fieldOf("item").forGetter(VocoTableCandle::item),
                                    Codec.intRange(-1, 0xFFFFFF)
                                            .optionalFieldOf("hex", MusavaccaTints.NO_TINT)
                                            .forGetter(VocoTableCandle::hex))
                            .apply(instance, VocoTableCandle::new));

    public static boolean accepts(BlockState state) {
        return state.is(BlockTags.CANDLES)
                && !state.is(BlockTags.CANDLE_CAKES)
                && !state.hasBlockEntity()
                && !(state.getBlock() instanceof PearlCopycatBlock)
                && PearlFlameConversion.flag(state, "lit") != null;
    }

    @Nullable
    public static VocoTableCandle place(BlockItem blockItem, BlockPlaceContext context) {
        BlockState state = blockItem.getBlock().getStateForPlacement(context);
        if (state == null) return null;
        ItemStack stack = context.getItemInHand();
        var properties = stack.get(DataComponents.BLOCK_STATE);
        if (properties != null) state = properties.apply(state);
        if (!accepts(state)) return null;
        var count = PearlFlameConversion.countProperty(state);
        if (count != null) state = state.setValue(count, first(count));
        state = PearlFlameConversion.withFlag(state, "lit", false);
        state = PearlFlameConversion.withFlag(state, "waterlogged", false);
        return new VocoTableCandle(state, stack.copyWithCount(1), MusavaccaTints.NO_TINT);
    }

    public boolean lit() {
        return PearlFlameConversion.value(state, "lit");
    }

    public VocoTableCandle withFlame(boolean lit, int color) {
        return new VocoTableCandle(
                PearlFlameConversion.withFlag(state, "lit", lit), item, lit ? MusavaccaTints.resolve(color) : MusavaccaTints.NO_TINT);
    }

    public int count() {
        var property = PearlFlameConversion.countProperty(state);
        return property == null ? 1 : state.getValue(property) - first(property) + 1;
    }

    @Nullable
    public VocoTableCandle addOne(ItemStack held) {
        if (!ItemStack.isSameItemSameComponents(item, held)) return null;
        var property = PearlFlameConversion.countProperty(state);
        if (property == null) return null;
        int next = state.getValue(property) + 1;
        return property.getPossibleValues().contains(next) ? new VocoTableCandle(state.setValue(property, next), item, hex) : null;
    }

    @Nullable
    public VocoTableCandle removeOne() {
        var property = PearlFlameConversion.countProperty(state);
        if (property == null || state.getValue(property) <= first(property)) return null;
        return new VocoTableCandle(state.setValue(property, state.getValue(property) - 1), item, hex);
    }

    private static int first(IntegerProperty count) {
        return count.getPossibleValues().contains(1) ? 1 : Collections.min(count.getPossibleValues());
    }
}
