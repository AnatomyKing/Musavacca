// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/block/custom/VocoReceptorBlock.java
package space.anatomyuniverse.musavacca.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;

public class VocoReceptorBlock extends Block {

    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    private static final net.minecraft.world.phys.shapes.VoxelShape SHAPE =
            Block.box(5.0D, 0.0D, 5.0D, 11.0D, 16.0D, 11.0D);

    public VocoReceptorBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(LIT, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIT);
    }

    @Override
    protected InteractionResult useWithoutItem(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            BlockHitResult hitResult
    ) {
        if (!level.isClientSide) {
            level.setBlock(pos, state.cycle(LIT), Block.UPDATE_ALL);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    protected net.minecraft.world.phys.shapes.VoxelShape getShape(
            BlockState state,
            net.minecraft.world.level.BlockGetter level,
            BlockPos pos,
            net.minecraft.world.phys.shapes.CollisionContext context
    ) {
        return SHAPE;
    }

    @Override
    protected net.minecraft.world.phys.shapes.VoxelShape getCollisionShape(
            BlockState state,
            net.minecraft.world.level.BlockGetter level,
            BlockPos pos,
            net.minecraft.world.phys.shapes.CollisionContext context
    ) {
        return SHAPE;
    }
}