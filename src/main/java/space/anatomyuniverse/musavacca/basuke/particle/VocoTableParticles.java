package space.anatomyuniverse.musavacca.basuke.particle;

//? if >=26.1 {
/*import net.minecraft.world.item.ItemStackTemplate;
*///?}
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
    private VocoTableParticles() {}
    public static void spawnCraftingParticles(@NotNull ServerLevel level, @NotNull BlockPos tablePos, @NotNull ItemStack resultStack, int glitherColor,
            @NotNull BlockState stateBeforeConsumption, @NotNull BlockState stateAfterConsumption) {
        Vec3 itemDisplayCenter = itemDisplayCenter(tablePos);
        List<ReceptorPosition> consumedReceptors = findConsumedReceptors(stateBeforeConsumption, stateAfterConsumption);
        if (consumedReceptors.isEmpty()) {
            VocoTableGlitherParticles.spawnTransformation(level, itemDisplayCenter, glitherColor);
        } else {
            for (ReceptorPosition receptor : consumedReceptors) {
                VocoTableGlitherParticles.spawnReceptorDirectional(level, tablePos, receptor, glitherColor);
            }
        }
        spawnItemParticles(level, itemDisplayCenter, resultStack);
    }

    public static void spawnPhysicalToBalanceParticles(@NotNull ServerLevel level, @NotNull BlockPos tablePos, @NotNull ItemStack bananaPearlStack) {
        Vec3 itemDisplayCenter = itemDisplayCenter(tablePos);
        VocoTableGlitherParticles.spawnOutwardFromCenter(level, itemDisplayCenter, DEFAULT_GLITHER_COLOR);
        spawnItemParticles(level, itemDisplayCenter, bananaPearlStack);
    }

    public static void spawnBalanceToPhysicalParticles(@NotNull ServerLevel level, @NotNull BlockPos tablePos, @NotNull ItemStack bananaPearlStack) {
        Vec3 itemDisplayCenter = itemDisplayCenter(tablePos);
        VocoTableGlitherParticles.spawnInwardToCenter(level, itemDisplayCenter, DEFAULT_GLITHER_COLOR);
        VocoTableGlitherParticles.spawnTransformation(level, itemDisplayCenter, DEFAULT_GLITHER_COLOR);
        spawnItemParticles(level, itemDisplayCenter, bananaPearlStack);
    }

    public static Vec3 itemDisplayCenter(@NotNull BlockPos tablePos) {
        return new Vec3(tablePos.getX() + 0.5D, tablePos.getY() + 1.20D, tablePos.getZ() + 0.5D);
    }

    private static List<ReceptorPosition>
    findConsumedReceptors(@NotNull BlockState stateBeforeConsumption, @NotNull BlockState stateAfterConsumption) {
        List<ReceptorPosition> consumedReceptors = new ArrayList<>();
        for (ReceptorPosition receptor : ReceptorPosition.values()) {
            if (
                    stateBeforeConsumption.getValue(VocoTableBlock.lightProperty(receptor))
                            && !stateAfterConsumption.getValue(VocoTableBlock.lightProperty(receptor))
            ) {
                consumedReceptors.add(receptor);
            }
        }
        return List.copyOf(consumedReceptors);
    }

    private static void spawnItemParticles(@NotNull ServerLevel level, @NotNull Vec3 center, @NotNull ItemStack stack) {
        if (stack.isEmpty()) return;
        //? if >=26.1 {
        /*level.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, ItemStackTemplate.fromNonEmptyStack(stack.copyWithCount(1))), center.x, center.y,
        *///?} else {
        level.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, stack.copyWithCount(1)), center.x, center.y, center.z, 12, 0.12D, 0.10D, 0.12D, 0.045D);
        //?}
                //? if >=26.1 {
                /*center.z, 12, 0.12D, 0.10D, 0.12D, 0.045D);
                *///?}
    }
}
