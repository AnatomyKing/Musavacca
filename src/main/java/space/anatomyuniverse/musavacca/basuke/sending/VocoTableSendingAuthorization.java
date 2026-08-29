package space.anatomyuniverse.musavacca.basuke.sending;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public final class VocoTableSendingAuthorization {
    private static final String TAG_SENDING_PLAYER_UUID =
            "musavacca_voco_sending_player_uuid";

    private VocoTableSendingAuthorization() {}

    public static void stamp(
            @NotNull ItemStack stack,
            @NotNull UUID playerUuid
    ) {
        if (
                stack.isEmpty()
                        || !VocoTableSendingCommand
                        .isCommand(stack)
        ) {
            return;
        }

        CustomData.update(
                DataComponents.CUSTOM_DATA,
                stack,
                tag -> tag.putString(
                        TAG_SENDING_PLAYER_UUID,
                        playerUuid.toString()
                )
        );
    }

    @Nullable
    public static UUID read(
            @NotNull ItemStack stack
    ) {
        if (
                stack.isEmpty()
                        || !VocoTableSendingCommand
                        .isCommand(stack)
        ) {
            return null;
        }

        CustomData customData =
                stack.get(
                        DataComponents.CUSTOM_DATA
                );

        if (customData == null) {
            return null;
        }

        CompoundTag tag = customData.copyTag();
        //? if >=1.21.5
        String uuidString = tag.getStringOr(TAG_SENDING_PLAYER_UUID, "");
        //? if <1.21.5
        //String uuidString = tag.getString(TAG_SENDING_PLAYER_UUID);

        if (uuidString.isEmpty()) {
            return null;
        }

        try {
            return UUID.fromString(uuidString);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    public static void clear(
            @NotNull ItemStack stack
    ) {
        if (stack.isEmpty()) {
            return;
        }

        CustomData customData =
                stack.get(
                        DataComponents.CUSTOM_DATA
                );

        if (customData == null) {
            return;
        }

        if (
                !customData.contains(
                        TAG_SENDING_PLAYER_UUID
                )
        ) {
            return;
        }

        CompoundTag tag =
                customData.copyTag();

        tag.remove(
                TAG_SENDING_PLAYER_UUID
        );

        if (tag.isEmpty()) {
            stack.remove(
                    DataComponents.CUSTOM_DATA
            );
            return;
        }

        stack.set(
                DataComponents.CUSTOM_DATA,
                CustomData.of(tag)
        );
    }
}


