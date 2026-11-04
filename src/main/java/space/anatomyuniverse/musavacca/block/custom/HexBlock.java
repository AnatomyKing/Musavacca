package space.anatomyuniverse.musavacca.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
//? if <1.21.2 {
/*import net.minecraft.world.ItemInteractionResult;
*///?}
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
//? if >=1.21.2 {
import net.minecraft.world.level.ScheduledTickAccess;
//?} else {
/*import net.minecraft.world.level.LevelAccessor;
*///?}
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import space.anatomyuniverse.musavacca.block.custom.logic.BreakHexLogic;
import space.anatomyuniverse.musavacca.block.custom.logic.GlinsterLogic;
import space.anatomyuniverse.musavacca.block.entity.custom.HexBlockEntity;
import space.anatomyuniverse.musavacca.component.ModDataComponents;
import space.anatomyuniverse.musavacca.particle.ModParticleTypes;
import space.anatomyuniverse.musavacca.particle.utils.HexColorParticleOptions;
import space.anatomyuniverse.musavacca.tint.MusavaccaTints;

public class HexBlock extends Block implements EntityBlock, BonemealableBlock {
    //? if <26.3 {
    public static final MapCodec<HexBlock> CODEC = simpleCodec(HexBlock::new);
    //?}
    public static final BooleanProperty CLIPPED = BooleanProperty.create("clipped");
    private static final VoxelShape SHAPE = Block.box(3.5, 8.0, 3.5, 12.5, 16.0, 12.5);
    //? if <26.3 {
    @Override
    public MapCodec<HexBlock> codec() {
        return CODEC;
    }
    //?}
    public HexBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(CLIPPED, true));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new HexBlockEntity(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(CLIPPED);
    }

    //? if >=1.21.2 {
    private static InteractionResult passToDefault() {
    //?} else {
    /*private static ItemInteractionResult passToDefault() {
    *///?}
        //? if >=1.21.2 {
        return InteractionResult.TRY_WITH_EMPTY_HAND;
        //?} else {
        /*return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        *///?}
    }

    //? if >=1.21.2 {
    private static InteractionResult successResult() {
    //?} else {
    /*private static ItemInteractionResult successResult() {
    *///?}
        //? if >=1.21.2 {
        return InteractionResult.SUCCESS;
        //?} else {
        /*return ItemInteractionResult.SUCCESS;
        *///?}
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return BreakHexLogic.canHexBlockSurvive(level, pos);
    }

    @Override
    //? if >=1.21.2 {
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess scheduledTickAccess, BlockPos pos,
            net.minecraft.core.Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
    //?} else {
    /*protected BlockState updateShape(BlockState state, net.minecraft.core.Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos,
            BlockPos neighborPos) {
    *///?}
        return net.minecraft.core.Direction.UP == direction && !this.canSurvive(state, level, pos)
                ? Blocks.AIR.defaultBlockState()
                //? if >=1.21.2 {
                : super.updateShape(state, level, scheduledTickAccess, pos, direction, neighborPos, neighborState, random);
                //?} else {
                /*: super.updateShape(state, direction, neighborState, level, pos, neighborPos);
                *///?}
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        if (level.isClientSide()) return;
        if (oldState.is(state.getBlock())) return;
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof HexBlockEntity hexBe) {
            hexBe.ensureRandomHexColor();
        }
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (level.isClientSide()) return;
        Integer savedHex = stack.get(ModDataComponents.HEX_COLOR.get());
        if (savedHex == null) return;
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof HexBlockEntity hexBe) {
            hexBe.setHexColor(savedHex);
        }
    }

    @Override
    //? if >=1.21.2 {
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
    //?} else {
    /*protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
            BlockHitResult hit) {
    *///?}
        if (!stack.is(Items.SHEARS)) return passToDefault();
        if (state.getValue(CLIPPED)) return passToDefault();
        if (!level.isClientSide()) {
            level.setBlock(pos, state.setValue(CLIPPED, true), Block.UPDATE_ALL);
            stack.hurtAndBreak(1, player, hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
            level.playSound(null, pos, SoundEvents.SHEEP_SHEAR, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.playSound(null, pos, SoundEvents.AMETHYST_CLUSTER_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
        return successResult();
    }

    //? if >=1.21.5 {
    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean movedByPiston) {
        BreakHexLogic.breakBreakBlockAboveIfPresent(level, pos);
        super.affectNeighborsAfterRemoval(state, level, pos, movedByPiston);
    }
    //?}
//? if <1.21.5 {
/*@Override
protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
    if (!state.is(newState.getBlock()) && newState.isAir()) {
        BreakHexLogic.breakBreakBlockAboveIfPresent(level, pos);
    }
    super.onRemove(state, level, pos, newState, movedByPiston);
}
*///?}
    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (state.getValue(CLIPPED)) return;
        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();
        int rgb = getParticleRgb(level, pos);
        GlinsterLogic.animateTick(level, pos, random);
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        for (int l = 0; l < 10; ++l) {
            mutablePos.set(i + Mth.nextInt(random, -10, 10), j - random.nextInt(10), k + Mth.nextInt(random, -10, 10));
            BlockState blockState = level.getBlockState(mutablePos);
            if (!blockState.isCollisionShapeFullBlock(level, mutablePos)) {
                level.addParticle(new HexColorParticleOptions(ModParticleTypes.HEX_SPORE_BLOSSOM_AIR.get(), rgb),
                        (double) mutablePos.getX() + random.nextDouble(), (double) mutablePos.getY() + random.nextDouble(),
                        (double) mutablePos.getZ() + random.nextDouble(), 0.0, 0.0, 0.0);
            }
        }
    }

    private static int getParticleRgb(Level level, BlockPos pos) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof HexBlockEntity hexBe && hexBe.hasHexColor()) return hexBe.getHexColor();
        return MusavaccaTints.DEFAULT_TINT;
    }

    @Override
    //? if >=26.3 {
    /*public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, net.minecraft.world.level.block.BonemealSource source) {
    *///?} else {
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
    //?}
        return true;
    }

    @Override
    //? if >=26.3 {
    /*public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state, net.minecraft.world.level.block.BonemealSource source) {
    *///?} else {
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
    //?}
        return true;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(CLIPPED)) return;
        if (random.nextInt(5) != 0) return;
        if (BreakHexLogic.canGrowHexIntoEggPair(level, pos, state)) {
            BreakHexLogic.growHexIntoEggPair(level, pos, state);
        }
    }

    @Override
    //? if >=26.3 {
    /*public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state, net.minecraft.world.level.block.BonemealSource source) {
    *///?} else {
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
    //?}
        if (state.getValue(CLIPPED)) {
            level.setBlock(pos, state.setValue(CLIPPED, false), Block.UPDATE_ALL);
            return;
        }
        if (BreakHexLogic.canGrowHexIntoEggPair(level, pos, state)) {
            BreakHexLogic.growHexIntoEggPair(level, pos, state);
            return;
        }
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof HexBlockEntity hexBe) {
            hexBe.setHexColor(random.nextInt(0x1000000));
        }
    }

    @Override
    public Type getType() {
        return Type.GROWER;
    }
}
