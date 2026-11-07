package space.anatomyuniverse.musavacca.basuke.summon;

//? if >=26.1 {
/*import net.minecraft.world.item.ItemStackTemplate;
*///?}
import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.anatomyuniverse.musavacca.basuke.eating.VocoTableEatingLogic;
import space.anatomyuniverse.musavacca.block.custom.VocoTableBlock;
import space.anatomyuniverse.musavacca.block.custom.logic.GlinsterLogic;
import space.anatomyuniverse.musavacca.block.entity.custom.VocoTableBlockEntity;
import space.anatomyuniverse.musavacca.component.ModDataComponents;
import space.anatomyuniverse.musavacca.entity.mob.basuke.Basuke;
import space.anatomyuniverse.musavacca.item.ModItems;
import space.anatomyuniverse.musavacca.item.custom.OpenVocoCallerItem;
import space.anatomyuniverse.musavacca.item.custom.SimCardItem;
import space.anatomyuniverse.musavacca.teleport.HexTeleportDirectory;
import space.anatomyuniverse.musavacca.tint.MusavaccaTints;

public final class BasukeVocoCalling {
    public static final int EATING_TIME_TICKS = VocoTableEatingLogic.DEFAULT_EATING_TIME_TICKS;
    private static final String TAG_CALLING_PLAYER_UUID = "musavacca_voco_calling_player_uuid";

    private BasukeVocoCalling() {
    }

    @Nullable
    public static ActiveCalling findActiveCalling(@NotNull Basuke basuke, @NotNull ServerLevel level, @NotNull ItemStack simCardStack) {
        if (!isBlankSimCard(simCardStack) || !basuke.isBoundToVocoTable()) {
            return null;
        }
        UUID givingPlayerUuid = readGivingPlayer(simCardStack);
        if (givingPlayerUuid == null) return null;
        BlockPos tablePos = basuke.getVocoTablePos();
        if (tablePos == null) return null;
        BlockState state = level.getBlockState(tablePos);
        if (!(state.getBlock() instanceof VocoTableBlock) || !state.hasProperty(VocoTableBlock.ROTARY_DIALERS)
                || !state.getValue(VocoTableBlock.ROTARY_DIALERS)) {
            return null;
        }
        if (!(level.getBlockEntity(tablePos) instanceof VocoTableBlockEntity tableBe)) {
            return null;
        }
        ItemStack displayedPhone = tableBe.getDisplayedItem();
        if (!isEmptyBananaPhone(displayedPhone)) return null;
        int matchingCandleColor = tableBe.getMatchingCandleHexColor();
        if (matchingCandleColor == MusavaccaTints.NO_TINT) return null;
        HexTeleportDirectory.PhoneRegistration registration = HexTeleportDirectory.get(level.getServer()).getPhoneRegistrationByHex(matchingCandleColor)
                .orElse(null);
        if (registration == null || !registration.ownerUuid().equals(givingPlayerUuid)) {
            return null;
        }
        return new ActiveCalling(matchingCandleColor, givingPlayerUuid);
    }

    public static boolean completeActiveCalling(@NotNull Basuke basuke, @NotNull ServerLevel level, @NotNull ItemStack simCardStack,
            @NotNull ActiveCalling expectedCalling) {
        ActiveCalling activeCalling = findActiveCalling(basuke, level, simCardStack);
        if (activeCalling == null || !activeCalling.equals(expectedCalling)) {
            return false;
        }
        BlockPos tablePos = basuke.getVocoTablePos();
        if (tablePos == null || !(level.getBlockEntity(tablePos) instanceof VocoTableBlockEntity tableBe)) {
            return false;
        }
        ItemStack displayedPhone = tableBe.getDisplayedItem();
        if (!isEmptyBananaPhone(displayedPhone)) return false;
        ItemStack recoveredSim = simCardStack.copyWithCount(1);
        clearGivingPlayer(recoveredSim);
        recoveredSim.set(ModDataComponents.HEX_COLOR.get(), activeCalling.hexColor());
        ItemStack recoveredPhone = displayedPhone.copyWithCount(1);
        //? if >=26.1 {
        /*recoveredPhone.set(DataComponents.BUNDLE_CONTENTS, new BundleContents(List.of(ItemStackTemplate.fromNonEmptyStack(recoveredSim))));
        *///?} else {
        recoveredPhone.set(DataComponents.BUNDLE_CONTENTS, new BundleContents(List.of(recoveredSim)));
        //?}
        tableBe.setDisplayedItem(recoveredPhone, 1);
        simCardStack.shrink(1);
        basuke.setItemInHand(InteractionHand.MAIN_HAND, simCardStack.isEmpty() ? ItemStack.EMPTY : simCardStack);
        GlinsterLogic.burst(level, tablePos);
        playCallingSounds(level, tablePos);
        return true;
    }

    public static void stampGivingPlayer(@NotNull ItemStack stack, @NotNull UUID playerUuid) {
        if (!isSimCard(stack)) return;
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putString(TAG_CALLING_PLAYER_UUID, playerUuid.toString()));
    }

    public static void clearGivingPlayer(@NotNull ItemStack stack) {
        if (!isSimCard(stack)) return;
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData == null || !customData.contains(TAG_CALLING_PLAYER_UUID)) {
            return;
        }
        CompoundTag tag = customData.copyTag();
        tag.remove(TAG_CALLING_PLAYER_UUID);
        if (tag.isEmpty()) {
            stack.remove(DataComponents.CUSTOM_DATA);
            return;
        }
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    @Nullable
    private static UUID readGivingPlayer(@NotNull ItemStack stack) {
        if (!isSimCard(stack)) return null;
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData == null) return null;
        CompoundTag tag = customData.copyTag();
        //? if >=1.21.5 {
        String uuidString = tag.getStringOr(TAG_CALLING_PLAYER_UUID, "");
        //?} else {
        /*String uuidString = tag.getString(TAG_CALLING_PLAYER_UUID);
        *///?}
        if (uuidString.isEmpty()) return null;
        try {
            return UUID.fromString(uuidString);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    private static boolean isSimCard(@NotNull ItemStack stack) {
        return !stack.isEmpty() && stack.is(ModItems.SIM_CARD.get()) && stack.getItem() instanceof SimCardItem;
    }

    private static boolean isBlankSimCard(@NotNull ItemStack stack) {
        return isSimCard(stack) && !SimCardItem.hasStoredHex(stack);
    }

    private static boolean isEmptyBananaPhone(@NotNull ItemStack stack) {
        return !stack.isEmpty() && stack.is(ModItems.BANANA_PHONE.get()) && stack.getItem() instanceof OpenVocoCallerItem
                && OpenVocoCallerItem.getSim(stack).isEmpty();
    }

    private static void playCallingSounds(@NotNull ServerLevel level, @NotNull BlockPos tablePos) {
        Vec3 itemDisplayCenter = new Vec3(tablePos.getX() + 0.5D, tablePos.getY() + 1.20D, tablePos.getZ() + 0.5D);
        level.playSound(null, itemDisplayCenter.x, itemDisplayCenter.y, itemDisplayCenter.z, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 0.85F,
                1.35F);
        level.playSound(null, itemDisplayCenter.x, itemDisplayCenter.y, itemDisplayCenter.z, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.BLOCKS, 0.65F,
                1.65F);
    }

    public record ActiveCalling(int hexColor, @NotNull UUID ownerUuid) {
        public ActiveCalling {
            hexColor &= 0xFFFFFF;
        }
    }
}
