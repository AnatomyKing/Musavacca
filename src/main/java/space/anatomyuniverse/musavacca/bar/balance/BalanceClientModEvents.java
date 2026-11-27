package space.anatomyuniverse.musavacca.bar.balance;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForge;
import space.anatomyuniverse.musavacca.MusaCore;
//? if <1.21.9 {
import net.minecraft.client.gui.screens.Screen;
//?}
//? if >=1.21.11 {
/*import net.minecraft.resources.Identifier;
*///?} else {
import net.minecraft.resources.ResourceLocation;
//?}
//? if >=1.21.6 {
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;
//?}
//? if >=1.21.7 {
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
//?} else {
/*import net.neoforged.neoforge.network.PacketDistributor;
*///?}
//? if <26.3 {
import org.lwjgl.glfw.GLFW;
//?}

public final class BalanceClientModEvents {
    private static KeyMapping withdraw;

    //? if >=1.21.11 {
    /*private static final Identifier BALANCE_LAYER = Identifier.fromNamespaceAndPath(MusaCore.MOD_ID, "balance_layer");
    *///?} else {
    private static final ResourceLocation BALANCE_LAYER = ResourceLocation.fromNamespaceAndPath(MusaCore.MOD_ID, "balance_layer");
    //?}

    private BalanceClientModEvents() {}

    public static void register(IEventBus modBus) {
        //? if >=1.21.6 {
        modBus.addListener(BalanceClientModEvents::registerClientPayloads);
        //?}
        modBus.addListener(BalanceClientModEvents::registerGuiLayers);
        modBus.addListener(BalanceClientModEvents::registerKeyMappings);
        NeoForge.EVENT_BUS.addListener(BalanceClientModEvents::onClientTick);
    }

    //? if >=1.21.6 {
    private static void registerClientPayloads(RegisterClientPayloadHandlersEvent event) {
        event.register(BalanceSyncPayload.TYPE, (payload, context) -> ClientBalanceData.set(payload.balance(), payload.active()));
    }
    //?}

    private static void registerGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.FOOD_LEVEL, BALANCE_LAYER, BalanceHud::render);
    }

    private static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        //? if >=26.3 {
        /*withdraw = new KeyMapping("key.musavacca.withdraw_pearl", InputConstants.KEY_B, KeyMapping.Category.MISC);
        *///?} else if >=1.21.9 {
        /*withdraw = new KeyMapping("key.musavacca.withdraw_pearl", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_B, KeyMapping.Category.MISC);
        *///?} else {
        withdraw = new KeyMapping("key.musavacca.withdraw_pearl", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_B, "key.categories.misc");
        //?}
        event.register(withdraw);
    }

    private static void onClientTick(ClientTickEvent.Post event) {
        if (withdraw == null) return;
        Minecraft minecraft = Minecraft.getInstance();
        while (withdraw.consumeClick()) {
            //? if >=26.2 {
            /*if (minecraft.player == null || minecraft.gui.screen() != null) continue;
            *///?} else {
            if (minecraft.player == null || minecraft.screen != null) continue;
             //?}

            //? if >=1.21.9 {
            /*boolean bulk = minecraft.hasShiftDown();
            *///?} else {
            boolean bulk = Screen.hasShiftDown();
             //?}

            //? if >=1.21.7 {
            ClientPacketDistributor.sendToServer(new BalanceWithdrawPayload(bulk));
            //?} else {
            /*PacketDistributor.sendToServer(new BalanceWithdrawPayload(bulk));
             *///?}
        }
    }
}
