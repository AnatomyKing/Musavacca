package space.anatomyuniverse.musavacca.basuke.particle;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import space.anatomyuniverse.musavacca.block.custom.VocoTableBlock;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoReceptorLogic.ReceptorPosition;

import java.util.ArrayList;
import java.util.List;

public final class VocoTableParticles {
    public static final int DEFAULT_GLITHER_COLOR = 0xCDB249;

    private static final int ITEM_PARTICLE_COUNT = 12;

    private static final double ITEM_DISPLAY_X = 0.5D;
    private static final double ITEM_DISPLAY_Y = 1.20D;
    private static final double ITEM_DISPLAY_Z = 0.5D;

    private VocoTableParticles() {}

    public static void spawnCraftingParticles(
            @NotNull ServerLevel level,
            @NotNull BlockPos tablePos,
            @NotNull ItemStack resultStack,
            int glitherColor,
            @NotNull BlockState stateBeforeConsumption,
            @NotNull BlockState stateAfterConsumption
    ) {
        Vec3 itemDisplayCenter =
                itemDisplayCenter(tablePos);

        List<ReceptorPosition> consumedReceptors =
                findConsumedReceptors(
                        stateBeforeConsumption,
                        stateAfterConsumption
                );

        if (consumedReceptors.isEmpty()) {
            VocoTableGlitherParticles
                    .spawnTransformation(
                            level,
                            itemDisplayCenter,
                            glitherColor
                    );
        } else {
            for (
                    ReceptorPosition receptor
                    : consumedReceptors
            ) {
                VocoTableGlitherParticles
                        .spawnReceptorDirectional(
                                level,
                                tablePos,
                                receptor,
                                glitherColor
                        );
            }
        }

        spawnItemParticles(
                level,
                itemDisplayCenter,
                resultStack
        );
    }

    public static void spawnPhysicalToBalanceParticles(
            @NotNull ServerLevel level,
            @NotNull BlockPos tablePos,
            @NotNull ItemStack bananaPearlStack
    ) {
        Vec3 itemDisplayCenter =
                itemDisplayCenter(tablePos);

        VocoTableGlitherParticles
                .spawnOutwardFromCenter(
                        level,
                        itemDisplayCenter,
                        DEFAULT_GLITHER_COLOR
                );

        spawnItemParticles(
                level,
                itemDisplayCenter,
                bananaPearlStack
        );
    }

    public static void spawnBalanceToPhysicalParticles(
            @NotNull ServerLevel level,
            @NotNull BlockPos tablePos,
            @NotNull ItemStack bananaPearlStack
    ) {
        Vec3 itemDisplayCenter =
                itemDisplayCenter(tablePos);

        VocoTableGlitherParticles
                .spawnInwardToCenter(
                        level,
                        itemDisplayCenter,
                        DEFAULT_GLITHER_COLOR
                );

        VocoTableGlitherParticles
                .spawnTransformation(
                        level,
                        itemDisplayCenter,
                        DEFAULT_GLITHER_COLOR
                );

        spawnItemParticles(
                level,
                itemDisplayCenter,
                bananaPearlStack
        );
    }

    public static Vec3 itemDisplayCenter(
            @NotNull BlockPos tablePos
    ) {
        return new Vec3(
                tablePos.getX() + ITEM_DISPLAY_X,
                tablePos.getY() + ITEM_DISPLAY_Y,
                tablePos.getZ() + ITEM_DISPLAY_Z
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

    private static void spawnItemParticles(
            @NotNull ServerLevel level,
            @NotNull Vec3 center,
            @NotNull ItemStack stack
    ) {
        if (stack.isEmpty()) {
            return;
        }

        level.sendParticles(
                new ItemParticleOption(
                        ParticleTypes.ITEM,
                        stack.copyWithCount(1)
                ),
                center.x,
                center.y,
                center.z,
                ITEM_PARTICLE_COUNT,
                0.12D,
                0.10D,
                0.12D,
                0.045D
        );
    }
}


