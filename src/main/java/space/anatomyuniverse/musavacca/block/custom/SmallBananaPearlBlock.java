package space.anatomyuniverse.musavacca.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import net.neoforged.neoforge.common.extensions.IBlockExtension;

public class SmallBananaPearlBlock extends FallingBlock implements IBlockExtension {

    public static final MapCodec<SmallBananaPearlBlock> CODEC = simpleCodec(SmallBananaPearlBlock::new);

    public static final int MAX_SMALL_PEARL_AMOUNT = 80;

    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final IntegerProperty SMALL_PEARL_AMOUNT =
            IntegerProperty.create("small_pearl_amount", 1, MAX_SMALL_PEARL_AMOUNT);

    private static final VoxelShape SHAPE_GROUND = Block.box(0, 0, 0, 16, 1, 16);
    private static final VoxelShape SHAPE_H3     = Block.box(0, 0, 0, 16, 3, 16);
    private static final VoxelShape SHAPE_H6     = Block.box(0, 0, 0, 16, 6, 16);
    private static final VoxelShape SHAPE_H9     = Block.box(0, 0, 0, 16, 9, 16);
    private static final VoxelShape SHAPE_H12    = Block.box(0, 0, 0, 16, 12, 16);
    private static final VoxelShape SHAPE_FULL   = Block.box(0, 0, 0, 16, 16, 16);

    public SmallBananaPearlBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(
                this.stateDefinition.any()
                        .setValue(FACING, Direction.NORTH)
                        .setValue(SMALL_PEARL_AMOUNT, 1)
        );
    }

    @Override
    protected MapCodec<? extends FallingBlock> codec() {
        return CODEC;
    }

    @Override
    public int getDustColor(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return 20;
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(SMALL_PEARL_AMOUNT, MAX_SMALL_PEARL_AMOUNT);
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING, SMALL_PEARL_AMOUNT);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return shapeForAmount(state.getValue(SMALL_PEARL_AMOUNT));
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return shapeForAmount(state.getValue(SMALL_PEARL_AMOUNT));
    }

    @Override
    protected void falling(FallingBlockEntity fallingEntity) {
        fallingEntity.disableDrop();
    }

    @Override
    public void onBrokenAfterFall(Level level, BlockPos pos, FallingBlockEntity fallingBlock) {
        BlockState falling = fallingBlock.getBlockState();
        BlockState landed  = level.getBlockState(pos);

        if (isSmallBananaPearlBlock(landed)) {
            int landedAmt  = landed.getValue(SMALL_PEARL_AMOUNT);
            int fallingAmt = falling.getValue(SMALL_PEARL_AMOUNT);

            if (landedAmt < MAX_SMALL_PEARL_AMOUNT && fallingAmt > 0) {
                int transfer = Math.min(MAX_SMALL_PEARL_AMOUNT - landedAmt, fallingAmt);
                int remain   = fallingAmt - transfer;

                level.setBlock(pos, landed.setValue(SMALL_PEARL_AMOUNT, landedAmt + transfer), 3);

                if (remain > 0) {
                    BlockPos above = pos.above();
                    if (level.getBlockState(above).canBeReplaced()) {
                        level.setBlock(above, falling.setValue(SMALL_PEARL_AMOUNT, remain), 3);
                    }
                }
                return;
            }
        }

        BlockPos place = level.getBlockState(pos).canBeReplaced() ? pos : pos.above();
        if (level.getBlockState(place).canBeReplaced() && falling.canSurvive(level, place)) {
            level.setBlock(place, falling, 3);
        }
    }

    @Override
    public boolean onDestroyedByPlayer(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            //? if >=1.21.10
            //ItemStack toolStack,
            boolean willHarvest,
            FluidState fluid
    ) {
        if (player != null && player.getAbilities().instabuild) {
            level.setBlock(pos, fluid.createLegacyBlock(), 3);
            return true;
        }

        int amount = state.getValue(SMALL_PEARL_AMOUNT);

        if (amount > 1) {
            if (!level.isClientSide() && willHarvest) {
                //? if >=1.21.10 {
                /*ItemStack effectiveTool = toolStack;
                *///?} else {
                ItemStack effectiveTool = player == null ? ItemStack.EMPTY : player.getMainHandItem();
                //?}

                Block.dropResources(state, (ServerLevel) level, pos, null, player, effectiveTool);
            }

            level.setBlock(pos, state.setValue(SMALL_PEARL_AMOUNT, amount - 1), 3);
            return false;
        }

        level.setBlock(pos, fluid.createLegacyBlock(), 3);
        return true;
    }


    private static VoxelShape shapeForAmount(int amount) {
        if (amount >= MAX_SMALL_PEARL_AMOUNT) return SHAPE_FULL;

        if (amount <= 15) return SHAPE_GROUND;
        if (amount <= 31) return SHAPE_H3;
        if (amount <= 47) return SHAPE_H6;
        if (amount <= 63) return SHAPE_H9;
        return SHAPE_H12;
    }

    public static boolean isSmallBananaPearlBlock(BlockState state) {
        return state.getBlock() instanceof SmallBananaPearlBlock;
    }

    public static boolean canAcceptPearl(BlockState state) {
        return isSmallBananaPearlBlock(state)
                && state.getValue(SMALL_PEARL_AMOUNT) < MAX_SMALL_PEARL_AMOUNT;
    }

    public static BlockState increment(BlockState state) {
        if (!isSmallBananaPearlBlock(state)) return state;

        int amount = state.getValue(SMALL_PEARL_AMOUNT);
        if (amount >= MAX_SMALL_PEARL_AMOUNT) return state;

        return state.setValue(SMALL_PEARL_AMOUNT, amount + 1);
    }
}

