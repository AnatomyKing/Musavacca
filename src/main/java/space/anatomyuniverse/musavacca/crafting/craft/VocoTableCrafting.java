package space.anatomyuniverse.musavacca.crafting.craft;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.anatomyuniverse.musavacca.block.custom.VocoTableBlock;
import space.anatomyuniverse.musavacca.block.entity.custom.VocoTableBlockEntity;
import space.anatomyuniverse.musavacca.entity.mob.basuke.Basuke;

public final class VocoTableCrafting {
    private VocoTableCrafting() {}

    @Nullable
    public static VocoTableCraftingRecipe findActiveRecipe(
            @NotNull Basuke basuke,
            @NotNull ServerLevel level,
            @NotNull ItemStack edibleStack
    ) {
        if (edibleStack.isEmpty() || !basuke.isBoundToVocoTable()) {
            return null;
        }

        BlockPos tablePos = basuke.getVocoTablePos();
        if (tablePos == null) {
            return null;
        }

        BlockState state = level.getBlockState(tablePos);
        if (!(state.getBlock() instanceof VocoTableBlock)
                || !state.hasProperty(VocoTableBlock.ROTARY_DIALERS)
                || !state.getValue(VocoTableBlock.ROTARY_DIALERS)) {
            return null;
        }

        if (!(level.getBlockEntity(tablePos) instanceof VocoTableBlockEntity tableBe)) {
            return null;
        }

        ItemStack displayedStack = tableBe.getDisplayedItem();
        if (displayedStack.isEmpty()) {
            return null;
        }

        VocoTableCraftingRecipe recipe = VocoTableCraftingRecipes.findMatchingRecipe(
                displayedStack,
                edibleStack
        );

        if (recipe == null) {
            return null;
        }

        return tableBe.hasLitReceptorCost(recipe.litReceptorCost())
                ? recipe
                : null;
    }

    public static boolean completeActiveRecipe(
            @NotNull Basuke basuke,
            @NotNull ServerLevel level,
            @NotNull ItemStack edibleStack,
            @NotNull VocoTableCraftingRecipe recipe
    ) {
        BlockPos tablePos = basuke.getVocoTablePos();
        if (tablePos == null) {
            return false;
        }

        if (!(level.getBlockEntity(tablePos) instanceof VocoTableBlockEntity tableBe)) {
            return false;
        }

        ItemStack displayedStack = tableBe.getDisplayedItem();
        if (!recipe.matches(displayedStack, edibleStack)) {
            return false;
        }

        if (!tableBe.consumeLitReceptorsForCrafting(level, recipe.litReceptorCost())) {
            return false;
        }

        ItemStack resultStack = recipe.createResultStack();
        tableBe.setDisplayedItem(resultStack);

        edibleStack.shrink(1);
        basuke.setItemInHand(
                InteractionHand.MAIN_HAND,
                edibleStack.isEmpty() ? ItemStack.EMPTY : edibleStack
        );

        playCraftingEffects(level, tablePos, resultStack);
        return true;
    }

    private static void playCraftingEffects(
            @NotNull ServerLevel level,
            @NotNull BlockPos tablePos,
            @NotNull ItemStack resultStack
    ) {
        Vec3 center = Vec3.atCenterOf(tablePos).add(0.0D, 0.85D, 0.0D);

        level.sendParticles(
                ParticleTypes.END_ROD,
                center.x,
                center.y,
                center.z,
                18,
                0.24D,
                0.18D,
                0.24D,
                0.025D
        );

        level.sendParticles(
                new ItemParticleOption(ParticleTypes.ITEM, resultStack.copyWithCount(1)),
                center.x,
                center.y,
                center.z,
                10,
                0.16D,
                0.12D,
                0.16D,
                0.035D
        );

        level.playSound(
                null,
                center.x,
                center.y,
                center.z,
                SoundEvents.ENCHANTMENT_TABLE_USE,
                SoundSource.BLOCKS,
                0.85F,
                1.35F
        );

        level.playSound(
                null,
                center.x,
                center.y,
                center.z,
                SoundEvents.AMETHYST_BLOCK_CHIME,
                SoundSource.BLOCKS,
                0.65F,
                1.65F
        );
    }
}