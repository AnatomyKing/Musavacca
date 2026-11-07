package space.anatomyuniverse.musavacca;

import com.mojang.logging.LogUtils;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
//? if >=1.21.9 {
/*import net.neoforged.fml.loading.FMLEnvironment;
*///?} else {
import net.neoforged.fml.loading.FMLLoader;
//?}
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;
import space.anatomyuniverse.musavacca.bar.ModAttachments;
import space.anatomyuniverse.musavacca.bar.balance.BalanceClientModEvents;
import space.anatomyuniverse.musavacca.bar.balance.BalanceEvents;
import space.anatomyuniverse.musavacca.bar.hunger.BonusHungerClientModEvents;
import space.anatomyuniverse.musavacca.bar.hunger.BonusHungerEvents;
import space.anatomyuniverse.musavacca.block.ModBlocks;
//? if >=26.3 {
/*import space.anatomyuniverse.musavacca.block.custom.StrippableLogBlock;
*///?}
import space.anatomyuniverse.musavacca.block.custom.logic.VocoTableItemDisplayLogic;
import space.anatomyuniverse.musavacca.block.entity.ModBlockEntities;
import space.anatomyuniverse.musavacca.block.entity.ModBlockEntityRenderers;
//? if <1.21.4 {
/*import space.anatomyuniverse.musavacca.client.helmet.CustomHelmetModels;
*///?}
//? if <1.21.2 {
/*import space.anatomyuniverse.musavacca.client.PearlIgniterModels;
*///?}
import space.anatomyuniverse.musavacca.component.ModDataComponents;
import space.anatomyuniverse.musavacca.data.ModDataGenerators;
import space.anatomyuniverse.musavacca.economy.EconomyConfig;
import space.anatomyuniverse.musavacca.effect.BananaCowBlessingEvents;
import space.anatomyuniverse.musavacca.effect.ModMobEffects;
import space.anatomyuniverse.musavacca.effect.playerstatus.PlayerStatusClient;
import space.anatomyuniverse.musavacca.entity.ModEntities;
import space.anatomyuniverse.musavacca.entity.ModEntityRenderers;
import space.anatomyuniverse.musavacca.gui.ModMenuEvents;
import space.anatomyuniverse.musavacca.gui.ModMenuRegistries;
import space.anatomyuniverse.musavacca.gui.voco.VocoCameraClientModEvents;
import space.anatomyuniverse.musavacca.item.ModCreativeTabs;
import space.anatomyuniverse.musavacca.item.ModItems;
import space.anatomyuniverse.musavacca.item.PearlIgniterEvents;
import space.anatomyuniverse.musavacca.particle.ModParticleProviders;
import space.anatomyuniverse.musavacca.particle.ModParticleTypes;
import space.anatomyuniverse.musavacca.portal.PearlPortalNetwork;
import space.anatomyuniverse.musavacca.render.MusaRenderLayers;
import space.anatomyuniverse.musavacca.tint.ModTints;
import space.anatomyuniverse.musavacca.vococaller.VocoCallerNetwork;
import space.anatomyuniverse.musavacca.vococaller.VocoCallerTooltipClient;
import space.anatomyuniverse.musavacca.worldgen.ModFeatures;

@Mod(MusaCore.MOD_ID)
public final class MusaCore {
    public static final String MOD_ID = "musavacca";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String VERSION =
            //$ mod_version
            "0.0.1";
    public static final String MINECRAFT =
            //$ minecraft
            "1.21.8";

    public MusaCore(IEventBus modBus, ModContainer container) {
        ModItems.register(modBus);
        ModBlocks.register(modBus);
        ModCreativeTabs.register(modBus);
        ModFeatures.register(modBus);
        ModDataComponents.register(modBus);
        ModMobEffects.register(modBus);
        ModBlockEntities.register(modBus);
        ModMenuRegistries.register(modBus);
        ModParticleTypes.register(modBus);
        ModEntities.register(modBus);
        ModAttachments.register(modBus);
        ModNetworking.register(modBus);
        EconomyConfig.register(container);
        //? if >=26.3 {
        /*NeoForge.EVENT_BUS.addListener(StrippableLogBlock::onUseItemOnBlock);
        *///?}
        modBus.addListener(ModDataGenerators::gatherData);
        NeoForge.EVENT_BUS.addListener(PearlPortalNetwork::onServerStopping);
        NeoForge.EVENT_BUS.register(BonusHungerEvents.class);
        NeoForge.EVENT_BUS.register(BalanceEvents.class);
        NeoForge.EVENT_BUS.register(BananaCowBlessingEvents.class);
        NeoForge.EVENT_BUS.register(VocoCallerNetwork.class);
        NeoForge.EVENT_BUS.register(PearlIgniterEvents.class);
        NeoForge.EVENT_BUS.addListener(VocoTableItemDisplayLogic::onRightClickBlock);

        //? if >=1.21.9 {
        /*if (FMLEnvironment.getDist() == Dist.CLIENT) {
        *///?} else {
        if (FMLLoader.getDist() == Dist.CLIENT) {
        //?}
            ModTints.register(modBus);
            VocoCallerTooltipClient.register(modBus);
            //? if <26.1 {
            modBus.addListener(MusaRenderLayers::onClientSetup);
            //?}
            modBus.addListener(MusaRenderLayers::onModifyBakingResult);
            //? if <1.21.4 {
            /*CustomHelmetModels.register(modBus);
            *///?}
            //? if <1.21.2 {
            /*PearlIgniterModels.register(modBus);
            *///?}
            ModMenuEvents.register(modBus);
            ModEntityRenderers.register(modBus);
            ModBlockEntityRenderers.register(modBus);
            ModParticleProviders.register(modBus);
            BonusHungerClientModEvents.register(modBus);
            BalanceClientModEvents.register(modBus);
            VocoCameraClientModEvents.register(modBus);
            PlayerStatusClient.register(modBus);
        }
    }
}
