package space.anatomyuniverse.musavacca.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import space.anatomyuniverse.musavacca.block.entity.custom.HexBlockEntity;
import space.anatomyuniverse.musavacca.component.ModDataComponents;
import space.anatomyuniverse.musavacca.particle.ModParticleTypes;
import space.anatomyuniverse.musavacca.particle.utils.HexColorParticleOptions;
import space.anatomyuniverse.musavacca.tint.TintColorUtil;

public class HexBlock extends Block implements EntityBlock, BonemealableBlock {
    public static final MapCodec<HexBlock> CODEC = simpleCodec(HexBlock::new);
    private static final VoxelShape SHAPE = Block.column(9.0, 8.0, 16.0);

    private static final int ADD_PARTICLE_ATTEMPTS = 14;
    private static final int PARTICLE_XZ_RADIUS = 10;
    private static final int PARTICLE_Y_MAX = 10;

    @Override
    public MapCodec<HexBlock> codec() {
        return CODEC;
    }

    public HexBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new HexBlockEntity(pos, state);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return Block.canSupportCenter(level, pos.above(), Direction.DOWN) && !level.isWaterAt(pos);
    }

    @Override
    protected BlockState updateShape(
            BlockState state,
            LevelReader level,
            ScheduledTickAccess scheduledTickAccess,
            BlockPos pos,
            Direction direction,
            BlockPos neighborPos,
            BlockState neighborState,
            RandomSource random
    ) {
        return direction == Direction.UP && !this.canSurvive(state, level, pos)
                ? Blocks.AIR.defaultBlockState()
                : super.updateShape(state, level, scheduledTickAccess, pos, direction, neighborPos, neighborState, random);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);

        if (level.isClientSide()) {
            return;
        }

        if (oldState.is(state.getBlock())) {
            return;
        }

        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof HexBlockEntity hexBe) {
            hexBe.ensureRandomHexColor();
        }
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);

        if (level.isClientSide()) {
            return;
        }

        Integer savedHex = stack.get(ModDataComponents.HEX_COLOR.get());
        if (savedHex == null) {
            return;
        }

        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof HexBlockEntity hexBe) {
            hexBe.setHexColor(savedHex);
        }
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();

        int rgb = getParticleRgb(level, pos);

        double d0 = (double) i + random.nextDouble();
        double d1 = (double) j + 0.7;
        double d2 = (double) k + random.nextDouble();

        level.addParticle(
                new HexColorParticleOptions(ModParticleTypes.HEX_FALLING_SPORE_BLOSSOM.get(), rgb),
                d0, d1, d2,
                0.0, 0.0, 0.0
        );

        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

        for (int l = 0; l < ADD_PARTICLE_ATTEMPTS; ++l) {
            mutablePos.set(
                    i + Mth.nextInt(random, -PARTICLE_XZ_RADIUS, PARTICLE_XZ_RADIUS),
                    j - random.nextInt(PARTICLE_Y_MAX),
                    k + Mth.nextInt(random, -PARTICLE_XZ_RADIUS, PARTICLE_XZ_RADIUS)
            );

            BlockState blockState = level.getBlockState(mutablePos);
            if (!blockState.isCollisionShapeFullBlock(level, mutablePos)) {
                level.addParticle(
                        new HexColorParticleOptions(ModParticleTypes.HEX_SPORE_BLOSSOM_AIR.get(), rgb),
                        (double) mutablePos.getX() + random.nextDouble(),
                        (double) mutablePos.getY() + random.nextDouble(),
                        (double) mutablePos.getZ() + random.nextDouble(),
                        0.0, 0.0, 0.0
                );
            }
        }
    }

    private static int getParticleRgb(Level level, BlockPos pos) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof HexBlockEntity hexBe && hexBe.hasHexColor()) {
            return hexBe.getHexColor();
        }

        return TintColorUtil.defaultHexBlockItemTint();
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof HexBlockEntity hexBe) {
            hexBe.setHexColor(random.nextInt(0x1000000));
        }
    }

    @Override
    public BonemealableBlock.Type getType() {
        return BonemealableBlock.Type.GROWER;
    }
}