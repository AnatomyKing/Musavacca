package space.anatomyuniverse.musavacca.item.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import space.anatomyuniverse.musavacca.bar.balance.BalanceApi;
import space.anatomyuniverse.musavacca.block.ModBlocks;
//? if >=26.1 {
/*import net.minecraft.world.level.block.SnowyBlock;
*///?} else {
import net.minecraft.world.level.block.SnowyDirtBlock;
//?}

public final class BananaPearlItem extends Item {
    public BananaPearlItem(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (!(entity instanceof ServerPlayer player) || stack.isEmpty()) return super.finishUsingItem(stack, level, entity);
        int amount = Math.min(player.isShiftKeyDown() ? stack.getCount() : 1, Integer.MAX_VALUE - BalanceApi.getBalance(player));
        if (amount <= 0) return stack;
        if (!BalanceApi.addBalance(level.getServer(), player.getUUID(), amount)) return stack;
        stack.shrink(amount - 1);
        return super.finishUsingItem(stack, level, entity);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getPlayer() != null && context.getPlayer().isShiftKeyDown()) return InteractionResult.PASS;
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        if (!level.getBlockState(pos).is(Blocks.ROOTED_DIRT)) return super.useOn(context);
        if (level instanceof ServerLevel serverLevel) {
            BlockState caroteneGrass =
                    ModBlocks.CAROTENE_GRASS
                            .get()
                            .defaultBlockState()
                            //? if >=26.1 {
                            /*.setValue(SnowyBlock.SNOWY, level.getBlockState(pos.above()).is(Blocks.SNOW));
                            *///?} else {
                            .setValue(SnowyDirtBlock.SNOWY, level.getBlockState(pos.above()).is(Blocks.SNOW));
                            //?}
            level.setBlockAndUpdate(pos, caroteneGrass);
            serverLevel.sendParticles(
                    ParticleTypes.HAPPY_VILLAGER, pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, 15, 0.35D, 0.20D, 0.35D, 0.0D);
            level.playSound(null, pos, SoundEvents.BONE_MEAL_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.playSound(null, pos, SoundEvents.AMETHYST_CLUSTER_BREAK, SoundSource.BLOCKS, 0.8F, 1.15F);
            Player player = context.getPlayer();
            if (player == null || !player.getAbilities().instabuild) {
                context.getItemInHand().shrink(1);
            }
        }
        return InteractionResult.SUCCESS;
    }
}
