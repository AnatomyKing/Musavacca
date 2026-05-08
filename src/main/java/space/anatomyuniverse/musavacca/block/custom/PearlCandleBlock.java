// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/block/custom/PearlCandleBlock.java
package space.anatomyuniverse.musavacca.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import space.anatomyuniverse.musavacca.block.entity.custom.PearlCandleBlockEntity;
import space.anatomyuniverse.musavacca.item.custom.FlintAndPearlItem;
import space.anatomyuniverse.musavacca.particle.ModParticleTypes;
import space.anatomyuniverse.musavacca.particle.tinted.ProfileTintParticles;

public class PearlCandleBlock extends CandleBlock implements EntityBlock {
    private final Block vanillaCandleBlock;

    public PearlCandleBlock(Block vanillaCandleBlock, BlockBehaviour.Properties properties) {
        super(properties);

        if (!(vanillaCandleBlock instanceof CandleBlock)) {
            throw new IllegalArgumentException("vanillaCandleBlock must be a CandleBlock");
        }

        this.vanillaCandleBlock = vanillaCandleBlock;
    }

    public Block getVanillaCandleBlock() {
        return this.vanillaCandleBlock;
    }

    public BlockState copyStateFromVanillaCandle(BlockState vanillaState, boolean forceLit) {
        boolean waterlogged = vanillaState.getValue(WATERLOGGED);

        return this.defaultBlockState()
                .setValue(CANDLES, vanillaState.getValue(CANDLES))
                .setValue(WATERLOGGED, waterlogged)
                .setValue(LIT, forceLit && !waterlogged);
    }

    public BlockState toVanillaCandleState(BlockState pearlState, boolean lit) {
        boolean waterlogged = pearlState.getValue(WATERLOGGED);

        return this.vanillaCandleBlock.defaultBlockState()
                .setValue(CANDLES, pearlState.getValue(CANDLES))
                .setValue(WATERLOGGED, waterlogged)
                .setValue(LIT, lit && !waterlogged);
    }

    public static boolean canPearlLight(BlockState state) {
        return state.hasProperty(LIT)
                && state.hasProperty(WATERLOGGED)
                && !state.getValue(LIT)
                && !state.getValue(WATERLOGGED);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PearlCandleBlockEntity(pos, state);
    }

    @Override
    protected void onPlace(
            BlockState state,
            Level level,
            BlockPos pos,
            BlockState oldState,
            boolean movedByPiston
    ) {
        if (!level.isClientSide() && !state.getValue(LIT)) {
            level.setBlock(
                    pos,
                    this.toVanillaCandleState(state, false),
                    Block.UPDATE_ALL | Block.UPDATE_IMMEDIATE
            );
            return;
        }

        super.onPlace(state, level, pos, oldState, movedByPiston);
    }

    @Override
    protected InteractionResult useItemOn(
            ItemStack stack,
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hitResult
    ) {
        if (stack.is(this.vanillaCandleBlock.asItem())
                && player.getAbilities().mayBuild
                && !player.isSecondaryUseActive()
                && state.getValue(LIT)
                && !state.getValue(WATERLOGGED)
                && state.getValue(CANDLES) < MAX_CANDLES) {

            if (!level.isClientSide()) {
                BlockState newState = state.cycle(CANDLES);

                level.setBlock(
                        pos,
                        newState,
                        Block.UPDATE_ALL | Block.UPDATE_IMMEDIATE
                );

                SoundType soundType = newState.getSoundType();

                level.playSound(
                        null,
                        pos,
                        soundType.getPlaceSound(),
                        SoundSource.BLOCKS,
                        (soundType.getVolume() + 1.0F) / 2.0F,
                        soundType.getPitch() * 0.8F
                );

                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }

                level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
            }

            return InteractionResult.SUCCESS;
        }

        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!state.getValue(LIT) || state.getValue(WATERLOGGED)) {
            return;
        }

        int hexColor = FlintAndPearlItem.DEFAULT_HEX_COLOR;

        if (level.getBlockEntity(pos) instanceof PearlCandleBlockEntity pearlCandleBe) {
            hexColor = pearlCandleBe.getHexColorOrFallback();
        }

        for (Vec3 offset : this.getParticleOffsets(state)) {
            addPearlCandleParticlesAndSound(
                    level,
                    offset.add(pos.getX(), pos.getY(), pos.getZ()),
                    random,
                    hexColor
            );
        }
    }

    private static void addPearlCandleParticlesAndSound(
            Level level,
            Vec3 particlePos,
            RandomSource random,
            int hexColor
    ) {
        float roll = random.nextFloat();

        if (roll < 0.30F) {
            level.addParticle(
                    ParticleTypes.SMOKE,
                    particlePos.x,
                    particlePos.y,
                    particlePos.z,
                    0.0D,
                    0.0D,
                    0.0D
            );

            if (roll < 0.17F) {
                level.playLocalSound(
                        particlePos.x + 0.5D,
                        particlePos.y + 0.5D,
                        particlePos.z + 0.5D,
                        SoundEvents.CANDLE_AMBIENT,
                        SoundSource.BLOCKS,
                        1.0F + random.nextFloat(),
                        random.nextFloat() * 0.7F + 0.3F,
                        false
                );
            }
        }

        ProfileTintParticles.spawn(
                level,
                random,
                ModParticleTypes.PEARL_FLAME.get(),
                hexColor,
                particlePos.x,
                particlePos.y,
                particlePos.z,
                0.0D,
                0.0D,
                0.0D
        );
    }

    @Override
    protected ItemStack getCloneItemStack(
            LevelReader level,
            BlockPos pos,
            BlockState state,
            boolean includeData
    ) {
        return new ItemStack(this.vanillaCandleBlock.asItem());
    }

    public static int candleLightLevel(BlockState state) {
        return state.getValue(LIT) ? 3 * state.getValue(CANDLES) : 0;
    }
}