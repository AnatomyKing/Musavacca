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
import space.anatomyuniverse.musavacca.block.custom.logic.VocoReceptorLogic.ReceptorPosition;
import space.anatomyuniverse.musavacca.block.entity.custom.VocoTableBlockEntity;
import space.anatomyuniverse.musavacca.component.HexColorComponent;
import space.anatomyuniverse.musavacca.entity.mob.basuke.Basuke;
import space.anatomyuniverse.musavacca.particle.ModParticleTypes;
import space.anatomyuniverse.musavacca.particle.tinted.ProfileTintParticles;

public final class VocoTableCrafting {
    private static final int GLITHER_PARTICLE_COUNT = 24;

    private static final int DEFAULT_GLITHER_COLOR = 0xFFFFFF;

    public static final String HEX_SLOT_RESULT = "voco_table_result";

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

        /*
         * Check color before consuming candles/receptors.
         * This makes the craft burst and injected color match the candle state that caused the craft.
         */
        Integer matchingCandleColor = matchingFourCandleColor(tableBe);
        int glitherColor = matchingCandleColor == null
                ? DEFAULT_GLITHER_COLOR
                : matchingCandleColor;

        if (!tableBe.consumeLitReceptorsForCrafting(level, recipe.litReceptorCost())) {
            return false;
        }

        ItemStack resultStack = recipe.createResultStack();
        injectHexColorIfAllowed(resultStack, recipe, matchingCandleColor);

        tableBe.setDisplayedItem(resultStack);

        edibleStack.shrink(1);
        basuke.setItemInHand(
                InteractionHand.MAIN_HAND,
                edibleStack.isEmpty() ? ItemStack.EMPTY : edibleStack
        );

        playCraftingEffects(level, tablePos, resultStack, glitherColor);
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

        /*
         * Safe even when the item has no tinted model.
         * Items that do not read ModDataComponents.HEX_COLOR will simply ignore it visually.
         */
        HexColorComponent.setSlot(
                resultStack,
                HEX_SLOT_RESULT,
                matchingCandleColor
        );
    }

    private static void playCraftingEffects(
            @NotNull ServerLevel level,
            @NotNull BlockPos tablePos,
            @NotNull ItemStack resultStack,
            int glitherColor
    ) {
        Vec3 itemDisplayCenter = itemDisplayCenter(tablePos);

        spawnGlitherTransformationParticles(level, itemDisplayCenter, glitherColor);

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

    private static void spawnGlitherTransformationParticles(
            @NotNull ServerLevel level,
            @NotNull Vec3 center,
            int glitherColor
    ) {
        ProfileTintParticles.send(
                level,
                level.random,
                ModParticleTypes.GLITHER.get(),
                glitherColor,
                center.x,
                center.y,
                center.z,
                GLITHER_PARTICLE_COUNT,
                0.22D,
                0.16D,
                0.22D,
                0.045D
        );
    }

    @Nullable
    private static Integer matchingFourCandleColor(@NotNull VocoTableBlockEntity tableBe) {
        Integer matchingColor = null;

        for (ReceptorPosition receptor : ReceptorPosition.values()) {
            int cornerColor = tableBe.getCornerHexColor(receptor);

            if (cornerColor == VocoTableBlockEntity.UNSET_HEX_COLOR) {
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
