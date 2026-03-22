package space.anatomyuniverse.musavacca.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class BreakBlock extends Block implements BonemealableBlock {

    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 2);
    public static final BooleanProperty ATTACHED = BlockStateProperties.ATTACHED;
    public static final int MAX_AGE = 2;

    private static final VoxelShape SHAPE_STAGE0 = Block.box(5.0, 0.0, 5.0, 11.0, 7.0, 11.0);
    private static final VoxelShape SHAPE_STAGE1 = Block.box(3.0, 0.0, 3.0, 13.0, 11.0, 13.0);
    private static final VoxelShape SHAPE_STAGE2 = Block.box(1.0, 0.0, 1.0, 15.0, 15.0, 15.0);

    public BreakBlock(Properties properties) {
        super(properties);

        this.registerDefaultState(
                this.stateDefinition.any()
                        .setValue(AGE, 0)
                        .setValue(ATTACHED, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(AGE, ATTACHED);
    }

    public static boolean isAttachedStem(BlockState state) {
        return state.getBlock() instanceof BreakBlock
                && state.hasProperty(AGE)
                && state.hasProperty(ATTACHED)
                && state.getValue(AGE) == 0
                && state.getValue(ATTACHED);
    }

    public static boolean isAttachedStem(BlockState state, Block expectedBlock) {
        return state.is(expectedBlock) && isAttachedStem(state);
    }

    public static BlockState makeAttachedStem(Block block) {
        if (!(block instanceof BreakBlock)) {
            throw new IllegalArgumentException("Block must be a BreakBlock");
        }

        return block.defaultBlockState()
                .setValue(AGE, 0)
                .setValue(ATTACHED, true);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        for (Direction direction : context.getNearestLookingDirections()) {
            if (direction.getAxis() == Axis.Y) {
                BlockState blockState = this.defaultBlockState()
                        .setValue(ATTACHED, direction == Direction.UP);

                if (blockState.canSurvive(context.getLevel(), context.getClickedPos())) {
                    return blockState;
                }
            }
        }

        return null;
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction direction = getConnectedDirection(state).getOpposite();
        return Block.canSupportCenter(level, pos.relative(direction), direction.getOpposite());
    }

    protected static Direction getConnectedDirection(BlockState state) {
        return state.getValue(ATTACHED) ? Direction.DOWN : Direction.UP;
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
        return getConnectedDirection(state).getOpposite() == direction && !state.canSurvive(level, pos)
                ? Blocks.AIR.defaultBlockState()
                : super.updateShape(state, level, scheduledTickAccess, pos, direction, neighborPos, neighborState, random);
    }

    private static VoxelShape shapeFor(BlockState state) {
        return switch (state.getValue(AGE)) {
            case 0 -> SHAPE_STAGE0;
            case 1 -> SHAPE_STAGE1;
            case 2 -> SHAPE_STAGE2;
            default -> SHAPE_STAGE0;
        };
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return shapeFor(state);
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return shapeFor(state);
    }

    @Override
    protected VoxelShape getOcclusionShape(BlockState state) {
        return Shapes.empty();
    }

    @Override
    protected void spawnAfterBreak(BlockState state, ServerLevel level, BlockPos pos, ItemStack stack, boolean dropExperience) {
        super.spawnAfterBreak(state, level, pos, stack, dropExperience);

        if (state.getValue(AGE) != MAX_AGE) {
            return;
        }

        Holder<Enchantment> silkTouch = level.registryAccess()
                .lookupOrThrow(Registries.ENCHANTMENT)
                .getOrThrow(Enchantments.SILK_TOUCH);

        if (stack.getEnchantmentLevel(silkTouch) > 0) {
            return;
        }

        boolean attached = state.getValue(ATTACHED);

        level.getServer().execute(() ->
                spawnEntitySafely(level, pos, attached, EntityType.COW)
        );
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
        return state.getValue(AGE) < MAX_AGE;
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return state.getValue(AGE) < MAX_AGE;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        int age = state.getValue(AGE);
        if (age < MAX_AGE) {
            level.setBlock(pos, state.setValue(AGE, age + 1), Block.UPDATE_ALL);
        }
    }

    @Override
    public BonemealableBlock.Type getType() {
        return BonemealableBlock.Type.GROWER;
    }

    private static <T extends Entity> void spawnEntitySafely(ServerLevel level, BlockPos pos, boolean attached, EntityType<T> type) {
        T entity = type.create(level, EntitySpawnReason.TRIGGERED);
        if (entity == null) return;

        final double x = pos.getX() + 0.5D;
        final double z = pos.getZ() + 0.5D;
        final float yaw = level.random.nextFloat() * 360.0F;

        boolean ground = !level.getBlockState(pos.below()).isAir();
        boolean ceiling = !level.getBlockState(pos.above()).isAir();

        if (!attached && ground && !ceiling && trySpawn(level, entity, x, pos.getY(), z, yaw)) return;

        if (attached && trySpawn(level, entity, x, pos.getY() + 0.90D - entity.getBbHeight(), z, yaw)) return;

        if (trySpawn(level, entity, x, ground ? pos.getY() : pos.getY() + 0.01D, z, yaw)) return;

        for (Direction d : Direction.Plane.HORIZONTAL) {
            BlockPos p = pos.relative(d);
            if (level.getBlockState(p).isAir() && level.getBlockState(p.above()).isAir()) {
                double y = level.getBlockState(p.below()).isAir() ? p.getY() + 0.01D : p.getY();
                if (trySpawn(level, entity, p.getX() + 0.5D, y, p.getZ() + 0.5D, yaw)) return;
            }
        }
    }

    private static boolean trySpawn(ServerLevel level, Entity entity, double x, double y, double z, float yaw) {
        entity.snapTo(x, y, z, yaw, 0.0F);
        if (!level.noCollision(entity)) return false;

        level.addFreshEntity(entity);
        return true;
    }
}