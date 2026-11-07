package space.anatomyuniverse.musavacca.block.custom.logic;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import space.anatomyuniverse.musavacca.block.ModBlocks;
import space.anatomyuniverse.musavacca.block.custom.PearlCandleBlock;
import space.anatomyuniverse.musavacca.block.entity.custom.PearlCandleBlockEntity;
import space.anatomyuniverse.musavacca.item.custom.PearlIgniter;
import space.anatomyuniverse.musavacca.particle.tinted.OriginalCandleParticles;

public final class PearlCandleConversion {
    private PearlCandleConversion() {}

    public static BooleanProperty flag(BlockState state, String name) {
        return state.getBlock().getStateDefinition().getProperty(name) instanceof BooleanProperty property ? property : null;
    }

    public static boolean value(BlockState state, String name) {
        var property = flag(state, name);
        return property != null && state.getValue(property);
    }

    public static BlockState withFlag(BlockState state, String name, boolean value) {
        var property = flag(state, name);
        return property == null ? state : state.setValue(property, value);
    }

    public static boolean isCandle(BlockState state) {
        return (state.is(BlockTags.CANDLES) || state.is(BlockTags.CANDLE_CAKES)) && flag(state, "lit") != null;
    }

    public static int count(BlockState state) {
        return state.getBlock().getStateDefinition().getProperty("candles") instanceof IntegerProperty property
                ? state.getValue(property) : 1;
    }

    public static boolean canLight(BlockState state) {
        return isCandle(state) && !(state.getBlock() instanceof PearlCandleBlock)
                && !value(state, "lit") && !value(state, "waterlogged") && !state.hasBlockEntity();
    }

    public static InteractionResult useOn(Level level, Player player, ItemStack stack, BlockPos pos, Supplier<InteractionResult> interaction) {
        if (!PearlIgniter.is(stack) || player.isSpectator() || !player.getAbilities().mayBuild) return interaction.get();
        BlockState before = level.getBlockState(pos);
        if (!canLight(before)) return interaction.get();
        int hex = PearlIgniter.getStoredHexOrDefault(stack);
        boolean client = level.isClientSide();
        Integer previous = client ? OriginalCandleParticles.swap(hex) : null;
        try {
            InteractionResult result = interaction.get();
            if (result.consumesAction() && level.getBlockState(pos).getBlock() == before.getBlock()) convert(level, pos, hex);
            return result;
        } finally {
            if (client) OriginalCandleParticles.swap(previous);
        }
    }

    private static void convert(Level level, BlockPos pos, int hex) {
        BlockState state = level.getBlockState(pos);
        if (!isCandle(state) || state.getBlock() instanceof PearlCandleBlock || !value(state, "lit")
                || value(state, "waterlogged") || state.hasBlockEntity() || level.getBlockEntity(pos) != null) return;

        var block = ModBlocks.PEARL_CANDLE.get();
        if (!level.setBlock(pos, block.wrapState(state), Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE)) return;
        if (level.getBlockEntity(pos) instanceof PearlCandleBlockEntity candle) {
            candle.setOriginalState(state);
            candle.setHexColor(hex);
            if (!level.isClientSide()) {
                level.updateNeighborsAt(pos, block);
                VocoPostCandleLogic.refreshPostBelowCandle(level, pos);
            }
        } else {
            level.setBlock(pos, state, Block.UPDATE_ALL);
        }
    }
}
