package space.anatomyuniverse.musavacca.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import space.anatomyuniverse.musavacca.block.custom.logic.PearlCandleConversion;

@Mixin(ServerPlayerGameMode.class)
public abstract class PearlCandleServerInteractionMixin {
    @WrapMethod(method = "useItemOn")
    private InteractionResult musavacca$lightCandle(ServerPlayer player, Level level, ItemStack stack, InteractionHand hand, BlockHitResult hit,
            Operation<InteractionResult> original) {
        return PearlCandleConversion.useOn(level, player, stack, hit.getBlockPos(), () -> original.call(player, level, stack, hand, hit));
    }
}
