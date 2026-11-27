package space.anatomyuniverse.musavacca.block.custom.logic;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.jetbrains.annotations.Nullable;
import space.anatomyuniverse.musavacca.block.ModBlocks;
import space.anatomyuniverse.musavacca.block.custom.PearlCopycatBlock;
import space.anatomyuniverse.musavacca.block.custom.PearlFlameBlock;
import space.anatomyuniverse.musavacca.block.custom.PearlCampfireBlock;
import space.anatomyuniverse.musavacca.block.entity.custom.PearlCampfireBlockEntity;
import space.anatomyuniverse.musavacca.block.entity.custom.PearlCopycatBlockEntity;
import space.anatomyuniverse.musavacca.item.PearlIgniter;

import java.util.function.Supplier;

public final class PearlFlameConversion {
    private PearlFlameConversion() {}

    @Nullable
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
        return (state.is(BlockTags.CANDLES) || state.is(BlockTags.CANDLE_CAKES) || state.is(BlockTags.CAMPFIRES)) && flag(state, "lit") != null;
    }

    @Nullable
    public static IntegerProperty countProperty(BlockState state) {
        return state.getBlock().getStateDefinition().getProperty("candles") instanceof IntegerProperty property ? property : null;
    }

    private static boolean canLight(BlockState state) {
        return isCandle(state)
                && !(state.getBlock() instanceof PearlCopycatBlock)
                && !value(state, "lit")
                && !value(state, "waterlogged");
    }

    public static InteractionResult useOn(
            Level level, Player player, ItemStack stack, BlockPos pos, Supplier<InteractionResult> interaction) {
        if (!PearlIgniter.is(stack) || player.isSpectator() || !player.getAbilities().mayBuild) return interaction.get();
        BlockState before = level.getBlockState(pos);
        if (!canLight(before)) return interaction.get();
        if (before.is(BlockTags.CAMPFIRES) && before.hasBlockEntity()
                && !(level.getBlockEntity(pos) instanceof CampfireBlockEntity)) return interaction.get();
        int hex = PearlIgniter.getStoredHexOrDefault(stack);
        return PearlFlameParticles.call(level, before, hex, () -> {
            InteractionResult result = interaction.get();
            if (result.consumesAction() && level.getBlockState(pos).getBlock() == before.getBlock()) convert(level, pos, hex);
            return result;
        });
    }

    private static void convert(Level level, BlockPos pos, int hex) {
        BlockState state = level.getBlockState(pos);
        if (!isCandle(state)
                || state.getBlock() instanceof PearlCopycatBlock
                || !value(state, "lit")
                || value(state, "waterlogged")) return;

        PearlFlameBlock block = state.is(BlockTags.CAMPFIRES) ? ModBlocks.PEARL_CAMPFIRE.get()
                : state.is(BlockTags.CANDLE_CAKES) ? ModBlocks.PEARL_CANDLE_CAKE.get()
                : ModBlocks.PEARL_CANDLE.get();
        CompoundTag campfireData = null;
        CampfireBlockEntity source = null;
        if (block instanceof PearlCampfireBlock) {
            if (level.getBlockEntity(pos) instanceof CampfireBlockEntity campfire) {
                source = campfire;
                campfireData = campfire.saveWithoutMetadata(level.registryAccess());
                campfire.clearContent();
            } else if (state.hasBlockEntity() || level.getBlockEntity(pos) != null) return;
        }

        if (!level.setBlock(pos, block.wrapState(state), Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE)) {
            if (source != null) PearlCampfireBlockEntity.loadCampfireData(source, campfireData, level);
            return;
        }
        if (level.getBlockEntity(pos) instanceof PearlCopycatBlockEntity copycat) {
            copycat.setCopycat(state, hex);
            if (campfireData != null && copycat instanceof PearlCampfireBlockEntity campfire) campfire.importCampfireData(campfireData);
            if (!level.isClientSide()) {
                level.updateNeighborsAt(pos, block);
                VocoPostCandleLogic.refreshPostBelowCandle(level, pos);
            }
        } else {
            level.setBlock(pos, state, Block.UPDATE_ALL);
            if (campfireData != null && level.getBlockEntity(pos) instanceof CampfireBlockEntity campfire) {
                PearlCampfireBlockEntity.loadCampfireData(campfire, campfireData, level);
                campfire.setChanged();
                level.sendBlockUpdated(pos, state, state, Block.UPDATE_CLIENTS);
            }
        }
    }
}
