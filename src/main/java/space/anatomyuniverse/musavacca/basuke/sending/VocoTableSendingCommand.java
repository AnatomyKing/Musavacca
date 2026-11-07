package space.anatomyuniverse.musavacca.basuke.sending;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record VocoTableSendingCommand(@NotNull Direction direction, int amount, @NotNull String playerName) {
    private static final String TAG_SENDING_PLAYER_UUID = "musavacca_voco_sending_player_uuid";
    private static final Pattern PHYSICAL_TO_BALANCE_PATTERN = Pattern.compile("^([1-9][0-9]*)[bB]@([A-Za-z0-9_]{1,16})$");
    private static final Pattern PLAYER_NAME_PATTERN = Pattern.compile("^[A-Za-z0-9_]{1,16}$");
    private static final Pattern AMOUNT_PATTERN = Pattern.compile("^[1-9][0-9]*$");

    public static boolean isCommand(@NotNull ItemStack stack) {
        return !parseCandidates(stack).isEmpty();
    }

    public static List<VocoTableSendingCommand> parseCandidates(@NotNull ItemStack stack) {
        if (!stack.is(Items.PAPER)) return List.of();
        Component customName = stack.get(DataComponents.CUSTOM_NAME);
        if (customName == null) return List.of();
        String command = customName.getString().trim();
        if (command.isEmpty()) return List.of();
        Matcher physicalToBalanceMatcher = PHYSICAL_TO_BALANCE_PATTERN.matcher(command);
        if (physicalToBalanceMatcher.matches()) {
            int amount = parseAmount(physicalToBalanceMatcher.group(1));
            if (amount <= 0) return List.of();
            return List.of(new VocoTableSendingCommand(Direction.PHYSICAL_TO_BALANCE, amount, physicalToBalanceMatcher.group(2)));
        }
        if (command.length() < 4 || command.charAt(0) != '@' || (command.charAt(command.length() - 1) != 'b' && command.charAt(command.length() - 1) != 'B')) {
            return List.of();
        }
        String body = command.substring(1, command.length() - 1);
        if (body.length() < 2) return List.of();
        List<VocoTableSendingCommand> candidates = new ArrayList<>();
        int maximumPlayerNameLength = Math.min(16, body.length() - 1);
        for (int playerNameLength = maximumPlayerNameLength; playerNameLength >= 1; --playerNameLength) {
            String playerName = body.substring(0, playerNameLength);
            String amountText = body.substring(playerNameLength);
            if (!PLAYER_NAME_PATTERN.matcher(playerName).matches() || !AMOUNT_PATTERN.matcher(amountText).matches()) {
                continue;
            }
            int amount = parseAmount(amountText);
            if (amount <= 0) continue;
            candidates.add(new VocoTableSendingCommand(Direction.BALANCE_TO_PHYSICAL, amount, playerName));
        }
        return List.copyOf(candidates);
    }

    public static void stampSender(@NotNull ItemStack stack, @NotNull UUID playerUuid) {
        if (stack.isEmpty() || !isCommand(stack)) {
            return;
        }
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putString(TAG_SENDING_PLAYER_UUID, playerUuid.toString()));
    }

    @Nullable
    public static UUID sender(@NotNull ItemStack stack) {
        if (stack.isEmpty() || !isCommand(stack)) {
            return null;
        }
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData == null) return null;
        CompoundTag tag = customData.copyTag();
        //? if >=1.21.5 {
        String uuidString = tag.getStringOr(TAG_SENDING_PLAYER_UUID, "");
        //?} else {
        /*String uuidString = tag.getString(TAG_SENDING_PLAYER_UUID);
        *///?}
        if (uuidString.isEmpty()) return null;
        try {
            return UUID.fromString(uuidString);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    public static void clearSender(@NotNull ItemStack stack) {
        if (stack.isEmpty()) return;
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData == null) return;
        if (!customData.contains(TAG_SENDING_PLAYER_UUID)) {
            return;
        }
        CompoundTag tag = customData.copyTag();
        tag.remove(TAG_SENDING_PLAYER_UUID);
        if (tag.isEmpty()) {
            stack.remove(DataComponents.CUSTOM_DATA);
            return;
        }
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    private static int parseAmount(String amountText) {
        try {
            return Integer.parseInt(amountText);
        } catch (NumberFormatException ignored) {
            return -1;
        }
    }

    public enum Direction {
        PHYSICAL_TO_BALANCE, BALANCE_TO_PHYSICAL
    }
}
