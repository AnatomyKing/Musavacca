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
import space.anatomyuniverse.musavacca.block.custom.logic.PearlSlotIgnition;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoReceptorLogic;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoReceptorLogic.ReceptorPosition;
import space.anatomyuniverse.musavacca.block.entity.custom.VocoTableBlockEntity;
import space.anatomyuniverse.musavacca.component.ModDataComponents;
import space.anatomyuniverse.musavacca.entity.mob.basuke.Basuke;
import space.anatomyuniverse.musavacca.particle.ModParticleTypes;
import space.anatomyuniverse.musavacca.particle.tinted.ProfileTintParticles;

import java.util.ArrayList;
import java.util.List;

public final class VocoTableCrafting {
    private static final int GLITHER_PARTICLE_COUNT = 3;

    private static final int DEFAULT_GLITHER_COLOR = 0xCDB249;

    private static final double GLITHER_FORWARD_SPEED = 0.085D;
    private static final double GLITHER_FORWARD_SPEED_SPREAD = 0.018D;
    private static final double GLITHER_MIN_FORWARD_SPEED = 0.055D;

    private static final double GLITHER_SIDEWAYS_SPEED_SPREAD = 0.030D;

    private static final double GLITHER_UPWARD_SPEED = -0.055D;
    private static final double GLITHER_UPWARD_SPEED_SPREAD = -0.016D;
    private static final double GLITHER_MIN_UPWARD_SPEED = -0.018D;

    private static final double GLITHER_SPAWN_SPREAD = 0.025D;
    private static final double GLITHER_START_Y_OFFSET = -0.13D;

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

        VocoTableCraftingRecipe recipe =
                VocoTableCraftingRecipes.findMatchingRecipe(
                        displayedStack,
                        edibleStack
                );

        if (recipe == null) {
            return null;
        }

        if (
                !tableBe.hasLitReceptorCost(
                        recipe.litReceptorCost()
                )
        ) {
            return null;
        }

        if (
                recipe.hexColorInject()
                        && matchingFourCandleColor(tableBe) == null
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

        int glitherColor =
                matchingCandleColor == null
                        ? DEFAULT_GLITHER_COLOR
                        : matchingCandleColor;

        BlockState stateBeforeConsumption =
                level.getBlockState(tablePos);

        if (
                !tableBe.consumeLitReceptorsForCrafting(
                        level,
                        recipe.litReceptorCost()
                )
        ) {
            return false;
        }

        List<ReceptorPosition> consumedReceptors =
                findConsumedReceptors(
                        stateBeforeConsumption,
                        level.getBlockState(tablePos)
                );

        ItemStack resultStack =
                recipe.createResultStack();

        injectHexColorIfAllowed(
                resultStack,
                recipe,
                matchingCandleColor
        );

        tableBe.setDisplayedItem(resultStack);

        edibleStack.shrink(1);

        basuke.setItemInHand(
                InteractionHand.MAIN_HAND,
                edibleStack.isEmpty()
                        ? ItemStack.EMPTY
                        : edibleStack
        );

        playCraftingEffects(
                level,
                tablePos,
                resultStack,
                glitherColor,
                consumedReceptors
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

    private static void playCraftingEffects(
            @NotNull ServerLevel level,
            @NotNull BlockPos tablePos,
            @NotNull ItemStack resultStack,
            int glitherColor,
            @NotNull List<ReceptorPosition> consumedReceptors
    ) {
        Vec3 itemDisplayCenter =
                itemDisplayCenter(tablePos);

        if (consumedReceptors.isEmpty()) {
            spawnGlitherTransformationParticles(
                    level,
                    itemDisplayCenter,
                    glitherColor
            );
        } else {
            for (
                    ReceptorPosition receptor
                    : consumedReceptors
            ) {
                spawnDirectionalGlitherParticles(
                        level,
                        tablePos,
                        receptor,
                        glitherColor
                );
            }
        }

        level.sendParticles(
                new ItemParticleOption(
                        ParticleTypes.ITEM,
                        resultStack.copyWithCount(1)
                ),
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

    private static List<ReceptorPosition>
    findConsumedReceptors(
            @NotNull BlockState stateBeforeConsumption,
            @NotNull BlockState stateAfterConsumption
    ) {
        List<ReceptorPosition> consumedReceptors =
                new ArrayList<>();

        for (
                ReceptorPosition receptor
                : ReceptorPosition.values()
        ) {
            if (
                    stateBeforeConsumption.getValue(
                            VocoTableBlock.lightProperty(
                                    receptor
                            )
                    )
                            && !stateAfterConsumption.getValue(
                            VocoTableBlock.lightProperty(
                                    receptor
                            )
                    )
            ) {
                consumedReceptors.add(receptor);
            }
        }

        return List.copyOf(consumedReceptors);
    }

    private static Vec3 itemDisplayCenter(
            BlockPos tablePos
    ) {
        return new Vec3(
                tablePos.getX() + ITEM_DISPLAY_X,
                tablePos.getY() + ITEM_DISPLAY_Y,
                tablePos.getZ() + ITEM_DISPLAY_Z
        );
    }

    private static void spawnDirectionalGlitherParticles(
            @NotNull ServerLevel level,
            @NotNull BlockPos tablePos,
            @NotNull ReceptorPosition receptor,
            int glitherColor
    ) {
        /*
         * Use the same shared slot definition as ignition
         * and shearing, so every system agrees on the exact
         * Banana Pearl position.
         */
        PearlSlotIgnition.Slot pearlSlot =
                VocoReceptorLogic.pearlSlot(
                        VocoTableBlock.lightProperty(
                                receptor
                        ),
                        VocoTableBlock.portalProperty(
                                receptor
                        ),
                        receptor
                );

        Vec3 source =
                pearlSlot.pearlPopPosition(tablePos);

        Vec3 tableCenter =
                itemDisplayCenter(tablePos);

        Vec3 outwardDirection = new Vec3(
                source.x - tableCenter.x,
                0.0D,
                source.z - tableCenter.z
        ).normalize();

        Vec3 sidewaysDirection = new Vec3(
                -outwardDirection.z,
                0.0D,
                outwardDirection.x
        );

        for (
                int i = 0;
                i < GLITHER_PARTICLE_COUNT;
                ++i
        ) {
            double forwardSpeed = Math.max(
                    GLITHER_MIN_FORWARD_SPEED,
                    GLITHER_FORWARD_SPEED
                            + level.random.nextGaussian()
                            * GLITHER_FORWARD_SPEED_SPREAD
            );

            double sidewaysSpeed =
                    level.random.nextGaussian()
                            * GLITHER_SIDEWAYS_SPEED_SPREAD;

            double upwardSpeed = Math.max(
                    GLITHER_MIN_UPWARD_SPEED,
                    GLITHER_UPWARD_SPEED
                            + level.random.nextGaussian()
                            * GLITHER_UPWARD_SPEED_SPREAD
            );

            double particleX =
                    source.x
                            + level.random.nextGaussian()
                            * GLITHER_SPAWN_SPREAD;

            double particleY =
                    source.y
                            + GLITHER_START_Y_OFFSET
                            + level.random.nextGaussian()
                            * GLITHER_SPAWN_SPREAD;

            double particleZ =
                    source.z
                            + level.random.nextGaussian()
                            * GLITHER_SPAWN_SPREAD;

            double particleXd =
                    outwardDirection.x
                            * forwardSpeed
                            + sidewaysDirection.x
                            * sidewaysSpeed;

            double particleZd =
                    outwardDirection.z
                            * forwardSpeed
                            + sidewaysDirection.z
                            * sidewaysSpeed;

            ProfileTintParticles.sendExact(
                    level,
                    level.random,
                    ModParticleTypes.GLITHER.get(),
                    glitherColor,
                    particleX,
                    particleY,
                    particleZ,
                    particleXd,
                    upwardSpeed,
                    particleZd
            );
        }
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