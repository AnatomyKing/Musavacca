// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/block/custom/logic/PearlCandleLogic.java
package space.anatomyuniverse.musavacca.block.custom.logic;

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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import space.anatomyuniverse.musavacca.block.custom.PearlCandleBlock;
import space.anatomyuniverse.musavacca.block.entity.custom.PearlCandleBlockEntity;
import space.anatomyuniverse.musavacca.item.custom.FlintAndPearlItem;
import space.anatomyuniverse.musavacca.particle.ModParticleTypes;
import space.anatomyuniverse.musavacca.particle.tinted.ProfileTintParticles;

public final class PearlCandleLogic {
    private PearlCandleLogic() {}

    public static boolean canPearlLight(BlockState state) {
        return state.hasProperty(CandleBlock.LIT)
                && state.hasProperty(CandleBlock.WATERLOGGED)
                && !state.getValue(CandleBlock.LIT)
                && !state.getValue(CandleBlock.WATERLOGGED);
    }

    public static void onPlace(
            PearlCandleBlock block,
            BlockState state,
            Level level,
            BlockPos pos,
            BlockState oldState,
            boolean movedByPiston
    ) {
        if (!level.isClientSide() && !state.getValue(CandleBlock.LIT)) {
            level.setBlock(
                    pos,
                    block.toVanillaCandleState(state, false),
                    Block.UPDATE_ALL | Block.UPDATE_IMMEDIATE
            );
        }
    }

    public static InteractionResult useItemOn(
            PearlCandleBlock block,
            ItemStack stack,
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hitResult
    ) {
        if (stack.is(block.getVanillaCandleBlock().asItem())
                && player.getAbilities().mayBuild
                && !player.isSecondaryUseActive()
                && state.getValue(CandleBlock.LIT)
                && !state.getValue(CandleBlock.WATERLOGGED)
                && state.getValue(CandleBlock.CANDLES) < CandleBlock.MAX_CANDLES) {

            if (!level.isClientSide()) {
                BlockState newState = state.cycle(CandleBlock.CANDLES);

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

        return InteractionResult.PASS;
    }

    public static void animateTick(
            PearlCandleBlock block,
            BlockState state,
            Level level,
            BlockPos pos,
            RandomSource random
    ) {
        if (!state.getValue(CandleBlock.LIT) || state.getValue(CandleBlock.WATERLOGGED)) {
            return;
        }

        int hexColor = FlintAndPearlItem.DEFAULT_HEX_COLOR;

        if (level.getBlockEntity(pos) instanceof PearlCandleBlockEntity pearlCandleBe) {
            hexColor = pearlCandleBe.getHexColorOrFallback();
        }

        for (Vec3 offset : block.pearlParticleOffsets(state)) {
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

    public static int candleLightLevel(BlockState state) {
        return state.getValue(CandleBlock.LIT) ? 3 * state.getValue(CandleBlock.CANDLES) : 0;
    }
}