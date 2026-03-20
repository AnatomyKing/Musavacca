// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/block/custom/HexBlock.java
package space.anatomyuniverse.musavacca.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import space.anatomyuniverse.musavacca.block.entity.HexBlockEntity;
import space.anatomyuniverse.musavacca.component.ModDataComponents;
import space.anatomyuniverse.musavacca.item.custom.HexBlockItem;

public class HexBlock extends Block implements EntityBlock, BonemealableBlock {

    public HexBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new HexBlockEntity(pos, state);
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

        // Item placement should not randomize here.
        // /setblock, structures, world placement should stay random.
        if (HexBlockItem.isForcingWhitePlacement()) {
            return;
        }

        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof HexBlockEntity hexBe && !hexBe.hasHexColor()) {
            hexBe.setHexColor(HexBlockEntity.createRandomHexColor());
        }
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);

        if (level.isClientSide()) {
            return;
        }

        if (!(stack.getItem() instanceof HexBlockItem)) {
            return;
        }

        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof HexBlockEntity hexBe)) {
            return;
        }

        Integer savedHex = stack.get(ModDataComponents.HEX_COLOR.get());

        // Silk-touched / preserved colored item: restore that exact color.
        if (savedHex != null) {
            hexBe.setHexColor(savedHex);
            return;
        }

        // Normal HexBlockItem with no saved color: place white.
        hexBe.setHexColor(HexBlockEntity.WHITE_HEX_COLOR);
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