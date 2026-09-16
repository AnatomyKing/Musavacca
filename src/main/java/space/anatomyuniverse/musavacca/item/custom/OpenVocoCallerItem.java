package space.anatomyuniverse.musavacca.item.custom;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;

//? if <1.21.2 {
/*import net.minecraft.ChatFormatting;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.TooltipFlag;
*///?}

import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;

//? if >=1.21.5
import net.minecraft.world.item.component.TooltipDisplay;

import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import space.anatomyuniverse.musavacca.MusaCore;
import space.anatomyuniverse.musavacca.gui.menu.VocoCallerMenu;
import space.anatomyuniverse.musavacca.teleport.HexTeleportDirectory;
import space.anatomyuniverse.musavacca.vococaller.VocoCallerBundleTooltip;
import space.anatomyuniverse.musavacca.vococaller.VocoCallerNetwork;

import java.util.List;
import java.util.Optional;

public class OpenVocoCallerItem
        extends BundleItem {

    private static final ResourceLocation LEGACY_BUNDLE_MODEL =
            ResourceLocation.fromNamespaceAndPath(
                    MusaCore.MOD_ID,
                    "item/banana_phone"
            );

    //? if <1.21.2 {
    /*public OpenVocoCallerItem(
            Properties properties
    ) {
        super(properties);
    }
    *///?} else if <1.21.4 {
    /*public OpenVocoCallerItem(
            Properties properties
    ) {
        super(
                LEGACY_BUNDLE_MODEL,
                LEGACY_BUNDLE_MODEL,
                properties
        );
    }
    *///?} else {
    public OpenVocoCallerItem(
            Properties properties
    ) {
        super(properties);
    }
    //?}

    public static ItemStack getSim(
            ItemStack phone
    ) {
        if (
                phone.isEmpty()
                        || !(phone.getItem()
                        instanceof OpenVocoCallerItem)
        ) {
            return ItemStack.EMPTY;
        }

        return phone
                .getOrDefault(
                        DataComponents.BUNDLE_CONTENTS,
                        BundleContents.EMPTY
                )
                .itemCopyStream()
                .filter(
                        stack ->
                                stack.getItem()
                                        instanceof SimCardItem
                )
                .findFirst()
                .orElse(ItemStack.EMPTY);
    }

    public static void setSim(
            ItemStack phone,
            ItemStack sim
    ) {
        if (
                phone.isEmpty()
                        || !(phone.getItem()
                        instanceof OpenVocoCallerItem)
        ) {
            return;
        }

        if (
                sim == null
                        || sim.isEmpty()
        ) {
            phone.set(
                    DataComponents.BUNDLE_CONTENTS,
                    BundleContents.EMPTY
            );

            return;
        }

        if (!(sim.getItem() instanceof SimCardItem)) {
            return;
        }

        ItemStack storedSim =
                sim.copy();

        storedSim.setCount(1);

        phone.set(
                DataComponents.BUNDLE_CONTENTS,
                new BundleContents(
                        List.of(storedSim)
                )
        );
    }

    public static int getSimHex(
            ItemStack phone
    ) {
        ItemStack sim =
                getSim(phone);

        if (
                sim.isEmpty()
                        || !SimCardItem.hasStoredHex(sim)
        ) {
            return -1;
        }

        return HexTeleportDirectory.normalizeHex(
                SimCardItem.getStoredHexOrFallback(
                        sim,
                        0
                )
        );
    }

    //? if <1.21.2 {
    /*@Override
    public void appendHoverText(
            ItemStack stack,
            TooltipContext context,
            List<Component> tooltipComponents,
            TooltipFlag tooltipFlag
    ) {
        int used =
                getSim(stack).isEmpty()
                        ? 0
                        : 1;

        tooltipComponents.add(
                Component.literal(
                                used + "/1"
                        )
                        .withStyle(
                                ChatFormatting.GRAY
                        )
        );
    }
    *///?}

    @Override
            //? if <1.21.2 {
    /*public InteractionResultHolder<ItemStack> use(
            Level level,
            Player player,
            InteractionHand hand
    ) {
        ItemStack stack =
                player.getItemInHand(hand);

        openPhone(
                level,
                player,
                stack
        );

        return InteractionResultHolder.sidedSuccess(
                stack,
                level.isClientSide()
        );
    }
    *///?} else {
    public InteractionResult use(
            Level level,
            Player player,
            InteractionHand hand
    ) {
        openPhone(
                level,
                player,
                player.getItemInHand(hand)
        );

        return InteractionResult.SUCCESS_SERVER;
    }
    //?}

    @Override
    public InteractionResult useOn(
            UseOnContext context
    ) {
        openPhone(
                context.getLevel(),
                context.getPlayer(),
                context.getItemInHand()
        );

        //? if <1.21.2 {
        /*return InteractionResult.sidedSuccess(
                context.getLevel().isClientSide()
        );
        *///?} else {
        return InteractionResult.SUCCESS_SERVER;
         //?}
    }

    private static boolean isInsertAction(
            ClickAction action
    ) {
        //? if <1.21.2 {
        /*return action == ClickAction.SECONDARY;
        *///?} else {
        return action == ClickAction.PRIMARY;
         //?}
    }

    @Override
    public boolean overrideOtherStackedOnMe(
            ItemStack phone,
            ItemStack carried,
            Slot slot,
            ClickAction action,
            Player player,
            SlotAccess carriedAccess
    ) {
        ItemStack currentSim =
                getSim(phone);

        if (!carried.isEmpty()) {
            if (
                    !isInsertAction(action)
                            || !(carried.getItem()
                            instanceof SimCardItem)
                            || !SimCardItem.hasStoredHex(
                            carried
                    )
                            || !currentSim.isEmpty()
            ) {
                return false;
            }

            ItemStack insertingSim =
                    carried.copy();

            insertingSim.setCount(1);

            if (player.level().isClientSide()) {
                return super.overrideOtherStackedOnMe(
                        phone,
                        carried,
                        slot,
                        action,
                        player,
                        carriedAccess
                );
            }

            if (
                    !(player
                            instanceof ServerPlayer serverPlayer)
                            || !activateForInsert(
                            serverPlayer,
                            insertingSim
                    )
            ) {
                return true;
            }

            boolean handled =
                    super.overrideOtherStackedOnMe(
                            phone,
                            carried,
                            slot,
                            action,
                            player,
                            carriedAccess
                    );

            if (getSim(phone).isEmpty()) {
                VocoCallerNetwork.release(
                        serverPlayer,
                        insertingSim
                );
            }

            return handled;
        }

        if (
                action != ClickAction.SECONDARY
                        || currentSim.isEmpty()
        ) {
            return false;
        }

        if (
                !player.level().isClientSide()
                        && player
                        instanceof ServerPlayer serverPlayer
                        && !VocoCallerNetwork.canEject(
                        serverPlayer,
                        currentSim
                )
        ) {
            denyEject(serverPlayer);
            return true;
        }

        boolean handled =
                super.overrideOtherStackedOnMe(
                        phone,
                        carried,
                        slot,
                        action,
                        player,
                        carriedAccess
                );

        if (
                !player.level().isClientSide()
                        && player
                        instanceof ServerPlayer serverPlayer
                        && handled
                        && getSim(phone).isEmpty()
        ) {
            VocoCallerNetwork.release(
                    serverPlayer,
                    currentSim
            );
        }

        return handled;
    }

    @Override
    public boolean overrideStackedOnOther(
            ItemStack phone,
            Slot slot,
            ClickAction action,
            Player player
    ) {
        ItemStack currentSim =
                getSim(phone);

        ItemStack slotStack =
                slot.getItem();

        if (!slotStack.isEmpty()) {
            if (
                    !isInsertAction(action)
                            || !(slotStack.getItem()
                            instanceof SimCardItem)
                            || !SimCardItem.hasStoredHex(
                            slotStack
                    )
                            || !currentSim.isEmpty()
            ) {
                return false;
            }

            ItemStack insertingSim =
                    slotStack.copy();

            insertingSim.setCount(1);

            if (player.level().isClientSide()) {
                return super.overrideStackedOnOther(
                        phone,
                        slot,
                        action,
                        player
                );
            }

            if (
                    !(player
                            instanceof ServerPlayer serverPlayer)
                            || !activateForInsert(
                            serverPlayer,
                            insertingSim
                    )
            ) {
                return true;
            }

            boolean handled =
                    super.overrideStackedOnOther(
                            phone,
                            slot,
                            action,
                            player
                    );

            if (getSim(phone).isEmpty()) {
                VocoCallerNetwork.release(
                        serverPlayer,
                        insertingSim
                );
            }

            return handled;
        }

        if (
                action != ClickAction.SECONDARY
                        || currentSim.isEmpty()
        ) {
            return false;
        }

        if (
                !player.level().isClientSide()
                        && player
                        instanceof ServerPlayer serverPlayer
                        && !VocoCallerNetwork.canEject(
                        serverPlayer,
                        currentSim
                )
        ) {
            denyEject(serverPlayer);
            return true;
        }

        boolean handled =
                super.overrideStackedOnOther(
                        phone,
                        slot,
                        action,
                        player
                );

        if (
                !player.level().isClientSide()
                        && player
                        instanceof ServerPlayer serverPlayer
                        && handled
                        && getSim(phone).isEmpty()
        ) {
            VocoCallerNetwork.release(
                    serverPlayer,
                    currentSim
            );
        }

        return handled;
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(
            ItemStack stack
    ) {
        //? if >=1.21.5 {
        TooltipDisplay display =
                stack.getOrDefault(
                        DataComponents.TOOLTIP_DISPLAY,
                        TooltipDisplay.DEFAULT
                );

        if (
                !display.shows(
                        DataComponents.BUNDLE_CONTENTS
                )
        ) {
            return Optional.empty();
        }
        //?}

        BundleContents contents =
                stack.get(
                        DataComponents.BUNDLE_CONTENTS
                );

        return contents == null
                ? Optional.empty()
                : Optional.of(
                new VocoCallerBundleTooltip(
                        contents
                )
        );
    }

    @Override
    public boolean isBarVisible(
            ItemStack stack
    ) {
        return false;
    }

    private static void openPhone(
            Level level,
            Player player,
            ItemStack phone
    ) {
        if (
                level.isClientSide()
                        || !(player
                        instanceof ServerPlayer serverPlayer)
        ) {
            return;
        }

        ItemStack sim =
                getSim(phone);

        if (sim.isEmpty()) {
            serverPlayer.displayClientMessage(
                    Component.literal(
                            "Insert a SIM card first."
                    ),
                    true
            );

            return;
        }

        int hex =
                getSimHex(phone);

        if (hex < 0) {
            serverPlayer.displayClientMessage(
                    Component.literal(
                            "This SIM card does not have a Voco hex code."
                    ),
                    true
            );

            return;
        }

        if (
                !VocoCallerNetwork.isActive(
                        level.getServer(),
                        hex
                )
        ) {
            serverPlayer.displayClientMessage(
                    Component.literal(
                            "This SIM card is not active on the Voco network."
                    ),
                    true
            );

            return;
        }

        VocoCallerMenu.open(
                serverPlayer,
                phone
        );
    }

    private static boolean activateForInsert(
            ServerPlayer player,
            ItemStack sim
    ) {
        int hex =
                SimCardItem.getStoredHexOrFallback(
                        sim,
                        0
                );

        HexTeleportDirectory.Result result =
                VocoCallerNetwork.activate(
                        player,
                        sim
                );

        if (
                result
                        == HexTeleportDirectory.Result.REGISTERED
        ) {
            return true;
        }

        player.displayClientMessage(
                Component.literal(
                        result
                                == HexTeleportDirectory.Result.HEX_OCCUPIED
                                ? "Voco address #"
                                  + HexTeleportDirectory.toHex(
                                hex
                        )
                                  + " is already reserved."
                                : "This SIM card cannot be activated."
                ),
                true
        );

        return false;
    }

    private static void denyEject(
            ServerPlayer player
    ) {
        player.displayClientMessage(
                Component.literal(
                        "Only this SIM's registered owner can remove it."
                ),
                true
        );
    }
}

