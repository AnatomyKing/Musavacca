package space.anatomyuniverse.musavacca.worldgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

import java.util.ArrayList;
import java.util.List;

public final class MusavaccaTemplateTreeFeature extends Feature<MusavaccaTemplateTreeConfiguration> {

    public MusavaccaTemplateTreeFeature(Codec<MusavaccaTemplateTreeConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<MusavaccaTemplateTreeConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        MusavaccaTemplateTreeConfiguration config = context.config();

        List<ResolvedBlock> resolvedBlocks = new ArrayList<>(config.blocks().size());

        for (MusavaccaTemplateTreeConfiguration.TemplateBlock templateBlock : config.blocks()) {
            BlockPos targetPos = origin.offset(templateBlock.offset());
            BlockState targetState = templateBlock.state().resolve();

            if (!canReplace(level, targetPos)) {
                return false;
            }

            resolvedBlocks.add(new ResolvedBlock(targetPos, targetState));
        }

        for (ResolvedBlock block : resolvedBlocks) {
            level.setBlock(block.pos(), block.state(), Block.UPDATE_ALL);
        }

        return !resolvedBlocks.isEmpty();
    }

    private static boolean canReplace(WorldGenLevel level, BlockPos pos) {
        BlockState existing = level.getBlockState(pos);

        return existing.canBeReplaced()
                || existing.is(BlockTags.LEAVES)
                || existing.is(BlockTags.LOGS);
    }

    private record ResolvedBlock(BlockPos pos, BlockState state) {}
}