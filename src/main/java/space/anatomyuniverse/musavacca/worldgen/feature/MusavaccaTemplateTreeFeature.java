package space.anatomyuniverse.musavacca.worldgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
//? if <26.3
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

import java.util.ArrayList;
import java.util.List;

//? if <26.3 {
public final class MusavaccaTemplateTreeFeature extends Feature<MusavaccaTemplateTreeConfiguration> {

    public MusavaccaTemplateTreeFeature(Codec<MusavaccaTemplateTreeConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<MusavaccaTemplateTreeConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        MusavaccaTemplateTreeConfiguration config = context.config();

//?} else {
/*public record MusavaccaTemplateTreeFeature(MusavaccaTemplateTreeConfiguration config) implements Feature {
    public static final com.mojang.serialization.MapCodec<MusavaccaTemplateTreeFeature> CODEC =
            MusavaccaTemplateTreeConfiguration.CODEC.fieldOf("config")
                    .xmap(MusavaccaTemplateTreeFeature::new, MusavaccaTemplateTreeFeature::config);
    @Override
    public com.mojang.serialization.MapCodec<MusavaccaTemplateTreeFeature> codec() { return CODEC; }
    @Override
    public java.util.stream.Stream<net.minecraft.core.Holder<Feature>> getSubFeatures() { return java.util.stream.Stream.empty(); }
    @Override
    public boolean place(WorldGenLevel level, net.minecraft.world.level.chunk.ChunkGenerator generator,
                         net.minecraft.util.RandomSource random, BlockPos origin) {
*///?}
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

