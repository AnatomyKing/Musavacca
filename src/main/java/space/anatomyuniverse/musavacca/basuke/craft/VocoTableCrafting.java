package space.anatomyuniverse.musavacca.basuke.craft;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.anatomyuniverse.musavacca.basuke.craft.VocoTableCraftingRecipes.Recipe;
import space.anatomyuniverse.musavacca.block.custom.VocoTableBlock;
import space.anatomyuniverse.musavacca.block.custom.logic.GlinsterLogic;
import space.anatomyuniverse.musavacca.block.entity.custom.VocoTableBlockEntity;
import space.anatomyuniverse.musavacca.component.ModDataComponents;
import space.anatomyuniverse.musavacca.entity.mob.basuke.Basuke;
import space.anatomyuniverse.musavacca.tint.MusavaccaTints;

public final class VocoTableCrafting {
    private VocoTableCrafting() {}

    @Nullable
    public static Recipe findActiveRecipe(@NotNull Basuke basuke, @NotNull ServerLevel level, @NotNull ItemStack edibleStack) {
        if (edibleStack.isEmpty() || !basuke.isBoundToVocoTable()) {
            return null;
        }
        BlockPos tablePos = basuke.getVocoTablePos();
        if (tablePos == null) return null;
        BlockState state = level.getBlockState(tablePos);
        if (!(state.getBlock() instanceof VocoTableBlock) || !state.hasProperty(VocoTableBlock.ROTARY_DIALERS)
                || !state.getValue(VocoTableBlock.ROTARY_DIALERS)) {
            return null;
        }
        if (!(level.getBlockEntity(tablePos) instanceof VocoTableBlockEntity tableBe)) {
            return null;
        }
        ItemStack displayedStack = tableBe.getDisplayedItem();
        if (displayedStack.isEmpty()) return null;
        int matchingCandleColor = tableBe.getMatchingCandleHexColor();
        Recipe recipe = VocoTableCraftingRecipes.findMatchingRecipe(displayedStack, edibleStack,
                matchingCandleColor != MusavaccaTints.NO_TINT);
        if (recipe == null) return null;
        if (recipe.hexColorInject() && matchingCandleColor == MusavaccaTints.NO_TINT) {
            return null;
        }
        ItemStack resultStack = recipe.createResultStack();
        if (tableBe.getDisplayedItemCount() > 1 && resultStack.getMaxStackSize() <= 1) {
            return null;
        }
        return recipe;
    }

    public static boolean completeActiveRecipe(@NotNull Basuke basuke, @NotNull ServerLevel level, @NotNull ItemStack edibleStack,
            @NotNull Recipe recipe) {
        BlockPos tablePos = basuke.getVocoTablePos();
        if (tablePos == null) return false;
        if (!(level.getBlockEntity(tablePos) instanceof VocoTableBlockEntity tableBe)) {
            return false;
        }
        ItemStack displayedStack = tableBe.getDisplayedItem();
        if (!recipe.matches(displayedStack, edibleStack)) return false;
        int matchingCandleColor = MusavaccaTints.NO_TINT;
        if (recipe.hexColorInject()) {
            matchingCandleColor = tableBe.getMatchingCandleHexColor();
            if (matchingCandleColor == MusavaccaTints.NO_TINT) return false;
        }
        int displayedItemCount = tableBe.getDisplayedItemCount();
        ItemStack resultStack = recipe.createResultStack();
        if (displayedItemCount > 1 && resultStack.getMaxStackSize() <= 1) {
            return false;
        }
        if (recipe.hexColorInject()) resultStack.set(ModDataComponents.HEX_COLOR.get(), matchingCandleColor);
        tableBe.setDisplayedItem(resultStack, displayedItemCount);
        edibleStack.shrink(1);
        basuke.setItemInHand(InteractionHand.MAIN_HAND, edibleStack.isEmpty() ? ItemStack.EMPTY : edibleStack);
        GlinsterLogic.burst(level, tablePos);
        playCraftingSounds(level, tablePos);
        return true;
    }

    private static void playCraftingSounds(@NotNull ServerLevel level, @NotNull BlockPos tablePos) {
        Vec3 itemDisplayCenter = new Vec3(tablePos.getX() + 0.5D, tablePos.getY() + 1.20D, tablePos.getZ() + 0.5D);
        level.playSound(null, itemDisplayCenter.x, itemDisplayCenter.y, itemDisplayCenter.z, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 0.85F,
                1.35F);
        level.playSound(null, itemDisplayCenter.x, itemDisplayCenter.y, itemDisplayCenter.z, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.BLOCKS, 0.65F,
                1.65F);
    }

}
