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

    /*
     * Canonical SIM writer for the Voco Caller.
     *
     * The phone physically supports exactly one SIM.
     *
     * BundleContents remains the real vanilla backing storage,
     * but every direct write goes through here so the stored
     * SIM is always normalized to count 1.
     */
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

        /*
         * Never allow arbitrary items to be written into the
         * Voco Caller through this helper.
         */
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

    /*
     * 1.21.1 uses the old Bundle capacity presentation where
     * the bundle internally exposes 64 weighted capacity units.
     *
     * Our SIM is stacksTo(1), so it already consumes the entire
     * old Bundle capacity naturally.
     *
     * We only change the visible text:
     *
     *     0/64 -> 0/1
     *     64/64 -> 1/1
     *
     * Actual BundleItem capacity mechanics remain vanilla.
     */
    //? if <1.21.2 {
    /*@Override
    public void appendHoverText(
            ItemStack stack,
            Item.TooltipContext context,
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

    /*
     * Bundle controls changed in Minecraft 1.21.2.
     *
     * 1.21.1:
     *     SECONDARY / right-click inserts into the old Bundle.
     *
     * 1.21.2+:
     *     PRIMARY / left-click inserts into the redesigned Bundle.
     */
    private static boolean isInsertAction(
            ClickAction action
    ) {
        //? if <1.21.2 {
        /*return action == ClickAction.SECONDARY;
        *///?} else {
        return action == ClickAction.PRIMARY;
         //?}
    }

    /*
     * Phone is in an inventory slot.
     * SIM is on the cursor.
     */
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

        /*
         * INSERT
         */
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

            /*
             * Preserve a stable copy before vanilla BundleItem
             * potentially shrinks/mutates the cursor stack.
             */
            ItemStack insertingSim =
                    carried.copy();

            insertingSim.setCount(1);

            /*
             * Client-side prediction remains vanilla.
             */
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

            /*
             * Registration succeeded but vanilla insertion
             * somehow failed. Undo the network reservation.
             */
            if (getSim(phone).isEmpty()) {
                VocoCallerNetwork.release(
                        serverPlayer,
                        insertingSim
                );
            }

            return handled;
        }

        /*
         * EJECT
         *
         * Both old and redesigned Bundle interaction use
         * SECONDARY for taking contents back out.
         */
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

    /*
     * Phone is on the cursor.
     * SIM/inventory slot is underneath it.
     */
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

        /*
         * INSERT
         */
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

            /*
             * Preserve the SIM before vanilla mutates the slot.
             */
            ItemStack insertingSim =
                    slotStack.copy();

            insertingSim.setCount(1);

            /*
             * Client-side prediction stays with the current
             * Minecraft version's vanilla BundleItem.
             */
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

            /*
             * Registration succeeded but vanilla insertion
             * failed: release the claimed Voco address again.
             */
            if (getSim(phone).isEmpty()) {
                VocoCallerNetwork.release(
                        serverPlayer,
                        insertingSim
                );
            }

            return handled;
        }

        /*
         * EJECT INTO EMPTY INVENTORY SLOT
         */
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

    /*
     * Use our Voco-specific tooltip marker.
     *
     * The real stored data is still vanilla BundleContents.
     */
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

    /*
     * One physical SIM bay means Minecraft's normal Bundle
     * fullness durability-style bar is unnecessary.
     */
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