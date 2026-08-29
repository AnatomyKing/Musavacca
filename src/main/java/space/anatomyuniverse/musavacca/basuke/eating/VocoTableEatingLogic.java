package space.anatomyuniverse.musavacca.basuke.eating;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.anatomyuniverse.musavacca.basuke.craft.VocoTableCrafting;
import space.anatomyuniverse.musavacca.basuke.craft.VocoTableCraftingRecipe;
import space.anatomyuniverse.musavacca.basuke.sending.VocoTableSending;
import space.anatomyuniverse.musavacca.basuke.sending.VocoTableSendingAuthorization;
import space.anatomyuniverse.musavacca.basuke.sending.VocoTableSendingCommand;
import space.anatomyuniverse.musavacca.basuke.summon.BasukeVocoCalling;
import space.anatomyuniverse.musavacca.entity.mob.basuke.Basuke;

public final class VocoTableEatingLogic {
    public static final int DEFAULT_EATING_TIME_TICKS = 28;

    private VocoTableEatingLogic() {}

    @Nullable
    public static VocoTableEatingAction findActiveAction(
            @NotNull Basuke basuke,
            @NotNull ServerLevel level,
            @NotNull ItemStack heldStack
    ) {
        VocoTableSending.ActiveSending activeSending =
                VocoTableSending.findActiveSending(
                        basuke,
                        level,
                        heldStack
                );

        if (activeSending != null) {
            return new SendingAction(activeSending);
        }

        BasukeVocoCalling.ActiveCalling activeCalling =
                BasukeVocoCalling.findActiveCalling(
                        basuke,
                        level,
                        heldStack
                );

        if (activeCalling != null) {
            return new CallingAction(activeCalling);
        }

        VocoTableCraftingRecipe recipe =
                VocoTableCrafting.findActiveRecipe(
                        basuke,
                        level,
                        heldStack
                );

        return recipe == null
                ? null
                : new CraftingAction(recipe);
    }

    public static void beforeItemInteraction(
            @NotNull Basuke basuke,
            @NotNull Player player,
            @NotNull InteractionHand hand
    ) {
        if (
                basuke.level().isClientSide()
                        || !player.getItemInHand(hand).isEmpty()
        ) {
            return;
        }

        ItemStack heldStack =
                basuke.getMainHandItem();

        if (
                VocoTableSendingCommand
                        .isCommand(heldStack)
        ) {
            VocoTableSendingAuthorization
                    .clear(heldStack);
        }

        BasukeVocoCalling.clearGivingPlayer(
                heldStack
        );
    }

    public static void afterItemInteraction(
            @NotNull Basuke basuke,
            @NotNull Player player,
            boolean wasHoldingNothing
    ) {
        if (
                basuke.level().isClientSide()
                        || !wasHoldingNothing
        ) {
            return;
        }

        ItemStack heldStack =
                basuke.getMainHandItem();

        if (
                VocoTableSendingCommand
                        .isCommand(heldStack)
        ) {
            VocoTableSendingAuthorization.stamp(
                    heldStack,
                    player.getUUID()
            );
        }

        BasukeVocoCalling.stampGivingPlayer(
                heldStack,
                player.getUUID()
        );
    }

    private record CraftingAction(
            @NotNull VocoTableCraftingRecipe recipe
    ) implements VocoTableEatingAction {
        @Override
        public int eatingTimeTicks() {
            return this.recipe.eatingTimeTicks();
        }

        @Override
        public boolean complete(
                Basuke basuke,
                ServerLevel level,
                ItemStack heldStack
        ) {
            return VocoTableCrafting
                    .completeActiveRecipe(
                            basuke,
                            level,
                            heldStack,
                            this.recipe
                    );
        }
    }

    private record SendingAction(
            @NotNull VocoTableSending.ActiveSending activeSending
    ) implements VocoTableEatingAction {
        @Override
        public int eatingTimeTicks() {
            return VocoTableSending.EATING_TIME_TICKS;
        }

        @Override
        public boolean complete(
                Basuke basuke,
                ServerLevel level,
                ItemStack heldStack
        ) {
            return VocoTableSending
                    .completeActiveSending(
                            basuke,
                            level,
                            heldStack,
                            this.activeSending
                    );
        }
    }

    private record CallingAction(
            @NotNull BasukeVocoCalling.ActiveCalling activeCalling
    ) implements VocoTableEatingAction {
        @Override
        public int eatingTimeTicks() {
            return BasukeVocoCalling.EATING_TIME_TICKS;
        }

        @Override
        public boolean complete(
                Basuke basuke,
                ServerLevel level,
                ItemStack heldStack
        ) {
            return BasukeVocoCalling
                    .completeActiveCalling(
                            basuke,
                            level,
                            heldStack,
                            this.activeCalling
                    );
        }
    }
}
