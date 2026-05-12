// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/crafting/craft/VocoTableCrafting.java
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
    private static final int END_ROD_PARTICLE_COUNT = 24;

    /*
     * Matches VocoTableBlockEntityItemDisplayRenderer:
     * ITEM_X = 0.5
     * ITEM_Y = 1.20
     * ITEM_Z = 0.5
     */
    private static final double ITEM_DISPLAY_X = 0.5D;
    private static final double ITEM_DISPLAY_Y = 1.20D;
    private static final double ITEM_DISPLAY_Z = 0.5D;

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
        Vec3 itemDisplayCenter = itemDisplayCenter(tablePos);

        spawnEndRodTransformationParticles(level, itemDisplayCenter);

        level.sendParticles(
                new ItemParticleOption(ParticleTypes.ITEM, resultStack.copyWithCount(1)),
                itemDisplayCenter.x,
                itemDisplayCenter.y,
                itemDisplayCenter.z,
                12,
                0.12D,
                0.10D,
                0.12D,
                0.045D
        );

        level.playSound(
                null,
                itemDisplayCenter.x,
                itemDisplayCenter.y,
                itemDisplayCenter.z,
                SoundEvents.ENCHANTMENT_TABLE_USE,
                SoundSource.BLOCKS,
                0.85F,
                1.35F
        );

        level.playSound(
                null,
                itemDisplayCenter.x,
                itemDisplayCenter.y,
                itemDisplayCenter.z,
                SoundEvents.AMETHYST_BLOCK_CHIME,
                SoundSource.BLOCKS,
                0.65F,
                1.65F
        );
    }

    private static Vec3 itemDisplayCenter(BlockPos tablePos) {
        return new Vec3(
                tablePos.getX() + ITEM_DISPLAY_X,
                tablePos.getY() + ITEM_DISPLAY_Y,
                tablePos.getZ() + ITEM_DISPLAY_Z
        );
    }

    private static void spawnEndRodTransformationParticles(
            @NotNull ServerLevel level,
            @NotNull Vec3 center
    ) {
        level.sendParticles(
                ParticleTypes.END_ROD,
                center.x,
                center.y,
                center.z,
                END_ROD_PARTICLE_COUNT,
                0.22D,
                0.16D,
                0.22D,
                0.045D
        );
    }
}