package space.anatomyuniverse.musavacca.mixin.client;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import space.anatomyuniverse.musavacca.block.custom.logic.PearlCandleConversion;

@Mixin(MultiPlayerGameMode.class)
public abstract class PearlCandleClientInteractionMixin {
    @WrapMethod(method = "performUseItemOn")
    private InteractionResult musavacca$lightCandle(LocalPlayer player, InteractionHand hand, BlockHitResult hit,
            Operation<InteractionResult> original) {
        return PearlCandleConversion.useOn(player.level(), player, player.getItemInHand(hand), hit.getBlockPos(),
                () -> original.call(player, hand, hit));
    }
}
