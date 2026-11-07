package space.anatomyuniverse.musavacca.block.custom;

//? if >=26.3 {
/*import net.minecraft.world.level.block.BonemealSource;
*///?}

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
//? if <1.21.2 {
/*import net.minecraft.world.ItemInteractionResult;
*///?}
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
//? if >=1.21.5 {
import net.minecraft.world.level.block.TintedParticleLeavesBlock;
//?} else {
/*import net.minecraft.world.level.block.LeavesBlock;
*///?}

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class MusavaccaLeaves
        //? if >=1.21.5 {
        extends TintedParticleLeavesBlock
        //?} else {
        /*extends LeavesBlock
        *///?}
        implements BonemealableBlock {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;
    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 2);
    public static final int MAX_AGE = 2;

    public MusavaccaLeaves(float leafParticleChance, Properties properties) {
        //? if >=1.21.5 {
        super(leafParticleChance, properties);
        //?} else {
        /*super(properties);
        *///?}
        this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.UP).setValue(AGE, 0));
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        if (state == null) return null;
        return state.setValue(FACING, context.getClickedFace()).setValue(AGE, 0);
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
        builder.add(FACING, AGE);
    }

    @Override
    //? if >=1.21.2 {
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
            BlockHitResult hit) {
    //?} else {
    /*protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
            BlockHitResult hit) {
    *///?}
        if (!stack.is(Items.SHEARS) || state.getValue(AGE) <= 0) {
            //? if >=1.21.2 {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
            //?} else {
            /*return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            *///?}
        }
        int age = state.getValue(AGE);
        if (!level.isClientSide()) {
            level.setBlock(pos, state.setValue(AGE, age - 1), Block.UPDATE_ALL);
            stack.hurtAndBreak(1, player, hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
            SoundType soundType = state.getSoundType(level, pos, player);
            level.playSound(null, pos, SoundEvents.SHEEP_SHEAR, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.playSound(null, pos, soundType.getBreakSound(), SoundSource.BLOCKS, soundType.getVolume(), soundType.getPitch());
        }
        //? if >=1.21.2 {
        return InteractionResult.SUCCESS;
        //?} else {
        /*return ItemInteractionResult.SUCCESS;
        *///?}
    }

    @Override
    //? if >=26.3 {
    /*public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, BonemealSource source) {
    *///?} else {
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
    //?}
        return state.getValue(AGE) < MAX_AGE;
    }

    @Override
    //? if >=26.3 {
    /*public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state, BonemealSource source) {
    *///?} else {
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
    //?}
        return state.getValue(AGE) < MAX_AGE;
    }

    @Override
    //? if >=26.3 {
    /*public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state, BonemealSource source) {
    *///?} else {
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
    //?}
        int age = state.getValue(AGE);
        if (age < MAX_AGE) {
            level.setBlock(pos, state.setValue(AGE, age + 1), Block.UPDATE_ALL);
        }
    }

    @Override
    public Type getType() {
        return Type.GROWER;
    }
}
