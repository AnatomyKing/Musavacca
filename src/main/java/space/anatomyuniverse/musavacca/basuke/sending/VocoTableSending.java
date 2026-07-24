package space.anatomyuniverse.musavacca.basuke.sending;

import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.anatomyuniverse.musavacca.bar.balance.BalanceApi;
import space.anatomyuniverse.musavacca.bar.balance.BalanceStore;
import space.anatomyuniverse.musavacca.block.custom.VocoTableBlock;
import space.anatomyuniverse.musavacca.block.entity.custom.VocoTableBlockEntity;
import space.anatomyuniverse.musavacca.basuke.eating.VocoTableEatingLogic;
import space.anatomyuniverse.musavacca.basuke.particle.VocoTableParticles;
import space.anatomyuniverse.musavacca.entity.mob.basuke.Basuke;
import space.anatomyuniverse.musavacca.item.ModItems;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class VocoTableSending {
    public static final int EATING_TIME_TICKS =
            VocoTableEatingLogic.DEFAULT_EATING_TIME_TICKS;

    private VocoTableSending() {}

    @Nullable
    public static ActiveSending findActiveSending(
            @NotNull Basuke basuke,
            @NotNull ServerLevel level,
            @NotNull ItemStack paperStack
    ) {
        if (
                paperStack.isEmpty()
                        || !basuke.isBoundToVocoTable()
        ) {
            return null;
        }

        UUID sendingPlayerUuid =
                VocoTableSendingAuthorization
                        .read(paperStack);

        if (sendingPlayerUuid == null) {
            return null;
        }

        BlockPos tablePos =
                basuke.getVocoTablePos();

        if (tablePos == null) {
            return null;
        }

        BlockState state =
                level.getBlockState(tablePos);

        if (
                !(state.getBlock()
                        instanceof VocoTableBlock)
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

        MinecraftServer server =
                level.getServer();

        rememberSendingPlayerIfOnline(
                server,
                sendingPlayerUuid
        );

        List<ResolvedCommand> resolvedCommands =
                resolveCommands(
                        server,
                        paperStack
                );

        if (resolvedCommands.size() != 1) {
            return null;
        }

        ResolvedCommand resolvedCommand =
                resolvedCommands.get(0);

        ActiveSending activeSending =
                new ActiveSending(
                        resolvedCommand.command(),
                        sendingPlayerUuid,
                        resolvedCommand.targetPlayerUuid()
                );

        return canExecute(
                server,
                tableBe,
                activeSending
        )
                ? activeSending
                : null;
    }

    public static boolean completeActiveSending(
            @NotNull Basuke basuke,
            @NotNull ServerLevel level,
            @NotNull ItemStack paperStack,
            @NotNull ActiveSending expectedSending
    ) {
        ActiveSending activeSending =
                findActiveSending(
                        basuke,
                        level,
                        paperStack
                );

        if (
                activeSending == null
                        || !activeSending.equals(
                        expectedSending
                )
        ) {
            return false;
        }

        BlockPos tablePos =
                basuke.getVocoTablePos();

        if (
                tablePos == null
                        || !(level.getBlockEntity(tablePos)
                        instanceof VocoTableBlockEntity tableBe)
        ) {
            return false;
        }

        MinecraftServer server =
                level.getServer();

        boolean completed =
                switch (
                        activeSending.command().direction()
                ) {
                    case PHYSICAL_TO_BALANCE ->
                            completePhysicalToBalance(
                                    server,
                                    tableBe,
                                    activeSending
                            );

                    case BALANCE_TO_PHYSICAL ->
                            completeBalanceToPhysical(
                                    server,
                                    tableBe,
                                    activeSending
                            );
                };

        if (!completed) {
            return false;
        }

        spawnSendingParticles(
                level,
                tablePos,
                activeSending.command().direction()
        );

        paperStack.shrink(1);

        basuke.setItemInHand(
                InteractionHand.MAIN_HAND,
                paperStack.isEmpty()
                        ? ItemStack.EMPTY
                        : paperStack
        );

        return true;
    }

    private static void spawnSendingParticles(
            @NotNull ServerLevel level,
            @NotNull BlockPos tablePos,
            @NotNull VocoTableSendingCommand.Direction direction
    ) {
        ItemStack bananaPearlStack =
                new ItemStack(
                        ModItems.BANANA_PEARL.get()
                );

        switch (direction) {
            case PHYSICAL_TO_BALANCE ->
                    VocoTableParticles
                            .spawnPhysicalToBalanceParticles(
                                    level,
                                    tablePos,
                                    bananaPearlStack
                            );

            case BALANCE_TO_PHYSICAL ->
                    VocoTableParticles
                            .spawnBalanceToPhysicalParticles(
                                    level,
                                    tablePos,
                                    bananaPearlStack
                            );
        }
    }

    private static void rememberSendingPlayerIfOnline(
            MinecraftServer server,
            UUID sendingPlayerUuid
    ) {
        ServerPlayer sendingPlayer =
                server.getPlayerList()
                        .getPlayer(sendingPlayerUuid);

        if (sendingPlayer != null) {
            BalanceStore.get(server)
                    .rememberPlayer(sendingPlayer);
        }
    }

    private static List<ResolvedCommand> resolveCommands(
            MinecraftServer server,
            ItemStack paperStack
    ) {
        List<VocoTableSendingCommand> candidates =
                VocoTableSendingCommand
                        .parseCandidates(paperStack);

        if (candidates.isEmpty()) {
            return List.of();
        }

        BalanceStore store =
                BalanceStore.get(server);

        List<ResolvedCommand> resolved =
                new ArrayList<>();

        for (
                VocoTableSendingCommand candidate
                : candidates
        ) {
            Optional<UUID> targetPlayerUuid =
                    findKnownPlayerUuid(
                            server,
                            store,
                            candidate.playerName()
                    );

            if (targetPlayerUuid.isEmpty()) {
                continue;
            }

            resolved.add(
                    new ResolvedCommand(
                            candidate,
                            targetPlayerUuid.get()
                    )
            );
        }

        if (resolved.size() <= 1) {
            return List.copyOf(resolved);
        }

        ResolvedCommand first =
                resolved.get(0);

        for (
                int index = 1;
                index < resolved.size();
                ++index
        ) {
            ResolvedCommand other =
                    resolved.get(index);

            if (
                    !first.targetPlayerUuid()
                            .equals(
                                    other.targetPlayerUuid()
                            )
                            || first.command().amount()
                            != other.command().amount()
            ) {
                return List.of();
            }
        }

        return List.of(first);
    }

    private static Optional<UUID> findKnownPlayerUuid(
            MinecraftServer server,
            BalanceStore store,
            String playerName
    ) {
        ServerPlayer onlinePlayer =
                server.getPlayerList()
                        .getPlayerByName(playerName);

        if (onlinePlayer != null) {
            store.rememberPlayer(onlinePlayer);
            return Optional.of(
                    onlinePlayer.getUUID()
            );
        }

        return store.findKnownUuid(playerName);
    }

    private static boolean canExecute(
            MinecraftServer server,
            VocoTableBlockEntity tableBe,
            ActiveSending activeSending
    ) {
        return switch (
                activeSending.command().direction()
        ) {
            case PHYSICAL_TO_BALANCE ->
                    canConvertPhysicalToBalance(
                            server,
                            tableBe,
                            activeSending
                    );

            case BALANCE_TO_PHYSICAL ->
                    canConvertBalanceToPhysical(
                            server,
                            tableBe,
                            activeSending
                    );
        };
    }

    private static boolean canConvertPhysicalToBalance(
            MinecraftServer server,
            VocoTableBlockEntity tableBe,
            ActiveSending activeSending
    ) {
        int amount =
                activeSending.command().amount();

        if (
                amount <= 0
                        || !isPlainBananaPearl(
                        tableBe.getDisplayedItem()
                )
                        || tableBe.getDisplayedItemCount()
                        < amount
        ) {
            return false;
        }

        int targetBalance =
                BalanceApi.getBalance(
                        server,
                        activeSending.targetPlayerUuid()
                );

        return (long) targetBalance + amount
                <= Integer.MAX_VALUE;
    }

    private static boolean canConvertBalanceToPhysical(
            MinecraftServer server,
            VocoTableBlockEntity tableBe,
            ActiveSending activeSending
    ) {
        int amount =
                activeSending.command().amount();

        if (
                amount <= 0
                        || !canDeductTargetBalance(
                        server,
                        activeSending.sendingPlayerUuid(),
                        activeSending.targetPlayerUuid()
                )
                        || !BalanceApi.hasBalance(
                        server,
                        activeSending.targetPlayerUuid(),
                        amount
                )
        ) {
            return false;
        }

        if (
                tableBe.hasDisplayedItem()
                        && !isPlainBananaPearl(
                        tableBe.getDisplayedItem()
                )
        ) {
            return false;
        }

        return (long) tableBe
                .getDisplayedItemCount()
                + amount
                <= Integer.MAX_VALUE;
    }

    private static boolean canDeductTargetBalance(
            MinecraftServer server,
            UUID sendingPlayerUuid,
            UUID targetPlayerUuid
    ) {
        if (
                sendingPlayerUuid.equals(
                        targetPlayerUuid
                )
        ) {
            return true;
        }

        ServerPlayer sendingPlayer =
                server.getPlayerList()
                        .getPlayer(sendingPlayerUuid);

        return sendingPlayer != null
                && server.getPlayerList()
                .isOp(
                        sendingPlayer.getGameProfile()
                );
    }

    private static boolean completePhysicalToBalance(
            MinecraftServer server,
            VocoTableBlockEntity tableBe,
            ActiveSending activeSending
    ) {
        int amount =
                activeSending.command().amount();

        ItemStack previousDisplayedItem =
                tableBe.getDisplayedItem()
                        .copyWithCount(1);

        int previousDisplayedItemCount =
                tableBe.getDisplayedItemCount();

        if (!tableBe.consumeDisplayedItems(amount)) {
            return false;
        }

        if (
                BalanceApi.addBalance(
                        server,
                        activeSending.targetPlayerUuid(),
                        amount
                )
        ) {
            return true;
        }

        tableBe.setDisplayedItem(
                previousDisplayedItem,
                previousDisplayedItemCount
        );

        return false;
    }

    private static boolean completeBalanceToPhysical(
            MinecraftServer server,
            VocoTableBlockEntity tableBe,
            ActiveSending activeSending
    ) {
        int amount =
                activeSending.command().amount();

        if (
                !BalanceApi.deductBalance(
                        server,
                        activeSending.targetPlayerUuid(),
                        amount
                )
        ) {
            return false;
        }

        int previousDisplayedItemCount =
                tableBe.getDisplayedItemCount();

        ItemStack bananaPearl =
                tableBe.hasDisplayedItem()
                        ? tableBe.getDisplayedItem()
                        .copyWithCount(1)
                        : new ItemStack(
                        ModItems.BANANA_PEARL.get()
                );

        tableBe.setDisplayedItem(
                bananaPearl,
                previousDisplayedItemCount + amount
        );

        return true;
    }

    private static boolean isPlainBananaPearl(
            ItemStack stack
    ) {
        return !stack.isEmpty()
                && stack.is(
                ModItems.BANANA_PEARL.get()
        )
                && ItemStack.isSameItemSameComponents(
                stack,
                new ItemStack(
                        ModItems.BANANA_PEARL.get()
                )
        );
    }

    public record ActiveSending(
            @NotNull VocoTableSendingCommand command,
            @NotNull UUID sendingPlayerUuid,
            @NotNull UUID targetPlayerUuid
    ) {}

    private record ResolvedCommand(
            VocoTableSendingCommand command,
            UUID targetPlayerUuid
    ) {}
}
