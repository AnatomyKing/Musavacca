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
import space.anatomyuniverse.musavacca.block.custom.VocoTableBlock;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoReceptorLogic.ReceptorPosition;
import space.anatomyuniverse.musavacca.block.entity.custom.VocoTableBlockEntity;
import space.anatomyuniverse.musavacca.basuke.particle.VocoTableParticles;
import space.anatomyuniverse.musavacca.component.ModDataComponents;
import space.anatomyuniverse.musavacca.entity.mob.basuke.Basuke;

public final class VocoTableCrafting {
    private VocoTableCrafting() {}

    @Nullable
    public static VocoTableCraftingRecipe findActiveRecipe(
            @NotNull Basuke basuke,
            @NotNull ServerLevel level,
            @NotNull ItemStack edibleStack
    ) {
        if (
                edibleStack.isEmpty()
                        || !basuke.isBoundToVocoTable()
        ) {
            return null;
        }

        BlockPos tablePos = basuke.getVocoTablePos();

        if (tablePos == null) {
            return null;
        }

        BlockState state = level.getBlockState(tablePos);

        if (
                !(state.getBlock() instanceof VocoTableBlock)
                        || !state.hasProperty(
                        VocoTableBlock.ROTARY_DIALERS
                )
                        || !state.getValue(
                        VocoTableBlock.ROTARY_DIALERS
                )
        ) {
            return null;
        }

        if (
                !(level.getBlockEntity(tablePos)
                        instanceof VocoTableBlockEntity tableBe)
        ) {
            return null;
        }

        ItemStack displayedStack =
                tableBe.getDisplayedItem();

        if (displayedStack.isEmpty()) {
            return null;
        }

        /*
         * Matching candles only determine whether a HEX_COLOR recipe
         * can be selected. They are not consumed by normal crafting.
         */
        Integer matchingCandleColor =
                matchingFourCandleColor(tableBe);

        VocoTableCraftingRecipe recipe =
                VocoTableCraftingRecipes.findMatchingRecipe(
                        displayedStack,
                        edibleStack,
                        matchingCandleColor != null
                );

        if (recipe == null) {
            return null;
        }

        /*
         * Additional safety check in case the selected recipe requires
         * HEX_COLOR but the candle arrangement is no longer valid.
         */
        if (
                recipe.hexColorInject()
                        && matchingCandleColor == null
        ) {
            return null;
        }

        ItemStack resultStack =
                recipe.createResultStack();

        if (
                tableBe.getDisplayedItemCount() > 1
                        && resultStack.getMaxStackSize() <= 1
        ) {
            return null;
        }

        return recipe;
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

        if (
                !(level.getBlockEntity(tablePos)
                        instanceof VocoTableBlockEntity tableBe)
        ) {
            return false;
        }

        ItemStack displayedStack =
                tableBe.getDisplayedItem();

        if (!recipe.matches(displayedStack, edibleStack)) {
            return false;
        }

        Integer matchingCandleColor = null;

        if (recipe.hexColorInject()) {
            matchingCandleColor =
                    matchingFourCandleColor(tableBe);

            if (matchingCandleColor == null) {
                return false;
            }
        }

        int displayedItemCount =
                tableBe.getDisplayedItemCount();

        ItemStack resultStack =
                recipe.createResultStack();

        if (
                displayedItemCount > 1
                        && resultStack.getMaxStackSize() <= 1
        ) {
            return false;
        }

        int glitherColor =
                matchingCandleColor == null
                        ? VocoTableParticles.DEFAULT_GLITHER_COLOR
                        : matchingCandleColor;

        BlockState stateBeforeCrafting =
                level.getBlockState(tablePos);

        injectHexColorIfAllowed(
                resultStack,
                recipe,
                matchingCandleColor
        );

        tableBe.setDisplayedItem(
                resultStack,
                displayedItemCount
        );

        edibleStack.shrink(1);

        basuke.setItemInHand(
                InteractionHand.MAIN_HAND,
                edibleStack.isEmpty()
                        ? ItemStack.EMPTY
                        : edibleStack
        );

        VocoTableParticles.spawnCraftingParticles(
                level,
                tablePos,
                resultStack,
                glitherColor,
                stateBeforeCrafting,
                level.getBlockState(tablePos)
        );

        playCraftingSounds(
                level,
                tablePos
        );

        return true;
    }

    private static void injectHexColorIfAllowed(
            @NotNull ItemStack resultStack,
            @NotNull VocoTableCraftingRecipe recipe,
            @Nullable Integer matchingCandleColor
    ) {
        if (!recipe.hexColorInject()) {
            return;
        }

        if (matchingCandleColor == null) {
            return;
        }

        resultStack.set(
                ModDataComponents.HEX_COLOR.get(),
                matchingCandleColor & 0xFFFFFF
        );
    }

    private static void playCraftingSounds(
            @NotNull ServerLevel level,
            @NotNull BlockPos tablePos
    ) {
        Vec3 itemDisplayCenter =
                VocoTableParticles.itemDisplayCenter(
                        tablePos
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

    @Nullable
    private static Integer matchingFourCandleColor(
            @NotNull VocoTableBlockEntity tableBe
    ) {
        Integer matchingColor = null;

        for (
                ReceptorPosition receptor
                : ReceptorPosition.values()
        ) {
            if (!tableBe.isCandleLit(receptor)) {
                return null;
            }

            int cornerColor =
                    tableBe.getCornerHexColor(receptor);

            if (
                    cornerColor
                            == VocoTableBlockEntity.UNSET_HEX_COLOR
            ) {
                return null;
            }

            cornerColor &= 0xFFFFFF;

            if (matchingColor == null) {
                matchingColor = cornerColor;
                continue;
            }

            if (!matchingColor.equals(cornerColor)) {
                return null;
            }
        }

        return matchingColor;
    }
}

